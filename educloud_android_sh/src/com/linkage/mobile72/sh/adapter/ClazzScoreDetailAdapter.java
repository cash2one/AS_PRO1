package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.NotifyReply;
import com.linkage.mobile72.sh.data.http.ClazzScoreDetail;
import com.linkage.lib.util.LogUtils;

public class ClazzScoreDetailAdapter extends BaseAdapter {
	
	private List<ClazzScoreDetail> replies;
	private LayoutInflater inflater;
	private Context context;
	private View mLastView;
	private int mLastPosition;
	private int mLastVisibility;

	public ClazzScoreDetailAdapter(Context context, List<ClazzScoreDetail> replies) {
		this.replies = replies;
		this.context = context;
		inflater = LayoutInflater.from(context);
		mLastPosition = -1;
	}

	public void addData(List<ClazzScoreDetail> data) {
        this.replies = data;
        notifyDataSetChanged();
    }
	
	@Override
	public int getCount() {
		return replies.size();
	}

	@Override
	public ClazzScoreDetail getItem(int position) {
		return replies.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		long mPosition = getItemId(position);
		ClazzScoreDetail np = getItem(position);
		LogUtils.e(mPosition + "    " + position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.adapter_clazz_score_detail, parent, false);
			holder = new ViewHolder();
			holder.nameText = (TextView) convertView.findViewById(R.id.nameText);
			holder.scoreText = (TextView) convertView.findViewById(R.id.scoreText);
//			holder.messageText = (LinearLayout) convertView.findViewById(R.id.btn_message);
//			holder.phoneText = (LinearLayout) convertView.findViewById(R.id.btn_call);
//			holder.expandLayout = (LinearLayout) convertView.findViewById(R.id.expend_buttons);
//			holder.layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		/*if(position == 0) {
			TextPaint tp1 = holder.nameText.getPaint(); 
			TextPaint tp2 = holder.scoreText.getPaint(); 
			holder.nameText.setTextColor(context.getResources().getColor(R.color.black));
			tp1.setFakeBoldText(true); 
			tp2.setFakeBoldText(true); 
		}else {
			TextPaint tp1 = holder.nameText.getPaint(); 
			TextPaint tp2 = holder.scoreText.getPaint(); 
			holder.nameText.setTextColor(context.getResources().getColor(R.color.title_bg_color));
			tp1.setFakeBoldText(false); 
			tp2.setFakeBoldText(false); 
		}*/
		holder.nameText.setText(np.getName());
		holder.scoreText.setText(np.getResult()+"");
//		holder.layout1.setOnClickListener(new MyClickListner(convertView, position, np));
		return convertView;
	}

	class ViewHolder {
		private TextView nameText, scoreText;
		private LinearLayout expandLayout, layout1, messageText, phoneText;
	}

	public void changeImageVisible(View view, int position) {
		if (mLastView != null && mLastPosition != position) {
			ViewHolder holder = (ViewHolder) mLastView.getTag();
			switch (holder.expandLayout.getVisibility()) {
			case View.VISIBLE:
				holder.expandLayout.setVisibility(View.GONE);
				mLastVisibility = View.GONE;
				break;
			default:
				break;
			}
		}
		mLastPosition = position;
		mLastView = view;
		ViewHolder holder = (ViewHolder) view.getTag();
		switch (holder.expandLayout.getVisibility()) {
		case View.GONE:
			holder.expandLayout.setVisibility(View.VISIBLE);
			mLastVisibility = View.VISIBLE;
			break;
		case View.VISIBLE:
			holder.expandLayout.setVisibility(View.GONE);
			mLastVisibility = View.GONE;
			break;
		}
	}
	
	class MyClickListner implements OnClickListener {
		private View mView;
		private int position;
		private NotifyReply np;
		private MyClickListner(View mView, int position, NotifyReply np){
			this.mView = mView;
			this.position = position;
			this.np = np;
		}

		public void onClick(View arg0) {
			changeImageVisible(mView, position);
			ViewHolder holder = (ViewHolder) mView.getTag();
			if (mLastPosition == position) {
				holder.expandLayout.setVisibility(mLastVisibility);
				holder.messageText.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						//系统默认的action，用来打开默认的短信界面
						intent.setAction(Intent.ACTION_SENDTO);
						//需要发短息的号码
						intent.setData(Uri.parse("smsto:"+np.getPhone()));
						context.startActivity(intent);
					}
				});
				holder.phoneText.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Uri uri = Uri.parse("tel:"+np.getPhone());
						Intent intent = new Intent(Intent.ACTION_DIAL, uri);
						context.startActivity(intent);
					}
				});
			} else {
				holder.expandLayout.setVisibility(View.GONE);
			}
		}
		
	}
}
