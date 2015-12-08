package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.JxhdListAdapter;
import com.linkage.mobile72.sh.adapter.JxhdListAdapter.DeleteListener;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.JXBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.multicolumnlist.MultiColumnPullToRefreshListView;
import com.linkage.ui.widget.swipelistview.BaseSwipeListViewListener;
import com.linkage.ui.widget.swipelistview.SwipeListView;
import com.nostra13.universalimageloader.utils.L;

public class JxHomeworkListActivity extends BaseActivity implements View.OnClickListener {
	private static final String TAG = JxHomeworkListActivity.class.getName();
	private static final int REQUEST_REFRESH = 1;
	public static final String KEY_SMSMESSAGETYPE = "smsmessagetype";
	public static String SMSMESSAGETYPE_HOMEWORK = String.valueOf(Consts.JxhdType.HOMEWORK);
	public static String SMSMESSAGETYPE_NOTICE = String.valueOf(Consts.JxhdType.NOTICE);
	public static String SMSMESSAGETYPE_COMMENT = String.valueOf(Consts.JxhdType.COMMENT);
	public static String SMSMESSAGETYPE_TOUPIAO = String.valueOf(Consts.JxhdType.TOUPIAO);
	public static String SMSMESSAGETYPE_SCORE = String.valueOf(Consts.JxhdType.TOUPIAO);
	
	
	private String mSmsMessageType;// 消息类型：1办公短信，2通知，3点评，4成绩，14作业，10投票，查询多个逗号分隔
	private String mSmsMessageStr = "消息";
	
	private int year, month, canyear, canmonth;
	private Time t;
	private Button createBtn;
	private TextView dateText;

	private View nextPageFootView;
	private TextView footText;
	private ProgressBar footProgress;
	private boolean loadNextFail = false;
	private int curPageIndex = 1;

	private List<JXBean> mData;
	private JxhdListAdapter mAdapter;
	private LinearLayout btnRight, btnLeft;
	private SwipeListView mList;
	private TextView mEmpty;
	private MyCommonDialog mDialog;
	private int choicedItem = -1;

