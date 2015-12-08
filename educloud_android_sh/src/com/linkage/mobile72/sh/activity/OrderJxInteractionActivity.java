package com.linkage.mobile72.sh.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.util.EncodingUtils;

import android.R.integer;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.lib.util.LogUtils;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.Consts;

public class OrderJxInteractionActivity extends WebViewActivity
{
    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_GOTO = "goto";

    private WebView mWebView;
    private int FROM_PAGE = 0;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        String title = getIntent().getStringExtra(KEY_TITLE);
        String url = getIntent().getStringExtra(KEY_URL);
        String token = getIntent().getStringExtra(KEY_TOKEN);
        FROM_PAGE  = getIntent().getIntExtra(KEY_GOTO, 0);
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
        if (StringUtils.isEmpty(token)) {
            mWebView.loadUrl(url);
        } else {
        	mWebView.loadUrl(url + "?token=" + token);
            /*String postData = "";
            try {
                postData = "appToken=" + URLEncoder.encode(token, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LogUtils.e(e.getMessage());
                UIUtilities.showToast(this, "appToken编码错误");
                finish();
            }
            mWebView.postUrl(url, EncodingUtils.getBytes(postData, "base64"));*/
        }
    }
    
    private void gotojxList()
    {
        Intent mIntent = null;
        if(FROM_PAGE > 0 && FROM_PAGE < 20) {
        	mIntent = new Intent(this, JxHomeworkListActivity3.class);
        	mIntent.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE, String.valueOf(FROM_PAGE));
        }else if(FROM_PAGE == 20) {
            mIntent = new Intent(this, ScoreActivity.class);
        }else if(FROM_PAGE == 21) {
        	mIntent = new Intent(this, KaoqinActivity2.class);
        }
        startActivity(mIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.back:
            gotojxList();
            break;
       
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (mWebView != null && mWebView.canGoBack()) {
//                mWebView.goBack();// 返回上一页面
//                return true;
//            } else {
//                finish();
//            }
            gotojxList();
        }
        return super.onKeyDown(keyCode, event);
    }
}
