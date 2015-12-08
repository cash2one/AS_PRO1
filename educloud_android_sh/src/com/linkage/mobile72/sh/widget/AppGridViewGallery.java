package com.linkage.mobile72.sh.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.AppLaunchActivity;
import com.linkage.mobile72.sh.activity.WebViewActivity;
import com.linkage.mobile72.sh.adapter.ViewPager_GV_ItemAdapter;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.AppBean;
import com.linkage.mobile72.sh.fragment.AppFragment;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.AppsUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * GridView滑动
 */

public class AppGridViewGallery extends LinearLayout {

    private static final String TAG = AppGridViewGallery.class.getSimpleName();
    
	private Context mContext;
    private ImageLoader imageLoaderPhoto;
    private DisplayImageOptions defaultOptionsPhoto;
	private List<AppBean> mData;
	private ViewPager mViewPager;
	private LinearLayout ll_dot;
	private ImageView[] dots;
	private int currentIndex = 0;
	private int viewPager_size;
	private int pageSize = 8;
	private MyCommonDialog dialog;
	
	/** 保存每个页面的GridView视图 */
	private List<View> mPageViews;

	public AppGridViewGallery(Context context, List<AppBean> list) {
		super(context);
		mContext = context;
		mData = list;
		initView();
		setAdapter();
        imageLoaderPhoto = ImageLoader.getInstance();
        defaultOptionsPhoto = new DisplayImageOptions.Builder().cacheOnDisc().cacheInMemory()
                .showStubImage(R.drawable.appdetail_icon_def)
                .showImageForEmptyUri(R.drawable.appdetail_icon_def)
                .showImageOnFail(R.drawable.appdetail_icon_def).build();
	}

