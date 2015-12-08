package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;

/**
 * 作业、通知、点评等的通用类
 */
public class JXBean implements Serializable {
	private static final long serialVersionUID = -4475819101435637712L;

	@DatabaseField(id = true)
	private long id;
	@DatabaseField
	private String subjectName;// 科目，没有就空字符串
	@DatabaseField
	private String recvTime;// 发布时间
	@DatabaseField
	private long sendUserId;// 发布者id
	@DatabaseField
	private String sendUserName;// 发布者name
	@DatabaseField
	private String recvUserName;// 接受对象（个人）name，逗号分隔
	@DatabaseField
	private String messageContent;// 内容
	@DatabaseField
	private int readFlag;// 已读（收件箱时有此字段）0为已读，1为未读，发件箱为空字符串
	@DatabaseField
	private String smsMessageType;// 1办公短信，2通知，3点评，4成绩，14作业，10投票，查询多个逗号分隔

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getRecvTime() {
		return recvTime;
	}

	public void setRecvTime(String recvTime) {
		this.recvTime = recvTime;
	}

	public long getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(long sendUserId) {
		this.sendUserId = sendUserId;
	}

	public String getSendUserName() {
		return sendUserName;
	}

	public void setSendUserName(String sendUserName) {
		this.sendUserName = sendUserName;
	}

	public String getRecvUserName() {
		return recvUserName;
	}

	public void setRecvUserName(String recvUserName) {
		this.recvUserName = recvUserName;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public int getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(int readFlag) {
		this.readFlag = readFlag;
	}

	public String getSmsMessageType() {
		return smsMessageType;
	}

	public void setSmsMessageType(String smsMessageType) {
		this.smsMessageType = smsMessageType;
	}

	public static JXBean parseFromJsonForParentHome(JSONObject jsonObj) {
		JXBean homework = new JXBean();
		homework.setId(jsonObj.optLong("id"));
		//homework.setSubjectName(jsonObj.optString("subjectName"));
		homework.setRecvTime(jsonObj.optString("sendTime"));
		//homework.setSendUserId(jsonObj.optLong("sendUserId"));
		//homework.setSendUserName(jsonObj.optString("sendUserName"));
		//homework.setRecvUserName(jsonObj.optString("recvUserName"));
		homework.setMessageContent(jsonObj.optString("messageContent"));
		homework.setReadFlag(jsonObj.optInt("readFlag"));
		homework.setSmsMessageType(""+jsonObj.optInt("smsMessageType"));
		return homework;
	}

	public static JXBean parseFromJsonForMessageFragment(JSONObject jsonObj) {
		JXBean homework = new JXBean();
		homework.setId(jsonObj.optLong("messageId"));
		//homework.setSubjectName(jsonObj.optString("subjectName"));
		homework.setRecvTime(jsonObj.optString("sendTime"));
		//homework.setSendUserId(jsonObj.optLong("sendUserId"));
		//homework.setSendUserName(jsonObj.optString("sendUserName"));
		//homework.setRecvUserName(jsonObj.optString("recvUserName"));
		homework.setMessageContent(jsonObj.optString("messageContent"));
		homework.setReadFlag(jsonObj.optInt("readFlag"));
		homework.setSmsMessageType(""+jsonObj.optInt("smsMessageType"));
		return homework;
	}
	
	// 根据请求的类型，人为加入信息类型
	public static JXBean parseFromJson(JSONObject jsonObj, String smsType) {
		JXBean homework = new JXBean();
		homework.setId(jsonObj.optLong("id"));
		homework.setSubjectName(jsonObj.optString("subjectName"));
		homework.setRecvTime(jsonObj.optString("recvTime"));
		homework.setSendUserId(jsonObj.optLong("sendUserId"));
		homework.setSendUserName(jsonObj.optString("sendUserName"));
		homework.setRecvUserName(jsonObj.optString("recvUserName"));
		homework.setMessageContent(jsonObj.optString("messageContent"));
		homework.setReadFlag(jsonObj.optInt("readFlag"));
		if(jsonObj.has("messageType")) {
			homework.setSmsMessageType(jsonObj.optString("messageType"));
		}else {
			homework.setSmsMessageType(smsType);
		}
		return homework;
	}

	public static List<JXBean> parseFromJson(JSONArray jsonArray, String smsType) {
		List<JXBean> homeworks = new ArrayList<JXBean>();
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				JXBean homework = parseFromJson(jsonArray.optJSONObject(i), smsType);
				homeworks.add(homework);
			}
		}
		return homeworks;
	}
}