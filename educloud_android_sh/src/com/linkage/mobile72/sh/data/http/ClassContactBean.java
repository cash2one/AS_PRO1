package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author wl
 * 班级通讯录接口返回数据结构 getClassroomContacts
 */
public class ClassContactBean implements Serializable 
{
    private static final long serialVersionUID = 1L;

    private String className;
    private String classLevel;
    private Integer classNumber;
    private String avatar;
    private Long classroomId;
    private long taskid;
    
   

    private List<ClassMemberBean> memberInfoList;
    
    
    public static ClassContactBean parseFromJson(JSONObject jsonObj) {
        ClassContactBean clazz = new ClassContactBean();
        clazz.setClassName(jsonObj.optString("className"));
        clazz.setClassLevel(jsonObj.optString("classLevel"));
        clazz.setClassNumber(jsonObj.optInt("classNumber"));
        clazz.setAvatar(jsonObj.optString("avatar"));
        clazz.setClassroomId(jsonObj.optLong("classroomId"));
        clazz.setTaskid(jsonObj.optLong("taskid"));
        
        clazz.setMemberInfoList(ClassMemberBean.parseFromJson(jsonObj.optJSONArray("memberInfoList")));
        return clazz;
    }
    
    public static List<ClassContactBean> parseFromJson(JSONArray jsonArray) {
        List<ClassContactBean> clazzs = new ArrayList<ClassContactBean>();
        if(jsonArray != null && jsonArray.length() > 0) {
            for(int i=0;i<jsonArray.length();i++) {
                ClassContactBean clazz = parseFromJson(jsonArray.optJSONObject(i));
                if(clazz != null)clazzs.add(clazz);
            }
        }
        return clazzs;
    }
    
    

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getClassLevel()
    {
        return classLevel;
    }

    public void setClassLevel(String classLevel)
    {
        this.classLevel = classLevel;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public Integer getClassNumber()
    {
        return classNumber;
    }

    public void setClassNumber(Integer classNumber)
    {
        this.classNumber = classNumber;
    }

    public Long getClassroomId()
    {
        return classroomId;
    }

    public void setClassroomId(Long classroomId)
    {
        this.classroomId = classroomId;
    }
    
    public long getTaskid()
    {
        return taskid;
    }

    public void setTaskid(long taskid)
    {
        this.taskid = taskid;
    }

    public List<ClassMemberBean> getMemberInfoList()
    {
        return memberInfoList;
    }

    public void setMemberInfoList(List<ClassMemberBean> memberInfoList)
    {
        this.memberInfoList = memberInfoList;
    }
}
