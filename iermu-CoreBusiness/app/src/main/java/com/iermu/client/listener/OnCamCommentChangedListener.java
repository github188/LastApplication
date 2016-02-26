package com.iermu.client.listener;

import com.iermu.client.model.CamLive;

/**
 * Created by zhangxq on 15/8/3.
 */
public interface OnCamCommentChangedListener {

    /**
     * 摄像机评论更新事件
     */
    public void onCamCommentChanged(boolean isNeedFinish);
}
