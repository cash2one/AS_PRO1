package com.linkage.mobile72.sh.adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.lib.util.LogUtils;

public class TalkGridVAdapter extends BaseAdapter {

	private Context context;
	private List<String> data;
	private Utils util;

	public TalkGridVAdapter(Context context, List<String> data) {
		this.context = context;
		this.data = data;
		this.util = new Utils(context);
	}

	public void addData(List<String> list, boolean append) {
		if (list != null && list.size() > 0) {
			if (!append)
				data.clear();
			data.addAll(list);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// if (data == null || data.size() == 0) {

		LogUtils.e("data:" + data);

		if (data == null) {
			return 0;
		} else if (data.size() == 0) {
			return 1;
		} else if (data.size() < 9) {
			return data.size() + 1;// 1-7张的加个加号
		} else {
			return 9;
		}
	}

	@Override
	public String getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Holder holder;

		if (convertView == null || convertView.getTag() == null) {

			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_tk_grid, parent, false);

			holder = new Holder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView1);

			// LayoutParams rlyParams = (LayoutParams) holder.imageView
			// .getLayoutParams();
			//
			// LogUtils.e("tk---->w,h:" + rlyParams.width + ", "
			// + rlyParams.height);

			convertView.setTag(holder);

		} else {

			holder = (Holder) convertView.getTag();

		}

		try {

			LogUtils.e("position:" + position + " data:" + data);

			if (position != data.size()) {
				// Bitmap bitmap = util.getPathBitmap(
				// Uri.fromFile(new File(getItem(position))), 230, 172);
				Bitmap bitmap = util.getPathBitmap(
						Uri.fromFile(new File(getItem(position))), 214, 214);
				if (bitmap != null)
					holder.imageView.setImageBitmap(bitmap);
				holder.imageView.setScaleType(ScaleType.CENTER_CROP);

			} else {

				// holder.imageView.setImageResource(R.drawable.jxhd_pic_add);
				holder.imageView
						.setImageResource(R.drawable.tk_add_pic_selector);
				holder.imageView.setScaleType(ScaleType.FIT_XY);

			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		return convertView;
	}

	class Holder {
		ImageView imageView;
	}

}
