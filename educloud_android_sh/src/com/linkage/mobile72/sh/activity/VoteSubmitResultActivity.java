package com.linkage.mobile72.sh.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;

public class VoteSubmitResultActivity extends BaseActivity implements OnClickListener {

	private Button back,goBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parent_activity_vote_submit_result);
		setTitle("消息");
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(this);
		goBack = (Button)findViewById(R.id.back_btn);
		goBack.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.back_btn:
			finish();
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
	}
}
