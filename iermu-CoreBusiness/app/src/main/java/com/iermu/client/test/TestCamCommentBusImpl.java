package com.iermu.client.test;

import com.iermu.client.IPubCamCommentBusiness;
import com.iermu.client.business.impl.BaseBusiness;
import com.iermu.client.model.CamComment;
import com.iermu.client.model.CamLive;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

/**
 * Created by zhangxq on 15/8/3.
 */
public class TestCamCommentBusImpl extends BaseBusiness implements IPubCamCommentBusiness {
    private List<CamComment> comments;
    private int page;

    public TestCamCommentBusImpl() {
        comments = new ArrayList<CamComment>();
    }

    @Override
    public void refreshCommentList(String shareId, String uk) {

    }

    @Override
    public void syncNewCommentList(String shareId, String uk) {

    }

    @Override
    public void syncOldCommentList(String shareId, String uk) {

    }

    @Override
    public void sendComment(String shareId, String deviceId, String comment, int parentId) {

    }

    @Override
    public List<CamComment> getCommentList(String shareId) {
        return null;
    }

    @Override
    public String getUid() {
        return null;
    }

    @Override
    public int getCount(String shareId) {
        return 0;
    }

    @Override
    public BaseDanmakuParser getDanmuInputStream(String shareId) {
        return null;
    }

    @Override
    public void addDanmaku(IDanmakuView viewDanmu, String content) {

    }

    @Override
    public void beginDanmu(IDanmakuView viewDanmu, String shareId) {

    }

    @Override
    public void setDanmuStudus(String shareId) {

    }

    @Override
    public void favour(String deviceId) {

    }

    @Override
    public void store(String shareId, String uk) {

    }

    @Override
    public void unStore(String shareId, String uk, String deviceId) {

    }

    @Override
    public void findCamLive(String shareId, String uk) {

    }
}
