package com.linkage.mobile72.sh.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.InputFilter.LengthFilter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;

public class MyEditDialog extends Dialog {

	private String title;

	private EditText editView;
	private String editText;
	private int maxLength;
	private String cancel;
	private String ok;
	private android.view.View.OnClickListener cancelListener;
	private android.view.View.OnClickListener okListener;

	public MyEditDialog(Context context) {
		super(context, R.style.MyDialogStyleBottom);
	}

	public MyEditDialog(Context context, int maxLength, String title, String editText,
			String cancel, String ok) {
		this(context);
		this.maxLength = maxLength;
		this.title = title;
		this.editText = editText;
		this.cancel = cancel;
		this.ok = ok;
	}

	public EditText getEditView() {
		return editView;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_edit_dialog);
		TextView titleTextView = (TextView) findViewById(R.id.title);
		editView = (EditText) findViewById(R.id.editView);
		Button cancelButton = (Button) findViewById(R.id.cancel);
		Button okButton = (Button) findViewById(R.id.ok);
		
		titleTextView.setText(title);
		editView.setText(editText);
		cancelButton.setText(cancel);
		okButton.setText(ok);
		
		cancelButton.setOnClickListener(getCancelListener());
		okButton.setOnClickListener(getOkListener());
		setCanceledOnTouchOutside(false);
		
		InputFilter[] filters = { new LengthFilter(maxLength) };
		editView.setFilters(filters);
		
		Editable etext = editView.getText();
        Selection.setSelection(etext, etext.length());
	}

	public android.view.View.OnClickListener getCancelListener() {
		return cancelListener;
	}

	public void setCancelListener(
			android.view.View.OnClickListener cancelListener) {
		this.cancelListener = cancelListener;
	}

	public android.view.View.OnClickListener getOkListener() {
		return okListener;
	}

	public void setOkListener(android.view.View.OnClickListener okListener) {
		this.okListener = okListener;
	}

	public String getDialogInputText() {
		return editView.getText().toString();
	}
}
