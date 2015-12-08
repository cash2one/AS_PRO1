package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.utils.Utils;

public class UserGuideActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {

	public static final String FROM = "from";
	public static final String FIRST_INSTALL_STR = "first_install";
	public static final String FIRST_LOGIN_STR = "first_login";
	public static final int FIRST_INSTALL = 0;
	public static final int FIRST_LOGIN = 1;
	public int type;
	
	private ViewPager vp;  
    private ViewPagerAdapter vpAdapter;  
    private List<View> views;  
      
    //引导图片资源  
    private int[] pics; 
      
    //底部小店图片  
    private ImageView[] dots ;  
      
    //记录当前选中位置  
    private int currentIndex;  
      
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        setContentView(R.layout.activity_user_guide);  
        type = getIntent().getIntExtra(FROM, FIRST_INSTALL);
        SharedPreferences.Editor editor = mApp.getSp().edit();
        if(type == FIRST_INSTALL) {
			editor.putInt(FIRST_INSTALL_STR, 1);
			pics = new int[]{ R.drawable.intr_page_1,  
		            R.drawable.intr_page_2, R.drawable.intr_page_3, R.drawable.intr_page_4, R.drawable.intr_page_5}; 
        }else {
        	editor.putInt(FIRST_LOGIN_STR, 1);
        	pics = new int[]{ R.drawable.intr_page_login_1,  
		            R.drawable.intr_page_login_2, R.drawable.intr_page_login_3}; 
        }
        editor.commit();
        views = new ArrayList<View>();  
        int width = Utils.getWindowWidth(this);
        int height = Utils.getWindowHeight(this);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(width, height);  
          
        //初始化引导图片列表  
        for(int i=0; i<pics.length; i++) {  
            ImageView iv = new ImageView(this);  
            iv.setLayoutParams(mParams);  
            iv.setBackgroundResource(pics[i]);
            iv.setTag(i);
            iv.setOnClickListener(this);
            views.add(iv);  
        }  
        vp = (ViewPager) findViewById(R.id.viewpager);  
        //初始化Adapter  
        vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);  
        //绑定回调  
        vp.setOnPageChangeListener(this);  
          
        //初始化底部小点  
        initDots();  
          
    }  
      
    private void initDots() {  
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);  
  
        dots = new ImageView[pics.length];  
  
        //循环取得小点图片  
        for (int i = 0; i < pics.length; i++) {  
            dots[i] = (ImageView) ll.getChildAt(i);  
            dots[i].setEnabled(true);//都设为灰色  
            //dots[i].setOnClickListener(this);  
            dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应  
        }  
  
        currentIndex = 0;  
        dots[currentIndex].setEnabled(false);//设置为白色，即选中状态  
    }  
      
    /** 
     *设置当前的引导页  
     */  
    private void setCurView(int position)  
    {  
        if (position < 0 || position >= pics.length) {  
            return;  
        }
  
        vp.setCurrentItem(position);  
    }  
  
    /** 
     *这只当前引导小点的选中  
     */  
    private void setCurDot(int positon)  
    {  
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {  
            return;  
        }  
  
        dots[positon].setEnabled(false);  
        dots[currentIndex].setEnabled(true);  
  
        currentIndex = positon;  
    }  
  
    //当滑动状态改变时调用  
    @Override  
    public void onPageScrollStateChanged(int arg0) {  
        // TODO Auto-generated method stub  
    }  
  
    //当当前页面被滑动时调用  
    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
        // TODO Auto-generated method stub  
    }  
  
    //当新的页面被选中时调用  
    @Override  
    public void onPageSelected(int arg0) {  
        //设置底部小点选中状态  
        setCurDot(arg0);  
    }  
  
    @Override  
    public void onClick(View v) {  
        int position = (Integer)v.getTag(); 
        setCurView(position);  
        setCurDot(position);
    	if(position == pics.length-1) {
    		if(type == FIRST_INSTALL) {
        		Intent i = new Intent(this, LoginActivity.class);
        		startActivity(i);
        	}else {
        		Intent i = new Intent(this, MainActivity.class);
        		startActivity(i);
        	}
        	finish();
    	}
    }
    
    class ViewPagerAdapter extends PagerAdapter {  
        
        //界面列表  
        private List<View> views;  
          
        public ViewPagerAdapter (List<View> views){  
            this.views = views;  
        }  
      
        //销毁arg1位置的界面  
        @Override  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            ((ViewPager) arg0).removeView(views.get(arg1));       
        }  
      
        @Override  
        public void finishUpdate(View arg0) {  
            // TODO Auto-generated method stub  
              
        }  
      
        //获得当前界面数  
        @Override  
        public int getCount() {  
            if (views != null)  
            {  
                return views.size();  
            }  
              
            return 0;  
        }  
          
      
        //初始化arg1位置的界面  
        @Override  
        public Object instantiateItem(View arg0, int arg1) {  
              
            ((ViewPager) arg0).addView(views.get(arg1), 0);  
              
            return views.get(arg1);  
        }  
      
        //判断是否由对象生成界面  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return (arg0 == arg1);  
        }  
      
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
              
        }  
      
        @Override  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
      
        @Override  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
              
        }  
      
    }  
}
