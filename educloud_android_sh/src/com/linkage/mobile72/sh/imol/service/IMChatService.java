package com.linkage.mobile72.sh.imol.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.JxHomeworkListActivity;
import com.linkage.mobile72.sh.activity.JxHomeworkListActivity2;
import com.linkage.mobile72.sh.activity.im.NewChatActivity;
import com.linkage.mobile72.sh.activity.manager.SocketReceiver;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.OLConfig;
import com.linkage.mobile72.sh.fragment.NewMessageFragment;
import com.linkage.mobile72.sh.http.WDJsonObjectForChatRequest;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.im.provider.Ws.MessageTable;
import com.linkage.mobile72.sh.im.provider.Ws.MessageType;
import com.linkage.mobile72.sh.im.provider.Ws.ThreadTable;
import com.linkage.mobile72.sh.imol.xmppmanager.XmppConnectionManager;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.lib.util.LogUtils;

/**
 * 1连接推送服务器，包括重新连接 2处理服务器推送内容 3通知其他界面更改
 * 
 * @author keqizwl
 * 
 */
public class IMChatService extends Service {
	
	private static final String TAG = IMChatService.class.getSimpleName();
	private static final String OL_LOGIN = "OLLogin";
	
	private int connectStatus = STATUS_CLOSE;
	private static final int STATUS_REJECT = -2;
	private static final int STATUS_CLOSE = -1;
	private static final int STATUS_INIT = 0;
	private static final int STATUS_AUTHENTICAL = 1;
	private static final int STATUS_READY_CONNECTE = 2;
	private static final int STATUS_XMPP_CONNECTING = 3;
	private static final int STATUS_XMPP_CONNECTED = 4;
	private static final int STATUS_IM_LOGIN = 5;
	private static final int HB_PERIOD = 30;

//	1：消息 2：群组创建 3：群组修改 4：群组删除  5：所有群组 6：任务 7：回执 8：离线消息 9：下线通知
	public static final int TYPE_MESSAGE = 1;
	public static final int TYPE_GROUP_CREATE = 2;
	public static final int TYPE_GROUP_MODIFY = 3;
	public static final int TYPE_GROUP_DELETE = 4;
	public static final int TYPE_ALL_GROUP = 5;
	public static final int TYPE_MISSION = 6;
	public static final int TYPE_RECEIPT = 7;
	public static final int TYPE_OFFLINE_MESSAGE = 8;
	public static final int TYPE_REJECT = 9;

	private SharedPreferences mSharedPre;
	private NotificationManager mNm;
	private Notification notification;
	private int notifyChatType;
	private long notifyUserId;

	private String userName;
	private String content = null;
	private long mBuddyId = -1;
	private int mChatType = ChatType.CHAT_TYPE_SINGLE;
	
	protected BaseApplication mApp;
	private HeartConnection heartConnection;
	private SocketReceiver mReceiver;
	private ScheduledExecutorService mPoolExecutor;
	
