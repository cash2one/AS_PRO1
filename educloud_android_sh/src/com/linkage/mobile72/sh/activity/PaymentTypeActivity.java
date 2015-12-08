package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshListView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.PaymentTypeListAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.PaymentTypeBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;

public class PaymentTypeActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = PaymentTypeActivity.class.getSimpleName();
	
	private PullToRefreshListView listView;
	private List<PaymentTypeBean> pts;
	private PaymentTypeListAdapter mAdapter;
	private Button back;
	private TextView mEmpty;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_type);
		setTitle(R.string.title_payment_type);
		
		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(this);
		
		listView = (PullToRefreshListView)findViewById(R.id.base_pull_list);
		pts = new ArrayList<PaymentTypeBean>();
		mAdapter = new PaymentTypeListAdapter(this, imageLoader, pts);
		listView.setAdapter(mAdapter);
		listView.setDivider(getResources().getDrawable(R.drawable.seperate_line));
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mEmpty.setText("查无数据");
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchPaymentType(false);
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PaymentTypeBean pt = mAdapter.getItem(position-1);
				Intent it = new Intent();
				Bundle b = new Bundle();
				b.putSerializable("PAY_TYPE", pt);
                it.putExtras(b);
                setResult(RESULT_OK, it);  
                finish();
			}
		});
		fetchPaymentType(true);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}
	
    
    private void fetchPaymentType(boolean firstRefresh) {
    	if(firstRefresh) {
    		ProgressDialogUtils.showProgressDialog("", this, false);
    	}
    	HashMap<String, String> params = new HashMap<String, String>();

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_PaymentType, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				listView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					pts = PaymentTypeBean.parseFromJson(response.optJSONArray("data"));
					mAdapter.addAll(pts);
				} 
				else {
					ProgressDialogUtils.dismissProgressBar();
					StatusUtils.handleStatus(response, PaymentTypeActivity.this);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				listView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, PaymentTypeActivity.this);
			}
		});
    	BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }
    
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("CLAZZ", clazz);			
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
        	return true;
        }
        return false;
    }*/
}
