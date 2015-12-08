package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Subject;
import com.linkage.mobile72.sh.fragment.ScoreFragment;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.lib.util.LogUtils;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.Consts;

public class ScoreActivity extends BaseActivity implements OnClickListener {

	public static String TAG = ScoreActivity.class.getSimpleName();

	public static final String SUBJECT_ID = "subject_id";
	public static final String SUBJECT_NAME = "subject_name";

	private int current_tab = 1;// 默认显示的为第一个标签页

	private FragmentManager fragmentManager;
	private RelativeLayout category_bar2;
	public RadioGroup topBtns;
	private Button leftButton, rightButton;
	private HorizontalScrollView scrollView;
	private List<Subject> categoryList = new ArrayList<Subject>();

	private ProgressDialog mProgressDialog;

	// private List<Subject> subjectList;

	// private List<CategoryItem> categoryViewList = new
	// ArrayList<CategoryItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);

		setTitle(R.string.score);
		Button setButton = (Button) findViewById(R.id.set);
		setButton.setText("成绩信箱");
		setButton.setVisibility(View.VISIBLE);
		fragmentManager = getSupportFragmentManager();
		category_bar2 = (RelativeLayout) findViewById(R.id.category_bar2);
		leftButton = (Button) findViewById(R.id.button_left);
		rightButton = (Button) findViewById(R.id.button_right);
		topBtns = (RadioGroup) findViewById(R.id.new_top_rg);
		scrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
		findViewById(R.id.back).setOnClickListener(this);
		setButton.setOnClickListener(this);
		// ((MenuActivity) getParent()).addIgnoreView(topBtns);

		categoryList.clear();

		topBtns.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				LogUtils.d(" ---------------------->checkedId=" + checkedId);
				current_tab = checkedId;

				long sbjId = -1;
				String sbjName = "";

				if (current_tab >= 0 && current_tab < categoryList.size()) {
					sbjId = categoryList.get(current_tab).getId();
					sbjName = categoryList.get(current_tab).getName();
				} else {
					sbjId = -1;
					LogUtils.e("invalid current_tab=" + current_tab);
				}

				LogUtils.d("sbjId = " + sbjId);
				showResFragment(sbjId, sbjName);
			}
		});

		mProgressDialog = new ProgressDialog(ScoreActivity.this);
		// makeDemo();
		// fetchSubjectData();

		try {

			categoryList = getDBHelper().getSubjectDao().queryForAll();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		if (categoryList == null || categoryList.size() <= 0) {

			fetchSubjectData();

		} else {

			updateCategory();

		}
		
		scrollView.setOnTouchListener(new TouchListenerImpl());
		leftButton.setVisibility(View.GONE);
	}

	private class TouchListenerImpl implements OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
 
                break;
            case MotionEvent.ACTION_MOVE:
                 int scrollX = view.getScrollX();
                 int width = view.getWidth();
                 int scrollViewMeasuredHeight = scrollView.getChildAt(0).getMeasuredWidth();
                 if(scrollX < 30){
                	 leftButton.setVisibility(View.GONE);
                 }
                 if(scrollX > 40){
                	 leftButton.setVisibility(View.VISIBLE);
                 }
                 if((scrollX + width) > scrollViewMeasuredHeight - 10){
                	 rightButton.setVisibility(View.GONE);
                 }
                 if((scrollX + width) < scrollViewMeasuredHeight - 20){
                	 rightButton.setVisibility(View.VISIBLE);
                 }
                break;
 
            default:
                break;
            }
            return false;
        }
         
    };
    
	private void showResFragment(long sbjId, String sbjName) {

		ScoreFragment fragment = new ScoreFragment();
		Bundle nBundle = new Bundle();
		nBundle.putLong(SUBJECT_ID, sbjId);
		nBundle.putString(SUBJECT_NAME, sbjName);

		fragment.setArguments(nBundle);

		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.container, fragment);
		fragmentTransaction.commitAllowingStateLoss();

		LogUtils.e("showResFragment sbjId:" + sbjId + " sbjName:" + sbjName);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.set:
			Intent mIntent = new Intent(this, JxHomeworkListActivity2.class);
			mIntent.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
					JxHomeworkListActivity.SMSMESSAGETYPE_SCORE);
			startActivity(mIntent);
			break;
		}

	}

	private void makeDemo() {
		Subject sbj;
		for (int i = 0; i < 12; i++) {
			sbj = new Subject();
			sbj.setId(i);
			sbj.setName("科目" + i);

			categoryList.add(sbj);
		}

		updateCategory();
		
		
	}

	private void updateCategory() {
		if(categoryList == null || categoryList.size() <= 0) {
			category_bar2.setVisibility(View.GONE);
		}else {
			category_bar2.setVisibility(View.VISIBLE);
			RadioButton btn = (RadioButton) LayoutInflater.from(ScoreActivity.this).inflate(R.layout.sc_top_rbtn, null);
			btn.setText(categoryList.get(0).getName());
			btn.setId(0);
			btn.setChecked(true);
			topBtns.addView(btn);
	
			RadioButton btn1;
	
			// 更新分类显示
			for (int i = 1; i < categoryList.size(); i++) {
	
				btn1 = (RadioButton) LayoutInflater.from(ScoreActivity.this)
						.inflate(R.layout.sc_top_rbtn, null);
				String subjectName = categoryList.get(i).getName();
				if(subjectName != null && subjectName.length() > 4) {
					subjectName = subjectName.substring(0,4) + "...";
				}
				btn1.setText(subjectName);
				btn1.setId(i);
				topBtns.addView(btn1);
			}
			btn.performClick();
		}
	}

	private void fetchSubjectData() {
		// mProgress.setVisibility(View.VISIBLE);

		mProgressDialog.setMessage(ScoreActivity.this.getResources().getString(
				R.string.fetch_sbj_data));
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getSubjectList");

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_getSubjectList, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// mProgress.setVisibility(View.GONE);
						mProgressDialog.dismiss();
						LogUtils.i(" response=" + response);
						if (response.optInt("ret") == 0) {
							categoryList.clear();
							JSONArray array = response.optJSONArray("data");

							if (array != null && array.length() > 0) {

								delLoalData();

								for (int i = 0; i < array.length(); i++) {

									JSONObject obj = array.optJSONObject(i);
									Subject subject = new Subject();
									subject.setId(obj.optLong("id"));
									subject.setName(obj.optString("name"));
									categoryList.add(subject);

									try {
										getDBHelper().getSubjectDao().create(
												subject);
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}

								updateCategory();

							} else {

								LogUtils.d("array empty!");
							}

						} else {
							T.showShort(ScoreActivity.this,
									response.optString("msg"));
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {

						mProgressDialog.dismiss();
						StatusUtils.handleError(arg0, ScoreActivity.this);

					}
				});

		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	private boolean delLoalData() {
		boolean ret = true;

		try {

			DeleteBuilder<Subject, Integer> deleteSubjectBuilder = getDBHelper()
					.getSubjectDao().deleteBuilder();
			deleteSubjectBuilder.delete();

		} catch (SQLException e) {
			e.printStackTrace();

			ret = false;
		}

		return ret;

	}

}
