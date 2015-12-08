package com.linkage.mobile72.sh.im.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.linkage.mobile72.sh.Consts;

public class Ws {
	
	public static final String AUTHORITY = Consts.IM_AUTHORITY;
	
	private Ws() {}
	
	/**
	 * 对话列<br>
	 * 对话是指发生的交互动作,比如与某人的对话，这整个过程就是一个thread<br>
	 * 
	 */
	public interface ThreadColumns {
		
		
		String ACCOUNT_NAME = "account_name";
		
		/**
		 *  消息id 
		 *	<P>Type: INT</P>
		 */
		String MSG_ID = "msg_id";
		
		/** 
		 * 好友ID /　群组ID
		 * <P>Type: INT</P>
		 */
		String BUDDY_ID = "buddy_id";
		
		/** 
		 * 好友名称 /�?��组名称
		 * <P>Type: INT</P>
		 */
		String BUDDY_NAME = "buddy_name";
		
		/** 
		 * 消息内容 
		 * <P>Type: TEXT</P>
		 */
		String MSG_BODY = "msg_body";
		
		/** 
		 * 收到消息时间 
		 * <P>Type: INT</P>
		 */
		String MSG_RECEIVED_TIME = "msg_receive_time";
		
		/** 
		 * 消息发送的时间 
		 * <P>Type: INT</P>
		 */
		String MSG_SENT_TIME = "msg_sent_time";
		
		/** 
		 * 未阅读数 
		 * <P>Type: INT</P>
		 */
		String UNREAD_COUNT = "unread_count";
		
		/**
		 *  发出消息的状态 
		 *  <P>Type: INT</P>
		 */
		String MSG_OUTBOUND_STATUS = "msg_outbound_status";
		
		/** 
		 * 接受的消息标志
		 * <P>Type: INT</P>
		 */
		String MSG_IS_INBOUND = "msg_is_inbound";
		
		/** 
		 * 消息类型 
		 * <P>Type: INT</P>
		 */
		String MSG_TYPE = "msg_type";
		
		/**
		 * 用户id
		 */
		String USER_ID = "user_id";
		
		/**
		 * 聊天类型
		 */
		String CHAT_TYPE = "chat_type";
		/**
		 * 用户区分家庭作业，班级，以及精彩资讯等和im聊天，并作为排序的依据置顶作业和班级，为0，班级为1，精彩资讯与im聊天为2//***
		 * 班级0，空间1，资讯2，系统通知3,4预留，5im聊天
		 */
		String THREAD_TYPE = "thread_type";
		
		//String USER_TYPE = "user_type";
	}
	
	/**
	 * 事件表<br>
	 * 
	 * @author songpeng
	 *
	 */
	public static final class ThreadTable implements BaseColumns, ThreadColumns {
		private ThreadTable() {}
		
		/**
		 * 此表的uri
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/threads");
		
		public static final Uri CONTENT_URI_BY_BUDDY_ID = Uri.parse("content://" + AUTHORITY + "/threadByBuddyId");
		
		/**
		 * MIME type
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/imps-threads";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/imps-threads";
		
		/**
		 * 广播url
		 */
		public static final String CONTENT_CHAGED = "com.linkage.mobile72.sh.threadtable.changed";
		
	}
	
	/**
	 * 消息类型
	 * 
	 * @author songpeng
	 *
	 */
	public interface MessageType {
		
		/** 
		 * 发送消息
		 */
		int OUTGOINT = 0;
		/**
		 * 接收的消息 
		 */
		int INCOMING = 1;
		
		/**
		 * 文本消息 
		 */
		int TYPE_MSG_TEXT = 1;
		
		/** 
		 * 音频消息  
		 */
		int TYPE_MSG_AUDIO = 2;
		
		/** 
		 * 图片消息 
		 */
		int TYPE_MSG_PIC = 3;
		
		
		int TYPE_MSG_HOMEWORK = 4;
		
		int TYPE_MSG_NOTICE = 5;
		int TYPE_MSG_JOB = 6;
		int TYPE_MSG_GRADE = 7;
		int TYPE_MSG_SAFE = 8;
		int TYPE_NEWS = 9;
		int TYPE_CLASS_SPACE =10;
		int TYPE_SYSTEM=11;
		int TYPE_SYLLABUS=12;
		int TYPE_MSG_FILE = 13;
		int TYPE_JX = 14;
		/**
		 * 发送失败状态 
		 */
		int MSG_STATUS_ERROR = -1;
		
		/**
		 * 待发送状态 ，尚未发送成功
		 */
		int MSG_STATUS_WAITING = 0;
		
