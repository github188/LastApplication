package com.iermu.client.business.api.converter;

import com.iermu.client.business.api.response.CloudPlatResponse;
import com.iermu.client.model.CloudPlat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhoushaopei on 15/10/22.
 */
public class CloudPlatConverter {


    public static CloudPlat fromJson(JSONObject json) throws JSONException {
        int plat            = json.getInt(Field.PLAT);
        int platMove        = json.getInt(Field.PLAT_MOVE);
        int platType        = json.getInt(Field.PLAT_TYPE);
        int platRotate      = json.getInt(Field.PLAT_ROTATE);
        int platRotateStatus= json.getInt(Field.PLAT_ROTATE_STATUS);
        int platTrackStatus = json.getInt(Field.PLAT_TRACK_STATUS);

        CloudPlat cloudPlat = new CloudPlat();
        cloudPlat.setPlat(plat == 1 ? true : false);
        cloudPlat.setPlatMove(platMove == 1 ? true : false);
        cloudPlat.setPlatType(platType);
        cloudPlat.setPlatRotate(platRotate == 1 ? true : false);
        cloudPlat.setPlatRotateStatus(platRotateStatus == 1 ? true : false);
        cloudPlat.setPlatTrackStatus(platTrackStatus == 1 ? true : false);

         return cloudPlat;
    }

    class Field {
       public static final String PLAT                  = "plat";
       public static final String PLAT_MOVE             = "plat_move";
       public static final String PLAT_TYPE             = "plat_type";
       public static final String PLAT_ROTATE           = "plat_rotate";
       public static final String PLAT_ROTATE_STATUS    = "plat_rotate_status";
       public static final String PLAT_TRACK_STATUS     = "plat_track_status";
    }
}