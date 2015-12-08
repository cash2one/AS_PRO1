package com.linkage.mobile72.sh.activity.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.LoginActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.lib.util.LogUtils;

public class DialogActivity extends Activity
{
    private static final String TAG = DialogActivity.class.getName();
    
    public static final String DLG_TTITLE = "dialogTitle";
    
    public static final String DLG_INFO = "dialogInfo";
    
    private TextView tvTitle, tvInfo;
    
    private Button btnOK;
    
    protected BaseApplication mApp;
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.dialog_activity);
        
        mApp = BaseApplication.getInstance();
        
        mApp.cancelAllRequest();
        mApp.logout(true);
        mApp.stopIMService();
        
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        btnOK = (Button) findViewById(R.id.btnOK);
        
        Intent intent = getIntent();
        if (null != intent)
        {
            int iTitle, iInfo;
            iTitle = intent.getIntExtra(DLG_TTITLE, 0);
            iInfo = intent.getIntExtra(DLG_INFO, 0);
            
            if (iTitle > 0)
            {
                tvTitle.setText(iTitle);
            }
            else
            {
                LogUtils.e("lf:DialogActivity, invalid titile text resId:"
                        + iTitle);
            }
            
            if (iInfo > 0)
            {
                tvInfo.setText(iInfo);
            }
            else
            {
                LogUtils.e("lf:DialogActivity, invalid info text resId:" + iInfo);
            }
            
        }
        else
        {
            LogUtils.e("lf:DialogActivity, intent is null!!!");
        }
        
        btnOK.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                finish();
                
                // 关闭所有activity，退出到登陆页面 （要断掉tcp？）
                ActivityMgr.getInstance().clear();
                //跳转到记住密码的界面
                Intent intent = new Intent(DialogActivity.this,
                        LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        BaseApplication.getInstance().setKickOffDlgShowing(true);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        LogUtils.d("DialogActivity onPause");
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop()
    {
        super.onStop();
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        BaseApplication.getInstance().setKickOffDlgShowing(false);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        LogUtils.d("DialogActivity onBackPressed");
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && isOutOfBounds(this, event))
        {
            LogUtils.d("DialogActivity outside");
            return true;
        }
        return super.onTouchEvent(event);
    }
    
    private boolean isOutOfBounds(Activity context, MotionEvent event)
    {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context)
                .getScaledWindowTouchSlop();
        final View decorView = context.getWindow().getDecorView();
        
        return (x < -slop) || (y < -slop)
                || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }
    
}
