package com.iermu.client;

/**
 * Created by zhoushaopei on 15/10/19.
 */
public interface ICloudPaltformBusiness extends IBaseBusiness {

    /**
     * 控制云台转动
     *
     * @param deviceId
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void cloudMove(String deviceId, int x1, int y1, int x2, int y2);

   /**
     * 根据预置点控制云台转动
     *
     * @param deviceId
     */
    public void cloudMovePreset(String deviceId, int preset);

    /**
     * 开启云台平扫
     *
     * @param deviceId
     */
    public void startCloudRotate(String deviceId);

    /**
     * 关闭云台平扫
     *
     * @param deviceId
     */
    public void stopCloudRotate(String deviceId);

    /**
     *  添加云台预置点
     *
     * @param deviceId
     * @param preset
     * @param title
     */
    public void addPreset(String deviceId, int preset, String title);

    /**
     * 删除云台预置点
     *
     * @param deviceId
     * @param preset
     */
    public void dropPreset(String deviceId, int preset);

    /**
     * 删除云台预置点
     *
     * @param deviceId
     */
    public void getListPreset(String deviceId);

    /**
     * 检测当前设备是否有云台
     *
     * @return
     */
    public void checkCloudPlatForm(String deviceId);

    /**
     * 检测是否处于平扫
     *
     * @return
     */
    public void checkIsRotate(String deviceId);

}
