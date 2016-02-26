package com.iermu.ui.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.config.AppConfig;
import com.iermu.client.util.Logger;
import com.iermu.ui.view.CommonDialog;

import im.fir.sdk.FIR;
import im.fir.sdk.callback.VersionCheckCallback;
import im.fir.sdk.version.AppVersion;

/**
 * 检测版本升级
 * Created by zhoushaopei on 15/11/19.
 */
public class CheckVersionUtils {

    public static void checkAlertUpdateVersion(final Activity context) {
        if (AppConfig.DEV_MODE) {
            FIR.checkForUpdateInFIR("9da1d54393ddbc3129daec3dfa8b490b", new VersionCheckCallback() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onSuccess(AppVersion appVersion, boolean b) {
                    Logger.i("FIR", "b: " + b + " " + appVersion.toString());
                    int build = ErmuApplication.getVersionBuild();
                    int versionCode = appVersion.getVersionCode();
                    int alertedVersion = ErmuBusiness.getPreferenceBusiness().getAlertedUpdateVersion();//是否提示检测版本升级
                    if (appVersion.getVersionCode() <= build || alertedVersion >= versionCode
                            || context.isFinishing() || context == null || context.isDestroyed()
                            ) {
                        return;
                    }

                    ErmuBusiness.getPreferenceBusiness().setAlertedUpdateVersion(versionCode);
                    final String updateUrl = appVersion.getUpdateUrl();

                    final CommonDialog commonDialog = new CommonDialog(context);
                    commonDialog.setCanceledOnTouchOutside(false);
                    commonDialog.setTitle("检测新版本应用")
                            .setContent(appVersion.getChangeLog())
                            .setCancelText("取消")
                            .setOkText("更新版本")
                            .setOkListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    Uri content_url = Uri.parse(updateUrl);
                                    intent.setData(content_url);
                                    context.startActivity(intent);
                                    commonDialog.dismiss();
                                }
                            })
                            .setCancelListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    commonDialog.dismiss();
                                }
                            }).show();
                }

                //@Override
                //public void onFail(String s, int i) {}
                //@Override
                //public void onError(Exception e) {}
                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                }
            });
        } else {
            com.iermu.ui.view.updateApp.updateAppUtils.checkToUpdate(context, true);
        }
    }

    public static void toastUpdateVersion(final Activity activity, final View view) {
        if (AppConfig.DEV_MODE) {
            FIR.checkForUpdateInFIR("9da1d54393ddbc3129daec3dfa8b490b", new VersionCheckCallback() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onSuccess(AppVersion appVersion, boolean b) {
                    Logger.i("FIR", "b: " + b + " " + appVersion.toString());
                    int build = ErmuApplication.getVersionBuild();
                    int versionCode = appVersion.getVersionCode();
                    if (versionCode <= build
                            || activity.isFinishing() || activity == null || activity.isDestroyed()) {
                        return;
                    }
                    ErmuBusiness.getPreferenceBusiness().setAlertedUpdateVersion(versionCode);
                    final String updateUrl = appVersion.getUpdateUrl();

                    final CommonDialog commonDialog = new CommonDialog(activity);
                    commonDialog.setCanceledOnTouchOutside(false);
                    commonDialog.setTitle(activity.getString(R.string.check_version_app))
                            .setContent(appVersion.getChangeLog())
                            .setCancelText(activity.getString(R.string.cancle_txt))
                            .setOkText(activity.getString(R.string.check_update_version))
                            .setOkListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    Uri content_url = Uri.parse(updateUrl);
                                    intent.setData(content_url);
                                    activity.startActivity(intent);
                                    commonDialog.dismiss();
                                }
                            })
                            .setCancelListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    commonDialog.dismiss();
                                }
                            }).show();
                }

                //@Override
                //public void onFail(String s, int i) {}
                //@Override
                //public void onError(Exception e) {}
                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                    if (view != null) view.setEnabled(true);
                }
            });
        } else {
            com.iermu.ui.view.updateApp.updateAppUtils.checkToUpdate(activity, true);
        }
    }

//    StatUpdateAgent.checkUpdate(this, true, new CheckUpdateListener() {
//        @Override
//        public void checkUpdateResponse(KirinCheckState kirinCheckState, HashMap <String, String> stringStringHashMap) {
//            Logger.i("");
//            switch (kirinCheckState) {
//            case ALREADY_UP_TO_DATE:
//                break;
//            case NEWER_VERSION_FOUND:
//                break;
//            case ERROR_CHECK_VERSION:
//                break;
//            }
//        }
//    });
}
