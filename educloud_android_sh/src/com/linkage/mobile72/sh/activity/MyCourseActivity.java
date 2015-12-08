package com.linkage.mobile72.sh.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;

public class MyCourseActivity extends BaseActivity {

	WebView webView;
	   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_webview);
        this.webView=(WebView) this.findViewById(R.id.webview);
        this.webView.getSettings().setSupportZoom(false);
        this.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.webView.loadUrl("http://121.40.83.136/artchina/public/download.html");
        this.webView.setWebViewClient(new WebViewClientDemo());
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                            String contentDisposition, String mimetype,
                            long contentLength) {
                    //实现下载的代码
                                          Uri uri = Uri.parse(url);
           Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
            }
    });

    }
  private class WebViewClientDemo extends WebViewClient {
    @Override
    // 在WebView中而不是默认浏览器中显示页面
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
     view.loadUrl(url);
      return true;
     }
     
    }
}
