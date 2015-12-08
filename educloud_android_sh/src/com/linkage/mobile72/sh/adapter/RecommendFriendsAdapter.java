package com.linkage.mobile72.sh.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.NewFriend;
import com.linkage.mobile72.sh.event.ContactsEvent;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

public class RecommendFriendsAdapter extends BaseAdapter {
	private static final String TAG = JoinGroupAdapter.class.getSimpleName();
	private List<NewFriend> friendsList;
	private ImageLoader imageLoader;
	private Context mContext;

	public RecommendFriendsAdapter(Context context, ImageLoader imageLoader,
			List<NewFriend> friendsList) {
		super();
		this.friendsList = friendsList;
		mContext = context;
		this.imageLoader = imageLoader;
	}

	@Override
	public int getCount() {
		return friendsList.size();
	}

	@Override
	public NewFriend getItem(int position) {
		return friendsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return friendsList.get(position).getUserId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.new_friends_item, parent,
					false);
			holder = new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final NewFriend friend = getItem(position);
		imageLoader.displayImage(Consts.SERVER_HOST + friend.getAvatar(), holder.avatar);
		System.out.println(friend.getAvatar() + "  url---------------");
		holder.userName.setText(friend.getUserName());
		holder.applyReasonText.setText(friend.getApplyReason());
		holder.acceptView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// agreeFriend(friend,1);
				final MyCommonDialog dialog = new MyCommonDialog(mContext, "提示消息", "同意添加好友吗？",
						"拒绝", "同意");
				dialog.setOkListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (dialog.isShowing())
							dialog.dismiss();
						agreeFriend(friend, 1);

					}
				});
				dialog.setCancelListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (dialog.isShowing())
							dialog.dismiss();
						agreeFriend(friend, 2);
					}
				});
				dialog.show();
			}
		});
		// holder.rejectView.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// agreeFriend(friend,2);
		// }
		// });
		return convertView;
	}

	class ViewHolder {
		private CircularImage avatar;
		private TextView userName;
		private TextView applyReasonText;
		private Button acceptView;

		// private Button rejectView;

		void init(View convertView) {
			avatar = (CircularImage) convertView.findViewById(R.id.avatar);
			userName = (TextView) convertView.findViewById(R.id.text_name);
			applyReasonText = (TextView) convertView.findViewById(R.id.text_reason);
			acceptView = (Button) convertView.findViewById(R.id.join);
			// rejectView =
			// (Button)convertView.findViewById(R.id.process_btn_reject);
		}
	}

	public void agreeFriend(final NewFriend friend, final int type) {
		ProgressDialogUtils.showProgressDialog("通讯中", mContext, true);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "approveFriendInvitation");
		params.put("friendId", String.valueOf(friend.getUserId()));
		params.put("isApprove", String.valueOf(type));

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();

						System.out.println("response=" + response);
						if (response.optInt("ret") == 0) {
							friendsList.remove(friend);
							notifyDataSetChanged();
							if (type == 1) {
								EventBus.getDefault().post(new ContactsEvent(1));
							}
							UIUtilities.showToast(mContext, "操作成功");
							((Activity) mContext).setResult(Activity.RESULT_OK);
						} else {
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