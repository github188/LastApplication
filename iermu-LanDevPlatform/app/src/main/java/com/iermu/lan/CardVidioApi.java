package com.iermu.lan;

import android.content.Context;

import com.iermu.lan.model.AirCapsuleResult;
import com.iermu.lan.model.CardInforResult;

/**
 * Created by zsj on 15/11/11.
 */
public interface CardVidioApi {
    /**
     * 获取 摄像机存储卡信息
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     */
    public CardInforResult getCardInfor(Context context, String devId, String uid);


}
