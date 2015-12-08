package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshListView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.RecommendFriendsAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.NewFriend;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;
/**
 * 班级中新加入的成员审核页面
 * @author Yao
 */
public class NewFriendsInGroupActivity extends BaseActivity {
	private static final String TAG = NewFriendsInGroupActivity.class.getSimpleName();
	
	private List<NewFriend> friendsList;
	private RecommendFriendsAdapter mAdapter;
	private PullToRefreshListView mList;
	private TextView mEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recomment_friends_layout);
		setTitle(R.string.new_friend);
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		friendsList = new ArrayList<NewFriend>();
		mAdapter = new RecommendFriendsAdapter(this, imageLoader, friendsList);
		mList = (PullToRefreshListView) findViewById(R.id.base_pull_list);
		mList.setAdapter(mAdapter);
		mList.setDivider(getResources().getDrawable(R.color.dark_gray));
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("没有新的好友");
		mList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getListFromNetwork();
			}
		});
//		mList.setOnItemClickListener(mOnItemClickListener);
		getListFromNetwork();
	}
	
	public void getListFromNetwork() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getWaitApproveFriendList");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtils.d(TAG + " response=" + response);
				mList.onRefreshComplete();
				if (response.optInt("ret") == 0) {
					friendsList.clear();
//					JSONArray jsonObjs = response.optJSONArray("friendlist");
					JSONArray jsonObjs = response.optJSONArray("data");
					// msgid表示获取最新的，所以要清空原有的列表
					if (jsonObjs != null) {
						for (int i = 0; i < jsonObjs.length(); i++) {
							JSONObject jsonObj = (JSONObject) jsonObjs.opt(i);
							NewFriend friend = NewFriend.parseFromJson(jsonObj);
							friendsList.add(friend);
						}
						mAdapter.notifyDataSetChanged();
						if (mAdapter.isEmpty()) {
							mEmpty.setVisibility(View.VISIBLE);
						} else {
							mEmpty.setVisibility(View.GONE);
						}
						mList.setMode(Mode.PULL_FROM_START);
					}
				} else {
					StatusUtils.handleStatus(response, NewFriendsInGroupActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				mList.onRefreshComplete();
				mList.setMode(Mode.PULL_FROM_START);
				StatusUtils.handleError(arg0, NewFriendsInGroupActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	@Override
	protected void onDestroy() {
		BaseApplication.getInstance().cancelPendingRequests(TAG);
		super.onDestroy();
	}
	
}
