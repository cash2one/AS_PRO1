package com.linkage.mobile72.sh.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.SearchSchool;

public class SearchSchoolListAdapter extends BaseAdapter {

	private Context mContext;
	private List<SearchSchool> clazzs;
	 public static ArrayList<NotifiHandler> ehList = new ArrayList<NotifiHandler>();
	 public static abstract interface NotifiHandler {
			public abstract void onMessage(long clazz,int type);
		}
	public SearchSchoolListAdapter(Context context, List<SearchSchool> clazzs) {
		this.mContext = context;
		this.clazzs = clazzs;
	}
	
	public void addAll(List<SearchSchool> clazzs) {
		if(this.clazzs != null) {
			this.clazzs.clear();
			this.clazzs.addAll(clazzs);
		}else {
			this.clazzs = clazzs;
		}
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return clazzs.size();
	}

	@Override
	public SearchSchool getItem(int position) {
		return clazzs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return clazzs.get(position).getSchoolId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_group_list_item, parent, false);
			holder = new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final SearchSchool clazz = getItem(position);
		holder.groupName.setText(clazz.getSchoolName());
		holder.groupAddress.setText("地址：" + clazz.getAddress());
		
		return convertView;
	}
	
	class ViewHolder {
		private TextView groupName;
		private TextView groupAddress;
		
		void init(View convertView) 
		{
			groupName = (TextView) convertView.findViewById(R.id.group_name);
			groupAddress =(TextView)  convertView.findViewById(R.id.group_address);
		}
	}
	
	
}
