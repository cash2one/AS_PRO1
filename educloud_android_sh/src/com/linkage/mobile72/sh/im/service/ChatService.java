package com.linkage.mobile72.sh.im.service;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.im.IUploadFileListener;
import com.linkage.mobile72.sh.im.service.IChatService;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.MyPaymentActivity;
import com.linkage.mobile72.sh.activity.im.NewChatActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.http.ParamItem;
import com.linkage.mobile72.sh.http.WDJsonObjectMultipartRequest;
import com.linkage.mobile72.sh.im.FileHelper;
import com.linkage.mobile72.sh.im.MyWebSocketClient;
import com.linkage.mobile72.sh.im.MyWebSocketClient.WebSocketClientListener;
import com.linkage.mobile72.sh.im.bean.AuthError;
import com.linkage.mobile72.sh.im.bean.LoginAction;
import com.linkage.mobile72.sh.im.bean.MessageIn;
import com.linkage.mobile72.sh.im.bean.MessageOut;
import com.linkage.mobile72.sh.im.bean.NewMessage;
import com.linkage.mobile72.sh.im.bean.SendAction;
import com.linkage.mobile72.sh.im.bean.Status;
import com.linkage.mobile72.sh.im.provider.Ws;
import com.linkage.mobile72.sh.im.provider.Ws.MessageTable;
import com.linkage.mobile72.sh.im.provider.Ws.MessageType;
import com.linkage.mobile72.sh.im.provider.Ws.ThreadTable;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.lib.util.LogUtils;

public class ChatService extends Service {
	private static final String TAG = ChatService.class.getSimpleName();

	private final static int MSG_OPEN = 1;
	private final static int MSG_ONMESSAGE = 2;
	private final static int MSG_CLOSE = 3;

	private final static int MSG_ERROR = 4;

	private BaseApplication mApp;
	// private Account mAccount;
	private long lastReveMsgTime = 0;

	// private GetUserInfoTask getUserInfo;
	private long ready_time = 0;
	private final long MAX_DELAY_TIME = 16 * 60 * 1000;
	// private final long MAX_DELAY_TIME = 90*1000;
	private String mWebSocketUrl = Consts.IM_SERVER;

	private MyWebSocketClient mWSClient;
	private long notic_id;
	private int notic_type;
	private static final long HB_PERIOD = 10;
	private static final String HB = "{\"action\":\"hb\"}";
	private ScheduledExecutorService mPoolExecutor = Executors
			.newSingleThreadScheduledExecutor();

	private static boolean ready = false;

	@SuppressLint("HandlerLeak")
	public Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FileHelper.MESSAGE_OPEN_DIALOG:// �?��启动Dialog

				break;
			case FileHelper.MESSAGE_START:// �?��下载

			case FileHelper.MESSAGE_PROGRESS:// 正在下载

				break;
			case FileHelper.MESSAGE_STOP:// 下载结束

				break;
			case FileHelper.MESSAGE_ERROR:

