package com.linkage.mobile72.sh.im.bean;

import java.util.HashMap;

import org.json.JSONObject;

public class MessageOut {

	private HashMap<String, Object> mKeyAndValue = new HashMap<String, Object>();
	
	public String toJson() {
		JSONObject json = new JSONObject(mKeyAndValue);
		return json.toString();
	}
	
	public void put(String key, Object value) {
		mKeyAndValue.put(key, value);
	}
	
	public static String json(MessageOut mo) {
		return mo.toJson();
	}
}
