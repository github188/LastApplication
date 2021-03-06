package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 我的摄像机列表更新事件
 *
 * Created by wcy on 15/6/27.
 */
public interface OnPublicCamChangedListener {

    /**
     * 我的摄像机列表更新事件
     *  1. 新增数据
     *  2. 删除数据
     *  3. 更新数据状态
     */
    public void onPublicCamChanged(Business business);

}
