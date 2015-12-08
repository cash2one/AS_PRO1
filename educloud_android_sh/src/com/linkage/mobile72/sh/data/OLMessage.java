package com.linkage.mobile72.sh.data;

import java.io.Serializable;

import com.linkage.gson.annotations.SerializedName;
/**
 * OL消息
 */
//自动创建表
public class OLMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int _id; // 主键,int类型,数据库建表时此字段会设为自增长
	
	public int get_id() {
		return _id;
	}

	public String uuid;

	public String content;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	

}
