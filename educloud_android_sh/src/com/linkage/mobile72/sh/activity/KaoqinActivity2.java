package com.linkage.mobile72.sh.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.Attendance2;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.widget.calendar.CalendarPickerView;
import com.linkage.mobile72.sh.widget.calendar.CalendarPickerView.OnInvalidDateSelectedListener;
import com.linkage.mobile72.sh.widget.calendar.CalendarPickerView.SelectionMode;
import com.linkage.mobile72.sh.Consts;

public class KaoqinActivity2 extends BaseActivity implements OnClickListener {

	private static final String TAG = KaoqinActivity2.class.getName();
	public static final String EXTRA_STU_ID = "stu_id";
	private long childid = 0;
	private Button back, set;
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat monthFormat;
	private CalendarPickerView babyLiveCalendar;
	private LinearLayout btnLeft, btnRight;
	private TextView monthText;
	private Calendar cal;
	private int year, month;
	
	private RelativeLayout kqSurveyStateLayout, kqStateLayout;
	private TextView kqSurveyStateText;
	private Button kqSurveyStatePhoto;
	private TextView kqStateText, kqStateOperate, kqStateReason;
	private String today;
	private List<Attendance2> attendanceList;
	private Map<String, Integer> map;
	private Attendance2 a;
	private Map<String, Attendance2> fullMap;

