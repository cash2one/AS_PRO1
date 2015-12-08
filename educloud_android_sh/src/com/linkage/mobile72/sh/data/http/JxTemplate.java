package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JxTemplate implements Serializable {

	
	private static final long serialVersionUID = 100000000011L;
	
	
	private long id;
	private String title;
	private String text;
	
	public static JxTemplate parseFromJson(JSONObject jsonObj) {
		JxTemplate template = new JxTemplate();
		template.setId(jsonObj.optLong("id"));
		template.setTitle(jsonObj.optString("title"));
		template.setText(jsonObj.optString("text"));
		return template;
	}
	
	public static List<JxTemplate> parseFromJson(JSONArray jsonArray) {
		List<JxTemplate> templates = new ArrayList<JxTemplate>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				JxTemplate template = parseFromJson(jsonArray.optJSONObject(i));
				if(template != null)templates.add(template);
			}
		}
		return templates;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}
