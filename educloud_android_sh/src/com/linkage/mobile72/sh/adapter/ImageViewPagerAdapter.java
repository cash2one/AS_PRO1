package com.linkage.mobile72.sh.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.linkage.mobile72.sh.widget.phview.PhotoView;


public class ImageViewPagerAdapter extends PagerAdapter {

	//ArrayList<MatrixImageView> _maImageViews;
	ArrayList<PhotoView> _maImageViews;
	
//	public ImageViewPagerAdapter(ArrayList<MatrixImageView> maImageViews){
	public ImageViewPagerAdapter(ArrayList<PhotoView> maImageViews){
		this._maImageViews = maImageViews;
	}
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _maImageViews.size();
	}

	// 是从ViewGroup中移出当前View
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(_maImageViews.get(arg1));
	}

	// 返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(_maImageViews.get(arg1));
		return _maImageViews.get(arg1);
	}


}
