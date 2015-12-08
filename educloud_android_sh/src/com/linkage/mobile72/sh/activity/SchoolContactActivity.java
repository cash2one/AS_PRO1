package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.im.NewChatActivity;
import com.linkage.mobile72.sh.adapter.JoinFriendAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.data.http.ApplyFriendBean;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.linkage.ui.widget.PullToRefreshListView;
/**
 * 校园通讯录页面
 * @author Yao
 */
public class SchoolContactActivity extends BaseActivity {
	
	private static final String TAG = SchoolContactActivity.class.getSimpleName();
	
	private List<ApplyFriendBean> mData;
	private List<Contact> mContact;
	private JoinFriendAdapter mAdapter;
	private PullToRefreshListView mListView;
	private TextView mEmpty;
	
	//	private Button btnSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recomment_friends_layout);
		setTitle("校园通讯录");
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
        //		btnSearch = (Button) findViewById(R.id.btn_search);
        //		btnSearch.setVisibility(View.VISIBLE);
        //		btnSearch.setOnClickListener(new OnClickListener()
        //        {
        //            
        //            @Override
        //            public void onClick(View v)
        //            {
        //                startActivity(new Intent(SchoolContactActivity.this, SearchPersonActivity.class));
        //            }
        //        });
		
		RelativeLayout searchLayout = (RelativeLayout)findViewById(R.id.search_btn);
		searchLayout.setVisibility(View.VISIBLE);
        searchLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SchoolContactActivity.this, LocalSearchResultActivity.class);
                i.putExtra("type", Consts.SEARCH_TYPE_CONTRACT);
                startActivity(i);
            }
        });
		
		mData = new ArrayList<ApplyFriendBean>();
		mListView = (PullToRefreshListView) findViewById(R.id.base_pull_list);
		//mListView.setAdapter(mAdapter);
		mListView.setDivider(null);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("暂时没有数据");
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchSchoolContact(false);
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ApplyFriendBean friend = mAdapter.getItem(position);
//				Intent intent = NewChatActivity.getIntent(SchoolContactActivity.this, friend.getUserId(),
//		                friend.getUserName(), ChatType.CHAT_TYPE_SINGLE,1);
//		        startActivity(intent);
		        
		        Intent intent = new Intent();
				intent.setClass(SchoolContactActivity.this, ChatActivity.class);
				Bundle bundle = new Bundle();

				bundle.putString("chatid", Consts.APP_ID + friend.getUserId().toString());
				bundle.putInt("chattype",  ChatType.CHAT_TYPE_SINGLE);
				bundle.putString("name", friend.getUserName());
				LogUtils.v("name" + friend.getUserName());
				bundle.putInt("type", 0);

				intent.putExtra("data", bundle);
				LogUtils.d("starting chat----> buddyId=" + friend.getUserId().toString()
						+ " chattype=" + ChatType.CHAT_TYPE_SINGLE);
				startActivity(intent);
		        
			}
			
		});
		mListView.setMode(Mode.PULL_FROM_START);
		fetchSchoolContact(true);
	}
	
	private void fetchSchoolContact(final boolean firstRefresh) {
		if(firstRefresh) {
			ProgressDialogUtils.showProgressDialog("", this, true);
		}
		String loginName = BaseApplication.getInstance().getDefaultAccount().getLoginname();
		try {
			final QueryBuilder<Contact, Integer> contactBuilder = getDBHelper().getContactData().queryBuilder();
			contactBuilder.where().eq("loginName", loginName).and().eq("usertype", "2").query();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						mContact = contactBuilder.query();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					mData.clear();
					if(mContact != null && mContact.size() > 0) {
						for(Contact c : mContact) {
							ApplyFriendBean f = new ApplyFriendBean();
							f.setAvater(c.getAvatar());
							f.setUserId(c.getId());
							f.setUserName(c.getName());
							f.setSchool(c.getPhone());
							mData.add(f);
						}
					}
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							ProgressDialogUtils.dismissProgressBar();
							mAdapter = new JoinFriendAdapter(SchoolContactActivity.this, imageLoader, false, mData, 1);
							mListView.setAdapter(mAdapter);
							if (mAdapter.isEmpty()) {
								mEmpty.setVisibility(View.VISIBLE);
							} else {
								mEmpty.setVisibility(View.GONE);
							}
							mListView.onRefreshComplete();
						}
					}, 500);
				}}, 500);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		BaseApplication.getInstance().cancelPendingRequests(TAG);
		super.onDestroy();
	}
	
}
