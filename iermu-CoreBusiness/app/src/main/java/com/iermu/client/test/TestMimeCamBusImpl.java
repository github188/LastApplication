package com.iermu.client.test;

import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.business.impl.MimeCamBusImpl;
import com.iermu.client.listener.OnMimeCamChangedListener;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.ShareType;
import com.iermu.client.model.viewmodel.CollectCamItem;
import com.iermu.client.model.viewmodel.MimeCamItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的摄像头(测试类)
 *
 * Created by zhoushaopei on 15/6/24.
 */
public class TestMimeCamBusImpl extends MimeCamBusImpl implements IMimeCamBusiness {

    private List<MimeCamItem> list = new ArrayList<MimeCamItem>();

    @Override
    public void syncNewCamList() {

//        MimeCamItem info1 = new MimeCamItem();
//        info1.setItemType(MimeCamItem.TYPE_MIME);
//        info1.setItem(new CamLive("zhjfjdk", "fjdjfajfk", "fhefiajff", "fdsjfkdjfksdhjkf"
//                , ShareType.PRI_HAVCLOUD, 1, "http://i.imgur.com/DvpvklR.png"
//                , 1, ConnectType.BAIDU, "", "", "", 0, "", "", 0, "0", 0, "fdaf", "fdaf"));
//
//        MimeCamItem info2 = new MimeCamItem();
//        info2.setItemType(MimeCamItem.TYPE_AUTHORIZE);
//        info2.setItem(new CamLive("zhjfjdk", "fjdjfajfk", "fhefiajff", "fdsjfkdjfksdhjkf"
//                , ShareType.PRI_HAVCLOUD, 2, "http://i.imgur.com/DvpvklR.png"
//                , 1, ConnectType.BAIDU, "", "", "", 0, "", "", 0, "0", 0, "fdaf", "fdaf"));
//
//
//        MimeCamItem info3 = new MimeCamItem();
//        info3.setItemType(MimeCamItem.TYPE_COLLECT);
//        info3.setItem(new CamLive("zhjfjdk", "fjdjfajfk", "fhefiajff", "fdsjfkdjfksdhjkf"
//                , ShareType.PRI_HAVCLOUD, 3, "http://i.imgur.com/DvpvklR.png"
//                , 1, ConnectType.BAIDU, "", "", "", 0, "", "", 0, "0", 0, "fdaf", "fdaf"));
//
//        CamLive aa = new CamLive("zhjfjdk", "fjdjfajfk", "fhefiajff", "fdsjfkdjfksdhjkf"
//                , ShareType.PRI_HAVCLOUD, 4, "http://i.imgur.com/DvpvklR.png"
//                , 1, ConnectType.BAIDU, "", "", "", 0, "", "", 0, "0", 0, "fdaf", "fdaf");
//        CamLive bb = new CamLive("zhjfjdk", "fjdjfajfk", "fhefiajff", "fdsjfkdjfksdhjkf"
//                , ShareType.PRI_HAVCLOUD, 5, "http://i.imgur.com/DvpvklR.png"
//                , 1, ConnectType.BAIDU, "", "", "", 0, "", "", 0, "0", 0, "fdaf", "fdaf");
//        CollectCamItem info4 = new CollectCamItem(aa,bb);
//
//        list.add(info1);
//        list.add(info2);
//        list.add(info3);
//        list.add(info4);
//
//        sendListener(OnMimeCamChangedListener.class);
    }

    @Override
    public void syncOldCamList() {
//        MimeCamItem info1 = new MimeCamItem();
//        info1.setItemType(MimeCamItem.TYPE_MIME);
//        info1.setItem(new CamLive("zhjfjdk", "fjdjfajfk", "fhefiajff", "fdsjfkdjfksdhjkf"
//                , ShareType.PRI_HAVCLOUD, 1, "http://i.imgur.com/DvpvklR.png"
//                , 1, ConnectType.BAIDU, "", "", "", 0, "", "", 0, "0", 0, "fdaf", "fdaf"));
//
//        MimeCamItem info2 = new MimeCamItem();
//        info2.setItemType(MimeCamItem.TYPE_AUTHORIZE);
//        info2.setItem(new CamLive("zhjfjdk", "fjdjfajfk", "fhefiajff", "fdsjfkdjfksdhjkf"
//                , ShareType.PRI_HAVCLOUD, 2, "http://i.imgur.com/DvpvklR.png"
//                , 1, ConnectType.BAIDU, "", "", "", 0, "", "", 0, "0", 0, "fdaf", "fdaf"));
//
//
//        MimeCamItem info3 = new MimeCamItem();
//        info3.setItemType(MimeCamItem.TYPE_COLLECT);
//        info3.setItem(new CamLive("zhjfjdk", "fjdjfajfk", "fhefiajff", "fdsjfkdjfksdhjkf"
//                , ShareType.PRI_HAVCLOUD, 3, "http://i.imgur.com/DvpvklR.png"
//                , 1, ConnectType.BAIDU, "", "", "", 0, "", "", 0, "0", 0, "fdaf", "fdaf"));
//
//        CamLive aa = new CamLive("zhjfjdk", "fjdjfajfk", "fhefiajff", "fdsjfkdjfksdhjkf"
//                , ShareType.PRI_HAVCLOUD, 4, "http://i.imgur.com/DvpvklR.png"
//                , 1, ConnectType.BAIDU, "", "", "", 0, "", "", 0, "0", 0, "fdaf", "fdaf");
//        CamLive bb = new CamLive("zhjfjdk", "fjdjfajfk", "fhefiajff", "fdsjfkdjfksdhjkf"
//                , ShareType.PRI_HAVCLOUD, 5, "http://i.imgur.com/DvpvklR.png"
//                , 1, ConnectType.BAIDU, "", "", "", 0, "", "", 0, "0", 0, "fdaf", "fdaf");
//        CollectCamItem info4 = new CollectCamItem(aa,bb);
//
//        list.add(info1);
//        list.add(info2);
//        list.add(info3);
//        list.add(info4);
//
//        sendListener(OnMimeCamChangedListener.class);
    }

    @Override
    public List<MimeCamItem> getCamItemList() {
        return list;
    }
}
