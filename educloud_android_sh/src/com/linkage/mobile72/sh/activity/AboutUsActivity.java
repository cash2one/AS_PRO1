package com.linkage.mobile72.sh.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.utils.Utils;

public class AboutUsActivity extends BaseActivity implements OnClickListener {
	
	private Button back;
	private TextView view2,view3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		
		back = (Button) findViewById(R.id.back);
		view2 = (TextView) findViewById(R.id.view2);
		view3 = (TextView) findViewById(R.id.view3);
		setTitle("关于我们");
		back.setOnClickListener(this);
		view2.setText("版本号：" + "  " + Utils.getVersion(this));
		view3.setText("版本号：" + Utils.getVersionCode(this));
		view3.setVisibility(View.INVISIBLE);
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
