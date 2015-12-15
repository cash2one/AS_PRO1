package com.linkage.app.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import com.linkage.app.data.PluginInfo;
import com.linkage.app.utils.APKUtils;
import com.linkage.app.utils.FileHelper;
import com.linkage.app.utils.Utilities;
import com.linkage.lib.util.LogUtils;
import com.linkage.lib.util.StatusUtils;
import com.linkage.lib.util.WDJsonObjectRequest;
import com.morgoo.droidplugin.pm.PluginManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SplashActivity extends BaseActivity {

	private static final String TAG = SplashActivity.class.getName();

	public static SplashActivity instance;
	private static final int GO_COPY_ASSET_SDCARD = 100;
	private static final int GO_FETCH_APKS_INFO = 200;
	private static final int GO_CHECK_LOCAL_APK = 300;
	private static final int GO_DOWNLOAD_NEW_APK = 400;
	private static final int GO_TO_LOGIN = 900;

	private View view;
	private AlphaAnimation anim;

	private Handler handler = new Handler();
	private Runnable runnable;
	private TextView progressText, progressDesc;
	private ProgressBar progressBar;

	private File sdcardApkPath;
	private PluginInfo mainApk;
	private List<PluginInfo> pluginApks;
	private int i;
	/**
	 * 跳转页面的handler what == 0 为广告 what == 1 为主页面
	 */
	private Handler startActivityHandle = new Handler(){

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case GO_COPY_ASSET_SDCARD:
					checkSDcard();
					break;
				case GO_FETCH_APKS_INFO:
					fetchApksInfo();
					break;
				case GO_CHECK_LOCAL_APK:
					checkApk();
					break;
				case GO_DOWNLOAD_NEW_APK:
					ArrayList<PluginInfo> needDownloadPlugins = (ArrayList<PluginInfo>)msg.obj;
					download(needDownloadPlugins);
					break;
				case GO_TO_LOGIN:
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
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		progressText = (TextView)findViewById(R.id.progressText);
		progressDesc = (TextView)findViewById(R.id.progressDesc);
		anim = new AlphaAnimation(0.5f, 1.0f);
		anim.setDuration(1000);
		view.setAnimation(anim);
		anim.startNow();
		sdcardApkPath = new File(Environment.getExternalStorageDirectory(), Consts.APK_FILE_DIR + Consts.appId + "/apk/");
		startActivityHandle.sendEmptyMessage(GO_COPY_ASSET_SDCARD);
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}

	private void showProgress(String progsText, String progsDesc) {
		progressText.setText(progsText);
		progressDesc.setText(progsDesc);
		runnable = new Runnable() {
			int i = 0;// 用来更新bar
			@Override
			public void run() {
				if(i == 100) {
					i = 0;
				}else {
					i = i + 2;
				}
				progressBar.setProgress(i);
				handler.postDelayed(runnable, 50);
			}
		};
		handler.post(runnable);
	}

	private void checkSDcard(){
		showProgress("正在检查环境","正在复制文件");
		if(Utilities.hasSDCardMounted()) {//存在SDCard
			LogUtils.e("---------sdcardApkPath:" + sdcardApkPath.exists());
			if(!sdcardApkPath.exists()) {
				sdcardApkPath.mkdirs();
			}
			LogUtils.e("---------sdcardApkPath:" + sdcardApkPath.getAbsolutePath());
			File sdcardApkFile = new File(sdcardApkPath, Consts.APK_FILE_NAME);
			LogUtils.e("---------sdcardApkFile:" + sdcardApkFile.exists());
			if(!sdcardApkFile.exists()) {
				InputStream is = null;
				try {
					is = getResources().getAssets().open(Consts.APK_FILE_NAME);
				} catch (IOException e) {
					e.printStackTrace();
				}
				FileHelper.inputStreamToFile(is, sdcardApkFile);
			}
			LogUtils.e("---------sdcardApkFile:" + sdcardApkFile.getAbsolutePath());
		}else {

		}
		startActivityHandle.sendEmptyMessage(GO_FETCH_APKS_INFO);
	}

	private void fetchApksInfo(){
		showProgress("正在检查环境","正在获取版本");
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
						mainApk = new PluginInfo();
						mainApk.appId = "1001";
						mainApk.packageName = "";
						mainApk.versionCode = 1;
						mainApk.url = "http://aservice.139jy.cn/webshare/static/ucenter/client/educloud_android_4.4.1_1201_3.encrypted_signed_Aligned.apk";
						pluginApks = new ArrayList<PluginInfo>();
						PluginInfo apk1 = new PluginInfo();
						apk1.appId = "1002";
						apk1.packageName = "";
						apk1.versionCode = 2;
						apk1.url = "http://aservice.139jy.cn/webshare/static/ucenter/client/educloud_android_parent_4.4.1_1201_3.encrypted_signed_Aligned.apk";
						/*PluginInfo apk2 = new PluginInfo();
						apk2.appId = "10003";
						apk2.packageName = "";
						apk2.versionCode = 1000;
						apk2.url = "";*/
						pluginApks.add(mainApk);
						pluginApks.add(apk1);
						//pluginApks.add(apk2);
						startActivityHandle.sendEmptyMessage(GO_CHECK_LOCAL_APK);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, instance);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	private void checkApk() {
		showProgress("正在检查环境","正在比对版本");
		File[] files = sdcardApkPath.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String filename = pathname.getName().toLowerCase();
				if(filename.contains(".apk")){
					return true;
				}else{
					return false;
				}
			}
		});
		ArrayList<PluginInfo> needDownloadPlugins = new ArrayList<PluginInfo>();
		for(PluginInfo i:pluginApks) {
			if (files != null && files.length > 0) {
				File f = null;
				for (File file : files) {
					String apkName = file.getName();
					String appId = apkName.substring(0, apkName.indexOf(".apk"));
					if(i.appId.equalsIgnoreCase(appId)) {
						f = file;
						break;
					}
				}
				if (f != null) {
					PackageInfo apkInfo = APKUtils.getApkInfo(this, f.getAbsolutePath());
					if (apkInfo != null && apkInfo.versionCode < i.versionCode) {
						needDownloadPlugins.add(i);
					}
				}else {
					needDownloadPlugins.add(i);
				}
			}else {
				needDownloadPlugins.add(i);
			}
		}
		if(needDownloadPlugins.size() > 0) {
			Message msg = new Message();
			msg.what = GO_DOWNLOAD_NEW_APK;
			msg.obj = needDownloadPlugins;
			startActivityHandle.sendMessage(msg);
		}else {
			startActivityHandle.sendEmptyMessage(GO_TO_LOGIN);
		}
	}

	private void download(final ArrayList<PluginInfo> needDownloadPlugins) {
		i = 0;
		Handler fileHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case FileHelper.MESSAGE_OPEN_DIALOG:
						showProgress("正在检查环境","正在下载文件");
						break;
					case FileHelper.MESSAGE_START:
						break;
					case FileHelper.MESSAGE_PROGRESS:
						break;
					case FileHelper.MESSAGE_STOP:
						if(i == needDownloadPlugins.size()) {
							startActivityHandle.sendEmptyMessage(GO_TO_LOGIN);
						}
						break;
					case FileHelper.MESSAGE_ERROR:
						break;
				}
			}
		};
		final FileHelper fileHelper = new FileHelper(fileHandler);
		for(final PluginInfo downloadPlugin : needDownloadPlugins) {
			i ++;
			new Thread() {
				@Override
				public void run() {
					fileHelper.down_file(downloadPlugin.url, sdcardApkPath.getAbsolutePath(), downloadPlugin.appId + ".apk");
				}
			}.start();
		}
	}
}
