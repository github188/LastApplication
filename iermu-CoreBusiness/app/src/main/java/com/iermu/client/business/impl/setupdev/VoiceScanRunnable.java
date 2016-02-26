package com.iermu.client.business.impl.setupdev;

/**
* 发送声波模式
*
* Created by wcy on 15/11/30.
*/
public class VoiceScanRunnable extends Thread {

//    static final int[] CMS_FREQ = {8000, 15000};  // 音频频率的基频   8000/15000
//    VoicePlayer mVoicePlayer;
//    private String SSID;
//    private String wifiPwd;
//    private int wifiType;
//    private String wifiAccount;
//
//    public VoiceScanRunnable(Context context, String ssid, String wifiPwd, int wifiType, String wifiAccount){
//        this.SSID = ssid;
//        this.wifiPwd = wifiPwd;
//        this.wifiType = wifiType;
//        this.wifiAccount = wifiAccount;
//    }
//
//    @Override
//    public void run() {
//        int iLen = 19;
//        int[][] freqs1 = new int[2][iLen];
//        for(int i=0; i<iLen; i++){
//            freqs1[0][i] = CMS_FREQ[0] + i*200;
//            freqs1[1][i] = CMS_FREQ[1] + i*200;
//        }
//        if(mVoicePlayer==null){
//            mVoicePlayer = new VoicePlayer();
//        } else{
//            mVoicePlayer.stop();
//        }
//        String str1 = SSID; // 需要控制每一段不能超过32字节
//        String str2 = wifiPwd;
//        String str3 = null;
//        if(wifiType==2){  // wep  "\n"
//            str3 = "1";
//        }
//        else if(wifiType==4){ // eap  "\n"
//            str3 = "2"+wifiAccount;
//        }
//        if(str3!=null){ // 拼接
//            String str = "\n";
//            int iLeftLen1 = 32-str1.length();
//            int iLeftLen2 = 32-str2.length();
//            int ilen3 = str3.length()+1;
//            if(ilen3<=iLeftLen1){
//                str1 += (str+str3);
//            }
//            else if(ilen3+1<=64){
//                String str31 = str3.substring(0, iLeftLen1-1);
//                String str32 = str3.substring(iLeftLen1);
//                str1 += (str+str31);
//                str2 += (str+str32);
//            }
//            else{ // 提示用户确认设备是否处于配置状态
//                return;
//            }
//        }
//        Log.d("tanhx", "voice comm:" + str1 + ", " + str2);
//        mVoicePlayer.setFreqs(freqs1[0]);
//        mVoicePlayer.play(DataEncoder.encodeSSIDWiFi(str1, str2), 1, 0);
//        mVoicePlayer.setFreqs(freqs1[1]);
//        mVoicePlayer.play(DataEncoder.encodeSSIDWiFi(str1, str2), 1, 0);
//		/*
//		//player.setVolume(1.2);
//		//player.getFreqs();
//		m_VoicePlayer.setFreqs(freqs1[0]);
//		int[] _freqs = m_VoicePlayer.getFreqs();
//		for(int i=0; i<_freqs.length; i++){
//			Log.d("tanhx", "freq[" + i + "]=" + _freqs[i]);
//		}
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				//while(!m_bStartSetDev && !m_bExitSet && !m_bExitThread){
//				Log.d("tanhx", "ssid=" + m_strSSID + ", pwd=" + m_strWifiPwd);
//					if(m_VoicePlayer.getPlayerState()==m_VoicePlayer.STATE_STOP)
//						m_VoicePlayer.play(NumberEncoder.encodeSSIDWiFi(m_strSSID, m_strWifiPwd), 2, 0);
//					//SystemClock.sleep(500);
//				//}
//			}
//		}).start();*/
//    }
}
