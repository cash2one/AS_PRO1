package com.linkage.mobile72.sh.activity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gauss.speex.recorder.SpeexPlayer;
import com.gauss.speex.recorder.SpeexRecorder;
import com.linkage.lib.util.LogUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.AttachmentPicGridAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Group;
import com.linkage.mobile72.sh.data.Person;
import com.linkage.mobile72.sh.http.ParamItem;
import com.linkage.mobile72.sh.http.WDJsonObjectMultipartRequest;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ActivityUtils;
import com.linkage.mobile72.sh.utils.BitmapUtils;
import com.linkage.mobile72.sh.utils.ImageUtils;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.SharedPreferencesUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.utils.multipic.ImgFileListActivity;
import com.linkage.mobile72.sh.utils.multipic.ImgsActivity;
import com.linkage.mobile72.sh.utils.multipic.ImgsAdapter;
import com.linkage.mobile72.sh.widget.DateTimePickerDialog;
import com.linkage.mobile72.sh.widget.DateTimePickerDialog.TimeSetListener;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.widget.MyEditDialog;
import com.linkage.ui.widget.CustomDialog;
import com.nostra13.universalimageloader.utils.L;

public class CreateCommentActivity extends BaseActivity implements View.OnClickListener,
		View.OnTouchListener {
	private static final String TAG = "CreateCommentActivity";

	private static final int REQUEST_RECEIVER = 1;
	private static final int REQUEST_PIC = 3;
	private static final int REQUEST_MB = 4;
    private static final int REQUEST_ADD_PIC = 6;

    private SharedPreferencesUtils spUtil;
	private ArrayList<Group> chooseReceivers;
	private ArrayList<String> choosePics = new ArrayList<String>();
	private String timerString = "";// 定时发送的时间
	private String picUrls;
	private String voiceUrl;
	private String classroomIds;
	private String userIds;
	private String messageContent;

	private ProgressDialog mProgressDialog;

	private Button commit, receiver_plus;
	private TextView tvInputTotal, receiverText;
	private EditText contentText;
	private GridView gridView;
	private ImageView voiceAnimImageView;
	private Button voiceDeleteButton;
	private RelativeLayout layoutVoice;
	private LinearLayout layoutTimer;
	private RelativeLayout layoutSend, layoutAddPic, layoutAddVoice, layoutAddTimer, layoutGotoMb,
			layoutSaveMb;
	private AttachmentPicGridAdapter imgsAdapter;
	private TextView voiceTime, timerTime;
	private DateTimePickerDialog dateTimeDialog;
	private TimeSetListener mDialogListener;

	// =========voice============//
	private String mVoicePath, tempVoicePath;// 这里需要注意下逻辑
	private Thread recordThread;
	private Boolean change_flag;
	private SpeexRecorder recorderInstance = null;
	private SpeexPlayer splayer = null;

	private static int MAX_TIME = 30; // 最长录制时间，单位秒，0为无时间限制
	private static int MIX_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1

	private static int RECORD_NO = 0; // 不在录音
	private static int RECORD_ING = 1; // 正在录音
	private static int RECODE_ED = 2; // 完成录音

	private static int RECODE_STATE = 0; // 录音的状态

	private static float recodeTime = 0.0f; // 录音的时间
	private static double voiceValue = 0.0; // 麦克风获取的音量值
	private Dialog voiceDialog;
	private Timer mTimer = null;
	// 语音动画控制任务
	private TimerTask mTimerTask = null;
	private ImageView dialog_img;
	private TextView dialog_txt;
	// 记录语音动画图片
	private int index = 1;
	private AudioAnimationHandler audioAnimationHandler = null;
	// =========voice============//
	private MyCommonDialog dialog;
	private MyEditDialog editDialog;
	
	private CustomDialog choosePicDialog;
	private TextWatcher mTextWatcher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_homework);

		setTitle("发点评");
		findViewById(R.id.back).setOnClickListener(this);
		commit = (Button) findViewById(R.id.set);
		commit.setText("查看历史");
		commit.setVisibility(View.VISIBLE);
		commit.setOnClickListener(this);
		
		findViewById(R.id.seperate_line1).setVisibility(View.GONE);
		findViewById(R.id.layout_subject).setVisibility(View.GONE);
		receiver_plus = (Button) findViewById(R.id.receiver_plus);
		receiver_plus.setOnClickListener(this);

		contentText = (EditText) findViewById(R.id.edit_input);
		tvInputTotal = (TextView) findViewById(R.id.tvInputTotal);
		
		gridView = (GridView) findViewById(R.id.pic_gridview);
		voiceAnimImageView = (ImageView) findViewById(R.id.voice_anim_img);
		voiceDeleteButton = (Button) findViewById(R.id.voice_delete_button);
		layoutVoice = (RelativeLayout) findViewById(R.id.layout_voice);
		layoutTimer = (LinearLayout) findViewById(R.id.layout_timer_send);
		layoutSend = (RelativeLayout) findViewById(R.id.layout_send);
		layoutAddPic = (RelativeLayout) findViewById(R.id.layout_add_pic);
		layoutAddVoice = (RelativeLayout) findViewById(R.id.layout_add_voice);
		layoutAddTimer = (RelativeLayout) findViewById(R.id.layout_add_timer);
		layoutGotoMb = (RelativeLayout) findViewById(R.id.layout_add_mb);
		layoutSaveMb = (RelativeLayout) findViewById(R.id.layout_save_mb_);
		receiverText = (TextView) findViewById(R.id.receiver);

		voiceDeleteButton.setOnClickListener(this);
		layoutSend.setOnClickListener(this);
		layoutAddPic.setOnClickListener(this);
		layoutAddVoice.setOnTouchListener(this);
		layoutAddTimer.setOnClickListener(this);
		layoutTimer.setOnClickListener(this);
		layoutGotoMb.setOnClickListener(this);
		layoutSaveMb.setOnClickListener(this);
		imgsAdapter = new AttachmentPicGridAdapter(this, choosePics);
		if (choosePics.size() < 1) {
			gridView.setVisibility(View.INVISIBLE);
		} else {
			gridView.setVisibility(View.VISIBLE);
		}
		gridView.setAdapter(imgsAdapter);
		contentText.setOnTouchListener(this);
		mDialogListener = new TimeSetListener() {
			@Override
			public void setTime(String time) {
				layoutTimer.setVisibility(View.VISIBLE);
				timerTime.setText(time.substring(5));
				timerString = time;
			}
		};

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (choosePics.size() < 8 && position == choosePics.size()) {// 点的是加号
//					Intent picIntent = new Intent(CreateCommentActivity.this,
//							ImgFileListActivity.class);
//					picIntent.putStringArrayListExtra(ImgFileListActivity.RES, choosePics);
//					picIntent.putExtra("append", true);
//					startActivityForResult(picIntent, REQUEST_PIC);
				    getPhoto();
				} else {// 点的都是图片
					Intent intent = new Intent(CreateCommentActivity.this,
							PictureReviewActivity.class);
					intent.putStringArrayListExtra(PictureReviewActivity.RES, choosePics);
					intent.putExtra("position", position);
					startActivityForResult(intent, REQUEST_PIC);
				}
			}
		});
		voiceTime = (TextView) findViewById(R.id.voice_time);
		timerTime = (TextView) findViewById(R.id.timer_time);
		spUtil = new SharedPreferencesUtils(this, getAccountName() + "_" + "JXHD");
		chooseReceivers = spUtil.getObject("comment_receiver", ArrayList.class);
		if(chooseReceivers != null) {
			receiverText.setText(getCaptioByChooseReceiver(chooseReceivers));
		}
		mTextWatcher = new TextWatcher() {
			private CharSequence temp;
			private int editStart;
			private int editEnd;

			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				temp = s;
			}

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				editStart = contentText.getSelectionStart();
				editEnd = contentText.getSelectionEnd();

				if (temp.length() > 244) {

					Toast.makeText(CreateCommentActivity.this,
							R.string.input_limit, Toast.LENGTH_SHORT).show();
					s.delete(editStart - 1, editEnd);
					int tempSelection = editStart;
					contentText.setText(s);
					contentText.setSelection(tempSelection);

				} else {

					String sOrg = getResources().getString(R.string.total_word);
					String sFinalOrg = String.format(sOrg, 244
							- s.length());
					tvInputTotal.setText(sFinalOrg);
				}
			}
		};
		contentText.addTextChangedListener(mTextWatcher);
		String sOrg = getResources().getString(R.string.total_word);
		String sFinalOrg = String.format(sOrg, 244);
		tvInputTotal.setText(sFinalOrg);
	}

	// 发送
	private void send() {
		String[] receiverInfo = getValueByChooseReceiver(chooseReceivers);
		classroomIds = receiverInfo[0];
		userIds = receiverInfo[1];
		messageContent = contentText.getText().toString();
		if (TextUtils.isEmpty(classroomIds) && TextUtils.isEmpty(userIds)) {
			T.showShort(this, "请选择接收人");
			return;
		}
		if (TextUtils.isEmpty(messageContent) || TextUtils.isEmpty(messageContent.trim())) {
			T.showShort(this, "请填写消息内容");
			return;
		}

		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCancelable(false);
		}

		dialog = new MyCommonDialog(this, "提示消息", "是否确认发送？", "取消", "确定");
		dialog.setOkListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				// 避免之前失败有残留....
				picUrls = "";
				voiceUrl = "";
				if (choosePics.size() > 0) {
					sendPic(0);
				} else if (!TextUtils.isEmpty(mVoicePath)) {
					sendVoice();
				} else {
					sendMessage();
				}
			}
		});
		dialog.setCancelListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}

	private void sendMessage() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "sendMessage");
		params.put("smsMessageType", String.valueOf(Consts.JxhdType.COMMENT));
		params.put("classroomIds", classroomIds);
		params.put("userIds", userIds);
		params.put("subjectid", "");
		params.put("messageContent", messageContent);
		params.put("picUrls", picUrls);
		params.put("voiceUrl", voiceUrl);
		params.put("timing", timerString);
		params.put("voteJson", "");
		if(!"".equalsIgnoreCase(voiceTime.getText().toString()))
		{
			String voicetime = voiceTime.getText().toString();
			voicetime=voicetime.replace("\"",""); 
			params.put("voicenum", voicetime);
		}else{
			params.put("voicenum", "0");
		}

		mProgressDialog.setMessage("正在发送");
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_sendMessage,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						mProgressDialog.dismiss();
						LogUtils.i(TAG + ":response=" + response);
						if (response.optInt("ret") == 0) {
							T.showShort(CreateCommentActivity.this, response.optString("msg"));
							spUtil.setObject("comment_receiver", chooseReceivers);
							setResult(RESULT_OK);
							finish();
						} else if(response.optInt("ret") == 2 || response.optInt("ret") == 3){
							dialog = new MyCommonDialog(CreateCommentActivity.this, "提示消息", response.optString("msg"), null, "确定");
							dialog.setOkListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									if (dialog.isShowing()) {
										dialog.dismiss();
									}
									spUtil.setObject("comment_receiver", new ArrayList<Group>());
									Intent receiverIntent = new Intent(CreateCommentActivity.this, SelectReceiverActivity.class);
									Bundle b = new Bundle();
									b.putInt(SelectReceiverActivity.RECEIVER_FROM, 1);
									b.putSerializable(SelectReceiverActivity.RECEIVER_RESULT, new ArrayList<Group>());
									receiverIntent.putExtras(b);
									startActivityForResult(receiverIntent, REQUEST_RECEIVER);
								}
							});
							dialog.show();
						} else {
							T.showShort(CreateCommentActivity.this, response.optString("msg"));
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						mProgressDialog.dismiss();
						StatusUtils.handleError(arg0, CreateCommentActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	// 发送图片
	private void sendPic(final int index) {
		mProgressDialog.setMessage("正在上传第" + (index + 1) + "张图片");
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
		File file = new File(choosePics.get(index));
		List<ParamItem> params = new ArrayList<ParamItem>();
		params.add(new ParamItem("commandtype", "sendMessageAttachment", ParamItem.TYPE_TEXT));
		params.add(new ParamItem("fileupload", file, ParamItem.TYPE_FILE));
		WDJsonObjectMultipartRequest mRequest = new WDJsonObjectMultipartRequest(Consts.SERVER_sendMessageAttachment,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						LogUtils.i(TAG + ":response=" + response);
						if (response.optInt("ret") == 0) {
							if (TextUtils.isEmpty(picUrls)) {
								picUrls = response.optString("path");
							} else {
								picUrls = picUrls + "," + response.optString("path");
							}
							// 仍然有图片需要上传
							if (index + 1 < choosePics.size()) {
								sendPic(index + 1);
							} else if (!TextUtils.isEmpty(mVoicePath)) {
								sendVoice();
							} else {
								sendMessage();
							}
						} else {
							mProgressDialog.dismiss();
							StatusUtils.handleStatus(response, CreateCommentActivity.this);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						mProgressDialog.dismiss();
						StatusUtils.handleError(arg0, CreateCommentActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);

	}

	protected void sendVoice() {
		mProgressDialog.setMessage("正在上传语音信息");
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
		File file = new File(mVoicePath);
		List<ParamItem> params = new ArrayList<ParamItem>();
		params.add(new ParamItem("commandtype", "sendMessageAttachment", ParamItem.TYPE_TEXT));
		params.add(new ParamItem("fileupload", file, ParamItem.TYPE_FILE));
		WDJsonObjectMultipartRequest mRequest = new WDJsonObjectMultipartRequest(Consts.SERVER_sendMessageAttachment,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						L.i(TAG, "response=" + response);
						if (response.optInt("ret") == 0) {
							voiceUrl = response.optString("path");
							sendMessage();
						} else {
							mProgressDialog.dismiss();
							StatusUtils.handleStatus(response, CreateCommentActivity.this);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						mProgressDialog.dismiss();
						StatusUtils.handleError(arg0, CreateCommentActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);

	}

	// 获取选择的接收人的显示文本
	private String getCaptioByChooseReceiver(ArrayList<Group> groups) {
		if (groups != null && groups.size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (Group group : groups) {
				if (group.isChecked()) {
					sb.append(group.getName());
					sb.append(",");
				} else {
					List<Person> persons = group.getPersons();
					if (persons != null && persons.size() > 0) {
						for (Person p : persons) {
							if (p.isChecked()) {
								sb.append(p.getName());
								sb.append(",");
							}
						}
					}
				}
			}
			String s = sb.toString();
			if (s.length() > 0) {
				s = s.substring(0,s.length()-1);
			}
			return s;
		}
		return "";
	}

	// 获取选择的接收人的值,按班级和人员分
	private String[] getValueByChooseReceiver(ArrayList<Group> groups) {
		String[] values = new String[2];
		String classroomIds = "";
		String userIds = "";
		if (groups != null && groups.size() > 0) {
			for (Group group : groups) {
				if (group.isChecked()) {
					classroomIds = classroomIds + group.getId() + ",";
				} else {
					List<Person> persons = group.getPersons();
					if (persons != null && persons.size() > 0) {
						for (Person p : persons) {
							if (p.isChecked()) {
								userIds = userIds + p.getId() + ",";
							}
						}
					}
				}
			}
			if (classroomIds.length() > 0) {
				classroomIds = classroomIds.substring(0, classroomIds.length() - 1);
			}
			if (userIds.length() > 0) {
				userIds = userIds.substring(0, userIds.length() - 1);
			}
		}
		values[0] = classroomIds;
		values[1] = userIds;
		return values;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.edit_input:
			// 解决scrollView中嵌套EditText导致不能上下滑动的问题
			v.getParent().requestDisallowInterceptTouchEvent(true);
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_UP:
				v.getParent().requestDisallowInterceptTouchEvent(false);
				break;
			}
			break;
		case R.id.layout_add_voice:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				tempVoicePath = getVoicePath();
				splayer = new SpeexPlayer(tempVoicePath);
				splayer.endPlay();
				change_flag = false;
				if (RECODE_STATE != RECORD_ING) {
					RECODE_STATE = RECORD_ING;
					showVoiceDialog();
					try {
						if (recorderInstance == null) {
							recorderInstance = new SpeexRecorder(tempVoicePath);
						}
						
						Thread th = new Thread(recorderInstance);
						th.start();
						recorderInstance.setRecording(true);
						Thread.sleep(500);
						if(recorderInstance.isForbiden()) {
							if (voiceDialog.isShowing()) {
								voiceDialog.dismiss();
							}
							Toast.makeText(this, "录音启动失败，请检查软件权限", Toast.LENGTH_SHORT).show();
						}else {
							recordThread = new Thread(ImgThread);
							recordThread.start();
						}
					} catch (Exception e) {
						e.printStackTrace();
						if (voiceDialog.isShowing()) {
							voiceDialog.dismiss();
						}
						Toast.makeText(this, "录音启动失败，请检查软件权限", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (event.getY() > 0.00) {
					change_flag = false;
				} else {
					change_flag = true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (RECODE_STATE == RECORD_ING) {
					RECODE_STATE = RECODE_ED;
					if (voiceDialog.isShowing()) {
						voiceDialog.dismiss();
					}

					recorderInstance.setRecording(false);
					recorderInstance = null;
					// change_flag = false;
					if (recodeTime < MIX_TIME) {
						if(recorderInstance != null && !recorderInstance.isForbiden()) {
							showWarnToast();
						}
						RECODE_STATE = RECORD_NO;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						if (!change_flag) {
							try {
								Thread.sleep(2000);
								saveVoice(tempVoicePath);
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
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();// 直接模拟返回键了，返回键设置了事件
			// finish();
			break;
		case R.id.receiver_plus:
			Intent receiverIntent = new Intent(this, SelectReceiverActivity.class);
			Bundle b = new Bundle();
			b.putInt(SelectReceiverActivity.RECEIVER_FROM, 1);
			b.putSerializable(SelectReceiverActivity.RECEIVER_RESULT, new ArrayList<Group>());
			receiverIntent.putExtras(b);
			startActivityForResult(receiverIntent, REQUEST_RECEIVER);
			break;
		case R.id.layout_add_pic:
			/*Intent picIntent = new Intent(this, ImgFileListActivity.class);
			startActivityForResult(picIntent, REQUEST_PIC);*/
		    getPhoto();
			break;
		case R.id.layout_add_timer:
			dateTimeDialog = new DateTimePickerDialog(this);
			dateTimeDialog.dateTimePicKDialog(layoutTimer, timerTime, 0, mDialogListener);
			break;
		case R.id.layout_add_mb:
			Intent mbIntent = new Intent(this, JxMbManagerListActivity.class);
			mbIntent.putExtra(JxMbManagerListActivity.KEY_TYPE, JxMbManagerListActivity.LOCATION_COMMENT);
			mbIntent.putExtra(JxMbManagerListActivity.KEY_ACTION,
					JxMbManagerListActivity.ACTION_GET_HOMEWORK);
			startActivityForResult(mbIntent, REQUEST_MB);
			break;
		case R.id.layout_send:
			send();
			break;
		case R.id.layout_timer_send:
			if (layoutTimer.getVisibility() == View.VISIBLE) {
				dialog = new MyCommonDialog(this, "提示消息", "定时发送已设置", "取消定时", "重新设置");
				dialog.setOkListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						dateTimeDialog = new DateTimePickerDialog(CreateCommentActivity.this);
						dateTimeDialog.dateTimePicKDialog(layoutTimer, timerTime, 0,
								mDialogListener);
					}
				});
				dialog.setCancelListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						layoutTimer.setVisibility(View.INVISIBLE);
						timerString = "";
					}
				});
				dialog.show();
			}
			break;
		case R.id.layout_save_mb_:
			if (TextUtils.isEmpty(contentText.getText()) || TextUtils.isEmpty(contentText.getText().toString().trim())) {
				Toast.makeText(this, "模板内容不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			editDialog = new MyEditDialog(CreateCommentActivity.this, 10, "输入模版标题", "", "取消", "确定");
			editDialog.setCancelListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					editDialog.dismiss();
					hideKeyboard(contentText.getWindowToken());
				}
			});
			editDialog.setOkListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					hideKeyboard(contentText.getWindowToken());
					saveMb();
				}
			});
			editDialog.show();
			break;
		case R.id.voice_delete_button:
			layoutVoice.setVisibility(View.INVISIBLE);
			mVoicePath = "";
			recodeTime = 0.0f;
			break;
		case R.id.set:
			//send();
			Intent mIntent = new Intent(this, JxHomeworkListActivity.class);
			mIntent.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
					JxHomeworkListActivity.SMSMESSAGETYPE_COMMENT);
			startActivity(mIntent);
			break;
		}
	}
	
	private void getPhoto()
    {
	    if (choosePics.size() >= 8)
        {
            Toast.makeText(CreateCommentActivity.this,
                    R.string.max_photo,
                    Toast.LENGTH_LONG).show();
            return;
        }
        LinearLayout lyDlg;
        
        Button btnTakePhoto, btnAlbum, btnCancel;
        
        choosePicDialog = new CustomDialog(CreateCommentActivity.this, true);
        choosePicDialog.setCustomView(R.layout.pic_select_dlg);
        
        Window window = choosePicDialog.getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        
        lyDlg = (LinearLayout) choosePicDialog.findViewById(R.id.dialog_layout);
        lyDlg.setPadding(0, 0, 0, 0);
        
        btnTakePhoto = (Button) choosePicDialog.findViewById(R.id.btnTakePhoto);
        btnAlbum = (Button) choosePicDialog.findViewById(R.id.btnAlbum);
        btnCancel = (Button) choosePicDialog.findViewById(R.id.btnCancel);
       
        
        btnTakePhoto.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                choosePicDialog.dismiss();
                // 启动拍照
//                takePhoto();
                ActivityUtils.startTakePhotActivity(CreateCommentActivity.this, PIC_TAKE_PHOTO);
            }
        });
        
        btnAlbum.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                choosePicDialog.dismiss();
                Intent picIntent = new Intent(CreateCommentActivity.this, ImgFileListActivity.class);
                picIntent.putExtra(Consts.CHOOSE_PIC_TOTAL, choosePics.size());
                startActivityForResult(picIntent, REQUEST_ADD_PIC);
            }
        });
        btnCancel.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                choosePicDialog.dismiss();
            }
        });
        
        choosePicDialog.setCancelable(true);
        choosePicDialog.show();
        
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		switch (requestCode) {
		case REQUEST_RECEIVER:
			if (data != null) {
				chooseReceivers = (ArrayList<Group>) data.getExtras().getSerializable(
						SelectReceiverActivity.RECEIVER_RESULT);
				
				receiverText.setText(getCaptioByChooseReceiver(chooseReceivers));
			}
			break;
		case REQUEST_PIC:
			if (data != null) {
				ArrayList<String> rawPics = data.getExtras().getStringArrayList(
						ImgsActivity.PIC_RESULT);
				if (rawPics.size() < 1) {
					gridView.setVisibility(View.INVISIBLE);
					choosePics.clear();
					imgsAdapter.addData(choosePics, false);
				} else {
					gridView.setVisibility(View.VISIBLE);
					choosePics.clear();
					new HandleLocalBitmapTask(rawPics).execute();
				}
			}
			break;
		case REQUEST_MB:
			if (data != null) {
				String con = data.getExtras().getString(JxMbManagerListActivity.KEY_CONTENT);
				contentText.setText(con);
			}
			break;
			
		case REQUEST_ADD_PIC:
            if (data != null) {
                ArrayList<String> rawPics = data.getExtras().getStringArrayList(
                        ImgsActivity.PIC_RESULT);
                if (rawPics.size() < 1) {
                    gridView.setVisibility(View.INVISIBLE);
                    choosePics.clear();
                    imgsAdapter.addData(choosePics, false);
                } else {
                    gridView.setVisibility(View.VISIBLE);
                    new HandleLocalBitmapTask(rawPics).execute();
                }
            }
            break;
		case PIC_TAKE_PHOTO:
			if(resultCode == RESULT_OK)
			onTakePhotoSucced(data);
			break;
		case REQ_EDIT_PHOTO:
			if(resultCode == RESULT_OK)
			onEditImageSucced(data);
			break;
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
		LogUtils.d("onEditImageSucced:" + data);
		if(data == null)return;
		Uri uri = data.getData();

		String ly_time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime());
		String filePath = mApp.getWorkspaceImage().toString() + "/" + ly_time + ".jpeg";
		try {
			ImageUtils.savePictoFile(uri, filePath);
			ArrayList<String> rawPics = new ArrayList<String>();
			rawPics.add(filePath);
			gridView.setVisibility(View.VISIBLE);
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
			tempDirPath = BaseApplication.getInstance().getWorkspaceImage().getAbsolutePath();
			mRawPics = rawpics;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ProgressDialogUtils.showProgressDialog("图片处理中", CreateCommentActivity.this);
		}

		@Override
		protected Void doInBackground(Void... params) {
//			choosePics.clear();
			for (int i = 0; i < mRawPics.size(); i++) {
				String temp = pressPic(mRawPics.get(i));
//				String temp = mRawPics.get(i);
				if (!TextUtils.isEmpty(temp)) {
					choosePics.add(temp);
				}
			}
			return null;
		}

		private String pressPic(String path) {
			String resultFileName = BitmapUtils.handleLocalBitmapFile(path, tempDirPath);
			if (resultFileName.endsWith(".png")) {
				FileInputStream fileinputstream = null;
				Bitmap bitmap = null;
				try {
					fileinputstream = new FileInputStream(resultFileName);
					FileDescriptor filedescriptor = fileinputstream.getFD();
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPurgeable = true;
					options.inInputShareable = true;
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeFileDescriptor(filedescriptor, null, options);
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
					String filaName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
							.format(Calendar.getInstance().getTime()) + UUID.randomUUID() + ".jpg";
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
				return resultFileName;
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			ProgressDialogUtils.dismissProgressBar();
			imgsAdapter.notifyDataSetChanged();
		}
	}

	ImgsAdapter.OnItemClickClass onPicItemClickClass = new ImgsAdapter.OnItemClickClass() {
		@Override
		public void OnItemClick(View v, int Position, CheckBox checkBox) {
			String filePath = choosePics.get(Position);

		}
	};

	private String getVoicePath() {
		String fileURL = mApp.getWorkspaceVoice().getAbsolutePath();
		String ly_time = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance()
				.getTime());
		fileURL = fileURL + "/" + mApp.getDefaultAccount().getLoginname() + "_" + ly_time + ".spx";
		Log.i("==========fileURL=========", fileURL);
		return fileURL;
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
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE = RECODE_ED;
						if (voiceDialog.isShowing()) {
							voiceDialog.dismiss();
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
							RECODE_STATE = RECORD_NO;
						} else {
							saveVoice(tempVoicePath);
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
				imageView.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f1
						: R.drawable.chat_out_voice_playing_f1);
				break;
			case 1:
				imageView.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f2
						: R.drawable.chat_out_voice_playing_f2);
				break;
			case 2:
				imageView.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f3
						: R.drawable.chat_out_voice_playing_f3);
				break;
			default:
				imageView.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f3
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

	// 录音时显示Dialog
	private void showVoiceDialog() {
		if (voiceDialog == null) {
			voiceDialog = new Dialog(this, R.style.DialogStyle);
			voiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			voiceDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			voiceDialog.setContentView(R.layout.my_dialog);
			dialog_img = (ImageView) voiceDialog.findViewById(R.id.dialog_img);
			dialog_txt = (TextView) voiceDialog.findViewById(R.id.dialog_txt);
		}
		voiceDialog.dismiss();
		voiceDialog.show();
	}

	// 录音时间太短时Toast显示
	private void showWarnToast() {
		// voicePath = "";
		Toast toast = new Toast(this);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 20, 20, 20);

		// 定义一个ImageView
		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.voice_to_short); // 图标

		TextView mTv = new TextView(this);
		mTv.setText("时间太短，录音失败，请长按");
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

	// 录音Dialog图片随声音大小切换
	private void setDialogImage() {

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

	private void saveVoice(final String voicePathName) {
		mVoicePath = voicePathName;
		layoutVoice.setVisibility(View.VISIBLE);
		layoutVoice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				splayer = new SpeexPlayer(voicePathName);
				splayer.endPlay();
				splayer.startPlay();
				playAudioAnimation(voiceAnimImageView, 1);
			}
		});
		voiceTime.setText((int) recodeTime + "\"");
	}

	// TODO
	private void saveMb() {
		String title = editDialog.getEditView().getText().toString();
		String content = contentText.getText().toString();
		if (TextUtils.isEmpty(title) || TextUtils.isEmpty(title.trim())) {
			Toast.makeText(this, "模板标题不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (title.length() > 10) {
			Toast.makeText(this, "模板标题不能超过10个字", Toast.LENGTH_SHORT).show();
			return;
		}
		editDialog.dismiss();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "userTemplateSave");
		params.put("title", title);
		params.put("text", content);
		params.put("type", String.valueOf(Consts.JxhdType.COMMENT));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						LogUtils.e(TAG + " response=" + response);
						if (response.optInt("ret") == 0) {
							UIUtilities.showToast(CreateCommentActivity.this, "模板保存成功");
						} else {
							StatusUtils.handleStatus(response, CreateCommentActivity.this);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, CreateCommentActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	@Override
	public void onBackPressed() {
		if (!TextUtils.isEmpty(contentText.getText().toString()) || choosePics.size() > 0
				|| (int) recodeTime >= 1) {
			dialog = new MyCommonDialog(this, "提示消息", "您正在编辑点评，确认退出吗？", "取消", "退出");
			dialog.setOkListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			dialog.setCancelListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
				}
			});
			dialog.show();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
		if(splayer != null && splayer.isAlive()) {
			splayer.endPlay();
		}
	}
}