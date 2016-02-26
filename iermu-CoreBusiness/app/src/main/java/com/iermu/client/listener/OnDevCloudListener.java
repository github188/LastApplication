package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 *  获取摄像云服务状态事件
 *
 * Created by wcy on 15/7/7.
 */
public interface OnDevCloudListener {

    /**
     * 接口回调成功\失败
     *
     */
    public void onDevCloud(Business business);

}
