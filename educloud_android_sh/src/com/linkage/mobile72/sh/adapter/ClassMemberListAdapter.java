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
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.http.ClassMemberBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ClassMemberListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<ClassMemberBean> members;
	private ImageLoader imageLoader;
	private long changerTID;
	private String clazzid;
	public ClassMemberListAdapter(Context context, ImageLoader imageLoader, List<ClassMemberBean> members,long changerTid,String clazzID) {
		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.imageLoader = imageLoader;
		this.members = members;
		changerTID = changerTid;
		clazzid = clazzID;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return members.size();
	}
	
	public void addAll(List<ClassMemberBean> data){
		if(this.members != null) {
			this.members.clear();
			this.members.addAll(data);
		}else {
			this.members = data;
		}
		notifyDataSetChanged();
	}

	@Override
	public ClassMemberBean getItem(int position) {
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
		CircularImage avatar = (CircularImage)convertView.findViewById(R.id.imageview);
		TextView text1 = (TextView)convertView.findViewById(R.id.text1);
		TextView text2 = (TextView)convertView.findViewById(R.id.text2);
		TextView text_desc = (TextView)convertView.findViewById(R.id.text_desc);
		
		ImageView image_group_item = (ImageView)convertView.findViewById(R.id.image_group_item);
		
		Button process = (Button)convertView.findViewById(R.id.process_btn);
		final ClassMemberBean member = getItem(position);
		AccountData currentAccount = BaseApplication.getInstance().getDefaultAccount();
	
		if(member != null) {
			imageLoader.displayImage(member.getAvatar(), avatar);
			text1.setText(member.getNickName());
			
			if(null == member.getRuf_auditStatus() || member.getRuf_auditStatus() == 0 || member.getRuf_auditStatus() == 2)
			{
			    text2.setText("");
			    text2.setVisibility(View.GONE);
			    text_desc.setVisibility(View.GONE);
			    /*RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams)text1.getLayoutParams();
			    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE); 
			    text1.setLayoutParams(layoutParams);*/
			}
			else 
			{
			    text2.setText("已是好友");
			    text_desc.setText(member.getPhone() == null ? "" : member.getPhone());
			    text2.setVisibility(View.VISIBLE);
	            text_desc.setVisibility(View.VISIBLE);
            }
			
			process.setVisibility(View.GONE);
			image_group_item.setVisibility(View.VISIBLE);
			if(changerTID ==currentAccount.getUserId() && member.getUserId()!=changerTID) {
				process.setVisibility(View.GONE);
				process.setText("删除");
				process.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final MyCommonDialog dialog = new MyCommonDialog(mContext, "提示消息", "确定删除用户"+member.getNickName(), "取消", "确定");
						dialog.setCancelListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(dialog != null && dialog.isShowing()) {
									dialog.dismiss();
								}
//								approveJoinClassroom(member, 2,position);
							}
						});
						dialog.setOkListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(dialog != null && dialog.isShowing()) {
									dialog.dismiss();
								}

								

								deleteClassroomMember(member, position);
							}
						});
						dialog.show();
					}
				});
			}
		}
		return convertView;
	}
	
	private void deleteClassroomMember(final ClassMemberBean member,final int position) {
		ProgressDialogUtils.showProgressDialog("", mContext, false);
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("commandtype", "kickoffClassroomMember");
		params.put("classroomId", clazzid);
		params.put("userId", String.valueOf(member.getUserId()));
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
