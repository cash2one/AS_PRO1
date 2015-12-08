package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppBean implements Serializable {

	public static final int COLLECT_APP = 4;
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long appId;
	private String appName;
	private String appLogo;
	private int appType;//1是原生APP，2是HTML的APP
	private int appAuth;//是否需要授权，0：不需要，1：需要
	private String appUrl;
	private String appVersion;
	private String appIntroduce;
	// private List<String> appCapture;
	private String appLauncherPath;
	private String appLauncherUrl;
	private String cpName;
	private String openid;
	
	private String appDesc;
	private String price_type;
	private String appPrice_me;
	private String appPrice;
	private String appDownNum;
	
	private int inapp;//应用里收费是1，应用外收费是0
	private String inapp_notice;//应用里收费说明
	private int installed;// 0未安装 1已安装
	
	private int sourceId;//标识应用来源，4的集体应用，其他为自有应用

	public static AppBean parseFromJson(JSONObject jsonObj) {
		AppBean app = new AppBean();
		app.setId(jsonObj.optLong("id"));
		app.setAppId(jsonObj.optLong("appId"));
		app.setAppName(jsonObj.optString("appName"));
		app.setAppLogo(jsonObj.optString("appLogo"));
//		app.setAppLogo("http://www.atool.org/res/atool-org-weixin.jpg");
		app.setAppType(jsonObj.optInt("appType"));
		app.setAppAuth(jsonObj.optInt("appAuth"));
		app.setAppUrl(jsonObj.optString("appUrl"));
		app.setAppVersion(jsonObj.optString("appVersion"));
		app.setAppIntroduce(jsonObj.optString("appIntroduce"));
		app.setAppDesc(jsonObj.optString("appDesc"));
		app.setPrice_type(jsonObj.optString("price_type"));
		app.setAppPrice(jsonObj.optString("appPrice"));
		app.setAppPrice_me(jsonObj.optString("appPrice_me"));
		app.setAppDownNum(jsonObj.optString("appDownNum"));
		app.setInapp(jsonObj.optInt("inapp"));
		app.setInapp_notice(jsonObj.optString("inapp_notice"));
		List<String> cap = new ArrayList<String>();
		if (jsonObj.has("appCapture")) {
			try {
				JSONArray array = jsonObj.getJSONArray("appCapture");
				if (array != null && array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						cap.add(array.getString(i));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// app.setAppCapture(cap);
		}
		app.setAppLauncherPath(jsonObj.optString("appLauncherPath"));
		app.setAppLauncherUrl(jsonObj.optString("appLauncherUrl"));
		app.setCpName(jsonObj.optString("cpName"));
		app.setAppToken(jsonObj.optString("openid"));
		app.setSourceId(jsonObj.optInt("sourceId"));
		return app;
	}

	public static List<AppBean> parseFromJson(JSONArray jsonArray) {
		List<AppBean> apps = new ArrayList<AppBean>();
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				AppBean app = parseFromJson(jsonArray.optJSONObject(i));
				if (app != null)
					apps.add(app);
			}
		}
		return apps;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppLogo() {
		return appLogo;
	}

	public void setAppLogo(String appLogo) {
		this.appLogo = appLogo;
	}

	public int getAppType() {
		return appType;
	}

	public void setAppType(int appType) {
		this.appType = appType;
	}

	public int getAppAuth() {
		return appAuth;
	}

	public void setAppAuth(int appAuth) {
		this.appAuth = appAuth;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getAppIntroduce() {
		return appIntroduce;
	}

	public void setAppIntroduce(String appIntroduce) {
		this.appIntroduce = appIntroduce;
	}

	public String getCpName() {
		return cpName;
	}

	public void setCpName(String cpName) {
		this.cpName = cpName;
	}

	public String getAppToken() {
		return openid;
	}

	public void setAppToken(String appToken) {
		this.openid = appToken;
	}

	// public List<String> getAppCapture() {
	// return appCapture;
	// }
	//
	// public void setAppCapture(List<String> appCapture) {
	// this.appCapture = appCapture;
	// }

	public String getAppLauncherPath() {
		return appLauncherPath;
	}

	public void setAppLauncherPath(String appLauncherPath) {
		this.appLauncherPath = appLauncherPath;
	}

	public String getAppLauncherUrl() {
		return appLauncherUrl;
	}

	public void setAppLauncherUrl(String appLauncherUrl) {
		this.appLauncherUrl = appLauncherUrl;
	}

	public int getInstalled() {
		return installed;
	}

	public void setInstalled(int installed) {
		this.installed = installed;
	}

	public String getAppDesc() {
		return appDesc;
	}

	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc;
	}

	public String getPrice_type() {
		return price_type;
	}

	public void setPrice_type(String price_type) {
		this.price_type = price_type;
	}

	public String getAppPrice_me() {
		return appPrice_me;
	}

	public void setAppPrice_me(String appPrice_me) {
		this.appPrice_me = appPrice_me;
	}

	public String getAppPrice() {
		return appPrice;
	}

	public void setAppPrice(String appPrice) {
		this.appPrice = appPrice;
	}

	public String getAppDownNum() {
		return appDownNum;
	}

	public void setAppDownNum(String appDownNum) {
		this.appDownNum = appDownNum;
	}

	public int getInapp() {
		return inapp;
	}

	public void setInapp(int inapp) {
		this.inapp = inapp;
	}

	public String getInapp_notice() {
		return inapp_notice;
	}

	public void setInapp_notice(String inapp_notice) {
		this.inapp_notice = inapp_notice;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
}
