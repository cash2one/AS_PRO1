package com.linkage.mobile72.sh.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.AppDetailActivity;
import com.linkage.mobile72.sh.activity.AppLaunchActivity;
import com.linkage.mobile72.sh.activity.ConfirmPaymentActivity;
import com.linkage.mobile72.sh.activity.WebViewActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.AppBean;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.fragment.AppFragment;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.AppsUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.gson.JsonObject;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class AppListAdapter extends BaseAdapter {

    private static final String TAG = AppListAdapter.class.getSimpleName();
	private Context mContext;
	private LayoutInflater mInflater;
	private List<AppBean> mData;
	private ImageLoader mImageLoader;
    private DisplayImageOptions defaultOptionsPhoto;
    private MyCommonDialog dialog;

	public AppListAdapter(Context context, DataHelper helper, ImageLoader imageLoader, DisplayImageOptions defaultOptionsPhoto,
			List<AppBean> apps, PullToRefreshListView list_newapp) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mImageLoader = imageLoader;
		mData = apps;
		this.defaultOptionsPhoto = new DisplayImageOptions.Builder().cacheOnDisc().cacheInMemory()
                .showStubImage(R.drawable.appdetail_icon_def)
                .showImageForEmptyUri(R.drawable.appdetail_icon_def)
                .showImageOnFail(R.drawable.appdetail_icon_def).build();
	}

	public void addAll(List<AppBean> data, boolean append) {
		if (mData != null) {
			if (!append) {
				mData.clear();
			}
			mData.addAll(data);
		} else {
			mData = data;
		}
		refreshData();
	}

	// 实时刷新是否安装
	public void refreshData() {
		/*if(local){//本地刷新 就再查一遍
			try {
				mData = helper.getAppDataDao().queryForAll();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}*/
		for (int i = 0; i < mData.size(); i++) {
            AppBean appBean = mData.get(i);
            if (appBean.getAppType() == 1) {
                if (Utils.checkApkExist(mContext, appBean.getAppLauncherPath())) {
                    appBean.setInstalled(1);
                } else {
                    appBean.setInstalled(0);
                }
            }
		}
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return mData.get(position).getAppId();
	}

	@Override
	public AppBean getItem(int position) {
		return mData.get(position);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_newapp, null);
			viewHolder = new ViewHolder();
			viewHolder.app_logo = (ImageView) convertView.findViewById(R.id.image_newapp);
			viewHolder.app_name = (TextView) convertView.findViewById(R.id.newapp_name);
			viewHolder.app_info = (TextView) convertView.findViewById(R.id.newapp_info);
			viewHolder.app_downnum = (TextView) convertView.findViewById(R.id.newapp_downnum);
			viewHolder.app_price = (TextView) convertView.findViewById(R.id.app_price);
			viewHolder.app_btn = (Button) convertView.findViewById(R.id.btn_newapp);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// image_newapp.setBackgroundResource(app_viewitem[arg0]);
		final AppBean app = getItem(position);
		Log.e("app.info", app.getAppIntroduce());
		mImageLoader.displayImage(app.getAppLogo(), viewHolder.app_logo, defaultOptionsPhoto);
		viewHolder.app_name.setText(app.getAppName());
		viewHolder.app_info.setText(app.getAppDesc());
		viewHolder.app_downnum.setText("下载："+app.getAppDownNum());
		int priceType = Integer.parseInt(app.getPrice_type());
		if(StringUtils.isEmpty(app.getAppToken()) && app.getSourceId() != AppBean.COLLECT_APP) {//未授权
			viewHolder.app_price.setVisibility(View.VISIBLE);
			if(priceType == 1) {//点播
				viewHolder.app_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); 
				viewHolder.app_price.setText("￥" + app.getAppPrice());
				if(app.getInapp() == 1) {//应用内
					viewHolder.app_btn.setText(app.getInapp_notice());
				}else {
					viewHolder.app_btn.setText("￥" + app.getAppPrice_me());
				}
			}else if(priceType == 2){//包月
				viewHolder.app_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); 
				viewHolder.app_price.setText("￥" + app.getAppPrice() + "/月");
				if(app.getInapp() == 1) {//应用内
					viewHolder.app_btn.setText(app.getInapp_notice());
				}else {
					viewHolder.app_btn.setText("￥" + app.getAppPrice_me() + "/月");
				}
			}
			viewHolder.app_btn.setBackgroundResource(R.drawable.app_download_bg);
			viewHolder.app_btn.setTextColor(mContext.getResources().getColor(android.R.color.black));
		}else {
			viewHolder.app_price.setVisibility(View.GONE);
			viewHolder.app_btn.setBackgroundResource(R.drawable.app_list_item_btn);
			viewHolder.app_btn.setTextColor(mContext.getResources().getColor(android.R.color.white));
			if(app.getAppType() == 1) {
				if(Utils.checkApkExist(mContext, app.getAppLauncherPath())) {
					viewHolder.app_btn.setText("打开");
				}else {
					viewHolder.app_btn.setText("下载");
				}
			}else {
				viewHolder.app_btn.setText("打开");
			}
		}
		viewHolder.app_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(mContext, Consts.CLICK_APP_OPEN);
				if(AppFragment.isLoading) return;
				if(StringUtils.isEmpty(app.getAppToken()) && app.getSourceId() != AppBean.COLLECT_APP) {
					double price = Double.parseDouble(app.getAppPrice_me());
					if(price > 0) {
						Intent i = new Intent(mContext, ConfirmPaymentActivity.class);
						Bundle b = new Bundle();
                        b.putSerializable("APP", app);
                        i.putExtras(b);
						mContext.startActivity(i);
					}else {
							Intent i = new Intent(mContext, AppLaunchActivity.class);
							Bundle b = new Bundle();
							b.putSerializable("APP", app);
							i.putExtras(b);
							mContext.startActivity(i);
					}
				}else {
					if(app.getSourceId() == AppBean.COLLECT_APP){
						AppsUtils.startCollectApp(mContext, app);
						return;
					} 
					if(app.getAppType() == 1) {//APP
						if(Utils.checkApkExist(mContext, app.getAppLauncherPath())) {
							AppsUtils.refreshScore(mContext, app.getId());
							Map<String, Object> params = new HashMap<String, Object>();
                        	params.put("appToken", app.getAppToken());
                        	LogUtils.e("*****************appToken**" + app.getAppToken()+"");
							if(TextUtils.isEmpty(app.getAppLauncherUrl())) {
                        		Utils.runAppByParam(mContext, app.getAppLauncherPath(), params);
                        	}else {
                            	Utils.runAppByParam(mContext, app.getAppLauncherPath(), app.getAppLauncherUrl(), params);
                        	}
						}else {
							if (app.getAppUrl()!= null && !"null".equals(app.getAppUrl()) && !TextUtils.isEmpty(app.getAppUrl())) {
								mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(app.getAppUrl())));
//								dialog = new MyCommonDialog(mContext, "下载提示", "您可以通过和校园140M的专属流量下载该应用", "取消", "确定");
//								dialog.setOkListener(new OnClickListener() {
//									@Override
//									public void onClick(View v) {
//										if(dialog != null && dialog.isShowing()) {
//											dialog.dismiss();
//										}
//										
//									}
//								});
//								dialog.setCancelListener(new OnClickListener() {
//									@Override
//									public void onClick(View v) {
//										if(dialog != null && dialog.isShowing()) {
//											dialog.dismiss();
//										}
//									}
//								});
//								dialog.show();
							}else {
								UIUtilities.showToast(mContext, "下载地址不正确");
							}
						}
					}else {//H5
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
		});
		return convertView;
	}

	class ViewHolder {
		private ImageView app_logo;
		private TextView app_name;
		private TextView app_info;
		private TextView app_downnum;
		private TextView app_price;
		private Button app_btn;
	}
	
}