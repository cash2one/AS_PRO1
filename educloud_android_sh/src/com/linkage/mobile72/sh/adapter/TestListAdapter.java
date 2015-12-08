package com.linkage.mobile72.sh.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.ProjectData;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TestListAdapter extends BaseAdapter{

	private static final String TAG = TestListAdapter.class.getSimpleName();
	
	private ArrayList<ProjectData> lists;
    private Context mContext;
    private int miss = 0;
    private int count;
    
    public TestListAdapter(Context mContext, ArrayList<ProjectData> lists){
    	this.mContext = mContext;
    	this.lists = lists;
    }
	
	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.all_test_item,parent,false);
			holder = new ViewHolder();
			holder.projectImg = (CircularImage)convertView.findViewById(R.id.project_img);
			holder.projectName = (TextView)convertView.findViewById(R.id.project_name);
			holder.projectStatus = (TextView)convertView.findViewById(R.id.project_status);
			holder.projectScore = (TextView)convertView.findViewById(R.id.project_score);
			holder.projectResult = (TextView)convertView.findViewById(R.id.project_result);
			holder.checkboxStatusText = (TextView)convertView.findViewById(R.id.checkbox_status_text);
			holder.isUnfoldBox = (CheckBox)convertView.findViewById(R.id.checkbox_unfold);
			holder.mainlLayout = (RelativeLayout)convertView.findViewById(R.id.main_layout);
			holder.edtLayout = (RelativeLayout)convertView.findViewById(R.id.edt_layout);
			holder.timeLayout = (RelativeLayout)convertView.findViewById(R.id.time_layout);
			holder.typeText = (TextView)convertView.findViewById(R.id.project_type_text);
			holder.uniText = (TextView)convertView.findViewById(R.id.project_unit_text);
			holder.submitBtn = (Button)convertView.findViewById(R.id.submit_btn);
			holder.startBtn = (Button)convertView.findViewById(R.id.time_start);
			holder.endBtn = (Button)convertView.findViewById(R.id.time_end);
			holder.chronometer = (Chronometer)convertView.findViewById(R.id.chronometer);
			holder.resultEdt = (EditText)convertView.findViewById(R.id.project_type_edt);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ProjectData project = lists.get(position);
		holder.projectImg.setBackgroundResource(R.drawable.default_avatar);
		holder.projectName.setText(project.getName());
		holder.isUnfoldBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (buttonView.isChecked()) {
					holder.checkboxStatusText.setText("展开");
					holder.mainlLayout.setBackgroundColor(mContext.getResources().getColor(R.color.common_bg_color));
					if (project.getType() == Consts.projectType.TIME) {
						holder.timeLayout.setVisibility(View.VISIBLE);
					}else if (project.getType() == Consts.projectType.LONG || project.getType() == Consts.projectType.COUNT) {
						holder.edtLayout.setVisibility(View.VISIBLE);
					}
				}else {
					holder.checkboxStatusText.setText("收起");
					holder.mainlLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
					holder.timeLayout.setVisibility(View.GONE);
					holder.edtLayout.setVisibility(View.GONE);
				}
			}
		});
		if (position == 0) {
			holder.isUnfoldBox.setChecked(true);
		}
		if (project.getType() == Consts.projectType.LONG) {
			holder.typeText.setText("距离");
			holder.uniText.setText(project.getUnit());
		}else if(project.getType() == Consts.projectType.COUNT){
			holder.typeText.setText("数量");
			holder.uniText.setText(project.getUnit());
		}
		holder.submitBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				String result = String.valueOf(holder.resultEdt.getText());
				holder.projectResult.setText(result+project.getUnit());
				int count = commitResult(result, project.getId());
				holder.projectScore.setText(count+"分");
				
			}
		});		
		holder.chronometer.setText("00:00:00");
		holder.chronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				chronometer.setText(FormatMiss(miss));
				miss++;
			}
		});
		holder.startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				miss = 0;
				holder.chronometer.setBase(SystemClock.elapsedRealtime());
				holder.chronometer.start();
				holder.startBtn.setEnabled(false);
				holder.endBtn.setEnabled(true);
			}
		});
		holder.endBtn.setEnabled(false);
		holder.endBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.chronometer.stop(); 
				holder.startBtn.setEnabled(true);
				holder.endBtn.setEnabled(false);
//				int count = commitResult(result, project.getId());
//				holder.projectScore.setText(count+"分");
			}
		});
		return convertView;
	}
	
	class ViewHolder {
		public CircularImage projectImg;
		public TextView projectName;
		public TextView projectStatus;
		public TextView projectResult;
		public TextView projectScore;
		public CheckBox isUnfoldBox;
		public TextView checkboxStatusText;
		public RelativeLayout mainlLayout,edtLayout, timeLayout;
		public TextView typeText, uniText;
		public Button submitBtn;
		public EditText resultEdt;
		public Chronometer chronometer;
		public Button startBtn, endBtn;
	}
	
	private int commitResult(String result, long projectId){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "submitResults");
			params.put("subjectid", String.valueOf(projectId));
			params.put("type", String.valueOf(1));
			params.put("position", result);
			
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
					Consts.SERVER_URL, Request.Method.POST, params, true,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
//							System.out.println("response=" + response);
							LogUtils.e("submitResults.response=" + response);
							if (response.optInt("ret") == 0) {
								count = response.optInt("count");
							}
						}
					},new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							StatusUtils.handleError(error, mContext);
						}
					}
			);
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public static String FormatMiss(int miss){     
        String hh=miss/3600>9?miss/3600+"":"0"+miss/3600;
        String  mm=(miss % 3600)/60>9?(miss % 3600)/60+"":"0"+(miss % 3600)/60;
        String ss=(miss % 3600) % 60>9?(miss % 3600) % 60+"":"0"+(miss % 3600) % 60;
        return hh+":"+mm+":"+ss;      
    }

}
