package com.linkage.mobile72.sh.widget;

import info.emm.LocalData.DateUnit;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.im.provider.Ws;
import com.linkage.mobile72.sh.utils.AvatarUrlUtils;
import com.linkage.mobile72.sh.utils.FaceUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.lib.util.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ChatListItem extends LinearLayout {

	// private ChatsActivity mActivity;
	private ImageView mAvatarView;
	private TextView mBuddyNameView;
	private TextView mMsgTextView;
	private TextView mDateView;
	private TextView mUnreadCountView;
	private ImageView unread_iv;
	Context mContext;

	private int mBuddyIdColumn;
	private int mBuddyNameColumn;
	private int mMsgBodyColumn;
	private int mRecieveTimeColumn;
	private int mSentTimeColumn;
	private int mIsInBoundColumn;
	private int mMsgTypeColumn;
	private int mChatTypeColumn;
	private int mUserIdColumn;
	private int mUnreadCountColumn;
	BaseApplication mBaseApp = BaseApplication.getInstance();
	AccountData account = mBaseApp.getDefaultAccount();

	// AccountDB mAccountDb = mBaseApp.getAccountDB();

	public ChatListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// mActivity = (ChatsActivity) context.getActivity();
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mAvatarView = (ImageView) findViewById(R.id.avatar);
		mBuddyNameView = (TextView) findViewById(R.id.buddy_name);
		mMsgTextView = (TextView) findViewById(R.id.msg_text);
		mDateView = (TextView) findViewById(R.id.date);
		mUnreadCountView = (TextView) findViewById(R.id.unread_count);
		unread_iv = (ImageView) findViewById(R.id.unread_iv);
	}

	public void init(Cursor cursor) {
		mBuddyIdColumn = cursor.getColumnIndexOrThrow(Ws.ThreadTable.BUDDY_ID);
		mBuddyNameColumn = cursor
				.getColumnIndexOrThrow(Ws.ThreadTable.BUDDY_NAME);
		mMsgBodyColumn = cursor.getColumnIndexOrThrow(Ws.ThreadTable.MSG_BODY);
		mRecieveTimeColumn = cursor
				.getColumnIndexOrThrow(Ws.ThreadTable.MSG_RECEIVED_TIME);
		mSentTimeColumn = cursor
				.getColumnIndexOrThrow(Ws.ThreadTable.MSG_SENT_TIME);
		mIsInBoundColumn = cursor
				.getColumnIndexOrThrow(Ws.ThreadTable.MSG_IS_INBOUND);
		mMsgTypeColumn = cursor.getColumnIndexOrThrow(Ws.ThreadTable.MSG_TYPE);
		mChatTypeColumn = cursor
				.getColumnIndexOrThrow(Ws.ThreadTable.CHAT_TYPE);
		mUserIdColumn = cursor.getColumnIndexOrThrow(Ws.ThreadTable.USER_ID);
		mUnreadCountColumn = cursor
				.getColumnIndexOrThrow(Ws.ThreadTable.UNREAD_COUNT);
	}

	public void bindView(Cursor cursor) {
		Resources rs = getResources();
		DataHelper helper = DataHelper.getHelper(mContext);
		ImageView avatarIcon = mAvatarView;
		TextView buddyNameView = mBuddyNameView;
		TextView msgText = mMsgTextView;
		TextView dateText = mDateView;

		// 获取用户信息
		String buddyId_bak = cursor.getString(mBuddyIdColumn);
		int chattype = cursor.getInt(mChatTypeColumn);// 0单聊 1群聊 2通知
		String msgBody = cursor.getString(mMsgBodyColumn);
		String buddyName = cursor.getString(mBuddyNameColumn);
		int unreadcount = cursor.getInt(mUnreadCountColumn);
		// 消息主题
		int msgType = cursor.getInt(mMsgTypeColumn);// 1文本消息 2音频消息 3图片消息 4作业通知
													// 5通知
		String user = "";
		long buddyId = 0;
		LogUtils.v("buddyId_bak:+++++++++++  " + buddyId_bak);
		if (chattype == ChatType.CHAT_TYPE_SINGLE) {
			buddyId = Long.parseLong(buddyId_bak.substring(Consts.APP_ID.length(), buddyId_bak.length()));
			buddyNameView.setText(buddyName);
			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheOnDisc().showStubImage(R.drawable.default_user)
					.cacheInMemory()
					.showImageForEmptyUri(R.drawable.default_user)
					.showImageOnFail(R.drawable.default_user).build();

			String loginName = BaseApplication.getInstance()
					.getDefaultAccount().getLoginname();
			try {
				QueryBuilder<Contact, Integer> contactBuilder = helper
						.getContactData().queryBuilder();
				contactBuilder.where().eq("loginName", loginName).and()
						.eq("id", buddyId);
				List<Contact> mContacts = contactBuilder.query();
				if (mContacts != null && mContacts.size() > 0) {
					Contact contact = (Contact) mContacts.get(0);
					BaseApplication.getInstance().imageLoader.displayImage(
							contact.getAvatar(), avatarIcon, defaultOptions);
				} else {
					BaseApplication.getInstance().imageLoader.displayImage(
							AvatarUrlUtils.getAvatarUrl(buddyId), avatarIcon,
							defaultOptions);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else if (chattype == ChatType.CHAT_TYPE_GROUP) {
			buddyId = Long.parseLong(buddyId_bak.substring(Consts.APP_ID0.length(), buddyId_bak.length()));
			buddyNameView.setText(buddyName);
			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheOnDisc().showStubImage(R.drawable.default_group)
					.showImageForEmptyUri(R.drawable.default_group)
					.showImageOnFail(R.drawable.default_group).build();
			BaseApplication.getInstance().imageLoader.displayImage(
					AvatarUrlUtils.getGroupUrl(buddyId), avatarIcon,
					defaultOptions);
		} else if (chattype == ChatType.CHAT_TYPE_NOTICE) {
			buddyId = Long.parseLong(buddyId_bak);
			user = buddyName;
			switch (msgType) {
			case Ws.MessageType.TYPE_MSG_HOMEWORK:
				// buddyNameView.setText(getResources().getString(R.string.notice_item_home_title,
				// user));
				// avatarIcon.setImageResource(R.drawable.home_avatar);
				break;
			default:
				DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
						.cacheOnDisc().showStubImage(R.drawable.default_user)
						.showImageForEmptyUri(R.drawable.default_user)
						.showImageOnFail(R.drawable.default_user).build();
				BaseApplication.getInstance().imageLoader.displayImage(
						AvatarUrlUtils.getGroupUrl(buddyId), avatarIcon,
						defaultOptions);
				break;
			}
		}
		// if(!TextUtils.isEmpty(user))
		// {
		// user = user+":";
		// }
		switch (msgType) {
		case Ws.MessageType.TYPE_MSG_TEXT:
			msgBody = cursor.getString(mMsgBodyColumn);
			msgText.setText(user + FaceUtils.replaceFace(mContext, msgBody));
			break;
		case Ws.MessageType.TYPE_MSG_PIC:
			msgText.setText(user + rs.getString(R.string.pic));
			break;
		case Ws.MessageType.TYPE_MSG_AUDIO:
			msgText.setText(user + rs.getString(R.string.audio));
			break;
		case Ws.MessageType.TYPE_MSG_HOMEWORK:
			msgBody = cursor.getString(mMsgBodyColumn);
			msgText.setText(FaceUtils.replaceFace(mContext, msgBody));
			break;
		case Ws.MessageType.TYPE_MSG_NOTICE:
			msgBody = cursor.getString(mMsgBodyColumn);
			// msgText.setText(FaceUtils.replaceFace(mContext, msgBody));
			buddyNameView.setText("缴费提醒");
			msgText.setText(msgBody);
			break;
		}
		
		// 日期
		if (msgType == Ws.MessageType.TYPE_MSG_TEXT
				|| msgType == Ws.MessageType.TYPE_MSG_PIC
				|| msgType == Ws.MessageType.TYPE_MSG_AUDIO) {
			int isInBound = cursor.getInt(mIsInBoundColumn);
			boolean inBound = isInBound == Ws.MessageType.INCOMING ? true
					: false;
			long dateTime;
			if (inBound) {
				dateTime = cursor.getLong(mRecieveTimeColumn);
			} else {
				dateTime = cursor.getLong(mSentTimeColumn);
			}
//					SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
//					java.util.Date dt = new Date(dateTime);

//					String sDateTime = Utilities.getRelativeDate(mContext, dt);
			String sDateTime1 = DateUnit.getMMddFormat1(dateTime);
			String sDateTime = "";
			int index = sDateTime1.indexOf("-");
			
			if (sDateTime1.length() <= index) {
				dateText.setText("");
				LogUtils.e("###### strange datetime, sDateTime=" + sDateTime1);
			} else {
				
				sDateTime = sDateTime1.substring(0, index);
				
				if (isValid(sDateTime)) {
					sDateTime = sDateTime1.substring(index+1, sDateTime1.length());
					dateText.setText(sDateTime);
					dateText.setVisibility(View.VISIBLE);
				} else {
					dateText.setText("");
				}
				
			}
			// if (chattype == ChatType.CHAT_TYPE_GROUP
			// && StringUtil.isNullOrEmpty(msgBody)) {
			// dateText.setText("");
			// }

			 LogUtils.e("buddid = "+buddyId + "  chattype = "+chattype
			 + "  msgBody = "+msgBody + " sDateTime1=" + sDateTime1
			 + " sDateTime=" + sDateTime
			 + " long time=" + dateTime);
			
		} else {
			dateText.setVisibility(View.GONE);
		}

//		int isInBound = cursor.getInt(mIsInBoundColumn);
//		boolean inBound = isInBound == Ws.MessageType.INCOMING ? true : false;
//		
//		String dateTime;
//		if (inBound) {
//			dateTime = cursor.getString(mRecieveTimeColumn);
//		} else {
//			dateTime = cursor.getString(mSentTimeColumn);
//		}
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		java.util.Date dt;
//		try {
//			if (dateTime != null) {
//				dt = sdf.parse(dateTime);
//				String sDateTime = Utilities.getRelativeDate(mContext, dt);
//				LogUtils.e("dateText::" + sDateTime);
//				dateText.setText(sDateTime);
//			}
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}// new Date(dateTime);

		if (unreadcount <= 0) {
			mUnreadCountView.setVisibility(View.GONE);
			unread_iv.setVisibility(View.GONE);
		} else {
			switch (msgType) {
			case Ws.MessageType.TYPE_MSG_TEXT:
			case Ws.MessageType.TYPE_MSG_PIC:
			case Ws.MessageType.TYPE_MSG_AUDIO:
				unread_iv.setVisibility(View.GONE);
				mUnreadCountView.setVisibility(View.VISIBLE);
				mUnreadCountView.setBackgroundResource(R.drawable.unread_bg);
				mUnreadCountView.setText(String.valueOf(unreadcount));
				break;
			case Ws.MessageType.TYPE_MSG_HOMEWORK:
			case Ws.MessageType.TYPE_MSG_NOTICE:
				unread_iv.setVisibility(View.GONE);
				mUnreadCountView.setVisibility(View.VISIBLE);
				mUnreadCountView.setBackgroundResource(R.drawable.unread_bg);
				break;
			}
		}
	}
	
	private boolean isValid(String y) {
		
		boolean ret = false;
		
		try {
			int year = Integer.parseInt(y);
			if (year > 1970 && year < 9999) {
				return true;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			ret = false;
		}
		
		return ret;
	}
}
