package com.linkage.app.recevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by fish902 on 15/12/11.
 */
public class AppReceiver extends BroadcastReceiver {
    //定义日志标签
    private static final String TAG = "Test";
    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        //输出日志信息

        String path=intent.getExtras().getString("path");
        String P1=intent.getExtras().getString("p1");
        String P2=intent.getExtras().getString("p2");
        String P3=intent.getExtras().getString("p3");
        String P4=intent.getExtras().getString("p4");
        String P5=intent.getExtras().getString("p5");
        String P6=intent.getExtras().getString("p6");
        String P7=intent.getExtras().getString("p7");
        String P8=intent.getExtras().getString("p8");
        String P9=intent.getExtras().getString("p9");




        PackageManager pm = context.getPackageManager();
            Intent intent1 = pm.getLaunchIntentForPackage(path);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("p1",P1);
            intent1.putExtra("p2",P2);
            intent1.putExtra("p3",P3);
            intent1.putExtra("p4",P4);
            intent1.putExtra("p5",P5);
            intent1.putExtra("p6",P6);
            intent1.putExtra("p7",P7);
            intent1.putExtra("p8",P8);
            intent1.putExtra("p9",P9);


        context.startActivity(intent1);
    }
}



