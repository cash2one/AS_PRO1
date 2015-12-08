package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.SelectClassActivity.HereAdapter.Group_CheckBox_Click;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Group;
import com.linkage.mobile72.sh.data.Person;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshListView;

public class SelectClassActivity extends BaseActivity implements View.OnClickListener {
	public static final String TAG = "SelectClassActivity";

	public static final String RECEIVER_RESULT = "receiver_result";
	private Button commit;
	private ArrayList<Group> mData;
	private PullToRefreshListView receiverListView;
	private HereAdapter mAdapter;
	private TextView mEmpty;

	private View mProgress;
	private Boolean needGet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_class);

//		mData = (ArrayList<Group>) getIntent().getExtras().getSerializable(RECEIVER_RESULT);
		if (mData == null) {
			mData = new ArrayList<Group>();
			needGet = true;
		}
		initView();

		if (needGet) {
			fetchData();
		} else {
			commit.setVisibility(View.VISIBLE);
		}
	}

	private void initView() {
		setTitle("发送范围");
		findViewById(R.id.back).setOnClickListener(this);
		commit = (Button) findViewById(R.id.set);
		commit.setText("完成");
		commit.setOnClickListener(this);

		mProgress = findViewById(R.id.progress_container);
		receiverListView = (PullToRefreshListView) findViewById(R.id.base_pull_list);
//		receiverListView.setGroupIndicator(null);
		receiverListView.setMode(Mode.DISABLED);
		mAdapter = new HereAdapter();
		receiverListView.setAdapter(mAdapter);
		receiverListView.setDivider(null);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("查无数据");
	}

	private void fetchData() {
		mProgress.setVisibility(View.VISIBLE);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getHomeSchoolGroupInfo");

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getHomeSchoolGroupInfo,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						mProgress.setVisibility(View.GONE);
						commit.setVisibility(View.VISIBLE);
//						LogUtils.i(TAG + " response=" + response);
						System.out.println(TAG + " response=" + response);
						
						if (response.optInt("ret", -1) == 0) {
							mData.clear();
							JSONArray groupArray = response.optJSONArray("groupList");
							//如果有数据，增加一个空的对象，用于表示“全部”
                            if(groupArray.length() > 0)
                            {
                                Group allGroup = new Group();
                                allGroup.setPersons(new ArrayList<Person>());
                                mData.add(allGroup);
                            }
							for (int i = 0; i < groupArray.length(); i++) {
							  
								JSONObject groupObj = groupArray.optJSONObject(i);
								Group group = new Group();
								group.setId(groupObj.optLong("group_id"));
								group.setName(groupObj.optString("group_name"));

								JSONArray personArray = groupObj.optJSONArray("group_members");
								List<Person> persons = new ArrayList<Person>();
								for (int j = 0; j < personArray.length(); j++) {
									JSONObject personObj = personArray.optJSONObject(j);
									Person p = new Person();
									p.setId(personObj.optLong("id"));
									p.setName(personObj.optString("name"));
									p.setUseravatar(personObj.optString("head_image"));
									persons.add(p);
								}
								group.setPersons(persons);
								mData.add(group);
							}
						} else {
							T.showShort(SelectClassActivity.this, response.optString("msg"));
						}
						mAdapter.notifyDataSetChanged();
						if (mAdapter.isEmpty()) {
							mEmpty.setVisibility(View.VISIBLE);
						} else {
							mEmpty.setVisibility(View.GONE);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, SelectClassActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	class HereAdapter extends BaseAdapter {

		public HereAdapter() {

		}


		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
//			View view;
			final ViewHolder viewHolder = new ViewHolder();
            if (position == 0)
            {
                convertView = getLayoutInflater().inflate(R.layout.item_list_single_class_select_checkbox,
                        null);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.list_textshow);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.list_checkbox);
                viewHolder.imgItem = (ImageView) convertView.findViewById(R.id.image_group_item);
                viewHolder.textView.setText("全部");
                viewHolder.imgItem.setVisibility(View.GONE);
                viewHolder.checkBox.setChecked(getItem(0).isChecked());
                viewHolder.checkBox.setOnClickListener(new All_Group_CheckBox_Click());
                convertView.setOnClickListener(new All_Group_CheckBox_Click());
                viewHolder.checkBox.setOnClickListener(new All_Group_CheckBox_Click());
            }
            else
            {
              convertView = getLayoutInflater().inflate(R.layout.item_list_single_class_select_checkbox, null);
              viewHolder.textView = (TextView) convertView.findViewById(R.id.list_textshow);
              viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.list_checkbox);
              viewHolder.imgItem = (ImageView) convertView.findViewById(R.id.image_group_item);
              viewHolder.imgItem.setVisibility(View.GONE);
              Group group = getItem(position);
              viewHolder.textView.setText(group.getName());
              // 找到需要选中的条目
              viewHolder.checkBox.setChecked(group.isChecked());
              viewHolder.checkBox.setOnClickListener(new Group_CheckBox_Click(position));
              convertView.setOnClickListener(new Group_CheckBox_Click(position));
              viewHolder.checkBox.setOnClickListener(new Group_CheckBox_Click(position));
            }
            convertView.setTag(viewHolder);
			return convertView;
		}

		class Group_CheckBox_Click implements View.OnClickListener {

            private int groupPosition;

            Group_CheckBox_Click(int groupPosition) {
                this.groupPosition = groupPosition;
            }

            public void onClick(View v) {
                mData.get(groupPosition).toggle();
                // 將 Children 的 isChecked 全面設成跟 Group 一樣
//                int childrenCount = mData.get(groupPosition).getPersons().size();
//                boolean groupIsChecked = mData.get(groupPosition).isChecked();
//                for (int i = 0; i < childrenCount; i++)
//                    mData.get(groupPosition).getPersons().get(i).setChecked(groupIsChecked);
                // 注意，一定要通知 ExpandableListView 資料已經改變，ExpandableListView 會重新產生畫面
                boolean isAllCheck = true;
                for(int j = 1;j<mData.size();j++)
                {
                    isAllCheck = mData.get(j).isChecked();
                    if(!isAllCheck)
                    {
                        break;
                    }
                }
                mData.get(0).setChecked(isAllCheck);
                
                notifyDataSetChanged();
            }
        }
		
		/**
         * 全部　选择项　的监听
         *
         */
        class All_Group_CheckBox_Click implements View.OnClickListener {

            private boolean curCheck;
            All_Group_CheckBox_Click() {
                
            }

            public void onClick(View v) {
                mData.get(0).toggle();
                curCheck = mData.get(0).isChecked();
                //对所有数据作整体处理
                for(int j = 0;j<mData.size();j++)
                {
                    mData.get(j).setChecked(curCheck);
                    
                }
                
                // 注意，一定要通知 ExpandableListView 資料已經改變，ExpandableListView 會重新產生畫面
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Group getItem(int position)
        {
            // TODO Auto-generated method stub
            return mData.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return 0;
        }


	}

	class ViewHolder {
		public TextView textView;
		public CheckBox checkBox;
		public ImageView imgItem;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.set:
		    removeHead();
			Intent it = new Intent();
			Bundle b = new Bundle();
			b.putSerializable(RECEIVER_RESULT, mData);
			it.putExtras(b);
			setResult(RESULT_OK, it);
			finish();
			break;
		}
	}
	
	private void removeHead()
    {
        // TODO Auto-generated method stub
        if(mData !=null && mData.size() > 0)
        {
            if(mData.get(0).getName() == null || mData.get(0).getName().equals(""))
            {
                mData.remove(0);
            }
        }
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}