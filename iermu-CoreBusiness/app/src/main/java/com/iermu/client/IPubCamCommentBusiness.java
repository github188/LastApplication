package com.iermu.client;

import com.iermu.client.model.CamComment;
import com.iermu.client.model.CamLive;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

/**
 * Created by zhangxq on 15/8/3.
 */
public interface IPubCamCommentBusiness extends IBaseBusiness {

    // 取最新的几条数据，做更新（临时定为3条）
    public void refreshCommentList(String shareId, String uk);

    /**
     * 取新的一页
     *
     * 事件函数:
     *      @see com.iermu.client.listener.OnCamCommentChangedListener
     *
     * @param shareId
     * @param uk
     */
    public void syncNewCommentList(String shareId, String uk);

    // 取老的一页
    public void syncOldCommentList(String shareId, String uk);

    // 发表评论
    public void sendComment(String shareId, String deviceId, String comment, int parentId);

    // 获取列表
    public List<CamComment> getCommentList(String shareId);

    // 获取uid
    public String getUid();

    // 获取评论个数
    public int getCount(String shareId);

    // 获取弹幕输入流
    public BaseDanmakuParser getDanmuInputStream(String shareId);

    // 添加弹幕
    public void addDanmaku(IDanmakuView viewDanmu, String content);

    // 开始播放弹幕
    public void beginDanmu(IDanmakuView viewDanmu, String shareId);

    // 停止播放弹幕
    public void setDanmuStudus(String shareId);

    // 点赞
    public void favour(String deviceId);

    // 收藏
    public void store(String shareId, String uk);

    // 取消收藏
    public void unStore(String shareId, String uk, String deviceId);

    // 获取摄像机
    public void findCamLive(String shareId, String uk);
}
