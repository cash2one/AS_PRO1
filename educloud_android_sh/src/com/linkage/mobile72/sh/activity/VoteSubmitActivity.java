package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.VoteListParentAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.JXBeanDetail.JXVote;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshListView;

public class VoteSubmitActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = VoteSubmitActivity.class.getName();
	
	protected static final String MSG_ID = "messageid";
	protected static final String EXTRAS = "vote";
	private Button back, commit;
	private long messageId;
	private ArrayList<JXVote> votes;
	private PullToRefreshListView listView;
	private VoteListParentAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent == null) {
			finish();
			return;
		}
		messageId = intent.getLongExtra(MSG_ID, 0);
		votes = (ArrayList<JXVote>)intent.getSerializableExtra("vote");
		if(votes == null) {
			finish();
			return;
		}
		setContentView(R.layout.parent_activity_vote_submit);
		setTitle("投票");
		back = (Button)findViewById(R.id.back);
		commit = (Button)findViewById(R.id.commit);
		back.setOnClickListener(this);
		commit.setOnClickListener(this);
		listView = (PullToRefreshListView)findViewById(R.id.base_pull_list);
		/*mAdapter = new ClassMemberListAdapter(this, imageLoader, clazzMembers, changerTID, String.valueOf(clazz.getClassroomId()));
		listView.setAdapter(mAdapter);
		listView.setDivider(getResources().getDrawable(R.color.dark_gray));
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("查无数据");*/
		
		 // 设为单选，允许列表项切换checked/unchecked状态  
		listView.getRefreshableView().setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
        // 列表的选择效果设为透明，由列表项自行维护各状态显示  
		listView.getRefreshableView().setSelector(android.R.color.transparent);  
		listView.setDivider(getResources().getDrawable(R.color.dark_gray));
		mAdapter = new VoteListParentAdapter(this, votes);
		listView.setAdapter(mAdapter);
	}
	
    
    private void commitVote() {
    	boolean choose = false; String voteOption = "";
    	HashMap<String, Boolean> states = mAdapter.states;
    	Set<Map.Entry<String, Boolean>> set = states.entrySet();
        for (Iterator<Map.Entry<String, Boolean>> it = set.iterator(); it.hasNext();) {
            Map.Entry<String, Boolean> entry = (Map.Entry<String, Boolean>) it.next();
            if(entry.getValue()) {
            	choose = true;
            	voteOption = entry.getKey();
            	break;
            }
        }
        if(!choose) {
        	UIUtilities.showToast(this, "投票需要选择一个投票选项");
        	return;
        }
        ProgressDialogUtils.showProgressDialog("", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "sendVote");
		params.put("studentid", getDefaultAccountChild().getId()+"");
		params.put("id", ""+messageId);
		params.put("option", voteOption);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_sendVote,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
		    				Intent i = new Intent(VoteSubmitActivity.this, VoteSubmitResultActivity.class);
		    				startActivity(i);
		    				finish();
						}else {
							String msg = response.optString("msg");
							if(TextUtils.isEmpty(msg)) {
								UIUtilities.showToast(VoteSubmitActivity.this, "投票失败了，请重新投票");
							}else {
								UIUtilities.showToast(VoteSubmitActivity.this, response.optString("msg"));
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.commit:
			commitVote();
			break;
		}
	}
	
}
