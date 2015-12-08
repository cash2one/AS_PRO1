package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.linkage.gson.JsonArray;

public class Topic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5415417321253873359L;
	private String id;
	private String title;
	private String date;
	private String picUrl;
	private String content;
	private String commentNum;
	private String readNum;
	private String detailUrl;
	private String commentUrl;
	
	
	
	public static List<Topic> parseJson(JSONObject jsonObject){
		List<Topic> list = new ArrayList<Topic>();
			JSONArray array = jsonObject.optJSONArray("data");
			if (null != array && array.length()>0) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.optJSONObject(i);
					Topic topic = new Topic();
					topic.setId(String.valueOf(obj.optInt("id")));
					topic.setTitle(obj.optString("name"));
					topic.setCommentNum(String.valueOf(obj.optInt("commentNum")));
					topic.setReadNum(String.valueOf(obj.optInt("clickNum")));
					topic.setPicUrl(obj.optString("picUrl"));
					topic.setDetailUrl(obj.optString("detailUrl"));
					topic.setCommentUrl(obj.optString("commentsUrl"));
					topic.setDate(obj.optString("time"));
					topic.setContent(obj.optString("summary"));
					list.add(topic);
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

	public String getCommentUrl() {
		return commentUrl;
	}

	public void setCommentUrl(String commentUrl) {
		this.commentUrl = commentUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(String commentNum) {
		this.commentNum = commentNum;
	}

	public String getReadNum() {
		return readNum;
	}

	public void setReadNum(String readNum) {
		this.readNum = readNum;
	}

}
