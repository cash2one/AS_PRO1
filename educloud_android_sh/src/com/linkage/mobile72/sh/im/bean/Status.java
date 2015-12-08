package com.linkage.mobile72.sh.im.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class Status extends MessageIn {
	
	public static final String ONLINE = "online";
	public static final String OFFLINE = "offline";

	private long friendId;
	private String friendName;
	private String status;
	
	public static Status fromJson(String str) throws JSONException {
		JSONObject json = new JSONObject(str);
		Status status = new Status();
		status.friendId = json.optLong("friend_id");
		status.friendName = json.optString("friend_name");
		status.status = json.optString("status");
		return status;
	}

	public long getFriendId() {
		return friendId;
	}

	public void setFriendId(long friendId) {
		this.friendId = friendId;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
