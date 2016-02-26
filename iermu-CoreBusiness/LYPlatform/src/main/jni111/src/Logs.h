#ifndef LOGS_H_
#define LOGS_H_

#define  JPLAYER_LOG_TAG    "JPlayer"

//#ifdef ANDROID_NDK
	#include <android/log.h>
	#define  JPLAYER_LOG_INFO(...)  __android_log_print(ANDROID_LOG_INFO,JPLAYER_LOG_TAG,__VA_ARGS__)
	#define  JPLAYER_LOG_ERROR(...)  __android_log_print(ANDROID_LOG_ERROR,JPLAYER_LOG_TAG,__VA_ARGS__)
//#else
//	#include <stdio.h>
//	#define  JPLAYER_LOG_INFO(...)   printf(__VA_ARGS__)
//	#define  JPLAYER_LOG_ERROR(...)  printf(__VA_ARGS__)
//#endif

#endif


