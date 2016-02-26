package com.iermu.ui.fragment;

import android.view.KeyEvent;

/**
 * FragmentActivity帮助类
 *  1. 键盘回调事件函数
 *
 *
 * @author wcy
 *
 */
public interface FragmentActivityHelper {

	/**
	 * 键盘事件回调函数
	 * @param keyCode	键值码
	 * @param event		事件
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event);


}
