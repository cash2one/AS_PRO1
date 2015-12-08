package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class AppApiBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private long app_id;
	private long api_id;
	private String api_name;
	private String api_desc;
	
	public static AppApiBean parseFromJson(JSONObject jsonObj) {
		AppApiBean appapi = new AppApiBean();
		appapi.setApp_id(jsonObj.optLong("app_id"));
		appapi.setApi_id(jsonObj.optLong("api_id"));
		appapi.setApi_name(jsonObj.optString("api_name"));
		appapi.setApi_desc(jsonObj.optString("api_desc"));
		return appapi;
	}
	
	public static List<AppApiBean> parseFromJson(JSONArray jsonArray) {
		List<AppApiBean> appapis = new ArrayList<AppApiBean>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				AppApiBean appapi = parseFromJson(jsonArray.optJSONObject(i));
				if(appapi != null)appapis.add(appapi);
			}
		}
		return appapis;
	}
	
	public long getApp_id() {
		return app_id;
	}
	public void setApp_id(long app_id) {
		this.app_id = app_id;
	}
	public long getApi_id() {
		return api_id;
	}
	public void setApi_id(long api_id) {
		this.api_id = api_id;
	}
	public String getApi_name() {
		return api_name;
	}
	public void setApi_name(String api_name) {
		this.api_name = api_name;
	}
	public String getApi_desc() {
		return api_desc;
	}
	public void setApi_desc(String api_desc) {
		this.api_desc = api_desc;
	}
	
	
}
