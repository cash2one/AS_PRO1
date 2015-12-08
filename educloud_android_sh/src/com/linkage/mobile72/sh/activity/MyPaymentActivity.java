package com.linkage.mobile72.sh.activity;

import android.content.ContentValues;
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
import com.linkage.mobile72.sh.fragment.MyPaymentFragment;
import com.linkage.mobile72.sh.im.provider.Ws.ThreadTable;

public class MyPaymentActivity extends BaseActivity implements OnCheckedChangeListener {

	private RadioButton mTab1;
    private RadioButton mTab2;
    private FrameLayout mContainer;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_payment);

		setTitle(R.string.title_my_payment);
		((Button)findViewById(R.id.back)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTab1 = (RadioButton) findViewById(R.id.radio1);
        mTab2 = (RadioButton) findViewById(R.id.radio2);
        mContainer = (FrameLayout) findViewById(R.id.content);
        mTab1.setOnCheckedChangeListener(this);
        mTab2.setOnCheckedChangeListener(this);
        mTab1.performClick();
        
        ContentValues cv = new ContentValues();
		cv.put(ThreadTable.UNREAD_COUNT, 0);
		getContentResolver().update(
				ThreadTable.CONTENT_URI,
				cv,
				ThreadTable.ACCOUNT_NAME + "=? and " + ThreadTable.BUDDY_ID
						+ "=? and " + ThreadTable.CHAT_TYPE + "=? ",
				new String[] { getAccountName(), String.valueOf(99999),
						String.valueOf(0) });
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
                return MyPaymentFragment.create(1);
            case R.id.radio2:
                return MyPaymentFragment.create(2);
            default:
            	return MyPaymentFragment.create(1);
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
    };


}
