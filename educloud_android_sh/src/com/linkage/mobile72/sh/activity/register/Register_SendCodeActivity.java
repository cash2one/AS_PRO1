package com.linkage.mobile72.sh.activity.register;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;

/**
 * 注册界面
 * @author Yao
 *
 */
public class Register_SendCodeActivity extends BaseActivity {
	
	private static final String TAG = Register_SendCodeActivity.class.getSimpleName();
	public static Register_SendCodeActivity instance;
	
	private TextView title;
	private Button back, getCode, validateCode;
	private EditText inputPhone, inputCode;
	private String accountPhone;
	private MyCommonDialog dialog;
	private ImageView phoneNumberImage, codeImage;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstance);
		instance = this;
		setContentView(R.layout.activity_register_sendcode);
		back = (Button)findViewById(R.id.back);
		getCode = (Button)findViewById(R.id.get_code);
		validateCode = (Button)findViewById(R.id.submit);
		inputPhone = (EditText)findViewById(R.id.input_phone);
		inputCode = (EditText)findViewById(R.id.input_code);
		phoneNumberImage = (ImageView) findViewById(R.id.image1);
		codeImage = (ImageView) findViewById(R.id.image2);
		inputPhone.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
//					account_image.setImageResource(R.drawable.icon_account_focus);
					phoneNumberImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_phone_focus));
				}else {
//					account_image.setImageResource(R.drawable.icon_account);
					phoneNumberImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_phone));
				}
			}
		});
		inputCode.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
//					password_image.setImageResource(R.drawable.icon_password_focus);
					codeImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_password_focus));
				}else {
//					password_image.setImageResource(R.drawable.icon_password);
					codeImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_password));
				}
			}
		});
		setTitle(R.string.title_register);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		accountPhone = getIntent().getStringExtra("PHONE");
		
		if(accountPhone != null && StringUtils.isMobileNO(accountPhone)) {
			inputPhone.setText(accountPhone);
			inputPhone.setEnabled(false);
		}
		
		getCode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String phone = inputPhone.getText().toString().trim();
				if(StringUtils.isEmpty(phone)) {
					dialog = new MyCommonDialog(instance, "提示消息", "手机号不可为空", null, "好");
					dialog.setCancelable(true);
					dialog.setOkListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(dialog.isShowing()) {
//								sendSmsCode(phone);
								dialog.dismiss();
							}
						}
					});
					if(!isFinishing())dialog.show();
				}else if(!StringUtils.isMobileNO(phone)){
					dialog = new MyCommonDialog(instance, "提示消息", "手机号输入不正确", null, "好");
					dialog.setCancelable(true);
					dialog.setOkListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(dialog.isShowing()) {
//								sendSmsCode(phone);
								dialog.dismiss();
							}
						}
					});
					if(!isFinishing())dialog.show();
				}else {
					getCode();
				}
			}
		});
		validateCode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String phone = inputPhone.getText().toString().trim();
				final String code = inputCode.getText().toString().trim();
				if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code) || StringUtils.isEmpty(accountPhone)) {
					dialog = new MyCommonDialog(instance, "提示消息", "您还没有获取验证码", null, "好");
					dialog.setCancelable(true);
					dialog.setOkListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(dialog.isShowing()) {
//								sendSmsCode(phone);
								dialog.dismiss();
							}
						}
					});
					if(!isFinishing())dialog.show();
				}else {
					validateCode();
				}
			}
		});
	}
	
	public void getCode() {
		ProgressDialogUtils.showProgressDialog("正在获取验证码", this, false);
//		getCode.setEnabled(false);
		HashMap<String, String> params = new HashMap<String, String>();
		final String account = inputPhone.getEditableText().toString();
		params.put("commandtype", "sendSMSCode");
		params.put("account", account);
		params.put("smsType", String.valueOf(2));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_sendSMSCode, Request.Method.POST, params, false, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
//				getCode.setEnabled(true);
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					accountPhone = account;
					startTimeThread();
					UIUtilities.showToast(instance, "验证码发送成功");
				}else if(response.optInt("ret") == 1) {
					dialog = new MyCommonDialog(instance, "提示消息", "您的号码已经注册，请重新登录", null, "去登录");
					dialog.setCancelable(false);
					dialog.setOkListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(dialog.isShowing()) {
								dialog.dismiss();
								finish();
							}
						}
					});
					if(!isFinishing())dialog.show();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
//				getCode.setEnabled(true);
				StatusUtils.handleError(arg0, instance);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		
	}
	
	 private static final int MSGWHAT_UPDATE_TIME = 55555;
	    
	    private Thread t;
	    
	    //30秒
	    private int timeCount = 60;
	    
	    @SuppressLint("HandlerLeak")
	    private Handler objHandler = new Handler()
	    {
	        @Override
	        public void handleMessage(Message msg)
	        {
	            switch (msg.what)
	            {
	                case MSGWHAT_UPDATE_TIME:
	                {
	                    if (timeCount >= 0)
	                    {
	                        getCode.setText(timeCount + " 秒");
	                        timeCount--;
	                    }
	                    else
	                    {
	                        getCode.setText("获取验证码");
	                        getCode.setClickable(true);
	                        t.interrupt();
	                    }
	                }
	            }
	        }
	    };
	    
	    public class MyThread implements Runnable
	    { // thread  
	        @Override
	        public void run()
	        {
	            try
	            {
	                while (true)
	                {
	                    Thread.sleep(1000);
	                    objHandler.sendEmptyMessage(MSGWHAT_UPDATE_TIME);
	                }
	            }
	            catch (Exception e)
	            {
	            }
	        }
	    }
	    
	    /**
	     * 
	     * 开始倒计时
	     * [功能详细描述]
	     */
	    private void startTimeThread()
	    {
	        getCode.setClickable(false);
	        timeCount = 60;
	        t = new Thread(new MyThread());
	        t.start();
	    }
	
	public void validateCode() {
		ProgressDialogUtils.showProgressDialog("正在验证", this, false);
		getCode.setEnabled(false);
		HashMap<String, String> params = new HashMap<String, String>();
		final String smsCode = inputCode.getEditableText().toString();
		params.put("commandtype", "checkSMSCode");
		params.put("account", accountPhone);
		params.put("smsType", String.valueOf(2));
		params.put("smsCode", smsCode);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, false, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				getCode.setEnabled(true);
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					Intent intent = new Intent(instance, InputPasswordActivity.class);
					intent.putExtra("PHONE", accountPhone);
					intent.putExtra("SMSCODE", smsCode);
					intent.putExtra("FROM", 2);//注册是2
					startActivity(intent);
				}else {
					UIUtilities.showToast(Register_SendCodeActivity.this, response.optString("msg"));
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				getCode.setEnabled(true);
				StatusUtils.handleError(arg0, instance);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
}
