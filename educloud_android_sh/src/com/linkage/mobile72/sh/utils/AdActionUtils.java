package com.linkage.mobile72.sh.utils;

import com.linkage.mobile72.sh.activity.WebViewActivity;
import com.linkage.mobile72.sh.activity.WebViewAdActivity;
import com.linkage.mobile72.sh.Consts;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class AdActionUtils {
	public static void open(Context context, String action, String needPost) {
		MobclickAgent.onEvent(context, Consts.CLICK_ADV);
		if (!TextUtils.isEmpty(action) && action.startsWith("http")) {
			if("1".equals(needPost)) {
				Intent mIntent = new Intent(context, WebViewAdActivity.class);
				mIntent.putExtra(WebViewAdActivity.KEY_URL, action);
				context.startActivity(mIntent);
			}else {
				Intent mIntent = new Intent(context, WebViewActivity.class);
				mIntent.putExtra(WebViewActivity.KEY_URL, action);
				context.startActivity(mIntent);
			}
		}
	}
}