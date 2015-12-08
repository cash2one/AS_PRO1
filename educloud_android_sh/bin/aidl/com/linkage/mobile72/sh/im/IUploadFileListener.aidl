package com.linkage.mobile72.sh.im;

interface IUploadFileListener {
	
	void onProgressUpdate(long id);
	
	void onUploadError(long id);
	
	void onUploadSuccess(long id);
}