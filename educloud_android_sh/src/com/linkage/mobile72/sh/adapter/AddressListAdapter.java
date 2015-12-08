package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.PhoneNameValuePair;

public class AddressListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<PhoneNameValuePair> members;
	
	public AddressListAdapter(Context context, List<PhoneNameValuePair> members) {
		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.members = members;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return members.size();
	}
	
	public void addAll(List<PhoneNameValuePair> data){
		if(this.members != null) {
			this.members.clear();
			this.members.addAll(data);
		}else {
			this.members = data;
		}
		notifyDataSetChanged();
	}

	@Override
	public PhoneNameValuePair getItem(int position) {
		return members.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.address_item, parent, false);
		}
		TextView text1 = (TextView)convertView.findViewById(R.id.text_name);
		Button process = (Button)convertView.findViewById(R.id.join);
		final PhoneNameValuePair member = getItem(position);
		if(member != null) {
			text1.setText(member.getUserName());
		}
		return convertView;
	}
	

}
