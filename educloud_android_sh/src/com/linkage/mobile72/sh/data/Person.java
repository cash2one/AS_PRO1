package com.linkage.mobile72.sh.data;

import com.j256.ormlite.field.DatabaseField;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class Person implements Serializable {

	private static final long serialVersionUID = 100123L;

    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private int auditStatus;
    @DatabaseField
	private String accountID;
    @DatabaseField
	private String sex;// 1男 2女
    @DatabaseField
	private String email;
    @DatabaseField
	private String name;
    @DatabaseField
	private long schoolId;// 学校ID
    @DatabaseField
	private String school;// 学校
    @DatabaseField
	private int gradeType;// 年级ID
    @DatabaseField
	private String grade;// 年级
    @DatabaseField
	private int schoolType;// 学制ID
    @DatabaseField
	private String eductionalystme;// 学制
    @DatabaseField
	private String usertype;// 0 家长 1教师
    @DatabaseField
	private String useravatar;
    @DatabaseField
	private String brith;
    @DatabaseField
    private String phone;
    private boolean isChecked;

    private int ruf_auditStatus;//0不是好友 1已是好友 2待审核
    private List<AccountChild> studentList;
    
    public void toggle() {
        this.isChecked = !this.isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getAccountID() {
		return accountID;
	}

	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(long schoolId) {
		this.schoolId = schoolId;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public int getGradeType() {
		return gradeType;
	}

	public void setGradeType(int gradeType) {
		this.gradeType = gradeType;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public int getSchoolType() {
		return schoolType;
	}

	public void setSchoolType(int schoolType) {
		this.schoolType = schoolType;
	}

	public String getEductionalystme() {
		return eductionalystme;
	}

	public void setEductionalystme(String eductionalystme) {
		this.eductionalystme = eductionalystme;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getUseravatar() {
		return useravatar;
	}

	public void setUseravatar(String useravatar) {
		this.useravatar = useravatar;
	}

	public String getBrith() {
		return brith;
	}

	public void setBrith(String brith) {
		this.brith = brith;
	}

	public int getRuf_auditStatus() {
		return ruf_auditStatus;
	}

	public void setRuf_auditStatus(int ruf_auditStatus) {
		this.ruf_auditStatus = ruf_auditStatus;
	}

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<AccountChild> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<AccountChild> studentList) {
		this.studentList = studentList;
	}

	public static Person parseFromJson(JSONObject jsonObj) {
		Person person = new Person();
		if(jsonObj.optString("accountID") == null || jsonObj.optString("accountID").equals("null")) {
			person.setAccountID("");
		}else {
			person.setAccountID(jsonObj.optString("accountID"));
		}
		person.setAuditStatus(jsonObj.optInt("auditStatus"));
		if(jsonObj.optString("brith") == null || jsonObj.optString("brith").equals("null")) {
			person.setBrith("");
		}else {
			person.setBrith(jsonObj.optString("brith"));
		}
		person.setSchoolType(jsonObj.optInt("schoolType"));
		person.setGradeType(jsonObj.optInt("gradeType"));
		person.setSchoolId(jsonObj.optLong("schoolId"));
		if(jsonObj.optString("eductionalystme") == null || jsonObj.optString("eductionalystme").equals("null")) {
			person.setEductionalystme("");
		}else {
			person.setEductionalystme(jsonObj.optString("eductionalystme"));
		}
		if(jsonObj.optString("email") == null || jsonObj.optString("email").equals("null")) {
			person.setEmail("");
		}else {
			person.setEmail(jsonObj.optString("email"));
		}
		if(jsonObj.optString("grade") == null || jsonObj.optString("grade").equals("null")) {
			person.setGrade("");
		}else {
			person.setGrade(jsonObj.optString("grade"));
		}
		if(jsonObj.optString("name") == null || jsonObj.optString("name").equals("null")) {
			person.setName("");
		}else {
			person.setName(jsonObj.optString("name"));
		}
		if(jsonObj.optString("school") == null || jsonObj.optString("school").equals("null")) {
			person.setSchool("");
		}else {
			person.setSchool(jsonObj.optString("school"));
		}
		if(jsonObj.optString("sex") == null || jsonObj.optString("sex").equals("null")) {
			person.setSex("");
		}else {
			person.setSex(jsonObj.optString("sex"));
		}
		if(jsonObj.optString("useravatar") == null || jsonObj.optString("useravatar").equals("null")) {
			person.setUseravatar("");
		}else {
			person.setUseravatar(jsonObj.optString("useravatar"));
		}
		if(jsonObj.optString("phone") == null || jsonObj.optString("phone").equals("null")) {
			person.setPhone("");
		}else {
			person.setPhone(jsonObj.optString("phone"));
		}
		person.setUsertype(jsonObj.optString("usertype"));
		person.setRuf_auditStatus(jsonObj.optInt("ruf_auditStatus" ,1));
		person.setStudentList(AccountChild.pareFromJsonForPersonInfo(jsonObj.optJSONArray("students")));
		return person;
	}

}
