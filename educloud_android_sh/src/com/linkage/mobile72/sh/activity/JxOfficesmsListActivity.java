package com.linkage.mobile72.sh.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.fragment.JxOfficesmsFragment;

public class JxOfficesmsListActivity extends BaseActivity implements OnClickListener {
	
	private static final int REQUEST_REFRESH = 1;
	
	public static final String KEY_ACTION = "action";
	public static final String KEY_CONTENT = "content";
	public static final String ACTION_GET_HOMEWORK = "get_homework";// 此值表示是获取模版并快速填充的
	public static final String ACTION_GET_NOTICE = "get_notice";// 此值表示是获取模版并快速填充的
	public static final String ACTION_GET_COMMENT = "get_comment";// 此值表示是获取模版并快速填充的

	private RadioGroup radioGroup;
	private FrameLayout mContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jxhd_office_list);

		Button back = (Button) findViewById(R.id.back);
		Button add = (Button) findViewById(R.id.set);
		back.setOnClickListener(this);
		setTitle("办公短信");
		add.setVisibility(View.GONE);
		add.setText("新增");
		add.setOnClickListener(this);

		mContainer = (FrameLayout)findViewById(R.id.fragment_container);
		radioGroup = (RadioGroup) findViewById(R.id.radio_group);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
	            Fragment fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(mContainer, checkedId);
	            mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
	            mFragmentPagerAdapter.finishUpdate(mContainer);
			}
		});
		Fragment fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(mContainer, R.id.radio_send_box);
        mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
        mFragmentPagerAdapter.finishUpdate(mContainer);
	}
	
	private FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int checkedId) {
        	switch (checkedId) {
			case R.id.radio_receive_box:
				return JxOfficesmsFragment.newInstance(1);
			case R.id.radio_send_box:
				return JxOfficesmsFragment.newInstance(2);
			default:
                return JxOfficesmsFragment.newInstance(1);
			}
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_REFRESH:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set:
			Intent i = new Intent(this, CreateOfficesmsActivity.class);
			startActivityForResult(i, REQUEST_REFRESH);
			break;
		case R.id.back:
			finish();
			break;
		}
	}
}