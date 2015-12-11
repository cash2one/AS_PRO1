package com.linkage.app.main;

//import info.emm.messenger.IMClient;
//import info.emm.messenger.VYCallBack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.j256.ormlite.stmt.DeleteBuilder;
//import com.j256.ormlite.stmt.QueryBuilder;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.app.BaseActivity;
import com.linkage.app.BaseApplication;
import com.linkage.app.Consts;
import com.linkage.app.R;
import com.linkage.app.data.AccountData;
import com.linkage.app.utils.Utilities;
import com.linkage.app.widget.CircularImage;
import com.linkage.lib.util.LogUtils;
import com.linkage.lib.util.ProgressDialogUtils;
import com.linkage.lib.util.StatusUtils;
import com.linkage.lib.util.UIUtilities;
import com.linkage.lib.util.WDJsonObjectRequest;
//import com.linkage.lib.util.LogUtils;
//import com.linkage.mobile72.sh.Consts;
//import com.linkage.mobile72.sh.R;
//import com.linkage.mobile72.sh.activity.register.Register_SendCodeActivity;
//import com.linkage.mobile72.sh.activity.register.Reset_SendCodeActivity;
//import com.linkage.mobile72.sh.app.BaseActivity;
//import com.linkage.mobile72.sh.app.BaseApplication;
//import com.linkage.mobile72.sh.data.AccountChild;
//import com.linkage.mobile72.sh.data.AccountData;
//import com.linkage.mobile72.sh.datasource.DataHelper;
//import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
//import com.linkage.mobile72.sh.im.FileHelper;
//import com.linkage.mobile72.sh.utils.APK.APKUtils;
//import com.linkage.mobile72.sh.utils.Des3;
//import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
//import com.linkage.mobile72.sh.utils.StatusUtils;
//import com.linkage.mobile72.sh.utils.StringUtils;
//import com.linkage.mobile72.sh.utils.UIUtilities;
//import com.linkage.mobile72.sh.utils.Utilities;
//import com.linkage.mobile72.sh.widget.CircularImage;
//import com.morgoo.droidplugin.pm.PluginManager;

