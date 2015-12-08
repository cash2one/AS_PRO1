package com.linkage.mobile72.sh.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.ImgDisplayActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.im.bean.ClazzImage;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.ScaleImageButton;
import com.linkage.ui.widget.lib.MultiColumnListView.OnLoadMoreListener;
import com.linkage.ui.widget.multicolumnlist.MultiColumnPullToRefreshListView;
import com.linkage.ui.widget.multicolumnlist.MultiColumnPullToRefreshListView.OnRefreshListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class ClazzImagesFragment extends BaseFragment {
	private final static int PAGESIZE = 20;
	private final static String TAG = "ClazzImagesFragment";
	private int page = 1;
	private boolean noImages;

	private List<ClazzImage> imageList;
	private DisplayImageOptions clazzImageOption;
	private MultiColumnPullToRefreshListView listView;
	private ImageLoader mLoader;

	private long clazzId = 0;

	private BaseAdapter mAdapter;
	
	public void onRefreshInfo(long clazzId, int topIndex) {
		this.imageList.clear();
		this.clazzId = clazzId;
		this.page = 1;
		getImageFromNet(topIndex);
	}


	public ClazzImagesFragment(long clazzId) {
		this.imageList = new ArrayList<ClazzImage>();
		this.clazzId = clazzId;
	}
	
	public ClazzImagesFragment() {
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
		Bundle data = new Bundle();
		data.putLong("clazzId", clazzId);
        outState.putBundle("data", data);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			Bundle data = savedInstanceState.getBundle("data");
			this.clazzId = data.getLong("clazzId");
		}
		clazzImageOption = new DisplayImageOptions.Builder().cacheOnDisc()
				.showStubImage(R.drawable.appdetail_icon_def)
				.showImageForEmptyUri(R.drawable.appdetail_icon_def)
				.showImageOnFail(R.drawable.appdetail_icon_def).resetViewBeforeLoading() // default
																				// 设置图片在加载前是否重置、复位
				.delayBeforeLoading(50) // 下载前的延迟时间
				.cacheInMemory() // default 设置下载的图片是否缓存在内存中
				.cacheOnDisc() // default 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
																		// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.ARGB_4444) // default 设置图片的解码类型
				.displayer(new SimpleBitmapDisplayer()) // default 还可以设置圆角图片new
														// RoundedBitmapDisplayer(20)
				.handler(new Handler()) // default
				.build();

		mLoader = ImageLoader.getInstance();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = View.inflate(getActivity(), R.layout.fragment_clazz_images, null);
		listView = (MultiColumnPullToRefreshListView) view.findViewById(R.id.list);
		noImages = true;
		listView.setColumnNumber(1);
		mAdapter = new BaseAdapter() {

			@SuppressLint("NewApi")
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = inflater.inflate(
							R.layout.item_clazz_album, null);
					holder = new ViewHolder();
					holder.imageView = (ScaleImageButton) convertView
							.findViewById(R.id.thumbnail);
					holder.noImage = (Button) convertView.findViewById(R.id.no_image);
					convertView.setTag(holder);
				}
				holder = (ViewHolder) convertView.getTag();
				if(imageList == null || noImages){
					holder.imageView.setVisibility(View.GONE);
					holder.noImage.setVisibility(View.VISIBLE);
				}else{
					holder.imageView.setVisibility(View.VISIBLE);
					holder.noImage.setVisibility(View.GONE);
					holder.imageView.setImageHeight(Integer.parseInt(imageList.get(
							position).getSmallPathH()));
					holder.imageView.setImageWidth(Integer.parseInt(imageList.get(
							position).getSmallPathW()));
					mLoader.displayImage(imageList.get(position).getSmallPath(),
							holder.imageView, clazzImageOption);
					holder.imageView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent inte = new Intent(getActivity(),
									ImgDisplayActivity.class);
							Bundle bu = new Bundle();
							bu.putSerializable("images", (Serializable) imageList);
							inte.putExtra("bundle", bu);
							inte.putExtra("position", position);
							inte.putExtra("isShowButton", true);
							getActivity().startActivity(inte);
						}
					});
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public int getCount() {
				if(noImages){
					return 1;
				}else {
					return imageList.size();
				}
			}
		};
		
		listView.setSelector(R.drawable.white_bg);
		listView.setAdapter(mAdapter);
		listView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				imageList.clear();
				page = 1;
				getImageFromNet();
			}
		});
		listView.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				if(imageList.size() > page*PAGESIZE - 1){
					page++;
					getImageFromNet();
				}
			}
		});
