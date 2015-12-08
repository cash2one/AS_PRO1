package com.linkage.mobile72.sh.app;

import info.emm.messenger.IMClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.toolbox.Volley;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.fragment.JxParentFragment;
import com.linkage.mobile72.sh.fragment.MainFragment2;
import com.linkage.mobile72.sh.im.service.IChatService;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.chat.MessageListener;
import com.linkage.mobile72.sh.data.AccountChild;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.AttenPicState;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.OLConfig;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.im.service.ChatService;
import com.linkage.mobile72.sh.imol.service.IMChatService;
import com.linkage.mobile72.sh.imol.service.IPushMessageService;
import com.linkage.mobile72.sh.imol.service.IMChatService.MsgBinder;
import com.linkage.mobile72.sh.utils.NativeImageLoader;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.exception.GlobalExceptionStatistics;
import com.linkage.lib.util.DESPlus72;
import com.linkage.lib.util.LogUtils;
import com.morgoo.droidplugin.PluginHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class BaseApplication extends Application   {
	
	public final String TAG = "BaseApplication";
	private static BaseApplication mInstance;
	private static SharedPreferences sp;
	private DataHelper mHelper;
	public AccountData mCurAccount;
	private OLConfig olConfig;
	public ImageLoader imageLoader,imageLoader_group;
	public DisplayImageOptions defaultOptions,defaultOptionsGroup,defaultOptionsPhoto, defaultOptionsAlbum;
	private RequestQueue mRequestQueue;
	private com.android.volley.toolbox.ImageLoader mNetworkImageLoader;
	private int mflag;
	private DESPlus72 mDESPlus72;
	private IChatService mChatService;
	private IMChatService mIMChatService;
	private boolean mIsImServiceStarted;
	private ArrayList<Message> mQueue = new ArrayList<Message>();
	private Handler handler = new Handler();
	public static IPushMessageService lisenter;
	private boolean hasLoginIm = false;
	private String chatUserId = "";
	// 0 - 不在 1-单聊 2 群聊
	private int inChat = 0;
	private String imcomeChatId = "";
	
	private boolean isKickOffDlgShowing = false;
	private boolean isInLoginActivity = false;
	
	public MainFragment2 mainFragment2;
    public JxParentFragment jxParentFragment;
	
	private Map<String, AttenPicState> picStateMap = new Hashtable<String, AttenPicState>();

	private GlobalExceptionStatistics ges;
	@Override
	public void onCreate() {
		super.onCreate();

		PluginHelper.getInstance().applicationOnCreate(getBaseContext());


		initDESPlus72();
		mInstance = this;
		ges = GlobalExceptionStatistics.getInstance();
		ges.init(this);


		sp = getSharedPreferences("XKZ", Context.MODE_PRIVATE);
		mHelper = DataHelper.getHelper(getApplicationContext()); 
		mCurAccount = getDefaultAccount();
		imageLoader = ImageLoader.getInstance();
        imageLoader_group = ImageLoader.getInstance();
		startImServiceIfNeed();
		startService(new Intent(this, ChatService.class));
		loginIm(null, null, 0);
		defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc()
                .showStubImage(R.drawable.default_user)
                .showImageForEmptyUri(R.drawable.default_user)
                .showImageOnFail(R.drawable.default_user).build();

		defaultOptionsGroup = new DisplayImageOptions.Builder().cacheOnDisc()
                .showStubImage(R.drawable.default_group)
                .showImageForEmptyUri(R.drawable.default_group)
                .showImageOnFail(R.drawable.default_group).build();

        defaultOptionsPhoto = new DisplayImageOptions.Builder().cacheOnDisc().cacheInMemory()
                .showStubImage(R.drawable.default_photo)
                .showImageForEmptyUri(R.drawable.default_photo)
                .showImageOnFail(R.drawable.default_photo).build();

        defaultOptionsAlbum = new DisplayImageOptions.Builder().cacheOnDisc().cacheInMemory()
                .showStubImage(R.drawable.empty_photo)
                .showImageForEmptyUri(R.drawable.empty_photo)
                .showImageOnFail(R.drawable.empty_photo).build();
        
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(defaultOptions).build();
        imageLoader.init(config);
        ImageLoaderConfiguration config1 = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(defaultOptionsGroup).build();
        imageLoader_group.init(config1);
//        bindIMService();
        IMClient.getInstance().init(getApplicationContext(),"120.55.138.134",8443,"120.55.144.110");
        IMClient.getInstance().registerEventListener(
				MessageListener.getInstance());
	}

	@Override
	protected void attachBaseContext(Context base) {
		PluginHelper.getInstance().applicationAttachBaseContext(base);
		super.attachBaseContext(base);
	}
	
	public SharedPreferences getSp() {
		return sp;
	}

	public static synchronized BaseApplication getInstance() {
		return mInstance;
	}

	public int getflag() {
		return mflag;
	}
	
	public void setflag(int flag) {
		this.mflag = flag;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			// mHttpClient = new DefaultHttpClient();
			// PoolingClientConnectionManager
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return mRequestQueue;
	}
	
	public String getAccessToken() {
		String token = "";
		AccountData currentAccount = getDefaultAccount();
		if (currentAccount != null) {
			token = currentAccount.getToken();
		}
		return token;
	}
	
	public AccountData getLastLogintAccount() {
		AccountData data = null;
		try {
			Map<String, Object> userMap = new HashMap<String, Object>(); 
	        userMap.put("defaultUser", 1); 
			List<AccountData> list = mHelper.getAccountDao().queryForFieldValues(userMap);
			if(list.size() > 0) {
				data = (AccountData)list.get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public AccountData getDefaultAccount() {
		AccountData data = null;
		try {
			Map<String, Object> userMap = new HashMap<String, Object>(); 
	        userMap.put("defaultUser", 1); 
			List<AccountData> list = mHelper.getAccountDao().queryForFieldValues(userMap);
			if(list.size() > 0) {
				data = (AccountData)list.get(0);
			}else {
				QueryBuilder<AccountData, Integer> queryBuilder = mHelper.getAccountDao().queryBuilder();
				data = mHelper.getAccountDao().queryForFirst(queryBuilder.orderBy("loginDate", true).prepare());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public List<AccountChild> getAccountChild() {
		List<AccountChild> childs = new ArrayList<AccountChild>();
		AccountData account = getDefaultAccount();
		if(account == null)return childs;
		try {
			Map<String, Object> userMap = new HashMap<String, Object>(); 
	        userMap.put("userid", account.getUserId()); 
			return mHelper.getAccountChildDao().queryForFieldValues(userMap);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return childs;
	}
	
	public List<ClassRoom> getClassRoom() {
		List<ClassRoom> classrooms = new ArrayList<ClassRoom>();
		AccountData account = getDefaultAccount();
		try {
		    QueryBuilder<ClassRoom, Integer> contactBuilder = mHelper.getClassRoomData().queryBuilder();
		    contactBuilder.where().eq("loginName", account.getLoginname()).and().eq("joinOrManage", "1");
		    classrooms = contactBuilder.query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classrooms;
	}
	
	public List<ClassRoom> getAllClassRoom() {
		List<ClassRoom> classrooms = new ArrayList<ClassRoom>();
		AccountData account = getDefaultAccount();
		try {
		    QueryBuilder<ClassRoom, Integer> contactBuilder = mHelper.getClassRoomData().queryBuilder();
		    contactBuilder.where().eq("loginName", account.getLoginname());
		    classrooms = contactBuilder.query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classrooms;
	}
	
	public void logout(boolean clearDefaultAccount) {
		if(getDefaultAccount() != null) {
			if(clearDefaultAccount)
				try {
					mHelper.getAccountDao().updateRaw("update AccountData set defaultUser = ?", "2");
				} catch (java.sql.SQLException e) {
					e.printStackTrace();
				}
			mCurAccount = null;
			//notifyAccountChanged();
			NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			if(notificationManager != null) {
				notificationManager.cancel(Consts.CHAT_NOTIFICATION_ID);
			}
		}
		IMClient.getInstance().logOut();
	}
	
	public void notifyAccountChanged() {
		mCurAccount = getDefaultAccount();
	}
	public com.android.volley.toolbox.ImageLoader getNetworkImageLoader() {
		if (mNetworkImageLoader == null) {
			mNetworkImageLoader = new com.android.volley.toolbox.ImageLoader(getRequestQueue(), NativeImageLoader.getInstance());
		}
		return mNetworkImageLoader;
	}
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		VolleyLog.d("添加请求至: %s", TextUtils.isEmpty(tag) ? TAG : tag);
		VolleyLog.d("添加请求至队列: %s", req.getUrl());
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		VolleyLog.d(tag.toString(), "从队列里移除请求");
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	public void cancelAllRequest() {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(new RequestFilter() {
				@Override
				public boolean apply(Request<?> request) {
					String reqUrl = request.getUrl();
					if(reqUrl != null && reqUrl.startsWith(Consts.SERVER_IP)) {
						return true;
					}
					return false;
				}
			});
		}
	}
	
	public File getWorkspace() {
		File file = Environment.getExternalStorageDirectory();
		File workspace = new File(file, Consts.PATH_APP);
		if (!workspace.exists()) {
			workspace.mkdirs();
		}
		return workspace;
	}

	public File getWorkspaceVoice() {
		File workspace = getWorkspace();
		File voideWorkspace = new File(workspace, Consts.PATH_VOICE);
		if (!voideWorkspace.exists()) {
			voideWorkspace.mkdirs();
		}
		return voideWorkspace;
	}
	private void initDESPlus72() {
		try {
			mDESPlus72 = new DESPlus72(Consts.SECRET_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File getWorkspaceImage() {
		File workspace = getWorkspace();
		File imageWorkspace = new File(workspace, Consts.PATH_IMAGE);
		if (!imageWorkspace.exists()) {
			imageWorkspace.mkdirs();
		}
		return imageWorkspace;
	}

	public File getWorkspaceVideo() {
		File workspace = getWorkspace();
		File imageWorkspace = new File(workspace, Consts.PATH_VIDEO);
		if (!imageWorkspace.exists()) {
			imageWorkspace.mkdirs();
		}
		return imageWorkspace;
	}

	public File getWorkspaceDownload() {
		File workspace = getWorkspace();
		File downloadWorkspace = new File(workspace, Consts.PATH_DOWNLOAD);
		if (!downloadWorkspace.exists()) {
			downloadWorkspace.mkdirs();
		}
		return downloadWorkspace;
	}
	
	public File getUploadImageOutputFile() {
		return new File(getWorkspaceImage(), Consts.UPLOAD_IMAGE_FILE);
	}

	public boolean isSDCardAvailable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public boolean isNetworkAvailable() {
		NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isAvailable();
	}
	
	public boolean serviceConnected() {
		return mChatService != null;
	}
	
	public IChatService getChatService() {
		return mChatService;
	}
	
	public void callWhenServiceConnected(Handler target, Runnable callback) {
		Message msg = Message.obtain(target, callback);
		if (serviceConnected()) {
			msg.sendToTarget();
		} else {
			startImServiceIfNeed();
			synchronized (mQueue) {
				mQueue.add(msg);
			}
		}
	}

	public void callWhenServiceConnected(Runnable callback) {
		Message msg = Message.obtain(handler, callback);
		if (serviceConnected()) {
			msg.sendToTarget();
		} else {
			startImServiceIfNeed();
			synchronized (mQueue) {
				mQueue.add(msg);
			}
		}
	}

	public void startImServiceIfNeed() {

		if (!mIsImServiceStarted) {
			Intent intent = new Intent();
			intent.setClass(this, ChatService.class);
			startService(intent);
			bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
			mIsImServiceStarted = true;
		}
	}
	
	public boolean chatServiceReady() {
		try {
			if (serviceConnected() && isNetworkAvailable()
					&& mChatService.ready()) {
				return true;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mChatService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mChatService = IChatService.Stub.asInterface(service);

			synchronized (mQueue) {
				for (Message msg : mQueue) {
					msg.sendToTarget();
				}
				mQueue.clear();
			}
		}
	};
	
	public void stopself(Activity activity) {
		if (serviceConnected()) {
			try {
				getChatService().logout();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		activity.finish();
	}
	
	private void loginIm(final String name, final String userName, final int userType) {
		
		callWhenServiceConnected(new Runnable() {
			@Override
			public void run() {
				try {
					mInstance.getChatService().login(name, userName, userType);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public String encrypt(String s) throws Exception {
		if (mDESPlus72 == null) {
			throw new RuntimeException("DES object is null");
		}
		return mDESPlus72.encrypt(s);
	}

    /**
     * @return the isKickOffDlgShowing
     */
    public boolean isKickOffDlgShowing()
    {
        return isKickOffDlgShowing;
    }

    /**
     * @param isKickOffDlgShowing the isKickOffDlgShowing to set
     */
    public void setKickOffDlgShowing(boolean isKickOffDlgShowing)
    {
        this.isKickOffDlgShowing = isKickOffDlgShowing;
    }

    /**
     * @return the isInLoginActivity
     */
    public boolean isInLoginActivity()
    {
        return isInLoginActivity;
    }

    /**
     * @param isInLoginActivity the isInLoginActivity to set
     */
    public void setInLoginActivity(boolean isInLoginActivity)
    {
        this.isInLoginActivity = isInLoginActivity;
    }

    /**
     * @return the isTakeAttenPhoto
     */
//    public boolean isTakeAttenPhoto()
//    {
//        return isTakeAttenPhoto;
//    }
//
//    /**
//     * @param isTakeAttenPhoto the isTakeAttenPhoto to set
//     */
//    public void setTakeAttenPhoto(boolean isTakeAttenPhoto)
//    {
//        this.isTakeAttenPhoto = isTakeAttenPhoto;
//    }
//
//    /**
//     * @return the localPath
//     */
//    public String getLocalPath()
//    {
//        return localPath;
//    }
//
//    /**
//     * @param localPath the localPath to set
//     */
//    public void setLocalPath(String localPath)
//    {
//        this.localPath = localPath;
//    }
//
//    /**
//     * @return the atPicUrl
//     */
//    public String getAtPicUrl()
//    {
//        return atPicUrl;
//    }
//
//    /**
//     * @param atPicUrl the atPicUrl to set
//     */
//    public void setAtPicUrl(String atPicUrl)
//    {
//        this.atPicUrl = atPicUrl;
//    }

	public Map<String, AttenPicState> getPicStateMap() {
		return picStateMap;
	}

	public void setPicStateMap(Map<String, AttenPicState> picStateMap) {
		this.picStateMap = picStateMap;
	}
	
	public void startIMService() {
		if (mIMChatService != null) {
			mIMChatService.startIMService();
		}
	}
	
	private void bindIMService() {
		Intent chatService = new Intent(getApplicationContext(), IMChatService.class); 
		startService(chatService);
		bindService(chatService, connection, BIND_AUTO_CREATE);
	}
	
	private void unbindIMService() {
		try {
			Intent chatService = new Intent(this, IMChatService.class); 
			if(connection !=null) {
				unbindService(connection);
			}
			stopService(chatService);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void stopIMService() {
		if (mIMChatService != null) {
			mIMChatService.stopIMService();
		}
		clearOlConfig();
		
		cancelAllRequest();
		NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		if(notificationManager != null) {
			notificationManager.cancel(Consts.CHAT_NOTIFICATION_ID);
		}
	}
	
	public void expiredIMToken() {
		if (mIMChatService != null) {
			mIMChatService.stopIMService();
		}
		clearOlConfig();
		if (mIMChatService != null) {
			mIMChatService.startIMService();
		}
	}
	
	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIMChatService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if(service instanceof MsgBinder) 
			mIMChatService = ((MsgBinder)service).getService(); 
		}
	};
	
	public void setActiveBuddy(long currentId, int chatType) {
		if (mIMChatService != null) {
			mIMChatService.setActiveBuddy(currentId, chatType);
		} else {
			LogUtils.e("mIMChatService为空！！！！");
		}
		
	}
	
	public boolean isActiveBuddy(long currentId, int chatType) {
		if (mIMChatService != null) {
			return mIMChatService.isActiveBuddy(currentId, chatType);
		}
		LogUtils.e("mIMChatService为空！！！！");
		return false;
	}

	public OLConfig getOlConfig() {
		if (olConfig == null) {
			try {
				olConfig = DataHelper.getHelper(this).getOLConfigDao().queryBuilder().queryForFirst();
			} catch (java.sql.SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return olConfig;
	}
	
	private void clearOlConfig() {
		try {
			Dao<OLConfig, Integer> olconfigDao = DataHelper.getHelper(this).getOLConfigDao();
			olconfigDao.deleteBuilder().delete();
			olConfig = null;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void setOlConfig(OLConfig olConfig) {
		this.olConfig = olConfig;
		try {
			Dao<OLConfig, Integer> olconfigDao = DataHelper.getHelper(this).getOLConfigDao();
			olconfigDao.deleteBuilder().delete();
			olconfigDao.create(olConfig);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getInChat() {
		return inChat;
	}

	public void setInChat(int inChat) {
		this.inChat = inChat;
	}

	public String getImcomeChatId() {
		return imcomeChatId;
	}

	public void setImcomeChatId(String imcomeChatId) {
		this.imcomeChatId = imcomeChatId;
	}
	public boolean isHasLoginIm() {
		return hasLoginIm;
	}

	public void setHasLoginIm(boolean hasLoginIm) {
		this.hasLoginIm = hasLoginIm;
	}
	
	public String getChatUserId() {
		return chatUserId;
	}

	public void setChatUserId(String chatUserId) {
		this.chatUserId = chatUserId;
	}
}