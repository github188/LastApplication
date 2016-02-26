package com.iermu.lan;

import android.content.Context;

import com.iermu.lan.model.AirCapsuleResult;
import com.iermu.lan.model.Result;

/**
 * Created by zsj on 15/11/09.
 */
public interface AirCapsuleApi {
    /**
     * 获取 空气胶囊数据
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     * @return AirCapsuleResult.temperature 温度
     * @return AirCapsuleResult.humidity 湿度
     */
    public AirCapsuleResult getAirCapsuleData(Context context, String devId, String uid);


}
