package com.linkage.mobile72.sh.activity.sports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.LoginActivity;
import com.linkage.mobile72.sh.activity.MyPaymentActivity;
import com.linkage.mobile72.sh.adapter.RankListAdapter;
import com.linkage.mobile72.sh.adapter.TestListAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.ProjectData;
import com.linkage.mobile72.sh.data.RankNumber;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.view.BarChartPanel;
import com.linkage.mobile72.sh.view.DataElement;
import com.linkage.mobile72.sh.view.DataSeries;
import com.linkage.mobile72.sh.view.OnChartItemClickListener;
import com.linkage.mobile72.sh.view.PinnedSectionListView;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshListView;

public class SportsActivity1 extends BaseActivity implements OnClickListener{
	
	private static final String TAG = SportsActivity1.class.getSimpleName();
	
	private TextView dailyText, standardText,showText;
	private TextView distandceRunText, pathPatternText, rankingListText, sportsLogText,
		allTestText, singleTestText, testStandardText,sportsLogText2;
	private PullToRefreshListView listView;
	private String[] strs;
	private String[] strs1;
	private LinearLayout header2Layout;
	
	private BarChartPanel chart;
	private String [] nameArray={"周一","周二","周三","周四","周五","周六","周日"};
	private int[] countArray = {1000,2000,3000 , 4000, 5000, 10000, 7000};
	private List<DataElement> dataElementArray=new ArrayList<DataElement>();
	
	private ArrayAdapter<String> mAdapter;
	private MyCommonDialog myCommonDialog;
	private String[]  projectNames;
	private ArrayList<ProjectData> projectLists = new ArrayList<ProjectData>();
	
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			startActivity(SingleTestActivity.getIntent(SportsActivity1.this, projectLists.get(position)));
			myCommonDialog.dismiss();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sports1);
		strs = new String[50];
		strs1 = new String[20];

		for (int i = 0; i < 50; i++) {
			strs[i] = "data-----" + i;
		}
		for (int i = 0; i < 20; i++) {
			strs1[i] = "datas-----" + i;
		}
		
		setTitle(R.string.sports_title);
		findViewById(R.id.back).setOnClickListener(this);
		init();
		
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,strs));
		
		chart = (BarChartPanel)findViewById(R.id.chart);
		DataSeries series = new DataSeries();
		for (int i = 0; i < countArray.length; i++) {
			dataElementArray.add(setDataElement(countArray[i]));
		}
		
		series.addSeries(nameArray, dataElementArray);
		chart.setSeries(series);
		chart.setOnChartItemClickListener(new MainActivityOnChartItemClickListener());
		
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
		projectNames =  new String[projectLists.size()];
		
		for (int i = 0; i < projectLists.size(); i++) {
			projectNames[i] = projectLists.get(i).getName();
		}
	}
	
	private void init(){
		listView = (PullToRefreshListView)findViewById(R.id.pull_list_view);
//		listView.addHeaderView(header);
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);  
//		View header = getLayoutInflater().inflate(R.layout.invite_record_header, mInviteRecordLv, false);  
		View header = View.inflate(this, R.layout.sports_header, null);
		header.setLayoutParams(layoutParams);  
		ListView lv = listView.getRefreshableView();  
		lv.addHeaderView(header);  
//		listView.setAdapter(new InviteRecordAdapter(this));  
		
		dailyText = (TextView)header.findViewById(R.id.text_daily_exercise);
		standardText = (TextView)header.findViewById(R.id.text_standard_test);
		distandceRunText = (TextView)header.findViewById(R.id.text_distance_run);
		pathPatternText = (TextView)header.findViewById(R.id.text_path_pattern);
		rankingListText = (TextView)header.findViewById(R.id.text_ranking_list);
		sportsLogText = (TextView)header.findViewById(R.id.text_sports_log);
		allTestText = (TextView)header.findViewById(R.id.text_all_test);
		singleTestText = (TextView)header.findViewById(R.id.text_single_test);
		testStandardText = (TextView)header.findViewById(R.id.text_test_standard);
		sportsLogText2 = (TextView)header.findViewById(R.id.text_sports_log2);
		showText = (TextView)header.findViewById(R.id.text_click_option);
		
		dailyText.setTextColor(getResources().getColor(R.color.sports_green));
		sportsLogText.setTextColor(getResources().getColor(R.color.sports_green));
		sportsLogText2.setTextColor(getResources().getColor(R.color.sports_green));
		showText.setText(sportsLogText.getText());
		dailyText.setOnClickListener(this);
		standardText.setOnClickListener(this);
		distandceRunText.setOnClickListener(this);
		pathPatternText.setOnClickListener(this);
		rankingListText.setOnClickListener(this);
		sportsLogText.setOnClickListener(this);
		allTestText.setOnClickListener(this);
		singleTestText.setOnClickListener(this);
		testStandardText.setOnClickListener(this);
		sportsLogText2.setOnClickListener(this);
	}
	
	private DataElement setDataElement(int count){
		return new DataElement(count, getResources().getColor(R.color.orange));
	}
	
	private class MainActivityOnChartItemClickListener implements OnChartItemClickListener{
		@Override
		public void onItemClick(int position) {
			Toast.makeText(SportsActivity1.this, countArray[position]+"米", Toast.LENGTH_LONG).show();
		}
	}
	
	private void getRankList(){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "getCharts");
			
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
					Consts.SERVER_URL_NEW, Request.Method.POST, params, true,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
