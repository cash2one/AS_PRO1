package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class RankNumber implements Serializable{

	private static final long serialVersionUID = -6106077909495515737L;

	private String userName;
	
	private String userImg;
	
	private String total;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserImg() {
		return userImg;
	}
	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	public static RankNumber parseFromJson(JSONObject jsonObj) {
		RankNumber number = new RankNumber();
		number.setUserName(jsonObj.optString("username"));
		number.setUserImg(jsonObj.optString("userimg"));
		number.setTotal(jsonObj.optString("total"));
		return number;
	}
	
	public static ArrayList<RankNumber> parseFromJson(JSONArray jsonArray) {
		ArrayList<RankNumber> numbers = new ArrayList<RankNumber>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				RankNumber number = parseFromJson(jsonArray.optJSONObject(i));
				if(number != null)numbers.add(number);
			}
		}
		return numbers;
	}
}
