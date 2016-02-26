package com.lingyang.sdk.demo;

public class PlatformAPI {
	
	static{
		System.loadLibrary("CloudService");
		System.loadLibrary("CloudAPI");
		System.loadLibrary("LYSDK");
	}

	public native static String GetStatus();
	public native static String CameraBound(String aSN);
	public native static int CameraUnBound(String aCameraID);
    public native static int ConnectMediaSource(String hashId);
	public native static void StartCloudService(String aUsername,String aPassword);
	public native static void StopCloudService();	

}
