package com.linkage.mobile72.sh.fragment;

import info.emm.LocalData.DateUnit;
import info.emm.messenger.ChatManager;
import info.emm.messenger.MQ;
import info.emm.messenger.MQ.VYMessage;
import info.emm.messenger.MQ.textMessageBody;
import info.emm.messenger.NotificationCenter;
import info.emm.messenger.NotificationCenter.NotificationCenterDelegate;
import info.emm.messenger.Utilities;
import info.emm.messenger.VYConversation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.ChatActivity;
import com.linkage.mobile72.sh.activity.JxHomeworkDetailActivity2;
import com.linkage.mobile72.sh.activity.SearchGroupActivity;
import com.linkage.mobile72.sh.activity.SearchPersonActivity;
import com.linkage.mobile72.sh.activity.TodayTopicActivity;
import com.linkage.mobile72.sh.activity.WonderExerciseActivity;
import com.linkage.mobile72.sh.activity.im.NewChatActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.data.http.JXBean;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.im.provider.Ws;
import com.linkage.mobile72.sh.im.provider.Ws.MessageTable;
import com.linkage.mobile72.sh.im.provider.Ws.MessageType;
import com.linkage.mobile72.sh.im.provider.Ws.ThreadTable;
import com.linkage.mobile72.sh.imol.service.IPushMessageService;
import com.linkage.mobile72.sh.utils.StringUtil;
import com.linkage.mobile72.sh.widget.ChatListItem;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.lib.database.sqlite.CursorUtils;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.CustomDialog;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshListView;
import com.nostra13.universalimageloader.utils.L;

