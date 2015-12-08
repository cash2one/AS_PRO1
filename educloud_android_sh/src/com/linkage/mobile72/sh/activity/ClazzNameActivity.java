package com.linkage.mobile72.sh.activity;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.http.ParamItem;
import com.linkage.mobile72.sh.http.WDJsonObjectMultipartRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.Consts;

public class ClazzNameActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = ClazzNameActivity.class.getSimpleName();
	
	private ClassInfoBean clazz;
	private EditText clazzName;
	private Button back,submit;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clazz_name);
		setTitle(R.string.title_clazz_name);
		
		clazz = (ClassInfoBean)getIntent().getExtras().getSerializable("CLAZZ");
		if(clazz == null) {
			finish();
		}
		back = (Button)findViewById(R.id.back);
		submit = (Button)findViewById(R.id.submit);
		clazzName = (EditText)findViewById(R.id.clazz_name);
		clazzName.setText(clazz.getClassroomName());
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.submit:
			if(StringUtils.isEmpty(clazzName.getText().toString())) {
				UIUtilities.showToast(this, "请输入班级名称");
			}else {
				sumbitAccountInfo();
			}
			break;
		}
	}
	
    private void sumbitAccountInfo() {
    	ProgressDialogUtils.showProgressDialog("正在提交，请稍候", this, false);
    	List<ParamItem> params = new ArrayList<ParamItem>();
    	params.add(new ParamItem("commandtype", "updateClassroomInfo", ParamItem.TYPE_TEXT));
    	params.add(new ParamItem("classroomId", String.valueOf(clazz.getClassroomId()), ParamItem.TYPE_TEXT));
    	params.add(new ParamItem("description", clazz.getDescription(), ParamItem.TYPE_TEXT));
    	params.add(new ParamItem("classroomName", clazzName.getText().toString(), ParamItem.TYPE_TEXT));
		params.add(new ParamItem("fileupload", "", ParamItem.TYPE_FILE));
    	WDJsonObjectMultipartRequest mRequest = new WDJsonObjectMultipartRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
    		@Override
    		public void onResponse(JSONObject response) {
    			ProgressDialogUtils.dismissProgressBar();
    			System.out.println("response=" + response);
    			if (response.optInt("ret") == 0) {
    				clazz.setClassroomName(clazzName.getText().toString());
    				clazz.setDescription(clazz.getDescription());
					UIUtilities.showToast(ClazzNameActivity.this, "修改成功");
					Intent intent = new Intent();
    				Bundle bundle = new Bundle();
					bundle.putSerializable("CLAZZ", clazz);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
    			}else {
    				StatusUtils.handleStatus(response, ClazzNameActivity.this);
    			}
    		}
    	}, new Response.ErrorListener() {
    		@Override
    		public void onErrorResponse(VolleyError arg0) {
    			ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, ClazzNameActivity.this);
    		}
    	});
    	BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
}
