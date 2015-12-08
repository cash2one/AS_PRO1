package com.linkage.mobile72.sh.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshListView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.PaymentDetailActivity;
import com.linkage.mobile72.sh.adapter.MyPaymentListAdapter;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.http.PaymentBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;

public class MyPaymentFragment extends BaseFragment {

	private static final String TAG = MyPaymentFragment.class.getSimpleName();
	
	protected static View mProgressBar;
	private PullToRefreshListView listView;
	private TextView mEmpty;
	private MyPaymentListAdapter mAdapter;
	private List<PaymentBean> mPayments;
	private int type;
	
	public static MyPaymentFragment create(int type) {
		MyPaymentFragment f = new MyPaymentFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("type", type);
        f.setArguments(args);
        return f; 
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		type = getArguments()!=null? getArguments().getInt("type") : 1;
		mPayments = new ArrayList<PaymentBean>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_my_payment, container, false);
		listView = (PullToRefreshListView)view.findViewById(R.id.base_pull_list);
		mEmpty = (TextView)view.findViewById(android.R.id.empty);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		mAdapter = new MyPaymentListAdapter(getActivity(), imageLoader, mPayments);
		listView.setAdapter(mAdapter);
		listView.setDivider(getResources().getDrawable(R.drawable.seperate_line));
		mEmpty.setText("暂无缴费");
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchPayMent(false, type);
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), PaymentDetailActivity.class);
				Bundle b = new Bundle();
				b.putSerializable("PB", mAdapter.getItem(position-1));
				intent.putExtras(b);
				startActivity(intent);
			}
		});
		fetchPayMent(true, type);
	}
	
	private void fetchPayMent(boolean firstRefresh, int type) {
		if(firstRefresh) {
    		ProgressDialogUtils.showProgressDialog("", getActivity(), false);
    	}
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("status", type == 1 ? "0" : "1");//status 0是未交 1是已交

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_PaymentList, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				listView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
				System.out.println("response=" + response);
				if (response.optInt("ret") == 0) {
					mPayments = PaymentBean.parseFromJson(response.optJSONArray("data"));
					if(mPayments.size() > 0) {
						mAdapter.addAll(mPayments);
					}
				} 
				else {
					ProgressDialogUtils.dismissProgressBar();
					StatusUtils.handleStatus(response, getActivity());
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				listView.onRefreshComplete();
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, getActivity());
			}
		});
    	BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
}
