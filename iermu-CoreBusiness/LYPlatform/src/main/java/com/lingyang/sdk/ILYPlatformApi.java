package com.lingyang.sdk;

/**
 * 羚羊云平台API接口
 *
 * Created by wcy on 15/10/20.
 */
public interface ILYPlatformApi {

    /**
     * 启用云服务
     * @param userToken  羚羊平台UserToken
     * @param userConfig 羚羊平台配置信息
     * @param listener   登录成功回调
     */
    public void StartCloudService(String userToken, String userConfig, LoginListener listener);

    /**
     * 停止云服务
     */
    void StopCloudService();

    /**
     * 连接私有摄像机
     * @param devToken
     * @param trackIp
     * @param trackPort
     */
    void StartConnectPrivateMedia(String devToken, String trackIp, int trackPort);

    /**
     * 连接公共摄像机
     * @param rtmpURL
     */
    void StartConnectPublicMedia(String rtmpURL);

    /**
     * 停止连接摄像机
     */
    void StopConnectMedia();

    /**
     *  连接录像
     * @param diskInfo
     * @param devToken
     * @param fromTime
     * @param toTime
     * @param playTime
     */
    void StartRecordMedia(String diskInfo, String devToken, int fromTime, int toTime, int playTime);

    /**
     * 停止连接摄像机
     */
    void StopRecordMedia();

    /**
     * 获取播放地址(假的)
     * @return
     */
    String GetPlayPath(String devToken);

    /**
     * 获取录像播放地址(假的)
     * @param devToken
     * @return
     */
    String GetRecordPlayPath(String devToken);

}
