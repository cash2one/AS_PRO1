package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.ClazzScore;

public class ClazzScoreAdapter extends BaseAdapter {

	private Context context;
	private List<ClazzScore> data;
	
	public ClazzScoreAdapter (Context context, List<ClazzScore> data) {
		this.context = context;
		this.data = data;
	}
	
	public void addAll(List<ClazzScore> list, boolean append){
		if (data != null) {
			if (!append) {
				data.clear();
			}
			data.addAll(list);
		} else {
			data = list;
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public ClazzScore getItem(int position) {
		return data == null ? null : data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return data == null ? 0 : data.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_clazz_score_sumary, parent, false);
			viewHolder.nameText = (TextView) convertView.findViewById(R.id.result_name);
			viewHolder.typeText = (TextView) convertView.findViewById(R.id.result_type);
			viewHolder.dateText = (TextView) convertView.findViewById(R.id.result_date);
			viewHolder.maxText = (TextView) convertView.findViewById(R.id.result_max);
			viewHolder.minText = (TextView) convertView.findViewById(R.id.result_min);
			viewHolder.averageText = (TextView) convertView.findViewById(R.id.result_average);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final ClazzScore item = getItem(position);
		String scoreName = item.getName();
		if(scoreName != null && scoreName.length() > 12) {
			scoreName = scoreName.substring(0,12) + "...";
		}
		viewHolder.nameText.setText(scoreName);
		viewHolder.typeText.setText(item.getTypeName());
		viewHolder.dateText.setText(item.getDate());
		viewHolder.maxText.setText("最高分："+item.getMax()+"");
		viewHolder.minText.setText("最低分："+item.getMin()+"");
		viewHolder.averageText.setText("均分："+item.getAverage()+"");
		return convertView;
	}

	class ViewHolder {
		TextView nameText;
		TextView typeText;
		TextView dateText;
		TextView maxText;
		TextView minText;
		TextView averageText;
	}
}
