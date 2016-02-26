package com.cms.iermu.cms;

public class CmsProtocolDef {
	// log code
	//所有|系统操作|配置操作|数据管理|报警事件|录像操作|用户管理
	/*[LOG0 MAXNUM=10]
	系统启动
	系统关机
	系统重启
	系统升级
	系统升级失败
	设置系统时间
	静音
	取消静音
	激活系统

	[LOG1 MAXNUM=40]
	恢复默认值
	保存普通设置
	保存编码设置
	保存录像设置
	保存串口设置
	保存网络设置
	保存报警设置
	保存侦测设置
	保存云台设置
	保存输出模式设置
	保存异常处理设置
	配置文件导入
	配置文件导出

	[LOG2 MAXNUM=10]
	清空硬盘
	数据恢复

	[LOG3 MAXNUM=20]
	报警输入
	视频丢失
	视频遮挡
	移动侦测
	报警输出
	清除报警
	无硬盘
	硬盘坏
	硬盘满
	硬盘覆盖
	菜单不匹配

	[LOG4 MAXNUM=20]
	启动录像
	停止录像
	录像列表
	查找录像
	录像备份开始
	录像备份结束
	开始播放录像文件
	录像文件播放完毕
	网络查看现场
	网络查看现场结束
	网络获取录像文件
	网络获取录像文件结束

	[LOG5 MAXNUM=20]
	用户登录
	用户退出登录
	用户登录失败
	修改用户密码
	修改用户
	增加用户
	删除用户
	增加用户组
	删除用户组
	修改用户组
	网络用户登录
	网络用户退出登录
	网络用户登录失败
	修改用户权限*/
	// ----alarm code ------//
	//无硬盘=7|硬盘出错=1|硬盘空间不足=0|断网事件=3|IP冲突=2|非法访问=4|制式不匹配=5|视频异常=6
	// type
	public byte ZSTATUSTYPE_ALARM = 1;
	public byte ZSTATUSTYPE_VIDEO = 2;
	// alarm type
	public byte ZSTATUSTYPE_ALARM_VLOST = 1;
	public byte ZSTATUSTYPE_ALARM_VCOVER = 2;
	public byte ZSTATUSTYPE_ALARM_MD = 3;
	public byte ZSTATUSTYPE_ALARM_HDD = 4;
	public byte ZSTATUSTYPE_ALARM_OUT = 5; // 外部报警
	// set bt656 output chanel
	public static final byte DEV_OP_CMSDVR_OUTPUT = 3;
	public static final byte DEVOP2_NETACCESS_GET = 4; //获取允许访问手机客户端列表
	public static final byte DEVOP2_NETACCESS_ADD = 5; //增加允许访问手机客户端
	public static final byte DEVOP2_NETACCESS_DEL = 6; //删除允许访问手机客户端
	public static final byte DEVOP2_WIFI_SCAN 	 = 7; //获取WIFI扫描结果
	public static final byte DEVOP2_WIFI_CONNECT  = 8; //连接指定WIFI网络  wep 1<<7  1<<6  是否直接连接设置wifi  1<<1 eap user 1<<0 保存wifi修改/默认密码
	public static final byte DEVOP2_WIFI_CANCEL   = 9; //忽略指定WIFI网络
	public static final byte DEVOP2_WIFI_TO_AP   = 11; //WIFI网络切换到AP模式
	public static final byte DEVOP2_SLEEP = 12;	     //设备切换到休眠模式
	public static final byte DEVOP2_WAKE = 13;	     //把设备从休眠模式唤醒
	public static final byte DEVOP2_DEFULTMENU = 14;	 //设备菜单回默认值 3518
	// -----net-------------// 
	public static final int DVR_DATA_LEN = 1000;
	public static final int DVR_DATA_LEN_MAX = 50000;
	public static final int DVR_DATA_LEN_TIME = 8;
	// main cmd
	public static final byte LAN2_NUL = 0x40;
	public static final byte LAN2_DVR_CTRL = 0x41;
	public static final byte LAN2_MENU_CTRL = 0x42;  // rec search
	public static final byte LAN2_RUNPARA_GET = 0x43;
	public static final byte LAN2_RUNPARA_SET = 0x44;
	public static final byte LAN2_AUTH = 0x45;
	public static final byte LAN2_INFO_GET = 0x46;   // 报警状态
	public static final byte LAN2_PTZ_CTL = 0x47;
	public static final byte LAN2_DEVICE_OP = 0x48;   // 设置bt656输出
	public static final byte LAN2_PLAY_CTRL = 0x49;
	public static final byte LAN2_RUNPARA3_GET = 0x4a;
	public static final byte LAN2_RUNPARA3_SET = 0x4b;

