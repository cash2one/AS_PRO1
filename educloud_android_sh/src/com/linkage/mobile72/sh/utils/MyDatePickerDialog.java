package com.linkage.mobile72.sh.utils;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.DatePicker;

public class MyDatePickerDialog extends DatePickerDialog {
	 public MyDatePickerDialog(Context context,
	            OnDateSetListener callBack, int year, int monthOfYear,
	            int dayOfMonth) {
	     
	        super(context, callBack, year, monthOfYear, dayOfMonth);
	    }
	 @Override
		protected void onCreate(Bundle savedInstanceState) {
			
			super.onCreate(savedInstanceState);
//			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}
	    @Override
	    public void onDateChanged(DatePicker view, int year, int month, int day) {
	        super.onDateChanged(view, year, month, day);
	        if(!isDateAfter(view)){
	            Calendar mCalendar = Calendar.getInstance();
	            view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
	        }
	    }
	    
	    private boolean isDateAfter(DatePicker tempView) {
	        Calendar mCalendar = Calendar.getInstance();
	        Calendar tempCalendar = Calendar.getInstance();
	        tempCalendar.set(tempView.getYear(), tempView.getMonth(), tempView.getDayOfMonth(), 0, 0, 0);
	        if(tempCalendar.after(mCalendar))
	            return true;
	        else 
	            return false;
	    }
}
