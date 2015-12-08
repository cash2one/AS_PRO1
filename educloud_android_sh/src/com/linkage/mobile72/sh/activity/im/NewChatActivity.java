package com.linkage.mobile72.sh.activity.im;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gauss.speex.recorder.SpeexPlayer;
import com.gauss.speex.recorder.SpeexRecorder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.lib.util.FileUtils;
import com.linkage.lib.util.LogUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.BrowseImageActivity;
import com.linkage.mobile72.sh.activity.ClazzInfoActivity;
import com.linkage.mobile72.sh.activity.PersonalInfoActivity;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.data.OLConfig;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.http.WDJsonObjectForChatRequest;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.im.FileHelper;
import com.linkage.mobile72.sh.im.provider.Ws.MessageTable;
import com.linkage.mobile72.sh.im.provider.Ws.MessageType;
import com.linkage.mobile72.sh.im.provider.Ws.ThreadTable;
import com.linkage.mobile72.sh.utils.FaceUtils;
import com.linkage.mobile72.sh.utils.FaceUtils.Face;
import com.linkage.mobile72.sh.utils.ImageUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.widget.FacePanelView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

@SuppressLint("NewApi")
public class NewChatActivity extends BaseActivity {
//	private Cursor onLongClickCursor;
	
	private static final String TAG = NewChatActivity.class.getSimpleName();
	private static final int REQ_TAKE_PHOTO = REQUEST_FIRST_USER + 1;
	private static final int REQ_LOCAL_ALBUM = REQUEST_FIRST_USER + 2;
	private static final int REQ_EDIT_PHOTO = REQUEST_FIRST_USER + 3;
	protected BaseApplication mApp;
	private AccountData mAccount;
	private static final int QUERY_TOKEN = 10;
	private static final int CHOOSE_CHAT_COPY = 0;
	private static final int CHOOSE_CHAT_REPLY = 1;
	private static final int CHOOSE_CHAT_DELETE = 2;
	private String[] allItems ;
	Timer mTimer = null;
	// 语音动画控制任务
	TimerTask mTimerTask = null;
	private int singleLineHeight;

