package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshListView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.ClassMemberApplyListAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.data.http.ClassMemberApplyBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;

public class ClazzMemberApplyActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = ClazzMemberActivity.class.getSimpleName();
	
	//private AccountData account;
	private ClassInfoBean clazz;
	private PullToRefreshListView listView;
	private List<ClassMemberApplyBean> clazzMembers;
	private ClassMemberApplyListAdapter mAdapter;
	private Button back;
	private TextView mEmpty;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clazz_member);
		setTitle(R.string.title_clazz_member_apply);
		
		clazz = (ClassInfoBean)getIntent().getExtras().getSerializable("CLAZZ");
		if(clazz == null) {
			finish();
		}
		//account = getCurAccount();
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(this);
		
		listView = (PullToRefreshListView)findViewById(R.id.base_pull_list);
		clazzMembers = new ArrayList<ClassMemberApplyBean>();
		mAdapter = new ClassMemberApplyListAdapter(this, imageLoader, clazz, clazzMembers);
		listView.setAdapter(mAdapter);
		listView.setDivider(getResources().getDrawable(R.color.dark_gray));
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("查无数据");
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchClazzMember(false);
			}
		});
		fetchClazzMember(true);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("CLAZZ", clazz);			
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
			break;
		default:
			break;
		}
	}
	
    
    private void fetchClazzMember(boolean firstRefresh) {
    	if(firstRefresh) {
    		ProgressDialogUtils.showProgressDialog("", this, false);
    	}
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("commandtype", "getWaitApproveUserList");
		params.put("classroomId", String.valueOf(clazz.getClassroomId()));

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				listView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					clazzMembers = ClassMemberApplyBean.parseFromJson(response.optJSONArray("data"));
					if(clazzMembers.size() > 0) {
						mAdapter.addAll(clazzMembers);
					}
				}
				else {
					ProgressDialogUtils.dismissProgressBar();
					StatusUtils.handleStatus(response, ClazzMemberApplyActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				listView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, ClazzMemberApplyActivity.this);
			}
		});
    	BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("CLAZZ", clazz);			
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
        	return true;
        }
        return false;
    }
}
