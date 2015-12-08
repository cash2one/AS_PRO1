package com.linkage.mobile72.sh.activity;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.im.bean.ClazzImage;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.MatrixImageView;
import com.linkage.ui.widget.lib.MultiColumnListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

@SuppressLint("UseSparseArrays")
public class ImgDisplayActivity extends BaseActivity implements OnClickListener{
	private final static String TAG = "ImgDisplayActivity";
	private final static int ISPRAISE = 1;
	private final static int NOTPRAISE = 0;
	private boolean isShowButton;
	
	private ViewPager vPager;
	private List<ClazzImage> imageList;
	private View[] mListViews;
	private DisplayImageOptions clazzImageOption;
	private LinearLayout buttonLinear;
	private ImageLoader mLoader;
	private TextView talkDynamic, supportNum, replyNum;
	private Button back;
	
	private HashMap<Long, Integer> talkSplitPraise;
	private HashMap<Long, Integer> talkSplitPraiseNum;
	
	private int getPosition;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_img_display);
		imageList = (ArrayList<ClazzImage>) getIntent()
				.getBundleExtra("bundle").getSerializable("images");
		getPosition = getIntent().getIntExtra("position", 0);
		isShowButton = getIntent().getBooleanExtra("isShowButton", false);
		mListViews = new View[imageList.size()];
		clazzImageOption = new DisplayImageOptions.Builder().cacheOnDisc()
				.showStubImage(R.drawable.appdetail_def)
				.showImageForEmptyUri(R.drawable.appdetail_def)
				.showImageOnFail(R.drawable.appdetail_def).resetViewBeforeLoading() // default
																				// 设置图片在加载前是否重置、复位
				.delayBeforeLoading(500) // 下载前的延迟时间
				.cacheInMemory() // default 设置下载的图片是否缓存在内存中
				.cacheOnDisc() // default 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
																		// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default 设置图片的解码类型
				.displayer(new SimpleBitmapDisplayer()) // default 还可以设置圆角图片new
														// RoundedBitmapDisplayer(20)
				.handler(new Handler()) // default
				.build();
		mLoader = ImageLoader.getInstance();
		talkSplitPraise = new HashMap<Long, Integer>();
		talkSplitPraiseNum = new HashMap<Long, Integer>();
		Log.v("sma", "mListViews" + mListViews.length);
		splitPraise();
		init();
	}

	private void splitPraise(){
		for(int i = 0;i < imageList.size();i++){
			talkSplitPraise.put(imageList.get(i).getTalkId(), imageList.get(i).getIsPraise());
			talkSplitPraiseNum.put(imageList.get(i).getTalkId(), imageList.get(i).getSupportNum());
		}
	}
	
	private void initZan(final int position){
		supportNum.setText("赞 (" + talkSplitPraiseNum.get(imageList.get(position).getTalkId()) + ")");
		if (talkSplitPraise.get(imageList.get(position).getTalkId()) == NOTPRAISE) {
			Drawable drawable = getResources().getDrawable(
					R.drawable.clazz_talk_list_zan_normal);
			supportNum.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null,
					null);
			supportNum.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sendZanFromNet(position, 1);
				}
			});
		} else {
			Drawable drawable = getResources().getDrawable(
					R.drawable.clazz_talk_list_zan_click);
			supportNum.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null,
					null);
			supportNum.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sendZanFromNet(position, 2);
				}
			});
		}
	}
	
	
	private void init() {
		vPager = (ViewPager) findViewById(R.id.vPager);
		buttonLinear = (LinearLayout) findViewById(R.id.button_linear);
		talkDynamic = (TextView) findViewById(R.id.talk_dynamic);
		supportNum = (TextView) findViewById(R.id.support_num);
		replyNum = (TextView) findViewById(R.id.reply_num);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		if(!isShowButton){
			buttonLinear.setVisibility(View.GONE);
		}
		vPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(final int arg0) {
				if(imageList.get(arg0).getTalkContent() == null || imageList.get(arg0).getTalkContent().equals("null")){
					talkDynamic.setVisibility(View.GONE);
				} else {
					talkDynamic.setVisibility(View.VISIBLE);
					talkDynamic.setText(imageList.get(arg0).getTalkContent());
				}
				initZan(arg0);
				replyNum.setText("评论 (" + imageList.get(arg0).getReplyNum() + ")");
				replyNum.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent in = new Intent(ImgDisplayActivity.this, ClazzTalkDetailActivity.class);
						in.putExtra("talkId", imageList.get(arg0).getTalkId());
						startActivity(in);
					}
				});
				setTitle("照片详情 (" + (arg0 + 1) + "/" + imageList.size() + ")");
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		vPager.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View initView;
				if (mListViews[position] == null) {
					initView = View.inflate(ImgDisplayActivity.this,
							R.layout.item_img_display, null);
					mListViews[position] = initView;
				} else {
					initView = mListViews[position];
				}
				final MatrixImageView imgView = (MatrixImageView) initView
						.findViewById(R.id.display_img);
				mLoader.displayImage(imageList.get(position).getOrgPath(), imgView,
						clazzImageOption);
				mLoader.loadImage(imageList.get(position).getOrgPath(),
						clazzImageOption, new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								imgView.setEnabled(false);
							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
									FailReason arg2) {
							}

							@Override
							public void onLoadingComplete(String arg0, View arg1,
									Bitmap arg2) {
								imgView.setEnabled(true);
							}

							@Override
							public void onLoadingCancelled(String arg0, View arg1) {
							}
						});
				container.addView(initView, 0);
				return initView;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				((ViewPager) container).removeView(mListViews[position]);
			}

			@Override
			public int getCount() {
				return imageList.size();
			}
			
		});
		vPager.setCurrentItem(getPosition, false);
		vPager.setOffscreenPageLimit(4);
		if(imageList.get(getPosition).getTalkContent() == null || imageList.get(getPosition).getTalkContent().equals("null")){
			talkDynamic.setVisibility(View.GONE);
		} else {
			talkDynamic.setVisibility(View.VISIBLE);
			talkDynamic.setText(imageList.get(getPosition).getTalkContent());
		}
		initZan(getPosition);
		replyNum.setText("评论 (" + imageList.get(getPosition).getReplyNum() + ")");
		replyNum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(ImgDisplayActivity.this, ClazzTalkDetailActivity.class);
				in.putExtra("talkId", imageList.get(getPosition).getTalkId());
				startActivity(in);
			}
		});
		setTitle("照片详情 (" + (getPosition + 1) + "/" + imageList.size() + ")");
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}
	
	/**
	 * 网络请求赞或取消赞
	 * @param type 请求类型 1为赞 2为取消赞
	 */
	private void sendZanFromNet(final int position, final int type) {
		ProgressDialogUtils.showProgressDialog("", ImgDisplayActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "praiseTalk");
		params.put("id", imageList.get(position).getTalkId() + "");
		params.put("type", type + "");
		params.put("studentid", (BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : getDefaultAccountChild().getUserid()) + "");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_praiseTalk, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							int count = response.optInt("count");
							if(type == 1){
								talkSplitPraise.put(imageList.get(position).getTalkId(), ISPRAISE);
							} else {
								talkSplitPraise.put(imageList.get(position).getTalkId(), NOTPRAISE);
							}
							talkSplitPraiseNum.put(imageList.get(position).getTalkId(), count);
							initZan(position);
							Toast.makeText(ImgDisplayActivity.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
						} else if (response.optInt("ret") == 1){
							Toast.makeText(ImgDisplayActivity.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
						}
						ProgressDialogUtils.dismissProgressBar();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, ImgDisplayActivity.this);
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
}
