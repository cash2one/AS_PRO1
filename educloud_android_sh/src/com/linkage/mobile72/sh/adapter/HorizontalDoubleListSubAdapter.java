package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.Region;

public class HorizontalDoubleListSubAdapter extends BaseAdapter {

	Context context;
	LayoutInflater layoutInflater;
	List<Region> regions;
	List<Region> childRegions;
	public int parentPoisition;

	public HorizontalDoubleListSubAdapter(Context context, List<Region> regions, int position) {
		this.context = context;
		this.regions = regions;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.parentPoisition = position;
		this.childRegions = regions.get(parentPoisition).getChildRegion();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return childRegions == null ? 0 : childRegions.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return childRegions == null ? null : childRegions.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.adapter_single_words, null);
			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.text1);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Region r = childRegions.get(position);
		viewHolder.textView.setText(r.getName());
		viewHolder.textView.setTextColor(Color.BLACK);
		
		return convertView;
	}

	public static class ViewHolder {
		public TextView textView;
	}

}
