package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.MbManagerListAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.JxTemplate;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener2;
import com.linkage.ui.widget.PullToRefreshListView;
/**
 * 模板搜索结果页面
 * @author Yao
 */
public class SearchMbResultActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = SearchMbResultActivity.class.getSimpleName();
	
	private static final int REQUEST_REFRESH = 1;
	private List<JxTemplate> mData;
	private MbManagerListAdapter mAdapter;
	private PullToRefreshListView mListView;
	private TextView mEmpty;
	private String keyword;
	private int page = 1;
    private int type;
    private Boolean flag = false;
	
	private EditText editInput;
	private Button searchBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra("type", Consts.JxhdType.HOMEWORK);
		setContentView(R.layout.mb_manage_search_layout);
		setTitle("查找结果");
		findViewById(R.id.back).setOnClickListener(this);
		editInput = (EditText)findViewById(R.id.search_input);
		searchBtn = (Button)findViewById(R.id.search_btn);
		searchBtn.setOnClickListener(this);
		mData = new ArrayList<JxTemplate>();
		mAdapter = new MbManagerListAdapter(this, mData);
		mListView = (PullToRefreshListView) findViewById(R.id.base_pull_list);
		mListView.setAdapter(mAdapter);
		mListView.setDivider(null);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("暂时没有数据");
		
		mListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				page = 1;
				fetchData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				fetchData();
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				JxTemplate jxtemplate = mAdapter.getItem(position);
				Intent intentMbManager = new Intent(SearchMbResultActivity.this, JxMbDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("TEMPLATE", jxtemplate); 
				intentMbManager.putExtras(bundle);
				startActivityForResult(intentMbManager, REQUEST_REFRESH);
			}
			
		});
		mListView.setMode(Mode.BOTH);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search_btn:
			page = 1;
			mData.clear();
            mAdapter.addAll(mData, page != 1);
			fetchData();
			break;
		case R.id.back:
		    setResult(RESULT_OK);
			finish();
			break;
		}
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
        case REQUEST_REFRESH:
            page = 1;
            fetchData();
            break;
        }
    }
	
	private void fetchData() {
		if(flag)
		{
			return ;
		}
		
		keyword = editInput.getText().toString();
		if(TextUtils.isEmpty(keyword) || TextUtils.isEmpty(keyword.trim())) {
			Toast.makeText(this, "搜的内容不能为空的", Toast.LENGTH_SHORT).show();
			return;
		}
		flag = true;
		ProgressDialogUtils.showProgressDialog("",SearchMbResultActivity.this);
		BaseApplication.getInstance().cancelPendingRequests(TAG);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "userTemplateSearch");
		params.put("type", ""+type);
		params.put("keyword", keyword);
		params.put("page", String.valueOf(page));
		params.put("pageSize", Consts.PAGE_SIZE);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true,
			new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					mListView.onRefreshComplete();
					flag = false;
					LogUtils.e("response=" + response.toString());
					if (response.optInt("ret") == 0) {
						if(page == 1)
							mData.clear();
						mData = JxTemplate.parseFromJson(response.optJSONArray("data"));
						if(mData.size() > 0 && mData.size() == Integer.parseInt(Consts.PAGE_SIZE)) {
							mAdapter.addAll(mData, page != 1);
							mListView.setMode(PullToRefreshBase.Mode.BOTH);
						}else if(mData.size() > 0 && mData.size() < Integer.parseInt(Consts.PAGE_SIZE)) {
							mAdapter.addAll(mData, page != 1);
							mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
						}
						page = page + 1;
					}
					else {
					    mData.clear();
					    mAdapter.addAll(mData, page != 1);
                    }
                    if (mAdapter.isEmpty()) {
                        mEmpty.setVisibility(View.VISIBLE);
                    } else {
                        mEmpty.setVisibility(View.GONE);
                    }
                    ProgressDialogUtils.dismissProgressBar();
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					flag = false;
					mListView.onRefreshComplete();
					ProgressDialogUtils.dismissProgressBar();
					StatusUtils.handleError(arg0, SearchMbResultActivity.this);
				}
			});
		BaseApplication.getInstance().addToRequestQueue(mRequest, "");
	}
	
	@Override
	protected void onDestroy() {
		BaseApplication.getInstance().cancelPendingRequests(TAG);
		super.onDestroy();
	}
	
}
