package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 待审核班级成员返回类
 * @author Yao
 *
 */
public class ClassMemberApplyBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long userId;
	private String userName;
	private String applyReason;
	private String picture;
	private Integer type;//1 普通成员 2班主任 3管理员
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getApplyReason() {
		return applyReason;
	}

	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public static ClassMemberApplyBean parseFromJson(JSONObject jsonObj) {
		ClassMemberApplyBean clazzMember = new ClassMemberApplyBean();
		clazzMember.setUserId(jsonObj.optLong("userId"));
		clazzMember.setUserName(jsonObj.optString("userName"));
		clazzMember.setPicture(jsonObj.optString("avatar"));
		clazzMember.setApplyReason(jsonObj.optString("applyReason"));
		clazzMember.setType(jsonObj.optInt("type"));
		return clazzMember;
	}
	
	public static List<ClassMemberApplyBean> parseFromJson(JSONArray jsonArray) {
		List<ClassMemberApplyBean> clazzMembers = new ArrayList<ClassMemberApplyBean>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				ClassMemberApplyBean clazzMember = parseFromJson(jsonArray.optJSONObject(i));
				if(clazzMember != null)clazzMembers.add(clazzMember);
			}
		}
		return clazzMembers;
	}
	
}
