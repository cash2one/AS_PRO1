package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AppDetailBean;
import com.linkage.mobile72.sh.data.http.AppBean;
import com.linkage.mobile72.sh.fragment.AppFragment;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.receiver.UriLaunchReceiver;
import com.linkage.mobile72.sh.utils.AppsUtils;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class AppDetailActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = AppDetailActivity.class.getSimpleName();

	private AppBean appBean;
	private AppDetailBean app;
	private String[] mPhotosStringArray;

	private LinearLayout mGalleryLinearLayout;

	private Context mContext;

	private ImageView appImage;

	private TextView appNameText, appDownloadCount, appDescText, appVendor,
			appUpdateTime, appVersion, appSize, appCompliant;

	private Button back, appButton;

	private DisplayImageOptions mDefaultOptions1, mDefaultOptions2;

	private MyCommonDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent i_getvalue = getIntent();
		String action = i_getvalue.getAction();
		if (Intent.ACTION_VIEW.equals(action)) {
			Uri uri = i_getvalue.getData();
			if (uri != null) {
				String id = uri.getQueryParameter("id");
				String priceType = uri.getQueryParameter("price_type");
				if(StringUtils.isEmpty(id) || !id.matches("\\d+")) {
					Toast.makeText(AppDetailActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
					finish();
					return;
				}
				if(StringUtils.isEmpty(priceType) || !priceType.matches("\\d+")) {
					Toast.makeText(AppDetailActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
					finish();
					return;
				}
				if(getCurAccount() == null) {
					Intent i = new Intent(this , UriLaunchReceiver.class);
					startActivity(i);
					finish();
					return;
				}
				appBean = new AppBean();
				appBean.setId(Long.valueOf(id));
				appBean.setPrice_type(priceType);
			}
		} else {
			appBean = (AppBean) getIntent().getExtras().getSerializable("app");
			if (appBean == null) {
				finish();
				return;
			}
		}
		mContext = this;
		setContentView(R.layout.activity_app_detail);
		setTitle(R.string.title_detail);

		mDefaultOptions1 = new DisplayImageOptions.Builder().cacheOnDisc()
				.showStubImage(R.drawable.appdetail_icon_def)
				.showImageForEmptyUri(R.drawable.appdetail_icon_def)
				.showImageOnFail(R.drawable.appdetail_icon_def).build();

		mDefaultOptions2 = new DisplayImageOptions.Builder().cacheOnDisc()
				.showStubImage(R.drawable.appdetail_def)
				.showImageForEmptyUri(R.drawable.appdetail_def)
				.showImageOnFail(R.drawable.appdetail_def).build();

		mGalleryLinearLayout = (LinearLayout) findViewById(R.id.galleryLinearLayout);
		appImage = (ImageView) findViewById(R.id.app_icon);
		appNameText = (TextView) findViewById(R.id.app_name);
		appDownloadCount = (TextView) findViewById(R.id.app_download_count);
		appDescText = (TextView) findViewById(R.id.app_desc);
		appVendor = (TextView) findViewById(R.id.app_vendor);
		appButton = (Button) findViewById(R.id.download);
		appUpdateTime = (TextView) findViewById(R.id.app_update_time);
		appVersion = (TextView) findViewById(R.id.app_version);
		appSize = (TextView) findViewById(R.id.app_size);
		appCompliant = (TextView) findViewById(R.id.app_compliant);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		appButton.setOnClickListener(this);

	}

	private void loadView() {
		if (app == null)
			return;
		imageLoader.displayImage(app.getAppLogo(), appImage, mDefaultOptions1);
		appNameText.setText(app.getAppName());
		appDescText.setText(app.getAppDesc());
		appDownloadCount.setText("下载：" + app.getAppDownNum());
		appVendor.setText(app.getCpname());
		// appButton.setText("￥" + app.getAppPrice_me());
		appUpdateTime.setText(app.getUpdateDate());
		appVersion.setText(app.getVersion());
		appSize.setText(app.getFileSize());
		appCompliant.setText(app.getCompatibilit());
		int priceType = Integer.parseInt(appBean.getPrice_type());
		if (StringUtils.isEmpty(app.getOpenid()) && appBean.getSourceId() != AppBean.COLLECT_APP) {// 未授权
			/*
			 * viewHolder.app_price.setVisibility(View.VISIBLE); if(priceType ==
			 * 1) {//点播 viewHolder.app_price.setText("￥" + app.getAppPrice());
			 * }else if(priceType == 2){//包月 viewHolder.app_price.setText("￥" +
			 * app.getAppPrice() + "/月"); }
			 */
			if (app.getInapp() == 1) {
				appButton.setText(app.getInapp_notice());
			} else {
				appButton.setText("￥" + app.getAppPrice_me()
						+ (priceType == 1 ? "" : "/月"));
			}
			appButton.setBackgroundResource(R.drawable.app_download_bg);
			appButton.setTextColor(mContext.getResources().getColor(
					android.R.color.black));
		} else {
			appButton.setBackgroundResource(R.drawable.app_list_item_btn);
			appButton.setTextColor(mContext.getResources().getColor(
					android.R.color.white));
			if (app.getAppType() == 1) {
				if (Utils.checkApkExist(mContext, app.getAppLauncherPath())) {
					appButton.setText("打开");
				} else {
					appButton.setText("下载");
				}
			} else {
				appButton.setText("打开");
			}
		}

		appButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (StringUtils.isEmpty(app.getOpenid()) && appBean.getSourceId() != AppBean.COLLECT_APP) {
					double price = Double.parseDouble(app.getAppPrice_me());
					if (price > 0) {
						Intent i = new Intent(mContext, ConfirmPaymentActivity.class);
						Bundle b = new Bundle();
						b.putSerializable("APP", appBean);
						i.putExtras(b);
						mContext.startActivity(i);
					} else {
						Intent i = new Intent(mContext, AppLaunchActivity.class);
						Bundle b = new Bundle();
						b.putSerializable("APP", appBean);
						i.putExtras(b);
						mContext.startActivity(i);
					}
				} else {
					if(appBean.getSourceId() == AppBean.COLLECT_APP){
						AppsUtils.startCollectApp(AppDetailActivity.this, appBean);
						return;
					} 
					if (app.getAppType() == 1) {// APP
						if (Utils.checkApkExist(mContext,
								app.getAppLauncherPath())) {
							AppsUtils.refreshScore(AppDetailActivity.this, appBean.getId());
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("appToken", app.getOpenid());
							LogUtils.e("*****************appToken**" + app.getOpenid() + "");
							if (TextUtils.isEmpty(app.getAppLauncherUrl())) {
								Utils.runAppByParam(mContext,
										app.getAppLauncherPath(), params);
							} else {
								Utils.runAppByParam(mContext,
										app.getAppLauncherPath(),
										app.getAppLauncherUrl(), params);
							}
						} else {
							if (app.getAppUrl() != null
									&& !"null".equals(app.getAppUrl())
									&& !TextUtils.isEmpty(app.getAppUrl())) {
								mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(app.getAppUrl())));
//								dialog = new MyCommonDialog(mContext, "下载提示", "您可以通过和校园500M的专属流量下载该应用", "取消", "确定");
//								dialog.setOkListener(new OnClickListener() {
//									@Override
//									public void onClick(View v) {
//										if (dialog != null && dialog.isShowing()) {
//											dialog.dismiss();
//										}
//										mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(app.getAppUrl())));
//									}
//								});
//								dialog.setCancelListener(new OnClickListener() {
//									@Override
//									public void onClick(View v) {
//										if (dialog != null && dialog.isShowing()) {
//											dialog.dismiss();
//										}
//									}
//								});
//								dialog.show();
							} else {
								UIUtilities.showToast(mContext, "下载地址不正确");
							}
						}
					} else {// H5
						AppsUtils.refreshScore(AppDetailActivity.this, appBean.getId());
						Intent mIntent = new Intent(mContext,
								WebViewActivity.class);
						mIntent.putExtra(WebViewActivity.KEY_URL,
								app.getAppLauncherUrl());
						mIntent.putExtra(WebViewActivity.KEY_TITLE,
								app.getAppName());
						if (app.getAppAuth() == 1)
							mIntent.putExtra(WebViewActivity.KEY_TOKEN,
									app.getOpenid());
						mContext.startActivity(mIntent);
					}
				}
			}
		});
		// 测试，接口开发完成这行要去掉
		// app.setAppImg("http://jsnxetd.org.cn/uploads/2a7e66b7fcd691c79fb2e1a5ae56453ae548d2b8.jpg,http://jsnxetd.org.cn/uploads/918d4e35d8de49b5c782e2898bbb4891186239c1.jpg,http://jsnxetd.org.cn/uploads/31ce9e48a6473740c9e671a37a5b26edbadaff7a.jpg");
		loadImg();
	}

	private ArrayList<String> choosePics = new ArrayList<String>();

	private void loadImg() {
		if (choosePics.size() > 0) {
			return;
		}

		mPhotosStringArray = app.getAppImg().split(";");

		for (int j = 0; j < mPhotosStringArray.length; j++) {
			choosePics.add(mPhotosStringArray[j]);
		}
		View itemView = null;
		ImageView imageView = null;

		for (int i = 0; i < mPhotosStringArray.length; i++) {
			itemView = LayoutInflater.from(mContext).inflate(
					R.layout.item_app_detail_gallery, null);
			imageView = (ImageView) itemView.findViewById(R.id.imageView);
			imageLoader.displayImage(mPhotosStringArray[i], imageView,
					mDefaultOptions2);
			imageView.setOnClickListener(new PicClickListener(i));
			mGalleryLinearLayout.addView(itemView);
		}
	}

	class PicClickListener implements View.OnClickListener {

		private int position;

		PicClickListener(int position) {
			this.position = position;
		}

		public void onClick(View v) {
			Intent intent = new Intent(AppDetailActivity.this,
					PictureReviewNetActivity.class);
			intent.putStringArrayListExtra(PictureReviewNetActivity.RES,
					choosePics);
			intent.putExtra("position", position);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}

	private void fetchData(long id) {
		ProgressDialogUtils.showProgressDialog("正在获取详情", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getAppDetail");// 测试，后面要，改这个地址不对
		params.put("id", String.valueOf(id));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_getAppDetail, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
							try {
								app = AppDetailBean.parseFromJson(response.optJSONObject("data"));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								app = null;
							}
							if(app != null) {
								appBean.setAppAuth(app.getAppAuth());
								appBean.setAppDesc(app.getAppDesc());
								appBean.setAppDownNum(String.valueOf(app.getAppDownNum()));
								appBean.setAppIntroduce(app.getAppIntroduce());
								appBean.setAppLauncherPath(app.getAppLauncherPath());
								appBean.setAppLauncherUrl(app.getAppLauncherUrl());
								appBean.setAppLogo(app.getAppLogo());
								appBean.setAppName(app.getAppName());
								appBean.setAppPrice(app.getAppPrice());
								appBean.setAppPrice_me(app.getAppPrice_me());
								appBean.setAppToken(app.getOpenid());
								appBean.setAppType(app.getAppType());
								appBean.setAppUrl(app.getAppUrl());
								//appBean.setAppVersion(app.getApp);
								appBean.setCpName(app.getCpname());
								appBean.setInapp(app.getInapp());
								appBean.setInapp_notice(app.getInapp_notice());
								//appBean.setInstalled(app.getIn);
								//appBean.setPrice_type(app.get);
								loadView();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, AppDetailActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fetchData(appBean.getId());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String action = intent.getAction();
		if (Intent.ACTION_VIEW.equals(action)) {
			Uri uri = intent.getData();
			if (uri != null) {
				String id = uri.getQueryParameter("id");
				String priceType = uri.getQueryParameter("price_type");
				Log.e("id+priceType", id+priceType);
				appBean = new AppBean();
				appBean.setId(Long.valueOf(id));
				appBean.setPrice_type(priceType);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);

	}
	
}
