package com.iermu.client.business.impl.stream;

/**
 * 流媒体状态(获取直播地址、羚羊云私有连接、羚羊云公开连接)
 *
 * Created by wcy on 16/1/9.
 */
public enum StreamStatus {

    //百度

    //羚羊
    /**
     * 获取直播地址(成功｜失败:错误码)
     *
     * 连接私有摄像机(成功 | 失败:连接返回码)
     *
     * 或者, 连接公开摄像机(成功 | 失败:连接返回码)
     *
     */

    LIVE_PLAY_ING,  //加载直播地址中
    LIVE_PLAY_FAIL, //加载直播地址失败  business + error_code

    BDIDU_RTMP_INVALID,//判断百度播放地址无效 | 播放人数限制
    OFFLINE,        //摄像机离线(0:离线)
    LY_STATUS_FAL,  //羚羊推流状态'未就绪' (1:准备就绪 2:未就绪 3:直播中)

    LY_PRI_ING,     //连接私有摄像机中
    LY_PRI_FAIL,    //连接私有摄像机失败

    LY_PUB_ING,     //连接公开摄像机中
    LY_PUB_FAIL,    //连接公开摄像机失败

    STREAM_OK,      //可以播放
    STREAM_STOP,    //停止播放

}
