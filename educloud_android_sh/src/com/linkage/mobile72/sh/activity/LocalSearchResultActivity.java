package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.activity.im.NewChatActivity;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.lib.util.LogUtils;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 好友列表、班级列表、校园通讯录 的本地搜索结果页面
 * @author Yao
 */
public class LocalSearchResultActivity extends BaseActivity implements
        OnClickListener
{
    
    private static final String TAG = LocalSearchResultActivity.class.getSimpleName();
    
    private List<Contact> mFriendData;
    
    private List<ClassRoom> mClassRoomData;
    
    private LocalFriendAdapter mFriendAdapter;
    
    private GroupAdapter mGroupAdapter;
    
    private PullToRefreshListView mListView;
    
    private TextView mEmpty;
    
    private String keyword;
    
    private int page = 1;
    
    private EditText editInput;
    
    private Button searchBtn;
    
    private int searchType = 0;//搜索类型。 0 好友列表.1 校园通讯录  2 班级列表
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mb_manage_search_layout);
        setTitle("查找结果");
        findViewById(R.id.back).setOnClickListener(this);
        
        editInput = (EditText) findViewById(R.id.search_input);
        searchBtn = (Button) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(this);
        
        mListView = (PullToRefreshListView) findViewById(R.id.base_pull_list);
        searchType = getIntent().getIntExtra("type", 1);
        if (searchType == 2 || searchType == 3)
        {
            mFriendData = new ArrayList<Contact>();
            mFriendAdapter = new LocalFriendAdapter(this, imageLoader_group,
                    mFriendData);
            mListView.setAdapter(mFriendAdapter);
        }
        else if (searchType == 1)
        {
            mClassRoomData = new ArrayList<ClassRoom>();
            mGroupAdapter = new GroupAdapter(this, imageLoader_group,
                    mClassRoomData);
            mListView.setAdapter(mGroupAdapter);
        }
        
        mListView.setDivider(null);
        mEmpty = (TextView) findViewById(android.R.id.empty);
        mEmpty.setText("暂时没有数据");
            
        //        mListView.setOnRefreshListener(new OnRefreshListener2()
        //        {
        //            
        //            @Override
        //            public void onPullDownToRefresh(PullToRefreshBase refreshView)
        //            {
        //                page = 1;
        //                fetchData();
        //            }
        //            
        //            @Override
        //            public void onPullUpToRefresh(PullToRefreshBase refreshView)
        //            {
        //                fetchData();
        //            }
        //        });
        
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                
                if (position < 0)
                {
                    LogUtils.e(TAG + "invalid postion:" + position);
                    return;
                }
                
                Intent intent;
                switch(searchType)
                {
                    case Consts.SEARCH_TYPE_CLASS:
                        ClassRoom classrm = mGroupAdapter.getItem(position);
                        //                        LogUtils.i("lf",
                        //                                "pos:" + position + "classname:"
                        //                                        + classrm.getName());
                        if (classrm.getTaskid() == 0)
                        {
                            UIUtilities.showToast(LocalSearchResultActivity.this,
                                    "群组信息有误，无法进入");
                        }
                        else
                        {
//                            intent = NewChatActivity.getIntent(LocalSearchResultActivity.this,
//                                    classrm.getTaskid(),
//                                    classrm.getName(),
//                                    ChatType.CHAT_TYPE_GROUP,
//                                    0);
//                            startActivity(intent);
                        	
                        	 Intent intent_chat = new Intent();
                             intent_chat.setClass(LocalSearchResultActivity.this, ChatActivity.class);
         					Bundle bundle = new Bundle();
         					bundle.putString("chatid", classrm.getTaskid() + "");
         					bundle.putInt("chattype", ChatType.CHAT_TYPE_GROUP);
         					bundle.putInt("type", 0);
//         					bundle.putString("name", classrm.getName());
         					intent_chat.putExtra("data", bundle);
         					LogUtils.d( "contact starting chat----> buddyId=" + classrm.getTaskid() + " chattype=" + ChatType.CHAT_TYPE_SINGLE
         							+ " name=" + classrm.getName());
         					startActivity(intent_chat);
         					
         					
                        }
                        break;
                    
                    case Consts.SEARCH_TYPE_FRIEND:
                        Contact ct = mFriendAdapter.getItem(position);
                        //                        LogUtils.i("lf",
                        //                                "pos:" + position + "phone:" + ct.getPhone());
//                        intent = NewChatActivity.getIntent(LocalSearchResultActivity.this,
//                                ct.getId(),
//                                ct.getName(),
//                                ChatType.CHAT_TYPE_SINGLE,
//                                0);
//                        startActivity(intent);
                        Intent intent_chat = new Intent();
                        intent_chat.setClass(LocalSearchResultActivity.this, ChatActivity.class);
    					Bundle bundle = new Bundle();
    					bundle.putString("chatid", ct.getId() + "");
    					bundle.putInt("chattype", ChatType.CHAT_TYPE_SINGLE);
    					bundle.putInt("type", 1);
    					bundle.putString("name", ct.getName());
    					intent_chat.putExtra("data", bundle);
    					LogUtils.d( "contact starting chat----> buddyId=" + ct.getId() + " chattype=" + ChatType.CHAT_TYPE_SINGLE
    							+ " name=" + ct.getName());
    					startActivity(intent_chat);
                        break;
                    
                    case Consts.SEARCH_TYPE_CONTRACT:
                        Contact friend = mFriendAdapter.getItem(position);
                        //                        LogUtils.i("lf",
                        //                                "pos:" + position + "phone:"
                        //                                        + friend.getPhone());
                        intent = new Intent(LocalSearchResultActivity.this,
                                PersonalInfoActivity.class);
                        intent.putExtra("id", friend.getId());
                        startActivity(intent);
                        break;
                    
                    default:
                        break;
                
                }
                
                //                if (null != mFriendAdapter && 0 < position && position <= mFriendAdapter.getCount())
                
            }
        });
        
        mListView.setMode(Mode.DISABLED);
    }
    
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case R.id.search_btn:
                page = 1;
                fetchData();
                break;
            case R.id.back:
                finish();
                break;
        }
    }
    
    private void fetchData() {
		keyword = editInput.getText().toString();
		if(TextUtils.isEmpty(keyword)) {
			Toast.makeText(this, "搜的内容不能为空的", Toast.LENGTH_SHORT).show();
			return;
		}
		if(searchType == Consts.SEARCH_TYPE_FRIEND)
		{
		    DataHelper helper = getDBHelper();
            DataHelper.getHelper(this);
            String loginName = BaseApplication.getInstance().getDefaultAccount().getLoginname();
            try {
                QueryBuilder<Contact, Integer> contactBuilder = helper.getContactData()
                        .queryBuilder();
                contactBuilder.orderBy("sortKey", true).where().eq("loginName", loginName).and()
                        .eq("usertype", "1").and()
                        .like("name", "%"+keyword+"%");
                List<Contact> mContacts = contactBuilder.query();
                
                mFriendAdapter.setType(Consts.SEARCH_TYPE_FRIEND);
                mFriendAdapter.addAll(mContacts, false);

          if (mFriendAdapter.isEmpty()) {
              mEmpty.setVisibility(View.VISIBLE);
          } else {
              mEmpty.setVisibility(View.GONE);
          }
            } catch (SQLException e) {
                e.printStackTrace();
            }
		}
		
		else if(searchType == Consts.SEARCH_TYPE_CONTRACT)
        {
            DataHelper helper = getDBHelper();
            DataHelper.getHelper(this);
            String loginName = BaseApplication.getInstance().getDefaultAccount().getLoginname();
            try {
                QueryBuilder<Contact, Integer> contactBuilder = helper.getContactData()
                        .queryBuilder();
                contactBuilder.where().eq("loginName", loginName).and()
                        .eq("usertype", "2").and()
                        .like("phone", "%"+keyword+"%");
                List<Contact> mContacts = contactBuilder.query();
                
                mFriendAdapter.setType(Consts.SEARCH_TYPE_CONTRACT);
                mFriendAdapter.addAll(mContacts, false);

          if (mFriendAdapter.isEmpty()) {
              mEmpty.setVisibility(View.VISIBLE);
          } else {
              mEmpty.setVisibility(View.GONE);
          }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		
		else if(searchType == Consts.SEARCH_TYPE_CLASS)
        {
            DataHelper helper = getDBHelper();
            
            DataHelper.getHelper(this);
            String loginName = BaseApplication.getInstance().getDefaultAccount().getLoginname();
            try {
                QueryBuilder<ClassRoom, Integer> contactBuilder = helper.getClassRoomData()
                        .queryBuilder();
                contactBuilder.where().eq("loginName", loginName).and()
                        .like("name", "%"+keyword+"%").or().like("schoolName", "%"+keyword+"%");
                List<ClassRoom> mContacts = contactBuilder.query();
                
                mGroupAdapter.addAll(mContacts, false);

          if (mGroupAdapter.isEmpty()) {
              mEmpty.setVisibility(View.VISIBLE);
          } else {
              mEmpty.setVisibility(View.GONE);
          }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
    
    @Override
    protected void onDestroy()
    {
        //		BaseApplication.getInstance().cancelPendingRequests(TAG);
        super.onDestroy();
    }
    
    class LocalFriendAdapter extends BaseAdapter
    {
        
        private Context mContext;
        
        private ImageLoader imageLoader;
        
        private LayoutInflater mLayoutInflater;
        
        private List<Contact> clazzs;
        
        private int type = Consts.SEARCH_TYPE_FRIEND;
        
        public LocalFriendAdapter(Context context, ImageLoader imageLoader,
                List<Contact> clazzs)
        {
            this.mContext = context;
            this.imageLoader = imageLoader;
            this.mLayoutInflater = LayoutInflater.from(mContext);
            this.clazzs = clazzs;
        }
        
        public void addAll(List<Contact> clazzs, boolean append)
        {
            if (this.clazzs != null)
            {
                if (!append)
                {
                    this.clazzs.clear();
                }
                this.clazzs.addAll(clazzs);
            }
            else
            {
                this.clazzs = clazzs;
            }
            notifyDataSetChanged();
        }
        
        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return clazzs.size();
        }
        
        @Override
        public Contact getItem(int position)
        {
            // TODO Auto-generated method stub
            return clazzs.get(position);
        }
        
        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return clazzs.get(position).getId();
        }
        
        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent)
        {
            ViewHolder holder;
            final Contact friend = getItem(position);
            if (convertView == null || convertView.getTag() == null)
            {
                convertView = mLayoutInflater.inflate(R.layout.adapter_join_friend_list_item,
                        parent,
                        false);
                /*convertView.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(mContext,PersonalInfoActivity.class);
                        intent.putExtra("id", friend.getUserId());
                        mContext.startActivity(intent);
                    }
                });*/
                holder = new ViewHolder();
                holder.init(convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            
            if (Consts.SEARCH_TYPE_FRIEND == type)
            {
                holder.userName.setText(friend.getName());
                holder.userSchool.setText(friend.getPhone() + "");
            }
            else
            {
                holder.userName.setText(friend.getPhone() + "");
                holder.userSchool.setText(friend.getId() + "");
            }
            
            imageLoader.displayImage(friend.getAvatar(), holder.avatar);
            holder.userFriendsNum.setVisibility(View.INVISIBLE);
            
            return convertView;
        }
        
        /**
         * @return the type
         */
        public int getType()
        {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(int type)
        {
            this.type = type;
        }

        class ViewHolder
        {
            private CircularImage avatar;
            
            private TextView userName;
            
            private TextView userSchool;
            
            private TextView userFriendsNum;
            
            void init(View convertView)
            {
                avatar = (CircularImage) convertView.findViewById(R.id.avatar);
                userName = (TextView) convertView.findViewById(R.id.user_name);
                userSchool = (TextView) convertView.findViewById(R.id.user_school);
                userFriendsNum = (TextView) convertView.findViewById(R.id.user_friends_num);
            }
        }
        
       
        
    }
    
    
    class GroupAdapter extends BaseAdapter {

        private Context mContext;
        private List<ClassRoom> clazzs;
        private ImageLoader imageLoader_group;
       
        public GroupAdapter(Context context, ImageLoader imageLoader_group, List<ClassRoom> clazzs) {
            this.mContext = context;
            this.clazzs = clazzs;
            this.imageLoader_group = imageLoader_group;
        }
        
        public void addAll(List<ClassRoom> clazzs, boolean append) {
            if(this.clazzs != null) {
                if(!append) {
                    this.clazzs.clear();
                }
                this.clazzs.addAll(clazzs);
            }else {
                this.clazzs = clazzs;
            }
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return clazzs.size();
        }

        @Override
        public ClassRoom getItem(int position) {
            return clazzs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return clazzs.get(position).getId();
        }

        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent)
        {
            ViewHolder holder;
            final ClassRoom clazz = getItem(position);
            if (convertView == null || convertView.getTag() == null)
            {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.adapter_join_group_list_item,
                                parent,
                                false);
                //                convertView.setOnClickListener(new OnClickListener() {
                //                    
                //                    @Override
                //                    public void onClick(View arg0) {
                //                        // TODO Auto-generated method stub
                //                        Intent intent = new Intent(mContext,ClazzInfoActivity.class);
                //                        intent.putExtra("CLAZZ_ID", clazz.getId());
                //                        mContext.startActivity(intent);
                //                    }
                //                });
                holder = new ViewHolder();
                holder.init(convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.userName.setText(clazz.getId() + "");
            //holder.clazzName.setText(clazz.getClassroomName() + "(" + clazz.getLeaderName() + ")");
            holder.clazzName.setText(clazz.getName());
            holder.text_count.setText(clazz.getClassNumber() + "人");
            
            DisplayImageOptions defaultOptions_group = new DisplayImageOptions.Builder().cacheOnDisc()
                    .showStubImage(R.drawable.default_group)
                    .showImageForEmptyUri(R.drawable.default_group)
                    .showImageOnFail(R.drawable.default_group)
                    .build();
            
            imageLoader_group.displayImage(Consts.SERVER_HOST
                    + clazz.getAvatar(),
                    holder.avatar,
                    defaultOptions_group);
            System.out.println(clazz.getAvatar() + "url---------------");
            return convertView;
        }
        
        class ViewHolder {
            private CircularImage avatar;
            private TextView userName;
            private TextView clazzName;
            private TextView text_count;
            
            void init(View convertView) 
            {
                avatar = (CircularImage) convertView.findViewById(R.id.avatar);
                userName = (TextView) convertView.findViewById(R.id.user_name);
                text_count = (TextView)convertView.findViewById(R.id.text_count);
                clazzName =(TextView)  convertView.findViewById(R.id.text_desc);
                
            }
        }
        
    }

    
}
