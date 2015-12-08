package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApplyFriendBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Long userId;
	private String userName;
	private int type;
	private String avater;
	
	private String school;

	public static ApplyFriendBean parseFromJson(JSONObject jsonObj) {
		ApplyFriendBean clazz = new ApplyFriendBean();
		clazz.setUserId(jsonObj.optLong("userId"));
		clazz.setUserName(jsonObj.optString("nickName"));
		clazz.setType(jsonObj.optInt("type"));
		clazz.setAvater(jsonObj.optString("avatar"));
		clazz.setSchool(jsonObj.optString("school"));
		return clazz;
	}
	
	public static List<ApplyFriendBean> parseFromJson(JSONArray jsonArray) {
		List<ApplyFriendBean> clazzs = new ArrayList<ApplyFriendBean>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				ApplyFriendBean clazz = parseFromJson(jsonArray.optJSONObject(i));
				if(clazz != null)clazzs.add(clazz);
			}
		}
		return clazzs;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAvater() {
		return avater;
	}

	public void setAvater(String avater) {
		this.avater = avater;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

}
