package com.linkage.mobile72.sh.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.ClazzTalkListAdapter;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.http.ClazzTalk;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener2;
import com.linkage.ui.widget.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class ClazzDynamicFragment extends BaseFragment{
	
	public static boolean NEEDFETCH = false;
	
	private final static String TAG = "ClazzDynamicFragment";
	public final static int TYPE_CLAZZ = 0;
	public final static int TYPE_PERSONAL = 1;
	
	private int type;
	private long clazzId;
	
	private int page = 1;
	private List<ClazzTalk> talkList;
	private PullToRefreshListView mList;
	private ClazzTalkListAdapter mAdapter;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private TextView empty;
	
	public ClazzDynamicFragment(long clazzId, int getType) {
		this.clazzId = clazzId;
		this.type = getType;
	}
	
	public ClazzDynamicFragment() {
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
		Bundle data = new Bundle();
		data.putLong("clazzId", clazzId);
		data.putInt("type", type);
        outState.putBundle("data", data);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			Bundle data = savedInstanceState.getBundle("data");
			this.clazzId = data.getLong("clazzId");
			this.type = data.getInt("type");
		}
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheOnDisc()
				.showStubImage(R.drawable.appdetail_icon_def)
				.showImageForEmptyUri(R.drawable.appdetail_icon_def)
				.showImageOnFail(R.drawable.appdetail_icon_def).resetViewBeforeLoading() // default
																				// 设置图片在加载前是否重置、复位
				.delayBeforeLoading(500) // 下载前的延迟时间
				.cacheInMemory() // default 设置下载的图片是否缓存在内存中
				.cacheOnDisc() // default 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
																		// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.ARGB_4444) // default 设置图片的解码类型
				.displayer(new SimpleBitmapDisplayer()) // default 还可以设置圆角图片new
														// RoundedBitmapDisplayer(20)
				.handler(new Handler()) // default
				.build();
		fetchData();
	}
	
	public void onRefreshInfo(long clazzId, int topIndex) {
		this.clazzId = clazzId;
		talkList.clear();
		page = 1;
		fetchData(topIndex);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(NEEDFETCH){
			fetchData();
			NEEDFETCH = false;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = View.inflate(getActivity(), R.layout.fragment_clazz_dynamic, null);
		talkList = new ArrayList<ClazzTalk>();
		empty = (TextView) view.findViewById(R.id.empty);
		mList = (PullToRefreshListView) view.findViewById(R.id.pullToRefreshListView);
		mList.setMode(Mode.BOTH);
		mList.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				talkList.clear();
				page = 1;
				fetchData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				page ++;
				fetchData();
			}
		});
		mAdapter = new ClazzTalkListAdapter(getActivity(), imageLoader, options, talkList, getDefaultAccountChild(), view, type);
		mList.setAdapter(mAdapter);
		return view;
	}
	
	private void fetchData() {
		if(type == TYPE_CLAZZ){
			fetchData(1);
		}else if(type == TYPE_PERSONAL){
			fetchData(3);
		}
	}
	private void fetchData(int topIndex) {
		if((type == TYPE_CLAZZ && topIndex == 1) || (type == TYPE_CLAZZ && topIndex == 3)){
			if(page == 1){
				ProgressDialogUtils.showProgressDialog("", getActivity());
			}
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("position", type + "");
		params.put("commandtype", "getClassTalk");
		params.put("classid", clazzId + "");
		params.put("studentid", (BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : getDefaultAccountChild().getId()) + "");
		params.put("page", page + "");
		params.put("pageSize", Consts.PAGE_SIZE);
		
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_getClazzTalk, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
							List<ClazzTalk> temp = ClazzTalk.parseFromJson(response.optJSONArray("data"));
							if(temp != null && temp.size() > 0) {
								mAdapter.addAll(temp, page != 1);
							}
							if(temp != null && temp.size() == Integer.parseInt(Consts.PAGE_SIZE)) {
								mList.setMode(Mode.BOTH);
							}else {
								mList.setMode(Mode.PULL_FROM_START);
							}
							if (mAdapter.isEmpty()) {
								empty.setVisibility(View.VISIBLE);
							} else {
								empty.setVisibility(View.GONE);
							}
							mList.onRefreshComplete();
						}else {
							T.showShort(getActivity(),response.optString("msg"));
							mList.onRefreshComplete();
						}
						ProgressDialogUtils.dismissProgressBar();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, getActivity());
						mList.onRefreshComplete();
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
}
