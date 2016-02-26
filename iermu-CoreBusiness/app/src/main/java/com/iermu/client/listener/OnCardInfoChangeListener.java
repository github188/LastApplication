package com.iermu.client.listener;

import com.iermu.lan.model.CardInforResult;
import com.iermu.lan.model.ErrorCode;

/**
 * 获取卡录信息
 * <p/>
 * Created by zhangxq on 15/11/27.
 */
public interface OnCardInfoChangeListener {

    /**
     * 获取到卡录信息
     *
     * @param errorCode
     */
    public void onCardInfoChange(ErrorCode errorCode);
}
