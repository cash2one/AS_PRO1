package com.linkage.mobile72.sh.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.utils.T;

public class AddVoteActivity extends BaseActivity implements OnClickListener {
	public static final String KEY_VOTEJSON = "options";

	private String[] INDEXS = { "A", "B", "C", "D", "E", "F", "G", "H", "I" };
	private Button commit, voteAddBtn;
	private int voteNumber;

	private ImageButton voteBtnDelete2, voteBtnDelete3, voteBtnDelete4, voteBtnDelete5,
			voteBtnDelete6, voteBtnDelete7, voteBtnDelete8, voteBtnDelete9;

	private View[] mVoteLayouts;
	private View[] mDeleteLayouts;
	private EditText[] mVoteEdits;
	private JSONArray mOptions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_vote);

		setTitle("投票");
		findViewById(R.id.back).setOnClickListener(this);
		commit = (Button) findViewById(R.id.set);
		commit.setText("提交");
		commit.setVisibility(View.VISIBLE);
		commit.setOnClickListener(this);

		// 获取前一个页面带过来的
		String mOptionsStr = getIntent().getStringExtra(KEY_VOTEJSON);

		voteNumber = 1;

		voteBtnDelete2 = (ImageButton) findViewById(R.id.vote_delete_2);
		voteBtnDelete3 = (ImageButton) findViewById(R.id.vote_delete_3);
		voteBtnDelete4 = (ImageButton) findViewById(R.id.vote_delete_4);
		voteBtnDelete5 = (ImageButton) findViewById(R.id.vote_delete_5);
		voteBtnDelete6 = (ImageButton) findViewById(R.id.vote_delete_6);
		voteBtnDelete7 = (ImageButton) findViewById(R.id.vote_delete_7);
		voteBtnDelete8 = (ImageButton) findViewById(R.id.vote_delete_8);
		voteBtnDelete9 = (ImageButton) findViewById(R.id.vote_delete_9);

		voteAddBtn = (Button) findViewById(R.id.vote_add_button);
		voteAddBtn.setOnClickListener(this);

		voteBtnDelete2.setOnClickListener(this);
		voteBtnDelete3.setOnClickListener(this);
		voteBtnDelete4.setOnClickListener(this);
		voteBtnDelete5.setOnClickListener(this);
		voteBtnDelete6.setOnClickListener(this);
		voteBtnDelete7.setOnClickListener(this);
		voteBtnDelete8.setOnClickListener(this);
		voteBtnDelete9.setOnClickListener(this);

		mVoteLayouts = new View[9];
		mVoteLayouts[0] = findViewById(R.id.layout_vote_1);
		mVoteLayouts[1] = findViewById(R.id.layout_vote_2);
		mVoteLayouts[2] = findViewById(R.id.layout_vote_3);
		mVoteLayouts[3] = findViewById(R.id.layout_vote_4);
		mVoteLayouts[4] = findViewById(R.id.layout_vote_5);
		mVoteLayouts[5] = findViewById(R.id.layout_vote_6);
		mVoteLayouts[6] = findViewById(R.id.layout_vote_7);
		mVoteLayouts[7] = findViewById(R.id.layout_vote_8);
		mVoteLayouts[8] = findViewById(R.id.layout_vote_9);

		mDeleteLayouts = new View[9];
		mDeleteLayouts[0] = findViewById(R.id.layout_delete_1);
		mDeleteLayouts[1] = findViewById(R.id.layout_delete_2);
		mDeleteLayouts[2] = findViewById(R.id.layout_delete_3);
		mDeleteLayouts[3] = findViewById(R.id.layout_delete_4);
		mDeleteLayouts[4] = findViewById(R.id.layout_delete_5);
		mDeleteLayouts[5] = findViewById(R.id.layout_delete_6);
		mDeleteLayouts[6] = findViewById(R.id.layout_delete_7);
		mDeleteLayouts[7] = findViewById(R.id.layout_delete_8);
		mDeleteLayouts[8] = findViewById(R.id.layout_delete_9);

		mVoteEdits = new EditText[9];
		mVoteEdits[0] = (EditText) findViewById(R.id.vote_edit_1);
		mVoteEdits[1] = (EditText) findViewById(R.id.vote_edit_2);
		mVoteEdits[2] = (EditText) findViewById(R.id.vote_edit_3);
		mVoteEdits[3] = (EditText) findViewById(R.id.vote_edit_4);
		mVoteEdits[4] = (EditText) findViewById(R.id.vote_edit_5);
		mVoteEdits[5] = (EditText) findViewById(R.id.vote_edit_6);
		mVoteEdits[6] = (EditText) findViewById(R.id.vote_edit_7);
		mVoteEdits[7] = (EditText) findViewById(R.id.vote_edit_8);
		mVoteEdits[8] = (EditText) findViewById(R.id.vote_edit_9);

		if (!TextUtils.isEmpty(mOptionsStr)) {
			try {
				mOptions = new JSONArray(mOptionsStr);
				initOptions();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// 根据前一个页面带来的值，初始化
	private void initOptions() {
		voteNumber = mOptions.length();
		for (int i = 0; i < voteNumber; i++) {
			JSONObject obj = (JSONObject) mOptions.opt(i);
			mVoteEdits[i].setText(obj.optString("content"));
			mVoteLayouts[i].setVisibility(View.VISIBLE);
		}
		if (voteNumber > 1) {
			mDeleteLayouts[voteNumber - 1].setVisibility(View.VISIBLE);
		}
		if (voteNumber == 9) {
			voteAddBtn.setVisibility(View.INVISIBLE);
		}
	}

	private void saveVote() {
		if (voteNumber < 2) {
			T.showShort(this, "至少输入两个选项");
			return;
		}

		mOptions = new JSONArray();
		for (int i = 0; i < voteNumber; i++) {
			String value = mVoteEdits[i].getText().toString().trim();
			if (!TextUtils.isEmpty(value)) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("id", INDEXS[i]);
					obj.put("content", value);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mOptions.put(obj);
			} else {
				T.showShort(this, "请输入选项信息");
				return;
			}
		}

		Intent it = new Intent();
		Bundle b = new Bundle();
		b.putString(KEY_VOTEJSON, mOptions.toString());
		it.putExtras(b);
		setResult(RESULT_OK, it);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.set:
			saveVote();
			break;
		case R.id.vote_delete_2:
		case R.id.vote_delete_3:
		case R.id.vote_delete_4:
		case R.id.vote_delete_5:
		case R.id.vote_delete_6:
		case R.id.vote_delete_7:
		case R.id.vote_delete_8:
		case R.id.vote_delete_9:
			voteNumber--;
			mDeleteLayouts[voteNumber - 1].setVisibility(View.VISIBLE);
			mVoteLayouts[voteNumber].setVisibility(View.GONE);
			if (voteNumber == 8) {
				voteAddBtn.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.vote_add_button:
			voteNumber++;
			mDeleteLayouts[voteNumber - 2].setVisibility(View.GONE);
			mVoteLayouts[voteNumber - 1].setVisibility(View.VISIBLE);
			mDeleteLayouts[voteNumber - 1].setVisibility(View.VISIBLE);
			if (voteNumber == 9) {
				voteAddBtn.setVisibility(View.INVISIBLE);
			}
			break;
		}
	}
}