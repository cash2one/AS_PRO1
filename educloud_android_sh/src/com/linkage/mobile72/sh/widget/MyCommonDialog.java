package com.linkage.mobile72.sh.widget;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.utils.StringUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class MyCommonDialog extends Dialog {

	private String title;
	
	private String contentText;
	private BaseAdapter mAdpater;
	private OnItemClickListener mItemClickListener;
	
	private String cancel;
	private String ok;
	private android.view.View.OnClickListener cancelListener;
	private android.view.View.OnClickListener okListener;
	
	
	public MyCommonDialog(Context context) {
		super(context, R.style.MyDialogStyleBottom);
	}
	
	public MyCommonDialog(Context context, String title, String contentText, String cancel, String ok) {
		this(context);
		this.title = title;
		this.contentText = contentText;
		this.cancel = cancel;
		this.ok = ok;
	}
	
	public MyCommonDialog(Context context, String title, BaseAdapter mAdpater, OnItemClickListener mItemClickListener, String cancel, String ok) {
		this(context);
		this.title = title;
		this.contentText = null;
		this.mAdpater = mAdpater;
		this.mItemClickListener = mItemClickListener;
		this.cancel = cancel;
		this.ok = ok;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_common_dialog);
		TextView titleTextView = (TextView)findViewById(R.id.title);
		TextView contentTextView = (TextView)findViewById(R.id.contentTextView);
		ListView contentListView = (ListView)findViewById(R.id.contentListView);
		contentListView.setDivider(null);
		Button cancelButton = (Button)findViewById(R.id.cancel);
		Button okButton = (Button)findViewById(R.id.ok);
		Button sureButton = (Button)findViewById(R.id.sure);
		titleTextView.setText(title);
		if(contentText != null) {
			contentListView.setVisibility(View.GONE);
			contentTextView.setVisibility(View.VISIBLE);
			contentTextView.setText(contentText);
		}else {
			contentTextView.setVisibility(View.GONE);
			contentListView.setVisibility(View.VISIBLE);
			contentListView.setAdapter(mAdpater);
			contentListView.setOnItemClickListener(mItemClickListener);
		}
		if(getCancelListener() == null || StringUtils.isEmpty(cancel)) {
			sureButton.setText(ok);
			cancelButton.setVisibility(View.GONE);
			okButton.setVisibility(View.GONE);
			sureButton.setVisibility(View.VISIBLE);
		}else {
			cancelButton.setText(cancel);
			okButton.setText(ok);
			cancelButton.setVisibility(View.VISIBLE);
			okButton.setVisibility(View.VISIBLE);
			sureButton.setVisibility(View.GONE);
		}
		cancelButton.setOnClickListener(getCancelListener());
		okButton.setOnClickListener(getOkListener());
		sureButton.setOnClickListener(getOkListener());
		setCanceledOnTouchOutside(false);
	}


	public android.view.View.OnClickListener getCancelListener() {
		return cancelListener;
	}

	public void setCancelListener(android.view.View.OnClickListener cancelListener) {
		this.cancelListener = cancelListener;
	}

	public android.view.View.OnClickListener getOkListener() {
		return okListener;
	}

	public void setOkListener(android.view.View.OnClickListener okListener) {
		this.okListener = okListener;
	}
	
	
}