//							System.out.println("response=" + response);
							LogUtils.e("response=" + response);
							if (response.optInt("ret") == 0) {
								ArrayList<RankNumber> numbers = RankNumber.parseFromJson(response.optJSONArray("data"));
								listView.setAdapter(new RankListAdapter(SportsActivity1.this, numbers));
							}
						}
					},new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							StatusUtils.handleError(error, SportsActivity1.this);
						}
					}
			);
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		} catch (Exception e) {
			e.printStackTrace();
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
								if (projects != null && projects.size() > 0) {
									for (int i = 0; i < projects.size(); i++) {
										projectNames[i] = projects.get(i).getName();
									}
								}
							}
						}
					},new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							StatusUtils.handleError(error, SportsActivity1.this);
						}
					}
			);
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.text_daily_exercise:
			dailyText.setTextColor(getResources().getColor(R.color.sports_green));
			standardText.setTextColor(getResources().getColor(R.color.black));
			distandceRunText.setVisibility(View.VISIBLE);
			pathPatternText.setVisibility(View.VISIBLE);
			rankingListText.setVisibility(View.VISIBLE);
			sportsLogText.setVisibility(View.VISIBLE);
			allTestText.setVisibility(View.GONE);
			singleTestText.setVisibility(View.GONE);
			testStandardText.setVisibility(View.GONE);
			sportsLogText2.setVisibility(View.GONE);
			showText.setText(sportsLogText.getText());
			break;
		case R.id.text_standard_test:
			dailyText.setTextColor(getResources().getColor(R.color.black));
			standardText.setTextColor(getResources().getColor(R.color.sports_green));
			distandceRunText.setVisibility(View.GONE);
			pathPatternText.setVisibility(View.GONE);
			rankingListText.setVisibility(View.GONE);
			sportsLogText.setVisibility(View.GONE);
			allTestText.setVisibility(View.VISIBLE);
			singleTestText.setVisibility(View.VISIBLE);
			testStandardText.setVisibility(View.VISIBLE);
			sportsLogText2.setVisibility(View.VISIBLE);
			showText.setText(sportsLogText2.getText());
			break;
		case R.id.text_distance_run:
			startActivity(new Intent(SportsActivity1.this, MapPatternActivity.class));
			break;
		case R.id.text_path_pattern:
			startActivity(new Intent(SportsActivity1.this, PathPatternActivity.class));
			break;
		case R.id.text_ranking_list:
			sportsLogText.setTextColor(getResources().getColor(R.color.white_gray));
			rankingListText.setTextColor(getResources().getColor(R.color.sports_green));
			showText.setText(rankingListText.getText());
			getRankList();
			break;
		case R.id.text_sports_log:
			sportsLogText.setTextColor(getResources().getColor(R.color.sports_green));
			rankingListText.setTextColor(getResources().getColor(R.color.white_gray));
			showText.setText(sportsLogText.getText());
			listView.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1,strs));
			break;
		case R.id.text_all_test:
			startActivity(new Intent(SportsActivity1.this, AllTestActivity.class));
			break;
		case R.id.text_single_test:
//			getSportsList();
			mAdapter = new ArrayAdapter<String>(this,
					R.layout.distance_run_typre_item, R.id.type_text,projectNames);
			myCommonDialog = new MyCommonDialog(SportsActivity1.this, "选择您运动的科目",mAdapter, mItemClickListener, null, "取消");
			myCommonDialog.setOkListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					myCommonDialog.dismiss();
				}
			});
			myCommonDialog.show();
			break;
		case R.id.text_test_standard:
			startActivity(new Intent(SportsActivity1.this, TestStandradActivity.class));
			break;
		case R.id.text_sports_log2:
			sportsLogText2.setTextColor(getResources().getColor(R.color.sports_green));
			showText.setText(sportsLogText2.getText());
			break;
		default:
			break;
		}
	}
}