	private AsyncTask<?, ?, ?> sendFileOLServerTask;
	private File sendfile;
	private String olFilePath;
	private Uri newURL;
	private DataHelper dataHelper;
	private Locale locale = Locale.getDefault();
	private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",locale);
	private SimpleDateFormat dateSpanFormat = new SimpleDateFormat("MM月dd日  HH:mm",locale);
	private Date now;
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		String s_temp;
		int p;
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			s_temp = s.toString();
			p = getEditTextCursorIndex(mMessageEdit);
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().trim().length()>0) {
				mSendBtn.setVisibility(View.VISIBLE);
				mChoosePhotoView.setVisibility(View.GONE);
			}else {
				mSendBtn.setVisibility(View.GONE);
				mChoosePhotoView.setVisibility(View.VISIBLE);
			}
			LogUtils.e("光标位置：" + p);
			if(s_temp.toString().equals(s.toString())){
				mMessageEdit.setSelection(p);
			}else{
				LogUtils.e("-----------------------"+p);
				mMessageEdit.setText(FaceUtils.replaceFace(NewChatActivity.this, s.toString()));
				mMessageEdit.setSelection(p);
			}
			if(mMessageEdit.getLineCount() > 1 && mMessageEdit.getLineCount() < 5) {
				android.view.ViewGroup.LayoutParams pp = chatBottomLayout.getLayoutParams();
				chatBottomLayout.getLayoutParams();
				pp.height = singleLineHeight + 35*(mMessageEdit.getLineCount()-1);
				chatBottomLayout.setLayoutParams(pp);
			}
		}
	};
	// 记录语音动画图片
	int index = 1;
	AudioAnimationHandler audioAnimationHandler = null;

	private static final String[] MESSAGE_PROJECTION = { MessageTable._ID,
			MessageTable.BUDDY_ID, MessageTable.BODY, MessageTable.IS_INBOUND,
			MessageTable.SENT_TIME, MessageTable.RECEIVED_TIME,
			MessageTable.OUTBOUND_STATUS, MessageTable.SENDER_ID,
			MessageTable.TYPE, MessageTable.CHAT_TYPE };

	private static final String EXTRAS_CHAT_TYPE = "extras_chat_type";
	private static final String EXTRAS_BUDDY_ID = "extras_buddy_id";
	private static final String EXTRAS_BUDDY_NAME = "extras_buddy_name";
	private static final String EXTRAS_GROUP_ID = "extras_group_id";
	private static final String EXTRAS_GROUP_NAME = "extras_group_name";
	private static final String EXTRAS_CHAT_SERVERTYPE = "extras_chat_servertype";
	
	public static Intent getIntent(Context context, long buddyId, String buddyName, int chattype,int serverType) {
		Intent intent = new Intent(context, NewChatActivity.class);
		intent.putExtra(EXTRAS_BUDDY_ID, buddyId);
		intent.putExtra(EXTRAS_CHAT_TYPE, chattype);
		intent.putExtra(EXTRAS_BUDDY_NAME, buddyName);
		intent.putExtra(EXTRAS_CHAT_SERVERTYPE, serverType);
		if(ChatType.CHAT_TYPE_GROUP == chattype) {
			intent.putExtra(EXTRAS_GROUP_ID, buddyId);
			intent.putExtra(EXTRAS_BUDDY_NAME, buddyName);
		}
		return intent;
	}

	public static Intent getIntent(Context context, long buddyId, String buddyName, long buddyGroupId, 
			String buddyGroupName, int chattype,int serverType) {
		Intent intent = new Intent(context, NewChatActivity.class);
		intent.putExtra(EXTRAS_BUDDY_ID, buddyId);
		intent.putExtra(EXTRAS_CHAT_TYPE, chattype);
		intent.putExtra(EXTRAS_BUDDY_NAME, buddyName);
		intent.putExtra(EXTRAS_GROUP_ID, buddyGroupId);
		intent.putExtra(EXTRAS_BUDDY_NAME, buddyGroupName);
		intent.putExtra(EXTRAS_CHAT_SERVERTYPE, serverType);
		return intent;
	}

	private long mBuddyId = -1;
	private String groupName = "";
	private String mBuddyName = "";
	private int mServerType = 0;
	private Contact mContact;
	private ListView mHistoryListView;
	private EditText mMessageEdit;
	private Button mSendBtn;
	private ImageButton mChoose_voice;
	private View mChoosePhotoView;
	private Dialog dialog;
	private MessageAdapter mMessageAdapter;
	private QueryHandler mQueryHandler;
	private RequeryCallback mRequeryCallback = null;
	private Handler mHandler = new Handler();
	private RelativeLayout chatBottomLayout;
	private LinearLayout toolsPanel;
	private View face_panel;
	private Button btn_yuyin;
	private GridView tools_more;
	private ImageView dialog_img;
	private TextView dialog_txt;
	private Thread recordThread;
	private Boolean change_flag;
	SpeexRecorder recorderInstance = null;
	SpeexPlayer splayer = null;

	private int mChatType = ChatType.CHAT_TYPE_SINGLE;

	private static final int MESSAGE_IN = 1;
	private static final int MESSAGE_OUT = 2;

	private static int MAX_TIME = 30; // 最长录制时间，单位秒，0为无时间限制
	private static int MIX_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1

	private static int RECORD_NO = 0; // 不在录音
	private static int RECORD_ING = 1; // 正在录音
	private static int RECODE_ED = 2; // 完成录音

	private static int RECODE_STATE = 0; // 录音的状态

	private static float recodeTime = 0.0f; // 录音的时间
	private static double voiceValue = 0.0; // 麦克风获取的音量值
	private TextView back;
	private TextView set;
	private String voidePathName;
	ArrayList<HashMap<String, Object>> imagelist = new ArrayList<HashMap<String, Object>>();
	private FileHelper fileHelper;
	private String voideName;
	private ImageView ant_imageView;
	public Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FileHelper.MESSAGE_OPEN_DIALOG:// 开始启动Dialog

				break;
			case FileHelper.MESSAGE_START:// 开始下载

			case FileHelper.MESSAGE_PROGRESS:// 正在下载

				break;
			case FileHelper.MESSAGE_STOP:// 下载结束
				try {
					splayer = new SpeexPlayer(voideName);

					splayer.endPlay();

					splayer.startPlay();
					playAudioAnimation(ant_imageView, MESSAGE_IN);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case FileHelper.MESSAGE_ERROR:

				break;

			}
			super.handleMessage(msg);
		}
	};

	private View.OnClickListener mOnClickTitleLeft = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};

	private View.OnClickListener mOnClickChat_Clean = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			//cleanMessage();
			if(ChatType.CHAT_TYPE_GROUP == mChatType) {
				Intent intent = new Intent(NewChatActivity.this, ClazzInfoActivity.class);
				long taskId = mBuddyId;
				long classroomId = 0;
				List<ClassRoom> rooms = null;
				try {
					rooms = getDBHelper().getClassRoomData().queryForEq("taskid", taskId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rooms != null && rooms.size() > 0) {
					classroomId = rooms.get(0).getId();
				}
				if(classroomId != 0) {
					intent.putExtra("CLAZZ_ID", classroomId);	
					startActivity(intent);
				}else {
					UIUtilities.showToast(NewChatActivity.this, "未获取到班级ID");
				}
			}else {
				Intent intent = new Intent(NewChatActivity.this, PersonalInfoActivity.class);
				intent.putExtra("id", mBuddyId);
                startActivity(intent);
			}
		}

	};
	
    @Override
    public View onCreatePanelView(int featureId) {
        return super.onCreatePanelView(featureId);
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_new);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		mApp = BaseApplication.getInstance();
		mAccount = mApp.getDefaultAccount();
		fileHelper = new FileHelper(mhandler);
		

		back = (TextView) findViewById(R.id.back);
		back.setOnClickListener(mOnClickTitleLeft);

		set = (TextView) findViewById(R.id.set);
		set.setVisibility(View.VISIBLE);
		FaceUtils.install(this, new FacePanelView.OnFaceClickListener() {

			@Override
			public void onFaceClick(Face face) {
				insertFace(face);
			}
		});
		
		dataHelper = new DataHelper(this);
		
		init();
		initchat_more();
		resolveIntent(getIntent());
        if (mChatType != ChatType.CHAT_TYPE_SINGLE) {
            set.setText("班级资料");
        }else {
        	set.setText("好友资料");
        }
        set.setOnClickListener(mOnClickChat_Clean);
		
        OLConfig olconfig = BaseApplication.getInstance().getOlConfig();
        if(olconfig == null) {
			UIUtilities.showToast(this, "网络异常");
			return;
		}
		olFilePath = "http://"+olconfig.ol_ip+":"+ olconfig.ol_port+"//";
		
	}

    void resolveIntent(Intent intent) {
		mBuddyId = intent.getExtras().getLong(EXTRAS_BUDDY_ID);
		mBuddyName = intent.getExtras().getString(EXTRAS_BUDDY_NAME);
		groupName = intent.getExtras().getString(EXTRAS_GROUP_NAME);
		mChatType = intent.getExtras().getInt(EXTRAS_CHAT_TYPE, ChatType.CHAT_TYPE_SINGLE);
		mServerType = intent.getExtras().getInt(EXTRAS_CHAT_SERVERTYPE, 0);
		LogUtils.d("chattype:" + mChatType);
		LogUtils.d("buddyId:" + mBuddyId);
		updateChat();

		// 更新未读数
		ContentValues cv = new ContentValues();
		cv.put(ThreadTable.UNREAD_COUNT, 0);
		getContentResolver().update(
				ThreadTable.CONTENT_URI,
				cv,
				ThreadTable.ACCOUNT_NAME + "=? and " + ThreadTable.BUDDY_ID
						+ "=? and " + ThreadTable.CHAT_TYPE + "=? and "
						+ ThreadTable.MSG_TYPE + " in "
						+ MessageType.MSG_TYPE_CHAT,
				new String[] { getAccountName(), String.valueOf(mBuddyId),
						String.valueOf(mChatType) });
	}

	void initchat_more() {

//		HashMap<String, Object> map1 = new HashMap<String, Object>();
//		map1.put("image", R.drawable.more_face);
//		map1.put("txt", "表情");
//		imagelist.add(map1);
//		HashMap<String, Object> map2 = new HashMap<String, Object>();
//		map2.put("image", R.drawable.more_camera);
//		map2.put("txt", "拍照");
//		imagelist.add(map2);
//		HashMap<String, Object> map3 = new HashMap<String, Object>();
//		map3.put("image", R.drawable.more_pic);
//		map3.put("txt", "图片");
//		imagelist.add(map3);
		// HashMap<String,Object> map4 = new HashMap<String,Object>();
		// map4.put("image", R.drawable.more_movice);
		// map4.put("txt", "通知");
		// imagelist.add(map4);
		// HashMap<String, Object> map5 = new HashMap<String, Object>();
		// map5.put("image", R.drawable.more_work);
		// map5.put("txt", "作业");
		// imagelist.add(map5);
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("image", R.drawable.photo);
		map1.put("txt", "照片");
		imagelist.add(map1);
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("image", R.drawable.camera);
		map2.put("txt", "拍照");
		imagelist.add(map2);
		/*HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("image", R.drawable.speech_call);
		map3.put("txt", "语音通话");
		imagelist.add(map3);
		HashMap<String, Object> map4 = new HashMap<String, Object>();
		map4.put("image", R.drawable.video_call);
		map4.put("txt", "视频通话");
		imagelist.add(map4);*/
		
	}

	public class ItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			switch (arg2) {
			case 0:
				UIUtilities.showChoosePhotoDialog(NewChatActivity.this,
						REQ_LOCAL_ALBUM);
//				if (face_panel.getVisibility() == View.VISIBLE) {
//					face_panel.setVisibility(View.GONE);
//				} else {
//					face_panel.setVisibility(View.VISIBLE);
//					toolsPanel.setVisibility(View.GONE);
//				}

				break;
			case 1:
				UIUtilities.showChoosePhotoDialog(NewChatActivity.this,
						REQ_TAKE_PHOTO);
				// UIUtilities.showChoosePhotoDialog(NewChatActivity.this,
				// REQ_TAKE_PHOTO, REQ_LOCAL_ALBUM);
				break;
			case 2:
				
				// UIUtilities.showChoosePhotoDialog(NewChatActivity.this,
				// REQ_TAKE_PHOTO, REQ_LOCAL_ALBUM);

				break;
			case 3:
				// if(isTeacher())
				// {
				// startActivity(new Intent(NewChatActivity.this,
				// WriteHomeWorkActivity.class));
				// }
				// else {
				// UIUtilities.showToast(NewChatActivity.this,
				// R.string.parent_cannot_sendhomework);
				// }

				break;
			case 4:
				break;
			case 5:
				break;
			}

		}

	}

	private void init() {
		chatBottomLayout = (RelativeLayout) findViewById(R.id.chat_bottom_layout);
		toolsPanel = (LinearLayout) findViewById(R.id.tools_panel);
		face_panel = findViewById(R.id.face_panel);
		tools_more = (GridView) findViewById(R.id.tools);
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, imagelist,
				R.layout.tools_panel_item, new String[] { "image", "txt" },
				new int[] { R.id.more_image, R.id.more_txt });
		tools_more.setAdapter(simpleAdapter);
		tools_more.setOnItemClickListener((OnItemClickListener) new ItemClickListener());
		android.view.ViewGroup.LayoutParams pp = chatBottomLayout.getLayoutParams();
		chatBottomLayout.getLayoutParams();
		singleLineHeight = pp.height;
		chatBottomLayout.setLayoutParams(pp);
		btn_yuyin = (Button) this.findViewById(R.id.btn_yuyin);
		btn_yuyin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					voidePathName = getVodiePath();
					splayer = new SpeexPlayer(voidePathName);

					splayer.endPlay();
					change_flag = false;
					if (RECODE_STATE != RECORD_ING) {
						RECODE_STATE = RECORD_ING;
						showVoiceDialog();
						try {
							if (recorderInstance == null) {
								recorderInstance = new SpeexRecorder(voidePathName);
							}
							
							Thread th = new Thread(recorderInstance);
							th.start();
							recorderInstance.setRecording(true);
							Thread.sleep(500);
							if(recorderInstance.isForbiden()) {
								if (dialog.isShowing()) {
									dialog.dismiss();
								}
								Toast.makeText(NewChatActivity.this, "录音启动失败，请检查软件权限", Toast.LENGTH_SHORT).show();
							}else {
								recordThread = new Thread(ImgThread);
								recordThread.start();
							}
						} catch (Exception e) {
							e.printStackTrace();
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
							Toast.makeText(NewChatActivity.this, "录音启动失败，请检查软件权限", Toast.LENGTH_SHORT).show();
						}
					}

					break;
				case MotionEvent.ACTION_MOVE:
					// LogUtils.e("","event.getRawY():==========="+event.getRawY());
					// LogUtils.e("","event.getY()==========="+event.getY());
					if (event.getY() > 0.00) {
						change_flag = false;
					} else {
						change_flag = true;
					}
					break;
				case MotionEvent.ACTION_UP:
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE = RECODE_ED;
						if (dialog.isShowing()) {
							dialog.dismiss();
						}

						recorderInstance.setRecording(false);
						recorderInstance = null;
						// sendVoice(voidePathName);
						// change_flag = false;
						if (recodeTime < MIX_TIME) {
							if(recorderInstance != null && !recorderInstance.isForbiden()) {
								showWarnToast();
							}
							// record.setText("按住开始录音");
							RECODE_STATE = RECORD_NO;
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {
							if (!change_flag) {
								try {
									// mr.stop();

									LogUtils.e("=========end====voideName==============：" + voidePathName);
									try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
//									sendVoice(voidePathName, 0);
									sendVoice(voidePathName);
									newURL = saveAudioMessage(mBuddyId,recodeTime + "," +voidePathName,3);
									voiceValue = 0.0;
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						}
						change_flag = false;

					}

					break;
				}
				return false;
			}
		});

		mHistoryListView = (ListView) findViewById(R.id.list);
		mMessageEdit = (EditText) findViewById(R.id.input_message);
		mMessageEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (toolsPanel.getVisibility() == View.VISIBLE) {
					toolsPanel.setVisibility(View.GONE);
				}
