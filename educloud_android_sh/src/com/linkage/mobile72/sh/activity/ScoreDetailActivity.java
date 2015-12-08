package com.linkage.mobile72.sh.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.JXBean;
import com.linkage.mobile72.sh.data.http.JXBeanDetail;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.Consts;
/**
 * 家长的成绩详情页面
 * @author Yao
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoreDetailActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = ScoreDetailActivity.class.getName();
	
	private JXBean bean; 
	private JXBeanDetail jxbean;//请求接口后返回的详情对象
	private Button back;
	private TextView senderText, sendTimeText;
	private EditText sendContentText;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent == null) {
			finish();
			return;
		}
		bean = (JXBean)intent.getSerializableExtra("jxbean");
		setContentView(R.layout.activity_score_detail);
		setTitle("成绩详情");
		back = (Button)findViewById(R.id.back);
		senderText = (TextView) findViewById(R.id.receiver);
		sendTimeText = (TextView) findViewById(R.id.time);
		sendContentText = (EditText)findViewById(R.id.edit_input);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			sendContentText.setTextIsSelectable(true);
			sendContentText.setKeyListener(null);
		}else{
			sendContentText.setKeyListener(null);
		}
        back.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getDetail();
	}
	
	private void getDetail() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getMessageDetail");
		params.put("studentid", getDefaultAccountChild().getId()+"");
		params.put("id", ""+bean.getId());
		params.put("type", "1");
		params.put("smsMessageType", bean.getSmsMessageType());
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getMessageDetail,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							jxbean = JXBeanDetail.parseFromJson(response.optJSONObject("data"), String.valueOf(Consts.JxhdType.NOTICE));
							fillDataToPage(jxbean);
							
						}else {
							UIUtilities.showToast(ScoreDetailActivity.this, "获取详情失败");
							finish();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, ScoreDetailActivity.this);
						finish();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	private void read(long id) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "messageRead");
		params.put("studentid", getDefaultAccountChild().getId()+"");
		params.put("id", ""+id);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_messageRead,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	private void fillDataToPage(JXBeanDetail jxbean) {
		if(jxbean != null) {
			senderText.setText(jxbean.getSendUserName());
			sendTimeText.setText(jxbean.getSendTime());
			sendContentText.setText(jxbean.getMessageContent());
			//Read Flag
			read(bean.getId());
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}
	
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	BaseApplication.getInstance().cancelPendingRequests(TAG);
    }
}
