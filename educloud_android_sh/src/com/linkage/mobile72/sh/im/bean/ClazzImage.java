package com.linkage.mobile72.sh.im.bean;

import java.io.Serializable;


@SuppressWarnings("serial")
public class ClazzImage implements Serializable {
	String smallPath;
	String smallPathH;
	String smallPathW;
	String orgPath;
	String fromWho;
	int supportNum;
	int replyNum;
	Long talkId;
	int isPraise;
	public int getIsPraise() {
		return isPraise;
	}
	public void setIsPraise(int isPraise) {
		this.isPraise = isPraise;
	}
	String talkContent;
	
	public String getTalkContent() {
		return talkContent;
	}
	public void setTalkContent(String talkContent) {
		this.talkContent = talkContent;
	}
	public String getSmallPath() {
		return smallPath;
	}
	public void setSmallPath(String smallPath) {
		this.smallPath = smallPath;
	}
	public String getSmallPathH() {
		return smallPathH;
	}
	public void setSmallPathH(String smallPathH) {
		this.smallPathH = smallPathH;
	}
	public String getSmallPathW() {
		return smallPathW;
	}
	public void setSmallPathW(String smallPathW) {
		this.smallPathW = smallPathW;
	}
	public String getOrgPath() {
		return orgPath;
	}
	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}
	public String getFromWho() {
		return fromWho;
	}
	public void setFromWho(String fromWho) {
		this.fromWho = fromWho;
	}
	public int getSupportNum() {
		return supportNum;
	}
	public void setSupportNum(int supportNum) {
		this.supportNum = supportNum;
	}
	public int getReplyNum() {
		return replyNum;
	}
	public void setReplyNum(int replyNum) {
		this.replyNum = replyNum;
	}
	public Long getTalkId() {
		return talkId;
	}
	public void setTalkId(Long talkId) {
		this.talkId = talkId;
	}
}
