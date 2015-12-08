package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.Consts;

public class OpinionActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = OpinionActivity.class.getSimpleName();

	private EditText editText;
	private Button opsubmit, back;
	private TextView countText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opinion);
		setTitle(R.string.title_opinion);
		back = (Button) findViewById(R.id.back);
		editText = (EditText) findViewById(R.id.edt_opinion);
		countText = (TextView) findViewById(R.id.count);
		editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				countText.setText("剩余"+(255-s.length())+"字");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		keyBoard(editText, "open", 300);
		opsubmit = (Button) findViewById(R.id.opsubmit);
		back.setOnClickListener(this);
		opsubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.back:
			keyBoard(editText, "close", 0);
			finish();
			break;
		case R.id.opsubmit:
			String str = editText.getText().toString().trim();
			if(str.length() != 0){
				submit(editText.getText().toString());
			}else{
				UIUtilities.showToast(OpinionActivity.this,
						"意见不可为空！");
			}
			break;
		default:
			break;
		}
	}

	public void submit(String desc) {
		ProgressDialogUtils.showProgressDialog("正在提交，请稍候", this, false);

		HashMap<String,String> params = new HashMap<String, String>();
		params.put("commandtype", "sendMachineRecord");
		params.put("content", desc);
		if (desc != null) {
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
					Consts.SERVER_URL, Request.Method.POST, params, true,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							ProgressDialogUtils.dismissProgressBar();
							System.out.println("response=" + response);
							if (response.optInt("ret") == 0) {
								AccountData currentAccount = BaseApplication
										.getInstance().getDefaultAccount();
								currentAccount.setUserType(-1);
								try {
									getDBHelper().getAccountDao()
											.createOrUpdate(currentAccount);
									mApp.notifyAccountChanged();
								} catch (SQLException e) {
									e.printStackTrace();
									UIUtilities.showToast(OpinionActivity.this,
											"提交意见失败");
								}
								UIUtilities.showToast(OpinionActivity.this,
										"提交意见成功");
								keyBoard(editText, "close", 0);
								finish();
							} else {
								StatusUtils.handleStatus(response,
										OpinionActivity.this);
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							ProgressDialogUtils.dismissProgressBar();
							StatusUtils.handleError(arg0, OpinionActivity.this);
						}
					});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		}
	}

}
