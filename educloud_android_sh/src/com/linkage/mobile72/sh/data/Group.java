package com.linkage.mobile72.sh.data;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable{

	private static final long serialVersionUID = 1L;

    @DatabaseField
	private long id;
    @DatabaseField
	private String name;
//  @DatabaseField(canBeNull = true, foreign = true, columnName = "person_id")
    private List<Person> persons;
    private boolean isChecked;
    
    //以下两个字段是办公短信联系人增加的
    private int type;//分组类型，0：学校，1：群组
    private int count;//分组成员数量

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
    
}