	// ptz
	//len = 3;
	//后续数据:字节 1 为通道号,字节 2 为预置点号(0-8),字节 3 为 0.
	public static final byte PTZ_CTRL_PRESET_GET = 19;
	public static final byte PTZ_CTRL_PRESET_SET = 18;
	
	//LAN2_DVR_CTRL
	public static final byte DVR2_CTR_REBOOT	= 1;
	public static final byte DVR2_CTR_TELCONTROL = 2;
	public static final byte DVR2_CTR_SAVEMENU = 3;    // 设备参数配置写盘指令
	public static final byte DVR2_CTR_GETSYSTIME = 4;
	public static final byte DVR2_CTR_SETSYSTIME = 5;
	public static final byte DVR2_CTR_HDDFORMAT = 6;
	public static final byte DVR2_CTR_HDDRECOVER = 7;
	public static final byte DVR2_CTR_ENCQUA = 8;
	public static final byte DVR2_CTR_ENCRATE = 9;
	public static final byte DVR2_CTR_ALARMCLEAR = 10;
	public static final byte DVR2_CTR_ETADPU = 94;		//固定数字
	public static final byte DVR2_CTR_SYSMSG	= 98;		//固定数字

	// LAN2_MENU_CTRL
	public static final int REC_LIST_ALL = 18;
	
	// for iermu add
	public static final int IPC_PUBLISH_CTRL = 1000;   // 设置ipc是否发布指令
	public static final byte START_PUBLISH = 1;  // 开始发布
	public static final byte STOP_PUBLISH = 0;	 // 停止发布
	// 对讲
	public static final int LAN2_IERMU_TALK = 1002; //对讲主命令 
	public static final byte IERMU_TALK_START = 1;  // 启动对讲
	public static final byte IERMU_TALK_DATA = 2;   // 音频数据
	public static final byte IERMU_TALK_END = 3;    // 停止对讲
	
	// iermu add end
	
