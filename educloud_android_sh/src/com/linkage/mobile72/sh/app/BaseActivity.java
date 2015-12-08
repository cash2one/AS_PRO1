package com.linkage.mobile72.sh.app;


import info.emm.messenger.VYCallBack;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.mobile72.sh.activity.manager.ActivityMgr;
import com.linkage.mobile72.sh.data.AccountChild;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends FragmentActivity  implements VYCallBack {
	//teacher 项目 添加
	public static final int REQUEST_FIRST_USER = 1;
	protected BaseApplication mApp;
	protected ImageLoader imageLoader,imageLoader_group;
	protected DisplayImageOptions defaultOptions,defaultOptionsGroup,defaultOptionsPhoto;
	protected RelativeLayout titleLayout;
	protected static final int PIC_TAKE_PHOTO = 1005;
	protected static final int REQ_EDIT_PHOTO = 1007;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActivityList.activitys.add(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mApp = BaseApplication.getInstance();
		imageLoader = mApp.imageLoader;
		imageLoader_group = mApp.imageLoader_group;
        defaultOptions = mApp.defaultOptions;
        defaultOptionsGroup = mApp.defaultOptionsGroup;
        defaultOptionsPhoto = mApp.defaultOptionsPhoto;
	}
	
	public void hideKeyboard(IBinder token) {
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(token, 0);
	}
	
	public DataHelper getDBHelper() {
		return  DataHelper.getHelper(BaseActivity.this); 
	}
	
	public AccountData getLastLoginAccount() {
		return BaseApplication.getInstance().getLastLogintAccount();
	}
	
	public AccountData getCurAccount() {
		return BaseApplication.getInstance().getDefaultAccount();
	}
	
	public String getAccountName() {
		AccountData account = getCurAccount();
		if (account != null) {
			return account.getLoginname();
		}
		return null;
	}
	
	public List<AccountChild> getAccountChild() {
		return BaseApplication.getInstance().getAccountChild();
	}
	
	public AccountChild getDefaultAccountChild() {
		List<AccountChild> childs = getAccountChild();
		AccountChild defaultChild = null;
		if(childs != null && childs.size() > 0) {
			if(childs.size() == 1) {
				defaultChild = childs.get(0);
			}else {
				for(AccountChild c : childs) {
					if(c.getDefaultChild() == 1) {
						defaultChild = c;
						break;
					}
				}
			}
		}
		return defaultChild;
	}
	
	public List<ClassRoom> getAccountClass() {
		return BaseApplication.getInstance().getClassRoom();
	}
	
	public ClassRoom getDefaultAccountClass() {
		List<ClassRoom> classs = getAccountClass();
		ClassRoom defaultClass = null;
		if(classs != null && classs.size() > 0) {
			if(classs.size() == 1) {
				defaultClass = classs.get(0);
			}else {
				for(ClassRoom c : classs) {
					if(c.getDefaultClass() == 1) {
						defaultClass = c;
						break;
					}
				}
			}
		}
		return defaultClass;
	}
	
	public boolean isTeacher() {			
		//教师版本返回
		return Consts.is_Teacher;
		//家长版本返回
		//return false;
	}
	
	public void setIsTeacher(boolean isTeacher){
		Consts.is_Teacher = isTeacher;
	}
	
	public void setTitle(int resId) {
		titleLayout = (RelativeLayout)findViewById(R.id.relativelayout1);
		if(isTeacher()) {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg_green);
		}else {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg);
		}
		TextView titleView = (TextView)findViewById(R.id.title);
		if(titleView != null)
			titleView.setText(resId);
	}
	
	public void setTitle(String title) {
		titleLayout = (RelativeLayout)findViewById(R.id.relativelayout1);
		if(isTeacher()) {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg_green);
		}else {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg);
		}
		TextView titleView = (TextView)findViewById(R.id.title);
		if(titleView != null)
			titleView.setText(title);
	}
	
	public void setTitleBg() {
		titleLayout = (RelativeLayout)findViewById(R.id.relativelayout1);
		if(isTeacher()) {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg_green);
		}else {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg);
		}
	}
	
	public String getTitleText() {
		TextView titleView = (TextView)findViewById(R.id.title);
		return titleView.getText().toString();
	}
	
	//强制显示或者关闭系统键盘
	public static void keyBoard(final EditText txtSearchKey, final String status, int time) {

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager m = (InputMethodManager) txtSearchKey
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				if (status.equals("open")) {
					m.showSoftInput(txtSearchKey,
							InputMethodManager.SHOW_FORCED);
				} else {
					m.hideSoftInputFromWindow(txtSearchKey.getWindowToken(), 0);
				}
			}
		}, time);
	}

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        ActivityMgr.getInstance().removeActivity(this);
        super.onDestroy();
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        
        ActivityMgr.getInstance().push(this);
        MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
    @Override
	public void onError(int arg0) {
		LogUtils.d("base, onError, arg0=" + arg0);
		
	}

	@Override
	public void onSuccess() {
		LogUtils.d("base, onSuccess");
	}
	
}