/**
 * 登录界面
 * 
 * @author Yao
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener/*, VYCallBack*/ {

	private static final String TAG = LoginActivity.class.getSimpleName();
//	private final int TYPE_TEACHER = 0;
//	private final int TYPE_PARENT = 1;
	private int userType;

	public static LoginActivity instance;
	private AccountData account;
	private String loginName;
	private String password;
	private CircularImage accountAvater;
	private ImageView accountDefaultAvater;
	private RelativeLayout mHeaderLayout;
	private LinearLayout mUsernameLayout, mPasswordLayout;
	private EditText mUsernameEdit, mPasswordEdit;
	private Button loginBtn;
	private TextView registerNew, forgetPwd, tvTips;
	private boolean defaultLogin;
	private ImageView account_image, password_image, imgDownUser;
	private String str_tmp;
	private String usrName;
	private AccountData user;
	
	private List<AccountData> historyAcountList = null;
	private CheckBox ckPass;

	private RadioGroup rGroupIsteacher;
	private RadioButton radioParent, radioTeacher;
	
	private PopupWindow namePop = null;
//	private NameAdapter nameAdapter;
	private String savePasswd;
	private boolean isDbPasswd = false;
//	private UserType userType = null;
//	List<UserType> rdTypeList = new ArrayList<UserType>();
	/**
	 * 跳转页面的handler what == 0 为广告 what == 1 为主页面
	 */
	private Handler startActivityHandle = new Handler(){
		
		public void handleMessage(Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			//广告
//			case 0:
//				intent.setClass(LoginActivity.this, AdvActivity.class);
//				startActivity(intent);
//				finish();
//				break;
			//主页面
//			case 1:

//				intent.setClass(LoginActivity.this, MainActivity.class);
//				intent.putExtra("extra_username", loginName);
//				intent.putExtra("extra_password", password);
//				intent.putExtra("extra_data", "{\"token\":\"16761b68366b2e0c592aab6ddd6a7c7c\",\"orgin\":2,\"monitorParam\":1,\n" +
//						"\"avatar\":\"http://121.41.62.98:9200/educloud_share/static/ucenter/user/60000/0102.jpg\",\"userId\":600000102,\"userLevel\":1,\"userType\":3,\n" +
//						"\"creditScoreEndtime\":\"20161231\",\"isSign\":0,\"studentData\":[{\"isActive\":0,\"phone\":null,\"sex\":0,\"school\":null,\"birthday\":null,\"remoteId\":0,\n" +
//						"\"xxtType\":1,\"classId\":0,\"kindred\":0,\"modifydDate\":null,\"name\":\"姚存果\",\"id\":600004497}],\n" +
//						"\"isChat\":\"[{\\\"type\\\":1,\\\"chat\\\":0},{\\\"type\\\":2,\\\"chat\\\":0},{\\\"type\\\":3,\\\"chat\\\":0},{\\\"type\\\":4,\\\"chat\\\":0},{\\\"type\\\":5,\\\"chat\\\":0},{\\\"type\\\":6,\\\"chat\\\":0}]\",\n" +
//						"\"loadingUrl\":\"http://121.41.62.98:9200/educloud_new/ucenter/activityBM/index/856\",\"loadingImg1\":\"http://121.41.62.98:9200/educloud_share/manage/push/ad/20151127/1448613882582.jpg\",\n" +
//						"\"loadingImg2\":\"http://121.41.62.98:9200/educloud_share/manage/push/ad/20151127/1448613882582_10.jpg\",\n" +
//						"\"loadingImg3\":\"http://121.41.62.98:9200/educloud_share/manage/push/ad/20151127/1448613882582_11.jpg\",\"loadingTime\":\"5\",\"satisfactionId\":\"0\",\"satisfactionTitle\":\"\",\n" +
//						"\"sidesViewConfig\":\"1,2,3,4,5\",\"versionReqInterval\":86400,\n" +
//						"\"officeSmsMenu\":0,\"loginReqInterval\":120,\"rollADReqInterval\":120,\"replyName\":\"姚存果的家长\",\"creditScore\":35,\"userName\":\"果果2\"}");
//				startActivity(intent);
//				finish();
//				break;
//			case 2:
//				fileHelper = new FileHelper(fileDownloadHandler);
//				new Thread() {
//					@Override
//					public void run() {
//						fileHelper.down_file(getCurAccount().getLoadingImg(),
//								BaseApplication.getInstance().getWorkspaceDownload().getPath(),
//								AdvActivity.LOADING_PIC_NAME + picName + ".jpg");
//					}
//				}.start();
//				break;
			}
		}
	};
	
//	private Handler fileDownloadHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case FileHelper.MESSAGE_OPEN_DIALOG:// 开始启动Dialog
//				break;
//			case FileHelper.MESSAGE_START:// 开始下载
//				break;
//			case FileHelper.MESSAGE_PROGRESS:// 正在下载
//				break;
//			case FileHelper.MESSAGE_STOP:// 下载结束
//				mApp.getSp().edit().putString(
//						AdvActivity.LOADING_PIC_HISTORY_LOCAL + getCurAccount().getUserId(), picName).commit();
//				mApp.getSp().edit().putString(
//						AdvActivity.LOADING_PIC_HISTORY_SERVER + getCurAccount().getUserId(), getCurAccount().getLoadingImg()).commit();
//				startActivityHandle.sendEmptyMessage(0);
//				break;
//			case FileHelper.MESSAGE_ERROR:
//				startActivityHandle.sendEmptyMessage(1);
//				break;
//			}
//			super.handleMessage(msg);
//		}
//	};

//	private FileHelper fileHelper;
	private String picName;
	
	private boolean isSavePassd = true;
	private CompoundButton.OnCheckedChangeListener savePassdListener = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton ck, boolean ischeck) {
//			LogUtils.i("passd ischeck = " + ischeck);
			isSavePassd = ischeck;
		}
	};
	
