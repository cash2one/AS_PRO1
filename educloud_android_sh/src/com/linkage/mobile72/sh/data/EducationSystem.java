package com.linkage.mobile72.sh.data;

import java.io.Serializable;

import org.json.JSONObject;

public class EducationSystem  implements Serializable{
	
	private static final long serialVersionUID = 100126L;

	private String schoolName;
	private long schoolId;
	private String gradename;
	private int gradeType;
	private String eductionalystme;
	
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
	public String getGradename() {
		return gradename;
	}
	public void setGradename(String gradename) {
		this.gradename = gradename;
	}
	public int getGradeType() {
		return gradeType;
	}
	public void setGradeType(int gradeType) {
		this.gradeType = gradeType;
	}
	public String getEductionalystme() {
		return eductionalystme;
	}
	public void setEductionalystme(String eductionalystme) {
		this.eductionalystme = eductionalystme;
	}
	
	public static EducationSystem parseFromJson(JSONObject jsonObj) {
		EducationSystem es = new EducationSystem();
		es.setSchoolId(jsonObj.optLong("schoolId"));
		es.setSchoolName(jsonObj.optString("schoolName"));
		es.setGradename(jsonObj.optString("gradename"));
		es.setGradeType(jsonObj.optInt("gradeType"));
		es.setEductionalystme(jsonObj.optString("eductionalystme"));
		return es;
	}
	
}
