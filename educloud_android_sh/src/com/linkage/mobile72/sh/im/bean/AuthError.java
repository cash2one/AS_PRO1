package com.linkage.mobile72.sh.im.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthError extends MessageIn {
	
	private String erroMessage;
	private int errorCode;
	
	public static AuthError fromJson(String str) throws JSONException	 {
		JSONObject json = new JSONObject(str);
		AuthError error = new AuthError();
		error.erroMessage = json.optString("msg");
		error.errorCode = json.optInt("error_code");
		error.setType(ERROR);
		return error;
	}

	public String getErroMessage() {
		return erroMessage;
	}

	public void setErroMessage(String erroMessage) {
		this.erroMessage = erroMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
}
