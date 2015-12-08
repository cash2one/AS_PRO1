package com.linkage.mobile72.sh.activity;

import info.emm.messenger.ChatManager;
import info.emm.messenger.IMClient;
import info.emm.messenger.MQ;
import info.emm.messenger.MQ.VYMessage;
import info.emm.messenger.MediaController;
import info.emm.messenger.NotificationCenter;
import info.emm.messenger.NotificationCenter.NotificationCenterDelegate;
import info.emm.messenger.VYConversation;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.sql.DataSource;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.im.NewChatActivity;
import com.linkage.mobile72.sh.adapter.ExpressionAdapter;
import com.linkage.mobile72.sh.adapter.IMMessageListAdapter;
import com.linkage.mobile72.sh.app.ActivityList;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.im.provider.Ws;
import com.linkage.mobile72.sh.im.provider.Ws.MessageTable;
import com.linkage.mobile72.sh.im.provider.Ws.MessageType;
import com.linkage.mobile72.sh.im.provider.Ws.ThreadTable;
import com.linkage.mobile72.sh.utils.ActivityUtils;
import com.linkage.mobile72.sh.utils.BitmapUtils;
import com.linkage.mobile72.sh.utils.CommonUtils;
import com.linkage.mobile72.sh.utils.FaceUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.utils.FaceUtils.Face;
import com.linkage.mobile72.sh.utils.ImageUtils;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.SmileUtils;
import com.linkage.mobile72.sh.utils.StringUtil;
import com.linkage.mobile72.sh.widget.CustomListView;
import com.linkage.mobile72.sh.widget.CustomListView.OnRefreshListener;
import com.linkage.mobile72.sh.widget.ExpandGridView;
import com.linkage.mobile72.sh.widget.FacePanelView;
import com.linkage.mobile72.sh.widget.PasteEditText;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.lib.util.LogUtils;

//public class ChatActivity extends BaseActivity
public class ChatActivity extends BaseActivity implements OnClickListener,
		NotificationCenterDelegate {
	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;

	public static final int FROM_CHAT = 1;

	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_FILE = 10;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_PICK_VIDEO = 12;
	public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
	public static final int REQUEST_CODE_VIDEO = 14;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;
	public static final int REQUEST_CODE_SELECT_VIDEO = 23;
	public static final int REQUEST_CODE_SELECT_FILE = 24;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	public static final int RESULT_CODE_TO_CLOUD = 6;
	public static final int RESULT_CODE_EXIT_GROUP = 7;

	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;

	public static final String COPY_IMAGE = "EASEMOBIMG";

	private static final int RECORD_NO = 0; // 不在录音
	private static final int RECORD_ING = 1; // 正在录音
	private static final int RECODE_ED = 2; // 完成录音

	private static int RECORD_STATE = RECORD_NO; // 录音的状态
	private DataHelper helper;
	private View recordingContainer;
	private ImageView micImage;
	private TextView recordingHint;
	private CustomListView listView;
	private PasteEditText mEditTextContent;
	private View buttonSetModeKeyboard;
	private View buttonSetModeVoice;
	private View buttonSend;
	private View buttonPressToSpeak;
	private LinearLayout btnContainer;
	
	private View more;
	private int position;
	private ClipboardManager clipboard;
	private InputMethodManager manager;
	private List<String> reslist;
	private Drawable[] micImages;
	private int chatType;
	public static ChatActivity activityInstance = null;
	// 给谁发送消息
	private String toChatUsername;
	private File cameraFile;
	static int resendPos;

	// private GroupListener groupListener;

	private ImageView iv_emoticons_normal;
	private ImageView iv_emoticons_checked;
	private RelativeLayout edittext_layout;
	private ProgressBar loadmorePB;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private Button btnMore;
	public String playMsgId;
	private String chatId;
	private long chatIdLg = 0;
	// private MarqueeText txt_chatid;
	private TextView txt_chatid;
	// private boolean isAlive;
	VYConversation conver;
	private VYMessage sendMsg = null;
	private String head;
	
	private RelativeLayout top_bar;

	private ArrayList<String> choosePics = new ArrayList<String>();

	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			LogUtils.d("mic get msg, what=" + msg.what);
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};
	// private EMGroup group;
	IMMessageListAdapter adapter;
	// xiaoyang
	// List<MQ.VYMessage> messages = new ArrayList<MQ.VYMessage>();
	MediaController controller = MediaController.getInstance();
	int chattype;
	private View face_panel;
//	private ClazzWorkContact mContact;
	private Contact mContact;
	private DataSource mDataSource;
	private String groupName = "";
	private int from = 0;
	private String singleName = "";
	protected ImageView moreIcon;
	private TextView chatTip, moreText;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat2);
		ActivityList.activitys.add(this);

