package com.iermu.client.config;

import android.os.Environment;

/**
 * 缓存路径配置
 * <p/>
 * Created by wcy on 15/6/19.
 */
public class PathConfig {

    //	private static final String ROOT = Environment.getExternalStorageDirectory().getPath() + "/iermu";
    public static final String ROOT = "/sdcard/爱耳目摄像机";
    public static final String ROOT_ENG = "/sdcard/iermuCam";
    public static final String CAMERA = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera";
    public static final String CACHE_IMG = ROOT + "/cache/images";
    public static final String CACHE_SHARE = ROOT + "/相册";
    public static final String CACHE_SHARE_ENG = ROOT + "/album";
    public static final String CACHE_CAPSULE = ROOT + "/cache/capsule";
    public static final String CACHE_PRESET = ROOT + "/cache/preset";
    public static final String CACHE_VIDEO = ROOT + "/视频";
    public static final String CACHE_VIDEO_ENG = ROOT + "/video";
    public static final String CACHE_AVATAR = ROOT + "/cache/avatar";

    /**
     * 应用日志目录文件
     */
    public static String APP_LOG_PATH = ROOT + "/log";

    /**
     * 日志文件路径
     */
    public static String LOGFILE = APP_LOG_PATH + "log.txt";
}
