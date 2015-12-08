package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Topic;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.CommonAdapter;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener2;
import com.linkage.ui.widget.PullToRefreshListView;

public class TodayTopicActivity extends BaseActivity implements OnClickListener {
	// private static WebView webView;
	// private ProgressBar progress;
	// private String url;
	private static final String TAG = TodayTopicActivity.class.getSimpleName();
	private Button back;
	private PullToRefreshListView mListView;

	private List<Topic> topicList;
	// private TopicAdapter topicAdapter;
	private CommonAdapter<Topic> mAdapter;
	private Button mSet;

	private long curPageIndex = 1;
	private final int pageSize = 20;

	// public static JzhFragment create(int titleRes) {
	// JzhFragment f = new JzhFragment();
	// // Supply num input as an argument.
	// Bundle args = new Bundle();
	// //args.putInt("titleRes", titleRes);
	// f.setArguments(args);
	// return f;
	// }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_today_topic);
		setTitle("今日话题");
		back = (Button) findViewById(R.id.back);
		mSet = (Button) findViewById(R.id.set);
		// mSet.setText("我的活动");
		// mSet.setVisibility(View.VISIBLE);
		SharedPreferences sp = BaseApplication.getInstance().getSp();
		Editor ed = sp.edit();
		ed.putInt("Huati", 0);
		ed.commit();
		mListView = (PullToRefreshListView) findViewById(R.id.list);
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				fetchTopicList(1);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				fetchTopicList(++curPageIndex);

			}
		});

		back.setOnClickListener(this);
		// topicAdapter = new TopicAdapter();

		topicList = new ArrayList<Topic>();
