package com.linkage.mobile72.sh.im.bean;

public class LoginAction extends Action {
	
	public LoginAction(String accessToken) {
		put(KEY_ACTION, VALUE_LOGIN);
		put("token", accessToken);
		put("from", "android");
	}
	
}
