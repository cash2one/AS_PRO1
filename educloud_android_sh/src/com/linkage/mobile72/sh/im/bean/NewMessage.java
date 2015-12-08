package com.linkage.mobile72.sh.im.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class NewMessage extends MessageIn {
	
	public static final String TYPE_TEXT = "txt";
	public static final String TYPE_AUDIO = "audio";
	public static final String TYPE_PICTURE = "picture";
	public static final String TYPE_HOMEWORK = "homework";
	public static final String TYPE_NOTICE = "notice";
	
	private long timestamp;
	private String content;
	private long groupId;
	private String groupName;//群名称
	private String messageType;
	private String contentType;
	private Member from;
	private String icon;
	private String name;
	

	private String title;
	
	
	public static NewMessage fromJson(String str) throws JSONException {
		JSONObject json = new JSONObject(str);
		NewMessage msg = new NewMessage();
		msg.setType(json.optString("type"));
		msg.timestamp = json.optLong("timestamp");
		msg.content = json.optString("content");
		msg.groupId = json.optLong("group_id");
		msg.groupName = json.optString("group_name");
		msg.messageType = json.optString("msg_type");
		msg.contentType = json.optString("content_type");
		msg.icon = json.optString("icon");
		msg.title = json.optString("title");
		msg.name = json.optString("groupCard");
		msg.groupName = json.optString("name");
		msg.from = Member.fromJsonObject(json.optJSONObject("from"));
		return msg;
	}

	public static class Member {
		private long id;
		private String name;

		public static Member fromJsonObject(JSONObject json) {
			Member m = new Member();
			m.id = json.optLong("id");
			m.name = json.optString("name");
			return m;
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

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Member getFrom() {
		return from;
	}

	public void setFrom(Member from) {
		this.from = from;
	}
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
