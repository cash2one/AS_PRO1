package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class InviteFriend implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long friendId;
	private String nickName;
	private String avatar;
	
	private String friendName;
	private String friendPhone;
	private Long userId;
	private Integer type;
	
	public static InviteFriend parseFromJson(JSONObject jsonObj) {
		InviteFriend clazz = new InviteFriend();
		clazz.setFriendId(jsonObj.optLong("userId"));
		clazz.setNickName(jsonObj.optString("nickName"));
		clazz.setAvatar(jsonObj.optString("avatar"));
		clazz.setFriendName(jsonObj.optString("friendName"));
		clazz.setFriendPhone(jsonObj.optString("friendPhone"));
		clazz.setUserId(jsonObj.optLong("userId"));
		clazz.setType(jsonObj.optInt("type"));
		
		return clazz;
	}
	
	public static List<InviteFriend> parseFromJson(JSONArray jsonArray) {
		List<InviteFriend> clazzs = new ArrayList<InviteFriend>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				InviteFriend clazz = parseFromJson(jsonArray.optJSONObject(i));
				if(clazz != null)clazzs.add(clazz);
			}
		}
		return clazzs;
	}
	
	public Long getFriendId() {
		return friendId;
	}
	public void setFriendId(Long friendId) {
		this.friendId = friendId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getFriendName() {
		return friendName;
	}
	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}
	public String getFriendPhone() {
		return friendPhone;
	}
	public void setFriendPhone(String friendPhone) {
		this.friendPhone = friendPhone;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}

	
}
