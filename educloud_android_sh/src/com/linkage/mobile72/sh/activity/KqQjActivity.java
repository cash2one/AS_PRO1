package com.linkage.mobile72.sh.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.JudgeDate;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.widget.ScreenInfo;
import com.linkage.mobile72.sh.widget.WheelMain;
import com.linkage.mobile72.sh.Consts;

public class KqQjActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = KqQjActivity.class.getName();

	private Button back, set;
	private AlertDialog dateDialog;
	private SimpleDateFormat dateFormat, dateFormat2;
	private WheelMain wheelMain;
	private View timepickerview;
	private LinearLayout btnStart, btnEnd;
	private TextView textStart, textEnd;
	private String dayStart, dayEnd;
	private MyCommonDialog dialog;
	private EditText editText;
	private TextView editWordText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kaoqin_qj);
		setTitle("请假");
		back = (Button)findViewById(R.id.back);
		set = (Button)findViewById(R.id.set);
		set.setVisibility(View.VISIBLE);
		set.setText("提交");
		btnStart = (LinearLayout)findViewById(R.id.btn_start);
		btnEnd = (LinearLayout)findViewById(R.id.btn_end);
		textStart = (TextView)findViewById(R.id.text_start);
		textEnd = (TextView)findViewById(R.id.text_end);
		editText = (EditText)findViewById(R.id.edit_input);
		editWordText = (TextView)findViewById(R.id.edit_input_word);
		back.setOnClickListener(this);
		set.setOnClickListener(this);
		btnStart.setOnClickListener(this);
		btnEnd.setOnClickListener(this);
		dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		dateFormat2 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		dayStart = dateFormat.format(new Date());
		dayEnd = dayStart;
		textStart.setText(dayStart);
		textEnd.setText(dayEnd);
		editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				if(s.length() <= 40) {
					editWordText.setText("您可以输入"+(40-s.length())+"个字");
				}else {
					editWordText.setText("您可以输入"+(40-s.length())+"个字");
					editText.setText(s.subSequence(0, 40));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.btn_start:
			initWheelMain(dayStart);
			dateDialog = new AlertDialog.Builder(this)
			.setTitle("请选择请假开始日期")
			.setView(timepickerview)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dayStart = wheelMain.getTime();
					textStart.setText(wheelMain.getTime());
					if(dateDialog.isShowing()) {
						dateDialog.dismiss();
					}
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(dateDialog.isShowing()) {
						dateDialog.dismiss();
					}
				}
			})
			.show();
			break;
		case R.id.btn_end:
			initWheelMain(dayStart);
			dateDialog = new AlertDialog.Builder(this)
			.setTitle("请选择请假结束日期")
			.setView(timepickerview)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dayEnd = wheelMain.getTime();
					textEnd.setText(wheelMain.getTime());
					if(dateDialog.isShowing()) {
						dateDialog.dismiss();
					}
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(dateDialog.isShowing()) {
						dateDialog.dismiss();
					}
				}
			})
			.show();
			break;
		case R.id.set:
			try {
				final Date start = dateFormat.parse(dayStart);
				final Date end = dateFormat.parse(dayEnd);
				String n = dateFormat.format(new Date());
				Date today = dateFormat.parse(n);
				if(today.after(start)) {
					UIUtilities.showToast(this, "不能请今天之前的假");
					return;
				}
				if(start.after(end)) {
					UIUtilities.showToast(this, "结束时间不能超过开始时间");
					return;
				}
				if(TextUtils.isEmpty(editText.getText())) {
					UIUtilities.showToast(this, "需要填写请假原因");
					return;
				}
				if(editText.getText().toString().trim().equals("")) {
					UIUtilities.showToast(this, "需要填写请假原因");
					return;
				}
				dialog = new MyCommonDialog(this, "提示消息", "确认提交请假吗？", "取消", "确定");
                dialog.setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        sumbitQj(dateFormat2.format(start), dateFormat2.format(end), editText.getText().toString());
                    }
                });
                dialog.setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if(dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
			} catch (ParseException e) {
				e.printStackTrace();
				UIUtilities.showToast(this, "请假日期不正确");
			}
			break;
		}
	}
	
	private void initWheelMain(String date) {
		timepickerview = LayoutInflater.from(this).inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(this);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		Calendar calendar = Calendar.getInstance();
		if(JudgeDate.isDate(date, "yyyy-MM-dd")){
			try {
				calendar.setTime(dateFormat.parse(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year,month,day);
	}
	
	private void sumbitQj(String start, String end, String reason) {
		if(dialog.isShowing()) {
            dialog.dismiss();
        }
		ProgressDialogUtils.showProgressDialog("正在提交请假", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "createAttendance");
		params.put("studentid", String.valueOf(getDefaultAccountChild().getId()));
		params.put("fromDate", start);
		params.put("toDate", end);
		params.put("reason", reason);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
							UIUtilities.showToast(KqQjActivity.this, "您已完成请假");
							Intent it = new Intent();  
			                setResult(Activity.RESULT_OK, it);  
			                finish();
						}else {
							StatusUtils.handleStatus(response, KqQjActivity.this);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, KqQjActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}
