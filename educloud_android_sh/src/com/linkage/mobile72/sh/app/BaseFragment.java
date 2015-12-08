package com.linkage.mobile72.sh.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.AccountChild;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class BaseFragment extends Fragment {

	protected BaseApplication mApp;
    protected ImageLoader imageLoader,imageLoader_group;
    protected DisplayImageOptions defaultOptions,defaultOptionsGroup,defaultOptionsPhoto;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mApp = BaseApplication.getInstance();
        imageLoader = mApp.imageLoader;
        imageLoader_group = mApp.imageLoader_group;
        defaultOptions = mApp.defaultOptions;
        defaultOptionsGroup = mApp.defaultOptionsGroup;
        defaultOptionsPhoto = mApp.defaultOptionsPhoto;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	protected View findViewById(int id) {
		View v = getActivity().findViewById(id);
		if (v != null)
			return v;
		return null;
	}
	
	public DataHelper getDBHelper() {
		return DataHelper.getHelper(getActivity()); 
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
	
	public String getChatAccount() {
		
		return mApp.getChatUserId();
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
	
	public void setTitle(View view, int resId) {
		RelativeLayout titleLayout = (RelativeLayout)view.findViewById(R.id.relativelayout1);
		if(isTeacher()) {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg_green);
		}else {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg);
		}
		TextView titleView = (TextView)view.findViewById(R.id.title);
		if(titleView != null)
			titleView.setText(resId);
	}
	
	public void setTitle(View view, String title) {
		RelativeLayout titleLayout = (RelativeLayout)view.findViewById(R.id.relativelayout1);
		if(isTeacher()) {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg_green);
		}else {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg);
		}
		TextView titleView = (TextView)view.findViewById(R.id.title);
		if(titleView != null)
			titleView.setText(title);
	}
	
	public void setTitleBg(View view) {
		RelativeLayout titleLayout = (RelativeLayout)view.findViewById(R.id.relativelayout1);
		if(isTeacher()) {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg_green);
		}else {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg);
		}
	}
	
	@Override
	public void setMenuVisibility(boolean menuVisible) {
	    super.setMenuVisibility(menuVisible);
	    if (this.getView() != null)
	        this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
}
