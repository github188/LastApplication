package com.iermu.client.test;

import com.iermu.client.IPubCamBusiness;
import com.iermu.client.business.impl.PubCamBusImpl;
import com.iermu.client.listener.OnPublicCamChangedListener;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.PubCamCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * 公开摄像头业务(测试类)
 *
 * Created by zhoushaopei on 15/6/26.
 */
public class TestPubCamBusImpl extends PubCamBusImpl implements IPubCamBusiness {
    private List<CamLive> camList = new ArrayList<CamLive>();       // 最热列表

    @Override
    public void syncNewCamList(String orderby) {
        List<CamLive> infos = new ArrayList<CamLive>();
        CamLive info1 = new CamLive();
        info1.setOwnerName("我的儿童房间");
        info1.setStatus(1);
        info1.setAvator("http://i.imgur.com/DvpvklR.png");
        info1.setThumbnail("http://i.imgur.com/DvpvklR.png");
        info1.setDescription("居庸关");
        info1.setPersonNum(32233);
        infos.add(info1);
        CamLive info2 = new CamLive();
        info2.setOwnerName("我的房间");
        info2.setStatus(2);
        info2.setAvator("http://i.imgur.com/DvpvklR.png");
        info1.setThumbnail("http://i.imgur.com/DvpvklR.png");
        info2.setDescription("长隆");
        info2.setPersonNum(32233);
        infos.add(info2);
        camList = infos;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendListener(OnPublicCamChangedListener.class);
    }
    @Override
    public void syncOldCamList(String orderby) {
        List<CamLive> infos = new ArrayList<CamLive>();
        CamLive info1 = new CamLive();
        info1.setOwnerName("敦煌阳关");
        info1.setStatus(3);
        info1.setAvator("http://i.imgur.com/DvpvklR.png");
        info1.setThumbnail("http://i.imgur.com/DvpvklR.png");
        info1.setDescription("长江");
        info1.setPersonNum(33);

        CamLive info2 = new CamLive();
        info2.setOwnerName("珠海长隆");
        info2.setStatus(4);
        info2.setAvator("http://i.imgur.com/DvpvklR.png");
        info1.setThumbnail("http://i.imgur.com/DvpvklR.png");
        info2.setDescription("大连");
        info2.setPersonNum(99999);

        CamLive info3 = new CamLive();
        info3.setOwnerName("小老虎");
        info3.setStatus(5);
        info3.setAvator("http://i.imgur.com/DvpvklR.png");
        info1.setThumbnail("http://i.imgur.com/DvpvklR.png");
        info3.setDescription("北京");
        info3.setPersonNum(7879990);

        CamLive info4 = new CamLive();
        info4.setOwnerName("教会");
        info4.setStatus(6);
        info4.setAvator("http://i.imgur.com/DvpvklR.png");
        info1.setThumbnail("http://i.imgur.com/DvpvklR.png");
        info4.setDescription("河南");
        info4.setPersonNum(8833);

        infos.add(info1);
        infos.add(info2);
        infos.add(info3);
        infos.add(info4);

        camList.addAll(infos);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendListener(OnPublicCamChangedListener.class);
    }

    @Override
    public List<CamLive> getCamList(String orderby) {
        return camList;
    }
}
