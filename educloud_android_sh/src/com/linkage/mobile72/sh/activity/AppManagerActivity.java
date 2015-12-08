package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.data.http.AppBean;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.ui.widget.swipelistview.SwipeListView;

public class AppManagerActivity extends BaseActivity implements OnClickListener {

	private SwipeListView mSwipeListView;
	private MyAdapter mAdapter;
	private List<AppBean> list_adapter;
//	private int deleteWidth;
	private int choicedItem = -1;
	private MyCommonDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		setTitle("应用管理");
		findViewById(R.id.back).setOnClickListener(this);

		mSwipeListView = (SwipeListView) findViewById(R.id.swipeListView);
		mSwipeListView.setOffsetLeft(this.getResources().getDisplayMetrics().widthPixels * 2 / 3);
		list_adapter = new ArrayList<AppBean>();
		mAdapter = new MyAdapter(this, list_adapter);
		mSwipeListView.setAdapter(mAdapter);
//		deleteWidth = getResources().getDisplayMetrics().widthPixels / 3;
//		mSwipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
//			@Override
//			public void onOpened(int position, boolean toRight) {
//				super.onOpened(position, toRight);
//				if (choicedItem != -1 && choicedItem != position) {
//					mSwipeListView.closeAnimate(choicedItem);
//				}
//				choicedItem = position;
//			}
//		});

	}

	class MyAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;
		private List<AppBean> mData;

		public MyAdapter(Context context, List<AppBean> objs) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
			mData = objs;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_app_manager, null);
				viewHolder = new ViewHolder();

				viewHolder.app_logo = (ImageView) convertView.findViewById(R.id.image_newapp);
				viewHolder.app_name = (TextView) convertView.findViewById(R.id.newapp_name);
				viewHolder.app_info = (TextView) convertView.findViewById(R.id.newapp_info);
				viewHolder.deleteBtn = (ImageView) convertView.findViewById(R.id.delapp);
//				viewHolder.deleteBtn.getLayoutParams().width = deleteWidth;
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final AppBean app = getItem(position);
			if(app.getAppType() == 1) {
				PackageInfo packageInfo = Utils.getPackageInfoByName(mContext,
						(app.getAppLauncherPath()));
				if (packageInfo != null) {
					Drawable appIconDrawable = packageInfo.applicationInfo.loadIcon(mContext
							.getPackageManager());
					viewHolder.app_logo.setImageDrawable(appIconDrawable);
				}
			}else {
				imageLoader.displayImage(app.getAppLogo(), viewHolder.app_logo, defaultOptionsPhoto);
			}
			viewHolder.app_name.setText(app.getAppName());
			viewHolder.app_info.setText(app.getAppIntroduce());
			viewHolder.deleteBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
//					mSwipeListView.closeAnimate(choicedItem);
					if(app.getAppType() == 1) {
						Uri packageURI = Uri.parse("package:" + app.getAppLauncherPath());
						Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
						uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(uninstallIntent);
					}else {
						mDialog = new MyCommonDialog(mContext, "提示消息", "您确定删除"+app.getAppName()+"？",
								"取消", "确定");
						mDialog.setCancelable(true);
						mDialog.setCancelListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (mDialog.isShowing()) {
									mDialog.dismiss();
								}
							}
						});
						mDialog.setOkListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (mDialog.isShowing()) {
									mDialog.dismiss();
									app.setInstalled(0);
									//refreshInstalledApp();
								}
							}
						});
						mDialog.show();
					}
				}
			});
			return convertView;
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public AppBean getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			private ImageView app_logo;
			private TextView app_name;
			private TextView app_info;
			private ImageView deleteBtn;
		}
	}

	/*private void refreshInstalledApp() {
		try {
			List<AppBean> list = getDBHelper().getAppDataDao().queryForAll();
			list_adapter.clear();
			for (int i = 0; i < list.size(); i++) {
				AppBean appBean = list.get(i);
				if(appBean.getAppType() == 1) {
					if (Utils.checkApkExist(this, appBean.getAppLauncherPath())) {
						appBean.setInstalled(1);
						list_adapter.add(appBean);
					}else {
						appBean.setInstalled(0);
					}
				}else {
					if(appBean.getInstalled() == 1) {
						list_adapter.add(appBean);
	                }
				}
				try {
					getDBHelper().getAppDataDao().createOrUpdate(appBean);
				} catch (java.sql.SQLException e) {
					e.printStackTrace();
				}
			}
			mAdapter.notifyDataSetChanged();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}*/
	
	@Override
	protected void onResume() {
		super.onResume();
		//refreshInstalledApp();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}
}