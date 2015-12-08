package com.linkage.mobile72.sh.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;

public class IntrPageBActivity extends BaseActivity {

	public static IntrPageBActivity instance;
	
	private Animation leftIn, topIn;
	private ImageView image1, image2, image3, image4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
		setContentView(R.layout.activity_intr_b);
		instance = this;
		image1 = (ImageView)findViewById(R.id.image1);
		image2 = (ImageView)findViewById(R.id.image2);
		image3 = (ImageView)findViewById(R.id.image3);
		image4 = (ImageView)findViewById(R.id.image4);
		
		leftIn = AnimationUtils.loadAnimation(this, R.anim.left_in);
		topIn = AnimationUtils.loadAnimation(this, R.anim.right_in);
		leftIn.setStartOffset(500);
		leftIn.setAnimationListener(new AAnimationListener(1));
	}
	
	private class AAnimationListener implements AnimationListener {
		
		private int position;
		
		public AAnimationListener(int position) {
			this.position = position;
		}
		
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation arg0) {
			
		}
	}
	
}