//		getImageFromNet();
		return view;
	}

	static class ViewHolder {
		ScaleImageButton imageView;
		Button noImage;
	}

	private void getImageFromNet() {
		getImageFromNet(2);
	}
	private void getImageFromNet(int topIndex) {
		if(topIndex == 2){
			ProgressDialogUtils.showProgressDialog("", getActivity());
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getClassAlbum");
		params.put("classid", clazzId + "");
		params.put("studentid", (BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : getDefaultAccountChild().getUserid()) + "");
		params.put("page", page + "");
		params.put("pageSize", PAGESIZE + "");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_getClazzAlbum, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
//						 try {
//						 response = new JSONObject(
//						 "{\"ret\":0,\"msg\":\"成功\",\"data\":[{\"smallpath\":\"http://img3.3lian.com/2013/s1/65/d/104.jpg\",\"smallpath_h\":520,\"smallpath_w\":400,\"orgpath\":\"http://img3.3lian.com/2013/s1/65/d/104.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://img2.3lian.com/2014/f4/171/d/67.jpg\",\"smallpath_h\":440,\"smallpath_w\":400,\"orgpath\":\"http://img2.3lian.com/2014/f4/171/d/67.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://img3.3lian.com/2013/s1/65/d/113.jpg\",\"smallpath_h\":320,\"smallpath_w\":400,\"orgpath\":\"http://img3.3lian.com/2013/s1/65/d/113.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://e.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134ab14467f271f95cad1c85e8b.jpg\",\"smallpath_h\":150,\"smallpath_w\":400,\"orgpath\":\"http://e.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134ab14467f271f95cad1c85e8b.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203}"
//						 +
//						 ",{\"smallpath\":\"http://img3.3lian.com/2013/s1/65/d/104.jpg\",\"smallpath_h\":520,\"smallpath_w\":300,\"orgpath\":\"http://img3.3lian.com/2013/s1/65/d/104.jpg\",\"from\":\"Mr.XX\",\"supportnum\":36,\"replynum\":65,\"talkId\":104203},{\"smallpath\":\"http://img2.3lian.com/2014/f4/171/d/67.jpg\",\"smallpath_h\":520,\"smallpath_w\":400,\"orgpath\":\"http://img2.3lian.com/2014/f4/171/d/67.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://img3.3lian.com/2013/s1/65/d/113.jpg\",\"smallpath_h\":440,\"smallpath_w\":400,\"orgpath\":\"http://img3.3lian.com/2013/s1/65/d/113.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://e.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134ab14467f271f95cad1c85e8b.jpg\",\"smallpath_h\":520,\"smallpath_w\":400,\"orgpath\":\"http://e.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134ab14467f271f95cad1c85e8b.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203}"
//						 +
//						 ",{\"smallpath\":\"http://img3.3lian.com/2013/s1/65/d/104.jpg\",\"smallpath_h\":260,\"smallpath_w\":300,\"orgpath\":\"http://img3.3lian.com/2013/s1/65/d/104.jpg\",\"from\":\"Mr.XX\",\"supportnum\":2,\"replynum\":349,\"talkId\":104203},{\"smallpath\":\"http://img2.3lian.com/2014/f4/171/d/67.jpg\",\"smallpath_h\":320,\"smallpath_w\":400,\"orgpath\":\"http://img2.3lian.com/2014/f4/171/d/67.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://img3.3lian.com/2013/s1/65/d/113.jpg\",\"smallpath_h\":320,\"smallpath_w\":400,\"orgpath\":\"http://img3.3lian.com/2013/s1/65/d/113.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://e.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134ab14467f271f95cad1c85e8b.jpg\",\"smallpath_h\":150,\"smallpath_w\":400,\"orgpath\":\"http://e.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134ab14467f271f95cad1c85e8b.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203}"
//						 +
//						 ",{\"smallpath\":\"http://img3.3lian.com/2013/s1/65/d/104.jpg\",\"smallpath_h\":440,\"smallpath_w\":300,\"orgpath\":\"http://img3.3lian.com/2013/s1/65/d/104.jpg\",\"from\":\"Mr.XX\",\"supportnum\":90,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://img2.3lian.com/2014/f4/171/d/67.jpg\",\"smallpath_h\":520,\"smallpath_w\":400,\"orgpath\":\"http://img2.3lian.com/2014/f4/171/d/67.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://img3.3lian.com/2013/s1/65/d/113.jpg\",\"smallpath_h\":520,\"smallpath_w\":400,\"orgpath\":\"http://img3.3lian.com/2013/s1/65/d/113.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://e.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134ab14467f271f95cad1c85e8b.jpg\",\"smallpath_h\":320,\"smallpath_w\":400,\"orgpath\":\"http://e.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134ab14467f271f95cad1c85e8b.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203}"
//						 +
//						 ",{\"smallpath\":\"http://img3.3lian.com/2013/s1/65/d/104.jpg\",\"smallpath_h\":150,\"smallpath_w\":300,\"orgpath\":\"http://img3.3lian.com/2013/s1/65/d/104.jpg\",\"from\":\"Mr.XX\",\"supportnum\":112,\"replynum\":1111,\"talkId\":104203},{\"smallpath\":\"http://img2.3lian.com/2014/f4/171/d/67.jpg\",\"smallpath_h\":440,\"smallpath_w\":400,\"orgpath\":\"http://img2.3lian.com/2014/f4/171/d/67.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://img3.3lian.com/2013/s1/65/d/113.jpg\",\"smallpath_h\":440,\"smallpath_w\":400,\"orgpath\":\"http://img3.3lian.com/2013/s1/65/d/113.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203},{\"smallpath\":\"http://e.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134ab14467f271f95cad1c85e8b.jpg\",\"smallpath_h\":440,\"smallpath_w\":400,\"orgpath\":\"http://e.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134ab14467f271f95cad1c85e8b.jpg\",\"from\":\"Mr.XX\",\"supportnum\":30,\"replynum\":1349,\"talkId\":104203}]}");
//						 } catch (JSONException e) {
//						 e.printStackTrace();
//						 }
						LogUtils.v("clazzId" + clazzId);
						if (response.optInt("ret") == 0) {
							JSONArray array = response.optJSONArray("data");
							for (int i = 0; i < array.length(); i++) {
								JSONObject obj = array.optJSONObject(i);
								ClazzImage img = new ClazzImage();
								img.setSmallPath(obj.optString("smallpath"));
								img.setSmallPathW(obj.optString("smallpath_w"));
								img.setSmallPathH(obj.optString("smallpath_h"));
								img.setOrgPath(obj.optString("orgpath"));
								img.setFromWho(obj.optString("from"));
								img.setSupportNum(obj.optInt("supportnum"));
								img.setReplyNum(obj.optInt("replynum"));
								img.setTalkId(obj.optLong("talkId"));
								img.setIsPraise(obj.optInt("isPraise"));
								img.setTalkContent(obj.optString("talkContent"));
								imageList.add(img);
							}
						}
						if(imageList.size() == 0){
							noImages = true;
							listView.setColumnNumber(1);
						}else{
							listView.setColumnNumber(3);
							noImages = false;
						}
						mAdapter.notifyDataSetChanged();
						listView.onLoadMoreComplete();
						listView.onRefreshComplete();
						ProgressDialogUtils.dismissProgressBar();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, getActivity());
						if(imageList.size() == 0){
							noImages = true;
							listView.setColumnNumber(1);
						}
						LogUtils.v(imageList.size() + "#############" + imageList.size() + "##########################");
						listView.onLoadMoreComplete();
						listView.onRefreshComplete();
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
}
