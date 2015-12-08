package com.linkage.mobile72.sh.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 班级管理[from 班级资料]
 * @author Yao
 */
public class ClazzManageActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = ClazzManageActivity.class.getSimpleName();
	
	private ClassInfoBean clazz;
	private Button back;
	private TextView groupNameText, groupWaitNumberText;
	private RelativeLayout groupAvatarLayout, groupNameLayout, groupWaitapplyLayout;
	private CheckBox grantCheckbox;
	private boolean isLeader = false;//is班主任
	private MyCommonDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clazz_manage);
		
		clazz = (ClassInfoBean)getIntent().getExtras().getSerializable("CLAZZ");
		if(clazz == null) {
			finish();
			return;
		}
		if(getCurAccount().getUserId() == Long.valueOf(clazz.getChange_teacherID())) {
			isLeader = true;
		}
		setTitle(R.string.title_clazz_manage);
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(this);
		groupAvatarLayout = (RelativeLayout)findViewById(R.id.group_avatar_layout);
		groupNameLayout = (RelativeLayout)findViewById(R.id.group_name_layout);
		groupWaitapplyLayout = (RelativeLayout)findViewById(R.id.group_waitapply_layout);
		groupNameText = (TextView)findViewById(R.id.group_name_text);
		groupWaitNumberText = (TextView)findViewById(R.id.group_waitapply_size);
		grantCheckbox = (CheckBox)findViewById(R.id.checkbox);
		
		groupNameText.setText(clazz.getClassroomName());
		groupWaitNumberText.setText(clazz.getWaitapplynum() + "人");
		grantCheckbox.setChecked(clazz.getIsAuto() == 0 ? false : true);
		grantCheckbox.setOnClickListener(this);
		DisplayImageOptions defaultOptions_group = new DisplayImageOptions.Builder().cacheOnDisc().showStubImage(R.drawable.default_group).showImageForEmptyUri(R.drawable.default_group).showImageOnFail(R.drawable.default_group).build();
		imageLoader_group.displayImage (Consts.SERVER_HOST + clazz.getAvatar(), (ImageView) findViewById(R.id.group_avatar), defaultOptions_group);
		groupWaitapplyLayout.setOnClickListener(this);
		groupAvatarLayout.setOnClickListener(this);
		groupNameLayout.setOnClickListener(this);
		//班级管理者
		if(isLeader) {
			
		}else {
			
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 1) { 
	        if (resultCode == RESULT_OK) {
	        	clazz = (ClassInfoBean)getIntent().getExtras().getSerializable("CLAZZ");
	    		if(clazz != null) {
	    			groupNameText.setText(clazz.getClassroomName());
	    			groupWaitNumberText.setText(clazz.getWaitapplynum() + "人");
	    			grantCheckbox.setChecked(clazz.getIsAuto() == 0 ? false : true);
	    		}
	        }
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		if(clazz != null)
		bundle.putSerializable("CLAZZ", clazz);
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.checkbox:
			popModifyIfNeedShenHe();
			break;
		case R.id.group_avatar_layout:
			Intent i1 = new Intent(this, ClazzAvatarActivity.class);
			i1.putExtras(bundle);
			startActivity(i1);
			break;
		case R.id.group_name_layout:
			Intent i2 = new Intent(this, ClazzNameActivity.class);
			i2.putExtras(bundle);
			startActivity(i2);
			break;
		case R.id.group_waitapply_layout:
			Intent i3 = new Intent(this, ClazzMemberApplyActivity.class);
			i3.putExtras(bundle);
			startActivity(i3);
			break;
		}
	}
	
	//弹出是否需要审核
	private void popModifyIfNeedShenHe() {
		String content = "";
		if(clazz == null)return;
		if(clazz.getIsAuto() == 0) {
			content = "目前加入本群不需要审核，设置为需要审核？";
		}else {
			content = "目前加入本群需要审核，取消审核？";
		}
		dialog = new MyCommonDialog(this, "提示消息", content, "取消", "确定");
		dialog.setCancelListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
				grantCheckbox.setChecked(clazz.getIsAuto() == 1);
			}
		});
		dialog.setOkListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setClassroomIsAutoApprove(clazz.getIsAuto()==0?1:0);
			}
		});
		dialog.show();
	}
		
	private void setClassroomIsAutoApprove(final int isAuto) {
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
		ProgressDialogUtils.showProgressDialog("", this, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "setClassroomIsAutoApprove");
		params.put("classroomId", String.valueOf(clazz.getClassroomId()));
		params.put("isAuto", String.valueOf(isAuto));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					UIUtilities.showToast(ClazzManageActivity.this, "修改成功");
					clazz.setIsAuto(isAuto);
					if(clazz.getIsAuto() == 0) {
						grantCheckbox.setChecked(false);
					}else {
						grantCheckbox.setChecked(true);
					}
				} 
				else {
					StatusUtils.handleStatus(response, ClazzManageActivity.this);
					grantCheckbox.setChecked(clazz.getIsAuto() == 1);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, ClazzManageActivity.this);
				grantCheckbox.setChecked(clazz.getIsAuto() == 1);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
}
