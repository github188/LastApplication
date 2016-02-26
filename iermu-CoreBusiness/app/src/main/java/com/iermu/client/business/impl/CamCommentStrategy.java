package com.iermu.client.business.impl;

import com.iermu.client.ErmuApplication;
import com.iermu.client.IPubCamCommentBusiness;
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
public class CamCommentStrategy extends BaseBusinessStrategy implements IPubCamCommentBusiness {

    private IPubCamCommentBusiness mBusiness;

    public CamCommentStrategy(IPubCamCommentBusiness business) {
        super(business);
        this.mBusiness = business;
    }

    @Override
    public void refreshCommentList(final String shareId, final String uk) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.refreshCommentList(shareId, uk);
            }
        });
    }

    @Override
    public void syncNewCommentList(final String shareId, final String uk) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncNewCommentList(shareId, uk);
            }
        });
    }

    @Override
    public void syncOldCommentList(final String shareId, final String uk) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncOldCommentList(shareId, uk);
            }
        });
    }

    @Override
    public void sendComment(final String shareId, final String deviceId, final String comment, final int parentId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.sendComment(shareId, deviceId, comment, parentId);
            }
        });
    }

    @Override
    public List<CamComment> getCommentList(String shareId) {
        return mBusiness.getCommentList(shareId);
    }

    @Override
    public String getUid() {
        return mBusiness.getUid();
    }

    @Override
    public int getCount(String shareId) {
        return mBusiness.getCount(shareId);
    }

    @Override
    public BaseDanmakuParser getDanmuInputStream(String shareId) {
        return mBusiness.getDanmuInputStream(shareId);
    }

    @Override
    public void addDanmaku(IDanmakuView viewDanmu, String content) {
        mBusiness.addDanmaku(viewDanmu, content);
    }

    @Override
    public void beginDanmu(final IDanmakuView viewDanmu, final String shareId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.beginDanmu(viewDanmu, shareId);
            }
        });
    }

    @Override
    public void setDanmuStudus(String shareId) {
        mBusiness.setDanmuStudus(shareId);
    }

    @Override
    public void favour(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.favour(deviceId);
            }
        });
    }

    @Override
    public void store(final String shareId, final String uk) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.store(shareId, uk);
            }
        });

    }

    @Override
    public void unStore(final String shareId, final String uk, final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.unStore(shareId, uk, deviceId);
            }
        });
    }

    @Override
    public void findCamLive(final String shareId, final String uk) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.findCamLive(shareId, uk);
            }
        });
    }
}
