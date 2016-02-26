package com.iermu.ui.fragment.authshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cms.iermu.wxapi.WXEntryActivity;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamShareBusiness;
import com.iermu.client.business.dao.AccountWrapper;
import com.iermu.client.listener.OnDropGrantUserListener;
import com.iermu.client.listener.OnGetGrantCodeListener;
import com.iermu.client.listener.OnGrantUserListener;
import com.iermu.client.listener.OnUserInfoListener;
import com.iermu.client.model.Account;
import com.iermu.client.model.Business;
import com.iermu.client.model.GrantUser;
import com.iermu.client.model.UserInfo;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.util.Logger;
import com.iermu.ui.adapter.AuthUserAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonCommitDialog;
import com.mob.tools.utils.UIHandler;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * Created by xjy on 15/6/27.
 */
public class AuthUserFragment extends BaseFragment implements PlatformActionListener,Handler.Callback
        ,OnGetGrantCodeListener, OnGrantUserListener, OnDropGrantUserListener, OnUserInfoListener {

    @ViewInject(R.id.auth_send)     LinearLayout mAuthSend;
    @ViewInject(R.id.auth_users)    LinearLayout mAuthUsers;
    @ViewInject(R.id.auth_load)     RelativeLayout mLoading;
    @ViewInject(R.id.wx_btn)        Button mWxBtn;
    @ViewInject(R.id.auth_listview) SwipeMenuListView litview;
    @ViewInject(R.id.user_num)      TextView userNum;
    @ViewInject(R.id.share_wx)      Button mShareBtn;
    @ViewInject(R.id.actionbar_back)    ImageView mActionbarBack;
    @ViewInject(R.id.actionbar_title)   TextView mActionbarTitle;
    @ViewInject(R.id.title_bg)      LinearLayout titlebg;

    private Context context;
    private static final String DEVICEID = "deviceid";
    private static final String DESC = "desc";
    private static final String UK = "uk";
    private static final int MSG_TOAST = 1;
    private static final int MSG_ACTION_CCALLBACK = 2;
    private static final int MSG_CANCEL_NOTIFY = 3;
    protected PlatformActionListener paListener;
    private String deviceid;
    private String describe;
    private String grantCode;
    private String uName;
    private String userName;
    private AuthUserAdapter adapter;
    SetAuthUser listener;
    private CommonCommitDialog cusDialog;

    public static Fragment actionInstance(FragmentActivity context, String deviceId, String desc, String uk) {
        AuthUserFragment fragment = new AuthUserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DEVICEID, deviceId);
        bundle.putString(DESC, desc);
        bundle.putString(UK, uk);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
//        setCommonActionBar(R.string.auth_users)
//            .setCommonBackgroud(R.color.share_color);
        setCustomActionBar(R.layout.actionbar_about);
//        setCustomActionBar(R.layout.actionbar_about);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        mActionbarTitle.setText(R.string.auth_users);
        titlebg.setBackgroundColor(0xff00a2ff);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_empower_homer, null);
        ViewHelper.inject(this, view);

        showDialog();

        View viewAuth = View.inflate(getActivity(), R.layout.grant_user, null);
        litview.addFooterView(viewAuth);
        ViewHelper.inject(this, viewAuth);
        Bundle bundle = getArguments();
        deviceid = bundle.getString(DEVICEID);
        describe = bundle.getString(DESC);
        uName = bundle.getString(UK);
        context = getActivity();

        adapter = new AuthUserAdapter(getActivity());
        litview.setAdapter(adapter);
        deleteUser();
        ErmuBusiness.getAccountAuthBusiness().getUserInfo();
        ICamShareBusiness business = ErmuBusiness.getShareBusiness();
        business.registerListener(OnGetGrantCodeListener.class, this);
        business.registerListener(OnGrantUserListener.class, this);
        business.registerListener(OnDropGrantUserListener.class, this);

        business.getGrantCode(deviceid);
        business.syncGrantUsers(deviceid);