		/**
		 * 已发送状态 ，发送成功
		 */
		int MSG_STATUS_SENT = 1;
		
		/** 
		 * 已阅读状态 
		 */
		int MSG_STATUS_ARRIVED = 2;
		
		/** 
		 * 已阅读状态 
		 */
		int MSG_STATUS_READED = 3;
		
		
		String MSG_TYPE_All = "('1','2','3','4','5')";
		
		String MSG_TYPE_CHAT = "('1','2','3')";
		
		String MSG_TYPE_HOME_NOTICE = "('4','5','6','7','8')";
		
		
	} 

	/**
	 * 消息列
	 * 
	 * @author songpeng
	 *
	 */
	public interface MessageColumns {
		
		String ACCOUNT_NAME = "account_name";
		
		/** 
		 * 好友ID /　群组ID
		 * <P>Type: INT</P>
		 */
		String BUDDY_ID = "buddy_id";
		
		/** 
		 * 好友名称 /�?��组名称
		 * <P>Type: INT</P>
		 */
		String BUDDY_NAME = "buddy_name";
		
		/**
		 * 消息内容 
		 * <P>Type: TEXT</P>
		 */
		String BODY = "body";
		
		/** 
		 * 接受的消息标志 
		 * <P>Type: INT</P>
		 */
		String IS_INBOUND = "is_inbound";
		
		/** 
		 * 是否阅读标志 
		 * <P>Type: INT</P>
		 */
		String IS_READ = "is_read";
		
		/**
		 * 发出的消息的状态 是否发出 是否被读etc -1 发送失， 0 未发送成功 ，  1成功未接受，  2已读
		 * <P>Type: INT</P>
		 */
		String OUTBOUND_STATUS = "outbound_status";
		
		/**
		 * 收到时间 
		 * <P>Type: INT</P>
		 */
		String RECEIVED_TIME = "received_time";
		
		/** 
		 * 发送者ID 
		 * <P>Type: INT</P>
		 */
		String SENDER_ID = "sender_id";
		
		/**
		 * 发送时间 
		 * <P>Type: INT</P>
		 */
		String SENT_TIME = "sent_time";
		
		/**
		 * 消息类型 
		 * <P>Type: INT</P>
		 */
		String TYPE = "type";
		
		/**
		 * 发送者设备id 
		 * <P>Type: INT</P>
		 */
		String SENDER_DEVICE_ID = "sender_device_id";
		
		/**
		 * 群组id
		 * <p>TYPE: INTEGER</p>
		 */
		String CHAT_TYPE = "chat_type";
		/**
		 * 用户区分家庭作业，班级，以及精彩资讯等和im聊天，并作为排序的依据置顶作业和班级，作业为0，班级为1，精彩资讯与im聊天为2
		 */
		String THREAD_TYPE = "thread_type";
		
	}
	
	/**
	 * 存储消息的表<br>
	 * 
	 * @author songpeng
	 */
	public static final class MessageTable implements BaseColumns, MessageColumns {
		
		private MessageTable() {
		}
		
		/**
		 * 此表的uri
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/messages");
		
		/**
		 * Content URL
		 */
		public static final Uri CONTENT_URI_MESSAGES_BY_BUDDY_ID = Uri
				.parse("content://" + AUTHORITY + "/messagesByBuddyId");
		
		/**
		 * Group Content URL
		 */
		public static final Uri CONTENT_URI_MESSAGES_BY_GROUP_ID = Uri
				.parse("content://" + AUTHORITY + "/messagesByGroupId");
		
		/**
		 * MIME TYPE
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/imps-messages";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/imps-messages";
		
		/**
		 * 获得通过好友的id获得聊天记录的uri
		 * 
		 * @param buddyId
		 * @return
		 */
		public static final Uri getContentUriByBuddyId(long buddyId) {
			Uri.Builder builder = CONTENT_URI_MESSAGES_BY_BUDDY_ID.buildUpon();
			ContentUris.appendId(builder, buddyId);
			return builder.build();
		}
	}
	
	/**
	 * 附件类型<br>
	 * 
	 * @author songpeng
	 *
	 */
	public interface AttachmentType {
		
		String MIME_TYPE_AUDIO = "audio/";
		
		String MIME_TYPE_IMAGE_PNG = "image/png";
		
		String MIME_TYPE_IMAGE_JPGE = "image/jpg";
	}
	
	/**
	 * 附件表的列<br>
	 * 
	 * @author songpeng
	 *
	 */
	public interface AttachmentColumns {
		
