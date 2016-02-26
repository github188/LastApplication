package com.iermu.client.business.impl.runnable;

/**
 * 流媒体扫描结果成功回调接口
 *
 * Created by wcy on 15/8/16.
 */
public interface MedaiScannerListener {


    /**
     * 更新设备流媒体扫描状态
     * @param deviceId  设备ID
     * @param status    扫描状态
     */
    void onScanValid(String deviceId, BaiduStreamMediaScanner.Status status);


}
