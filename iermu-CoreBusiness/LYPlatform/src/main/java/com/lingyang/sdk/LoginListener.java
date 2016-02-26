package com.lingyang.sdk;

/**
 * 登录羚羊云服务状态回调接口
 *
 * Created by wcy on 15/10/20.
 */
public interface LoginListener {

    /**
     * 用户上线或者登陆成功
     */
    void Online();

    /**
     * 用户离线或者登录失败
     */
    void OffOnline();

}
