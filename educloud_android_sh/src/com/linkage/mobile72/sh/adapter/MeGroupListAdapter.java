package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.SchoolData;

public class MeGroupListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<SchoolData> groups;
	
	public MeGroupListAdapter(Context context, List<SchoolData> groups) {
		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.groups = groups;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return groups.size();
	}
	
	public void addAll(List<SchoolData> data){
		if(this.groups != null) {
			this.groups.clear();
			this.groups.addAll(data);
		}else {
			this.groups = data;
		}
		notifyDataSetChanged();
	}

	@Override
	public SchoolData getItem(int position) {
		// TODO Auto-generated method stub
		return groups.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return groups.get(position).getSchoolId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.adapter_me_group_list_item, parent, false);
		}
		TextView groupName = (TextView)convertView.findViewById(R.id.me_group_name);
		TextView join_txt = (TextView)convertView.findViewById(R.id.me_group_join_txt);
		ImageView join_img = (ImageView)convertView.findViewById(R.id.me_group_join_ic);
		SchoolData group = getItem(position);
		if(group != null) {
			groupName.setText(group.getSchoolName());
			if(group.getIsJoin()== 0) {
				join_txt.setText("加入");
				join_txt.setTextColor(mContext.getResources().getColor(R.color.blue));
				join_img.setVisibility(View.VISIBLE);
			}else {
				join_txt.setText("已加入");
				join_txt.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
				join_img.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

}
