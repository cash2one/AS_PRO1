package com.linkage.mobile72.sh.activity.register;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

/**
 * 输入密码界面 （from:注册和忘记密码时获取验证码后的界面）
 * @author Yao
 *
 */
public class InputPasswordActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = InputPasswordActivity.class.getSimpleName();
	
	public static InputPasswordActivity instance;
	private EditText new_password,renew_password;
	private Button submit, back;
	private String phone, code, newPassword, renewPassword;
	private MyCommonDialog dialog;
	private int from;
	private ImageView newPasswordImage, newPasswordImage2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_input_password);
		phone = getIntent().getStringExtra("PHONE");
		code = getIntent().getStringExtra("SMSCODE");
		if(phone == null || code == null) {
			finish();
		}
		from = getIntent().getIntExtra("FROM", 2);
		new_password = (EditText) findViewById(R.id.new_password);
		renew_password = (EditText) findViewById(R.id.renew_password);
		newPasswordImage = (ImageView) findViewById(R.id.image1);
		newPasswordImage2 = (ImageView) findViewById(R.id.image2);
		new_password.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
//					account_image.setImageResource(R.drawable.icon_account_focus);
					newPasswordImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_password_focus));
				}else {
//					account_image.setImageResource(R.drawable.icon_account);
					newPasswordImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_password));
				}
			}
		});
		renew_password.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
//					password_image.setImageResource(R.drawable.icon_password_focus);
					newPasswordImage2.setImageDrawable(getResources().getDrawable(R.drawable.icon_password_focus));
				}else {
//					password_image.setImageResource(R.drawable.icon_password);
					newPasswordImage2.setImageDrawable(getResources().getDrawable(R.drawable.icon_password));
				}
			}
		});
		if(from == 2) {
			setTitle(R.string.title_register);
		}else if(from == 3) {
			setTitle("找回密码");
		}
		submit = (Button) findViewById(R.id.enter_system);
		back = (Button)findViewById(R.id.back);
		submit.setOnClickListener(this);
		back.setOnClickListener(this);
		
	}
	
	public void resetPassword() {
		ProgressDialogUtils.showProgressDialog("认证中", this, false);
		submit.setEnabled(false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "resetPassword");
		params.put("account", phone);
		newPassword = Utilities.md5(newPassword);
		params.put("password", newPassword);
		params.put("smsCode", code);
		params.put("user_type", isTeacher()?"1":"3");
		
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, false, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				submit.setEnabled(true);
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					// TODO 登录成功后的帐号更新等
					/*AccountData user = AccountData.parseFromJson(response.optJSONObject("data"));
					
					if(user ==null || "".equalsIgnoreCase(user.getToken()))
					{
						StatusUtils.handleOtherError("登录失败：userInfo为空", InputPasswordActivity.this);
						return;
					}
					onLoginSuccess( user);*/
					UIUtilities.showToast(InputPasswordActivity.this, "密码重置成功，请重新登录");
					Intent i = new Intent(InputPasswordActivity.this, LoginActivity.class);
					startActivity(i);
					finish();
				} 
				else {
					StatusUtils.handleStatus(response, InputPasswordActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				submit.setEnabled(true);
				StatusUtils.handleError(arg0, InputPasswordActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		
	}
	

	public void register() {
		ProgressDialogUtils.showProgressDialog("认证中", this, false);
		submit.setEnabled(false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "register");
		params.put("account", phone);
		params.put("password", newPassword);
		params.put("user_type", isTeacher()?"1":"3");
		params.put("smsCode", code);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, false, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				submit.setEnabled(true);
				if (response.optInt("ret") == 0) {
					// TODO 登录成功后的帐号更新等
					/*AccountData user = AccountData.parseFromJson(response.optJSONObject("data"));
					
					if(user ==null || "".equalsIgnoreCase(user.getToken()))
					{
						StatusUtils.handleOtherError("登录失败：userInfo为空", InputPasswordActivity.this);
						return;
					}
					
					onLoginSuccess( user);*/
					UIUtilities.showToast(InputPasswordActivity.this, "注册成功，请重新登录");
					Intent i = new Intent(InputPasswordActivity.this, LoginActivity.class);
					startActivity(i);
					finish();
				} 
				else {
					StatusUtils.handleStatus(response, InputPasswordActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				submit.setEnabled(true);
				StatusUtils.handleError(arg0, InputPasswordActivity.this);
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
		case R.id.enter_system:
			newPassword = new_password.getText().toString().trim();
			renewPassword = renew_password.getText().toString().trim();
			if (StringUtils.isEmpty(code)) {
				dialog = new MyCommonDialog(InputPasswordActivity.this, "提示消息", "验证码不可为空", null, "知道了");
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
			}else if (StringUtils.isEmpty(renewPassword)) {
				dialog = new MyCommonDialog(InputPasswordActivity.this, "提示消息", "旧密码不可为空", null, "知道了");
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
			}else if (!newPassword.equals(renewPassword)) {
				dialog = new MyCommonDialog(InputPasswordActivity.this, "提示消息", "两次密码不一致", null, "知道了");
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
			}else{
				if(from == 2) {
					register();
				}else if(from == 3) {
					
					String regex = "^[0-9A-Za-z]{6,16}$";
					if(newPassword.matches(regex))
					{
					resetPassword();
					}else{
						dialog = new MyCommonDialog(InputPasswordActivity.this, "提示消息", "密码6-16位，且只支持数字，大小写字母", null, "知道了");
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
					}
				}
			}
			break;
		}
		
	}

	protected void onLoginSuccess( AccountData user) {
		
		DataHelper helper = getDBHelper();
		try {
			
		//初始化用户数据
		user.setLoginname(phone);
		user.setLoginpwd(new_password.getEditableText().toString());
		user.setDefaultUser(1);
		helper.getAccountDao().updateRaw("update AccountData set defaultUser = 0");
		helper.getAccountDao().createOrUpdate(user);
		
		mApp.notifyAccountChanged();
		Intent i = null;
		if(from == 2) {
			i= new Intent(this, Register_FinishActivity.class);
		}else if(from == 3) {
			i= new Intent(this, MainActivity.class);
		}
		
		
		startActivity(i);
		finish();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StatusUtils.handleOtherError("网络通讯异常", InputPasswordActivity.this);
		} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StatusUtils.handleOtherError("网络通讯异常", InputPasswordActivity.this);
		}

		
		
	}
}
