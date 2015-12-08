package com.linkage.mobile72.sh.widget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.linkage.mobile72.sh.R;

/**
 * 日期时间选择控件
 * 
 * @author 大漠
 */
public class DateTimePickerDialog implements OnDateChangedListener, OnTimeChangedListener {
	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;

	public interface TimeSetListener {
		void setTime(String time);
	}

	/**
	 * 日期时间弹出选择框构
	 * 
	 * @param activity
	 *            ：调用的父activity
	 */
	public DateTimePickerDialog(Activity activity) {
		this.activity = activity;
	}

	public void init(DatePicker datePicker, TimePicker timePicker) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		initDateTime = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY)
				+ ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), this);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);  
		timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS); 
	}

	/**
	 * 弹出日期时间选择框
	 * 
	 * @param linearLayout
	 * @param dialogListener
	 * @param type
	 *            : 0为日期时间类型:yyyy-MM-dd HH:mm:ss 1为日期类型:yyyy-MM-dd
	 *            2为时间类型:HH:mm:ss
	 * @return
	 */
	public AlertDialog dateTimePicKDialog(final LinearLayout linearLayout, final TextView textView,
			int type, final TimeSetListener timeSetListener) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		switch (type) {
		case 1:
			new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
						int dayOfMonth) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(datePicker.getYear(), datePicker.getMonth(),
							datePicker.getDayOfMonth());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					dateTime = sdf.format(calendar.getTime());
					linearLayout.setVisibility(View.VISIBLE);
					textView.setText(dateTime);
				}
			}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
			return null;
		case 2:
			new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
				public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH,
							timePicker.getCurrentHour(), timePicker.getCurrentMinute());
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
					dateTime = sdf.format(calendar.getTime());
					linearLayout.setVisibility(View.VISIBLE);
					textView.setText(dateTime);
				}
			}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
			return null;
		default:
			LinearLayout dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(
					R.layout.date_time_picker, null);
			datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
			timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
			init(datePicker, timePicker);
			
			timePicker.setOnTimeChangedListener(this);

			ad = new AlertDialog.Builder(activity).setIcon(R.drawable.ic_launcher)
					.setTitle(initDateTime).setView(dateTimeLayout)
					.setPositiveButton("设置", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							timeSetListener.setTime(dateTime);
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// dateTimeTextEdite.setText("");
						}
					}).show();

			onDateChanged(null, 0, 0, 0);
			return ad;
		}
	}

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
				timePicker.getCurrentHour(), timePicker.getCurrentMinute());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateTime = sdf.format(calendar.getTime());
		ad.setTitle(dateTime);
	}
}