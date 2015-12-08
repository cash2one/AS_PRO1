package com.linkage.mobile72.sh.activity.sports;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.RankListAdapter;
import com.linkage.mobile72.sh.adapter.TestListAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.ProjectData;
import com.linkage.mobile72.sh.data.RankNumber;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

public class AllTestActivity extends BaseActivity implements OnClickListener{

	private static final String TAG = AllTestActivity.class.getSimpleName();
	
	private TestListAdapter mAdapter;
	private ListView listView;
	private String[]  projects = {"800米","实心球","立定跳远","跳远"};
	private ArrayList<ProjectData> projectLists = new ArrayList<ProjectData>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_test);
		
		setTitle(R.string.sports_title);
		findViewById(R.id.back).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.test_listview);
		
		ProjectData project1 = new ProjectData();
		project1.setName("800米");
		project1.setId(1);
		project1.setScale("25%");
		project1.setType(1);
		project1.setUnit(null);
		
		ProjectData project2 = new ProjectData();
		project2.setName("跳绳");
		project2.setId(4);
		project2.setScale("25%");
		project2.setType(2);
		project2.setUnit("个");
		
		ProjectData project3 = new ProjectData();
		project3.setName("立定跳远");
		project3.setId(3);
		project3.setScale("25%");
		project3.setType(3);
		project3.setUnit("米");
		
		ProjectData project4 = new ProjectData();
		project4.setName("铅球");
		project4.setId(2);
		project4.setScale("25%");
		project4.setType(3);
		project4.setUnit("米");
		
		projectLists.add(project1);
		projectLists.add(project2);
		projectLists.add(project3);
		projectLists.add(project4);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mAdapter = new TestListAdapter(AllTestActivity.this, projectLists);
		listView.setAdapter(mAdapter);
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
	
	private void getSportsList(){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "getSportItemList");
//			params.put("gradeid", value);
			
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
					Consts.SERVER_URL_NEW, Request.Method.POST, params, true,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							LogUtils.e("response=" + response);
							if (response.optInt("ret") == 0) {
								ArrayList<ProjectData> projects = ProjectData.parseFromJson(response.optJSONArray("data"));
								listView.setAdapter(new TestListAdapter(AllTestActivity.this, projects));
							}
						}
					},new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							StatusUtils.handleError(error, AllTestActivity.this);
						}
					}
			);
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
