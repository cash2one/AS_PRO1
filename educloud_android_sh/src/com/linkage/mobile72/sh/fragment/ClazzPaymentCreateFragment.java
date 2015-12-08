package com.linkage.mobile72.sh.fragment;

import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.PaymentTypeActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.data.http.PaymentTypeBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;

public class ClazzPaymentCreateFragment extends BaseFragment implements OnClickListener {

	private static final String TAG = ClazzPaymentCreateFragment.class.getSimpleName();
	
	private ClassInfoBean clazz;
	
	private RelativeLayout jfmcLayout,jfsjLayout;
	private TextView jfmc,jfsj;
	private EditText jfje,jfsm;
	private Button submit;
	private final int REQUEST_CHOOSE_TYPE = 1;
	private PaymentTypeBean ptb;
	
	private DatePickerDialog datePickerDialog;
	private Calendar calendar;
	private DatePickerDialog.OnDateSetListener dateListener;
	private String pickerDate;
	private String money;
	private MyCommonDialog dialog; 
	
	public static ClazzPaymentCreateFragment create(ClassInfoBean clazz) {
		ClazzPaymentCreateFragment f = new ClazzPaymentCreateFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("CLAZZ", clazz);
        f.setArguments(args);
        return f; 
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		clazz = getArguments()!=null? (ClassInfoBean)getArguments().getSerializable("CLAZZ") : null;
		if(clazz == null) {
			getActivity().finish();
		}
		calendar = Calendar.getInstance();
		dateListener =  new DatePickerDialog.OnDateSetListener() {
	        @Override 
	        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) { 
	            pickerDate = year + "-" + (month+1) + "-" + dayOfMonth;
	            System.out.println(pickerDate);
	            jfsj.setText(pickerDate);
	            jfsj.setTextColor(getResources().getColor(R.color.black));
	        }
	    }; 
	    datePickerDialog = new DatePickerDialog(getActivity(), 
                dateListener, 
                calendar.get(Calendar.YEAR), 
                calendar.get(Calendar.MONTH), 
                calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_payment_create, container, false);
		jfmcLayout = (RelativeLayout)view.findViewById(R.id.layout_jfmc);
		jfsjLayout = (RelativeLayout)view.findViewById(R.id.layout_jfsj);
		jfmc = (TextView)view.findViewById(R.id.text_jfmc);
		jfsj = (TextView)view.findViewById(R.id.text_jfsj);
		jfje = (EditText)view.findViewById(R.id.input_jfje);
		jfsm = (EditText)view.findViewById(R.id.input_jfsm);
		submit = (Button)view.findViewById(R.id.submit);
		
		jfmcLayout.setOnClickListener(this);
		jfsjLayout.setOnClickListener(this);
		submit.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == getActivity().RESULT_OK) {
			if(requestCode == REQUEST_CHOOSE_TYPE) {
				if(data != null) {
					Bundle b = data.getExtras();
					ptb = (PaymentTypeBean)b.getSerializable("PAY_TYPE");
					jfmc.setText(ptb.getTypeName());
					jfmc.setTextColor(getResources().getColor(R.color.black));
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.layout_jfmc:
			intent = new Intent(getActivity(), PaymentTypeActivity.class);
			startActivityForResult(intent, REQUEST_CHOOSE_TYPE);
			break;
		case R.id.layout_jfsj:
			datePickerDialog.show();
			break;
		case R.id.submit:
			money = jfje.getText().toString();
			if(ptb == null) {
				UIUtilities.showToast(getActivity(), "必须要选择一种缴费名称");
				return;
			}
			if(StringUtils.isEmpty(money)) {
				UIUtilities.showToast(getActivity(), "缴费金额不能为空");
				return;
			}
			if(StringUtils.isEmpty(pickerDate)) {
				UIUtilities.showToast(getActivity(), "缴费截止日期不能为空");
				return;
			}
			dialog = new MyCommonDialog(getActivity(), "提示消息", "班级缴费创建后不能更改，您是否确定创建？", "再看看", "确定");
			dialog.setOkListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					submit();
				}
			});
			dialog.setCancelListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(dialog.isShowing())
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		}
	}
	
	private void submit() {
		if(dialog != null && dialog.isShowing())
		dialog.dismiss();
		ProgressDialogUtils.showProgressDialog("", getActivity(), false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("name", ptb.getTypeName());
		params.put("classroomId", String.valueOf(clazz.getClassroomId()));
		params.put("classroomNames", clazz.getClassroomName());
		params.put("typeId", String.valueOf(ptb.getTypeId()));
		params.put("money", money);
		params.put("expirationDate", pickerDate);
		params.put("description", jfsm.getText().toString());
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_PaymentCreate, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					UIUtilities.showToast(getActivity(), "创建成功");
					
				} else {
					StatusUtils.handleStatus(response, getActivity());
				}
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
	
	
}
