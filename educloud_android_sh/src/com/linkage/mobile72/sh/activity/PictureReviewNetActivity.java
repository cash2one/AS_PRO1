package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.lib.util.LogUtils;

public class PictureReviewNetActivity extends BaseActivity implements
		View.OnClickListener {

	public static final String RES = "res";
	private ArrayList<String> imgList = new ArrayList<String>();// 图像LOCAL_URL
	private int position;
	public static final String TITLE = "title";
	private String title;
	private ImageAdapter imageAdapter;
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_review);
		Intent i = getIntent();
		imgList = i.getExtras().getStringArrayList(RES);
		position = i.getExtras().getInt("position", 0);
		// position = position + 1;
            if (imgList == null || imgList.size() < 1) {
			finish();
			return;
		}
        title = i.getExtras().getString(TITLE);
		LogUtils.e("PictureReviewNetActivity" + position + "");
		if(title != null && !"".equals(title)) {
			setTitle(title);
		}else {
			setTitle(position + 1 + " / " + imgList.size());
		}
		Button back = (Button) findViewById(R.id.back);
		Button delete = (Button) findViewById(R.id.set);
		back.setOnClickListener(this);
		delete.setVisibility(View.GONE);
		pager = (ViewPager) findViewById(R.id.view_pager);
		imageAdapter = new ImageAdapter(imgList, this, position);
		pager.setAdapter(imageAdapter);
		pager.setCurrentItem(position);
		pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
            	if(title != null && !"".equals(title)) {
        			setTitle(title);
        		}else {
        			int mPosition = pager.getCurrentItem() + 1;
                    String text = mPosition + " / " + pager.getAdapter().getCount();
                    setTitle(text);
        		}
                position = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

	}

	class ImageAdapter extends PagerAdapter {
		private ArrayList<String> imgList;
		private Context mContext;
		private int deletePosition;

		public ImageAdapter(ArrayList<String> imgList, Context mContext,
				int deletePosition) {
			// TODO Auto-generated constructor stub
			this.imgList = imgList;
			this.mContext = mContext;
			this.deletePosition = deletePosition;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imgList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			// TODO Auto-generated method stub
			return view.equals(object);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return POSITION_NONE;
		}

		@Override
		public void finishUpdate(ViewGroup container) {
			// TODO Auto-generated method stub
			super.finishUpdate(container);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			ImageView i = new ImageView(mContext);
			String iFilePath = imgList.get(position);
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
            imageLoader.displayImage(iFilePath, i, defaultOptionsPhoto);
			((ViewPager) container).addView(i);
			return i;
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

}
