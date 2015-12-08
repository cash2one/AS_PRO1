package com.linkage.mobile72.sh.im.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PushFriendList extends MessageIn {
	
	private List<PushContact> mFriends;
	private List<PushContact> mClassFriends;
	
	public static PushFriendList fromJson(String str) throws JSONException {
		JSONObject json = new JSONObject(str);
		PushFriendList list = new PushFriendList();
		list.setType(json.optString("type"));
		list.setType(FRIENDS);
		JSONArray jsonArray = json.getJSONArray("friends");
		int size = jsonArray.length();
		list.mFriends = new ArrayList<PushContact>(size);
		for(int i = 0; i < size; i ++) {
			PushContact c = PushContact.fromJsonObject(jsonArray.getJSONObject(i));
			if(c.getId() != -1) {
				list.mFriends.add(c);
			}
		}
		jsonArray = json.getJSONArray("class_members");
		size = jsonArray.length();
		list.mClassFriends = new ArrayList<PushContact>(size);
		for(int i = 0; i < size; i ++) {
			PushContact c = PushContact.fromJsonObject(jsonArray.getJSONObject(i));
			if(c.getId() != -1) {
				list.mClassFriends.add(c);
			}
		}
		return list;
	}
	
	public List<PushContact> getFriends() {
		return mFriends;
	}
	
	public List<PushContact> getClassFriends() {
		return mClassFriends;
	}
}
