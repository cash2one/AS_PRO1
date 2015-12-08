package com.linkage.mobile72.sh.chat;

import info.emm.messenger.IMClient;
import info.emm.messenger.MQ;
import info.emm.messenger.MQ.VYMessage;
import info.emm.messenger.NotificationCenter;
import info.emm.messenger.VYEventListener;

import javax.sql.DataSource;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.ChatActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.lib.util.LogUtils;

public class MessageListener implements VYEventListener {
	private static MessageListener messageCatcher;
	public static Context context;
	private DataSource mDataSource;
	protected BaseApplication mApp; 
	
	private long fromId, groupId;

	public static MessageListener getInstance() {
		MessageListener localmessageCatcher = messageCatcher;
		if (localmessageCatcher == null) {
			synchronized (IMClient.class) {
				localmessageCatcher = messageCatcher;
				if (localmessageCatcher == null) {
					messageCatcher = localmessageCatcher = new MessageListener();
				}
			}
		}
		return localmessageCatcher;
	}

	public void init(Context context) {
		this.context = context;
		 IMClient.getInstance().registerEventListener(this);
	}

	@SuppressWarnings("static-access")
	@Override
	public void onMessageArrival(VYMessage MSG) {

		Log.e("MessageListener",
				"chat----->onMessageArrival, id=" + MSG.getMsgId() + " from="
						+ MSG.getFrom() 
						+ " to="+ MSG.getTo() + " chattype=" + MSG.getChatType()
						+ " type=" + MSG.getType() + " status="
						+ MSG.getStatus() + " direct=" + MSG.getDirect()
						+ " attachfilename=" + MSG.getAttachFileName()
						+ " body=" + MSG.getBody());
		Log.e("MessageListener", "Thread id=" + Thread.currentThread().getId());

		NotificationCenter.getInstance().postNotificationName(
				NotificationCenter.MessageArrivalCode, MSG);

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		Log.e("MessageListener", "--------->currentPackageName"
				+ currentPackageName);
		String myPackName = "com.linkage.mobile72.sx";
		if (!TextUtils.isEmpty(currentPackageName)
				&& currentPackageName.equals(myPackName)) {
			// 消息分发？
			Log.e("MessageListener", "--------->1");

		} else {
			if ( (1 == BaseApplication.getInstance().getInChat() 
					&& (MSG.getChatType() == MQ.VYMessage.ChatType.CHATTYPE_SINGLE)
					&& (BaseApplication.getInstance().getImcomeChatId().equals(MSG.getFrom())))
					|| ( 2 == BaseApplication.getInstance().getInChat() 
							&& (MSG.getChatType() == MQ.VYMessage.ChatType.CHATTYPE_GROUP))) {
				Log.e("MessageListener", "----->no need to notify!!!");
				return;
			}
			Log.e("MessageListener", "--------->2");
			// 定义NotificationManager
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			// 定义通知栏展现的内容信息
			int icon = R.drawable.ic_launcher;
			CharSequence tickerText = context.getResources().getString(R.string.app_name);
			long when = System.currentTimeMillis();
			Notification notification = new Notification(icon, tickerText, when);

			// 定义下拉通知栏时要展现的内容信息
			Context context = this.context;
			CharSequence contentTitle = "";
			String gpName = "";
			String sgName = "";
			if (MSG.getChatType() == MQ.VYMessage.ChatType.CHATTYPE_GROUP) {
				gpName = getContactName(MSG.getFrom(),
						MSG.getTo(), MSG.getChatType());
				//sgName = getSgContactName(fromId);
				
				contentTitle = gpName + " 有新消息";
				
				if (StringUtils.isEmpty(gpName)) {
					Log.e("MessageListener", "--------->2-1");
					return;
				}
				
			} else {
				sgName = getContactName(MSG.getFrom(),
						MSG.getTo(), MSG.getChatType());
				contentTitle = sgName + " 发来消息";
				
				if (StringUtils.isEmpty(sgName)) {
					Log.e("MessageListener", "--------->2-2");
					return;
				}
			}
			
			LogUtils.e("chat---->msg listener, tilte=" + contentTitle);
			
			CharSequence contentText = null;
			
			switch (MSG.getType()) {
			case TEXT:
				contentText = ((MQ.textMessageBody) MSG.getBody()).getMessage();
				break;
			case IMAGE:
				contentText = "图片";
				break;
			case DOCUMENT:
				contentText = "文件";
				break;
			case VOICE:
				contentText = "语音";
				break;
			}
			Intent notificationIntent = new Intent(context, ChatActivity.class);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			Bundle bundle = new Bundle();
			//bundle.putInt("type", 1);
			if (MQ.VYMessage.ChatType.CHATTYPE_GROUP == MSG.getChatType()) {
				bundle.putInt("chattype", ChatType.CHAT_TYPE_GROUP);
				bundle.putString("chatid", MSG.getTo());
				//bundle.putString("chatid", groupId + "");
				//bundle.putString("name", gpName);
			} else {
				bundle.putString("chatid", MSG.getFrom() + "");
				//bundle.putString("chatid", fromId + "");
				bundle.putInt("chattype", ChatType.CHAT_TYPE_SINGLE);
				//bundle.putString("name", sgName);
			}
			notificationIntent.putExtra("data", bundle);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);

			// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
			mNotificationManager.notify(1, notification);
			Log.e("MessageListener", "--------->3");
		}

