package com.iermu.client;

import com.iermu.client.model.ScreenClip;

import java.util.List;
import java.util.Map;

/**
 * Created by zhoushaopei on 16/1/7.
 *
 * 个人中心业务接口
 * 1、获取所有截图
 * 2、获取剪辑
 */
public interface IUserCenterBusiness extends IBaseBusiness {


    /**
     * 获取全部
     */
    public void getScreenClip();
    /**
     * 获取本地截屏
     */
    public void getUserScreen();

    /**
     * 获取网络剪辑
     */
    public void getUserClip();

    /**
     *  删除截屏、剪辑
     */
    public void deleteScreenClip(Map<String, ScreenClip> map);

    /**
     * 缓存剪辑、截屏
     */
    public List<ScreenClip> getScreenClip(int type);
}
