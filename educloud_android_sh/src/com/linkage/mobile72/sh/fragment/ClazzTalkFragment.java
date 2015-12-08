package com.linkage.mobile72.sh.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.MainActivity;
import com.linkage.mobile72.sh.activity.WriteTalkActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.widget.SelectClazzDialog;

public class ClazzTalkFragment extends BaseFragment implements OnClickListener,
		OnCheckedChangeListener {
	public static int topChildIndex = 1;
	
	private final static String TAG = "ClazzImagesFragment";

	private AccountData account;
	private CircularImage avatar;

	// fragment
	private RadioButton mTab1;
	private RadioButton mTab2;
	private RadioButton mTab3;
	private FrameLayout mContainer;
	private FragmentPagerAdapter mFragmentPagerAdapter;
	private RelativeLayout empty;

	private Button jiaButton;
	private TextView txtSelector, txtClazzNum, title;

	private View baseView;
	// 班级选择器
	private int selectorNum = 0;
	private SelectClazzDialog selectClazz;

	// 班级名称列表
	private String[] clazzs;
	private long clazzId = 0;
	private List<ClassRoom> mClassRooms;

	private static ClazzDynamicFragment claiFrag1, claiFrag3;
	private static ClazzImagesFragment claiFrag2;
	private static ClazzTalkFragment mySelf;

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		account = getCurAccount();
		if (account != null) {
			imageLoader.displayImage(account.getAvatar(), avatar);
		}
		title.setText("班级＋");
		new LoadContacts().execute();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		baseView = View.inflate(getActivity(), R.layout.fragment_clazz_talk, null);
		avatar = (CircularImage) baseView.findViewById(R.id.user_avater);
		avatar.setOnClickListener(this);
		RelativeLayout titleLayout = (RelativeLayout) baseView.findViewById(R.id.relativelayout1);
		if (isTeacher()) {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg_green);
		} else {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg);
		}
		empty = (RelativeLayout) baseView.findViewById(R.id.empty);
		jiaButton = (Button) baseView.findViewById(R.id.jia_button);
		title = (TextView) baseView.findViewById(R.id.title);
		return baseView;
	}

	public static ClazzTalkFragment create(int titleRes) {
		mySelf = new ClazzTalkFragment();
		Bundle args = new Bundle();
		// args.putInt("titleRes", titleRes);
		mySelf.setArguments(args);
		return mySelf;
	}

	public static ClazzTalkFragment getInstence() {
		if(mySelf == null){
			mySelf = new ClazzTalkFragment();
			Bundle args = new Bundle();
			// args.putInt("titleRes", titleRes);
			mySelf.setArguments(args);;
		}
		return mySelf;
	}
	
	/**
	 * 读取本地班级列表 班级数＝0 显示空界面 ＝1 不可切换班级 >1 可以切换班级
	 */
	private class LoadContacts extends AsyncTask<Integer, Void, Boolean> {
		boolean isReset = false;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			DataHelper helper = getDBHelper();
			DataHelper.getHelper(getActivity());
			String loginName = BaseApplication.getInstance()
					.getDefaultAccount().getLoginname();
			try {
				QueryBuilder<ClassRoom, Integer> classroomBuilder = helper
						.getClassRoomData().queryBuilder();
				classroomBuilder.orderBy("joinOrManage", true)
						.orderBy("schoolId", true).where()
						.eq("loginName", loginName);
				List<ClassRoom> newClassRoom = classroomBuilder.query();
				//如果班级数为空或者班级数量与新获取到得不同，那么就刷新界面
				if(mClassRooms == null || (newClassRoom != null && mClassRooms.size() != newClassRoom.size())){
					mClassRooms = newClassRoom;
					isReset = true;
				} 
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try{
				if(isReset){
					if (mClassRooms == null) {
						mClassRooms = new ArrayList<ClassRoom>();
					}
					
					if (mClassRooms.size() > 0) {
						clazzs = new String[mClassRooms.size()];
						clazzId = mClassRooms.get(selectorNum).getId();
						for (int i = 0; i < mClassRooms.size(); i++) {
							clazzs[i] = mClassRooms.get(i).getName();
						}
						empty.setVisibility(View.GONE);
						init();
						if (mClassRooms.size() > 1) {
							initDialog();
						}
					} else {
						empty.setVisibility(View.VISIBLE);
						jiaButton.setVisibility(View.GONE);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				empty.setVisibility(View.VISIBLE);
				jiaButton.setVisibility(View.GONE);
			}
		}
	}

	private void initDialog() {
		// Dialog初始化
		selectClazz = new SelectClazzDialog(getActivity(), mClassRooms, "取消", "确定");
		selectClazz.setCancelListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectClazz.setCheckNum(selectorNum);
				selectClazz.dismiss();
			}
		});
		selectClazz.setOkListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(selectorNum != selectClazz.getCheckNum()){
					selectorNum = selectClazz.getCheckNum();
					txtClazzNum.setText(clazzs[selectorNum]);
					clazzId = mClassRooms.get(selectorNum).getId();
					if (claiFrag1 != null) {
						claiFrag1.onRefreshInfo(clazzId, topChildIndex);
					}
					if (claiFrag2 != null) {
						claiFrag2.onRefreshInfo(clazzId, topChildIndex);
					}
					if (claiFrag3 != null) {
						claiFrag3.onRefreshInfo(clazzId, topChildIndex);
					}
				}
				selectClazz.dismiss();
			}
		});
	}

	/**
	 * 初始化界面控件
	 */
	private void init() {
		mFragmentPagerAdapter = new FragmentPagerAdapter(
				getChildFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				switch (position) {
				case R.id.tab_clazz_dynamic:
					if (claiFrag1 == null) {
						claiFrag1 = new ClazzDynamicFragment(clazzId,
								ClazzDynamicFragment.TYPE_CLAZZ);
					} 
					topChildIndex = 1;
					return claiFrag1;
				case R.id.tab_clazz_album:
					if (claiFrag2 == null) {
						claiFrag2 = new ClazzImagesFragment(clazzId);
					} 
					topChildIndex = 2;
					return claiFrag2;
				case R.id.tab_my_posts:
					if (claiFrag3 == null) {
						claiFrag3 = new ClazzDynamicFragment(clazzId,
								ClazzDynamicFragment.TYPE_PERSONAL);
					} 
					topChildIndex = 3;
					return claiFrag3;
				default:
					topChildIndex = 1;
					return claiFrag1;
				}
			}

			@Override
			public int getCount() {
				return 3;
			}
		};
		mTab1 = (RadioButton) baseView.findViewById(R.id.tab_clazz_dynamic);
		mTab2 = (RadioButton) baseView.findViewById(R.id.tab_clazz_album);
		mTab3 = (RadioButton) baseView.findViewById(R.id.tab_my_posts);

		txtSelector = (TextView) baseView.findViewById(R.id.txt_selector);
		txtClazzNum = (TextView) baseView.findViewById(R.id.txt_clazz_num);
		mContainer = (FrameLayout) baseView.findViewById(R.id.content);

		mTab1.setOnCheckedChangeListener(this);
		mTab2.setOnCheckedChangeListener(this);
		mTab3.setOnCheckedChangeListener(this);

		jiaButton.setVisibility(View.VISIBLE);
		jiaButton.setBackgroundDrawable(null);
		jiaButton.setText("发言");
		jiaButton.setOnClickListener(this);
		txtSelector.setOnClickListener(this);
		txtClazzNum.setText(clazzs[selectorNum]);
		if (mClassRooms.size() == 1) {
			 txtSelector.setVisibility(View.GONE);
		} else {
			 txtSelector.setVisibility(View.VISIBLE);
		}
		Fragment fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(
				mContainer, R.id.tab_clazz_dynamic);
		mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
		mFragmentPagerAdapter.finishUpdate(mContainer);
		
		mTab1.performClick();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.jia_button:
		    Intent wtIntent = new Intent(getActivity(), WriteTalkActivity.class);
		    wtIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		    getActivity().startActivityForResult(wtIntent, 1001);
			break;
		case R.id.txt_selector:
			selectClazz.show();
			break;
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

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1001 && resultCode == getActivity().RESULT_OK){
			if (claiFrag1 != null) {
				claiFrag1.onRefreshInfo(clazzId, topChildIndex);
			}
			if (claiFrag2 != null) {
				claiFrag2.onRefreshInfo(clazzId, topChildIndex);
			}
			if (claiFrag3 != null) {
				claiFrag3.onRefreshInfo(clazzId, topChildIndex);
			}
		}
	}
	
	/**
	 * 改变页面的fragment
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			Fragment fragment = (Fragment) mFragmentPagerAdapter
					.instantiateItem(mContainer, buttonView.getId());
			mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
			mFragmentPagerAdapter.finishUpdate(mContainer);
			switch (buttonView.getId()) {
			case R.id.tab_clazz_dynamic:
				if (claiFrag1 != null) {
					claiFrag1.onRefreshInfo(clazzId, topChildIndex);
				}
				break;
			case R.id.tab_clazz_album:
				if (claiFrag2 != null) {
					claiFrag2.onRefreshInfo(clazzId, topChildIndex);
				}
				break;
			case R.id.tab_my_posts:
				if (claiFrag3 != null) {
					claiFrag3.onRefreshInfo(clazzId, topChildIndex);
				}
				break;
			}
		}
	}

}
