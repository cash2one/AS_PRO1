package com.linkage.mobile72.sh.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.AppApiBean;
import com.linkage.lib.util.LogUtils;

@SuppressLint("UseSparseArrays")
public class AppDetailGrantAdapter extends BaseAdapter {

	private Context mContext;
	public Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();
	private List<AppApiBean> list;

	public AppDetailGrantAdapter(Context context, List<AppApiBean> list) {
		this.mContext = context;
		this.list = list;
	}

	public void addAll(List<AppApiBean> data){
		this.list.addAll(data);
		if(list != null && list.size() > 0) {
			for(int i=0;i<list.size();i++) {
				isCheckMap.put(i, true);
			}
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public AppApiBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).getApi_id();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		AppApiBean api = getItem(position);
		if (convertView == null) {
			LogUtils.e("MainActivity" + "position1 = " + position);

			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.activity_app_launch_item, null);
			holder = new ViewHolder();
			holder.selected = (CheckBox) convertView.findViewById(R.id.grantApi);
			holder.name = (TextView) convertView.findViewById(R.id.grantText);
			holder.selected.setTag(api.getApi_id());
			holder.selected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					//int radiaoId = Integer.parseInt(buttonView.getTag().toString());
                    if(isChecked) {
                        //将选中的放入hashmap中
                        isCheckMap.put(position, isChecked);
                    }else {
                        //取消选中的则剔除
                        isCheckMap.remove(position);
                    }
				}
			});
			convertView.setTag(holder);
		} else {
			LogUtils.e("MainActivity" + "position2 = " + position);
			holder = (ViewHolder) convertView.getTag();
		}
		//找到需要选中的条目
		if(isCheckMap!=null && isCheckMap.containsKey(position)){
			holder.selected.setChecked(isCheckMap.get(position));
		}else {
			holder.selected.setChecked(false);
		}
		holder.name.setText(api.getApi_desc());

		return convertView;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return false;
	}
	
	public static class ViewHolder {
		CheckBox selected;
		TextView name;
	}

}
