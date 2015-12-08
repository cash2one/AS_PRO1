package com.linkage.mobile72.sh.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.linkage.ui.widget.PullToRefreshScrollView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.PaymentBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;

public class PaymentDetailActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = PaymentDetailActivity.class.getSimpleName();
	
	private PaymentBean pb;
	private PullToRefreshScrollView pullScrollView;
	private ImageView avatar;
	private long relProjectClassId;
	private TextView paySchool, payName, payMoney, payDesc, payStart, payEnd;
	private RelativeLayout unionPay,aliPay;
	private Button payRefuse;
	private MyCommonDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_detail);
		pb = (PaymentBean)getIntent().getExtras().getSerializable("PB");
		if(pb == null) {
			finish();
		}
		
		setTitle(pb.getProjectName() + " 详情");
		((Button)findViewById(R.id.back)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		pullScrollView = (PullToRefreshScrollView)findViewById(R.id.pullScrollView);
		avatar = (ImageView)findViewById(R.id.pay_picture);
		paySchool = (TextView)findViewById(R.id.pay_school_class);
		payName = (TextView)findViewById(R.id.pay_name);
		payMoney = (TextView)findViewById(R.id.pay_money);
		payDesc = (TextView)findViewById(R.id.pay_desc);
		payStart = (TextView)findViewById(R.id.pay_start);
		payEnd = (TextView)findViewById(R.id.pay_end);
		unionPay = (RelativeLayout)findViewById(R.id.pay_unionpay);
		aliPay = (RelativeLayout)findViewById(R.id.pay_alipay);
		payRefuse = (Button)findViewById(R.id.pay_reject);
		
		paySchool.setText(pb.getSchoolName() + "  " + pb.getClassroomName());
		imageLoader.displayImage(Consts.SERVER_IP + pb.getProjectTypePicture(), avatar);
		payName.setText(pb.getProjectName());
		payMoney.setText("￥" + pb.getMoney());
		payDesc.setText(pb.getDescription());
		payStart.setText(StringUtils.format(pb.getStartDate(), "yyyy-MM-dd", "yyyy.MM.dd"));
		payEnd.setText(StringUtils.format(pb.getExpirationDate(), "yyyy-MM-dd", "yyyy.MM.dd"));
		
		relProjectClassId = pb.getProjectId();
		findPaymentDetail(relProjectClassId);
		pullScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				findPaymentDetail(relProjectClassId);
			}
		});
		
	}
	
	private void findPaymentDetail(long projectId) {
		ProgressDialogUtils.showProgressDialog("", this, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("relProjectClassId", String.valueOf(projectId));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_PaymentDetail, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				pullScrollView.onRefreshComplete();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					//解析
					//List<ClassInfoBean> clazzs = ClassInfoBean.parseFromJson(response.optJSONArray("data"));
					//if(clazzs!=null&&clazzs.size()>0) {
					//	clazz = clazzs.get(0);
					//}
					pb = PaymentBean.parseFromJson(response.optJSONObject("data"));
					if(pb != null) {
						paySchool.setText(pb.getSchoolName() + "  " + pb.getClassroomName());
						imageLoader.displayImage(Consts.SERVER_IP + pb.getProjectTypePicture(), avatar);
						payName.setText(pb.getProjectName());
						payMoney.setText("￥" + pb.getMoney());
						payDesc.setText(StringUtils.isEmpty(pb.getDescription()) ? "暂无缴费描述说明" : pb.getDescription());
						payStart.setText(StringUtils.format(pb.getStartDate(), "yyyy-MM-dd", "yyyy.MM.dd"));
						payEnd.setText(StringUtils.format(pb.getExpirationDate(), "yyyy-MM-dd", "yyyy.MM.dd"));
						
						if(pb.getPayState() != 0) {
							unionPay.setVisibility(View.INVISIBLE);
							aliPay.setVisibility(View.INVISIBLE);
							payRefuse.setText("已支付");
							payRefuse.setClickable(false);
						}
						payRefuse.setOnClickListener(PaymentDetailActivity.this);
					}
				} 
				else {
					StatusUtils.handleStatus(response, PaymentDetailActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				pullScrollView.onRefreshComplete();
				StatusUtils.handleError(arg0, PaymentDetailActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	private void refusePay(long projectId) {
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
		ProgressDialogUtils.showProgressDialog("", this, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("relProjectClassId", String.valueOf(projectId));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_PaymentRefuse, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				pullScrollView.onRefreshComplete();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					//解析
					UIUtilities.showToast(PaymentDetailActivity.this, "缴费已忽略");
					unionPay.setVisibility(View.INVISIBLE);
					aliPay.setVisibility(View.INVISIBLE);
					payRefuse.setText("已忽略");
					payRefuse.setClickable(false);
				} 
				else {
					StatusUtils.handleStatus(response, PaymentDetailActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				pullScrollView.onRefreshComplete();
				StatusUtils.handleError(arg0, PaymentDetailActivity.this);
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.pay_reject:
			dialog = new MyCommonDialog(this, "提示消息", "确认要忽略这次缴费吗？", "关闭", "确认忽略");
			dialog.setOkListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					refusePay(relProjectClassId);
				}
			});
			dialog.setCancelListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(dialog.isShowing())
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		default:
			break;
		}
	}
}