	private Runnable connectHolder = new Runnable() {

		@Override
		public void run() {
			if (connectStatus != STATUS_IM_LOGIN) {
				LogUtils.e("Service try to reconnect()");
				try {
					reconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				LogUtils.e("Service is connected");
			}
		}

	};
	
	private ConnectionListener mConnectionListener = new ConnectionListener() {
        @Override
        public void reconnectionSuccessful() {
        	connectStatus = STATUS_IM_LOGIN;
        	heartConnection = new HeartBreakerImpl();
			heartConnection.start();
        }
        
        @Override
        public void reconnectionFailed(Exception arg0) {
        	connectStatus = STATUS_XMPP_CONNECTING;
        }
        
        @Override
        public void reconnectingIn(int arg0)
        {
        	connectStatus = STATUS_XMPP_CONNECTING;
        }
        
        @Override
        public void connectionClosedOnError(Exception arg0)
        {
            LogUtils.e("connectionClosedOnError....");
            connectStatus = STATUS_XMPP_CONNECTING;
            if (heartConnection != null) {
            	heartConnection.shutDownHeartBreaker();
            	heartConnection = null;
			}
        }
        
        @Override
        public void connectionClosed()
        {
        	connectStatus = STATUS_CLOSE;
        	if (heartConnection != null) {
            	heartConnection.shutDownHeartBreaker();
            	heartConnection = null;
			}
        }
    };
    
    private ChatManagerListener pListeners = new ChatManagerListener() {
		
		public void chatCreated(Chat chat, boolean arg1) {
			chat.addMessageListener(new MessageListener() {
				public void processMessage(Chat arg0,
						final org.jivesoftware.smack.packet.Message msg) {
					// 消息内容
					String body = msg.getBody();
					Log.e("lf", "chatCreated, body:" + body);
					
					int type = 0;
					JSONObject jsonObject = null;
					
					try
                    {
                        jsonObject = new JSONObject(body);
                        
                        type = Integer.parseInt(jsonObject.getString("position"));
                        
                        Log.d("lf", "chatCreated sucess, type:" + type);
                        
                        android.os.Message message = new android.os.Message();
                        message.what = type;
                        message.obj = body;
                        mHandler1.sendMessage(message);
                    }
                    catch (JSONException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
					catch (NumberFormatException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
					
				}
			});
		}
	};

	private Handler mHandler1 = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
		    
		    Log.d("lf", "mHandler1 msg, what:" + msg.what
                    + " obj:" + msg.obj);
		    
			switch (msg.what) {
			case TYPE_MESSAGE:
				LogUtils.e(msg.obj.toString());
				String ts = msg.obj.toString();
				// 判断是在线还是离线消息，1为在线消息，6为在线群推,8为离线消息
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(ts);
					//解析在线消息
					getOnLineMessageResult(jsonObject);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
				
			case TYPE_REJECT:
                Log.d("lf", "socketservice, STATUS_REJECT.....");
                Intent intent = new Intent();
                intent.setAction(Consts.BROADCAST_ACTION_CONNECT);
                intent.putExtra(Consts.BROADCAST_ACTTYPE_CONNECT, Consts.BROADCAST_REJECT);
                sendBroadcast(intent);
                onReject();
				break;
			}

			super.handleMessage(msg);
		}

	};
	
	private PacketListener heartBreakListener = new PacketListener() {
		@Override
		public void processPacket(Packet arg0) {
			Presence presence = (Presence) arg0;
			if ("ping".equals(presence.getStatus())) {
				if (heartConnection != null) {
					heartConnection.onReceiveTimeOutCallBack();
				}
			}
			// 增加的out消息接收到后，发送退出的广播
		}
	};
	
	@Override
	public void onCreate() {
		try {
			super.onCreate();
			mApp = BaseApplication.getInstance();

			mSharedPre = getSharedPreferences("sp_is_send_message",
					Context.MODE_PRIVATE);

			// heartConnection = new HeartConnection(this,
			// ChmobileApplication.mUser.id);

			mNm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			notification = new Notification(R.drawable.ic_launcher, "",
					System.currentTimeMillis());
			notification.flags = Notification.FLAG_AUTO_CANCEL;// 自动清除通知栏
			// notification.flags = Notification.FLAG_NO_CLEAR;;// 点击不清除
			
			mReceiver = new SocketReceiver();
            IntentFilter intentFilter= new IntentFilter(Consts.BROADCAST_ACTION_CONNECT);
            registerReceiver(mReceiver, intentFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		if (null != mReceiver) {
		    unregisterReceiver(mReceiver);
		}
		XmppConnectionManager.getInstance().disconnect();
		if (mPoolExecutor != null) {
			mPoolExecutor.shutdown();
		}
		super.onDestroy();
	}
	
	@Override  
	public IBinder onBind(Intent intent) {  
		return new MsgBinder();  
	} 
	
	public void startIMService() {
		connectStatus = STATUS_INIT;
		if (mPoolExecutor != null) {
			mPoolExecutor.shutdown();
		}
		mPoolExecutor = Executors
				.newSingleThreadScheduledExecutor();
        mPoolExecutor.scheduleAtFixedRate(connectHolder, 1, HB_PERIOD,
				TimeUnit.SECONDS);
	}
	
	public void stopIMService() {
		XmppConnectionManager.getInstance().disconnect();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
		BaseApplication.getInstance().cancelPendingRequests(OL_LOGIN);
		connectStatus = STATUS_CLOSE;
		if (mPoolExecutor != null) {
			mPoolExecutor.shutdown();
			mPoolExecutor = null;
		}
		if (heartConnection != null) {
			heartConnection.shutDownHeartBreaker();
		}
	}
	
	public void setActiveBuddy(long currentId, int chatType) {
		if (notifyUserId == currentId && notifyChatType == chatType) {
			mNm.cancelAll();
		}
		mBuddyId = currentId;
		mChatType = chatType;
	}
	
	public boolean isActiveBuddy(long currentId, int chatType) {
		return currentId == mBuddyId && chatType == mChatType;
	}
	
	private void reconnect() {
		switch (connectStatus) {
		case STATUS_INIT:
			LogUtils.e("STATUS_INIT");
			loginOL();
			break;
		case STATUS_AUTHENTICAL:
			LogUtils.e("STATUS_AUTHENTICAL");
			break;
		case STATUS_READY_CONNECTE:
			LogUtils.e("STATUS_READY_CONNECTE");
			doXMPPLogin();
			break;
		case STATUS_XMPP_CONNECTING:
			LogUtils.e("STATUS_XMPP_CONNECTING");
			break;
		case STATUS_XMPP_CONNECTED:
			LogUtils.e("STATUS_XMPP_CONNECTED");
			OLConfig olConfig = BaseApplication.getInstance().getOlConfig();
			if (olConfig == null) {
				connectStatus = STATUS_INIT;
				return;
			}
			String username = olConfig.ol_userName;
			String password = olConfig.ol_userPassword;
			new LoginXMPPTask(username, password).execute("");
			break;
		case STATUS_IM_LOGIN:
			LogUtils.e("STATUS_IM_LOGIN");
			break;
		case STATUS_CLOSE:
			LogUtils.e("STATUS_CLOSE");
		case STATUS_REJECT:
			LogUtils.e("STATUS_REJECT");
			if (heartConnection != null) {
				heartConnection.shutDownHeartBreaker();
				heartConnection = null;
			}
			if (mPoolExecutor != null) {
				mPoolExecutor.shutdown();
			}
			break;
		default:
			break;
		}
	}
	
	private void loginOL() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "olLogin");
		params.put("act", "login");
		params.put("uid", BaseApplication.getInstance().getDefaultAccount().getUserId()+"");
		//params.put("uid","600000120");
		params.put("ostype", "android");
		params.put("osversion", android.os.Build.VERSION.RELEASE);
		params.put("devicemodel", android.os.Build.MODEL);
		params.put("deviceno", android.os.Build.DEVICE);
		params.put("appversion", Utils.getVersion(IMChatService.this));
		params.put("role", "2131");

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtils.d("response=" + response);
				if (response.optInt("ret") == 0) {
					//帐号更新等
					BaseApplication.getInstance().setOlConfig(OLConfig.parseFromJson(response));
					connectStatus = STATUS_READY_CONNECTE;
					LogUtils.e("http若雅登陆成功返回!");
					doXMPPLogin();
				} else {
					connectStatus = STATUS_INIT;
					StatusUtils.handleMsg(response, IMChatService.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				StatusUtils.handleError(arg0, IMChatService.this);
				connectStatus = STATUS_INIT;
			}
		});
		connectStatus = STATUS_AUTHENTICAL;
		BaseApplication.getInstance().addToRequestQueue(mRequest, OL_LOGIN);
	}