//				if (face_panel.getVisibility() == View.VISIBLE) {
//					face_panel.setVisibility(View.GONE);
//				}
			}
		});
		mSendBtn = (Button) findViewById(R.id.send_btn);
		mSendBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mServerType == 0){
					sendText();
				}else{
//					sendTextForServer();
					sendText();
				}
			}
		});
		mChoose_voice = (ImageButton) findViewById(R.id.choose_voice);
		mChoose_voice.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// FaceUtils.toggleFacePanel(NewChatActivity.this);
				// if(toolsPanel.getVisibility() ==View.GONE)
				// {
				// toolsPanel.setVisibility(View.VISIBLE);
				// }else{
				// toolsPanel.setVisibility(View.GONE);
				// }
				toolsPanel.setVisibility(View.GONE);
				if (mMessageEdit.getVisibility() == View.GONE) {
					mMessageEdit.setVisibility(View.VISIBLE);
					btn_yuyin.setVisibility(View.GONE);
					mChoose_voice.setBackgroundResource(R.drawable.ic_voice);
					if (mMessageEdit.getText().toString().trim().length()>0) {
						mSendBtn.setVisibility(View.VISIBLE);
						mChoosePhotoView.setVisibility(View.GONE);
					}else {
						mSendBtn.setVisibility(View.GONE);
						mChoosePhotoView.setVisibility(View.VISIBLE);
					}
					android.view.ViewGroup.LayoutParams pp = chatBottomLayout.getLayoutParams();
					chatBottomLayout.getLayoutParams();
					pp.height = singleLineHeight + 35*(mMessageEdit.getLineCount()-1);
					chatBottomLayout.setLayoutParams(pp);
				} else {
					if (!mApp.isSDCardAvailable()) {
						UIUtilities.showToast(NewChatActivity.this,
								R.string.sd_card_unavaiable);
						return;
					}
					hideKeyboard(mMessageEdit.getWindowToken());
					mMessageEdit.setVisibility(View.GONE);
					btn_yuyin.setVisibility(View.VISIBLE);
					mChoose_voice.setBackgroundResource(R.drawable.ic_menu);
					if (toolsPanel.getVisibility() == View.VISIBLE) {
						toolsPanel.setVisibility(View.GONE);
					}
					if (face_panel.getVisibility() == View.VISIBLE) {
						face_panel.setVisibility(View.GONE);
					}
					mSendBtn.setVisibility(View.GONE);
					mChoosePhotoView.setVisibility(View.VISIBLE);
					android.view.ViewGroup.LayoutParams pp = chatBottomLayout.getLayoutParams();
					chatBottomLayout.getLayoutParams();
					pp.height = singleLineHeight;
					chatBottomLayout.setLayoutParams(pp);
				}
			}
		});
		mChoosePhotoView = findViewById(R.id.choose_photo_image);
		mChoosePhotoView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (toolsPanel.getVisibility() == View.GONE) {
					toolsPanel.setVisibility(View.VISIBLE);
					/*if (mMessageEdit.getVisibility() == View.GONE) {
						mMessageEdit.setVisibility(View.VISIBLE);
						btn_yuyin.setVisibility(View.GONE);
						mChoose_voice.setBackgroundResource(R.drawable.ic_voice);
					}
					hideKeyboard(mMessageEdit.getWindowToken());*/
				} else {
					toolsPanel.setVisibility(View.GONE);
				}
