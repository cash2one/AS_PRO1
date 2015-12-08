package com.linkage.mobile72.sh.data.http;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 班级详细页返回的班级资料对象
 * @author Yao
 *
 */
public class ClassInfoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long classroomId;
	private String classroomName;
	private String classroom_popId;
	private String change_teacherID;
	private String description;
	private String avatar;
    private int memberCount;//班级人数
	private String redion;//新加 区域
	private String schoolName;//新加 学校
	private Integer waitapplynum;
	private String mycard;
	private Integer isJoin;//1已加入 2未加入 3申请待审核中
	private Integer isAuto;//0 不需要验证  1要验证
	
	
	private List<ClassMemberBean> memberInfoList;
	
	public static ClassInfoBean parseFromJson(JSONObject jsonObj) {
		ClassInfoBean clazz = new ClassInfoBean();
		clazz.setClassroomId(jsonObj.optLong("classroomId"));
		clazz.setClassroomName(jsonObj.optString("classroomName"));
		clazz.setClassroom_popId(jsonObj.optString("classroom_popId"));
		clazz.setChange_teacherID(jsonObj.optString("change_teacherID"));
		clazz.setDescription(jsonObj.optString("description"));
		clazz.setAvatar(jsonObj.optString("avatar"));
		clazz.setRedion(jsonObj.optString("redion"));
		clazz.setSchoolName(jsonObj.optString("schoolName"));
		clazz.setIsJoin(jsonObj.optInt("isJoin"));
		clazz.setWaitapplynum(jsonObj.optInt("waitapplynum"));
		clazz.setMycard(jsonObj.optString("mycard"));
		clazz.setIsAuto(jsonObj.optInt("isAuto"));
        clazz.setMemberCount(jsonObj.optInt("memCount"));
		clazz.setMemberInfoList(ClassMemberBean.parseFromJson(jsonObj.optJSONArray("memberInfoList")));
		return clazz;
	}
	
	public static List<ClassInfoBean> parseFromJson(JSONArray jsonArray) {
		List<ClassInfoBean> clazzs = new ArrayList<ClassInfoBean>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				ClassInfoBean clazz = parseFromJson(jsonArray.optJSONObject(i));
				if(clazz != null)clazzs.add(clazz);
			}
		}
		return clazzs;
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
	public String getClassroom_popId() {
		return classroom_popId;
	}
	public void setClassroom_popId(String classroom_popId) {
		this.classroom_popId = classroom_popId;
	}
	public String getChange_teacherID() {
		return change_teacherID;
	}
	public void setChange_teacherID(String change_teacherID) {
		this.change_teacherID = change_teacherID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Integer getWaitapplynum() {
		return waitapplynum;
	}
	public void setWaitapplynum(Integer waitapplynum) {
		this.waitapplynum = waitapplynum;
	}
	public String getMycard() {
		return mycard;
	}
	public void setMycard(String mycard) {
		this.mycard = mycard;
	}

	public List<ClassMemberBean> getMemberInfoList() {
		return memberInfoList;
	}

	public void setMemberInfoList(List<ClassMemberBean> memberInfoList) {
		this.memberInfoList = memberInfoList;
	}
	public Integer getIsJoin() {
		return isJoin;
	}

	public void setIsJoin(Integer isJoin) {
		this.isJoin = isJoin;
	}

	public Integer getIsAuto() {
		return isAuto;
	}

	public void setIsAuto(Integer isAuto) {
		this.isAuto = isAuto;
	}

	public String getRedion() {
		return redion;
	}

	public void setRedion(String redion) {
		this.redion = redion;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}
