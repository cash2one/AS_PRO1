package com.linkage.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.app.BaseActivity;
import com.linkage.app.BaseApplication;
import com.linkage.app.Consts;
import com.linkage.app.R;
import com.linkage.app.data.ApkInfo;
import com.linkage.app.utils.APKUtils;
import com.linkage.app.utils.Utilities;
import com.linkage.lib.util.StatusUtils;
import com.linkage.lib.util.WDJsonObjectRequest;
import com.morgoo.droidplugin.pm.PluginManager;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SplashActivity extends BaseActivity {

	private static final String TAG = SplashActivity.class.getName();

	public static SplashActivity instance;
	private View view;
	private AlphaAnimation anim;

	private TextView progressText, progressDesc;
	private ProgressBar progressBar;

	private ApkInfo mainApk;
	private List<ApkInfo> pluginApks;

	/**
	 * 跳转页面的handler what == 0 为广告 what == 1 为主页面
	 */
	private Handler startActivityHandle = new Handler(){

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 100:
					checkSDcard();
					break;
				case 200:
					fetchApksInfo();
					break;
				case 1:
					APKUtils utils = new APKUtils();
					/*try {
						if (PluginManager.getInstance().isConnected()) {
							PluginManager.getInstance().deletePackage("com.linkage.mobile72.sh", 0);
						}
						utils.installAPK(SplashActivity.this,"file:///android_asset/educloud_android_sh-release.apk","com.linkage.mobile72.sh",true);
					}catch (Exception e) {
						e.printStackTrace();
					}*/
					gotoLogin();
			}

		}
	};

	private void gotoLogin() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
		view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);
		instance = this;
		PluginManager.getInstance().init(this);
		anim = new AlphaAnimation(0.5f, 1.0f);
		anim.setDuration(1000);
		view.setAnimation(anim);
		anim.startNow();
		startActivityHandle.sendEmptyMessage(100);
		startActivityHandle.sendEmptyMessage(200);
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}

	private void checkSDcard(){
		if(Utilities.hasSDCardMounted()) {//存在SDCard
			File sdcardApkPath = getExternalFilesDir(Consts.APK_FILE_DIR);
			if(!sdcardApkPath.exists()) {
				sdcardApkPath.mkdirs();
			}
			File sdcardApkFile = new File(sdcardApkPath, Consts.APK_FILE_NAME);
			if(!sdcardApkFile.exists()) {
				File assetApkFile = new File("file:///android_asset/" + Consts.APK_FILE_NAME);
				try {
					org.apache.commons.io.FileUtils.copyFile(assetApkFile, sdcardApkFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}else {

		}
	}

	private void fetchApksInfo(){
		HashMap<String, String> params = new HashMap<String, String>();
		final String info = "JS" + "," + "1000" + "," + "a01";
		//params.put("commandtype", "fetchApksInfo");
		params.put("commandtype", "uploadversion");
		params.put("clientinfo", info);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_URL, Request.Method.POST, params, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						//String resp = "{ret:"1",msg:"",data:{main:{package:"com.linkage.main",v:1000,url:""},plugin:[{package:"com.linkage.p1",v:1000,url:""},{package:"com.linkage.p2",v:1000,url:""}]}";
						mainApk = new ApkInfo();
						mainApk.packageName = "";
						mainApk.versionCode = 1000;
						mainApk.url = "";
						pluginApks = new ArrayList<ApkInfo>();
						ApkInfo apk1 = new ApkInfo();
						apk1.packageName = "";
						apk1.versionCode = 1000;
						apk1.url = "";
						ApkInfo apk2 = new ApkInfo();
						apk2.packageName = "";
						apk2.versionCode = 1000;
						apk2.url = "";
						pluginApks.add(apk1);
						pluginApks.add(apk2);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						//LogUtils.v("登录失败=================");
						StatusUtils.handleError(arg0, instance);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
}
