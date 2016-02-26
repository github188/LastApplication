package com.iermu.client;

import android.content.Context;

import com.iermu.client.listener.OnUserInfoListener;

/**
 * 账号关联的身份验证业务(爱耳目、百度、羚羊云)
 *
 *  1.登录
 *  2.向百度服务器添加帐号授权信息
 *  3.获取已登录百度帐号的帐号信息
 *  4.退出登录
 *  5.获取用户账号的AuthToken
 *  6.获取用户信息
 *
 * Created by wcy on 15/7/23.
 */
public interface IAccountAuthBusiness extends IBaseBusiness {

    /**
     * 注册获取服务器AccessToken监听器
     * @param listener
     */
    //public void addAccountTokenListener(OnAccountAuthListener listener);

    /**
     * 清除获取服务器AccessToken监听器
     */
    //public void removeAccountTokenListener();

    /**
     * 注册获取用户信息监听器
     * @param listener
     */
    public void addUserInfoListener(OnUserInfoListener listener);

    /**
     * 清楚用户信息监听器
     */
    public void removeUserInfoListener();

    /**
     * 注册用户
     */
    public void emailRegister(String username, String password, String email);

    /**
     * 根据注册获取token
     * @param username
     * @param password
     */
    public void registerAccount(String username, String password);

    /**
     * 根据用户登录授权码, 获取AccessToken(青岛联通)
     *
     * 事件函数:
     *      @see com.iermu.client.listener.OnAccountAuthListener
     * @param redirectUrl   重定向地址(带授权码:code)
     * @param connectUid    青岛联通手机号
     */
    public void getAccessTokenAsQDLT(String redirectUrl, String connectUid);

    /**
     * 根据用户登录授权码, 获取AccessToken
     *
     * 事件函数:
     *      @see com.iermu.client.listener.OnAccountAuthListener
     *
     * @param redirectUrl   重定向地址(带授权码:code)
     * @return
     */
    public void getAccessToken(String redirectUrl);

    /**
     * 注销登录账号
     */
    public void logout();

    /**
     * 活动海报
     * @param context
     */
    public void getNewPoster(Context context);

    /**
     * 判断当前用户是否登录
     * @return
     */
    public boolean isLogin();

    /**
     * 判断青岛联通登录状态
     * @param connectUid
     * @return
     */
    public boolean isQDLTLogin(String connectUid);

    /**
     * 获取当前用户AccessToken
     * @return
     */
    public String getAccessToken();

    /**
     * 获取当前用户UID
     * @return
     */
    public String getUid();

    /**
     * 获取用户信息
     * @return
     */
    public void getUserInfo();

    /**
     * 意见反馈
     * @param opinion
     * @param contact
     */
    public void feedBack(String opinion, String contact);

    /**
     * 修改用户名
     * @param userName 用户名
     */
    public void updateUserName(String userName);

    /**
     * 修改登录密码
     * @param password
     */
    public void updatePassword(String oldPassword, String password);

    /**
     * 完善用户资料
     * @param username
     * @param email
     * @param password
     */
    public void completeUserInfo(String username, String email, String password);

    /**
     * 获取第三方账号信息
     */
    public void getThirdConnect();

    /**
     * 获取百度UID
     * @return
     */
    public String getBaiduUid();

    /**
     * 获取百度AccessToken
     * @return
     */
    public String getBaiduAccessToken();

}
