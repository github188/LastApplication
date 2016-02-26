package com.iermu.client.business.impl.event;

import com.iermu.client.model.Business;

/**
 * Created by zhoushaopei on 15/12/25.
 */
public interface OnBaiduTokenInvalidEvent {

    public void onBaiduTokenInvalid(Business business);
}
