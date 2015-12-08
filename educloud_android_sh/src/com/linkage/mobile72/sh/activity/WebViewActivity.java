package com.linkage.mobile72.sh.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.util.EncodingUtils;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.lib.util.LogUtils;

public class WebViewActivity extends BaseActivity implements OnClickListener {
	public static final String KEY_TITLE = "title";
	public static final String KEY_URL = "url";
	public static final String KEY_TOKEN = "token";
	public static final String KEY_ACCESSTOKEN = "accesstoken";
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		String title = getIntent().getStringExtra(KEY_TITLE);
		String url = getIntent().getStringExtra(KEY_URL);
		String token = getIntent().getStringExtra(KEY_TOKEN);
		String accesstoken = getIntent().getStringExtra(KEY_ACCESSTOKEN);
		title = TextUtils.isEmpty(title) ? "详情" : title;

		setTitle(title);
		findViewById(R.id.back).setOnClickListener(this);

		if (!url.startsWith("http")) {
			T.showShort(this, "地址格式不对");
			return;
		}

		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		// webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		String postData = "";
		try {
			if (!StringUtils.isEmpty(token)) {
				postData = "appToken=" + URLEncoder.encode(token, "UTF-8");
				mWebView.postUrl(url,
						EncodingUtils.getBytes(postData, "base64"));
			} else if(!StringUtils.isEmpty(accesstoken)){
				postData = "accesstoken=" + accesstoken;
				mWebView.loadUrl(url + "?" + postData);
			} else {
				mWebView.loadUrl(url);
			}
		} catch (UnsupportedEncodingException e) {
			LogUtils.e(e.getMessage());
			UIUtilities.showToast(this, "appToken编码错误");
			finish();
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (mWebView!=null&&mWebView.canGoBack()) {
//				mWebView.goBack();// 返回上一页面
//				return true;
//			} else {
				finish();
//			}
		}
		return super.onKeyDown(keyCode, event);
	}
}