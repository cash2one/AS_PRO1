package com.linkage.mobile72.sh.data;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.linkage.mobile72.sh.datasource.DataHelper;

public class OLConfig {

	@DatabaseField
	public String ol_userName;
	@DatabaseField
	public String ol_userPassword;
	@DatabaseField
	public String ol_pushserver_ip;
	@DatabaseField
	public long ol_pushserver_port;
	@DatabaseField
	public String ol_hostname = "192.168.1.21";
	@DatabaseField
	public String ol_token;
	@DatabaseField
	public long ol_timeOut;
	@DatabaseField
	public String ol_ip;
	@DatabaseField
	public long ol_port;
	@DatabaseField
	public String ol_socket_ip;
	@DatabaseField
	public long ol_socket_port;
	

	public static OLConfig parseFromJson(JSONObject jsonObj) {
		OLConfig olConfig = new OLConfig();
		olConfig.ol_userName = jsonObj.optString("push_user");
		olConfig.ol_userPassword = jsonObj.optString("push_password");
		olConfig.ol_pushserver_ip = jsonObj.optString("push_server");
		olConfig.ol_pushserver_port = jsonObj.optLong("push_port");
		olConfig.ol_ip = jsonObj.optString("ol_ip");
		olConfig.ol_port = jsonObj.optLong("ol_port");
		olConfig.ol_socket_ip = jsonObj.optString("ol_socket_ip");
		olConfig.ol_socket_port = jsonObj.optLong("ol_socket_port");
		olConfig.ol_token = jsonObj.optString("token");
		olConfig.ol_timeOut = jsonObj.optLong("timeout");
		return olConfig;
	}
	
}
