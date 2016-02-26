package com.iermu.client;

import com.iermu.client.business.impl.AccountAuthBusImpl;
import com.iermu.client.business.impl.AccountAuthStrategy;
import com.iermu.client.business.impl.BaseBusiness;
import com.iermu.client.business.impl.CamAlarmBusImpl;
import com.iermu.client.business.impl.CamAlarmStrategy;
import com.iermu.client.business.impl.CamCommentImpl;
import com.iermu.client.business.impl.CamCommentStrategy;
import com.iermu.client.business.impl.CamSettingBusImpl;
import com.iermu.client.business.impl.CamSettingStrategy;
import com.iermu.client.business.impl.CamShareBusImpl;
import com.iermu.client.business.impl.CamShareStrategy;
import com.iermu.client.business.impl.CloudPlatformBusImpl;
import com.iermu.client.business.impl.CloudPlatformStrategy;
import com.iermu.client.business.impl.LYYAuthBusiness;
import com.iermu.client.business.impl.MessageCamBusImpl;
import com.iermu.client.business.impl.MessageCamStrategy;
import com.iermu.client.business.impl.MimeCamBusImpl;
import com.iermu.client.business.impl.MimeCamStrategy;
import com.iermu.client.business.impl.MineRecordBusImpl;
import com.iermu.client.business.impl.MineRecordStrategy;
import com.iermu.client.business.impl.PreferenceBusImpl;
import com.iermu.client.business.impl.PubCamBusImpl;
import com.iermu.client.business.impl.PubCamStrategy;
import com.iermu.client.business.impl.StatisticsBusImpl;
import com.iermu.client.business.impl.StatisticsBusStrategy;
import com.iermu.client.business.impl.StreamMediaBusiness;
import com.iermu.client.business.impl.StreamMediaStrategy;
import com.iermu.client.business.impl.UserCenterBusImpl;
import com.iermu.client.business.impl.UserCenterBusStrategy;
import com.iermu.client.business.impl.setupdev.SetupDevBusImpl;
import com.iermu.client.business.impl.setupdev.SetupDevStrategy;
import com.iermu.client.test.TestCamCommentBusImpl;
import com.iermu.client.test.TestCamSettingBusImpl;
import com.iermu.client.test.TestMessageCamBusImpl;
import com.iermu.client.test.TestMimeCamBusImpl;
import com.iermu.client.test.TestPubCamBusImpl;
import com.iermu.client.test.TestSetupDevImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务核心
 *
 *
 * Created by wcy on 15/6/22.
 */
public class ErmuBusiness {



    private static boolean TESTMODE_SETUPDEV    = false;    //ISetupDevBusiness是否为测试模式
    private static boolean TESTMODE_MIMECAM     = false;   //IMimeCamBusiness是否为测试模式
    private static boolean TESTMODE_MESSAGE     = true;  //IMessageCamBusiness是否为测试模式
    private static boolean TESTMODE_PUBCAM      = false;     //IPubCamBusiness是否为测试模式
    private static boolean TESTMODE_CAMSETTING  = false;    //ICamSettingBusiness是否为测试模式
    private static boolean TESTMODE_BAIDUAUTH   = true;    //IBaiduAuthBusiness是否为测试模式
    private static boolean TESTMODE_COMMENT     = false;    //IPubCamCommentBusiness是否为测试模式
    private static boolean TESTMODE_USERCENTER  = false;    //IUserCenterBusiness是否为测试模式

    private static Map<Class, BaseBusiness> devMap = new HashMap<Class, BaseBusiness>();
    private static ErmuBusiness ermuBusiness;

    /**
     * 业务模块注册
     */
    static {
        devMap.put(IAccountAuthBusiness.class, new AccountAuthBusImpl());
        devMap.put(LYYAuthBusiness.class,      new LYYAuthBusiness());
        devMap.put(IPreferenceBusiness.class,   new PreferenceBusImpl());
        devMap.put(ISetupDevBusiness.class,     TESTMODE_SETUPDEV   ? new TestSetupDevImpl()        : new SetupDevBusImpl());
        devMap.put(IMimeCamBusiness.class,      TESTMODE_MIMECAM    ? new TestMimeCamBusImpl()      : new MimeCamBusImpl());
        devMap.put(IPubCamBusiness.class,       TESTMODE_PUBCAM     ? new TestPubCamBusImpl()       : new PubCamBusImpl());
        devMap.put(IMessageCamBusiness.class,   TESTMODE_MESSAGE    ? new TestMessageCamBusImpl()   : new MessageCamBusImpl());
        devMap.put(ICamSettingBusiness.class,   TESTMODE_CAMSETTING ? new TestCamSettingBusImpl()   : new CamSettingBusImpl());
        devMap.put(IPubCamCommentBusiness.class,TESTMODE_COMMENT    ? new TestCamCommentBusImpl()   : new CamCommentImpl());
        devMap.put(ICloudPaltformBusiness.class, new CloudPlatformBusImpl());
        devMap.put(IStreamMediaBusiness.class, new StreamMediaBusiness());
        devMap.put(ICamShareBusiness.class, new CamShareBusImpl());
        devMap.put(IMineRecordBusiness.class, new MineRecordBusImpl());
        devMap.put(IStatisticsBusiness.class, new StatisticsBusImpl());
        devMap.put(IUserCenterBusiness.class, new UserCenterBusImpl());
        devMap.put(ICamAlarmBusiness.class, new CamAlarmBusImpl());
    }

