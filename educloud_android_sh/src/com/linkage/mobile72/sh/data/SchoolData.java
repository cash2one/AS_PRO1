package com.linkage.mobile72.sh.data;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.linkage.mobile72.sh.app.BaseApplication;

public class SchoolData {
	@DatabaseField
	long userid;
	@DatabaseField
	String schoolName;
	@DatabaseField
	String schoolPicture;
	@DatabaseField
	long schoolId;
	@DatabaseField
	int isJoin;
	@DatabaseField
	int type;
	
	public SchoolData() {
	}
	 
	@Override
	public String toString() {
	  StringBuilder sb = new StringBuilder();
//	  sb.append("id=").append(id);
	  sb.append(" ,userid=").append(userid);
	  sb.append(" ,schoolName=").append(schoolName);
	  sb.append(" ,schoolPicture=").append(schoolPicture);
	  sb.append(" ,schoolId=").append(schoolId);
	  sb.append(" ,isJoin=").append(isJoin);
	  sb.append(" ,type=").append(type);
	
	  return sb.toString();
	}
	
	public static SchoolData parseFromJson(JSONObject jsonObj) {
		SchoolData info = new SchoolData();
		AccountData currentAccount = BaseApplication.getInstance().getDefaultAccount();
		info.setUserid(currentAccount.getUserId());
		info.setSchoolName(jsonObj.optString("schoolName"));
		info.setSchoolPicture(jsonObj.optString("schoolPicture"));
		info.setSchoolId(jsonObj.optLong("schoolId"));
		info.setIsJoin(jsonObj.optInt("isJoin"));
		info.setType(jsonObj.optInt("type"));
		return info;
	}
	
	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

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

	public int getIsJoin() {
		return isJoin;
	}

	public void setIsJoin(int isJoin) {
		this.isJoin = isJoin;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSchoolPicture() {
		return schoolPicture;
	}

	public void setSchoolPicture(String schoolPicture) {
		this.schoolPicture = schoolPicture;
	}
	

}
