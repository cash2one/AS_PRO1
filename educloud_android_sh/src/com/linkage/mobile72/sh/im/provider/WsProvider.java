package com.linkage.mobile72.sh.im.provider;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.linkage.mobile72.sh.im.provider.Ws.AttachmentTable;
import com.linkage.mobile72.sh.im.provider.Ws.ContactTable;
import com.linkage.mobile72.sh.im.provider.Ws.MessageTable;
import com.linkage.mobile72.sh.im.provider.Ws.MessageType;
import com.linkage.mobile72.sh.im.provider.Ws.RecordsTable;
import com.linkage.mobile72.sh.im.provider.Ws.SettingTable;
import com.linkage.mobile72.sh.im.provider.Ws.ThreadTable;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

/**
 * A Provider for IM
 * 
 * @author songpeng
 * 
 */
public class WsProvider extends ContentProvider {

	private static final String LOG_TAG = "ImpsProvider";
	private static final boolean DBG = true;

	private static final String TABLE_MESSAGES = "messages";
	private static final String TABLE_ATTACHMENTS = "attachments";
	private static final String TABLE_THREADS = "threads";
	private static final String TABLE_RECORDS = "records";

	private static final String DATABASE_NAME = "educloud_im.db";
	private static final int DATABASE_VERSION = 18;

	private static final int MATCH_MESSAGES = 1;
	private static final int MATCH_MESSAGES_BY_BUDDY_ID = 3;
	private static final int MATCH_MESSAGE = 4;
	private static final int MATCH_MESSAGES_BY_GROUP_ID = 5;
	private static final int MATCH_MESSAGES_BY_ID = 6;

	private static final int MATCH_THREADS = 10;
	private static final int MATCH_THREADS_BY_ID = 11;
	// private static final int MATCH_THREAD = 12;
	private static final int MATCH_THREAD_BY_BUDDY_ID = 13;

	private static final int MATCH_ATTACHMENTS = 20;
	private static final int MATCH_ATTACHMENTS_BY_ID = 21;

	private static final int MATCH_CONTACT = 30;

	private static final int MATCH_SETTINGS = 40;

	private static final int MATCH_RECORDS = 50;

	protected final UriMatcher mUrlMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	// private static final HashMap<String, String> sMessagesProjectionMap;
	// private static final HashMap<String, String> sThreadsProjectionMap;
	// private static final HashMap<String, String> sAttachmentsProjectionMap;

	private DatabaseHelper mOpenHelper;
	private String mDatabaseName;
	private int mDatabaseVersion;

	private class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, mDatabaseName, null, mDatabaseVersion);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_THREADS + "("
					+ "_id INTEGER PRIMARY KEY," + "account_name text,"
					//+ "msg_id INTEGER," + "buddy_id INTEGER,"
					+ "msg_id INTEGER," + "buddy_id TEXT,"
					+ "msg_body TEXT," + "msg_receive_time INTEGER DEFAULT 0,"
					+ "msg_sent_time INTEGER DEFAULT 0,"
					+ "unread_count INTEGER DEFAULT 0,"
					+ "msg_outbound_status INTEGER DEFAULT 0,"
					+ "msg_is_inbound INTEGER," + "msg_type INTEGER,"
					+ "user_id INTEGER DEFAULT 0,"
					+ "chat_type INTEGER INTEGER DEFAULT 0,"
					+ "buddy_name TEXT,"
					+ "thread_type INTEGER DEFAULT 5" + ");");