	private void doXMPPLogin() {
		OLConfig olConfig = BaseApplication.getInstance().getOlConfig();
		if (olConfig == null || StringUtils.isEmpty(olConfig.ol_ip)) {
			connectStatus = STATUS_INIT;
			return;
		}
		String ip = olConfig.ol_pushserver_ip;
		//int port = Integer.parseInt(String.valueOf(olConfig.ol_pushserver_port));
		String host_name = olConfig.ol_hostname;
		if(StringUtils.isEmpty(ip) || StringUtils.isEmpty(host_name)) {
			return;
		}
		XmppConnectionManager.getInstance().init();
		String username = olConfig.ol_userName;
		String password = olConfig.ol_userPassword;
		try {
			XMPPConnection connection = XmppConnectionManager.getInstance()
					.getConnection();
			Collection<ChatManagerListener> collections = connection
					.getChatManager().getChatListeners();
			
			// 保持一个接收消息接口
			if (collections != null && !collections.isEmpty()) {
				for (ChatManagerListener listener : collections) {
					connection.getChatManager()
							.removeChatListener(listener);// 删除
				}
			}
			
			// 重新添加接收消息接口
			connection.getChatManager().addChatListener(pListeners);
			// 添加心跳
			connection.addPacketListener(heartBreakListener, new PacketTypeFilter(Presence.class));
			connection.addConnectionListener(mConnectionListener);

			new LoginXMPPTask(username, password).execute("");
		} catch (final Exception e1) {
			e1.printStackTrace();
//			if (e1 instanceof XMPPException) {
//				XMPPException xe = (XMPPException) e1;
//				final XMPPError error = xe.getXMPPError();
//				int errorCode = 0;
//				if (error != null) {
//					errorCode = error.getCode();
//
//				}
//				if (errorCode == 401) {
//					return Consts.LOGIN_ERROR_ACCOUNT_PASS;// 账号或密码错误
//				} else if (errorCode == 403) {
//					return Consts.LOGIN_ERROR_ACCOUNT_PASS;
//				} else {
//					return Consts.SERVER_UNAVAILABLE;// 无法连接到服务器
//				}
//			}
		}
	}

	private boolean isNotifyEnable() {
		return mSharedPre.getBoolean("is_send_message", true);
	}

	private void onReject() {
		LogUtils.e("OnReject");
		XmppConnectionManager.getInstance().disconnect();
		connectStatus = STATUS_REJECT;
		if (mPoolExecutor != null) {
			mPoolExecutor.shutdown();
			mPoolExecutor = null;
		}
		if (heartConnection != null) {
			heartConnection.shutDownHeartBreaker();
		}
	}

	/*
	 * 获取应用配置信息
	 */
//	private String getApp() {
//		OLConfig olConfig = BaseApplication.getInstance().getOlConfig();
//		String ip;
//		Long port;
//		if (olConfig != null) {
//			ip = olConfig.ol_pushserver_ip;
//			port = olConfig.ol_pushserver_port;
//			return ip + ":" + port;
//		} else {
//			return "112.4.28.127:80";
//		}
//
//	}

