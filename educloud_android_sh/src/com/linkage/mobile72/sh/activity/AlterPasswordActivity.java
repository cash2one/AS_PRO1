package com.linkage.mobile72.sh.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.register.Register_SendCodeActivity;
import com.linkage.mobile72.sh.activity.register.Validate_SmsActivity;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.utils.Utilities;
import com.linkage.mobile72.sh.Consts;

public class AlterPasswordActivity extends BaseActivity implements OnClickListener{
	
	public static AlterPasswordActivity instance;
	private static final String TAG = AlterPasswordActivity.class.getSimpleName();
	private Button back;
	private Button enter;
	private EditText inputSmsCode;
	private EditText inputPassword;
	public String mAccountName;
	private EditText verifyPassword;
	private TextView notReceiveSmsCode,inputUserSmsCode;
	private String mSmsCode,mPassword,mVerifyPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_alterpsw);
		mAccountName = getIntent().getStringExtra("PHONE");
		if(mAccountName == null || "".equals(mAccountName)) {
			finish();
		}
		back = (Button)findViewById(R.id.back);
		enter = (Button)findViewById(R.id.enter_system);
		inputSmsCode = (EditText)findViewById(R.id.input_sms_code);
		inputPassword = (EditText)findViewById(R.id.input_password);
		verifyPassword = (EditText)findViewById(R.id.verify_password);
		notReceiveSmsCode = (TextView)findViewById(R.id.not_receive);
		inputUserSmsCode = (TextView)findViewById(R.id.input_username_words);
		
		back.setOnClickListener(this);
		enter.setOnClickListener(this);
		notReceiveSmsCode.setOnClickListener(this);
		inputUserSmsCode.setText("验证码已发送至("+mAccountName+")手机上，请查收");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.enter_system:
				mSmsCode = inputSmsCode.getEditableText().toString();
				mPassword = inputPassword.getEditableText().toString();
				mVerifyPassword = verifyPassword.getEditableText().toString();
				if (TextUtils.isEmpty(mSmsCode)) {
					UIUtilities.showToast(this, R.string.smscode_null);
				}else if(TextUtils.isEmpty(mPassword)){
					UIUtilities.showToast(this, R.string.password_null);
				}else if(TextUtils.isEmpty(mVerifyPassword)){
					UIUtilities.showToast(this, R.string.verifypassword_null);
				}else if(!mPassword.equals(mVerifyPassword)){
					UIUtilities.showToast(this, R.string.password_inconformity);
				}else {
					doLogin();
//					Intent i = new Intent(this, MainActivity.class);
//					startActivity(i);
				}			
				break;
			case R.id.not_receive:
				getCode();
				
				break;
		}
	}
	
	
	private void doLogin()
	{
		try {
			ProgressDialogUtils.showProgressDialog("正在和服务器通讯中", this, false);
			enter.setEnabled(false);
			HashMap<String, String> params = new HashMap<String, String>();
		
			params.put("account", mAccountName);
			params.put("password", inputPassword.getText().toString());
			params.put("smsCode", inputSmsCode.getText().toString());
			String IMEI = Utilities.getIMEI(this);
			if (IMEI == null) {
				IMEI = android.os.Build.MODEL;
				// IMEI = Utilities.getMacAddress(this);
			}
			if (IMEI == null) {
				IMEI = "";
			}
			params.put("term_manufacturer", "android,"+IMEI);

			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_EnteringPerson_Register, Request.Method.POST, params, false, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					ProgressDialogUtils.dismissProgressBar();

					enter.setEnabled(true);
					System.out.println("response=" + response);
					if (response.optInt("ret") == 0) {
						// TODO 登录成功后的帐号更新等
					
						

						AccountData user = AccountData.parseFromJson(response.optJSONObject("data"));
						
						
						if(user ==null || "".equalsIgnoreCase(user.getToken()))
						{
							StatusUtils.handleOtherError("登录失败：userInfo为空", AlterPasswordActivity.this);
							return;
						}
						
						
						
							onLoginSuccess( user);
						
					} 
					else if(response.optInt("ret") == 1) {
						
//						getCode();
					}else {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleStatus(response, AlterPasswordActivity.this);
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					enter.setEnabled(true);
					ProgressDialogUtils.dismissProgressBar();
					StatusUtils.handleError(arg0, AlterPasswordActivity.this);
				}
			});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
			enter.setEnabled(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	public void getCode() {
		ProgressDialogUtils.showProgressDialog("获取中", this, false);
		notReceiveSmsCode.setEnabled(false);
		HashMap<String, String> params = new HashMap<String, String>();
//		final String account = inputPhone.getEditableText().toString();
		params.put("account", mAccountName);
		params.put("smsType", String.valueOf(2));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_GetSMSCode, Request.Method.POST, params, false, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				notReceiveSmsCode.setEnabled(true);
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					// TODO 登录成功后的帐号更新等
//					AccountData user = AccountData.parseFromJson(response.optJSONObject("userInfo"));
					
//					if(user ==null || "".equalsIgnoreCase(user.getToken()))
//					{
//						StatusUtils.handleOtherError("注册失败：userInfo为空", Register_SendCodeActivity.this);
//						return;
//					}
					
//					Intent intent = new Intent(AlterPasswordActivity.this, Validate_SmsActivity.class);
//					intent.putExtra("PHONE", account);
//					startActivity(intent);
					
				} 
				else {
					StatusUtils.handleStatus(response, AlterPasswordActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				notReceiveSmsCode.setEnabled(true);
				StatusUtils.handleError(arg0, AlterPasswordActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		
	}

	protected void onLoginSuccess( AccountData user) {
		
		DataHelper helper = getDBHelper();
		try {
			
		//初始化用户数据
		user.setLoginname(mAccountName);
		user.setLoginpwd(inputPassword.getText().toString());
		user.setDefaultUser(1);
		helper.getAccountDao().updateRaw("update AccountData set defaultUser = 0");
		helper.getAccountDao().createOrUpdate(user);
		mApp.notifyAccountChanged();
		
		
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StatusUtils.handleOtherError("网络通讯异常", AlterPasswordActivity.this);
		} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StatusUtils.handleOtherError("网络通讯异常", AlterPasswordActivity.this);
		}

		
		
	}
}
