package com.linkage.mobile72.sh.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.ClassRoomBean;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class JoinGroupAdapter extends BaseAdapter {

	private Context mContext;
	private List<ClassRoomBean> clazzs;
	private ImageLoader imageLoader_group;
	public static ArrayList<NotifiHandler> ehList = new ArrayList<NotifiHandler>();
	public static abstract interface NotifiHandler {
		public abstract void onMessage(long clazz,int type);
	}
	
	public JoinGroupAdapter(Context context, ImageLoader imageLoader_group, List<ClassRoomBean> clazzs) {
		this.mContext = context;
		this.clazzs = clazzs;
		this.imageLoader_group = imageLoader_group;
	}
	
	public void addAll(List<ClassRoomBean> clazzs, boolean append) {
		if(this.clazzs != null) {
			if(!append) {
				this.clazzs.clear();
			}
			this.clazzs.addAll(clazzs);
		}else {
			this.clazzs = clazzs;
		}
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return clazzs.size();
	}

	@Override
	public ClassRoomBean getItem(int position) {
		return clazzs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return clazzs.get(position).getClassroomId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final ClassRoomBean clazz = getItem(position);
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_join_group_list_item, parent, false);
			holder = new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.userName.setText(clazz.getClassroom_popId());
		//holder.clazzName.setText(clazz.getClassroomName() + "(" + clazz.getLeaderName() + ")");
		holder.clazzName.setText(clazz.getClassroomName());
		holder.text_count.setText(clazz.getMemCount()+"人");
//		if(clazz.getIsChoose() == 1) {//接口isJoin = 1 表示已加入
//			holder.joinView.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
//			holder.joinView.setText(" 已加入 ");
//			holder.joinView.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					
//				}
//			});
//		}else if(clazz.getIsChoose() == 2){//接口isJoin = 2 表示未加入
//			holder.joinView.setTextColor(mContext.getResources().getColor(R.color.dark_green));
//			holder.joinView.setText(" 加入 ");
//			holder.joinView.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					showJionGroup(clazz);
//				}
//			});
//		}else if(clazz.getIsChoose() == 3) {//3代表已申请，但未加入
//			holder.joinView.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
//			holder.joinView.setText(" 等待验证 ");
//			holder.joinView.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					
//				}
//			});
//		}
		DisplayImageOptions defaultOptions_group = new DisplayImageOptions.Builder().cacheOnDisc().showStubImage(R.drawable.default_group).showImageForEmptyUri(R.drawable.default_group).showImageOnFail(R.drawable.default_group).build();
		
		imageLoader_group.displayImage(Consts.SERVER_HOST + clazz.getAvatar(), holder.avatar,defaultOptions_group);
		System.out.println(clazz.getAvatar()+"url---------------");
		return convertView;
	}
	
	class ViewHolder {
		private CircularImage avatar;
		private TextView userName;
		private TextView clazzName;
		private TextView text_count;
		
		void init(View convertView) 
		{
			avatar = (CircularImage) convertView.findViewById(R.id.avatar);
			userName = (TextView) convertView.findViewById(R.id.user_name);
			text_count = (TextView)convertView.findViewById(R.id.text_count);
			clazzName =(TextView)  convertView.findViewById(R.id.text_desc);
			
		}
	}
	
}