	/*
	 * 发送请求（离线和回执）到OL服务器
	 */
	private void sendRequestWithHttpClient(String msgid,String chatid,String chattype,String fromuserid) {
		try{
			OLConfig olConfig = mApp.getOlConfig();
			// 开启线程来发起网络请求
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("act", "MsgResp");
			params.put("uid", mApp.mCurAccount.getUserId()+"");
			params.put("token", olConfig.ol_token);
			params.put("lastid", msgid);
			params.put("chatid", chatid);
			params.put("chattype", chattype);
			params.put("fromuserid", fromuserid);
	
			WDJsonObjectForChatRequest mRequest = new WDJsonObjectForChatRequest("http://"+olConfig.ol_ip+":"+olConfig.ol_port+"/im.do", Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					LogUtils.e("response=" + response);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					StatusUtils.handleError(arg0, IMChatService.this);
				}
			});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 获取在线消息解析结果
	 */
	private void getOnLineMessageResult(JSONObject result) {
		if(result != null){
			try{
			    
				if (result.getString("chattype").equalsIgnoreCase("0")) {
					// 在线单聊
					saveMessage(result);
					// 通知栏提示
					if (isNotifyEnable()) {
						JSONObject imMessage = result.getJSONObject("ImMessage");
						if (mChatType != result.getInt("chattype") || mBuddyId != Long.parseLong(imMessage.getString("fromUserId"))) {
							showNotification(result,false);
						}
					}
					
					// 消息回执
					sendRequestWithHttpClient(result.getString("messageId"),result.getString("chatid"),result.getString("chattype"),result.getString("fromid"));
				}else if (result.getString("chattype").equalsIgnoreCase("1")) {
					// 在线群聊
					saveMessage(result);
					// 通知栏提示
					if (isNotifyEnable()) {
						JSONObject imMessage = result.getJSONObject("ImMessage");
						if (mChatType != result.getInt("chattype") || mBuddyId != Long.parseLong(imMessage.getString("chatid"))) {
							showNotification(result,false);
						}
					}
	
					// 消息回执
					sendRequestWithHttpClient(result.getString("messageId"),result.getString("chatid"),result.getString("chattype"),result.getString("fromid"));

				}else if (result.getString("chattype").equalsIgnoreCase("2")) {
					// 在线群推
					// 刷新监听
					if(mApp.lisenter != null){
						mApp.lisenter.updatePushMessage(result.getJSONObject("ImMessage")
								.getString("sendContents"));
					}
					//小红点显示
					
					// 响铃（通知栏提示暂时没有）
					if (isNotifyEnable()) {
						//showPushNotification(result,false);
						Notification nt = new Notification(); 
				        nt.defaults = Notification.DEFAULT_SOUND; 
				        mNm.notify(Consts.CHAT_NOTIFICATION_ID, nt);
					}
					// 消息回执
					sendRequestWithHttpClient(result.getString("messageId"),result.getString("chatid"),result.getString("chattype"),result.getString("fromid"));
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	/*
	 * 获取离线消息解析结果
	 */
	private void getOffLineMessageResult() {
		try{
			OLConfig olConfig = mApp.getOlConfig();
			
			// 开启线程来发起网络请求
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("act", "OffMsg");
			params.put("uid",mApp.mCurAccount.getUserId()+"");
			params.put("token", olConfig.ol_token);
	
			WDJsonObjectForChatRequest mRequest = new WDJsonObjectForChatRequest("http://"+olConfig.ol_ip+":"+olConfig.ol_port+"/im.do", Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					LogUtils.e("response=" + response);
					try{
						JSONArray result = response.getJSONArray("offlineList");
						if(result.length() > 0){
							for(int i = 0;i < result.length();i++){
								JSONObject message = result.getJSONObject(i);
								
								String chattype =  message.getJSONObject("message").getString("chattype");
								if(chattype.equals("0") || chattype.equals("1")) {//单聊群聊类聊天信息
									//拉取历史消息
									int unreadCount = message.getJSONObject("message").getInt("unreadcount");
									if (unreadCount > 0) {
										historyMessage(chattype,message.getJSONObject("message").getString("chatid") ,String.valueOf(unreadCount) ,message.getJSONObject("message").getString("lastmsgid"),message);
										
									}else{
										if(chattype.equals("0")||chattype.equals("1")){
		//										// 离线消息
											saveOutLineMessage(message);
											if (isNotifyEnable()) {
												JSONObject imMessage = message.getJSONObject("message");
												if (mChatType !=imMessage.getInt("chattype") || mBuddyId != Long.parseLong(imMessage.getString("fromuserid"))) {
													showNotification(message,true);
												}
											}
										}
										// 消息回执
										sendRequestWithHttpClient(message.getJSONObject("message").getString("lastmsgid"),message.getJSONObject("message").getString("chatid"),chattype,message.getJSONObject("message").getString("fromuserid"));
									}
								}else {//非单聊群聊类聊天信息
									//拉取历史消息
									int unreadCount = message.getJSONObject("message").getInt("unreadcount");
									if (unreadCount > 0) {
										historyMessage(chattype,message.getJSONObject("message").getString("chatid") ,String.valueOf(unreadCount) ,message.getJSONObject("message").getString("lastmsgid"),message);
									}
									//小红点显示
									SharedPreferences sp = BaseApplication.getInstance().getSp();
									Editor ed = sp.edit();
									if(message.getJSONObject("message").getString("chatid").equals("10001")) {
										ed.putInt("Huati", 1);
										if(NewMessageFragment.huatiImageView != null) {
											NewMessageFragment.huatiImageView.setVisibility(View.VISIBLE);
										}
									}else if(message.getJSONObject("message").getString("chatid").equals("10002")) {
										ed.putInt("Huodong", 1);
										if(NewMessageFragment.huodongImageView != null) {
											NewMessageFragment.huodongImageView.setVisibility(View.VISIBLE);
										}
									}
									ed.commit();
									// 响铃（通知栏提示暂时没有）
									if (isNotifyEnable()) {
										//showPushNotification(result,false);
										Notification nt = new Notification(); 
								        nt.defaults = Notification.DEFAULT_SOUND; 
								        mNm.notify(Consts.CHAT_NOTIFICATION_ID, nt);
									}
								}
							}
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					StatusUtils.handleError(arg0, IMChatService.this);
				}
			});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showPushNotification(JSONObject bean,boolean isOfficeLine) {
		String nickname = null;
		notification.when = System.currentTimeMillis();
		Intent i = new Intent();
		i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		
		try{
			if(!isOfficeLine){
				JSONObject imMessage = bean.getJSONObject("ImMessage");
				String content = "";
				if (bean.getString("chattype").equalsIgnoreCase("2")) {
					content = imMessage.getString("sendContents");
					//获得推送消息内容
					JSONObject pushObj = new JSONObject(content);
					userName = pushObj.getString("title");
//					JXBean jxbean = new JXBean();
//					jxbean.setId(pushObj.getLong("id"));
					if(pushObj.getString("type").equals("4")){
						//jxbean.setSmsMessageType("2");
						i.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
								JxHomeworkListActivity.SMSMESSAGETYPE_NOTICE);
					}else if(pushObj.getString("type").equals("3")){
						//jxbean.setSmsMessageType("14");
						i.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
								JxHomeworkListActivity.SMSMESSAGETYPE_HOMEWORK);
					}else if(pushObj.getString("type").equals("5")){
						//jxbean.setSmsMessageType("3");
						i.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
								JxHomeworkListActivity.SMSMESSAGETYPE_COMMENT);
					}else if(pushObj.getString("type").equals("7")){
						//jxbean.setSmsMessageType("3");
						i.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
								JxHomeworkListActivity.SMSMESSAGETYPE_TOUPIAO);
					}
					//i.putExtra("id", pushObj.getString("id"));
					//i.putExtra("jxbean", jxbean);
					i.setClass(this, JxHomeworkListActivity2.class);
					notification.tickerText = pushObj.getString("desc");
					content = pushObj.getString("desc");
					notifyChatType = -1;
					notifyUserId = -1;
				} else {
					nickname = imMessage.getString("fromUserName");
					notifyChatType = imMessage.getInt("chattype");
					String userIdString = imMessage.getString("fromUserId");
					if (notifyChatType == Consts.ChatType.CHAT_TYPE_SINGLE) {
						notifyUserId = Long.parseLong(userIdString);
					} else {
						notifyUserId = bean.getInt("chatid");
					}
					
					i.putExtra("name", nickname);
					userName = nickname + "的新消息";
					i.putExtra("buddy_id", userIdString);
					i.setClass(this, NewChatActivity.class);
					content = imMessage.getString("sendContents");
					System.out.print(content);
					notification.tickerText = content;
				}
		
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,
						PendingIntent.FLAG_UPDATE_CURRENT);
				notification.setLatestEventInfo(this, userName, content,
						contentIntent);
				notification.defaults |= Notification.DEFAULT_ALL;
				mNm.notify(Consts.CHAT_NOTIFICATION_ID, notification);
			}else{
				JSONObject imMessage = bean.getJSONObject("message");
				content = imMessage.getString("lastmsg");
				//获得推送消息内容
				JSONObject pushObj = new JSONObject(content);
				userName = pushObj.getString("title");
//				JXBean jxbean = new JXBean();
//				jxbean.setId(pushObj.getLong("id"));
				if(pushObj.getString("type").equals("4")){
					//jxbean.setSmsMessageType("2");
					i.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
							JxHomeworkListActivity.SMSMESSAGETYPE_NOTICE);
				}else if(pushObj.getString("type").equals("3")){
					//jxbean.setSmsMessageType("14");
					i.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
							JxHomeworkListActivity.SMSMESSAGETYPE_HOMEWORK);
				}else if(pushObj.getString("type").equals("5")){
					//jxbean.setSmsMessageType("3");
					i.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
							JxHomeworkListActivity.SMSMESSAGETYPE_COMMENT);
				}else if(pushObj.getString("type").equals("7")){
					//jxbean.setSmsMessageType("3");
					i.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
							JxHomeworkListActivity.SMSMESSAGETYPE_TOUPIAO);
				}
				
