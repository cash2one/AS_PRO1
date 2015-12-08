package com.linkage.mobile72.sh.im.receiver;

import com.linkage.mobile72.sh.im.service.ChatService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    //河北去掉im服务启动
//		context.startService(new Intent(context, ChatService.class));
	}
	
}
