package com.linkage.mobile72.sh.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;

public class AdvActivity extends BaseActivity implements OnClickListener {

	public static final int REQUEST_CODE = 0x000001;
	public static final String LOADING_PIC_NAME = "loading_";
	public static final String LOADING_PIC_HISTORY_LOCAL = "loading_history_local";
	public static final String LOADING_PIC_HISTORY_SERVER = "loading_history_server";

	private TextView mCountText, mSkipBtn;
	private ImageView mAdvPic;
//	private RelativeLayout mToAdvPage;

	private int countDownNum = 5;
	private Timer mTimer;

	private AccountData mAccount;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				Intent intent = new Intent(AdvActivity.this, MainActivity.class);
				startActivity(intent);
				mTimer.cancel();
				finish();
			}
			mCountText.setText(msg.what + "秒后自动关闭");
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
		setContentView(R.layout.activity_adv);
		mAccount = getCurAccount();
		String loadingTime = mAccount.getLoadingTime();
		mSkipBtn = (TextView) findViewById(R.id.skip_btn);
		mCountText = (TextView) findViewById(R.id.count_text);
		mAdvPic = (ImageView) findViewById(R.id.advPic);
//		mToAdvPage = (RelativeLayout) findViewById(R.id.toAdvPage);


		String historyLocal = mApp.getSp().getString(LOADING_PIC_HISTORY_LOCAL + mAccount.getUserId(), "");
		
		mAdvPic.setImageURI(Uri.parse("file://" + BaseApplication.getInstance().getWorkspaceDownload().getPath() + "/" +
				AdvActivity.LOADING_PIC_NAME + historyLocal + ".jpg"));
		
		countDownNum = TextUtils.isEmpty(loadingTime) ? 6 : Integer
				.valueOf(loadingTime);
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				countDownNum = countDownNum - 1;
				mHandler.sendEmptyMessage(countDownNum);
			}
		}, 0, 1000);
//		mToAdvPage.setOnClickListener(this);
		mAdvPic.setOnClickListener(this);
		mSkipBtn.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE:
			startActivity(new Intent(this, MainActivity.class));
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.skip_btn:
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			mTimer.cancel();
			finish();
			break;
		case R.id.advPic:
			intent = new Intent(this, WebViewActivity.class);
			intent.putExtra(WebViewActivity.KEY_URL, mAccount.getLoadingUrl());
			intent.putExtra(WebViewActivity.KEY_TITLE, "广告");
			mTimer.cancel();
			startActivityForResult(intent, REQUEST_CODE);
			break;
//		case R.id.toAdvPage:
//			intent = new Intent(this, WebViewActivity.class);
//			// intent.putExtra(WebViewActivity.KEY_URL,
//			// mAccount.getLoadingUrl());
//			intent.putExtra(WebViewActivity.KEY_URL, mAccount.getLoadingUrl());
//			LogUtils.v(mAccount.getLoadingUrl());
//			intent.putExtra(WebViewActivity.KEY_TITLE, "广告");
//			// intent.putExtra(WebViewActivity.KEY_TOKEN, BaseApplication
//			// .getInstance().getAccessToken());
//			mTimer.cancel();
//			startActivityForResult(intent, REQUEST_CODE);
		default:
			break;
		}

	}

}
