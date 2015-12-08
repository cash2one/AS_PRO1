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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.WonderExercise;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.CommonAdapter;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.utils.ViewHolder;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener2;
import com.linkage.ui.widget.PullToRefreshListView;

public class WonderExerciseActivity extends BaseActivity implements
		OnClickListener {
	private static final String TAG = WonderExerciseActivity.class
			.getSimpleName();

	private PullToRefreshListView mListView;
	private List<WonderExercise> exerList;
	private Button back;
	private CommonAdapter<WonderExercise> mAdapter;
	private Button mSet;
	private long curPageIndex = 1;
	private final int pageSize = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wonder_exercise);
		setTitle("精彩活动");
		back = (Button) findViewById(R.id.back);
		mSet = (Button) findViewById(R.id.set);
		mSet.setText("我的活动");
		mSet.setVisibility(View.INVISIBLE);
		SharedPreferences sp = BaseApplication.getInstance().getSp();
		Editor ed = sp.edit();
		ed.putInt("Huodong", 0);
		ed.commit();
		mListView = (PullToRefreshListView) findViewById(R.id.list);
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				fetchExerciseList(1);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				fetchExerciseList(++curPageIndex);
			}
		});
		back.setOnClickListener(this);
		mSet.setOnClickListener(this);
		exerList = new ArrayList<WonderExercise>();
		// for (int i = 0; i < 10; i++) {
		// WonderExercise we = new WonderExercise();
		// we.setDetailUrl("http://www.baidu.com");
		// exerList.add(we);
		// }
		mAdapter = new CommonAdapter<WonderExercise>(this,
                exerList, R.layout.adapter_wonder_exercise_item) {

            @Override
            public void convert(ViewHolder helper, final WonderExercise item) {
                NetworkImageView image = ((NetworkImageView) helper.getView(R.id.item_pic));
                image.setDefaultImageResId(R.drawable.default_wonder_excise_item_pic);
                int width = Utils.getWindowWidth(WonderExerciseActivity.this);
                int height = (int)(width * 0.4);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(new ViewGroup.LayoutParams(width, height));
                layoutParams.setMargins(30, 30, 30, 10);
                image.setLayoutParams(layoutParams);
                image.setScaleType(ScaleType.CENTER_CROP);
                helper.setImageUrl(R.id.item_pic, item.getPicUrl());
                helper.setText(R.id.item_title, item.getTitle());
                TextView view = helper.getView(R.id.item_flag);
                if (item.getFlag() == 1) {
                    view.setText("进行中");
                    view.setTextColor(getResources().getColor(
                            R.color.wonder_exer_item_60cdf6));
                } else if(item.getFlag() == 2){
                    helper.setText(R.id.item_flag, "未开始");
                    view.setTextColor(getResources()
                            .getColor(R.color.dark_gray));
                } else {
                	helper.setText(R.id.item_flag, "已结束");
                    view.setTextColor(getResources()
                            .getColor(R.color.dark_gray));
                }

//                helper.getView(R.id.itemLayout).setOnClickListener(
//                        new OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                Intent mIntent = new Intent(WonderExerciseActivity.this, WebViewHtHdActivity.class);
//                                mIntent.putExtra(WebViewHtHdActivity.KEY_URL, item.getDetailUrl());
//                                mIntent.putExtra(WebViewHtHdActivity.KEY_TITLE, "活动详情");
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
                WonderExercise item = exerList.get(position);
                Intent mIntent = new Intent(WonderExerciseActivity.this, WebViewHtHdActivity.class);
                mIntent.putExtra(WebViewHtHdActivity.KEY_URL, item.getDetailUrl());
                mIntent.putExtra(WebViewHtHdActivity.KEY_TITLE, item.getTitle());
                mIntent.putExtra(WebViewHtHdActivity.KEY_TOKEN, BaseApplication.getInstance().getAccessToken());
                mIntent.putExtra(WebViewHtHdActivity.FROM, "WonderExerciseActivity");
                
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(WebViewHtHdActivity.KEY_RES, item);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });
		fetchExerciseList(1);
	}

	private void fetchExerciseList(final long targetPage) {
		try {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "getActivityList");
			params.put("page", String.valueOf(targetPage));
			params.put("pageSize", String.valueOf(pageSize));
			// params.put("activityType", String.valueOf(0));//// 活动类型（0： 所有 1：
			// 我的）
			// params.put("cityId", value);//当前城市编号，-1时返回默认城市
			// params.put("activityState", String.valueOf(0));//活动状态(0: 全部 1:
			// 进行中 2:已经结束 )
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
					Consts.SERVER_getActivityList, Request.Method.POST, params,
					true, new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							mListView.onRefreshComplete();
							try {
								System.out.println("response=  " + response);
								if (targetPage == 1) {
									exerList.clear();
								}
								if (response.optInt("ret") == 0) {
									curPageIndex = targetPage;
									exerList.addAll(WonderExercise
											.parseJson(response));
									mAdapter.notifyDataSetChanged();
									if (exerList.size() > 0) {
										mListView.setMode(Mode.BOTH);
									} else {
										mListView
												.setMode(Mode.PULL_DOWN_TO_REFRESH);
									}
								} else {
									StatusUtils.handleStatus(response,
											WonderExerciseActivity.this);
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
									WonderExerciseActivity.this);
						}
					});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);

		} catch (Exception e) {
			e.printStackTrace();
		}
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
}
