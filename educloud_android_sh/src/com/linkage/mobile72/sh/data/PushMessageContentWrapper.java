package com.linkage.mobile72.sh.data;

import java.io.Serializable;

import org.json.JSONObject;

import com.linkage.gson.annotations.SerializedName;

public class PushMessageContentWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String title;
	public String content;
	@SerializedName("messageType")
	public int messageType;
	public String name;
	public long id;
	
//	public PushMessageContentWrapper parseJson(JSONObject json){
//		PushMessageContentWrapper temp = new PushMessageContentWrapper();
//		temp.id = 
//		return temp;
//	}
}
