package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OffLineMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int _id; // 主键,int类型,数据库建表时此字段会设为自增长
	
	public int get_id() {
		return _id;
	}
	
	public OffLineImMessage message;
	public OffLineImMessage getMessage() {
		return message;
	}
	public void setMessage(OffLineImMessage message) {
		this.message = message;
	}
	

}
