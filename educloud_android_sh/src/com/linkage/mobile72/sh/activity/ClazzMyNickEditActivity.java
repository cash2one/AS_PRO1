package com.linkage.mobile72.sh.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
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
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.Consts;

public class ClazzMyNickEditActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = ClazzMyNickEditActivity.class.getSimpleName();
	
	private ClassInfoBean clazz;
	private Button back, submit;
	private EditText myNickName;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_clazz_my_nick_edit);
		setTitle(R.string.title_clazz_my_nick_edit);
		clazz = (ClassInfoBean)getIntent().getExtras().getSerializable("CLAZZ");
		if(clazz == null) {
			finish();
			return;
		}
		back = (Button)findViewById(R.id.back);
		submit = (Button)findViewById(R.id.submit);
		myNickName = (EditText)findViewById(R.id.my_nick);
		if(clazz.getMycard() != null)
			myNickName.setText(clazz.getMycard());
		CharSequence text = myNickName.getText();
		if (text instanceof Spannable) {
			Spannable spanText = (Spannable)text;
			Selection.setSelection(spanText, text.length());
		}
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
			if(StringUtils.isEmpty(myNickName.getText().toString())) {
				UIUtilities.showToast(this, "不能提交空的名片");
			}else if(myNickName.getText().toString().trim().equals(clazz.getMycard())) {
				UIUtilities.showToast(this, "昵称没有变化");
			}else {
				submitNick();
			}
			break;
		}
	}
	
	public void submitNick() {
		ProgressDialogUtils.showProgressDialog("", this, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "updateClassroomVisitCard");
		params.put("classroomId", String.valueOf(clazz.getClassroomId()));
		params.put("visitCard", myNickName.getText().toString().trim());
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					clazz.setMycard(myNickName.getText().toString());
					UIUtilities.showToast(ClazzMyNickEditActivity.this, "修改成功");
					Intent intent = new Intent();
    				Bundle bundle = new Bundle();
					bundle.putSerializable("CLAZZ", clazz);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				} 
				else {
					StatusUtils.handleStatus(response, ClazzMyNickEditActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, ClazzMyNickEditActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		
	}
}
