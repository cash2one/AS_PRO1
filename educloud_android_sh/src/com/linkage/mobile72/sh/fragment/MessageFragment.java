package com.linkage.mobile72.sh.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.MainActivity;
import com.linkage.mobile72.sh.activity.MyContactActivity;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.widget.CircularImage;

public class MessageFragment extends BaseFragment implements
		OnCheckedChangeListener, OnClickListener {

	private AccountData account;
	private CircularImage avatar;
	private RadioButton mTab1;
	private RadioButton mTab2;
	private FrameLayout mContainer;
	private FragmentPagerAdapter mFragmentPagerAdapter;

	private Button configBtn;
	private PopupWindow titlePopup;

	private TextView tvAddrBook;

	public static MessageFragment create(int titleRes) {
		MessageFragment f = new MessageFragment();
		// Supply num input as an argument.
		Bundle args = new Bundle();
		// args.putInt("titleRes", titleRes);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		account = getCurAccount();
		mFragmentPagerAdapter = new FragmentPagerAdapter(
				getChildFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				switch (position) {
				case R.id.radio1:
					return NewMessageFragment.create(R.string.tab_txt_message);
				case R.id.radio2:
					return JzhFragment.create(R.string.tab_txt_contact);
				default:
					return NewMessageFragment.create(R.string.tab_txt_message);
				}
			}

			@Override
			public int getCount() {
				return 1;
			}
		};

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_message, null);
		RelativeLayout titleLayout = (RelativeLayout) view
				.findViewById(R.id.relativelayout1);

		avatar = (CircularImage) view.findViewById(R.id.user_avater);
		imageLoader.displayImage(account.getAvatar(), avatar);
		// 初始化布局元素
		mTab1 = (RadioButton) view.findViewById(R.id.radio1);
		mTab2 = (RadioButton) view.findViewById(R.id.radio2);
		if (isTeacher()) {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg_green);
			// mTab1.setBackgroundResource(R.drawable.radio_btn_left_green);
			// mTab2.setBackgroundResource(R.drawable.radio_btn_right_green);
			mTab2.setVisibility(View.GONE);
			mTab1.setBackgroundDrawable(null);
		} else {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg);
			// mTab1.setBackgroundResource(R.drawable.radio_btn_left);
			// mTab2.setBackgroundResource(R.drawable.radio_btn_right);
			mTab2.setVisibility(View.GONE);
			mTab1.setBackgroundDrawable(null);
		}

		mContainer = (FrameLayout) view.findViewById(R.id.content);
		configBtn = (Button) view.findViewById(R.id.jia_button);
		mTab1.setOnCheckedChangeListener(this);
		mTab2.setOnCheckedChangeListener(this);
		configBtn.setVisibility(View.GONE);
//		configBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent i = new Intent(getActivity(),
//						QrCaptureResultActivity.class);
//				startActivity(i);
//			}
//		});
		mTab1.setText("消息");
		mTab2.setText("话题");
		mTab1.performClick();

		avatar.setOnClickListener(this);

		tvAddrBook = (TextView) view.findViewById(R.id.tvAddrBook);
		tvAddrBook.setVisibility(View.VISIBLE);

		tvAddrBook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// LogUtils.e("msf--->tvAddrBook is onclick!!!");
				Intent intent = new Intent(getActivity(),
						MyContactActivity.class);
				// Intent intent = new Intent(getActivity(),
				// WonderExerciseActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		account = getCurAccount();
		if (account != null)
			imageLoader.displayImage(account.getAvatar(), avatar);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			Fragment fragment = (Fragment) mFragmentPagerAdapter
					.instantiateItem(mContainer, buttonView.getId());
			mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
			mFragmentPagerAdapter.finishUpdate(mContainer);

			// if(R.id.radio1 == buttonView.getId()) {
			// configBtn.setVisibility(View.VISIBLE);
			// configBtn.setBackgroundResource(R.drawable.button_jia);
			// }else {
			// configBtn.setVisibility(View.GONE);
			// }
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.user_avater:
			if (!MainActivity.slideMenu.isMenuShowing()) {
				new Handler().post(new Runnable() {
					public void run() {
						MainActivity.slideMenu.showMenu(true);
					}
				});
			}
			break;
		}
	}
}
