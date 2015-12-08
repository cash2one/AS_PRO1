package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class NotifyReply implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private String picture;
	private String phone;
	private Boolean state;

	private int webflag;//web发送状态 1--成功 0--失败
 
	private int smsflag;// Int 短信发送状态 1--成功 0--失败 
	private int phoneflag;//   Int 客户端发送状态 1--成功 0--失败 

	
	public int getWebflag()
    {
        return webflag;
    }

    public void setWebflag(int webflag)
    {
        this.webflag = webflag;
    }

    public int getSmsflag()
    {
        return smsflag;
    }

    public void setSmsflag(int smsflag)
    {
        this.smsflag = smsflag;
    }

    public int getPhoneflag()
    {
        return phoneflag;
    }

    public void setPhoneflag(int phoneflag)
    {
        this.phoneflag = phoneflag;
    }

    public static NotifyReply parseFromJson(JSONObject jsonObj) {
		NotifyReply n = new NotifyReply();
		n.setId(jsonObj.optLong("userId"));
		n.setName(jsonObj.optString("userName"));
		n.setPhone(jsonObj.optString("phone"));
		n.setPicture(jsonObj.optString("picture"));
		n.setState(jsonObj.optInt("flag") == 1);
		n.setSmsflag(jsonObj.optInt("smsflag"));
		n.setWebflag(jsonObj.optInt("webflag"));
		n.setPhoneflag(jsonObj.optInt("phoneflag"));
		return n;
	}
	
	public static List<NotifyReply> parseFromJson(JSONArray jsonArray) {
		List<NotifyReply> n = new ArrayList<NotifyReply>();
		if(jsonArray != null && jsonArray.length() > 0) {
			for(int i=0;i<jsonArray.length();i++) {
				NotifyReply nn = parseFromJson(jsonArray.optJSONObject(i));
				if(nn != null)n.add(nn);
			}
		}
		return n;
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
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Boolean getState() {
		return state;
	}
	public void setState(Boolean state) {
		this.state = state;
	}

}
