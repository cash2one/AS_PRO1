package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class PaymentBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long projectId;//relProjectClassId 班级缴费关系表ID，用于在用户查看待缴费通知时，关联所在班级和缴费内容用。
	private String projectName;
	private long schoolId;
	private String schoolName;
	private String classroomId;
	private String classroomName;
	private long projectTypeId;
	private String projectTypeName;
	private String projectTypePicture;
	private double money;
	private String startDate;
	private String expirationDate;
	private String description;
	
	private int payState;
	private String payDate;
	
	public static PaymentBean parseFromJson(JSONObject jsonObj) {
		PaymentBean pt = new PaymentBean();
		if(jsonObj.has("projectId"))
			pt.setProjectId(jsonObj.optLong("projectId"));
		else if(jsonObj.has("relProjectClassId"))
			pt.setProjectId(jsonObj.optLong("relProjectClassId"));
		pt.setProjectName(jsonObj.optString("projectName"));
		pt.setSchoolId(jsonObj.optLong("schoolId"));
		pt.setSchoolName(jsonObj.optString("schoolName"));
		pt.setClassroomId(jsonObj.optString("classroomId"));
		pt.setClassroomName(jsonObj.optString("classroomName"));
		pt.setProjectTypeId(jsonObj.optLong("projectTypeId"));
		pt.setProjectTypeName(jsonObj.optString("projectTypeName"));
		pt.setProjectTypePicture(jsonObj.optString("projectTypePicture"));
		pt.setMoney(jsonObj.optDouble("money"));
		pt.setStartDate(jsonObj.optString("startDate"));
		pt.setExpirationDate(jsonObj.optString("expirationDate"));
		pt.setDescription(jsonObj.optString("description"));
		pt.setPayState(jsonObj.optInt("payState"));
		pt.setPayDate(jsonObj.optString("payDate"));
		return pt;
	}
	
	public static List<PaymentBean> parseFromJson(JSONArray jsonArray) {
		List<PaymentBean> pts = new ArrayList<PaymentBean>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				PaymentBean pt = parseFromJson(jsonArray.optJSONObject(i));
				if(pt != null)pts.add(pt);
			}
		}
		return pts;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public long getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(long schoolId) {
		this.schoolId = schoolId;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getClassroomId() {
		return classroomId;
	}

	public void setClassroomId(String classroomId) {
		this.classroomId = classroomId;
	}

	public String getClassroomName() {
		return classroomName;
	}

	public void setClassroomName(String classroomName) {
		this.classroomName = classroomName;
	}

	public long getProjectTypeId() {
		return projectTypeId;
	}

	public void setProjectTypeId(long projectTypeId) {
		this.projectTypeId = projectTypeId;
	}

	public String getProjectTypeName() {
		return projectTypeName;
	}

	public void setProjectTypeName(String projectTypeName) {
		this.projectTypeName = projectTypeName;
	}

	public String getProjectTypePicture() {
		return projectTypePicture;
	}

	public void setProjectTypePicture(String projectTypePicture) {
		this.projectTypePicture = projectTypePicture;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPayState() {
		return payState;
	}

	public void setPayState(int payState) {
		this.payState = payState;
	}

	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}
	
	
}
