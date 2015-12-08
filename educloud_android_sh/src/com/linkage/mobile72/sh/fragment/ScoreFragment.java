package com.linkage.mobile72.sh.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.activity.ScoreActivity;
import com.linkage.mobile72.sh.adapter.ScoreListAdapter;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.AccountChild;
import com.linkage.mobile72.sh.data.Score;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.widget.LineChartView;
import com.linkage.lib.util.LogUtils;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener2;
import com.linkage.ui.widget.PullToRefreshListView;

public class ScoreFragment extends BaseFragment {

	private final String TAG = ScoreFragment.this.getClass().getSimpleName();
	
	private static final int TYPE_DAY = 0;
	private static final int TYPE_PHASE = 1;

	private static final int SHOW_TYPE_EMPTY = 1;
	private static final int SHOW_TYPE_DATA = 2;

	private long subjectId = -1;
	private String subjectName = "";
	private int type;
	private LineChartView lineChartRt;

	private TextView tvPer, tvDaily, tvPhase, tvTestName, tvUp, tvScore, tvSum, tvDate, tvEmpty;
	private RelativeLayout rlyPer, rlyLine, rlySum;
	private LinearLayout rlyType, lyEmpty;
	private ImageView imgEmpty;
	private Context mContext;

	private AccountChild dftChild;

	private ProgressDialog mProgressDialog;

	private List<Score> scoreInfoList = new ArrayList<Score>();
	private List<String> dateList = new ArrayList<String>();
	private List<Double> scoreList = new ArrayList<Double>();

	private boolean firstPage = true;

	private int screenWidth = 0;

