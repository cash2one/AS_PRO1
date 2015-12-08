package com.linkage.mobile72.sh.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.utils.C2;
import com.linkage.mobile72.sh.utils.Des3;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.Utilities;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

/**
 * 我的优豆 优豆规则 我的消费 帮助中心 用户权益Web页面
 */
@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class NewWebViewActivity extends BaseActivity implements OnClickListener {

	public static final String KEY_TITLE = "title";
	public static final String KEY_URL = "url";
	public static final String KEY_TOKEN = "token";

	public static final String MY_YOUDOU = "我的优豆";
	public static final String MY_EXPENSE = "我的消费";
	public static final String HELP_CENTER = "帮助中心";
	public static final String YOUDOU_RULE = "优豆规则";
	public static final String SET_YHQY = "用户权益";
	private WebView mWebView;
	private TextView youdouRule;
	private String token;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_webview);
		
		String title = getIntent().getStringExtra(KEY_TITLE);
		String url = getIntent().getStringExtra(KEY_URL);
		token = getIntent().getStringExtra(KEY_TOKEN);
		title = TextUtils.isEmpty(title) ? "详情" : title;
		setTitle(title);
		youdouRule = (TextView) findViewById(R.id.tvSet);
		if (!TextUtils.isEmpty(title) && title.equals(MY_YOUDOU)) {
			youdouRule.setVisibility(View.VISIBLE);
		}
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.close).setOnClickListener(this);
		youdouRule.setOnClickListener(this);
		if (!url.startsWith("http")) {
			T.showShort(this, "地址格式不对");
			return;
		}
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.setWebViewClient(new WebViewClient() {});
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
//		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		if(StringUtils.isEmpty(token)){
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
		} else {
			LogUtils.e(url + "?token=" + token);
			mWebView.loadUrl(url + "?token=" + token);
		}
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
		case R.id.close:
			finish();
			break;
		case R.id.back:
//			if (mWebView != null && mWebView.canGoBack()) {
//				mWebView.goBack();// 返回上一页面
//			}else {
				finish();
//			}
			break;
		case R.id.tvSet:
			Intent intent = new Intent(this, NewWebViewActivity.class);
			intent.putExtra(NewWebViewActivity.KEY_TITLE, YOUDOU_RULE);
			intent.putExtra(NewWebViewActivity.KEY_URL, Consts.YOUDOU_RULE);
			// intent.putExtra(NewWebViewActivity.KEY_TOKEN, "");
			startActivity(intent);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode,event);
	}

}
