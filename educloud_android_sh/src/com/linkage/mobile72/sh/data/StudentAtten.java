package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;

public class StudentAtten implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -179450899102222564L;

    public static final int ATTEN_NORMAL = 1;// 正常
    
    public static final int ATTEN_ASK_FOR_LEAVE = 2;// 请假
    
    public static final int ATTEN_LEAVE = 3;// 缺勤
    
    @DatabaseField(id = true)
    private long stuId;
    
    @DatabaseField
    private String name;
    
    @DatabaseField
    private int state;
    
    @DatabaseField
    private String parentPhone;
    
    /**
     * @return the stuId
     */
    public long getStuId()
    {
        return stuId;
    }
    
    /**
     * @param stuId the stuId to set
     */
    public void setStuId(long stuId)
    {
        this.stuId = stuId;
    }
    
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * @return the state
     */
    public int getState()
    {
        return state;
    }
    
    /**
     * @param state the state to set
     */
    public void setState(int state)
    {
        this.state = state;
    }
    
    /**
     * @return the parentPhone
     */
    public String getParentPhone()
    {
        return parentPhone;
    }
    
    /**
     * @param parentPhone the parentPhone to set
     */
    public void setParentPhone(String parentPhone)
    {
        this.parentPhone = parentPhone;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("id:").append(stuId).append("\n");
        sb.append("name:").append(name).append("\n");
        sb.append("state:").append(state).append("\n");
        sb.append("parentPhone:").append(parentPhone).append("\n");
        
        return sb.toString();
    }
    
    public String toStringSimple()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("id:").append(stuId).append("\n");
        sb.append("state:").append(state).append("\n");
        
        return sb.toString();
    }
    
    public static StudentAtten parseJson(JSONObject jsonObj)
            throws JSONException
    {
        StudentAtten stuAtten = new StudentAtten();
        
        stuAtten.setStuId(jsonObj.optLong("id"));
        stuAtten.setName(jsonObj.optString("name"));
        stuAtten.setState(jsonObj.optInt("state"));
        stuAtten.setParentPhone(jsonObj.optString("phone"));
        
        return stuAtten;
    }
    
    public static List<StudentAtten> parseFromJson(JSONArray jsonArray)
            throws JSONException
    {
        List<StudentAtten> stuAttenList = new ArrayList<StudentAtten>();
        if (jsonArray != null && jsonArray.length() > 0)
        {
            for (int i = 0; i < jsonArray.length(); i++)
            {
                StudentAtten clazz = parseJson(jsonArray.optJSONObject(i));
                if (clazz != null)
                    stuAttenList.add(clazz);
            }
        }
        return stuAttenList;
    }
}
