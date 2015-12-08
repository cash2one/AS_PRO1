package com.linkage.mobile72.sh.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static boolean isEmpty(String s) {
		if(s == null)
			return true;
		if("".equals(s.trim()))
			return true;
		return false;
	}
	
	/**
	 * �?�??????��?��??
	 * 
	 * @param mobiles
	 * @return [0-9]{5,9}
	 */
	public static boolean isMobileNO(String mobiles) {
		boolean flag = false;
		try {
//			Pattern p = Pattern
//					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Pattern p = Pattern.compile("^0?1\\d{10}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	public static String format(String date, String format, String exportFormat) {
		if(date == null) return "";
		Date d = null;
		SimpleDateFormat sdf =  new SimpleDateFormat(format);
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		if(d == null) {
			return "";
		}
		SimpleDateFormat exportSdf =  new SimpleDateFormat(exportFormat);
		return exportSdf.format(d);
	}
	
	public static boolean isDate(String date, String format) {
		if(date == null)return false;
		Date d = null;
		SimpleDateFormat sdf =  new SimpleDateFormat(format);
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		if(d == null) {
			return false;
		}
		return true;
	}
	
	public static String doubleTrans(double d) {
		if (Math.round(d) - d == 0) {
			return String.valueOf((long) d);
		}
		return String.valueOf(d);
	}
}
