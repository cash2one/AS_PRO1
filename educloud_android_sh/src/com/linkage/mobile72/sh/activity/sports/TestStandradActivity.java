package com.linkage.mobile72.sh.activity.sports;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;

public class TestStandradActivity extends BaseActivity implements OnClickListener{
	
	private WebView mDetailView;
	private String urlString = "http://221.130.6.212:2580/classSpace/login/beautiful_teacher.html ";

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_standard);
		
		setTitle(R.string.sports_title);
		findViewById(R.id.back).setOnClickListener(this);
		
		mDetailView = (WebView) findViewById(R.id.detail_webview);
		mDetailView.getSettings().setJavaScriptEnabled(true);
		
		mDetailView.loadUrl(urlString);
		mDetailView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}
}
