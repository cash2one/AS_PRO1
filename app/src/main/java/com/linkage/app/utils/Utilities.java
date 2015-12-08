package com.linkage.app.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Utilities {

	public static int screenHeight;

	static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	static SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日");
	private static Random sRandom = new Random();

	public static final SimpleDateFormat FORMAT_YYYYMMDDHHMMSS = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.US);

	public static String formatNow(SimpleDateFormat format) {
		if (format == null)
			format = FORMAT_YYYYMMDDHHMMSS;
		return format.format(new Date());
	}

	// public static int randomInt() {
	// return sRandom.nextInt();
	// }

	public static long randomLong() {
		return sRandom.nextLong();
	}

	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Activity.TELEPHONY_SERVICE);
		PackageManager pm = context.getPackageManager();
		int permissionState = pm.checkPermission(
				Manifest.permission.READ_PHONE_STATE, context.getPackageName());
		if (PackageManager.PERMISSION_GRANTED == permissionState) {
			return tm.getDeviceId();
		} else {
			return "";
		}
	}


	public static String sdk() {
		return android.os.Build.VERSION.SDK;
	}

	public static String model() {
		return android.os.Build.MODEL;
	}

	// 获取手机系统版本
	public static String release() {
		return android.os.Build.VERSION.RELEASE;
	}

	/** 没有网络 */
	public static final int NETWORKTYPE_INVALID = 0;
	/** wap网络 */
	public static final int NETWORKTYPE_WAP = 1;
	/** 2G网络 */
	public static final int NETWORKTYPE_2G = 2;
	/** 3G和3G以上网络，或统称为快速网络 */
	public static final int NETWORKTYPE_3G = 3;
	/** wifi网络 */
	public static final int NETWORKTYPE_WIFI = 4;

	private static boolean isFastMobileNetwork(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		switch (telephonyManager.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return false; // ~ 14-64 kbps
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return true; // ~ 400-1000 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return true; // ~ 600-1400 kbps
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return false; // ~ 100 kbps
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return true; // ~ 2-14 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return true; // ~ 700-1700 kbps
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return true; // ~ 1-23 Mbps
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return true; // ~ 400-7000 kbps
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return true; // ~ 1-2 Mbps
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return true; // ~ 5 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return true; // ~ 10-20 Mbps
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return false; // ~25 kbps
		case TelephonyManager.NETWORK_TYPE_LTE:
			return true; // ~ 10+ Mbps
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return false;
		default:
			return false;
		}
	}

	/**
	 * 获取网络状态，wifi,wap,2g,3g.
	 *
	 * @param context
	 *            上下文
	 * @return int 网络状态 {@link #NETWORKTYPE_2G},{@link #NETWORKTYPE_3G}, *
	 *         {@link #NETWORKTYPE_INVALID},{@link #NETWORKTYPE_WAP}*
	 *         <p>
	 *         {@link #NETWORKTYPE_WIFI}
	 */
	public static int getNetWorkType(Context context) {
		int mNetWorkType = 0;
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();
			if (type.equalsIgnoreCase("WIFI")) {
				mNetWorkType = NETWORKTYPE_WIFI;
			} else if (type.equalsIgnoreCase("MOBILE")) {
				String proxyHost = android.net.Proxy.getDefaultHost();
				mNetWorkType = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G
						: NETWORKTYPE_2G)
						: NETWORKTYPE_WAP;
			}
		} else {
			mNetWorkType = NETWORKTYPE_INVALID;
		}
		return mNetWorkType;
	}

	/**
	 * md5加密
	 * 
	 * @param str
	 *            字符串
	 * @return 经过md5处理后的字符串
	 */
	public static String md5(String str) {
		try {
			return getMD5(str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UnsupportedEncoding UTF-8");
		}
	}

	/**
	 * md5加密
	 * 
	 * @param source
	 *            字符串
	 * @return 经过md5处理后的字符串
	 */
	public static String getMD5(byte[] source) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			StringBuffer result = new StringBuffer();
			for (byte b : md5.digest(source)) {
				result.append(Integer.toHexString((b & 0xf0) >>> 4));
				result.append(Integer.toHexString(b & 0x0f));
			}
			return result.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("不支持MD5算法");
		}
	}

	public static String getRelativeDate(Context context, Date oldTime) {
		Date now = new Date();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String todayStr = format.format(now);
		Date today = null;
		try {
			today = format.parse(todayStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 昨天
		if ((today.getTime() - oldTime.getTime()) > 0
				&& (today.getTime() - oldTime.getTime()) <= 86400000) {
			return "昨天";
		} else if ((today.getTime() - oldTime.getTime()) <= 0) { // 至少是今天
			return sdf.format(oldTime);
		} else { // 至少是前天
			return sdf2.format(oldTime);
		}

	}

	public static String getRelativeDate(Date oldTime) {
		Date now = new Date();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String todayStr = format.format(now);
		Date today = null;
		try {
			today = format.parse(todayStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 昨天
		if ((today.getTime() - oldTime.getTime()) > 0
				&& (today.getTime() - oldTime.getTime()) <= 86400000) {
			return "昨天";
		} else if ((today.getTime() - oldTime.getTime()) <= 0) { // 至少是今天
			return sdf.format(oldTime);
		} else { // 至少是前天
			return sdf2.format(oldTime);
		}

	}

}
