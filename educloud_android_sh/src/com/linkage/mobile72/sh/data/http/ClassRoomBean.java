package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 根据学校（组织）获取班级（子组织）列表时返回的类
 * @author Yao
 *
 */
public class ClassRoomBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Long schoolId;
	private String schoolName;
	private Long classroomId;
	private String classroomName;
	private String leaderName;
	private Integer isChoose;//1代表用户已加入 2代表未加入
	private String avatar;
	private String classroom_popId;
	private Long memCount;
	private Integer isJoin;//isJoin:1已加入 2未加入
	
	public Long getMemCount() {
		return memCount;
	}
	
	public void setMemCount(Long memCount) {
		this.memCount = memCount;
	}
	
	public String getClassroom_popId() {
		return classroom_popId;
	}

	public void setClassroom_popId(String classroom_popId) {
		this.classroom_popId = classroom_popId;
	}

	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public Long getSchoolId() {
		return schoolId;
	}


	public void setSchoolId(Long schoolId) {
		this.schoolId = schoolId;
	}


	public String getSchoolName() {
		return schoolName;
	}


	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}


	public Long getClassroomId() {
		return classroomId;
	}


	public void setClassroomId(Long classroomId) {
		this.classroomId = classroomId;
	}


	public String getClassroomName() {
		return classroomName;
	}


	public void setClassroomName(String classroomName) {
		this.classroomName = classroomName;
	}


	public String getLeaderName() {
		return leaderName;
	}


	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}


	public Integer getIsChoose() {
		return isChoose;
	}


	public void setIsChoose(Integer isChoose) {
		this.isChoose = isChoose;
	}


	public static ClassRoomBean parseFromJson(JSONObject jsonObj) {
		ClassRoomBean clazz = new ClassRoomBean();
		clazz.setClassroomId(jsonObj.optLong("classroomId"));
		clazz.setClassroomName(jsonObj.optString("classroomName"));
		if(jsonObj.has("memCount"))
			clazz.setMemCount(jsonObj.optLong("memCount"));
		else
			clazz.setMemCount(jsonObj.optLong("classNumber"));
		clazz.setClassroom_popId(jsonObj.optString("classroom_popId"));
		if(jsonObj.has("isChoose")) {
			clazz.setIsChoose(jsonObj.optInt("isChoose"));
		}else if(jsonObj.has("isJoin")) {
			clazz.setIsChoose(jsonObj.optInt("isJoin"));
		}
		if(jsonObj.has("leaderName")) {
			clazz.setLeaderName(jsonObj.optString("leaderName"));
		}else if(jsonObj.has("change_teacherName")) {
			clazz.setLeaderName(jsonObj.optString("change_teacherName"));
		}
		clazz.setSchoolId(jsonObj.optLong("schoolId"));
		clazz.setSchoolName(jsonObj.optString("schoolName"));
		clazz.setAvatar(jsonObj.optString("avatar"));
		return clazz;
	}
	
	public static List<ClassRoomBean> parseFromJson(JSONArray jsonArray) {
		List<ClassRoomBean> clazzs = new ArrayList<ClassRoomBean>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				ClassRoomBean clazz = parseFromJson(jsonArray.optJSONObject(i));
				if(clazz != null)clazzs.add(clazz);
			}
		}
		return clazzs;
	}
}
