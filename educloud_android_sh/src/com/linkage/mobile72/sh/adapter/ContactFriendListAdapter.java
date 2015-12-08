package com.linkage.mobile72.sh.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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
import com.linkage.mobile72.sh.activity.ClassContactActivity;
import com.linkage.mobile72.sh.activity.NewFriendsActivity;
import com.linkage.mobile72.sh.activity.SchoolContactActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.data.NewFriend;
import com.linkage.mobile72.sh.fragment.ContactsFriendFragment;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.im.provider.Ws.MessageTable;
import com.linkage.mobile72.sh.im.provider.Ws.MessageType;
import com.linkage.mobile72.sh.im.provider.Ws.ThreadTable;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.swipelistview.SwipeListView;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("DefaultLocale")
public class ContactFriendListAdapter extends BaseAdapter {

	private List<Contact> mContactList;
	private Context mContext;
	private Handler mHandler;
	private SwipeListView mSwipeListView;
	private ImageLoader mImageLoader;
	private LayoutInflater inflater;
	private float density;
	private int deleteWidth;

	private MyCommonDialog dialog;

	private OnClickListener newFriendsClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			((Activity) mContext).startActivityForResult(new Intent(mContext,
					NewFriendsActivity.class), ContactsFriendFragment.RESULT_CODE_REFRESH);
		}
	};

	private OnClickListener addressClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mContext.startActivity(new Intent(mContext, SchoolContactActivity.class));
		}
	};
	
	private OnClickListener classContactClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mContext.startActivity(new Intent(mContext, ClassContactActivity.class));
        }
    };

	/*
	 * private OnClickListener searchGroupClickListener = new OnClickListener()
	 * {
	 * 
	 * @Override public void onClick(View v) { mContext.startActivity(new
	 * Intent(mContext, SearchGroupActivity.class)); } };
	 */

	public ContactFriendListAdapter(Context context, Handler mHandler, ImageLoader imageLoader,
			SwipeListView swipeListView, List<Contact> contactList) {
		this.mContext = context;
		this.mHandler = mHandler;
		mSwipeListView = swipeListView;
		mContactList = contactList;
		inflater = LayoutInflater.from(context);
		mImageLoader = imageLoader;
		density = context.getResources().getDisplayMetrics().density;
		deleteWidth = context.getResources().getDisplayMetrics().widthPixels / 3;
	}

	public void setDatas(List<Contact> contactList) {
		mContactList.clear();
		mContactList.addAll(contactList);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if(Consts.is_Teacher)
            return mContactList.size() + 3;
        else
            return mContactList.size() + 2;
	}

	@Override
	public Object getItem(int position) {
        if(Consts.is_Teacher) {
            if (position < 3) {
                return new NewFriend();
            } else {
                position = position - 3;
                return mContactList.get(position);
            }
        }else {
            if (position < 1) {
                return new NewFriend();
            } else {
                position = position - 1;
                return mContactList.get(position);
            }
        }
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void remove(int position) {
        if(Consts.is_Teacher) {
            if (position < 3) {
                throw new RuntimeException("");
            } else {
                position = position - 3;
                mContactList.remove(position);
            }
        }else {
            if (position < 1) {
                throw new RuntimeException("");
            } else {
                position = position - 1;
                mContactList.remove(position);
            }
        }
	}

	@Override
	public boolean isEmpty() {
		return mContactList.size() == 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		// if(convertView == null) {
		viewHolder = new ViewHolder();
		switch (position) {
		case 0:
			convertView = inflater.inflate(R.layout.header_new_friend, parent, false);
			convertView.setOnClickListener(newFriendsClickListener);
			break;
		case 1:
            if(Consts.is_Teacher) {
                convertView = inflater.inflate(R.layout.header_new_friend_contact, parent, false);
                convertView.setOnClickListener(addressClickListener);
                break;
            }
		case 2:
            if(Consts.is_Teacher) {
                convertView = inflater.inflate(R.layout.header_class_contact, parent, false);
                convertView.setOnClickListener(classContactClickListener);
                break;
            }
		default:
			convertView = inflater.inflate(R.layout.contact_list_item, parent, false);
			LinearLayout categoryLayout = (LinearLayout) convertView
					.findViewById(R.id.category_layout);
			TextView category = (TextView) convertView.findViewById(R.id.category);
			viewHolder.frontView = convertView.findViewById(R.id.front);
			viewHolder.backView = convertView.findViewById(R.id.back);
			ImageView avatarView = (ImageView) convertView.findViewById(R.id.contact_avatar);
			TextView nameTextView = (TextView) convertView.findViewById(R.id.contact_name);
			RelativeLayout deleteBtn = (RelativeLayout) convertView.findViewById(R.id.delete);
			deleteBtn.getLayoutParams().width = deleteWidth;
			final Object item = getItem(position);
			Object lastItem = getItem(position - 1);
			Contact contactItem = (Contact) item;
			String categoryLabel = contactItem.getCategoryLabel();
			if (lastItem instanceof Contact
					&& ((Contact) lastItem).getSortKey().substring(0, 1).toUpperCase()
							.equals(categoryLabel)) {
				categoryLayout.setVisibility(View.GONE);
				((FrameLayout.LayoutParams) viewHolder.frontView.getLayoutParams()).setMargins(0,
						0, 0, 0);
				((FrameLayout.LayoutParams) viewHolder.backView.getLayoutParams()).setMargins(0, 0,
						0, 0);
			} else {
				categoryLayout.setVisibility(View.VISIBLE);
				category.setText(categoryLabel);
				((FrameLayout.LayoutParams) viewHolder.frontView.getLayoutParams()).setMargins(0,
						(int) (20 * density), 0, 0);
				((FrameLayout.LayoutParams) viewHolder.backView.getLayoutParams()).setMargins(0,
						(int) (20 * density), 0, 0);
			}
			nameTextView.setText(contactItem.getName());
			mImageLoader.displayImage(contactItem.getAvatar(), avatarView);

			deleteBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final Object item = getItem(position);
					Contact contactItem = (Contact) item;
					popIfQuitPerson(contactItem.getId(), contactItem.getName(), position);
				}
			});
			convertView.setOnClickListener(null);
			viewHolder.frontView.setOnLongClickListener(new OnLongClickListener(){
		         
				   @Override
				   public boolean onLongClick(View v) {
				    // TODO Auto-generated method stub
					   final Object item = getItem(position);
						Contact contactItem = (Contact) item;
						popIfQuitPerson(contactItem.getId(), contactItem.getName(), position);
				    return true;
				   }        
				        });
			
			viewHolder.frontView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final Object item = getItem(position);
					Contact contactItem = (Contact) item;
