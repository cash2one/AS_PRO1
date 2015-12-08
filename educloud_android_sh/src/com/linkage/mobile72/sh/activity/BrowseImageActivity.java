package com.linkage.mobile72.sh.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.utils.ImageUtils;
import com.linkage.mobile72.sh.widget.ZoomableImageView;
import com.linkage.lib.util.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class BrowseImageActivity extends BaseActivity {
	
	private static final String ACTION_EDIT = "action_edit";
	
	public static Intent getViewIntent(Context from, Uri uri) {
		Intent intent = new Intent(from, BrowseImageActivity.class);
		intent.setData(uri);
		return intent;
	}
	
	public static Intent getEditIntent(Activity from, Uri uri) {
		Intent intent = new Intent(from, BrowseImageActivity.class);
		intent.setAction(ACTION_EDIT);
		intent.setData(uri);
		return intent;
	}
	
	private String action;
	private ZoomableImageView mImageView;
	private RelativeLayout titleLayout;
	private View mToolBar;
	private View mOkBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = LayoutInflater.from(this).inflate(R.layout.big_image, null);
		setContentView(v);
		setTitle(R.string.photo);
		titleLayout = (RelativeLayout)findViewById(R.id.relativelayout1);
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mImageView = (ZoomableImageView) findViewById(R.id.zoomable_image);

		action = getIntent().getAction();
		final Uri data = getIntent().getData();
		if(data == null) {
			LogUtils.e("uri data is null");
			finish();
			return;
		}
		
		String scheme = data.getScheme();
		if("http".equals(scheme)) {
			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc().showStubImage(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).build();
			BaseApplication.getInstance().imageLoader.displayImage(data.toString(), mImageView, defaultOptions);
		} else if("file".equals(scheme)) {
			viewFile(data.getPath());
		}
		if(ACTION_EDIT.equals(action)) {
			mToolBar = findViewById(R.id.big_image_tool_bar);
			mToolBar.setVisibility(View.VISIBLE);
			mOkBtn = findViewById(R.id.ok_btn);
			mOkBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setData(data);
					setResult(RESULT_OK, intent);
					finish();
				}
			});
			titleLayout.setVisibility(View.VISIBLE);
			v.setBackgroundColor(getResources().getColor(R.color.white_gray));
		}else {
			titleLayout.setVisibility(View.GONE);
			v.setBackgroundColor(getResources().getColor(R.color.half_transparent_black));
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
	}
	
	private void viewFile(String filePath) {
		try {
			int screenWidth = getResources().getDisplayMetrics().widthPixels; 
			Bitmap scaleBitmap = ImageUtils.scaleImage(filePath, screenWidth, screenWidth);
			if (scaleBitmap == null) {
				Toast.makeText(this, "无法打开图片", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				mImageView.setImageBitmap(scaleBitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
