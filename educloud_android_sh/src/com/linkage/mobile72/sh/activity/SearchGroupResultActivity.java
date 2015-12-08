package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.JoinGroupAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClassRoomBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener2;
import com.linkage.ui.widget.PullToRefreshListView;
/**
 * 群组搜索结果页面
 * @author Yao
 */
public class SearchGroupResultActivity extends BaseActivity {
	
	private static final String TAG = SearchGroupResultActivity.class.getSimpleName();
	
	private List<ClassRoomBean> mData;
	private JoinGroupAdapter mAdapter;
	private PullToRefreshListView mListView;
	private TextView mEmpty;
	private String keyword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recomment_friends_layout);
		setTitle("查找结果");
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Intent i = getIntent();
		if(i == null) {
			finish();
		}
		keyword = i.getStringExtra("keyword");
		mData = new ArrayList<ClassRoomBean>();
		mAdapter = new JoinGroupAdapter(this,imageLoader_group, mData);
		mListView = (PullToRefreshListView) findViewById(R.id.base_pull_list);
		mListView.setAdapter(mAdapter);
		mListView.setDivider(null);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("暂时没有数据");
		mListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				fetchGroup(false, 0);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				fetchGroup(false, mAdapter.getItemId(mAdapter.getCount()-1));
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(SearchGroupResultActivity.this, ClazzInfoActivity.class);
				intent.putExtra("CLAZZ_ID", mAdapter.getItemId(position));			
				startActivity(intent);
			}
		});
		mListView.setMode(Mode.BOTH);
		fetchGroup(true, 0);
	}
	
	private void fetchGroup(final boolean firstRefresh, final long id) {
		mListView.setMode(PullToRefreshBase.Mode.BOTH);
		if(id == 0) {
			mData.clear();
			mAdapter.addAll(mData, false);
		}
		/*if(firstRefresh) {
			ProgressDialogUtils.showProgressDialog("查询中", this, false);
		}*/
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "findClassroomByCnoAndName");
		params.put("name", keyword);
		params.put("id", String.valueOf(id));
		params.put("pageSize", Consts.PAGE_SIZE);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
				if (response.optInt("ret") == 0) {
					List<ClassRoomBean> data = ClassRoomBean.parseFromJson(response.optJSONArray("data"));
					if(data.size() > 0 && data.size() == Integer.parseInt(Consts.PAGE_SIZE)) {
						mAdapter.addAll(data, id != 0);
						mListView.setMode(PullToRefreshBase.Mode.BOTH);
					}else if(data.size() > 0 && data.size() < Integer.parseInt(Consts.PAGE_SIZE)) {
						mAdapter.addAll(data, id != 0);
						mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					}
					if (mAdapter.isEmpty()) {
						mEmpty.setVisibility(View.VISIBLE);
					} else {
						mEmpty.setVisibility(View.GONE);
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				mListView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
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
