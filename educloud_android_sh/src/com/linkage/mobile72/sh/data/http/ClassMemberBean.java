package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 班级成员返回类
 * @author Yao
 *
 */
public class ClassMemberBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long userId;
	private String nickName;
	private String avatar;
	private Integer userRole;//1 普通成员 2班主任 3管理员
	private String phone;
	private Integer ruf_auditStatus;
	
	public Integer getRuf_auditStatus()
    {
        return ruf_auditStatus;
    }
    public void setRuf_auditStatus(Integer ruf_auditStatus)
    {
        this.ruf_auditStatus = ruf_auditStatus;
    }
    public String getPhone()
    {
        return phone;
    }
    public void setPhone(String phone)
    {
        this.phone = phone;
    }
    public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Integer getUserRole() {
		return userRole;
	}
	public void setUserRole(Integer userRole) {
		this.userRole = userRole;
	}
	
	public static ClassMemberBean parseFromJson(JSONObject jsonObj) {
		ClassMemberBean clazzMember = new ClassMemberBean();
		clazzMember.setUserId(jsonObj.optLong("userId"));
		clazzMember.setPhone(jsonObj.optString("phone"));
		clazzMember.setNickName(jsonObj.optString("nickName"));
		clazzMember.setAvatar(jsonObj.optString("avatar"));
		clazzMember.setUserRole(jsonObj.optInt("userRole"));
		clazzMember.setRuf_auditStatus(jsonObj.optInt("ruf_auditStatus"));
		return clazzMember;
	}
	
	public static List<ClassMemberBean> parseFromJson(JSONArray jsonArray) {
		List<ClassMemberBean> clazzMembers = new ArrayList<ClassMemberBean>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				ClassMemberBean clazzMember = parseFromJson(jsonArray.optJSONObject(i));
				if(clazzMember != null)clazzMembers.add(clazzMember);
			}
		}
		return clazzMembers;
	}
}
