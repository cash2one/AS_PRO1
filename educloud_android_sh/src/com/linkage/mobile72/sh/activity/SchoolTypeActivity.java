package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;

public class SchoolTypeActivity extends BaseActivity implements OnClickListener{
	private Button back;
	private ListView schoolList;
	private String[] getSchoolTypeInfo;
	private String[] getSchoolTypeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schooltype);
		
		back = (Button) findViewById(R.id.back);
		schoolList = (ListView) findViewById(R.id.list_localname);
		
		setTitle("选择学制");
		back.setOnClickListener(this);
		initView();
	}
	
	private void initView(){
		
		getSchoolTypeInfo = getResources().getStringArray(R.array.select_schoolType);
		getSchoolTypeId = getResources().getStringArray(R.array.schoolType_id);
		ArrayList<HashMap<String, String>> info = new ArrayList<HashMap<String,String>>();
		for(int i = 0;i<getSchoolTypeInfo.length;i++){
			HashMap<String, String> newline = new HashMap<String, String>();
			newline.put("schoolType", getSchoolTypeInfo[i]);
			info.add(newline);
		}
		schoolList.setDivider(null);
		schoolList.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(final int position, View view, ViewGroup arg2) {
				// TODO Auto-generated method stub
				view = LayoutInflater.from(SchoolTypeActivity.this).inflate(R.layout.item_list_single_text, null);
				LinearLayout selectlocal = (LinearLayout) view.findViewById(R.id.local_list_item);
				TextView infoshow = (TextView) view.findViewById(R.id.list_textshow);
				infoshow.setText(getSchoolTypeInfo[position]);
				
				selectlocal.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.putExtra("schoolTypeId", getSchoolTypeId[position]);
						intent.putExtra("schoolType", getSchoolTypeInfo[position]);
						SchoolTypeActivity.this.setResult(RESULT_OK, intent);
						finish();
					}
				});
				return view;
			}
			
			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return getSchoolTypeInfo.length;
			}
		});
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
}
