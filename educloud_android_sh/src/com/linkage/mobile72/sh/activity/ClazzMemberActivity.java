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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.ClassMemberListAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.data.http.ClassMemberBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener2;
import com.linkage.ui.widget.PullToRefreshListView;

public class ClazzMemberActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = ClazzMemberActivity.class.getSimpleName();
	
	//private AccountData account;
	private ClassInfoBean clazz;
	private PullToRefreshListView listView;
	private List<ClassMemberBean> clazzMembers;
	private ClassMemberListAdapter mAdapter;
	private Button back, menu;
	private TextView mEmpty;
	private long changerTID;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clazz_member);
		setTitle(R.string.title_clazz_member);
		
		clazz = (ClassInfoBean)getIntent().getExtras().getSerializable("CLAZZ");
		if(clazz == null) {
			finish();
		}
		//account = getCurAccount();
		back = (Button)findViewById(R.id.back);
		menu = (Button)findViewById(R.id.set);
		menu.setVisibility(View.INVISIBLE);
		menu.setBackgroundResource(R.drawable.title_menu);
		back.setOnClickListener(this);
		menu.setOnClickListener(this);
//		changerTID  = getIntent().getLongExtra("CLAZZ_LOADER",0);
		changerTID= 0;
		if(clazz.getChange_teacherID() !=null)
		changerTID = Long.parseLong(clazz.getChange_teacherID());
		listView = (PullToRefreshListView)findViewById(R.id.base_pull_list);
		clazzMembers = new ArrayList<ClassMemberBean>();
		mAdapter = new ClassMemberListAdapter(this, imageLoader, clazzMembers, changerTID, String.valueOf(clazz.getClassroomId()));
		listView.setAdapter(mAdapter);
		listView.setDivider(null);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("查无数据");
		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchClazzMember(false, 0);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchClazzMember(false, mAdapter.getItemId(mAdapter.getCount()-1));
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ClassMemberBean contact = (ClassMemberBean) mAdapter.getItem(position);
				/*Intent intent = NewChatActivity.getIntent(ClazzMemberActivity.this, contact.getUserId(),
						clazz.getClassroomName(),ChatType.CHAT_TYPE_GROUP,0);*/
				Intent intent = new Intent(ClazzMemberActivity.this,
                        PersonalInfoActivity.class);
                intent.putExtra("id", contact.getUserId());
                startActivity(intent);
			}
		});
		fetchClazzMember(true, 0);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			/*Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("CLAZZ", clazz);			
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);*/
			finish();
			break;
		case R.id.set:
			Bundle bundle = new Bundle();
			bundle.putSerializable("CLAZZ", clazz);
			Intent intent = new Intent(this, InviteFriendActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
	}
	
    
    private void fetchClazzMember(boolean firstRefresh, long id) {
    	if(firstRefresh) {
    		ProgressDialogUtils.showProgressDialog("", this, true);
    	}
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("commandtype", "getClassroomRemListByCId");
		params.put("classroomId", String.valueOf(clazz.getClassroomId()));
		params.put("pageSize", Consts.PAGE_SIZE);
		params.put("id", ""+id);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				listView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
				if (response.optInt("ret") == 0) {
					clazzMembers = ClassMemberBean.parseFromJson(response.optJSONArray("data"));
					if(clazzMembers.size() > 0) {
						mAdapter.addAll(clazzMembers);
					}
				} 
				else {
					ProgressDialogUtils.dismissProgressBar();
					StatusUtils.handleStatus(response, ClazzMemberActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				listView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, ClazzMemberActivity.this);
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
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	BaseApplication.getInstance().cancelPendingRequests(TAG);
    }
}
