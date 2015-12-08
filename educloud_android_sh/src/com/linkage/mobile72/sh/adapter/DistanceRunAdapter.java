package com.linkage.mobile72.sh.adapter;

import java.util.ArrayList;

import com.linkage.mobile72.sh.data.DistanceRunData;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DistanceRunAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<DistanceRunData> sorts;
	
	public DistanceRunAdapter(Context context, ArrayList<DistanceRunData> sorts){}
	@Override
	public int getCount() {
		return sorts.size();
	}

	@Override
	public Object getItem(int position) {
		return sorts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
