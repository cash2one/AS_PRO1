package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class WonderExercise implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5097292441186643676L;
	private String id;
	private String picUrl;
	private String title;
	private int flag;// 0表已结束，1表进行中
	private String detailUrl;

	public static List<WonderExercise> parseJson(JSONObject json) {
		List<WonderExercise> list = new ArrayList<WonderExercise>();
		JSONArray array = json.optJSONArray("data");
		if (null != array && array.length() > 0) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.optJSONObject(i);
				if (null != obj) {
					WonderExercise we = new WonderExercise();
					we.setId(String.valueOf(obj.optLong("id")));
					we.setPicUrl(obj.optString("url"));
					we.setTitle(obj.optString("title"));
					we.setDetailUrl(obj.optString("action"));
					we.setFlag(obj.optInt("timeout"));
					list.add(we);
				}
			}
		}
		return list;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

}
