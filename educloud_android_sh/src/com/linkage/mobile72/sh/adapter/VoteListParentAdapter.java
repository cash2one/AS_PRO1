package com.linkage.mobile72.sh.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.JXBeanDetail.JXVote;

public class VoteListParentAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<JXVote> votes;
	// 用于记录每个RadioButton的状态，并保证只可选一个
	public HashMap<String, Boolean> states = new HashMap<String, Boolean>();

	class ViewHolder {
		RadioButton rb_state;
	}

	public VoteListParentAdapter(Context context, ArrayList<JXVote> votes) {
		// TODO Auto-generated constructor stub
		this.votes = votes;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return votes != null ? votes.size() : 0;
	}

	@Override
	public JXVote getItem(int position) {
		// TODO Auto-generated method stub
		return votes != null ? votes.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 页面
		ViewHolder holder;
		final JXVote bean = getItem(position);
		LayoutInflater inflater = LayoutInflater.from(context);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.vote_list_item_parent, parent, false);
			holder = new ViewHolder();
			holder.rb_state = (RadioButton) convertView.findViewById(R.id.vote_option);
			if(position == 0){
				holder.rb_state.setBackgroundResource(R.drawable.vote_list_item_bg_top);
			}else if(position == getCount()-1) {
				holder.rb_state.setBackgroundResource(R.drawable.vote_list_item_bg_bottom);
			}else {
				holder.rb_state.setBackgroundResource(R.drawable.vote_list_item_bg_center);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.rb_state.setText(bean.getVoteContent());
		final RadioButton radio = (RadioButton) convertView.findViewById(R.id.vote_option);
		holder.rb_state = radio;
		holder.rb_state.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				// 重置，确保最多只有一项被选中
				for (String key : states.keySet()) {
					states.put(key, false);

				}
				states.put(bean.getVoteOption(), radio.isChecked());
				VoteListParentAdapter.this.notifyDataSetChanged();
			}
		});

		boolean res = false;
		if (states.get(bean.getVoteOption()) == null
				|| states.get(bean.getVoteOption()) == false) {
			res = false;
			states.put(bean.getVoteOption(), false);
		} else
			res = true;

		holder.rb_state.setChecked(res);
		return convertView;
	}
}
