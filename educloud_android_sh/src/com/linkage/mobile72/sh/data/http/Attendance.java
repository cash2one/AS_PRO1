package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Attendance implements Serializable {

	private static final long serialVersionUID = 1L;

	private String today;
	private int displayKqCount;//考勤总人数
	private int displayKqQj;//请假人数
	private int displayKqQq;//缺勤人数
	private String displayKqPhoto;//
	private boolean teacherHasCreateKq;
	private List<AttendanceState> kqList;
	
	public static class AttendanceState {
		//月考勤状态里的每日考勤状态列表里的对象
		private String kqDate;
		private int kqStatus;
		
		//日考勤状态
		private String name;
		private String phone;
		private String reason;
		private int state;
		
		public String getKqDate() {
			return kqDate;
		}
		public void setKqDate(String kqDate) {
			this.kqDate = kqDate;
		}
		public int getKqStatus() {
			return kqStatus;
		}
		public void setKqStatus(int kqStatus) {
			this.kqStatus = kqStatus;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getReason() {
			return reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}
		public int getState() {
			return state;
		}
		public void setState(int state) {
			this.state = state;
		}
		public static AttendanceState parseFromJson(JSONObject jsonObj, int type) {
			AttendanceState clazz = new AttendanceState();
			if(type == 1) {
				clazz.setKqDate(jsonObj.optString("kqDate"));
				clazz.setKqStatus(jsonObj.optInt("kqStatus"));
			}else {
				clazz.setName(jsonObj.optString("name"));
				clazz.setPhone(jsonObj.optString("phone"));
				clazz.setReason(jsonObj.optString("reason"));
				clazz.setState(jsonObj.optInt("state"));
			}
			return clazz;
		}
		
		public static List<AttendanceState> parseFromJson(JSONArray jsonArray, int type) {
			List<AttendanceState> clazzs = new ArrayList<AttendanceState>();
			if(jsonArray != null && jsonArray.length() > 0) {
				for(int i=0;i<jsonArray.length();i++) {
					AttendanceState clazz = parseFromJson(jsonArray.optJSONObject(i), type);
					if(clazz != null)clazzs.add(clazz);
				}
			}
			return clazzs;
		}
	}
	
	/**
	 * @param jsonObj
	 * @param type 1解析月考勤 2解析日考勤
	 * @return
	 */
	public static Attendance parseFromJson(JSONObject jsonObj, int type) {
		Attendance clazz = new Attendance();
		clazz.setDisplayKqCount(jsonObj.optInt("displayKqCount"));
		clazz.setDisplayKqPhoto(jsonObj.optString("displayKqPhoto"));
		clazz.setDisplayKqQj(jsonObj.optInt("displayKqQj"));
		clazz.setDisplayKqQq(jsonObj.optInt("displayKqQq"));
		clazz.setToday(jsonObj.optString("today"));
		clazz.setKqList(AttendanceState.parseFromJson(jsonObj.optJSONArray("kqList"), type));
		return clazz;
	}
	
	public String getToday() {
		return today;
	}

	public void setToday(String today) {
		this.today = today;
	}

	public boolean isTeacherHasCreateKq() {
		return teacherHasCreateKq;
	}

	public void setTeacherHasCreateKq(boolean teacherHasCreateKq) {
		this.teacherHasCreateKq = teacherHasCreateKq;
	}

	public int getDisplayKqCount() {
		return displayKqCount;
	}

	public void setDisplayKqCount(int displayKqCount) {
		this.displayKqCount = displayKqCount;
	}

	public int getDisplayKqQj() {
		return displayKqQj;
	}

	public void setDisplayKqQj(int displayKqQj) {
		this.displayKqQj = displayKqQj;
	}

	public int getDisplayKqQq() {
		return displayKqQq;
	}

	public void setDisplayKqQq(int displayKqQq) {
		this.displayKqQq = displayKqQq;
	}

	public String getDisplayKqPhoto() {
		return displayKqPhoto;
	}

	public void setDisplayKqPhoto(String displayKqPhoto) {
		this.displayKqPhoto = displayKqPhoto;
	}

	public List<AttendanceState> getKqList() {
		return kqList;
	}

	public void setKqList(List<AttendanceState> kqList) {
		this.kqList = kqList;
	}

}
