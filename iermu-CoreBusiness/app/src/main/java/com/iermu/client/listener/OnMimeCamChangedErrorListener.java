package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 我的摄像机列表更新出错时间事件
 * <p/>
 * Created by wcy on 15/6/27.
 */
public interface OnMimeCamChangedErrorListener {

    /**
     * 我的摄像机列表更新出错时间事件
     *
     * @param business
     */
    public void onMimeCamChangedError(Business business);

}
