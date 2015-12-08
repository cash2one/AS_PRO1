package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.List;

public class Region implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private int parentId;
	private List<Region> childRegion;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public List<Region> getChildRegion() {
		return childRegion;
	}
	public void setChildRegion(List<Region> childRegion) {
		this.childRegion = childRegion;
	}
	
}
