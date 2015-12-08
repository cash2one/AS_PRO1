package com.linkage.mobile72.sh.fragment;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.TodayTopicActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

public class JzhFragment extends BaseFragment {

	private static WebView webView;
	private ProgressBar progress;
	private static final String TAG = TodayTopicActivity.class.getSimpleName(); 
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
		// 实例化WebView对象
	}
	
	 @Override  
     public void onResume() {  
		 gettopicnew();
         // TODO Auto-generated method stub  
         super.onResume();  
     }  
	 
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtils.e("JzhFragment" + "onCreateView");
		View view = inflater.inflate(R.layout.fragment_jzh, container, false);
		webView = (WebView)view.findViewById(R.id.webview);
		progress = (ProgressBar)view.findViewById(R.id.progress);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		AccountData account = getCurAccount();
		progress.setVisibility(View.GONE);
		webView.getSettings().setJavaScriptEnabled(true);
		/*webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				progress.setProgress(newProgress);
				if (newProgress == 100) {
					progress.setVisibility(View.GONE);
				}
			}
		});*/

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
//		webView.loadUrl("http://www.baidu.com");
//		webView.loadUrl("http://www.baidu.com");

		
		/*back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				webView.goBack();
			}
		});*/
	}
	
	private void gettopicnew(){
		try {
			
			ProgressDialogUtils.showProgressDialog("请求数据中", getActivity(), false);
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "getTopic");
			
			
			
	
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					ProgressDialogUtils.dismissProgressBar();
					try {
					System.out.println("response=" + response);
//					webView.loadUrl("http://www.baidu.com");
					if (response.optInt("ret") == 0) {
						// TODO 登录成功后的帐号更新等

							String url = response.optString("url") ;
							System.out.println("url=" + url);
							webView.loadUrl(url);
						

					}else {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleStatus(response, getActivity());
					}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0,getActivity());
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean onKeyDown() {
		// TODO Auto-generated method stub
		if(webView.canGoBack()){
			webView.goBack();
			return true;
		}
		return false;
	}
}
