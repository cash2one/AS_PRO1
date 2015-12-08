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
public class Reset_SendCodeActivity extends BaseActivity {
	
	private static final String TAG = Reset_SendCodeActivity.class.getSimpleName();
	public static Reset_SendCodeActivity instance;
	
	private Button back, getCode, validateCode;
	private TextView tip,tip1,tip2;
	private EditText inputPhone, inputCode;
	private String accountPhone;
	private MyCommonDialog dialog;
	private ImageView phoneNumberImage, codeImage;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstance);
		instance = this;
		setContentView(R.layout.activity_reset_sendcode);
		back = (Button)findViewById(R.id.back);
		getCode = (Button)findViewById(R.id.get_code);
		tip = (TextView)findViewById(R.id.find_pw_tip);
        tip1 = (TextView)findViewById(R.id.find_pw_tip_1);
        tip2 = (TextView)findViewById(R.id.find_pw_tip_2);
        tip.setVisibility(View.INVISIBLE);
        tip1.setVisibility(View.INVISIBLE);
        tip2.setVisibility(View.INVISIBLE);
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
		setTitle("找回密码");
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		getCode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String phone = inputPhone.getText().toString().trim();
				if(phone.length() != 11) {
					dialog = new MyCommonDialog(instance, "提示消息", "请输入正确的帐号", null, "好");
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
		params.put("smsType", String.valueOf(1));
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
				}else {
					UIUtilities.showToast(instance, response.optString("msg"));
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
                        tip.setVisibility(View.VISIBLE);
                        tip1.setVisibility(View.VISIBLE);
                        tip2.setVisibility(View.VISIBLE);
                        tip1.setText(timeCount+"");
                        timeCount--;
                    }
                    else
                    {
                        tip.setVisibility(View.INVISIBLE);
                        tip1.setVisibility(View.INVISIBLE);
                        tip2.setVisibility(View.INVISIBLE);
                        getCode.setText("获取验证码");
                        getCode.setClickable(true);
                        getCode.setBackgroundColor(getResources().getColor(R.color.reset_pw_click));
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
		timeCount = -1;
		HashMap<String, String> params = new HashMap<String, String>();
		final String smsCode = inputCode.getEditableText().toString();
		params.put("commandtype", "checkSMSCode");
		params.put("account", accountPhone);
		params.put("smsType", String.valueOf(1));
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
					intent.putExtra("FROM", 3);//重置密码是3
					startActivity(intent);
				}else {
					if(response.optString("msg")!=null || "".equalsIgnoreCase( response.optString("msg")))
					UIUtilities.showToast(instance, response.optString("msg"));
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
