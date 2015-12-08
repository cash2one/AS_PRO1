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
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClassRoomBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.InnerListView;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;

public class JoinClazzListAdapter extends BaseAdapter {

	private Context mContext;
	private InnerListView listView;
	private LayoutInflater mLayoutInflater;
	private List<ClassRoomBean> clazzs;
	private int schoolType;
	
	private MyCommonDialog dialog;
	
	public JoinClazzListAdapter(Context context, InnerListView listView, List<ClassRoomBean> clazzs, int schoolType) {
		this.mContext = context;
		this.listView = listView;
		this.schoolType = schoolType;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.clazzs = clazzs;
	}
	
	public void addAll(List<ClassRoomBean> clazzs) {
		if(this.clazzs != null) {
			this.clazzs.clear();
			this.clazzs.addAll(clazzs);
		}else {
			this.clazzs = clazzs;
		}
		notifyDataSetChanged();
		if(this == null || getCount() < 1) {
			listView.setMaxHeight(-1);
		}else {
			listView.setMaxHeight(-1);
			listView.setAdapter(this);
			listView.setCanScroll(false);//ListView
		}
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return clazzs.size();
	}

	@Override
	public ClassRoomBean getItem(int position) {
		// TODO Auto-generated method stub
		return clazzs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return clazzs.get(position).getClassroomId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.adapter_join_clazz_list_item, parent, false);
		}
		TextView clazzName = (TextView)convertView.findViewById(R.id.clazz_name);
		CheckBox join_img = (CheckBox)convertView.findViewById(R.id.clazz_if_join);
		final ClassRoomBean clazz = getItem(position);
		if(clazz != null) {
			clazzName.setText(clazz.getClassroomName());
			if(clazz.getIsChoose() == 1) {//1代表用户已加入 2代表未加入
				join_img.setChecked(true);
			}else {
				join_img.setChecked(false);
			}
		}
		join_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				joinOrQuit(clazz);
			}
		});
		return convertView;
	}

	private void joinOrQuit(final ClassRoomBean group) {
		if (group.getIsChoose() == 1) {
			dialog = new MyCommonDialog(mContext, "提示消息", "确认退出班级吗？", "取消","退出");
			dialog.setOkListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();
					quitClazz(group);

				}
			});
			dialog.setCancelListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();
					group.setIsChoose(1);
					notifyDataSetChanged();
				}
			});
			dialog.show();
		} else {
			dialog = new MyCommonDialog(mContext, "提示消息", "确认加入班级吗？", "取消", "加入");
			dialog.setOkListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();
					addClazz(group);
				}
			});
			dialog.setCancelListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();
					group.setIsChoose(2);
					notifyDataSetChanged();
				}
			});
			dialog.show();
		}
	}
	
	public void addClazz(final ClassRoomBean group) {
		if(dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		ProgressDialogUtils.showProgressDialog("申请中", mContext, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("classroomId", String.valueOf(group.getClassroomId()));
		params.put("type", String.valueOf(schoolType));
		params.put("applyReason", "申请加入");
		
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_Apply_EnterClassroom, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();

				System.out.println("response=" + response);
				if (response.optInt("ret") == 2) {
					// TODO 登录成功后的帐号更新等
					UIUtilities.showToast(mContext, "您已成功加班级"+group.getClassroomName());
					group.setIsChoose(1);
					notifyDataSetChanged();
				} else {
					StatusUtils.handleStatus(response, mContext);
					group.setIsChoose(2);
					notifyDataSetChanged();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, mContext);
				group.setIsChoose(2);
				notifyDataSetChanged();
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, "JoinClazzActivity");
		
	}
	
	
	public void quitClazz(final ClassRoomBean group) {
		if(dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		ProgressDialogUtils.showProgressDialog("申请中", mContext, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("classroomId", String.valueOf(group.getClassroomId()));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_Exit_Classroom, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();

				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					// TODO 登录成功后的帐号更新等
					UIUtilities.showToast(mContext, "您已成功退出班级"+group.getClassroomName());
					group.setIsChoose(2);
					notifyDataSetChanged();
				} else {
					StatusUtils.handleStatus(response, mContext);
					group.setIsChoose(1);
					notifyDataSetChanged();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, mContext);
				group.setIsChoose(1);
				notifyDataSetChanged();
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, "JoinClazzActivity");
		
	}
}
