package com.linkage.mobile72.sh.activity;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.data.ClassRoom;

public class ClazzChooseActivity extends BaseActivity implements
		OnClickListener {

	public static final String CURRENT_CLAZZ_KEY = "from_class_choose_activity";
	public static final int REQUEST_CODE = 0x000001;

	private Button mBack;
	private ListView mListView;

	private List<ClassRoom> mClassRoomList;
	private ClassRoom mCurrentClass;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clazz_choose);
		setTitle(R.string.title_clazz_choose);
		mBack = (Button) findViewById(R.id.back);
		mBack.setText(R.string.cancel);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mBack.setBackground(null);
		} else {
			mBack.setBackgroundDrawable(null);
		}
		mListView = (ListView) findViewById(R.id.listView);
		mBack.setOnClickListener(this);
		mClassRoomList = mApp.getAllClassRoom();

		final ChildAdapter adapter = new ChildAdapter();
		mListView.setAdapter(adapter);
		mListView.setDivider(null);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCurrentClass = adapter.getItem(position);
				Intent intent = new Intent();
				intent.putExtra(CURRENT_CLAZZ_KEY, mCurrentClass);
				setResult(REQUEST_CODE, intent);
				finish();
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

	class ChildAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mClassRoomList.size();
		}

		@Override
		public ClassRoom getItem(int position) {
			return mClassRoomList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(ClazzChooseActivity.this)
						.inflate(R.layout.item_list_single_text_center, null);
				viewHolder = new ViewHolder();
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.list_textshow);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			ClassRoom r = getItem(position);
			viewHolder.textView.setText(r.getName());
			//viewHolder.textView.setTextColor(Color.rgb(96, 205, 246));
			return convertView;
		}

		class ViewHolder {
			public TextView textView;
		}
	}
}
