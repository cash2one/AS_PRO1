package com.linkage.mobile72.sh.activity;

import info.emm.messenger.IMClient;
import info.emm.messenger.VYCallBack;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountChild;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.im.FileHelper;
import com.linkage.mobile72.sh.utils.Des3;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.Utilities;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

public class SplashActivity extends BaseActivity implements VYCallBack {

	private static final String TAG = SplashActivity.class.getName();

	public static SplashActivity instance;
	private AccountData account;
	private View view;
	private MyCommonDialog dialog;
	private AlphaAnimation anim;
	
	private String username;
	private String password;
	private AccountData user;

	@SuppressLint("HandlerLeak")
	private Handler finishHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			anim.cancel();
			if (account != null) {
				doLogin(account.getLoginname(), account.getLoginpwd());
			} else {
				gotoLogin();
			}
		};
	};
	
	/**
	 * 跳转页面的handler what == 0 为广告 what == 1 为主页面
	 */
	private Handler startActivityHandle = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			//广告
			case 0:
				intent.setClass(SplashActivity.this, AdvActivity.class);
				startActivity(intent);
				finish();
				break;
			//主页面
			case 1:
				intent.setClass(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case 2:
				fileHelper = new FileHelper(fileDownloadHandler);
				new Thread() {
					@Override
					public void run() {
						fileHelper.down_file(getCurAccount().getLoadingImg(),
								BaseApplication.getInstance().getWorkspaceDownload().getPath(),
								AdvActivity.LOADING_PIC_NAME + picName + ".jpg");
					}
				}.start();
				break;
			}
			
		}
	};
	
	private Handler fileDownloadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FileHelper.MESSAGE_OPEN_DIALOG:// 开始启动Dialog
				break;
			case FileHelper.MESSAGE_START:// 开始下载
				break;
			case FileHelper.MESSAGE_PROGRESS:// 正在下载
				break;
			case FileHelper.MESSAGE_STOP:// 下载结束
				mApp.getSp().edit().putString(
						AdvActivity.LOADING_PIC_HISTORY_LOCAL + getCurAccount().getUserId(), picName).commit();
				mApp.getSp().edit().putString(
						AdvActivity.LOADING_PIC_HISTORY_SERVER + getCurAccount().getUserId(), getCurAccount().getLoadingImg()).commit();
				startActivityHandle.sendEmptyMessage(0);
				break;
			case FileHelper.MESSAGE_ERROR:
				startActivityHandle.sendEmptyMessage(1);
				break;
			}
			super.handleMessage(msg);
		}
	};

	private FileHelper fileHelper;
	private String picName;
	
	private void gotoLogin() {
		SharedPreferences sp = BaseApplication.getInstance().getSp();
		int firstInstall = sp.getInt(UserGuideActivity.FIRST_INSTALL_STR, 0);
		Intent intent = new Intent();
		if (firstInstall == 0) {
			intent.setClass(this, UserGuideActivity.class);
			intent.putExtra(UserGuideActivity.FROM,
					UserGuideActivity.FIRST_INSTALL);
		} else {
			intent.setClass(this, LoginActivity.class);
		}
		startActivity(intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
		view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);
		instance = this;
		account = getLastLoginAccount();
		anim = new AlphaAnimation(0.5f, 1.0f);
		anim.setDuration(1000);
		view.setAnimation(anim);
		anim.startNow();
		updateVersionByServer();
		MobclickAgent.updateOnlineConfig(this);// 友盟发送策略
		AnalyticsConfig.enableEncrypt(true);// 设置对日志加密
		
		if(account != null){
			//数据库判断是否为老师
			int userType = account.getUserType();
			if(userType == 1){
				setIsTeacher(true);
			} else {
				setIsTeacher(false);
			}
		}
	}

	private void updateVersionByServer() {
		HashMap<String, String> params = new HashMap<String, String>();
		final String verNo = Utils.getVersionCode(this);
		final String info = "JS" + "," + verNo + "," + Consts.getServer();
		params.put("commandtype", "uploadversion");
		params.put("clientinfo", info);
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_URL, Request.Method.POST, params, false,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						LogUtils.e("111" + response.toString());
						if (response.optInt("ret") == 0) {
							JSONObject data = response.optJSONObject("data");
							String version = data.optString("version");
							if (version.equalsIgnoreCase(verNo)) {
								finishHandler.sendEmptyMessage(1);
								return;
							}
							String description = data.optString("description");
							String created_at = data.optString("created_at");
							final String url = data.optString("url");
							Boolean update = data.optBoolean("update");
							Boolean forceUpdate = data
									.optBoolean("forceUpdate");
							if (update == true && forceUpdate == false) {
								dialog = new MyCommonDialog(
										SplashActivity.this, "新版本提示",
										" 检测到最新版本:" + version + "\n "
												+ description, "取消", "确定更新");
								dialog.setCancelable(false);
								dialog.setOkListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										update(url);
									}
								});
								dialog.setCancelListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if (dialog.isShowing())
											dialog.dismiss();
										finishHandler.sendEmptyMessage(1);
									}
								});
								dialog.show();
							} else if (update == false && forceUpdate == true) {
								dialog = new MyCommonDialog(
										SplashActivity.this, "新版本提示",
										" 检测到最新版本:" + version + "\n "
												+ description, "退出应用", "确定更新");
								dialog.setCancelable(false);
								dialog.setOkListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										update(url);
									}
								});
								dialog.setCancelListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if (dialog.isShowing())
											dialog.dismiss();
										finish();
									}
								});
								dialog.show();

							} else if (update == true && forceUpdate == true) {
								dialog = new MyCommonDialog(
										SplashActivity.this, "新版本提示",
										" 检测到最新版本:" + version + "\n "
												+ description, "退出应用", "确定更新");
								dialog.setCancelable(false);
								dialog.setOkListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										update(url);
									}
								});
								dialog.setCancelListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if (dialog.isShowing())
											dialog.dismiss();
										finish();
									}
								});
								dialog.show();
							} else if (update == false && forceUpdate == false) {
								finishHandler.sendEmptyMessage(1);
							}

						} else {
							StatusUtils.handleStatus(response,
									SplashActivity.this);
							finishHandler.sendEmptyMessage(1);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						finishHandler.sendEmptyMessage(1);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	private int update(String url) {
		int ret = -1;
		try {
			Uri uri = Uri.parse(url);
			startActivity(new Intent(Intent.ACTION_VIEW, uri));
			ret = 0;
		} catch (Exception e) {

		}
		return ret;
	}

	private void doLogin(final String userName, final String passWord) {
		username = userName;
		password = passWord;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "login");
			params.put("user_type", isTeacher() ? Consts.UserType.TEACHER + ""
					: Consts.UserType.PARENT + "");
			// params.put("user_type", 1+"");
			params.put("account", username);
			params.put("password", password);

			String IMEI = Utilities.getIMEI(this);
			if (IMEI == null) {
				IMEI = android.os.Build.MODEL;
				// IMEI = Utilities.getMacAddress(this);
			}
			if (IMEI == null) {
				IMEI = "";
			}
			params.put("term_manufacturer", "android," + IMEI);

			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
					Consts.SERVER_URL, Request.Method.POST, params, false,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							ProgressDialogUtils.dismissProgressBar();
							if (response.optInt("ret") == 0) {
								// TODO 登录成功后的帐号更新等
								try {
									user = AccountData.parseFromJson(response.optJSONObject("data"));
									if (user == null || "".equalsIgnoreCase(user.getToken())) {
										StatusUtils.handleOtherError("登录失败：userInfo为空", instance);
										return;
									}
									String usrName = Consts.APP_ID + user.getUserId();
									try {
//										正式
										String pwd = "";
										pwd = Des3.encode(usrName);
										
										if(IMClient.getInstance().isConnected()){
											IMClient.getInstance().logOut();
										}
										IMClient.getInstance().login(usrName, pwd, SplashActivity.this);
										
										LogUtils.e("chat-------------->login activity usrName=" + usrName + " pwd=" + pwd);
										
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
//									onLoginSuccess(user, username, password);
								} catch (Exception e) {
									gotoLogin();
									StatusUtils.handleStatus(response, instance);
								}
//							} else if (response.optInt("ret") == 1) {// 用户未注册//
//																		// 但是是系统录入的Teacher//
//																		// Or//
//																		// Student
//								Intent i = new Intent(instance,
//										Register_SendCodeActivity.class);
//								i.putExtra("PHONE", username);
//								startActivity(i);
							} else {
								gotoLogin();
								StatusUtils.handleStatus(response, instance);
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {

							StatusUtils.handleError(arg0, instance);
							gotoLogin();
						}
					});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void onLoginSuccess(final AccountData user) {

		List<AccountChild> childs = user.getStudentData();
		DataHelper helper = getDBHelper();
		try {
			helper.getAccountDao().updateRaw(
					"update AccountData set defaultUser = 0");
			helper.getAccountDao().updateRaw(
					"update AccountData set lastLoginUser = 0");
			// 初始化用户数据
			user.setLoginname(username);
			user.setLoginpwd(password);
			user.setDefaultUser(1);
			user.setLastLoginUser(1);
			user.setLoginDate(System.currentTimeMillis());
			QueryBuilder<AccountData, Integer> accountBuilder = helper.getAccountDao().queryBuilder();
			accountBuilder.where().eq("userId", user.getUserId()).and().eq("userType", user.getUserType());
			AccountData ad = accountBuilder.queryForFirst();
			if(ad != null) {
				helper.getAccountDao().update(user);
			}else {
				helper.getAccountDao().create(user);
			}
			//helper.getAccountDao().createOrUpdate(user);
			if (null != childs && childs.size() > 0) {
				DeleteBuilder<AccountChild, Integer> deleteBuilder;
				deleteBuilder = helper.getAccountChildDao().deleteBuilder();
				deleteBuilder.where().eq("userid", user.getUserId());
				deleteBuilder.delete();
				for (int i = 0; i < childs.size(); i++) {
					AccountChild child = childs.get(i);
					if (i == 0)
						child.setDefaultChild(1);
					child.setUserid(user.getUserId());
					helper.getAccountChildDao().create(child);
				}
			}
			mApp.notifyAccountChanged();

//			BaseApplication.getInstance().startIMService();
			// 上次加载网络图片的Url
			final String historyLocal = mApp.getSp().getString(
					AdvActivity.LOADING_PIC_HISTORY_LOCAL + user.getUserId(), "");
			final String historyServer = mApp.getSp().getString(
					AdvActivity.LOADING_PIC_HISTORY_SERVER + user.getUserId(), "");
			Log.d("tag_", "historty----------------" + historyLocal);
			Log.d("tag_", "histortyServer----------------" + historyServer);
			// 用当前时间字符串命名图片名称
			picName = Utilities.formatNow(new SimpleDateFormat("yyyyMMdd")).substring(0, 8) + "_" + user.getUserId();
			//如果本地图片为空，或者与现创文件的文件名不匹配，则进入线程
			if(StringUtils.isEmpty(getCurAccount().getLoadingImg())) {
				startActivityHandle.sendEmptyMessage(1);
			}else {
				if(StringUtils.isEmpty(historyLocal)) {
					startActivityHandle.sendEmptyMessage(2);
				}else {
					String historyLocalTime = historyLocal.split("_")[0];
					String todayTime = picName.split("_")[0];
					if(!historyLocalTime.equals(todayTime)) {
						startActivityHandle.sendEmptyMessage(2);
					}else {
						if(!historyServer.equals(getCurAccount().getLoadingImg())) {
							startActivityHandle.sendEmptyMessage(2);
						}else {
							startActivityHandle.sendEmptyMessage(1);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			StatusUtils.handleOtherError("网络通讯异常", instance);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			StatusUtils.handleOtherError("网络通讯异常", instance);
		}
		// loginIm(loginName, password, user.getUserType());
	}

	public static void loginIm(final String name, final String password,
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
	
	@Override
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
		
		mApp.setChatUserId(username);
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
	}
}
