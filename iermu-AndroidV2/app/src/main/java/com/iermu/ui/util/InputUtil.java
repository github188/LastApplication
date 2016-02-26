package com.iermu.ui.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 输入法工具类
 * @author wcy
 *
 */
public class InputUtil {

	/**
	 * 隐藏软键盘
	 * @param ctx
	 * @param edit
	 */
	public static void hideSoftInput(Activity ctx, View edit){
		if(edit != null) {
			edit.clearFocus();
			InputMethodManager mInputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			mInputManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);
		}
	}

	/**
	 * 弹出软键盘
	 * @param ctx
	 * @param edit
	 */
	public static void showSoftInput(Activity ctx, EditText edit){
		if(edit != null) {
			edit.setFocusable(true);
			edit.requestFocus();
			InputMethodManager mInputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			mInputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
