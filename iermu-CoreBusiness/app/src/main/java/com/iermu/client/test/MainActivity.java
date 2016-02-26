package com.iermu.client.test;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.R;
import com.iermu.client.listener.OnSetupDevListener;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;

import java.util.List;

/**
 * 
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ErmuBusiness.getMimeCamBusiness().syncNewCamList();

        //ErmuBusiness.getSetupDevBusiness().registerSetupDevListener(listener);
        //ErmuBusiness.getSetupDevBusiness().scanCam();

//        CamDev camDev = new CamDev();
//        camDev.setDevID("137892434827");
//        camDev.setDevType(CamDevType.TYPE_ETH);
//        ErmuBusiness.getSetupDevBusiness().connectCam(camDev);

        //ErmuBusiness.getCamSettingBusiness().getCamSettings("137893001259");
        ErmuBusiness.getCamSettingBusiness().getCamInfo("137893001259");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    static OnSetupDevListener listener = new OnSetupDevListener() {
        @Override
        public void onScanCamList(List<CamDev> list) {
            super.onScanCamList(list);
            if(list.size()<=0) return;
            CamDev camDev = list.get(0);
            String ssid         = "iermu-1601";
            String wifiAccount  = "cmstest";
            String pwd          = "cms19981111";
            boolean connAp      = true;
            String ip           = "192.168.1.1";
            String netmask      = "255.255.255.0";
            String gateway      = "192.168.1.111";

            CamDevConf conf = new CamDevConf(ssid);
            conf.setWifiAccount(wifiAccount);
            conf.setWifiPwd(pwd);
            conf.setDhcpIP(ip);
            conf.setDhcpGateway(netmask);
            conf.setDhcpNetmask(gateway);
            ErmuBusiness.getSetupDevBusiness().connectCam(camDev, connAp, conf);
        }

//        @Override
//        public void onSetupDev(boolean success) {
//            super.onSetupDev(success);
//            Toast.makeText(ErmuApplication.getContext(), "成功!", Toast.LENGTH_SHORT).show();
//        }

        @Override
        public void onScanFail(int businessCode, String message) {
            super.onScanFail(businessCode, message);
            Toast.makeText(ErmuApplication.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    };

}
