package com.linkage.mobile72.sh.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.NotifyReplyAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.NotifyReply;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.Consts;

public class NotifyReplyActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = NotifyReplyActivity.class.getName();
	
	private long id;
	private int type;//1 是已读未读列表 2是已回复未回复列表
	private ListView listView;
	private NotifyReplyAdapter adapter;
	private View headView;
	private List<NotifyReply> replies;
	private Button back;
	private String time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notify_reply_layout);
		Intent i = getIntent();
		if(i == null) {
			finish();
			return;
		}
		id = i.getLongExtra("id", 0);
		if(id == 0) {
			finish();
			return;
		}
		type = i.getIntExtra("type", 1);
		time = i.getStringExtra("time");
		setTitle("发送方式");
		listView = (ListView) findViewById(R.id.listview);
		back = (Button)findViewById(R.id.back);
	    back.setOnClickListener(this);
//		headView = this.getLayoutInflater().inflate(
//				R.layout.headview_notify_reply, null);
//		listView.addHeaderView(headView);
		replies = new ArrayList<NotifyReply>();
		adapter = new NotifyReplyAdapter(replies, this, type);
		listView.setAdapter(adapter);
		listView.setDivider(null);
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View view, int position,
//					long arg3) {
//				adapter.changeImageVisible(view, position);
//				
//			}
//		});
		getData();
	}
	
	@SuppressLint("SimpleDateFormat")
    private void getData() {
		HashMap<String, String> params = new HashMap<String, String>();
		String url = "";
		/*if(1 == type) {
			params.put("commandtype", "getMessageUnreadList");
			url = Consts.SERVER_getMessageUnreadList;
		}else {*/
			params.put("commandtype", "getMessageUnRePlyListNew");
			url = Consts.SERVER_getMessageUnRePlyList;
		//}
		params.put("id", ""+id);
		params.put("time", new SimpleDateFormat("yyyyMM").format(Utils.getDateFromDefString(time)));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(url,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							replies = NotifyReply.parseFromJson(response.optJSONArray("data"));
							adapter.addData(replies, false);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, NotifyReplyActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

}
