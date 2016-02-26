package com.iermu.client.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.iermu.client.ErmuBusiness;
import com.igexin.sdk.PushConsts;

public class GetuiPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if(bundle==null) {
            Log.d("GetuiPushReceiver", "onReceive() bundle is null");
            return;
        }

        Log.d("GetuiPushReceiver", "onReceive() action=" + bundle.getInt("action"));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
        case PushConsts.GET_MSG_DATA:
            String appid    = bundle.getString("appid");
            byte[] payload  = bundle.getByteArray("payload");
            String taskid   = bundle.getString("taskid");
            String messageid = bundle.getString("messageid");
            Log.d("GetuiPushReceiver", "receiver payload: " + payload);

            ParsePushMessageHelper.handleGetuiPushMessage(context, payload, appid, taskid, messageid);
            // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
            //boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
            //System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
            break;
        case PushConsts.GET_CLIENTID:
            String cid = bundle.getString("clientid");
            Log.i("GetuiPushReceiver", "clientId="+cid);

            if(!TextUtils.isEmpty(cid)) {
                ErmuBusiness.getCamSettingBusiness().startRegisterGetuiPush(cid);
            }
            break;
        case PushConsts.THIRDPART_FEEDBACK:
            /*
             * String appid = bundle.getString("appid"); String taskid =
             * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
             * String result = bundle.getString("result"); long timestamp =
             * bundle.getLong("timestamp");
             *
             * Log.d("GetuiPushReceiver", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
             * taskid); Log.d("GetuiPushReceiver", "actionid = " + actionid); Log.d("GetuiSdkDemo",
             * "result = " + result); Log.d("GetuiPushReceiver", "timestamp = " + timestamp);
             */
            break;
        default:
            break;
        }
    }
}