	/**
	 * 前12个字节为保留位(第一个字节为0,其余全为0xff),后面为4个int,按顺序为查找时间,0,通道信息,结束时间
	 */
	public static final int REC_LIST_BY_PARAM = 21;
	public static final int REC_LIST_BACK = 20;
	public static final int REC_LIST_PRE = 19;
	public static final int REC_LIST_BACK_DAY = 24;
	public static final int REC_LIST_PRE_DAY = 23;
	public static final int REC_PARAM_LEN = 28;
	// LAN2_RUNPARA3_GET
	// LAN2_RUNPARA3_SET
	public static final byte PARA3_HDDINFO = 1;
	public static final byte PARA3_LOGINFO = 2;
	public static final byte PARA3_VERINFO = 3;  //122
	public static final byte PARA3_NORSET = 4;   //27
	public static final byte PARA3_CODESET = 5;  //322
	public static final byte PARA3_RECSET = 6;   //12
	public static final byte PARA3_SERISET = 7;  //28
	public static final byte PARA3_NETSET = 8;   //44
	public static final byte PARA3_NETNTP = 9;
	public static final byte PARA3_NETDIAL = 10;
	public static final byte PARA3_NETDDNS = 11;
	public static final byte PARA3_NETEMAIL = 12;
	public static final byte PARA3_ALMINSET = 13;  //836
	public static final byte PARA3_VIDEOLOST = 14;
	public static final byte PARA3_VIDEOCOVER = 15;
	public static final byte PARA3_VIDEOMOTION = 16;
	public static final byte PARA3_PTZSET = 17;
	public static final byte PARA3_PTZPObyte = 18;
	public static final byte PARA3_PTZCRUISE = 19;
	public static final byte PARA3_VOUTSET = 20;
	public static final byte PARA3_CHANNAME = 21;
	public static final byte PARA3_EVENTSET = 22;
	public static final byte PARA3_ALMOUT = 23;
	public static final byte PARA3_RECADMIN = 24;
	public static final byte PARA3_USERLIST = 25;
	public static final byte PARA3_GROUPLIST = 26;
	public static final byte PARA3_USERINFO = 27;
	public static final byte PARA3_GROUPINFO = 28;
	public static final byte PARA3_AUTOMAbyte = 29;
	public static final byte PARA3_PTZDEVICE = 30;
	public static final byte PARA3_WIFI = 38; //WIFI设置
	public static final byte PARA3_TOKEN = 39; //TOKEN设置
	public static final byte PARA3_CLOUD_PUBLISH = 40;     //云端允许/禁止发布
	public static final byte PARA3_INFRARED = 41;     //红外
	public static final byte PARA3_HTTPUPDATE = 42;     //HTTP升级
	public static final byte PARA3_SENSECONF = 43;	//移动侦测等配置  *** 参数需要注意其他功能集成在这里  ****
	public static final byte PARA3_ADJUSTRATE = 45;     //动态调整码流、帧率，不更改菜单
	public static final byte PARA3_ENSUREUPDATE = 47;     //升级确认
	public static final byte PARA3_GETDEBUG = 48;     //获取WIFI信号强度等调试信息
	public static final byte PARA3_LIMITSTREAM = 49;     //限制带宽kb 内容4个字节（网络序INT）值有效范围50~2000
	public static final byte PARA3_NETMAILPORT = 50;	//发送邮件端口   内容为8字节,前两字节为网络序SHORT端口值，其他为0保留
	public static final byte PARA3_SENSETIMER = 51;     //移动侦测使能时间段、正常工作时间段（其他时间休眠）、报警（推送、邮件）、定时开关录像设置
	public static final byte PARA3_PUSHKEY = 52;     //推送SECRETKEY、APIKEY 协议内容（SET、GET相同）64字节：前32字节为SECRETKEY；后32字节为APIKEY；不足部分补0x00
	public static final byte PARA3_RTMPLOCALPLAY = 54;     //RTMP局域网播放
	public static final byte PARA3_EXTREME = 55;     //系统极值，如码流、带宽等
	public static final byte PARA3_DEVUID = 59;     // 59 //20 string 
	public static final byte PARA3_AUDIOALARM = 60;     //声音报警
	public static final byte PARA3_NASCONF = 62;     //NAS配置
	public static final byte PARA3_PRODUCTTYPE = 63;     //产品类型相关
	public static final byte PARA3_NASPLAYPARA = 64;     //NAS播放参数
	public static final byte PARA3_PTZOFFSET = 66;     //云台位移
	public static final byte PARA3_NASDELBYTIME = 67;     //按时间删除NAS录像段 20150227
	public static final byte PARA3_NASFIND = 68;     //NAS查找 20150302
	public static final byte PARA3_GETNASRESULT = 69;     //获取NAS查找结果 20150319
	
	// 一、
	/*
	 * tChanNo = tChar[0];  // 通道
	 * tStream = tChar[1];  // 主／子码流
	 * tQuality = cmsNtohs(tShort[0]); // 码流值
	 * tFrameRate = cmsNtohs(tShort[1]); // 帧率： 100
	 * */
	
	// 二、
	/*子命令
	#define PARA3_CLOUD_PUBLISH                           40     //云端允许/禁止发布
	#define PARA3_INFRARED                                41     //红外

	1、允许/禁止发布（3518爱耳目）
	内容4字节：0字节：1、允许；0、禁止，1字节：时区  2字节：取休眠状态：0-正常 1-休眠 3字节：扩展
	2、红外参数
	内容24字节：0、1字节为网络序USHORT，表示感光电阻高值，2、3字节为网络序USHORT，表示感光电阻低值，
	                            4字节为红外模式（室内：0：自动，1：常关，2：常开；
	                            			  室外：3：自动，4：常关，5：常开），5字节为平滑度，其他18字节为0；
	                            			  UCHAR  曝光优先： 0为高光优先；1为低光优先；2为自动模式。（原为平滑度）

	// 三、
	网络协议新增HTTP升级
	主命令：
	         LAN2_RUNPARA3_GET,    //0x4a
	         LAN2_RUNPARA3_SET,    //0x4b
	子命令：
	#define PARA3_HTTPUPDATE   42     //HTTP升级, 暂时不用
	传输内容结构如下：
	#define HTTPUPDATE_HOSTNAMELEN     64
	#define HTTPUPDATE_URLFILENAMELEN  512
	typedef struct cmsHTTPUpdateNet_tag   //88b   同意升级：0,0,1,0  不同意：0，0，0，0
	{
	         UInt16      HTTPPort;    //端口（80）网络字节序
	         UInt8        AgressUpdate;  //最低位=〉0：不同意（缺省），1：同意；最高位=〉0：推消息提示（缺省）当前版本，1：当前版本不再提示升级；
	         UInt8        HostName[HTTPUPDATE_HOSTNAMELEN]; //域名或IP，结尾为0
	         UInt8        UrlFileName[HTTPUPDATE_URLFILENAMELEN];// 以/开始，结尾为0
	}cmsHTTPUpdateNet,*pcmsHTTPUpdateNet;
	//============================================================================
	LAN2_RUNPARA3_GET方式仅返回前4字节
	
	new HTTP升级协议  20150319
	#define PARA3_HTTPUPDATE                            42     //HTTP升级
	SET共>=4字节(USHORT UINT皆为网络序)
	序列 含义
	USHORT 端口缺省为802
	UCHAR 1：升级APP；2：升级KERNEL
	UCHAR[64]  HOST NAME，填一个0也可以，这时无下项
	UCHAR若干 URL串，设一个0即可
	目前SET只需填写前4字节，升级KERNEL提示用户不要断电

	有两种升级方式：
	1、客户收到推送消息后确认升级，置AgressUpdate，其他置0，
	2、客户端主动升级，填写cmsHTTPUpdateNet结构，让设备升级
	*/
	
