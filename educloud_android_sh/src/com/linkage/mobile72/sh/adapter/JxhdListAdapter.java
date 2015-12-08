package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.JXBean;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.swipelistview.SwipeListView;

@SuppressLint("ViewHolder")
public class JxhdListAdapter extends BaseAdapter {

	private int messageListType;
	private List<JXBean> mData;
	private Context mContext;
	private SwipeListView mSwipeListView;
	private int deleteWidth;
	private DeleteListener mDeleteListener;

	public interface DeleteListener {
		void delete(int position);
	}

	public JxhdListAdapter(Context context, int messageListType, List<JXBean> list,
			SwipeListView swipeListView, DeleteListener deleteListener) {
		LogUtils.e("&&&&&&&&&&&&&&&&&&&&&&&&&&&messageListType&&" + messageListType);
		this.messageListType = messageListType;
		mContext = context;
		mData = list;
		mSwipeListView = swipeListView;
		mDeleteListener = deleteListener;
		deleteWidth = context.getResources().getDisplayMetrics().widthPixels / 4;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mData.get(position).getId();
	}

	@Override
	public boolean isEmpty() {
		return mData.size() == 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.homework_list_item,
					parent, false);
			holder = new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final JXBean homework = (JXBean) getItem(position);
		if (Consts.MessageListType.SEND_BOX == messageListType) {
			if(String.valueOf(Consts.JxhdType.HOMEWORK).equals(homework.getSmsMessageType())) {
			if(TextUtils.isEmpty(homework.getSubjectName()))
        		holder.titleText.setText(homework.getRecvUserName());
        	else
        		holder.titleText.setText("["+homework.getSubjectName()+"]"+homework.getRecvUserName());
			}else {
				holder.titleText.setText(homework.getRecvUserName());
			}
		} else {
            if(String.valueOf(Consts.JxhdType.COMMENT).equals(homework.getSmsMessageType()))
                holder.titleText.setText(homework.getSendUserName() + " [点评]");
            else if(String.valueOf(Consts.JxhdType.NOTICE).equals(homework.getSmsMessageType()))
                holder.titleText.setText(homework.getSendUserName() + " [通知]");
            else if(String.valueOf(Consts.JxhdType.TOUPIAO).equals(homework.getSmsMessageType()))
                holder.titleText.setText(homework.getSendUserName() + " [通知]");
            else {
            	if(TextUtils.isEmpty(homework.getSubjectName()))
            		holder.titleText.setText(homework.getSendUserName());
            	else
            		holder.titleText.setText("["+homework.getSubjectName()+"]"+homework.getSendUserName());
            }
		}
		if(!TextUtils.isEmpty(homework.getRecvTime())) {
			if(homework.getRecvTime().length() > 10) {
				holder.dateText.setText(homework.getRecvTime().substring(0, 10));
			}else {
				holder.dateText.setText(homework.getRecvTime());
			}
		}
		holder.contentText.setText(homework.getMessageContent());
		holder.deleteBtn.getLayoutParams().width = deleteWidth;
		holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDeleteListener != null) {
					mDeleteListener.delete(position);
				}
				// mSwipeListView.closeAnimate(position + 1);
			}
		});
		convertView.setOnClickListener(null);
		return convertView;
	}

	class ViewHolder {
		private TextView titleText;
		private TextView dateText;
		private TextView contentText;
		private RelativeLayout deleteBtn;

		void init(View convertView) {
			titleText = (TextView) convertView.findViewById(R.id.title);
			dateText = (TextView) convertView.findViewById(R.id.date);
			contentText = (TextView) convertView.findViewById(R.id.content);
			deleteBtn = (RelativeLayout) convertView.findViewById(R.id.delete);
		}
	}
}