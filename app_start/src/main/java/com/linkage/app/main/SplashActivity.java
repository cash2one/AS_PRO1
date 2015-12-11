package com.linkage.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import com.linkage.app.BaseActivity;
import com.linkage.app.R;
import com.linkage.app.data.AccountData;
import com.linkage.app.utils.APKUtils;
import com.morgoo.droidplugin.pm.PluginManager;

public class SplashActivity extends BaseActivity /*implements VYCallBack*/ {

	private static final String TAG = SplashActivity.class.getName();

	public static SplashActivity instance;
	private AccountData account;
	private View view;
//	private MyCommonDialog dialog;
	private AlphaAnimation anim;

	private String username;
	private String password;
	private AccountData user;


	/**
	 * 跳转页面的handler what == 0 为广告 what == 1 为主页面
	 */
	private Handler startActivityHandle = new Handler(){

		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {

				case 1:
					APKUtils utils = new APKUtils();
					try {
						if (PluginManager.getInstance().isConnected()) {
							PluginManager.getInstance().deletePackage("com.linkage.mobile72.sh", 0);
						}
						utils.installAPK(SplashActivity.this,"file:///android_asset/educloud_android_sh-release.apk","com.linkage.mobile72.sh",true);
					}catch (Exception e) {
						e.printStackTrace();
					}
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
//		account = getLastLoginAccount();
		anim = new AlphaAnimation(0.5f, 1.0f);
		anim.setDuration(1000);
		view.setAnimation(anim);
		anim.startNow();
		startActivityHandle.sendEmptyMessageDelayed(1, 1000);
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}

}
