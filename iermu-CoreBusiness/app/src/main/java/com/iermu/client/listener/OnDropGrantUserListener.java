package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 删除授权用户事件
 *
 * Created by zhoushaopei on 15/8/14.
 */
public interface OnDropGrantUserListener {

    /**
     * 删除授权用户回调
     * @param business
     */
    public void onDropGrantUser(Business business);

}
