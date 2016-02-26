package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 *  设置摄像机画面旋转180度
 *
 * Created by wcy on 15/7/7.
 */
public interface OnSetDevInvertListener {

    public void onSetDevInvert(Business bus,boolean invert);

}
