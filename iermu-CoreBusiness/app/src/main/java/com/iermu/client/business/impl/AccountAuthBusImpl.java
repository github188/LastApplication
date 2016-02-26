package com.iermu.client.business.impl;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.IPreferenceBusiness;
import com.iermu.client.business.api.AccountAuthApi;
import com.iermu.client.business.api.response.ClientResponse;
import com.iermu.client.business.api.response.Response;
import com.iermu.client.business.api.response.TokenResponse;
import com.iermu.client.business.api.response.UserInfoResponse;
import com.iermu.client.business.dao.AccountWrapper;
import com.iermu.client.business.impl.event.OnAbortAccountEvent;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.business.impl.event.OnBaiduTokenInvalidEvent;
import com.iermu.client.business.impl.event.OnLoginSuccessEvent;
import com.iermu.client.business.impl.event.OnLogoutEvent;
import com.iermu.client.config.ApiConfig;
import com.iermu.client.listener.OnAbortAccountListener;
import com.iermu.client.listener.OnAccountAuthListener;
import com.iermu.client.listener.OnBaiduTokenInvalidListener;
import com.iermu.client.listener.OnClientListener;
import com.iermu.client.listener.OnCompleteUserInfoListener;
import com.iermu.client.listener.OnFeedBackListener;
import com.iermu.client.listener.OnGetThirdConnectListener;
import com.iermu.client.listener.OnRegisterListener;
import com.iermu.client.listener.OnUpdatePasswordListener;
import com.iermu.client.listener.OnUpdateUserNameListener;
import com.iermu.client.listener.OnUserInfoListener;
import com.iermu.client.model.Account;
import com.iermu.client.model.Business;
import com.iermu.client.model.Connect;
import com.iermu.client.model.NewPoster;
import com.iermu.client.model.UserInfo;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.util.FileUtil;
import com.iermu.client.util.Logger;
import com.iermu.client.util.PhoneDevUtil;

import java.util.List;

/**
 * 账号关联的身份验证业务(爱耳目、百度、羚羊云)
 * <p/>
 * Created by wcy on 15/7/23.
 */
