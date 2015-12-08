package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Subject;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshListView;

public class SelectSubjectActivity extends BaseActivity implements OnClickListener {
	public static final String TAG = "SelectSubjectActivity";
	public static final String SUBJECT_RESULT = "subject_result";
	private ArrayList<Subject> mData;
	private PullToRefreshListView subjectListView;
	private MyAdapter mAdapter;

	private View mProgress;
	private Boolean needGet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_simple_listview);

		mData = (ArrayList<Subject>) getIntent().getExtras().getSerializable(SUBJECT_RESULT);
		if (mData == null) {
			mData = new ArrayList<Subject>();
			needGet = true;
		} else {
			for (Subject subject : mData) {
				subject.setChecked(false);
			}
		}
		initView();

		if (needGet) {
			fetchData();
		}
	}

	private void initView() {
		setTitle("选择科目");
		findViewById(R.id.back).setOnClickListener(this);

		mProgress = findViewById(R.id.progress_container);
		subjectListView = (PullToRefreshListView) findViewById(R.id.base_pull_list);
		subjectListView.setMode(Mode.DISABLED);
		mAdapter = new MyAdapter();
		subjectListView.getRefreshableView().setAdapter(mAdapter);
		subjectListView.setDivider(null);

		subjectListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent mIntent = new Intent(SelectSubjectActivity.this,
						CreateHomeworkActivity.class);
				mData.get(position - 1).setChecked(true);
				//T.showShort(SelectSubjectActivity.this, mData.get(position - 1).getName());
				mIntent.putExtra(SUBJECT_RESULT, mData);
				setResult(RESULT_OK, mIntent);
				finish();
			}

		});
	}

	private void fetchData() {
		mProgress.setVisibility(View.VISIBLE);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getSubjectList");

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getSubjectList,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						mProgress.setVisibility(View.GONE);
						LogUtils.i(TAG + ":response=" + response);
						if (response.optInt("ret") == 0) {
							mData.clear();
							JSONArray array = response.optJSONArray("data");
							if(array != null && array.length() > 0) {
								try {
									DeleteBuilder<Subject, Integer> deleteSubjectBuilder = getDBHelper().getSubjectDao().deleteBuilder();
									deleteSubjectBuilder.delete();
								} catch (SQLException e) {
									e.printStackTrace();
								}
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.optJSONObject(i);
									Subject subject = new Subject();
									subject.setId(obj.optLong("id"));
									subject.setName(obj.optString("name"));
									mData.add(subject);
									try {
										getDBHelper().getSubjectDao().create(subject);
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
							}
						} else {
							T.showShort(SelectSubjectActivity.this, response.optString("msg"));
						}
						mAdapter.notifyDataSetChanged();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, SelectSubjectActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	class MyAdapter extends BaseAdapter {

		public MyAdapter() {

		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public Object getItem(int arg0) {
			return mData.get(arg0);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				view = getLayoutInflater().inflate(R.layout.item_list_single_text, null);
				viewHolder.textView = (TextView) view.findViewById(R.id.list_textshow);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.textView.setText(mData.get(position).getName());
			return view;
		}
	}

	class ViewHolder {
		public TextView textView;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}