    private static ErmuBusiness getInstance() {
        if(ermuBusiness == null) {
            synchronized (ErmuBusiness.class) {
                if(ermuBusiness == null) {
                    ermuBusiness = new ErmuBusiness();
                }
            }
        }
        return ermuBusiness;
    }

    /**
     * 添加(安装)摄像机设备业务
     * @return
     */
    public static ISetupDevBusiness getSetupDevBusiness() {
        ISetupDevBusiness business = (ISetupDevBusiness) getInstance().getBusiness(ISetupDevBusiness.class);
        SetupDevStrategy strategy = new SetupDevStrategy(business);
        return strategy;
    }

    /**
     * 我的耳目
     * @return
     */
    public static IMimeCamBusiness getMimeCamBusiness() {
        IMimeCamBusiness business = (IMimeCamBusiness) getInstance().getBusiness(IMimeCamBusiness.class);
        MimeCamStrategy strategy = new MimeCamStrategy(business);
        return strategy;
    }

    /**
     * 公共频道业务
     * @return
     */
    public static IPubCamBusiness getPubCamBusiness() {
        IPubCamBusiness business = (IPubCamBusiness) getInstance().getBusiness(IPubCamBusiness.class);
        PubCamStrategy strategy = new PubCamStrategy(business);
        return strategy;
    }

    public static IMessageCamBusiness getMessageCamBusiness() {
        IMessageCamBusiness business = (IMessageCamBusiness) getInstance().getBusiness(IMessageCamBusiness.class);
        MessageCamStrategy strategy = new MessageCamStrategy(business);
        return strategy;
    }

    public static synchronized ICamSettingBusiness getCamSettingBusiness() {
        ICamSettingBusiness business = (ICamSettingBusiness) getInstance().getBusiness(ICamSettingBusiness.class);
        CamSettingStrategy strategy = new CamSettingStrategy(business);
        return strategy;
    }

    public static LYYAuthBusiness getLYYAuthBusiness() {
        LYYAuthBusiness business = (LYYAuthBusiness) getInstance().getBusiness(LYYAuthBusiness.class);
        return business;
    }

    public static IAccountAuthBusiness getAccountAuthBusiness() {
        IAccountAuthBusiness business = (IAccountAuthBusiness) getInstance().getBusiness(IAccountAuthBusiness.class);
        AccountAuthStrategy strategy = new AccountAuthStrategy(business);
        return strategy;
    }

    public static IStreamMediaBusiness getStreamMediaBusiness() {
        IStreamMediaBusiness business = (IStreamMediaBusiness) getInstance().getBusiness(IStreamMediaBusiness.class);
        StreamMediaStrategy strategy = new StreamMediaStrategy(business);
        return strategy;
    }

    public static IPubCamCommentBusiness getPubCamCommentBusiness() {
        IPubCamCommentBusiness business = (IPubCamCommentBusiness) getInstance().getBusiness(IPubCamCommentBusiness.class);
        CamCommentStrategy strategy = new CamCommentStrategy(business);
        return strategy;
    }

    public static ICloudPaltformBusiness getCloudPlatFormBusiness() {
        ICloudPaltformBusiness business = (ICloudPaltformBusiness) getInstance().getBusiness(ICloudPaltformBusiness.class);
        CloudPlatformStrategy strategy = new CloudPlatformStrategy(business);
        return strategy;
    }

    public static IPreferenceBusiness getPreferenceBusiness() {
        IPreferenceBusiness business = (IPreferenceBusiness) getInstance().getBusiness(IPreferenceBusiness.class);
        return business;
    }

    public static ICamShareBusiness getShareBusiness() {
        ICamShareBusiness business = (ICamShareBusiness) getInstance().getBusiness(ICamShareBusiness.class);
        CamShareStrategy strategy = new CamShareStrategy(business);
        return strategy;
    }

    public static IMineRecordBusiness getMineRecordBusiness() {
        IMineRecordBusiness business = (IMineRecordBusiness) getInstance().getBusiness(IMineRecordBusiness.class);
        MineRecordStrategy strategy = new MineRecordStrategy(business);
        return strategy;
    }

    public static IStatisticsBusiness getStatisticsBusiness() {
        IStatisticsBusiness business = (IStatisticsBusiness) getInstance().getBusiness(IStatisticsBusiness.class);
        StatisticsBusStrategy strategy = new StatisticsBusStrategy(business);
        return strategy;
    }

    public static IUserCenterBusiness getUserCenterBusiness() {
        IUserCenterBusiness business = (IUserCenterBusiness) getInstance().getBusiness(IUserCenterBusiness.class);
        UserCenterBusStrategy strategy = new UserCenterBusStrategy(business);
        return strategy;
    }

    public static ICamAlarmBusiness getCamAlarmBusiness() {
        ICamAlarmBusiness business = (ICamAlarmBusiness) getInstance().getBusiness(ICamAlarmBusiness.class);
        CamAlarmStrategy strategy = new CamAlarmStrategy(business);
        return strategy;
    }

    private BaseBusiness getBusiness(Class clz) {
        return devMap.get(clz);
    }


}
