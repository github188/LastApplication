package com.iermu.client.business.impl.setupdev;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
* 有线直连扫描模式
*
* Created by wcy on 15/11/30.
*/
public class WifiDirectScanRunnable extends Thread {

    // for wifi direct
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager          mP2pManager;
    private WifiP2pManager.Channel  mP2pChannel;
    private BroadcastReceiver       mP2pReceiver;
    private Context mContext;

    public WifiDirectScanRunnable(Context context) {
        this.mContext = context;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try{
            if(mP2pReceiver!=null) mContext.unregisterReceiver(mP2pReceiver);
        } catch(Exception e){
            e.printStackTrace();
        }
        mP2pChannel = null;
        mP2pManager = null;
        mP2pReceiver = null;
    }

    @Override
    public void run() {
        scanDevByDirect();
    }

    // wifi direct模式扫描摄像头
    private void scanDevByDirect(){
        mP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        mP2pChannel = mP2pManager.initialize(mContext, mContext.getMainLooper(), null);
        mP2pReceiver = new WiFiDirectBroadcastReceiver(mP2pManager, mP2pChannel); /*, h*/

        mContext.registerReceiver(mP2pReceiver, intentFilter);
        mP2pManager.discoverPeers(mP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {}
            @Override
            public void onFailure(int reason) {}
        });
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                h.sendEmptyMessage(110);
//            }
//        }, 4000);
    }


    /**
     * A BroadcastReceiver that notifies of important wifi p2p events.
     *
     * Created by wcy on 15/6/22.
     */
    public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
        private WifiP2pManager manager;
        private WifiP2pManager.Channel channel;
        //private Handler m_h;
        /**
         * @param manager
         *            WifiP2pManager system service
         * @param channel
         *            Wifi p2p channel
         */
        public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel /*,Handler h*/) {
            super();
            this.manager = manager;
            this.channel = channel;
//            m_h = h;
        }

        /**
         * (non-Javadoc)
         *
         * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
         *      android.content.Intent)
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("tanhx", "receive:" + action);
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // UI update to indicate wifi p2p status.
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi Direct mode is enabled
                } else {
                }
                Log.d("tanhx", "P2P state changed - " + state);
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                if (manager != null) {
                    manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            Log.d("tanhx", "onPeersAvailable dev: " + peers);
//                            if(m_h==null) return;
//                            Message msg = new Message();
//                            msg.what = 100;
//                            msg.obj = peers;
//                            m_h.sendMessage(msg);
                        }
                    });
                }
                Log.d("tanhx", "P2P peers changed");
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                if (manager == null) {
                    return;
                }
                final NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    // we are connected with the other device, request connection
                    // info to find group owner IP
                    manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(WifiP2pInfo info) {
                            Log.d("tanhx", "conn dev: " + info.groupOwnerAddress.getHostAddress());
//                            if(m_iConnType!=2) return;
//                            Message msg = new Message();
//                            msg.what = MSG_WIFI_PWD_OK;
//                            //msg.obj = info;
//                            m_handler.sendMessage(msg);
                        }
                    });
                } else {
                    // It's a disconnect
                    Log.d("tanhx", "conn dev:" + networkInfo.getState().toString());
//                    if(m_SetIpcStep==CONN_IPC_AP_2){
//                        disconnDevByDirect();
//                        m_iConnType = 0;
//                        m_handler.sendEmptyMessage(MSG_WIFI_DIRECT_CONN_FAIL);
//                    }
//                    else if(m_SetIpcStep==CONN_MYWIFI_AP_2) m_handler.sendEmptyMessage(MSG_WIFI_PWD_OK);
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
                    .equals(action)) {
            }
        }
    }
}
