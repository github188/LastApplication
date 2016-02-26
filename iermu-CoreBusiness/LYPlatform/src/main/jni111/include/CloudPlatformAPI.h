#ifndef __CLOUD_PLATFORM_API_H__
#define __CLOUD_PLATFORM_API_H__

#ifdef __cplusplus
#if __cplusplus
extern "C"{
#endif
#endif

//瀵瑰鍙戦�佹秷鎭殑鍥炶皟鍑芥暟锛�
//褰撹鏈嶅姟鎺ユ敹鍒拌皟搴︽湇鍔″櫒鐨勫紑濮嬫帹閫佸懡浠ゆ椂 浼氬彂鍑烘秷鎭�氱煡澶栧３璋冪敤PushData鍑芥暟濉炴暟鎹� json鏍煎紡锛歿"Name":"StartPushData"}
//褰撻渶瑕佸仠姝㈠鏁版嵁鏃� json鏍煎紡锛歿"Name":"StopPushData"}
//褰撹鏈嶅姟鎺ユ敹鍒版椂 瀵规柟鍙戦�佺殑濯掍綋鏁版嵁鏃讹紝浼氬彂鍑烘秷鎭�氱煡澶栧３璋冪敤PopData鍑芥暟鏀舵暟鎹� json鏍煎紡锛歿"Name":"StartPopData"}
//褰撻渶瑕侀渶瑕佸仠姝㈡敹鏁版嵁鏃� json鏍煎紡锛歿"Name":"StopPopData"}
typedef int (*A_MessageCallBack)(void *apData, const char *aMessage);

//寮�濮嬩簯鏈嶅姟 							鐢ㄦ埛鍚嶏紙16瀛楄妭浠ュ唴锛�  	瀵嗙爜				  鍥炶皟鍙傛暟     寮�濮嬮噰闆嗗苟鍘嬬缉闊宠棰戞暟鎹殑鍥炶皟鍑芥暟
void A_StartCloudService(const char* aUsername, const char* aPassword, void *apData, A_MessageCallBack apMessageCallBack);

//鍏抽棴浜戞湇鍔�
void A_StopCloudService();

//鎽勫儚鏈虹粦瀹�     鎽勫儚鏈哄簭鍒楀彿20瀛楄妭   杩斿洖hashid
char *A_CameraBound(const char *aSNumber);
//int A_CameraBound(const char *aSNumber,char *aHashID);

//鎽勫儚鏈鸿В闄ょ粦瀹�              鎽勫儚澶磆ashID
int A_CameraUnBound(const char *aHashCid);

//鑾峰彇浠SON鏍煎紡缁欏嚭鐨勫悇绉嶇姸鎬佷俊鎭� 杩斿洖鐨勫瓧绗︿覆鐢卞闈㈣礋璐ｉ噴鏀�
char *A_GetStatus();

//鎺ㄩ�佹秷鎭�  8瀛楄妭鎽勫儚澶村搱甯孖D 寰�鎵嬫満鎺ㄩ�佹椂涓虹┖     16瀛楄妭娑堟伅浣�
int   A_PushMessage(const char *aHashCid, const char *aMessage, const int aMessageLength);

//寰�璇ユ湇鍔″鍏ラ煶瑙嗛閲囨牱鏁版嵁  鏁版嵁灞傜储寮� 褰撳墠鍥哄畾涓�0    闊宠棰戦噰鏍风紦鍐插尯          闊宠棰戦噰鏍风紦鍐插尯闀垮害    32浣嶆椂闂存埑鍗曚綅姣  閲囨牱绫诲瀷h264 NALU_TYPE 鎴栬�� 闊抽绫诲瀷
int   A_PushData(unsigned long aDataLevelPos, const char *aDataBuffer, unsigned long aBufferLength, unsigned long aTimestamp, unsigned char apFrameSampleType);

//寮瑰嚭涓�涓煶瑙嗛閲囨牱缂撳啿鍖� 鏁版嵁灞傜储寮� 褰撳墠鍥哄畾涓�0           閲囨牱鏁版嵁闀垮害              32浣嶆椂闂存埑  閲� 鏍风被鍨媓264 NALU_TYPE 鎴栬�� 闊抽绫诲瀷
char* A_PopData(unsigned long aDataLevelPos, unsigned long *apFrameSampleLength, unsigned long *apTimestamp, unsigned char *apFrameSampleType);

//鑾峰彇鎽勫儚鏈虹洿鎾皝闈㈡埅鍥緐rl浠ュ強鏃堕棿鎴�,杩斿洖json涓�
//char *A_GetCameraConver(const char *aHashCid);

//鑾峰彇鎽勫儚鏈虹缉鐣ユ埅鍥緐rl  杩斿洖json涓� (瀹㈡埛绔幏鍙栨埅鍥惧畬鏁磋矾寰勶細   鎺ュ彛杩斿洖鐨剈rl + 鏃堕棿鎴筹紱  鏃堕棿鎴充负unix鏃堕棿锛屽崟浣嶏細绉�)
//aPicSize涓虹缉鐣ュ浘灏哄  榛樿涓�128
//char *A_GetThumbnailConver(const char *aHashCid, const int aPicSize = 128);

//鐩存挱鎽勫儚澶�                       鎽勫儚澶碔D
int A_ConnectMediaSource(const char *aHashCid);

//鏂紑鐩存挱
void  A_DisconnectMediaSource();


//褰曞儚鏁版嵁鍥炶皟鍑芥暟
typedef int (*A_RecorStreamCallBack)(const char *apData,int aDataLength,unsigned long long apTimestamp, unsigned char aType);

//鑾峰彇褰曞儚鍒楄〃锛�7澶╋級锛� 杩斿洖json涓�
char *A_GetRecordList(const char *aHashCid);

//璇锋眰褰曞儚淇℃伅  杩斿洖json褰曞儚鍒嗘淇℃伅锛� 鍙傛暟 锛氭挱鏀炬椂闂村尯闂达紙鏈�澶氫竴灏忔椂锛夛級
char *A_RecordPlaySource( const char *aHashCid, const int aFromTime, const int aToTime);

//鎵撳紑褰曞儚璇锋眰杩炴帴
int  A_ConnectRecordPlaySource( const char *aHashCid, A_RecorStreamCallBack apMessageCallBack);

//鍏抽棴褰曞儚璇锋眰杩炴帴
int  A_DisconnectRecordPlaySource();

//瀹氫綅褰曞儚璇锋眰婧愮储寮曚綅缃�
int  A_LocateRecordPlaySource(int aLocateTime);

#ifdef __cplusplus
#if __cplusplus
}
#endif
#endif

#endif