	// 四、SenseIsAlarm
	/* 
	 * 8字节设置参数***************************************
	 * 总共8字节(保持原来值的传0xff)
	 * 第一字节： 设置移动侦测是否报警 //最低位=〉0：移动侦测不为报警，1：是；最高位=〉0：不推送消息，1：推送；
	 * 第二字节：是否关闭指示灯：0：不关 1:关闭 
	 * 第三字节：180旋转 0:正常  1：画面倒置   （自动重启生效）
	 * 第四字节：录像开关设置   //0:允许录像 1:禁止录像
	 #define PARA3_SENSECONF	 43	//移动侦测等配置*/
	
	// 五、
	/*
	 *通过rtmp控制通道对讲协议
	 * 
	#define LAN2_IERMU_TALK	1002
	#define IERMU_TALK_START	1
	#define IERMU_TALK_DATA	2
	#define IERMU_TALK_END		3

	主命令：1002
	子命令：1，2，3*/
	
	// 六、摄像头休眠协议
	/*LAN2_DEVICE_OP,                     //0x48      72
	#define DEVOP2_SLEEP         12     //设备切换到休眠模式
	#define DEVOP2_WAKE          13     //把设备从休眠模式唤醒
	#define DEVOP2_DEFULTMENU	 14		//设备菜单回默认值 3518
	获取设备休眠状态
	#define PARA3_CLOUD_PUBLISH                      40     //云端允许/禁止发布
	第3个字节取休眠状态，休眠是非0，0是正常
	*/	
	
	// 七、摄像头升级确认协议
	/*#define PARA3_ENSUREUPDATE              47     //升级确认
	内容四个字节，第一个字节表示升级确认：0没有升级 1要求确认      2已经确认或无需确认
	其他字节暂时保留，都为0XFF
	GET：可以获取是否有升级确认
	SET：若确认设第一个字节为2，其他为0XFF, 不需要发写flash指令
	如果推送正常且升级需要确认时，会收到消息*/
	
	// 八、获取wifi等调试信息
	//共96字节：
	/*0、1字节：网络序SHORT，程序版本CMSDVR_VERSION
	2、3字节：网络序SHORT，(程序平台版本<<8)|程序子版本（(CMSDVR_PLATVERSION<<8)|CMSDVR_SUBVERSION)）
	4、5、6、7字节：网络序UINT，WIFI信号：(强度值（百分数）<< 16) | ((信号强度+256) << 8) | (噪声强度+256)
	8~39字节：32字节SSID
	40~71字节：调试信息
	72~95字节：0，保留*/
	
	// 九、#define PARA3_RTMPLOCALPLAY            54     //RTMP局域网播放
	/*GET、SET内容相同36字节
	前4字节网络序UINT表示局域网直连命令（SET）或状态（GET）：0x7：表示启动或正在RTMP局域网播放。
	后32字节为客户端生成的最大32字节的随机串，作为播放URL中的流ID
	客户端退出RTMP局域网播放后，设备会自动设置局域网直连状态为0x1，即IERMU常规模式。*/
	
