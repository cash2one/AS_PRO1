package com.linkage.mobile72.sh.data;

import java.io.Serializable;

public class OffLineContent implements Serializable {
	private static final long serialVersionUID = 1L;
	public String content;
	public String name;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
