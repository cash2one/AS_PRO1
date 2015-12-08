package com.linkage.mobile72.sh.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.JxHomeworkListActivity;
import com.linkage.mobile72.sh.activity.JxOfficesmsListActivity;
import com.linkage.mobile72.sh.activity.MainActivity;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.im.provider.Ws.MessageTable;
import com.linkage.mobile72.sh.im.provider.Ws.ThreadTable;


public class MainFragment extends BaseFragment implements OnTouchListener, View.OnClickListener {

	private RadioButton mTab1;
	private RadioButton mTab2;
	private RadioButton mTab3;
	private RadioButton mTab4;
//	private Button jiaButton;
	private PopupWindow jiaPopwindow;
	private FrameLayout mContainer;
	private TextView messageCount;

	private FragmentPagerAdapter mFragmentPagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mFragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				switch (position) {
				case R.id.tab_message:
					return MessageFragment.create(R.string.tab_txt_message);
				case R.id.tab_jx:
					return JxHomeFragment.newInstance();
					// case R.id.tab_contacts:
					// return ContactsFragment.create(R.string.tab_txt_contact);
				case R.id.tab_contacts:
					return ClazzTalkFragment.create(R.string.tab_txt_clazz);
				case R.id.tab_app:
					return AppFragment.create(R.string.tab_txt_app);
				default:
					return JxHomeFragment.newInstance();
				}
			}

			@Override
			public int getCount() {
				return 3;
			}
		};
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View view = View.inflate(getActivity(), R.layout.fragment_main, null);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (MainActivity.slideMenu.isMenuShowing()) {
					new Handler().post(new Runnable() {
						public void run() {
							MainActivity.slideMenu.showContent(true);
						}
					});
					return true;
				}
				return false;
			}
		});
		// 初始化布局元素
		mTab1 = (RadioButton) view.findViewById(R.id.tab_message);
		mTab2 = (RadioButton) view.findViewById(R.id.tab_jx);
		mTab3 = (RadioButton) view.findViewById(R.id.tab_app);
		mTab4 = (RadioButton) view.findViewById(R.id.tab_contacts);
		mTab3.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.tab_teach), null, null);
		mTab3.setText("教学");
//		jiaButton = (Button) view.findViewById(R.id.tab_jia);
		mContainer = (FrameLayout) view.findViewById(R.id.content);
		mTab1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Fragment fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(
							mContainer, buttonView.getId());

					mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
					mFragmentPagerAdapter.finishUpdate(mContainer);
				}
			}
		});
		mTab2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					/*if (getCurAccount().getOrigin() != 2) {
						T.showShort(getActivity(), "您不是校讯通用户，无权限使用该功能");
						return;
					}*/
					Fragment fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(
							mContainer, buttonView.getId());

					mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
					mFragmentPagerAdapter.finishUpdate(mContainer);
				}
			}
		});
		mTab3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Fragment fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(
							mContainer, buttonView.getId());

					mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
					mFragmentPagerAdapter.finishUpdate(mContainer);
				}
			}
		});
		 mTab4.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						Fragment fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(
								mContainer, buttonView.getId());

						mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
						mFragmentPagerAdapter.finishUpdate(mContainer);
					}
				}
			});
		initJiaPop();
//		jiaButton.setOnClickListener(this);
		messageCount = (TextView) view.findViewById(R.id.message_num);

		mTab2.performClick();
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		notifyMessageCount();
		getActivity().registerReceiver(receiver, new IntentFilter(ThreadTable.CONTENT_CHAGED));
