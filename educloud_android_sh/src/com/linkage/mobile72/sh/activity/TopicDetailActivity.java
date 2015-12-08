package com.linkage.mobile72.sh.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.EncodingUtils;
import org.json.JSONObject;

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
import android.widget.EditText;
import android.widget.TextView;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Topic;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.C2;
import com.linkage.mobile72.sh.utils.Des3;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.Utilities;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

/**
 * 话题、活动相关的webview
 * @author Yao
 */
public class TopicDetailActivity extends BaseActivity implements OnClickListener {
    
    private final static String TAG = "TopicDetailActivity";
	public static final String KEY_TITLE = "title";
	public static final String KEY_URL = "url";
	public static final String KEY_TOKEN = "token";
	public static final String KEY_ID = "id";
	public static final String KEY_RES = "res";
	private WebView mWebView;
	// 新增
	public static final String COMMENT_URL = "comment_url";
	public static final String COMMENT_NUM = "comment_num";
	private String commentUrl;
	private String commentNum;
	private Button mSet;
	private EditText mEditText;
	private TextView sendCmd;
	private String id;
	private TextView shareCmd;
    private Topic curTopicInfo;
    private int shareType;
    
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topicdetail);
		curTopicInfo = (Topic) getIntent().getSerializableExtra(KEY_RES);
		String title = getIntent().getStringExtra(KEY_TITLE);
		String url = getIntent().getStringExtra(KEY_URL);
		String token = getIntent().getStringExtra(KEY_TOKEN);
		id = getIntent().getStringExtra(KEY_ID);
		// ============新增开始==============
		commentUrl = getIntent().getStringExtra(COMMENT_URL);
		commentNum = getIntent().getStringExtra(COMMENT_NUM);
		mSet = (Button) findViewById(R.id.set);
		if (!StringUtils.isEmpty(commentUrl)
				&& !StringUtils.isEmpty(commentNum)) {
			mSet.setVisibility(View.VISIBLE);
			mSet.setOnClickListener(this);
			mSet.setPadding(20, 8, 20, 8);
			mSet.setText("分享");
		}
		// ============新增结束==============
		title = TextUtils.isEmpty(title) ? "详情" : title;

		setTitle(title);
		findViewById(R.id.back).setOnClickListener(this);

		if (!url.startsWith("http")) {
			T.showShort(this, "地址格式不对");
			return;
		}
		
		mEditText = (EditText)findViewById(R.id.end_1);
		sendCmd = (TextView)findViewById(R.id.end_2);
		
		sendCmd.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
            	if(StringUtils.isEmpty(mEditText.getText().toString().trim())) {
                    T.showShort(TopicDetailActivity.this, "评论不可为空");
                    return;
                }
                if(mEditText.getText().toString().trim().length() < 15) {
                    T.showShort(TopicDetailActivity.this, "评论内容不得少于15个字");
                    return;
                }
                sendComment();
            }
        });
		
		
		shareCmd = (TextView)findViewById(R.id.share_btn);
        shareCmd.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
            	Intent mIntent = new Intent(TopicDetailActivity.this, WebViewHtHdActivity.class);
    			mIntent.putExtra(TopicDetailActivity.KEY_URL, commentUrl);
    			mIntent.putExtra(TopicDetailActivity.KEY_TITLE, "评论");
    			mIntent.putExtra(TopicDetailActivity.KEY_TOKEN, BaseApplication.getInstance().getAccessToken());
    			startActivity(mIntent);
            }
        });

		mWebView = (WebView) findViewById(R.id.webView);
		clearWebViewCache(mWebView);
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
		case R.id.set:
			showShareTypeDialog();
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
	
	
	private void sendComment() {
		ProgressDialogUtils.showProgressDialog("", this, true);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("commandtype", "sendTopicComment");
        params.put("id", id);
        params.put("content", mEditText.getText().toString().trim());
        params.put("commentId", String.valueOf(-1));
        
        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_sendTopicComment,
                Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    	ProgressDialogUtils.dismissProgressBar();
                        System.out.println("response=" + response);
                        if (response.optInt("ret") == 0) {
                            commentNum = String.valueOf(response.optInt("commentNum"));
                            //mSet.setText(commentNum + "评论");
                            T.showShort(TopicDetailActivity.this, "评论发送成功");
                            mEditText.setText("");
                            hideKeyboard(mEditText.getWindowToken());
                        } else {
                            StatusUtils.handleStatus(response, TopicDetailActivity.this);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                    	ProgressDialogUtils.dismissProgressBar();
                        StatusUtils.handleError(arg0, TopicDetailActivity.this);
                    }
                });
        BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
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
		String content = curTopicInfo.getContent();
		if(StringUtils.isEmpty(content)) {
			content = "";
		}else if(content.length() > 20){
			content = content.substring(0, 20) + "...";
		}
		oks.setText(content);
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
                Intent intent = new Intent(TopicDetailActivity.this,
                        TopicShareActivity.class);
                intent.putExtra(TopicShareActivity.ID, curTopicInfo.getId());
                intent.putExtra(TopicShareActivity.PICURL, curTopicInfo.getPicUrl());
                intent.putExtra(TopicShareActivity.TITLE, curTopicInfo.getTitle());
                intent.putExtra(TopicShareActivity.DURL, curTopicInfo.getDetailUrl());
                intent.putExtra(TopicShareActivity.TYPE, 1);
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
	
	@Override
    protected void onDestroy()
    {
        BaseApplication.getInstance().cancelPendingRequests(TAG);
        super.onDestroy();
    }
}