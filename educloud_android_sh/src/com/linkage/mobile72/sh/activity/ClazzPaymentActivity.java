package com.linkage.mobile72.sh.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.fragment.ClazzPaymentCreateFragment;
import com.linkage.mobile72.sh.fragment.ClazzPaymentListFragment;

public class ClazzPaymentActivity extends BaseActivity implements OnCheckedChangeListener {

	private ClassInfoBean clazz;
	
	private RadioButton mTab1;
    private RadioButton mTab2;
    private FrameLayout mContainer;
    public CompoundButton currentButtonView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_payment);

		clazz = (ClassInfoBean)getIntent().getExtras().getSerializable("CLAZZ");
		if(clazz == null) {
			finish();
		}
		
		setTitle(R.string.title_clazz_payment);
		((Button)findViewById(R.id.back)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTab1 = (RadioButton) findViewById(R.id.radio1);
        mTab2 = (RadioButton) findViewById(R.id.radio2);
        mTab1.setText("创建缴费");
        mTab2.setText("已建缴费");
        mContainer = (FrameLayout) findViewById(R.id.content);
        mTab1.setOnCheckedChangeListener(this);
        mTab2.setOnCheckedChangeListener(this);
        mTab1.performClick();
	}

	@Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Fragment fragment = (Fragment) mFragmentPagerAdapter
                    .instantiateItem(mContainer, buttonView.getId());
            mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
            mFragmentPagerAdapter.finishUpdate(mContainer);
        }
    }
	
	private FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter(
            getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            switch (position) {
            case R.id.radio1:
                return ClazzPaymentCreateFragment.create(clazz);
            case R.id.radio2:
                return ClazzPaymentListFragment.create();
            default:
            	return ClazzPaymentCreateFragment.create(clazz);
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
    };


}