				if(!pushObj.getString("type").equals("")){
					//i.putExtra("id", pushObj.getString("id"));
					//i.putExtra("jxbean", jxbean);
					i.setClass(this, JxHomeworkListActivity2.class);
					notification.tickerText = pushObj.getString("desc");
					content = pushObj.getString("desc");
			
					PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,
							PendingIntent.FLAG_UPDATE_CURRENT);
					notification.tickerText = pushObj.getString("desc");
					notification.setLatestEventInfo(this, userName, content,
							contentIntent);
					notification.defaults |= Notification.DEFAULT_ALL;
					mNm.notify(Consts.CHAT_NOTIFICATION_ID, notification);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/*
	 * 发送消息广播
	 */
	private void showNotification(JSONObject bean, boolean isOfficeLine) {
		String nickname = null;
		notification.when = System.currentTimeMillis();
		Intent i = new Intent();
		i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		String content = "";
		try{
			//在线
			if(!isOfficeLine){
				JSONObject imMessage = bean.getJSONObject("ImMessage");
				
				nickname = imMessage.getString("fromUserName");
				userName = nickname + "的新消息";
				
				notifyChatType = bean.getInt("chattype");
				if (notifyChatType == Consts.ChatType.CHAT_TYPE_SINGLE) {
					notifyUserId = Long.parseLong(imMessage.getString("fromUserId"));
				} else {
					notifyUserId = Long.parseLong(bean.getString("chatid"));
				}
				
				
				if(notifyChatType == 0){
					i = NewChatActivity.getIntent(IMChatService.this, Long.parseLong(imMessage.getString("fromUserId")),
							imMessage.getString("fromUserName"), bean.getInt("chattype"),0);
				}else if(notifyChatType == 1){
					JSONObject imGroup = bean.getJSONObject("ImGroup");
					i = NewChatActivity.getIntent(IMChatService.this, Long.parseLong(bean.getString("chatid")),
							imGroup.getString("groupName"), bean.getInt("chattype"),0);
				}
		
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,
						PendingIntent.FLAG_UPDATE_CURRENT);
				if(imMessage.getString("type").equalsIgnoreCase("1")){
					content = imMessage.getString("sendContents");
				}else if(imMessage.getString("type").equalsIgnoreCase("2")){
					content = "您有一条新的图片消息";
				}else if(imMessage.getString("type").equalsIgnoreCase("3")){
					content = "您有一条新的语音消息";
				}else{
					content = imMessage.getString("sendContents");
				}
				System.out.print(content);
				notification.tickerText = nickname + "：" + content;
				notification.setLatestEventInfo(this, userName, content,
						contentIntent);
				notification.defaults |= Notification.DEFAULT_ALL;
				mNm.notify(Consts.CHAT_NOTIFICATION_ID, notification);
			}else{
				JSONObject imMessage = bean.getJSONObject("message");
				
				notifyChatType = imMessage.getInt("chattype");
				if (notifyChatType == Consts.ChatType.CHAT_TYPE_SINGLE) {
					notifyUserId = Long.parseLong(imMessage.getString("fromuserid"));
				} else {
					notifyUserId = Long.parseLong(imMessage.getString("chatid"));
				}
				
				String fromUserName = imMessage.getString("fromusername");
				
				if(notifyChatType == 0){
					i = NewChatActivity.getIntent(IMChatService.this, Long.parseLong(imMessage.getString("fromuserid")),
							fromUserName, imMessage.getInt("chattype"),0);
				}else if(notifyChatType == 1){
					i = NewChatActivity.getIntent(IMChatService.this, Long.parseLong(imMessage.getString("chatid")),
							imMessage.getString("chatName"), notifyChatType, 0);
				}
		
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,
						PendingIntent.FLAG_UPDATE_CURRENT);
				if(imMessage.getString("lasttype").equalsIgnoreCase("1")){
					content = imMessage.getString("lastmsg");
				}else if(imMessage.getString("lasttype").equalsIgnoreCase("2")){
					content = "您有一条新的图片消息";
				}else if(imMessage.getString("lasttype").equalsIgnoreCase("3")){
					content = "您有一条新的语音消息";
				}else{
					content = imMessage.getString("lastmsg");
				}
				
				userName = fromUserName + "的新消息";
				System.out.print(content);
				notification.tickerText = fromUserName + "：" + content;
				notification.setLatestEventInfo(this, userName, content,
						contentIntent);
				notification.defaults |= Notification.DEFAULT_ALL;
				mNm.notify(Consts.CHAT_NOTIFICATION_ID, notification);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void savePushMessage(JSONObject message) {
		try{
			ContentValues cv = new ContentValues();
			cv.put(MessageTable.BUDDY_ID, message.getString("fromid"));
//			cv.put(MessageTable.BUDDY_NAME, message.getString("fromname"));
//			cv.put(MessageTable.BUDDY_ID,  mApp.mCurAccount.getUserId());
//			cv.put(MessageTable.BUDDY_NAME,  mApp.mCurAccount.getUserName());
			cv.put(MessageTable.SENDER_ID, message.getString("fromid"));
			cv.put(MessageTable.BODY, message.getJSONObject("ImMessage").getString("sendContents"));
			cv.put(MessageTable.IS_INBOUND, MessageType.INCOMING);
			
			if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("1")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_TEXT);
			}else if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("2")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_PIC);
			}else if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("3")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_AUDIO);
			}else if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("4")){
//				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_VIDEO);
			}
			cv.put(MessageTable.RECEIVED_TIME, Utils.defaultFormat(null));
			cv.put(MessageTable.SENT_TIME, message.getJSONObject("ImMessage").getString("sendTime"));
			cv.put(MessageTable.ACCOUNT_NAME, mApp.mCurAccount.getLoginname());
			cv.put(MessageTable.CHAT_TYPE, message.getString("chattype"));
			cv.put(MessageTable.OUTBOUND_STATUS,
					mApp.chatServiceReady() ? MessageType.MSG_STATUS_WAITING
							: MessageType.MSG_STATUS_ERROR);
	
			getContentResolver().insert(ThreadTable.CONTENT_URI, cv);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/*
	 * 保存在线消息记录
	 */
	private void saveMessage(JSONObject message) {
		try{
			ContentValues cv = new ContentValues();
			if(message.getString("chattype").equalsIgnoreCase("0")){
				cv.put(MessageTable.BUDDY_ID, message.getString("fromid"));
//				cv.put(MessageTable.BUDDY_NAME, message.getString("fromname"));
			}else{
				cv.put(MessageTable.BUDDY_ID, message.getString("chatid"));
//				cv.put(MessageTable.BUDDY_NAME, message.getJSONObject("ImGroup").getString("groupName"));
			}
//			cv.put(MessageTable.BUDDY_ID,  mApp.mCurAccount.getUserId());
//			cv.put(MessageTable.BUDDY_NAME,  mApp.mCurAccount.getUserName());
			cv.put(MessageTable.SENDER_ID, message.getString("fromid"));
			cv.put(MessageTable.BODY, message.getJSONObject("ImMessage").getString("sendContents"));
			cv.put(MessageTable.IS_INBOUND, MessageType.INCOMING);
			
			if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("1")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_TEXT);
			}else if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("2")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_PIC);
			}else if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("3")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_AUDIO);
			}else if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("4")){
//				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_VIDEO);
			}
			
			cv.put(MessageTable.RECEIVED_TIME, Utils.defaultFormat(null));
			//cv.put(MessageTable.SENT_TIME, message.getJSONObject("ImMessage").getString("sendTime"));
			cv.put(MessageTable.SENT_TIME, message.optString("createTime"));
			cv.put(MessageTable.ACCOUNT_NAME, mApp.mCurAccount.getLoginname());
			cv.put(MessageTable.CHAT_TYPE, message.getString("chattype"));
			cv.put(MessageTable.OUTBOUND_STATUS,
					mApp.chatServiceReady() ? MessageType.MSG_STATUS_WAITING
							: MessageType.MSG_STATUS_ERROR);
	
			getContentResolver().insert(MessageTable.CONTENT_URI, cv);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/*
	 * 保存离线消息记录
	 */
	private void saveOutLineMessage(JSONObject outlinemessage){
		try{
			JSONObject message = outlinemessage.getJSONObject("message");
			ContentValues cv = new ContentValues();
			
			if(message.getString("chattype").equalsIgnoreCase("0")){
				cv.put(MessageTable.BUDDY_ID, message.getString("fromuserid"));
//				cv.put(MessageTable.BUDDY_NAME, message.getString("fromusername"));
			}else{
				cv.put(MessageTable.BUDDY_ID, message.getString("chatid"));
//				cv.put(MessageTable.BUDDY_NAME, message.getString("chatName"));
			}
			
			cv.put(MessageTable.SENDER_ID, message.getString("fromuserid"));
			cv.put(MessageTable.BODY, message.getString("lastmsg"));
			cv.put(MessageTable.IS_INBOUND, MessageType.INCOMING);
			
			if(message.getString("lasttype").equalsIgnoreCase("1")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_TEXT);
			}else if(message.getString("lasttype").equalsIgnoreCase("2")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_PIC);
			}else if(message.getString("lasttype").equalsIgnoreCase("3")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_AUDIO);
			}else if(message.getString("lasttype").equalsIgnoreCase("4")){
//				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_VIDEO);
			}
			
			cv.put(MessageTable.RECEIVED_TIME, Utils.defaultFormat(null));
			cv.put(MessageTable.SENT_TIME, message.getString("lasttime"));
			cv.put(MessageTable.ACCOUNT_NAME, mApp.mCurAccount.getLoginname());
			cv.put(MessageTable.CHAT_TYPE, message.getString("chattype"));
			cv.put(MessageTable.OUTBOUND_STATUS,
					mApp.chatServiceReady() ? MessageType.MSG_STATUS_WAITING
							: MessageType.MSG_STATUS_ERROR);
	
			getContentResolver().insert(MessageTable.CONTENT_URI, cv);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/*
	 * 保存历史记录消息
	 */
	private void saveHistoryMessage(JSONObject message) {
		try{
			ContentValues cv = new ContentValues();
			if(message.getString("chattype").equalsIgnoreCase("0")){
				JSONObject imMessage = message.getJSONObject("ImMessage");
				cv.put(MessageTable.BUDDY_ID, imMessage.getString("fromUserId"));
//				cv.put(MessageTable.BUDDY_NAME, imMessage.getString("fromUserName"));
			}else{
				cv.put(MessageTable.BUDDY_ID, message.getString("chatid"));
//				cv.put(MessageTable.BUDDY_NAME, message.getJSONObject("ImGroup").getString("groupName"));
			}
			
			cv.put(MessageTable.SENDER_ID, message.getString("fromid"));
			cv.put(MessageTable.BODY, message.getJSONObject("ImMessage").getString("sendContents"));
			cv.put(MessageTable.IS_INBOUND, MessageType.INCOMING);
			
			if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("1")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_TEXT);
			}else if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("2")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_PIC);
			}else if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("3")){
				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_AUDIO);
			}else if(message.getJSONObject("ImMessage").getString("type").equalsIgnoreCase("4")){
//				cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_VIDEO);
			}
			cv.put(MessageTable.RECEIVED_TIME, Utils.defaultFormat(null));
			//cv.put(MessageTable.SENT_TIME, message.getJSONObject("ImMessage").getString("sendTime"));
			cv.put(MessageTable.SENT_TIME, message.optString("createTime"));
			cv.put(MessageTable.ACCOUNT_NAME, mApp.mCurAccount.getLoginname());
			cv.put(MessageTable.CHAT_TYPE, message.getString("chattype"));
			cv.put(MessageTable.OUTBOUND_STATUS,
					mApp.chatServiceReady() ? MessageType.MSG_STATUS_WAITING
							: MessageType.MSG_STATUS_ERROR);
	
			getContentResolver().insert(MessageTable.CONTENT_URI, cv);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * 解析非聊天消息文本
	 */
