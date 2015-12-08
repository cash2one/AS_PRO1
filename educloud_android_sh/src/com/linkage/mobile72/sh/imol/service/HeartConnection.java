package com.linkage.mobile72.sh.imol.service;

import org.jivesoftware.smack.packet.Presence;

import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.OLConfig;
import com.linkage.mobile72.sh.imol.xmppmanager.XmppConnectionManager;
import com.linkage.lib.util.LogUtils;

public abstract class HeartConnection extends Thread {
	private String TAG = "HeartConnection";

	private boolean isOnReceiving = false;
	private boolean runningSwitch = false;

	public HeartConnection() {
		setDaemon(true);
	}
	
	//延迟1秒，每隔60秒执行一次心跳连接，即自己给自己发送一条信息
	@Override
	public final void run() {
		runningSwitch = true;
		while (runningSwitch) {
			try {
				if (XmppConnectionManager.getInstance().getConnection() != null) {
					isOnReceiving = true;
					pingConnected("heart");
				} else {
					runningSwitch = false;
					onHeartTimeOut();
					return;
				}
				Thread.sleep(5000);
				if (runningSwitch) {
					if (isOnReceiving) {
						runningSwitch = false;
						onHeartTimeOut();
					} else {
						Thread.sleep(15000);
					}
				}
			} catch (Exception e) {
				runningSwitch = false;
				onHeartTimeOut();
			}
		}
	}
	
	public void onReceiveTimeOutCallBack() {
		LogUtils.e("收到心跳包");
		if (runningSwitch = false) {
			LogUtils.e("心跳包超时或无效");
		}
		isOnReceiving = false;
	}
	
	public void shutDownHeartBreaker() {
		runningSwitch = false;
	}

	/**
	 * 监测是否连接在openfire上
	 * 
	 * @param ping
	 * @throws Exception
	 */
	private void pingConnected(String ping) {
		//putPing(ping, false);
		OLConfig olConfig = BaseApplication.getInstance().getOlConfig();
		LogUtils.d("发心跳包");
		Presence p = new Presence(Presence.Type.available);
		p.setStatus("ping");
		p.setProperty("ping", ping);
		p.setTo(olConfig.ol_userName
				+ "@"
				+ XmppConnectionManager.getInstance().getConnection()
						.getServiceName());
		XmppConnectionManager.getInstance().getConnection().sendPacket(p);
	}
	
	public abstract void onHeartTimeOut();
}
