package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 中断登录状态
 *  Token过期 110
 *  服务器挂机 50301
 *
 * Created by wcy on 15/10/16.
 */
public interface OnAbortAccountListener {

    public void onAbortAccount(Business business);

}
