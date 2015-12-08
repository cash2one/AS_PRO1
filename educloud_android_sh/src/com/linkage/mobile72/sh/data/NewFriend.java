package com.linkage.mobile72.sh.data;

import org.json.JSONObject;

public class NewFriend {

	private String avatar;
	private String userName;
	private long userId;
	private String applyReason;
	
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String getApplyReason() {
		return applyReason;
	}
	
	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}
	
	public static NewFriend parseFromJson(JSONObject jsonObj) {
		NewFriend friend = new NewFriend();
		friend.setUserId(jsonObj.optLong("userId"));
		friend.setUserName(jsonObj.optString("userName"));
		friend.setAvatar(jsonObj.optString("avatar"));
		friend.setApplyReason(jsonObj.optString("applyReason"));
		return friend;
	}
	
}
