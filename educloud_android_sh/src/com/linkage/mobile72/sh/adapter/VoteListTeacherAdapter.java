package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.JXBeanDetail.JXVote;

public class VoteListTeacherAdapter extends BaseAdapter {

    private Context context;
    private List<JXVote> data;

	public VoteListTeacherAdapter(Context context, List<JXVote> data) {
		this.context = context;
		this.data = data;
	}

	public void addData(List<JXVote> list, boolean append) {
        if(list != null && list.size() > 0) {
            if(!append)
                data.clear();
            data.addAll(list);
        }
        notifyDataSetChanged();
    }
	
	@Override
	public int getCount() {
        return data.size();
	}

	@Override
	public JXVote getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_jx_detail_vote_item, parent, false);
            holder = new Holder();
            holder.voteOption = (TextView) convertView.findViewById(R.id.voteOption);
            holder.voteContent = (TextView) convertView.findViewById(R.id.voteContent);
            holder.voteNum = (TextView) convertView.findViewById(R.id.voteNum);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        JXVote vote = getItem(position);
        if(vote != null) {
        	holder.voteOption.setText(vote.getVoteOption() + ".");
            holder.voteContent.setText(vote.getVoteContent());
            holder.voteNum.setText(vote.getVoteNum() + "人");
        }
		return convertView;
	}
	
	class Holder{
		TextView voteOption;
		TextView voteContent;
        TextView voteNum;
	}

}
