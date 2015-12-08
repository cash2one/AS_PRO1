package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.linkage.mobile72.sh.app.BaseApplication;

public class ClassRoom implements Serializable {

	private static final long serialVersionUID = -6144540780406960117L;

	@DatabaseField(uniqueCombo = true)
	private long id;
	@DatabaseField
	private String name;
	@DatabaseField
	private String avatar;
	@DatabaseField
	private long schoolId;
	@DatabaseField
	private String schoolName;
	@DatabaseField
	private int classNumber;
	@DatabaseField
	private String classLevel;
	@DatabaseField(uniqueCombo = true)
	private String loginName;
	@DatabaseField
	private int joinOrManage;//2已加入 1已管理的班级
	
	@DatabaseField
	private long taskid;
	@DatabaseField
	private int is_xxt;
	
	@DatabaseField(defaultValue="0")
	private int defaultClass;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	public int getClassNumber() {
		return classNumber;
	}

	public void setClassNumber(int classNumber) {
		this.classNumber = classNumber;
	}

	public String getClassLevel() {
		return classLevel;
	}

	public void setClassLevel(String classLevel) {
		this.classLevel = classLevel;
	}

	public int getJoinOrManage() {
		return joinOrManage;
	}

	public void setJoinOrManage(int joinOrManage) {
		this.joinOrManage = joinOrManage;
	}

	public long getTaskid() {
		return taskid;
	}

	public void setTaskid(long taskid) {
		this.taskid = taskid;
	}

	public int getIs_xxt() {
		return is_xxt;
	}

	public void setIs_xxt(int is_xxt) {
		this.is_xxt = is_xxt;
	}

	public int getDefaultClass() {
		return defaultClass;
	}

	public void setDefaultClass(int defaultClass) {
		this.defaultClass = defaultClass;
	}

	public static ClassRoom parseJson(JSONObject jsonObj, int joinOrManage) throws JSONException {
		ClassRoom classRoom = new ClassRoom();
		classRoom.setId(jsonObj.optLong("classroomId"));
		classRoom.setLoginName(BaseApplication.getInstance().getDefaultAccount().getLoginname());
		classRoom.setJoinOrManage(joinOrManage);
		classRoom.setName(jsonObj.optString("className"));
		classRoom.setClassNumber(jsonObj.optInt("classNumber"));
		classRoom.setAvatar(jsonObj.optString("avatar"));
		classRoom.setClassLevel(jsonObj.optString("classLevel"));
		classRoom.setSchoolId(jsonObj.optLong("schoolId"));
		classRoom.setSchoolName(jsonObj.optString("schoolName"));
		if(jsonObj.get("taskid") == null){
			classRoom.setTaskid(0);
		}else{
			classRoom.setTaskid(jsonObj.optLong("taskid"));
		}
		classRoom.setIs_xxt(jsonObj.optInt("is_xxt"));
		return classRoom;
	}

	public static List<ClassRoom> parseFromJson(JSONArray jsonArray, int joinOrManage) throws JSONException {
		List<ClassRoom> clazzs = new ArrayList<ClassRoom>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				ClassRoom clazz = parseJson(jsonArray.optJSONObject(i), joinOrManage);
				if(clazz != null)clazzs.add(clazz);
			}
		}
		return clazzs;
	}
}