//	public static PushMessageContentWrapper getPushMessageContentWrapper(
//			String contentWrapper) {
//		String str = null;
//		String content = null;
//		if (contentWrapper.contains("\n")) {// 换行符
//			str = contentWrapper.replaceAll("\n", "<br/>");
//		}
//		if (str != null && str.contains(" ")) {// 空格
//			str = str.replaceAll(" ", "&nbsp;");
//		} else if (str == null && contentWrapper.contains(" ")) {
//			str = contentWrapper.replaceAll(" ", "&nbsp;");
//		}
//		try {
//
//			if (str != null) {
//				content = Html.fromHtml(str).toString();// 解析文本
//			} else {
//				content = Html.fromHtml(contentWrapper).toString();// 解析文本
//			}
//			PushMessageContentWrapper tmp = Mobile72Client.gson.fromJson(
//					content, PushMessageContentWrapper.class);
//			if (tmp.content == null) {
//				return null;
//			}
//			return tmp;
//		} catch (Exception e) {
//			PushMessageContentWrapper wrapper = new PushMessageContentWrapper();
//			wrapper.content = Html.fromHtml(contentWrapper).toString();
//			wrapper.name = null;
//			return wrapper;
//		}
//	}
	
	/*
	 * 获取历史消息记录
	 */
	private void historyMessage(String chatType,String chatId,final String count,String msgid,final JSONObject lastMessage){
		try{
			OLConfig olConfig = mApp.getOlConfig();
			// 开启线程来发起网络请求
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("act", "historyMsg");
			params.put("token", olConfig.ol_token);
			params.put("userid",mApp.mCurAccount.getUserId()+"");
			params.put("chattype",chatType);
			params.put("chatid",chatId);
			params.put("frommsgid",msgid);
			params.put("count",(Integer.parseInt(count))+"");
	
			WDJsonObjectForChatRequest mRequest = new WDJsonObjectForChatRequest("http://"+olConfig.ol_ip+":"+olConfig.ol_port+"/im.do", Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					LogUtils.e("response=" + response);
					
					try{
						JSONArray result = response.getJSONArray("historyList");
						
						if(result.length() > 0){
							
							for(int i = result.length() - 1;i >= 0;i--){
								JSONObject message = result.getJSONObject(i).getJSONObject("message");
								if (message.getString("chattype").equalsIgnoreCase("0")) {
									// 在线单聊
									saveHistoryMessage(message);
					
									// 消息回执
//									sendRequestWithHttpClient(message.getString("messageId"));
									sendRequestWithHttpClient(message.getString("messageId"),message.getString("chatid"),message.getString("chattype"),message.getString("fromid"));

								}else if (message.getString("chattype").equalsIgnoreCase("1")) {
									// 在线群聊
									saveHistoryMessage(message);
					
									// 消息回执
									sendRequestWithHttpClient(message.getString("messageId"),message.getString("chatid"),message.getString("chattype"),message.getString("fromid"));

								}else if (message.getString("chattype").equalsIgnoreCase("2")) {
									if(mApp.lisenter != null){
										mApp.lisenter.updatePushMessage(message.getJSONObject("message")
												.getString("lastmsg"));
									}
									// 消息回执
									sendRequestWithHttpClient(message.getString("messageId"),message.getString("chatid"),message.getString("chattype"),message.getString("fromid"));

								}
							}
						}
						if(!lastMessage.getJSONObject("message").getString("chattype").equals("2")) {
							// 离线消息
							saveOutLineMessage(lastMessage);
							if (isNotifyEnable()) {
								JSONObject imMessage = lastMessage.getJSONObject("message");
								if (mChatType !=imMessage.getInt("chattype") || mBuddyId != Long.parseLong(imMessage.getString("fromuserid"))) {
									showNotification(lastMessage,true);
								}
							}
						}
						sendRequestWithHttpClient(lastMessage.getJSONObject("message").getString("lastmsgid"),lastMessage.getJSONObject("message").getString("chatid"),lastMessage.getJSONObject("message").getString("chattype"),lastMessage.getJSONObject("message").getString("fromuserid"));

					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					StatusUtils.handleError(arg0, IMChatService.this);
				}
			});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class MsgBinder extends Binder {  
		/** 
		* 获取当前Service的实例 
		* @return 
		*/  
		public IMChatService getService() { 
			return IMChatService.this;  
		}  
	}  
	
	private class HeartBreakerImpl extends HeartConnection {

		@Override
		public void onHeartTimeOut() {
			LogUtils.e("onHeartTimeOut");
			BaseApplication.getInstance().cancelPendingRequests(TAG);
			XmppConnectionManager.getInstance().disconnect();
			connectStatus = STATUS_READY_CONNECTE;
		}
	}
	
	class LoginXMPPTask extends AsyncTask<String, Integer, Boolean> {
		String username;
		String password;
		
		public LoginXMPPTask(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			XMPPConnection connection = XmppConnectionManager.getInstance()
					.getConnection();
			connectStatus = STATUS_READY_CONNECTE;
			if (!connection.isConnected()) {
//				try {
////					connection.connect();
//				} catch (XMPPException e) {
//					e.printStackTrace();
//				}
			}
			if (connection.isConnected()) {
				if(connectStatus == STATUS_IM_LOGIN) {
					return true;
				}
				connectStatus = STATUS_XMPP_CONNECTED;
				return XmppConnectionManager.getInstance().login(username, password);
			} else {
				return false;
			}
			
		}
		
		@Override
		protected void onPostExecute(Boolean loginSuccess) {
			super.onPostExecute(loginSuccess);
			if (loginSuccess) {
				getOffLineMessageResult();
				connectStatus = STATUS_IM_LOGIN;
				onLoginXMPPSuccessful();
			} else {
				connectStatus = STATUS_XMPP_CONNECTED;
			}
		}
	}
	
	private void onLoginXMPPSuccessful() {
		heartConnection = new HeartBreakerImpl();
		heartConnection.start();
	}
	
}
