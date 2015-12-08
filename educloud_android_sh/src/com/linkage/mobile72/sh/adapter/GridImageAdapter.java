package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.InviteFriendActivity;
import com.linkage.mobile72.sh.activity.SearchPersonActivity;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.data.http.ClassMemberBean;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GridImageAdapter extends BaseAdapter {
	private ImageLoader imageLoader;
	private List<ClassMemberBean> clazzMembers;
	private Context mContext;
	private boolean isJoin;
	private ClassInfoBean clazz;
	
	public GridImageAdapter(Context con, ImageLoader imageLoader,
			List<ClassMemberBean> clazzMembers, boolean isJoin, ClassInfoBean clazz) {
		// TODO Auto-generated constructor stub
		this.clazzMembers = clazzMembers;
		this.imageLoader = imageLoader;
		this.mContext = con;
		this.isJoin = isJoin;
		this.clazz = clazz;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		/*if(isJoin)
			return clazzMembers.size() + 1;
		else*/
			return clazzMembers.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View grview = (LinearLayout) View.inflate(mContext, R.layout.grid_item_img,
				null);
		ImageView img = (ImageView) grview.findViewById(R.id.item_img);
		/*if(isJoin) {
			if (arg0 == clazzMembers.size()) {
				img.setBackgroundResource(R.drawable.create_group_plus_btn);
				img.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Bundle bundle = new Bundle();
						bundle.putSerializable("CLAZZ", clazz);
						Intent intent = new Intent(mContext, InviteFriendActivity.class);
						intent.putExtras(bundle);
						mContext.startActivity(intent);
					}
				});
				return grview;
			} else {
				imageLoader.displayImage(Consts.SERVER_HOST
						+ clazzMembers.get(arg0).getAvatar(), img);
				return grview;
			}
		}else {*/
			imageLoader.displayImage(clazzMembers.get(arg0).getAvatar(), img);
			return grview;
		//}

	}

}