	private MyCommonDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kaoqin2);
		setTitle("考勤");
		back = (Button)findViewById(R.id.back);
		set = (Button)findViewById(R.id.set);
		set.setVisibility(View.VISIBLE);
		set.setText("请假");
		childid = getIntent().getLongExtra(EXTRA_STU_ID, 0);
		kqSurveyStateLayout = (RelativeLayout)findViewById(R.id.kaoqin_survey_state_layout);
		kqStateLayout = (RelativeLayout)findViewById(R.id.kaoqin_state_layout);
		kqSurveyStateText = (TextView)findViewById(R.id.kaoqin_survey_state);
		kqSurveyStatePhoto = (Button)findViewById(R.id.kaoqin_survey_show_photo_btn);
		kqStateText = (TextView)findViewById(R.id.kaoqin_state);
		kqStateOperate = (TextView)findViewById(R.id.kaoqin_state_operate);
		kqStateReason = (TextView)findViewById(R.id.kaoqin_state_reason);
		back.setOnClickListener(this);
		set.setOnClickListener(this);
		kqSurveyStatePhoto.setOnClickListener(this);
		kqStateOperate.setOnClickListener(this);
		dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		monthFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 0);
		btnLeft = (LinearLayout)findViewById(R.id.btn_left);
		btnRight = (LinearLayout)findViewById(R.id.btn_right);
		monthText = (TextView) findViewById(R.id.time_text);
		babyLiveCalendar = (CalendarPickerView) findViewById(R.id.calendar_view);
		
		map = new HashMap<String, Integer>();
		fullMap = new HashMap<String, Attendance2>();
		showMonthTitle();
		btnLeft.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		
		long cid = 0;
		if(childid == 0) cid = getDefaultAccountChild().getId();
		else cid = childid;
		SharedPreferences.Editor editor = BaseApplication.getInstance().getSharedPreferences(""+cid, Context.MODE_PRIVATE).edit();
		editor.putInt("attend_notice", 0);
		editor.commit();
		
		Intent intent = new Intent();
        intent.setAction(Consts.BROADCAST_ACTION_CONNECT);
        intent.putExtra(Consts.BROADCAST_ACTTYPE_CONNECT, Consts.BROADCAST_JX_REMIND_HIDE);
        sendBroadcast(intent);
	}
	
	private void showMonthTitle() {
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
				if(fullMap.containsKey(day)) {
					a = fullMap.get(day);
				}else {
					a = null;
				}
				refreshDay();
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
		
		getChildAttendance();
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
			Intent intent = new Intent(this, KqQjActivity.class);
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
		case R.id.kaoqin_survey_show_photo_btn:
			if(TextUtils.isEmpty(a.getPhoto())) {
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
				choosePics.add(a.getPhoto());
				Intent in = new Intent(KaoqinActivity2.this,
                        PictureReviewNetActivity.class);
				in.putStringArrayListExtra(PictureReviewNetActivity.RES, choosePics);
				in.putExtra(PictureReviewNetActivity.TITLE, "考勤照片");
				in.putExtra("position", 0);
                startActivity(in);
			}
			break;
		case R.id.kaoqin_state_operate:
			dialog = new MyCommonDialog(this, "提示消息", "您确定要取消请假吗？",
					"取消", "确定");
			dialog.setCancelable(true);
			dialog.setCancelListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
				}
			});
			dialog.setOkListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
					delAttendance2();
				}
			});
			dialog.show();
			break;
		}
	}
	
	private void getChildAttendance() {
		ProgressDialogUtils.showProgressDialog("", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getStudentAttendanceByMonth");
		if(childid == 0) {
			params.put("studentid", String.valueOf(getDefaultAccountChild().getId()));
		}else {
			params.put("studentid", String.valueOf(childid));
		}
		params.put("month", monthFormat.format(cal.getTime()));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				if (response.optInt("ret") == 0) {
					today = response.optJSONObject("data").optString("today");
					attendanceList = Attendance2.parseFromJson(response.optJSONObject("data").optJSONArray("kqList"));
					if(attendanceList.size() > 0) {
						map.clear();
						fullMap.clear();
						for(Attendance2 a : attendanceList) {
							map.put(a.getDay(), a.getState());
							fullMap.put(a.getDay(), a);
						}
						babyLiveCalendar.setKaoQinData(map);
					
						if(fullMap.containsKey(today)) {
							a = fullMap.get(today);
						}else {
							a = null;
						}
					}else {
						a = null;
					}
					refreshDay();
				}else {
					StatusUtils.handleStatus(response, KaoqinActivity2.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, KaoqinActivity2.this);
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
	
	private void delAttendance2() {
		ProgressDialogUtils.showProgressDialog("", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "delAttendance");
		params.put("studentid", String.valueOf(getDefaultAccountChild().getId()));
		params.put("date", a.getDay());
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				if (response.optInt("ret") == 0) {
					UIUtilities.showToast(KaoqinActivity2.this, "您已取消当天请假");
					//a.setState(0);
					//refreshDay();
					showMonthTitle();
				}else {
					StatusUtils.handleStatus(response, KaoqinActivity2.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, KaoqinActivity2.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	private void refreshDay() {
		if(a == null) {
			kqSurveyStateLayout.setVisibility(View.INVISIBLE);
			kqStateLayout.setVisibility(View.INVISIBLE);
		}else {
			if(a.getState() == 0) {//未考勤
				kqSurveyStateLayout.setVisibility(View.INVISIBLE);
				kqStateLayout.setVisibility(View.INVISIBLE);
			}else if(a.getState() == 1) {//正常
				kqSurveyStateLayout.setVisibility(View.VISIBLE);
				kqStateLayout.setVisibility(View.INVISIBLE);
				kqSurveyStateText.setVisibility(View.VISIBLE);
				kqSurveyStateText.setText("按时到校");
			}else if(a.getState() == 2) {//请假
				kqSurveyStateLayout.setVisibility(View.VISIBLE);
				kqStateLayout.setVisibility(View.VISIBLE);
				kqSurveyStateText.setVisibility(View.VISIBLE);
				kqSurveyStateText.setText("未按时到校");
				kqStateText.setText("请假");
				if(a.getCanEdit()==0){
					kqSurveyStateLayout.setVisibility(View.GONE);
					kqStateOperate.setVisibility(View.VISIBLE);
					kqStateOperate.setText("取消请假");
				}else {
					kqSurveyStateLayout.setVisibility(View.VISIBLE);
					kqStateOperate.setVisibility(View.GONE);
				}
				if(TextUtils.isEmpty(a.getReason()) || "null".equals(a.getReason())) {
					kqStateReason.setVisibility(View.GONE);
				}else {
					kqStateReason.setVisibility(View.VISIBLE);
					kqStateReason.setText(a.getReason());
				}
			}else if(a.getState() == 3) {//缺勤
				kqSurveyStateLayout.setVisibility(View.VISIBLE);
				kqStateLayout.setVisibility(View.INVISIBLE);
				kqSurveyStateText.setVisibility(View.VISIBLE);
				kqSurveyStateText.setText("缺勤");
			}
			if(TextUtils.isEmpty(a.getPhoto()) || "null".equals(a.getPhoto())) {
				kqSurveyStatePhoto.setVisibility(View.INVISIBLE);
			}else {
				kqSurveyStatePhoto.setVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}
