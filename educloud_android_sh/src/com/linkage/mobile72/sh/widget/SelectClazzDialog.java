package com.linkage.mobile72.sh.widget;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.lib.util.LogUtils;

public class SelectClazzDialog extends Dialog {

	private Context context;
	private String cancel;
	private String ok;
	private android.view.View.OnClickListener cancelListener;
	private android.view.View.OnClickListener okListener;
	private ListView listSelector;

	private int checkNum = 0;
	private BaseAdapter baseAdapter;
	private List<ClassRoom> mClassRooms;

	public SelectClazzDialog(Context context) {
		super(context, R.style.MyDialogStyleBottom);
	}

	public SelectClazzDialog(Context context, List<ClassRoom> mClassRooms,
			String cancel, String ok) {
		this(context);
		this.mClassRooms = mClassRooms;
		this.context = context;
		this.cancel = cancel;
		this.ok = ok;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select_clazz);
		listSelector = (ListView) findViewById(R.id.list_selector);

		View buttonView = View.inflate(context, R.layout.item_selector_button,
				null);
		Button cancelButton = (Button) buttonView.findViewById(R.id.btn_cancel);
		cancelButton.setText(cancel);
		cancelButton.setOnClickListener(getCancelListener());
		Button okButton = (Button) buttonView.findViewById(R.id.btn_ok);
		okButton.setText(ok);
		okButton.setOnClickListener(getOkListener());
		listSelector.addFooterView(buttonView);
		listSelector.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				checkNum = position;
				baseAdapter.notifyDataSetChanged();
			}
		});
		baseAdapter = new BaseAdapter() {

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				if (convertView == null) {
					convertView = View.inflate(context,
							R.layout.item_selector_clazz, null);
				}

				TextView txtView = (TextView) convertView
						.findViewById(R.id.textView);
				txtView.setText(mClassRooms.get(position).getName());

				ImageView imgView = (ImageView) convertView
						.findViewById(R.id.imageView);
				if (checkNum == position) {
					imgView.setImageResource(R.drawable.dialog_selector_on);
				} else {
					imgView.setImageResource(R.drawable.dialog_selector_off);
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mClassRooms.size();
			}
		};
		listSelector.setAdapter(baseAdapter);
		setCanceledOnTouchOutside(false);
	}

	public int getCheckNum() {
		return checkNum;
	}

	public void setCheckNum(int checkNum) {
		this.checkNum = checkNum;
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

	@Override
	public void show() {
		super.show();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display d = windowManager.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.85); // 宽度设置为屏幕的0.7
		p.height = LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(p); // 设置生效
	}

}
