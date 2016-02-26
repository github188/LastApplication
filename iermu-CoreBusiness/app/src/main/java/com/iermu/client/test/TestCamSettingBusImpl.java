package com.iermu.client.test;

import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.business.impl.CamSettingBusImpl;
import com.iermu.client.model.CamLive;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wcy on 15/7/2.
 */
public class TestCamSettingBusImpl extends CamSettingBusImpl implements ICamSettingBusiness {

    private List<CamLive> list = new ArrayList<CamLive>();

    @Override
    public void syncCamCloud() {
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy.MM.dd");
            CamLive cloud = new CamLive();
            Date date = myFormatter.parse("2016.09.1");
            cloud.setCvrEndTime(19039374);
            cloud.setDescription("姥姥家0");
            cloud.setDeviceId("000000000");
            list.add(cloud);

            CamLive cloud1 = new CamLive();
            Date date1= myFormatter.parse("2018.08.1");
            cloud1.setCvrEndTime(19039374);
            cloud1.setDescription("姥姥家1");
            cloud1.setDeviceId("1111111111");
            list.add(cloud1);

            CamLive cloud2 = new CamLive();
            Date date2= myFormatter.parse("2002.08.1");
            cloud2.setCvrEndTime(19039374);
            cloud2.setDescription("姥姥家2");
            cloud2.setDeviceId("2222222222");
            list.add(cloud2);

            CamLive cloud3 = new CamLive();
            Date date3= myFormatter.parse("2004.10.10");
            cloud3.setCvrEndTime(19039374);
            cloud3.setDescription("姥姥家3");
            cloud3.setDeviceId("33333333333");
            list.add(cloud3);

            CamLive cloud4 = new CamLive();
            Date date4= myFormatter.parse("2018.08.1");
            cloud4.setCvrEndTime(19039374);
            cloud4.setDescription("姥姥家4");
            cloud4.setDeviceId("4444444444");
            list.add(cloud4);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CamLive> getCamCloud() {
        return list;
    }
}