//	private void initTypeList()
//	{
//		UserType usType = new UserType();
//
//		usType.setUserType(Consts.UserType.TEACHER);
//		usType.setTypeName("教师");
//		rdTypeList.add(usType);
//
//		usType = new UserType();
//		usType.setUserType(Consts.UserType.PARENT);
//		usType.setTypeName("家长");
//		rdTypeList.add(usType);
//	}
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstance);
		instance = this;
		setContentView(R.layout.activity_login);
		account_image = (ImageView) findViewById(R.id.image1);
		password_image = (ImageView) findViewById(R.id.image2);
		accountAvater = (CircularImage) findViewById(R.id.user_avater);
		accountDefaultAvater = (ImageView) findViewById(R.id.user_avater_default);
		mHeaderLayout = (RelativeLayout) findViewById(R.id.layout_header);
		mUsernameLayout = (LinearLayout) findViewById(R.id.layout_phone);
		mPasswordLayout = (LinearLayout) findViewById(R.id.layout_password);
		mUsernameEdit = (EditText) findViewById(R.id.input_phone);
		mPasswordEdit = (EditText) findViewById(R.id.input_password);
		loginBtn = (Button) findViewById(R.id.login);
		registerNew = (TextView) findViewById(R.id.text_register);
		forgetPwd = (TextView) findViewById(R.id.text_forget1);
		
		rGroupIsteacher = (RadioGroup) findViewById(R.id.rdp);
		radioParent = (RadioButton) findViewById(R.id.rd_parent);
		radioTeacher = (RadioButton) findViewById(R.id.rd_teacher);
		ckPass = (CheckBox) findViewById(R.id.ck_pass);
		imgDownUser = (ImageView) findViewById(R.id.imgDownUser);
		ckPass.setOnCheckedChangeListener(savePassdListener);
		
		tvTips = (TextView) findViewById(R.id.tips);
//		try {
//			Resources res = getResources();
//			tvTips.append(res.getString(R.string.login_tips_head));
//			tvTips.append(Html.fromHtml("<u>" + res.getString(R.string.login_tips_tail) + "</u>"));
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
		tvTips.setOnClickListener(this);
		
		imgDownUser.setOnClickListener(this);
		registerNew.setOnClickListener(this);
		forgetPwd.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		
//		if(isTeacher()){
//			radioTeacher.setChecked(true);
//		} else {
//			radioParent.setChecked(true);
//		}
		
//		Button back = (Button) findViewById(R.id.back);
//		back.setVisibility(View.GONE);
		registerNew.setVisibility(View.GONE);
		//初始化类型
//		initTypeList();
//		userType = rdTypeList.get(TYPE_TEACHER);

		rGroupIsteacher.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (radioTeacher.getId() == checkedId) {
					userType = 1;
				}else if (radioParent.getId() == checkedId) {
					userType = 3;
				}
			}});
//		rGroupIsteacher.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				if (radioTeacher.getId() == checkedId) {
//					setIsTeacher(true);
//					userType = rdTypeList.get(TYPE_TEACHER);
//				} else if (radioParent.getId() == checkedId) {
//					setIsTeacher(false);
//					userType = rdTypeList.get(TYPE_PARENT);
//				}
//				getHistoryAccount();
//				if(historyAcountList != null && historyAcountList.size() > 0){
//					AccountData data = historyAcountList.get(0);
//					loginName = data.getLoginname();
//					savePasswd = data.getLoginpwd();
//
//					if (StringUtils.isEmpty(savePasswd)) {
//						savePasswd = "";
//						isDbPasswd = false;
//						mPasswordEdit.setText("");
//					} else {
//						isDbPasswd = true;
//						mPasswordEdit.setText("111111");
//					}
//
//					mUsernameEdit.setText(loginName);
//					LogUtils.d("pop savePasswd:" + savePasswd + " isDbPasswd=" + isDbPasswd);
//				} else {
//					isDbPasswd = false;
//					loginName = "";
//					savePasswd = "";
//					mPasswordEdit.setText("");
//					mUsernameEdit.setText("");
//				}
//			}
//		});
//
//		mPasswordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if (actionId == EditorInfo.IME_NULL) {
//					hideKeyboard(mPasswordEdit.getWindowToken());
//					return true;
//				}
//				return false;
//			}
//		});
//		DataHelper mHelper = DataHelper.getHelper(this);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lastLoginUser", 1);
		List<AccountData> list = null;
