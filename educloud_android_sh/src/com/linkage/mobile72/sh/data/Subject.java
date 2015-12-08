package com.linkage.mobile72.sh.data;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

public class Subject implements Serializable {
	private static final long serialVersionUID = 3422084029198485154L;

	@DatabaseField
	private long id;
	@DatabaseField
	private String name;
	private boolean isChecked;

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

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean checked) {
		this.isChecked = checked;
	}
}