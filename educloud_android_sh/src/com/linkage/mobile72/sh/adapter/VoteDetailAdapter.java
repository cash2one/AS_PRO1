package com.linkage.mobile72.sh.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.JXBeanDetail.JXVotePerson;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.nostra13.universalimageloader.core.ImageLoader;

public class VoteDetailAdapter extends BaseAdapter {
	
	private ImageLoader imageLoader;
	private ArrayList<JXVotePerson> votes;
	private LayoutInflater inflater;

	public VoteDetailAdapter(Context context, ImageLoader imageLoader, ArrayList<JXVotePerson> votes) {
		this.imageLoader = imageLoader;
		this.votes = votes;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return votes.size();
	}

	@Override
	public JXVotePerson getItem(int position) {
		return votes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.vote_list_item, parent, false);
			holder = new ViewHolder();
			holder.nameText = (TextView) convertView.findViewById(R.id.name_text);
			holder.circularImage = (CircularImage) convertView.findViewById(R.id.avatar_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		JXVotePerson p = votes.get(position);
		if(p != null) {
			holder.nameText.setText(p.getUserName());
			imageLoader.displayImage(p.getPicture(), holder.circularImage);
		}
		return convertView;
	}
	
	class ViewHolder {
    	private TextView nameText;
    	private CircularImage circularImage;
    }

}
