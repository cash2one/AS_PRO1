package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Group;
import com.linkage.mobile72.sh.data.Person;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

public class SelectReceiverActivity extends BaseActivity implements View.OnClickListener {
	public static final String TAG = "SelectReceiverActivity";

	public static final String RECEIVER_FROM = "receiver_from";
	public static final String RECEIVER_RESULT = "receiver_result";
	private Button commit;
	private int from;
	private ArrayList<Group> mData;
	private ExpandableListView receiverListView;
	private HereAdapter mAdapter;
	private TextView mEmpty;

	private View mProgress;
	private Boolean needGet = false;

	@SuppressWarnings("unchecked")
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_receiver);
		
		Bundle bundle = getIntent().getExtras();
		if(bundle == null) {
			finish();
			return;
		}
		from = bundle.getInt(RECEIVER_FROM, 1);
		mData = (ArrayList<Group>) bundle.getSerializable(RECEIVER_RESULT);
		
		if (mData == null || mData.size() <= 0) {
			mData = new ArrayList<Group>();
			needGet = true;
		}
		//如果有数据，增加一个全部的数据
		else {
		    boolean isAllCheck = true;
		    //如果数据原来都被选中，全部设置为true
            for(int i = 0;i<mData.size();i++)
            {
                if(!mData.get(i).isChecked())
                {
                    isAllCheck = false;
                    break;
                }
            }
            
            Group allGroup = new Group();
            allGroup.setPersons(new ArrayList<Person>());
            allGroup.setChecked(isAllCheck);
            mData.add(0, allGroup);
        }
		initView();

		if (needGet) {
			fetchData();
		} else {
			commit.setVisibility(View.VISIBLE);
		}
	}

	private void initView() {
		setTitle("选择收件人");
		findViewById(R.id.back).setOnClickListener(this);
		commit = (Button) findViewById(R.id.set);
		commit.setText("完成");
		commit.setOnClickListener(this);

		mProgress = findViewById(R.id.progress_container);
		receiverListView = (ExpandableListView) findViewById(android.R.id.list);
		receiverListView.setGroupIndicator(null);

		mAdapter = new HereAdapter();
		receiverListView.setAdapter(mAdapter);
		receiverListView.setDivider(null);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("查无数据");
	}

	private void fetchData() {
		mProgress.setVisibility(View.VISIBLE);

		HashMap<String, String> params = new HashMap<String, String>();
		String url = "";
		if(from == 1) {
			params.put("commandtype", "getHomeSchoolGroupInfo");
			url = Consts.SERVER_getHomeSchoolGroupInfo;
		}else {
			params.put("commandtype", "getOfficeGroupInfo");
			url = Consts.SERVER_getOfficeGroupInfo;
		}
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(url,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						mProgress.setVisibility(View.GONE);
						commit.setVisibility(View.VISIBLE);
						LogUtils.i(TAG + ":response=" + response);
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
								if(groupObj.has("group_type")){
									group.setType(groupObj.optInt("group_type"));
								}
								JSONArray personArray = groupObj.optJSONArray("group_members");
								List<Person> persons = new ArrayList<Person>();
								for (int j = 0; j < personArray.length(); j++) {
									JSONObject personObj = personArray.optJSONObject(j);
									Person p = new Person();
									p.setId(personObj.optLong("id"));
									p.setName(personObj.optString("name"));
									p.setUseravatar(personObj.optString("head_image"));
									p.setPhone(personObj.optString("tel"));
									persons.add(p);
								}
								group.setPersons(persons);
								mData.add(group);
							}
						} else {
							T.showShort(SelectReceiverActivity.this, response.optString("msg"));
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
						StatusUtils.handleError(arg0, SelectReceiverActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	@SuppressLint("InflateParams")
    class HereAdapter extends BaseExpandableListAdapter {

		public HereAdapter() {

		}

		@Override
		public int getGroupCount() {
			return mData.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
		    
			return mData.get(groupPosition ).getPersons().size();
		}

		@Override
		public Group getGroup(int groupPosition) {

			return mData.get(groupPosition);
		}

		@Override
		public Person getChild(int groupPosition, int childPosition) {
		    
			return mData.get(groupPosition ).getPersons().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
				ViewGroup parent) {
//			View view;
//			ViewHolder viewHolder = null;
//			if (convertView == null) {
//				viewHolder = new ViewHolder();
//				view = getLayoutInflater().inflate(R.layout.item_list_single_text_checkbox, null);
//				viewHolder.textView = (TextView) view.findViewById(R.id.list_textshow);
//				viewHolder.checkBox = (CheckBox) view.findViewById(R.id.list_checkbox);
//				viewHolder.imgItem = (ImageView) view.findViewById(R.id.image_group_item);
//				view.setTag(viewHolder);
//			} else {
//				view = convertView;
//				viewHolder = (ViewHolder) view.getTag();
//			}
			final ViewHolder viewHolder = new ViewHolder();
            if (groupPosition == 0)
            {
                convertView = getLayoutInflater().inflate(R.layout.item_list_single_text_checkbox,
                        null);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.list_textshow);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.list_checkbox);
                viewHolder.imgItem = (ImageView) convertView.findViewById(R.id.image_group_item);
                viewHolder.textView.setText("全部");
                viewHolder.imgItem.setVisibility(View.GONE);
                viewHolder.checkBox.setChecked(getGroup(0).isChecked());
                viewHolder.checkBox.setOnClickListener(new All_Group_CheckBox_Click());
            }
			else
			{
			  convertView = getLayoutInflater().inflate(R.layout.item_list_single_text_checkbox, null);
              viewHolder.textView = (TextView) convertView.findViewById(R.id.list_textshow);
              viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.list_checkbox);
              viewHolder.imgItem = (ImageView) convertView.findViewById(R.id.image_group_item);
              
              if (isExpanded) {
                  viewHolder.imgItem.setBackgroundResource(R.drawable.expandable_groupitem_onclick);
              } else {
                  viewHolder.imgItem.setBackgroundResource(R.drawable.expandable_groupitem);
              }
              Group group = getGroup(groupPosition);
              viewHolder.textView.setText(group.getName());
              // 找到需要选中的条目
              viewHolder.checkBox.setChecked(group.isChecked());
              viewHolder.checkBox.setOnClickListener(new Group_CheckBox_Click(groupPosition));
            }
			
			convertView.setTag(viewHolder);
			return convertView;
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
                    // 將 Children 的 isChecked 全面設成跟 Group 一樣
                    int childrenCount = mData.get(j).getPersons().size();
                    boolean groupIsChecked = mData.get(j).isChecked();
                    for (int i = 0; i < childrenCount; i++)
                        mData.get(j).getPersons().get(i).setChecked(groupIsChecked);
                }
                
                // 注意，一定要通知 ExpandableListView 資料已經改變，ExpandableListView 會重新產生畫面
                notifyDataSetChanged();
            }
        }
		

		class Group_CheckBox_Click implements View.OnClickListener {

			private int groupPosition;

			Group_CheckBox_Click(int groupPosition) {
				this.groupPosition = groupPosition;
			}

			public void onClick(View v) {
				mData.get(groupPosition).toggle();
				// 將 Children 的 isChecked 全面設成跟 Group 一樣
				int childrenCount = mData.get(groupPosition).getPersons().size();
				boolean groupIsChecked = mData.get(groupPosition).isChecked();
				for (int i = 0; i < childrenCount; i++)
					mData.get(groupPosition).getPersons().get(i).setChecked(groupIsChecked);
				// 注意，一定要通知 ExpandableListView 資料已經改變，ExpandableListView 會重新產生畫面
				notifyDataSetChanged();
			}
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
				View convertView, ViewGroup parent) {
			View view;
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				view = getLayoutInflater().inflate(R.layout.item_list_single_text_circurimg, null);
				viewHolder.textView = (TextView) view.findViewById(R.id.list_textshow);
				viewHolder.checkBox = (CheckBox) view.findViewById(R.id.list_checkbox);
				viewHolder.imgItem = (ImageView) view.findViewById(R.id.user_avater);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			// RelativeLayout.LayoutParams lp = new
			// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
			// RelativeLayout.LayoutParams.WRAP_CONTENT);
			// lp.setMargins(70, 16, 20, 16);
			// viewHolder.textView.setLayoutParams(lp);
			Person person = getChild(groupPosition, childPosition);
			viewHolder.textView.setText(person.getName());
			// 找到需要选中的条目
			viewHolder.checkBox.setChecked(person.isChecked());
			viewHolder.checkBox.setTag(String.valueOf(childPosition));
			viewHolder.checkBox.setOnClickListener(new Child_CheckBox_Click(groupPosition,
					childPosition));
			return view;
		}

		/** 勾選 Child CheckBox 時，存 Child CheckBox 的狀態 */
		class Child_CheckBox_Click implements View.OnClickListener {

			private int groupPosition;
			private int childPosition;

			Child_CheckBox_Click(int groupPosition, int childPosition) {
				this.groupPosition = groupPosition;
				this.childPosition = childPosition;
			}

			public void onClick(View v) {
				mData.get(groupPosition).getPersons().get(childPosition).toggle();

				// 檢查 Child CheckBox 是否有全部勾選，以控制 Group CheckBox
				int childrenCount = mData.get(groupPosition).getPersons().size();
				boolean childrenAllIsChecked = true;
				for (int i = 0; i < childrenCount; i++) {
					if (!mData.get(groupPosition).getPersons().get(i).isChecked())
						childrenAllIsChecked = false;
				}

				mData.get(groupPosition).setChecked(childrenAllIsChecked);

				// 注意，一定要通知 ExpandableListView 資料已經改變，ExpandableListView 會重新產生畫面
				notifyDataSetChanged();
			}
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
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
		    removeHead();
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