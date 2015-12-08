package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.linkage.ui.widget.PullToRefreshScrollView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.JoinClazzListAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClassRoomBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.InnerListView;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;

public class JoinClazzActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = JoinClazzActivity.class.getSimpleName();
	
	private Long schoolId; String schoolName, schoolPicture; int isFirstComeIn;
	private RelativeLayout quitClazzLayout;
	private MyCommonDialog dialog;
	private Button back;
	private ImageView schoolAvatar;
	private TextView schoolNameTextView,schoolDescTextView;
	private PullToRefreshScrollView parentScrollView;
	private InnerListView clazzListView;
	private JoinClazzListAdapter mClazzAdapter;
	private List<ClassRoomBean> clazzs;
	private int schoolType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		schoolId = getIntent().getLongExtra("SCHOOL_ID", 0);
		schoolName = getIntent().getStringExtra("SCHOOL_NAME");
		schoolPicture = getIntent().getStringExtra("SCHOOL_PICTURE");
		isFirstComeIn = getIntent().getIntExtra("IS_FRIST", 0);
		schoolType = getIntent().getIntExtra("SCHOOL_TYPE", 0);
		if(schoolType == 3)
		{
			schoolType =1 ;
		}
		if(schoolId == 0) {
			finish();
		}
		setContentView(R.layout.activity_join_clazz);
		setTitle(R.string.join_clazz);
		back = (Button)findViewById(R.id.back);
		schoolAvatar = (ImageView)findViewById(R.id.avatar);
		schoolNameTextView = (TextView)findViewById(R.id.text1);
		schoolDescTextView = (TextView)findViewById(R.id.text2);
		schoolNameTextView.setText(schoolName);
		schoolDescTextView.setVisibility(View.GONE);
		parentScrollView = (PullToRefreshScrollView)findViewById(R.id.pullScrollView);
		clazzListView = (InnerListView)findViewById(R.id.clazz_list);
		quitClazzLayout=(RelativeLayout)findViewById(R.id.mylayout3);
		clazzs = new ArrayList<ClassRoomBean>();
		fetchClazzData();
		mClazzAdapter = new JoinClazzListAdapter(this, clazzListView, clazzs, schoolType);
		if(isFirstComeIn == 1)//第一次进入该学校的班级列表时 退出班级菜单隐藏
			quitClazzLayout.setVisibility(View.INVISIBLE);
		back.setOnClickListener(this);
		quitClazzLayout.setOnClickListener(this);
		if(!StringUtils.isEmpty(schoolPicture)) {
			imageLoader_group.displayImage(Consts.SERVER_IP + schoolPicture, schoolAvatar);
		}
		parentScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// TODO Auto-generated method stub
				fetchClazzData();
			}
			
		});
//		clazzListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				final ClassRoomBean group = clazzs.get(position);
//				if(group.getIsChoose() == 1)
//				{
//					dialog = new MyCommonDialog(JoinClazzActivity.this, "提示消息", "确认加入班级吗？", "取消", "加入");
//					dialog.setOkListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							if(dialog.isShowing())
//								dialog.dismiss();
//							addClazz(group.getClassroomId());
//							
//						}
//					});
//					dialog.setCancelListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							if(dialog.isShowing())
//							dialog.dismiss();
//						}
//					});
//					dialog.show();
//				}else{
//					dialog = new MyCommonDialog(JoinClazzActivity.this, "提示消息", "确认退出班级吗？", "取消", "加入");
//					dialog.setOkListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							if(dialog.isShowing())
//								dialog.dismiss();
//							quitClazz(group.getClassroomId());
//							
//						}
//					});
//					dialog.setCancelListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							if(dialog.isShowing())
//							dialog.dismiss();
//						}
//					});
//					dialog.show();
//				}
//				
//				
//			}
//		});
	}
	

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

	private void fetchClazzData() {
		ProgressDialogUtils.showProgressDialog("查询中", this, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("schoolId", String.valueOf(schoolId));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_GetClassroom_bySchoolID, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				parentScrollView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					clazzs = ClassRoomBean.parseFromJson(response.optJSONArray("data"));
					if(clazzs.size() > 0) {
						mClazzAdapter.addAll(clazzs);
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				parentScrollView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.mylayout3:
			dialog = new MyCommonDialog(this, "提示消息", "确认退出"+schoolName+"吗？", "取消", "退出");
			dialog.setOkListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					quitSchool(schoolId);
				}
			});
			dialog.setCancelListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(dialog.isShowing())
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		default:
			break;
		}
	}
	
	public void quitSchool(final long schoolId) {
		ProgressDialogUtils.showProgressDialog("申请中",this, false);
	
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("schoolId", String.valueOf(schoolId));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_Exit_School, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();

				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					// TODO 登录成功后的帐号更新等
					try {
						getDBHelper().getSchoolData().updateRaw("update SchoolData set isJoin = 0 where schoolId = "+schoolId);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					UIUtilities.showToast(JoinClazzActivity.this, "您已成功退出"+schoolName);
					finish();
				} else {
					StatusUtils.handleStatus(response, JoinClazzActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, JoinClazzActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		
	}
}
