package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class DistanceRunData implements Serializable{

	private static final long serialVersionUID = 9035934224057512885L;

	private long range;
	
	private String sortName;

	public long getRange() {
		return range;
	}

	public void setRange(long range) {
		this.range = range;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	
	public static DistanceRunData parseFromJson(JSONObject jsonObj) {
		DistanceRunData sort = new DistanceRunData();
		sort.setRange(jsonObj.optLong("range"));
		sort.setSortName(jsonObj.optString("name"));
		return sort;
	}
	
	public static ArrayList<DistanceRunData> parseFromJson(JSONArray jsonArray) {
		ArrayList<DistanceRunData> sorts = new ArrayList<DistanceRunData>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				DistanceRunData sort = parseFromJson(jsonArray.optJSONObject(i));
				if(sort != null)sorts.add(sort);
			}
		}
		return sorts;
	}
}
