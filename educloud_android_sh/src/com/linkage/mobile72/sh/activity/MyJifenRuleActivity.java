package com.linkage.mobile72.sh.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;

public class MyJifenRuleActivity extends BaseActivity implements OnClickListener {

	private Button back, set;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_jifen_rule);
		setTitle("我的积分：1350");
		back = (Button)findViewById(R.id.back);
		set = (Button)findViewById(R.id.set);
		
		set.setVisibility(View.INVISIBLE);
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
			break;
		}
	}
}