//        refreshView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamShareBusiness business = ErmuBusiness.getShareBusiness();
        business.unRegisterListener(OnGetGrantCodeListener.class, this);
        business.unRegisterListener(OnGrantUserListener.class, this);
        business.unRegisterListener(OnDropGrantUserListener.class, this);
    }

    @OnClick(value = {R.id.wx_btn, R.id.share_wx,R.id.actionbar_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_wx:
            case R.id.wx_btn:
                shareWx();
                break;
            case R.id.actionbar_back:
                popBackStack();
                break;
        }
    }

    private void showDialog() {
        String str = getActivity().getString(R.string.grant_user_loading);
        cusDialog = new CommonCommitDialog(getActivity());
        cusDialog.show();
        cusDialog.setStatusText(str);
    }

    private void shareWx () {
        if (!TextUtils.isEmpty(grantCode)) {
            String userName = null;
            String describeEnc = null;
            try {
                userName = URLEncoder.encode(AccountWrapper.queryAccount().getUname(), "UTF-8");
                describeEnc = URLEncoder.encode(describe, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = "http://www.iermu.com/grant?code=" + grantCode + "&desc="+describeEnc+"&uname="+userName;//userName
            Logger.i("url_auth:" + url);
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            final byte[] b = Bitmap2Bytes(bmp);
            if (WXEntryActivity.isWxInstalled(getActivity())) {
                String shareTxt = getActivity().getResources().getString(R.string.auth_share_text);
                String lookTxt = getActivity().getResources().getString(R.string.auth_look_text);
                WXEntryActivity.shareToAuth(getActivity(), url, shareTxt, lookTxt, b);
            } else {
                ErmuApplication.toast(context.getResources().getString(R.string.no_wx_client));
            }
        } else {
            ErmuApplication.toast(context.getResources().getString(R.string.get_code_fail));
        }
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // 成功
    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }

    // 失敗
    @Override
    public void onError(Platform platform, int action, Throwable t) {
        t.printStackTrace(); //打印错误信息,print the error msg
        Message msg = new Message(); //错误监听,handle the error msg
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 2;
        msg.arg2 = action;
        msg.obj = t;
        UIHandler.sendMessage(msg, this);
    }

    // 取消
    @Override
    public void onCancel(Platform platform, int action) {
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 3;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }

    public boolean handleMessage(Message msg) {
        switch (msg.arg1) {
        case 1:  // 成功
            ErmuApplication.toast(getResources().getString(R.string.share_success));
        break;
        case 2: // 失败
            ErmuApplication.toast(getResources().getString(R.string.share_fail));
        break;
        case 3: // 取消
            ErmuApplication.toast(getResources().getString(R.string.cancel_share));
        break;
        }
        return false;

    }

    @Override
    public void onGetGrantCode(Business bus, String devId, String code) {
        if (deviceid.equals(devId)) this.grantCode = code;
        Logger.i("grantCode:" + grantCode);
    }

    @Override
    public void onGrantUser(Business business, String devId, int count) {
        if(cusDialog != null) cusDialog.dismiss();
        if(!deviceid.equals(devId)) return;
        refreshView();
    }

    @Override
    public void onDropGrantUser(Business bus) {
        if (!bus.isSuccess()) ErmuApplication.toast(getResources().getString(R.string.cancel_share_fail));
        refreshView();
    }

    private void deleteUser() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.cancle_auth);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        litview.setMenuCreator(creator);
        // step 2. listener item click event
        litview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //TODO 判断没网
                        if (!Util.isNetworkConn(getActivity())) {
                            ErmuApplication.toast(getActivity().getResources().getString(R.string.no_net));
                        } else {
                            GrantUser grant = (GrantUser) adapter.getItem(index);
                            String uk = grant.getUk();
                            adapter.removeUser(index);
                            if (adapter.getCount() <= 0) {
                                mAuthSend.setVisibility(View.VISIBLE);
                                titlebg.setBackgroundColor(getResources().getColor(R.color.auth_title_bg));
                                mAuthUsers.setVisibility(View.GONE);
                            }
                            //TODO 删除授权用户
                            ErmuBusiness.getShareBusiness().dropGrantUser(deviceid, uk);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void setOnControlListener(SetAuthUser listener) {
        this.listener = listener;
    }

    private void refreshView() {
        List<GrantUser> users = ErmuBusiness.getShareBusiness().getGrantUser(deviceid);
        int count = users.size();
        adapter.notifyDataChange(users);
        if (count > 0) {
            mAuthSend.setVisibility(View.GONE);
            mAuthUsers.setVisibility(View.VISIBLE);
        } else {
            mAuthSend.setVisibility(View.VISIBLE);
            titlebg.setBackgroundColor(getResources().getColor(R.color.auth_title_bg));
            mAuthUsers.setVisibility(View.GONE);
        }
        if (listener != null){
            listener.setUser(String.valueOf(count));
        }
        userNum.setText(String.valueOf(count));
//        mLoading.setVisibility((users.size() > 0) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onUserInfo(UserInfo info) {
        if (info == null) return;
        userName = info.getUserName();
    }
     public interface  SetAuthUser{
         void setUser(String num);
     }


}
