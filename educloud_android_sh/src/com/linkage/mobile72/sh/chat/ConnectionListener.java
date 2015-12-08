package com.linkage.mobile72.sh.chat;

import info.emm.messenger.VYConnectionListener;
import android.content.Context;
import android.content.Intent;

import com.linkage.mobile72.sh.activity.LoginActivity;
import com.linkage.mobile72.sh.app.ActivityList;
import com.linkage.lib.util.LogUtils;

public class ConnectionListener implements VYConnectionListener {
	private Context _context= null;
	private boolean isFirstKick = true;
	
	public ConnectionListener(Context context){
		this._context = context;
	}
	@Override
	public void onConnected(int result) {
		LogUtils.e("chat-------->ConnectionListener onConnected resulton=" + result);
	}

	@Override
	public void onDisconnected(int result) {
		LogUtils.e("chat-------->ConnectionListener onDisconnected _result="+result);
if(result == 2 && isFirstKick){
			
			isFirstKick = false;
			ActivityList.finshAllActivity();
			
			
			Intent intent = new Intent();
			intent.setClass(_context, LoginActivity.class);
			intent.putExtra("logout", 1);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			_context.startActivity(intent);
			
			
		}

	}

}
