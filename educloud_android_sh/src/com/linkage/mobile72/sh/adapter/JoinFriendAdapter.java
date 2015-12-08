package com.linkage.mobile72.sh.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.PersonalInfoActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ApplyFriendBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.ImageLoader;

public class JoinFriendAdapter extends BaseAdapter {
	
	private static final String TAG = JoinFriendAdapter.class.getSimpleName();
	private Context mContext;
	private ImageLoader imageLoader;
	private LayoutInflater mLayoutInflater;
	private List<ApplyFriendBean> clazzs;
	private boolean isTuijian;
	private int mType;
	
	public static ArrayList<NotifiHandler> ehList = new ArrayList<NotifiHandler>();
	public static abstract interface NotifiHandler {
		public abstract void onMessage(long friend,int type);
	}
	public JoinFriendAdapter(Context context, ImageLoader imageLoader, boolean isTuijian, List<ApplyFriendBean> clazzs,int type) {
		this.mContext = context;
		this.imageLoader = imageLoader;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.clazzs = clazzs;
		this.isTuijian = isTuijian;
		this.mType = type;
	}
	
	public void addAll(List<ApplyFriendBean> clazzs, boolean append) {
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
		// TODO Auto-generated method stub
		return clazzs.size();
	}

	@Override
	public ApplyFriendBean getItem(int position) {
		// TODO Auto-generated method stub
		return clazzs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return clazzs.get(position).getUserId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final ApplyFriendBean friend = getItem(position);
		if (convertView == null || convertView.getTag() == null) {
			convertView = mLayoutInflater.inflate(R.layout.adapter_join_friend_list_item, parent, false);
			holder = new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(isTuijian) {
		holder.userName.setText(friend.getUserName());
		imageLoader.displayImage(friend.getAvater(), holder.avatar);
		System.out.println(friend.getAvater()+"url---------------");
		/*if(friend.getType() == 1) {//接口返回type = 1 表示未添加好友
			holder.joinView.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.joinView.setText(" 添加 ");
			holder.joinView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showJionFriend(friend);
				}
			});
		}else if(friend.getType() == 2) {//接口返回type = 1 表示已添加好友但还未通过
			holder.joinView.setTextColor(mContext.getResources().getColor(R.color.white_gray));
			holder.joinView.setText(" 等待验证 ");
		}else if(friend.getType() == 3) {//接口返回type = 3 表示已添加为好友
			holder.joinView.setTextColor(mContext.getResources().getColor(R.color.white_gray));
			holder.joinView.setText(" 已加入");
		}*/
		holder.userSchool.setText(friend.getSchool());
		holder.userFriendsNum.setVisibility(View.INVISIBLE);
		}else {
			holder.userName.setText(friend.getUserName());
			imageLoader.displayImage(friend.getAvater(), holder.avatar);
			holder.userSchool.setText(mType == 1 ? friend.getSchool() : friend.getUserId()+"");
			holder.userFriendsNum.setVisibility(View.INVISIBLE);
			holder.avatar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, PersonalInfoActivity.class);
					intent.putExtra("id", friend.getUserId());
					mContext.startActivity(intent);
				}
			});
		}
		return convertView;
	}
	
	class ViewHolder {
		private CircularImage avatar;
		private TextView userName;
		private TextView userSchool;
		private TextView userFriendsNum;
		void init(View convertView) 
		{
			avatar = (CircularImage) convertView.findViewById(R.id.avatar);
			userName = (TextView) convertView.findViewById(R.id.user_name);
			userSchool = (TextView) convertView.findViewById(R.id.user_school);
			userFriendsNum = (TextView) convertView.findViewById(R.id.user_friends_num);
		}
	}
	
	private void showJionFriend(final ApplyFriendBean friend) {
		final MyCommonDialog dialog = new MyCommonDialog(mContext, "提示消息", "确定要加"+friend.getUserName()+"为好友吗？", "取消","确定");
		dialog.setOkListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				jionFriend(friend);

			}
		});
		dialog.setCancelListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public void jionFriend(final ApplyFriendBean friend) {
		ProgressDialogUtils.showProgressDialog("", mContext, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "addFriend");
		params.put("friendId", String.valueOf(friend.getUserId()));
		params.put("applyReason","hi，你好");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					UIUtilities.showToast(mContext, "添加成功");
//					finish();
					clazzs.remove(friend);
					notifyDataSetChanged();
				} 
				else {
					StatusUtils.handleStatus(response, mContext);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, mContext);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		
	}

	
}
