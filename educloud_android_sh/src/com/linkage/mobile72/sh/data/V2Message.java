package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.Date;

import com.linkage.gson.annotations.SerializedName;


/*
 * 本地记录聊天界面消息20条
 */
public class V2Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id; // 主键,int类型,数据库建表时此字段会设为自增长

	@SerializedName("fromUserName")
	public String fromUserName;

	@SerializedName("fromUserId")
	public String fromUserId;

	@SerializedName("fromUserid")
	public long fromUserid;// long

	@SerializedName("toRecordid")
	public long toRecordid;

	@SerializedName("contackerId")
	public long contackerId;

	@SerializedName("taskId")
	public String taskId;

	@SerializedName("sendType")
	public int sendType;

	public String type;// OL

	public Date sendtime;

	@SerializedName("sendTime")
	// OL
	public String sendTime;

	@SerializedName("messageType")
	// int
	public String messageType;

	@SerializedName("groupId")
	public String groupId;

	@SerializedName("sendContents")
	public String sendContents;

	@SerializedName("content")
	public String content;// content

	@SerializedName("serverTime")
	public String serverTime;

	@SerializedName("voiceLength")
	public String voiceLength;

	@SerializedName("timeFlag")
	public String timeFlag;
	

	// 发送使用
	public int sendFlag; // 是否发送成功0发送成功1正在发送2发送失败
	public long sendFileLength;
	public long sendVoiceLenght;

	@SerializedName("filePath")
	public String filePath;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getToRecordid() {
		return toRecordid;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getMessageType() {
		return messageType;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getSendContents() {
		return sendContents;
	}

	public String getServerTime() {
		return serverTime;
	}

	public String getVoiceLength() {
		return voiceLength;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public void setToRecordid(long toRecordid) {
		this.toRecordid = toRecordid;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setSendContents(String sendContents) {
		this.sendContents = sendContents;
	}

	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}

	public void setVoiceLength(String voiceLength) {
		this.voiceLength = voiceLength;
	}

	public String getTimeFlag() {
		return timeFlag;
	}

	public void setTimeFlag(String timeFlag) {
		this.timeFlag = timeFlag;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public long getFromUserid() {
		return fromUserid;
	}

	public void setFromUserid(long fromUserid) {
		this.fromUserid = fromUserid;
	}

	public int getSendType() {
		return sendType;
	}

	public void setSendType(int sendType) {
		this.sendType = sendType;
	}

	public Date getSendtime() {
		return sendtime;
	}

	public void setSendtime(Date sendtime) {
		this.sendtime = sendtime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
}
