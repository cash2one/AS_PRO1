package com.linkage.mobile72.sh.activity.register;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.LoginActivity;
import com.linkage.mobile72.sh.activity.MainActivity;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.utils.Utilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;


public class Validate_SmsActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = Validate_SmsActivity.class.getSimpleName();
	public static Validate_SmsActivity instance;
	
	private Button back;
	private Button regist;
	private EditText input_Sms_Code,input_name,input_password,input_password2;
	private MyCommonDialog dialog;
	private String code,name,phone,password,password2;
	private TextView input_username_words,not_receive;
	
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstance);
		instance = this;
		setContentView(R.layout.validate_sms);
		
		back = (Button)findViewById(R.id.back);
		regist = (Button)findViewById(R.id.regist);
		input_Sms_Code = (EditText)findViewById(R.id.input_sms_code);
		input_name = (EditText)findViewById(R.id.input_name);
		input_password = (EditText)findViewById(R.id.input_password);
		input_password2 = (EditText)findViewById(R.id.input_password2);
		input_username_words = (TextView) findViewById(R.id.input_username_words);
		not_receive = (TextView) findViewById(R.id.not_receive);
		setTitle(R.string.fill_sms_code);
		
		Intent intent = getIntent();
		if(intent == null) {
			finish();
		}
		phone = intent.getStringExtra("PHONE");
		input_username_words.setText("验证码已发送至("+phone+")手机上，请查收");
		back.setOnClickListener(this);
		regist.setOnClickListener(this);
		not_receive.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.not_receive:
//			UIUtilities.showToast(getApplicationContext(), "验证码发送成功");
			getCode();
			break;

		case R.id.regist:
			 code = input_Sms_Code.getText().toString();
			 name = input_name.getText().toString();
			 password = input_password.getText().toString();
			 password2 = input_password2.getText().toString();
			if(StringUtils.isEmpty(code)) {
				dialog = new MyCommonDialog(Validate_SmsActivity.this, "提示消息", "验证码不可为空", null, "知道了");
				dialog.setCancelable(true);
				dialog.setOkListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(dialog.isShowing()) {
							dialog.dismiss();
							
						}
					}
				});
				if(!isFinishing())dialog.show();
			}else if (StringUtils.isEmpty(name)) {
				dialog = new MyCommonDialog(Validate_SmsActivity.this, "提示消息", "用户昵称不可为空", null, "知道了");
				dialog.setCancelable(true);
				dialog.setOkListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(dialog.isShowing()) {
							dialog.dismiss();
							
						}
					}
				});
				if(!isFinishing())dialog.show();
			}else if (StringUtils.isEmpty(password)) {
				dialog = new MyCommonDialog(Validate_SmsActivity.this, "提示消息", "密码不可为空", null, "知道了");
				dialog.setCancelable(true);
				dialog.setOkListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(dialog.isShowing()) {
							dialog.dismiss();
							
						}
					}
				});
				if(!isFinishing())dialog.show();
				
			}else if (StringUtils.isEmpty(password2)) {
				dialog = new MyCommonDialog(Validate_SmsActivity.this, "提示消息", "确认密码不可为空", null, "知道了");
				dialog.setCancelable(true);
				dialog.setOkListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(dialog.isShowing()) {
							dialog.dismiss();
							
						}
					}
				});
				if(!isFinishing())dialog.show();
			}else if (!password.equals(password2)) {
				dialog = new MyCommonDialog(Validate_SmsActivity.this, "提示消息", "密码和确认密码必须一致", null, "知道了");
				dialog.setCancelable(true);
				dialog.setOkListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(dialog.isShowing()) {
							dialog.dismiss();
							
						}
					}
				});
				if(!isFinishing())dialog.show();
			}else {
//				startActivity(new Intent(Validate_SmsActivity.this,MainActivity.class));
//				finish();
				register();
			}
			break;
			
		}
	}
	
	public void getCode() {
		ProgressDialogUtils.showProgressDialog("获取中", this, false);
		not_receive.setEnabled(false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("account", phone);
		params.put("smsType", String.valueOf(2));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_GetSMSCode, Request.Method.POST, params, false, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				not_receive.setEnabled(true);
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					// TODO 登录成功后的帐号更新等
					UIUtilities.showToast(getApplicationContext(), "验证码发送成功");
					
				} 
				else {
					StatusUtils.handleStatus(response, Validate_SmsActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				not_receive.setEnabled(true);
				StatusUtils.handleError(arg0, Validate_SmsActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		
	}
	
	void register()
	{

		try {
			ProgressDialogUtils.showProgressDialog("认证中", this, false);
			HashMap<String, String> params = new HashMap<String, String>();
			regist.setEnabled(false);
			params.put("account", phone);
			params.put("password", password);
			params.put("nickname", input_name.getText().toString());
			params.put("smsCode", input_Sms_Code.getText().toString());
			String IMEI = Utilities.getIMEI(this);
			if (IMEI == null) {
				IMEI = android.os.Build.MODEL;
				// IMEI = Utilities.getMacAddress(this);
			}
			if (IMEI == null) {
				IMEI = "";
			}
			params.put("term_manufacturer", "android,"+IMEI);

			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_Register, Request.Method.POST, params, false, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					ProgressDialogUtils.dismissProgressBar();

					regist.setEnabled(true);
					System.out.println("response=" + response);
					if (response.optInt("ret") == 0) {
						// TODO 登录成功后的帐号更新等
/*						AccountData user = AccountData.parseFromJson(response.optJSONObject("data"));
						if(user ==null || "".equalsIgnoreCase(user.getToken()))
						{
							StatusUtils.handleOtherError("注册失败：userInfo为空", Validate_SmsActivity.this);
							return;
						}
						onLoginSuccess( user);*/
						UIUtilities.showToast(Validate_SmsActivity.this, "注册成功，请重新登录");
						Intent i = new Intent(Validate_SmsActivity.this, LoginActivity.class);
						startActivity(i);
						finish();
					} 
					else if(response.optInt("ret") == 1) {
					
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleStatus(response, Validate_SmsActivity.this);
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					regist.setEnabled(true);
					ProgressDialogUtils.dismissProgressBar();
					StatusUtils.handleError(arg0, Validate_SmsActivity.this);
				}
			});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
			regist.setEnabled(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	}
	
	protected void onLoginSuccess( AccountData user) {
		
		DataHelper helper = getDBHelper();
		try {
			
		//初始化用户数据
		user.setLoginname(phone);
		user.setLoginpwd(password);
		user.setDefaultUser(1);
		helper.getAccountDao().updateRaw("update AccountData set defaultUser = 0");
		helper.getAccountDao().createOrUpdate(user);
		
		mApp.notifyAccountChanged();
		
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StatusUtils.handleOtherError("网络通讯异常", Validate_SmsActivity.this);
		} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StatusUtils.handleOtherError("网络通讯异常", Validate_SmsActivity.this);
		}

		
		
	}
}
