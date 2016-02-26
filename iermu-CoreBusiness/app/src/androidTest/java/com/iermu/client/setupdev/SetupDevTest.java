package com.iermu.client.setupdev;

import android.net.wifi.ScanResult;
import android.test.AndroidTestCase;

import com.iermu.client.ErmuBusiness;
import com.iermu.client.ISetupDevBusiness;
import com.iermu.client.business.impl.setupdev.SetupDevBusImpl;
import com.iermu.client.listener.OnSetupDevListener;
import com.iermu.client.model.CamDev;

import java.util.List;

/**
 *  添加(安装配置)设备流程测试
 *
 * Created by wcy on 15/6/21.
 */
public class SetupDevTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected void runTest() throws Throwable {
        super.runTest();
    }

    public void testSetupDev() {
        ISetupDevBusiness bus = new SetupDevBusImpl();
        bus.addSetupDevListener(mListener);

        ISetupDevBusiness business = ErmuBusiness.getSetupDevBusiness();
//        business.scanCam();
    }
    //@MediumTest

    static OnSetupDevListener mListener = new OnSetupDevListener() {
        @Override
        public void onScanCamList(List<CamDev> list) {
            super.onScanCamList(list);
        }

//        @Override
//        public void onScanWifiList(List<ScanResult> list) {
//            super.onScanWifiList(list);
//        }

        @Override
        public void onScanFail(int businessCode, String message) {
            super.onScanFail(businessCode, message);
        }

//        @Override
//        public void onSetupDev(boolean success) {
//            super.onSetupDev(success);
//        }
    };


}