//		try {
//			list = mHelper.getAccountDao().queryForFieldValues(params);
//		} catch (java.sql.SQLException e) {
//			e.printStackTrace();
//		}
		mPasswordEdit.clearFocus();
		if (list != null && list.size() > 0) {
			account = list.get(list.size() - 1);
			mHeaderLayout.setVisibility(View.VISIBLE);
			accountDefaultAvater.setVisibility(View.GONE);
			accountAvater.setVisibility(View.VISIBLE);
//			imageLoader.displayImage(account.getAvatar(), accountAvater);
			mUsernameEdit.setText(account.getLoginname());
			defaultLogin = true;
			mPasswordEdit.setFocusable(true);
			mPasswordEdit.setFocusableInTouchMode(true);
			mPasswordEdit.requestFocus();
		} else {
			mHeaderLayout.setVisibility(View.VISIBLE);
		}
//		mUsernameEdit.addTextChangedListener(textWatcher);
		
		str_tmp = "";
		
//		mUsernameEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if(hasFocus) {
//					account_image.setImageResource(R.drawable.icon_account_focus);
//				}else {
//					account_image.setImageResource(R.drawable.icon_account);
//				}
//			}
//		});
//		mPasswordEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if(hasFocus) {
//					password_image.setImageResource(R.drawable.icon_password_focus);
//				}else {
//					password_image.setImageResource(R.drawable.icon_password);
//				}
//			}
//		});
		mPasswordEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString() != null && !s.toString().equals("111111")) {
					isDbPasswd = false;
				}
			}
		});
		Intent intent = getIntent();
//		getHistoryAccount();
		
		if (null != intent) {
			//LogUtils.e("chat-------->Login onCreate 2");
			int logout = getIntent().getIntExtra("logout", 0);
			if (logout == 1) {
//				LogUtils.e("chat-------->Login onCreate 3");
				//IMClient.getInstance().logOut();
//				LogUtils.e("chat-------->Login onCreate 4");
				showAlrter();
			}
			
		} else {
//			LogUtils.e("LoginActivity, intent is null!!");
		}

//		APKUtils utils = new APKUtils();
//		try {
//			if (PluginManager.getInstance().isConnected()) {
//				PluginManager.getInstance().deletePackage("com.linkage.webviewapp", 0);
//			}
//			utils.installAPK(this,"/storage/emulated/0/download/webviewapp-release(1).apk","com.linkage.webviewapp",true);
//
//
//		}catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		PackageManager pm = getPackageManager();
//		Intent intent1 = pm.getLaunchIntentForPackage("com.linkage.webviewapp");
//		intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent1);

	}
	
	public void showAlrter() {
		Builder builder = new Builder(this);
		builder.setMessage("帐号已在其他设备上登录");
		builder.setTitle("提示");
		//LogUtils.e("chat-------->Login onCreate 5");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create();
		builder.show();
		// AlertDialog mDialog=builder.create();
		// //
		// mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		// mDialog.show();
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login:
			login();
			break;
//		case R.id.text_register:
//			gotoRegister();
//			break;
//		case R.id.text_forget1:
//			gotoReset();
//			break;
//		case R.id.imgDownUser:
//			if (null == historyAcountList || historyAcountList.size() <= 0) {
////				Toast.makeText(LoginActivity.this, "没有历史数据",
////				Toast.LENGTH_SHORT).show();
//				LogUtils.v("查无记录！");
//			} else {
//				showNamePop();
//			}
//
//			break;
//		case R.id.tips:
//			gotoReset();
//			break;
		}
	}
	
	//历史登陆pop
//	private void showNamePop() {
//
//		if (null == namePop) {
//			initNamePop(0);
//		}
//
//		if (namePop.isShowing()) {
//			// indicate.setImageResource(R.drawable.jx_parent_choose_child_down);
//			namePop.dismiss();
//		} else {
//			// indicate.setImageResource(R.drawable.jx_parent_choose_child_up);
//			namePop.showAsDropDown(mUsernameEdit, 0, -6);
//		}
//	}

