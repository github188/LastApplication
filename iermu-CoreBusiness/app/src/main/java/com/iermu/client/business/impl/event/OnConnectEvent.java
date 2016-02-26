package com.iermu.client.business.impl.event;

import com.iermu.client.model.Connect;

/**
 * 平台数据(百度云、羚羊云)回调
 *
 * Created by wcy on 15/7/31.
 */
public interface OnConnectEvent {


    public void onConnectChanged(Connect connect);

}
