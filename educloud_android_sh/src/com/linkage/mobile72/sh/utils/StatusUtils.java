package com.linkage.mobile72.sh.utils;

import org.json.JSONObject;

import android.content.Context;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class StatusUtils {
	public static void handleStatus(JSONObject response, Context context) {
		T.showShort(context, response.optString("msg", "返回状态错误"));
		System.out.println("StatusUtils.handleStatus:\n" + response);

	}

	public static void handleMsg(JSONObject response, Context context) {
		T.showShort(context, response.optString("msg", "返回状态错误"));
		System.out.println("StatusUtils.handleStatus:\n" + response);

	}

	public static void handleError(VolleyError err, Context context) {
		System.out.println("StatusUtils.handleError:\n" + err);
		err.printStackTrace();
		if (err instanceof TimeoutError) {
			T.showShort(context, "请求超时");
			return;
		} else if (err instanceof com.android.volley.NoConnectionError) {
			T.showShort(context, "网络异常");
			return;
		}

		T.showShort(context, "连接服务器错误");
		System.out.println("StatusUtils.handleError:\n" + err);
		err.printStackTrace();
	}

	public static void handleOtherError(String err, Context context) {
		T.showShort(context, err);
		System.out.println("StatusUtils.handleOtherError:\n" + err);
		// err.printStackTrace();
	}

}
