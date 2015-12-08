package com.linkage.mobile72.sh.im;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.linkage.mobile72.sh.im.provider.Ws.SettingTable;

public class ChatNotifyUtils {
	
	public static void setDefaultSettings(Context context, String name) {
		ContentValues cv = new ContentValues();
		cv.put(SettingTable.MESSAGE_NOTIFY, 1);
		cv.put(SettingTable.MESSAGE_VIBRATE, 1);
		cv.put(SettingTable.MESSAGE_VOICE, 1);
		cv.put(SettingTable.STATUS_NOTIFY, 1);
		cv.put(SettingTable.STATUS_VIBRATE, 1);
		cv.put(SettingTable.STATUS_VOICE, 1);
		cv.put(SettingTable.ACCOUNT_NAME, name);
		context.getContentResolver().insert(SettingTable.CONTENT_URI, cv);
	}
	
	public static boolean messageNotifyEnable(Context context, String name) {
		Cursor cursor = context.getContentResolver().query(SettingTable.CONTENT_URI, 
				new String[] {SettingTable.MESSAGE_NOTIFY}, 
				SettingTable.ACCOUNT_NAME + "=?", 
				new String[] {name}, 
				null);
		cursor.moveToFirst();
		int flag = cursor.getInt(0);
		cursor.close();
		return flag == 1;
	}
	
	public static boolean messageVoiceEnable(Context context, String name) {
		Cursor cursor = context.getContentResolver().query(SettingTable.CONTENT_URI, 
				new String[] {SettingTable.MESSAGE_VOICE}, 
				SettingTable.ACCOUNT_NAME + "=?", 
				new String[] {name}, 
				null);
		cursor.moveToFirst();
		int flag = cursor.getInt(0);
		cursor.close();
		return flag == 1;
	}
	
	public static boolean messageVibrateEnable(Context context, String name) {
		Cursor cursor = context.getContentResolver().query(SettingTable.CONTENT_URI, 
				new String[] {SettingTable.MESSAGE_VIBRATE}, 
				SettingTable.ACCOUNT_NAME + "=?", 
				new String[] {name}, 
				null);
		cursor.moveToFirst();
		int flag = cursor.getInt(0);
		cursor.close();
		return flag == 1;
	}
	
	public static boolean statusNotifyEnable(Context context, String name) {
		Cursor cursor = context.getContentResolver().query(SettingTable.CONTENT_URI, 
				new String[] {SettingTable.STATUS_NOTIFY}, 
				SettingTable.ACCOUNT_NAME + "=?", 
				new String[] {name}, 
				null);
		cursor.moveToFirst();
		int flag = cursor.getInt(0);
		cursor.close();
		return flag == 1;
	}
	
	public static boolean statusVoiceEnable(Context context, String name) {
		Cursor cursor = context.getContentResolver().query(SettingTable.CONTENT_URI, 
				new String[] {SettingTable.STATUS_VOICE}, 
				SettingTable.ACCOUNT_NAME + "=?", 
				new String[] {name}, 
				null);
		cursor.moveToFirst();
		int flag = cursor.getInt(0);
		cursor.close();
		return flag == 1;
	}
	
	public static boolean statusVibrateEnable(Context context, String name) {
		Cursor cursor = context.getContentResolver().query(SettingTable.CONTENT_URI, 
				new String[] {SettingTable.STATUS_VIBRATE}, 
				SettingTable.ACCOUNT_NAME + "=?", 
				new String[] {name}, 
				null);
		cursor.moveToFirst();
		int flag = cursor.getInt(0);
		cursor.close();
		return flag == 1;
	}
}
