package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class PaymentTypeBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long typeId;
	private String typeName;
	private String picture;
	private String description;
	
	public static PaymentTypeBean parseFromJson(JSONObject jsonObj) {
		PaymentTypeBean pt = new PaymentTypeBean();
		pt.setTypeId(jsonObj.optLong("typeId"));
		pt.setTypeName(jsonObj.optString("typeName"));
		pt.setPicture(jsonObj.optString("picture"));
		pt.setDescription(jsonObj.optString("description"));
		return pt;
	}
	
	public static List<PaymentTypeBean> parseFromJson(JSONArray jsonArray) {
		List<PaymentTypeBean> pts = new ArrayList<PaymentTypeBean>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				PaymentTypeBean pt = parseFromJson(jsonArray.optJSONObject(i));
				if(pt != null)pts.add(pt);
			}
		}
		return pts;
	}
	
	public long getTypeId() {
		return typeId;
	}
	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
