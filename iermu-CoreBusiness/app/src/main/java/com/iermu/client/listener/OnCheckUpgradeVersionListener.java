package com.iermu.client.listener;

import com.iermu.client.model.Business;
import com.iermu.client.model.UpgradeVersion;
import com.iermu.client.model.viewmodel.CamUpdateStatus;

/**
 *获取固件最新版本信息
 *
 * Created by zsj on 16/1/14.
 */
public interface OnCheckUpgradeVersionListener {

    public void OnCheckUpgradeVersion(UpgradeVersion upgradeVersion,Business business);

}
