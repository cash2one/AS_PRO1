package com.linkage.mobile72.sh.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.lib.util.LogUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.activity.WebViewActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.AppBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.widget.MyCommonDialog;

public class AppsUtils {

	private static final String TAG = "AppsUtils";

	public static void startCollectApp(final Context mContext, final AppBean app) {
		ProgressDialogUtils.showProgressDialog("正在打开应用", mContext);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "companyAppGrant");
		params.put("id", String.valueOf(app.getId()));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_companyAppGrant, Request.Method.POST, params,
				true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						try {
							if (response.optInt("ret") == 0) {
								final String callbackurl = response
										.getString("callbackurl");
								final String accesstoken = response
										.getString("accesstoken");
								final String extend = response
										.getString("extend");

								if (app.getAppType() == 1) {// APP
									if (Utils.checkApkExist(mContext,
											app.getAppLauncherPath())) {
										Map<String, Object> params = new HashMap<String, Object>();
										params.put("accesstoken", accesstoken);
										// params.put("extend", extend);
										LogUtils.e("*****************appToken**"
												+ accesstoken + "");
										if (TextUtils.isEmpty(app
												.getAppLauncherUrl())) {
											Utils.runAppByParam(mContext,
													app.getAppLauncherPath(),
													params);
										} else {
											Utils.runAppByParam(mContext,
													app.getAppLauncherPath(),
													app.getAppLauncherUrl(),
													params);
										}
									} else {
										if (callbackurl != null
												&& !"null".equals(callbackurl)
												&& !TextUtils
														.isEmpty(callbackurl)) {
											mContext.startActivity(new Intent(
													Intent.ACTION_VIEW,
													Uri.parse(callbackurl)));
//											final MyCommonDialog dialog = new MyCommonDialog(
//													mContext, "下载提示",
//													"您可以通过和校园140M的专属流量下载该应用",
//													"取消", "确定");
//											dialog.setOkListener(new OnClickListener() {
//												@Override
//												public void onClick(View v) {
//													if (dialog != null
//															&& dialog
//																	.isShowing()) {
//														dialog.dismiss();
//													}
//													
//												}
//											});
//											dialog.setCancelListener(new OnClickListener() {
//												@Override
//												public void onClick(View v) {
//													if (dialog != null
//															&& dialog
//																	.isShowing()) {
//														dialog.dismiss();
//													}
//												}
//											});
//											dialog.show();
										} else {
											UIUtilities.showToast(mContext,
													"下载地址不正确");
										}
									}
								} else { // H5
									Intent mIntent = new Intent(mContext,
											WebViewActivity.class);
									mIntent.putExtra(WebViewActivity.KEY_URL,
											callbackurl);
									mIntent.putExtra(WebViewActivity.KEY_TITLE,
											app.getAppName());
									mIntent.putExtra(
											WebViewActivity.KEY_ACCESSTOKEN,
											accesstoken);
									mContext.startActivity(mIntent);
								}
							} else {
								UIUtilities.showToast(mContext,
										response.getString("desc"));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, mContext);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	public static void refreshScore(final Context mContext, long id) {
		// ProgressDialogUtils.showProgressDialog("正在获取详情", this, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "refreshScore");
		params.put("causeId", String.valueOf(id));
		params.put("type", String.valueOf(1));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_refreshScore, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
							// T.showShort(mContext, "积分更新成功");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						// ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, mContext);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
}
