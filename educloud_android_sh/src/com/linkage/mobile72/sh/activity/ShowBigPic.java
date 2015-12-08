package com.linkage.mobile72.sh.activity;

import info.emm.messenger.ChatManager;
import info.emm.messenger.MQ.VYMessage.ChatType;
import info.emm.messenger.MQ.VYMessage.Direct;
import info.emm.messenger.MQ.VYMessage.Type;
import info.emm.messenger.MQ.imageMessageBody;
import info.emm.messenger.NotificationCenter;
import info.emm.messenger.NotificationCenter.NotificationCenterDelegate;
import info.emm.messenger.Utilities;
import info.emm.messenger.VYConversation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.linkage.mobile72.sh.widget.phview.PhotoView;
import com.linkage.mobile72.sh.widget.phview.PhotoViewPager;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.ImageViewPagerAdapter;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ShowBigPic extends Activity implements NotificationCenterDelegate {
//	private ViewPager viewPager;
	private PhotoViewPager viewPager;
	private List<String> items = null;// 存放名称
	private List<String> paths = null;// 存放路径
	private String rootPath = "/";
	//ArrayList<MatrixImageView> maImageViews = null;
	ArrayList<PhotoView> maImageViews = null;
	private TextView textView;
	ImageViewPagerAdapter viewPagerAdapter;
	ArrayList<String> uriArrayList;
	ArrayList<Long> idArrayList;
	VYConversation conversation;
	DisplayImageOptions options;
	
	
	long msgid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_big_pic);
		NotificationCenter.getInstance().addObserver(this,
				NotificationCenter.processGetMedias);
		textView = (TextView) findViewById(R.id.count);
		Intent data = getIntent();
		Bundle bundle = data.getBundleExtra("data");
		String RemoteUrl = bundle.getString("RemoteUrl");
		Log.e("chat", "aaa RemoteUrl=" + RemoteUrl);
		String chatid = bundle.getString("chatid");
		int isgroup = bundle.getInt("isgroup");
		msgid = bundle.getLong("msgid");
		conversation = ChatManager.getInstance().getConversation(chatid,
				ChatType.valueOf(isgroup));
		conversation.getMedias();

		uriArrayList = new ArrayList<String>();
		idArrayList = new ArrayList<Long>();
//		viewPager = (ViewPager) findViewById(R.id.img_big);
//		maImageViews = new ArrayList<MatrixImageView>();
		viewPager = (PhotoViewPager) findViewById(R.id.img_big);
		maImageViews = new ArrayList<PhotoView>();

//		viewPagerAdapter = new ImageViewPagerAdapter(maImageViews);
		viewPagerAdapter = new ImageViewPagerAdapter(maImageViews);
		viewPager.setAdapter(viewPagerAdapter);

		File cacheDir = new File("mnt/sdcard/imageloader/Cache/");

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this)
				.threadPoolSize(3)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.discCache(new UnlimitedDiscCache(cacheDir))
				//.denyCacheImageMultipleSizesInMemory().writeDebugLogs() // Remove
				.build();// 开始构建
		// Initialize ImageLoader with configuration.
		options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
		// 设置图片加载或解码过程中发生错误显示的图片
				//.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
				//.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// 设置成圆角图片
				.build();
		ImageLoader.getInstance().init(config);// 全局初始化此配置

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				textView.setText(viewPager.getCurrentItem() + 1 + "/"
						+ maImageViews.size());

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				textView.setText(viewPager.getCurrentItem() + 1 + "/"
						+ maImageViews.size());

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				textView.setText(viewPager.getCurrentItem() + 1 + "/"
						+ maImageViews.size());

			}
		});

	}

	@Override
	protected void onStop() {
		uriArrayList.clear();
		idArrayList.clear();
		NotificationCenter.getInstance().removeObserver(this,
				NotificationCenter.processGetMedias);
		super.onStop();
	}

	@Override
	public void didReceivedNotification(int id, Object... args) {
		if (id == NotificationCenter.processGetMedias) {

			for (int i = 0; i < conversation.getImageMessageCount(); i++) {
				if (conversation.getImageMessageByIndex(i).getType() == Type.IMAGE) {
					imageMessageBody body = (imageMessageBody) conversation
							.getImageMessageByIndex(i).getBody();
					if (conversation.getImageMessageByIndex(i).getDirect() == Direct.SEND) {
						uriArrayList.add(body.getLocalUrl());
					} else {
						uriArrayList.add(body.getRemoteUrl());
						Log.e("chat", "aaa RemoteUrl2=" + body.getRemoteUrl());
					}
					idArrayList.add(conversation.getImageMessageByIndex(i)
							.getMsgId());
				}
			}

			for (int i = 0; i < uriArrayList.size(); i++) {
//				MatrixImageView view = new MatrixImageView(this);
				PhotoView view = new PhotoView(this);
				view.setZoomable(true);
				view.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				maImageViews.add(view);
			}
			viewPagerAdapter.notifyDataSetChanged();

			for (int i = 0; i < maImageViews.size(); i++) {
				if (uriArrayList.get(i).startsWith("http")) {
					Utilities.ShowProgressDialog(this, "正在加载图片...");
					synchronized (this) {
						ImageLoader.getInstance().displayImage(
								uriArrayList.get(i), maImageViews.get(i).getImageView(),
								options);
					}
					Utilities.HideProgressDialog(this);
				} else {
					String uri = "file://" + uriArrayList.get(i);
					Utilities.ShowProgressDialog(this, "正在加载图片...");
					synchronized (this) {
						ImageLoader.getInstance().displayImage(uri,
								maImageViews.get(i), options);
					}
					Utilities.HideProgressDialog(this);
				}
			}
			for (int i = 0; i < uriArrayList.size(); i++) {
				if (idArrayList.get(i) == msgid) {
					viewPager.setCurrentItem(i);
				}
			}
			textView.setText(viewPager.getCurrentItem() + 1 + "/"
					+ maImageViews.size());
		}
	}

}
