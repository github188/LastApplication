package com.iermu.ui.view.updateApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cms.iermu.cmsUtils;
import com.iermu.R;

import cn.pedant.sweetalert.SweetAlertDialog;


public class updateAppUtils {
	static AlertDialog m_showDlg;


	private static ProgressDialog pBar;
	//private static String downPath = "http://bcs.pubbcsapp.com/iermu-android/";  // 旧版本升级下载地址
	private static String downPath = "http://bj.bcebos.com/iermuapp/android/";
	private static String appName = "iErmu.apk";
	private static String appVersion = null;//"ver_iermu.json";  // 根据包名最后一个字段，设置升级文件
	private static int newVerCode = 0;
	private static String newVerName = "";
	private static String newVerTip = null;
	private static Handler m_handler = new Handler();
	private static long m_FileSize;
	private static long m_DownSize;
	private static boolean m_bUpdateApp = false;
	
	private static void initParams(Context c){
		if(appVersion!=null) return;
		
		String[] strTemp = cmsUtils.split(cmsUtils.getPkgname(c), ".");
		if(strTemp!=null){
			int ilen = strTemp.length;
			String str = strTemp[ilen-1];
			appVersion = "ver_" + str + ".json";
		}
		
	}
	
	// update
	// check new version and update
	public static void checkToUpdate(final Context c, final boolean bTip) {
		
		initParams(c);
		
		if(m_bUpdateApp) {
			Toast.makeText(c, c.getResources().getString(
					R.string.tip_update_app_running), Toast.LENGTH_SHORT).show();
			return;
		}
		
		// 提示信息
		final ProgressDialog cmsDlg = new ProgressDialog(c);
		if(bTip){
			//cmsDlg.setTitle(cmsUtils.getRes(c, "app_name", "string"));
			cmsDlg.setMessage(c.getResources().getString(R.string.tip_app_check_update));
			cmsDlg.setCanceledOnTouchOutside(false);
			cmsDlg.setOnDismissListener(null);
			cmsUtils.setPrgDlg(cmsDlg);
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean ret = getServerVersion();
				Message msg = new Message();
				msg.obj = ret;
				m_handler.sendMessage(msg);
			}
		}).start();
		
		m_handler = new Handler() {
			public void handleMessage(Message msg) {	
				if(bTip) cmsDlg.dismiss();
				boolean ret = (Boolean) msg.obj;
				if (ret) {
					int currentCode;
					try {
						currentCode = CurrentVersion.getVerCode(c);
						if (newVerCode>currentCode) {
							if(bTip){								
								// 弹出更新提示对话框
								showUpdateDialog(c);
							}
							else{ // 静默升级
								downAppFile(c, downPath + appName);
							}
						}
						else if(bTip){
							String strMsg = c.getResources().getString(
									R.string.tip_app_last_ver);
							Dialog dialog = new AlertDialog.Builder(c)
									.setTitle(c.getResources().getString(R.string.app_name))
									.setMessage(strMsg)
									.setPositiveButton(
											c.getResources().getString(R.string.btn_cam_ok),
											null).create();
							dialog.cancel();
						}
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	// show Update Dialog
	private static void showUpdateDialog(final Context c) throws NameNotFoundException {
		// TODO Auto-generated method stub
		StringBuffer sbTitil = new StringBuffer();
		sbTitil.append(c.getResources().getString(R.string.tip_update_app_have_new));
		sbTitil.append("：");
		sbTitil.append(newVerName);
		/*sbTitil.append("，");
		sbTitil.append(c.getResources().getString(cmsUtils.getRes(c, "tip_update_app_yorno", "string")));
		sbTitil.append("？");	*/	
		StringBuffer sb = new StringBuffer();
		if(newVerTip!=null){
			sb.append(newVerTip);
		}
		else{
			sb.append("");
		}
		
		final SweetAlertDialog dialog = new SweetAlertDialog(c);
		dialog.setTitleText(sbTitil.toString());
		dialog.setContentText(sb.toString());
//		dialog.setPositiveButton(c.getResources().getString(R.string.tip_update_app_yes),
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								showProgressBar(c);// 更新当前版本
//							}
//				})
//				.setNegativeButton(c.getResources().getString(R.string.tip_update_app_no), null).create();
		dialog.setConfirmText(c.getResources().getString(R.string.tip_update_app_yes));
		dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
					showProgressBar(c);//更新当前版本
				dialog.cancel();
			}
		});
		dialog.setCancelText(c.getResources().getString(R.string.tip_update_app_no));
		dialog.setCancelClickListener(null);
		dialog.show();
	}

	protected static void showProgressBar(final Context c) {
		pBar = new ProgressDialog(c);
		pBar.setTitle(c.getResources().getString(
				R.string.tip_update_app_title));
		pBar.setMessage(c.getResources().getString(
				R.string.tip_update_app_wait));
		pBar.setCancelable(false);
		pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pBar.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				pBar.cancel();
				// 提示，到后台下载模式
				Toast.makeText(c, c.getResources().getString(
						R.string.tip_update_app_back), Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		downAppFile(c, downPath + appName);
		final Handler handler = new Handler();  
	    Runnable runnable = new Runnable(){  
	         @Override  
	         public void run() {
	        	 if(pBar.isShowing()){
	        		 if(m_FileSize>0 && m_DownSize<m_FileSize){
			             pBar.setMessage(m_DownSize*100/m_FileSize + 
			            		 "% (" + Long.toString(m_DownSize/1024) + "k/" + 
			            		 Long.toString(m_FileSize/1024) + "k), " + 
			            		 c.getResources().getString(
			            					R.string.tip_update_app_wait));
			             //Log.d("tanhx", "down " + m_DownSize);
			             handler.postDelayed(this, 1000);
	        		 }
	        	 }
	         }   
	    };   
	    handler.postDelayed(runnable, 1000);
	}


	private static boolean getServerVersion() {
		try {
			appVersion = ("https://pcs.baidu.com/rest/2.0/pcs/device").equals("https://58.67.194.222/rest/2.0/pcs/device")? "ver_iermu_cms.json" : "ver_iermu.json";
			String newVerJSON = GetUpdateInfo.getUpdataVerJSON(downPath
					+ appVersion);
			JSONArray jsonArray = new JSONArray(newVerJSON);
			if (jsonArray.length() > 0) {
				JSONObject obj = jsonArray.getJSONObject(0);
				try {
					newVerCode = Integer.parseInt(obj.getString("verCode"));
					newVerName = obj.getString("verName");
					newVerTip = obj.getString("verTip");
					appName = obj.getString("apkname");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	protected static void downAppFile(final Context c, final String url) {
		if(pBar!=null) {
			pBar.show();
			cmsUtils.setPrgDlg(pBar);
		}
		new Thread() {
			public void run() {
				m_bUpdateApp = true;
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					m_FileSize = entity.getContentLength();
					//Log.d("DownTag", Long.toString(length));
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is == null) {
						throw new RuntimeException("isStream is null");
					}
					File file = new File(
							Environment.getExternalStorageDirectory(), appName);
					
					fileOutputStream = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int ch = -1;
					m_DownSize = 0;
					do {
						ch = is.read(buf);
						if (!m_bUpdateApp) return;
						if (ch <= 0)
							break;
						fileOutputStream.write(buf, 0, ch);
						m_DownSize+=ch;
					} while (true);
					fileOutputStream.flush();
					is.close();
					m_bUpdateApp = false;
					fileOutputStream.close();
					haveDownLoad(c);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					//Log.d("tanhx", e.getMessage());
					e.printStackTrace();
				}
			}
		}.start();
	}

	// cancel progressBar and start new App
	protected static void haveDownLoad(final Context c) {
		m_handler.post(new Runnable() {
			public void run() {
				if(pBar!=null) pBar.cancel();
				installNewApk(c);
			}
		});
	}

	// 安装新的应用
	protected static void installNewApk(Context c) {
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // android4.0+需要增加这条语句
															// 才能显示安装成功的提示！！
			intent.setDataAndType(Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), appName)),
					"application/vnd.android.package-archive");
			c.startActivity(intent);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