	private void initView() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.fragment_app_installed_viewpager, null);
		mViewPager = (ViewPager) view.findViewById(R.id.vPager);
		ll_dot = (LinearLayout) view.findViewById(R.id.tips);
		addView(view);
	}

	private void setAdapter() {
		refreshDots();
		mPageViews = new ArrayList<View>();
		ViewPagerViewAdapter mViewPagerAdapter = new ViewPagerViewAdapter(mPageViews);
		mViewPager.setAdapter(mViewPagerAdapter);
	}

	public void refresh(List<AppBean> list) {
        this.mData = list;
        LogUtils.e("AppGridViewGallery.refresh.list.size():" + list.size());
        refreshDots();
		// 动态改变区域高度
		if (mData.size() == 0) {
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, 0));
			mViewPager.setLayoutParams(layoutParams);
		} else if (mData.size() <= 4) {
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, Utils.dp2px(mContext, 75)));
			mViewPager.setLayoutParams(layoutParams);
		} else {
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, Utils.dp2px(mContext, 150)));
			mViewPager.setLayoutParams(layoutParams);
		}
		if (viewPager_size > 0) {
			mPageViews.clear();
			for (int i = 0; i < viewPager_size; i++) {
				mPageViews.add(getViewPagerItem(i));
			}
			ViewPagerViewAdapter mViewPagerAdapter = new ViewPagerViewAdapter(mPageViews);
			mViewPager.setAdapter(mViewPagerAdapter);
		}
	}

	private View getViewPagerItem(int index) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.fragment_app_installed, null);
		GridView gridView = (GridView) layout.findViewById(R.id.gridview);

		ViewPager_GV_ItemAdapter adapter = new ViewPager_GV_ItemAdapter(mContext, imageLoaderPhoto, defaultOptionsPhoto, mData, index,
				pageSize);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mData.get(position + currentIndex * pageSize) != null) {
					final AppBean app = mData.get(position + currentIndex * pageSize);
                    if(app.getAppType() == 1) {
                        if (StringUtils.isEmpty(app.getAppToken())) {
                            Intent i = new Intent(mContext, AppLaunchActivity.class);
                            Bundle b = new Bundle();
                            b.putSerializable("APP", app);
                            i.putExtras(b);
                            mContext.startActivity(i);
                        } else {
                        	AppsUtils.refreshScore(mContext, app.getId());
                        	Map<String, Object> params = new HashMap<String, Object>();
                        	params.put("appToken", app.getAppToken());
                        	LogUtils.e("*****************appToken**" + app.getAppToken()+"");
                        	if(Utils.checkApkExist(mContext, app.getAppLauncherPath())) {
                        		if(TextUtils.isEmpty(app.getAppLauncherUrl())) {
                            		Utils.runAppByParam(mContext, app.getAppLauncherPath(), params);
                            	}else {
                                	Utils.runAppByParam(mContext, app.getAppLauncherPath(), app.getAppLauncherUrl(), params);
                            	}
                        	}else {
                        		if (app.getAppUrl()!= null && !"null".equals(app.getAppUrl()) && !TextUtils.isEmpty(app.getAppUrl())) {
                        			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(app.getAppUrl())));
//                        			dialog = new MyCommonDialog(mContext, "下载提示", "您可以通过和校园140M的专属流量下载该应用", "取消", "确定");
//    								dialog.setOkListener(new OnClickListener() {
//    									@Override
//    									public void onClick(View v) {
//    										if(dialog != null && dialog.isShowing()) {
//    											dialog.dismiss();
//    										}
//    										
//    									}
//    								});
//    								dialog.setCancelListener(new OnClickListener() {
//    									@Override
//    									public void onClick(View v) {
//    										if(dialog != null && dialog.isShowing()) {
//    											dialog.dismiss();
//    										}
//    									}
//    								});
//    								dialog.show();
    							}else {
    								UIUtilities.showToast(mContext, "下载地址不正确");
    							}
                        	}
                        }
                    }else {
                    	if(app.getSourceId() == AppBean.COLLECT_APP){
                    		AppsUtils.startCollectApp(mContext, app);
    						return;
    					} 
                    	if(app.getAppAuth() == 1 && StringUtils.isEmpty(app.getAppToken())) {//需要授权 并且授权token为空
    						Intent i = new Intent(mContext, AppLaunchActivity.class);
    						Bundle b = new Bundle();
    						b.putSerializable("APP", app);
    						i.putExtras(b);
    						mContext.startActivity(i);
    					}else {
    						AppsUtils.refreshScore(mContext, app.getId());
    						Intent mIntent = new Intent(mContext, WebViewActivity.class);
    						mIntent.putExtra(WebViewActivity.KEY_URL, app.getAppLauncherUrl());
    						mIntent.putExtra(WebViewActivity.KEY_TITLE, app.getAppName());
    						if(app.getAppAuth() == 1)
    							mIntent.putExtra(WebViewActivity.KEY_TOKEN, app.getAppToken());
    						mContext.startActivity(mIntent);
    					}
                    }
				}
			}
		});
		return layout;
	}

	// 初始化底部小圆点
	private void refreshDots() {
		viewPager_size = (int) Math.ceil((double) mData.size() / (double) pageSize);
		ll_dot.removeAllViews();
		if (viewPager_size == 0 || viewPager_size == 1) {
			ll_dot.setVisibility(View.GONE);
		} else if (viewPager_size > 1) {
			ll_dot.setVisibility(View.VISIBLE);
			for (int j = 0; j < viewPager_size; j++) {
				ImageView image = new ImageView(mContext);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
				params.setMargins(3, 0, 3, 0);
				image.setBackgroundResource(R.drawable.dot);
				ll_dot.addView(image, params);
			}
		}

		if (viewPager_size > 1) {
			dots = new ImageView[viewPager_size];
			for (int i = 0; i < viewPager_size; i++) {
				dots[i] = (ImageView) ll_dot.getChildAt(i);
				dots[i].setEnabled(true);
				dots[i].setTag(i);
			}
			currentIndex = 0;
			dots[currentIndex].setEnabled(false);
			dots[currentIndex].setBackgroundResource(R.drawable.dot_select);
			mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageSelected(int arg0) {
					setCurDot(arg0);
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
		}
	}

	/** 当前底部小圆点 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > viewPager_size - 1 || currentIndex == positon) {
			return;
		}
		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);
		dots[positon].setBackgroundResource(R.drawable.dot_select);
		dots[currentIndex].setBackgroundResource(R.drawable.dot);
		currentIndex = positon;
	}

	class ViewPagerViewAdapter extends PagerAdapter {

		private List<View> mListViews;

		public ViewPagerViewAdapter(List<View> list) {
			mListViews = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if (mListViews.size() > position) {
				container.removeView(mListViews.get(position));
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}
	
}