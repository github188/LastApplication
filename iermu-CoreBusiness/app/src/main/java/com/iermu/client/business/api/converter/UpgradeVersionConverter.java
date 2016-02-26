package com.iermu.client.business.api.converter;

import com.iermu.client.model.UpgradeVersion;
import com.iermu.client.model.viewmodel.CamUpdateStatus;

import org.json.JSONObject;

/**
 * 获取摄像机升级状态
 *
 * Created by zsj on 16/1/4.
 */
public class UpgradeVersionConverter {

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static UpgradeVersion fromJson(JSONObject json) {
        UpgradeVersion upgradeVersion = new UpgradeVersion();
        int needUpgrade      = json.optInt(Field.NEED_UPGRADE);
        String version = json.optString(Field.VERSION);
        String desc = json.optString(Field.DESC);

        upgradeVersion.setNeedUpgrade(needUpgrade);
        upgradeVersion.setDesc(desc);
        upgradeVersion.setVersion(version);
        return upgradeVersion;
    }

    class Field {
        public static final String NEED_UPGRADE     = "need_upgrade";
        public static final String VERSION     = "version";
        public static final String DESC     = "desc";
    }
}
