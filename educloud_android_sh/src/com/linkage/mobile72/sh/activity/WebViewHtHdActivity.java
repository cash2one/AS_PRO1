package com.linkage.mobile72.sh.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.WonderExercise;
import com.linkage.mobile72.sh.utils.C2;
import com.linkage.mobile72.sh.utils.Des3;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.Utilities;
import com.linkage.lib.util.LogUtils;

/**
 * 话题、活动相关的webview
 * @author Yao
 */
@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class WebViewHtHdActivity extends BaseActivity implements OnClickListener {
	public static final String KEY_TITLE = "title";
	public static final String KEY_URL = "url";
	public static final String KEY_TOKEN = "token";
	public static final String KEY_RES = "res";
	private WebView mWebView;
	// 新增
	public static final String COMMENT_URL = "comment_url";
	public static final String COMMENT_NUM = "comment_num";
	public static final String FROM = "from"; 
	private String commentUrl;
	private String commentNum;
	private Button mSet;
	private String from;
	private WonderExercise curTopicInfo;
	private String postData;
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		curTopicInfo = (WonderExercise) getIntent().getSerializableExtra(KEY_RES);
		
		String title = getIntent().getStringExtra(KEY_TITLE);
		String url = getIntent().getStringExtra(KEY_URL);
		String token = getIntent().getStringExtra(KEY_TOKEN);
		// ============新增开始==============
		//判断是否是从精彩活动过来
		from = getIntent().getStringExtra(FROM);
		if(!StringUtils.isEmpty(from) && from.equals("WonderExerciseActivity"))
		{
		    mSet = (Button) findViewById(R.id.set);
		    mSet.setVisibility(View.VISIBLE);
		    mSet.setText("分享");
		    mSet.setOnClickListener(this);
		}
		else
		{
		    commentUrl = getIntent().getStringExtra(COMMENT_URL);
	        commentNum = getIntent().getStringExtra(COMMENT_NUM);
	        mSet = (Button) findViewById(R.id.set);
	        if (!StringUtils.isEmpty(commentUrl)
	                || !StringUtils.isEmpty(commentNum)) {
	            mSet.setVisibility(View.VISIBLE);
	            mSet.setOnClickListener(this);
	            mSet.setPadding(20, 8, 20, 8);
	        }
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
	            mSet.setBackground(getResources().getDrawable(
	                    R.drawable.comment_num_bg));
	        } else {
	            mSet.setBackgroundDrawable(getResources().getDrawable(
	                    R.drawable.comment_num_bg));
	        }
	        mSet.setTextColor(getResources().getColor(R.color.white));
	        mSet.setText(commentNum + "评论");
        }
		
		
		// ============新增结束==============
		title = TextUtils.isEmpty(title) ? "详情" : title;
		if(title.length() > 6) {
			title = title.substring(0, 6) + "...";
		}
		setTitle(title);
		findViewById(R.id.back).setOnClickListener(this);

		if (!url.startsWith("http")) {
			T.showShort(this, "地址格式不对");
			return;
		}
		mWebView = (WebView) findViewById(R.id.webView);
