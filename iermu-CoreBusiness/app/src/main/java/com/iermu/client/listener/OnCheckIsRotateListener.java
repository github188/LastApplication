package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 检测当前设备是否处于平扫状态
 *
 * Created by zhoushaopei on 15/10/21.
 */
public interface OnCheckIsRotateListener {

    /**
     * 接口回调 成功｜失败
     * @param b
     */
    public void onCheckIsRotate(Business business, boolean b);
}
