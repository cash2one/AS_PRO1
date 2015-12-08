package com.linkage.mobile72.sh.http;

public class ParamItem {
	public final static int TYPE_TEXT = 1;
	public final static int TYPE_FILE = 2;

	private String key;
	private String value;
	private int type;

	public ParamItem(String key, Object value, int type) {
		this.key = key;
		this.value = String.valueOf(value);
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("key:").append(key).append("\n");
		sb.append("value:").append(value).append("\n");
		sb.append("type:").append(type).append("\n");
		return sb.toString();
	}
}