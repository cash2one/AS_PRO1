package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.http.Attendance;
import com.linkage.mobile72.sh.data.http.Attendance.AttendanceState;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.widget.calendar.CalendarPickerView;
import com.linkage.mobile72.sh.widget.calendar.CalendarPickerView.OnInvalidDateSelectedListener;
import com.linkage.mobile72.sh.widget.calendar.CalendarPickerView.SelectionMode;
import com.linkage.mobile72.sh.Consts;

public class KaoqinActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = KaoqinActivity.class.getName();

	private TextView title;
	private Button back, set;
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat monthFormat;
	private CalendarPickerView babyLiveCalendar;
	private LinearLayout btnLeft, btnRight;
	private TextView monthText;
	private Calendar cal;
	private int year, month;
	
	private LinearLayout kqSurveyStateLayout;
	private TextView kqSurveyZSText,kqSurveyQJText,kqSurveyQQText;
	private Button kqSurveyStatePhoto;
	private LinearLayout kqStateLayout;
	private String today;
	private Attendance monthAttendance;//
	private List<Attendance.AttendanceState> monthAttendanceList;
	private Attendance dayAttendance;//
	private List<Attendance.AttendanceState> dayAttendanceList;
	private Map<String, Integer> map;

	private MyCommonDialog dialog;
	private ClassRoom mCurrentClass;
	private AccountData account;
	private ImageView indicate;
	private PopupWindow popWindow;
	private List<ClassRoom> mClassRoomList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kaoqin);
		title = (TextView)findViewById(R.id.title);
		back = (Button)findViewById(R.id.back);
		set = (Button)findViewById(R.id.set);
		set.setVisibility(View.INVISIBLE);
		mCurrentClass = (ClassRoom)getIntent().getSerializableExtra("ClassRoom");
		if(mCurrentClass == null) {
			mCurrentClass = getDefaultAccountClass();
		}
		indicate = (ImageView)findViewById(R.id.indicate);
		showClazzTitle();
		kqSurveyStateLayout = (LinearLayout)findViewById(R.id.kaoqin_survey_state_layout);
		kqStateLayout = (LinearLayout)findViewById(R.id.kaoqin_state_layout);
		kqSurveyZSText = (TextView)findViewById(R.id.kaoqin_survey_zs);
		kqSurveyQJText = (TextView)findViewById(R.id.kaoqin_survey_qj);
		kqSurveyQQText = (TextView)findViewById(R.id.kaoqin_survey_qq);
		kqSurveyStatePhoto = (Button)findViewById(R.id.kaoqin_survey_show_photo_btn);
		
		title.setOnClickListener(this);
		back.setOnClickListener(this);
		set.setOnClickListener(this);
		kqSurveyStatePhoto.setOnClickListener(this);
		
		dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		monthFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 0);
		btnLeft = (LinearLayout)findViewById(R.id.btn_left);
		btnRight = (LinearLayout)findViewById(R.id.btn_right);
		monthText = (TextView) findViewById(R.id.time_text);
		babyLiveCalendar = (CalendarPickerView) findViewById(R.id.calendar_view);
		
		map = new HashMap<String, Integer>();
		showMonthTitle();
		btnLeft.setOnClickListener(this);
		btnRight.setOnClickListener(this);
	}
	
	private void showMonthTitle() {
		ProgressDialogUtils.showProgressDialog("", this, true);
		year = cal.get(Calendar.YEAR);//2014
		month = cal.get(Calendar.MONTH) + 1;//12
		monthText.setText(year + "年" + month + "月");
		babyLiveCalendar.init(cal.getTime(), cal.getTime()).inMode(
				SelectionMode.SINGLE);// .withSelectedDate(new Date());
		// SelectionMode.SINGLE 只能点一个日期， MULTIPLE 能多选，RANGE 能跨选
		babyLiveCalendar.setOnInvalidDateSelectedListener(new OnInvalidDateSelectedListener() {

			@Override
			public void onInvalidDateSelected(Date date) {
				babyLiveCalendar.selectDate(date);
				String day = dateFormat.format(date);
				getAttendanceByDate(day);
			}
		});
		/*Calendar now = Calendar.getInstance();
		int nowYear = now.get(Calendar.YEAR);//2015
		int nowMonth = now.get(Calendar.MONTH) + 1;//3
		if(year == nowYear && month >= nowMonth) {
			btnRight.setVisibility(View.INVISIBLE);
		}else if(nowYear == year && nowMonth - month >= 6){
			btnLeft.setVisibility(View.INVISIBLE);
		}else if((nowYear-1) == year && (nowMonth+12) - month >= 6) {
			btnLeft.setVisibility(View.INVISIBLE);
		}else {
			btnLeft.setVisibility(View.VISIBLE);
			btnRight.setVisibility(View.VISIBLE);
		}*/
		btnLeft.setVisibility(View.VISIBLE);
		btnRight.setVisibility(View.VISIBLE);
		getAttendanceByMonth();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1 && resultCode == RESULT_OK) {
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 0);
			showMonthTitle();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.set:
			Intent intent = new Intent(this, AttenActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.btn_left:
			cal.add(Calendar.MONTH, -1);
			//mList.startRefreshing();
			showMonthTitle();
			break;
		case R.id.btn_right:
			cal.add(Calendar.MONTH, 1);
			//mList.startRefreshing();
			showMonthTitle();
			break;
		case R.id.title:
			if(mClassRoomList != null && mClassRoomList.size() > 1) {
				if(popWindow.isShowing()) {
					indicate.setImageResource(R.drawable.jx_parent_choose_child_down);
					popWindow.dismiss();
				}else {
					indicate.setImageResource(R.drawable.jx_parent_choose_child_up);
					popWindow.showAsDropDown(titleLayout, 0, 15);
				}
			}
			break;
		case R.id.kaoqin_survey_show_photo_btn:
			if(TextUtils.isEmpty(dayAttendance.getDisplayKqPhoto())) {
				dialog = new MyCommonDialog(this, "提示消息", "该日没有上传考勤图片", null, "确定");
				dialog.setCancelable(true);
				dialog.setOkListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
					}
				});
				dialog.show();
			}else {
				ArrayList<String> choosePics = new ArrayList<String>();
				choosePics.add(dayAttendance.getDisplayKqPhoto());
				Intent in = new Intent(KaoqinActivity.this,
                        PictureReviewNetActivity.class);
				in.putStringArrayListExtra(PictureReviewNetActivity.RES, choosePics);
				in.putExtra(PictureReviewNetActivity.TITLE, "考勤照片");
				in.putExtra("position", 0);
                startActivity(in);
			}
			break;
		}
	}
	
	private void getAttendanceByMonth() {
		if(mCurrentClass ==null)
		{
			 Toast.makeText(this, "没有可以管理的班级", Toast.LENGTH_SHORT).show();
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getClassAttendanceByMonth");
		params.put("classid", String.valueOf(mCurrentClass.getId()));
		params.put("month", monthFormat.format(cal.getTime()));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				if (response.optInt("ret") != 1) {
					if(response.optInt("ret") == 0)StatusUtils.handleStatus(response, KaoqinActivity.this);
					monthAttendance = Attendance.parseFromJson(response.optJSONObject("data"), 1);
					if(monthAttendance != null) {
						dayAttendance = new Attendance();
						dayAttendance.setDisplayKqCount(monthAttendance.getDisplayKqCount());
						dayAttendance.setDisplayKqPhoto(monthAttendance.getDisplayKqPhoto());
						dayAttendance.setDisplayKqQj(monthAttendance.getDisplayKqQj());
						dayAttendance.setDisplayKqQq(monthAttendance.getDisplayKqQq());
						monthAttendanceList = monthAttendance.getKqList(); 
						if(monthAttendanceList != null && monthAttendanceList.size() > 0){
							map.clear();
							for(Attendance.AttendanceState a : monthAttendanceList) {
								map.put(a.getKqDate(), a.getKqStatus());
							}
							babyLiveCalendar.setKaoQinData(map);
						}
						Calendar now = Calendar.getInstance();
						if(cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)) {
							today = dateFormat.format(now.getTime());
						}else {
							cal.set(Calendar.DATE, 1);
							today = dateFormat.format(cal.getTime());
						}
					}
					getAttendanceByDate(today);
				}else {
					StatusUtils.handleStatus(response, KaoqinActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, KaoqinActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);	
		/*new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				ProgressDialogUtils.dismissProgressBar();
				map.clear();
				map.put(year + "0" + month + "" + "01", 1);
				map.put(year + "0" + month + "" + "02", 2);
				map.put(year + "0" + month + "" + "03", 3);
				map.put(year + "0" + month + "" + "04", 2);
				map.put(year + "0" + month + "" + "05", 2);
				map.put(year + "0" + month + "" + "06", 3);
				babyLiveCalendar.setKaoQinData(map);
			}
		}, 500);*/
		
	}
	
	private void getAttendanceByDate(String day) {
		if(mCurrentClass ==null)
		{
			 Toast.makeText(this, "没有可以管理的班级", Toast.LENGTH_SHORT).show();
			return;
		}
		ProgressDialogUtils.showProgressDialog("", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getClassAttendanceByDate");
		params.put("classid", String.valueOf(mCurrentClass.getId()));
		params.put("date", day);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				kqStateLayout.removeAllViews();
				if (response.optInt("ret") == 0) {
					dayAttendance = Attendance.parseFromJson(response.optJSONObject("data"), 2);
					if(dayAttendance != null)dayAttendance.setTeacherHasCreateKq(true);
					refreshDay(dayAttendance);
				}else if(response.optInt("ret") == 1){
					dayAttendance = Attendance.parseFromJson(response.optJSONObject("data"), 2);
					if(dayAttendance != null)dayAttendance.setTeacherHasCreateKq(false);
					refreshDay(dayAttendance);
				}else {
					StatusUtils.handleStatus(response, KaoqinActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, KaoqinActivity.this);
				dayAttendance = null;
				refreshDay(dayAttendance);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	private void refreshDay(Attendance a) {
		if(a == null) {
			kqSurveyStateLayout.setVisibility(View.INVISIBLE);
			kqStateLayout.setVisibility(View.INVISIBLE);
		}else {
			if(a.isTeacherHasCreateKq()) {
				kqSurveyStateLayout.setVisibility(View.VISIBLE);
				kqSurveyZSText.setText(""+a.getDisplayKqCount());
				kqSurveyQJText.setText(""+a.getDisplayKqQj());
				kqSurveyQQText.setText(""+a.getDisplayKqQq());
				if(TextUtils.isEmpty(a.getDisplayKqPhoto())||"null".equals(a.getDisplayKqPhoto())) {
					kqSurveyStatePhoto.setVisibility(View.INVISIBLE);
				}else {
					kqSurveyStatePhoto.setVisibility(View.VISIBLE);
				}
			}else {
				kqSurveyStateLayout.setVisibility(View.GONE);
			}
			dayAttendanceList = a.getKqList();
			if(dayAttendanceList!=null&&dayAttendanceList.size() > 0) {
				kqStateLayout.setVisibility(View.VISIBLE);
	 			for(int i = 0;i<dayAttendanceList.size();i++){
	 				final AttendanceState aa = dayAttendanceList.get(i);
	 				View view = LayoutInflater.from(this).inflate(R.layout.adapter_attendance_list, null);
					TextView name = (TextView)view.findViewById(R.id.name);
					TextView state = (TextView)view.findViewById(R.id.state);
					RadioButton call = (RadioButton)view.findViewById(R.id.call);
					LinearLayout layout = (LinearLayout)view.findViewById(R.id.reason_layout);
					TextView reason = (TextView)view.findViewById(R.id.reason);
					if(aa != null) {
						name.setText(""+aa.getName());
						state.setText(aa.getState() == 1 ? "正常" : (aa.getState() == 2 ? "请假" : "缺勤"));
						if(aa.getState() == 3) {
							state.setTextColor(getResources().getColor(R.color.color_queqin));
						}
						call.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if(TextUtils.isEmpty(aa.getPhone())) {
									UIUtilities.showToast(KaoqinActivity.this, "未获取电话号码，无法拨打电话");
								}else {
									Uri uri = Uri.parse("tel:"+aa.getPhone());
									Intent intent = new Intent(Intent.ACTION_DIAL, uri);
									startActivity(intent);
								}
							}
						});
						
						if(TextUtils.isEmpty(aa.getReason())||"null".equals(aa.getReason())) {
							layout.setVisibility(View.GONE);
						}else {
							layout.setVisibility(View.VISIBLE);
							reason.setText(""+aa.getReason());
						}
						kqStateLayout.addView(view);
					}
				}
			}else {
				kqStateLayout.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private void showClazzTitle() {
    	account = getCurAccount();
		if (account != null) {
			mClassRoomList = getAccountClass();
			if(mClassRoomList != null && mClassRoomList.size() > 0) {
				if(mClassRoomList.size() == 1) {
					indicate.setVisibility(View.INVISIBLE);
					mCurrentClass = mClassRoomList.get(0);
					refreshDefaultClassRoom();
				}else {
					indicate.setVisibility(View.VISIBLE);
					initPopWindow();
					for(ClassRoom c : mClassRoomList) {
						if(c.getDefaultClass() == 1) {
							mCurrentClass = c;
							break;
						}
					}
					if(mCurrentClass == null) {
						mCurrentClass = mClassRoomList.get(0);
						refreshDefaultClassRoom();
					}
				}
				setTitle(mCurrentClass.getName());
			}else {
				setTitle("家校互动");
			}
		}
    }
	
	private void initPopWindow() {
		LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件  
        View view = inflater.inflate(R.layout.pop_jx_parent_choose_child, null);
        ListView listView = (ListView)view.findViewById(R.id.listView);
        final ChildAdapter adapter = new ChildAdapter();
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ClassRoom child = adapter.getItem(position);
				if(popWindow.isShowing()) {
					popWindow.dismiss();
				}
				checkTodayAttandance(child);
			}
		});
        listView.setItemsCanFocus(false);    
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        popWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, false);
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
				convertView = LayoutInflater.from(KaoqinActivity.this).inflate(R.layout.item_list_single_text_center, null);
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
    
	private void refreshDefaultClassRoom() {
		try {
			getDBHelper().getClassRoomData().updateRaw("update ClassRoom set defaultClass = 0 where loginName = " + getAccountName() + " and joinOrManage = 1");
			getDBHelper().getClassRoomData().updateRaw("update ClassRoom set defaultClass = 1 where loginName = " + getAccountName() + " and joinOrManage = 1" + " and id = " + mCurrentClass.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void checkTodayAttandance(final ClassRoom clazz) {
		ProgressDialogUtils.showProgressDialog("", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getClassAttendanceByDate");
		params.put("classid", String.valueOf(clazz.getId()));
		params.put("date", dateFormat.format(new Date()));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				//-1 服务器try catch一些异常
				//0 已考勤 不管全到还是没到
				//1 未考勤
				mCurrentClass = clazz;
				refreshDefaultClassRoom();
				//refreshData();
				
				if (response.optInt("ret") == 1) {
					Intent intentAtten = new Intent(KaoqinActivity.this, AttenActivity.class);
					startActivity(intentAtten);
					finish();
				}else {
					setTitle(mCurrentClass.getName());
					showMonthTitle();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, KaoqinActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}
