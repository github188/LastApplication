/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_lingyang_sdk_demo_PlatformAPI */

#ifndef _Included_com_lingyang_sdk_demo_PlatformAPI
#define _Included_com_lingyang_sdk_demo_PlatformAPI
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_lingyang_sdk_demo_PlatformAPI
 * Method:    GetStatus
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_GetStatus
  (JNIEnv *, jclass);

/*
 * Class:     com_lingyang_sdk_demo_PlatformAPI
 * Method:    CameraBound
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_CameraBound
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_lingyang_sdk_demo_PlatformAPI
 * Method:    CameraUnBound
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_CameraUnBound
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_lingyang_sdk_demo_PlatformAPI
 * Method:    ConnectMediaSource
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_ConnectMediaSource
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_lingyang_sdk_demo_PlatformAPI
 * Method:    StartCloudService
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_StartCloudService
  (JNIEnv *, jclass, jstring, jstring);

/*
 * Class:     com_lingyang_sdk_demo_PlatformAPI
 * Method:    StopCloudService
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_lingyang_sdk_demo_PlatformAPI_StopCloudService
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
