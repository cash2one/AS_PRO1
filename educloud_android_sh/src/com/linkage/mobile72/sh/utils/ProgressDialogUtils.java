package com.linkage.mobile72.sh.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtils {

	public static ProgressDialog proDialog;

	public static void showProgressDialog(int resId, Context context) {
		showProgressDialog(context.getString(resId), context);
	}

	public static void showProgressDialog(int resId, Context context, Boolean cancelable) {
		showProgressDialog(context.getString(resId), context, cancelable);
	}

	public static void showProgressDialog(String msg, Context context) {
		showProgressDialog(msg, context, true);
	}

	public static void showProgressDialog(String msg, Context context, Boolean cancelable) {
		proDialog = new ProgressDialog(context);
		proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置�???�为???形�??�????
		proDialog.setMessage(msg);
		proDialog.setIndeterminate(false);// 设置�?�???��?????为�?????�?
		proDialog.setCancelable(cancelable);// 设置�?�???��????????以�??????????????�?
		proDialog.show();
	}

	public static void setDialogMsg(String msg) {
		if (proDialog != null && proDialog.isShowing()) {
			proDialog.setMessage(msg);
		}
	}

	public static void dismissProgressBar() {
		if (proDialog != null && proDialog.isShowing()) {
			proDialog.dismiss();
		}
	}

}
