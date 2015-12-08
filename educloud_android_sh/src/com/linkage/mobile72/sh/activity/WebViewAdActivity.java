package com.linkage.mobile72.sh.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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
import com.linkage.mobile72.sh.utils.C2;
import com.linkage.mobile72.sh.utils.Des3;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.Utilities;
import com.linkage.lib.util.LogUtils;

public class WebViewAdActivity extends BaseActivity implements OnClickListener {
	public static final String KEY_TITLE = "title";
	public static final String KEY_URL = "url";
	public static final String KEY_TOKEN = "token";
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		String title = getIntent().getStringExtra(KEY_TITLE);
		String url = getIntent().getStringExtra(KEY_URL);
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
		String extend = getExtend();
		Map<String,String> params = new HashMap<String,String>();
		params.put("extend", extend);
		params.put("origin", "activitys");
		String sig = C2.getSig(params);
		LogUtils.e("url:" + url);
		LogUtils.e("extend:" + extend);
		LogUtils.e("origin:" + "activitys");
		LogUtils.e("sig:" + sig);
		String postData = "";
		try {
			postData = "extend=" + URLEncoder.encode(extend, "UTF-8") + "&origin=activitys&sig=" + URLEncoder.encode(sig, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mWebView.postUrl(url, EncodingUtils.getBytes(postData, "base64"));
	}

	private String getExtend() {
		StringBuffer sb = new StringBuffer();
		sb.append(getCurAccount().getUserId());
		//sb.append("600003184");
		sb.append(",");
		sb.append(getCurAccount().getUserType());
		sb.append(",");
		sb.append(Utilities.formatNow(null));
		sb.append(",");
		sb.append(Utilities.randomLong());
		String extend = null;
		try {
			extend = Des3.adExtendEncode(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return extend;
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