//	private void initNamePop(int wid) {
//		LayoutInflater inflater = LayoutInflater.from(this);
//		// 引入窗口配置文件
//		View view = inflater.inflate(R.layout.pop_login, null);
//		ListView listView = (ListView) view.findViewById(R.id.listView);
//		nameAdapter = new NameAdapter();
//		listView.setAdapter(nameAdapter);
//		listView.setDivider(null);
//		listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				AccountData data = historyAcountList.get(position);
//				loginName = data.getLoginname();
//				savePasswd = data.getLoginpwd();
//				// refreshDefaultClassRoom();
//				// refreshData();
//				if (namePop.isShowing()) {
//					namePop.dismiss();
//				}
//
//				if (StringUtils.isEmpty(savePasswd)) {
//					savePasswd = null;
//					isDbPasswd = false;
//					mPasswordEdit.setText("");
//				} else {
//					isDbPasswd = true;
//					mPasswordEdit.setText("111111");
//				}
//
//				mUsernameEdit.setText(loginName);
//				LogUtils.d("pop savePasswd:" + savePasswd + " isDbPasswd="
//						+ isDbPasswd);
//			}
//		});
//		listView.setItemsCanFocus(false);
//		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//
//		if (wid > 0) {
//			LogUtils.e("wid=" + wid);
//			namePop = new PopupWindow(view, wid, LayoutParams.WRAP_CONTENT,
//					false);
//		} else {
//			namePop = new PopupWindow(view, LayoutParams.FILL_PARENT,
//					LayoutParams.WRAP_CONTENT, false);
//		}
//
//		namePop.setBackgroundDrawable(new BitmapDrawable());
//		namePop.setOutsideTouchable(true);
//		namePop.setFocusable(true);
//		namePop.update();
//		namePop.setOnDismissListener(new OnDismissListener() {
//			@Override
//			public void onDismiss() {
//				// indicate.setImageResource(R.drawable.jx_parent_choose_child_down);
//			}
//		});
//
//	}

//	class NameAdapter extends BaseAdapter {
//
//		public NameAdapter() {
//
//		}
//
//		@Override
//		public int getCount() {
//			if (null == historyAcountList) {
//				return 0;
//			} else {
//				return (historyAcountList.size() > 3) ? 3 : historyAcountList
//						.size();
//			}
//		}
//
//		@Override
//		public AccountData getItem(int position) {
//			// TODO Auto-generated method stub
//			return historyAcountList.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			ViewHolder viewHolder = null;
//			if (convertView == null) {
//				convertView = LayoutInflater.from(LoginActivity.this).inflate(
//						R.layout.login_user_list_item2, null);
//				viewHolder = new ViewHolder();
//
//				// viewHolder.tvUsType = (TextView) convertView
//				// .findViewById(R.id.tvUsType);
//				viewHolder.tvUsPhone = (TextView) convertView
//						.findViewById(R.id.tvUsPhone);
//				// viewHolder.imgHead = (CircularImage) convertView
//				// .findViewById(R.id.imgHead);
//				viewHolder.imgHead = (ImageView) convertView
//						.findViewById(R.id.imgHead);
//
//				convertView.setTag(viewHolder);
//			} else {
//				viewHolder = (ViewHolder) convertView.getTag();
//			}
//			final AccountData r = getItem(position);
//			// if (Consts.UserType.TEACHER == r.getUserType()) {
//			// viewHolder.tvUsType.setText(R.string.teacher);
//			// } else if (Consts.UserType.PARENT == r.getUserType()){
//			// viewHolder.tvUsType.setText(R.string.parent);
//			// } else {
//			//
//			// }
//			// viewHolder.tvUsType.setText(userType.getTypeName());
//			viewHolder.tvUsPhone.setText(r.getLoginname());
//			// imageLoader.displayImage(r.getAvatar(), viewHolder.imgHead);
//			viewHolder.imgHead.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					DataHelper dataHelper = DataHelper
//							.getHelper(LoginActivity.this);
//					try {
//						DeleteBuilder<AccountData, Integer> deleteBuilder = dataHelper
//								.getAccountDao().deleteBuilder();
//						deleteBuilder.where().eq("loginName", r.getLoginname());
//						// .and().eq("usertype", userType);
//						int result1 = deleteBuilder.delete();
//						LogUtils.i("AccountData().deleteBuilder().delete():"
//								+ result1);
//						if ((mUsernameEdit.getEditableText().toString().trim())
//								.equals(r.getLoginname())) {
//							mUsernameEdit.setText("");
//						}
//						getHistoryAccount();
//						nameAdapter.notifyDataSetChanged();
//
//					} catch (SQLException e) {
//						e.printStackTrace();
//					} catch (java.sql.SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//			});
//
//			return convertView;
//		}
//
//		class ViewHolder {
//			public TextView tvUsType, tvUsPhone;
//			// public CircularImage imgHead;
//			public ImageView imgHead;
//		}
//	}
	
