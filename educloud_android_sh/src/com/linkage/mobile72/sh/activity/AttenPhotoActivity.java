package com.linkage.mobile72.sh.activity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AttenPicState;
import com.linkage.mobile72.sh.utils.ActivityUtils;
import com.linkage.mobile72.sh.utils.BitmapUtils;
import com.linkage.mobile72.sh.utils.FileUtils;
import com.linkage.mobile72.sh.utils.ImageUtils;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.lib.util.LogUtils;

public class AttenPhotoActivity extends BaseActivity implements OnClickListener {
	public static final String PHOTO_STATE = "photo_state";

	public static final String PHOTO_NAME = "photo_name";

	public static final int PHOTO_STATE_ORG = 0;

	public static final int PHOTO_STATE_EDIT = 1;

	public static final int PHOTO_STATE_DEL = 2;

	public final static int REQUEST_TAKE_PHOTO = 3001;

	private TextView btnBack, btnReTake, btnDelPhoto;

	// private NetworkImageView img;
	private ImageView img;

	String localUrl, picUrl;

	private String ImageDirPath;

	private String imgFileName;

	private File currentPhoto;

	private int changeMode = PHOTO_STATE_ORG;

	private MyCommonDialog mDialog;

	private String pressPath;

	private AttenPicState mPicState = new AttenPicState();