//					Intent intent = NewChatActivity.getIntent(mContext,
//							contactItem.getId(),contactItem.getName(),
//							ChatType.CHAT_TYPE_SINGLE,0);
//					mContext.startActivity(intent);
					Intent intent = new Intent();
					intent.setClass(mContext, ChatActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("chatid", contactItem.getId() + "");
					bundle.putInt("chattype", ChatType.CHAT_TYPE_SINGLE);
					bundle.putInt("type", 1);
					bundle.putString("name", contactItem.getName());
					intent.putExtra("data", bundle);
					LogUtils.d( "contact starting chat----> buddyId=" + contactItem.getName() + " chattype=" + ChatType.CHAT_TYPE_SINGLE
							+ " name=" + contactItem.getName());
					mContext.startActivity(intent);

				}
			});
			break;
		}
		convertView.setTag(viewHolder);
		return convertView;
	}

	class ViewHolder {
		LinearLayout categoryLayout;
		TextView category;
		View frontView;
		View backView;
		ImageView avatarView;
		TextView nameTextView;
		RelativeLayout deleteBtn;
	}

	// 弹出是否取消好友
	private void popIfQuitPerson(final long userid, final String userName, final int position) {
		dialog = new MyCommonDialog(mContext, "提示消息", "确定要删除好友" + userName + "？", "取消", "删除");
		dialog.setCancelListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
		dialog.setOkListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				quitPerson(userid, userName, position);
			}
		});
		dialog.show();
	}

	public void quitPerson(final long userid, final String userName, final int position) {
		mSwipeListView.closeAnimate(position + 1);
		ProgressDialogUtils.showProgressDialog("正在删除好友", mContext, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "deleteFriend");
		params.put("friendId", String.valueOf(userid));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
							UIUtilities.showToast(mContext, "您已成功删除好友" + userName);
//							EventBus.getDefault().post(new ContactsEvent(1));
							Message m = new Message();
							m.what = 1;
							mHandler.sendMessageDelayed(m, 100);
							remove(position);
							notifyDataSetChanged();
							// 更新未读数
							ContentValues cv = new ContentValues();
							cv.put(ThreadTable.UNREAD_COUNT, 0);
							mContext.getContentResolver().update(
									ThreadTable.CONTENT_URI,
									cv,
									ThreadTable.ACCOUNT_NAME + "=? and " + ThreadTable.BUDDY_ID
											+ "=? and " + ThreadTable.CHAT_TYPE + "=? and "
											+ ThreadTable.MSG_TYPE + " in "
											+ MessageType.MSG_TYPE_CHAT,
									new String[] { BaseApplication.getInstance().getDefaultAccount().getLoginname(), String.valueOf(userid),
											Consts.ChatType.CHAT_TYPE_SINGLE+"" });
							// 删除聊天记录
							mContext.getContentResolver().delete(
									MessageTable.CONTENT_URI,
									MessageTable.ACCOUNT_NAME + "=? AND " + MessageTable.BUDDY_ID
											+ "=? AND " + MessageTable.CHAT_TYPE + "=?",
									new String[] { BaseApplication.getInstance().getDefaultAccount().getLoginname(), String.valueOf(userid),
											Consts.ChatType.CHAT_TYPE_SINGLE+"" });
							String msgtype = "(" + MessageType.TYPE_MSG_TEXT + ","
									+ MessageType.TYPE_MSG_PIC + "," + MessageType.TYPE_MSG_AUDIO + ")";
							mContext.getContentResolver().delete(
									ThreadTable.CONTENT_URI,
									ThreadTable.ACCOUNT_NAME + "=? AND " + ThreadTable.BUDDY_ID
											+ "=? AND " + ThreadTable.MSG_TYPE + " in " + msgtype,
									new String[] { BaseApplication.getInstance().getDefaultAccount().getLoginname(), String.valueOf(userid) });
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
		BaseApplication.getInstance().addToRequestQueue(mRequest, ContactsFriendFragment.TAG);
	}
}