package com.linkage.mobile72.sh.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.utils.StringUtils;


public class CommonDialog extends Dialog {

	private String title;
	private Context context;
	private String cancel;
	private String ok;
	private android.view.View.OnClickListener cancelListener;
	private android.view.View.OnClickListener okListener;
	
	
	public CommonDialog(Context context) {
		super(context, R.style.MyDialogStyleBottom);
	}
	
	public CommonDialog(Context context, String msg, String title, String cancel, String ok) {
		this(context);
		this.context = context;
		this.title = title;
		this.cancel = cancel;
		this.ok = ok;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_dialog);
		TextView titleTextView = (TextView)findViewById(R.id.title);
		Button cancelButton = (Button)findViewById(R.id.cancel);
		Button okButton = (Button)findViewById(R.id.ok);
		titleTextView.setText(title);
		if(getCancelListener() == null || StringUtils.isEmpty(cancel)) {
			cancelButton.setVisibility(View.GONE);
			okButton.setText(ok);
			okButton.setVisibility(View.VISIBLE);
		}else {
			cancelButton.setText(cancel);
			okButton.setText(ok);
			cancelButton.setVisibility(View.VISIBLE);
			okButton.setVisibility(View.VISIBLE);
		}
		cancelButton.setOnClickListener(getCancelListener());
		okButton.setOnClickListener(getOkListener());
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
	
	@Override
	public void show() {
		super.show();
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); 
		Display d = windowManager.getDefaultDisplay();  //为获取屏幕宽、高  
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值  
		if(getCancelListener() == null || StringUtils.isEmpty(cancel)) {
			p.height = (int) (d.getHeight() * 0.2);
		}else {
			p.height = (int) (d.getHeight() * 0.3);   //高度设置为屏幕的0.3
		}
		p.width = (int) (d.getWidth() * 0.7);    //宽度设置为屏幕的0.7
		getWindow().setAttributes(p);     //设置生效  
	}
}
