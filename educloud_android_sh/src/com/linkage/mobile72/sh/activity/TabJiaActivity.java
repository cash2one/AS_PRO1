package com.linkage.mobile72.sh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;

public class TabJiaActivity extends BaseActivity implements OnClickListener{

    private Button close;
    private Button createHomeworkBtn, createNoticeBtn, createCommentBtn, createOfficeSmsBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_jia);
        close = (Button)findViewById(R.id.create_page_close_btn);
        createHomeworkBtn = (Button)findViewById(R.id.create_homework_btn);
        createNoticeBtn = (Button)findViewById(R.id.create_notice_btn);
        createCommentBtn = (Button)findViewById(R.id.create_comment_btn);
        createOfficeSmsBtn = (Button)findViewById(R.id.create_officesms_btn);

        close.setOnClickListener(this);
        createHomeworkBtn.setOnClickListener(this);
        createNoticeBtn.setOnClickListener(this);
        createCommentBtn.setOnClickListener(this);
        createOfficeSmsBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.create_homework_btn:
            Intent intentHomework = new Intent(this, CreateHomeworkActivity.class);
            startActivity(intentHomework);
            finish();
			break;
        case R.id.create_notice_btn:
            Intent intentNotice = new Intent(this, CreateNoticeActivity.class);
            startActivity(intentNotice);
            finish();
            break;
        case R.id.create_comment_btn:
            Intent intentComment = new Intent(this, CreateCommentActivity.class);
            startActivity(intentComment);
            finish();
            break;
        case R.id.create_officesms_btn:
            Intent intentOfficesms = new Intent(this, CreateOfficesmsActivity.class);
            startActivity(intentOfficesms);
            finish();
            break;
        case R.id.create_page_close_btn:
            finish();
            break;
		}
	}
}