	private PullToRefreshListView list_newapp;
	private ScoreListAdapter scoreListAdapter;
	private TextView mEmpty; View header;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initData();
	}

	private void initData() {

		Bundle bdl = getArguments();
		if (null != bdl) {
			subjectId = bdl.getLong(ScoreActivity.SUBJECT_ID, -1);
			subjectName = bdl.getString(ScoreActivity.SUBJECT_NAME);
			
			LogUtils.e(" subjectId = " + subjectId);
		} else {
			LogUtils.e(" bdl is null");
			subjectId = -1;
			subjectName = "";
		}

		LogUtils.d(" subjectId=" + subjectId + " subjectName=" + subjectName);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_score, null);
		mContext = ScoreFragment.this.getActivity();
		rlyType = (LinearLayout) view.findViewById(R.id.rlyType);
		rlyPer = (RelativeLayout) view.findViewById(R.id.rlyPer);
		lyEmpty = (LinearLayout) view.findViewById(R.id.lyEmpty);
		imgEmpty = (ImageView) view.findViewById(R.id.imgEmpty);
		tvPer = (TextView) view.findViewById(R.id.tvPer);
		tvDaily = (TextView) view.findViewById(R.id.tvDaily);
		tvPhase = (TextView) view.findViewById(R.id.tvPhase);
		tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);
		list_newapp = (PullToRefreshListView) view.findViewById(R.id.base_pull_list2);
		mEmpty = (TextView) view.findViewById(R.id.empty);
		mEmpty.setText("查无数据");
		scoreListAdapter = new ScoreListAdapter(getActivity(), scoreInfoList, list_newapp);
		list_newapp.getRefreshableView().setVerticalScrollBarEnabled(false);
		list_newapp.setDivider(null);
		list_newapp.setAdapter(scoreListAdapter);
		list_newapp.setMode(Mode.BOTH);

		list_newapp.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				firstPage = true;
				fetchScoreData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				firstPage = false;
				fetchScoreData();
			}
		});
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		
		mProgressDialog = new ProgressDialog(getActivity());
		
		dftChild = getDefaultAccountChild();
		type = TYPE_DAY;
		if (0 > subjectId || null == dftChild) {
			showDataInfo(SHOW_TYPE_EMPTY);
		} else {
			list_newapp.setRefreshing(true);
//			fetchScoreData();
		}
		
		tvDaily.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				firstPage = true;
				type = TYPE_DAY;
				tvDaily.setTextColor(getResources().getColor(
						R.color.intr_page_bg_parent));
				tvPhase.setTextColor(getResources().getColor(
						R.color.sc_light_gray));
				scoreInfoList.clear();
				updateView(scoreInfoList);
				fetchScoreData();
			}
		});
		
		tvPhase.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				firstPage = true;
				type = TYPE_PHASE;
				tvDaily.setTextColor(getResources().getColor(
						R.color.sc_light_gray));
				tvPhase.setTextColor(getResources().getColor(
						R.color.intr_page_bg_parent));
				scoreInfoList.clear();
				updateView(scoreInfoList);
				fetchScoreData();
			}
		});
		
		/*lineChartRt.setListener(new LineClickListener() {
		
			@Override
			public void OnClick(float x, float y, int pos) {
				Log.d("LineTestActivity", "x=" + x + " y=" + y + " pos=" + pos);
		
				if (0 <= pos && pos < scoreInfoList.size()) {
					updateCommonInfo(scoreInfoList.get(pos));
				} else {
					LogUtils.d("invalid pos, scoreInfoList size="
							+ scoreInfoList.size());
				}
			}
		});*/
		tvDaily.performClick();
		return view;
	}

	private void showDataInfo(int type) {

		switch (type) {

		case SHOW_TYPE_EMPTY:

			lyEmpty.setVisibility(View.VISIBLE);
			imgEmpty.setVisibility(View.VISIBLE);
			tvEmpty.setText(R.string.sc_empty);
			tvEmpty.setVisibility(View.VISIBLE);

			/*rlyPer.setVisibility(View.GONE);
			rlyLine.setVisibility(View.GONE);
			rlySum.setVisibility(View.GONE);*/

			break;

		case SHOW_TYPE_DATA:
			lyEmpty.setVisibility(View.GONE);

			/*rlyPer.setVisibility(View.VISIBLE);
			rlyLine.setVisibility(View.VISIBLE);
			rlySum.setVisibility(View.VISIBLE);*/
			break;

		default:
			LogUtils.e("invalid type:" + type);
			break;

		}
	}

	private void fetchScoreData() {

		mProgressDialog.setMessage(mContext.getResources().getString(
				R.string.fetch_data));
		if (firstPage) {
			mProgressDialog.show();
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getChildResults");
		params.put("studentid", dftChild.getId() + "");
		if(!firstPage){
			params.put("id", scoreInfoList.get(scoreInfoList.size() - 1).getId());
		}else {
			params.put("id", "0");
		}
		params.put("subjectid", subjectId + "");
		params.put("page", Consts.PAGE_SIZE);
		params.put("type", type + "");

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_getChildResults, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						list_newapp.onRefreshComplete();
						if(mProgressDialog != null && mProgressDialog.isShowing())
						mProgressDialog.dismiss();
						LogUtils.i(" response=" + response);
						if (response.optInt("ret") == 0) {
							String perStr = response.optString("examString");
							if(!TextUtils.isEmpty(perStr) && firstPage)
							tvPer.setText("您孩子的" + subjectName + "成绩超过了" + perStr + "%的同学");
							JSONArray array = response.optJSONArray("examlist");
							List<Score> list = null;
							try {
								list = Score.parseFromJson(array);
							} catch (JSONException e) {
								LogUtils.e("parse scoreinfo err:" + e.getMessage());
							}
							if(list == null || list.size() < Integer.parseInt(Consts.PAGE_SIZE)) {
								list_newapp.setMode(Mode.PULL_FROM_START);
							}else {
								list_newapp.setMode(Mode.BOTH);
							}
							if (null == list || 0 == list.size()) {
								if(firstPage)
								showDataInfo(SHOW_TYPE_EMPTY);
							} else {
								lyEmpty.setVisibility(View.GONE);
								//scoreInfoList.clear();
								//scoreInfoList.addAll(list);
								updateView(list);
							}
						} else {
							T.showShort(mContext, response.optString("msg"));
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						list_newapp.onRefreshComplete();
						if(mProgressDialog != null && mProgressDialog.isShowing())
						mProgressDialog.dismiss();
						StatusUtils.handleError(arg0, mContext);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	private void updateView(List<Score> list) {
		
		showDataInfo(SHOW_TYPE_DATA);

		if(firstPage && header != null) {
			list_newapp.getRefreshableView().removeHeaderView(header);
		}
		if(firstPage && type == TYPE_DAY && list != null && list.size() > 0) {
			header = LayoutInflater.from(getActivity()).inflate(R.layout.header_score_chart, null);
			//HorizontalScrollView horiView = (HorizontalScrollView) header.findViewById(R.id.horizontalScrollView1);
			lineChartRt = (LineChartView) header.findViewById(R.id.lineChartRt);
			rlyLine = (RelativeLayout) header.findViewById(R.id.rlyLine);
			
			int size = list.size();
			
			updateLineChartW(header, size);
			
			dateList.clear();
			scoreList.clear();
			// make line data
			for (int i = 0; i < size; i++) {
				Score score = list.get(i);
				String sDate = score.getDate();
				sDate = sDate.substring(5, sDate.length());
				sDate = sDate.replace("月", "-").replace("日", "");
				dateList.add(sDate);
				scoreList.add(Double.valueOf(score.getPersent()));
			}
			
			lineChartRt.setXAxisData(dateList);
			lineChartRt.updateLineChart(scoreList);
	
		}
		scoreListAdapter.addAll(list, !firstPage);
	}

	private void updateLineChartW(View header, int len) {
		int mywid = 90 * (len - 1);

		if (mywid + 102 <= screenWidth) {
			LayoutParams lParams = (LayoutParams) lineChartRt.getLayoutParams();
			lParams.width = screenWidth - 62;
			// lineChartRt.setLineChartWidth(mywid);
			lineChartRt.setLayoutParams(lParams);
			rlyLine.invalidate();

		} else {

			LayoutParams lParams = (LayoutParams) lineChartRt.getLayoutParams();
			lParams.width = mywid + 180;
			// lineChartRt.setLineChartWidth(mywid);
			lineChartRt.setLayoutParams(lParams);
			rlyLine.invalidate();
		}
		list_newapp.getRefreshableView().addHeaderView(header);
		//LogUtils.d("mywid:" + mywid);
	}
}
