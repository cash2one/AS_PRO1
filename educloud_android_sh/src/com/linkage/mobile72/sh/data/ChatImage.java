package com.linkage.mobile72.sh.data;

import java.io.Serializable;

public class ChatImage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5913736610466718982L;
	private Long id;
	private String type;
	private String body;

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
