package com.linkage.mobile72.sh.im.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageIn {

	public static final String ERROR = "error";
	public static final String FRIENDS = "friend";
	public static final String STATUS = "status";
	public static final String HB = "hb";
	public static final String MESSAGE = "message";
	public static final String CREATE_GROUP = "create_group";
	public static final String EXIT_GROUP = "quit_group";
	public static final String AUTH_SUCCESS = "auth-success";

	public static final String NOTICE = "notice"; // 通知
	public static final String NEWS = "1002"; // 新闻
	public static final String REMIND = "1003"; // 提醒

	private String type;

	public static MessageIn fromJson(String str) throws JSONException {
		JSONObject json = new JSONObject(str);
		MessageIn in = new MessageIn();
		in.type = json.optString("type");
		return in;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
