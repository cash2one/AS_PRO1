package com.linkage.mobile72.sh.im.service;

import com.linkage.mobile72.sh.im.IUploadFileListener;

interface IChatService{
	
	void login(String token,String name,long id);
	
	void logout();
	
   	void send(String message);
   	void sendPicFile(long toId, String filePath,int chattype,long id,String groupName);
	void sendVoiceFile(long toId, String filePath,int chattype,int time,long id,String groupName);
   	void setActiveBuddyId(long buddyId,int chattype);
   
   	void registerUploadListener(IUploadFileListener listener);
   	void unregisterUploadListener(IUploadFileListener listener);
   	
   	boolean ready();
   	
   	boolean getNotifyStatus(long userId, String key);
   	void setNotifyStatus(long userId, String key, boolean opened);
}