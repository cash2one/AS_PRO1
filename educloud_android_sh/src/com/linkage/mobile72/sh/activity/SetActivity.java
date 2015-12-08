package com.linkage.mobile72.sh.activity;

import info.emm.messenger.IMClient;

import java.io.File;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;

public class SetActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = SetActivity.class.getName();
	
	private MyCommonDialog dialog;
	private View layout_user_version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_set);
		setTitle("系统设置");
		viewinit();
	}

	private void viewinit() {
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.layout_user_pwd).setOnClickListener(this);
		findViewById(R.id.layout_user_logout).setOnClickListener(this);
		findViewById(R.id.layout_user_opinion).setOnClickListener(this);
		findViewById(R.id.layout_user_about).setOnClickListener(this);
		findViewById(R.id.layout_user_version).setOnClickListener(this);
		findViewById(R.id.layout_user_Flush).setOnClickListener(this);
		findViewById(R.id.layout_help_center).setOnClickListener(this);
		if(isTeacher()){
			findViewById(R.id.layout_recv_set).setOnClickListener(this);
		} else {
			findViewById(R.id.layout_recv_set).setVisibility(View.GONE);
		}
		layout_user_version = findViewById(R.id.layout_user_version);
		layout_user_version.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.layout_help_center:
			Intent intent = new Intent(this,NewWebViewActivity.class);
			intent.putExtra(NewWebViewActivity.KEY_TITLE, NewWebViewActivity.HELP_CENTER);
			intent.putExtra(NewWebViewActivity.KEY_URL, Consts.HELP_CENTER_URL);
//			intent.putExtra(NewWebViewActivity.KEY_TOKEN, "");
			startActivity(intent);
			break;
		case R.id.layout_user_version:
			updateVersionByServer();
			break;
		case R.id.layout_user_opinion:
			Intent opinionIntent = new Intent(this, OpinionActivity.class);
			opinionIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
			startActivity(opinionIntent);
			break;
		case R.id.layout_user_about:
			Intent aboutIntent = new Intent(this, AboutUsActivity.class);
			aboutIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
			startActivity(aboutIntent);
			break;
		case R.id.layout_user_pwd:
			Intent pwdIntent = new Intent(this, AccountPasswordActivity.class);
			pwdIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
			startActivity(pwdIntent);
			break;
		case R.id.layout_user_Flush:
			ClearCacheTask clearCacheTask = new ClearCacheTask();
			clearCacheTask.execute();
			break;

		case R.id.layout_user_logout:
			dialog = new MyCommonDialog(this, "提示消息", "确认要退出登录吗？", "取消", "确认");
			dialog.setOkListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mApp.logout(true);
					IMClient.getInstance().logOut();
					Intent intent = new Intent(SetActivity.this, LoginActivity.class);
					startActivity(intent);
					MainActivity.instance.finish();
					finish();
				}
			});
			dialog.setCancelListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (dialog.isShowing())
						dialog.dismiss();
				}
			});
			dialog.show();
			break;
	     case R.id.layout_recv_set:
	            Intent recvIntent = new Intent(this, OTTPullActitvity.class);
	            recvIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	            startActivity(recvIntent);
	            break;
		}
	}

	/**
	 * 清除缓存
	 */
	private class ClearCacheTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ProgressDialogUtils.showProgressDialog("正在清除缓存", SetActivity.this, true);
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			imageLoader.clearDiscCache();
			imageLoader.clearMemoryCache();
			File cacheDir = getCacheDir();
			if (cacheDir != null && cacheDir.exists()) {
				for (File item : cacheDir.listFiles()) {
					deleteFilesByDirectory(item);
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}

		private void deleteFilesByDirectory(File directory) {
			if (directory != null && directory.exists() && directory.isDirectory()) {
				for (File item : directory.listFiles()) {
					if (item.isDirectory()) {
						deleteFilesByDirectory(item);
					} else {
						item.delete();
					}
				}
			} else if (directory != null && directory.exists()) {
				directory.delete();
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			ProgressDialogUtils.dismissProgressBar();
			T.showShort(SetActivity.this, "清空缓存完毕");
		}
	}

	public static String getVersionCode(Context context) {// 获取版本号(内部识别号)
		try {
			PackageInfo pi = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return String.valueOf(pi.versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void updateVersionByServer() {
		ProgressDialogUtils.showProgressDialog("正在获取最新版本", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		final String verNo = Utils.getVersionCode(this);
		final String info = "JS"+","+verNo+","+Consts.getServer();
        params.put("commandtype", "uploadversion");
        params.put("clientinfo", info);
        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
                Consts.SERVER_URL, Request.Method.POST, params, false,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
							JSONObject data = response.optJSONObject("data");
							String version = data.optString("version");
							String description = data.optString("description");
							String created_at = data.optString("created_at");
							final String url = data.optString("url");
							Boolean update = data.optBoolean("update");
							Boolean forceUpdate = data.optBoolean("forceUpdate");
							if (update == true && forceUpdate == false) {
								dialog = new MyCommonDialog(SetActivity.this, "新版本提示", " 检测到最新版本:" + version + "\n " + description, "取消", "确定更新");
								dialog.setOkListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										update(url);
									}
								});
								dialog.setCancelListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if(dialog.isShowing())
											dialog.dismiss();
									}
								});
								dialog.show();
							} else if (update == false && forceUpdate == true) {
								dialog = new MyCommonDialog(SetActivity.this, "新版本提示", " 检测到最新版本:" + version + "\n " + description, null, "确定更新");
								dialog.setOkListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										update(url);
									}
								});
								/*dialog.setCancelListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if(dialog.isShowing())
											dialog.dismiss();
										android.os.Process.killProcess(android.os.Process.myPid());
									}
								});*/
								dialog.show();
								
							} else if (update == true && forceUpdate == true) {
								dialog = new MyCommonDialog(SetActivity.this, "新版本提示", " 检测到最新版本:" + version + "\n " + description, null, "确定更新");
								dialog.setOkListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										update(url);
									}
								});
								/*dialog.setCancelListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if(dialog.isShowing())
											dialog.dismiss();
										android.os.Process.killProcess(android.os.Process.myPid());
									}
								});*/
								dialog.show();
							} else if (update == false && forceUpdate == false) {
								UIUtilities.showToast(SetActivity.this, "已经是最新版本了");
							}

						} else {
							StatusUtils.handleStatus(response, SetActivity.this);
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
	
}