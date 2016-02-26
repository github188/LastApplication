
package com.cms.iermu.cms;

public final class CmsConstants {
	
	/*<!-- 
    for baidu server
    1、package="com.cms.iermu" 
    2、settings
    3、android:label="@string/app_name
    -->
    
    <!-- 
    for cms server
    1、package="com.cms.myiermu"
    2、settings
    3、android:label="@string/app_name_cms
     -->*/
	
	public static final String BD_IERMU_PKG = "com.cms.iermu";
	public static final String CMS_IERMU_PKG = "com.cms.myiermu";
	public static final String QDLT_IERMU_PKG = "com.cms.iermu_lt"; // for 青岛联通 联通爱视界
	
	public static final boolean IS_QDLT = false;  // 青岛联通
	
	/*	
    public static final String BD_IERMU_HD_PKG = "com.cms.iermu";
	public static final String CMS_IERMU_HD_PKG = "com.cms.myiermu_hd";
	*/
	
	public static final int COLOR_REC_NOR = 0x8017afeb;  // 普通录像颜色
	public static final int COLOR_REC_ALARM = 0x4000ff00;// 报警录像颜色
	public static final String ADD_NEW_DEV = "add_new_dev";
	public static final String RESET_DEV_WIFI = "reset_dev_wifi";
	public static final String ADD_DEV_ID = "scan_dev";
	public static final String ADD_DEV_STREAMID = "scan_dev_streamid";
	
	public static final int VOD_NUM = 10;
	public static final int START_YEAR = 1990, END_YEAR = 2100;
	
	public static final int PUBCAM_RECOMMOND = 0;
	public static final int PUBCAM_HOT = 1;
	public static final int PUBCAM_NEW = 2;
	
	public static final int RECPLAY_THUMB = 0;
	public static final int RECPLAY_TIMELINE = 1;
	
	public static final int PLAY_MY_CAM = 0;
	public static final int PLAY_PUB_CAM = 1;
	public static final int PLAY_FAV_CAM = 10;
	public static final int PLAY_AUTH_CAM = 2;
	public static final int PLAY_MY_VOD = 3;
	public static final int PLAY_AUTH_VOD = 31;
	public static final int PLAY_PUB_VOD = 32;
	public static final int PLAY_MULTI = 4; 
	public static final int PLAY_AP_CAM = 5;
	
	// for cms lan err code
	public static final int CMS_LAN_OK = 0;
	public static final int CMS_LAN_CONN_FAIL = -101;
	public static final int CMS_LAN_SEND_FAIL = -102;
	public static final int CMS_LAN_RECV_FAIL = -103;
	public static final int CMS_LAN_PWD_ERR = -109;
	
	public static final int LIVE_LAN = 7;
	public static final int LIVE_LAN_BD = 5;
	public static final int LIVE_BD = 1;
	
	public static final String CMS_BD_IERMU = "bd_iermu";
	public static final String CMS_AP_IERMU = "AP_iermu";
	
	public static final String CMS_ETH = "******";
	
	// for weibo
	/** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
    public static final String APP_KEY      =  "3828340988";

    /** 
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * 
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * 
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * 
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
	
	/*
	public static final String[] IMAGES = new String[] {			
			// Heavy images
			"https://lh6.googleusercontent.com/-jZgveEqb6pg/T3R4kXScycI/AAAAAAAAAE0/xQ7CvpfXDzc/s1024/sample_image_01.jpg",
			"https://lh4.googleusercontent.com/-K2FMuOozxU0/T3R4lRAiBTI/AAAAAAAAAE8/a3Eh9JvnnzI/s1024/sample_image_02.jpg",
			"http://macprovid.vo.llnwd.net/o43/hub/media/1090/6882/01_headline_Muse.jpg",
			// Special cases
			"http://cdn.urbanislandz.com/wp-content/uploads/2011/10/MMSposter-large.jpg", // very large image
			"file:///sdcard/Universal Image Loader @#&=+-_.,!()~'%20.png", // Image from SD card with encoded symbols
			"assets://Living Things @#&=+-_.,!()~'%20.jpg", // Image from assets
			//"drawable://" + R.drawable.ic_launcher, // Image from drawables
			"http://upload.wikimedia.org/wikipedia/ru/b/b6/Как_кот_с_мышами_воевал.png", // Link with UTF-8
			"https://www.eff.org/sites/default/files/chrome150_0.jpg", // Image from HTTPS
			"http://bit.ly/soBiXr", // Redirect link
			"http://img001.us.expono.com/100001/100001-1bc30-2d736f_m.jpg", // EXIF
			"", // Empty link
			"http://wrong.site.com/corruptedLink", // Wrong link
	};
	*/
	
	// 微信app id
	public static final String APP_ID = "wx77c140da40bc45c0"; // debug: "wxe8f7b83c8c2d73f0"  rel: "wx77c140da40bc45c0";
    
    public static class ShowMsgActivity {
		public static final String STitle = "showmsg_title";
		public static final String SMessage = "showmsg_message";
		public static final String BAThumbData = "showmsg_thumb_data";
	}
	
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	public static class Extra {
		public static final String IMAGES = "com.nostra13.example.universalimageloader.IMAGES";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}
}
