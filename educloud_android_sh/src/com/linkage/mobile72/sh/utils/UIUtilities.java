package com.linkage.mobile72.sh.utils;

import com.linkage.mobile72.sh.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class UIUtilities {
	
	private static final int CHOOSE_TAKE_PHOTO = 0;
	private static final int CHOOSE_SELECT_LOCAL = 1;

	public static void showToast(Context context, int resId) {
		showToast(context, resId, false);
	}

	public static void showToast(Context context, int resId,
			boolean durationLong) {
		int duration;
		if (durationLong) {
			duration = Toast.LENGTH_LONG;
		} else {
			duration = Toast.LENGTH_SHORT;
		}
		Toast.makeText(context, resId, duration).show();
	}

	public static void showToast(Context context, String msg) {
		if(!StringUtils.isEmpty(msg))
		showToast(context, msg, false);
	}

	public static void showToast(Context context, String msg,
			boolean durationLong) {
		int duration;
		if (durationLong) {
			duration = Toast.LENGTH_LONG;
		} else {
			duration = Toast.LENGTH_SHORT;
		}
		if(!StringUtils.isEmpty(msg))
		Toast.makeText(context, msg, duration).show();
	}

	public static void showChoosePhotoDialog(final Activity activity,
			final int takePhotoReqCode, final int selectPhotoReqCode) {
		new AlertDialog.Builder(activity)
				.setTitle(R.string.choose_photo)
				.setItems(R.array.choose_photo_method,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case CHOOSE_TAKE_PHOTO:
									ActivityUtils.startTakePhotActivity(
											activity, takePhotoReqCode);
									break;
								case CHOOSE_SELECT_LOCAL:
									ActivityUtils
											.startSelectLocalPhotoActivity(
													activity,
													selectPhotoReqCode);
									break;
								case 2:
									break;
								}
								dialog.dismiss();
							}
						}).show();
	}

	public static void showChoosePhotoDialog(final Activity activity,
			final int code) {

		switch (code) {
		case 2:
			ActivityUtils.startTakePhotActivity(activity, code);
			break;
		case 3:
			ActivityUtils.startSelectLocalPhotoActivity(activity, code);
			break;
		}

	}

	public static interface DialogChooseListener {
		void onChoose();
	}
}
