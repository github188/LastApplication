package com.iermu.client.model.viewmodel;

import com.iermu.client.model.CamLive;
import com.iermu.client.model.CamLive;

/**
 * 我的摄像机Item (MimeCam、CollectCamItem)
 *
 * Created by wcy on 15/6/27.
 */
public class MimeCamItem {

    public static final int TYPE_MIME        = 0;//我的摄像机
    public static final int TYPE_AUTHORIZE   = 1;//授权给我的摄像机
    public static final int TYPE_COLLECTITEM = 2;//收藏的摄像机Item
    public static final int TYPE_COLLECT     = 3;//收藏的摄像机

    private int itemType;   //我的摄像机Item 类型
    private CamLive item;   //我的摄像机

    public CamLive getItem() {
        return item;
    }

    public void setItem(CamLive item) {
        this.item = item;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

}
