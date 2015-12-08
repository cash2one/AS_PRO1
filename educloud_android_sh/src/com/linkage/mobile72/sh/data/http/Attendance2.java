package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Attendance2 implements Serializable {

	private static final long serialVersionUID = 1L;

	private String day;
	private int state;
	private String photo;
	private String reason;
	private int canEdit;
	
	
	public static Attendance2 parseFromJson(JSONObject jsonObj) {
		Attendance2 clazz = new Attendance2();
		clazz.setDay(jsonObj.optString("day"));
		clazz.setState(jsonObj.optInt("state"));
		clazz.setPhoto(jsonObj.optString("photo"));
		clazz.setReason(jsonObj.optString("reason"));
		clazz.setCanEdit(jsonObj.optInt("canEdit"));
		return clazz;
	}
	
	public static List<Attendance2> parseFromJson(JSONArray jsonArray) {
		List<Attendance2> clazzs = new ArrayList<Attendance2>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				Attendance2 clazz = parseFromJson(jsonArray.optJSONObject(i));
				if(clazz != null)clazzs.add(clazz);
			}
		}
		return clazzs;
	}
	
	
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getCanEdit() {
		return canEdit;
	}
	public void setCanEdit(int canEdit) {
		this.canEdit = canEdit;
	}
	
	
}