//					+ "thread_type INTEGER DEFAULT 5,"
//					+ "user_type INTEGER DEFAULT 1" + ");");

			db.execSQL("CREATE TABLE " + TABLE_ATTACHMENTS + "("
					+ "_id INTEGER PRIMARY KEY," + "attachment_id INTEGER,"
					+ "mime_type TEXT," + "resource_id INTEGER,"
					+ "filename TEXT," + "local_path TEXT,"
					+ "file_size INTEGER," + "status INTEGER,"
					+ "audio_len INTEGER," + "ext_id INTEGER,"
					+ "extension TEXT" + ");");

			db.execSQL("CREATE TABLE " + TABLE_MESSAGES + "("
					+ "_id INTEGER PRIMARY KEY," + "account_name text,"
					+ "buddy_id INTEGER," + "body TEXT,"
					//+ "buddy_id TEXT," + "body TEXT,"
					+ "is_inbound INTEGER," + "is_read INTEGER,"
					+ "outbound_status INTEGER,"
					+ "buddy_name TEXT,"
					+ "received_time INTEGER DEFAULT 0," + "sender_id INTEGER,"
					+ "sent_time INTEGER," + "type INTEGER,"
					+ "chat_type INTEGER INTEGER DEFAULT 0,"
					+ "thread_type INTEGER DEFAULT 5" + ");");
			
			db.execSQL(ContactTable.CREATE_TABLE_SQL);
			db.execSQL(SettingTable.CREATE_TABLE_SQL);
			db.execSQL("CREATE TABLE " + TABLE_RECORDS + "("
					+ "_id INTEGER PRIMARY KEY," + "account_name text,"
					+ "records_id INTEGER," + "reciver TEXT,"
					+ "records_time INTEGER DEFAULT 0,"
					+ "reciver_avatal TEXT," + "short_dn TEXT" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			destroyOldTables(db);
			onCreate(db);
		}

		private void destroyOldTables(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTACHMENTS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_THREADS);
			db.execSQL("DROP TABLE IF EXISTS " + "contacts");
			db.execSQL("DROP TABLE IF EXISTS " + "settings");
			db.execSQL("DROP TABLE IF EXISTS " + "records");
		}

	}

	// static {
	// sAttachmentsProjectionMap = new HashMap<String, String>();
	// sAttachmentsProjectionMap.put(AttachmentTable._COUNT,
	// "attachments._count AS _count");
	// sAttachmentsProjectionMap.put(AttachmentTable._ID,
	// "attachments._id AS _id");
	// sAttachmentsProjectionMap.put(AttachmentTable.ATTACHMENT_ID,
	// "attachments.attachment_id AS attachment_id");
	// sAttachmentsProjectionMap.put(AttachmentTable.AUDIO_LEN,
	// "attachments.audio_len AS audio_len");
	// sAttachmentsProjectionMap.put(AttachmentTable.EXT_ID,
	// "attachments.ext_id AS ext_id");
	// sAttachmentsProjectionMap.put(AttachmentTable.EXTENSION,
	// "attachments.extension AS extension");
	// sAttachmentsProjectionMap.put(AttachmentTable.FILE_NAME,
	// "attachments.filename AS filename");
	// sAttachmentsProjectionMap.put(AttachmentTable.LOCAL_PATH,
	// "attachments.local_path AS local_path");
	// sAttachmentsProjectionMap.put(AttachmentTable.MIME_TYPE,
	// "attachments.mime_type AS mime_type");
	// sAttachmentsProjectionMap.put(AttachmentTable.RESOURCE_ID,
	// "attachments.resource_id AS resource_id");
	// sAttachmentsProjectionMap.put(AttachmentTable.STATUS,
	// "attachments.status AS status");
	//
	// sThreadsProjectionMap = new HashMap<String, String>();
	// sThreadsProjectionMap.put(ThreadTable._COUNT,
	// "threads._count AS _count");
	// sThreadsProjectionMap.put(ThreadTable._ID, "threads._id AS _id");
	// sThreadsProjectionMap.put(ThreadTable.BUDDY_ID,
	// "threads.buddy_id AS buddy_id");
	// sThreadsProjectionMap.put(ThreadTable.MSG_BODY,
	// "threads.msg_body AS msg_body");
	// sThreadsProjectionMap.put(ThreadTable.MSG_ID,
	// "threads.msg_id AS msg_id");
	// sThreadsProjectionMap.put(ThreadTable.MSG_IS_INBOUND,
	// "threads.msg_is_inbound AS msg_is_inbound");
	// sThreadsProjectionMap.put(ThreadTable.MSG_OUTBOUND_STATUS,
	// "threads.msg_outbound_status AS msg_outbound_status");
	// sThreadsProjectionMap.put(ThreadTable.MSG_RECEIVED_TIME,
	// "threads.msg_received_time AS msg_received_time");
	// sThreadsProjectionMap.put(ThreadTable.MSG_SENT_TIME,
	// "threads.sent_time AS sent_time");
	// sThreadsProjectionMap.put(ThreadTable.MSG_TYPE,
	// "threads.msg_type AS msg_type");
	// sThreadsProjectionMap.put(ThreadTable.UNREAD_COUNT,
	// "threads.unread_count AS unread_count");
	//
	// sMessagesProjectionMap = new HashMap<String, String>();
	// sMessagesProjectionMap.put(MessageTable._ID, "messages._id AS _id");
	// sMessagesProjectionMap.put(MessageTable._COUNT,
	// "messages._count AS _count");
	// sMessagesProjectionMap.put(MessageTable.BODY, "messages.body AS body");
	// sMessagesProjectionMap.put(MessageTable.IS_INBOUND,
	// "messages.is_bound AS is_bound");
	// sMessagesProjectionMap.put(MessageTable.IS_READ,
	// "messages.is_read AS is_read");
	// sMessagesProjectionMap.put(MessageTable.OUTBOUND_STATUS,
	// "messages.outbound_status AS outbound_status");
	// sMessagesProjectionMap.put(MessageTable.RECEIVED_TIME,
	// "messages.received_time AS received_time");
	// sMessagesProjectionMap.put(MessageTable.SENDER_DEVICE_ID,
	// "messages.sender_device_id AS sender_device_id");
	// sMessagesProjectionMap.put(MessageTable.SENDER_ID,
	// "messages.sender_id AS sender_id");
	// sMessagesProjectionMap.put(MessageTable.SENT_TIME,
	// "messages.sent_time AS sent_time");
	// sMessagesProjectionMap.put(MessageTable.TYPE, "messages.type AS type");
	// }

	public WsProvider() {
		this(DATABASE_NAME, DATABASE_VERSION);
		setupImUrlMatchers(Consts.IM_AUTHORITY);
	}

	protected WsProvider(String databaseName, int databaseVersion) {
		this.mDatabaseName = databaseName;
		this.mDatabaseVersion = databaseVersion;
	}

	private void setupImUrlMatchers(String authority) {
		mUrlMatcher.addURI(authority, "threads", MATCH_THREADS);
		mUrlMatcher.addURI(authority, "threads/#", MATCH_THREADS_BY_ID);
		mUrlMatcher.addURI(authority, "threadByBuddyId/#",
				MATCH_THREAD_BY_BUDDY_ID);

		mUrlMatcher.addURI(authority, "messages", MATCH_MESSAGES);
		mUrlMatcher.addURI(authority, "messagesByBuddyId/#",
				MATCH_MESSAGES_BY_BUDDY_ID);
		mUrlMatcher.addURI(authority, "messagesByGroupId/#",
				MATCH_MESSAGES_BY_GROUP_ID);
		mUrlMatcher.addURI(authority, "messages/#", MATCH_MESSAGES_BY_ID);

		mUrlMatcher.addURI(authority, "attachments", MATCH_ATTACHMENTS);
		mUrlMatcher.addURI(authority, "attachments/#", MATCH_ATTACHMENTS_BY_ID);

		mUrlMatcher.addURI(authority, ContactTable.PATH, MATCH_CONTACT);

		mUrlMatcher.addURI(authority, SettingTable.PATH, MATCH_SETTINGS);

		mUrlMatcher.addURI(authority, "records", MATCH_RECORDS);
	}

	@Override
	public String getType(Uri uri) {
		int match = mUrlMatcher.match(uri);
		switch (match) {
		case MATCH_THREADS:
			return ThreadTable.CONTENT_TYPE;
		case MATCH_THREADS_BY_ID:
			return ThreadTable.CONTENT_ITEM_TYPE;
		case MATCH_MESSAGES:
			return MessageTable.CONTENT_TYPE;
		case MATCH_ATTACHMENTS:
			return AttachmentTable.CONTENT_TYPE;
		case MATCH_ATTACHMENTS_BY_ID:
			return AttachmentTable.CONTENT_ITEM_TYPE;
		case MATCH_RECORDS:
			return RecordsTable.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknow URL");
		}
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(final Uri uri, final String[] projection,
			final String selection, final String[] selectionArgs,
			final String sortOrder) {
		return queryInternal(uri, projection, selection, selectionArgs,
				sortOrder);
	}

	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		Uri result;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			result = insertInternal(uri, values);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		if (result != null) {
			getContext().getContentResolver().notifyChange(uri, null, false);
		}
		return result;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (DBG) {
			log("url " + uri.toString());
		}
		int result;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			result = deleteInternal(uri, selection, selectionArgs);
			LogUtils.e("////////////////////////del data, " + "selection=" + selection);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		if (result > 0) {
			getContext().getContentResolver().notifyChange(uri, null, false);
		}

		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int result = 0;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		db.beginTransaction();
		try {
			result = updateInternal(uri, values, selection, selectionArgs);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		return result;
	}

	private int updateInternal(Uri url, ContentValues values, String selection,
			String[] selectionArgs) {
		String tableToChanged = null;
		String changeItemId = null;
		String idColumnName = null;
		//long buddyId = -1;
		String buddyId = "";
		long msgId = -1;

		boolean notifyThreadsContentUri = false;
		boolean notifyMessagesContentUri = false;
		boolean notifyRecordsContentUri = false;

		StringBuilder whereClause = new StringBuilder();
		if (selection != null) {
			whereClause.append(selection);
		}

		int match = mUrlMatcher.match(url);
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		switch (match) {
		case MATCH_THREADS:
			tableToChanged = TABLE_THREADS;
			notifyThreadsContentUri = true;
			break;
		case MATCH_MESSAGES_BY_BUDDY_ID:
			tableToChanged = TABLE_THREADS;
//			try {
//				buddyId = Long.parseLong(url.getPathSegments().get(1));
//			} catch (NumberFormatException e) {
//				throw new IllegalArgumentException();
//			}

			buddyId = url.getPathSegments().get(1);
			appendWhere(whereClause, ThreadTable.BUDDY_ID, "=", buddyId);
			notifyThreadsContentUri = true;
			break;
		case MATCH_MESSAGES:
			tableToChanged = TABLE_MESSAGES;
			notifyMessagesContentUri = true;
			break;
		case MATCH_MESSAGES_BY_ID:
			tableToChanged = TABLE_MESSAGES;
			try {
				msgId = Long.parseLong(url.getPathSegments().get(1));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException();
			}

			appendWhere(whereClause, MessageTable._ID, "=", msgId);
			notifyMessagesContentUri = true;
			break;
		case MATCH_CONTACT:
			tableToChanged = ContactTable.TABLE_NAME;
			break;
		case MATCH_SETTINGS:
			tableToChanged = SettingTable.TABLE_NAME;
			break;
		case MATCH_RECORDS:
			notifyRecordsContentUri =true;
			tableToChanged = TABLE_RECORDS;
			break;
		default:
			throw new IllegalArgumentException("can't update the url" + url);
		}

		if (idColumnName == null) {
			idColumnName = "_id";
		}

		int count = db.update(tableToChanged, values, whereClause.toString(),
				selectionArgs);

		if (count > 0) {
			ContentResolver cr = getContext().getContentResolver();
			if (notifyThreadsContentUri) {
				cr.notifyChange(ThreadTable.CONTENT_URI, null, false);
			}

			if (notifyMessagesContentUri) {
				cr.notifyChange(MessageTable.CONTENT_URI_MESSAGES_BY_BUDDY_ID,
						null, false);
			}
			if (notifyRecordsContentUri) {
				cr.notifyChange(RecordsTable.CONTENT_URI, null);
			}
		}

		return 0;
	}

	public Uri insertInternal(Uri uri, ContentValues values) {
		Uri resultUri = null;
		long rowId = 0;

		Uri notifyMessagesByBuddyIdUri = null;
		boolean notifyMessagesContentUri = false;
		boolean notifyMessagesByBuddyIdContentUri = false;
		boolean notifyThreadsContentUri = false;
		boolean notifyMessagesByGroupIdContentUri = false;
		boolean notifyRecordsContentUri = false;

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int match = mUrlMatcher.match(uri);

		String accountname;
		
		switch (match) {
		case MATCH_MESSAGES_BY_BUDDY_ID:
			appendValuesFromUrl(values, uri, MessageTable.BUDDY_ID);

		case MATCH_MESSAGES:
			notifyMessagesContentUri = true;
			notifyMessagesByBuddyIdContentUri = true;
			notifyMessagesByGroupIdContentUri = true;
			int type = values.getAsInteger(MessageTable.TYPE);
			LogUtils.e("---------------> update message table, msgtype=" + type);
			if (type == MessageType.TYPE_MSG_HOMEWORK
					|| type == MessageType.TYPE_MSG_NOTICE
					|| type == MessageType.TYPE_MSG_GRADE
					|| type == MessageType.TYPE_MSG_JOB
					|| type == MessageType.TYPE_MSG_SAFE
					|| type == MessageType.TYPE_CLASS_SPACE
					|| type == MessageType.TYPE_NEWS
					|| type == MessageType.TYPE_JX) {
				ContentValues cv = createThreadContentValues(values, -1);
				// cv.put(ThreadTable.UNREAD_COUNT, 1);
				// db.insert(TABLE_THREADS, ThreadTable.BUDDY_ID, cv);
				// ContentResolver cr = getContext().getContentResolver();
				// cr.notifyChange(ThreadTable.CONTENT_URI, null);
//				String accountname = values
				accountname = values
						.getAsString(MessageTable.ACCOUNT_NAME);
				//long buddyid = values.getAsLong(MessageTable.BUDDY_ID);
				String buddyid = values.getAsString(MessageTable.BUDDY_ID);
				int chattype = values.getAsInteger(MessageTable.CHAT_TYPE);
				Cursor c = db
						.query(TABLE_THREADS,
								null,
								ThreadTable.ACCOUNT_NAME + "=? and "
										+ ThreadTable.BUDDY_ID + "=? and "
										+ ThreadTable.CHAT_TYPE + "=? and "
										+ ThreadTable.MSG_TYPE + " =?",
								new String[] { accountname,
										//String.valueOf(buddyid),
										buddyid,
										String.valueOf(chattype),
										String.valueOf(type) }, null, null,
								null);
				if (c != null && c.getCount() > 0) {
					c.moveToFirst();
					long id = c.getLong(c.getColumnIndex(ThreadTable._ID));
					int un_read_count = c.getInt(c
							.getColumnIndex(ThreadTable.UNREAD_COUNT));
					db.update(TABLE_THREADS,
							createThreadContentValues(values, ++un_read_count),
							ThreadTable._ID + "=?",
							new String[] { String.valueOf(id) });

				} else {
					db.insert(TABLE_THREADS, null,
							createThreadContentValues(values, 1));
				}
				ContentResolver cr = getContext().getContentResolver();
				cr.notifyChange(ThreadTable.CONTENT_URI, null);
				return null;
			}

			rowId = db.insert(TABLE_MESSAGES, null, values);
			LogUtils.v("=======rowId:" + rowId);
			
			if (rowId > 0) {
				resultUri = Uri.parse(MessageTable.CONTENT_URI + "/" + rowId);
				notifyThreadsContentUri = true;

				//String accountname = values
				accountname = values
						.getAsString(MessageTable.ACCOUNT_NAME);
				//long buddyid = values.getAsLong(MessageTable.BUDDY_ID);
				String buddyid = Consts.APP_ID0 + values.getAsString(MessageTable.BUDDY_ID);
				int chattype = values.getAsInteger(MessageTable.CHAT_TYPE);
				String msgtype = "(" + MessageType.TYPE_MSG_TEXT + ","
						+ MessageType.TYPE_MSG_PIC + ","
						+ MessageType.TYPE_MSG_AUDIO + ")";
				Cursor c = db.query(TABLE_THREADS, null,
						ThreadTable.ACCOUNT_NAME + "=? and "
								+ ThreadTable.BUDDY_ID + "=? and "
								+ ThreadTable.CHAT_TYPE + "=? and "
								+ ThreadTable.MSG_TYPE + " in " + msgtype,
						//new String[] { accountname, String.valueOf(buddyid),
								new String[] { accountname, buddyid,
								String.valueOf(chattype) }, null, null, null);
				
				values.put(ThreadTable.BUDDY_ID, buddyid);
				
				LogUtils.e("---------------> find condition, accountname=" + accountname
						+ ", buddyid=" + buddyid + ", chattype=" + chattype + " type=" + values.getAsInteger(MessageTable.TYPE));
				
				if (c != null && c.getCount() > 0) {
					LogUtils.e("---------------> find old data, update, msgtype=" + values.getAsInteger(MessageTable.TYPE));
					c.moveToFirst();
					long id = c.getLong(c.getColumnIndex(ThreadTable._ID));
					int un_read_count = c.getInt(c
							.getColumnIndex(ThreadTable.UNREAD_COUNT));
					db.update(TABLE_THREADS,
							createThreadContentValues(values, ++un_read_count),
							ThreadTable._ID + "=?",
							new String[] { String.valueOf(id) });

				} else {
					db.insert(TABLE_THREADS, null,
							createThreadContentValues(values, 1));
					LogUtils.e("--------------->insert data, msgtype=" + values.getAsInteger(MessageTable.TYPE)
							+ " buddyid=" + buddyid);
				}
				// db.replace(TABLE_THREADS, ThreadTable.BUDDY_ID,
				// createThreadContentValues(values));
			}
			break;
		case MATCH_CONTACT:
			rowId = db.insert(ContactTable.TABLE_NAME, null, values);
			if (rowId > 0) {
				resultUri = Uri.parse(ContactTable.CONTENT_URI + "/" + rowId);
			}
			break;
		case MATCH_SETTINGS:
			rowId = db.replace(SettingTable.TABLE_NAME, null, values);
			if (rowId > 0) {
				resultUri = Uri.parse(ContactTable.CONTENT_URI + "/" + rowId);
			}
			break;
		case MATCH_RECORDS:
//			String accountname = values.getAsString(RecordsTable.ACCOUNT_NAME);
			accountname = values.getAsString(RecordsTable.ACCOUNT_NAME);
			long records_id = values.getAsLong(RecordsTable.RECORDS_ID);
			Cursor cur = db.query(TABLE_RECORDS, null,
					RecordsTable.ACCOUNT_NAME + "=? and "
							+ RecordsTable.RECORDS_ID + " =?", new String[] {
							accountname, String.valueOf(records_id) }, null,
					null, null);
			notifyRecordsContentUri=true;
			if (cur != null && cur.getCount() > 0) {
				cur.moveToFirst();
				long id = cur.getLong(cur.getColumnIndex(RecordsTable._ID));
				rowId = db.update(TABLE_RECORDS, values, RecordsTable._ID + " =?",
						new String[] { String.valueOf(id) });
			}else{
				rowId = db.insert(TABLE_RECORDS, null, values);
			}
			if (rowId > 0) {
				resultUri = Uri.parse(RecordsTable.CONTENT_URI + "/" + rowId);

			}
			break;
			
		case MATCH_THREADS:
			LogUtils.e("insert or update threads----->");
			notifyThreadsContentUri = true;

			int type1 = values.getAsInteger(ThreadTable.CHAT_TYPE);
			
			//ContentValues cv = createThreadContentValues(values, -1);
			accountname = values.getAsString(MessageTable.ACCOUNT_NAME);
			//long buddyid = values.getAsLong(MessageTable.BUDDY_ID);
			String buddyid = values.getAsString(MessageTable.BUDDY_ID);
			int chattype = values.getAsInteger(MessageTable.CHAT_TYPE);
//			Cursor c = db.query(
//					TABLE_THREADS,
//					null,
//					ThreadTable.ACCOUNT_NAME + "=? and " + ThreadTable.BUDDY_ID
//							+ "=? and " + ThreadTable.CHAT_TYPE + "=? and "
//							+ ThreadTable.MSG_TYPE + " =?",
//					new String[] { accountname, String.valueOf(buddyid),
//							String.valueOf(chattype), String.valueOf(type1) },
//					null, null, null);
			
			Cursor c = db.query(
					TABLE_THREADS,
					null,
					ThreadTable.ACCOUNT_NAME + "=? and " + ThreadTable.BUDDY_ID
							+ "=? and " + ThreadTable.CHAT_TYPE + "=?",
					//new String[] { accountname, String.valueOf(buddyid),
					new String[] { accountname, buddyid,
							String.valueOf(chattype)},
					null, null, null);
			
			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				long id = c.getLong(c.getColumnIndex(ThreadTable._ID));
				int un_read_count = c.getInt(c
						.getColumnIndex(ThreadTable.UNREAD_COUNT));
				LogUtils.e("ws, un_read_count=" + un_read_count);
				
				db.update(TABLE_THREADS,
//						createThreadContentValues(values, ++un_read_count),
						createThreadContentValues(values, -1),
						ThreadTable._ID + "=?",
						new String[] { String.valueOf(id) });
				
				LogUtils.e("update data, id=" + id + ", buddyid=" + buddyid);

			} else {
				db.insert(TABLE_THREADS, null,
//						createThreadContentValues(values, 1));
						createThreadContentValues(values, -1));
				LogUtils.e("insert data, " + "buddyid=" + buddyid);
			}
//			ContentResolver cr = getContext().getContentResolver();
//			cr.notifyChange(ThreadTable.CONTENT_URI, null);
			break;
		default:
			throw new UnsupportedOperationException("Cannot insert into URL: "
					+ uri);
		}

		if (resultUri != null) {
			ContentResolver cr = getContext().getContentResolver();

			if (notifyMessagesContentUri) {
				cr.notifyChange(MessageTable.CONTENT_URI, null);
			}

			if (notifyThreadsContentUri) {
				cr.notifyChange(ThreadTable.CONTENT_URI, null);
			}

			if (notifyMessagesByBuddyIdContentUri) {
				cr.notifyChange(MessageTable.CONTENT_URI_MESSAGES_BY_BUDDY_ID,
						null);
			}

			if (notifyMessagesByGroupIdContentUri) {
				cr.notifyChange(MessageTable.CONTENT_URI_MESSAGES_BY_GROUP_ID,
						null);
			}
			if (notifyRecordsContentUri) {
				cr.notifyChange(RecordsTable.CONTENT_URI, null);
			}
		}

		return resultUri;
	}

	private ContentValues createThreadContentValues(ContentValues values,
			int unread_count) {
		ContentValues cv = new ContentValues();

		int is_inbound = values.getAsInteger(MessageTable.IS_INBOUND);

		cv.put(ThreadTable.ACCOUNT_NAME,
				values.getAsString(MessageTable.ACCOUNT_NAME));
		// ThreadTable ��buddyid �� MessageTable ��buddyid һ��
		//cv.put(ThreadTable.BUDDY_ID, values.getAsLong(MessageTable.BUDDY_ID));
		cv.put(ThreadTable.BUDDY_ID, values.getAsString(MessageTable.BUDDY_ID));
		cv.put(ThreadTable.BUDDY_NAME, values.getAsString(ThreadTable.BUDDY_NAME));
		LogUtils.e("insert buddyid=" + values.getAsString(MessageTable.BUDDY_ID));
		cv.put(ThreadTable.MSG_BODY, values.getAsString(MessageTable.BODY));
		cv.put(ThreadTable.MSG_IS_INBOUND,
				values.getAsInteger(MessageTable.IS_INBOUND));
		cv.put(ThreadTable.MSG_TYPE, values.getAsInteger(MessageTable.TYPE));
		cv.put(ThreadTable.MSG_SENT_TIME,
				values.getAsLong(MessageTable.SENT_TIME));
		cv.put(ThreadTable.MSG_RECEIVED_TIME,
				values.getAsLong(MessageTable.RECEIVED_TIME));
		cv.put(ThreadTable.CHAT_TYPE, values.getAsLong(MessageTable.CHAT_TYPE));
		cv.put(ThreadTable.THREAD_TYPE,
				values.getAsInteger(MessageTable.THREAD_TYPE));

		// Ⱥ����Ϣ�������û�id
		boolean group = values.containsKey(MessageTable.CHAT_TYPE)
				&& values.getAsInteger(MessageTable.CHAT_TYPE) == 1;
		if (group) {
			cv.put(ThreadTable.USER_ID,
					values.getAsLong(MessageTable.SENDER_ID));
		} else {
			cv.put(ThreadTable.USER_ID, 0);
		}
		// δ����
//		if (unread_count >= 0 && is_inbound == MessageType.INCOMING) {
//			cv.put(ThreadTable.UNREAD_COUNT, unread_count);
//		}
		
		if (unread_count >=0 ) {
			if (is_inbound == MessageType.INCOMING) {
				cv.put(ThreadTable.UNREAD_COUNT, unread_count);
				LogUtils.d("chat---->createThreadContentValues, 1 unread_count=" + unread_count);
			} else {
				LogUtils.d("chat---->createThreadContentValues, no unread_count");
			}
			
		} else {
			cv.put(ThreadTable.UNREAD_COUNT, values.getAsInteger(ThreadTable.UNREAD_COUNT));
			LogUtils.d("chat---->createThreadContentValues, 2 unread_count=" + values.getAsInteger(ThreadTable.UNREAD_COUNT));
		}

		return cv;
	}

	public Cursor queryInternal(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		StringBuilder whereClause = new StringBuilder();
		if (selection != null) {
			whereClause.append(selection);
		}

		String groupBy = null;
		String limit = null;

		int match = mUrlMatcher.match(uri);
		if (DBG) {
			log("query url " + uri + ", match " + match + ", where "
					+ selection);
			if (selectionArgs != null) {
				for (String arg : selectionArgs) {
					log(" selectionArg:" + arg);
				}
			}
		}

		switch (match) {
		case MATCH_THREADS_BY_ID:
			appendWhere(whereClause, ThreadTable._ID, "=", uri
					.getPathSegments().get(1));
		case MATCH_THREADS:
			qb.setTables(TABLE_THREADS);
			// sortOrder = ThreadTable.MSG_SENT_TIME + " DESC";
			break;
		case MATCH_MESSAGES_BY_BUDDY_ID:
			appendWhere(whereClause, MessageTable.BUDDY_ID, "=", uri
					.getPathSegments().get(1));
		case MATCH_MESSAGES:
			qb.setTables(TABLE_MESSAGES);
			break;
		case MATCH_MESSAGE:
			qb.setTables(TABLE_MESSAGES);
			appendWhere(whereClause, MessageTable._ID, "=", uri
					.getPathSegments().get(1));
			break;
		case MATCH_CONTACT:
			qb.setTables(ContactTable.TABLE_NAME);
			break;
		case MATCH_SETTINGS:
			qb.setTables(SettingTable.TABLE_NAME);
			break;
		case MATCH_RECORDS:
			qb.setTables(TABLE_RECORDS);
			break;
		default:
			throw new IllegalArgumentException("Unknow URL");
		}

		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = null;

		try {
			c = qb.query(db, projection, whereClause.toString(), selectionArgs,
					groupBy, null, sortOrder, limit);
			if (c != null) {
				c.setNotificationUri(getContext().getContentResolver(), uri);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "query db caugh ", e);
		}

		return c;
	}

	private int deleteInternal(Uri url, String userWhere, String[] whereArgs) {
		String tableToChange;

		String idColumnName = null;
		String changedItemId = null;

		boolean notifyMessagesByBuddyId = false;
		boolean notifyThreadsContentUri = false;
		boolean notifyMessagesByGroupId = false;
		boolean notifyRecordsContentUri = false;

		StringBuilder whereClause = new StringBuilder();
		if (userWhere != null) {
			whereClause.append(userWhere);
		}

		int match = mUrlMatcher.match(url);

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		switch (match) {
		case MATCH_THREADS:
			tableToChange = TABLE_THREADS;
			LogUtils.e("////////////////////////del data, " + "userWhere=" + userWhere + " whereArgs=");
			for (int i = 0; i < whereArgs.length; i++) {
				LogUtils.e("////////////////////////del data, " + "i=" + i + " whereArgs=" + whereArgs[i]);
			}
			break;
		case MATCH_THREADS_BY_ID:
			tableToChange = TABLE_THREADS;
			changedItemId = url.getPathSegments().get(1);
			idColumnName = ThreadTable._ID;
			break;
		case MATCH_THREAD_BY_BUDDY_ID:
			tableToChange = TABLE_THREADS;
			changedItemId = url.getPathSegments().get(1);
			idColumnName = ThreadTable.BUDDY_ID;
			notifyThreadsContentUri = true;
			break;
		case MATCH_MESSAGES_BY_BUDDY_ID:
			tableToChange = TABLE_MESSAGES;
			changedItemId = url.getPathSegments().get(1);
			idColumnName = MessageTable.BUDDY_ID;
			notifyMessagesByBuddyId = true;
			break;
		case MATCH_MESSAGE:
			tableToChange = TABLE_MESSAGES;
			changedItemId = url.getPathSegments().get(1);
			notifyMessagesByBuddyId = true;
			notifyThreadsContentUri = true;
			break;
		case MATCH_MESSAGES:
			tableToChange = TABLE_MESSAGES;
			break;
		case MATCH_ATTACHMENTS:
			tableToChange = TABLE_ATTACHMENTS;
			break;
		case MATCH_CONTACT:
			tableToChange = ContactTable.TABLE_NAME;
			break;
		case MATCH_SETTINGS:
			tableToChange = SettingTable.TABLE_NAME;
			break;
		case MATCH_RECORDS:
			notifyRecordsContentUri=true;
			tableToChange = TABLE_RECORDS;
			break;
		default:
			throw new UnsupportedOperationException("Can't delete the url");
		}

		if (idColumnName == null) {
			idColumnName = "_id";
		}

		if (changedItemId != null) {
			appendWhere(whereClause, idColumnName, "=", changedItemId);
		}

		if (DBG)
			log("delete from " + url + " WHERE  " + whereClause);

		int count = db.delete(tableToChange, whereClause.toString(), whereArgs);

		if (count > 0) {
			ContentResolver cr = getContext().getContentResolver();

			if (notifyMessagesByBuddyId) {
				cr.notifyChange(MessageTable.CONTENT_URI_MESSAGES_BY_BUDDY_ID,
						null);
			}

			if (notifyThreadsContentUri) {
				cr.notifyChange(ThreadTable.CONTENT_URI, null, false);
			}

			if (notifyMessagesByGroupId) {
				cr.notifyChange(MessageTable.CONTENT_URI_MESSAGES_BY_GROUP_ID,
						null, false);
			}
			if (notifyRecordsContentUri) {
				cr.notifyChange(RecordsTable.CONTENT_URI, null);
			}
		}

		return count;
	}

	private void appendValuesFromUrl(ContentValues values, Uri url,
			String... columns) {
		if (url.getPathSegments().size() <= columns.length) {
			throw new IllegalArgumentException("Not enough values in url");
		}
		for (int i = 0; i < columns.length; i++) {
			if (values.containsKey(columns[i])) {
				throw new UnsupportedOperationException(
						"Cannot override the value for " + columns[i]);
			}
			values.put(columns[i],
					decodeURLSegment(url.getPathSegments().get(i + 1)));
		}
	}

	private static String decodeURLSegment(String segment) {
		try {
			return URLDecoder.decode(segment, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// impossible
			return segment;
		}
	}

	private static void appendWhere(StringBuilder where, String columnName,
			String condition, Object value) {
		if (where.length() > 0) {
			where.append(" AND ");
		}
		where.append(columnName).append(condition);
		if (value != null) {
			DatabaseUtils.appendValueToSql(where, value);
		}
	}

	static void log(String message) {
		Log.d(LOG_TAG, message);
	}
}