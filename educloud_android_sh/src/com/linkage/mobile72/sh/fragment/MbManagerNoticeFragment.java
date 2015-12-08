package com.linkage.mobile72.sh.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.JxMbDetailActivity;
import com.linkage.mobile72.sh.activity.JxMbManagerListActivity;
import com.linkage.mobile72.sh.activity.SearchMbResultActivity;
import com.linkage.mobile72.sh.adapter.MbManagerListAdapter;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.http.JxTemplate;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener2;
import com.linkage.ui.widget.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MbManagerNoticeFragment extends BaseFragment {
	private static final String TAG = "MbManagerNoticeFragment";
	private static final int REQUEST_REFRESH = 1;

	private List<JxTemplate> mList = new ArrayList<JxTemplate>();
	private MbManagerListAdapter mAdapter;
	private PullToRefreshListView mListView;
	private TextView mEmpty;
	private int curPageIndex = 1;
	private int type;
	private String mAction;

	public static MbManagerNoticeFragment newInstance(int type, String action) {
		MbManagerNoticeFragment fragment = new MbManagerNoticeFragment();
		Bundle args = new Bundle();
		args.putInt("type", type);
		args.putString("action", action);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = getArguments().getInt("type", Consts.JxhdType.HOMEWORK);
		mAction = getArguments().getString("action");
		mAdapter = new MbManagerListAdapter(getActivity(), mList);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_mb_manager_notice, container, false);
		RelativeLayout searchLayout = (RelativeLayout) rootView.findViewById(R.id.search_btn);
		searchLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), SearchMbResultActivity.class);
                i.putExtra("type", type);
//				startActivity(i);
				startActivityForResult(i, REQUEST_REFRESH);
			}
		});
		mListView = (PullToRefreshListView) rootView.findViewById(R.id.base_pull_list);
		mListView.setAdapter(mAdapter);
		mListView.setDivider(null);
		mEmpty = (TextView) rootView.findViewById(android.R.id.empty);
		mEmpty.setText("暂时没有数据");
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!TextUtils.isEmpty(mAction)) {
					Intent it = new Intent();
					JxTemplate jxtemplate = mList.get(position);
					Bundle b = new Bundle();
					b.putSerializable(JxMbManagerListActivity.KEY_CONTENT, jxtemplate.getText());
					it.putExtras(b);
					getActivity().setResult(Activity.RESULT_OK, it);
					getActivity().finish();
				} else {
					Intent intentMbManager = new Intent(getActivity(), JxMbDetailActivity.class);
					JxTemplate jxtemplate = mList.get(position);
					Bundle bundle = new Bundle();
					bundle.putSerializable("TEMPLATE", jxtemplate);
					intentMbManager.putExtras(bundle);
					startActivityForResult(intentMbManager, REQUEST_REFRESH);
				}
			}
		});
		mListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				fetchData(1);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				fetchData(curPageIndex + 1);
			}
		});
		fetchData(1);
		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_REFRESH:
			fetchData(1);
			break;
		}
	}

	private void fetchData(final int targetPage) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "userTemplateList");
		params.put("type", String.valueOf(type));
		params.put("page", String.valueOf(targetPage));
		params.put("pageSize", Consts.PAGE_SIZE);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						mListView.onRefreshComplete();
						if (targetPage == 1) {
							mList.clear();
						}
						if (response.optInt("ret") == 0) {
							curPageIndex = targetPage;
							List<JxTemplate> list = JxTemplate.parseFromJson(response
									.optJSONArray("data"));
							mList.addAll(list);
						}
						mAdapter.notifyDataSetChanged();
                        if (mAdapter.isEmpty()) {
                            mEmpty.setVisibility(View.VISIBLE);
                        } else {
                            mEmpty.setVisibility(View.GONE);
                        }
						/*else {
							StatusUtils.handleStatus(response, getActivity());
						}*/
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						mListView.onRefreshComplete();
						StatusUtils.handleError(arg0, getActivity());
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}