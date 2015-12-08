package com.linkage.mobile72.sh.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import com.linkage.mobile72.sh.adapter.ContactGroupListAdapter;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.swipelistview.SwipeListView;

public class ContactsGroupFragment extends BaseFragment {

	private static final String TAG = ContactsGroupFragment.class.getSimpleName();

	private SwipeListView mSwipeListView;
	private ContactGroupListAdapter adapter;
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

	public static ContactsGroupFragment create() {
		ContactsGroupFragment f = new ContactsGroupFragment();
		// Supply num input as an argument.
		Bundle args = new Bundle();
		// args.putInt("titleRes", titleRes);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contact_group, container, false);

		LogUtils.e("ContactFragment" + "onCreateView");
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

		adapter = new ContactGroupListAdapter(getActivity(), mHandler, imageLoader_group, mSwipeListView,
				new ArrayList<ClassRoom>());
		mSwipeListView.setAdapter(adapter);
		mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
		mSwipeListView.setonRefreshListener(new SwipeListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				syncSmsContact();
			}
		});

		new LoadContacts(true).execute();
//		syncSmsContact();
//		flash_data = 0;
//		EventBus.getDefault().register(this);
//		EventBus.getDefault().register(new ContactsGroupFragment());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
//		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onResume() {
		super.onResume();
//		System.out.println("-------------onResume+++++++++++:"
//				+ BaseApplication.getInstance().getflag());
//		if (flash_data == 1) {
//			flash_data = 0;
//			BaseApplication.getInstance().setflag(0);
//			syncSmsContact();
//		}
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
			// syncSmsContact();

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
		params.put("commandtype", "getJoinedClassroomList");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						LogUtils.d(TAG + " response=" + response);
						onSyncEnd();
						if (response.optInt("ret") == 0) {
							try{
								List<ClassRoom> classList1 = ClassRoom.parseFromJson(
										response.optJSONArray("data"), 1);
								List<ClassRoom> classList2 = ClassRoom.parseFromJson(
										response.optJSONArray("data2"), 2);
								classList1.addAll(classList2);
								syncContactsSucced(classList1);
							}catch(Exception ex){
								ex.printStackTrace();
							}
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
		}, 0);
	}

	@Override
	public void onDestroy() {
		BaseApplication.getInstance().cancelPendingRequests(TAG);
		super.onDestroy();
	}

	private void syncContactsSucced(List<ClassRoom> classRooms) {
		SaveContactClassroomTask saveTask = new SaveContactClassroomTask(classRooms);
		saveTask.execute();
	}

	private void loadLocalContacts(boolean firstload) {
		LogUtils.d("loadLocalContacts");
		new LoadContacts(firstload).execute();
	}

	class SaveContactClassroomTask extends AsyncTask<Void, Void, Boolean> {

		private List<ClassRoom> classRooms;

		SaveContactClassroomTask(List<ClassRoom> classRooms) {
			this.classRooms = classRooms;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DataHelper dataHelper = DataHelper.getHelper(getActivity());
			String loginName = BaseApplication.getInstance().getDefaultAccount().getLoginname();
			// helper.getContactData().executeRawNoArgs("DELETE FROM Contact");
			// all = dao.queryBuilder().orderBy("Id", true).where().eq("Type",
			// key).and().eq("owner",
			// Pub.user.getAccount()).and().eq("UserType",
			// Pub.user.getUserType()).query();
			try {
				DeleteBuilder<ClassRoom, Integer> deleteClassroomBuilder = dataHelper
						.getClassRoomData().deleteBuilder();
				deleteClassroomBuilder.where().eq("loginName", loginName);
				int result2 = deleteClassroomBuilder.delete();
				LogUtils.i("ClassRoomData().deleteBuilder().delete():" + result2);
				for (ClassRoom classroom : classRooms) {
					dataHelper.getClassRoomData().create(classroom);
					fetchClazzMember(classroom.getId());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				loadLocalContacts(false);
			}
		}
	}

	private class LoadContacts extends AsyncTask<Integer, Void, Boolean> {

		boolean firstload = true;
		private List<ClassRoom> mClassRooms;

		public LoadContacts(boolean firstload) {
			this.firstload = firstload;
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
				QueryBuilder<ClassRoom, Integer> classroomBuilder = helper.getClassRoomData()
						.queryBuilder();
				classroomBuilder.orderBy("joinOrManage", true).orderBy("schoolId", true).where()
						.eq("loginName", loginName);
				mClassRooms = classroomBuilder.query();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// 去掉自己
			// adapter.setIMContactGroups(removeMyselfContact(result));
			if (mClassRooms == null) {
				mClassRooms = new ArrayList<ClassRoom>();
			}
			adapter.setDatas(mClassRooms);
			if (firstload && adapter.isEmpty()) {
				syncSmsContact();
			} else {
				adapter.notifyDataSetChanged();
				onSyncEnd();
				// 获取真实ID
				// getRealIdIfNeed();
			}
		}
	}
	
	private void fetchClazzMember(final long classId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("commandtype", "getClassroomRemListByCId");
        params.put("classroomId", String.valueOf(classId));

        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optInt("ret") == 0) {
                    List<Contact> contactList = Contact.parseFromJsonByClassMember(response
                            .optJSONArray("data"), classId);
                    syncContactsSuccess(contactList, (int)classId);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }
	
	private void syncContactsSuccess(List<Contact> contacts, int userType) {
        SaveContactTask saveTask = new SaveContactTask(contacts, userType);
        saveTask.execute();
    }

    class SaveContactTask extends AsyncTask<Void, Void, Boolean> {

        private List<Contact> contacts;
        private long classid;

        SaveContactTask(List<Contact> contacts, long classid) {
            this.contacts = contacts;
            this.classid = classid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String loginName = BaseApplication.getInstance().getDefaultAccount().getLoginname();
            if(contacts != null && contacts.size() >= 0) {
	            try {
	                DeleteBuilder<Contact, Integer> deleteContactBuilder = getDBHelper().getContactData().deleteBuilder();
	                deleteContactBuilder.where().eq("loginName", loginName).and().eq("usertype", classid);
	                int result1 = deleteContactBuilder.delete();
	                LogUtils.i("ContactData().deleteBuilder().delete():" + result1);
	                for (Contact contact : contacts) {
	                    /*DeleteBuilder<Contact, Integer> deleteContactBuilder2 = dataHelper.getContactData().deleteBuilder();
	                    deleteContactBuilder.where().eq("id", contact.getId()).and().eq("loginName", loginName).and().eq("usertype", userType);
	                    deleteContactBuilder.delete();*/
	                	contact.setUsertype((int)classid);
	                	getDBHelper().getContactData().create(contact);
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }
    }

}