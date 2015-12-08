package com.linkage.mobile72.sh.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.im.NewChatActivity;
import com.linkage.mobile72.sh.adapter.ContactFriendListAdapter2;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.swipelistview.BaseSwipeListViewListener;
import com.linkage.ui.widget.swipelistview.SwipeListView;

public class ContactsFriendFragment2 extends BaseFragment {
	public static final String TAG = ContactsFriendFragment2.class.getSimpleName();

	public static final int RESULT_CODE_REFRESH = 1;

	private List<Contact> mData;
	private SwipeListView mSwipeListView;
	private ContactFriendListAdapter2 adapter;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				syncSmsContact();
				break;
			}
		}
	};

	public static ContactsFriendFragment2 create() {
		ContactsFriendFragment2 f = new ContactsFriendFragment2();
		// Supply num input as an argument.
		Bundle args = new Bundle();
		// args.putInt("titleRes", titleRes);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mData = new ArrayList<Contact>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contact_friends, container, false);
		LogUtils.e("onCreateView");
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSwipeListView = (SwipeListView) view.findViewById(R.id.example_lv_list);
		mSwipeListView.setOffsetLeft(this.getResources().getDisplayMetrics().widthPixels * 2 / 3);
		// mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
		// mSwipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
		// mSwipeListView.setAnimationTime(0);
		// mSwipeListView.setSwipeOpenOnLongPress(false);
		mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
		
		
		
		adapter = new ContactFriendListAdapter2(getActivity(), mHandler, imageLoader, mSwipeListView, mData);
		mSwipeListView.setAdapter(adapter);
		mSwipeListView.setonRefreshListener(new SwipeListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							syncSmsContact();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						// adapter.notifyDataSetChanged();
						onSyncEnd();
					}
				}.execute(null, null, null);
			}
		});
		mSwipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {

			@Override
			public void onOpened(int position, boolean toRight) {
				// TODO Auto-generated method stub
				super.onOpened(position, toRight);
//				MainActivity.slideMenu.setSlidingEnabled(false);
			}

			@Override
			public void onClosed(int position, boolean fromRight) {
				// TODO Auto-generated method stub
				super.onClosed(position, fromRight);
//				MainActivity.slideMenu.setSlidingEnabled(true);
			}

			@Override
			public void onClickFrontView(int position) {
//				if (position == 0)
//					return;
//				super.onClickFrontView(position);
//				mSwipeListView.closeOpenedItems();
//				Object obj = adapter.getItem(position - 1);
//				if (obj instanceof Contact) {
//					// chatIntent.putExtra("chat_type", 1);//单聊
//					// bundle.putSerializable("chat_user",
//					// (Contact)adapter.getItem(position));
//					LogUtils.e("*************buddyId***********" + ((Contact) obj).getId() + "");
//					Intent intent = NewChatActivity.getIntent(getActivity(),
//							((Contact) obj).getId(), ((Contact) obj).getName(),
//							ChatType.CHAT_TYPE_SINGLE,0);
//					getActivity().startActivity(intent);
//
//				} else if (obj instanceof ClassRoom) {
//					// chatIntent.putExtra("chat_type", 2);//群聊
//					// bundle.putSerializable("chat_user",
//					// (ClassRoom)adapter.getItem(position));
//					ClassRoom chatUserClassRoom = (ClassRoom) obj;
//					ClassInfoBean clazz = new ClassInfoBean();
//					// clazz.setClassroom_popId(chatUserClassRoom.getClassroom_popId());
//					clazz.setClassroomId(chatUserClassRoom.getId());
//					clazz.setClassroomName(chatUserClassRoom.getName());
//					clazz.setAvatar(chatUserClassRoom.getAvatar());
//					clazz.setDescription(chatUserClassRoom.getName());
//					Intent intent = NewChatActivity.getIntent(getActivity(),
//							((ClassRoom) obj).getId(), ((ClassRoom) obj).getName(),
//							ChatType.CHAT_TYPE_GROUP,0);
//					getActivity().startActivity(intent);
//				}
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {
				super.onDismiss(reverseSortedPositions);
				// for (int i : reverseSortedPositions) {
				// adapter.remove(i);
				// }
				// adapter.notifyDataSetChanged();
				if (reverseSortedPositions.length > 0) {
					adapter.remove(reverseSortedPositions[0]);
					adapter.notifyDataSetChanged();
				}
			}

		});

		// new LoadContacts(true).execute();
//		syncSmsContact();
//		flash_data = 0;
//		EventBus.getDefault().register(this);
//		EventBus.getDefault().register(new ContactsFriendFragment2());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_CODE_REFRESH:
			if (resultCode == Activity.RESULT_OK) {
				syncSmsContact();
			}
			break;
		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