//		clearWebViewCache(mWebView);
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
//		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		String extend = getExtend();
		Map<String,String> params = new HashMap<String,String>();
		params.put("extend", extend);
		params.put("origin", "activitys");
		String sig = C2.getSig(params);
		LogUtils.e("url:" + url);
		LogUtils.e("extend:" + extend);
		LogUtils.e("origin:" + "activitys");
		LogUtils.e("sig:" + sig);
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
		case R.id.set:
		    if(!StringUtils.isEmpty(from) && from.equals("WonderExerciseActivity"))
		    {
		        showShareTypeDialog();
		    }
		    else
		    {
		        Intent mIntent = new Intent(this, WebViewHtHdActivity.class);
	            mIntent.putExtra(WebViewHtHdActivity.KEY_URL, commentUrl);
	            mIntent.putExtra(WebViewHtHdActivity.KEY_TITLE, "评论");
	            mIntent.putExtra(WebViewHtHdActivity.KEY_TOKEN, BaseApplication.getInstance().getAccessToken());
	            startActivity(mIntent);
            }
			
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView != null && mWebView.canGoBack()) {
				mWebView.goBack();// 返回上一页面
				return true;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private void showShareTypeDialog() {
	        
		final OnekeyShare oks = new OnekeyShare();
		//oks.setAddress("12345678901");
		/*if(!StringUtils.isEmpty(myAddress) && !"null".equals(myAddress)) {
			oks.setText("我在" + myAddress);
		}else {
			oks.setText("");
		}*/
		oks.setTitle(curTopicInfo.getTitle());
		oks.setImageUrl(curTopicInfo.getPicUrl());
		oks.setUrl(curTopicInfo.getDetailUrl());
		oks.setText(curTopicInfo.getTitle());
		oks.setSilent(false);
		//oks.setShareFromQQAuthSupport(shareFromQQLogin);
		oks.setTheme(OnekeyShareTheme.CLASSIC);
		oks.setDialogMode();
		// 令编辑页面显示为Dialog模式

		// 在自动授权时可以禁用SSO方式
		//if(!CustomShareFieldsPage.getBoolean("enableSSO", true))
		oks.disableSSOWhenAuthorize();

		// 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
		//oks.setCallback(new OneKeyShareCallback());

		// 去自定义不同平台的字段内容
		//oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());

		// 去除注释，演示在九宫格设置自定义的图标
		Bitmap enableLogo = BitmapFactory.decodeResource(getResources(), R.drawable.short_sms);
		Bitmap disableLogo = BitmapFactory.decodeResource(getResources(), R.drawable.short_sms);
		String label = "短信";
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				Uri uri = Uri.parse("smsto:");
				Intent ii = new Intent(Intent.ACTION_SENDTO, uri);//绿色文字就是启动发送短信窗口
				ii.putExtra("sms_body", "我在和校园客户端看到一个很好的文章推荐给你：" + curTopicInfo.getDetailUrl());
				startActivity(ii);
			}
		};
		oks.setCustomerLogo(enableLogo, disableLogo, label, listener);
		
		Bitmap enableLogo2 = BitmapFactory.decodeResource(getResources(), R.drawable.bjkj);
		Bitmap disableLogo2 = BitmapFactory.decodeResource(getResources(), R.drawable.bjkj);
		String label2 = "班级+";
		OnClickListener listener2 = new OnClickListener() {
			public void onClick(View v) {
                Intent intent = new Intent(WebViewHtHdActivity.this,
                        TopicShareActivity.class);
                intent.putExtra(TopicShareActivity.ID, curTopicInfo.getId());
                intent.putExtra(TopicShareActivity.PICURL, curTopicInfo.getPicUrl());
                intent.putExtra(TopicShareActivity.TITLE, curTopicInfo.getTitle());
                intent.putExtra(TopicShareActivity.DURL, curTopicInfo.getDetailUrl());
                intent.putExtra(TopicShareActivity.TYPE, 2);
                startActivity(intent);
			}
		};
		oks.setCustomerLogo(enableLogo2, disableLogo2, label2, listener2);

		// 去除注释，则快捷分享九宫格中将隐藏新浪微博和腾讯微博
//			oks.addHiddenPlatform(SinaWeibo.NAME);
//			oks.addHiddenPlatform(TencentWeibo.NAME);

		// 为EditPage设置一个背景的View
		oks.show(this);
        
    }
	
	/**
     * 清除WebView缓存
     */ 
	public void clearWebViewCache(WebView webview) {
		// 清除cookie即可彻底清除缓存
		CookieSyncManager.createInstance(this);
		CookieManager.getInstance().removeAllCookie();
		webview.clearCache(true);
		webview.clearHistory();
		webview.clearFormData();
		try {
	        deleteDatabase("webview.db");
	        deleteDatabase("webviewCache.db");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // WebView缓存文件
	    File appCacheDir = new File(getFilesDir().getAbsolutePath() + "/webcache");

	    File webviewCacheDir = new File(getCacheDir().getAbsolutePath()  + "/webviewCache");

	    // 删除webView缓存目录
	    if (webviewCacheDir.exists()) {
	        deleteFile(webviewCacheDir);
	    }
	    // 删除webView缓存，缓存目录
	    if (appCacheDir.exists()) {
	        deleteFile(appCacheDir);
	    }
	}
    
	public void deleteFile(File file) {
	    if (file.exists()) {
	        if (file.isFile()) {
	            file.delete();
	        } else if (file.isDirectory()) {
	            File files[] = file.listFiles();
	            for (int i = 0; i < files.length; i++) {
	                deleteFile(files[i]);
	            }
	        }
	        file.delete();
	    } else {
	    }
	}
	
}