		/**
		 * 附件id 
		 * <P>Type: INT</P>
		 */
		String ATTACHMENT_ID = "attachment_id";
		
		/**
		 * 附件类型 
		 * <P>Type: INT</P>
		 */
		String MIME_TYPE = "mime_type";
		
		/**
		 * 资源id 不知道小米怎么生成的?
		 * <P>Type: INT</P>
		 */
		String RESOURCE_ID = "resource_id";
		
		/**
		 * 文件名称 
		 * <P>Type: TEXT</P>
		 */
		String FILE_NAME = "filename";
		
		/**
		 * 本地路径 
		 * <P>Type: INT</P>
		 */
		String LOCAL_PATH = "local_path";
		
		/**
		 * 文件大小 
		 * <P>Type: INT</P>
		 */
		String FILE_SIZE = "file_size";
		
		/**
		 * 状态 
		 * <P>Type: INT</P>
		 */
		String STATUS = "status";
		
		/**
		 * 音频时间长度 秒 
		 * <P>Type: INT</P>
		 */
		String AUDIO_LEN = "audio_len";
		
		/**
		 * 所属的消息的id 
		 * <P>Type: INT</P>
		 */
		String EXT_ID = "ext_id";
		
		/**
		 * ??小米的发送json格式 
		 * <P>Type: TEXT</P>
		 */
		String EXTENSION = "extension";
	}
	
	/**
	 * 附件表<br>
	 * 
	 * @author songpeng
	 *
	 */
	public static final class AttachmentTable implements BaseColumns, AttachmentColumns {
		private AttachmentTable() {}
		
		/**
		 * 此表的uri
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/attachments");
		
		/**
		 * MIME TYPE
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/imps-attachments";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/imps-attachments";
	}
	
	public interface ContactColumns {
		String ACCOUNT_NAME = "account_name";
		String ID = "id";
		String NAME = "name";
		String TYPE = "type";
		String GROUP_ID = "group_id";
		String GROUP_NAME = "group_name";
		String AVATAR_URL = "avatar_url";
		String STATUS = "status";
		String CONTACT_TYPE = "contact_type";
	}
	
	public static final class ContactTable implements BaseColumns, ContactColumns {
		private ContactTable() {}
		
		public static final int TYPE_FRIEND = 1;
		public static final int TYPE_CLASS = 2;
		
		public static final String PATH = "contacts";
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/imps-contacts";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/imps-contacts";
		
		public static final String TABLE_NAME = "contacts";
		public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "("
				+ ACCOUNT_NAME + " TEXT,"
				+ CONTACT_TYPE + " INTEGER,"
				+ ID + " INTEGER,"
				+ NAME + " TEXT,"
				+ TYPE + " INTEGER,"
				+ GROUP_ID + " INTEGER,"
				+ GROUP_NAME + " TEXT,"
				+ AVATAR_URL + " TEXT,"
				+ STATUS + " TEXT"
				+ ");";
	}
	
	public interface SettingColumns {
		String ACCOUNT_NAME = "account_name";
		String MESSAGE_NOTIFY = "message_notify";
		String MESSAGE_VOICE = "message_voice";
		String MESSAGE_VIBRATE = "message_vibrate";
		String STATUS_NOTIFY = "status_notify";
		String STATUS_VOICE = "status_voice";
		String STATUS_VIBRATE = "status_vibrate";
	}
	
	public static class SettingTable implements BaseColumns, SettingColumns {
		public static final String PATH = "settings";
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/imps-settings";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/imps-settings";
		
		public static final String TABLE_NAME = "settings";
		public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "("
				+ ACCOUNT_NAME + " TEXT UNIQUE,"
				+ MESSAGE_NOTIFY + " integer DEFAULT 1,"
				+ MESSAGE_VOICE + " INTEGER DEFAULT 1,"
				+ MESSAGE_VIBRATE + " INTEGER DEFAULT 1,"
				+ STATUS_NOTIFY + " integer DEFAULT 1,"
				+ STATUS_VOICE + " INTEGER DEFAULT 1,"
				+ STATUS_VIBRATE + " INTEGER DEFAULT 1"
				+ ");";
		
	}
	public interface RecordsColumns {
		String ACCOUNT_NAME = "account_name";
		String RECORDS_ID = "records_id";
		String RECIVER = "reciver";
		String RECORDS_TIME = "records_time";
		String RECIVER_AVATAL = "reciver_avatal";
		String SHORT_DN = "short_dn";
	}
	public static class RecordsTable implements BaseColumns, RecordsColumns {
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/records");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/imps-records";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/imps-records ";
	}
}

