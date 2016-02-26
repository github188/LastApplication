package com.lingyang.sdk;

/**
 * 连接摄像机流媒体扫描状态回调接口
 * Created by guixiaomei on 15/10/21.
 */
public interface MediaScannerListener {

    void mediaScanner(int connect, String devToken);

}

