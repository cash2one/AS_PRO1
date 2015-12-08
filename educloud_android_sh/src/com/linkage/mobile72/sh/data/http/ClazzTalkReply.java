package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class ClazzTalkReply implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private long fromId;
	private long fromStudentId;
	private String fromName;
	private long toId;
	private long toStudentId;
	private String toName;
	private String content;
	
	public static ClazzTalkReply parseFromJson(JSONObject jsonObj) {
		ClazzTalkReply talk = new ClazzTalkReply();
		talk.setId(jsonObj.optLong("id"));
		talk.setContent(jsonObj.optString("content"));
		talk.setFromId(jsonObj.optLong("fromId"));
		talk.setFromName(jsonObj.optString("fromName"));
		talk.setFromStudentId(jsonObj.optLong("fromstudentId"));
		talk.setToId(jsonObj.optLong("toId"));
		talk.setToName(jsonObj.optString("toName"));
		talk.setToStudentId(jsonObj.optLong("tostudentid"));
		return talk;
	}

	public static List<ClazzTalkReply> parseFromJson(JSONArray jsonArray) {
		List<ClazzTalkReply> talks = new ArrayList<ClazzTalkReply>();
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				ClazzTalkReply talk = parseFromJson(jsonArray.optJSONObject(i));
				if (talk != null)
					talks.add(talk);
			}
		}
		return talks;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getFromId() {
		return fromId;
	}
	public void setFromId(long fromId) {
		this.fromId = fromId;
	}
	public long getFromStudentId() {
		return fromStudentId;
	}
	public void setFromStudentId(long fromStudentId) {
		this.fromStudentId = fromStudentId;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public long getToId() {
		return toId;
	}
	public void setToId(long toId) {
		this.toId = toId;
	}
	public long getToStudentId() {
		return toStudentId;
	}
	public void setToStudentId(long toStudentId) {
		this.toStudentId = toStudentId;
	}
	public String getToName() {
		return toName;
	}
	public void setToName(String toName) {
		this.toName = toName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
