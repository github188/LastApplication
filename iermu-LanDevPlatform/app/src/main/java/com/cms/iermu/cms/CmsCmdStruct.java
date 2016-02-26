package com.cms.iermu.cms;

public class CmsCmdStruct {
	public int cmsMainCmd = -1; // 主命令
	public byte cmsSubCmd = -1;   //子命令
	public int paramLen = 0;
	public byte[] bParams = null;   // 参数
}