	private long mClassId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.atten_dsp_photo);

		btnBack = (Button) findViewById(R.id.back);
		btnReTake = (TextView) findViewById(R.id.btnReTake);
		btnDelPhoto = (TextView) findViewById(R.id.btnDelPhoto);

		img = (ImageView) findViewById(R.id.img);
		img.setImageResource(R.drawable.app_one);
		// img.setDefaultImageResId(R.drawable.app_one);

		Intent intent = getIntent();

		if (null != intent) {

			mClassId = intent.getLongExtra(AttenActivity.CURRENT_CLASS_ID, 0);

			if (null != mApp.getPicStateMap()) {

				mPicState = mApp.getPicStateMap().get(mClassId + "");

				LogUtils.d("mClassId=" + mClassId);

			} else {
				LogUtils.d("mApp.getPicStateMap()=" + mApp.getPicStateMap());
				mPicState = new AttenPicState();
			}
		} else {
			mPicState = new AttenPicState();
			LogUtils.d("intent=" + intent);
		}

		// localUrl = mApp.getLocalPath();
		// picUrl = mApp.getAtPicUrl();

		localUrl = mPicState.getLocalPath();
		picUrl = mPicState.getAtPicUrl();

		// LogUtils.e("localUrl=" + localUrl + " picUrl=" + picUrl);

		if (StringUtils.isEmpty(localUrl)) {
			useNetImg();
		} else {
			File file = new File(localUrl);
			if (file.exists() && file.isFile()) {
				// img.setImageDrawable(Drawable.createFromPath(new
				// File(localUrl)
				// .getAbsolutePath()));
				img.setImageDrawable(Drawable.createFromPath(file
						.getAbsolutePath()));

				LogUtils.e("ab localUrl:" + file.getAbsolutePath());
			} else {
				LogUtils.d("local file is invalid, file.exists="
						+ file.exists() + "  file.isFile=" + file.isFile());

				useNetImg();
			}

		}

		ImageDirPath = BaseApplication.getInstance().getWorkspaceImage()
				.getAbsolutePath();

		btnBack.setOnClickListener(this);
		btnReTake.setOnClickListener(this);
		btnDelPhoto.setOnClickListener(this);
	}

	private void useNetImg() {
		if (!StringUtils.isEmpty(picUrl)) {
			// img.setImageUrl(picUrl, BaseApplication.getInstance()
			// .getNetworkImageLoader());
			imageLoader.displayImage(picUrl, img, defaultOptionsPhoto);
		} else {
			LogUtils.d("picUrl is null!!");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finishEdit();
			break;

		case R.id.btnReTake:
			ActivityUtils.startTakePhotActivity(this, PIC_TAKE_PHOTO);
			break;

		case R.id.btnDelPhoto:
			delPhoto();
			break;
		}
	}

	private void finishEdit() {
		Intent mIntent = new Intent(AttenPhotoActivity.this,
				AttenActivity.class);

		mIntent.putExtra(PHOTO_STATE, changeMode);
		mIntent.putExtra(PHOTO_NAME, pressPath);
		setResult(RESULT_OK, mIntent);

		finish();
	}

	private void delPhoto() {
		mDialog = new MyCommonDialog(AttenPhotoActivity.this, "提示消息", "确认删除吗？",
				"取消", "确认");
		mDialog.setOkListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img.setImageResource(R.drawable.app_one);
				if (!StringUtils.isEmpty(localUrl)) {
					FileUtils.deleteFile(localUrl);
				} else {
					LogUtils.d("file has been deleted!!");
				}
				changeMode = PHOTO_STATE_DEL;
				mDialog.dismiss();
				// mApp.setTakeAttenPhoto(false);
				mPicState.setTakeAttenPhoto(false);
				finish();
			}
		});
		mDialog.setCancelListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDialog.isShowing())
					mDialog.dismiss();
			}
		});
		mDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		LogUtils.d("requestCode= " + requestCode + " resultCode=" + resultCode);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PIC_TAKE_PHOTO:
				onTakePhotoSucced(data);
				break;
			case REQ_EDIT_PHOTO:
				onEditImageSucced(data);
				break;
			}
		}
	}

	private void onTakePhotoSucced(Intent data) {
		LogUtils.d("onTakePhotoSucced:" + data);
		String filePath = mApp.getUploadImageOutputFile().toString();
		LogUtils.d("filePath:" + filePath);
		// sendPic(filePath);
		
		startActivityForResult(
				BrowseImageActivity.getEditIntent(this,
						Uri.fromFile(new File(filePath))), REQ_EDIT_PHOTO);
	}
	
	private void onEditImageSucced(Intent data) {
		LogUtils.d("onEditImageSucced:" + data);
		if(data == null)return;
		Uri uri = data.getData();

		String ly_time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime());
		String filePath = mApp.getWorkspaceImage().toString() + "/" + ly_time + ".jpeg";
		try {
			ImageUtils.savePictoFile(uri, filePath);
			changeMode = PHOTO_STATE_EDIT;
			new HandleLocalBitmapTask(filePath).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void onBackPressed() {
		finishEdit();
	}

	public Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private class HandleLocalBitmapTask extends AsyncTask<Void, Void, Void> {
		private String tempDirPath;
		private String mRawPics;

		public HandleLocalBitmapTask(String rawpics) {
			super();
			tempDirPath = BaseApplication.getInstance().getWorkspaceImage()
					.getAbsolutePath();
			mRawPics = rawpics;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ProgressDialogUtils.showProgressDialog("图片处理中",
					AttenPhotoActivity.this);
		}

		@Override
		protected Void doInBackground(Void... params) {
			 pressPath = pressPic(mRawPics);
//			pressPath = mRawPics;
			return null;
		}

		private String pressPic(String path) {
			String resultFileName = BitmapUtils.handleLocalBitmapFile(path,
					tempDirPath);
			if (resultFileName.endsWith(".png")) {
				FileInputStream fileinputstream = null;
				Bitmap bitmap = null;
				try {
					fileinputstream = new FileInputStream(resultFileName);
					FileDescriptor filedescriptor = fileinputstream.getFD();
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPurgeable = true;
					options.inInputShareable = true;
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeFileDescriptor(filedescriptor,
							null, options);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						fileinputstream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (bitmap != null) {
					String filaName = new SimpleDateFormat("yyyyMMddHHmmss",
							Locale.getDefault()).format(Calendar.getInstance()
							.getTime())
							+ UUID.randomUUID() + ".jpg";
					File aimFile = new File(tempDirPath, filaName);
					String aimPath = aimFile.getAbsolutePath();
					if (BitmapUtils.writeImageFile(aimPath, bitmap)) {
						// mApp.setLocalPath(aimPath);
						return aimPath;
					} else {
						return null;
					}
				}
				return null;
			} else {
				// mApp.setLocalPath(resultFileName);
				return resultFileName;
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			ProgressDialogUtils.dismissProgressBar();
			File file = new File(pressPath);

			if (file.exists() && file.isFile()) {
				// mApp.setLocalPath(pressPath);
				img.setImageDrawable(Drawable.createFromPath(pressPath));
				mPicState.setLocalPath(pressPath);
				LogUtils.d("pressPath" + pressPath);
			} else {
				LogUtils.d("file is invalid, file.exists=" + file.exists()
						+ "  file.isFile=" + file.isFile());
			}
			/*
			 * new Handler().postDelayed(new Runnable() {
			 * 
			 * @Override public void run() { finish(); } }, 100);
			 */
			// finish();
			finishEdit();
		}
	}

}
