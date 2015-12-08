package com.linkage.mobile72.sh.activity.sports;

import java.util.HashMap;

import org.json.JSONObject;

import u.aly.bu;
import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Chronometer.OnChronometerTickListener;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.ProjectData;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

public class SingleTestActivity extends BaseActivity implements OnClickListener{

	private static final String TAG = SingleTestActivity.class.getSimpleName();
	private static final String PROJECT_DATA = "project_data";
	
	private CircularImage projectImg;
	private TextView projectName,projectStatus, projectResult, projectScore;
	private RelativeLayout edtLayout, timeLayout;
	
	private Chronometer chronometer;
	private Button startBtn, endBtn;
	
	private TextView typeText, unitText;
	private EditText resultEdt;
	private Button submitBtn;
	
	private ProjectData project;
	private int count;
	private int miss = 0;
	
	public static Intent getIntent(Context context, ProjectData project){
		Intent intent =  new Intent(context, SingleTestActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(PROJECT_DATA, project);
		intent.putExtras(bundle);
		return intent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_test);
		
		setTitle(R.string.sports_title);
		findViewById(R.id.back).setOnClickListener(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			project = (ProjectData) bundle.get(PROJECT_DATA);
		}
		initView();
	}
	
	private void initView(){
		projectImg = (CircularImage)findViewById(R.id.project_img);
		projectName = (TextView)findViewById(R.id.project_name);
		projectStatus = (TextView)findViewById(R.id.project_status);
		projectScore = (TextView)findViewById(R.id.project_score);
		projectResult = (TextView)findViewById(R.id.project_result);
		typeText = (TextView)findViewById(R.id.project_type_text);
		unitText = (TextView)findViewById(R.id.project_unit_text);
		submitBtn = (Button)findViewById(R.id.submit_btn);
		startBtn = (Button)findViewById(R.id.time_start);
		endBtn = (Button)findViewById(R.id.time_end);
		chronometer = (Chronometer)findViewById(R.id.chronometer);
		resultEdt = (EditText)findViewById(R.id.project_type_edt);
		edtLayout = (RelativeLayout)findViewById(R.id.edt_layout);
		timeLayout = (RelativeLayout)findViewById(R.id.time_layout);
		
		projectImg.setBackgroundResource(R.drawable.default_avatar);
		projectName.setText(project.getName());
		
		if (project.getType() == Consts.projectType.TIME) {
			timeLayout.setVisibility(View.VISIBLE);
		}else if (project.getType() == Consts.projectType.LONG || project.getType() == Consts.projectType.COUNT) {
			edtLayout.setVisibility(View.VISIBLE);
		}
		
		if (project.getType() == Consts.projectType.LONG) {
			typeText.setText("距离");
			unitText.setText(project.getUnit());
		}else if(project.getType() == Consts.projectType.COUNT){
			typeText.setText("数量");
			unitText.setText(project.getUnit());
		}
		submitBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				String result = String.valueOf(resultEdt.getText());
				projectResult.setText(result+project.getUnit());
				int count = commitResult(result, project.getId());
				projectScore.setText(count+"分");
				
			}
		});		
		chronometer.setText("00:00:00");
		chronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				chronometer.setText(FormatMiss(miss));
				miss++;
			}
		});
		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				miss = 0;
				chronometer.setBase(SystemClock.elapsedRealtime());
				chronometer.start();
				startBtn.setEnabled(false);
				endBtn.setEnabled(true);
			}
		});
		endBtn.setEnabled(false);
		endBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chronometer.stop(); 
				startBtn.setEnabled(true);
				endBtn.setEnabled(false);
//				int count = commitResult(result, project.getId());
//				projectScore.setText(count+"分");
			}
		});
	}
	
	private int commitResult(String result, long projectId){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "submitResults");
			params.put("subjectid", String.valueOf(projectId));
			params.put("type", String.valueOf(1));
			params.put("position", result);
			
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
					Consts.SERVER_URL_NEW, Request.Method.POST, params, true,
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
							StatusUtils.handleError(error, SingleTestActivity.this);
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