//				if (face_panel.getVisibility() == View.VISIBLE) {
//					face_panel.setVisibility(View.GONE);
//				}
			}
		});
		mMessageEdit.addTextChangedListener(mTextWatcher);
	}

	@Override
	protected void onResume() {
		super.onResume();
		whenResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtils.e("onPause");
		BaseApplication.getInstance().setActiveBuddy(-1, -1);
	}
	
	private void whenResume() {
		Cursor cursor = getMessageCursor();
		if (cursor == null) {
			startQuery();
		} else {
			requeryCursor();
		}
		BaseApplication.getInstance().setActiveBuddy(mBuddyId, mChatType);
		LogUtils.e("OnResume mBuddyId:" + mBuddyId + "  mChatType:" + mChatType);
		
		ContentValues cv = new ContentValues();
		cv.put(ThreadTable.UNREAD_COUNT, 0);
		getContentResolver().update(
				ThreadTable.CONTENT_URI,
				cv,
				ThreadTable.ACCOUNT_NAME + "=? and " + ThreadTable.BUDDY_ID
						+ "=? and " + ThreadTable.CHAT_TYPE + "=? and "
						+ ThreadTable.MSG_TYPE + " in "
						+ MessageType.MSG_TYPE_CHAT,
				new String[] { getAccountName(), String.valueOf(mBuddyId),
						String.valueOf(mChatType) });
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Cursor cursor = getMessageCursor();
		if (cursor != null) {
			cursor.deactivate();
			cursor.close();
		}
		mServerType = 0;
		cancelRequery();
		if(splayer != null && splayer.isAlive()) {
			splayer.endPlay();
		}
	}

	private void cancelRequery() {
		if (mRequeryCallback != null) {
			mHandler.removeCallbacks(mRequeryCallback);
			mRequeryCallback = null;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		resolveIntent(intent);
	}
	
	private void sendTextForServer() {
		try {
			String message = mMessageEdit.getEditableText().toString();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "olSendMessage");
			params.put("chattype", "0");
			params.put("chatid",mBuddyId+"");
			params.put("fromuserid", mApp.mCurAccount.getUserId()+"");
			params.put("fromusername", mApp.mCurAccount.getUserName());
			params.put("sendcontents", message);
			params.put("module", "0");
			params.put("type", "1");
	
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					LogUtils.e("response=" + response);
					if (response.optInt("ret") == 0) {
						updateMessageStatus(newURL, response.optString("server_time"), 1);//如果调用 ，这个地方要改
						mMessageEdit.setText("");
					}else if(response.optInt("ret") == 1) {
						
					}else {
						//
						//StatusUtils.handleStatus(response, instance);
					}
				}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				//StatusUtils.handleError(arg0, instance);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		newURL = saveMessage(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendText() {
		final String message = mMessageEdit.getEditableText().toString();
		if (TextUtils.isEmpty(message)) {
			return;
		}
		mMessageEdit.setText("");
		try {
			sendMessageToOL(message,false,0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mApp.callWhenServiceConnected(new Runnable() {

			@Override
			public void run() {
//				try {
//					if (mChatType == ChatType.CHAT_TYPE_SINGLE) {
//						mApp.getChatService().send(
//								SendAction.sendText(mBuddyId, message));
//					} else {
//						mApp.getChatService().send(
//								SendAction.sendTextToGroup(mBuddyId, message,
//										groupName));
//					}
//
//					// mApp.getChatService().send(SendAction.sendHome(mBuddyId,
//					// message));
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
			}
		});
		newURL = saveMessage(message);
	}
	
	private void replyTextForServer(final long msgId) {
		try {
			String message = mMessageEdit.getEditableText().toString();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "olSendMessage");
			params.put("chattype", "0");
			params.put("chatid",mBuddyId+"");
			params.put("fromuserid", mApp.mCurAccount.getUserId()+"");
			params.put("fromusername", mApp.mCurAccount.getUserName());
			params.put("sendcontents", message);
			params.put("module", "0");
			params.put("type", "1");
	
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					LogUtils.e("response=" + response);
					if (response.optInt("ret") == 0) {
						updateMessage(msgId, response.optString("server_time"), 1);
					}else if(response.optInt("ret") == 1) {
						
					}else {
						//
						//StatusUtils.handleStatus(response, instance);
					}
				}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				//StatusUtils.handleError(arg0, instance);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ReplyText(final String message, final long msgId) {
		try {
			sendMessageToOL(message,true,msgId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		if (mChatType == ChatType.CHAT_TYPE_SINGLE) {
//			mApp.getChatService().send(
//					SendAction.sendText(mBuddyId, message));
//		} else {
//			mApp.getChatService().send(
//					SendAction.sendTextToGroup(mBuddyId, message,
//							groupName));
//		}
	}

	private void sendPic(final String filePath, final long id) {
		mApp.callWhenServiceConnected(new Runnable() {

			@Override
			public void run() {
				try {
					mApp.getChatService().sendPicFile(mBuddyId, filePath,
							mChatType, id, groupName);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void sendVoice(final String filePath, final long id) {
		mApp.callWhenServiceConnected(new Runnable() {

			@Override
			public void run() {
				try {

					mApp.getChatService().sendVoiceFile(mBuddyId, filePath,
							mChatType, (int) recodeTime, id, groupName);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void updateMessage(long msgId, String server_time, int msgStatus) {
		ContentValues cv = new ContentValues();
		cv.put(MessageTable.OUTBOUND_STATUS,msgStatus);
		cv.put(MessageTable.SENT_TIME, server_time);//因为按照SENT_TIME排序
		getContentResolver()
				.update(MessageTable.CONTENT_URI, cv, MessageTable._ID + "=?",
						new String[] { String.valueOf(msgId) });
	}

	private void updateChat() {
		if (mMessageAdapter == null) {
			mMessageAdapter = new MessageAdapter(this, null);
			mHistoryListView.setAdapter(mMessageAdapter);
		}

//		long oldBuddyId = mBuddyId;

//		if (mBuddyId != oldBuddyId) {
			startQuery(); 
			mMessageEdit.setText("");
//		}

		setupTitle();
	}

	private void setupTitle() {
		DataHelper helper = DataHelper.getHelper(this);
		if (mChatType == ChatType.CHAT_TYPE_SINGLE) {
//			try {
//				QueryBuilder<Contact, Integer> queryBuilder = helper.getContactData().queryBuilder();  
//				queryBuilder.where().eq("loginName", mAccount.getLoginname()).and().eq("id", mBuddyId);  
//				mContact = helper.getContactData().queryForFirst(queryBuilder.prepare());
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			LogUtils.e("^^^^^^^^^^mBuddyId^^^^^^^", "" + mBuddyId);
//			if (mContact != null) {
				setTitle(mBuddyName);
//			}
		} else {
			ClassRoom group = null;
			try {
				QueryBuilder<ClassRoom, Integer> queryBuilder = helper.getClassRoomData().queryBuilder();
				queryBuilder.where().eq("loginName", mAccount.getLoginname()).and().eq("id", mBuddyId);  
				group = helper.getClassRoomData().queryForFirst(queryBuilder.prepare());
			} catch (SQLException e) {
				e.printStackTrace();
			}
            if(TextUtils.isEmpty(groupName))groupName = mBuddyName;
			setTitle(group != null ? group.getName() : groupName);
//			title.setText(groupName);
		}

	}

	private void startQuery() {
		if (mQueryHandler == null) {
			mQueryHandler = new QueryHandler(this);
		} else {
			mQueryHandler.cancelOperation(QUERY_TOKEN);
		}

		Uri uri = ContentUris.withAppendedId(
				MessageTable.CONTENT_URI_MESSAGES_BY_BUDDY_ID, mBuddyId);
		mQueryHandler.startQuery(QUERY_TOKEN, null, uri, MESSAGE_PROJECTION,
				MessageTable.ACCOUNT_NAME + "=? AND " + MessageTable.CHAT_TYPE
						+ "=?",
				new String[] { getAccountName(), String.valueOf(mChatType) },
				MessageTable.SENT_TIME);
		// mQueryHandler.startQuery(QUERY_TOKEN,
		// null,
		// uri,
		// MESSAGE_PROJECTION,
		// MessageTable.ACCOUNT_NAME + "=?",
		// new String[] {getAccountName()},
		// null);
	}

	private void requeryCursor() {
		if (mMessageAdapter.isScrolling()) {
			mMessageAdapter.setNeedRequeryCursor(true);
		}
		Cursor cursor = getMessageCursor();
		if (cursor != null) {
			cursor.requery();
		}
	}

	private Cursor getMessageCursor() {
		return mMessageAdapter == null ? null : mMessageAdapter.getCursor();
	}

	@Override
	public void onActivityResult(int request, int result, Intent data) {
		super.onActivityResult(request, result, data);

		LogUtils.d("================request:" + request + "result:" + result);
		if (request == REQ_TAKE_PHOTO) {
			if (result == RESULT_OK) {
				onTakePhotoSucced(data);
			}
		} else if (request == REQ_LOCAL_ALBUM) {
			if (result == RESULT_OK) {
				onSelectPhotoSucced(data);
			}
		} else if (request == REQ_EDIT_PHOTO) {
			if (result == RESULT_OK) {
				onEditImageSucced(data);
			}
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

	private void onSelectPhotoSucced(Intent data) {
		LogUtils.d("onSelectPhotoSucced:" + data);
		//Uri uri = data.getData();
		// String filePath = ImageUtils.getImagePathFromProvider(this, uri);
		String filePath = FileUtils.getPath(this, data.getData());
		LogUtils.d("filePath:" + filePath);
		//sendPhoto(filePath);
		// sendPic(uri.getPath());
		startActivityForResult(
				BrowseImageActivity.getEditIntent(this,
						Uri.fromFile(new File(filePath))), REQ_EDIT_PHOTO);

	}

	private void onEditImageSucced(Intent data) {
		LogUtils.d("onEditImageSucced:" + data);
		Uri uri = data.getData();
		// sendPic(uri.getPath());

		String ly_time = new SimpleDateFormat("yyyyMMddHHmmss", locale).format(Calendar
				.getInstance().getTime());
		String filePath = mApp.getWorkspaceImage().toString() + "/" + ly_time
				+ ".jpeg";
		try {
			ImageUtils.savePictoFile(uri, filePath);
			//sendPic(filePath, 0);
			sendPhoto(filePath);
			newURL = savePicMessage(mBuddyId,filePath,2);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private final class QueryHandler extends AsyncQueryHandler {

		public QueryHandler(Context context) {
			super(context.getContentResolver());
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (mMessageAdapter == null) {
				mMessageAdapter = new MessageAdapter(NewChatActivity.this, null);
			}
			mMessageAdapter.changeCursor(cursor);
		}
	}

	private class RequeryCallback implements Runnable {

		@Override
		public void run() {
			requeryCursor();
		}
	}

	private static final int TYPE_IN_TEXT = 0;
	private static final int TYPE_IN_PIC = 1;
	private static final int TYPE_OUT_TEXT = 2;
	private static final int TYPE_OUT_PIC = 3;
	private static final int TYPE_IN_AUDIO = 4;
	private static final int TYPE_OUT_AUDIO = 5;
	private static final int SHOW_TIME_INTERVAL = 5;

	private class MessageAdapter extends CursorAdapter implements
			OnScrollListener {

		private int midColumn;
		private int mMsgStatusColumn;
		private int mBodyColumn;
		private int mSenderidColum;
		private int mReceiveTimeColum;
		private int mSendTimeColum;
		
		private int mScrollState;
		private boolean mNeedRequeryCursor;

		public MessageAdapter(Context context, Cursor c) {
			super(context, c);
			if (c != null) {
				resolveColumnIndex(c);
			}
		}

		@Override
		public void changeCursor(Cursor cursor) {
			super.changeCursor(cursor);
			if (cursor != null) {
				resolveColumnIndex(cursor);
			}
		}

		private void resolveColumnIndex(Cursor cursor) {
			midColumn = cursor.getColumnIndexOrThrow(MessageTable._ID);
			mBodyColumn = cursor.getColumnIndexOrThrow(MessageTable.BODY);
			mSenderidColum = cursor.getColumnIndex(MessageTable.SENDER_ID);
			mMsgStatusColumn = cursor
					.getColumnIndex(MessageTable.OUTBOUND_STATUS);
			//@@@@@@@@
			mReceiveTimeColum = cursor.getColumnIndex(MessageTable.RECEIVED_TIME);
			mSendTimeColum = cursor.getColumnIndex(MessageTable.SENT_TIME);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			View view = null;
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			int viewType = getItemViewType(cursor.getPosition());
			if (viewType == TYPE_IN_TEXT) {
				view = layoutInflater.inflate(R.layout.chat_message_in_text,
						parent, false);
			} else if (viewType == TYPE_IN_PIC) {
				view = layoutInflater.inflate(R.layout.chat_message_in_pic,
						parent, false);
			} else if (viewType == TYPE_OUT_TEXT) {
				view = layoutInflater.inflate(R.layout.chat_message_out_text,
						parent, false);
			} else if (viewType == TYPE_OUT_PIC) {
				view = layoutInflater.inflate(R.layout.chat_message_out_pic,
						parent, false);
			} else if (viewType == TYPE_IN_AUDIO) {
				view = layoutInflater.inflate(R.layout.chat_message_in_voide,
						parent, false);
			} else if (viewType == TYPE_OUT_AUDIO) {
				view = layoutInflater.inflate(R.layout.chat_message_out_voide,
						parent, false);
			}

			return view;
		}

		/**
		 * 获取游标当前位置消息的时间的Date对象，如果是发出的消息则取发送时间，如果是收到的消息则取接收时间
		 * 
		 * @param cursor
		 *            即时消息游标
		 * @return Date 消息时间
		 * @author lyz
		 * */
		private String getMsgDate_new(Cursor cursor) {
			int isIncoming = cursor.getInt(cursor
					.getColumnIndexOrThrow(MessageTable.IS_INBOUND));
			String msgDateVal = "";
			/*if (isIncoming == MessageType.INCOMING) {
				msgDateVal = cursor.getString(cursor
						.getColumnIndexOrThrow(MessageTable.RECEIVED_TIME));
			} else {*/
				msgDateVal = cursor.getString(cursor
						.getColumnIndexOrThrow(MessageTable.SENT_TIME));
			//}
			try {
				Date d = dataFormat.parse(msgDateVal);
				msgDateVal = dateSpanFormat.format(d);
			} catch (ParseException e) {
				e.printStackTrace();
				msgDateVal = "";
			} finally {
				return msgDateVal;
			}
		}

		private void setDateSpan(Cursor cursor, View view, Calendar currentCal) {
			String msgDate = this.getMsgDate_new(cursor);
			
			RelativeLayout chat_date_ll = (RelativeLayout) view
					.findViewById(R.id.chat_date_ll);
			chat_date_ll.setVisibility(View.VISIBLE);
			TextView dateView = (TextView) view.findViewById(R.id.date_text);
			dateView.setVisibility(View.VISIBLE);
			dateView.setText(msgDate);
		}

		/**
		 * @param formerCal  现在的数据时间
		 * @param laterCal 原来的时间
		 * @return
		 */
		private boolean needShowTime_new(String formerCal,String laterCal){
		 Boolean time_flag = false;
		 try{
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
			  Date date1 = sd.parse(formerCal);
			  Date date2 = sd.parse(laterCal);
			long  s1 = date1.getTime() ;//时间的毫秒
			long s2 = date2.getTime() ;
			double day = (s1-s2)/60/1000;
			if(day>6){
				time_flag= true;
			}else{
				time_flag= false;
			}
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return time_flag;
		}

		@Override
		public void bindView(View view, Context context, final Cursor cursor) {
			final int position = cursor.getPosition();
			// 取系统当前时间作为参照，判断消息是否是今天的
			Date currentDate = new Date(System.currentTimeMillis());
			Calendar currentCal = Calendar.getInstance();
			currentCal.setTime(currentDate);
			currentCal.get(Calendar.DAY_OF_MONTH);
			// 判断当前消息是否是列表中第一条消息（最早的消息）
			boolean first = !cursor.moveToPrevious();
			// 第一条消息一定需要显示时间栏
			if (first) {
				cursor.moveToFirst();
				setDateSpan(cursor, view, currentCal);
			} else {
				// 取当前消息的前一条的时间
				String preDate = cursor.getString(cursor
						.getColumnIndexOrThrow(MessageTable.SENT_TIME));
				cursor.moveToNext();
				String oriDate = cursor.getString(cursor
						.getColumnIndexOrThrow(MessageTable.SENT_TIME));
				if (needShowTime_new(oriDate, preDate)) {
					setDateSpan(cursor, view, currentCal);
				} else {
					RelativeLayout chat_date_ll = (RelativeLayout) view.findViewById(R.id.chat_date_ll);
					chat_date_ll.setVisibility(View.GONE);
				}
			}
			
			int viewType = getItemViewType(cursor.getPosition());
			long senderid = cursor.getLong(mSenderidColum);
			Contact contact = null;
			if (mChatType == ChatType.CHAT_TYPE_SINGLE) {
				contact = mContact;
			} else {
				DataHelper helper = DataHelper.getHelper(NewChatActivity.this);
				try {
					QueryBuilder<Contact, Integer> queryBuilder = helper.getContactData().queryBuilder();
					queryBuilder.where().eq("loginName", mAccount.getLoginname()).and().eq("id", senderid);  
					contact = helper.getContactData().queryForFirst(queryBuilder.orderBy("usertype", true).prepare());
				} catch (SQLException e) {
					e.printStackTrace();
				}  
			}
			
			CircularImage avatarView = (CircularImage) view.findViewById(R.id.chat_avatar_image);
			
			if (viewType == TYPE_IN_TEXT) {
				setAvatar(senderid, contact, avatarView);
				TextView msgView = (TextView) view.findViewById(R.id.chat_message_text);
				//@@@@@@@@
//				msgView.setText(FaceUtils.replaceFace(NewChatActivity.this, cursor.getString(mBodyColumn)) + ";" + cursor.getString(mSendTimeColum));
				msgView.setText(FaceUtils.replaceFace(NewChatActivity.this, cursor.getString(mBodyColumn)));
				final String content = cursor.getString(mBodyColumn);
				final long _id = cursor.getLong(midColumn);
				msgView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						showChooseChatDialog(NewChatActivity.this, content,
								TYPE_IN_TEXT, 0, _id,position);
						return true;
					}
				});
			} else if (viewType == TYPE_IN_PIC) {
				setAvatar(senderid, contact, avatarView);
				ImageView imageView = (ImageView) view.findViewById(R.id.chat_message_image);
				final String imageUrl = cursor.getString(mBodyColumn);
				DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc().showStubImage(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).build();
				LogUtils.e("imageUrl===========:"+imageUrl);
				final String[] pic_url=imageUrl.split("#"); 
				if(pic_url.length>1){
					BaseApplication.getInstance().imageLoader.displayImage(olFilePath +pic_url[0], imageView, defaultOptions);
					imageView.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivity(BrowseImageActivity.getViewIntent(
									NewChatActivity.this, Uri.parse(olFilePath +pic_url[1])));
						}
					});
				}else{
					BaseApplication.getInstance().imageLoader.displayImage(olFilePath + imageUrl, imageView, defaultOptions);
					imageView.setOnClickListener(new View.OnClickListener() {
	
						@Override
						public void onClick(View v) {
							startActivity(BrowseImageActivity.getViewIntent(
									NewChatActivity.this, Uri.parse(olFilePath + imageUrl)));
						}
					});
				}
			} else if (viewType == TYPE_OUT_TEXT) {
				setMyAvatar(avatarView);
				TextView msgView = (TextView) view.findViewById(R.id.chat_message_text);
				ImageView chat_message_error = (ImageView) view.findViewById(R.id.chat_message_error);
				//@@@@@@@@
//				msgView.setText(FaceUtils.replaceFace(NewChatActivity.this, cursor.getString(mBodyColumn)) + ";" + cursor.getString(mSendTimeColum));
				msgView.setText(FaceUtils.replaceFace(NewChatActivity.this, cursor.getString(mBodyColumn)));
				final int msgstatue = cursor.getInt(mMsgStatusColumn);
				if (msgstatue == MessageType.MSG_STATUS_ERROR) {
					chat_message_error.setVisibility(View.VISIBLE);
				} else {
					chat_message_error.setVisibility(View.GONE);
				}
				DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc().showStubImage(R.drawable.default_user).showImageForEmptyUri(R.drawable.default_user).showImageOnFail(R.drawable.default_user).build();
				BaseApplication.getInstance().imageLoader.displayImage(mAccount.getAvatar(), avatarView, defaultOptions);
				final String content = cursor.getString(mBodyColumn);
				final long _id = cursor.getLong(midColumn);
				
				view.setOnLongClickListener(new View.OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						showChooseChatDialog(NewChatActivity.this, content,
								TYPE_OUT_TEXT, msgstatue, _id,position);
						return true;
					}
				});
			} else if (viewType == TYPE_OUT_PIC) {
				setMyAvatar(avatarView);
				View progressBar = view.findViewById(R.id.chat_message_progressbar);
				ImageView imageView = (ImageView) view.findViewById(R.id.chat_message_image);
				ProgressBar chat_message_progressbar = (ProgressBar) view.findViewById(R.id.chat_message_progressbar);
				ImageView chat_message_error = (ImageView) view.findViewById(R.id.chat_message_error);
				final String imageUrl = cursor.getString(mBodyColumn);
				DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc()
						.showStubImage(R.drawable.default_image)
						.showImageForEmptyUri(R.drawable.default_image)
						.showImageOnFail(R.drawable.default_image).build();
				BaseApplication.getInstance().imageLoader.displayImage("file:///"+imageUrl, imageView, defaultOptions);
				int msgstatue = cursor.getInt(mMsgStatusColumn);
				setOutMsgStatue(msgstatue, chat_message_progressbar, chat_message_error);

				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(BrowseImageActivity.getViewIntent(
								NewChatActivity.this, Uri.parse("file:///"+imageUrl)));
					}
				});
				if (MessageType.MSG_STATUS_ERROR == msgstatue) {
					final long _id = cursor.getLong(midColumn);
					imageView.setOnLongClickListener(new View.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							showChooseChatFileDialog(
									NewChatActivity.this, TYPE_OUT_PIC,
									imageUrl.replace("file://", ""),
									_id,position);
							return true;
						}
					});
				} else {

					final long _id = cursor.getLong(midColumn);
					imageView.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							showChooseChatFileDialog(
									NewChatActivity.this, TYPE_OUT_PIC,
									imageUrl.replace("file://", ""),
									_id,position);
							return true;
						}
					});
				}
			} else if (viewType == TYPE_IN_AUDIO) {
				setAvatar(senderid, contact, avatarView);
				final ImageView imageView = (ImageView) view.findViewById(R.id.chat_message_image);
				final TextView voidetime = (TextView) view.findViewById(R.id.voidetime);
				String BodyColumn = cursor.getString(mBodyColumn);
				LogUtils.d(TAG + "TYPE_IN_AUDIO BodyColumn =="+BodyColumn);
				final String m_msg[] = BodyColumn.split("#");
				if (m_msg.length == 2) {

				    String stime = m_msg[1];
				    if(!StringUtils.isEmpty(stime))
				    {
				        int ll = Integer.valueOf(stime);
				        String showtime = new DecimalFormat("0").format((double)ll/1000);
	                    voidetime.setText(showtime + "\"");
				    }
				    
				} else {
					voidetime.setVisibility(View.GONE);
				}
				imageView.setBackgroundResource(R.drawable.chat_voice_playing_bg);
				imageView.setImageResource(R.drawable.chat_in_voice_playing_f3);
				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						new Thread() {
							@Override
							public void run() {
								String fileUrl = "";
								if (m_msg.length == 2) {
									fileUrl = m_msg[0];
								} else {
									fileUrl = m_msg[0];
								}
								String fileURL = java.net.URLDecoder.decode(olFilePath + fileUrl);
								String name = fileURL.substring(fileURL.lastIndexOf("/") + 1);
								voideName = mApp.getWorkspaceVoice().getAbsolutePath() + "/" + name;
								fileHelper.down_file(fileURL, mApp.getWorkspaceVoice().getAbsolutePath(), name);
								ant_imageView = imageView;
							}
						}.start();

					}
				});
				final long _id = cursor.getLong(midColumn);
				imageView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						showChooseChatFileDialog(
								NewChatActivity.this,
								TYPE_IN_AUDIO,
								m_msg[m_msg.length == 2 ? 1 : 0],
								_id,position);
						return true;
					}
				});
			} else if (viewType == TYPE_OUT_AUDIO) {
				setMyAvatar(avatarView);
				final ImageView imageView = (ImageView) view.findViewById(R.id.chat_message_image);
				// final String fileName = cursor.getString(mBodyColumn);
				imageView.setBackgroundResource(R.drawable.chat_voice_playing_bg);
				imageView.setImageResource(R.drawable.chat_out_voice_playing_f3);
				ProgressBar chat_message_progressbar = (ProgressBar) view.findViewById(R.id.chat_message_progressbar);
				ImageView chat_message_error = (ImageView) view.findViewById(R.id.chat_message_error);
				int msgstatue = cursor.getInt(mMsgStatusColumn);
				setOutMsgStatue(msgstatue, chat_message_progressbar,chat_message_error);

				final TextView voidetime = (TextView) view.findViewById(R.id.voidetime);

				String BodyColumn = cursor.getString(mBodyColumn);
				LogUtils.e(TAG + " TYPE_OUT_AUDIO BodyColumn == "+BodyColumn);
				// NewChatActivityBodyColumn == 5.7999988,/storage/emulated/0/gnx/voice/13813883236_20150418144011.spx

				final String m_msg[] = BodyColumn.split(",");
				if (m_msg.length == 2) {
				    String stime = m_msg[0];
                    if(!StringUtils.isEmpty(stime))
                    {
                        double ll = Double.valueOf(stime);
//                        LogUtils.e("ll =="+ll);
                        long showtime = (long)( Math.ceil(ll));
//                        LogUtils.e("showtime =="+showtime);
                        voidetime.setText(showtime + "\"");
                    }
//					voidetime.setText(m_msg[0] + "\"");
				} else {
					voidetime.setVisibility(View.GONE);
				}

				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						String fileUrl = "";
						if (m_msg.length == 2) {
							fileUrl = m_msg[1];
						} else {
							fileUrl = m_msg[0];
						}
						splayer = new SpeexPlayer(fileUrl);
						splayer.endPlay();
						splayer.startPlay();
						playAudioAnimation(imageView, MESSAGE_OUT);

					}
				});
				if (MessageType.MSG_STATUS_ERROR == msgstatue) {
					final long _id = cursor.getLong(midColumn);
					imageView.setOnLongClickListener(new View.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							showChooseChatFileDialog(
									NewChatActivity.this,
									TYPE_OUT_AUDIO,
									m_msg[m_msg.length == 2 ? 1 : 0],
									_id,position);
							return true;
						}
					});
				} else {
					final long _id = cursor.getLong(midColumn);
					imageView.setOnLongClickListener(new View.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							showChooseChatFileDialog(
									NewChatActivity.this,
									TYPE_OUT_AUDIO,
									m_msg[m_msg.length == 2 ? 1 : 0],
									_id,position);
							return true;
						}
					});
				}
			}
			TextView sendername = (TextView) view.findViewById(R.id.username);
			if (mChatType == ChatType.CHAT_TYPE_GROUP) {
				sendername.setVisibility(View.VISIBLE);
				switch (viewType) {
				case TYPE_IN_AUDIO:
				case TYPE_IN_PIC:
				case TYPE_IN_TEXT:
					sendername.setText(contact != null ? contact.getName() : "");
					break;
				case TYPE_OUT_AUDIO:
				case TYPE_OUT_PIC:
				case TYPE_OUT_TEXT:
					sendername.setVisibility(View.GONE);
//					sendername.setText(getAccountName());
					break;
				default:
					break;
				}
			}else {
				sendername.setVisibility(View.GONE);
			}
		}
		
		private void setOutMsgStatue(int statue, ProgressBar progressBar,
				ImageView iv_error) {
			switch (statue) {
			case MessageType.MSG_STATUS_ERROR:
				progressBar.setVisibility(View.GONE);
				iv_error.setVisibility(View.VISIBLE);
				break;
			case MessageType.MSG_STATUS_WAITING:
				progressBar.setVisibility(View.VISIBLE);
				iv_error.setVisibility(View.GONE);
				break;
			case MessageType.MSG_STATUS_SENT:
			case MessageType.MSG_STATUS_ARRIVED:
			case MessageType.MSG_STATUS_READED:
				progressBar.setVisibility(View.GONE);
				iv_error.setVisibility(View.GONE);
				break;

			default:
				break;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 6;
		}

		@Override
		public int getItemViewType(int position) {
			Cursor cursor = (Cursor) getItem(position);
			int isIncoming = cursor.getInt(cursor
					.getColumnIndexOrThrow(MessageTable.IS_INBOUND));
			int msgType = cursor.getInt(cursor
					.getColumnIndexOrThrow(MessageTable.TYPE));
			if (isIncoming == MessageType.INCOMING
					&& msgType == MessageType.TYPE_MSG_TEXT) {
				return TYPE_IN_TEXT;
			} else if (isIncoming == MessageType.INCOMING
					&& msgType == MessageType.TYPE_MSG_PIC) {
				return TYPE_IN_PIC;
			} else if (isIncoming == MessageType.OUTGOINT
					&& msgType == MessageType.TYPE_MSG_PIC) {
				return TYPE_OUT_PIC;
			} else if (isIncoming == MessageType.OUTGOINT
					&& msgType == MessageType.TYPE_MSG_TEXT) {
				return TYPE_OUT_TEXT;
			} else if (isIncoming == MessageType.INCOMING
					&& msgType == MessageType.TYPE_MSG_AUDIO) {
				return TYPE_IN_AUDIO;
			} else if (isIncoming == MessageType.OUTGOINT
					&& msgType == MessageType.TYPE_MSG_AUDIO) {
				return TYPE_OUT_AUDIO;
			}
			return super.getItemViewType(position);
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			int oldState = mScrollState;
			mScrollState = scrollState;

			if (oldState == OnScrollListener.SCROLL_STATE_FLING) {
				if (mNeedRequeryCursor) {
					requeryCursor();
				} else {
					notifyDataSetChanged();
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}

		public boolean isScrolling() {
			return mScrollState == OnScrollListener.SCROLL_STATE_FLING;
		}

		public void setNeedRequeryCursor(boolean requeryCursor) {
			mNeedRequeryCursor = requeryCursor;
		}
	}

	// 录音时显示Dialog
	void showVoiceDialog() {
		if (dialog == null) {
			dialog = new Dialog(NewChatActivity.this, R.style.DialogStyle);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			dialog.setContentView(R.layout.my_dialog);
			dialog_img = (ImageView) dialog.findViewById(R.id.dialog_img);
			dialog_txt = (TextView) dialog.findViewById(R.id.dialog_txt);
		}
		dialog.dismiss();
		dialog.show();
	}

	// 录音时间太短时Toast显示
	void showWarnToast() {
		Toast toast = new Toast(NewChatActivity.this);
		LinearLayout linearLayout = new LinearLayout(NewChatActivity.this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 20, 20, 20);

		// 定义一个ImageView
		ImageView imageView = new ImageView(NewChatActivity.this);
		imageView.setImageResource(R.drawable.voice_to_short); // 图标

		TextView mTv = new TextView(NewChatActivity.this);
		mTv.setText("时间太短   录音失败");
		mTv.setTextSize(14);
		mTv.setTextColor(Color.WHITE);// 字体颜色
		// mTv.setPadding(0, 10, 0, 0);

		// 将ImageView和ToastView合并到Layout中
		linearLayout.addView(imageView);
		linearLayout.addView(mTv);
		linearLayout.setGravity(Gravity.CENTER);// 内容居中
		linearLayout.setBackgroundResource(R.drawable.record_bg);// 设置自定义toast的背景

		toast.setView(linearLayout);
		toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间 100为向下移100dp
		toast.show();

		change_flag = true;
	}

	// 录音计时线程
	void mythread() {
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	// 录音Dialog图片随声音大小切换
	void setDialogImage() {

		if (change_flag) {
			dialog_img.setImageResource(R.drawable.voicesearch_nonetwork);
			dialog_txt.setText(R.string.chat_vodio_dialog_down);
			dialog_txt.setTextColor(Color.RED);

		} else {
			dialog_txt.setText(R.string.chat_vodio_dialog_up);
			dialog_txt.setTextColor(Color.WHITE);
			if (recorderInstance != null) {
				voiceValue = recorderInstance.value;
			}
			LogUtils.e("====" + voiceValue);
			if (voiceValue <= 2.0) {
				dialog_img.setImageResource(R.drawable.record_animate_01);
			} else if (voiceValue > 2.0 && voiceValue <= 4) {
				dialog_img.setImageResource(R.drawable.record_animate_02);
			} else if (voiceValue > 4.0 && voiceValue <= 6) {
				dialog_img.setImageResource(R.drawable.record_animate_03);
			} else if (voiceValue > 6.0 && voiceValue <= 8) {
				dialog_img.setImageResource(R.drawable.record_animate_04);
			} else if (voiceValue > 8.0 && voiceValue <= 10) {
				dialog_img.setImageResource(R.drawable.record_animate_05);
			} else if (voiceValue > 10.0 && voiceValue <= 12) {
				dialog_img.setImageResource(R.drawable.record_animate_06);
			} else if (voiceValue > 12.0 && voiceValue <= 14) {
				dialog_img.setImageResource(R.drawable.record_animate_07);
			} else if (voiceValue > 14.0 && voiceValue <= 16.0) {
				dialog_img.setImageResource(R.drawable.record_animate_08);
			} else if (voiceValue > 16.0 && voiceValue <= 20.0) {
				dialog_img.setImageResource(R.drawable.record_animate_09);
			} else if (voiceValue > 20.0 && voiceValue <= 23.0) {
				dialog_img.setImageResource(R.drawable.record_animate_10);
			} else if (voiceValue > 23.0 && voiceValue <= 26.0) {
				dialog_img.setImageResource(R.drawable.record_animate_11);
			} else if (voiceValue > 26.0 && voiceValue <= 30.0) {
				dialog_img.setImageResource(R.drawable.record_animate_12);
			} else if (voiceValue > 30.0 && voiceValue <= 35.0) {
				dialog_img.setImageResource(R.drawable.record_animate_13);
			} else if (voiceValue > 35.0) {
				dialog_img.setImageResource(R.drawable.record_animate_14);
			}
		}
	}

	// 录音线程
	private Runnable ImgThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (RECODE_STATE == RECORD_ING) {
				if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
					imgHandle.sendEmptyMessage(0);
				} else {
					try {
						Thread.sleep(200);
						recodeTime += 0.2;
						if (RECODE_STATE == RECORD_ING) {
							// voiceValue = mr.getAmplitude();

							imgHandle.sendEmptyMessage(1);

						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					// 录音超过15秒自动停止
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE = RECODE_ED;
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						try {
							// mr.stop();
							recorderInstance.setRecording(false);
							recorderInstance = null;
							change_flag = false;
							voiceValue = 0.0;
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (recodeTime < 1.0) {
							showWarnToast();
							// record.setText("按住开始录音");
							RECODE_STATE = RECORD_NO;
						} else {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							sendVoice(voidePathName, 0);
							// record.setText("录音完成!点击重新录音");
							// luyin_txt.setText("录音时间："+((int)recodeTime));
							// luyin_path.setText("文件路径："+getAmrPath());
						}
					}
					break;
				case 1:
					setDialogImage();
					break;
				default:
					break;
				}

			}
		};
	};

	private void playAudioAnimation(final ImageView imageView, final int inOrOut) {
		// 定时器检查播放状态
		stopTimer();
		mTimer = new Timer();
		// 将要关闭的语音图片归位
		if (audioAnimationHandler != null) {
			Message msg = new Message();
			msg.what = 3;
			audioAnimationHandler.sendMessage(msg);
		}

		audioAnimationHandler = new AudioAnimationHandler(imageView, inOrOut);
		mTimerTask = new TimerTask() {
			public boolean hasPlayed = false;

			@Override
			public void run() {
				if (splayer.isAlive()) {
					hasPlayed = true;
					index = (index + 1) % 3;
					Message msg = new Message();
					msg.what = index;
					audioAnimationHandler.sendMessage(msg);
				} else {
					// 当播放完时
					Message msg = new Message();
					msg.what = 3;
					audioAnimationHandler.sendMessage(msg);
					// 播放完毕时需要关闭Timer等
					if (hasPlayed) {
						stopTimer();
					}
				}
			}
		};
		// 调用频率为500毫秒一次
		mTimer.schedule(mTimerTask, 0, 500);
	}

	class AudioAnimationHandler extends Handler {
		ImageView imageView;
		// 判断是左对话框还是右对话框
		private boolean isLeft;

		public AudioAnimationHandler(ImageView imageView, int inOrOut) {
			this.imageView = imageView;
			// 判断是左对话框还是右对话框 我这里是在前面设置ScaleType来表示的
			// isleft=imageView.getScaleType()==ScaleType.FIT_START?true:false;
			this.isLeft = inOrOut == 1 ? true : false;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 根据msg.what来替换图片，达到动画效果
			switch (msg.what) {
			case 0:
				imageView
						.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f1
								: R.drawable.chat_out_voice_playing_f1);
				break;
			case 1:
				imageView
						.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f2
								: R.drawable.chat_out_voice_playing_f2);
				break;
			case 2:
				imageView
						.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f3
								: R.drawable.chat_out_voice_playing_f3);
				break;
			default:
				imageView
						.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f3
								: R.drawable.chat_out_voice_playing_f3);
				break;
			}
		}

	}

	/**
	 * 停止
	 */
	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}

	}

	private String getVodiePath() {
		String fileURL = mApp.getWorkspaceVoice().getAbsolutePath();
		String ly_time = new SimpleDateFormat("yyyyMMddHHmmss",locale).format(Calendar
				.getInstance().getTime());
		fileURL = fileURL + "/"
				+ mAccount.getLoginname() + "_"
				+ ly_time + ".spx";
		Log.i("", "=============fileURL:" + fileURL);

		return fileURL;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (toolsPanel.getVisibility() == View.VISIBLE
					|| face_panel.getVisibility() == View.VISIBLE) {
				toolsPanel.setVisibility(View.GONE);
				face_panel.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	public void showChooseChatDialog(final Activity activity,
			final String content, final int viewtype, int msgstatue,
			final long msgId,final int position) {
		int itemresid = 0;
		
		if (viewtype == TYPE_IN_TEXT) {
			itemresid = R.array.choose_chat_method_copy;
			allItems=activity.getResources().getStringArray(R.array.choose_chat_method_copy);
		} else if (viewtype == TYPE_OUT_TEXT) {
			if (msgstatue == MessageType.MSG_STATUS_ERROR) {
				itemresid = R.array.choose_chat_method_copyandreply;
				allItems=activity.getResources().getStringArray(R.array.choose_chat_method_copyandreply);
			} else {
				itemresid = R.array.choose_chat_method_copy;
				allItems=activity.getResources().getStringArray(R.array.choose_chat_method_copy);			}

		}
		//获取完整的ARRAY 用来判断点击项目
//		final String[] allItems=activity.getResources().getStringArray(R.array.choose_chat_method_copyandreply);
		new AlertDialog.Builder(activity).setTitle(R.string.chat_hislist)
				.setItems(itemresid, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case CHOOSE_CHAT_REPLY:
							if("删除".equals(allItems[which])){
								//删除操作
								LogUtils.e("=========删除此聊天条目========"+msgId);
								deleteMsg(msgId, position);
								
							}else{
								if(mServerType == 0){
									ReplyText(content, msgId);
								}else{
									replyTextForServer(msgId);
								}
							}
							
							break;
						case CHOOSE_CHAT_COPY:
							int currentapiVersion = android.os.Build.VERSION.SDK_INT;
							if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
								android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity
										.getSystemService(activity.CLIPBOARD_SERVICE);
								ClipData clip = ClipData.newPlainText("label",
										content);
								clipboard.setPrimaryClip(clip);
							} else {
								android.text.ClipboardManager clipboard = (android.text.ClipboardManager) activity
										.getSystemService(activity.CLIPBOARD_SERVICE);
								clipboard.setText(content);
							}
							break;
							//删除
						case CHOOSE_CHAT_DELETE:
							LogUtils.e("=========删除此聊天条目========"+msgId);
							deleteMsg(msgId, position);
//							mHistoryListView.notifyDataSetChanged();
							break;
						}
						dialog.dismiss();
					}
				}).show();
	}
	//删除一条聊天
	private void deleteMsg(long msgId,int  position){
		getContentResolver().delete(MessageTable.CONTENT_URI,
				MessageTable.ACCOUNT_NAME
						+ "=? AND "
						+ MessageTable.BUDDY_ID
						+ "=? AND "
						+ MessageTable.CHAT_TYPE
						+ "=? AND "
						+ MessageTable._ID
						+ "=? ",
				new String[] {
						getAccountName(),
						String.valueOf(mBuddyId),
						String.valueOf(mChatType),
						String.valueOf(msgId)
						});
		getMessageCursor().requery();
//		onLongClickCursor.getPosition();
		
		
		
		if(0<mMessageAdapter.getCount()&&position==mMessageAdapter.getCount()){
//			position
			Cursor cursor=	mMessageAdapter.getCursor();
			cursor.moveToPosition(mMessageAdapter.getCount()-1);
			String mBodyStr=cursor.getString(cursor.getColumnIndexOrThrow(MessageTable.BODY));
			int type  =  cursor.getInt(cursor.getColumnIndexOrThrow(MessageTable.TYPE));
			// 更新thread内容
			ContentValues cv = new ContentValues();
			cv.put(ThreadTable.MSG_BODY, mBodyStr);
			if(MessageType.TYPE_MSG_TEXT==type){
				//文本不做处理
				cv.put(ThreadTable.MSG_TYPE, 1);
			}else if (MessageType.TYPE_MSG_AUDIO==type){
				//语音
				cv.put(ThreadTable.MSG_TYPE, 2);
			}else if (MessageType.TYPE_MSG_PIC==type){
				//图片
				cv.put(ThreadTable.MSG_TYPE, 3);
			}
			getContentResolver().update(
					ThreadTable.CONTENT_URI,
					cv,
					ThreadTable.ACCOUNT_NAME + "=? and " + ThreadTable.BUDDY_ID
							+ "=? and " + ThreadTable.CHAT_TYPE + "=? and "
							+ ThreadTable.MSG_TYPE + " in "
							+ MessageType.MSG_TYPE_CHAT,
					new String[] { getAccountName(), String.valueOf(mBuddyId),
							String.valueOf(mChatType) });
		}else if(0==mMessageAdapter.getCount()){
			//没有历史信息 全部删除关闭页面
			getContentResolver()
			.delete(MessageTable.CONTENT_URI,
					MessageTable.ACCOUNT_NAME
							+ "=? AND "
							+ MessageTable.BUDDY_ID
							+ "=? AND "
							+ MessageTable.CHAT_TYPE
							+ "=?",
					new String[] {
							getAccountName(),
							String.valueOf(mBuddyId),
							String.valueOf(mChatType) });
					String msgtype = "("
							+ MessageType.TYPE_MSG_TEXT + ","
							+ MessageType.TYPE_MSG_PIC + ","
							+ MessageType.TYPE_MSG_AUDIO + ")";
					getContentResolver().delete(
							ThreadTable.CONTENT_URI,
							ThreadTable.ACCOUNT_NAME
									+ "=? AND "
									+ ThreadTable.BUDDY_ID
									+ "=? AND "
									+ ThreadTable.MSG_TYPE
									+ " in " + msgtype,
							new String[] { getAccountName(),
									String.valueOf(mBuddyId) });
					//finish();
					onBackPressed();
		};
	
		


	}
	public void showChooseChatFileDialog(final Activity activity,
			final int viewtype, final String filePath, final long msgId,final int position) {

		new AlertDialog.Builder(activity)
				.setTitle(R.string.chat_hislist)
				.setItems(R.array.choose_chat_method_reply,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								/*case 0:
									if (viewtype == TYPE_OUT_PIC) {
										sendPic(filePath, msgId);
									} else {
										sendVoice(filePath, msgId);
									}
									break;*/
								case 0:
									LogUtils.e("=========删除此聊天条目========"+msgId);
									deleteMsg(msgId, position);
									break;
								}
								dialog.dismiss();
							}
						}).show();
	}
	
	//获取头像
	private void setAvatar(long senderid, Contact contact, ImageView avatarView){
		//if (contact != null) {
			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc().showStubImage(R.drawable.default_user).showImageForEmptyUri(R.drawable.default_user).showImageOnFail(R.drawable.default_user).build();
			QueryBuilder<Contact, Integer> contactBuilder = null;
	        List<Contact> contacts = null;
	        long contactId = 0;
	        try {
	            contactBuilder = dataHelper.getContactData().queryBuilder();
	            if(mChatType == ChatType.CHAT_TYPE_SINGLE || contact==null) {
	            	contactId = mBuddyId;
	            }else{
	            	contactId = contact.getId();
	            }
	            contactBuilder.where().eq("id", contactId);
	            contacts = contactBuilder.query();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        if(contacts.size() > 0){
	            if(!contacts.get(0).getAvatar().endsWith("null"))
	            {
	                imageLoader.displayImage(contacts.get(0).getAvatar(), avatarView, defaultOptions);

	            }
	            else {
	                avatarView.setImageResource(R.drawable.default_user);
                }
	        	
	        }
	        avatarView.setOnClickListener(new AvatarClickListener(senderid));
		//}
	}
	
	private void setMyAvatar(ImageView avatarView) {
    	imageLoader.displayImage(getCurAccount().getAvatar(), avatarView, defaultOptions);
        avatarView.setOnClickListener(new AvatarClickListener(getCurAccount().getUserId()));
	}
	
	class AvatarClickListener implements OnClickListener {
		long userId;
		public AvatarClickListener(long contactId) {
			this.userId = contactId;
		}
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(NewChatActivity.this,
                    PersonalInfoActivity.class);
            intent.putExtra("id", userId);
            startActivity(intent);
		}
	}
	
	private void insertFace(Face face) {
		// mContentEdit.append(FaceUtils.replaceFace(this, face.text));
		insertText(mMessageEdit, FaceUtils.replaceFace(this, face.text));
	}

	/** 获取EditText光标所在的位置 */
	private int getEditTextCursorIndex(EditText mEditText) {
		return mEditText.getSelectionStart();
	}

	/** 向EditText指定光标位置插入字符串 */
	private void insertText(EditText mEditText, SpannableString mText) {
		mEditText.getText().insert(getEditTextCursorIndex(mEditText), mText);
	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	public void sendPhoto(String file) {
		sendfile = new File(file);
		// 发送到服务器 0为路径,图片是2,
		now = new Date();
		String sendTime = dataFormat.format(now);// 获得当前的时间戳

		long fileLength = sendfile.length();
		int timeOfVoice = 0;

		try {
			new sendFileOLServerTask().execute(
					sendTime, "2",
					fileLength+"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送语音
	 * 
	 * @param filePath
	 */
	public void sendVoice(String file) {
		sendfile = new File(file);
		// 发送到服务器 0为路径,图片是2,
		now = new Date();
		String sendTime = dataFormat.format(now);// 获得当前的时间戳

		long fileLength = sendfile.length();
		int timeOfVoice = (int)Math.ceil(recodeTime);
		
		try {
			new sendFileOLServerTask().execute(
					sendTime, "3",(timeOfVoice * 1000)+"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送文件到OL服务器
	 * 
	 */
	public class sendFileOLServerTask extends AsyncTask<String, Void, String> {

		private String type = null;

		@Override
		protected String doInBackground(String... params) {
			String ret = null;
			try {
				String p1 = params[0];
				type = params[1];// 发送类型
				String p3 = params[2];// 声音长度
				ret = uploadfile(sendfile, p1, type, p3);

				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ret;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			updateMessageStatus(newURL, result, 1);
		}
	}

	/*
	 * 图片，音频等发送到OL
	 */
	private String uploadfile(final File file, final String sendTime,
			final String sendType, final String voiceLength) {

		String resultStr = "";
		String path = null;
		Socket socket = null;
		InputStreamReader inSR = null;
		OutputStream outStream = null;
		PushbackInputStream inStream = null;
		RandomAccessFile fileOutStream = null;
		try {
			OLConfig olConfig = BaseApplication.getInstance().getOlConfig();
			socket = new Socket(olConfig.ol_socket_ip, (int)olConfig.ol_socket_port);
			outStream = socket.getOutputStream();
			socket.setSoTimeout(15 * 1000);
			String head = "Content-Length=" + Long.toString(file.length())
					+ ";filename=" + file.getName() + ";sourceid=0"
					+ ";taskId=" + mBuddyId + ";fromUserId="
					+ Long.toString(mApp.mCurAccount.getUserId())
					+ ";sendTime=" + sendTime + ";type=" + sendType
					+ ";voiceLength=" + voiceLength + ";action="+(mChatType == 0 ? "user" : "task")+";fromUserName=" + mApp.mCurAccount.getUserName() +"\r\n";
			outStream.write(head.getBytes());// 向服务器发数据

			inStream = new PushbackInputStream(socket.getInputStream());// 接收服务器传回的数据
			fileOutStream = new RandomAccessFile(file, "r");
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fileOutStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			//String aa = outStream.toString();
			resultStr = readLine(inStream);
			LogUtils.e("Send OL Attach:"+resultStr);
			// {"result":200,"createTime":"2014-09-09 09:58:07","path":"/andron_img/out/20140909095642.jpg#/andron_img/20140909095642.jpg","fileLength":null,"resultMsg":"è¯­è¨å¾çä¼ è¾æå","position":"1","ImGroup":{"createTime":"2014-05-14 13:53:04","fromUserId":"4765305979","fromUserName":"","groupName":"","id":5,"persons":"4739802084;4765305979","reserve1":"","reserve2":"","reserve3":"","reserve4":"","type":"0","updateFlag":""},"ImMessage":{"fromUserId":"4765305979","groupId":5,"id":0,"reserve1":"","reserve2":"","reserve3":"","reserve4":"","sendContents":"/andron_img/out/20140909095642.jpg#/andron_img/20140909095642.jpg","sendTime":"2014-09-09 09:56:43","serverTime":"2014-09-09 09:58:07","type":"2"}}
			try {
				JSONObject json = new JSONObject(resultStr);
				path = json.get("createTime").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (Exception e) { //
			e.printStackTrace();

		} finally {
			try {
				if (socket != null)
					socket.shutdownOutput();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fileOutStream != null)
					fileOutStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// try {
			// inSR.close();
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// }
			try {
				if (inStream != null)
					inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (outStream != null)
					outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (path != null) {
			return path;
		} else {
			return null;
		}

	}
	 public static String readLine(PushbackInputStream in) throws IOException {
			char buf[] = new char[128];
			int room = buf.length;
			int offset = 0;
			int c;
			loop: while (true) {
				switch (c = in.read()) {
				case -1:
				case '\n':
					break loop;
				case '\r':
					int c2 = in.read();
					if ((c2 != '\n') && (c2 != -1))
						in.unread(c2);
					break loop;
				default:
					if (--room < 0) {
						char[] lineBuffer = buf;
						buf = new char[offset + 128];
						room = buf.length - offset - 1;
						System.arraycopy(lineBuffer, 0, buf, 0, offset);

					}
					buf[offset++] = (char) c;
					break;
				}
			}
			if ((c == -1) && (offset == 0))
				return null;
			return String.copyValueOf(buf, 0, offset);
		}

		private Uri saveMessage(String message) {
			ContentValues cv = new ContentValues();
			cv.put(MessageTable.BUDDY_ID, mBuddyId);
//			cv.put(MessageTable.BUDDY_NAME, getTitleText());
			cv.put(MessageTable.SENDER_ID, mAccount.getUserId());
			cv.put(MessageTable.BODY, message);
			cv.put(MessageTable.IS_INBOUND, MessageType.OUTGOINT);
			cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_TEXT);
			
			now = new Date();
			cv.put(MessageTable.SENT_TIME, dataFormat.format(now));
			cv.put(MessageTable.RECEIVED_TIME, dataFormat.format(now));
			cv.put(MessageTable.ACCOUNT_NAME, getAccountName());
			cv.put(MessageTable.CHAT_TYPE, mChatType);
			cv.put(MessageTable.OUTBOUND_STATUS,
					mApp.chatServiceReady() ? MessageType.MSG_STATUS_WAITING
							: MessageType.MSG_STATUS_ERROR);

			return getContentResolver().insert(MessageTable.CONTENT_URI, cv);
		}

		private Uri savePicMessage(long buddyId, String message, int chattype) {
			ContentValues cv = new ContentValues();
			cv.put(MessageTable.BUDDY_ID, buddyId);
			// cv.put(MessageTable.SENDER_ID, m_id);
//			cv.put(MessageTable.BUDDY_NAME, getTitleText());
			cv.put(MessageTable.SENDER_ID, mAccount.getUserId());
			cv.put(MessageTable.BODY, message);
			cv.put(MessageTable.IS_INBOUND, MessageType.OUTGOINT);
			cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_PIC);
			
			now = new Date();
			cv.put(MessageTable.SENT_TIME, dataFormat.format(now));
			cv.put(MessageTable.RECEIVED_TIME, dataFormat.format(now));
			cv.put(MessageTable.ACCOUNT_NAME, mAccount.getLoginname());
			cv.put(MessageTable.CHAT_TYPE, mChatType);
			cv.put(MessageTable.OUTBOUND_STATUS,
					mApp.chatServiceReady() ? MessageType.MSG_STATUS_WAITING
							: MessageType.MSG_STATUS_ERROR);

			return getContentResolver().insert(MessageTable.CONTENT_URI, cv);
		}

		private Uri saveAudioMessage(long buddyId, String message, int chattype) {
			ContentValues cv = new ContentValues();
			cv.put(MessageTable.BUDDY_ID, buddyId);
//			cv.put(MessageTable.BUDDY_NAME, getTitleText());
			cv.put(MessageTable.SENDER_ID, mAccount.getUserId());
			cv.put(MessageTable.BODY, message);
			cv.put(MessageTable.IS_INBOUND, MessageType.OUTGOINT);
			cv.put(MessageTable.TYPE, MessageType.TYPE_MSG_AUDIO);
			
			now = new Date();
			cv.put(MessageTable.SENT_TIME, dataFormat.format(now));
			cv.put(MessageTable.RECEIVED_TIME, dataFormat.format(now));
			cv.put(MessageTable.ACCOUNT_NAME, mAccount.getLoginname());
			cv.put(MessageTable.CHAT_TYPE, mChatType);
			cv.put(MessageTable.OUTBOUND_STATUS,
					mApp.chatServiceReady() ? MessageType.MSG_STATUS_WAITING
							: MessageType.MSG_STATUS_ERROR);

			return getContentResolver().insert(MessageTable.CONTENT_URI, cv);
		}

		private void updateMessageStatus(Uri uri, String server_time, int msgstutas) {
			ContentValues cv = new ContentValues();
			cv.put(MessageTable.OUTBOUND_STATUS, msgstutas);
			cv.put(MessageTable.SENT_TIME, server_time);//因为按照SENT_TIME排序
			getContentResolver().update(uri, cv, null, null);
		}
		
		private void sendMessageToOL(String message,final boolean isReplay,final long msgId){
			OLConfig olConfig = BaseApplication.getInstance().getOlConfig();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("act", "sendmsg");
			params.put("token", olConfig.ol_token);
			params.put("chatid", mBuddyId+"");
			if(mChatType == 0){
				params.put("taskid", "0");
			}else{
				params.put("taskid", "0");
			}
			params.put("chattype", mChatType+"");
			params.put("userid", olConfig.ol_userName+"");
			now = new Date();
			params.put("sendtime", dataFormat.format(now));
			params.put("fromuserid", mApp.mCurAccount.getUserId()+"");
			params.put("fromusername", mApp.mCurAccount.getUserName());
			params.put("sendcontents", message);
			params.put("module", "0");
			params.put("type", "1");
			params.put("chatseq", new Random().nextInt()+"");
	
			WDJsonObjectForChatRequest mRequest = new WDJsonObjectForChatRequest("http://"+ olConfig.ol_ip+":"+ olConfig.ol_port+"/im.do", Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					LogUtils.e("response=" + response);
					try{
						if(response.getString("response_code").equals("200")){
							if(!isReplay){
								updateMessageStatus(newURL, response.optString("server_time"), 1);
								mMessageEdit.setText("");
							}else{
								updateMessage(msgId, response.optString("server_time"), 1);
							}
						}else if(response.getString("response_code").equals("404") 
								|| response.getString("response_code").equals("403")){
							BaseApplication.getInstance().expiredIMToken();
						}
					}catch(Exception ex){
						
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					//
				}
			});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		}

}
