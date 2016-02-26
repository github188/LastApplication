package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 *  设置摄像机是否静音
 *
 * Created by wcy on 15/7/7.
 */
public interface OnSetDevAudioListener {

    public void onSetDevAudio(Business business,boolean audio);

}
