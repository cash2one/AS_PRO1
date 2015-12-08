package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.JoinFriendAdapter;
import com.linkage.mobile72.sh.adapter.JoinGroupAdapter;
import com.linkage.mobile72.sh.adapter.JoinGroupAdapter.NotifiHandler;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ApplyFriendBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.linkage.ui.widget.PullToRefreshListView;

public class SearchPersonActivity extends BaseActivity implements OnClickListener, NotifiHandler {
	private static final String TAG = SearchPersonActivity.class.getSimpleName();

	private RelativeLayout search_btn;
	// private RelativeLayout interestLayout;
	private PullToRefreshListView mListView;
	private JoinFriendAdapter mAdapter;
	private EditText edit_input;
	private List<ApplyFriendBean> mData;
	private TextView mEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_person);
		setTitle(R.string.title_search_person);
		findViewById(R.id.back).setOnClickListener(this);
		edit_input = (EditText) findViewById(R.id.search_edit);
		search_btn = (RelativeLayout) findViewById(R.id.search_btn);
		if(isTeacher()){
			search_btn.setBackgroundResource(R.drawable.search_btn_bg_green);
		}
		// interestLayout = (RelativeLayout) findViewById(R.id.relativelayout3);
		mListView = (PullToRefreshListView) findViewById(R.id.base_pull_list);
		mData = new ArrayList<ApplyFriendBean>();
		mAdapter = new JoinFriendAdapter(this, imageLoader_group, true, mData,2);
		mListView.setAdapter(mAdapter);
		mListView.setDivider(getResources().getDrawable(R.color.dark_gray));
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("查无数据");
		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchData(false, 0);
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ApplyFriendBean friend = mAdapter.getItem(position);
				Intent intent = new Intent(SearchPersonActivity.this, PersonalInfoActivity.class);
				intent.putExtra("id", friend.getUserId());
				startActivity(intent);
			}

		});
		search_btn.setOnClickListener(this);
		edit_input.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_GO
						|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
							Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					}
					Animation shake = AnimationUtils.loadAnimation(SearchPersonActivity.this,
							R.anim.shake);
					String search_str = edit_input.getEditableText().toString();
					if (TextUtils.isEmpty(search_str)) {
						edit_input.setText("");
						edit_input.startAnimation(shake);
						UIUtilities.showToast(SearchPersonActivity.this, "搜索内容不能为空");
						return false;
					}
					Intent i = new Intent(SearchPersonActivity.this,
							SearchPersonResultActivity.class);
					i.putExtra("keyword", search_str);
					startActivity(i);
				}
				return false;
			}
		});
		fetchData(true, 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.search_btn:
			Animation shake = AnimationUtils.loadAnimation(SearchPersonActivity.this, R.anim.shake);
			String search_str = edit_input.getEditableText().toString();
			if (TextUtils.isEmpty(search_str)) {
				edit_input.setText("");
				edit_input.startAnimation(shake);
				UIUtilities.showToast(this, "搜索内容不能为空");
				return;
			}
			Intent i = new Intent(this, SearchPersonResultActivity.class);
			i.putExtra("keyword", search_str);
			startActivity(i);
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		JoinGroupAdapter.ehList.add(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// MyPushMessageReceiver.ehList.remove(this);
	}

	private void fetchData(boolean firstRefresh, final long id) {
		mData.clear();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "findInterstedFriendList");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						mListView.onRefreshComplete();
						// ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
							mData = ApplyFriendBean.parseFromJson(response.optJSONArray("data"));
							if (mData.size() > 0) {
								mAdapter.addAll(mData, false);
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
						// ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	@Override
	public void onMessage(final long friendID, final int type) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onBackPressed() {
		hideKeyboard(edit_input.getApplicationWindowToken());
		super.onBackPressed();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		JoinGroupAdapter.ehList.remove(this);
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}