package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class NotifyRead implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private String picture;
	private String phone;
	private Boolean state;
	
	public static NotifyRead parseFromJson(JSONObject jsonObj) {
		NotifyRead n = new NotifyRead();
		n.setId(jsonObj.optLong("userId"));
		n.setName(jsonObj.optString("userName"));
		n.setPhone(jsonObj.optString("phone"));
		n.setPicture(jsonObj.optString("picture"));
		n.setState(jsonObj.optInt("flag") == 1);
		return n;
	}
	
	public static List<NotifyRead> parseFromJson(JSONArray jsonArray) {
		List<NotifyRead> n = new ArrayList<NotifyRead>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				NotifyRead nn = parseFromJson(jsonArray.optJSONObject(i));
				if(nn != null)n.add(nn);
			}
		}
		return n;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Boolean getState() {
		return state;
	}
	public void setState(Boolean state) {
		this.state = state;
	}

}
