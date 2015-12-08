package com.linkage.mobile72.sh.im.bean;

import org.json.JSONObject;

import android.database.Cursor;

import com.linkage.mobile72.sh.im.provider.Ws.ContactTable;
import com.linkage.mobile72.sh.utils.CursorHelper;

public class PushContact implements java.io.Serializable{

	private static final long serialVersionUID = -6232486549715966627L;
	
	private long id;
	private String name;
	private int type;
	private String status;
	private long groupId;
	private String groupName;
	private String avatarUrl;
	private int contactType;
	
	public static PushContact fromJsonObject(JSONObject json) {
		PushContact c = new PushContact();
		c.id = json.optLong("id");
		c.name = json.optString("name");
		c.type = json.optInt("type");
		c.status = json.optString("status");
		c.groupId = json.optLong("group_id");
		c.groupName = json.optString("group_name");
		c.avatarUrl = json.optString("avatar");
		return c;
	}
	
	public static PushContact fromCursor(Cursor cursor) {
		PushContact c = new PushContact();
		CursorHelper helper = new CursorHelper(cursor);
		c.id = helper.getLong(ContactTable.ID);
		c.name = helper.getString(ContactTable.NAME);
		c.type = helper.getInt(ContactTable.TYPE);
		c.status = helper.getString(ContactTable.STATUS);
		c.groupId = helper.getLong(ContactTable.GROUP_ID);
		c.groupName = helper.getString(ContactTable.GROUP_NAME);
		c.avatarUrl = helper.getString(ContactTable.AVATAR_URL);
		c.contactType = helper.getInt(ContactTable.CONTACT_TYPE);
		return c;
	}
	
	public int getContactType() {
		return contactType;
	}

	public void setContactType(int contactType) {
		this.contactType = contactType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

}
