package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;

public class AccountChild  implements Serializable{
	private static final long serialVersionUID = 100125L;
	@DatabaseField(generatedId=true)
	private long _id;
	@DatabaseField(uniqueCombo=true)
	private long userid;
	@DatabaseField(uniqueCombo=true)
	private long id;
	@DatabaseField
	private String name;
	@DatabaseField
	private String picture;
	@DatabaseField(defaultValue="0")
	private int defaultChild;
	@DatabaseField
	private int xxt_type;// 1、已订购,0未订购,2退订 
	
	private EducationSystem studentExtend;
	
    public int getXxt_type()
    {
        return xxt_type;
    }
    public void setXxt_type(int xxt_type)
    {
        this.xxt_type = xxt_type;
    }
    
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
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
	public int getDefaultChild() {
		return defaultChild;
	}
	public void setDefaultChild(int defaultChild) {
		this.defaultChild = defaultChild;
	}
	public EducationSystem getStudentExtend() {
		return studentExtend;
	}
	public void setStudentExtend(EducationSystem studentExtend) {
		this.studentExtend = studentExtend;
	}
	
	public static AccountChild parseFromJsonForLogin(JSONObject jsonObj) {
		AccountChild child = new AccountChild();
		child.setId(jsonObj.optLong("id"));
		child.setName(jsonObj.optString("name"));
		child.setXxt_type(jsonObj.optInt("xxtType"));
		return child;
	}
	
	public static AccountChild pareFromJsonForPersonInfo(JSONObject jsonObj) {
		AccountChild child = new AccountChild();
		child.setId(jsonObj.optLong("id"));
		child.setName(jsonObj.optString("name"));
		child.setPicture(jsonObj.optString("picture"));
		child.setStudentExtend(EducationSystem.parseFromJson(jsonObj.optJSONObject("studentExtend")));
		return child;
	}
	
	public static List<AccountChild> parseFromJsonForLogin(JSONArray jsonArray) {
		List<AccountChild> clazzs = new ArrayList<AccountChild>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				AccountChild clazz = parseFromJsonForLogin(jsonArray.optJSONObject(i));
				if(clazz != null)clazzs.add(clazz);
			}
		}
		return clazzs;
	}
	
	public static List<AccountChild> pareFromJsonForPersonInfo(JSONArray jsonArray) {
		List<AccountChild> clazzs = new ArrayList<AccountChild>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				AccountChild clazz = pareFromJsonForPersonInfo(jsonArray.optJSONObject(i));
				if(clazz != null)clazzs.add(clazz);
			}
		}
		return clazzs;
	}
}
