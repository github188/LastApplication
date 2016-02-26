package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * token 失效接口回调
 *
 */
public interface OnBaiduTokenInvalidListener {

    /**
     * 回调成功｜失败
     * @param business
     */
    public void onBaiduTokenInvalid(Business business);

}
