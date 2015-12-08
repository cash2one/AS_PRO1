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
import com.linkage.mobile72.sh.data.http.PaymentTypeBean;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PaymentTypeListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<PaymentTypeBean> pts;
	private ImageLoader imageLoader;
	
	public PaymentTypeListAdapter(Context context, ImageLoader imageLoader, List<PaymentTypeBean> pts) {
		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.pts = pts;
		this.imageLoader = imageLoader;
	}
	
	public void addAll(List<PaymentTypeBean> pts) {
		if(this.pts != null) {
			this.pts.clear();
			this.pts.addAll(pts);
		}else {
			this.pts = pts;
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pts.size();
	}
	
	@Override
	public PaymentTypeBean getItem(int position) {
		// TODO Auto-generated method stub
		return pts.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return pts.get(position).getTypeId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.adapter_payment_type_list_item, parent, false);
		}
		ImageView avatar = (ImageView)convertView.findViewById(R.id.avatar);
		TextView name = (TextView)convertView.findViewById(R.id.name);
		TextView desc = (TextView)convertView.findViewById(R.id.desc);
		PaymentTypeBean pt = getItem(position);
		if(pt != null) {
			imageLoader.displayImage(Consts.SERVER_IP + pt.getPicture(), avatar);
			name.setText(pt.getTypeName());
			desc.setText(pt.getDescription());
		}
		return convertView;
	}

}
