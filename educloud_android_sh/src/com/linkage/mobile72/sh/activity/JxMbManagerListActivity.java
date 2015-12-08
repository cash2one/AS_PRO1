package com.linkage.mobile72.sh.activity;

import java.util.Locale;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.fragment.MbManagerNoticeFragment;
import com.linkage.mobile72.sh.Consts;

public class JxMbManagerListActivity extends BaseActivity implements OnClickListener {
	public static final String KEY_TYPE = "type";
	public static final String KEY_ACTION = "action";
	public static final String KEY_CONTENT = "content";
	public static final String ACTION_GET_OFFICE = "get_office";
	public static final String ACTION_GET_HOMEWORK = "get_homework";// 此值表示是获取模版并快速填充的
	public static final String ACTION_GET_NOTICE = "get_notice";// 此值表示是获取模版并快速填充的
	public static final String ACTION_GET_COMMENT = "get_comment";// 此值表示是获取模版并快速填充的
	public int location = 0;//
	public static final int LOCATION_HOMEWORKS = 0;
	public static final int LOCATION_NOTICE = 1;
	public static final int LOCATION_COMMENT = 2;
	public static final int LOCATION_OFFICESMS = 3;
	
	private String mAction;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private TextView title1, title2, title3, title4;
	private ImageView cursorImage;
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mb_manager_list);

		location = getIntent().getIntExtra(KEY_TYPE, LOCATION_HOMEWORKS);
		mAction = getIntent().getStringExtra(KEY_ACTION);

		Button back = (Button) findViewById(R.id.back);
		Button add = (Button) findViewById(R.id.set);
		back.setOnClickListener(this);
		setTitle("模板管理");
		add.setVisibility(View.INVISIBLE);
		add.setText("新增");

		initTitle();
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
			int two = one * 2;// 页卡1 -> 页卡3 偏移量

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				TranslateAnimation animation = new TranslateAnimation(one * currIndex, one * arg0,
						0, 0);// 显然这个比较简洁，只有一行代码。
				currIndex = arg0;
				animation.setFillAfter(true);// True:图片停在动画结束位置
				animation.setDuration(200);
				cursorImage.startAnimation(animation);
				selectTitleChange(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		selectTitleChange(0);
		mViewPager.setCurrentItem(location);
	}

	private void selectTitleChange(int position) {
		title1.setTextColor(0xff000000);
		title2.setTextColor(0xff000000);
		title3.setTextColor(0xff000000);
		title4.setTextColor(0xff000000);
		switch (position) {
		case LOCATION_HOMEWORKS:
			title1.setTextColor(0xff009911);
			break;
		case LOCATION_NOTICE:
			title2.setTextColor(0xff009911);
			break;
		case LOCATION_COMMENT:
			title3.setTextColor(0xff009911);
			break;
		case LOCATION_OFFICESMS:
			title4.setTextColor(0xff009911);
			break;
		default:
			break;
		}
	}

	private void initTitle() {
		// TODO Auto-generated method stub
		title1 = (TextView) findViewById(R.id.textView1);
		title2 = (TextView) findViewById(R.id.textView2);
		title3 = (TextView) findViewById(R.id.textView3);
		title4 = (TextView) findViewById(R.id.textView4);
		title4.setVisibility(View.VISIBLE);
		cursorImage = (ImageView) findViewById(R.id.cursor_image);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 4) / 2;// 计算偏移量
		cursorImage.setMinimumWidth(screenW / 4);
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursorImage.setImageMatrix(matrix);// 设置动画初始位置
		title1.setOnClickListener(this);
		title2.setOnClickListener(this);
		title3.setOnClickListener(this);
		title4.setOnClickListener(this);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case LOCATION_HOMEWORKS:
				return MbManagerNoticeFragment.newInstance(Consts.JxhdType.HOMEWORK, mAction);
			case LOCATION_NOTICE:
				return MbManagerNoticeFragment.newInstance(Consts.JxhdType.NOTICE, mAction);
			case LOCATION_COMMENT:
				return MbManagerNoticeFragment.newInstance(Consts.JxhdType.COMMENT, mAction);
			case LOCATION_OFFICESMS:
                return MbManagerNoticeFragment.newInstance(Consts.JxhdType.OFFICESMS, mAction);
			default:
				return MbManagerNoticeFragment.newInstance(Consts.JxhdType.HOMEWORK, mAction);
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case LOCATION_HOMEWORKS:
				return title1.getText().toString();
			case LOCATION_NOTICE:
				return title2.getText().toString();
			case LOCATION_COMMENT:
				return title3.getText().toString();
			case LOCATION_OFFICESMS:
                return title4.getText().toString();
			}
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textView1:
			mViewPager.setCurrentItem(LOCATION_HOMEWORKS);
			break;
		case R.id.textView2:
			mViewPager.setCurrentItem(LOCATION_NOTICE);
			break;
		case R.id.textView3:
			mViewPager.setCurrentItem(LOCATION_COMMENT);
			break;
		case R.id.textView4:
			mViewPager.setCurrentItem(LOCATION_OFFICESMS);
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}
}