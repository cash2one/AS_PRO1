package com.linkage.mobile72.sh.data.http;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业、通知、点评等的通用类
 */
public class JXBeanDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String subjectName;// 科目，没有就空字符串
	private String sendTime;//发送时间
	private String recvTime;// 接收时间
    private String preSendTime;//定时时间
	private long sendUserId;// 发布者id
	private String sendUserName;// 发布者name
	private String sendPhone;//发布者的手机号码
	private String recvUserName;// 接受对象（个人）name，逗号分隔
	private String messageContent;// 内容
	private int readFlag;// 已读（收件箱时有此字段）0为已读，1为未读，发件箱为空字符串
	private String smsMessageType;// 1办公短信，2通知，3点评，4成绩，14作业，10投票，查询多个逗号分隔

	private String userGroupIds;//详情多加的参数 为回复带的参数
	private String userGroupTypes;//详情多加的参数 为回复带的参数
	
	private int readNum;
	private int unReadNum;
	private int replyNum;
	private int unReplyNum;
	private ArrayList<JXVote> voteJson;
	private int voteStatus;
	private ArrayList<JXMessageAttachment> msgAttachment;
	
	//新增的
	private int webNum;//web发送数量

	private int smsNum;//短信发送数量
 
	private int phoneNum;//手机发送数量 

    public static class JXMessageAttachment implements Serializable {
		private static final long serialVersionUID = 1L;
		private int attachmentType;
		private String attachmentUrl;
		private String voiceTime;
		
		public int getAttachmentType() {
			return attachmentType;
		}

		public void setAttachmentType(int attachmentType) {
			this.attachmentType = attachmentType;
		}

		public String getAttachmentUrl() {
			return attachmentUrl;
		}

		public void setAttachmentUrl(String attachmentUrl) {
			this.attachmentUrl = attachmentUrl;
		}

		public String getVoiceTime() {
			return voiceTime;
		}

		public void setVoiceTime(String voiceTime) {
			this.voiceTime = voiceTime;
		}

		public static JXMessageAttachment parseFromJson(JSONObject jsonObj){
			JXMessageAttachment a = new JXMessageAttachment();
			a.setAttachmentType(jsonObj.optInt("attachmentType"));
			a.setAttachmentUrl(jsonObj.optString("attachmentUrl"));
			a.setVoiceTime(jsonObj.optString("voiceTime"));
			return a;
		}
		
		public static ArrayList<JXMessageAttachment> parseFromJson(JSONArray jsonArray){
			ArrayList<JXMessageAttachment> attachs = new ArrayList<JXMessageAttachment>();
			if(jsonArray != null && jsonArray.length() > 0) {
				for(int i=0;i<jsonArray.length();i++) {
					JXMessageAttachment attach = parseFromJson(jsonArray.optJSONObject(i));
					if(attach != null)attachs.add(attach);
				}
			}
			return attachs;
		}
	}
	
	public static class JXVote implements Serializable {
		private static final long serialVersionUID = 1L;
		private String voteOption;
		private String voteContent;
		private int voteNum;
		private ArrayList<JXVotePerson> userList;
		
		public String getVoteOption() {
			return voteOption;
		}

		public void setVoteOption(String voteOption) {
			this.voteOption = voteOption;
		}

		public String getVoteContent() {
			return voteContent;
		}

		public void setVoteContent(String voteContent) {
			this.voteContent = voteContent;
		}

		public int getVoteNum() {
			return voteNum;
		}

		public void setVoteNum(int voteNum) {
			this.voteNum = voteNum;
		}

		public ArrayList<JXVotePerson> getUserList() {
			return userList;
		}

		public void setUserList(ArrayList<JXVotePerson> userList) {
			this.userList = userList;
		}

		public static JXVote parseFromJson(JSONObject jsonObj){
			JXVote a = new JXVote();
			a.setVoteOption(jsonObj.optString("voteOption"));
			a.setVoteContent(jsonObj.optString("voteContent"));
			a.setVoteNum(jsonObj.optInt("voteNum"));
			a.setUserList(JXVotePerson.parseFromJson(jsonObj.optJSONArray("userList")));
			return a;
		}
		
		public static ArrayList<JXVote> parseFromJson(JSONArray jsonArray){
			ArrayList<JXVote> attachs = new ArrayList<JXVote>();
			if(jsonArray != null && jsonArray.length() > 0) {
				for(int i=0;i<jsonArray.length();i++) {
					JXVote attach = parseFromJson(jsonArray.optJSONObject(i));
					if(attach != null)attachs.add(attach);
				}
			}
			return attachs;
		}
	}
	
	public static class JXVotePerson implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private long userId;
		private String userName;
		private String picture;
		
		public long getUserId() {
			return userId;
		}

		public void setUserId(long userId) {
			this.userId = userId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getPicture() {
			return picture;
		}

		public void setPicture(String picture) {
			this.picture = picture;
		}

		public static JXVotePerson parseFromJson(JSONObject jsonObj){
			JXVotePerson a = new JXVotePerson();
			a.setUserId(jsonObj.optLong("userId"));
			a.setUserName(jsonObj.optString("userName"));
			a.setPicture(jsonObj.optString("picture"));
			return a;
		}
		
		public static ArrayList<JXVotePerson> parseFromJson(JSONArray jsonArray){
			ArrayList<JXVotePerson> attachs = new ArrayList<JXVotePerson>();
			if(jsonArray != null && jsonArray.length() > 0) {
				for(int i=0;i<jsonArray.length();i++) {
					JXVotePerson attach = parseFromJson(jsonArray.optJSONObject(i));
					if(attach != null)attachs.add(attach);
				}
			}
			return attachs;
		}
		
	}
	
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

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getRecvTime() {
		return recvTime;
	}

	public void setRecvTime(String recvTime) {
		this.recvTime = recvTime;
	}

    public String getPreSendTime() {
        return preSendTime;
    }

    public void setPreSendTime(String preSendTime) {
        this.preSendTime = preSendTime;
    }

    public long getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(long sendUserId) {
		this.sendUserId = sendUserId;
	}

	public String getSendPhone() {
		return sendPhone;
	}

	public void setSendPhone(String sendPhone) {
		this.sendPhone = sendPhone;
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

	public String getUserGroupIds() {
		return userGroupIds;
	}

	public void setUserGroupIds(String userGroupIds) {
		this.userGroupIds = userGroupIds;
	}

	public String getUserGroupTypes() {
		return userGroupTypes;
	}

	public void setUserGroupTypes(String userGroupTypes) {
		this.userGroupTypes = userGroupTypes;
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

	public int getReadNum() {
		return readNum;
	}

	public void setReadNum(int readNum) {
		this.readNum = readNum;
	}

	public int getUnReadNum() {
		return unReadNum;
	}

	public void setUnReadNum(int unReadNum) {
		this.unReadNum = unReadNum;
	}

	public int getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(int replyNum) {
		this.replyNum = replyNum;
	}

	public int getUnReplyNum() {
		return unReplyNum;
	}

	public void setUnReplyNum(int unReplyNum) {
		this.unReplyNum = unReplyNum;
	}

	public ArrayList<JXVote> getVoteJson() {
		return voteJson;
	}

	public void setVoteJson(ArrayList<JXVote> voteJson) {
		this.voteJson = voteJson;
	}

	public void setMsgAttachment(ArrayList<JXMessageAttachment> msgAttachment) {
		this.msgAttachment = msgAttachment;
	}

	public int getVoteStatus() {
		return voteStatus;
	}

	public void setVoteStatus(int voteStatus) {
		this.voteStatus = voteStatus;
	}

	public List<JXMessageAttachment> getMsgAttachment() {
		return msgAttachment;
	}
	
	public int getWebNum()
    {
        return webNum;
    }

    public void setWebNum(int webNum)
    {
        this.webNum = webNum;
    }

    public int getSmsNum()
    {
        return smsNum;
    }

    public void setSmsNum(int smsNum)
    {
        this.smsNum = smsNum;
    }

    public int getPhoneNum()
    {
        return phoneNum;
    }

    public void setPhoneNum(int phoneNum)
    {
        this.phoneNum = phoneNum;
    }


	// 根据请求的类型，人为加入信息类型
	public static JXBeanDetail parseFromJson(JSONObject jsonObj, String smsType) {
		JXBeanDetail homework = new JXBeanDetail();
		//homework.setId(jsonObj.optLong("id"));
		homework.setSubjectName(jsonObj.optString("subject"));
		homework.setRecvTime(jsonObj.optString("recvTime"));
		homework.setSendTime(jsonObj.optString("sendTime"));
        homework.setPreSendTime(jsonObj.optString("presendTime"));
		homework.setSendUserId(jsonObj.optLong("sendUserId"));
		homework.setSendUserName(jsonObj.optString("sendName"));
		homework.setSendPhone(jsonObj.optString("sendPhone"));
		homework.setRecvUserName(jsonObj.optString("recvName"));
		homework.setMessageContent(jsonObj.optString("msgContent"));
		homework.setUserGroupIds(jsonObj.optString("userGroupIds"));
		homework.setUserGroupTypes(jsonObj.optString("userGroupTypes"));
		homework.setReadNum(jsonObj.optInt("readNum"));
		homework.setUnReadNum(jsonObj.optInt("unReadNum"));
		homework.setReplyNum(jsonObj.optInt("replyNum"));
		homework.setUnReplyNum(jsonObj.optInt("unReplyNum"));
		homework.setVoteJson(JXVote.parseFromJson(jsonObj.optJSONArray("voteJson")));
		homework.setVoteStatus(jsonObj.optInt("voteStatus"));
		homework.setMsgAttachment(JXMessageAttachment.parseFromJson(jsonObj.optJSONArray("msgAttachment")));
		homework.setSmsMessageType(smsType);
		homework.setWebNum(jsonObj.optInt("webNum"));
		homework.setSmsNum(jsonObj.optInt("smsNum"));
		homework.setPhoneNum(jsonObj.optInt("phoneNum"));
		return homework;
	}

}