				break;

			}
			super.handleMessage(msg);
		}
	};

	private Runnable mWorker = new Runnable() {

		@Override
		public void run() {
			long now = System.currentTimeMillis();

			if (!isWebScoketAvailable() || !ready
					|| (now - ready_time > MAX_DELAY_TIME)) {
				LogUtils.e("===isWebScoketAvailable==mWorker====isWebScoketAvailable==mWorker=");
				// if (reOpen) {
				reconnect();
				// }
				// closeWebSocket();
				// doLogin();

			} else {

				LogUtils.e("===isWebScoketAvailable==mWorker================================");
			}
		}

	};

	private long mActiveBuddyId = -1;
	private int mActiveType = -1;
	private NetworkInfo mNetworkInfo;
	private ConnectivityManager mCm;
	private NotificationManager mNm;
	private PendingIntent mPendingIntent_chat = null;
	private PendingIntent mPendingIntent_myPaymentActivity = null;
	
	private FileHelper fileHelper;

	private final BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			NetworkInfo oldInfo = mNetworkInfo;
			mNetworkInfo = mCm.getActiveNetworkInfo();
			if (oldInfo == null) {
				if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
					newChatSession();
				}
			} else {
				if (mNetworkInfo != null) {
					if (!oldInfo.isConnected()) {
						if (mNetworkInfo.isConnected()) {
							newChatSession();
						}
					} else {
						if (mNetworkInfo.isConnected()) {
							if (mNetworkInfo.getType() != oldInfo.getType()) {
								newChatSession();
							}
						}
					}
				}
			}
		}
	};
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
//			LogUtils.e("我疯了", msg.what + "");
			switch (msg.what) {
			case MSG_OPEN:
				onWebSocketOpened();
				break;
			case MSG_ONMESSAGE:
				onReceiveMessage((String) msg.obj);
				break;
			case MSG_CLOSE:
				onClose(msg.arg1);
				break;
			case MSG_ERROR:
				onError();
				break;
			}
		};
	};

	private WebSocketClientListener mWebSocketListener = new WebSocketClientListener() {

		@Override
		public void onOpen(ServerHandshake handshakedata) {
			handler.sendEmptyMessage(MSG_OPEN);
		}

		@Override
		public void onMessage(String message) {
			handler.sendMessage(handler.obtainMessage(MSG_ONMESSAGE, message));
		}

		@Override
		public void onClose(int code, String reason, boolean remote) {
			handler.sendMessage(handler.obtainMessage(MSG_CLOSE, code, 0));
		}

		@Override
		public void onError(Exception ex) {
			handler.sendEmptyMessage(MSG_ERROR);
		}
	};

	public AccountData account;
	public ContentResolver contentResolver;
	public Cursor mThreadsCursor;
	
	public void onCreate() {
		super.onCreate();
		LogUtils.d("onCreate");
		mApp = BaseApplication.getInstance();
		contentResolver = mApp.getContentResolver();
		account = mApp.mCurAccount;
		
		Intent myPaymentIntent= new Intent(getApplicationContext(), MyPaymentActivity.class);

		myPaymentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mPendingIntent_myPaymentActivity = PendingIntent.getActivity(getApplicationContext(), 0, myPaymentIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		mNm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mCm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		mNetworkInfo = mCm.getActiveNetworkInfo();

		newChatSession();

		mPoolExecutor.scheduleAtFixedRate(mWorker, 1, HB_PERIOD,
				TimeUnit.SECONDS);
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mNetworkStateReceiver, filter);
		ready_time = System.currentTimeMillis();
		fileHelper = new FileHelper(mhandler);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		mSchoolApp.getTaskManager().unregisterDataListener(this);
		// unregisterReceiver(mNetworkStateReceiver);
		handler.removeCallbacksAndMessages(null);
		mPoolExecutor.shutdown();
		closeWebSocket();
		try {
			unregisterReceiver(mNetworkStateReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mService;
	}

	private IChatService.Stub mService = new IChatService.Stub() {

		@Override
		public void unregisterUploadListener(IUploadFileListener listener)
				throws RemoteException {

			if (mWSClient != null) {
				mWSClient.removeListener();
				mWSClient.close();
			}

		}

		@Override
		public void setActiveBuddyId(long buddyId, int chattype)
				throws RemoteException {
			LogUtils.d("setActiveBuddyId====notic_id:" + notic_id);
			LogUtils.d("setActiveBuddyId====buddyId:" + buddyId);
			LogUtils.d("setActiveBuddyId====notic_type:" + notic_type);
			LogUtils.d("setActiveBuddyId====chattype:" + chattype);
			if (notic_id == buddyId && notic_type == chattype) {
				mNm.cancelAll();
			}
			if (mActiveBuddyId != -99) {
				mActiveBuddyId = buddyId;
				mActiveType = chattype;
			} else {
				mActiveBuddyId = -1;
				mActiveType = -1;
			}
		}

		@Override
		public void sendPicFile(final long toId, final String filePath,
				final int chattype, long id, final String groupName)
				throws RemoteException {
			LogUtils.d("sendFile");
			Uri fileUri = null;
			if (id != 0) {
				fileUri = Uri.parse(MessageTable.CONTENT_URI + "/" + id);
				updateMessageStatus(fileUri, MessageType.MSG_STATUS_WAITING);
			} else {
				fileUri = savePicMessage(toId, Uri.fromFile(new File(filePath))
						.toString(), chattype);
			}
			final Uri uri = fileUri;
			List<ParamItem> params = new ArrayList<ParamItem>();
			params.add(new ParamItem("file", filePath, ParamItem.TYPE_FILE));

			WDJsonObjectMultipartRequest mRequest = new WDJsonObjectMultipartRequest(Consts.SERVER_uploadAttachment, Request.Method.POST, params, true,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							ProgressDialogUtils.dismissProgressBar();
							System.out.println("response=" + response);
							
							if (response.optInt("ret") == 0) {
								String uploadFileUrl = null;
								try {
									uploadFileUrl = response.getString("data");
//									if (jsonObject != null) {
//										uploadFileUrl = jsonObject.getString("url");
//									}
								} catch (JSONException e1) {
									e1.printStackTrace();
								}
								if (!TextUtils.isEmpty(uploadFileUrl)) {
									uploadFileUrl = Consts.IM_ATTACHMENT_PACKAGE + uploadFileUrl;
									try {
										LogUtils.d("upload photo succed");
										if (chattype == 0) {
											send(SendAction.sendPicture(toId,
													uploadFileUrl));
										} else {
											send(SendAction.sendPictureToGroup(toId,
													uploadFileUrl, groupName));
										}
										updateMessageStatus(uri,
												MessageType.MSG_STATUS_SENT);
									} catch (RemoteException e) {
										e.printStackTrace();
									}
								}
								else {
									updateMessageStatus(uri,
											MessageType.MSG_STATUS_ERROR);
								}
								
							} else {
								updateMessageStatus(uri,
										MessageType.MSG_STATUS_ERROR);
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							ProgressDialogUtils.dismissProgressBar();
							updateMessageStatus(uri,
									MessageType.MSG_STATUS_ERROR);
						}
					});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		}

		@Override
		public void sendVoiceFile(final long toId, final String filePath,
				final int chattype, final int time, long id,
				final String groupName) throws RemoteException {
			LogUtils.d("sendFile:" + filePath);
			Uri fileUri = null;
			if (id != 0) {
				fileUri = Uri.parse(MessageTable.CONTENT_URI + "/" + id);
				updateMessageStatus(fileUri, MessageType.MSG_STATUS_WAITING);
			} else {
				fileUri = saveAudioMessage(toId, time + "," + filePath,
						chattype);
			}
			final Uri uri = fileUri;
			List<ParamItem> params = new ArrayList<ParamItem>();
			params.add(new ParamItem("file", filePath, ParamItem.TYPE_FILE));

			WDJsonObjectMultipartRequest mRequest = new WDJsonObjectMultipartRequest(Consts.SERVER_uploadAttachment, Request.Method.POST, params, true,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							ProgressDialogUtils.dismissProgressBar();
							System.out.println("response=" + response);
							
							if (response.optInt("ret") == 0) {
								String uploadFileUrl = null;
								try {
									uploadFileUrl = response.getString("data");
//									jsonObject = response.getJSONObject("body");
//									if (jsonObject != null) {
//										uploadFileUrl = jsonObject.getString("url");
//									}
								} catch (JSONException e1) {
									e1.printStackTrace();
								}
								if (!TextUtils.isEmpty(uploadFileUrl)) {
									uploadFileUrl = Consts.IM_ATTACHMENT_PACKAGE + uploadFileUrl;
									try {
										LogUtils.d("upload photo succed");
										if (chattype == 0) {
											send(SendAction.sendAudio(toId, time + ","
													+ uploadFileUrl));
										} else {
											send(SendAction.sendAudioToGroup(toId, time
													+ "," + uploadFileUrl, groupName));
										}
										updateMessageStatus(uri,
												MessageType.MSG_STATUS_SENT);
									} catch (RemoteException e) {
										e.printStackTrace();
									}
								}
								else {
									updateMessageStatus(uri,
											MessageType.MSG_STATUS_ERROR);
								}
								
							} else {
								updateMessageStatus(uri,
										MessageType.MSG_STATUS_ERROR);
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							ProgressDialogUtils.dismissProgressBar();
							updateMessageStatus(uri,
									MessageType.MSG_STATUS_ERROR);
						}
					});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		}

		@Override
		public void send(String message) throws RemoteException {
			LogUtils.i("send->" + message);

			if (isWebScoketAvailable()) {
				try {
					mWSClient.send(message);
					// NewMessage msg = new NewMessage();
					// Member member = new Member();
					// member.setId(1);
					// member.setName("name");
					// msg.setContent("test");
					// msg.setFrom(member);
					// notifyNewMessage(msg);
				} catch (WebsocketNotConnectedException e) {
					LogUtils.e("WebSocket NotYetConnected");
				}
			} else {
				closeWebSocket();
				doLogin();
			}
		}

		@Override
		public void registerUploadListener(IUploadFileListener listener)
				throws RemoteException {

		}

		@Override
		public void logout() throws RemoteException {
			// mAccountManager.logout();
			// if(mNetworkStateReceiver!=null)
			// {
			// unregisterReceiver(mNetworkStateReceiver);
			// }
			mNm.cancelAll();
			LogUtils.d("logout====222222222====");
			handler.removeCallbacksAndMessages(null);
			mPoolExecutor.shutdown();
			closeWebSocket();

			ChatService.this.stopSelf();
			// onDestroy();
		}

		@Override
		public void login(String token, String name, long id)
				throws RemoteException {
			// TODO Auto-generated method stub
			// m_token = token;
			LogUtils.w("login======");
			// closeWebSocket();

			doLogin();
		}

		@Override
		public boolean ready() throws RemoteException {
			return ready;
		}

		@Override
		public boolean getNotifyStatus(long userId, String key)
				throws RemoteException {
			String name = userId + "_Notice";
			SharedPreferences sp = getSharedPreferences(name, MODE_PRIVATE);
			Boolean notifyStatus = sp.getBoolean(key, true);
			return notifyStatus;
		}

		@Override
		public void setNotifyStatus(long userId, String key, boolean opened)
				throws RemoteException {
			String name = userId + "_Notice";
			SharedPreferences sp = getSharedPreferences(name, MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean(key, opened);
			editor.commit();
		}

	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.v("TrafficService", "startCommand");

		flags = START_REDELIVER_INTENT;
		return super.onStartCommand(intent, flags, startId);
	}

	private synchronized void doLogin() {
		closeWebSocket();

		LogUtils.w("doLogin");
		ready = false;
		Intent intent = new Intent(
				"com.linkage.mobile.classwork.fragment.main.SmsBroadCastReceiver");
		intent.putExtra("message", "GetUserInfoTask is success!");
		// send Broadcast
		sendBroadcast(intent);
		// mAccount = mAccountManager.getAccount();
		if (getAccount() == null) {
			return;
		}
		// getAccount().setimAccessToken(getIMtoken());
		newChatSession();
		/**
		 * 家长汇登录接口，待定
		 */
		// if(mAccount.getimAccessToken() ==null
		// ||"".equalsIgnoreCase(mAccount.getimAccessToken()))
		// {
		// GetXXTContactTask mLoginTask = new
		// GetXXTContactTask(RequestUtils.createGetClassWorkContact(m_userid,
		// m_name,Long.toString( m_type),0,0)) {
		// @Override
		// protected void onFailed(Exception e) {
		// LogUtils.w("GetXXTContactTask error");
		// }
		//
		// @Override
		// protected void onSucceed(String data) {
		// super.onSucceed(data);
		// ClassWorkContactTask classWorkContactTask = new
		// ClassWorkContactTask(data) {
		// @Override
		// protected void onFailed(Exception e) {
		// LogUtils.w("GetXXTContactTask error");
		// }
		//
		// @Override
		// protected void onSucceed(ClassWorkContactResult data) {
		// super.onSucceed(data);
		// mAccount.setimAccessToken(data.Token);
		// mAccount.setimId(data.id);
		// newChatSession();
		// }
		// };
		// classWorkContactTask.execute();
		// }
		// };
		// mLoginTask.execute();
		// }else{
		// newChatSession();
		// }
	}

	private boolean isWebScoketAvailable() {
		System.out.println("___isWebScoketAvailable");
		System.out.println("mWSClient != null===" + (mWSClient != null));
		System.out.println("mWSClient.getReadyState()="
				+ mWSClient.getReadyState());
		System.out.println("WebSocket.READYSTATE.OPEN="
				+ WebSocket.READYSTATE.OPEN);

		return mWSClient != null
				&& mWSClient.getReadyState() == WebSocket.READYSTATE.OPEN;
	}

	private synchronized void newChatSession() {
		ready = false;
		if (!mApp.isNetworkAvailable()) {
			LogUtils.w("network is not avaiable");
			return;
		}

		try {
			LogUtils.d("create new WebSocketClient");
			closeWebSocket();
			mWSClient = new MyWebSocketClient(mWebSocketUrl);
			mWSClient.setWebSocketClientListener(mWebSocketListener);
			mWSClient.connect();
			LogUtils.d("newChatSession end");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void closeWebSocket() {
		LogUtils.w("close WebSocket");

		if (mWSClient != null) {
			mWSClient.close();
			mWSClient.removeListener();
		}
		// if (reOpen) {
		// reconnect();
		// return;
		// }
	}

	private void onWebSocketOpened() {
		LogUtils.e("WebSocket is Opened");
		try {
			LogUtils.e("WebSocket is Opened:"
							+ MessageOut.json(new LoginAction(getIMtoken())));
			mWSClient.send(MessageOut.json(new LoginAction(getIMtoken())));
		} catch (WebsocketNotConnectedException e) {
			LogUtils.e("WebSocket NotYetConnected");
		}
	}

	private void onReceiveMessage(String message) {
		LogUtils.d("onReceiveMessage:" + message);
		System.out.println("====" + message);
		ready_time = System.currentTimeMillis();
		try {
			MessageIn in = MessageIn.fromJson(message);
			String type = in.getType();
			if (MessageIn.ERROR.equals(type)) {
				AuthError error = AuthError.fromJson(message);
				LogUtils.e(error.getErrorCode() + ":" + error.getErroMessage());
				if (error.getErrorCode() == 403) {
					LogUtils.e("try to oauth");
					doLogin();
				}
			} 
			/*
			else if (MessageIn.NOTICE.equals(type)
					|| MessageIn.NEWS.equals(type)
					|| MessageIn.REMIND.equals(type)) {
				// String[] ss = {
				// "{'title': '西祠','url': 'http://www.xici.net/','content': '西祠�?,'type': '1003'}",
				// "{'title': '百度','url': 'http://www.baidu.com/','content': '百度','type': '1003'}",
				// "{'title': '猩阆','url': 'http://www.sina.com/','content': '新浪','type': '1003'}"
				// };
				// long tt = System.currentTimeMillis();
				// notifyNewNotice(ss[(int) (tt % 3)]);
				notifyNewNotice(message);
			} 
			else if (MessageIn.FRIENDS.equals(type)) {
				// mShouldSendHB = true;
				// mScClsedBeacauseOfToken = false;
				saveFrineds(message);
			} 
			*/
			else if (MessageIn.STATUS.equals(type)) {
				Status status = Status.fromJson(message);
				notifyNewStatus(status);
			} else if (MessageIn.MESSAGE.equals(type)) {
				NewMessage msg = NewMessage.fromJson(message);
//				if(!MessageIn.NOTICE.equalsIgnoreCase( msg.getType()))
//				{
				saveNewMessage(msg);
//				}
				notifyNewMessage(msg);
			} else if (MessageIn.HB.equals(type)) {
				// mWSClient.send(HB);
				LogUtils.e("send->" + HB);
				// if (isWebScoketAvailable()) {
				try {
					mWSClient.send(HB);
				} catch (WebsocketNotConnectedException e) {
					LogUtils.e("WebSocket NotYetConnected");
				}
				// }
			} else if (MessageIn.AUTH_SUCCESS.equals(type)) {
				ready_time = System.currentTimeMillis();
				SharedPreferences.Editor editor = mApp.getSp().edit();
				editor.putLong("CONNECT_TIME", ready_time);
				editor.commit();
				ready = true;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void onClose(int errorType) {
		LogUtils.e("onClose");
		ready = false;
		if (mWSClient != null) {
			mWSClient.removeListener();
		}
		// if (reOpen) {
		// reconnect();
		// }
	}

	private void reconnect() {
		LogUtils.d("reconnect");
		newChatSession();
	}

	private void onError() {
		LogUtils.e("onError");
		ready = false;
	}

	/*
	private void saveFrineds(String message) {
		L.i("***********存储聊天联系�?*************", "---start---");
		ClazzWorkContactGroupList groups = new ClazzWorkContactGroupList();
		JSONObject result;
		try {
			result = new JSONObject(message);
			if (result.opt("value").equals("成功")) {
				JSONArray array = result.optJSONArray("list");
				for (int i = 0; i < array.length(); i++) {
					groups.add(ClazzWorkContactGroup.fromJsonObject(array
							.optJSONObject(i)));
				}
			}
			mSchoolApp.getAccountDB()
					.insertContacts(getAccount().getLoginName(),
							groups.getList());
			L.i("***********存储聊天联系�?*************", "---end---");
			// 获取真实ID + 存储
			getRealIdIfNeed();
		} catch (JSONException e) {
			L.i("***********存储聊天联系�?*************", "---error---");
			e.printStackTrace();
		}
	}
	*/

	public void getRealIdIfNeed() {
		/*
		List<ClazzWorkContactGroup> groups = mSchoolApp
				.getDataSource()
				.getContactGroups(getAccount().getLoginName(), false);
		for (int i = 0; i < groups.size(); i++) {
			ClazzWorkContactGroup group = groups.get(i);
			List<ClazzWorkContact> contacts = group.group_members;
			for (int j = 0; j < contacts.size(); j++) {
				long userid = contacts.get(j).userid;
				long realId = SchoolApp.getInstance().getDataSource()
						.getContactRealIdById(userid);
				if (realId == 0) {
					syncRealId();
					return;
				}
			}
		}
		*/
	}

	// 获取真实ID 用来显示头像
	/*
	private void syncRealId() {
		List<ClazzWorkContactGroup> clazzWorkContactGroupList = SchoolApp
				.getInstance().getDataSource()
				.getContactGroups(getAccount().getLoginName(), false);
		StringBuffer sb_parent = new StringBuffer();
		StringBuffer sb_teacher = new StringBuffer();
		for (int a = 0; clazzWorkContactGroupList.size() > a; a++) {
			List<ClazzWorkContact> membersList = clazzWorkContactGroupList
					.get(a).group_members;
			for (int b = 0; membersList.size() > b; b++) {
				if (!String.valueOf(membersList.get(b).userid).startsWith("2")) {
					sb_parent.append(membersList.get(b).userid + ",");
				} else {
					sb_teacher.append(membersList.get(b).userid + ",");
				}
			}
		}
		SchoolApp.getInstance().getTaskManager()
				.getUserIdTask(sb_parent.toString());
		SchoolApp.getInstance().getTaskManager()
				.getTeacherUserIdTask(sb_teacher.toString());
	}
	*/

	// private ContentValues getFriendContentValues(Contact c, String
	// accountName,
	// int contactType) {
	// ContentValues cv = new ContentValues();
	// cv.put(ContactTable.ID, c.getId());
	// cv.put(ContactTable.NAME, c.getName());
	// cv.put(ContactTable.TYPE, c.getType());
	// cv.put(ContactTable.STATUS, c.getStatus());
	// cv.put(ContactTable.GROUP_ID, c.getGroupId());
	// cv.put(ContactTable.GROUP_NAME, c.getGroupName());
	// cv.put(ContactTable.AVATAR_URL, c.getAvatarUrl());
	// cv.put(ContactTable.ACCOUNT_NAME, accountName);
	// cv.put(ContactTable.CONTACT_TYPE, contactType);
	// return cv;
	// }

	private void saveNewMessage(final NewMessage msg) {
		ContentValues cv = new ContentValues();
		cv.put(MessageTable.ACCOUNT_NAME, getAccount().getLoginname());
		String content = msg.getContent();
		if(content != null && content.contains(";")) {
			String[] con = content.split(";");
			long t0 = Long.valueOf(con[1]);
			long t1 = System.currentTimeMillis();
			SharedPreferences sp = BaseApplication.getInstance().getSp();
			content = con[0] + "\n" 
//					+ "ct:" + Utils.format(sp.getLong("CONNECT_TIME", 0)) + "\n" 
//					+ "st:" + Utils.format(t0) + "\n" 
//					+ "rt:" + Utils.format(t1) + "\n"
					+ "ct:" + sp.getLong("CONNECT_TIME", 0) + "ms\n" 
					+ "st:" + t0 + "ms\n" 
					+ "rt:" + t1 + "ms\n"
					+ "rt-st:" + (t1-t0) + "ms\n"
					+ "rt-ct:" + (t1-sp.getLong("CONNECT_TIME", 0)) + "ms"; 
		}
		cv.put(Ws.MessageTable.BODY, content);
		cv.put(Ws.MessageTable.IS_INBOUND, MessageType.INCOMING);
		String type = msg.getContentType();
//		type = "notice";
		int msgType;
		if (NewMessage.TYPE_TEXT.equals(type)) {
			msgType = MessageType.TYPE_MSG_TEXT;
		} else if (NewMessage.TYPE_PICTURE.equals(type)) {
			msgType = MessageType.TYPE_MSG_PIC;
		} else if (NewMessage.TYPE_AUDIO.equals(type)) {
			msgType = MessageType.TYPE_MSG_AUDIO;
			String m_msg = msg.getContent();
			final String path[] = m_msg.split(",");

			new Thread() {
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					//
					String fileUrl = "";
					if (path.length == 2) {

						fileUrl = path[1];
					} else {
						fileUrl = path[0];
					}
					String fileURL = java.net.URLDecoder.decode(fileUrl);
					LogUtils.i("***********download_fileurl**************" + fileURL);
					String fileName = fileURL.substring(fileURL
							.lastIndexOf("/"));
					fileHelper.down_file(fileURL, mApp
							.getWorkspaceVoice().getAbsolutePath(), fileName);
				}
			}.start();

			// String fileURL = java.net.URLDecoder.decode(msg.getContent());
			// L.i("***********download_fileurl**************", ""+fileURL);
			// String fileName = fileURL.substring(fileURL.lastIndexOf("/"));
			// fileHelper.down_file(fileURL,
			// mSchoolApp.getWorkspaceVoide().getAbsolutePath(),
			// fileName);

		} else if (NewMessage.TYPE_HOMEWORK.equals(type)) {
			msgType = MessageType.TYPE_MSG_HOMEWORK;
		} else if (NewMessage.TYPE_NOTICE.equals(type)) {
			msgType = MessageType.TYPE_MSG_NOTICE;
		} else {
			msgType = MessageType.TYPE_MSG_NOTICE;
		}
		cv.put(Ws.MessageTable.TYPE, msgType);
		cv.put(Ws.MessageTable.SENT_TIME, msg.getTimestamp());
		cv.put(Ws.MessageTable.RECEIVED_TIME, System.currentTimeMillis());

		long groupid = msg.getGroupId();
		if (groupid != -1 && groupid != 0) {
//			cv.put(Ws.MessageTable.BUDDY_ID, groupid);
//			cv.put(Ws.MessageTable.BUDDY_NAME, msg.getGroupName());
//			cv.put(MessageTable.CHAT_TYPE, ChatType.CHAT_TYPE_GROUP);
		} else {
//			cv.put(Ws.MessageTable.BUDDY_ID, msg.getFrom().getId());
			cv.put(Ws.MessageTable.BUDDY_NAME, msg.getFrom().getName());
			LogUtils.v("===========BuddyName:" + msg.getFrom().getName());
//			cv.put(MessageTable.CHAT_TYPE, ChatType.CHAT_TYPE_SINGLE);
		}
		if(msgType == MessageType.TYPE_MSG_NOTICE)
		{
			cv.put(Ws.MessageTable.BUDDY_ID, 99999);
			cv.put(MessageTable.CHAT_TYPE, ChatType.CHAT_TYPE_SINGLE);
			cv.put(MessageTable.SENDER_ID, 99999);
			LogUtils.e("=========++++++++++++++++++++");
		}else{
			cv.put(MessageTable.SENDER_ID, msg.getFrom().getId());
		}
		LogUtils.e("m_name:" + getAccount().getLoginname()
				+ "msg.getFrom().getId():" + msg.getFrom().getId());
		getContentResolver().insert(MessageTable.CONTENT_URI, cv);
	}

	private Uri savePicMessage(long buddyId, String message, int chattype) {
		ContentValues cv = new ContentValues();
		cv.put(MessageTable.BUDDY_ID, buddyId);
		// cv.put(MessageTable.SENDER_ID, m_id);
		cv.put(MessageTable.SENDER_ID, getAccount().getUserId());
		cv.put(MessageTable.BODY, message);
		cv.put(MessageTable.IS_INBOUND, MessageType.OUTGOINT);
		cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_PIC);
		cv.put(MessageTable.SENT_TIME, System.currentTimeMillis());
		cv.put(MessageTable.ACCOUNT_NAME, getAccount().getLoginname());
		cv.put(MessageTable.CHAT_TYPE, chattype);

		return getContentResolver().insert(MessageTable.CONTENT_URI, cv);
	}

	private Uri saveAudioMessage(long buddyId, String message, int chattype) {
		ContentValues cv = new ContentValues();
		cv.put(MessageTable.BUDDY_ID, buddyId);
		cv.put(MessageTable.SENDER_ID, getAccount().getUserId());
		cv.put(MessageTable.BODY, message);
		cv.put(MessageTable.IS_INBOUND, MessageType.OUTGOINT);
		cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_AUDIO);
		cv.put(MessageTable.SENT_TIME, System.currentTimeMillis());
		cv.put(MessageTable.RECEIVED_TIME, System.currentTimeMillis());
		cv.put(MessageTable.ACCOUNT_NAME, getAccount().getLoginname());
		cv.put(MessageTable.CHAT_TYPE, chattype);

		return getContentResolver().insert(MessageTable.CONTENT_URI, cv);
	}

	private void updateMessageStatus(Uri uri, int msgstutas) {
		ContentValues cv = new ContentValues();
		cv.put(MessageTable.OUTBOUND_STATUS, msgstutas);
		getContentResolver().update(uri, cv, null, null);
	}

	private void notifyNewStatus(Status status) {
		// if ((mActiveBuddyId == -1 || mActiveBuddyId != status.getFriendId())
		// && status.getStatus().equals(Status.ONLINE)) {
		// String name = mAccountManager.getAccount().getLoginName();
		// if (ChatNotifyUtils.statusNotifyEnable(this, name)) {
		// String ticker = getString(R.string.status_triker_format,
		// status.getFriendName());
		// NotificationCompat.Builder builder = new NotificationCompat.Builder(
		// this);
		// builder.setAutoCancel(true);
		// builder.setSmallIcon(R.drawable.app_logo);
		// builder.setTicker(ticker);
		// builder.setContentTitle(getString(R.string.online_notify));
		// builder.setContentText(ticker);
		// builder.setContentIntent(mPendingIntent);
		// // if(ChatNotifyUtils.statusVibrateEnable(this, name)) {
		// // if(ChatNotifyUtils.statusVoiceEnable(this, name)) {
		// // builder.setDefaults(Notification.DEFAULT_ALL);
		// // } else {
		// // builder.setDefaults(Notification.DEFAULT_LIGHTS |
		// // Notification.DEFAULT_VIBRATE);
		// // }
		// // } else {
		// // if(ChatNotifyUtils.statusVoiceEnable(this, name)) {
		// // builder.setDefaults(Notification.DEFAULT_LIGHTS |
		// // Notification.DEFAULT_SOUND);
		// // } else {
		// // builder.setDefaults(Notification.DEFAULT_LIGHTS);
		// // }
		// // }
		// // Notification notification = builder.build();
		// // mNm.notify(R.string.app_name, notification);
		// }
		// }
	}

	/**
	 * 1001：�?�?1002：新�?1003：提�?
	 */
/*
	private void notifyNewNotice(String message) {
		LogUtils.d("notifyNewNotice:" + message);
		// 定义Notification的各种属�?
		Notification notification = new Notification();
		notification.icon = R.drawable.app_logo;
		String name = getAccount().getUserId() + "_Notice";
		SharedPreferences sp = getSharedPreferences(name, MODE_PRIVATE);
		Boolean b_setting_sound_switch = sp.getBoolean("setting_sound_switch",
				true);
		Boolean b_setting_vibrations_switch = sp.getBoolean(
				"setting_vibrations_switch", true);
		if (b_setting_sound_switch && b_setting_vibrations_switch) {
			notification.defaults = Notification.DEFAULT_LIGHTS
					| Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
		} else {
			if (b_setting_sound_switch) {
				notification.defaults = Notification.DEFAULT_LIGHTS
						| Notification.DEFAULT_SOUND;
			} else if (b_setting_vibrations_switch) {
				notification.defaults = Notification.DEFAULT_LIGHTS
						| Notification.DEFAULT_VIBRATE;
			} else {
				notification.defaults = Notification.DEFAULT_LIGHTS;
			}
		}

		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(message);
			// 设置通知的事件消�?
			String contentTitle = jsonObj.optString("title"); // 通知栏标�?
			String contentText = jsonObj.optString("content"); // 通知栏内�?

			Intent mIntent = new Intent(Intent.ACTION_MAIN);

			String type = jsonObj.optString("type");
			int notifyId = 0;
			if (MessageIn.NEWS.equals(type)) {
				System.out.println("新闻");
				notifyId = Integer.parseInt(MessageIn.NEWS);

				mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				mIntent.setClass(this, PushNewsDetailActicity.class);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			} else if (MessageIn.NOTICE.equals(type)) {
				notifyId = Integer.parseInt(MessageIn.NOTICE);

				mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				mIntent.setClass(this, PushNoticeDetailActicity.class);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			} else if (MessageIn.REMIND.equals(type)) {
				notifyId = Integer.parseInt(MessageIn.REMIND);

				mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				mIntent.setClass(this, SplashActivity.class);
			} else {
				return;
			}
			// Bundle bundle = new Bundle();
			// bundle.putString("message", message);
			// mIntent.putExtras(bundle);
			notifyId = (int) (Math.random() * 100000);
			mIntent.putExtra("message", message);
			mIntent.putExtra("notifyId", notifyId);

			PendingIntent contentItent = PendingIntent.getActivity(this,
					notifyId, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(this, contentTitle, contentText,
					contentItent);
			((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
					.notify(notifyId, notification);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
*/

	private void notifyNewMessage(NewMessage msg) {
		
		if(MessageIn.NOTICE.equalsIgnoreCase(msg.getContentType()))
		{
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					this);
			builder.setAutoCancel(true);
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setTicker("缴费提醒");
//			builder.setContentTitle(msg.getTitle());
			builder.setContentTitle("缴费提醒");
			builder.setContentText(msg.getContent());
	
		
			
//			ContentValues cv = createThreadContentValues(values,-1);
//			cv.put(ThreadTable.UNREAD_COUNT, 1);
//			db.insert(TABLE_THREADS, ThreadTable.BUDDY_ID, cv);
			// 组聊天，即语音家长会
		    builder.setContentIntent(mPendingIntent_myPaymentActivity);
		

			String name = getAccount().getUserId() + "_Notice";
			SharedPreferences sp = getSharedPreferences(name, MODE_PRIVATE);
			// 存入数据
			Boolean b_setting_sound_switch = sp.getBoolean(
					"setting_sound_switch", true);
			Boolean b_setting_vibrations_switch = sp.getBoolean(
					"setting_vibrations_switch", true);
			if (b_setting_sound_switch && b_setting_vibrations_switch) {
				builder.setDefaults(Notification.DEFAULT_LIGHTS
						| Notification.DEFAULT_VIBRATE
						| Notification.DEFAULT_SOUND);
			} else {
				if (b_setting_sound_switch) {
					builder.setDefaults(Notification.DEFAULT_LIGHTS
							| Notification.DEFAULT_SOUND);
				} else if (b_setting_vibrations_switch) {
					builder.setDefaults(Notification.DEFAULT_LIGHTS
							| Notification.DEFAULT_VIBRATE);
				} else {
					builder.setDefaults(Notification.DEFAULT_LIGHTS);
				}
			}

			long now_time = System.currentTimeMillis();
			if ((now_time - lastReveMsgTime) > 2000) {
				lastReveMsgTime = now_time;

			} else {
				builder.setDefaults(Notification.DEFAULT_LIGHTS);
			}
			Notification notification = builder.build();
			mNm.notify(R.string.app_name, notification);
		}else{
			String type = msg.getContentType();
			LogUtils.e("mActiveBuddyId====:" + mActiveBuddyId + "");
			LogUtils.e("msg.getFrom().getId()====:" + msg.getFrom().getId() + "");
			LogUtils.e("mActiveType====:" + mActiveType + "");
			LogUtils.e("getGroupId====:" + msg.getGroupId() + "");
			long groupid = msg.getGroupId();
			int tmpType = -1;
			if (groupid != -1 && groupid != 0) {
				tmpType = ChatType.CHAT_TYPE_GROUP;
	
			} else {
				tmpType = ChatType.CHAT_TYPE_SINGLE;
	
			}
			if (mActiveBuddyId == -1
					|| (mActiveBuddyId != msg.getFrom().getId() && mActiveType == ChatType.CHAT_TYPE_SINGLE)
					|| (mActiveBuddyId != msg.getGroupId() && mActiveType == ChatType.CHAT_TYPE_GROUP)
					|| (mActiveType != tmpType)
					|| (NewMessage.TYPE_HOMEWORK.equals(type) || NewMessage.TYPE_NOTICE
							.equals(type))) {
	
				if (groupid != -1 && groupid != 0) {
					notic_type = ChatType.CHAT_TYPE_GROUP;
					notic_id = msg.getGroupId();
				} else {
					notic_type = ChatType.CHAT_TYPE_SINGLE;
					notic_id = msg.getFrom().getId();
				}
	
				// if (ChatNotifyUtils.messageNotifyEnable(this, name)) {
				String ticker = getString(R.string.message_ticker_format, msg
						.getFrom().getName());
				NotificationCompat.Builder builder = new NotificationCompat.Builder(
						this);
				builder.setAutoCancel(true);
				builder.setSmallIcon(R.drawable.ic_launcher);
				builder.setTicker(ticker);
				builder.setContentTitle(ticker);
				if (NewMessage.TYPE_TEXT.equals(type)) {
					builder.setContentText(msg.getContent());
				} else if (NewMessage.TYPE_PICTURE.equals(type)) {
					builder.setContentText("图片");
				} else if (NewMessage.TYPE_AUDIO.equals(type)) {
					builder.setContentText("语音");
				} else if (NewMessage.TYPE_HOMEWORK.equals(type)
						|| NewMessage.TYPE_NOTICE.equals(type)) {
					notic_id = -99;
					notic_type = -99;
					builder.setContentText("作业提醒");
				}
	
				if(account != null) {
					mThreadsCursor = contentResolver.query(ThreadTable.CONTENT_URI, null,
							ThreadTable.ACCOUNT_NAME + "=?", 
							new String[] { String.valueOf(account.getLoginname()) },
							ThreadTable.MSG_SENT_TIME + " desc");
					mThreadsCursor.moveToFirst();
					if(!mThreadsCursor.isAfterLast()) {
						
						long buddyId = mThreadsCursor.getLong(mThreadsCursor
								.getColumnIndexOrThrow(ThreadTable.BUDDY_ID));
//						String buddyName = mThreadsCursor.getString(mThreadsCursor
//								.getColumnIndexOrThrow(ThreadTable.BUDDY_NAME));
						int chatType = mThreadsCursor.getInt(mThreadsCursor
								.getColumnIndexOrThrow(ThreadTable.CHAT_TYPE));
//						Intent intent = NewChatActivity.getIntent(mApp, buddyId, buddyName, chatType,0);
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//								| Intent.FLAG_ACTIVITY_SINGLE_TOP);
//						mPendingIntent_chat = PendingIntent.getActivity(this, 0, intent,
//								PendingIntent.FLAG_CANCEL_CURRENT);
					}
				}
				builder.setContentIntent(mPendingIntent_chat);
	
				String name = getAccount().getUserId() + "_Notice";
				SharedPreferences sp = getSharedPreferences(name, MODE_PRIVATE);
				// 存入数据
				Boolean b_setting_sound_switch = sp.getBoolean(
						"setting_sound_switch", true);
				Boolean b_setting_vibrations_switch = sp.getBoolean(
						"setting_vibrations_switch", true);
				if (b_setting_sound_switch && b_setting_vibrations_switch) {
					builder.setDefaults(Notification.DEFAULT_LIGHTS
							| Notification.DEFAULT_VIBRATE
							| Notification.DEFAULT_SOUND);
				} else {
					if (b_setting_sound_switch) {
						builder.setDefaults(Notification.DEFAULT_LIGHTS
								| Notification.DEFAULT_SOUND);
					} else if (b_setting_vibrations_switch) {
						builder.setDefaults(Notification.DEFAULT_LIGHTS
								| Notification.DEFAULT_VIBRATE);
					} else {
						builder.setDefaults(Notification.DEFAULT_LIGHTS);
					}
				}
	
				long now_time = System.currentTimeMillis();
				if ((now_time - lastReveMsgTime) > 2000) {
					lastReveMsgTime = now_time;
	
				} else {
					builder.setDefaults(Notification.DEFAULT_LIGHTS);
				}
				Notification notification = builder.build();
				mNm.notify(R.string.app_name, notification);
				// }
			} else {
				int chattype = ChatType.CHAT_TYPE_SINGLE;
	
				if (groupid != -1 && groupid != 0) {
					chattype = ChatType.CHAT_TYPE_GROUP;
				}
				ContentValues cv = new ContentValues();
				cv.put(ThreadTable.UNREAD_COUNT, 0);
				getContentResolver().update(
						ThreadTable.CONTENT_URI,
						cv,
						ThreadTable.ACCOUNT_NAME + "=? and " + ThreadTable.BUDDY_ID
								+ "=? and " + ThreadTable.CHAT_TYPE + "=? and "
								+ ThreadTable.MSG_TYPE + " in "
								+ MessageType.MSG_TYPE_CHAT,
						new String[] { getAccount().getLoginname(),
								String.valueOf(mActiveBuddyId),
								String.valueOf(chattype) });
			}
		}
	}

	private AccountData getAccount() {
		if (mApp == null) {
			mApp = BaseApplication.getInstance();
		}
		return mApp.getDefaultAccount();
	}

	private String getIMtoken() {
		String token = "";
		AccountData account = getAccount();
		if (account != null) {
//			token = account.getImToken();
			token = account.getToken();
		}
//		return "100000";
		return token;
	}

	/*
	@Override
	public void onUpdate(int what, BaseData date, boolean successed) {
		// TODO Auto-generated method stub
		L.e("===Service=onUpdate====", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		if (what == USERINFO_GET_USERID) {
			if (!successed) {
				return;
			}
			// 真实ID存入数据�?
			SchoolApp
					.getInstance()
					.getDataSource()
					.insertContactsRealId(
							((ListRelUserIdResult) date).getList());
		} else if (what == USERINFO_GET_USERID_TEACHER) {
			if (!successed) {
				return;
			}
			// 真实ID存入数据�?
			SchoolApp
					.getInstance()
					.getDataSource()
					.insertContactsRealId(
							((ListRelUserIdResult) date).getList());
		}
	}
	*/
}