package com.linkage.mobile72.sh.app;

import java.util.ArrayList;

import android.app.Activity;

public class ActivityList {
	public static ArrayList<Activity> activitys = new ArrayList<Activity>();

	public static void finshAllActivity() {
		if (activitys != null) {
			for (Activity activity : activitys) {
				activity.finish();
			}
		}
	}
}
