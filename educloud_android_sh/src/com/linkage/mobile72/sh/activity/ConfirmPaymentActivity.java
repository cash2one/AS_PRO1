package com.linkage.mobile72.sh.activity;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.AppBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;

public class ConfirmPaymentActivity extends BaseActivity
{
    private static final String TAG = ConfirmPaymentActivity.class.getSimpleName();
    public static ConfirmPaymentActivity instance;
    
    private Button back, getCode, validateCode;
    private TextView goodsname,ordertypename;
    private EditText inputPhone, inputCode;
    private MyCommonDialog dialog;
    private ImageView phoneNumberImage, codeImage;
    private AppBean app;
    private CheckBox checkBox;
    
    @Override
    protected void onCreate(Bundle savedInstance) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstance);
        instance = this;
        app = (AppBean) getIntent().getSerializableExtra("APP");
        if (app == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_confirm_payment);
        back = (Button)findViewById(R.id.back);
        getCode = (Button)findViewById(R.id.get_code);
        
        ((TextView)findViewById(R.id.top_price)).setText("￥"+app.getAppPrice_me());
        goodsname = (TextView)findViewById(R.id.top_goods_name);
//        collectname  = (TextView)findViewById(R.id.top_collect_name);
        ordertypename = (TextView)findViewById(R.id.top_ordertype_name);
        
        
        goodsname.setText(app.getAppName());
//        collectname.setText(app.getAppName());
        ordertypename.setText(app.getPrice_type().equals("1") ? "点播":"包月");
        
//        tip.setVisibility(View.INVISIBLE);
//        tip1.setVisibility(View.INVISIBLE);
//        tip2.setVisibility(View.INVISIBLE);
        validateCode = (Button)findViewById(R.id.submit);
        inputPhone = (EditText)findViewById(R.id.input_phone);
        inputCode = (EditText)findViewById(R.id.input_code);
        phoneNumberImage = (ImageView) findViewById(R.id.image1);
        codeImage = (ImageView) findViewById(R.id.image2);
        if(getCurAccount() != null && StringUtils.isMobileNO(getCurAccount().getLoginname())) {
        	inputPhone.setText(getCurAccount().getLoginname());
        }
        inputPhone.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
//                  account_image.setImageResource(R.drawable.icon_account_focus);
                    phoneNumberImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_phone_focus));
                }else {
//                  account_image.setImageResource(R.drawable.icon_account);
                    phoneNumberImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_phone));
                }
            }
        });
        inputCode.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
//                  password_image.setImageResource(R.drawable.icon_password_focus);
                    codeImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_password_focus));
                }else {
//                  password_image.setImageResource(R.drawable.icon_password);
                    codeImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_password));
                }
            }
        });
        setTitle("确认支付");
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
//                              sendSmsCode(phone);
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
                if(!checkBox.isChecked())
                {
                    showTip();
                    return;
                }
                final String phone = inputPhone.getText().toString().trim();
                final String code = inputCode.getText().toString().trim();
                if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
                    dialog = new MyCommonDialog(instance, "提示消息", "请输入手机号码和验证码", null, "好");
                    dialog.setCancelable(true);
                    dialog.setOkListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(dialog.isShowing()) {
//                              sendSmsCode(phone);
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
        
        checkBox = (CheckBox)findViewById(R.id.layout_end_1);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                // TODO Auto-generated method stub
                if(!isChecked)
                {
                    showTip();
                }
            }
        });
        
        findViewById(R.id.layout_end_3).setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Intent mIntent = new Intent(ConfirmPaymentActivity.this, WebViewActivity.class);
                mIntent.putExtra(WebViewActivity.KEY_URL, Consts.RIGHT_RESERVE);
                mIntent.putExtra(WebViewActivity.KEY_TITLE, "免责声明");
                
                startActivity(mIntent);
                checkBox.setChecked(true);
            }
        });
    }
    
    private void showTip()
    {
        dialog = new MyCommonDialog(instance, "提示消息", "您未阅读免责声明，请阅读。", null, "好");
        dialog.setCancelable(true);
        dialog.setOkListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
    
    public void getCode() {
        ProgressDialogUtils.showProgressDialog("正在获取验证码", this, false);
        getCode.setEnabled(false);
        HashMap<String, String> params = new HashMap<String, String>();
        final String account = inputPhone.getEditableText().toString();
        params.put("commandtype", "sendPayCode");
        params.put("id", String.valueOf(app.getId()));
        params.put("phone",account);
        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_sendPayCode, 
                Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ProgressDialogUtils.dismissProgressBar();
                getCode.setEnabled(true);
                System.out.println("response=" + response);
                if (response.optInt("ret") == 0) {
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
                getCode.setEnabled(true);
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
//                        tip.setVisibility(View.VISIBLE);
//                        tip1.setVisibility(View.VISIBLE);
//                        tip2.setVisibility(View.VISIBLE);
                        getCode.setText(timeCount+"");
                        timeCount--;
                    }
                    else
                    {
//                        tip.setVisibility(View.INVISIBLE);
//                        tip1.setVisibility(View.INVISIBLE);
//                        tip2.setVisibility(View.INVISIBLE);
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
    
    /**
     * 支付
     */
    public void validateCode() {
        ProgressDialogUtils.showProgressDialog("正在支付", this, false);
        validateCode.setEnabled(false);
        timeCount = -1;
        HashMap<String, String> params = new HashMap<String, String>();
        final String phone = inputPhone.getText().toString().trim();
        final String code = inputCode.getText().toString().trim();
        params.put("commandtype", "appPay");
        params.put("phone", phone);
        params.put("id", String.valueOf(app.getId()));
        params.put("sms_code", code);
        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_appPay, 
                Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ProgressDialogUtils.dismissProgressBar();
                validateCode.setEnabled(true);
                System.out.println("response=" + response);
                if (response.optInt("ret") == 0) {
//                    Intent intent = new Intent(instance, InputPasswordActivity.class);
//                    intent.putExtra("PHONE", accountPhone);
//                    intent.putExtra("SMSCODE", smsCode);
//                    intent.putExtra("FROM", 3);//重置密码是3
//                    startActivity(intent);
                    String openid = response.optString("openid");
                    UIUtilities.showToast(instance, "支付成功");
                    finish();
                }else {
                    if(response.optString("msg")!=null || "".equalsIgnoreCase( response.optString("msg")))
                    UIUtilities.showToast(instance, response.optString("msg"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                ProgressDialogUtils.dismissProgressBar();
                validateCode.setEnabled(true);
                StatusUtils.handleError(arg0, instance);
            }
        });
        BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        BaseApplication.getInstance().cancelPendingRequests(TAG);
        
    }
}
