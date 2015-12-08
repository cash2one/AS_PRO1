package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshListView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.HorizontalDoubleListAdapter;
import com.linkage.mobile72.sh.adapter.HorizontalDoubleListSubAdapter;
import com.linkage.mobile72.sh.adapter.JoinGroupAdapter;
import com.linkage.mobile72.sh.adapter.SearchSchoolListAdapter;
import com.linkage.mobile72.sh.adapter.JoinGroupAdapter.NotifiHandler;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Region;
import com.linkage.mobile72.sh.data.http.SearchSchool;
import com.linkage.mobile72.sh.datasource.AssetsDatabaseManager;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.HorizontalDoubleListView;
import com.linkage.mobile72.sh.Consts;

public class SelectSchoolActivity extends BaseActivity implements OnClickListener, NotifiHandler {

	private static final String TAG = SelectSchoolActivity.class.getSimpleName();
	private Button back, set;
	private RelativeLayout search_btn;
	private TextView city, area; int regionId;
	private HorizontalDoubleListView cityListView, areaListView;
	private List<Region> cities;
	private HorizontalDoubleListAdapter cityListAdapter;
	private HorizontalDoubleListSubAdapter areaListAdapter;
	private LinearLayout chooseAreaLayout, areaListViewLayout, schoolResultLayout;
	private PullToRefreshListView schoolListView;
	private SearchSchoolListAdapter mFriendAdapter;
	private EditText edit_input;
	private List<SearchSchool> friends;
	private TextView mEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_school);
		back = (Button) findViewById(R.id.back);
		set = (Button) findViewById(R.id.set);
		setTitle(R.string.title_select_school);
		edit_input = (EditText) findViewById(R.id.search_edit);
		search_btn = (RelativeLayout) findViewById(R.id.search_btn);
		chooseAreaLayout = (LinearLayout) findViewById(R.id.linearlayout2);
		areaListViewLayout = (LinearLayout) findViewById(R.id.linearlayout4);
		schoolResultLayout = (LinearLayout) findViewById(R.id.linearlayout3);
		city = (TextView) findViewById(R.id.city);
		area = (TextView) findViewById(R.id.area);
		cityListView = (HorizontalDoubleListView) findViewById(R.id.listView);
		areaListView = (HorizontalDoubleListView) findViewById(R.id.subListView);
		schoolListView = (PullToRefreshListView) findViewById(R.id.base_pull_list);

		initCityArea();
		set.setVisibility(View.VISIBLE);
		chooseAreaLayout.setVisibility(View.VISIBLE);
		schoolResultLayout.setVisibility(View.VISIBLE);
		set.setText("南京");
		friends = new ArrayList<SearchSchool>();
		mFriendAdapter = new SearchSchoolListAdapter(this, friends);
		schoolListView.setAdapter(mFriendAdapter);
		schoolListView.setDivider(getResources().getDrawable(R.color.dark_gray));
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("查无数据");
		schoolListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchData(false);
			}
		});
		schoolListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent it = new Intent();
				SearchSchool school = mFriendAdapter.getItem(position - 1);
                it.putExtra("SCHOOL_ID", school.getSchoolId());
                it.putExtra("SCHOOL_NAME", school.getSchoolName());
                setResult(Activity.RESULT_OK, it);  
                finish();  
			}
		});
		back.setOnClickListener(this);
		search_btn.setOnClickListener(this);
		chooseAreaLayout.setOnClickListener(this);
		set.setOnClickListener(this);
		edit_input.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if ((actionId == 0 || actionId == 3) && event != null) {
					InputMethodManager imm = (InputMethodManager) v
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								v.getApplicationWindowToken(), 0);
					}
					fetchData(true);
				}
				return false;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1){
			if(data!=null){
				String getlocal = data.getStringExtra("local");
				Log.v("sma", getlocal);
				set.setText(getlocal);
			}
		}	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.search_btn:
			String editName = edit_input.getText().toString();
			if (StringUtils.isEmpty(editName)) {
				UIUtilities.showToast(this, "搜索内容不能为空");
				return;
			}
			fetchData(true);
			break;
		case R.id.linearlayout2:
			if (areaListViewLayout.getVisibility() == View.GONE) {
				areaListViewLayout.setVisibility(View.VISIBLE);

			} else {
				areaListViewLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.set:
			Intent in = new Intent().setClass(SelectSchoolActivity.this, SelectCitylActivity.class);
			in.putExtra("localname", set.getText().toString());
			startActivityForResult(in, 1);
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		JoinGroupAdapter.ehList.add(this);

	}

	@Override
	public void onPause() {
		super.onPause();

		// MyPushMessageReceiver.ehList.remove(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		JoinGroupAdapter.ehList.remove(this);
	}

	private void fetchData(boolean firstRefresh) {
		Animation shake = AnimationUtils.loadAnimation(
				SelectSchoolActivity.this, R.anim.shake);
		String search_str = edit_input.getEditableText().toString();
		if (TextUtils.isEmpty(search_str)) {
			edit_input.setText("");
			edit_input.startAnimation(shake);
			UIUtilities.showToast(this, "搜索内容不能为空");
		}
		if (firstRefresh) {
			ProgressDialogUtils.showProgressDialog("查询中", this, false);
		}
		friends.clear();
		mFriendAdapter.addAll(friends);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "searchSchool");
		if(regionId != 0)
			params.put("region", String.valueOf(regionId));
		params.put("schoolName", search_str);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_URL, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						schoolListView.onRefreshComplete();
						ProgressDialogUtils.dismissProgressBar();
						System.out.println("response=" + response);
						if (response.optInt("ret") == 0) {
							friends = SearchSchool.parseFromJson(response
									.optJSONArray("data"));
							// friends = new ArrayList<ClassRoomBean>();
							// friends.add(ClassRoomBean.parseFromJson(response.optJSONObject("data")));
							if (friends.size() > 0) {
								mFriendAdapter.addAll(friends);
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						schoolListView.onRefreshComplete();
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	public void agreeFriend(final long friendid, int type) {
		ProgressDialogUtils.showProgressDialog("通讯中", this, false);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("friendId", String.valueOf(friendid));
		params.put("isApprove", String.valueOf(type));

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_ApplyFriend, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();

						System.out.println("response=" + response);
						if (response.optInt("ret") == 0) {
							// TODO 登录成功后的帐号更新等

							UIUtilities.showToast(SelectSchoolActivity.this,
									"操作成功");
							fetchData(true);
						} else {
							StatusUtils.handleStatus(response,
									SelectSchoolActivity.this);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils
								.handleError(arg0, SelectSchoolActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);

	}

	@Override
	public void onMessage(final long friendID, final int type) {
		// TODO Auto-generated method stub
		agreeFriend(friendID, type);

	}

	private void initCityArea() {
		AssetsDatabaseManager.initManager(this);
		SQLiteDatabase database = AssetsDatabaseManager.getManager().getDatabase("conditions_db.sqlite");
		Cursor cursor = database.query("city", new String[]{"orgid,orgname,provinceid"}, null, null, null, null, "orgid asc");
		cities = new ArrayList<Region>();
		while (cursor.moveToNext()) {
			Region r = new Region();
			int orgid = cursor.getInt(0);
			r.setId(orgid);//获取第一列的值,第一列的索引从0开始
			r.setName(cursor.getString(1));
			Cursor cursor2 = database.query("country", new String[]{"orgid,orgname,cityid"}, "cityid = ?", new String[]{""+orgid}, null, null, "orgid asc");
			List<Region> childRegion = new ArrayList<Region>();
			while (cursor2.moveToNext()) {
				Region child = new Region();
				int childorgid = cursor2.getInt(0);
				child.setId(childorgid);//获取第一列的值,第一列的索引从0开始
				child.setName(cursor2.getString(1));
				child.setParentId(orgid);
				childRegion.add(child);
			}
			cursor2.close();
			r.setChildRegion(childRegion);
			cities.add(r);
		}
		cursor.close();
		database.close();
		AssetsDatabaseManager.closeAllDatabase();
		cityListAdapter = new HorizontalDoubleListAdapter(getApplicationContext(), cities);
		cityListView.setAdapter(cityListAdapter);

		selectDefult();

		cityListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				final int location = position;
				cityListAdapter.setSelectedPosition(position);
				cityListAdapter.notifyDataSetInvalidated();
				areaListAdapter = new HorizontalDoubleListSubAdapter(
						getApplicationContext(), cities, position);
				areaListView.setAdapter(areaListAdapter);
				areaListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub
						city.setText(cities.get(location).getName());
						area.setText(cities.get(location).getChildRegion().get(position).getName());
						regionId = cities.get(location).getChildRegion().get(position).getId();
						areaListViewLayout.setVisibility(View.GONE);
					}
				});
			}
		});
	}

	private void selectDefult() {
		final int location = 0;
		cityListAdapter.setSelectedPosition(0);
		cityListAdapter.notifyDataSetInvalidated();
		areaListAdapter = new HorizontalDoubleListSubAdapter(
				getApplicationContext(), cities, 0);
		areaListView.setAdapter(areaListAdapter);
		areaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				city.setText(cities.get(location).getName());
				area.setText(cities.get(location).getChildRegion().get(position).getName());
				regionId = cities.get(location).getChildRegion().get(position).getId();
				areaListViewLayout.setVisibility(View.GONE);
			}
		});
		city.setText(cities.get(0).getName());
		area.setText(cities.get(0).getChildRegion().get(0).getName());
		regionId = cities.get(0).getChildRegion().get(0).getId();
	}
	
}
