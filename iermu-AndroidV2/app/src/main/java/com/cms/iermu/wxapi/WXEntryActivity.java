/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.cms.iermu.wxapi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.cms.iermu.cmsUtils;
import com.iermu.client.util.Logger;
import com.iermu.ui.activity.MainActivity;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;

/** 微信客户端回调activity示例 */
public class WXEntryActivity extends WechatHandlerActivity implements IWXAPIEventHandler {

	private static IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//微信使用
		api = WXAPIFactory.createWXAPI(this, "wx885bb48c38bf16f4", false);
		api.handleIntent(getIntent(), this);

	}

	//ShareSDK
	/**
	 * 处理微信发出的向第三方应用请求app message
	 * <p>
	 * 在微信客户端中的聊天页面有“添加工具”，可以将本应用的图标添加到其中
	 * 此后点击图标，下面的代码会被执行。Demo仅仅只是打开自己而已，但你可
	 * 做点其他的事情，包括根本不打开任何页面
	 */
	public void onGetMessageFromWXReq(WXMediaMessage msg) {
//		Toast.makeText(this, "getMessage: "+msg.title, Toast.LENGTH_SHORT).show();
		Intent iLaunchMyself = getPackageManager().getLaunchIntentForPackage(getPackageName());
		startActivity(iLaunchMyself);
	}

	//ShareSDK
	/**
	 * 处理微信向第三方应用发起的消息
	 * <p>
	 * 此处用来接收从微信发送过来的消息，比方说本demo在wechatpage里面分享
	 * 应用时可以不分享应用文件，而分享一段应用的自定义信息。接受方的微信
	 * 客户端会通过这个方法，将这个信息发送回接收方手机上的本demo中，当作
	 * 回调。
	 * <p>
	 * 本Demo只是将信息展示出来，但你可做点其他的事情，而不仅仅只是Toast
	 */
	public void onShowMessageFromWXReq(WXMediaMessage msg) {
//		Toast.makeText(this, "showMessage"+ msg.title, Toast.LENGTH_SHORT).show();
		if (msg != null && msg.mediaObject != null
				&& (msg.mediaObject instanceof WXAppExtendObject)) {
			WXAppExtendObject obj = (WXAppExtendObject) msg.mediaObject;
//			Toast.makeText(this, obj.extInfo, Toast.LENGTH_SHORT).show();
		}
	}

	//微信使用
	@Override
	public void onReq(BaseReq req) {
		Bundle bundle = new Bundle();
		req.toBundle(bundle);
		Logger.i("get req from wx:" + req.toString());
//		Toast.makeText(this,req.toString(),Toast.LENGTH_SHORT).show();
		switch (req.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
				Logger.i("----get message from wx:" + req.toString());
				goToGetMsg();
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
				Logger.i("----show message from wx:" + req.toString());
				goToShowMsg((ShowMessageFromWX.Req) req);
				break;
			default:
				break;
		}
	}

	//微信使用
	@Override
	public void onResp(BaseResp baseResp) {

	}

	//微信使用
	private void goToGetMsg() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtras(getIntent());
		startActivity(intent);
		finish();
	}

	//微信使用
	public static void shareToAuth(Context c, String strUrl, String strTitle, String strDesc, byte[] thumbImg){
		com.tencent.mm.sdk.openapi.WXAppExtendObject appdata = new com.tencent.mm.sdk.openapi.WXAppExtendObject();
		appdata.extInfo = strUrl;

		com.tencent.mm.sdk.openapi.WXMediaMessage msg = new com.tencent.mm.sdk.openapi.WXMediaMessage();
		msg.title = strTitle;
		msg.description = strDesc;
		if(thumbImg==null) {
			Bitmap img = BitmapFactory.decodeResource(c.getResources(), cmsUtils.getRes(c, "ic_launcher", "drawable"));
			thumbImg = cmsUtils.bmpToByteArray(img, true);
		}
		msg.thumbData = thumbImg;
		//msg.setThumbImage(thumbImg);
		msg.mediaObject = appdata; //appdata;

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("appdata");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		IWXAPI api = WXAPIFactory.createWXAPI(c, "wx885bb48c38bf16f4", false);
		api.registerApp("wx885bb48c38bf16f4");
		boolean ret = api.sendReq(req);
	}

	//微信使用
	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	//微信使用
	// 处理获取的微信请求
	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		com.tencent.mm.sdk.openapi.WXMediaMessage wxMsg = showReq.message;
		com.tencent.mm.sdk.openapi.WXAppExtendObject obj = (com.tencent.mm.sdk.openapi.WXAppExtendObject) wxMsg.mediaObject;

		StringBuffer msg = new StringBuffer(); // 组织一个待显示的消息内容
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);
		Log.d("tanhx", "get show msg is: " + msg);
		String extInfo = obj.extInfo;

		if(extInfo.contains("view_share.php")) {//strLiveplay xxxx
			MainActivity.actionShareAuth(this, extInfo);
		} else if(extInfo.contains("grant")){	//strLiveplay gran
			MainActivity.actionShareAuth(this, extInfo);
		}
	}

	public static boolean isWxInstalled(Context c){
		if(api==null){
			api = WXAPIFactory.createWXAPI(c, "wx885bb48c38bf16f4", false);
		}
		return api.isWXAppInstalled();
	}
}