//		for (int i = 0; i < 5; i++) {
//			Topic t = new Topic();
//			t.setCommentUrl("http://www.baidu.com");
//			t.setDetailUrl("http://www.baidu.com");
//			t.setCommentNum("52000");
//			t.setReadNum("2340000");
//			topicList.add(t);
//		}
		
		mAdapter = new CommonAdapter<Topic>(this,
                topicList, R.layout.adapter_topic_item) {

            @Override
            public void convert(
                    com.linkage.mobile72.sh.utils.ViewHolder helper,
                    final Topic item) {
                helper.setText(R.id.item_title, item.getTitle());
                helper.setText(R.id.item_date, item.getDate());
                String content = item.getContent();
                if(content != null) {
                    if(content.length() > 60) {
                        content = content.substring(0,60) + "...";
                    }
                }else {
                    content = "";
                }
                helper.setText(R.id.item_content, content);
                helper.setText(
                        R.id.item_commentNum,
                        "评论("
                                + translateNum(Integer.valueOf(item
                                        .getCommentNum())) + ")");
                helper.setText(R.id.item_readNum,
                        "阅读(" + translateNum(Integer.valueOf(item.getReadNum()))
                                + ")");
                ((NetworkImageView) helper.getView(R.id.item_pic))
                        .setDefaultImageResId(R.drawable.default_today_topic_item);
                helper.setImageUrl(R.id.item_pic, item.getPicUrl());
//                helper.getView(R.id.item_layout).setOnClickListener(
//                        new OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                Intent mIntent = new Intent(
//                                        TodayTopicActivity.this,
//                                        WebViewHtHdActivity.class);
//                                mIntent.putExtra(WebViewHtHdActivity.KEY_URL, item.getDetailUrl());
//                                mIntent.putExtra(WebViewHtHdActivity.KEY_TITLE, "正文");
//                                mIntent.putExtra(WebViewHtHdActivity.COMMENT_URL, item.getCommentUrl());
//                                mIntent.putExtra(WebViewHtHdActivity.COMMENT_NUM, translateNum(Integer.valueOf(item.getCommentNum())));
//                                mIntent.putExtra(WebViewHtHdActivity.KEY_TOKEN, BaseApplication.getInstance().getAccessToken());
//                                startActivity(mIntent);
//                            }
//                        });
            }
        };
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                // TODO Auto-generated method stub
                Topic item = topicList.get(position);
                Intent mIntent = new Intent(
                        TodayTopicActivity.this,
                        TopicDetailActivity.class);
                mIntent.putExtra(TopicDetailActivity.KEY_URL, item.getDetailUrl());
                mIntent.putExtra(TopicDetailActivity.KEY_TITLE, "正文");
                mIntent.putExtra(TopicDetailActivity.KEY_ID, item.getId());
                mIntent.putExtra(TopicDetailActivity.COMMENT_URL, item.getCommentUrl());
                mIntent.putExtra(TopicDetailActivity.COMMENT_NUM, translateNum(Integer.valueOf(item.getCommentNum())));
                mIntent.putExtra(TopicDetailActivity.KEY_TOKEN, BaseApplication.getInstance().getAccessToken());
                
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(TopicDetailActivity.KEY_RES, item);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });
		 fetchTopicList(1);
		/*
		 * View view = findViewById(R.layout.fragment_jzh, container, false);
		 * webView = (WebView)findViewById(R.id.webview); progress =
		 * (ProgressBar)findViewById(R.id.progress); // AccountData account =
		 * BaseApplication.getInstance().getDefaultAccount();
		 * progress.setVisibility(View.GONE);
		 * webView.getSettings().setJavaScriptEnabled(true);
		 * setTitle(R.string.jf_jfgz); webView.setWebViewClient(new
		 * WebViewClient() {
		 * 
		 * @Override public boolean shouldOverrideUrlLoading(WebView view,
		 * String url) { //progress.setVisibility(View.VISIBLE); return
		 * super.shouldOverrideUrlLoading(view, url); }
		 * 
		 * @Override public void onPageFinished(WebView view, String url) { //
		 * TODO Auto-generated method stub super.onPageFinished(view, url);
		 * if(webView.canGoBack()) { back.setVisibility(View.VISIBLE); }else {
		 * back.setVisibility(View.GONE); } }
		 * 
		 * }); gettopicnew();
		 * webView.loadUrl("http://www.buqingwen.com/jfgz/jfgz.html");
		 * 实例化WebView对象
		 */
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.set:
			T.showShort(this, "我的活动。。。");
			break;
		}

	}

	/**
	 * 将数字转换为万级，如果大于一万
	 * 
	 * @param num
	 * @return
	 */
	private String translateNum(int num) {
		String result = "";
		if (num > 10000) {
			result = (double) num / 10000 + "万";
		} else {
			result = String.valueOf(num);
		}
		return result;
	}

	/*
	 * @SuppressLint("SetJavaScriptEnabled")
	 * 
	 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
	 * container, Bundle savedInstanceState) { LogUtils.e("JzhFragment",
	 * "onCreateView"); View view = inflater.inflate(R.layout.fragment_jzh,
	 * container, false); webView = (WebView)view.findViewById(R.id.webview);
	 * progress = (ProgressBar)view.findViewById(R.id.progress); return view; }
	 * 
	 * webView.setWebViewClient(new WebViewClient() {
	 * 
	 * @Override public boolean shouldOverrideUrlLoading(WebView view, String
	 * url) { //progress.setVisibility(View.VISIBLE); return
	 * super.shouldOverrideUrlLoading(view, url); }
	 * 
	 * @Override public void onPageFinished(WebView view, String url) { // TODO
	 * Auto-generated method stub super.onPageFinished(view, url);
	 * if(webView.canGoBack()) { back.setVisibility(View.VISIBLE); }else {
	 * back.setVisibility(View.GONE); } }
	 * 
	 * });
	 * 
	 * webView.loadUrl("http://www.ixxt.net/push/index");
	 */

	/*
	 * back.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { webView.goBack(); } });
	 */
	// }

	private void fetchTopicList(final long targetPage) {
		try {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "getTopicList");
			params.put("page", String.valueOf(targetPage));
			params.put("pageSize", String.valueOf(pageSize));
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
					Consts.SERVER_URL, Request.Method.POST, params, true,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							mListView.onRefreshComplete();
							try {
								System.out.println("response=  " + response);
								if (targetPage == 1) {
									topicList.clear();
								}
								if (response.optInt("ret") == 0) {
									curPageIndex = targetPage;
									topicList.addAll(Topic.parseJson(response));
									mAdapter.notifyDataSetChanged();
									if (topicList.size() > 0) {
										mListView.setMode(Mode.BOTH);
									} else {
										mListView
												.setMode(Mode.PULL_DOWN_TO_REFRESH);
									}
								} else {
									StatusUtils.handleStatus(response,
											TodayTopicActivity.this);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							mListView.onRefreshComplete();
							StatusUtils.handleError(arg0,
									TodayTopicActivity.this);
						}
					});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public static boolean onKeyDown() {
	// // TODO Auto-generated method stub
	// if (webView.canGoBack()) {
	// webView.goBack();
	// return true;
	// }
	// return false;
	// }

}
