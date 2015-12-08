package com.linkage.mobile72.sh.activity;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.data.AccountData;

@SuppressLint("SimpleDateFormat") public class MyJifenActivity extends BaseActivity implements OnClickListener {

	private Button back, set;
	private TextView semiTitle;
	private AccountData account;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		account = getCurAccount();
		setContentView(R.layout.activity_my_jifen);
		setTitle("我的积分:"+account.getCreditScore());
		account = getCurAccount();
		back = (Button)findViewById(R.id.back);
		set = (Button)findViewById(R.id.set);
		semiTitle = (TextView)findViewById(R.id.semi_title);
		
		set.setVisibility(View.VISIBLE);
		set.setText("积分规则");
//		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
//		String sDateTime = sdf3.format(account.getCreditScoreEndtime());
		semiTitle.setText("有效期至："+account.getCreditScoreEndtime());
		back.setOnClickListener(this);
		set.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.set:
			Intent i = new Intent(this, JFRuleActivity.class);
			startActivity(i);
			break;
		}
	}
}
