package com.linkage.mobile72.sh.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;

import org.json.JSONObject;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.JxTemplate;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.view.View.OnClickListener;

public class JxMbDetailActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = JxMbDetailActivity.class.getName();
	private Button set;
	private EditText message;
	private TextView titleText;
	private MyCommonDialog dialog;
	private JxTemplate jxtemplate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mb_detail);
		setTitle("模板详情");
		findViewById(R.id.back).setOnClickListener(this);
		set = (Button) findViewById(R.id.set);
		set.setText("删除");
		set.setVisibility(View.VISIBLE);
		set.setOnClickListener(this);

		jxtemplate = (JxTemplate) getIntent().getExtras().getSerializable("TEMPLATE");
		if (jxtemplate == null) {
			finish();
		}
		titleText = (TextView) findViewById(R.id.title_text);
		titleText.setText(jxtemplate.getTitle());
		message = (EditText) findViewById(R.id.message);
		titleText.setText(jxtemplate.getTitle());
		message.setText(jxtemplate.getText());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.set:
			dialog = new MyCommonDialog(JxMbDetailActivity.this, "提示消息", "确认删除此模板吗？", "取消", "确定");
			dialog.setOkListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ProgressDialogUtils.showProgressDialog("正在删除", JxMbDetailActivity.this, false);
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("commandtype", "userTemplateDelete");
					params.put("id", jxtemplate.getId() + "");

					WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
							Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									ProgressDialogUtils.dismissProgressBar();
									if (response.optInt("ret", -1) == 0) {
										StatusUtils.handleOtherError("删除模板成功",
												JxMbDetailActivity.this);
										setResult(RESULT_OK);
										finish();
									} else {
										StatusUtils.handleStatus(response, JxMbDetailActivity.this);
									}
								}
							}, new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError arg0) {
									ProgressDialogUtils.dismissProgressBar();
									StatusUtils.handleError(arg0, JxMbDetailActivity.this);
								}
							});
					BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
				}
			});
			dialog.setCancelListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (dialog.isShowing())
						dialog.dismiss();
				}
			});
			dialog.show();
			// if(set.getText().toString().equals("编辑")){
			// set.setText("完成");
			// titleEdit.setText(titleText.getText().toString());
			// titleEdit.setVisibility(View.VISIBLE);
			// titleText.setVisibility(View.GONE);
			// message.setEnabled(true);
			// message.setBackgroundResource(R.drawable.main_search_bg);
			// }else{
			// titleText.setText(titleEdit.getText().toString());
			// titleText.setVisibility(View.VISIBLE);
			// titleEdit.setVisibility(View.GONE);
			// message.setEnabled(false);
			// message.setBackgroundColor(0xffffffff);
			// set.setText("编辑");
			// }
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}