package com.linkage.mobile72.sh.activity.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

public class SocketReceiver extends BroadcastReceiver
{
    
    @Override
    public void onReceive(Context context, Intent intent)
    {
        LogUtils.d("lf onReceive");
        int actType = intent.getIntExtra(Consts.BROADCAST_ACTTYPE_CONNECT, 0);
//        Log.d("lf", "SocketReceiver threadId= "
//                + Thread.currentThread().getId());
        
//        ActivityManager am = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);  
//        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
//        Log.d("lf", "TopActivity = " + cn.getClassName());  
        
        Intent tipsIntent;
        
        switch (actType)
        {
            case Consts.BROADCAST_REJECT:
                LogUtils.e("lf BROADCAST_REJECT:" + actType);
                
                if (BaseApplication.getInstance().isKickOffDlgShowing()
                        || BaseApplication.getInstance().isInLoginActivity())
                {
                    LogUtils.d("SocketReceiver, dialog is showing or in LoginActivity, no need to reopen!!");
                }
                else
                {
                    tipsIntent = new Intent(context, DialogActivity.class);
                    tipsIntent.putExtra(DialogActivity.DLG_TTITLE, R.string.tips);
                    tipsIntent.putExtra(DialogActivity.DLG_INFO,
                            R.string.tips_reject);
                    
                    tipsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(tipsIntent);
                }
                
               
                break;
            
            case Consts.BROADCAST_DISCONNECT:
                LogUtils.e("lf BROADCAST_DISCONNECT:" + actType);
                tipsIntent = new Intent(context, DialogActivity.class);
                tipsIntent.putExtra(DialogActivity.DLG_TTITLE, R.string.tips);
                tipsIntent.putExtra(DialogActivity.DLG_INFO,
                        R.string.tips_disconnect);
                
                tipsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(tipsIntent);
                break;
            
            default:
                LogUtils.e("lf invalid broadcast, type:" + actType);
                break;
        }
        
    }
    
}
