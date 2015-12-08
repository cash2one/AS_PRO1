package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.AddressListAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.data.http.PhoneNameValuePair;
import com.linkage.mobile72.sh.utils.PhoneContactUtils;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshListView;

public class LocalAddressActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = LocalAddressActivity.class.getSimpleName();
	private PullToRefreshListView listView;
	private List<PhoneNameValuePair> inviteFriends;
	private AddressListAdapter mAdapter;
	private Button back;
	private TextView mEmpty;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_friend);
		setTitle("手机通讯录");
		
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(this);
		
		listView = (PullToRefreshListView)findViewById(R.id.base_pull_list);
		inviteFriends = new ArrayList<PhoneNameValuePair>();
		fetchInviteFriend(true);
		mAdapter = new AddressListAdapter(this, inviteFriends);
		listView.setAdapter(mAdapter);
		listView.setDivider(null);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("查无数据");
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchInviteFriend(false);
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}
	
	class FetchAddressesTask extends AsyncTask<Void, Void, List<PhoneNameValuePair>> {
		private boolean firstRefresh;
		
		public FetchAddressesTask(boolean firstRefresh) {
			super();
			this.firstRefresh = firstRefresh;
		}
		
		@Override
		protected List<PhoneNameValuePair> doInBackground(Void... params) {
			inviteFriends.clear();
	    	if(firstRefresh) {
//	    		ProgressDialogUtils.showProgressDialog("", this, false);
	    	}
//	    	List<PhoneNameValuePair> pairs = new ArrayList<PhoneNameValuePair>();
	    	Map<String,String> friend = new HashMap<String, String>();
	    	List<Contact> xkzContacts = null;
	    	try {
	    		xkzContacts = getDBHelper().getContactData().queryForAll();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	if(xkzContacts != null && xkzContacts.size() > 0) {
	    		for(Contact contact : xkzContacts) {
	    			friend.put(contact.getPhone(), contact.getName());
	    		}
	    	}
	    	Map<String,String> localContact = PhoneContactUtils.fetchPhoneContacts(LocalAddressActivity.this);
	    	if(localContact != null) {
	    		friend.putAll(localContact);
	    	}
	    	for(Iterator<String> it = friend.keySet().iterator(); it.hasNext();) {
	    		String key = it.next();//key
				String value = friend.get(key);//value
				PhoneNameValuePair pair = new PhoneNameValuePair();
				pair.setUserPhone(key);
				pair.setUserName(value);
				inviteFriends.add(pair);
	    	}
			return inviteFriends;
		}
		
		@Override
		protected void onPostExecute(List<PhoneNameValuePair> result) {
			super.onPostExecute(result);
			ProgressDialogUtils.dismissProgressBar();
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
    
    private void fetchInviteFriend(boolean firstRefresh) {
    	if(firstRefresh) {
    		ProgressDialogUtils.showProgressDialog("", this, true);
    	}
    	
    	new FetchAddressesTask(firstRefresh).execute();
    	
//    	HashMap<String, String> params = new HashMap<String, String>();
//    	JSONArray array = new JSONArray();
//    	try {
//	        for(PhoneNameValuePair a : pairs){
//	            JSONObject json = new JSONObject();
//				json.put("userName", a.getUserName());
//	            json.put("userPhone", a.getUserPhone());
//	            array.put(json);
//	        }
//        } catch (JSONException e) {
//			e.printStackTrace();
//		}
//    	params.put("commandtype", "getFriendList");
//		params.put("userlist", array.toString());
//
//		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
//			@Override
//			public void onResponse(JSONObject response) {
//				listView.onRefreshComplete();
//				ProgressDialogUtils.dismissProgressBar();
//				System.out.println("response=" + response);
//				if (response.optInt("ret") == 0) {
//					inviteFriends = InviteFriend.parseFromJson(response.optJSONArray("data"));
//					if(inviteFriends.size() > 0) {
//						mAdapter.addAll(inviteFriends);
//					}
//				} 
//				else {
//					StatusUtils.handleStatus(response, LocalAddressActivity.this);
//				}
//			}
//		}, new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				listView.onRefreshComplete();
//				ProgressDialogUtils.dismissProgressBar();
//				StatusUtils.handleError(arg0, LocalAddressActivity.this);
//			}
//		});
//    	BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }
    
}
