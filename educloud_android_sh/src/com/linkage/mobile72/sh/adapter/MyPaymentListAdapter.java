package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.PaymentBean;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyPaymentListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<PaymentBean> payments;
	private ImageLoader imageLoader;
	
	public MyPaymentListAdapter(Context context, ImageLoader imageLoader, List<PaymentBean> payments) {
		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.payments = payments;
		this.imageLoader = imageLoader;
	}
	
	public void addAll(List<PaymentBean> data){
		if(this.payments != null) {
			this.payments.clear();
			this.payments.addAll(data);
		}else {
			this.payments = data;
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return payments.size();
	}
	
	@Override
	public PaymentBean getItem(int position) {
		// TODO Auto-generated method stub
		return payments.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return payments.get(position).getProjectId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.adapter_payment_list_item, parent, false);
		}
		ImageView avatar = (ImageView)convertView.findViewById(R.id.avatar);
		TextView school = (TextView)convertView.findViewById(R.id.pay_school_class);
		TextView payName = (TextView)convertView.findViewById(R.id.pay_name);
		TextView payTime = (TextView)convertView.findViewById(R.id.pay_time);
		TextView payMoney = (TextView)convertView.findViewById(R.id.pay_money);
		
		PaymentBean pb = getItem(position);
		if(pb != null) {
			imageLoader.displayImage(Consts.SERVER_IP + pb.getProjectTypePicture(), avatar);
			school.setText(pb.getClassroomName() + "  " + pb.getSchoolName());
			payName.setText(pb.getProjectName());
			payTime.setText(StringUtils.format(pb.getExpirationDate(), "yyyy-MM-dd", "yyyy.MM.dd") + " 截止");
			payMoney.setText("￥" + StringUtils.doubleTrans(pb.getMoney()));
		}
		return convertView;
	}

}
