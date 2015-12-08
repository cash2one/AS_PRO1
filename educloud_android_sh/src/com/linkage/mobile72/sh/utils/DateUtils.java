package com.linkage.mobile72.sh.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseApplication;

public class DateUtils {
	
	public static SimpleDateFormat FORMAT_DEFAULT_ALL = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
	
	/**
	 * format:yyyy年MM月dd日 hh:mm
	 */
	public static SimpleDateFormat FORMAT_DEFAULT = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
	/**
	 * format:yyyy年MM月
	 */
	public static SimpleDateFormat FORMAT_YY_MM = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
	
	public static SimpleDateFormat FORMAT_SEND_SMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat FORMAT_SEND_SMS1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static SimpleDateFormat FORMAT_FEED = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static SimpleDateFormat FORMAT_REQUEST = new SimpleDateFormat("yyyyMM");
	
	public static String nowToString(SimpleDateFormat format) {
		if(format == null) {
			return null;
		}
		return format.format(new Date());
	}
	
	public static String nowToStringYYMM() {
		return nowToString(FORMAT_YY_MM);
	}
	public static long StringToNow(String time){
		try {
			return FORMAT_SEND_SMS.parse(time).getTime();
		} catch (ParseException e) {
			return 0;
		}
	}
	public static long getNowBeforeWeek(){
		Calendar curr = Calendar.getInstance();
		curr.set(Calendar.DAY_OF_MONTH,curr.get(Calendar.DAY_OF_MONTH)-7);
//		Date date=curr.getTime();
		return curr.getTimeInMillis();
		 
	}
	public static String getSmsMonthRequestFormat(String date){
		Date d=new Date();
		try {
			d=FORMAT_REQUEST.parse(date);
		} catch (ParseException e) {
		}
		return FORMAT_REQUEST.format(d);
	}
	public static String getSmsMonthRequestFormat(Calendar c) {
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		month++;
		return String.format("%d%02d", year, month);
	}
	
	public static String getSmsMonthShowFormat(Calendar c) {
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		month++;
		return String.format("%d - %d月", year, month);
	}
	
	public static String getSchoolTimeDateShowFormat(Calendar c) {
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		month++;
		return String.format("%d-%02d-%02d", year, month, day);
	}
	
	public static String getSchoolTimeDateFormat(Calendar c) {
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		month++;
		return String.format("%d%02d%02d", year, month, day);
	}
	
	public static String getSchoolTimeWeekFormat(Context context, Calendar c) {
		int day = c.get(Calendar.DAY_OF_WEEK);
		return context.getResources().getStringArray(R.array.week)[day-1];
	}
	
	public static String getRelativeDate(String str) {
		try {
			Date date = FORMAT_FEED.parse(str);
			return getRelativeDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(str.endsWith(".0")) {
			return str.substring(0, str.length() - 2);
		}
		return str;
	}
	
	public static String getRelativeDate(Date date) {
		Date now = new Date();
		BaseApplication app = BaseApplication.getInstance();
        String sec = app.getString(R.string.created_at_beautify_sec);
        String min = app.getString(R.string.created_at_beautify_min);
        String hour = app.getString(R.string.created_at_beautify_hour);
        String day = app.getString(R.string.created_at_beautify_day);
        String suffix = app.getString(R.string.created_at_beautify_suffix);
        
        // seconds 
        long diff = (now.getTime() - date.getTime()) / 1000;
        
        if(diff < 0) {
        	diff = 0;
        }
        
        if(diff < 60)
        	return diff + sec + suffix;
        
        // minutes
        diff /= 60;
        if(diff < 60)
        	return diff + min + suffix;
        
        // hours
        diff /= 60;
        if(diff < 24)
        	return diff + hour + suffix;
        
        // days
        diff /= 24;
        if(diff < 15)
        	return diff + day + suffix;
        
        return FORMAT_DEFAULT_ALL.format(date);
	}
}
