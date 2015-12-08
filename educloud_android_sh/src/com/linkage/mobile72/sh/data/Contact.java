package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.utils.PinYinUtils;
import com.linkage.lib.util.LogUtils;

public class Contact implements Serializable{
	
	private static final long serialVersionUID = -6246130341205792988L;
	
	@DatabaseField(uniqueCombo=true)
	private long id;
	@DatabaseField(uniqueCombo=true)
	private String loginName;
	@DatabaseField(uniqueCombo=true)
	private int usertype;//1.好友、2、校园通讯录好友（老师专有）
	@DatabaseField(uniqueCombo=true)
	private String name;
	@DatabaseField
	private String phone;
	@DatabaseField
	private String avatar;
	@DatabaseField
	private String sortKey;
	@DatabaseField(defaultValue="#")
	private String categoryLabel;
	
	
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
	
	public String getCategoryLabel() {
		return categoryLabel;
	}

	public void setCategoryLabel(String categoryLabel) {
		this.categoryLabel = categoryLabel;
	}

	public int getUsertype() {
		return usertype;
	}

	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}

	public static Contact parseJson(JSONObject jsonObj) {
		Contact contact = new Contact();
		if(jsonObj.has("userId")) {
			contact.setId(jsonObj.optLong("userId"));
		}else if(jsonObj.has("friendId")) {
			contact.setId(jsonObj.optLong("friendId"));
		}
		contact.setPhone(jsonObj.optString("friendPhone"));
		contact.setLoginName(BaseApplication.getInstance().getDefaultAccount().getLoginname());
		String name = jsonObj.optString("friendName");
		contact.setAvatar(jsonObj.optString("avatar"));
		contact.setName(name);
		String temp = "#";
		try
        {
            temp = PinYinUtils.initSortKey(name);
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }
		contact.setSortKey(temp);
		contact.setCategoryLabel(PinYinUtils.getCategoryLabel(contact.getSortKey()));
		contact.setUsertype(jsonObj.optInt("usertype"));
		return contact;
	}

	public static List<Contact> parseFromJson(JSONArray jsonArray) {
		List<Contact> clazzs = new ArrayList<Contact>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				Contact clazz = parseJson(jsonArray.optJSONObject(i));
				if(clazz != null)clazzs.add(clazz);
			}
		}
		return clazzs;
	}

    public static Contact parseJsonByClassMember(JSONObject jsonObj, long classId) {
        Contact contact = new Contact();
        contact.setId(jsonObj.optLong("userId"));
        //contact.setPhone(jsonObj.optString("friendPhone"));
        contact.setLoginName(BaseApplication.getInstance().getDefaultAccount().getLoginname());
        contact.setAvatar(jsonObj.optString("avatar"));
        contact.setName(jsonObj.optString("nickName"));
        contact.setSortKey(jsonObj.optString("userRole"));
        contact.setUsertype((int)classId);
        return contact;
    }

    public static List<Contact> parseFromJsonByClassMember(JSONArray jsonArray, long classId) {
        List<Contact> clazzs = new ArrayList<Contact>();
        if(jsonArray != null && jsonArray.length() > 0) {
            for(int i=0;i<jsonArray.length();i++) {
                Contact clazz = parseJsonByClassMember(jsonArray.optJSONObject(i), classId);
                if(clazz != null)clazzs.add(clazz);
            }
        }
        return clazzs;
    }
}
