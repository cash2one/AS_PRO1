package com.linkage.mobile72.sh.fragment;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.AppBean;
import com.linkage.mobile72.sh.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Listview_HeadView extends View{
	
	private int[] imgIds;
	private ImageView[] tips;
	private List<View> mListViews;
	private ViewPager mImageSwitcher;
	private LinearLayout installAppLayout, linearLayout, linearinstalled;
	private Context mContext;
	//private List<AppData> installedApps;
	private ImageLoader imageLoader;
	
	public Listview_HeadView(Context context) {
		super(context);
		mContext = context;
	}
	
	public View RetView(List<AppBean> installedApps){
		//this.installedApps = installedApps;
		imageLoader = ImageLoader.getInstance();
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc().showStubImage(R.drawable.ic_launcher).showImageForEmptyUri(R.drawable.ic_launcher).showImageOnFail(R.drawable.ic_launcher).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).defaultDisplayImageOptions(defaultOptions).build();
		imageLoader.init(config);
		View viewsub = View.inflate(mContext, R.layout.head_fragment_app, null);
		installAppLayout = (LinearLayout)viewsub.findViewById(R.id.install_app_layout);
		linearinstalled = (LinearLayout) viewsub.findViewById(R.id.layout_app_installed);
		mImageSwitcher = (ViewPager) viewsub.findViewById(R.id.imageSwitcher1);
		linearLayout = (LinearLayout) viewsub.findViewById(R.id.viewGroup);
		
		mListViews = new ArrayList<View>();
		imgIds = new int[] { R.drawable.app_one, R.drawable.app_one, R.drawable.app_one };
		tips = new ImageView[imgIds.length];
		/*
		 * 初始化list存放view
		 */
		for(int i = 0;i <imgIds.length;i++){
			View topimage = new View(mContext);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			topimage.setBackgroundResource(imgIds[i]);
			topimage.setLayoutParams(layoutParams);
			mListViews.add(topimage);
		}
		/*
		 * 圆点************
		 */
		for (int i = 0; i < imgIds.length; i++) {
			ImageView mImageView = new ImageView(mContext);

			tips[i] = mImageView;
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			
			layoutParams.rightMargin = 5;
			layoutParams.leftMargin = 5;
			layoutParams.bottomMargin = 5;
			layoutParams.width = 17;
			layoutParams.height = 17;
			mImageView.setBackgroundResource(R.drawable.dot);
			linearLayout.addView(mImageView, layoutParams);
		}
		// handler.sendEmptyMessageDelayed(1, 2000);
		setImageBackground(0);
		/*
		 * 适配器**************
		 */
		mImageSwitcher.setAdapter(new PagerAdapter() {
			
			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				// TODO Auto-generated method stub
				container.removeView(mListViews.get(position));
			}
			
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				// TODO Auto-generated method stub
				container.addView(mListViews.get(position));
				return mListViews.get(position);
			}
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mListViews.size();
			}
		});
		/*
		 * 监听
		 */
		
		mImageSwitcher.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				setImageBackground(position%tips.length);	
				if ( tips.length > 1) { //多于1，才会循环跳转  
	                if ( position < 0) { //首位之前，跳转到末尾（N）  
	                	position = tips.length - 1;   
	                	mImageSwitcher.setCurrentItem(position, false);  
	                } else if ( position > tips.length - 1) { //末位之后，跳转到首位（1）  
	                	mImageSwitcher.setCurrentItem(1, false); //false:不显示跳转过程的动画  
	                    position = 1;  
	                }  else {
	                	mImageSwitcher.setCurrentItem(position, false);
	                }
	            }
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
		
		mImageSwitcher.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				arg0.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		mImageSwitcher.setCurrentItem(0);
		/*
		 * 已安装应用
		 */
		if(installedApps != null && installedApps.size() > 0) {
			installAppLayout.setVisibility(View.VISIBLE);
			for(AppBean app : installedApps) {
				setInstalledView(app); 
			}
		}else {
			installAppLayout.setVisibility(View.GONE);
		}
		return viewsub;
	}
	
	
	private void setImageBackground(int selectItems) {
		for (int i = 0; i < tips.length; i++) {
			if (i == selectItems) {
				tips[i].setBackgroundResource(R.drawable.dot_select);
			} else {
				tips[i].setBackgroundResource(R.drawable.dot);
			}
		}
	}
	
	private void setInstalledView(AppBean appBean) {
		LinearLayout newone = (LinearLayout) View.inflate(mContext,
				R.layout.item_app_installed, null);
		ImageView imgapp_installed1 = (ImageView) newone
				.findViewById(R.id.image_app_item);
		Drawable appDrawable = Utils.getAppDrawable(mContext, appBean.getAppLauncherPath());
		if(appDrawable == null) {
			imageLoader.displayImage(appBean.getAppLogo(), imgapp_installed1);
		}else {
			imgapp_installed1.setImageDrawable(appDrawable);
		}
		TextView txtapp_installed1 = (TextView) newone
				.findViewById(R.id.text_app_item);
		txtapp_installed1.setText(appBean.getAppName());
		newone.setClickable(true);
		newone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		linearinstalled.addView(newone);
	}
}
