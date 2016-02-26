package com.iermu.ui.view.updateApp;

import com.cms.iermu.cmsUtils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class CurrentVersion {
	private static final String TAG = "Config";

	public static int getVerCode(Context context) throws NameNotFoundException {
		int verCode = -1;
		try {
			verCode = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return verName;
	}

	public static String getAppName(Context context) {
		String appName = context.getResources().getText(cmsUtils.getRes(context, "app_name", "string"))
				.toString();
		return appName;
	}
}
