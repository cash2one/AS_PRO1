package com.linkage.mobile72.sh.chat;


import info.emm.messenger.IMClient;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.linkage.mobile72.sh.chat.ConnectionListener;
import com.linkage.mobile72.sh.chat.MessageListener;
import com.linkage.mobile72.sh.app.BaseApplication;

public class InitReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.i("chat", "onReceive, get message ----------------------------@@@");

		// Bef0629
		// IMClient.getInstance().init(arg0);
		// IMClient.getInstance().setHttpServerAddress("120.55.144.110");
		// IMClient.getInstance().registerEventListener(new MessageListener());

		try {
			Context appContext = BaseApplication.getInstance()
					.getApplicationContext();
			IMClient.getInstance().init(appContext, "120.55.138.134", 8443,
					"120.55.144.110");
			IMClient.getInstance().addConnectionListener(
					new ConnectionListener(arg0));
			MessageListener.getInstance().init(appContext);
			IMClient.getInstance().registerEventListener(
					MessageListener.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
