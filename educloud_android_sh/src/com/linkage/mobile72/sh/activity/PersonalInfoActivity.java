package com.linkage.mobile72.sh.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.im.NewChatActivity;
import com.linkage.mobile72.sh.adapter.PersonalInfoAdapter;
import com.linkage.mobile72.sh.adapter.PersonalInfoStrangerAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.Person;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.widget.ListViewForScrollView;
import com.linkage.mobile72.sh.widget.MyEditDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.mobile72.sh.Consts.ChatType;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshScrollView;

public class PersonalInfoActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = PersonalInfoActivity.class.getSimpleName();
	private static final int EDIT_PERSONAL_INFO = 1000;
	private AccountData account;
	private Person person;
	private Button back, editBtn, addFriendBtn;
	private CircularImage userAvatar;
	private PullToRefreshScrollView scrollView;
	private RelativeLayout accountLayout, mailLayout, sexLayout, phoneLayout;
	private TextView titleText, userName, userRole, applyToTeacher, accountText, mailText, sexText,
			phoneText;
	// private TextView schoolTypeText, schoolText, gradeText;
	private long userId;
	private ListViewForScrollView childInfoListView;
	private PersonalInfoStrangerAdapter mAdapter;
	private PersonalInfoAdapter nAdapter;
	private RelativeLayout childInfoLayout, addFriendLayout;
	private MyEditDialog addFriendDialog;
	private long type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		account = getCurAccount();
		Intent intent = getIntent();
		if (intent != null) {
			userId = intent.getLongExtra("id", 0);
			type = intent.getLongExtra("type", 0);
		}
		setContentView(R.layout.activity_personal_info);
		titleText = (TextView) findViewById(R.id.title);
		titleText.setText(R.string.title_person_info);
		back = (Button) findViewById(R.id.back);
		editBtn = (Button) findViewById(R.id.set);
		addFriendBtn = (Button) findViewById(R.id.add_friend_btn);

		userAvatar = (CircularImage) findViewById(R.id.user_avater);
		userName = (TextView) findViewById(R.id.user_name);
		userRole = (TextView) findViewById(R.id.user_role);

		accountLayout = (RelativeLayout) findViewById(R.id.personal_info_account_layout);
		mailLayout = (RelativeLayout) findViewById(R.id.personal_info_mail_layout);
		sexLayout = (RelativeLayout) findViewById(R.id.personal_info_sex_layout);
		phoneLayout = (RelativeLayout) findViewById(R.id.personal_info_phone_layout);

		accountText = (TextView) findViewById(R.id.personal_info_account_text);
		mailText = (TextView) findViewById(R.id.personal_info_mail_text);
		sexText = (TextView) findViewById(R.id.personal_info_sex_text);
		phoneText = (TextView) findViewById(R.id.personal_info_phone_text);

		LogUtils.e("userId:" + userId + " account.getUserId():" + account.getUserId());
		if (account != null && userId == account.getUserId()) {
			editBtn.setVisibility(View.VISIBLE);
			addFriendBtn.setVisibility(View.GONE);
		} else {
			editBtn.setVisibility(View.GONE);
			addFriendBtn.setVisibility(View.VISIBLE);
		}

		scrollView = (PullToRefreshScrollView) findViewById(R.id.pullScrollView);
		childInfoLayout = (RelativeLayout) findViewById(R.id.info_child_list_layout);
		addFriendLayout = (RelativeLayout) findViewById(R.id.info_add_friend_layout);

		applyToTeacher = (TextView) findViewById(R.id.apply_to_teacher);
		back.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		userAvatar.setOnClickListener(this);
		addFriendBtn.setOnClickListener(this);
		fetchData();
		scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		scrollView.getRefreshableView().smoothScrollTo(0, 0);
	}

	private void fetchData() {
		ProgressDialogUtils.showProgressDialog("", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "findUserMassge");
		params.put("userId", String.valueOf(userId == 0 ? account.getUserId() : userId));

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
							person = Person.parseFromJson(response.optJSONObject("data"));
							fillDataToPage(person);
						} else {
							ProgressDialogUtils.dismissProgressBar();
							StatusUtils.handleStatus(response, PersonalInfoActivity.this);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	private void fillDataToPage(Person person) {
		if (person != null) {
			LogUtils.e("person.getUseravatar():" + "" + person.getUseravatar());
			imageLoader.displayImage(person.getUseravatar(), userAvatar);
			userName.setText(person.getName());
			userRole.setText(person.getUsertype().equals("1") ? "教师" : "家长");
			sexText.setText(person.getSex().equals("1") ? "男" : "女");
            phoneText.setText(person.getPhone());
			if (getCurAccount().getUserId() == userId && person.getUsertype().equals("0")) {
				applyToTeacher.setVisibility(View.VISIBLE);
				applyToTeacher.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
				applyToTeacher.setOnClickListener(this);
			}
			applyToTeacher.setVisibility(View.INVISIBLE);
			accountText.setText(person.getAccountID());
			mailText.setText(person.getEmail());
			// schoolTypeText.setText(person.getEductionalystme());
			// schoolText.setText(person.getSchool());
			// gradeText.setText(person.getGrade());
			/** Ruf_auditStatus = 0 不是好友 1是好友 2是待审核 **/
			if (userId == getCurAccount().getUserId()) {
				addFriendLayout.setVisibility(View.GONE);
				mailLayout.setVisibility(View.VISIBLE);
				phoneLayout.setVisibility(View.VISIBLE);
			} else {
				if (account != null && userId == account.getUserId()) {
					mailLayout.setVisibility(View.VISIBLE);
					phoneLayout.setVisibility(View.VISIBLE);
				}else{
				if (person.getRuf_auditStatus() == 0) {// 不是自己 看别人 而且是没小孩的
					addFriendLayout.setVisibility(View.VISIBLE);
					addFriendBtn.setText("申请加为好友");
					addFriendBtn.setEnabled(true);
				} else if (person.getRuf_auditStatus() == 1) {
					addFriendLayout.setVisibility(View.VISIBLE);
					addFriendBtn.setText("发消息");
					addFriendBtn.setEnabled(true);
					mailLayout.setVisibility(View.VISIBLE);
					phoneLayout.setVisibility(View.VISIBLE);
				} else {
					addFriendLayout.setVisibility(View.VISIBLE);
					addFriendBtn.setText("等待对方验证");
					addFriendBtn.setEnabled(false);
				}
				}
			}
			if (person.getStudentList() != null && person.getStudentList().size() > 0) {
				childInfoLayout.setVisibility(View.VISIBLE);
				childInfoListView = (ListViewForScrollView) findViewById(R.id.child_info_list);
				/*
				 * childInfos = new ArrayList<ChildInfo>(); childInfo = new
				 * ChildInfo(); for (int i = 0; i <
				 * person.getStudentList().size(); i++) {
				 * childInfo.setName("张三"); childInfo.setId("123456");
				 * childInfo.setImage("/static/ucenter/user/60000/0331.jpg");
				 * childInfo.setGrade("一年级"); childInfo.setSchoolName("南外");
				 * childInfo.setSchoolType("小学"); childInfos.add(child); }
				 */
				if (account != null && userId == account.getUserId()) {// 看自己
																		// 有小孩
					nAdapter = new PersonalInfoAdapter(person.getStudentList(), this, imageLoader);
					childInfoListView.setAdapter(nAdapter);
				} else {// 看别人 有小孩的
					mAdapter = new PersonalInfoStrangerAdapter(person.getStudentList(), this,
							imageLoader);
					childInfoListView.setAdapter(mAdapter);
				}
				childInfoListView.setDivider(null);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.set:
			Intent i = new Intent(PersonalInfoActivity.this, PersonalInfoEditActivity.class);
			if (person != null) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("PERSON", person);
				i.putExtras(bundle);
				startActivityForResult(i, EDIT_PERSONAL_INFO);
			}
			break;
		case R.id.user_avater:
			if (account != null && userId == account.getUserId()) {
				Intent intentEdit = new Intent(PersonalInfoActivity.this, PersonalInfoEditActivity.class);
				if (person != null) {
					Bundle bundle = new Bundle();
					bundle.putSerializable("PERSON", person);
					intentEdit.putExtras(bundle);
					startActivityForResult(intentEdit, EDIT_PERSONAL_INFO);
				}
			}
			break;
		case R.id.apply_to_teacher:
			Intent applyToTeacherIntent = new Intent(PersonalInfoActivity.this,
					ApplyToTeacherActivity.class);
			startActivity(applyToTeacherIntent);
			break;
		case R.id.add_friend_btn:
		    //是好友，进入聊天界面
			if (person != null) {
			    if(person.getRuf_auditStatus() == 1 )
			    {
			    	Intent intent = new Intent();
					intent.setClass(PersonalInfoActivity.this, ChatActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("chatid", userId + "");
					bundle.putInt("chattype", ChatType.CHAT_TYPE_SINGLE);
					bundle.putInt("type", 1);
					bundle.putString("name", person.getName());
					intent.putExtra("data", bundle);
					LogUtils.d( "contact starting chat----> buddyId=" + person.getName() + " chattype=" + ChatType.CHAT_TYPE_SINGLE
							+ " name=" + person.getName());
					startActivity(intent);
			    }
			    //不是好友，发送验证消息
			    else if(person.getRuf_auditStatus() == 0)
			    {
			        addFriendDialog = new MyEditDialog(this, 12, "验证消息", "", "取消", "确定");
		            addFriendDialog.setCancelListener(new OnClickListener() {
	
		                @Override
		                public void onClick(View v) {
		                    // TODO Auto-generated method stub
		                    addFriendDialog.dismiss();
		                }
		            });
		            addFriendDialog.setOkListener(new OnClickListener() {
	
		                @Override
		                public void onClick(View v) {
		                    // TODO Auto-generated method stub
		                    String applyReason = addFriendDialog.getEditView().getText().toString().trim();
		                    if (TextUtils.isEmpty(applyReason)) {
		                        // UIUtilities.showToast(PersonalInfoActivity.this,
		                        // "申请内容不能空");
		                        Toast.makeText(PersonalInfoActivity.this, "申请内容不能空", Toast.LENGTH_SHORT)
		                                .show();
		                    } else {
		                        add(userId, applyReason);
		                        addFriendDialog.dismiss();
		                    }
		                    // Toast.makeText(PersonalInfoActivity.this, "申请成功",
		                    // Toast.LENGTH_SHORT).show();
		                }
		            });
		            addFriendDialog.show();
			    }
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg1 == RESULT_OK) {
			switch (arg0) {
			case EDIT_PERSONAL_INFO:
				fetchData();
				break;
			default:
				break;
			}
		}
	}

	public void add(final long friendid, String reason) {
		ProgressDialogUtils.showProgressDialog("", PersonalInfoActivity.this, false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "addFriend");
		params.put("friendId", String.valueOf(friendid));
		params.put("applyReason", reason);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						System.out.println("response=" + response);
						if (response.optInt("ret") == 0) {
							UIUtilities.showToast(PersonalInfoActivity.this, "加好友申请成功");
							finish();
						} else {
							StatusUtils.handleStatus(response, PersonalInfoActivity.this);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, PersonalInfoActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);

	}
}