	// 十、#define PARA3_EXTREME                                    55     //系统极值，如码流、带宽等
	/*内容共16字节（仅GET有效）
	序列（SHORT、INT网络序）		
	含义
	USHORT
	带宽高限值
	USHORT
	带宽低限值
	USHORT
	码流高限值
	USHORT
	码流低限值
	其余8字节
	0，保留*/
	
	//十一、产品类型协议，在摄像头配置时，进行权限控制使用
	/*
	#define PARA3_PRODUCTTYPE                          63     //产品类型相关
	GET 共32字节(USHORT UINT皆为网络序)
	UCHAR[6] MAC地址
	USHORT 程序大版本号
	USHORT （平台号<<8|程序小版本号）
 	USHORT 保留为0
	UINT 产品类型
	UCHAR[16] 保留为0
	*/
	
	//十二
	/*
	#define PARA3_AUDIOALARM                           60     //声音报警
	共8字节，以下各字节如果设置为0xff则表示忽略该值设置
	UCHAR 声音报警使能0:关闭1:打开
	UCHAR 声音报警灵敏度级别(0~4)
	UCHAR 移动侦测报警使能灵敏度级别(0~4) 0x7f=移动侦测报警关闭
	UCHAR 移动侦测录像使能灵敏度级别(0~4)，同以前移动侦测灵敏度
	其余4字节保留为0
	*/

	//十三
	/*
	#define PARA3_NASCONF  62     //NAS配置
	共142字节
	UCHAR NAS使能  	最高位表示SAMBA, 第0位表示nas录像使能（0:关闭1:打开），第1位表示NFS, 第2位是是否升级kernel;
			第3位为1表示正在配置NFS,为0表示NFS配置结束【GET有效】  第4位为1表示配置NFS成功,为0表示NFS配置失败【GET有效】;  第4位为1表示nas工作正常  0工作异常
	UCHAR[33] NAS盘登录用户名，最后一个字符强制为0
	UCHAR[33] NAS盘登录密码，最后一个字符强制为0
	UCHAR[51] NAS盘路径，最后一个字符强制为0  例如/home/hisi/nfs/nas
	UCHAR[16] NAS盘子目录名称，最后一个字符强制为0 缺省可以为NATID串
	UINT NAS IP地址（网络序）
	UINT NAS盘大小GB单位（网络序）
	注意：SET NAS配置后，不能发菜单写命令。
	如果原使能和新使能不全为0，配置成功，固件自动写菜单并重启；如果失败，返回-1，并维持原来配置。
	如果原使能和新使能都为0，则自动写菜单不重启。
	*/

	//十四
	/* ==========新nas播放协议
	(一)、NAS查找协议
	#define PARA3_NASFIND                                     68     //NAS查找 20150302
	GET共4字节(USHORT UINT皆为网络序)
	序列 含义 
	UINT NAS服务器IP地址，本机序格式为0x01020304:表示“1.2.3.4” 
	返回格式(USHORT UINT皆为网络序)
	序列  含义
	UINT 条目数n（n=0则无下述内容）
	UCHAR若干 
	条目1，为带0字符串，格式为：
	NAS Export出来的挂接目录名|该目录容量(G)|该目录剩余空间（G）|当前设备在该目录下的配置大小（G），例如：/home/hisi/nfs|422|422|0
	UCHAR若干
	条目2，。。。。
	。。。。
	UCHAR若干   条目n，。。。。
	(二)、NAS播放协议
	#define PARA3_NASPLAYPARA                            64     //本地NAS播放参数【更改以前】
	GET共44字节(USHORT UINT皆为网络序)
	序列 含义
	USHORT RTMP播放端口
	UCHAR 播放模式，待用为0
	UCHAR 播放状态，待用为0
	UCHAR[32] 播放流名称，用于认证
	UINT 开始时间（例子） CMS_CONV_ENCDATE(2014,12,2,9,01,10);
	UINT 结束时间：（例子） CMS_CONV_ENCDATE(2014,12,12,19,21,13);
	SET共若干字节(USHORT UINT皆为网络序)
	序列 含义
	UINT 播放子命令0x01,0x02,0x03,…..
	若干
	子命令内容
	1、子命令0x01：准备播放【共48字节】
	序列 含义
	UINT 播放子命令0x01
	UCHAR[32]  播放流名称，用于认证
	UCHAR 播放模式，暂为0
	UCHAR[3] 保留为0
	UINT 开始时间（例子） CMS_CONV_ENCDATE(2014,12,2,9,01,10);
	UINT 结束时间：（例子） CMS_CONV_ENCDATE(2014,12,12,19,21,13);
	2、子命令0x02：更改播放时间【共48字节】
	序列 含义
	UINT 播放子命令0x02
	UCHAR[32] 播放流名称，用于认证
	UCHAR[4] 保留为0
	UINT 开始时间（例子） CMS_CONV_ENCDATE(2014,12,2,9,01,10);
	UINT 结束时间：（例子） CMS_CONV_ENCDATE(2014,12,12,19,21,13);
	3、子命令0x03：停止播放【共36字节】
	序列 含义
	UINT 播放子命令0x03
	UCHAR[32] 播放流名称，用于认证
	======新nas播放协议
	*/
	
