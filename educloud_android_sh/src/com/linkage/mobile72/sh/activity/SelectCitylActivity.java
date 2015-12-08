package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkage.ui.widget.PullToRefreshListView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.manager.ActivityMgr;

public class SelectCitylActivity extends Activity implements OnClickListener {

	private String localname;
	private Button back;
	private String[] getinfo;
	private PullToRefreshListView listlocalname;
    private HereAdapter mAdapter;
    private TextView mEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_listview);
		Intent it = getIntent();
		localname = it.getStringExtra("localname");
		viewinit();
	}

	private void viewinit() {
		// TODO Auto-generated method stub
		back = (Button) findViewById(R.id.back);
		listlocalname = (PullToRefreshListView) findViewById(R.id.base_pull_list);
		
		setTitle(localname);
		back.setOnClickListener(this);
		
		getinfo = getResources().getStringArray(R.array.select_localname);
		ArrayList<HashMap<String, String>> info = new ArrayList<HashMap<String,String>>();
		for(int i = 0;i<getinfo.length;i++){
			HashMap<String, String> newline = new HashMap<String, String>();
			newline.put("localname", getinfo[i]);
			info.add(newline);
		}
        mAdapter = new HereAdapter();
        listlocalname.setAdapter(mAdapter);
        listlocalname.setDivider(getResources().getDrawable(R.color.dark_gray));
        mEmpty = (TextView) findViewById(android.R.id.empty);
        mEmpty.setText("查无数据");
        if (mAdapter.isEmpty()) {
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mEmpty.setVisibility(View.GONE);
        }
	}

    class HereAdapter extends BaseAdapter {

        public HereAdapter() {

        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return getinfo.length;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(SelectCitylActivity.this).inflate(R.layout.item_list_single_text, null);
            LinearLayout selectlocal = (LinearLayout) convertView.findViewById(R.id.local_list_item);
            TextView infoshow = (TextView) convertView.findViewById(R.id.list_textshow);
            infoshow.setText(getinfo[position]);

            selectlocal.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    SelectCitylActivity.this.setResult(RESULT_OK,
                            new Intent().putExtra("local", getinfo[position]));
                    finish();
                }
            });
            return convertView;
        }
    }

	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        ActivityMgr.getInstance().removeActivity(this);
        super.onDestroy();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        ActivityMgr.getInstance().push(this);
    }
	
	

}
