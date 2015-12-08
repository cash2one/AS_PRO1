package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.ClassAttenSearchAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.data.DataChangedListener;
import com.linkage.mobile72.sh.data.StudentAtten;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshListView;

public class AttenSearchActivity extends BaseActivity implements
        OnClickListener
{
    public static final String STUDENT_SEARCH = "student_search";
    
    private static final String TAG = AttenSearchActivity.class.getSimpleName();
    
    private List<StudentAtten> mStuAttenList;
    
    private ClassAttenSearchAdapter mStuSearchAdapter;
    
    private PullToRefreshListView mListView;
    
    private TextView mEmpty;
    
    private String keyword;
    
    private EditText editInput;
    
    private Button searchBtn;
    
    private boolean isDataChanged = false;
    
    private boolean isConfirmed = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mb_manage_search_layout);
        setTitle("查找结果");
        findViewById(R.id.back).setOnClickListener(this);
        
        //        Intent intent = getIntent();
        //        
        //        if (null == intent)
        //        {
        //            LogUtils.e(TAG, "intent in null!!");
        //            
        //            finish();
        //        }
        //        else
        //        {
        //            mStuAttenList = (List<StudentAtten>) intent.getExtras().getSerializable(STUDENT_SEARCH);
        //            
        //            Log.d(TAG, "mStuAttenList ----->");
        //            dspData(mStuAttenList);
        //            
        //            initView();
        //        }
        
        Intent intent = getIntent();
        
        if (null == intent) {
        	
        	finish();
        	
        } else {
        	isConfirmed = intent.getBooleanExtra(AttenActivity.IS_CONFIRMED, false);
        	LogUtils.d("search, isConfirmed=" + isConfirmed);
        }
        
        initView();
    }
    
    private void dspData(List<StudentAtten> list)
    {
        Log.i(TAG, "dspData----->start size=" + list.size());
        for (int i = 0; i < list.size(); i++)
        {
            Log.i(TAG, "i=" + i + " stu:" + list.get(i).toString());
        }
        
        Log.i(TAG, "OrgSearch data -----> end");
    }
    
    private void initView()
    {
        editInput = (EditText) findViewById(R.id.search_input);
        searchBtn = (Button) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(this);
        
        mListView = (PullToRefreshListView) findViewById(R.id.base_pull_list);
        
        mStuAttenList = new ArrayList<StudentAtten>();
        
        DataChangedListener listener = new DataChangedListener()
        {
            @Override
            public void onDataChanged(int index)
            {
                isDataChanged = true;
                Log.d(TAG, "data has changed!! index=" + index + " states:"
                        + mStuAttenList.get(index).getState()
                        + " name:" + mStuAttenList.get(index).getName());
                
                if (index < mStuAttenList.size())
                {
                    updateAttenTask updateTsk = new updateAttenTask(mStuAttenList.get(index), false);
                    updateTsk.execute();
                }
                else
                {
                    LogUtils.e(TAG+":onDataChanged, invalid index:" + index
                            + " mStuAttenList size=" + mStuAttenList.size());
                }
                
            }
        };
        
        mStuSearchAdapter = new ClassAttenSearchAdapter(this, mStuAttenList,
                listener, isConfirmed);
        mListView.setAdapter(mStuSearchAdapter);
        
        mListView.setDivider(null);
        mEmpty = (TextView) findViewById(android.R.id.empty);
        mEmpty.setText("暂时没有数据");
        
        mListView.setMode(Mode.DISABLED);
    }
    
    
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case R.id.search_btn:
                fetchData();
                break;
            case R.id.back:
                finishSearch();
                break;
        }
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        Log.d(TAG, "onBackPressed!!!");
        //        super.onBackPressed();
        finishSearch();
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy!!!");
        super.onDestroy();
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause!!!");
        super.onPause();
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop()
    {
        Log.d(TAG, "onStop!!!");
        super.onStop();
    }
    
    private void finishSearch()
    {
        Intent mIntent = new Intent(AttenSearchActivity.this,
                AttenActivity.class);
        
        mIntent.putExtra(STUDENT_SEARCH, isDataChanged);
        setResult(RESULT_OK, mIntent);
        
        finish();
    }
    
    private void fetchData()
    {
        keyword = editInput.getText().toString();
        if (TextUtils.isEmpty(keyword))
        {
            Toast.makeText(this, "搜的内容不能为空的", Toast.LENGTH_SHORT).show();
            return;
        }
        // 从db查找
        DataHelper helper = getDBHelper();
        DataHelper.getHelper(this);
        try
        {
            QueryBuilder<StudentAtten, Integer> contactBuilder = helper.getStudentAttenData()
                    .queryBuilder();
            contactBuilder.where().like("name", "%" + keyword + "%");
            
            List<StudentAtten> mContacts = contactBuilder.query();
            
            mStuAttenList.clear();
            mStuAttenList.addAll(mContacts);
            
            dspData(mStuAttenList);
            
            if (mStuSearchAdapter.isEmpty())
            {
                mEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(AttenSearchActivity.this,
                        R.string.no_search_data,
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                mEmpty.setVisibility(View.GONE);
            }
            
            mStuSearchAdapter.notifyDataSetChanged();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
    }
    
    class updateAttenTask extends AsyncTask<Void, Void, Boolean>
    {
        
        private boolean flag;
        
        private StudentAtten stuAtten;
        
        updateAttenTask(StudentAtten atten, boolean f_flag)
        {
            stuAtten = atten;
            this.flag = f_flag;
        }
        
        @Override
        protected Boolean doInBackground(Void... params)
        {
            DataHelper helper = getDBHelper();
            DataHelper.getHelper(AttenSearchActivity.this);
            try
            {
                Dao<StudentAtten, Integer> stDao = helper.getStudentAttenData();
                if (stuAtten != null)
                {
                    //更新某记录项  
                    stDao.update(stuAtten);
                }
                else
                {
                    Log.d(TAG, "updateAttenTask, stuAtten is null!!!");
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return flag;
        }
        
        @Override
        protected void onPostExecute(Boolean result)
        {
            if (result)
            {
                
            }
        }
    }
}