@SuppressLint("HandlerLeak")
public class NewMessageFragment extends BaseFragment implements
		OnClickListener, IPushMessageService , NotificationCenterDelegate{
	private final static String TAG = "NewMessageFragment";
	private ScheduledExecutorService scheduledExecutorService;

	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();

	public static abstract interface EventHandler {
		public abstract void onMessage(int num);
	}

	
	public static NewMessageFragment create(int titleRes) {
		NewMessageFragment f = new NewMessageFragment();
		// Supply num input as an argument.
		Bundle args = new Bundle();
		// args.putInt("titleRes", titleRes);
		f.setArguments(args);
		return f;
	}

	private int oldPosition = 0;// 记录上一次点的位置
	private int currentItem; // 当前页面

	protected ImageView main_title_left;
	protected TextView main_title_mid;
	protected ImageView main_title_right;
	protected ImageView main_title_right_2;

	float startY;
	float endY;
	Button box, box1;
	public static int pos;
	int itemnum;
	private TextView empty;
	private FrameLayout singleChat;
	private static final String[] THREAD_PORJECTION = { Ws.ThreadTable._ID,
		Ws.ThreadTable.BUDDY_ID, Ws.ThreadTable.BUDDY_NAME, Ws.ThreadTable.MSG_BODY,
		Ws.ThreadTable.MSG_ID, Ws.ThreadTable.MSG_IS_INBOUND,
		Ws.ThreadTable.MSG_OUTBOUND_STATUS,
		Ws.ThreadTable.MSG_RECEIVED_TIME, Ws.ThreadTable.MSG_SENT_TIME,
		Ws.ThreadTable.MSG_TYPE, Ws.ThreadTable.UNREAD_COUNT,
		Ws.ThreadTable.USER_ID, Ws.ThreadTable.CHAT_TYPE,
		Ws.ThreadTable.THREAD_TYPE, Ws.ThreadTable.UNREAD_COUNT };

	/**
	 * 顶部滚动广告需要的
	 */
	//private List<NetworkImageView> adImgs;// 广告图片
	//private ImageView[] indicators;// 圆点指示器
	//private ViewGroup indicatorLayout;// 圆点指示器布局
	//private AdImgAdapter mAdImgAdapter;
	//private ViewPager mImageSwitcher;
	//private View headAdView;
	//private View newjx_layout;

	private PullToRefreshListView mListView;
	private ThreadAdapter mAdapter;
	private Cursor mThreadsCursor;
	/**
	 * Title 抬头布局元素
	 */
	private PopupWindow titlePopup;
	private RelativeLayout mmGroupLayout, mmFriendLayout;
	
	// 活动、话题 小红点
	public static ImageView huodongImageView, huatiImageView;
	
	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		
		
			mThreadsCursor.requery();
			mThreadsCursor.moveToPosition(position - 1);
			int msgtype = mThreadsCursor.getInt(mThreadsCursor
					.getColumnIndexOrThrow(ThreadTable.MSG_TYPE));
			int _id = mThreadsCursor.getInt(mThreadsCursor
					.getColumnIndexOrThrow(ThreadTable._ID));
		
			String buddyId = mThreadsCursor.getString(mThreadsCursor
					.getColumnIndexOrThrow(ThreadTable.BUDDY_ID));
			String buddyName = mThreadsCursor.getString(mThreadsCursor
					.getColumnIndexOrThrow(ThreadTable.BUDDY_NAME));
			switch (msgtype) {
			case MessageType.TYPE_MSG_TEXT:
			case MessageType.TYPE_MSG_AUDIO:
			case MessageType.TYPE_MSG_PIC:
				
				Intent intent = new Intent();
				intent.setClass(getActivity(), ChatActivity.class);
				Bundle bundle = new Bundle();

			
				int chattype = mThreadsCursor.getInt(mThreadsCursor
						.getColumnIndexOrThrow(ThreadTable.CHAT_TYPE));

				
				bundle.putString("chatid", buddyId);
				bundle.putInt("chattype", chattype);
				bundle.putString("name", buddyName);
				LogUtils.v("name" + buddyName);
				bundle.putInt("type", 0);

				intent.putExtra("data", bundle);
				LogUtils.d("starting chat----> buddyId=" + buddyId
						+ " chattype=" + chattype);

				getActivity().startActivity(intent);

				break;
			
			default:
				break;
			}
			if (msgtype != MessageType.TYPE_MSG_TEXT
					|| msgtype != MessageType.TYPE_MSG_AUDIO
					|| msgtype != MessageType.TYPE_MSG_PIC) {
				ContentValues values = new ContentValues();
				values.put(ThreadTable.UNREAD_COUNT, 0);
				getActivity().getContentResolver().update(
						ThreadTable.CONTENT_URI, values,
						ThreadTable._ID + " = " + _id, null);

				updateReadStatus(buddyId);
			}
			getActivity().sendBroadcast(new Intent(ThreadTable.CONTENT_CHAGED));
		}
	};

	private void updateReadStatus(String chatId) {
		ChatManager cm = ChatManager.getInstance();
		VYConversation conver = null;
		VYMessage message;
		int i = 0;

		for (i = 0; i < cm.getCount(); i++) {
			conver = ChatManager.getInstance().getConversationByIndex(i);
			if (StringUtil.equals(conver.getChatID(), chatId)) {
				break;
			}
		}

		if (null != conver && i < cm.getCount()) {
			message = conver.getMessage();
			message.setUnread(false);
			LogUtils.e("updateReadStatus sucess, chatId=" + chatId);
		} else {
			LogUtils.e("updateReadStatus fail conver is null, chatId=" + chatId
					+ "i=" + i + "count=" + cm.getCount());
		}

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initPopup();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtils.e("MessageFragment" + "onCreateView");
		View view = View.inflate(getActivity(), R.layout.chat_history_list, null);
		mListView = (PullToRefreshListView) view
				.findViewById(R.id.base_pull_list);
		initHeaderLayout();
		initHisData();
		return view;
	}
	
	private void initHisData() {
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.MessageArrivalCode);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.SendMessageResponseCode);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.conversionsLoadedComplete);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.processGetUnReadCount);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.delConversationComplete);

		
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		singleChat = (FrameLayout) view.findViewById(R.id.singleChat);
		empty = (TextView) view.findViewById(R.id.empty);

		/*headAdView = View.inflate(getActivity(), R.layout.head_fragment_ads, null); 
		mImageSwitcher = (ViewPager)headAdView.findViewById(R.id.today_topic_layout);
		mImageSwitcher.setVisibility(View.GONE);
		mListView.getRefreshableView().addHeaderView(headAdView);
		newjx_layout = headAdView.findViewById(R.id.newjx_layout);*/
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		BaseApplication.lisenter = this;

		ContentResolver cr = getActivity().getContentResolver();
		mThreadsCursor = cr.query(ThreadTable.CONTENT_URI,
				THREAD_PORJECTION,
				// null,
				ThreadTable.ACCOUNT_NAME + "=?",
				new String[] { String.valueOf(getAccountName()) },
				ThreadTable.MSG_SENT_TIME + " desc");
		mAdapter = new ThreadAdapter(getActivity(), mThreadsCursor);
		mListView.setAdapter(mAdapter);
		mListView.setDivider(null);
		// showempty()
		mListView.setOnItemClickListener(mOnItemClickListener);
		// mListView.setMode(Mode.DISABLED);
		mListView.setMode(Mode.PULL_FROM_START);
		// mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
		// @Override
		// public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// getRollAds();
		// if(!Consts.is_Teacher)
		// getLatestHomework();
		// }
		// });
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				mThreadsCursor.moveToPosition(position-1);
				int msgtype = mThreadsCursor.getInt(mThreadsCursor
						.getColumnIndexOrThrow(ThreadTable.MSG_TYPE));
				if (msgtype == MessageType.TYPE_MSG_TEXT
						|| msgtype == MessageType.TYPE_MSG_AUDIO
						|| msgtype == MessageType.TYPE_MSG_PIC) {
					Log.d("TYPE_MSG_MSG", "TYPE_MSG_MSG:");
					String buddyId = mThreadsCursor.getString(mThreadsCursor
							.getColumnIndexOrThrow(ThreadTable.BUDDY_ID));
					int chattype = mThreadsCursor.getInt(mThreadsCursor
							.getColumnIndexOrThrow(ThreadTable.CHAT_TYPE));
					LogUtils.v("Long Click ======== buddyId:" + buddyId + " chattype:" + chattype);
					cleanMessage(buddyId, chattype);
				}
				return true;
			}
		});

		// cr.registerContentObserver(ThreadTable.CONTENT_URI, true,
		// new ContentObserver(new Handler()) {
		// @Override
		// public void onChange(boolean selfChange) {
		// showempty();
		// }
		// });
		getActivity().registerReceiver(receiver,
				new IntentFilter(ThreadTable.CONTENT_CHAGED));

		// getRollAds();
		/*if (!Consts.is_Teacher)
			getLatestHomework();*/
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(),
		// 6, 6, TimeUnit.SECONDS);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			showempty();
			mAdapter.notifyDataSetChanged();
		}
	};

	// 切换图片
	/*private class ViewPagerTask implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			currentItem = (currentItem + 1) % adImgs.size();
			// LogUtils.i(TAG + "currentItem"+currentItem);
			handler.obtainMessage().sendToTarget();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// LogUtils.i(TAG + "Handler:currentItem"+currentItem);
			mImageSwitcher.setCurrentItem(currentItem);
		}
	};

	// 获取最新的家校互动信息
	private void getLatestHomework() {
		if (getDefaultAccountChild() == null) {
			return;
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getTheLatestOneMessage");
		params.put("studentid", getDefaultAccountChild().getId() + "");
		params.put("smsMessageType", "2,3,14");// 参看接口固定值
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_getTheLatestOneMessage, Request.Method.POST,
				params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						mListView.onRefreshComplete();
						if (response.optInt("ret", -1) == 0) {
							JXBean homework = JXBean
									.parseFromJsonForMessageFragment(response
											.optJSONObject("data"));
							if (homework != null
									&& !TextUtils.isEmpty(""
											+ homework.getMessageContent())) {
								showLastHomeWork(homework);
							} else {
								if (newjx_layout != null) {
									newjx_layout.setVisibility(View.GONE);
								}
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						mListView.onRefreshComplete();
						arg0.printStackTrace();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}*/

	// 获取顶部滚动广告
	// private void getRollAds() {
	// HashMap<String, String> params = new HashMap<String, String>();
	// params.put("commandtype", "getRollAds");
	// params.put("radsType", "2");// 参看接口固定值
	// WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
	// Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
	// @Override
	// public void onResponse(JSONObject response) {
	// mListView.onRefreshComplete();
	// LogUtils.i(TAG + ":response=" + response);
	// if (response.optInt("ret") == 0) {
	// JSONArray array = response.optJSONArray("data");
	// initRollAdView(array);
	// }
	// }
	// }, new Response.ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError arg0) {
	// mListView.onRefreshComplete();
	// StatusUtils.handleError(arg0, getActivity());
	// }
	// });
	// BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	// }

	// 显示最新的家校互动
	/*private void showLastHomeWork(final JXBean jxbean) {
		if(newjx_layout != null){
			newjx_layout.setVisibility(View.VISIBLE);
			newjx_layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.setClass(getActivity(), JxHomeworkDetailActivity2.class);
					i.putExtra("jxbean", jxbean);
					startActivity(i);
				}
			});
		}
		TextView newjx_date = (TextView) headAdView
				.findViewById(R.id.newjx_date);
		TextView newjx_text = (TextView) headAdView
				.findViewById(R.id.newjx_text);

		newjx_date.setText(jxbean.getRecvTime().substring(0, 10));
		newjx_text.setText(jxbean.getMessageContent());
	}


	private void setImageBackground(int selectItems) {
		for (int i = 0; i < indicators.length; i++) {
			if (i == selectItems) {
				indicators[i].setBackgroundResource(R.drawable.dot_select);
			} else {
				indicators[i].setBackgroundResource(R.drawable.dot);
			}
		}
	}

	class AdImgAdapter extends PagerAdapter {
		List<NetworkImageView> views;

		public AdImgAdapter(List<NetworkImageView> imgs) {
			views = imgs;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(views.get(position));
			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return views.size();
		}
	};*/

	@Override
	public void onResume() {
		super.onResume();
		showempty();
		ChatManager.getInstance().loadAllConversations();
		if(mThreadsCursor != null){
			mThreadsCursor.requery();
		}
		mAdapter.notifyDataSetChanged();
//		SharedPreferences sp = BaseApplication.getInstance().getSp();
//		int huodong = sp.getInt("Huodong", 0);
//		int huati = sp.getInt("Huati", 0);
//		NewMessageFragment.huodongImageView.setVisibility(View.GONE);
//		if(huodong == 1 && NewMessageFragment.huodongImageView != null) {
//			NewMessageFragment.huodongImageView.setVisibility(View.VISIBLE);
//		}
//		NewMessageFragment.huatiImageView.setVisibility(View.GONE);
//		if(huati == 1) {
//			NewMessageFragment.huatiImageView.setVisibility(View.VISIBLE);
//		}
	}

	void browerHistoryChat(long buddyId, int chattype, String buddyName) {
		if (chattype == ChatType.CHAT_TYPE_SINGLE) {
			startActivity(NewChatActivity.getIntent(getActivity(), buddyId,
					buddyName, chattype, 0));
		} else {
			ClassRoom group = null;
			DataHelper helper = DataHelper.getHelper(getActivity());
			BaseApplication mBaseApp = BaseApplication.getInstance();
			AccountData account = mBaseApp.getDefaultAccount();
			try {
				QueryBuilder<ClassRoom, Integer> queryBuilder = helper
						.getClassRoomData().queryBuilder();
				queryBuilder.where().eq("loginName", account.getLoginname())
						.and().eq("id", buddyId);
				group = helper.getClassRoomData().queryForFirst(
						queryBuilder.prepare());
				ClassInfoBean bean = new ClassInfoBean();
				/*
				 * bean.setAvatar(group.getAvatar());
				 * bean.setChange_teacherID(String
				 * .valueOf(group.getLeaderId()));
				 * bean.setClassroom_popId(group.getClassroom_popId());
				 * bean.setClassroomId(group.getGroupId());
				 * bean.setClassroomName(group.getGroupName());
				 * bean.setDescription("");
				 */

				startActivity(NewChatActivity.getIntent(getActivity(), buddyId,
						buddyName, chattype, 0));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private void cleanMessage(final String buddyId, final int chattype) {
		System.out.println("========buddyId======chattype===" + buddyId);
		System.out.println("========buddyId======chattype===" + chattype);
		new CustomDialog(getActivity())
				.setTitle("删除")
				.setMessage("确定删除该聊天以及所有聊天记录?")
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						getActivity().getContentResolver().delete(
								MessageTable.CONTENT_URI,
								MessageTable.ACCOUNT_NAME + "=? AND "
										+ MessageTable.BUDDY_ID + "=? AND "
										+ MessageTable.CHAT_TYPE + "=?",
								new String[] { getAccountName(),
										String.valueOf(buddyId),
										String.valueOf(chattype) });
						String msgtype = "(" + MessageType.TYPE_MSG_TEXT + ","
								+ MessageType.TYPE_MSG_PIC + ","
								+ MessageType.TYPE_MSG_AUDIO + ")";

						getActivity().getContentResolver().delete(
								ThreadTable.CONTENT_URI,
								ThreadTable.ACCOUNT_NAME + "=? AND "
										+ ThreadTable.BUDDY_ID + "=? AND "
										+ ThreadTable.MSG_TYPE + " in "
										+ msgtype,
								new String[] { getAccountName(),
										String.valueOf(buddyId) });
						getActivity().sendBroadcast(
								new Intent(ThreadTable.CONTENT_CHAGED));
						// main_title.setBackgroundResource(SkinManager
						// .getinstance(mSchoolApp).getCurrentSkinRes(
						// R.id.main_title));
						// int currenttheme = SkinManager.getCurrentTheme();
						// if (theme != currenttheme) {
						// theme = currenttheme;
						// main_title.setBackgroundResource(SkinManager
						// .getinstance(mSchoolApp).getCurrentSkinRes(
						// R.id.main_title));
						// SkinManager.SetBackground(getActivity(),
						// R.id.app_bg);
						// }
						if(chattype == ChatType.CHAT_TYPE_GROUP){
							ChatManager.getInstance().deleteConversation(buddyId, true);
						} else {
							ChatManager.getInstance().deleteConversation(buddyId, false);
						}
						showempty();
						mAdapter.notifyDataSetChanged();
						dialog.dismiss();
						// finish();

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	private void showempty() {
		// 目前只在改广告，加入了消息后，需要调整下显隐逻辑
		// if (mAdapter.getCount() > 0) {
		// mListView.setVisibility(View.VISIBLE);
		// singleChat.setVisibility(View.GONE);
		// empty.setVisibility(View.GONE);
		// } else {
		// mListView.setVisibility(View.GONE);
		// singleChat.setVisibility(View.VISIBLE);
		// empty.setVisibility(View.VISIBLE);
		// }
	}

	private final class ThreadAdapter extends CursorAdapter {

		private LayoutInflater mLayoutInflater;

		@SuppressWarnings("deprecation")
		public ThreadAdapter(Context context, Cursor c) {
			super(context, c);
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			((ChatListItem) view).bindView(cursor);
			// final Button chat_delete = (Button)
			// view.findViewById(R.id.chat_delete);
			// chat_delete.setVisibility(View.GONE);
		}

		@Override
		public View newView(final Context context, Cursor cursor,
				ViewGroup parent) {
			ChatListItem listItem = (ChatListItem) mLayoutInflater.inflate(
					R.layout.chat_history_list_item, parent, false);
			listItem.init(cursor);

			return listItem;
		}
	}

	/**
	 * 初始化头部布局
	 */
	private void initHeaderLayout() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_new_message_header, null);
		mListView.getRefreshableView().addHeaderView(view);
		LinearLayout wonderExerciseLayout = (LinearLayout) view.findViewById(R.id.wonder_exercise_layout);
		huodongImageView = (ImageView)wonderExerciseLayout.findViewById(R.id.unread_iv);
		LinearLayout todayTopicLayout = (LinearLayout) view.findViewById(R.id.today_topic_layout);
		huatiImageView = (ImageView)todayTopicLayout.findViewById(R.id.unread_iv1);
		wonderExerciseLayout.setOnClickListener(this);
		todayTopicLayout.setOnClickListener(this);
	}

	/**
	 * 初始化弹窗
	 */
	@SuppressWarnings("deprecation")
	private void initPopup() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.title_popup, null);
		titlePopup = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		titlePopup.setBackgroundDrawable(new BitmapDrawable());
		titlePopup.setOutsideTouchable(true);
		mmGroupLayout = (RelativeLayout) view
				.findViewById(R.id.mm_title_group_layout);
		mmFriendLayout = (RelativeLayout) view
				.findViewById(R.id.mm_title_friend_layout);
		mmGroupLayout.setOnClickListener(this);
		mmFriendLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent mIntent = null;
		switch (v.getId()) {
		case R.id.mm_title_group_layout:
			if (titlePopup.isShowing())
				titlePopup.dismiss();
			Intent jiaGroupIntent = new Intent(getActivity(),
					SearchGroupActivity.class);
			startActivity(jiaGroupIntent);
			break;
		case R.id.mm_title_friend_layout:
			if (titlePopup.isShowing())
				titlePopup.dismiss();
			Intent jiaFriendIntent = new Intent(getActivity(),
					SearchPersonActivity.class);
			startActivity(jiaFriendIntent);
			break;
		case R.id.wonder_exercise_layout:
			mIntent = new Intent(getActivity(), WonderExerciseActivity.class);
			startActivity(mIntent);
			break;
		case R.id.today_topic_layout:
			mIntent = new Intent(getActivity(), TodayTopicActivity.class);
			startActivity(mIntent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		BaseApplication.lisenter = null;
		if (getActivity() != null) {
			getActivity().unregisterReceiver(receiver);
		}
	}

	@Override
	public void updatePushMessage(String json) {
		JSONObject obj;
		try {
			obj = new JSONObject(json);
			int type = obj.optInt("type");
			String desc = obj.optString("desc");
			SharedPreferences sp = BaseApplication.getInstance().getSp();
			Editor ed = sp.edit();
			if(type == 2) {
				ed.putInt("Huati", 1);
				if(NewMessageFragment.huatiImageView != null) {
					NewMessageFragment.huatiImageView.setVisibility(View.VISIBLE);
				}
				ed.commit();
			}else if(type == 3) {
				ed.putInt("Huodong", 1);
				if(NewMessageFragment.huodongImageView != null) {
					NewMessageFragment.huodongImageView.setVisibility(View.VISIBLE);
				}
				ed.commit();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void didReceivedNotification(int id, Object... args) {
		Utilities.HideProgressDialog(getActivity());
		if (id == NotificationCenter.MessageArrivalCode) {
			VYMessage MSG = (VYMessage) args[0];
			LogUtils.i("did rev msg MessageArrivalCode---------------------->");


			printData(1);
			updateHistoryData();
		
			updateUI();

		} else if (id == NotificationCenter.SendMessageResponseCode) {
			LogUtils.i("did SendMessageResponseCode----------------------<<");
			printData(2);

		} else if (id == NotificationCenter.conversionsLoadedComplete) {
			LogUtils.i("did conversionsLoadedComplete---------------------->> count="
					+ ChatManager.getInstance().getCount());
			printData(3);

			updateHistoryData();
		
			updateUI();
		} else if (id == NotificationCenter.delConversationComplete) {
			LogUtils.i("did delConversationComplete----------------------<<");
			printData(4);
		} else if (id == NotificationCenter.processGetUnReadCount) {
			LogUtils.i("did delConversationComplete----------------------<<");
			printData(5);
		} else {
			LogUtils.i("did un listening notify!!! id=" + id);
		}

		// mAdapter.notifyDataSetChanged();
		// if (null != getActivity()) {
		// LogUtils.e("chat------->send update broadcast!!!");
		// getActivity().sendBroadcast(new Intent(ThreadTable.CONTENT_CHAGED));
		// } else {
		// LogUtils.e("chat------->newMessageFragment, activity is null!!!");
		// }
	}
	
	private void updateUI() {
		if (null != getActivity()) {
			ContentResolver cr = getActivity().getContentResolver();
			mThreadsCursor = cr.query(ThreadTable.CONTENT_URI, THREAD_PORJECTION,
					ThreadTable.ACCOUNT_NAME + "=?",
					new String[] { String.valueOf(getAccountName()) },
					ThreadTable.THREAD_TYPE + " asc,"
							+ ThreadTable.MSG_RECEIVED_TIME + " desc");
			
			// mThreadsCursor.moveToFirst();
			mAdapter = new ThreadAdapter(getActivity(), mThreadsCursor);
			mListView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			if (null != getActivity()) {
				LogUtils.e("chat------->send update broadcast!!!");
				getActivity().sendBroadcast(new Intent(ThreadTable.CONTENT_CHAGED));
			} else {
				LogUtils.e("chat------->newMessageFragment, activity is null!!!");
			}
		} else {
			LogUtils.e("chat------->newMessageFragment, activity is null, just requery!!");
			mThreadsCursor.requery();
			mAdapter.notifyDataSetChanged();
		}
		
	}

	private void updateHistoryData() {
		ChatManager cm = ChatManager.getInstance();
		// String content = "";
		VYMessage message;
		VYConversation conver;

		ContentResolver resolver;

		if (null != getActivity()) {
			resolver = getActivity().getContentResolver();
			if (null == resolver) {
				LogUtils.e("NeMessageFragment, resolver is null");
				return;
			}

		} else {
			LogUtils.e("NeMessageFragment, getActivity() =" + getActivity());
			return;
		}

		// Cursor cr;
		String accountName = getAccountName();
		int chatType;
		ContentValues cv;
		String head;
		head = Consts.APP_ID;
		String chatId;
		Long id;
		Contact contact = null;
		ClassRoom classRoom = null;
				
		for (int i = 0; i < cm.getCount(); i++) {
			conver = ChatManager.getInstance().getConversationByIndex(i);
			message = conver.getMessage();
			cv = new ContentValues();

			cv.put(ThreadTable.ACCOUNT_NAME, accountName);
			cv.put(ThreadTable.BUDDY_ID, conver.getChatID());
			chatId = message.getFrom();
			
			if (VYMessage.Type.TEXT == message.getType()) {
				cv.put(MessageTable.BODY,
						((MQ.textMessageBody) message.getBody()).getMessage());
			}

			// cv.put(ThreadTable.MSG_RECEIVED_TIME, (int) conver.getDate());
			if (StringUtil.equals(message.getFrom(), getChatAccount())) {
				cv.put(MessageTable.IS_INBOUND, MessageType.OUTGOINT);
				cv.put(ThreadTable.UNREAD_COUNT, 0);
				cv.put(MessageTable.SENT_TIME, (long) message.getMsgTime());
				LogUtils.e("i=" + i + "chatid=" + conver.getChatID()
						+ ", from=" + message.getFrom() + " chatAccount="
						+ getChatAccount() + " outgoing msg, unread=0"
						+ " time="
						+ DateUnit.getMMddFormat1((long) message.getMsgTime()));
			} else {
				cv.put(MessageTable.IS_INBOUND, MessageType.INCOMING);
				cv.put(ThreadTable.UNREAD_COUNT, conver.getUnReadCount());
				cv.put(MessageTable.RECEIVED_TIME, (long) message.getMsgTime());
				LogUtils.e("i=" + i + "chatid=" + conver.getChatID()
						+ ", from=" + message.getFrom() + " chatAccount="
						+ getChatAccount() + " incoming msg, unread="
						+ conver.getUnReadCount() + " time="
						+ DateUnit.getMMddFormat1((long) message.getMsgTime())
						+ " long time=" + (long) message.getMsgTime());
			}

			LogUtils.e("unread=" + conver.getUnReadCount());

			switch (conver.getChatType()) {
			case CHATTYPE_GROUP:
				chatType = ChatType.CHAT_TYPE_GROUP;
				cv.put(ThreadTable.THREAD_TYPE, 0);
				// cv.put(ThreadTable.BUDDY_ID,
				// Integer.valueOf(conver.getChatID()));
				break;
			case CHATTYPE_SINGLE:
				chatType = ChatType.CHAT_TYPE_SINGLE;
				cv.put(ThreadTable.THREAD_TYPE, 5);
				// cv.put(ThreadTable.BUDDY_ID, Integer.valueOf("10000005"));
				break;
			default:
				chatType = ChatType.CHAT_TYPE_SINGLE;
				cv.put(ThreadTable.THREAD_TYPE, 5);
				// cv.put(ThreadTable.BUDDY_ID, Integer.valueOf("10000005"));
				break;
			}
			
//			if (chattype == ChatType.CHAT_TYPE_GROUP) {
//				head = Consts.APP_ID0;
//			}
//			if (chatId.length() <= head.length()) {
//				LogUtils.e("WARNING!!!!!===========ChatActivity, invalid chatId! chatId=" + chatId);
//				continue;
//			} else {
//				id = Long.parseLong(chatId.substring(head.length(),
//						chatId.length()));
//			}
//			
//			
//			if(contact == null){
//				LogUtils.v("WARNING!!!!!===========contact == null");
//				continue;
//			}
//			cv.put(ThreadTable.BUDDY_NAME, contact.getName());
			LogUtils.v("=============chatType : " + chatType);
			
			// 过滤掉----非当前用户通讯录中的、非当前用户聊天群组的消息，不执行插入数据库
			if (chatType == ChatType.CHAT_TYPE_SINGLE) {
				// 来自IM的单人聊天，去掉前缀"1000_"
				long userId = Long.parseLong(message.getFrom().substring(Consts.APP_ID.length()));
				if(message.getDirect() == VYMessage.Direct.SEND)
				{
					userId = Long.parseLong(message.getTo().substring(Consts.APP_ID.length(),
							message.getFrom().length()));
				}
				
				LogUtils.v("=============continue userId"+userId);
				contact = getContact(accountName, userId);
				
				if (null == contact) {
					LogUtils.v("=============continue Single");
					continue;
				}
				LogUtils.v("=============continue contact.getName()"+contact.getName());
				cv.put(ThreadTable.BUDDY_NAME, contact.getName());
			} else if (chatType == ChatType.CHAT_TYPE_GROUP) {
				// 来自IM的群组聊天，去掉前缀"1000"
				long groupId = Long.parseLong(message.getTo().substring(Consts.APP_ID0.length()));
				classRoom = getClassroom(accountName, groupId);
				if (null == classRoom) {
					LogUtils.v("=============continue Group:" + groupId);
					continue;
				}
				cv.put(ThreadTable.BUDDY_NAME, classRoom.getName());
			}
			cv.put(ThreadTable.CHAT_TYPE, chatType);

			switch (message.getType()) {
			case TEXT:
				chatType = Ws.MessageType.TYPE_MSG_TEXT;
				break;
			case IMAGE:
				chatType = Ws.MessageType.TYPE_MSG_PIC;
				break;
			case VOICE:
				chatType = Ws.MessageType.TYPE_MSG_AUDIO;
				break;
			case DOCUMENT:
				chatType = Ws.MessageType.TYPE_MSG_FILE;
				break;
			default:
				break;
			}
			cv.put(MessageTable.TYPE, chatType);

			resolver.insert(ThreadTable.CONTENT_URI, cv);
		}

	}
	
	private ClassRoom getClassroom(String accountName, Long id){
		DataHelper helper = getDBHelper();
		DataHelper.getHelper(getActivity());
		try {
			QueryBuilder<ClassRoom, Integer> classroomBuilder = helper.getClassRoomData()
					.queryBuilder();
			classroomBuilder.where()
					.eq("loginName", accountName).and().eq("taskid", id);;
			return classroomBuilder.queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//
	private Contact getContact(String accountName, Long id) {
		Contact contact = null;
		DataHelper helper = DataHelper.getHelper(getActivity());
		try {
			QueryBuilder<Contact, Integer> queryBuilder = helper.getContactData().queryBuilder();
			queryBuilder.where().eq("loginName", accountName).and().eq("id", id);  
			contact = helper.getContactData().queryForFirst(queryBuilder.orderBy("usertype", true).prepare());
		} catch (SQLException e) {
			e.printStackTrace();
			LogUtils.v("WARNING!!!!!===========SQLException");
		} 
		return contact;
	}
	private void printData(int type) {
		LogUtils.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>....type=" + type);
		ChatManager cm = ChatManager.getInstance();
		String content = "";
		VYMessage message;
		VYConversation conver;

		for (int i = 0; i < cm.getCount(); i++) {
			conver = ChatManager.getInstance().getConversationByIndex(i);
			message = conver.getMessage();

			switch (ChatManager.getInstance().getConversationByIndex(i)
					.getMessage().getType()) {
			case TEXT:
				MQ.textMessageBody messageBody = (textMessageBody) message
						.getBody();
				content = messageBody.getMessage();
				break;
			case IMAGE:
				content = "图片 " + message.getAttachFileName();
				break;
			case VOICE:
				content = "声音 " + message.getAttachFileName();
				break;
			case DOCUMENT:
				content = "文件 " + message.getAttachFileName();
				break;
			default:
				break;
			}
			LogUtils.e("converID=" + conver.getChatID() + ", chatype="
					+ conver.getChatType().ordinal() + ", chatype2="
					+ conver.getChatType() + ", from=" + message.getFrom()
					+ ", to=" + message.getTo() + ", unRead="
					+ cm.getConversationByIndex(i).getUnReadCount()
					+ ", content=" + content);
		}
	}
}