package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.GridImageAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.data.http.ClassMemberBean;
import com.linkage.mobile72.sh.event.ContactsEvent;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.widget.TouchInterceptGridView;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.linkage.ui.widget.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import de.greenrobot.event.EventBus;

/**
 * 班级资料界面
 * @author Yao
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ClazzInfoActivity extends BaseActivity implements OnClickListener{

	private static final String TAG = ClazzInfoActivity.class.getSimpleName();
	
	private long clazzId;
	private ClassInfoBean clazz;
	private List<ClassMemberBean> clazzMembers;
	private Button back, menu;
	private PullToRefreshScrollView pullScrollView;
	private RelativeLayout groupMynickLayout;
	private RelativeLayout groupMembersLayout;
	private RelativeLayout groupManageLayout;
	//private RelativeLayout groupAreaLayout;
	private RelativeLayout groupSchoolLayout;
	private Button applyComingBtn;
	private TextView className, classNo, groupMemberSize, nickName, groupAreaName, groupSchoolName;
	private ImageView classAvater;
	private MyCommonDialog dialog;
	public static final int REQUEST_CODE_CLAZZINFO = 1;
	public static final int REQUEST_CODE_NICKNAME = 2;
	public static final int REQUEST_CODE_APPLY = 3;
	public static final int REQUEST_CODE_MEMBER = 5;
	public boolean isJoin = false, isLeader = false;
	private PopupWindow pop;  
	private View view;
	private TouchInterceptGridView group_members_avatar;
	private GridImageAdapter imgadapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clazz_info);
		/*clazz = (ClassInfoBean)getIntent().getExtras().getSerializable("CLAZZ");
		if(clazz == null) {
			finish();
		}*/
		clazzId = getIntent().getLongExtra("CLAZZ_ID", 0);
		if(clazzId == 0) {
			finish();
		}
		setTitle(R.string.title_clazz_info);
		back = (Button)findViewById(R.id.back);
		menu = (Button)findViewById(R.id.set);
		menu.setBackgroundResource(R.drawable.title_menu);
		initPopupWindow();
		
		pullScrollView = (PullToRefreshScrollView)findViewById(R.id.pullScrollView);
		groupMynickLayout = (RelativeLayout)findViewById(R.id.group_mynick_layout);
		groupMembersLayout = (RelativeLayout)findViewById(R.id.group_members_layout);
		groupManageLayout = (RelativeLayout)findViewById(R.id.group_manage_layout);
		//groupAreaLayout = (RelativeLayout)findViewById(R.id.group_area_layout);
		groupSchoolLayout = (RelativeLayout)findViewById(R.id.group_school_layout);
		applyComingBtn = (Button)findViewById(R.id.apply_coming_btn);
		
		applyComingBtn.setVisibility(View.GONE);
		className = (TextView)findViewById(R.id.textview_classname);
		classNo = (TextView)findViewById(R.id.textview_class_no);
		classAvater = (ImageView)findViewById(R.id.imageview_classavater);
		groupMemberSize = (TextView)findViewById(R.id.group_members_size);
		group_members_avatar = (TouchInterceptGridView) findViewById(R.id.group_members_avatar);
		nickName = (TextView)findViewById(R.id.nick_name);
		groupAreaName = (TextView)findViewById(R.id.group_area_text);
		groupSchoolName = (TextView)findViewById(R.id.group_school_text);
		
		/*className.setText(clazz.getClassroomName());
		classNo.setText("班级号："+clazz.getClassroom_popId());
		clazzMembers = clazz.getMemberInfoList();
		groupAreaName.setText(clazz.getRedion());
		groupSchoolName.setText(clazz.getSchoolName());*/
		
		findClassroomById(String.valueOf(clazzId));
		
		back.setOnClickListener(this);
		//groupMynickLayout.setOnClickListener(this);
		groupMembersLayout.setOnClickListener(this);
		group_members_avatar.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ClazzInfoActivity.this, ClazzMemberActivity.class);
				Bundle bundle = new Bundle();
				if(clazz != null)
				bundle.putSerializable("CLAZZ", clazz);
				intent.putExtras(bundle);
				if(clazz.getChange_teacherID()!=null)
				intent.putExtra("changerTID",Long.parseLong(clazz.getChange_teacherID()));
				startActivityForResult(intent, REQUEST_CODE_MEMBER);
			}
		});
		pullScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				findClassroomById(String.valueOf(clazzId));
			}
		});
		/*
		 * 头像显示************************************************
		 */
		clazzMembers = new ArrayList<ClassMemberBean>();
	}
	
	private void initPopupWindow() {  
        view = this.getLayoutInflater().inflate(R.layout.popup_windows_menu, null);  
        pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,  
                ViewGroup.LayoutParams.MATCH_PARENT,true);  
        pop.setOutsideTouchable(true); 
        ColorDrawable dw = new ColorDrawable(-00000);
        pop.setBackgroundDrawable(dw);
        pop.setAnimationStyle(R.style.popupWindowAnimation);
        pop.update();
        Button pop_cancel = (Button) view.findViewById(R.id.btn_pop_cancel);
        pop_cancel.setOnClickListener(this); 
        Button pop_report = (Button) view.findViewById(R.id.btn_pop_report);
        pop_report.setOnClickListener(this); 
        Button pop_exitgroup = (Button) view.findViewById(R.id.btn_pop_exitgroup);
        pop_exitgroup.setOnClickListener(this); 
    } 
	
	@Override
	public void onClick(View v) {
		Intent intent;
		Bundle bundle = new Bundle();
		if(clazz != null)
		bundle.putSerializable("CLAZZ", clazz);
		switch (v.getId()) {
			case R.id.back:
				setResult(0,  getIntent());
				finish();
				break;
			/*
			 * PopupWindows!!!!!!!!
			 */
			case R.id.set:
//				popIfQuitClazz();
				if (pop.isShowing()) {  
	                pop.dismiss();  
	            } else {  
	                pop.showAtLocation(findViewById(R.id.layout_class), Gravity.BOTTOM, 0, 0);  
	            }  
				
				break;
			case R.id.group_school_text:
				/*intent = new Intent(this, InviteFriendActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);*/
				break;
			case R.id.group_members_layout:
				intent = new Intent(this, ClazzMemberActivity.class);
				intent.putExtras(bundle);
				if(clazz.getChange_teacherID()!=null)
				intent.putExtra("changerTID",Long.parseLong(clazz.getChange_teacherID()));
				startActivityForResult(intent, REQUEST_CODE_MEMBER);
				break;
			case R.id.group_manage_layout:
				/*intent = new Intent(this, ClazzManageActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);*/
				break;
			case R.id.group_mynick_layout:
				intent = new Intent(this, ClazzMyNickActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case R.id.apply_coming_btn:
				popIfJoinClazz();
				break;
//			case R.id.group_exit_group:
//				popIfQuitClazz();
//				break;
			case R.id.btn_pop_cancel:
				pop.dismiss();  
				break;
			case R.id.btn_pop_report:
				break;
			case R.id.btn_pop_exitgroup:
				popIfQuitClazz();
				break;
		}
	}
	//弹出是否加入群组织
	private void popIfJoinClazz() {
		dialog = new MyCommonDialog(this, "提示消息", "确定要加入本群吗？", "取消", "加入");
		dialog.setCancelListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
			}
		});
		dialog.setOkListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
				addClazz();
			}
		});
		dialog.show();
	}
	//弹出是否退出群组织
	private void popIfQuitClazz() {
		dialog = new MyCommonDialog(this, "提示消息", "确定要退出本群吗？", "取消", "退出本群");
		dialog.setCancelListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
			}
		});
		dialog.setOkListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
				quitClazz();
			}
		});
		dialog.show();
	}
	
	private void findClassroomById(String classroomId) {
		ProgressDialogUtils.showProgressDialog("", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "findClassroomByClassId");
		params.put("classId", classroomId);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				pullScrollView.onRefreshComplete();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					//解析
					clazz = ClassInfoBean.parseFromJson(response.optJSONObject("data"));
					if(clazz != null) {
						fillClazzToPage(clazz);
					}
				} 
				else {
					StatusUtils.handleStatus(response, ClazzInfoActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				pullScrollView.onRefreshComplete();
				StatusUtils.handleError(arg0, ClazzInfoActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	//获取到班级资料 绑定到页面元素
	private void fillClazzToPage(ClassInfoBean clazz) {
		className.setText(clazz.getClassroomName());
		classNo.setText("班级号："+clazz.getClassroom_popId());
		clazzMembers = clazz.getMemberInfoList();
		DisplayImageOptions defaultOptions_group = new DisplayImageOptions.Builder().cacheOnDisc().showStubImage(R.drawable.default_group).showImageForEmptyUri(R.drawable.default_group).showImageOnFail(R.drawable.default_group).build();
		imageLoader_group.displayImage (Consts.SERVER_HOST + clazz.getAvatar(), classAvater, defaultOptions_group);
		groupAreaName.setText(clazz.getRedion());
		groupSchoolName.setText(clazz.getSchoolName());
		System.out.println(getCurAccount().getUserId());
		System.out.println(Long.valueOf(clazz.getChange_teacherID()).longValue());
		isJoin = (clazz.getIsJoin() == 1 || clazz.getIsJoin() == 3);
		if(getCurAccount().getUserId() == Long.valueOf(clazz.getChange_teacherID()).longValue()) {
			isLeader = true;
		}
		//是否加入
//		if(isJoin) {
//			menu.setVisibility(View.VISIBLE);
//			applyComingBtn.setVisibility(View.GONE);
//			menu.setOnClickListener(this);
//			groupMynickLayout.setVisibility(View.VISIBLE);
//		}else {
//			menu.setVisibility(View.GONE);
//			applyComingBtn.setVisibility(View.VISIBLE);
//			applyComingBtn.setOnClickListener(this);
//			groupMynickLayout.setVisibility(View.GONE);
//			
//		}
		groupManageLayout.setClickable(true);
		groupSchoolLayout.setClickable(true);
		groupManageLayout.setVisibility(View.GONE);
		//班级管理者
		if(isLeader) {
			groupManageLayout.setOnClickListener(this);
			groupSchoolLayout.setOnClickListener(this);
		}
		groupMemberSize.setText(clazz.getMemberCount()+"人");
		nickName.setText(clazz.getMycard());
		imgadapter = new GridImageAdapter(ClazzInfoActivity.this,imageLoader,clazzMembers, isJoin, clazz);
		group_members_avatar.setAdapter(imgadapter);
		
	}
	
	//申请加入班级
	private void addClazz() {
		if(dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		ProgressDialogUtils.showProgressDialog("加入申请中", this, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "applyJoinClassroom");
		params.put("classroomId", String.valueOf(clazz.getClassroomId()));
		params.put("type", String.valueOf(getCurAccount().getUserType()));
		params.put("applyReason", "申请加入");
		
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();

				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {//0 申请成功 2是加入成功 3已加入无需重复加入
					// TODO 登录成功后的帐号更新等
					UIUtilities.showToast(ClazzInfoActivity.this, "申请成功，请耐心等待群管理员审核");
					applyComingBtn.setVisibility(View.GONE);
				} else if(response.optInt("ret") == 2){
					UIUtilities.showToast(ClazzInfoActivity.this, "您已成功加班级"+clazz.getClassroomName());
					applyComingBtn.setVisibility(View.GONE);
				}else {
					StatusUtils.handleStatus(response, ClazzInfoActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, ClazzInfoActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, "JoinClazzActivity");
		
	}
	
	//退出班级
	private void quitClazz() {
		ProgressDialogUtils.showProgressDialog("退出中",this, false);
	
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "quitClassroom");
		params.put("classroomId", String.valueOf(clazz.getClassroomId()));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();

				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					// TODO 登录成功后的帐号更新等
					EventBus.getDefault().post(new ContactsEvent(1));
					UIUtilities.showToast(ClazzInfoActivity.this, "您已成功退出班级"+clazz.getClassroomName());
					setResult(1,  getIntent());
					finish();
				} else {
					StatusUtils.handleStatus(response, ClazzInfoActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, ClazzInfoActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case REQUEST_CODE_CLAZZINFO:
			if(resultCode == RESULT_OK)
				findClassroomById(String.valueOf(clazzId));
				break;
		case REQUEST_CODE_NICKNAME:
			if(resultCode == RESULT_OK)
				findClassroomById(String.valueOf(clazzId));
				break;
		case REQUEST_CODE_APPLY:
			if(resultCode == RESULT_OK)
				findClassroomById(String.valueOf(clazzId));
				break;
				
		case REQUEST_CODE_MEMBER:
			/*if(resultCode == RESULT_OK)
				findClassroomById(String.valueOf(clazzId));*/
				break;
		} 
	}
	
}
