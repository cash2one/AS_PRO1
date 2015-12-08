package com.linkage.mobile72.sh.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.JxTemplate;

import java.util.List;

@SuppressLint("ViewHolder")
public class MbManagerListAdapter extends BaseAdapter{
	
	private List<JxTemplate> mList;
	private Context con;
	
	public MbManagerListAdapter(Context con, List<JxTemplate> mList) {
		// TODO Auto-generated constructor stub
		this.con = con;
		this.mList = mList;
	}
	
	public void addAll(List<JxTemplate> mData, boolean append) {
		if(this.mList != null) {
			if(!append) {
				this.mList.clear();
			}
			this.mList.addAll(mData);
		}else {
			this.mList = mData;
		}
		notifyDataSetChanged();
	}

    @Override
    public boolean isEmpty() {
        return mList==null || mList.size()<=0;
    }

    @Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public JxTemplate getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = View.inflate(con, R.layout.mb_manager_list_item, null);
		TextView title1 = (TextView) view.findViewById(R.id.title1);
		TextView title2 = (TextView) view.findViewById(R.id.title2);
		JxTemplate t = getItem(position);
		if(t != null) {
			title1.setText(t.getTitle());
			title2.setText(t.getText());
		}
		return view;
	}

}
