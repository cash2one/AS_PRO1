package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.ClazzScoreAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.Subject;
import com.linkage.mobile72.sh.data.http.ClazzScore;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener2;
import com.linkage.ui.widget.PullToRefreshListView;

public class ClazzScoreActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = ClazzScoreActivity.class.getName();
	private TextView title;
	private Button back;
	private int current_tab = 1;// 默认显示的为第一个标签页
	private RelativeLayout category_bar2;
	public RadioGroup topBtns;
	private Button leftButton, rightButton;
	private HorizontalScrollView scrollView;
	private List<Subject> subjectList;
	private PullToRefreshListView listView;
	private ClazzScoreAdapter mAdapter;
	private List<ClazzScore> scoreList;
	private TextView mEmpty;
	ImageView mEmptyImage;

	private SharedPreferences shared;
	private long mCurrentClassId;
	private String mCurrentClassName;
	private long subjectId;
	private int page = 1;

	private ClassRoom mCurrentClass;
	private ImageView indicate;
	private PopupWindow popWindow;
	private List<ClassRoom> mClassRoomList;

	private Button mChooseClazz;
	private TextView mShowCurClassName;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clazz_score);
		indicate = (ImageView) findViewById(R.id.indicate);
		mChooseClazz = (Button) findViewById(R.id.set);
		mChooseClazz.setText(R.string.title_clazz_choose);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mChooseClazz.setBackground(null);
		} else {
			mChooseClazz.setBackgroundDrawable(null);
		}
		mChooseClazz.setOnClickListener(this);
		mShowCurClassName = (TextView) findViewById(R.id.current_clazz_name);
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.back);
		category_bar2 = (RelativeLayout) findViewById(R.id.category_bar2);
		leftButton = (Button) findViewById(R.id.button_left);
		rightButton = (Button) findViewById(R.id.button_right);
		topBtns = (RadioGroup) findViewById(R.id.new_top_rg);
		scrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
		listView = (PullToRefreshListView) findViewById(R.id.base_pull_list);
		mEmpty = (TextView) findViewById(R.id.empty_text);
		mEmptyImage = (ImageView) findViewById(R.id.empty_image);
		mCurrentClass = (ClassRoom) getIntent().getSerializableExtra(
				"ClassRoom");
		shared = mApp.getSharedPreferences(getAccountName(),
				Context.MODE_PRIVATE);
		if (mCurrentClass == null) {
			mCurrentClassId = shared.getLong("choose_class_id", 0);
			mCurrentClassName = shared.getString("choose_class_name", "");
			mCurrentClass = new ClassRoom();
			mCurrentClass.setId(mCurrentClassId);
			mCurrentClass.setName(mCurrentClassName);
		} else {
			mCurrentClassId = mCurrentClass.getId();
			mCurrentClassName = mCurrentClass.getName();
		}
		mClassRoomList = mApp.getAllClassRoom();
		if (mCurrentClassId == 0) {
			if (mClassRoomList == null || mClassRoomList.size() <= 0) {
				Toast.makeText(ClazzScoreActivity.this, "没有可管理的班级！", Toast.LENGTH_SHORT).show();
				finish();
				return;
			} else {
				mCurrentClass = mClassRoomList.get(0);
				mCurrentClassId = mCurrentClass.getId();
				mCurrentClassName = mCurrentClass.getName();
				SharedPreferences.Editor editor = shared.edit();
				editor.putLong("choose_class_id", mCurrentClass.getId());
				editor.putString("choose_class_name", mCurrentClass.getName());
				editor.commit();
			}
		}
		topBtns.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				LogUtils.d(" ---------------------->checkedId=" + checkedId);
				current_tab = checkedId;
				if (current_tab >= 0 && current_tab < subjectList.size()) {
					subjectId = subjectList.get(current_tab).getId();
					// sbjName = subjectList.get(current_tab).getName();
				} else {
					subjectId = -1;
					LogUtils.e("invalid current_tab=" + current_tab);
				}
				LogUtils.d("sbjId = " + subjectId);
				page = 1;
				fetchScoreData();
			}
		});
		/*try {
			subjectList = getDBHelper().getSubjectDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		if (subjectList == null || subjectList.size() <= 0) {
			fetchSubjectData();
		} else {
			updateCategory();
		}
		scoreList = new ArrayList<ClazzScore>();
		mAdapter = new ClazzScoreAdapter(this, scoreList);
		listView.setAdapter(mAdapter);
		listView.setDivider(null);
		mEmpty.setText("暂时没有成绩");
		mEmptyImage.setImageResource(R.drawable.no_score);
		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				page = 1;
				fetchScoreData();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				fetchScoreData();
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ClazzScore clazzScore = (ClazzScore) mAdapter.getItem(position);
				Intent intent = new Intent(ClazzScoreActivity.this,
						ClazzScoreDetailActivity.class);
				if (clazzScore != null) {
					intent.putExtra("scoreId", clazzScore.getId());
					intent.putExtra("subjectId", subjectId);
					intent.putExtra("classid", mCurrentClassId);
					startActivity(intent);
				}
			}
		});
		if (mClassRoomList != null && mClassRoomList.size() > 1) {
			indicate.setVisibility(View.VISIBLE);
		} else {
			indicate.setVisibility(View.GONE);
		}
		initPopWindow();
		// setTitle(mCurrentClassName);
		mShowCurClassName.setText(mCurrentClassName);
		if (mClassRoomList.size() > 1) {
			mChooseClazz.setVisibility(View.VISIBLE);
		} else {
			mChooseClazz.setVisibility(View.GONE);
		}
		back.setOnClickListener(this);
		//title.setOnClickListener(this);
		scrollView.setOnTouchListener(new TouchListenerImpl());
		leftButton.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.title:
			if (mClassRoomList != null && mClassRoomList.size() > 1) {
				if (popWindow.isShowing()) {
					indicate.setImageResource(R.drawable.jx_parent_choose_child_down);
					popWindow.dismiss();
				} else {
					indicate.setImageResource(R.drawable.jx_parent_choose_child_up);
					popWindow.showAsDropDown(titleLayout, 0, 15);
				}
			}
			break;
		case R.id.set:
			Intent intent = new Intent(this, ClazzChooseActivity.class);
			startActivityForResult(intent, ClazzChooseActivity.REQUEST_CODE);
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ClazzChooseActivity.REQUEST_CODE:
			if (null != data) {
				ClassRoom room = (ClassRoom) data
						.getSerializableExtra(ClazzChooseActivity.CURRENT_CLAZZ_KEY);
				if (null != room) {
					mShowCurClassName.setText(room.getName());
					mCurrentClassId = room.getId();
					mCurrentClass = room;
					mCurrentClassId = mCurrentClass.getId();
					SharedPreferences.Editor editor = shared.edit();
					editor.putLong("choose_class_id", mCurrentClass.getId());
					editor.putString("choose_class_name", mCurrentClass.getName());
					editor.commit();
					page = 1;
					fetchScoreData();
				}
			}
			break;

		default:
			break;
		}

	}

	private void updateCategory() {

		if(subjectList == null || subjectList.size() <= 0) {
			category_bar2.setVisibility(View.GONE);
		}else {
			category_bar2.setVisibility(View.VISIBLE);
			topBtns.removeAllViews();
			RadioButton btn = (RadioButton) LayoutInflater.from(this).inflate(
					R.layout.sc_top_rbtn, null);
			btn.setText(subjectList.get(0).getName());
			btn.setId(0);
			btn.setChecked(true);
			topBtns.addView(btn);
	
			RadioButton btn1;
	
			// 更新分类显示
			for (int i = 1; i < subjectList.size(); i++) {
				btn1 = (RadioButton) LayoutInflater.from(this).inflate(
						R.layout.sc_top_rbtn, null);
				String subjectName = subjectList.get(i).getName();
				if(subjectName != null && subjectName.length() > 4) {
					subjectName = subjectName.substring(0,4) + "...";
				}
				btn1.setText(subjectName);
				btn1.setId(i);
				topBtns.addView(btn1);
			}
			btn.performClick();
		}
	}

	private void initPopWindow() {
		LayoutInflater inflater = LayoutInflater.from(this);
		// 引入窗口配置文件
		View view = inflater.inflate(R.layout.pop_jx_parent_choose_child, null);
		ListView listView = (ListView) view.findViewById(R.id.listView);
		final ChildAdapter adapter = new ChildAdapter();
		listView.setAdapter(adapter);
		listView.setDivider(null);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ClassRoom clazz = adapter.getItem(position);
				if (popWindow.isShowing()) {
					popWindow.dismiss();
				}
				mCurrentClass = clazz;
				mCurrentClassId = mCurrentClass.getId();
				// refreshDefaultClassRoom();
				page = 1;
				fetchScoreData();
			}
		});
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		popWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, false);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setOutsideTouchable(true);
		popWindow.setFocusable(true);
		popWindow.update();
		popWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				indicate.setImageResource(R.drawable.jx_parent_choose_child_down);
			}
		});

	}

	class ChildAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mClassRoomList.size();
		}

		@Override
		public ClassRoom getItem(int position) {
			// TODO Auto-generated method stub
			return mClassRoomList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(ClazzScoreActivity.this)
						.inflate(R.layout.item_list_single_text_center, null);
				viewHolder = new ViewHolder();
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.list_textshow);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			ClassRoom r = getItem(position);
			viewHolder.textView.setText(r.getName());
			viewHolder.textView.setTextColor(Color.rgb(96, 205, 246));

			return convertView;
		}

		class ViewHolder {
			public TextView textView;
		}
	}

	private void fetchSubjectData() {
		ProgressDialogUtils.showProgressDialog("", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getSubjectList");

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_getSubjectList, Request.Method.POST, params,
				true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// mProgress.setVisibility(View.GONE);
						LogUtils.i(" response=" + response);
						ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
							subjectList = new ArrayList<Subject>();
							JSONArray array = response.optJSONArray("data");
							if (array != null && array.length() > 0) {
								try {
									DeleteBuilder<Subject, Integer> deleteSubjectBuilder = getDBHelper()
											.getSubjectDao().deleteBuilder();
									deleteSubjectBuilder.delete();
								} catch (SQLException e) {
									e.printStackTrace();
								}
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.optJSONObject(i);
									Subject subject = new Subject();
									subject.setId(obj.optLong("id"));
									subject.setName(obj.optString("name"));
									subjectList.add(subject);
									try {
										getDBHelper().getSubjectDao().create(
												subject);
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								updateCategory();
							}
						} else {
							T.showShort(ClazzScoreActivity.this,
									response.optString("msg"));
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, ClazzScoreActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	private void fetchScoreData() {
		if (page == 1)
			ProgressDialogUtils.showProgressDialog("", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getClassResultBySubject");
		params.put("classid", String.valueOf(mCurrentClassId));
		params.put("subjectid", String.valueOf(subjectId));
		params.put("page", String.valueOf(page));
		params.put("pageSize", Consts.PAGE_SIZE);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_getClassResultBySubject, Request.Method.POST,
				params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// mProgress.setVisibility(View.GONE);
						listView.onRefreshComplete();
						ProgressDialogUtils.dismissProgressBar();
						LogUtils.e(" response=" + response);
						if (response.optInt("ret") == 0) {
							setTitle(mCurrentClass.getName());
							SharedPreferences.Editor editor = shared.edit();
							editor.putLong("choose_class_id",
									mCurrentClass.getId());
							editor.putString("choose_class_name",
									mCurrentClass.getName());
							editor.commit();
							List<ClazzScore> temp = ClazzScore.parseFromJson(response.optJSONArray("data"));
							if(temp != null) {
								if(temp.size() == Integer.parseInt(Consts.PAGE_SIZE)) {
									listView.setMode(Mode.BOTH);
								}else {
									listView.setMode(Mode.PULL_FROM_START);
								}
								mAdapter.addAll(temp, page != 1);
							}
							mAdapter.notifyDataSetChanged();
							if (mAdapter.isEmpty()) {
								mEmpty.setVisibility(View.VISIBLE);
								mEmptyImage.setVisibility(View.VISIBLE);
							} else {
								mEmpty.setVisibility(View.GONE);
								mEmptyImage.setVisibility(View.GONE);
							}
							page = page + 1;
						} else {
							T.showShort(ClazzScoreActivity.this,
									response.optString("msg"));
							if (mAdapter.isEmpty()) {
								mEmpty.setVisibility(View.VISIBLE);
								mEmptyImage.setVisibility(View.VISIBLE);
							} else {
								mEmpty.setVisibility(View.GONE);
								mEmptyImage.setVisibility(View.GONE);
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						listView.onRefreshComplete();
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, ClazzScoreActivity.this);
					}
				});

		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		/*
		 * mAdapter.addAll(null, page != 1); if (mAdapter.isEmpty()) {
		 * mEmpty.setVisibility(View.VISIBLE); } else {
		 * mEmpty.setVisibility(View.GONE); } new Handler().postDelayed(new
		 * Runnable() {
		 * 
		 * @Override public void run() { LogUtils.e("FetchScoreData");
		 * List<ClazzScore> temp = new ArrayList<ClazzScore>(); int index =
		 * (page-1)*Integer.parseInt(Consts.PAGE_SIZE); for(int i=index; i<index
		 * + Integer.parseInt(Consts.PAGE_SIZE); i++) { ClazzScore c = new
		 * ClazzScore(); c.setAverage(80); c.setDate("2015-04-10");
		 * c.setId(i+1); c.setMax(90); c.setMin(60);
		 * c.setName(subjectList.get(current_tab).getName()+"考试"+i);
		 * c.setTypeName("期中考试"); temp.add(c); }
		 * LogUtils.e("scoreList.size:"+temp.size()); mAdapter.addAll(temp, page
		 * != 1); if (mAdapter.isEmpty()) { mEmpty.setVisibility(View.VISIBLE);
		 * } else { mEmpty.setVisibility(View.GONE); }
		 * listView.onRefreshComplete(); } }, 1000);
		 * setTitle(mCurrentClass.getName());
		 */
	}

	private class TouchListenerImpl implements OnTouchListener {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:

				break;
			case MotionEvent.ACTION_MOVE:
				int scrollX = view.getScrollX();
				int width = view.getWidth();
				int scrollViewMeasuredHeight = scrollView.getChildAt(0)
						.getMeasuredWidth();
				if (scrollX < 30) {
					leftButton.setVisibility(View.GONE);
				}
				if (scrollX > 40) {
					leftButton.setVisibility(View.VISIBLE);
				}
				if ((scrollX + width) > scrollViewMeasuredHeight - 10) {
					rightButton.setVisibility(View.GONE);
				}
				if ((scrollX + width) < scrollViewMeasuredHeight - 20) {
					rightButton.setVisibility(View.VISIBLE);
				}
				break;

			default:
				break;
			}
			return false;
		}

	};
}