	//十五
	/* 云台检测协议  暂时没有用到
	主协议：LAN2_PTZ_CTL,                          //0x47      71
	子协议：#define PTZ2_CTL_YUNTAICHECK           26     //检测是否有云台
	无内容
	返回值为0表示有云台，-1表示无云台。
	*/

	// 十六
	/* 通过图像坐标控制云台
	#define PARA3_PTZOFFSET                        66     //云台位移
	GET 返回8字节
	返回格式如下：	
	序列 含义  
	UCHAR 设备是否接有云台：1表示接有，否则没有
	UCHAR 设备是否支持云台位移命令：1表示支持，否则不支持
	其他6个UCHAR 保留为0
	SET内容为8字节格式如下：(USHORT UINT皆为网络序)
	序列 含义
	UINT 屏幕水平坐标值(H)，以左上角为0坐标
	UINT 屏幕垂直坐标值(V)，以左上角为0坐标
	返回值： <=0：表示失败 0x10：表示成功
	0x01~0x0f：表示到边界（bit0--左边界；bit1--右边界；bit2--上边界；bit3--下边界）
	*/
	
	// 十七
	/*
	#define PARA3_NASDELBYTIME               67     //按时间删除NAS录像段 20150227
	SET共12字节(USHORT UINT皆为网络序)
	序列 含义
	UINT 开始时间（例子） CMS_CONV_ENCDATE(2014,12,2,9,01,10);
	UINT 结束时间：（例子） CMS_CONV_ENCDATE(2014,12,12,19,21,13);
	UINT 通道掩码，IERMU目前为1
	返回值 <0：表示失败（正在删除）。
	*/
	
	private static final int m_chNum = 1;
	
	/* 取dvr报警状态
	main: 0x46
	sub: 10
	*/
	// receive data length
	// 与通道有关的协议需要注意长度的大小（返回值长度不固定），对应修改
	public static final int[] PARA3_DATA_LEN = new int[]{
		0,	   //0  unused	
		32,   //1   与安装的硬盘数有关 1:16+1×16 2:16+2×16， 以此类推
		500,   //2
		122,   //3
		27,    //4
		322,   //5
		12+12*6*7*m_chNum,    //6
		28,    //7
		44,    //8
		48,    //9
		50,    //10
		114,   //11
		176,   //12
		836,   //13
		420,   //14
		420,   //15
		468,   //16
		68,    //17
		500,   //18
		500,   //19
		20,   //20
		500,   //21
		836,   //22
		4+2*8,   //23
		4+m_chNum,   //24 
		500,   //25
		500,   //26
		500,   //27
		500,   //28
		8,   //29
		455    //30
	};
	
	// for iermu.com log  (4000-5999)
	public static short LOG_SEND_CMD = 4000;
	public static short LOG_SEND_CMD_BY_PCS = 4001;  // 控制通道与设备通讯日志
	public static short LOG_BD_START = 4002;  // 百度帐号启动应用,列出绑定的百度帐号
	public static short LOG_MYCAM_LIST = 4003;  // 进入我的摄像头列表   	固定格式： uid+\n+uname+\n+devid\n+devid\0
	public static short LOG_PUBCAM_LIST = 4004; // 进入公开摄像头列表
	public static short LOG_FAVCAM_LIST = 4005; // 进入收藏摄像头列表
	public static short LOG_MYCAM_PLAY = 4006; // 播放我的摄像头
	public static short LOG_PUBCAM_PLAY = 4007; // 播放分享的摄像头视频
	public static short LOG_FAVCAM_PLAY = 4008; // 播放收藏摄像头视频
	public static short LOG_REGDEV = 4009;  // 注册、解绑设备与pcs通讯信息
}