public class AccountAuthBusImpl extends BaseBusiness implements IAccountAuthBusiness
        , OnAbortAccountEvent, OnBaiduTokenInvalidEvent {

    private Account mAccount;
    private int loginAccount = -1;   //-1:未定义 0: 失败 1:成功
    private OnUserInfoListener mInfoListener;

    public AccountAuthBusImpl() {
        subscribeEvent(OnAbortAccountEvent.class, this);
        subscribeEvent(OnBaiduTokenInvalidEvent.class, this);
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mAccount = AccountWrapper.queryAccount();
                if (mAccount != null) {
                    String accessToken  = mAccount.getAccessToken();
                    String refreshToken = mAccount.getRefreshToken();
                    String uid = mAccount.getUid();
                    publishEvent(OnAccountTokenEvent.class, uid, accessToken, refreshToken);
                }
            }
        });
    }

    @Override
    public void addUserInfoListener(OnUserInfoListener listener) {
        this.mInfoListener = listener;
    }

    @Override
    public void removeUserInfoListener() {
        this.mInfoListener = null;
    }

    @Override
    public void emailRegister(String username, String password, String email) {
        UserInfoResponse response = AccountAuthApi.register(username, password, email);
        Business business = response.getBusiness();
        sendListener(OnRegisterListener.class, business);
    }

    @Override
    public void registerAccount(String username, String password) {
        TokenResponse res = AccountAuthApi.getRegisterToken(username, password);
        Business business   = res.getBusiness();
        String accessToken  = res.getAccessToken();
        String refreshToken = res.getRefreshToken();
        String uid          = res.getUid();
        List<Connect> connectList = res.getConnectList();
        if(connectList != null && connectList.size() <= 0) {   //注释: 后台接口调整Token接口不再返回Connect参数
            UserInfoResponse infoRes = AccountAuthApi.getUserInfo(accessToken);
            business = infoRes.getBusiness();
            if (business.isSuccess()) {
                UserInfo info = infoRes.getInfo();
                connectList   = infoRes.getConnectList();
                AccountWrapper.insertUserInfo(info);
            }
        }
        if(business.isSuccess()) {
            AccountWrapper.updateConnect(uid, connectList);
            mAccount = AccountWrapper.insert(uid, accessToken, refreshToken);
            super.publishEvent(OnLoginSuccessEvent.class);
            super.publishEvent(OnAccountTokenEvent.class, uid, accessToken, refreshToken);
        }
        super.sendListener(OnAccountAuthListener.class, business);
    }

    @Override
    public void getAccessToken(String redirectUrl) {
        boolean login = isLogin();
        getAccessToken(redirectUrl, login);
    }

    @Override
    public void getAccessTokenAsQDLT(String redirectUrl, String connectUid) {
        boolean login = isLogin();
        boolean loginQdlt = isQDLTLogin(connectUid);
        if(login && !loginQdlt) {
            logout();//先退出之前登录的账号
        }
        getAccessToken(redirectUrl, loginQdlt);
    }

    private void getAccessToken(String redirectUrl, boolean isLogin) {
        Logger.i("AccountAuthBus getAccessToken:" + redirectUrl);
        String codeKey = "code";
        if (TextUtils.isEmpty(redirectUrl)
                || !redirectUrl.contains(codeKey)
                || !redirectUrl.startsWith(ApiConfig.iermu_CODE_REDIRECT)) return;

        synchronized (AccountAuthBusImpl.class) {
            Business business = new Business();
            //boolean isLogin = (mAccount!=null) ? mAccount.isLogin() : false;
            Logger.i("AccountAuthBus account:"+mAccount+" login:"+isLogin);
            if(!isLogin) {
                int indexOf = redirectUrl.indexOf("?");
                redirectUrl = "http://localhost/?" + redirectUrl.substring(indexOf + 1);
                String authCode = Uri.parse(redirectUrl).getQueryParameter(codeKey);

                String clientId     = ApiConfig.iermu_APPKEY;
                String redirectUri  = ApiConfig.iermu_CODE_REDIRECT;
                TokenResponse res   = AccountAuthApi.getToken(authCode, clientId, redirectUri);
                String accessToken  = res.getAccessToken();
                String refreshToken = res.getRefreshToken();
                String uid          = res.getUid();
                business                 = res.getBusiness();
                List<Connect> connectList = res.getConnectList();
                Logger.i("AccountAuthBus accessToken:"+accessToken+" uid:"+uid);
                if(business.isSuccess() && (connectList==null || connectList.size()<=0) ) {   //注释: 后台接口调整Token接口不再返回Connect参数
                    UserInfoResponse infoRes = AccountAuthApi.getUserInfo(accessToken);
                    business = infoRes.getBusiness();
                    if (business.isSuccess()) {
                        UserInfo info = infoRes.getInfo();
                        connectList   = infoRes.getConnectList();
                        AccountWrapper.insertUserInfo(info);
                    }
                }
                if(business.isSuccess()) {
                    AccountWrapper.updateConnect(uid, connectList);
                    mAccount = AccountWrapper.insert(uid, accessToken, refreshToken);
                    super.publishEvent(OnLoginSuccessEvent.class);
                    super.publishEvent(OnAccountTokenEvent.class, uid, accessToken, refreshToken);
                }
            } else {//已登录状态
                business.setCode(BusinessCode.SUCCESS);
            }
            super.sendListener(OnAccountAuthListener.class, business);
        }
    }

    @Override
    public void getUserInfo() {
        String accessToken   = getAccessToken();
        UserInfoResponse res = AccountAuthApi.getUserInfo(accessToken);
        final UserInfo info  = res.getInfo();
        if (info != null) {
            AccountWrapper.insertUserInfo(info);
        }
        execMainThread(new Runnable() {
            @Override
            public void run() {
                if (info != null) {
                    if (mInfoListener != null) mInfoListener.onUserInfo(info);
                }
            }
        });
    }

    @Override
    public void feedBack(String opinion, String contact) {
        String accessToken= getAccessToken();
        Response response = AccountAuthApi.feedBack(opinion, contact, accessToken);
        Business business = response.getBusiness();
        sendListener(OnFeedBackListener.class, business);
    }

    @Override
    public void updateUserName(String userName) {
        String accessToken  = getAccessToken();
        String uid          = getUid();
        Response response = AccountAuthApi.updateUserName(userName, accessToken);
        Business business = response.getBusiness();
        if (business.isSuccess()) {
            AccountWrapper.updateUserName(uid, userName);
        }
        sendListener(OnUpdateUserNameListener.class, business);
    }

    @Override
    public void updatePassword(String oldPassword, String password) {
        String accessToken  = getAccessToken();
        String uid          = getUid();
        Response response = AccountAuthApi.updatePassword(uid, oldPassword, password, accessToken);
        Business business = response.getBusiness();
        sendListener(OnUpdatePasswordListener.class, business);
    }

    @Override
    public void completeUserInfo(String username, String email, String password) {
        String accessToken  = getAccessToken();
        String uid          = getUid();
        Response response = AccountAuthApi.completeUserInfo(username, email, password, accessToken);
        Business business = response.getBusiness();
        sendListener(OnCompleteUserInfoListener.class, business);
        if (business.isSuccess()) {
            AccountWrapper.updateUserInfo(uid, username, email);
        }
    }

    @Override
    public void getThirdConnect() {
        String accessToken  = getAccessToken();
        String uid          = getUid();
        UserInfoResponse res = AccountAuthApi.getUserInfo(accessToken);
        Business business = res.getBusiness();
        if (business.isSuccess()) {
            UserInfo info = res.getInfo();
            List<Connect> connectList = res.getConnectList();
            AccountWrapper.insertUserInfo(info);
            AccountWrapper.updateConnect(uid, connectList);
        }
        mAccount = AccountWrapper.queryAccount();
        String baiduUid     = (mAccount!=null) ? mAccount.getBaiduUid() : "";
        String baiduUName   = (mAccount!=null) ? mAccount.getBaiduUName() : "";
        super.sendListener(OnGetThirdConnectListener.class, business, baiduUid, baiduUName);
    }

    @Override
    public void logout() {
        String accessToken = getAccessToken();
        String uid         = getUid();
        AccountWrapper.logout(uid);
        synchronized (AccountAuthBusImpl.class) {
            this.mAccount = null;
        }
        super.publishEvent(OnLogoutEvent.class);
        AccountAuthApi.logout(accessToken);
    }

    @Override
    public void getNewPoster(Context context) {
        int width = PhoneDevUtil.getScreenWidth(context);
        int height = PhoneDevUtil.getScreenHeight(context);
        ClientResponse response = AccountAuthApi.newPoster(width, height);
        Business bus = response.getBusiness();
        if (bus.isSuccess()) {
            NewPoster client = response.getClient();
            sendListener(OnClientListener.class, bus, client);
            String imgUrl = client.getImgUrl();
            String webUrl = client.getWebUrl();
            long startTime = client.getStartTime();
            long endTime = client.getEndTime();
            IPreferenceBusiness pre = ErmuBusiness.getPreferenceBusiness();
            pre.setPosterImgUrl(imgUrl);
            pre.setPosterWebUrl(webUrl);
            pre.setPosterStartTime(startTime);
            pre.setPosterEndTime(endTime);
            if (!TextUtils.isEmpty(imgUrl)) FileUtil.downAppFile(context, imgUrl);
        }
    }

    @Override
    public boolean isLogin() {
        synchronized (AccountAuthBusImpl.class) {
            return (mAccount!=null) ? mAccount.isLogin() : false;
        }
    }

    /**
     * 判断青岛联通登录状态
     * @param connectUid
     * @return
     */
    public boolean isQDLTLogin(String connectUid) {
        synchronized (AccountAuthBusImpl.class) {
            String uid    = (mAccount!=null) ? mAccount.getQdltUid() : "";
            boolean login = (mAccount!=null) ? mAccount.isLogin() : false;
            boolean equals= TextUtils.isEmpty(uid) ? false : uid.equals(connectUid);
            return login && equals;
        }
    }

    @Override
    public String getBaiduUid() {
        return (mAccount!=null) ? mAccount.getBaiduUid() : "";
    }

    @Override
    public String getBaiduAccessToken() {
        return (mAccount!=null) ? mAccount.getBaiduAK() : "";
    }

    @Override
    public String getAccessToken() {
        return (mAccount!=null) ? mAccount.getAccessToken() : "";
    }

    @Override
    public String getUid() {
        return (mAccount!=null) ? mAccount.getUid() : "";
    }

    /**
     * 处理账号异常
     * 110:Token失效  50201:服务器出错 //{"error_code":50201, "error_msg":"api closed"}
     * 110: 密码被修改|账号登录异常
     * 111: Token失效
     */
    @Override
    public void onAbortAccount(Business business) {
        if( !isLogin()) return;
        logout();
        sendListener(OnAbortAccountListener.class, business);
    }

    /** 百度AccessToken过期导致需要重新授权 */
    @Override
    public void onBaiduTokenInvalid(Business business) {
        sendListener(OnBaiduTokenInvalidListener.class, business);
    }
}
