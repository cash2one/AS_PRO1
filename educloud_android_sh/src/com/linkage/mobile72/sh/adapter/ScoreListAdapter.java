package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkage.mobile72.sh.data.Score;
import com.linkage.mobile72.sh.R;
import com.linkage.ui.widget.PullToRefreshListView;

public class ScoreListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Score> mData;

	public ScoreListAdapter(Context context, List<Score> apps, PullToRefreshListView list_newapp) {
		mInflater = LayoutInflater.from(context);
		mData = apps;
	}

	public void addAll(List<Score> data, boolean append) {
		if (mData != null) {
			if (!append) {
				mData.clear();
			}
			mData.addAll(data);
		} else {
			mData = data;
		}
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public Score getItem(int position) {
		return mData.get(position);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_score_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvTestName = (TextView) convertView.findViewById(R.id.tvTestName);
			viewHolder.tvScore = (TextView) convertView.findViewById(R.id.tvScore);
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			viewHolder.tvAver = (TextView) convertView.findViewById(R.id.tvAver);
			viewHolder.tvMax = (TextView) convertView.findViewById(R.id.tvMax);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// image_newapp.setBackgroundResource(app_viewitem[arg0]);
		final Score score = getItem(position);
		String scoreName = score.getName();
		if(scoreName != null && scoreName.length() > 12) {
			scoreName = scoreName.substring(0,12) + "...";
		}
		viewHolder.tvTestName.setText(scoreName);
		viewHolder.tvScore.setText(score.getScore());
		viewHolder.tvDate.setText(score.getDate());
		viewHolder.tvAver.setText("均分" + score.getEverage());
		viewHolder.tvMax.setText("最高分" + score.getHighest());
		return convertView;
	}

	class ViewHolder {
		private TextView tvTestName;
		private TextView tvScore;
		private TextView tvDate;
		private TextView tvAver;
		private TextView tvMax;
	}
}