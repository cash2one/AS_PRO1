package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChooseSexListAdapter extends BaseAdapter {

	private Context mContext;
	private List<String> mList;
	
	public ChooseSexListAdapter(Context context, List<String> list) {
		this.mContext = context;
		this.mList = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mList != null) {
			return mList.size();
		}
		return 0;
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		if(mList != null) {
			return mList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
			convertView = new TextView(mContext);
		}
		((TextView)convertView).setText(getItem(position));
		
		return convertView;
	}

}
