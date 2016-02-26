//#include <jni.h>
//#include <string.h>
//#include <time.h>
//
//extern "C"
//{
//	#include <stdio.h>
//	#include <sys/epoll.h>
//	#include  <netinet/in.h>
//	#include  <arpa/inet.h>
//	#include <semaphore.h>
//	#include <string.h>
//	#include <stdlib.h>
//	#include <pthread.h>
//	#include <unistd.h>
//
//#include "Logs.h"
//#include "include/cJSON.h"
//#include "include/CloudPlatformAPI.h"
//
//}
//
//#ifdef OMX_DECODE
//#include <android/native_window_jni.h>
//#include <android/native_window.h>
//#include "OmxDecode.h"
//#endif
//
//#ifdef XBMC_DECODE
//#include "JNIThreading.h"
//#include "XbmcDecode.h"
//#endif
//
//
/////////////////////////////////////////////
//#define JDEBUG
//#ifdef JDEBUG
////void testPlayer();
//
//char * appGetStatus();
//int  appCameraBound(const char* aSN, char *aCid);
//int  appCameraUnBound(const char * aCameraID);
//int  appConnectMediaSource(const char *aHashCid);
//void  appStartCloudService(const char* aUsername, const char* aPassword);
//void  appStopCloudService();
//
//#endif
//
//
//extern "C"
//{
//
//	JNIEXPORT jstring JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_GetStatus(JNIEnv * env,
//		jobject obj);
//
//	JNIEXPORT jstring JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_CameraBound(JNIEnv * env,
//			jobject obj,jstring aSN);
//
//	JNIEXPORT jint JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_CameraUnBound(JNIEnv * env,
//			jobject obj,jstring aCameraID);
//
//	JNIEXPORT jint JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_ConnectMediaSource(JNIEnv * env,
//			jobject obj,jstring aHashCid);
//
//	JNIEXPORT void JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_StartCloudService(JNIEnv * env,
//			jobject obj,jstring aUsername, jstring aPassword);
//
//	JNIEXPORT void JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_StopCloudService(JNIEnv * env,
//		jobject obj);
//
//};
//
//#ifdef XBMC_DECODE
//jobject g_surface = NULL;
//#endif
//
//
//JNIEXPORT jstring JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_GetStatus(JNIEnv * env,
//		jobject obj)
//{
//	jstring jstatus;
//
//	JPLAYER_LOG_INFO("jni GetStatus :%s",appGetStatus());
//	jstatus=env->NewStringUTF(appGetStatus());
//
//	return jstatus;
//}
//
//JNIEXPORT jstring JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_CameraBound(JNIEnv * env,
//		jobject obj,jstring aSN)
//{
//	const char* sn = (env)->GetStringUTFChars(aSN,0);
//	JPLAYER_LOG_INFO("Java_com_lingyang_sdk_CameraBound  sn size: %d; sn: %s\n",strlen(sn),sn);
//
//	int nRet = 0;
//	char cid[20] = {0};
//	jstring jstatus;
//	if(sn != NULL)
//	{
//		nRet = appCameraBound(sn,cid);
//		jstatus=env->NewStringUTF(cid);
//
//		(env)->ReleaseStringUTFChars(aSN, sn);
//	}
//
//	return jstatus;
//}
//
//JNIEXPORT jint JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_CameraUnBound(JNIEnv * env,
//		jobject obj,jstring aCameraID)
//{
//	JPLAYER_LOG_INFO("Java_com_lingyang_sdk_api_PlatformAPI_CameraUnBound \n");
//	const char* scid = (env)->GetStringUTFChars(aCameraID,0);
//	int nRet = 0;
//	if(scid != NULL)
//	{
//		nRet = appCameraUnBound(scid);
//		(env)->ReleaseStringUTFChars(aCameraID, scid);
//	}
//	return nRet;
//}
//
//JNIEXPORT jint JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_ConnectMediaSource(JNIEnv * env,
//		jobject obj,jstring aHashCid)
//{
//	JPLAYER_LOG_INFO("Java_com_lingyang_sdk_api_PlatformAPI_ConnectMediaSource \n");
//	const char* scid = (env)->GetStringUTFChars(aHashCid,0);
//	int nRet = 0;
//	if(scid != NULL)
//	{
//		nRet = appConnectMediaSource(scid);
//		(env)->ReleaseStringUTFChars(aHashCid, scid);
//	}
//	return nRet;
//}
//
//JNIEXPORT void JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_StartCloudService(JNIEnv * env,jobject obj,jstring aUsername, jstring aPassword)
//{
//	const char* Username = (env)->GetStringUTFChars(aUsername,0);
//	const char* Password = (env)->GetStringUTFChars(aPassword,0);
//	JPLAYER_LOG_INFO("Java_com_lingyang_sdk_api_StartCloudService usrname: %s ,pwd: %s\n",Username,Password);
//
//	if(Username != NULL && Password != NULL)
//	{
//		appStartCloudService(Username,Password);
//		(env)->ReleaseStringUTFChars(aUsername, Username);
//		(env)->ReleaseStringUTFChars(aPassword, Password);
//	}
//}
//
//JNIEXPORT void JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_StopCloudService(JNIEnv * env,
//		jobject obj)
//{
//	JPLAYER_LOG_INFO("Java_com_lingyang_sdk_api_PlatformAPI_StopCloudService \n");
//	return appStopCloudService();
//}
//
//
//extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved)
//{
//	JPLAYER_LOG_INFO("JNI_OnLoad()\n");
//
//	jint version = JNI_VERSION_1_6;
//	JNIEnv* env;
//	if (vm->GetEnv(reinterpret_cast<void**>(&env), version) != JNI_OK)
//	{
//		return -1;
//	}
//
//#ifdef XBMC_DECODE
//	int ret = xbmc_jni_on_load(vm, env);
//	JPLAYER_LOG_INFO("JNI_OnLoad() ret %d\n", ret);
//#endif
//	return version;
//}
//
//int PopMessage(void *apData, const char *aMessage)
//{
//	cJSON *root = cJSON_Parse(aMessage);
//	cJSON *name = cJSON_GetObjectItem(root, "Name");
//	if (memcmp("StartPopData", name->valuestring, strlen(name->valuestring)) == 0)
//	{
//		JPLAYER_LOG_INFO("...............StartPopData..................");
//		//openVideoPlayer();
//	}
//	else if (memcmp("StopPopData", name->valuestring, strlen(name->valuestring)) == 0)
//	{
//		JPLAYER_LOG_INFO("...............StopPopData..................");
//		//closeVideoPlayer();
//	}
//	else if (memcmp("StartPushData", name->valuestring, strlen(name->valuestring)) == 0)
//	{
//		JPLAYER_LOG_INFO("................StartPushData................");
//		//startpushflag = 1;
//	}
//	else if (memcmp("StopPushData", name->valuestring, strlen(name->valuestring)) == 0)
//	{
//		JPLAYER_LOG_INFO("................StopPushData.................");
//		//startpushflag = 0;
//	}
//	cJSON_Delete(root);
//	return 0;
//}
//
//
//char * appGetStatus()
//{
//	//JPLAYER_LOG_INFO("appget status .......%s",A_GetStatus());
//	return A_GetStatus();
//}
//
//void  appStartCloudService(const char* aUsername, const char* aPassword)
//{
//	A_StartCloudService(aUsername,aPassword, NULL,PopMessage);
//}
//
//void  appStopCloudService()
//{
//	A_StopCloudService();
//}
//
//int  appCameraBound(const char* aSN, char *aCid)
//{
//	//aCid = A_CameraBound(aSN);
//	sprintf(aCid,"%s",A_CameraBound(aSN));
//	JPLAYER_LOG_INFO("appCameraBound aCid == %s ",aCid);
//	return 0;
//}
//
//int  appCameraUnBound(const char * aCameraID)
//{
//	return A_CameraUnBound(aCameraID);
//}
//
//int  appConnectMediaSource(const char * aHashCid)
//{
//	return A_ConnectMediaSource(aHashCid);
//}
//
