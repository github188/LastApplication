package com.iermu.analysis.server;

interface AIDLService {
	void setting(String action, String value);
	void saveLog(String action, String jsonString);
	void uploadLog();
}