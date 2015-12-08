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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.JoinGroupAdapter;
import com.linkage.mobile72.sh.adapter.JoinGroupAdapter.NotifiHandler;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClassRoomBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.linkage.ui.widget.PullToRefreshListView;

/**
 * 搜索群组 -- 感兴趣的群组 界面
 * @author Yao
 *
 */
public class SearchGroupActivity extends BaseActivity implements OnClickListener,NotifiHandler {

	private static final String TAG = SearchGroupActivity.class.getSimpleName();
	private Button back;
	private RelativeLayout search_btn;
	//private RelativeLayout interestLayout;
	private PullToRefreshListView mListView;
	private JoinGroupAdapter mAdapter;
	private EditText edit_input;
	private List<ClassRoomBean> mData;
	private TextView mEmpty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_group);
		back = (Button)findViewById(R.id.back);
		setTitle(R.string.title_search_group);
		edit_input =(EditText)findViewById(R.id.search_edit);
		search_btn =(RelativeLayout)findViewById(R.id.search_btn);
		//interestLayout = (RelativeLayout)findViewById(R.id.relativelayout3);
		mListView = (PullToRefreshListView) findViewById(R.id.base_pull_list);
		mData = new ArrayList<ClassRoomBean>();
		mAdapter = new JoinGroupAdapter(this,imageLoader_group, mData);
		mListView.setAdapter(mAdapter);
		mListView.setDivider(null);
		//friendListView.setDivider(getResources().getDrawable(R.color.dark_gray));
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("暂时没有数据");
		mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchInterestGroup();
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(SearchGroupActivity.this, ClazzInfoActivity.class);
				intent.putExtra("CLAZZ_ID", mAdapter.getItemId(position));			
				startActivity(intent);
			}
			
		});
		back.setOnClickListener(this);
		search_btn.setOnClickListener(this);
		edit_input.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((actionId == 0 || actionId == 3) && event != null) {
					InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					if(imm.isActive()){
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
					}
					Animation shake = AnimationUtils.loadAnimation(SearchGroupActivity.this, R.anim.shake);
					String search_str = edit_input.getEditableText().toString();
					if (TextUtils.isEmpty(search_str)) {
						edit_input.setText("");
						edit_input.startAnimation(shake);
						UIUtilities.showToast(SearchGroupActivity.this, "搜索内容不能为空");
						return false;
					}
					Intent i = new Intent(SearchGroupActivity.this, SearchGroupResultActivity.class);
					i.putExtra("keyword", search_str);
					startActivity(i);
				}
				return false;
			}
		});
		
		fetchInterestGroup();
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.search_btn:
			Animation shake = AnimationUtils.loadAnimation(SearchGroupActivity.this, R.anim.shake);
			String search_str = edit_input.getEditableText().toString();
			if (TextUtils.isEmpty(search_str)) {
				edit_input.setText("");
				edit_input.startAnimation(shake);
				UIUtilities.showToast(this, "搜索内容不能为空");
				return;
			}
			Intent i = new Intent(this, SearchGroupResultActivity.class);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		JoinGroupAdapter.ehList.remove(this);
	}
	
	    
    private void fetchInterestGroup() {
		mData.clear();
		mListView.setMode(PullToRefreshBase.Mode.BOTH);
		mAdapter.addAll(mData, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "findInterstedClassroomList");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
				if (response.optInt("ret") == 0) {
					mData = ClassRoomBean.parseFromJson(response.optJSONArray("data"));
					if(mData.size() > 0) {
						mAdapter.addAll(mData, false);
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
	public void onMessage(long clazz, int type) {
		// TODO Auto-generated method stub
		
	}

}
