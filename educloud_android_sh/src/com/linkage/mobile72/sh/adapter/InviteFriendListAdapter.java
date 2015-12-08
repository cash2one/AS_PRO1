package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.data.http.InviteFriend;
import com.linkage.mobile72.sh.utils.AvatarUrlUtils;
import com.linkage.mobile72.sh.utils.ImageUtils;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.ImageLoader;

public class InviteFriendListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private ClassInfoBean clazz;
	private List<InviteFriend> members;
	private ImageLoader imageLoader;
	
	public InviteFriendListAdapter(Context context, ImageLoader imageLoader, ClassInfoBean clazz, List<InviteFriend> members) {
		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.imageLoader = imageLoader;
		this.clazz = clazz;
		this.members = members;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return members.size();
	}
	
	public void addAll(List<InviteFriend> data){
		if(this.members != null) {
			this.members.clear();
			this.members.addAll(data);
		}else {
			this.members = data;
		}
		notifyDataSetChanged();
	}

	@Override
	public InviteFriend getItem(int position) {
		// TODO Auto-generated method stub
		return members.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return members.get(position).getFriendId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.new_friends_item, parent, false);
		}
		ImageView avatar = (ImageView)convertView.findViewById(R.id.avatar);
		TextView text1 = (TextView)convertView.findViewById(R.id.text_name);
		Button process = (Button)convertView.findViewById(R.id.join);
		final InviteFriend member = getItem(position);
		imageLoader.displayImage(member.getAvatar(), avatar);
		if(member != null) {
			text1.setText(member.getFriendName());
		
			if(member.getType() == 3) {
				process.setText("选择");
				process.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
				process.setBackgroundResource(R.drawable.btn_short_select);
				//process.setBackground(mContext.getResources().getDrawable(R.drawable.btn_white));
				process.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
					}
				});
			}else {
				process.setText(" 选择 ");
				process.setTextColor(mContext.getResources().getColor(R.color.white));
				process.setBackgroundResource(R.drawable.btn_common_selector);
				//process.setBackground(mContext.getResources().getDrawable(R.drawable.btn_common_selector));
				process.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Uri smsToUri = Uri.parse("smsto:" + member.getFriendPhone());  
						Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
						intent.putExtra("sms_body", "云平台可以加好友加群组，推荐你用一下，" +
								"下载地址是http://www.xiaokezhuo.com/public//ufiles/apk/down.myapp.com/xiaokezhuo_android_p.apk，" +
								"安装后别忘了加群号哦：" + clazz.getClassroom_popId());
						mContext.startActivity(intent);
					}
				});
			}
		}
		return convertView;
	}
	

}