//		mDataSource = mApp.getDataSource();
		helper = DataHelper.getHelper(ChatActivity.this);
		initProcess(getIntent());

	}

	private void initProcess(Intent intent) {

		if (null == intent) {
			LogUtils.e("intent is null, finish!!!");
			finish();
			return;
		}

		Bundle bundle = intent.getBundleExtra("data");
		chatId = bundle.getString("chatid");
		System.out.println("chatid=" + chatId);
		chattype = bundle.getInt("chattype");
		System.out.println("chattype=" + chattype);
		from = bundle.getInt("type");
		System.out.println("from=" + from);
		singleName = bundle.getString("name");
		System.out.println("singleName=" + singleName);

		//右上角详情===========================
		this.moreIcon = (ImageView) findViewById(R.id.title_more_icon);
		this.moreIcon.setOnClickListener(this);
		this.moreText = (TextView) findViewById(R.id.title_more_txt);
		this.moreText.setOnClickListener(this);

		if (from == 1) {
			chatIdLg = Long.parseLong(chatId);
			if (chattype == ChatType.CHAT_TYPE_SINGLE) {
				chatId = Consts.APP_ID + chatId;
			} else {
				chatId = Consts.APP_ID0 + chatId;
			}

			LogUtils.e("from contact add head, chatId=" + chatId);
			moreIcon.setVisibility(View.GONE);
		} else {
			head = Consts.APP_ID;
			if (chattype == ChatType.CHAT_TYPE_GROUP) {
				head = Consts.APP_ID0;
			}
			if (chatId.length() <= head.length()) {
				// txt_chatid.setText(chatId);
				LogUtils.e("ChatActivity, invalid chatId! chatId=" + chatId);
			} else {
				Long id = Long.parseLong(chatId.substring(head.length(),
						chatId.length()));
				chatIdLg = id.longValue();
			}
		}

		LogUtils.e("from=" + from + ", chatId=" + chatId);

		conver = ChatManager.getInstance().getConversation(chatId,
				typeConvert(chattype));

		FaceUtils.install(this, new FacePanelView.OnFaceClickListener() {

			@Override
			public void onFaceClick(Face face) {
				mEditTextContent.append(FaceUtils.replaceFace(
						ChatActivity.this, face.text));
			}
		});

		initView();
		setUpView();

		//右上角显示
		if (ChatType.CHAT_TYPE_GROUP == chattype) {
			moreText.setVisibility(View.GONE);
			adapter = new IMMessageListAdapter(this, conver, chattype, mContact);
		} else {
			moreIcon.setVisibility(View.GONE);
			adapter = new IMMessageListAdapter(this, conver, chattype, null);
		}

		listView.setAdapter(adapter);
		conver.loadMessages(20);
		conver.setReadState(true);

		MediaController.getInstance().SetPlayoutSpeaker(true);
	}

	private MQ.VYMessage.ChatType typeConvert(int type) {

		MQ.VYMessage.ChatType vyType = MQ.VYMessage.ChatType.CHATTYPE_GROUP;

		if (type == ChatType.CHAT_TYPE_SINGLE) {
			vyType = MQ.VYMessage.ChatType.CHATTYPE_SINGLE;
		}

		LogUtils.d("chat oncreate----> type=" + type + " vyType="
				+ vyType.name());

		return vyType;
	}

	private void setupTitle() {

		if (chatId.length() <= head.length()) {
			txt_chatid.setText(chatId);
			LogUtils.e("ChatActivity, invalid chatId! chatId=" + chatId);
		} else {

			try {
				if (chattype == ChatType.CHAT_TYPE_SINGLE) {
					// if (1 == from && !(StringUtils.isEmpty(singleName))) {
					// txt_chatid.setText(singleName);
					// LogUtils.e("------> use singleName=" + singleName);
					// } else {
//					mContact = getContact(chatIdLg);
//					LogUtils.e("^^^^^^^^^^chatIdLg^^^^^^^" + chatIdLg);
//					if (mContact != null) {
//						txt_chatid.setText(mContact.getname());
//						LogUtils.e("Single contact name" + mContact.getname());
//						// }
//					}
					String loginName = BaseApplication.getInstance()
							.getDefaultAccount().getLoginname();
					try {
						QueryBuilder<Contact, Integer> contactBuilder = helper
								.getContactData().queryBuilder();
						contactBuilder.where().eq("loginName", loginName).and()
								.eq("id", chatIdLg);
						List<Contact> mContacts = contactBuilder.query();
						if (mContacts != null && mContacts.size() > 0) {
							Contact contact = (Contact) mContacts.get(0);
							txt_chatid.setText(mContact.getName());
						} else {	
							txt_chatid.setText(mContact.getName());
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}

				} else {
					ClassRoom group = null;
					String loginName = BaseApplication.getInstance()
							.getDefaultAccount().getLoginname();
					try {
						QueryBuilder<ClassRoom, Integer> queryBuilder = helper
								.getClassRoomData().queryBuilder();
						queryBuilder.where().eq("loginName", loginName)
								.and().eq("id", chatIdLg);
						group = helper.getClassRoomData().queryForFirst(
								queryBuilder.prepare());
						if(group == null)
						{
							Toast.makeText(ChatActivity.this, "此群组已不存在",
									Toast.LENGTH_SHORT).show();
						}else{
							groupName = group.getName();
							txt_chatid
									.setText(group.getSchoolName() + group.getName());
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

//	private ClazzWorkContact getContact(long buddyId) {
//		return mDataSource.getContactById(mApp.getDefaultAccount()
//				.getLoginname(), buddyId);
//
//	}

	protected void initView() {
		top_bar = (RelativeLayout) findViewById(R.id.top_bar);
		recordingContainer = findViewById(R.id.recording_container);
		micImage = (ImageView) findViewById(R.id.mic_image);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		listView = (CustomListView) findViewById(R.id.list);
		mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
		buttonSend = findViewById(R.id.btn_send);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
		// expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		// emojiIconContainer = (LinearLayout)
		// findViewById(R.id.ll_face_container);
		face_panel = findViewById(R.id.face_panel);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
		// locationImgview = (ImageView) findViewById(R.id.btn_location);
		iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
		iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		btnMore = (Button) findViewById(R.id.btn_more);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		more = findViewById(R.id.more);
		edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
		//txt_chatid = (MarqueeText) findViewById(R.id.name);
		txt_chatid = (TextView) findViewById(R.id.name);
		chatTip = (TextView) findViewById(R.id.chat_tip);
		// txt_chatid.setText(conver.getChatID());
		
		if(isTeacher()) {
			top_bar.setBackgroundResource(R.drawable.title_top_bg_green);
		}else {
			top_bar.setBackgroundResource(R.drawable.title_top_bg);
		}
		
		if(!StringUtil.isNullOrEmpty(singleName) && (chattype == ChatType.CHAT_TYPE_SINGLE)) {
			LogUtils.v("chatType ======== :" + chatType);
			txt_chatid.setText(singleName);
		} else if(1 == from){
			txt_chatid.setText(chatIdLg + "");
		} else {
			setupTitle();
		}

		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] {
				getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };
		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		// View gv1 = getGridChildView(1);
		// View gv2 = getGridChildView(2);
		// views.add(gv1);
		// views.add(gv2);
		// expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		edittext_layout.requestFocus();
		// voiceRecorder = new VoiceRecorder(micImageHandler);
		// controller.setApplicationHandler(micImageHandler);
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());

		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edittext_layout
							.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					edittext_layout
							.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}

			}
		});
		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edittext_layout
						.setBackgroundResource(R.drawable.input_bar_bg_active);
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				// emojiIconContainer.setVisibility(View.GONE);
				if (face_panel.getVisibility() == View.VISIBLE) {
					face_panel.setVisibility(View.GONE);
				}
				btnContainer.setVisibility(View.GONE);
			}
		});
		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
				
				LogUtils.i("-->mEditTextContent, onTextChanged, s=" + s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
				LogUtils.i("-->mEditTextContent, beforeTextChanged, s=" + s);
			}

			@Override
			public void afterTextChanged(Editable s) {
				LogUtils.i("-->mEditTextContent, afterTextChanged, s=" + s);
//				if (s.toString().contains("#")) {
//					mEditTextContent.setText(FaceUtils.replaceFace(
//							ChatActivity.this, s.toString()));
//					
//				}

			}
		});

		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				try {
					if (conver.getMessageCount() == 0) {
						conver.loadMessages(20);
					} else {
						conver.loadMoreMessages(20);
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				listView.onRefreshComplete();

			}
		});
	}

	private void setUpView() {
		iv_emoticons_normal.setOnClickListener(this);
		iv_emoticons_checked.setOnClickListener(this);

		// position = getIntent().getIntExtra("position", -1);
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		// 群聊
		// findViewById(R.id.container_to_group).setVisibility(View.VISIBLE);
		// findViewById(R.id.container_remove).setVisibility(View.GONE);
		findViewById(R.id.container_voice_call).setVisibility(View.GONE);
		findViewById(R.id.container_video_call).setVisibility(View.GONE);

		// 监听当前会话的群聊解散被T事件
		// groupListener = new GroupListener();
		// EMGroupManager.getInstance().addGroupChangeListener(groupListener);

		// show forward message if the message is not null
		String forward_msg_id = getIntent().getStringExtra("forward_msg_id");

		// conversation =
		// EMChatManager.getInstance().getConversation(toChatUsername);
		// // 把此会话的未读数置为0
		// conversation.resetUnreadMsgCount();
	}

	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void setModeVoice(View view) {
		hideKeyboard();
		edittext_layout.setVisibility(View.GONE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.VISIBLE);
		buttonSend.setVisibility(View.GONE);
		btnMore.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.VISIBLE);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		btnContainer.setVisibility(View.VISIBLE);
		// emojiIconContainer.setVisibility(View.GONE);
		if (face_panel.getVisibility() == View.VISIBLE) {
			face_panel.setVisibility(View.GONE);
		}
		
		scrollListViewToBottom();

	}

	/**
	 * 点击文字输入框
	 * 
	 * @param v
	 */
	public void editClick(View v) {
		listView.setSelection(listView.getCount() - 1);
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * 显示或隐藏图标按钮页
	 * 
	 * @param view
	 */
	public void more(View view) {
		if (more.getVisibility() == View.GONE) {
			System.out.println("more gone");
			hideKeyboard();
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			// emojiIconContainer.setVisibility(View.GONE);
			if (face_panel.getVisibility() == View.VISIBLE) {
				face_panel.setVisibility(View.GONE);
			}
		} else {
			// if (emojiIconContainer.getVisibility() == View.VISIBLE) {
			// emojiIconContainer.setVisibility(View.GONE);
			if (face_panel.getVisibility() == View.VISIBLE) {
				face_panel.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
			} else {
				more.setVisibility(View.GONE);
			}

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (more.getVisibility() == View.VISIBLE
					|| face_panel.getVisibility() == View.VISIBLE) {
				more.setVisibility(View.GONE);
				face_panel.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 显示键盘图标
	 * 
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeVoice.setVisibility(View.VISIBLE);
		mEditTextContent.requestFocus();
		buttonPressToSpeak.setVisibility(View.GONE);
		if (TextUtils.isEmpty(mEditTextContent.getText())) {
			btnMore.setVisibility(View.VISIBLE);
			buttonSend.setVisibility(View.GONE);
		} else {
			btnMore.setVisibility(View.GONE);
			buttonSend.setVisibility(View.VISIBLE);
		}
		
		scrollListViewToBottom();

	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 * @param isResend
	 *            boolean resend
	 */
	private void sendText(String content) {

		if (StringUtil.isNullOrEmpty(content)) {
			LogUtils.d("can not send empty txt msg!!");
			return;
		}
		if (content.length() > 0) {
			try {
				MQ.VYMessage message = MQ.VYMessage
						.createSendMessage(MQ.VYMessage.Type.TEXT);
				MQ.VYMessageBody msgbody = new MQ.textMessageBody(content);
				message.addBody(msgbody);
				if (conver.getChatType() == VYMessage.ChatType.CHATTYPE_GROUP) {
					message.setChatType(VYMessage.ChatType.CHATTYPE_GROUP);
					System.out.println("group");
				} else {
					message.setChatType(VYMessage.ChatType.CHATTYPE_SINGLE);
				}
				System.out.println("sendTextChatId=" + chatId
						+ " conver chatid=" + conver.getChatID());
				// message.setTo(conver.getChatID());
				message.setTo(conver.getChatID());
				sendMsg = message;
				IMClient.getInstance().SendMessage(message);
				mEditTextContent.setText("");
				adapter.notifyDataSetChanged();
				// listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
				// listView.setStackFromBottom(true);
				scrollListViewToBottom();
				
				if (chatTip.VISIBLE == View.VISIBLE) {
					chatTip.setVisibility(View.GONE);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	private void scrollListViewToBottom() {
		// if (adapter.getCount() > 1) {
		// listView.setSelection(adapter.getCount() - 1);
		// }
	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			String st = getResources().getString(
					R.string.sd_card_does_not_exist);
			Toast.makeText(getApplicationContext(), st, 0).show();
			return;
		}

		// cameraFile = new File(PathUtil.getInstance().getImagePath(),
		// System.currentTimeMillis() + ".jpg");
		cameraFile = new File(mApp.getWorkspaceImage().toString(),
				System.currentTimeMillis() + ".jpg");
		cameraFile.mkdirs();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this,
				1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class
									.forName("com.linkage.mobile72.sh.utils.SmileUtils");
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(
									ChatActivity.this, (String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {

								int selectionStart = mEditTextContent
										.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditTextContent.getText()
											.toString();
									String tempStr = body.substring(0,
											selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i,
												selectionStart);
										if (SmileUtils.containsKey(cs
												.toString()))
											mEditTextContent.getEditableText()
													.delete(i, selectionStart);
										else
											mEditTextContent.getEditableText()
													.delete(selectionStart - 1,
															selectionStart);
									} else {
										mEditTextContent.getEditableText()
												.delete(selectionStart - 1,
														selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	/**
	 * 消息图标点击事件
	 * 
	 * @param view
	 */
	@Override
	public void onClick(View view) {
		String st1 = getResources().getString(R.string.not_connect_to_server);
		int id = view.getId();
		if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
			if (mApp.isHasLoginIm()) {
				String s = mEditTextContent.getText().toString();
				sendText(s);
			} else {
				if (IMClient.getInstance().isLoggedIn()) {
					LogUtils.i("ChatActivity, send txt isHasLoginIm = false, isLoggedIn = true");
					mApp.setHasLoginIm(true);
					String s = mEditTextContent.getText().toString();
					sendText(s);
				} else {
					Toast.makeText(ChatActivity.this,
							R.string.has_not_login_im, Toast.LENGTH_SHORT)
							.show();
				}
			}

		} else if (id == R.id.btn_take_picture) {
			// selectPicFromCamera();// 点击照相图标
			if (mApp.isHasLoginIm()) {
				ActivityUtils.startTakePhotActivity(ChatActivity.this,
						PIC_TAKE_PHOTO);
			} else {
				if (IMClient.getInstance().isLoggedIn()) {
					LogUtils.i("ChatActivity, send take pic isHasLoginIm = false, isLoggedIn = true");
					mApp.setHasLoginIm(true);
					ActivityUtils.startTakePhotActivity(ChatActivity.this,
							PIC_TAKE_PHOTO);
				} else {
					Toast.makeText(ChatActivity.this,
							R.string.has_not_login_im, Toast.LENGTH_SHORT)
							.show();
				}
			}

		} else if (id == R.id.btn_picture) {
			// selectPicFromLocal(); // 点击图片图标

			if (mApp.isHasLoginIm()) {
				selectPicFromLocal();
			} else {
				if (IMClient.getInstance().isLoggedIn()) {
					LogUtils.i("ChatActivity, send selpic isHasLoginIm = false, isLoggedIn = true");
					mApp.setHasLoginIm(true);
					selectPicFromLocal();
				} else {
					Toast.makeText(ChatActivity.this,
							R.string.has_not_login_im, Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
		// else if (id == R.id.btn_location) { // 位置
		// startActivityForResult(new Intent(this, BaiduMapActivity.class),
		// REQUEST_CODE_MAP);
		// }
		else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
			more.setVisibility(View.VISIBLE);
			LogUtils.e("dsp emotions, visble:" + face_panel.getVisibility()
					+ " more visble:" + more.getVisibility());
			iv_emoticons_normal.setVisibility(View.INVISIBLE);
			iv_emoticons_checked.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.GONE);
			// emojiIconContainer.setVisibility(View.VISIBLE);
			if (face_panel.getVisibility() == View.VISIBLE) {
				LogUtils.e("hide emotions, visble now ,set gone....");
				face_panel.setVisibility(View.GONE);
			} else {
				LogUtils.e("dsp emotions, hide now ,set visble....");
				face_panel.setVisibility(View.VISIBLE);
				// more.setVisibility(View.GONE);
			}
			hideKeyboard();
			LogUtils.e("---->visble:" + face_panel.getVisibility()
					+ " more visble:" + more.getVisibility());
		} else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			// emojiIconContainer.setVisibility(View.GONE);
			face_panel.setVisibility(View.GONE);
			more.setVisibility(View.GONE);

		} else if (id == R.id.btn_video) {
			// // 点击摄像图标
			// Intent intent = new Intent(ChatActivity.this,
			// ImageGridActivity.class);
			// startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
		} else if (id == R.id.btn_file) { // 点击文件图标
			selectFileFromLocal();
		} else if (id == R.id.btn_voice_call) { // 点击语音电话图标
			// if (!EMChatManager.getInstance().isConnected())
			// Toast.makeText(this, st1, 0).show();
			// else
			// startActivity(new Intent(ChatActivity.this,
			// VoiceCallActivity.class).putExtra("username", toChatUsername)
			// .putExtra("isComingCall", false));
		} else if (id == R.id.btn_video_call) { // 视频通话
			// if (!EMChatManager.getInstance().isConnected())
			// Toast.makeText(this, st1, 0).show();
			// else
			// startActivity(new Intent(this,
			// VideoCallActivity.class).putExtra("username", toChatUsername)
			// .putExtra("isComingCall", false));
		} else if (id == R.id.title_more_icon){
			Intent intent = new Intent(ChatActivity.this,
					ClazzInfoActivity.class);
			long taskId = chatIdLg;
			LogUtils.v("taskId=================== : " + taskId);
			long classroomId = 0;
			List<ClassRoom> rooms = null;
			try {
				rooms = getDBHelper().getClassRoomData().queryForEq("taskid",
						Consts.APP_ID0 + taskId);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (java.sql.SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (rooms != null && rooms.size() > 0) {
				classroomId = rooms.get(0).getId();
			}
			if (classroomId != 0) {
				intent.putExtra("CLAZZ_ID", classroomId);
				startActivity(intent);
			} else {
				UIUtilities.showToast(ChatActivity.this, "未获取到班级ID");
			}
		} else if (id == R.id.title_more_txt){
			Intent intent = new Intent(ChatActivity.this, PersonalInfoActivity.class);
			intent.putExtra("id", chatIdLg);
            startActivity(intent);
		}
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	private PowerManager.WakeLock wakeLock;

	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			boolean iscannel = false;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					String st4 = getResources().getString(
							R.string.Send_voice_need_sdcard_support);
					Toast.makeText(ChatActivity.this, st4, Toast.LENGTH_SHORT)
							.show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();

					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					controller.startRecording(chatId);
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint
							.setText(getString(R.string.release_to_cancel));
					recordingHint
							.setBackgroundResource(R.drawable.recording_text_hint_bg);
					controller.stopRecording(false);
					iscannel = true;
				} else {
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.

				} else {
					String st3 = getResources().getString(
							R.string.send_failure_please);
					try {
						if (!iscannel) {
							controller.stopRecording(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(ChatActivity.this, st3,
								Toast.LENGTH_SHORT).show();
					}

				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				return false;
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CODE_EXIT_GROUP) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			// switch (resultCode) {
			// case RESULT_CODE_COPY: // 复制消息
			// EMMessage copyMsg = ((EMMessage)
			// adapter.getItem(data.getIntExtra("position", -1)));
			// // clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
			// // ((TextMessageBody) copyMsg.getBody()).getMessage()));
			// clipboard.setText(((TextMessageBody)
			// copyMsg.getBody()).getMessage());
			// break;
			// case RESULT_CODE_DELETE: // 删除消息
			// EMMessage deleteMsg = (EMMessage)
			// adapter.getItem(data.getIntExtra("position", -1));
			// conversation.removeMessage(deleteMsg.getMsgId());
			// adapter.refreshSeekTo(data.getIntExtra("position",
			// adapter.getCount()) - 1);
			// break;
			//
			// case RESULT_CODE_FORWARD: // 转发消息
			// EMMessage forwardMsg = (EMMessage)
			// adapter.getItem(data.getIntExtra("position", 0));
			// Intent intent = new Intent(this, ForwardMessageActivity.class);
			// intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
			// startActivity(intent);
			//
			// break;
			//
			// default:
			// break;
			// }
		}
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
				// 清空会话 hahaha
				// EMChatManager.getInstance().clearConversation(toChatUsername);
				// adapter.refresh();
			} else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
			}
			// else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频
			//
			// int duration = data.getIntExtra("dur", 0);
			// String videoPath = data.getStringExtra("path");
			// File file = new File(PathUtil.getInstance().getImagePath(),
			// "thvideo" + System.currentTimeMillis());
			// Bitmap bitmap = null;
			// FileOutputStream fos = null;
			// try {
			// if (!file.getParentFile().exists()) {
			// file.getParentFile().mkdirs();
			// }
			// bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
			// if (bitmap == null) {
			// EMLog.d("chatactivity",
			// "problem load video thumbnail bitmap,use default icon");
			// bitmap = BitmapFactory.decodeResource(getResources(),
			// R.drawable.app_panel_video_icon);
			// }
			// fos = new FileOutputStream(file);
			//
			// bitmap.compress(CompressFormat.JPEG, 100, fos);
			//
			// } catch (Exception e) {
			// e.printStackTrace();
			// } finally {
			// if (fos != null) {
			// try {
			// fos.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// fos = null;
			// }
			// if (bitmap != null) {
			// bitmap.recycle();
			// bitmap = null;
			// }
			//
			// }
			// sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);
			//
			// }
			else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					LogUtils.d("------->REQUEST_CODE_SELECT_FILE: uri=" + selectedImage);
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
			} else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
				if (data != null) {
					String uri = data.getStringExtra("path");
					if (uri != null) {
						sendFile(uri);
					}
				}

			} else if (requestCode == PIC_TAKE_PHOTO) {
				onTakePhotoSucced(data);

			} else if (requestCode == REQ_EDIT_PHOTO) {
				onEditImageSucced(data);

			}
			// else if (requestCode == REQUEST_CODE_MAP) { // 地图
			// double latitude = data.getDoubleExtra("latitude", 0);
			// double longitude = data.getDoubleExtra("longitude", 0);
			// String locationAddress = data.getStringExtra("address");
			// if (locationAddress != null && !locationAddress.equals("")) {
			// more(more);
			// sendLocationMsg(latitude, longitude, "", locationAddress);
			// } else {
			// String st =
			// getResources().getString(R.string.unable_to_get_loaction);
			// Toast.makeText(this, st, 0).show();
			// }
			// // 重发消息
			// } else if (requestCode == REQUEST_CODE_TEXT || requestCode ==
			// REQUEST_CODE_VOICE
			// || requestCode == REQUEST_CODE_PICTURE || requestCode ==
			// REQUEST_CODE_LOCATION
			// || requestCode == REQUEST_CODE_VIDEO || requestCode ==
			// REQUEST_CODE_FILE) {
			// // resendMessage();
			// } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
			// // 粘贴
			// if (!TextUtils.isEmpty(clipboard.getText())) {
			// String pasteText = clipboard.getText().toString();
			// if (pasteText.startsWith(COPY_IMAGE)) {
			// // 把图片前缀去掉，还原成正常的path
			// sendPicture(pasteText.replace(COPY_IMAGE, ""));
			// }
			//
			// }
			// } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { //
			// 移入黑名单
			// EMMessage deleteMsg = (EMMessage)
			// adapter.getItem(data.getIntExtra("position", -1));
			// addUserToBlacklist(deleteMsg.getFrom());
			// } else if (conversation.getMsgCount() > 0) {
			// adapter.refresh();
			// setResult(RESULT_OK);
			// } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
			// adapter.refresh();
			// }
		}
	}

	private void onTakePhotoSucced(Intent data) {
		LogUtils.d("onTakePhotoSucced:" + data);
		String filePath = mApp.getUploadImageOutputFile().toString();
		LogUtils.d("filePath:" + filePath);
		// sendPic(filePath);

		startActivityForResult(
				BrowseImageActivity.getEditIntent(this,
						Uri.fromFile(new File(filePath))), REQ_EDIT_PHOTO);
	}

	private void onEditImageSucced(Intent data) {
		if (data == null)
			return;
		Uri uri = data.getData();

		String ly_time = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.getDefault()).format(Calendar.getInstance().getTime());
		String filePath = mApp.getWorkspaceImage().toString() + "/" + ly_time
				+ ".jpeg";
		LogUtils.d("------->onEditImageSucced: filePath=" + filePath);
		try {
			ImageUtils.savePictoFile(uri, filePath);
			ArrayList<String> rawPics = new ArrayList<String>();
			rawPics.add(filePath);
			new HandleLocalBitmapTask(rawPics).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private class HandleLocalBitmapTask extends AsyncTask<Void, Void, Void> {
		private String tempDirPath;
		private ArrayList<String> mRawPics;

		public HandleLocalBitmapTask(ArrayList<String> rawpics) {
			super();
			tempDirPath = BaseApplication.getInstance().getWorkspaceImage()
					.getAbsolutePath();
			mRawPics = rawpics;
			LogUtils.e("pic add 3, tempDirPath = " + tempDirPath
					+ " mRawPics size =" + mRawPics.size());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ProgressDialogUtils.showProgressDialog("图片处理中", ChatActivity.this);
			LogUtils.e("pic add 4, onPreExecute");
		}

		@Override
		protected Void doInBackground(Void... params) {
			choosePics.clear();
			for (int i = 0; i < mRawPics.size(); i++) {
				String temp = pressPic(mRawPics.get(i));
				// String temp = mRawPics.get(i);
				if (!TextUtils.isEmpty(temp)) {
					choosePics.add(temp);
				}
				LogUtils.e("pic add 8, choosePics =" + choosePics);
			}
			return null;
		}

		private String pressPic(String path) {
			String resultFileName = BitmapUtils.handleLocalBitmapFile(path,
					tempDirPath);
			LogUtils.e("pic add 5, resultFileName =" + resultFileName);
			if (resultFileName.endsWith(".png")
					|| resultFileName.endsWith(".jpg")) {
				LogUtils.e("pic add 6");
				FileInputStream fileinputstream = null;
				Bitmap bitmap = null;
				try {
					fileinputstream = new FileInputStream(resultFileName);
					FileDescriptor filedescriptor = fileinputstream.getFD();
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPurgeable = true;
					options.inInputShareable = true;
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeFileDescriptor(filedescriptor,
							null, options);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						fileinputstream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (bitmap != null) {
					String filaName = new SimpleDateFormat("yyyyMMddHHmmss",
							Locale.getDefault()).format(Calendar.getInstance()
							.getTime())
							+ UUID.randomUUID() + ".jpg";
					File aimFile = new File(tempDirPath, filaName);
					String aimPath = aimFile.getAbsolutePath();
					if (BitmapUtils.writeImageFile(aimPath, bitmap)) {
						return aimPath;
					} else {
						return null;
					}
				}
				return null;
			} else {

				LogUtils.e("pic add 7, resultFileName =" + resultFileName);
				return resultFileName;
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			ProgressDialogUtils.dismissProgressBar();
			if (null != choosePics && choosePics.size() > 0) {
				sendPicture(choosePics.get(0));
			}
		}
	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		LogUtils.d("------->sendPicture: filePath=" + filePath);

		if (StringUtil.isNullOrEmpty(filePath)) {
			LogUtils.d("can not send empty picture msg!!");
			return;
		}

		try {

			File imgFile = new File(filePath);
			MQ.VYMessage message = MQ.VYMessage
					.createSendMessage(MQ.VYMessage.Type.IMAGE);
			MQ.imageMessageBody msgbody = new MQ.imageMessageBody(imgFile);
			if (conver.getChatType() == VYMessage.ChatType.CHATTYPE_GROUP) {
				message.setChatType(VYMessage.ChatType.CHATTYPE_GROUP);
			} else {
				message.setChatType(VYMessage.ChatType.CHATTYPE_SINGLE);
			}
			message.addBody(msgbody);
			message.setTo(conver.getChatID());
			sendMsg = message;
			IMClient.getInstance().SendMessage(message);

			adapter.notifyDataSetChanged();
			// listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			// listView.setStackFromBottom(true);
			// scrollListViewToBottom();
			
			if (chatTip.VISIBLE == View.VISIBLE) {
				chatTip.setVisibility(View.GONE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		//System.out.println("selectedImage" + selectedImage);
		LogUtils.d("------->sendPicByUri: uri=" + selectedImage);
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage, null, null,
				null, null);
		String st8 = getResources().getString(R.string.cant_find_pictures);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			
			LogUtils.d("------->sendPicByUri: path1=" + picturePath);
			//sendPicture(picturePath);
			ArrayList<String> rawPics = new ArrayList<String>();
			rawPics.add(picturePath);
			new HandleLocalBitmapTask(rawPics).execute();
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			
			LogUtils.d("------->sendPicByUri: path2=" + file.getAbsolutePath());
			//sendPicture(file.getAbsolutePath());
			
			ArrayList<String> rawPics = new ArrayList<String>();
			rawPics.add(file.getAbsolutePath());
			new HandleLocalBitmapTask(rawPics).execute();
		}

	}

	/**
	 * 选择文件
	 */
	private void selectFileFromLocal() {
		// Intent intent = null;
		// // if (Build.VERSION.SDK_INT < 19) {
		// intent = new Intent(Intent.ACTION_GET_CONTENT);
		// intent.setType("*/*");
		// // Uri uri = Uri.parse("content://media/external/");
		// // intent.setData(uri);
		// intent.addCategory(Intent.CATEGORY_OPENABLE);
		//
		// // } else {
		// // intent = new Intent(
		// // Intent.ACTION_PICK,
		// // android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// // }
		Intent intent = new Intent();
		intent.setClass(this, FileChooserActivity.class);
		startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
	}

	/**
	 * 发送文件
	 * 
	 * @param uri
	 */
	private void sendFile(String uri) {

		if (StringUtil.isNullOrEmpty(uri)) {
			LogUtils.d("can not send empty file msg!!");
			return;
		}

		try {
			File file = new File(uri);
			MQ.VYMessage message = MQ.VYMessage
					.createSendMessage(MQ.VYMessage.Type.DOCUMENT);
			MQ.NormalFileMessageBody msgbody = new MQ.NormalFileMessageBody(
					file);
			if (conver.getChatType() == VYMessage.ChatType.CHATTYPE_GROUP) {
				message.setChatType(VYMessage.ChatType.CHATTYPE_GROUP);
			} else {
				message.setChatType(VYMessage.ChatType.CHATTYPE_SINGLE);
			}
			message.addBody(msgbody);
			message.setTo(conver.getChatID());
			IMClient.getInstance().SendMessage(message);

			adapter.notifyDataSetChanged();
			// listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			scrollListViewToBottom();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wakeLock.isHeld())
			wakeLock.release();
		
		mApp.setInChat(0);
		mApp.setImcomeChatId("");
		
		MediaController.getInstance().stopAudio();

		// try {
		// // 停止录音
		// if (voiceRecorder.isRecording()) {
		// voiceRecorder.discardRecording();
		// recordingContainer.setVisibility(View.INVISIBLE);
		// }
		// } catch (Exception e) {
		// }

		// if(conver.getMessage()==null){
		// if(conver.getChatType() == VYMessage.ChatType.CHATTYPE_GROUP){
		// ChatManager.getInstance().removeConversion(conver.getChatID(), true);
		// }else{
		// ChatManager.getInstance().removeConversion(conver.getChatID(),
		// false);
		// }
		// }

		// LogUtils.e("chat-------------->Acitivity onPause");
	}

	public ListView getListView() {
		return listView;
	}

	/**
	 * 监测群组解散或者被T事件
	 * 
	 */
	// class GroupListener extends GroupReomveListener {
	//
	// @Override
	// public void onUserRemoved(final String groupId, String groupName) {
	// runOnUiThread(new Runnable() {
	// String st13 = getResources().getString(R.string.you_are_group);
	//
	// public void run() {
	// if (toChatUsername.equals(groupId)) {
	// Toast.makeText(ChatActivity.this, st13, 1).show();
	// // if (GroupDetailsActivity.instance != null)
	// // GroupDetailsActivity.instance.finish();
	// finish();
	// }
	// }
	// });
	// }
	//
	// @Override
	// public void onGroupDestroy(final String groupId, String groupName) {
	// // 群组解散正好在此页面，提示群组被解散，并finish此页面
	// runOnUiThread(new Runnable() {
	// String st14 = getResources().getString(
	// R.string.the_current_group);
	//
	// public void run() {
	// if (toChatUsername.equals(groupId)) {
	// Toast.makeText(ChatActivity.this, st14, 1).show();
	// // if (GroupDetailsActivity.instance != null)
	// // GroupDetailsActivity.instance.finish();
	// finish();
	// }
	// }
	// });
	// }
	//
	// }

	/**
	 * listview滑动监听listener
	 * 
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}

	}

	/**
	 * 转发消息
	 * 
	 * @param forward_msg_id
	 */

	@Override
	protected void onStart() {
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.recordStopped);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.MessageArrivalCode);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.SendMessageResponseCode);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.processGetMessages);

		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.recordStarted);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.recordStartError);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.recordMICChange);
		// NotificationCenter.getInstance().addObserver(this,
		// NotificationCenter.UploadFileCompleted);
		// NotificationCenter.getInstance().addObserver(this,
		// NotificationCenter.UploadFileFailed);
		// NotificationCenter.getInstance().addObserver(this,
		// NotificationCenter.UploadFileProgressChanged);

		super.onStart();
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onRestart() {
		adapter.notifyDataSetChanged();
		super.onRestart();

		// LogUtils.e("chat-------------->Acitivity onRestart");
	}

	@Override
	protected void onResume() {
		if (chattype == ChatType.CHAT_TYPE_SINGLE) {
			mApp.setInChat(1);
			mApp.setImcomeChatId(chatId);
		} else {
			mApp.setInChat(2);
		}
		adapter.notifyDataSetChanged();
		super.onResume();

		// LogUtils.e("chat-------------->Acitivity onResume");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// resolveIntent(intent);
		LogUtils.e("chat-------------->Acitivity onNewIntent");
		initProcess(intent);
	}

	// @Override
	// protected void onRestart() {
	// // TODO Auto-generated method stub
	// conver = ChatManager.getInstance().getConversation(chatId,
	// MQ.VYMessage.ChatType.valueOf(chattype));
	// adapter = new IMMessageListAdapter(this,conver);
	// super.onRestart();
	// }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// LogUtils.e("chat-------------->Acitivity onDestroy");
	}

	@Override
	protected void onStop() {
		// isAlive = false;
		NotificationCenter.getInstance().removeObserver(this,
				NotificationCenter.MessageArrivalCode);
		NotificationCenter.getInstance().removeObserver(this,
				NotificationCenter.recordStopped);
		NotificationCenter.getInstance().removeObserver(this,
				NotificationCenter.SendMessageResponseCode);
		NotificationCenter.getInstance().removeObserver(this,
				NotificationCenter.processGetMessages);

		NotificationCenter.getInstance().removeObserver(this,
				NotificationCenter.recordStarted);
		NotificationCenter.getInstance().removeObserver(this,
				NotificationCenter.recordStartError);
		NotificationCenter.getInstance().removeObserver(this,
				NotificationCenter.recordMICChange);

		// NotificationCenter.getInstance().removeObserver(this,
		// NotificationCenter.UploadFileCompleted);
		// NotificationCenter.getInstance().removeObserver(this,
		// NotificationCenter.UploadFileFailed);
		// NotificationCenter.getInstance().removeObserver(this,
		// NotificationCenter.UploadFileProgressChanged);

		// 用户调用这个函数释放内存
		// this.conver.close();
		super.onStop();
	}

	private void updateLocalMsgList() {
		LogUtils.i("updateLocalMsgList ---------------------->");

		if (null == sendMsg) {
			LogUtils.e("updateLocalMsgList, sendMsg is null");
			return;
		}

		try {
			ContentResolver resolver;

			resolver = ChatActivity.this.getContentResolver();
			if (null == resolver) {
				LogUtils.e("ChatActivity, resolver is null");
				return;
			}

			String accountName = getAccountName();
			int chatType;
			ContentValues cv;

			// SharedPreferences sp = BaseApplication.getInstance().getSp();
			// String key;
			int unreadCount = -1;

			cv = new ContentValues();
			cv.put(ThreadTable.ACCOUNT_NAME, accountName);

			LogUtils.i("updateLocalMsgList ---------------------->VYMessage.Type.TEXT="
					+ VYMessage.Type.TEXT + " sendMsg=" + sendMsg);
			if (null == sendMsg) {
				LogUtils.e("updateLocalMsgList, sendMsg2 is null");
				return;
			}

			if (VYMessage.Type.TEXT == sendMsg.getType()) {
				cv.put(MessageTable.BODY,
						((MQ.textMessageBody) sendMsg.getBody()).getMessage());
			}

			cv.put(MessageTable.IS_INBOUND, MessageType.OUTGOINT);

			switch (sendMsg.getChatType()) {
			case CHATTYPE_GROUP:
				chatType = ChatType.CHAT_TYPE_GROUP;
				cv.put(ThreadTable.THREAD_TYPE, 0);
				cv.put(ThreadTable.BUDDY_ID, sendMsg.getTo());
				
				// key = accountName+sendMsg.getTo();
				break;
			case CHATTYPE_SINGLE:
			default:
				chatType = ChatType.CHAT_TYPE_SINGLE;
				cv.put(ThreadTable.THREAD_TYPE, 5);
				cv.put(ThreadTable.BUDDY_ID, sendMsg.getTo());
				
				// key = accountName+sendMsg.getFrom();
				break;
			}
			cv.put(ThreadTable.BUDDY_NAME, txt_chatid.getText().toString());
			cv.put(ThreadTable.CHAT_TYPE, chatType);

			// unreadCount = sp.getInt(key, 1);
			// if (sendMsg.isUnread()) {
			// unreadCount++;
			//
			// Editor editor = sp.edit();
			// editor.putInt(key, unreadCount);
			// editor.commit();
			//
			// LogUtils.d("updateArivalMsg, unreadCount=" + unreadCount);
			// }

			cv.put(ThreadTable.UNREAD_COUNT, unreadCount);

			switch (sendMsg.getType()) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void didReceivedNotification(int id, Object... args) {
		LogUtils.i("--->ChatActivity, didReceivedNotification, id=" + id);

		if (id == NotificationCenter.recordStopped) {
			RECORD_STATE = RECODE_ED;
			Log.e("chat", "did, recordStopped---->RECORD_STATE=" + RECORD_STATE);

			int length = (Integer) args[1];
			String filepath = (String) args[2];
			MQ.VYMessage message = MQ.VYMessage
					.createSendMessage(MQ.VYMessage.Type.VOICE);
			MQ.voiceMessageBody msgbody = new MQ.voiceMessageBody();
			if (conver.getChatType() == VYMessage.ChatType.CHATTYPE_GROUP) {
				message.setChatType(VYMessage.ChatType.CHATTYPE_GROUP);
			} else {
				message.setChatType(VYMessage.ChatType.CHATTYPE_SINGLE);
			}
			msgbody.setFileName(filepath);
			msgbody.setDuration(length);
			message.addBody(msgbody);
			message.setTo(conver.getChatID());
			sendMsg = message;
			IMClient.getInstance().SendMessage(message);

			adapter.notifyDataSetChanged();
			// listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			scrollListViewToBottom();

		} else if (id == NotificationCenter.recordStarted) {
			RECORD_STATE = RECORD_ING;
			LogUtils.i("--->ChatActivity, recordStarted---->RECORD_STATE="
					+ RECORD_STATE);

		} else if (id == NotificationCenter.recordStartError) {
			RECORD_STATE = RECORD_NO;
			Toast.makeText(this, "录音启动失败，请检查软件权限", Toast.LENGTH_SHORT).show();
			LogUtils.e("--->ChatActivity,recordStartError---->RECORD_STATE="
					+ RECORD_STATE);

		} else if (id == NotificationCenter.MessageArrivalCode) {
			LogUtils.i("--->ChatActivity, 接收成功！");
			VYMessage MSG = (VYMessage) args[0];
			if (MSG.getChatType() == MQ.VYMessage.ChatType.CHATTYPE_GROUP) {
				if (!MSG.getTo().equals(conver.getChatID())) {
					LogUtils.e("group msg, no need to set states, chatID="
							+ conver.getChatID() + " to=" + MSG.getTo());
					return;
				}
			} else {
				if (!MSG.getFrom().equals(conver.getChatID())) {
					LogUtils.e("sigle msg, no need to set states, chatID="
							+ conver.getChatID() + " from=" + MSG.getFrom());
					return;
				}
			}
			// if (isAlive) {
			conver.setReadState(true);
			LogUtils.e("has set read states, chatID=" + conver.getChatID());
			adapter.notifyDataSetChanged();
			// listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			scrollListViewToBottom();
			// listView.setSelection(conver.getMessageCount());
			if (chatTip.VISIBLE == View.VISIBLE) {
				chatTip.setVisibility(View.GONE);
			}
		} else if (id == NotificationCenter.SendMessageResponseCode) {
			adapter.notifyDataSetChanged();
			// listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			// scrollListViewToBottom();
			// listView.setSelection(conver.getMessageCount());
			LogUtils.i("--->ChatActivity,发送成功");
			// LogUtils.e("timetxt------->发送成功");
			updateLocalMsgList();
			if (chatTip.VISIBLE == View.VISIBLE) {
				chatTip.setVisibility(View.GONE);
			}
		} else if (id == NotificationCenter.processGetMessages) {
			adapter.notifyDataSetChanged();
			// listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			scrollListViewToBottom();
			// 从数据库取出数据完成，隐藏图片
			LogUtils.e("---->ChatActivity, processGetMessages conver.getMessageCount()="
					+ conver.getMessageCount());

			if (conver.getMessageCount() > 0) {
				if (chatTip.VISIBLE == View.VISIBLE) {
					chatTip.setVisibility(View.GONE);
				}
			} else {
				if (chatTip.VISIBLE == View.GONE) {
					chatTip.setVisibility(View.VISIBLE);
				}
			}
		} else if (id == NotificationCenter.UploadFileFailed) {
			adapter.notifyDataSetChanged();
			// listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			scrollListViewToBottom();

		} else if (id == NotificationCenter.UploadFileCompleted) {
			String location = (String) args[0];
			if (args.length > 1) {
				String location2 = (String) args[1];
				Log.e("did", "UploadFileCompleted, location=" + location
						+ ", location2:" + location2);
			} else {
				Log.e("did", "UploadFileCompleted, location=" + location);
			}
		} else if (id == NotificationCenter.recordMICChange) {
			int micChange = (Integer) args[0];
			int temp = (int) (micChange / (100 / 14));
			LogUtils.e("mic-----------------> micChange=" + micChange
					+ " temp=" + temp);
			micImageHandler.sendEmptyMessage(temp);
		}
		// else if (id == NotificationCenter.UploadFileFailed) {
		// // VYMessage MSG = (VYMessage) args[0];
		// String location = (String) args[0];
		// Log.e("did", "UploadFileFailed, location="
		// + location);
		//
		// } else if (id == NotificationCenter.UploadFileProgressChanged) {
		// String location = (String) args[0];
		// Float progress = (Float) args[1];// 上传进度
		// Log.e("did",
		// "UploadFileProgressChanged, location=" + location
		// + ", progress:" + progress);
		// }

	}

	// 录音线程
	// private Runnable ImgThread = new Runnable() {
	// @Override
	// public void run() {
	// while (RECORD_STATE == RECORD_ING) {
	// try {
	// Thread.sleep(200);
	// if (RECORD_STATE == RECORD_ING) {
	// // voiceValue = mr.getAmplitude();
	// imgHandle.sendEmptyMessage(1);
	// }
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// // 录音Dialog图片随声音大小切换
	// private int getMicImg() {
	//
	// dialog_txt.setText(R.string.chat_vodio_dialog_up);
	// dialog_txt.setTextColor(Color.WHITE);
	// if (recorderInstance != null) {
	// voiceValue = recorderInstance.value;
	// }
	// LogUtils.e("====" + voiceValue);
	// if (voiceValue <= 2.0) {
	// dialog_img.setImageResource(R.drawable.record_animate_01);
	// } else if (voiceValue > 2.0 && voiceValue <= 4) {
	// dialog_img.setImageResource(R.drawable.record_animate_02);
	// } else if (voiceValue > 4.0 && voiceValue <= 6) {
	// dialog_img.setImageResource(R.drawable.record_animate_03);
	// } else if (voiceValue > 6.0 && voiceValue <= 8) {
	// dialog_img.setImageResource(R.drawable.record_animate_04);
	// } else if (voiceValue > 8.0 && voiceValue <= 10) {
	// dialog_img.setImageResource(R.drawable.record_animate_05);
	// } else if (voiceValue > 10.0 && voiceValue <= 12) {
	// dialog_img.setImageResource(R.drawable.record_animate_06);
	// } else if (voiceValue > 12.0 && voiceValue <= 14) {
	// dialog_img.setImageResource(R.drawable.record_animate_07);
	// } else if (voiceValue > 14.0 && voiceValue <= 16.0) {
	// dialog_img.setImageResource(R.drawable.record_animate_08);
	// } else if (voiceValue > 16.0 && voiceValue <= 20.0) {
	// dialog_img.setImageResource(R.drawable.record_animate_09);
	// } else if (voiceValue > 20.0 && voiceValue <= 23.0) {
	// dialog_img.setImageResource(R.drawable.record_animate_10);
	// } else if (voiceValue > 23.0 && voiceValue <= 26.0) {
	// dialog_img.setImageResource(R.drawable.record_animate_11);
	// } else if (voiceValue > 26.0 && voiceValue <= 30.0) {
	// dialog_img.setImageResource(R.drawable.record_animate_12);
	// } else if (voiceValue > 30.0 && voiceValue <= 35.0) {
	// dialog_img.setImageResource(R.drawable.record_animate_13);
	// } else if (voiceValue > 35.0) {
	// dialog_img.setImageResource(R.drawable.record_animate_14);
	// }
	// }
	//
	// Handler imgHandle = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	//
	// switch (msg.what) {
	// case 0:
	// if (RECODE_STATE == RECORD_ING) {
	// RECODE_STATE = RECODE_ED;
	// if (voiceDialog.isShowing()) {
	// voiceDialog.dismiss();
	// }
	// try {
	// // mr.stop();
	// recorderInstance.setRecording(false);
	// recorderInstance = null;
	// change_flag = false;
	// voiceValue = 0.0;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// if (recodeTime < 1.0) {
	// showWarnToast();
	// RECODE_STATE = RECORD_NO;
	// } else {
	// saveVoice(tempVoicePath);
	// }
	// }
	// break;
	// case 1:
	// setDialogImage();
	// break;
	// default:
	// break;
	// }
	// }
	// };
	// };

}
