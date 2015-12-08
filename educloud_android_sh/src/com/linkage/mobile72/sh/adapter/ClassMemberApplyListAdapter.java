package com.linkage.mobile72.sh.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.data.http.ClassMemberApplyBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ClassMemberApplyListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<ClassMemberApplyBean> members;
	private ClassInfoBean clazz;
	private ImageLoader imageLoader;
	
	public ClassMemberApplyListAdapter(Context context, ImageLoader imageLoader, ClassInfoBean clazz, List<ClassMemberApplyBean> members) {
		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.imageLoader = imageLoader;
		this.members = members;
		this.clazz = clazz;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return members.size();
	}
	
	public void addAll(List<ClassMemberApplyBean> data){
		if(this.members != null) {
			this.members.clear();
			this.members.addAll(data);
		}else {
			this.members = data;
		}
		notifyDataSetChanged();
		
	}

	@Override
	public ClassMemberApplyBean getItem(int position) {
		// TODO Auto-generated method stub
		return members.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return members.get(position).getUserId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.adapter_class_member_list_item, parent, false);
		}
		ImageView avatar = (ImageView)convertView.findViewById(R.id.imageview);
		TextView text1 = (TextView)convertView.findViewById(R.id.text1);
		TextView text2 = (TextView)convertView.findViewById(R.id.text2);
		Button process = (Button)convertView.findViewById(R.id.process_btn);
		final ClassMemberApplyBean member = getItem(position);
		if(member != null) {
			imageLoader.displayImage(Consts.SERVER_IP + member.getPicture(), avatar);
			text1.setText(member.getUserName());
			text2.setVisibility(View.GONE);
			process.setVisibility(View.VISIBLE);
			process.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final MyCommonDialog dialog = new MyCommonDialog(mContext, "提示消息", "是否同意"+member.getUserName()+"加入？", "拒绝", "同意");
					dialog.setCancelListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(dialog != null && dialog.isShowing()) {
								dialog.dismiss();
							}
							approveJoinClassroom(member, 2,position);
						}
					});
					dialog.setOkListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(dialog != null && dialog.isShowing()) {
								dialog.dismiss();
							}

							

							approveJoinClassroom(member, 1,position);
						}
					});
					dialog.show();
				}
			});
		}
		return convertView;
	}

	private void approveJoinClassroom(final ClassMemberApplyBean member, int isApprove,final int position) {
		ProgressDialogUtils.showProgressDialog("", mContext, false);
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("commandtype", "approveJoinClassroom");
		params.put("classroomId", String.valueOf(clazz.getClassroomId()));
		params.put("userId", String.valueOf(member.getUserId()));
		params.put("isApprove", String.valueOf(isApprove));
		params.put("type", String.valueOf(member.getType()));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					UIUtilities.showToast(mContext, "处理成功");
					members.remove(position);
					notifyDataSetChanged();
				} 
				else {
					ProgressDialogUtils.dismissProgressBar();
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
    	BaseApplication.getInstance().addToRequestQueue(mRequest, "");
	}
}
