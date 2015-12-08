package com.linkage.mobile72.sh.data;

import java.util.List;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;

public class AccountData {

	@DatabaseField(id = true)
	String loginname;
	@DatabaseField
	String loginpwd;
	@DatabaseField
	String userName;
	@DatabaseField
	long userId;
	@DatabaseField
	String token;
	@DatabaseField(defaultValue = "0")
	int defaultUser;
	@DatabaseField(defaultValue = "0")
	int lastLoginUser;
	@DatabaseField(canBeNull = true)
	String phone;
	@DatabaseField(defaultValue = "0")
	int rememberPassword;
	@DatabaseField(canBeNull = true, defaultValue = "0")
	int userType;// 用户角色1:教师 2：学生 3：家长
	@DatabaseField
	String avatar;
	@DatabaseField(defaultValue = "1")
	int origin;// 用户来源：1.个人中心 2.校讯通
	@DatabaseField
	String replyName;// 家长回复家校互动时使用名称，例如：某某学生的家长
	@DatabaseField(defaultValue = "0")
	long creditScore;// 积分数
	@DatabaseField
	String creditScoreEndtime;// 积分有效期
	@DatabaseField(defaultValue = "0")
	int isSign;// 是否已经签到（1、已签到，0未签到）
	@DatabaseField
	int userLevel;//1：A级家长，2：B级家长，3：C级家长，4：A级教师，5：B级教师
	@DatabaseField
	long loginDate;
	@DatabaseField
	String loadingUrl;//loading页面跳转url
	@DatabaseField
	String loadingImg;//loading页面图片地址
	@DatabaseField
	String loadingTime;//loading页面自动跳转等待时间
	
	List<AccountChild> studentData;
	
	public String getLoadingUrl() {
		return loadingUrl;
	}

	public void setLoadingUrl(String loadingUrl) {
		this.loadingUrl = loadingUrl;
	}

	public String getLoadingImg() {
		return loadingImg;
	}

	public void setLoadingImg(String loadingImg) {
		this.loadingImg = loadingImg;
	}

	public String getLoadingTime() {
		return loadingTime;
	}

	public void setLoadingTime(String loadingTime) {
		this.loadingTime = loadingTime;
	}

	public int getLastLoginUser() {
		return lastLoginUser;
	}

	public void setLastLoginUser(int lastLoginUser) {
		this.lastLoginUser = lastLoginUser;
	}
	
	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getLoginpwd() {
		return loginpwd;
	}

	public void setLoginpwd(String loginpwd) {
		this.loginpwd = loginpwd;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getDefaultUser() {
		return defaultUser;
	}

	public void setDefaultUser(int defaultUser) {
		this.defaultUser = defaultUser;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getRememberPassword() {
		return rememberPassword;
	}

	public void setRememberPassword(int rememberPassword) {
		this.rememberPassword = rememberPassword;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getOrigin() {
		return origin;
	}

	public void setOrigin(int origin) {
		this.origin = origin;
	}

	public String getReplyName() {
		return replyName;
	}

	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}

	public long getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(long creditScore) {
		this.creditScore = creditScore;
	}

	public String getCreditScoreEndtime() {
		return creditScoreEndtime;
	}

	public void setCreditScoreEndtime(String creditScoreEndtime) {
		this.creditScoreEndtime = creditScoreEndtime;
	}

	public int getIsSign() {
		return isSign;
	}

	public void setIsSign(int isSign) {
		this.isSign = isSign;
	}

	public List<AccountChild> getStudentData() {
		return studentData;
	}

	public void setStudentData(List<AccountChild> studentData) {
		this.studentData = studentData;
	}

	public long getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(long loginDate) {
		this.loginDate = loginDate;
	}

	public int getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}

	public static AccountData parseFromJson(JSONObject jsonObj) {
		AccountData user = new AccountData();
		user.setUserId(jsonObj.optLong("userId"));
		user.setUserName(jsonObj.optString("userName"));
		user.setUserType(jsonObj.optInt("userType"));
		user.setAvatar(jsonObj.optString("avatar"));
		user.setToken(jsonObj.optString("token"));
		user.setOrigin(jsonObj.optInt("orgin"));
		user.setCreditScore(jsonObj.optLong("creditScore"));
		user.setIsSign(jsonObj.optInt("isSign"));
		user.setCreditScoreEndtime(jsonObj.optString("creditScoreEndtime"));
		user.setStudentData(AccountChild.parseFromJsonForLogin(jsonObj.optJSONArray("studentData")));
		user.setLoadingUrl(jsonObj.optString("loadingUrl"));
		user.setLoadingImg(jsonObj.optString("loadingImg2"));
		user.setLoadingTime(jsonObj.optString("loadingTime"));
		user.setUserLevel(jsonObj.optInt("userLevel"));
		return user;
	}
}
