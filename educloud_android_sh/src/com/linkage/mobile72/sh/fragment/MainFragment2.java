package com.linkage.mobile72.sh.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.mobile72.sh.activity.MainActivity;
import com.linkage.mobile72.sh.activity.TabJiaActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.fragment.NewMessageFragment.EventHandler;
import com.linkage.mobile72.sh.im.provider.Ws.MessageTable;
import com.linkage.mobile72.sh.im.provider.Ws.ThreadTable;
import com.linkage.mobile72.sh.R;


public class MainFragment2 extends BaseFragment implements EventHandler, OnCheckedChangeListener, OnTouchListener, View.OnClickListener {
	
	private RadioButton mTab1;
    private RadioButton mTab2;
    private RadioButton mTab3;
    private RadioButton mTab4;
    private RelativeLayout mJiaLayout, mJiaNumLayout;
    
    private FrameLayout mContainer;
    private TextView messageCount;
    public ImageView messageNotice;
    
    private FragmentPagerAdapter mFragmentPagerAdapter;
//    private SlidingMenu slideMenu;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        
        mFragmentPagerAdapter = new FragmentPagerAdapter(
                getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                case R.id.tab_message:
                	return MessageFragment.create(R.string.tab_txt_message);
                case R.id.tab_jx:
            		return JxParentFragment.newInstance();
//              case R.id.tab_contacts:
//                  return ContactsFragment.create(R.string.tab_txt_contact);
                case R.id.tab_contacts:
					return ClazzTalkFragment.create(R.string.tab_txt_clazz);
                case R.id.tab_app:
                    return AppFragment.create(R.string.tab_txt_app);
                default:
                	return MessageFragment.create(R.string.tab_txt_message);
                }
            }
            @Override
            public int getCount() {
                return 3;
            }
        };
        NewMessageFragment.ehList.add(this);
        
//        slideMenu = ((MainActivity)getActivity()).getSlidingMenu();
    }  
    @Override
	public void onMessage(int num) {
		notifyMessageCount();
	}
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		NewMessageFragment.ehList.remove(this);
		if (getActivity() != null) {
//			getActivity().getContentResolver().unregisterContentObserver(contentObserver);
			getActivity().unregisterReceiver(receiver);
		}
	}
  
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	// TODO Auto-generated method stub
    	View view = View.inflate(getActivity(), R.layout.fragment_main, null);
    	view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(MainActivity.slideMenu.isMenuShowing()) {
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
//        mTab3 = (RadioButton) view.findViewById(R.id.tab_contacts);
        mTab3 = (RadioButton) view.findViewById(R.id.tab_app);
        mTab4 = (RadioButton) view.findViewById(R.id.tab_contacts);
        mTab3.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.tab_learn), null, null);
        mTab3.setText("学习");
        mContainer = (FrameLayout) view.findViewById(R.id.content);
        mTab1.setOnCheckedChangeListener(this);
        mTab2.setOnCheckedChangeListener(this);
        mTab3.setOnCheckedChangeListener(this);
        mTab4.setOnCheckedChangeListener(this);
        //mTab4.setOnCheckedChangeListener(this);
        mJiaLayout = (RelativeLayout)view.findViewById(R.id.tab_jia_layout);
        mJiaNumLayout = (RelativeLayout)view.findViewById(R.id.jia_layout_num);
        mJiaLayout.setVisibility(View.GONE);
        mJiaNumLayout.setVisibility(View.GONE);
        messageCount = (TextView) view.findViewById(R.id.message_num);
        messageNotice = (ImageView) view.findViewById(R.id.contacts_num);
        
        mTab2.performClick();
        
    	return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	notifyMessageCount();
		getActivity().registerReceiver(receiver, new IntentFilter(ThreadTable.CONTENT_CHAGED));
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			notifyMessageCount();
		}
	};
  
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
        	/*if (buttonView.getId() == R.id.tab_jx && getCurAccount().getOrigin() != 2) {
				T.showShort(getActivity(), "您不是校讯通用户，还不能使用该功能");
				return;
			}*/
            Fragment fragment = (Fragment) mFragmentPagerAdapter
                    .instantiateItem(mContainer, buttonView.getId());
            mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
            mFragmentPagerAdapter.finishUpdate(mContainer);
        }
    }
  
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
    	//更新未读数
        notifyMessageCount();
        
        SharedPreferences sp = BaseApplication.getInstance().getSharedPreferences(""+getDefaultAccountChild().getId(), Context.MODE_PRIVATE);
        int i = sp.getInt("attend_notice", 0);
        if(i == 1) {
        	messageNotice.setVisibility(View.VISIBLE);
        }else {
        	messageNotice.setVisibility(View.INVISIBLE);
        }
	}
	
	public void notifyMessageCount() {
		//messageNotice.setVisibility(View.VISIBLE);
		int unreadNum = 0;
		Cursor threadCursor = null;
		try {
			threadCursor = getActivity().getContentResolver().query(ThreadTable.CONTENT_URI, null, 
					MessageTable.ACCOUNT_NAME + "=?",
					new String[] { mApp.mCurAccount.getLoginname() },
					ThreadTable.MSG_SENT_TIME + " desc");
			while(threadCursor.moveToNext()) {
				unreadNum = unreadNum + threadCursor.getInt(threadCursor.getColumnIndexOrThrow(ThreadTable.UNREAD_COUNT));
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
		if(MainActivity.slideMenu.isMenuShowing()) {
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
        switch (v.getId()) {
            case R.id.tab_jia:
                Intent i = new Intent(getActivity(), TabJiaActivity.class);
                startActivity(i);
                break;
        }
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		ClazzTalkFragment.getInstence().onActivityResult(requestCode, resultCode, data);
	}
}
