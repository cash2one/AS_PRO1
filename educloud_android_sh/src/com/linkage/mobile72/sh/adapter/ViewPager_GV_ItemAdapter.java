package com.linkage.mobile72.sh.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.AppBean;
import com.linkage.mobile72.sh.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ViewPager_GV_ItemAdapter extends BaseAdapter {

	private Context mContext;
	private List<AppBean> apps;
	private int mPageSize;
    private ImageLoader imageLoaderPhoto;
    private DisplayImageOptions defaultOptionsPhoto;

	public ViewPager_GV_ItemAdapter(Context context, ImageLoader imageLoaderPhoto, DisplayImageOptions defaultOptionsPhoto, List<AppBean> list, int index, int pageSize) {
		mContext = context;
		mPageSize = pageSize;
		apps = new ArrayList<AppBean>();
		int start = index * mPageSize;
		int end = start + mPageSize;
		if (end > list.size()) {
			end = list.size();
		}
		for (int i = start; i < end; i++) {
			apps.add(list.get(i));
		}
        this.imageLoaderPhoto = imageLoaderPhoto;
        this.defaultOptionsPhoto = defaultOptionsPhoto;
	}

	@Override
	public int getCount() {
		return apps.size();
	}

	@Override
	public AppBean getItem(int arg0) {
		return apps.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_app_installed_item, parent, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
			holder.textView = (TextView) convertView.findViewById(R.id.textView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AppBean app = getItem(position);
		if (app != null) {
            /*if(app.getAppType() == 1) {
                PackageInfo packageInfo = Utils.getPackageInfoByName(mContext,
                        (app.getAppLauncherPath()));
                if (packageInfo != null) {
                    Drawable appIconDrawable = packageInfo.applicationInfo.loadIcon(mContext
                            .getPackageManager());
                    holder.imageView.setImageDrawable(appIconDrawable);
                }
            }else {*/
                imageLoaderPhoto.displayImage(app.getAppLogo(), holder.imageView, defaultOptionsPhoto);
            //}
			holder.textView.setText(app.getAppName());
		}
		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView textView;
	}
}