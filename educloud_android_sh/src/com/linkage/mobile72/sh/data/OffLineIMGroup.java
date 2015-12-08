package com.linkage.mobile72.sh.data;

import java.io.Serializable;

import com.linkage.gson.annotations.SerializedName;

public class OffLineIMGroup implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int _id; // 主键,int类型,数据库建表时此字段会设为自增长
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	@SerializedName("createTime")
	public String createTime;

	@SerializedName("fromUserId")
	public String fromUserId;

	@SerializedName("fromUserName")
	public String fromUserName;// long
	
	@SerializedName("persons")
	public String persons;// longpersons
	
	@SerializedName("type")
	public String type;// longpersons

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getPersons() {
		return persons;
	}

	public void setPersons(String persons) {
		this.persons = persons;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}
