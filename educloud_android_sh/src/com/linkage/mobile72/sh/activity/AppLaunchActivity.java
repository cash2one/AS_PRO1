package com.linkage.mobile72.sh.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.AppBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.Consts;

public class AppLaunchActivity extends BaseActivity implements OnClickListener {
	private final static String TAG = "AppLaunchActivity";

	private AppBean app;
	private ImageView appImage;
	private TextView appNameText, info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_launch);

		app = (AppBean) getIntent().getSerializableExtra("APP");
		if (app == null) {
			finish();
		}
		setTitle(R.string.title_detail);
		findViewById(R.id.back).setOnClickListener(this);

		appImage = (ImageView) findViewById(R.id.app_icon);
		appNameText = (TextView) findViewById(R.id.app_name);
		info = (TextView) findViewById(R.id.info);

		findViewById(R.id.grantLogin).setOnClickListener(this);
		imageLoader.displayImage(app.getAppLogo(), appImage, defaultOptionsPhoto);
		appNameText.setText(app.getAppName());
		info.setText("授权登录" + app.getAppName());
	}

	private void grantLogin(long appId) {
		ProgressDialogUtils.showProgressDialog("正在授权", this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "appSimpleGrant");
		params.put("appId", String.valueOf(appId));

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
							JSONObject json = response.optJSONObject("data");
							app.setAppToken(json.optString("appToken"));
							Toast.makeText(AppLaunchActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
							if(app.getAppType() == 1) {
								Map<String, Object> params = new HashMap<String, Object>();
								params.put("appToken", app.getAppToken());
								if(Utils.checkApkExist(AppLaunchActivity.this, app.getAppLauncherPath())) {
									Utils.runAppByParam(AppLaunchActivity.this, app.getAppLauncherPath(), app.getAppLauncherUrl(), params);
								}
							}else {
								Intent mIntent = new Intent(AppLaunchActivity.this, WebViewActivity.class);
								mIntent.putExtra(WebViewActivity.KEY_URL, app.getAppLauncherUrl());
								mIntent.putExtra(WebViewActivity.KEY_TITLE, app.getAppName());
								mIntent.putExtra(WebViewActivity.KEY_TOKEN, app.getAppToken());
							}
							finish();
						} else {
							T.showShort(AppLaunchActivity.this, response.optString("msg"));
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, AppLaunchActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.grantLogin:
			grantLogin(app.getAppId());
			break;
		}
	}
}