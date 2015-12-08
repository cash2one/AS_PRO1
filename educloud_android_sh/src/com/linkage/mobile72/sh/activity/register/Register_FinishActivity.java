package com.linkage.mobile72.sh.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.AlterPasswordActivity;
import com.linkage.mobile72.sh.activity.LoginActivity;
import com.linkage.mobile72.sh.activity.MainActivity;
import com.linkage.mobile72.sh.activity.PersonalInfoActivity;
import com.linkage.mobile72.sh.activity.SplashActivity;
import com.linkage.mobile72.sh.app.BaseActivity;

/**
 * 注册完成界面
 * @author Yao
 *
 */
public class Register_FinishActivity extends BaseActivity implements OnClickListener {

	private Button finishInfoBtn, returnIndexBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		finishObviousPage();
		setContentView(R.layout.activity_register_finish);
		Button back = (Button)findViewById(R.id.back);
		back.setVisibility(View.GONE);
		setTitle("注册成功");
		finishInfoBtn = (Button)findViewById(R.id.finish_info_btn);
		returnIndexBtn = (Button)findViewById(R.id.return_index_btn);
		finishInfoBtn.setOnClickListener(this);
		returnIndexBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.finish_info_btn:
			startActivity(new Intent(Register_FinishActivity.this, PersonalInfoActivity.class));
			break;

		case R.id.return_index_btn:
			startActivity(new Intent(Register_FinishActivity.this, MainActivity.class));
			finish();
			break;
		}
	}
	
	private void finishObviousPage() {
		if(SplashActivity.instance!=null && !SplashActivity.instance.isFinishing())
			SplashActivity.instance.finish();
		if(LoginActivity.instance!=null && !LoginActivity.instance.isFinishing())
			LoginActivity.instance.finish();
		if(AlterPasswordActivity.instance!=null && !AlterPasswordActivity.instance.isFinishing())
			AlterPasswordActivity.instance.finish();
		if(Register_SendCodeActivity.instance!=null && !Register_SendCodeActivity.instance.isFinishing())
			Register_SendCodeActivity.instance.finish();
		if(Reset_SendCodeActivity.instance!=null && !Reset_SendCodeActivity.instance.isFinishing())
			Reset_SendCodeActivity.instance.finish();
		if(InputPasswordActivity.instance!=null && !InputPasswordActivity.instance.isFinishing())
			InputPasswordActivity.instance.finish();
		if(Validate_SmsActivity.instance!=null && !Validate_SmsActivity.instance.isFinishing())
			Validate_SmsActivity.instance.finish();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			startActivity(new Intent(Register_FinishActivity.this, MainActivity.class));
			finish();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
}
