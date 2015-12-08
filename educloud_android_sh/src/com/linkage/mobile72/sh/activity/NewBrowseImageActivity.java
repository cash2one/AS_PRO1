package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.ChatImage;
import com.linkage.mobile72.sh.data.OLConfig;
import com.linkage.mobile72.sh.im.provider.Ws.MessageTable;
import com.linkage.mobile72.sh.im.provider.Ws.MessageType;
import com.linkage.ui.widget.AlbumViewPager;
import com.linkage.ui.widget.MatrixImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class NewBrowseImageActivity extends BaseActivity implements
		OnClickListener {

	private AlbumViewPager mViewPager;
	private Button mBack;

	private List<ChatImage> imageList;
	private View[] mListViews;

	private DisplayImageOptions clazzImageOption;
	private ImageLoader mLoader;
	private Long mId;
	private String mBuddyId;
	private String mChatType;
	private String olFilePath;
	
	private Cursor mCursor;

	// Cursor cur = getContentResolver()
	// .query(MessageTable.CONTENT_URI,
	// new String[] { MessageTable.BODY, MessageTable.TYPE },
	// MessageTable.ACCOUNT_NAME + "=? and "
	// + MessageTable.BUDDY_ID + " =? and "
	// + MessageTable.TYPE + " = "
	// + MessageType.TYPE_MSG_PIC + " and "
	// + MessageTable.CHAT_TYPE + " =? and "
	// + MessageTable.SENDER_ID + " is not null",
	// new String[] { mApp.getDefaultAccount().getLoginname(),
	// String.valueOf(mBuddyId), String.valueOf(mChatType) },
	// null);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_browse_image);
		OLConfig olconfig = BaseApplication.getInstance().getOlConfig();
		if(olconfig != null){
			olFilePath = "http://" + olconfig.ol_ip + ":" + olconfig.ol_port + "//";
		}

		mId = getIntent().getLongExtra("_id", 0);
		mBuddyId = getIntent().getStringExtra("buddy_id");
		mChatType = getIntent().getStringExtra("chat_type");
		// Bundle bundle = getIntent().getExtras();
		// mId = bundle.getLong("_id");
		// mBuddyId = bundle.getString("buddy_id");
		// mChatType = bundle.getString("chat_type");
		Log.d("tag_", "receive click id ==========" + mId);
		mCursor = getContentResolver().query(
				MessageTable.CONTENT_URI,
				null,
				MessageTable.ACCOUNT_NAME + "=? and " + MessageTable.BUDDY_ID
						+ " =? and " + MessageTable.TYPE + " = "
						+ MessageType.TYPE_MSG_PIC + " and "
						+ MessageTable.CHAT_TYPE + " =? and "
						+ MessageTable.SENDER_ID + " is not null",
				new String[] {
						BaseApplication.getInstance().getDefaultAccount()
								.getLoginname(), mBuddyId, mChatType }, null);
		if (null == mCursor) {
			finish();
			return;
		}
		imageList = new ArrayList<ChatImage>();
		while (mCursor.moveToNext()) {
			ChatImage image = new ChatImage();
			image.setId(mCursor.getLong(mCursor
					.getColumnIndexOrThrow(MessageTable._ID)));
			Long is_inbound = mCursor.getLong(mCursor
					.getColumnIndexOrThrow(MessageTable.IS_INBOUND));
			if (is_inbound == 1) {
				image.setBody(olFilePath
						+ mCursor.getString(mCursor
								.getColumnIndex(MessageTable.BODY)));
			} else {
				image.setBody("file:///"
						+ mCursor.getString(mCursor
								.getColumnIndex(MessageTable.BODY)));
			}
			imageList.add(image);
		}

		mViewPager = (AlbumViewPager) findViewById(R.id.viewpager);
		mBack = (Button) findViewById(R.id.back);
		mBack.setOnClickListener(this);
		mListViews = new View[imageList.size()];
		clazzImageOption = new DisplayImageOptions.Builder().cacheOnDisc()
				.showStubImage(R.drawable.appdetail_def)
				.showImageForEmptyUri(R.drawable.appdetail_def)
				.showImageOnFail(R.drawable.appdetail_def)
				.resetViewBeforeLoading() // default
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

		init();
	}

	private void init() {

		// 查询数据库初始化list

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				setTitle("聊天图片 (" + (arg0 + 1) + "/" + imageList.size() + ")");

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		mViewPager.setAdapter(new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return imageList.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				((ViewPager) container).removeView(mListViews[position]);
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View initView;
				if (mListViews[position] == null) {
					initView = View.inflate(NewBrowseImageActivity.this,
							R.layout.item_img_display, null);
					mListViews[position] = initView;
				} else {
					initView = mListViews[position];
				}

				final MatrixImageView imgView = (MatrixImageView) initView
						.findViewById(R.id.display_img);
				mLoader.displayImage(imageList.get(position).getBody(),
						imgView, clazzImageOption);
				mLoader.loadImage(imageList.get(position).getBody(),
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
							public void onLoadingComplete(String arg0,
									View arg1, Bitmap arg2) {
								imgView.setEnabled(true);
							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {

							}
						});
				container.addView(initView, 0);
				return initView;
			}

		});

		for (int i = 0; i < imageList.size(); i++) {
			if (imageList.get(i).getId() == mId) {
				Log.d("tag_", "imageList.get(i).getId() =========="
						+ imageList.get(i).getId());
				mViewPager.setCurrentItem(i);
			}
		}
		setTitle("聊天图片 (" + (mViewPager.getCurrentItem() + 1) + "/"
				+ imageList.size() + ")");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		mCursor.close();
	}
}
