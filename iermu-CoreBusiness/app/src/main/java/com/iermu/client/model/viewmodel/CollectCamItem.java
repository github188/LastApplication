package com.iermu.client.model.viewmodel;

import com.iermu.client.model.CamLive;

/**
 * 收藏的摄像机Item (left、right)
 *
 * Created by wcy on 15/6/27.
 */
public class CollectCamItem extends MimeCamItem{

    private CamLive leftCam;    //左边摄像机
    private CamLive rightCam;   //右边摄像机

    public CollectCamItem(CamLive leftCam) {
        if(leftCam == null) throw new RuntimeException("CollectCamItem is null.");
        this.leftCam = leftCam;
        setItemType(MimeCamItem.TYPE_COLLECTITEM);
    }

    public CollectCamItem(CamLive leftCam, CamLive rightCam) {
        if(leftCam == null) throw new RuntimeException("CollectCamItem is null.");
        if(rightCam == null) throw new RuntimeException("CollectCamItem is null.");
        this.leftCam = leftCam;
        this.rightCam = rightCam;
        setItemType(MimeCamItem.TYPE_COLLECTITEM);
    }

    public CamLive getLeftCam() {
        return leftCam;
    }

    public void setLeftCam(CamLive leftCam) {
        this.leftCam = leftCam;
    }

    public CamLive getRightCam() {
        return rightCam;
    }

    public void setRightCam(CamLive rightCam) {
        this.rightCam = rightCam;
    }
}

