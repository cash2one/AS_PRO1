package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class ClazzScore implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private int typeId;
	private String typeName;
	private String date;
	private String max;
	private String min;
	private String average;
	
	public static ClazzScore parseFromJson(JSONObject jsonObj) {
		ClazzScore clazzResult = new ClazzScore();
		clazzResult.setId(jsonObj.optLong("id"));
		clazzResult.setName(jsonObj.optString("name"));
		clazzResult.setTypeId(jsonObj.optInt("type"));
		clazzResult.setTypeName(jsonObj.optString("typeName"));
		clazzResult.setDate(jsonObj.optString("date"));
		clazzResult.setMax(jsonObj.optString("max"));
		clazzResult.setMin(jsonObj.optString("min"));
		clazzResult.setAverage(jsonObj.optString("average"));
		return clazzResult;
	}
	
	public static List<ClazzScore> parseFromJson(JSONArray jsonArray) {
		List<ClazzScore> clazzResults = new ArrayList<ClazzScore>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				ClazzScore clazzResult = parseFromJson(jsonArray.optJSONObject(i));
				if(clazzResult != null)clazzResults.add(clazzResult);
			}
		}
		return clazzResults;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getAverage() {
		return average;
	}

	public void setAverage(String average) {
		this.average = average;
	}
	
}
