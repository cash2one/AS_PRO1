package com.linkage.app;


import com.linkage.lib.util.LogUtils;

public class Consts {
	
	public static final boolean DEBUG_MODE = true;
	public static final String appId = "1001";
	public static String SERVER_IP;//"http://112.4.28.173:8000";
	public static String SERVER_PRO;
	public static String SERVER_URL;//主路径

	static {
		if(DEBUG_MODE) {
			//阿里云
			SERVER_IP = "http://121.41.62.98:9200";//"http://112.4.28.173:8000";
			SERVER_PRO = SERVER_IP + "/educloud_new";
			SERVER_URL = SERVER_IP + "/educloud_new/api/terminal"; //主路径
			LogUtils.allowE = true;
		}else {
			//现网主机
			SERVER_IP = "http://Aservice.139jy.cn";
			SERVER_PRO = SERVER_IP + "/educloud";
			SERVER_URL = SERVER_IP + "/educloud/api/terminal"; //主路径
		}
	}

	public static final String APK_FILE_DIR = "linkage/";
	public static final String APK_FILE_NAME = "1001.apk";

}
