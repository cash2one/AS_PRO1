package com.linkage.mobile72.sh.data;

public class AttenPicState {
	
	private boolean isTakeAttenPhoto = false;
	
	private String localPath = null;
	
	private String atPicUrl = null;

	public boolean isTakeAttenPhoto() {
		return isTakeAttenPhoto;
	}

	public void setTakeAttenPhoto(boolean isTakeAttenPhoto) {
		this.isTakeAttenPhoto = isTakeAttenPhoto;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getAtPicUrl() {
		return atPicUrl;
	}

	public void setAtPicUrl(String atPicUrl) {
		this.atPicUrl = atPicUrl;
	}
	
}