		// Log.e("MessageListener", "before post--->");
		// NotificationCenter.getInstance().postNotificationName(
		// MessageArrivalCode, MSG);
	}
	
	private String getContactName(String from, String to, MQ.VYMessage.ChatType type) {
		String strName = "";
		if (mApp == null) {
			mApp = BaseApplication.getInstance();
		}
//		if (mDataSource == null) {
//			mDataSource = mApp.getDataSource();
//		}
//		
//		
//		if (null == mDataSource || StringUtils.isEmpty(from)) {
//			LogUtils.e("chat----->msglistener mDataSource or from is null!! mDataSource=" + mDataSource + " from=" + from);
//			return strName;
//		}
		
		String head = Consts.APP_ID;
		String targetId = from;
		long chatIdLg;
		
		if (type == MQ.VYMessage.ChatType.CHATTYPE_GROUP) {
			head = Consts.APP_ID0;
			targetId = to;
		} else {
			head = Consts.APP_ID;
			targetId = from;
		}
		
		if (targetId.length() <= head.length()) {
			LogUtils.e("chat-----> msglistener invalid chatId! targetId=" + targetId);
		} else {
			try {
				Long id = Long.parseLong(targetId.substring(head.length(), targetId.length()));
				chatIdLg = id.longValue();
				LogUtils.e("chat----->msglistener chatIdLg="+ chatIdLg);
				
				if (type == MQ.VYMessage.ChatType.CHATTYPE_GROUP) {
					groupId =chatIdLg;
					
//					ClazzWorkContactGroup group = mDataSource.getContactGroupById(
//							mApp.getDefaultAccount().getLoginname(), chatIdLg);
					
//					if (group != null) {
//						strName = group.group_name;
//					}
					
				} else {
					fromId = chatIdLg;
					
//					ClazzWorkContact contact = mDataSource.getContactById(mApp.getDefaultAccount()
//							.getLoginname(), chatIdLg);
//					
//					if (contact != null) {
//						strName = contact.getname();
//					}
					
					strName = getSgContactName(chatIdLg);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		LogUtils.e("chat----->msglistener get contact name, contact name=" + strName);
		
		return strName;
	}
	
	private String getSgContactName(long Id) {
//		ClazzWorkContact contact = mDataSource.getContactById(mApp.getDefaultAccount()
//				.getLoginname(), Id);
//		
//		if (null == contact) {
			return "";
//		} else {
//			return contact.getname();
//		}
	}

	@Override
	public void onSendMessageResponse(VYMessage MSG) {
		NotificationCenter.getInstance().postNotificationName(
				NotificationCenter.SendMessageResponseCode, MSG);
		
		Log.e("MessageListener",
				"onSendMessageResponse, id=" + MSG.getMsgId() + " from="
						+ MSG.getFrom() + " to=" + MSG.getTo() + " chattype="
						+ MSG.getChatType() + " type=" + MSG.getType()
						+ " status=" + MSG.getStatus() + " direct="
						+ MSG.getDirect() + " attachfilename="
						+ MSG.getAttachFileName() + " body=" + MSG.getBody());

		Log.e("onSendMessageResponse", "Thread id="
				+ Thread.currentThread().getId());

	}
}