	private View search_init;
	private EditText search_input;
	private Button search_btn;
	private String keyValue = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jxhd_base_view);

		mSmsMessageType = getIntent().getStringExtra(KEY_SMSMESSAGETYPE);
		if (TextUtils.equals(mSmsMessageType, SMSMESSAGETYPE_HOMEWORK)) {
			mSmsMessageStr = "作业";
		} else if (TextUtils.equals(mSmsMessageType, SMSMESSAGETYPE_NOTICE)) {
			mSmsMessageStr = "通知";
		} else if (TextUtils.equals(mSmsMessageType, SMSMESSAGETYPE_COMMENT)) {
			mSmsMessageStr = "点评";
		}
		setTitle(mSmsMessageStr);
		findViewById(R.id.back).setOnClickListener(this);

		btnRight = (LinearLayout) findViewById(R.id.btn_right);
		btnLeft = (LinearLayout) findViewById(R.id.btn_left);
		dateText = (TextView) findViewById(R.id.time_text);
		createBtn = (Button) findViewById(R.id.set);
		createBtn.setOnClickListener(this);
		createBtn.setVisibility(View.GONE);
		createBtn.setText("新建");
		btnRight.setOnClickListener(this);
		btnLeft.setOnClickListener(this);

		nextPageFootView = LayoutInflater.from(this).inflate(R.layout.swipe_list_view_footer, null);
		footText = (TextView)nextPageFootView.findViewById(R.id.textView);
		footProgress = (ProgressBar)nextPageFootView.findViewById(R.id.progressBar);
		nextPageFootView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				footText.setVisibility(View.GONE);
				footProgress.setVisibility(View.VISIBLE);
				if(!loadNextFail)
					curPageIndex = curPageIndex + 1;
				getListFromNetwork(curPageIndex);
			}
		});
		search_init = findViewById(R.id.search_init);
		search_input = (EditText) findViewById(R.id.search_input);
		search_btn = (Button) findViewById(R.id.search_btn);
		search_init.setOnClickListener(this);
		search_btn.setOnClickListener(this);

		mData = new ArrayList<JXBean>();

		t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
		t.setToNow();
		year = t.year;
		month = t.month;
		canmonth = t.month > 5 ? t.month - 6 : t.month + 6;
		canyear = canmonth > 5 ? t.year - 1 : t.year;
		dateText.setText(year + "年" + (month + 1) + "月");
		btnRight.setVisibility(View.INVISIBLE);

		mList = (SwipeListView) findViewById(R.id.swipelistview);
		mList.setOffsetLeft(this.getResources().getDisplayMetrics().widthPixels * 3 / 4);
		DeleteListener deleteListener = new DeleteListener() {
			@Override
			public void delete(final int position) {
				mDialog = new MyCommonDialog(JxHomeworkListActivity.this, "提示消息", "您确定删除该记录？",
						"取消", "确定");
				mDialog.setCancelable(true);
				mDialog.setCancelListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mDialog.isShowing()) {
							mDialog.dismiss();
						}
					}
				});
				mDialog.setOkListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mDialog.isShowing()) {
							mDialog.dismiss();
							deleteFile(mData.get(position), position);
						}
					}
				});
				mDialog.show();
			}
		};
		mAdapter = new JxhdListAdapter(JxHomeworkListActivity.this,
				Consts.MessageListType.SEND_BOX, mData, mList, deleteListener);
		mList.setAdapter(mAdapter);
		mList.setonRefreshListener(new SwipeListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getListFromNetwork(1);
			}
		});
		mList.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(int position, boolean toRight) {
				super.onOpened(position, toRight);
				if (choicedItem != -1 && choicedItem != position) {
					mList.closeAnimate(choicedItem);
				}
				choicedItem = position;
			}

			@Override
			public void onClosed(int position, boolean fromRight) {
				super.onClosed(position, fromRight);
			}

			@Override
			public void onClickFrontView(int position) {
				if (position == 0)
					return;
				super.onClickFrontView(position);
				mList.closeOpenedItems();
				JXBean jxbean = mData.get(position - 1);
				Intent i = new Intent();
				i.setClass(JxHomeworkListActivity.this, JxHomeworkDetailActivity.class);
				i.putExtra("jxbean", jxbean);
				startActivity(i);
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {
				super.onDismiss(reverseSortedPositions);
				// if (reverseSortedPositions.length > 0) {
				// mAdapter.remove(reverseSortedPositions[0]);
				// mAdapter.notifyDataSetChanged();
				// }
			}
		});
		// mList.setDivider(getResources().getDrawable(R.color.dark_gray));
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("暂无" + mSmsMessageStr);
		loadLocaData();
		mList.startRefreshing();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_REFRESH:
			mList.startRefreshing();
			break;
		}
	}

	private void deleteFile(final JXBean jxBean, final int position) {
		mList.closeAnimate(choicedItem);
		ProgressDialogUtils.showProgressDialog("正在删除", this, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "deleteMessage");
		params.put("id", String.valueOf(jxBean.getId()));
		params.put("type", "2");
		params.put("smsMessageType", mSmsMessageType);

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_deleteMessage,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						LogUtils.i(TAG + ":response=" + response);
						if (response.optInt("ret") == 0) {
							choicedItem = -1;
							mData.remove(position);
							mAdapter.notifyDataSetChanged();
							try {
								getDBHelper().getJXBeanDao().delete(jxBean);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						T.showShort(JxHomeworkListActivity.this, response.optString("msg"));
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, JxHomeworkListActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);

	}

	private void getListFromNetwork(final int targetPage) {
		BaseApplication.getInstance().cancelPendingRequests(TAG);
		String time = "";
		if (month < 9) {
			time = year + "0" + String.valueOf(month + 1);
		} else {
			time = year + String.valueOf(month + 1);
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getMessageList");
		params.put("page", String.valueOf(targetPage));
		params.put("pageSize", Consts.PAGE_SIZE);
		params.put("time", time);
		params.put("type", String.valueOf(2));// 1是收件箱(家长)、2是发件箱（教师）
		params.put("keyValue", keyValue);
		String smsMessageType_send = mSmsMessageType;
		if(TextUtils.equals(mSmsMessageType, SMSMESSAGETYPE_NOTICE))
		{
			smsMessageType_send = SMSMESSAGETYPE_NOTICE+","+SMSMESSAGETYPE_TOUPIAO;
		}
		params.put("smsMessageType", smsMessageType_send);

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getMessageList,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						mList.onRefreshComplete();
						L.i(TAG + " response=" + response);
						if (response.optInt("ret") == 0) {
							mList.removeFooterView(nextPageFootView);
							JSONArray array = response.optJSONArray("data");
							List<JXBean> list = JXBean.parseFromJson(array, mSmsMessageType);
							curPageIndex = targetPage;
							if (targetPage == 1) {
								mData.clear();
								mData.addAll(list);
								try {
									DeleteBuilder<JXBean, Integer> deleteJXBeanBuilder;
									deleteJXBeanBuilder = getDBHelper().getJXBeanDao()
											.deleteBuilder();
									deleteJXBeanBuilder.where().eq("smsMessageType",
											mSmsMessageType);
									deleteJXBeanBuilder.delete();
									for (int i = 0; i < list.size(); i++) {
										getDBHelper().getJXBeanDao().createOrUpdate(list.get(i));
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
							} else {
								mData.addAll(list);
							}
							if(list.size() == Integer.parseInt(Consts.PAGE_SIZE)) {
								mList.addFooterView(nextPageFootView);
								footText.setVisibility(View.VISIBLE);
								footProgress.setVisibility(View.GONE);
							}
							mAdapter.notifyDataSetChanged();
							if (mAdapter.isEmpty()) {
								mEmpty.setText("暂无"+mSmsMessageStr);
								mEmpty.setVisibility(View.VISIBLE);
							} else {
								mEmpty.setVisibility(View.GONE);
							}
						} else {
							T.showShort(JxHomeworkListActivity.this, response.optString("msg"));
							loadNextFail = true;
							footText.setVisibility(View.VISIBLE);
							footProgress.setVisibility(View.GONE);
							footText.setText("加载失败，点击重新加载");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						mList.onRefreshComplete();
						StatusUtils.handleError(arg0, JxHomeworkListActivity.this);
						loadNextFail = true;
						footText.setVisibility(View.VISIBLE);
						footProgress.setVisibility(View.GONE);
						footText.setText("加载失败，点击重新加载");
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	/**
	 * 加载本地存储的家校互动列表信息
	 */
	private void loadLocaData() {
		Map<String, Object> appMap = new HashMap<String, Object>();
		appMap.put("smsMessageType", mSmsMessageType);
		try {
			List<JXBean> list = getDBHelper().getJXBeanDao().queryForFieldValues(appMap);
			mData.clear();
			mData.addAll(list);
			mAdapter.notifyDataSetChanged();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(mData.size() > 0) {
			mList.addFooterView(nextPageFootView);
			footText.setVisibility(View.VISIBLE);
			footProgress.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.set:
			Intent i = null;
			if (TextUtils.equals(mSmsMessageType, SMSMESSAGETYPE_HOMEWORK)) {
				i = new Intent(this, CreateHomeworkActivity.class);
			} else if (TextUtils.equals(mSmsMessageType, SMSMESSAGETYPE_NOTICE)) {
				i = new Intent(this, CreateNoticeActivity.class);
			} else if (TextUtils.equals(mSmsMessageType, SMSMESSAGETYPE_COMMENT)) {
				i = new Intent(this, CreateCommentActivity.class);
			}
			startActivityForResult(i, REQUEST_REFRESH);
			break;
		case R.id.btn_left:
			month--;
			if (month == -1) {
				year--;
				month = 11;
			}
			if (year == canyear && month == canmonth) {
				btnLeft.setVisibility(View.INVISIBLE);
			}
			btnRight.setVisibility(View.VISIBLE);
			dateText.setText(year + "年" + (month + 1) + "月");
			mList.startRefreshing();
			break;
		case R.id.btn_right:
			month++;
			if (month == 12) {
				year++;
				month = 0;
			}
			if (year == t.year && month == t.month) {
				btnRight.setVisibility(View.INVISIBLE);
			}
			btnLeft.setVisibility(View.VISIBLE);
			dateText.setText(year + "年" + (month + 1) + "月");
			mList.startRefreshing();
			break;

		case R.id.search_init:
			search_init.setVisibility(View.GONE);
			search_input.setVisibility(View.VISIBLE);
			search_input.setFocusable(true);
			search_input.setFocusableInTouchMode(true);
			search_input.requestFocus();
			search_input.requestFocusFromTouch();
			search_btn.setVisibility(View.VISIBLE);
			break;
		case R.id.search_btn:
			if (TextUtils.isEmpty(search_input.getText().toString())) {
				T.showShort(this, "请输入关键字");
			} else {
				keyValue = search_input.getText().toString();
				mList.startRefreshing();
			}
			break;

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}