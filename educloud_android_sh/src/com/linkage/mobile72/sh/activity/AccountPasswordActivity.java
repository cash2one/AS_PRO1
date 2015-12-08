package com.linkage.mobile72.sh.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
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
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.utils.Utilities;
import com.linkage.mobile72.sh.Consts;

public class AccountPasswordActivity extends BaseActivity implements OnClickListener {

	private Button back,submit;
	private EditText input_old_passsword,input_new_password,input_re_password;
	private static final String TAG = AccountPasswordActivity.class.getSimpleName();
    private ImageView oldPasswordImage, newPasswordImage, newPasswordImage2;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_password);
		setTitle(R.string.title_account_password);
		
		input_old_passsword = (EditText) findViewById(R.id.input_old_passsword);
		input_new_password = (EditText) findViewById(R.id.input_new_password);
		input_re_password = (EditText) findViewById(R.id.input_re_password);
		oldPasswordImage = (ImageView) findViewById(R.id.image1);
		newPasswordImage = (ImageView) findViewById(R.id.image2);
		newPasswordImage2 = (ImageView) findViewById(R.id.image3);
		input_old_passsword.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
//					account_image.setImageResource(R.drawable.icon_account_focus);
					oldPasswordImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_password_focus));
				}else {
//					account_image.setImageResource(R.drawable.icon_account);
					oldPasswordImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_password));
				}
				
			}
		});
		input_new_password.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
//					password_image.setImageResource(R.drawable.icon_password_focus);
					newPasswordImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_password_focus));
				}else {
//					password_image.setImageResource(R.drawable.icon_password);
					newPasswordImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_password));
				}
			}
		});
		input_re_password.setOnFocusChangeListener(new OnFocusChangeListener() {
			
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
		back = (Button)findViewById(R.id.back);
		submit = (Button)findViewById(R.id.submit);
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
//			UIUtilities.showToast(this, R.string.modify_success);
//			finish();
			changePasswordByServer();
			break;
		default:
			break;
		}
	}
	public void changePasswordByServer() {

		final String oldPassword = input_old_passsword.getEditableText().toString();
		final String newPassword = input_new_password.getEditableText().toString();
		final String rePassword = input_re_password.getEditableText().toString();
		if (TextUtils.isEmpty(oldPassword)) {
			UIUtilities.showToast(this, "请输入旧密码！");
			return;
		}
		if (TextUtils.isEmpty(newPassword)) {
			UIUtilities.showToast(this, "请输入新密码！");
			return;
		}
		if (TextUtils.isEmpty(rePassword)) {
			UIUtilities.showToast(this, "请确认新密码！");
			return;
		}
		if (!rePassword.equals(newPassword)) {
			UIUtilities.showToast(this, "两次填写的密码不一致！");
			return;
		}
		String regex = "^[0-9A-Za-z]{6,16}$";
		if(!newPassword.matches(regex)) {
			UIUtilities.showToast(this, "密码6-16位，且只支持数字，大小写字母");
			return;
		}
		hideKeyboard(input_old_passsword.getWindowToken());
		hideKeyboard(input_new_password.getWindowToken());
		hideKeyboard(input_re_password.getWindowToken());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "updatePassword");
		params.put("oldPassword", Utilities.md5(oldPassword));
		params.put("newPassword", Utilities.md5(newPassword));
		ProgressDialogUtils.showProgressDialog("", this, true);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				submit.setEnabled(true);
				if (response.optInt("ret") == 0) {
					UIUtilities.showToast(AccountPasswordActivity.this, "密码修改成功");
					finish();
				}else {
					StatusUtils.handleStatus(response, AccountPasswordActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				submit.setEnabled(true);
				StatusUtils.handleError(arg0, AccountPasswordActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(ProgressDialogUtils.proDialog != null && ProgressDialogUtils.proDialog.isShowing()){
				ProgressDialogUtils.dismissProgressBar();
				BaseApplication.getInstance().cancelPendingRequests(TAG);
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
}
