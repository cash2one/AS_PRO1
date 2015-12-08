package com.linkage.mobile72.sh.adapter;

import info.emm.LocalData.DateUnit;
import info.emm.messenger.ChatManager;
import info.emm.messenger.IMClient;
import info.emm.messenger.MQ;
import info.emm.messenger.MQ.NormalFileMessageBody;
import info.emm.messenger.MQ.VYMessage;
import info.emm.messenger.MQ.VYMessage.Direct;
import info.emm.messenger.MQ.VYMessage.Status;
import info.emm.messenger.MQ.VYMessage.Type;
import info.emm.messenger.MQ.imageMessageBody;
import info.emm.messenger.MQ.textMessageBody;
import info.emm.messenger.MQ.voiceMessageBody;
import info.emm.messenger.MediaController;
import info.emm.messenger.NotificationCenter;
import info.emm.messenger.NotificationCenter.NotificationCenterDelegate;
import info.emm.messenger.VYConversation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.sql.DataSource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.ShowBigPic;
import com.linkage.mobile72.sh.activity.im.NewChatActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.utils.FaceUtils;
import com.linkage.mobile72.sh.utils.ImageUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.lib.util.LogUtils;

public class IMMessageListAdapter extends BaseAdapter implements
		NotificationCenterDelegate {

	private static final int SHOW_TIME_INTERVAL = 5;

	private List<MQ.VYMessage> imMessages;
	private Context context;
	private String location;
	private float progress;
	private boolean iscomplate;
	private int downprogress;
	private int pos;
	private List<Integer> integers = new ArrayList<Integer>();
	private StringBuffer uris = new StringBuffer();
	private StringBuffer ids = new StringBuffer();
	private ArrayList<String> arrayList = new ArrayList<String>();
	private ArrayList<Long> arrMsgId = new ArrayList<Long>();
	private VYConversation conversation;

	private Timer mTimer = null;
	// 语音动画控制任务
	private TimerTask mTimerTask = null;
	private AudioAnimationHandler audioAnimationHandler = null;
	private int index = 1;
	private boolean isFinish = true;

	private long preMsgDate;

	protected BaseApplication mApp;
	protected String userHeaderUrl;

	private DataSource mDataSource;
	private int chatType = ChatType.CHAT_TYPE_GROUP;

//	private ClazzWorkContact mContact;

	private HashMap<String, String> contactNameMap = new HashMap<String, String>();

	public IMMessageListAdapter(Context context, VYConversation conversation,
			int type, Contact contact) {
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.UploadFileProgressChanged);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.UploadFileCompleted);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.DownloadFileProgressChanged);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.UploadFileFailed);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.playaudiocomplete);
		mApp = BaseApplication.getInstance();
		userHeaderUrl = mApp.getDefaultAccount().getAvatar();
		this.conversation = conversation;
		this.imMessages = conversation.getMessages();
		this.context = context;
		this.chatType = type;
//		this.mContact = contact;

		for (int i = 0; i < imMessages.size(); i++) {
			LogUtils.i(">>>@@@@@@@@@@....chatId=" + conversation.getChatID()
					+ " SIZE=" + imMessages.size());
			if (imMessages.get(i).getType() == Type.IMAGE) {
				if (imMessages.get(i).getDirect() == Direct.SEND) {
					if (arrayList.contains(((imageMessageBody) imMessages
							.get(i).getBody()).getLocalUrl())) {
						continue;
					} else {
						arrayList.add(((imageMessageBody) imMessages.get(i)
								.getBody()).getLocalUrl());
						arrMsgId.add(imMessages.get(i).getMsgId());
					}
				} else {
					if (arrayList.contains(((imageMessageBody) imMessages
							.get(i).getBody()).getRemoteUrl())) {
						continue;
					} else {
						arrayList.add(((imageMessageBody) imMessages.get(i)
								.getBody()).getRemoteUrl());
						arrMsgId.add(imMessages.get(i).getMsgId());
					}
				}

			}

		}
		for (int j = 0; j < arrayList.size(); j++) {
			uris.append(arrayList.get(j) + "@");
			ids.append(arrMsgId.get(j) + "@");
		}

