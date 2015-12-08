package com.linkage.mobile72.sh.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.widget.calendar.CalendarPickerView;
import com.linkage.mobile72.sh.widget.calendar.CalendarPickerView.OnInvalidDateSelectedListener;
import com.linkage.mobile72.sh.widget.calendar.CalendarPickerView.SelectionMode;

public class SchoolAttendanceActivity  extends BaseActivity implements View.OnClickListener {
	
	private static final String TAG = SchoolAttendanceActivity.class.getSimpleName();
	private CalendarPickerView babyLiveCalendar;
	private TextView tvCount;
	private TextView tvCountLeave;
	private TextView tvCountAbsenteeism;
	
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat format1 = new SimpleDateFormat("d");
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_school_attendance);
		
		setTitle("考勤");
		findViewById(R.id.back).setOnClickListener(this);
		
		tvCount = (TextView)findViewById(R.id.tvCount);
		tvCountLeave = (TextView)findViewById(R.id.tvCountLeave);
		tvCountAbsenteeism = (TextView)findViewById(R.id.tvCountAbsenteeism);
		
		String sText = "总人数：<font color='#74d245'>32</font>人";
		tvCount.setText(Html.fromHtml(sText));
		sText = "<font color='#e50d10'>3</font>人请假";
		tvCountLeave.setText(Html.fromHtml(sText));
		sText = "<font color='#f19149'>2</font>人缺勤";
		tvCountLeave.setText(Html.fromHtml(sText));
		
 	    Calendar nextYear = Calendar.getInstance();
 	    nextYear.add(Calendar.MONTH, 0);
 	    Calendar lastYear = Calendar.getInstance();
 	    lastYear.add(Calendar.MONTH, 0);
 		
 		babyLiveCalendar = (CalendarPickerView)findViewById(R.id.calendar_view);
 		babyLiveCalendar.init(lastYear.getTime(), nextYear.getTime())
 			.inMode(SelectionMode.MULTIPLE);//.withSelectedDate(new Date());
 		
 		babyLiveCalendar.setOnInvalidDateSelectedListener(new OnInvalidDateSelectedListener() {
 			
 			@Override
 			public void onInvalidDateSelected(Date date) {
 				// TODO Auto-generated method stub
 				String day = format.format(date);
 				
 				babyLiveCalendar.selectDate(date);
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
		}
	}

}
