package com.linkage.mobile72.sh.fragment;

import java.sql.SQLException;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.MyContactActivity;
import com.linkage.mobile72.sh.activity.MyJifenActivity;
import com.linkage.mobile72.sh.activity.NewWebViewActivity;
import com.linkage.mobile72.sh.activity.PersonalInfoActivity;
import com.linkage.mobile72.sh.activity.SetActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

public class MenuFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = MenuFragment.class.getName();
	private AccountData account;
	private CircularImage avatar;
	private TextView nickName, jfName;
	private RelativeLayout leftMenuSetLayout;
	private LinearLayout leftMenuAccountLayout;
	private RelativeLayout myContactLayout, leftMenuJifen,myYouDouLayout,myExpenseLayout;
	private Button sign_btn;
	private TextView myRights;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		account = getCurAccount();
	}

	@Override
	public void onResume() {
		super.onResume();
		account = getCurAccount();
		LogUtils.e("account.getAvatar():" + account.getAvatar());
		if (account != null)
		{
			imageLoader.displayImage(account.getAvatar(), avatar);
			nickName.setText(account.getUserName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_left_menu, null);
		avatar = (CircularImage) view.findViewById(R.id.user_avater);
		nickName = (TextView) view.findViewById(R.id.text_name);
		jfName = (TextView) view.findViewById(R.id.text_jifen);
		leftMenuSetLayout = (RelativeLayout) view.findViewById(R.id.layout_my_set);
		leftMenuAccountLayout = (LinearLayout) view.findViewById(R.id.left_menu_layout_account);
		leftMenuJifen = (RelativeLayout) view.findViewById(R.id.left_menu_jifen);
//		leftMenuTalk = (RelativeLayout) view.findViewById(R.id.layout_my_talk);
		myContactLayout = (RelativeLayout) view.findViewById(R.id.layout_my_contact);
		//myContactLayout.setBackgroundColor(getResources().getColor(R.color.left_menu_item_sel_bg));
		//leftMenuSetLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
		myYouDouLayout = (RelativeLayout) view.findViewById(R.id.layout_my_youdou);
		myExpenseLayout = (RelativeLayout) view.findViewById(R.id.layout_my_expense);
		myRights = (TextView) view.findViewById(R.id.text_my_rights);
		myYouDouLayout.setOnClickListener(this);
		myExpenseLayout.setOnClickListener(this);
		leftMenuSetLayout.setOnClickListener(this);
		leftMenuAccountLayout.setOnClickListener(this);
//		leftMenuTalk.setVisibility(View.GONE);
//		leftMenuTalk.setOnClickListener(this);
		myContactLayout.setOnClickListener(this);
		leftMenuJifen.setOnClickListener(this);
		imageLoader.displayImage(account.getAvatar(), avatar);

		sign_btn = (Button) view.findViewById(R.id.sign_btn);
		sign_btn.setOnClickListener(this);

		if (account != null) {
			LogUtils.e(account.getUserName() + "account.getIsSign()" + account.getIsSign());
			nickName.setText(account.getUserName());
			myRights.setVisibility(View.GONE);
			if (isTeacher()) {
				if (account.getIsSign() == 0) {
					sign_btn.setText("签到");
					sign_btn.setBackgroundResource(R.drawable.sign_green_gray);
					jfName.setText("积分:" + account.getCreditScore());
				}
				switch (account.getUserLevel()) {
				case 4:
					myRights.setText("金牌教师");
					Drawable drawable1 = getResources().getDrawable(R.drawable.menu_level_pic1);
					myRights.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, null, null);
					break;
				case 5:
					myRights.setText("银牌教师");
					Drawable drawable2 = getResources().getDrawable(R.drawable.menu_level_pic2);
					myRights.setCompoundDrawablesWithIntrinsicBounds (drawable2, null, null, null);
					break;
				}
			} else {
				myYouDouLayout.setVisibility(View.GONE);
				switch (account.getUserLevel()) {
				case 1:
					myRights.setText("金牌家长");
					Drawable drawable1 = getResources().getDrawable(R.drawable.menu_level_pic1);
					myRights.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, null, null);
					break;
				case 2:
					myRights.setText("银牌家长");
					Drawable drawable2 = getResources().getDrawable(R.drawable.menu_level_pic2);
					myRights.setCompoundDrawablesWithIntrinsicBounds (drawable2, null, null, null);
					break;
				case 3:
					myRights.setText("铜牌家长");
					Drawable drawable3 = getResources().getDrawable(R.drawable.menu_level_pic3);
					myRights.setCompoundDrawablesWithIntrinsicBounds (drawable3, null, null, null);
					break;
				}
			}
			sign_btn.setVisibility(View.GONE);
			jfName.setVisibility(View.GONE);
			leftMenuJifen.setVisibility(View.GONE);
			view.findViewById(R.id.left_menu_jifen_line).setVisibility(View.GONE);
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.left_menu_layout_account:
			intent = new Intent(getActivity(), PersonalInfoActivity.class);
			intent.putExtra("id", account.getUserId());
			intent.putExtra("type", 1);
			startActivity(intent);
			break;
		case R.id.layout_my_contact:
			intent = new Intent(getActivity(), MyContactActivity.class);
			// intent.putExtra("id", account.getUserId());
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
			startActivity(intent);
			//myContactLayout.setBackgroundColor(getResources().getColor(R.color.left_menu_item_sel_bg));
            //leftMenuSetLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
			break;
//		case R.id.layout_my_talk:
//			intent = new Intent(getActivity(), ClazzTalkActivity.class);
//			startActivity(intent);
//			break;
		case R.id.layout_my_set:
			intent = new Intent(getActivity(), SetActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
			startActivity(intent);
			//leftMenuSetLayout.setBackgroundColor(getResources().getColor(R.color.left_menu_item_sel_bg));
            //myContactLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
			break;
		case R.id.left_menu_jifen:
			intent = new Intent(getActivity(), MyJifenActivity.class);
			startActivity(intent);
			break;
		case R.id.sign_btn:

			if (account.getIsSign() == 0) {
				sign_btn.setEnabled(false);
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("commandtype", "signIn");

				WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
						Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								ProgressDialogUtils.dismissProgressBar();

								sign_btn.setEnabled(true);
								System.out.println("response=" + response);
								if (response.optInt("ret") == 0) {
									StatusUtils.handleOtherError("签到成功", getActivity());
									sign_btn.setText("已签到");
									sign_btn.setBackgroundResource(R.drawable.sign_gray);
									account.setIsSign(1);
									DataHelper helper = getDBHelper();
									try {
										helper.getAccountDao().createOrUpdate(account);
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (response.optInt("ret") == 1) {
									StatusUtils.handleOtherError("签到失败", getActivity());
								} else {
									ProgressDialogUtils.dismissProgressBar();
									StatusUtils.handleOtherError("签到失败", getActivity());
									// StatusUtils.handleStatus(response,
									// instance);
								}
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
								sign_btn.setEnabled(true);
								ProgressDialogUtils.dismissProgressBar();
								// StatusUtils.handleError(arg0, instance);
							}
						});
				BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);

			}
			break;
		case R.id.layout_my_youdou:
			intent = new Intent(getActivity(),NewWebViewActivity.class);
			intent.putExtra(NewWebViewActivity.KEY_TITLE, NewWebViewActivity.MY_YOUDOU);
			intent.putExtra(NewWebViewActivity.KEY_URL, Consts.MY_YOUDOU_URL);
//			intent.putExtra(NewWebViewActivity.KEY_TOKEN, "");
			getActivity().startActivity(intent);
			break;
		case R.id.layout_my_expense:
			intent = new Intent(getActivity(),NewWebViewActivity.class);
			intent.putExtra(NewWebViewActivity.KEY_TITLE, NewWebViewActivity.MY_EXPENSE);
			intent.putExtra(NewWebViewActivity.KEY_URL, Consts.MY_EXPENSE_URL);
//			intent.putExtra(NewWebViewActivity.KEY_TOKEN, "");
			getActivity().startActivity(intent);
			break;
		default:
			break;
		}
	}

}