//		mDataSource = mApp.getDataSource();

		printData();
	}

	// public IMMessageListAdapter(Context context, VYConversation conversation,
	// DataSource ds) {
	// mDataSource = ds;
	// IMMessageListAdapter(context, conversation);
	// }

	// private void IMMessageListAdapter(Context context2,
	// VYConversation conversation2) {
	// // TODO Auto-generated method stub
	//
	// }

	private void printData() {
		LogUtils.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>....chatId="
				+ conversation.getChatID() + " SIZE=" + imMessages.size());
		ChatManager cm = ChatManager.getInstance();
		String content = "";
		VYMessage message = null;

		for (int i = 0; i < imMessages.size(); i++) {
			message = imMessages.get(i);

			switch (message.getType()) {
			case TEXT:
				MQ.textMessageBody messageBody = (textMessageBody) message
						.getBody();
				content = messageBody.getMessage();
				break;
			case IMAGE:
				content = "图片 " + " LOCAL URL:"
						+ ((imageMessageBody) message.getBody()).getLocalUrl()
						+ " REMOTE URL:"
						+ ((imageMessageBody) message.getBody()).getRemoteUrl();
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
			LogUtils.e("messageID=" + message.getMsgId() + ", chatype="
					+ message.getChatType().ordinal() + ", direct="
					+ message.getDirect() + ", from=" + message.getFrom()
					+ ", to=" + message.getTo() + ", STATUS="
					+ message.getStatus() + ", content=" + content);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return imMessages.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int arg0, View converView, ViewGroup arg2) {
		Holder holder = null;

		if (0 >= arg0) {
			preMsgDate = 0;
		} else {
			preMsgDate = (long) imMessages.get(arg0 - 1).getMsgTime();
		}

		if (converView == null) {
			if (imMessages.get(arg0).getDirect() == Direct.SEND) {

				switch (imMessages.get(arg0).getType()) {
				case TEXT:
					holder = new TxtHolder();
					converView = View.inflate(context, R.layout.row_sent_message,
							null);
					((TxtHolder) holder).messview = (TextView) converView
							.findViewById(R.id.tv_chatcontent);
					((TxtHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((TxtHolder) holder).progressBar = (ProgressBar) converView
							.findViewById(R.id.pb_sending);
					((TxtHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					((TxtHolder) holder).imageFail = (ImageView) converView
							.findViewById(R.id.msg_status);
					break;
				case IMAGE:
					holder = new ImgHolder();
					converView = View.inflate(context, R.layout.row_sent_picture,
							null);
					((ImgHolder) holder).imageView = (ImageView) converView
							.findViewById(R.id.iv_sendPicture);
					((ImgHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((ImgHolder) holder).imageFail = (ImageView) converView
							.findViewById(R.id.msg_status);
					((ImgHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				case VOICE:
					holder = new VoiceHolder();
					converView = View
							.inflate(context, R.layout.row_sent_voice, null);
					((VoiceHolder) holder).imgvoice = (ImageView) converView
							.findViewById(R.id.iv_voice);
					((VoiceHolder) holder).lengthview = (TextView) converView
							.findViewById(R.id.tv_length);
					((VoiceHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((VoiceHolder) holder).progressBar = (ProgressBar) converView
							.findViewById(R.id.pb_sending);
					((VoiceHolder) holder).iv_unread_voice = (ImageView) converView
							.findViewById(R.id.iv_unread_voice);
					((VoiceHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				case DOCUMENT:
					holder = new FileHolder();
					converView = View.inflate(context, R.layout.row_sent_file, null);
					((FileHolder) holder).fileivew = (TextView) converView
							.findViewById(R.id.tv_file_name);
					((FileHolder) holder).txtpro = (TextView) converView
							.findViewById(R.id.percentage);
					((FileHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((FileHolder) holder).imageFail = (ImageView) converView
							.findViewById(R.id.msg_status);
					((FileHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				default:
					break;
				}
			} else {
				switch (imMessages.get(arg0).getType()) {
				case TEXT:
					holder = new TxtHolder();
					converView = View.inflate(context,
							R.layout.row_received_message, null);
					((TxtHolder) holder).messview = (TextView) converView
							.findViewById(R.id.tv_chatcontent);
					((TxtHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((TxtHolder) holder).tvName = (TextView) converView
							.findViewById(R.id.tvName);
					((TxtHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				case IMAGE:
					holder = new ImgHolder();
					converView = View.inflate(context,
							R.layout.row_received_picture, null);
					((ImgHolder) holder).imageView = (ImageView) converView
							.findViewById(R.id.iv_sendPicture);
					((ImgHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((ImgHolder) holder).imageFail = (ImageView) converView
							.findViewById(R.id.msg_status);
					((ImgHolder) holder).tvName = (TextView) converView
							.findViewById(R.id.tvName);
					((ImgHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				case VOICE:
					holder = new VoiceHolder();
					converView = View.inflate(context, R.layout.row_received_voice,
							null);
					((VoiceHolder) holder).imgvoice = (ImageView) converView
							.findViewById(R.id.iv_voice);
					((VoiceHolder) holder).lengthview = (TextView) converView
							.findViewById(R.id.tv_length);
					((VoiceHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((VoiceHolder) holder).progressBar = (ProgressBar) converView
							.findViewById(R.id.pb_sending);
					((VoiceHolder) holder).iv_unread_voice = (ImageView) converView
							.findViewById(R.id.iv_unread_voice);
					((VoiceHolder) holder).tvName = (TextView) converView
							.findViewById(R.id.tvName);
					((VoiceHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				case DOCUMENT:
					holder = new FileHolder();
					converView = View.inflate(context, R.layout.row_received_file,
							null);
					((FileHolder) holder).fileivew = (TextView) converView
							.findViewById(R.id.tv_file_name);
					((FileHolder) holder).txtpro = (TextView) converView
							.findViewById(R.id.tv_file_state);
					((FileHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((FileHolder) holder).imageFail = (ImageView) converView
							.findViewById(R.id.msg_status);

					break;
				default:
					break;
				}
			}
			converView.setTag(holder);
		} else {
			holder = (Holder) converView.getTag();
			if (imMessages.get(arg0).getDirect() == Direct.SEND) {

				switch (imMessages.get(arg0).getType()) {
				case TEXT:
					if (holder instanceof TxtHolder) {
						holder = (TxtHolder) holder;
					} else {
						holder = new TxtHolder();
					}
					converView = View.inflate(context, R.layout.row_sent_message,
							null);
					((TxtHolder) holder).messview = (TextView) converView
							.findViewById(R.id.tv_chatcontent);
					((TxtHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((TxtHolder) holder).progressBar = (ProgressBar) converView
							.findViewById(R.id.pb_sending);
					((TxtHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					((TxtHolder) holder).imageFail = (ImageView) converView
							.findViewById(R.id.msg_status);
					break;
				case IMAGE:
					if (holder instanceof ImgHolder) {
						holder = (ImgHolder) holder;
					} else {
						holder = new ImgHolder();
					}
					converView = View.inflate(context, R.layout.row_sent_picture,
							null);
					((ImgHolder) holder).imageView = (ImageView) converView
							.findViewById(R.id.iv_sendPicture);
					((ImgHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);

					((ImgHolder) holder).imageFail = (ImageView) converView
							.findViewById(R.id.msg_status);
					((ImgHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				case VOICE:
					if (holder instanceof VoiceHolder) {
						holder = (VoiceHolder) holder;
					} else {
						holder = new VoiceHolder();
					}
					converView = View
							.inflate(context, R.layout.row_sent_voice, null);
					((VoiceHolder) holder).imgvoice = (ImageView) converView
							.findViewById(R.id.iv_voice);
					((VoiceHolder) holder).lengthview = (TextView) converView
							.findViewById(R.id.tv_length);
					((VoiceHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((VoiceHolder) holder).progressBar = (ProgressBar) converView
							.findViewById(R.id.pb_sending);
					((VoiceHolder) holder).iv_unread_voice = (ImageView) converView
							.findViewById(R.id.iv_unread_voice);
					((VoiceHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				case DOCUMENT:
					if (holder instanceof FileHolder) {
						holder = (FileHolder) holder;
					} else {
						holder = new FileHolder();
					}
					converView = View.inflate(context, R.layout.row_sent_file, null);
					((FileHolder) holder).fileivew = (TextView) converView
							.findViewById(R.id.tv_file_name);
					((FileHolder) holder).txtpro = (TextView) converView
							.findViewById(R.id.percentage);
					((FileHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((FileHolder) holder).imageFail = (ImageView) converView
							.findViewById(R.id.msg_status);
					((FileHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				default:
					break;
				}
			} else {
				switch (imMessages.get(arg0).getType()) {
				case TEXT:
					if (holder instanceof TxtHolder) {
						holder = (TxtHolder) holder;
					} else {
						holder = new TxtHolder();
					}
					converView = View.inflate(context,
							R.layout.row_received_message, null);
					((TxtHolder) holder).messview = (TextView) converView
							.findViewById(R.id.tv_chatcontent);
					((TxtHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((TxtHolder) holder).tvName = (TextView) converView
							.findViewById(R.id.tvName);
					((TxtHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					((TxtHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				case IMAGE:
					if (holder instanceof ImgHolder) {
						holder = (ImgHolder) holder;
					} else {
						holder = new ImgHolder();
					}
					converView = View.inflate(context,
							R.layout.row_received_picture, null);
					((ImgHolder) holder).imageView = (ImageView) converView
							.findViewById(R.id.iv_sendPicture);
					((ImgHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((ImgHolder) holder).imageFail = (ImageView) converView
							.findViewById(R.id.msg_status);
					((ImgHolder) holder).tvName = (TextView) converView
							.findViewById(R.id.tvName);
					((ImgHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				case VOICE:
					if (holder instanceof VoiceHolder) {
						holder = (VoiceHolder) holder;
					} else {
						holder = new VoiceHolder();
					}
					converView = View.inflate(context, R.layout.row_received_voice,
							null);
					((VoiceHolder) holder).imgvoice = (ImageView) converView
							.findViewById(R.id.iv_voice);
					((VoiceHolder) holder).lengthview = (TextView) converView
							.findViewById(R.id.tv_length);
					((VoiceHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((VoiceHolder) holder).progressBar = (ProgressBar) converView
							.findViewById(R.id.pb_sending);
					((VoiceHolder) holder).iv_unread_voice = (ImageView) converView
							.findViewById(R.id.iv_unread_voice);
					((VoiceHolder) holder).tvName = (TextView) converView
							.findViewById(R.id.tvName);
					((VoiceHolder) holder).userHeaderView = (ImageView) converView
							.findViewById(R.id.iv_userhead);
					break;
				case DOCUMENT:
					if (holder instanceof FileHolder) {
						holder = (FileHolder) holder;
					} else {
						holder = new FileHolder();
					}
					converView = View.inflate(context, R.layout.row_received_file,
							null);
					((FileHolder) holder).fileivew = (TextView) converView
							.findViewById(R.id.tv_file_name);
					((FileHolder) holder).txtpro = (TextView) converView
							.findViewById(R.id.tv_file_state);
					((FileHolder) holder).timeview = (TextView) converView
							.findViewById(R.id.timestamp);
					((FileHolder) holder).imageFail = (ImageView) converView
							.findViewById(R.id.msg_status);

					break;
				default:
					break;
				}
			}
		}
		if (imMessages.get(arg0).getDirect() == Direct.SEND) {
			switch (imMessages.get(arg0).getType()) {
			case TEXT:
				ImageUtils.displayAvatar(userHeaderUrl,
						((TxtHolder) holder).userHeaderView);
				String message = ((textMessageBody) imMessages.get(arg0)
						.getBody()).getMessage();
				// ((TxtHolder)
				// holder).messview.setText(SmileUtils.getSmiledText(
				// context, message));
				// ((TxtHolder) holder).timeview.setText(DateUnit
				// .getMMddFormat1(((long)imMessages.get(arg0).getMsgTime())));
				((TxtHolder) holder).messview.setText(FaceUtils.replaceFace(
						context, message));
				setTimeTextView(arg0, ((TxtHolder) holder).timeview);

				if (imMessages.get(arg0).getStatus() == Status.SUCCESS) {
					((TxtHolder) holder).progressBar.setVisibility(View.GONE);
					LogUtils.e("-----> status SUCCESS");
				} else if (imMessages.get(arg0).getStatus() == Status.FAILED) {
					((TxtHolder) holder).progressBar.setVisibility(View.GONE);
					LogUtils.e("-----> status FAILED");
					((TxtHolder) holder).imageFail.setVisibility(View.VISIBLE);
					((TxtHolder) holder).imageFail
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									IMClient.getInstance().SendMessage(
											imMessages.get(arg0));
									IMMessageListAdapter.this
											.notifyDataSetChanged();
									LogUtils.e("-----> txt resend...");
								}
							});
				} else {
					LogUtils.e("-----> status =" + imMessages.get(arg0).getStatus());
					((TxtHolder) holder).progressBar
							.setVisibility(View.VISIBLE);
				}
				break;
			case IMAGE:
				ImageUtils.displayAvatar(userHeaderUrl,
						((ImgHolder) holder).userHeaderView);
				String smallUrl = ((imageMessageBody) imMessages.get(arg0)
						.getBody()).getThumbnailUrl();
//				Bitmap bitmap = BitmapFactory.decodeFile(smallUrl);
//				((ImgHolder) holder).imageView.setImageBitmap(bitmap);
//				if (null == bitmap) {
//					((ImgHolder) holder).imageView.setImageResource(R.drawable.default_photo);
//				} else {
//					((ImgHolder) holder).imageView.setImageBitmap(bitmap);
//				}
				
				 if (smallUrl.startsWith("http")) {
				 mApp.imageLoader.displayImage(smallUrl, ((ImgHolder)
				 holder).imageView, mApp.defaultOptionsPhoto);
				
				 } else {
				 String uri = "file://" + smallUrl;
				 mApp.imageLoader.displayImage(uri, ((ImgHolder)
				 holder).imageView, mApp.defaultOptionsPhoto);
				 LogUtils.e("---->outgoing, uri=" + uri);
				 }
				
				// ((ImgHolder) holder).timeview.setText(DateUnit
				// .getMMddFormat1(((long)imMessages.get(arg0).getMsgTime())));
				LogUtils.e("---->outgoing, img url=" + smallUrl);
				
				setTimeTextView(arg0, ((ImgHolder) holder).timeview);

				final int num = arg0;
				((ImgHolder) holder).imageView
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent();
								intent.setClass(context, ShowBigPic.class);
								Bundle bundle = new Bundle();
								bundle.putInt("type", arrayList
										.indexOf(((imageMessageBody) imMessages
												.get(num).getBody())
												.getLocalUrl()));
								bundle.putString("RemoteUrl",
										((imageMessageBody) imMessages.get(num)
												.getBody()).getLocalUrl());
								bundle.putString("chatid",
										conversation.getChatID());
								bundle.putInt("isgroup", conversation
										.getChatType().ordinal());
								bundle.putLong("msgid", imMessages.get(num)
										.getMsgId());
								intent.putExtra("data", bundle);
								context.startActivity(intent);
							}
						});

				if (imMessages.get(arg0).getStatus() == Status.FAILED) {
					((ImgHolder) holder).imageFail.setVisibility(View.VISIBLE);
					((ImgHolder) holder).imageFail
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									IMClient.getInstance().SendMessage(
											imMessages.get(arg0));
									IMMessageListAdapter.this
											.notifyDataSetChanged();
									Log.e("eeeee", "重新发送了。。。。。");
								}
							});
				}
				break;
			case VOICE:
				ImageUtils.displayAvatar(userHeaderUrl,
						((VoiceHolder) holder).userHeaderView);
				int length = ((voiceMessageBody) imMessages.get(arg0).getBody())
						.getDuration();
				String filaname = ((voiceMessageBody) imMessages.get(arg0)
						.getBody()).getFileName();
				((VoiceHolder) holder).lengthview.setText((length) + "\"");
				// ((VoiceHolder) holder).timeview.setText(DateUnit
				// .getMMddFormat1(((long)imMessages.get(arg0).getMsgTime())));
				setTimeTextView(arg0, ((VoiceHolder) holder).timeview);

				if (imMessages.get(arg0).getStatus() == Status.SUCCESS) {
					((VoiceHolder) holder).progressBar.setVisibility(View.GONE);
				} else {
					((VoiceHolder) holder).progressBar
							.setVisibility(View.VISIBLE);
				}

				((VoiceHolder) holder).imgvoice
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View view) {
								// TODO Auto-generated method stub
								MediaController.getInstance().playAudio(
										imMessages.get(arg0));
								LogUtils.e("---------------->imgvoice send...");
								playAudioAnimation(((ImageView) view), 0);
							}
						});
				break;

			case DOCUMENT:
				ImageUtils.displayAvatar(userHeaderUrl,
						((FileHolder) holder).userHeaderView);
				String filename = ((NormalFileMessageBody) imMessages.get(arg0)
						.getBody()).getFileName();
				((FileHolder) holder).fileivew.setText(filename);
				((FileHolder) holder).timeview.setText(DateUnit
						.getMMddFormat1(((long) imMessages.get(arg0)
								.getMsgTime())));
				setTimeTextView(arg0, ((FileHolder) holder).timeview);

				if (!integers.contains(arg0)) {

					if (((NormalFileMessageBody) imMessages.get(arg0).getBody())
							.getLocalUrl().equals(this.location)) {
						((FileHolder) holder).txtpro
								.setVisibility(View.VISIBLE);
						((FileHolder) holder).txtpro
								.setText(((int) (progress * 100)) + "%");
						if (iscomplate || ((int) (progress * 100)) >= 95) {
							((FileHolder) holder).txtpro
									.setVisibility(View.INVISIBLE);
						}
					}
				}

				final int num1 = arg0;
				converView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String path = ((MQ.NormalFileMessageBody) imMessages
								.get(num1).getBody()).getLocalUrl();
						System.out
								.println(((MQ.NormalFileMessageBody) imMessages
										.get(num1).getBody()).getRemoteUrl());
						File file = new File(path);
						if (path != null && file != null && file.exists()) {

							// hahaha
							// com.easemob.util.FileUtils.openFile(file,
							// (Activity) context);

						} else {
							// 下载
							new Thread(new Runnable() {

								@Override
								public void run() {
									String urlStr = ((MQ.NormalFileMessageBody) imMessages
											.get(num1).getBody())
											.getRemoteUrl();
									String path1 = "file";
									String fileName = ((MQ.NormalFileMessageBody) imMessages
											.get(num1).getBody()).getFileName();
									OutputStream output = null;
									File file = null;
									try {
										URL url = new URL(urlStr);
										HttpURLConnection conn = (HttpURLConnection) url
												.openConnection();
										String SDCard = Environment
												.getExternalStorageDirectory()
												+ "";
										String pathName = SDCard + "/" + path1
												+ "/" + fileName;// 文件存储路径

										file = new File(pathName);
										InputStream input = conn
												.getInputStream();
										long size = ((MQ.NormalFileMessageBody) imMessages
												.get(num1).getBody())
												.getFileSize();
										if (file.exists()) {
											System.out.println("exits");
											// com.easemob.util.FileUtils.openFile(file,
											// (Activity) context);
										} else if (file.exists()) {
											return;
										} else {
											String dir = SDCard + "/" + path1;
											new File(dir).mkdir();// 新建文件夹
											file.createNewFile();// 新建文件
											output = new FileOutputStream(file);
											// 读取大文件

											System.out.println("size=" + size);
											byte[] bs = new byte[1024];
											// 读取到的数据长度
											int len;
											// 输出的文件流
											// 开始读取
											while ((len = input.read(bs)) != -1) {
												output.write(bs, 0, len);
												output.flush();
											}
										}
										System.out.println("下载成功");
										integers.add(num1);
										// ((MQ.NormalFileMessageBody)
										// imMessages
										// .get(num1).getBody()).setLocalUrl(pathName);
									} catch (MalformedURLException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										try {
											if (file.exists()) {
												System.out.println("已经存在");
											} else {
												output.close();
												System.out.println("下载成功");
											}
										} catch (IOException e) {
											System.out.println("下载失败");
											e.printStackTrace();
										}
									}

								}
							}).start();

						}
					}
				});
				if (imMessages.get(arg0).getStatus() == Status.FAILED) {
					((FileHolder) holder).imageFail.setVisibility(View.VISIBLE);
				}
				break;
			default:
				break;
			}
		} else {
			switch (imMessages.get(arg0).getType()) {
			case TEXT:
				ImageUtils.displayAvatar(getUsrIconUrl(imMessages.get(arg0)
						.getFrom()), ((TxtHolder) holder).userHeaderView);
				String message = ((textMessageBody) imMessages.get(arg0)
						.getBody()).getMessage();
				// ((TxtHolder)
				// holder).messview.setText(SmileUtils.getSmiledText(
				// context, message));
				// ((TxtHolder) holder).timeview.setText(DateUnit
				// .getMMddFormat1(((long)imMessages.get(arg0).getMsgTime())));
				((TxtHolder) holder).messview.setText(FaceUtils.replaceFace(
						context, message));
				setTimeTextView(arg0, ((TxtHolder) holder).timeview);

				// if (chatType == ChatType.CHAT_TYPE_SINGLE) {
				// ((TxtHolder) holder).tvName.setVisibility(View.GONE);
				// } else {
				// String name = getContactName(imMessages.get(arg0).getFrom());
				// if (StringUtils.isEmpty(name)) {
				// ((TxtHolder) holder).tvName.setVisibility(View.GONE);
				// } else {
				// ((TxtHolder) holder).tvName.setText(name);
				// ((TxtHolder) holder).tvName.setVisibility(View.VISIBLE);
				// }
				// }
				setInCommingName(((TxtHolder) holder).tvName,
						imMessages.get(arg0).getFrom());
				break;
			case IMAGE:
				String smallUrl = ((imageMessageBody) imMessages.get(arg0)
						.getBody()).getThumbnailUrl();
//				Bitmap bitmap = BitmapFactory.decodeFile(smallUrl);
//				if (null == bitmap) {
//					((ImgHolder) holder).imageView.setImageResource(R.drawable.default_photo);
//				} else {
//					((ImgHolder) holder).imageView.setImageBitmap(bitmap);
//				}
				
				
				 if (smallUrl.startsWith("http")) {
				 mApp.imageLoader.displayImage(smallUrl, ((ImgHolder)
				 holder).imageView, mApp.defaultOptionsPhoto);
				 LogUtils.e("---->incoming, img url=" + smallUrl);
				 } else {
				 String uri = "file://" + smallUrl;
				 mApp.imageLoader.displayImage(uri, ((ImgHolder)
				 holder).imageView, mApp.defaultOptionsPhoto);
				 LogUtils.e("---->incoming, uri=" + uri);
				 }
				
				// ((ImgHolder) holder).timeview.setText(DateUnit
				// .getMMddFormat1(((long)imMessages.get(arg0).getMsgTime())));
				setTimeTextView(arg0, ((ImgHolder) holder).timeview);
				final int num = arg0;
				((ImgHolder) holder).imageView
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent();
								intent.setClass(context, ShowBigPic.class);
								Bundle bundle = new Bundle();
								bundle.putInt("type", arrayList
										.indexOf(((imageMessageBody) imMessages
												.get(num).getBody())
												.getRemoteUrl()));
								bundle.putString("chatid",
										conversation.getChatID());
								bundle.putInt("isgroup", conversation
										.getChatType().ordinal());
								bundle.putLong("msgid", imMessages.get(num)
										.getMsgId());
								intent.putExtra("data", bundle);
								context.startActivity(intent);
							}
						});
				setInCommingName(((ImgHolder) holder).tvName,
						imMessages.get(arg0).getFrom());
				ImageUtils.displayAvatar(getUsrIconUrl(imMessages.get(arg0)
						.getFrom()), ((ImgHolder) holder).userHeaderView);
				break;
			case VOICE:
				int length = ((voiceMessageBody) imMessages.get(arg0).getBody())
						.getDuration();
				String filaname = ((voiceMessageBody) imMessages.get(arg0)
						.getBody()).getFileName();
				((VoiceHolder) holder).lengthview.setText((length) + "\"");
				// ((VoiceHolder) holder).timeview.setText(DateUnit
				// .getMMddFormat1(((long)imMessages.get(arg0).getMsgTime())));
				setTimeTextView(arg0, ((VoiceHolder) holder).timeview);

				if (imMessages.get(arg0).isListened()) {
					((VoiceHolder) holder).iv_unread_voice
							.setVisibility(View.INVISIBLE);
				} else {
					((VoiceHolder) holder).iv_unread_voice
							.setVisibility(View.VISIBLE);
				}

				((VoiceHolder) holder).imgvoice
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View view) {
								// TODO Auto-generated method stub
								MediaController.getInstance().playAudio(
										imMessages.get(arg0));
								// imMessages.get(arg0).setListened(true);
								// conversation.updateMessagesListenbyMid(
								// imMessages.get(arg0).getMsgId(),
								// imMessages.get(arg0));
								// notifyDataSetChanged();
								LogUtils.e("---------------->imgvoice receive...");
								playAudioAnimation(((ImageView) view), 1);
							}
						});

				setInCommingName(((VoiceHolder) holder).tvName,
						imMessages.get(arg0).getFrom());
				ImageUtils.displayAvatar(getUsrIconUrl(imMessages.get(arg0)
						.getFrom()), ((VoiceHolder) holder).userHeaderView);
				break;

			case DOCUMENT:
				MQ.VYMessage MSG = imMessages.get(arg0);
				String filename = ((NormalFileMessageBody) MSG.getBody())
						.getFileName();
				((FileHolder) holder).fileivew.setText(filename);
				// ((FileHolder)
				// holder).timeview.setText(DateUnit.getMMddFormat1(((long)imMessages.get(arg0).getMsgTime())));
				setTimeTextView(arg0, ((FileHolder) holder).timeview);

				String SDCard = Environment.getExternalStorageDirectory() + "";
				String path1 = "file";
				String fileName = ((MQ.NormalFileMessageBody) imMessages.get(
						arg0).getBody()).getFileName();
				final String pathName = SDCard + "/" + path1 + "/" + fileName;// 文件存储路径
				int progress = MSG.getProgress();
				if (progress > 0 && progress < 100) {
					if (progress < 100)
						((FileHolder) holder).txtpro.setText(progress + "%");
				} else {

					File file = new File(pathName);
					if (file.exists()) {
						((FileHolder) holder).txtpro.setText("下载完成");
					} else {
						((FileHolder) holder).txtpro.setText("未下载");
					}
				}
				// final int num1 = arg0;
				converView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg1) {
						int s = arg0;
						int size = imMessages.size();
						MQ.VYMessage MSG = imMessages.get(arg0);
						if (MSG != null) {
							MQ.NormalFileMessageBody body = (MQ.NormalFileMessageBody) MSG
									.getBody();
							String path = body.getLocalUrl();
							// System.out.println(path);
							System.out
									.println(((MQ.NormalFileMessageBody) imMessages
											.get(arg0).getBody())
											.getRemoteUrl());
							// if (path != null &&!path.isEmpty()) {
							if (StringUtils.isEmpty(path)) {
								File file = new File(path);
								if (file != null && file.exists()) {
									// com.easemob.util.FileUtils.openFile(file,
									// (Activity) context);
								} else {
									IMClient.getInstance().download(
											imMessages.get(arg0));

								}

							} else {
								File file = new File(pathName);
								if (file != null && file.exists()) {
									// com.easemob.util.FileUtils.openFile(file,
									// (Activity) context);
								} else {
									IMClient.getInstance().download(
											imMessages.get(arg0));

								}

							}
						}
					}
				});
				break;
			default:
				break;
			}
		}
		return converView;
	}

	private String getUsrIconUrl(String from) {
		
		String url = "";

		if (StringUtils.isEmpty(from)) {
			LogUtils.e("getUsrIconUrl mDataSource or from is null!! mDataSource="
					+ mDataSource + " from=" + from);
			return url;
		}

		Contact contact = null;
		String head = Consts.APP_ID;
		long chatIdLg;

		// if (this.chatType == ChatType.CHAT_TYPE_SINGLE) {
		// contact = mContact;
		// } else {
		try {
			if (from.length() <= head.length()) {
				LogUtils.e("getUsrIconUrl, invalid group chatId! chatId="
						+ from);
			} else {
				Long id = Long.parseLong(from.substring(head.length(),
						from.length()));
				chatIdLg = id.longValue();

				DataHelper helper = DataHelper.getHelper(this.context);
				try {
					QueryBuilder<Contact, Integer> queryBuilder = helper.getContactData().queryBuilder();
					queryBuilder.where().eq("loginName", mApp.getDefaultAccount().getLoginname()).and().eq("id", chatIdLg);  
					contact = helper.getContactData().queryForFirst(queryBuilder.orderBy("usertype", true).prepare());
				} catch (SQLException e) {
					e.printStackTrace();
				}  
//				contact = mDataSource.getContactById(mApp.getDefaultAccount()
//						.getLoginname(), chatIdLg);

				LogUtils.e("getUsrIconUrl^^chatIdLg^^^^^^^" + chatIdLg);
			}
		} catch (Exception e) {
			LogUtils.e("getUsrIconUrl^^get contack err^^^^^^^" + e.getMessage());
		}
		// }

		if (contact != null) {
			url = contact.getAvatar();
		} else {
			LogUtils.e("getUsrIconUrl^^contact is null");
		}

		LogUtils.e("getUsrIconUrl^^from=" + from + " headurl=" + url);

		return url;
	}

	private void setInCommingName(TextView tv, String from) {
		if (null == tv) {
			LogUtils.e("err, tv is null----> from=" + from);
			return;
		}
		if (chatType == ChatType.CHAT_TYPE_SINGLE) {
			tv.setVisibility(View.GONE);
		} else {
			String name = getContactName(from);
			if (StringUtils.isEmpty(name)) {
				tv.setVisibility(View.GONE);
			} else {
				tv.setText(name);
				tv.setVisibility(View.VISIBLE);
			}
		}
	}

	private String getContactName(String from) {
		String strName = "";

		if (contactNameMap.containsKey(from)) {
			strName = contactNameMap.get(from);

			if (!StringUtils.isEmpty(strName)) {
				LogUtils.e("get name from map============strName=" + strName);
				return strName;
			}
		}

		if (StringUtils.isEmpty(from)) {
			LogUtils.e("mDataSource or from is null!! mDataSource="
					+ mDataSource + " from=" + from);
			return strName;
		}

		String head = Consts.APP_ID;
		long chatIdLg;

		if (from.length() <= head.length()) {
			LogUtils.e("IMMessageListAdapter, invalid chatId! chatId=" + from);
		} else {
			Long id = Long.parseLong(from.substring(head.length(),
					from.length()));
			chatIdLg = id.longValue();

			Contact contact = new Contact();
//			ClazzWorkContact contact = mDataSource.getContactById(mApp
//					.getDefaultAccount().getLoginname(), chatIdLg);
			DataHelper helper = DataHelper.getHelper(this.context);
			try {
				QueryBuilder<Contact, Integer> queryBuilder = helper.getContactData().queryBuilder();
				queryBuilder.where().eq("loginName", mApp.getDefaultAccount().getLoginname()).and().eq("id", chatIdLg);  
				contact = helper.getContactData().queryForFirst(queryBuilder.orderBy("usertype", true).prepare());
			} catch (SQLException e) {
				e.printStackTrace();
			}  

			LogUtils.e("^^^^^^^^^^chatIdLg^^^^^^^" + chatIdLg);
			if (contact != null) {
				strName = contact.getName();
				contactNameMap.put(from, strName);
			}
		}

		LogUtils.e("IMMessageListAdapter, get contact name contact name="
				+ strName);

		return strName;
	}

	private void setTimeTextView(int pos, TextView tv) {
		long lTime = (long) imMessages.get(pos).getMsgTime();

		// LogUtils.e("timetxt----->status=" + imMessages.get(pos).getStatus()
		// + " time=" + DateUnit.getMMddFormat1(lTime));

		// 第一条消息一定需要显示时间栏
		if (0 >= pos) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(DateUnit.getMMddFormat1(lTime));
		} else {
			// LogUtils.e("timetxt----->long pre=" + preMsgDate + ", time pre="
			// + DateUnit.getMMddFormat1(preMsgDate) + ", long now="
			// + lTime + ", time now=" + DateUnit.getMMddFormat1(lTime)
			// + ", mabs=" + Math.abs(preMsgDate - lTime));

			// 判断前一条消息和当前消息时间间隔是否在定义的范围内，如果大于定义范围则显示时间
			if (needShowTime(preMsgDate, lTime)) {
				tv.setText(DateUnit.getMMddFormat1(lTime));
				tv.setVisibility(View.VISIBLE);
				// LogUtils.e("timetxt----->dsp");
			} else {
				tv.setVisibility(View.GONE);
				// LogUtils.e("timetxt----->no dsp");
			}
		}
	}

	private boolean needShowTime(long pre, long now) {
		long minusMinutes = (Math.abs(now - pre)) / 60;

		// LogUtils.e("timetxt----> minusMinutes=" + minusMinutes);

		if (minusMinutes > SHOW_TIME_INTERVAL) {
			return true;
		} else {
			return false;
		}
	}

	class Holder {

	}

	class TxtHolder extends Holder {
		ProgressBar progressBar;
		TextView timeview;
		TextView messview;
		TextView tvName;
		ImageView userHeaderView;// 头像
		ImageView imageFail;
	}

	class ImgHolder extends Holder {
		TextView timeview;
		TextView tvName;
		ImageView imageView;
		ImageView imageFail;
		ImageView userHeaderView;// 头像
	}

	class FileHolder extends Holder {
		TextView timeview;
		TextView fileivew;
		TextView txtpro;
		ImageView imageFail;
		ImageView userHeaderView;// 头像
	}

	class VoiceHolder extends Holder {
		ProgressBar progressBar;
		TextView timeview;
		TextView lengthview;
		TextView tvName;
		ImageView imgvoice;
		ImageView iv_unread_voice;
		ImageView userHeaderView;// 头像
	}

	@Override
	public void didReceivedNotification(int id, Object... args) {
		if (id == NotificationCenter.UploadFileProgressChanged) {
			this.location = (String) args[0];
			this.progress = (Float) args[1];
			System.out.println("progress=" + progress);
			System.out.println("location=" + location);

		} else if (id == NotificationCenter.UploadFileCompleted) {
			this.location = (String) args[0];
			this.iscomplate = true;
			System.out.println("finish=" + iscomplate);
		} else if (id == NotificationCenter.UploadFileFailed) {
			Toast.makeText(context, "上传文件失败！", Toast.LENGTH_SHORT).show();
		} else if (id == NotificationCenter.DownloadFileProgressChanged) {
			// Float process = (Float) args[0];
			// float proces = process;
			// pos = (Integer) args[1];
			// downprogress = (int) proces;
		} else if (id == NotificationCenter.playaudiocomplete) {
			Log.e("didReceivedNotification", "playaudiocomplete--->");
			setFinish(true);
		}

		notifyDataSetChanged();
	}

	private void playAudioAnimation(final ImageView imageView, final int inOrOut) {

		LogUtils.i("playam, imageView=" + imageView + ", inOrOut=" + inOrOut);

		setFinish(false);
		// 定时器检查播放状态
		stopTimer();
		mTimer = new Timer();
		// 将要关闭的语音图片归位
		if (audioAnimationHandler != null) {
			LogUtils.i("playam, audioAnimationHandler != null");
			Message msg = new Message();
			msg.what = 3;
			audioAnimationHandler.sendMessage(msg);
		}

		audioAnimationHandler = new AudioAnimationHandler(imageView, inOrOut);
		index = 1;
		mTimerTask = new TimerTask() {
			public boolean hasPlayed = false;

			@Override
			public void run() {
				LogUtils.i("playam, timer is going...");
				// if (splayer.isAlive()) {
				if (!isFinish()) {
					hasPlayed = true;
					index = (index + 1) % 3;
					Message msg = new Message();
					msg.what = index;
					LogUtils.i("playam, playing---> index=" + index);
					audioAnimationHandler.sendMessage(msg);
				} else {
					// 当播放完时
					Message msg = new Message();
					msg.what = 3;
					LogUtils.i("playam, finishing---> 3");
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

		LogUtils.i("playam, finish timer or task----> mTimer=" + mTimer
				+ " mTimerTask=" + mTimerTask);
	}

	public synchronized boolean isFinish() {
		return isFinish;
	}

	public synchronized void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
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

			LogUtils.i("playam, handler---> imageView=" + imageView
					+ ", inOrOut=" + inOrOut);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			LogUtils.i("playam, handler---> rev msg what=" + msg.what
					+ ", isLeft=" + isLeft);

			// 根据msg.what来替换图片，达到动画效果
			switch (msg.what) {
			case 0:
				imageView
						.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f1
								: R.drawable.chat_out_voice_playing_f1);
				LogUtils.i("playam, handler---> 0, pic="
						+ (isLeft ? R.drawable.chat_in_voice_playing_f1
								: R.drawable.chat_out_voice_playing_f1));
				break;
			case 1:
				imageView
						.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f2
								: R.drawable.chat_out_voice_playing_f2);
				LogUtils.i("playam, handler---> 1, pic="
						+ (isLeft ? R.drawable.chat_in_voice_playing_f2
								: R.drawable.chat_out_voice_playing_f2));
				break;
			case 2:
				imageView
						.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f3
								: R.drawable.chat_out_voice_playing_f3);
				LogUtils.i("playam, handler---> 1, pic="
						+ (isLeft ? R.drawable.chat_in_voice_playing_f3
								: R.drawable.chat_out_voice_playing_f3));
				break;
			default:
				imageView
						.setImageResource(isLeft ? R.drawable.chat_in_voice_playing_f3
								: R.drawable.chat_out_voice_playing_f3);
				LogUtils.i("playam, handler---> 1, pic="
						+ (isLeft ? R.drawable.chat_in_voice_playing_f3
								: R.drawable.chat_out_voice_playing_f3));
				break;
			}
		}

	}

}