//		getActivity().getContentResolver().registerContentObserver(ThreadTable.CONTENT_URI, false, contentObserver);
	}
	
	ContentObserver contentObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			notifyMessageCount();
		}
	};
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			notifyMessageCount();
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (getActivity() != null) {
//			getActivity().getContentResolver().unregisterContentObserver(contentObserver);
			getActivity().unregisterReceiver(receiver);
		}
	}

	public void notifyMessageCount() {
		int unreadNum = 0;
		Cursor threadCursor = null;
		try {
			threadCursor = getActivity().getContentResolver().query(ThreadTable.CONTENT_URI,
					null, MessageTable.ACCOUNT_NAME + "=?",
					new String[] { mApp.mCurAccount.getLoginname() },
					ThreadTable.MSG_SENT_TIME + " desc");
			while (threadCursor.moveToNext()) {
				unreadNum = unreadNum
						+ threadCursor.getInt(threadCursor
								.getColumnIndexOrThrow(ThreadTable.UNREAD_COUNT));
				;
			}
		} catch (Exception e) {
			e.printStackTrace();
			/*messageCount.setVisibility(View.INVISIBLE);
			return;*/
		} finally {
			if(threadCursor != null)
			threadCursor.close();
		}
		if (unreadNum <= 0) {
			messageCount.setVisibility(View.INVISIBLE);
		} else if (unreadNum < 99) {
			messageCount.setVisibility(View.VISIBLE);
			messageCount.setText("" + unreadNum);
		} else if (unreadNum > 99) {
			messageCount.setVisibility(View.VISIBLE);
			messageCount.setText("99+");
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (MainActivity.slideMenu.isMenuShowing()) {
			new Handler().post(new Runnable() {
				public void run() {
					MainActivity.slideMenu.showContent(true);
				}
			});
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		Intent mIntent;
		switch (v.getId()) {
//		case R.id.tab_jia:
//			/*Intent i = new Intent(getActivity(), TabJiaActivity.class);
//			startActivity(i);*/
//			
//			jiaPopwindow.setAnimationStyle(R.style.JiaPop);
//			jiaPopwindow.showAtLocation(jiaButton, Gravity.NO_GRAVITY, 0, 10);
//			break;
		case R.id.create_homework_btn:
//            Intent intentHomework = new Intent(getActivity(), CreateHomeworkActivity.class);
//            startActivity(intentHomework);
			mIntent = new Intent(getActivity(), JxHomeworkListActivity.class);
			mIntent.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
					JxHomeworkListActivity.SMSMESSAGETYPE_HOMEWORK);
			startActivity(mIntent);
            jiaPopwindow.dismiss();
			break;
        case R.id.create_notice_btn:
//            Intent intentNotice = new Intent(getActivity(), CreateNoticeActivity.class);
//            startActivity(intentNotice);
        	mIntent = new Intent(getActivity(), JxHomeworkListActivity.class);
			mIntent.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
					JxHomeworkListActivity.SMSMESSAGETYPE_NOTICE);
			startActivity(mIntent);
            jiaPopwindow.dismiss();
            break;
        case R.id.create_comment_btn:
//            Intent intentComment = new Intent(getActivity(), CreateCommentActivity.class);
//            startActivity(intentComment);
        	mIntent = new Intent(getActivity(), JxHomeworkListActivity.class);
			mIntent.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
					JxHomeworkListActivity.SMSMESSAGETYPE_COMMENT);
			startActivity(mIntent);
            jiaPopwindow.dismiss();
            break;
        case R.id.create_officesms_btn:
//            Intent intentOfficesms = new Intent(getActivity(), CreateOfficesmsActivity.class);
//            startActivity(intentOfficesms);
        	Intent intentOfficesms = new Intent(getActivity(), JxOfficesmsListActivity.class);
			startActivity(intentOfficesms);
            jiaPopwindow.dismiss();
            break;
        case R.id.create_page_close_btn:
        	jiaPopwindow.dismiss();
            break;
		}
	}
	
	private void initJiaPop() {
		View view = getActivity().getLayoutInflater().inflate(R.layout.activity_tab_jia, null);
		jiaPopwindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,  
                ViewGroup.LayoutParams.MATCH_PARENT,true);  
		jiaPopwindow.setOutsideTouchable(true); 
        ColorDrawable dw = new ColorDrawable(-00000);
        jiaPopwindow.setBackgroundDrawable(dw);
        jiaPopwindow.setAnimationStyle(R.style.popupWindowAnimation);
        jiaPopwindow.update();
        ((Button)view.findViewById(R.id.create_homework_btn)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.create_notice_btn)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.create_comment_btn)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.create_officesms_btn)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.create_page_close_btn)).setOnClickListener(this);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		ClazzTalkFragment.getInstence().onActivityResult(requestCode, resultCode, data);
	}
}