package com.linkage.mobile72.sh.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.ChatActivity;
import com.linkage.mobile72.sh.activity.NewFriendsInGroupActivity;
import com.linkage.mobile72.sh.activity.SearchGroupActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.swipelistview.SwipeListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ContactGroupListAdapter extends BaseAdapter {

	private static final String TAG = ContactGroupListAdapter.class.getSimpleName();

	private List<ClassRoom> classList;
	private Context mContext;
	private Handler mHandler;
	private SwipeListView mSwipeListView;
	private ImageLoader imageLoader_group;
	private LayoutInflater inflater;
	private float density;
	private int deleteWidth;

	private MyCommonDialog dialog;

	OnClickListener newFriendsClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mContext.startActivity(new Intent(mContext, NewFriendsInGroupActivity.class));
		}
	};

	OnClickListener searchGroupClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mContext.startActivity(new Intent(mContext, SearchGroupActivity.class));
		}
	};

	public ContactGroupListAdapter(Context context, Handler mHandler, ImageLoader imageLoader_group,
			SwipeListView mSwipeListView, List<ClassRoom> classList) {
		this.mContext = context;
		this.mHandler = mHandler;
		this.mSwipeListView = mSwipeListView;
		this.classList = classList;
		inflater = LayoutInflater.from(context);
		this.imageLoader_group = imageLoader_group;
		density = context.getResources().getDisplayMetrics().density;
		deleteWidth = context.getResources().getDisplayMetrics().widthPixels / 3;
	}

	public void setDatas(List<ClassRoom> classList) {
		this.classList = classList;
	}

	@Override
	public int getCount() {
		// return classList.size() + 1;
		return classList.size();
	}

	@Override
	public Object getItem(int position) {
		/*
		 * if (position < 1) { return new NewFriend(); } else { return
		 * classList.get(position - 1); }
		 */
		return classList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void remove(int position) {
		// if (position < 1) {
		// throw new RuntimeException("前1条无法移除");
		// } else {
		// position = position - 1;
		if (position < classList.size()) {
			classList.remove(position);
		}
		// }
	}

	@Override
	public boolean isEmpty() {
		return classList.size() == 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.group_list_item, parent, false);
			viewHolder.categoryLayout = (LinearLayout) convertView
					.findViewById(R.id.category_layout);
			viewHolder.category = (TextView) convertView.findViewById(R.id.category);
			viewHolder.frontView = convertView.findViewById(R.id.front);
			viewHolder.backView = convertView.findViewById(R.id.back);
			viewHolder.avatarView = (ImageView) convertView.findViewById(R.id.contact_avatar);
			viewHolder.classTv = (TextView) convertView.findViewById(R.id.class_name);
			viewHolder.SchoolTv = (TextView) convertView.findViewById(R.id.school_name);
			viewHolder.deleteBtn = (RelativeLayout) convertView.findViewById(R.id.delete);
			viewHolder.deleteBtn.getLayoutParams().width = deleteWidth;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final Object item = getItem(position);
		Object preItem = null;
		if (position > 0) {
			preItem = getItem(position - 1);
		}
		if (item instanceof ClassRoom) {
			ClassRoom classRoomItem = (ClassRoom) item;
			if (classRoomItem.getJoinOrManage() == 1) {
				viewHolder.category.setText("我管理的班级");
			} else if (classRoomItem.getJoinOrManage() == 2) {
				viewHolder.category.setText("我加入的班级");
			}
			if (preItem == null) {
				viewHolder.categoryLayout.setVisibility(View.VISIBLE);
				((FrameLayout.LayoutParams) viewHolder.frontView.getLayoutParams()).setMargins(0,
						(int) (30 * density), 0, 0);
				((FrameLayout.LayoutParams) viewHolder.backView.getLayoutParams()).setMargins(0,
						(int) (30 * density), 0, 0);
			} else {
				ClassRoom lastClassRoomItem = (ClassRoom) preItem;
				if (classRoomItem.getJoinOrManage() != lastClassRoomItem.getJoinOrManage()) {
					viewHolder.categoryLayout.setVisibility(View.VISIBLE);
					((FrameLayout.LayoutParams) viewHolder.frontView.getLayoutParams()).setMargins(
							0, (int) (30 * density), 0, 0);
					((FrameLayout.LayoutParams) viewHolder.backView.getLayoutParams()).setMargins(
							0, (int) (30 * density), 0, 0);
				} else {
					viewHolder.categoryLayout.setVisibility(View.GONE);
					((FrameLayout.LayoutParams) viewHolder.frontView.getLayoutParams()).setMargins(
							0, 0, 0, 0);
					((FrameLayout.LayoutParams) viewHolder.backView.getLayoutParams()).setMargins(
							0, 0, 0, 0);
				}
			}

			viewHolder.classTv.setText(classRoomItem.getName() + "  "
					+ classRoomItem.getClassNumber() + "人");
			viewHolder.SchoolTv.setText(classRoomItem.getSchoolName());
			imageLoader_group.cancelDisplayTask(viewHolder.avatarView);
			DisplayImageOptions defaultOptions_group = new DisplayImageOptions.Builder()
					.cacheOnDisc().showStubImage(R.drawable.default_group)
					.showImageForEmptyUri(R.drawable.default_group)
					.showImageOnFail(R.drawable.default_group).build();
			imageLoader_group.displayImage(Consts.SERVER_HOST + classRoomItem.getAvatar(),
					viewHolder.avatarView, defaultOptions_group);
		}

        viewHolder.frontView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Object obj = getItem(position);
				if (obj instanceof ClassRoom) {
					// chatIntent.putExtra("chat_type", 2);//群聊
					// bundle.putSerializable("chat_user",
					// (ClassRoom)adapter.getItem(position));
					ClassRoom chatUserClassRoom = (ClassRoom) obj;
					ClassInfoBean clazz = new ClassInfoBean();
					// clazz.setClassroom_popId(chatUserClassRoom.getClassroom_popId());
					clazz.setClassroomId(chatUserClassRoom.getId());
					clazz.setClassroomName(chatUserClassRoom.getName());
					clazz.setAvatar(chatUserClassRoom.getAvatar());
					clazz.setDescription(chatUserClassRoom.getName());
					if(((ClassRoom) obj).getTaskid() == 0) {
						UIUtilities.showToast(mContext, "人数过少，无法创建群聊");
					}else {
//						Intent intent = NewChatActivity.getIntent(mContext, ((ClassRoom) obj).getTaskid(),
//								((ClassRoom) obj).getName(), ChatType.CHAT_TYPE_GROUP,0);
//						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
//						mContext.startActivity(intent);
						
						Intent intent = new Intent();
						intent.setClass(mContext, ChatActivity.class);
						Bundle bundle = new Bundle();

						bundle.putString("chatid", ((ClassRoom) obj).getTaskid()+"");
						bundle.putInt("chattype", Consts.ChatType.CHAT_TYPE_GROUP);
						bundle.putInt("type", 0);

					
						intent.putExtra("data", bundle);
						

						mContext.startActivity(intent);
					}
					
					
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		LinearLayout categoryLayout;
		TextView category;
		View frontView;
		View backView;
		ImageView avatarView;
		TextView classTv;
		TextView SchoolTv;
		RelativeLayout deleteBtn;
	}

	// 弹出是否取消好友
	private void popIfQuitPerson(final long userid, final String userName, final int position) {
		dialog = new MyCommonDialog(mContext, "提示消息", "确定要删除好友" + userName + "？", "取消", "删除");
		dialog.setCancelListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
			}
		});
		dialog.setOkListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				quitPerson(userid, userName, position);
			}
		});
		dialog.show();
	}

	// 弹出是否退出群组织
	private void popIfQuitClazz(final long clazzId, final String clazzName, final int position) {
		dialog = new MyCommonDialog(mContext, "提示消息", "确定要退出本群吗？", "取消", "退出本群");
		dialog.setCancelListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
			}
		});
		dialog.setOkListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				quitClazz(clazzId, clazzName, position);
			}
		});
		dialog.show();
	}

	public void quitClazz(final long clazzId, final String clazzName, final int position) {
		ProgressDialogUtils.showProgressDialog("通讯中", mContext, false);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "quitClassroom");
		params.put("classroomId", String.valueOf(clazzId));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();

						System.out.println("response=" + response);
						if (response.optInt("ret") == 0) {
							// TODO 登录成功后的帐号更新等

							UIUtilities.showToast(mContext, "您已成功退出班级" + clazzName);
							mSwipeListView.closeAnimate(position);
							mSwipeListView.dismiss(position);
							Message m = new Message();
							m.what = 1;
							mHandler.sendMessageDelayed(m, 100);
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

	public void quitPerson(final long userid, final String userName, final int position) {
		ProgressDialogUtils.showProgressDialog("通讯中", mContext, false);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "deleteFriend");
		params.put("friendId", String.valueOf(userid));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();

						System.out.println("response=" + response);
						if (response.optInt("ret") == 0) {
							// TODO 登录成功后的帐号更新等

							UIUtilities.showToast(mContext, "您已成功删除好友" + userName);
							mSwipeListView.closeAnimate(position);
							mSwipeListView.dismiss(position);
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
