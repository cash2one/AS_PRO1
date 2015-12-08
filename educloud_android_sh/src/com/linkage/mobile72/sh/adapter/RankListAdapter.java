package com.linkage.mobile72.sh.adapter;

import java.util.List;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.RankNumber;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RankListAdapter extends BaseAdapter{

	private ImageLoader imageLoader;
	private Context context;
	private List<RankNumber> numberLists;
	private BaseApplication mApp;
	
	public RankListAdapter(Context context, List<RankNumber> numberLists){
		this.context = context;
		this.numberLists = numberLists;
		mApp = BaseApplication.getInstance();
        imageLoader = mApp.imageLoader;
	}
	@Override
	public int getCount() {
		return numberLists.size();
	}

	@Override
	public Object getItem(int position) {
		return numberLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.rank_list_item, parent, false);
			holder = new ViewHolder();
			holder.userAvatar = (ImageView)convertView.findViewById(R.id.user_avatar);
			holder.userName = (TextView)convertView.findViewById(R.id.user_name_text);
			holder.totalDistance = (TextView)convertView.findViewById(R.id.total_distance_text);
			holder.rankingText = (TextView)convertView.findViewById(R.id.ranking_text);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}
		RankNumber number = numberLists.get(position);
		imageLoader.displayImage(number.getUserImg(), holder.userAvatar);
		holder.userName.setText(number.getUserName());
		holder.totalDistance.setText(number.getTotal());
		holder.rankingText.setText("第"+(position+1)+"名：");
		return convertView;
	}
	
	class ViewHolder {
		public ImageView userAvatar;
		public TextView userName;
		public TextView totalDistance;
		public TextView rankingText;
	}

}
