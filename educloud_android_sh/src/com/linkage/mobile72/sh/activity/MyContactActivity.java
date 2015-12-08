package com.linkage.mobile72.sh.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.fragment.ContactsFriendFragment;
import com.linkage.mobile72.sh.fragment.ContactsFriendFragment2;
import com.linkage.mobile72.sh.fragment.ContactsGroupFragment;
import com.linkage.mobile72.sh.fragment.ContactsGroupFragment2;
import com.linkage.mobile72.sh.Consts;

public class MyContactActivity extends BaseActivity implements OnCheckedChangeListener,
		OnClickListener {
	public static final String TAG = MyContactActivity.class.getSimpleName();

	private AccountData account;
	private Button back;
	private RadioButton mTab1;
	private RadioButton mTab2;
	private FrameLayout mContainer;
	private FragmentPagerAdapter mFragmentPagerAdapter;

	private Button configBtn;
	private PopupWindow titlePopup;
	
	private RadioGroup radioRp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_contact);
		initPopup();
		setTitleBg();
		account = getCurAccount();
		mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				switch (position) {
				case R.id.radio1:
					if(isTeacher()){
						return ContactsGroupFragment.create();
					} else {
						return ContactsGroupFragment2.create();
					}
				case R.id.radio2:
					if(isTeacher()){
						return ContactsFriendFragment.create();
					} else {
						return ContactsFriendFragment2.create();
					}
				default:
					if(isTeacher()){
						return ContactsFriendFragment.create();
					} else {
						return ContactsFriendFragment2.create();
					}
				}
			}

			@Override
			public int getCount() {
				return 2;
			}
		};

		// 初始化布局元素
		mTab1 = (RadioButton) findViewById(R.id.radio1);
		mTab2 = (RadioButton) findViewById(R.id.radio2);
//		if (isTeacher()) {
//			mTab1.setTextColor(getResources().getColor(R.color.contact_tab_text));
//			mTab2.setTextColor(getResources().getColor(R.color.contact_tab_text));
//		} else {
//			mTab1.setTextColor(getResources().getColor(R.color.contact_tab_text2));
//			mTab2.setTextColor(getResources().getColor(R.color.contact_tab_text2));
//		}
		mContainer = (FrameLayout) findViewById(R.id.content);
		back = (Button) findViewById(R.id.back);
		configBtn = (Button) findViewById(R.id.jia_button);
		final RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.relativelayout1);
		back.setOnClickListener(this);
		mTab1.setOnCheckedChangeListener(this);
		mTab2.setOnCheckedChangeListener(this);
		configBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mTab1.isChecked()) {
					if (!titlePopup.isShowing())
						titlePopup.showAsDropDown(titleLayout);
					else
						titlePopup.dismiss();
				} else {
					Intent i = new Intent(MyContactActivity.this, SearchPersonActivity.class);
					startActivity(i);
				}
			}
		});
		
		radioRp = (RadioGroup) findViewById(R.id.rdp);
		RelativeLayout searchLayout = (RelativeLayout)findViewById(R.id.search_btn);
        searchLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyContactActivity.this, LocalSearchResultActivity.class);
                if (mTab1.getId() == radioRp.getCheckedRadioButtonId()) {
                    i.putExtra("type", Consts.SEARCH_TYPE_CLASS);
                } else {
                    i.putExtra("type", Consts.SEARCH_TYPE_FRIEND);
                }
                
                startActivity(i);
            }
        });
        
		mTab2.performClick();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			Fragment fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(mContainer,
					buttonView.getId());
			mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
			mFragmentPagerAdapter.finishUpdate(mContainer);

			if (R.id.radio1 == buttonView.getId()) {
				configBtn.setBackgroundResource(R.drawable.button_config);
				configBtn.setVisibility(View.INVISIBLE);
			} else {
				configBtn.setBackgroundResource(R.drawable.button_jia);
				configBtn.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.mm_title_group_config_plus_layout:
			if (titlePopup != null && titlePopup.isShowing()) {
				titlePopup.dismiss();
			}
			Intent i1 = new Intent(this, CreateGroupActivity.class);
			startActivity(i1);
			break;
		case R.id.mm_title_group_config_search_layout:
			if (titlePopup != null && titlePopup.isShowing()) {
				titlePopup.dismiss();
			}
			Intent i2 = new Intent(this, SearchGroupActivity.class);
			startActivity(i2);
			break;
		case R.id.mm_title_group_config_set_layout:
			if (titlePopup != null && titlePopup.isShowing()) {
				titlePopup.dismiss();
			}
			Intent i3 = new Intent(this, SearchGroupActivity.class);
			startActivity(i3);
			break;
		}
	}

	/**
	 * 初始化弹窗
	 */
	private void initPopup() {
		View view = LayoutInflater.from(this).inflate(R.layout.title_popup_group, null);
		LinearLayout other = (LinearLayout) view.findViewById(R.id.linearlayout2);
		titlePopup = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		titlePopup.setBackgroundDrawable(new ColorDrawable(0x55000000));
		titlePopup.setOutsideTouchable(true);
		RelativeLayout mmGroupPlusLayout = (RelativeLayout) view
				.findViewById(R.id.mm_title_group_config_plus_layout);
		RelativeLayout mmGroupSearchLayout = (RelativeLayout) view
				.findViewById(R.id.mm_title_group_config_search_layout);
		RelativeLayout mmGroupSetLayout = (RelativeLayout) view
				.findViewById(R.id.mm_title_group_config_set_layout);
		mmGroupPlusLayout.setOnClickListener(this);
		mmGroupSearchLayout.setOnClickListener(this);
		mmGroupSetLayout.setOnClickListener(this);
		other.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (titlePopup != null && titlePopup.isShowing()) {
					titlePopup.dismiss();
				}
				return false;
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}
