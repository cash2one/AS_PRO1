package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.Region;

public class HorizontalDoubleListAdapter extends BaseAdapter {

	Context context;
	LayoutInflater inflater;
	List<Region> region;
	int last_item;
	private int selectedPosition = -1;

	public HorizontalDoubleListAdapter(Context context, List<Region> region) {
		this.context = context;
		this.region = region;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return region == null ? 0 : region.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return region == null ? null : region.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return region == null ? 0 : region.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.adapter_single_words, null);
			holder = new ViewHolder();
			holder.textView = (TextView) convertView
					.findViewById(R.id.text1);
			holder.layout = (RelativeLayout) convertView
					.findViewById(R.id.relativelayout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 设置选中效果
		if (selectedPosition == position) {
			holder.layout.setBackgroundColor(context.getResources().getColor(R.color.white));
		} else {
			holder.layout.setBackgroundColor(context.getResources().getColor(R.color.search_select_area_bg));
		}
		Region r = region.get(position);
		holder.textView.setText(r.getName());
		holder.textView.setTextColor(Color.BLACK);

		return convertView;
	}

	public static class ViewHolder {
		public TextView textView;
		public RelativeLayout layout;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

}
