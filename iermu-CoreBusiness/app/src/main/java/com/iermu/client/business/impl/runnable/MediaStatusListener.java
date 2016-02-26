package com.iermu.client.business.impl.runnable;

/**
 * 流媒体扫描状态回调接口
 *
 * Created by wcy on 15/8/6.
 */
public interface MediaStatusListener {

    /**
     * 更新设备流媒体扫描状态
     * @param deviceId  设备ID
     * @param status    扫描状态
     */
    void onUpdate(String deviceId, BaiduStreamMediaScanner.Status status);

}
