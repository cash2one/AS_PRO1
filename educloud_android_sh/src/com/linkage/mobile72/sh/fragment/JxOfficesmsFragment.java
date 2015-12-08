package com.linkage.mobile72.sh.fragment;

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
import android.view.ViewGroup;
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
import com.linkage.mobile72.sh.activity.JxHomeworkDetailActivity2;
import com.linkage.mobile72.sh.activity.JxHomeworkListActivity;
import com.linkage.mobile72.sh.adapter.JxhdListAdapter;
import com.linkage.mobile72.sh.adapter.JxhdListAdapter.DeleteListener;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.http.JXBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.swipelistview.BaseSwipeListViewListener;
import com.linkage.ui.widget.swipelistview.SwipeListView;
import com.nostra13.universalimageloader.utils.L;

public class JxOfficesmsFragment extends BaseFragment implements OnClickListener {
	
	private static final String TAG = JxHomeworkListActivity.class.getName();
	private static final int REQUEST_REFRESH = 1;
	public static final String KEY_SMSMESSAGETYPE = "smsmessagetype";
	public static String SMSMESSAGETYPE_HOMEWORK = String.valueOf(Consts.JxhdType.HOMEWORK);
	public static String SMSMESSAGETYPE_NOTICE = String.valueOf(Consts.JxhdType.NOTICE);
	public static String SMSMESSAGETYPE_COMMENT = String.valueOf(Consts.JxhdType.COMMENT);
	public static String SMSMESSAGETYPE_TOUPIAO = String.valueOf(Consts.JxhdType.TOUPIAO);
	
	private String mMessageBoxType;// 1收件箱 2发件箱
	private String mMessageType = Consts.JxhdType.OFFICESMS+"";//1 办公短信
	private String mSmsMessageStr = "消息";
	
	private int year, month, canyear, canmonth;
	private Time t;
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

	public static JxOfficesmsFragment newInstance(int type) {
		JxOfficesmsFragment fragment = new JxOfficesmsFragment();
		Bundle args = new Bundle();
		args.putInt("type", type);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMessageBoxType = ""+getArguments().getInt("type");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_jxhd_office_view, container, false);

		btnRight = (LinearLayout) rootView.findViewById(R.id.btn_right);
		btnLeft = (LinearLayout) rootView.findViewById(R.id.btn_left);
		dateText = (TextView) rootView.findViewById(R.id.time_text);
		btnRight.setOnClickListener(this);
		btnLeft.setOnClickListener(this);

		nextPageFootView = LayoutInflater.from(getActivity()).inflate(R.layout.swipe_list_view_footer, null);
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
		search_init = rootView.findViewById(R.id.search_init);
		search_input = (EditText) rootView.findViewById(R.id.search_input);
		search_btn = (Button) rootView.findViewById(R.id.search_btn);
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

		mList = (SwipeListView) rootView.findViewById(R.id.swipelistview);
		mList.setOffsetLeft(this.getResources().getDisplayMetrics().widthPixels * 3 / 4);
		DeleteListener deleteListener = new DeleteListener() {
			@Override
			public void delete(final int position) {
				mDialog = new MyCommonDialog(getActivity(), "提示消息", "您确定删除该记录？",
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
		mAdapter = new JxhdListAdapter(getActivity(),
				Integer.parseInt(mMessageBoxType), mData, mList, deleteListener);
		mList.setFocusableInTouchMode(true);
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
				i.setClass(getActivity(), JxHomeworkDetailActivity2.class);
				i.putExtra("jxbean", jxbean);
				i.putExtra("sendOrReceiveBox", Integer.parseInt(mMessageBoxType));
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
		mEmpty = (TextView) rootView.findViewById(android.R.id.empty);
		mEmpty.setText("暂无" + mSmsMessageStr);
		loadLocaData();
		mList.startRefreshing();
		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_REFRESH:
			break;
		}
	}

	private void deleteFile(final JXBean jxBean, final int position) {
		mList.closeAnimate(choicedItem);
		ProgressDialogUtils.showProgressDialog("正在删除", getActivity(), false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "deleteMessage");
		params.put("id", String.valueOf(jxBean.getId()));
		params.put("type", mMessageBoxType);
		params.put("smsMessageType", mMessageType);

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
						T.showShort(getActivity(), response.optString("msg"));
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, getActivity());
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
		params.put("commandtype", "getOfficeMessageList");
		params.put("page", String.valueOf(targetPage));
		params.put("pageSize", Consts.PAGE_SIZE);
		params.put("time", time);
		params.put("type", mMessageBoxType);// 1是收件箱(家长)、2是发件箱（教师）
		params.put("keyValue", keyValue);
		params.put("smsMessageType", "1");

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getOfficeMessageList,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						mList.onRefreshComplete();
						L.i(TAG + " response=" + response);
						if (response.optInt("ret") == 0) {
							mList.removeFooterView(nextPageFootView);
							JSONArray array = response.optJSONArray("data");
							List<JXBean> list = JXBean.parseFromJson(array, mMessageType);
							curPageIndex = targetPage;
							if (targetPage == 1) {
								mData.clear();
								mData.addAll(list);
								try {
									DeleteBuilder<JXBean, Integer> deleteJXBeanBuilder;
									deleteJXBeanBuilder = getDBHelper().getJXBeanDao()
											.deleteBuilder();
									deleteJXBeanBuilder.where().eq("smsMessageType",
											mMessageType);
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
							T.showShort(getActivity(), response.optString("msg"));
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
						StatusUtils.handleError(arg0, getActivity());
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
		appMap.put("smsMessageType", mMessageType);
		try {
			List<JXBean> list = getDBHelper().getJXBeanDao().queryForFieldValues(appMap);
			mData.clear();
			mData.addAll(list);
			mAdapter.notifyDataSetChanged();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(mData.size() > 0) {
			//mList.addFooterView(nextPageFootView);
			footText.setVisibility(View.VISIBLE);
			footProgress.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
				T.showShort(getActivity(), "请输入关键字");
			} else {
				keyValue = search_input.getText().toString();
				mList.startRefreshing();
			}
			break;

		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}