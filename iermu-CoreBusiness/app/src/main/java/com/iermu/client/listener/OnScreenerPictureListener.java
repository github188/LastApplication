package com.iermu.client.listener;

import com.iermu.client.model.ScreenClip;

import java.util.List;

/**
 * Created by zhoushaopei on 16/1/7.
 */
public interface OnScreenerPictureListener {

    public void onScreenPicture(List<ScreenClip> pictures, int type);
}
