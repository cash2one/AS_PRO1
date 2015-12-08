package com.linkage.mobile72.sh.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.AppDetailActivity;
import com.linkage.mobile72.sh.activity.AppManagerActivity;
import com.linkage.mobile72.sh.activity.MainActivity;
import com.linkage.mobile72.sh.activity.WebViewActivity;
import com.linkage.mobile72.sh.adapter.AppListAdapter;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.http.AppBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.AdActionUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.widget.AppGridViewGallery;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener2;
import com.linkage.ui.widget.PullToRefreshListView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("HandlerLeak")
public class AppFragment extends BaseFragment implements OnClickListener {
	private final static String TAG = "AppFragment";

	private View appAdHeadView;
	
	private AccountData account;
	private ArrayList<AppBean> installedApps;// 已安装app
	private List<AppBean> apps;// 服务端获取的app列表

	private RelativeLayout installedLayout;
	private AppGridViewGallery installedAppGrid;

	private PullToRefreshListView list_newapp;
	private AppListAdapter appListAdapter;
	private CircularImage avatar;
	private Button configBtn;
	private TextView mEmpty;
	private int page;
	public static boolean isLoading;
	
	public static AppFragment create(int titleRes) {
		AppFragment f = new AppFragment();
		Bundle args = new Bundle();
		f.setArguments(args);
		return f;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		account = getCurAccount();

		apps = new ArrayList<AppBean>();
		installedApps = new ArrayList<AppBean>();
		installedAppGrid = new AppGridViewGallery(getActivity(), installedApps);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_app, null);
		setTitle(view, "应用");
		configBtn = (Button) view.findViewById(R.id.jia_button);
		configBtn.setVisibility(View.INVISIBLE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			configBtn.setBackground(null);
		}else {
			configBtn.setBackgroundDrawable(null);
		}
		configBtn.setText("管理");
		configBtn.setOnClickListener(this);

		installedLayout = (RelativeLayout) view.findViewById(R.id.installed_layout);
		installedLayout.addView(installedAppGrid);

		avatar = (CircularImage) view.findViewById(R.id.user_avater);
		imageLoader.displayImage(account.getAvatar(), avatar);

		appAdHeadView = View.inflate(getActivity(), R.layout.head_fragment_app_ad, null);
		list_newapp = (PullToRefreshListView) view.findViewById(R.id.base_pull_list2);
		list_newapp.getRefreshableView().addHeaderView(appAdHeadView);
		mEmpty = (TextView) view.findViewById(R.id.empty);
		mEmpty.setText("查无数据");
		appListAdapter = new AppListAdapter(getActivity(), getDBHelper(), imageLoader, defaultOptionsPhoto, apps,
				list_newapp);
        list_newapp.getRefreshableView().setVerticalScrollBarEnabled(false);
		list_newapp.setDivider(null);
		list_newapp.setAdapter(appListAdapter);
		list_newapp.setMode(Mode.BOTH);

