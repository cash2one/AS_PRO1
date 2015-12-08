package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SearchSchool implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
    private String schoolName;
    private long schoolId;
    private	String schoolAvatar;
    private String address;
    
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public long getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(long schoolId) {
		this.schoolId = schoolId;
	}
	public String getSchoolAvatar() {
		return schoolAvatar;
	}
	public void setSchoolAvatar(String schoolAvatar) {
		this.schoolAvatar = schoolAvatar;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
    
	public static SearchSchool parseFromJson(JSONObject jsonObj) {
		SearchSchool school = new SearchSchool();
		school.setSchoolId(jsonObj.optLong("schoolId"));
		school.setSchoolName(jsonObj.optString("schoolName"));
		school.setSchoolAvatar(jsonObj.optString("schoolAvatar"));
		school.setAddress(jsonObj.optString("address"));
		return school;
	}
	
	public static List<SearchSchool> parseFromJson(JSONArray jsonArray) {
		List<SearchSchool> schools = new ArrayList<SearchSchool>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				SearchSchool school = parseFromJson(jsonArray.optJSONObject(i));
				if(school != null)schools.add(school);
			}
		}
		return schools;
	}
}
