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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClassContactBean;
import com.linkage.mobile72.sh.data.http.ClassMemberBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

/**
 * @author wl
 * 班级通讯录
 */
public class ClassContactActivity extends BaseActivity
{
    
    private static final String TAG = ClassContactActivity.class.getSimpleName();
    
    private List<ClassContactBean> mData = new ArrayList<ClassContactBean>();
    
    private HereAdapter mAdapter;
    
    private ExpandableListView mListView;
    
    private TextView mEmpty;
    
    private View mProgress;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_contact);
        setTitle("班级通讯录");
        findViewById(R.id.back).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        
        mProgress = findViewById(R.id.progress_container);
        mListView = (ExpandableListView) findViewById(android.R.id.list);
        mListView.setGroupIndicator(null);
        
        mAdapter = new HereAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setDivider(null);
        mEmpty = (TextView) findViewById(android.R.id.empty);
        mEmpty.setText("查无数据");
        
        mListView.setOnChildClickListener(new OnChildClickListener()
        {
            
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id)
            {
                // TODO Auto-generated method stub
                ClassMemberBean person = mAdapter.getChild(groupPosition, childPosition);
                Intent intent = new Intent(ClassContactActivity.this,
                        PersonalInfoActivity.class);
                intent.putExtra("id", person.getUserId());
                startActivity(intent);
                /*Intent intent = NewChatActivity.getIntent(ClassContactActivity.this, person.getUserId(),
                		person.getNickName(), ChatType.CHAT_TYPE_SINGLE,1);
		        startActivity(intent);*/
                return true;
            }
        });
        
        fetchData();
    }
    
    private void fetchData()
    {
        mProgress.setVisibility(View.VISIBLE);
        
        HashMap<String, String> params = new HashMap<String, String>();
        
        params.put("commandtype", "getClassroomContacts");
        
        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
                Consts.SERVER_URL, Request.Method.POST, params, true,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        mProgress.setVisibility(View.GONE);
                        LogUtils.i(TAG + ":response=" + response);
                        if (response.optInt("ret", -1) == 0)
                        {
                            mData.clear();
                            JSONArray groupArray = response.optJSONArray("classdata");
                            for (int i = 0; i < groupArray.length(); i++)
                            {
                                JSONObject jsonObj = groupArray.optJSONObject(i);
                                ClassContactBean clazz = new ClassContactBean();
                                clazz.setClassName(jsonObj.optString("className"));
                                clazz.setClassLevel(jsonObj.optString("classLevel"));
                                clazz.setClassNumber(jsonObj.optInt("classNumber"));
                                clazz.setAvatar(jsonObj.optString("avatar"));
                                clazz.setClassroomId(jsonObj.optLong("classroomId"));
                                clazz.setTaskid(jsonObj.optLong("taskid"));
                                
                                JSONArray personArray = jsonObj.optJSONArray("classmemberdata");
                                List<ClassMemberBean> persons = new ArrayList<ClassMemberBean>();
                                for (int j = 0; j < personArray.length(); j++)
                                {
                                    JSONObject personObj = personArray.optJSONObject(j);
                                    ClassMemberBean clazzMember = new ClassMemberBean();
                                    clazzMember.setUserId(personObj.optLong("userId"));
                                    clazzMember.setPhone(personObj.optString("phone"));
                                    clazzMember.setNickName(personObj.optString("nickName"));
                                    clazzMember.setAvatar(personObj.optString("avatar"));
                                    clazzMember.setUserRole(personObj.optInt("userRole"));
                                    persons.add(clazzMember);
                                }
                                clazz.setMemberInfoList(persons);
                                mData.add(clazz);
                            }
                        }
                        else
                        {
                            T.showShort(ClassContactActivity.this,
                                    response.optString("msg"));
                        }
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.isEmpty())
                        {
                            mEmpty.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            mEmpty.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError arg0)
                    {
                        StatusUtils.handleError(arg0, ClassContactActivity.this);
                    }
                });
        BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }
    
    @SuppressLint("InflateParams")
    class HereAdapter extends BaseExpandableListAdapter
    {
        public HereAdapter()
        {
            
        }
        
        @Override
        public int getGroupCount()
        {
            return mData.size();
        }
        
        @Override
        public int getChildrenCount(int groupPosition)
        {
            return mData.get(groupPosition).getMemberInfoList().size();
        }
        
        @Override
        public ClassContactBean getGroup(int groupPosition)
        {
            
            return mData.get(groupPosition);
        }
        
        @Override
        public ClassMemberBean getChild(int groupPosition, int childPosition)
        {
            
            return mData.get(groupPosition)
                    .getMemberInfoList()
                    .get(childPosition);
        }
        
        @Override
        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }
        
        @Override
        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }
        
        @Override
        public boolean hasStableIds()
        {
            return false;
        }
        
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent)
        {
            View view;
            ViewHolder viewHolder = null;
            if (convertView == null)
            {
                viewHolder = new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.item_class_contact_group_layout,
                        null);
                viewHolder.textView = (TextView) view.findViewById(R.id.list_textshow);
                viewHolder.imgItem = (ImageView) view.findViewById(R.id.image_group_item);
                view.setTag(viewHolder);
            }
            else
            {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            if (isExpanded)
            {
                viewHolder.imgItem.setBackgroundResource(R.drawable.expandable_groupitem_onclick);
            }
            else
            {
                viewHolder.imgItem.setBackgroundResource(R.drawable.expandable_groupitem);
            }
            ClassContactBean group = getGroup(groupPosition);
            viewHolder.textView.setText(group.getClassName());
            return view;
        }
        
        @Override
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent)
        {
            View view;
            ViewHolder viewHolder = null;
            if (convertView == null)
            {
                viewHolder = new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.item_class_contact_chilen_layout,
                        null);
                viewHolder.textView = (TextView) view.findViewById(R.id.list_textshow);
                viewHolder.imgItem = (ImageView) view.findViewById(R.id.user_avater);
                viewHolder.phoneView = (TextView) view.findViewById(R.id.list_phoneshow);
                view.setTag(viewHolder);
            }
            else
            {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            final ClassMemberBean person = getChild(groupPosition,
                    childPosition);
            viewHolder.textView.setText(person.getNickName());
            viewHolder.phoneView.setText(person.getPhone());
            imageLoader.displayImage(person.getAvatar(), viewHolder.imgItem);
            return view;
        }
        
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return true;
        }
    }
    
    class ViewHolder
    {
        public TextView textView;
        
        public TextView phoneView;
        
        public ImageView imgItem;
    }
    
    @Override
    protected void onDestroy()
    {
        BaseApplication.getInstance().cancelPendingRequests(TAG);
        super.onDestroy();
    }
    
}
