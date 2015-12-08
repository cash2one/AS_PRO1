package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProjectData implements Serializable{

	private static final long serialVersionUID = -2463179383864664899L;
	
	private long id;
	
	private String name;
	
	private String scale;
	
	private int type;
	
	private String unit;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public static ProjectData parseFromJson(JSONObject jsonObj) {
		ProjectData project = new ProjectData();
		project.setId(jsonObj.optLong("id"));
		project.setName(jsonObj.optString("name"));
		project.setScale(jsonObj.optString("scale"));
		project.setType(jsonObj.optInt("type"));
		project.setUnit(jsonObj.optString("unit"));
		return project;
	}
	
	public static ArrayList<ProjectData> parseFromJson(JSONArray jsonArray) {
		ArrayList<ProjectData> projects = new ArrayList<ProjectData>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				ProjectData project = parseFromJson(jsonArray.optJSONObject(i));
				if(project != null)projects.add(project);
			}
		}
		return projects;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
