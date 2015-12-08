package com.linkage.mobile72.sh.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.fragment.JzhFragment;

public class JFRuleActivity extends BaseActivity implements OnClickListener {
	private static WebView webView;
	private ProgressBar progress;
	private Button back;
	public static JzhFragment create(int titleRes) {
		JzhFragment f = new JzhFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        //args.putInt("titleRes", titleRes);
        f.setArguments(args);
        return f; 
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		View view = findViewById(R.layout.fragment_jzh, container, false);
		setContentView(R.layout.activity_jfgz);
		webView = (WebView)findViewById(R.id.webview);
		progress = (ProgressBar)findViewById(R.id.progress);
//		AccountData account =  BaseApplication.getInstance().getDefaultAccount();
		progress.setVisibility(View.GONE);
		webView.getSettings().setJavaScriptEnabled(true);
		setTitle(R.string.jf_jfgz);
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(this);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//progress.setVisibility(View.VISIBLE);
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				/*if(webView.canGoBack()) {
					back.setVisibility(View.VISIBLE);
				}else {
					back.setVisibility(View.GONE);
				}*/
			}
			
		});

		webView.loadUrl("http://file.jiangsuedu.net/activity/jfgz/jfgz.html");
		// 实例化WebView对象
	}
	
//	@SuppressLint("SetJavaScriptEnabled")
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		LogUtils.e("JzhFragment", "onCreateView");
//		View view = inflater.inflate(R.layout.fragment_jzh, container, false);
//		webView = (WebView)view.findViewById(R.id.webview);
//		progress = (ProgressBar)view.findViewById(R.id.progress);
//		return view;
//	}
	
	

//		webView.setWebViewClient(new WebViewClient() {
//			@Override
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				//progress.setVisibility(View.VISIBLE);
//				return super.shouldOverrideUrlLoading(view, url);
//			}
//
//			@Override
//			public void onPageFinished(WebView view, String url) {
//				// TODO Auto-generated method stub
//				super.onPageFinished(view, url);
//				/*if(webView.canGoBack()) {
//					back.setVisibility(View.VISIBLE);
//				}else {
//					back.setVisibility(View.GONE);
//				}*/
//			}
//			
//		});

//		webView.loadUrl("http://www.ixxt.net/push/index");

		
		/*back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				webView.goBack();
			}
		});*/
//	}
	
	public static boolean onKeyDown() {
		// TODO Auto-generated method stub
		if(webView.canGoBack()){
			webView.goBack();
			return true;
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
		
	}
}
