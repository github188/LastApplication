package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 取消分享接口
 *
 * Created by zhoushaopei on 15/8/18.
 */
public interface OnCancleShareListener {

    /**
     * 接口回调成功\失败
     * @param bus
     */
    public void onCancleShare(Business bus);

}
