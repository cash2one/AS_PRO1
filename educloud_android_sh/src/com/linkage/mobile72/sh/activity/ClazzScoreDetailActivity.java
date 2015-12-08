package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.ClazzScoreDetailAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClazzScoreDetail;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.linkage.ui.widget.PullToRefreshListView;

public class ClazzScoreDetailActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = ClazzScoreDetailActivity.class.getName();
	private long scoreId, subjectId, mCurrentClassId;
	private Button back,set;
	private PullToRefreshListView listView;
	private ClazzScoreDetailAdapter mAdapter;
	private List<ClazzScoreDetail> scoreList;
	private TextView mEmpty;
	
	private boolean orderDesc = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clazz_score_detail);
		scoreId = getIntent().getLongExtra("scoreId", 0);
		subjectId = getIntent().getLongExtra("subjectId", 0);
		mCurrentClassId = getIntent().getLongExtra("classid", 0);
		back = (Button)findViewById(R.id.back);
		set = (Button)findViewById(R.id.set);
		set.setText("由高到低");
		set.setVisibility(View.VISIBLE);
		/*Drawable nav_up = getResources().getDrawable(R.drawable.nav_desc);  
		nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());  
		set.setCompoundDrawables(null, null, nav_up, null); */ 
		listView = (PullToRefreshListView)findViewById(R.id.base_pull_list);
		scoreList = new ArrayList<ClazzScoreDetail>();
		mAdapter = new ClazzScoreDetailAdapter(this, scoreList);
		listView.setAdapter(mAdapter);
		listView.setDivider(null);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("查无数据");
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchScoreData();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		setTitle("成绩详情");
		back.setOnClickListener(this);
		set.setOnClickListener(this);
		fetchScoreData();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.set:
			if(orderDesc) {
				/*List<ClazzScoreDetail> numberList = new ArrayList<ClazzScoreDetail>();
				List<ClazzScoreDetail> charList = new ArrayList<ClazzScoreDetail>();
				if(scoreList != null && scoreList.size()>0) {
					for(ClazzScoreDetail c:scoreList){
						if(isNumeric(c.getResult())){
							numberList.add(c);
						}else {
							charList.add(c);
						}
					}
				}
				Collections.sort(numberList, new ClazzScoreDetail.ScoreDescComparator());
				numberList.addAll(0, charList);*/
				orderDesc = false;
				Collections.reverse(scoreList);
				mAdapter.addData(scoreList);
				/*Drawable nav_up = getResources().getDrawable(R.drawable.nav_asc);  
				nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());  
				set.setCompoundDrawables(null, null, nav_up, null);  */
				set.setText("由低到高");
			}else {
				/*List<ClazzScoreDetail> numberList = new ArrayList<ClazzScoreDetail>();
				List<ClazzScoreDetail> charList = new ArrayList<ClazzScoreDetail>();
				if(scoreList != null && scoreList.size()>0) {
					for(ClazzScoreDetail c:scoreList){
						if(isNumeric(c.getResult())){
							numberList.add(c);
						}else {
							charList.add(c);
						}
					}
				}
				Collections.sort(numberList, new ClazzScoreDetail.ScoreAcsComparator());
				numberList.addAll(0, charList);*/
				orderDesc = true;
				Collections.reverse(scoreList);
				mAdapter.addData(scoreList);
				/*Drawable nav_up = getResources().getDrawable(R.drawable.nav_desc);  
				nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());  
				set.setCompoundDrawables(null, null, nav_up, null);  */
				set.setText("由高到低");
			}
			break;
		}

	}
	
	private void fetchScoreData() {
		// mProgress.setVisibility(View.VISIBLE);
		ProgressDialogUtils.showProgressDialog("", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getStuResultList");
		params.put("id", String.valueOf(scoreId));
		params.put("subjectid", String.valueOf(subjectId));
		params.put("classid", String.valueOf(mCurrentClassId));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_getStuResultList, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// mProgress.setVisibility(View.GONE);
						ProgressDialogUtils.dismissProgressBar();
						LogUtils.i(" response=" + response);
						if (response.optInt("ret") == 0) {
							List<ClazzScoreDetail> temp = ClazzScoreDetail.parseFromJson(response.optJSONArray("data"));
							scoreList = temp;
							mAdapter.addData(temp);
							mAdapter.notifyDataSetChanged();
							if (mAdapter.isEmpty()) {
								mEmpty.setVisibility(View.VISIBLE);
							} else {
								mEmpty.setVisibility(View.GONE);
							}
							listView.onRefreshComplete();
							
						} else {
							T.showShort(ClazzScoreDetailActivity.this,response.optString("msg"));
							scoreList.clear();
							mAdapter.notifyDataSetChanged();
							if (mAdapter.isEmpty()) {
								mEmpty.setVisibility(View.VISIBLE);
							} else {
								mEmpty.setVisibility(View.GONE);
							}
							listView.onRefreshComplete();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, ClazzScoreDetailActivity.this);
					}
				});
		
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		/*mAdapter.addAll(null, page != 1);
		if (mAdapter.isEmpty()) {
			mEmpty.setVisibility(View.VISIBLE);
		} else {
			mEmpty.setVisibility(View.GONE);
		}
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				LogUtils.e("FetchScoreData");
				List<ClazzScore> temp = new ArrayList<ClazzScore>();
				int index = (page-1)*Integer.parseInt(Consts.PAGE_SIZE);
				for(int i=index; i<index + Integer.parseInt(Consts.PAGE_SIZE); i++) {
					ClazzScore c = new ClazzScore();
					c.setAverage(80);
					c.setDate("2015-04-10");
					c.setId(i+1);
					c.setMax(90);
					c.setMin(60);
					c.setName(subjectList.get(current_tab).getName()+"考试"+i);
					c.setTypeName("期中考试");
					temp.add(c);
				}
				LogUtils.e("scoreList.size:"+temp.size());
				mAdapter.addAll(temp, page != 1);
				if (mAdapter.isEmpty()) {
					mEmpty.setVisibility(View.VISIBLE);
				} else {
					mEmpty.setVisibility(View.GONE);
				}
				listView.onRefreshComplete();
			}
		}, 1000);
		setTitle(mCurrentClass.getName());*/
	}
	
	public static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	}
}