//		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// if(flash_data ==1){
		// flash_data=0;
		// BaseApplication.getInstance().setflag(0);
		System.out.println("-------------onResume+++++++++++:");
		syncSmsContact();
		// }
		// new LoadContacts(true).execute();
	}

	/*public void onEventAsync(ContactsEvent event) {
		// Log.v("EventBus1", Thread.currentThread().getId()+"----");
		if (event.getType() == 1) {
			BaseApplication.getInstance().setflag(1);
			flash_data = 1;
			System.out.println("-------------onEventAsync+++++++++++:"
					+ BaseApplication.getInstance().getflag());
			// syncSmsContact_bak();
			syncSmsContact_bak();

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}*/

	private void syncSmsContact() {
		HashMap<String, String> params = new HashMap<String, String>();
		// params.put("id",
		// String.valueOf(BaseApplication.getInstance().getDefaultAccount().getUserId()));
		// params.put("token",
		// BaseApplication.getInstance().getDefaultAccount().getToken());
		// params.put("commandtype", "getJoinedClassroomAndFriendList");
		params.put("commandtype", "getJoinedFriendList");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						onSyncEnd();
						if (response.optInt("ret") == 0) {
							List<Contact> contactList = Contact.parseFromJson(response
									.optJSONArray("data"));
							List<Contact> tmp1 = new ArrayList<Contact>();
		                    List<Contact> tmp2 = new ArrayList<Contact>();
		                    if(contactList!=null && contactList.size()>0){
		                        for(Contact c:contactList){
		                            if(c.getUsertype() == 1){
		                                tmp1.add(c);
		                            }else {
		                                tmp2.add(c);
		                            }
		                        }
		                    }
		                    syncContactsSuccess(tmp1, 1);
		                    syncContactsSuccess(tmp2, 2);
						} else {
							StatusUtils.handleStatus(response, getActivity());
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						onSyncEnd();
						StatusUtils.handleError(arg0, getActivity());
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	private void onSyncEnd() {

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mSwipeListView.onRefreshComplete();
			}
		}, 500);
	}

	private void syncContactsSuccess(List<Contact> contacts, int userType) {
        SaveContactTask saveTask = new SaveContactTask(contacts, userType, true);
        saveTask.execute();
    }

    class SaveContactTask extends AsyncTask<Void, Void, Boolean> {

        private List<Contact> contacts;
        private int userType;
        private boolean flag;

        SaveContactTask(List<Contact> contacts, int userType, boolean f_flag) {
            this.contacts = contacts;
            this.userType = userType;
            this.flag = f_flag;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
        	DataHelper dataHelper = DataHelper.getHelper(getActivity());
            String loginName = BaseApplication.getInstance().getDefaultAccount().getLoginname();
            if(contacts != null && contacts.size() >= 0) {
	            try {
	                DeleteBuilder<Contact, Integer> deleteContactBuilder = dataHelper.getContactData().deleteBuilder();
	                deleteContactBuilder.where().eq("loginName", loginName).and().eq("usertype", userType);
	                int result1 = deleteContactBuilder.delete();
	                LogUtils.i("ContactData().deleteBuilder().delete():" + result1);
	                for (Contact contact : contacts) {
	                    /*DeleteBuilder<Contact, Integer> deleteContactBuilder2 = dataHelper.getContactData().deleteBuilder();
	                    deleteContactBuilder.where().eq("id", contact.getId()).and().eq("loginName", loginName).and().eq("usertype", userType);
	                    deleteContactBuilder.delete();*/
	                	contact.setUsertype(userType);
	                    dataHelper.getContactData().create(contact);
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
            }
            return flag;
        }

        @Override
        protected void onPostExecute(Boolean result) {
        	if (result) {
				loadLocalContacts();
			}
        }
    }
    
	private void loadLocalContacts() {
		LogUtils.d("loadLocalContacts");
		new LoadContacts().execute();
	}

	private class LoadContacts extends AsyncTask<Integer, Void, Boolean> {

		private List<Contact> mContacts;

		public LoadContacts() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// showProgressBar(true);
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			DataHelper helper = getDBHelper();
			DataHelper.getHelper(getActivity());
			String loginName = BaseApplication.getInstance().getDefaultAccount().getLoginname();
			try {
				QueryBuilder<Contact, Integer> contactBuilder = helper.getContactData()
						.queryBuilder();
				contactBuilder.orderBy("sortKey", true).where().eq("loginName", loginName).and()
						.eq("usertype", "1");
				mContacts = contactBuilder.query();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// 去掉自己
			// adapter.setIMContactGroups(removeMyselfContact(result));

			if (mContacts == null) {
				mContacts = new ArrayList<Contact>();
			}
			//
			adapter.setDatas(mContacts);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}