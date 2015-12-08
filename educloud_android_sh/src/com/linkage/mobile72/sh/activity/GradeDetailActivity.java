package com.linkage.mobile72.sh.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;

public class GradeDetailActivity extends BaseActivity implements
		OnClickListener {
	private Button mBack;
	private TextView mSenderName;
	private TextView mSendTime;
	private TextView mdetailContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grade_detail);
		setTitle(R.string.title_grade_detail);
		mBack = (Button) findViewById(R.id.back);
		mSenderName = (TextView) findViewById(R.id.senderName);
		mSendTime  = (TextView) findViewById(R.id.sendTime);
		mdetailContent = (TextView) findViewById(R.id.detailContent);
		mBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

}
