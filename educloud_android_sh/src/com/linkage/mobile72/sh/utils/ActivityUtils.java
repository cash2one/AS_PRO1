package com.linkage.mobile72.sh.utils;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseApplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class ActivityUtils {
	
	public static void startTakePhotActivity(Activity activity, int requestCode) {
		BaseApplication app = BaseApplication.getInstance();
		if(!app.isSDCardAvailable()) {
			UIUtilities.showToast(activity, R.string.sd_card_unavaiable);
			return;
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(app.getUploadImageOutputFile()));
		activity.startActivityForResult(intent, requestCode);
	}
	
	public static void startSelectLocalPhotoActivity(Activity activity, int requestCode) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(intent, requestCode);
	}
}
