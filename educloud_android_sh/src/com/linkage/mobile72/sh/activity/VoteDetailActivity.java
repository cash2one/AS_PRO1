package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.VoteDetailAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.data.http.JXBeanDetail.JXVote;
import com.linkage.mobile72.sh.data.http.JXBeanDetail.JXVotePerson;
import com.linkage.ui.widget.PullToRefreshListView;

public class VoteDetailActivity extends BaseActivity implements OnClickListener {
	
	protected static final String EXTRAS = "vote";
	private JXVote vote;
	private ArrayList<JXVotePerson> userList;
	private TextView semiTitle;
	private PullToRefreshListView listView;
	private VoteDetailAdapter mAdapter;
	private TextView mEmpty;
	private Button back, set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote_detail_activity);
        setTitle("投票详情");
        semiTitle = (TextView)findViewById(R.id.semi_title);
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        set = (Button)findViewById(R.id.set);
        set.setVisibility(View.INVISIBLE);
        
        vote = (JXVote)getIntent().getSerializableExtra(EXTRAS);
        semiTitle.setText(vote.getVoteContent());
        userList = vote.getUserList();
        listView = (PullToRefreshListView) findViewById(R.id.base_pull_list);
        mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("此处空空~");
        userList = vote.getUserList();
        mAdapter = new VoteDetailAdapter(this, imageLoader, userList);
        if (mAdapter.isEmpty()) {
			mEmpty.setVisibility(View.VISIBLE);
		} else {
			mEmpty.setVisibility(View.GONE);
		}
        listView.setDivider(null);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
    
}