		list_newapp.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				page = 1;
				fetchApp();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchApp();
			}
		});
		list_newapp.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position > 0) {
					Intent i = new Intent(getActivity(), AppDetailActivity.class);
					Bundle b = new Bundle();
					b.putSerializable("app", appListAdapter.getItem(position - 1));
					i.putExtras(b);
					startActivity(i);
				}
			}
		});
		avatar.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	// 获取顶部滚动广告
	private void getRollAds() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getRollAds");
		params.put("radsType", "2");// 参看接口固定值
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getRollAds,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						LogUtils.i(TAG + "response=" + response);
						if (response.optInt("ret") == 0) {
							JSONArray array = response.optJSONArray("data");
							initRollAdView(array);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, getActivity());
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	// 初始化滚动广告的views
	private void initRollAdView(JSONArray array) {
		int imgLen = array.length();
		// 默认3个，客户端这里做下判断，为3个时展示
		//RelativeLayout ad_layout1 = (RelativeLayout)appAdHeadView.findViewById(R.id.ad_layout1);
		LinearLayout ad_layout2 = (LinearLayout)appAdHeadView.findViewById(R.id.ad_layout2);
		if (imgLen >= 2) {
			ad_layout2.setVisibility(View.VISIBLE);
			NetworkImageView[] adImgs = new NetworkImageView[imgLen];
			adImgs[0] = (NetworkImageView) appAdHeadView.findViewById(R.id.ad_image2);
			adImgs[1] = (NetworkImageView) appAdHeadView.findViewById(R.id.ad_image3);
			//adImgs[2] = (NetworkImageView) appAdHeadView.findViewById(R.id.ad_image3);
			/*int width = Utils.getWindowWidth(getActivity());
			int height = (int)(width * 0.4);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					new ViewGroup.LayoutParams(width, height));
			layoutParams.setMargins(10, 10, 10, 10);  
			adImgs[0].setLayoutParams(layoutParams);
			adImgs[0].setScaleType(ScaleType.CENTER_CROP);
			adImgs[0].setVisibility(View.GONE);*/
			
			int width1 = Utils.getWindowWidth(getActivity()) / 2 - 20;
			int height1 = (int)(width1 * 0.5);
			RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
					new ViewGroup.LayoutParams(width1, height1));
			layoutParams1.setMargins(10, 10, 10, 10);
			adImgs[0].setLayoutParams(layoutParams1);
			adImgs[0].setScaleType(ScaleType.CENTER_CROP);
			RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
					new ViewGroup.LayoutParams(width1, height1));
			layoutParams2.setMargins(10, 10, 10, 10);
			adImgs[1].setLayoutParams(layoutParams2);
			adImgs[1].setScaleType(ScaleType.CENTER_CROP);
			for (int i = 0; i < 2; i++) {
				final JSONObject obj = array.optJSONObject(i);
				adImgs[i].setImageUrl(obj.optString("url"), BaseApplication.getInstance()
						.getNetworkImageLoader());
				adImgs[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						AdActionUtils.open(getActivity(), obj.optString("action"), obj.optString("monitorParam"));
					}
				});
			}
		}
	}

	// 获取我的应用
	private void fetchMyApp() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getMyAppList");
		params.put("app_type", String.valueOf(1));
		params.put("pageSize", "100");
		/*if (loadMore && apps.size() > 0) {
			append = true;
			params.put("id", String.valueOf(apps.get(apps.size() - 1).getId()));
		} else {
			append = false;
			params.put("id", String.valueOf(0));
		}*/
		params.put("id", String.valueOf(0));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getMyAppList,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							List<AppBean> apps = AppBean.parseFromJson(response.optJSONArray("data"));
							installedApps.clear();
							installedApps.addAll(apps);
							installedAppGrid.refresh(installedApps);
						} else {
							StatusUtils.handleStatus(response, getActivity());
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, getActivity());
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	private void fetchApp() {
		getRollAds();
		fetchMyApp();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getAppList");
		params.put("app_type", String.valueOf(1));
		params.put("pageSize", Consts.PAGE_SIZE);
		params.put("page", String.valueOf(page));
		isLoading = true;
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getAppList,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						isLoading = false;
						list_newapp.onRefreshComplete();
						System.out.println("response=" + response);
						if (response.optInt("ret") == 0) {
							apps = AppBean.parseFromJson(response.optJSONArray("data"));
							if (apps.size() > 0) {
								if(apps.size() < Integer.parseInt(Consts.PAGE_SIZE)) {
									list_newapp.setMode(Mode.PULL_FROM_START);
								}else {
									list_newapp.setMode(Mode.BOTH);
								}
								appListAdapter.addAll(apps, page != 1);
								page ++;
							} else {
								list_newapp.setMode(Mode.PULL_FROM_START);
							}
						} else {
							StatusUtils.handleStatus(response, getActivity());
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						isLoading = false;
						StatusUtils.handleError(arg0, getActivity());
						list_newapp.onRefreshComplete();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}


	@Override
	public void onResume() {
		super.onResume();
		page = 1;
		fetchApp();
		account = getCurAccount();
		if(account !=null)
	        imageLoader.displayImage( account.getAvatar(), avatar); 
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_avater:
			if (!MainActivity.slideMenu.isMenuShowing()) {
				new Handler().post(new Runnable() {
					public void run() {
						MainActivity.slideMenu.showMenu(true);
					}
				});
			}
			break;
		case R.id.jia_button:
			startActivity(new Intent(getActivity(), AppManagerActivity.class));
			break;
		}
	}
	
}