//	private void getHistoryAccount() {
//
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("userType", userType.getUserType());
//
//		try {
//
//			DataHelper helper = DataHelper.getHelper(this);
//			// historyAcountList = helper.getAccountDao().queryForFieldValues(
//			// params);
//
//			QueryBuilder<AccountData, Integer> accountBuilder = helper
//					.getAccountDao().queryBuilder();
//			accountBuilder.orderBy("loginDate", false).where()
//					.eq("userType", userType.getUserType());
//			historyAcountList = accountBuilder.query();
//
//			if (null != historyAcountList) {
//				for (int i = 0; i < historyAcountList.size(); i++) {
//					LogUtils.v("history info:"
//							+ historyAcountList.get(i).toString());
//				}
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} catch (java.sql.SQLException e) {
//			e.printStackTrace();
//		}
//	}
	
//	private class UserType {
//		int userType;
//		String userTypeName;
//
//		public int getUserType() {
//			return userType;
//		}
//
//		public void setUserType(int userType) {
//			this.userType = userType;
//		}
//
//		public String getTypeName() {
//			return userTypeName;
//		}
//
//		public void setTypeName(String userTypeName) {
//			this.userTypeName = userTypeName;
//		}
//	}

	// Goto 注册页面
//	private void gotoRegister() {
//		Intent i = new Intent(this, Register_SendCodeActivity.class);
//		startActivity(i);
//	}

	// Goto 忘记密码页面
//	private void gotoReset() {
//		Intent i = new Intent(this, Reset_SendCodeActivity.class);
//		startActivity(i);
//	}

	private void login() {
//		Animation shake = AnimationUtils.loadAnimation(instance, R.anim.shake);
		loginName = mUsernameEdit.getEditableText().toString().trim();
		password = mPasswordEdit.getEditableText().toString().trim();
		/*
		 * if(defaultLogin) loginName = account.getLoginname();
		 */
		if (loginName.length() < 1) {
			// mUsernameEdit.setText("");
//			mUsernameEdit.startAnimation(shake);
			UIUtilities.showToast(this, "请输入正确的帐号");
		} else if (TextUtils.isEmpty(password)) {
			mPasswordEdit.setText("");
//			mPasswordEdit.startAnimation(shake);
			UIUtilities.showToast(this, "请输入密码");

		} else {
			doLogin();
			// Intent i = new Intent(this, MainActivity.class);
			// startActivity(i);
		}
	}

	private void doLogin() {

		try {
//			if(isDbPasswd){
//				password = savePasswd;
//			} else {
				password = Utilities.md5(password);
//			}
			LogUtils.e("=============password is =======" + password);
			ProgressDialogUtils.showProgressDialog("正在登录,请稍候...", this, false);
			loginBtn.setEnabled(false);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "login");
			params.put("user_type", String.valueOf(userType));
			// params.put("user_type", 1+"");
			params.put("account", loginName);
			params.put("password", password);

			String IMEI = Utilities.getIMEI(this);
			if (IMEI == null) {
				IMEI = android.os.Build.MODEL;
				// IMEI = Utilities.getMacAddress(this);
			}
			params.put("term_manufacturer", "android," + IMEI);

			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
					Consts.SERVER_URL, Request.Method.POST, params, null,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							ProgressDialogUtils.dismissProgressBar();

							loginBtn.setEnabled(true);
							System.out.println("response=" + response);
							if (response.optInt("ret") == 0) {
								// TODO 登录成功后的帐号更新等
								user = AccountData.parseFromJson(response.optJSONObject("data"));
								onLoginSuccess(response.optString("data"));
							} else {
								ProgressDialogUtils.dismissProgressBar();
								StatusUtils.handleStatus(response, instance);
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							loginBtn.setEnabled(true);
							LogUtils.v("登录失败=================");
							ProgressDialogUtils.dismissProgressBar();
							StatusUtils.handleError(arg0, instance);
						}
					});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
			loginBtn.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onLoginSuccess(String data) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("extra_username", loginName);
		params.put("extra_password", password);
		params.put("extra_data", data);
		runAppByParam(this, "com.linkage.mobile72.sh", "com.linkage.mobile72.sh.activity.MainActivity", params);
		finish();
	}

	public static void runAppByParam(Context context, String packageName, String launchUrl, Map<String, Object> params) {
		LogUtils.e("packageName:" + packageName);
		ComponentName componetName = new ComponentName(
				//这个是另外一个应用程序的包名
				packageName,
				//这个参数是要启动的Activity
				launchUrl);
		try {
			Intent intent = new Intent();
			intent.setComponent(componetName);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if(params != null && params.size() > 0) {
				Set<Map.Entry<String, Object>> set = params.entrySet();
				for (Iterator<Map.Entry<String, Object>> it = set.iterator(); it.hasNext();) {
					Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
					intent.putExtra(entry.getKey(), entry.getValue().toString());
				}
			}
			context.startActivity(intent);
		} catch (Exception e) {
			UIUtilities.showToast(context, "应用未安装或路径不存在");
		}
	}

	/*public static void loginIm(final String name, final String password,
			final int userType) {
		BaseApplication.getInstance().callWhenServiceConnected(new Runnable() {

			@Override
			public void run() {
				try {
					BaseApplication.getInstance().getChatService()
							.login(name, password, userType);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			if (!isDbPasswd && null != savePasswd) {
				savePasswd = null;
				LogUtils.v("---> beforeTextChanged pwd, clear savePasswd isDbPasswd:" + isDbPasswd);
			}
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		
			if (!str_tmp.equalsIgnoreCase(mUsernameEdit.getText().toString())) {
				str_tmp = mUsernameEdit.getText().toString();
				DataHelper mHelper = DataHelper.getHelper(LoginActivity.this);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("loginname", mUsernameEdit.getText().toString());
				List<AccountData> list = null;
				try {
					list = mHelper.getAccountDao().queryForFieldValues(params);
				} catch (java.sql.SQLException e) {
					e.printStackTrace();
				}
				if (list != null && list.size() > 0) {
					AccountData data = list.get(list.size() - 1);
					mHeaderLayout.setVisibility(View.VISIBLE);
					accountDefaultAvater.setVisibility(View.GONE);
					accountAvater.setVisibility(View.VISIBLE);
					imageLoader.displayImage(data.getAvatar(), accountAvater);
				} else {
					mHeaderLayout.setVisibility(View.VISIBLE);
					accountDefaultAvater.setVisibility(View.VISIBLE);
					accountAvater.setVisibility(View.GONE);
					accountAvater.setImageDrawable(getResources().getDrawable(
							R.drawable.logo_aboutus));
				}
			}
		}
	};*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.linkage.mobile72.sh.app.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
//		BaseApplication.getInstance().setInLoginActivity(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
//		BaseApplication.getInstance().setInLoginActivity(false);
	}

	/*@Override
	public void onError(int code) {
		//Toast.makeText(this, "用户名密码错误code="+code, Toast.LENGTH_SHORT).show();
		LogUtils.e("chat-------------->im login err, code=" + code);
		ProgressDialogUtils.dismissProgressBar();
		sendToMainActivity(0);
	}

	@Override
	public void onSuccess() {
		//IMClient.getInstance().connect("120.55.138.134", 8443, this);
		LogUtils.d("chat-------->login sucess");
		
		mApp.setChatUserId(usrName);
		mApp.setHasLoginIm(true);
		ProgressDialogUtils.dismissProgressBar();
//		sendToMainActivity(1);
		onLoginSuccess(user);
	}
	
	private void sendToMainActivity(int isSucess) {
//		Intent intent = new Intent(this, MainActivity.class);
//		intent.putExtra("haslogin", isSucess);
//		// intent.putExtra("login", true);
//		startActivity(intent);
//		finish();
		onLoginSuccess(user);
	}*/
}
