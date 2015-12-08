package com.linkage.mobile72.sh.receiver;

import java.util.List;

import com.linkage.lib.util.LogUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class UriLaunchReceiver extends Activity {
	
	private void restart(Context context)
	{
	     Intent intent = null;
         if (isRunning(this)) {
        	 intent = new Intent();
        	 intent.setAction("android.intent.action.MAIN");
             intent.addCategory("android.intent.category.LAUNCHER");
             intent.setComponent(ComponentName.unflattenFromString("com.linkage.mobile72.sx/.activity.SplashActivity"));
        	 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                     | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
         } else {
        	 PackageManager packageManager = this.getPackageManager(); 
        	 intent = packageManager.getLaunchIntentForPackage(getPackageName());
        	 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
        try 
        {
        	context.startActivity(intent);
		} 
        catch (ActivityNotFoundException activityNotFoundException)
		{
        	Toast.makeText(context, "找不到页面", Toast.LENGTH_SHORT).show();
			activityNotFoundException.printStackTrace();
		}
        catch (SecurityException localSecurityException)
		{
			localSecurityException.printStackTrace();
			Toast.makeText(context, "权限不足，无法跳转", Toast.LENGTH_SHORT).show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(context, "无法跳转", Toast.LENGTH_SHORT).show();
		}
	}
	
	public boolean isRunning(Context context) {
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(Integer.MAX_VALUE);
		for (RunningTaskInfo runningTaskInfo : runningTaskInfos) {
			if (runningTaskInfo.baseActivity.getPackageName().equals(context.getPackageName())) {
//				LogUtils.e("在任务栈中 + 页面数:" + runningTaskInfo.numActivities);
//				LogUtils.e("在任务栈中 + 当前页面:" + runningTaskInfo.topActivity);
				/*
				if (runningTaskInfo.numActivities > 0 && !runningTaskInfo.topActivity.getClassName().endsWith(UriLaunchReceiver.class.getSimpleName())) {
					LogUtils.e("有页面存在");
					return true;
				} else {
					LogUtils.e("没有页面了");
				}
				*/
				if (runningTaskInfo.numActivities > 1) {
					return true;
				} else {
				}
			}
		}
        return false;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		restart(this);
		finish();
	}

}
