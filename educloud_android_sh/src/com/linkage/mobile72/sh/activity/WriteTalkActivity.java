package com.linkage.mobile72.sh.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.TalkGridVAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Group;
import com.linkage.mobile72.sh.http.ParamItem;
import com.linkage.mobile72.sh.http.WDJsonObjectMultipartRequest;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ActivityUtils;
import com.linkage.mobile72.sh.utils.BitmapUtils;
import com.linkage.mobile72.sh.utils.ImageUtils;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.utils.multipic.ImgFileListActivity;
import com.linkage.mobile72.sh.utils.multipic.ImgsActivity;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.CustomDialog;

@SuppressLint("NewApi")
public class WriteTalkActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_RECEIVER = 1;
	private static final int REQUEST_ClASS = 2;
	private static final int REQUEST_PIC = 3;
	private static final int REQUEST_CAMERA = 5;
	private static final int REQUEST_ADD_PIC = 6;
	private static final int REQUEST_VIDEO = 7;

	private final int TALK_TYPE_TEXT = -1;
	private final int TALK_TYPE_PICS = 0;
	private final int TALK_TYPE_VIDEO = 1;

	private final int TALK_MODE_INIT = -1;
	private final int TALK_MODE_PICS = 0;
	private final int TALK_MODE_VIDEO = 1;

	private final String TAG = WriteTalkActivity.this.getClass()
			.getSimpleName();

	private final int INPUT_CHARACTER_MAX = 250;

	private RelativeLayout rlySend, rlyTitle;
	private TextView tvCancel, tvTitle, tvPublish, tvInputTotal, tvSendClass,
			tvSendAll;

	private EditText edInput;

	private GridView picGrid;

	private RelativeLayout videolayout;
	private ImageView imgVideo,imgVideoDel,imgVideoplay;

	private MyCommonDialog dialog;

	private ArrayList<String> choosePics = new ArrayList<String>();

	private String picUrls;
	private String videoUrl;

	private TextWatcher mTextWatcher;

	private TalkGridVAdapter imgsAdapter;

	private CustomDialog choosePicDialog;

	private boolean isFirstSelect = true;

	// private boolean isPic = true;

	private int currentMode = TALK_MODE_INIT;

	private String messageContent;
	private ProgressDialog mProgressDialog;

	private String videoPath, tempVideoPath;// 这里需要注意下逻辑

	private String classIds;

	private String picWids, picHeights;
	
	private ArrayList<Group> chooseReceivers;
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
		Bundle data = new Bundle();
		data.putBoolean("isTeacher", isTeacher());
        outState.putBundle("data", data);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			Bundle data = savedInstanceState.getBundle("data");
			boolean isTeacher = data.getBoolean("isTeacher");
			setIsTeacher(isTeacher);
		}
		setContentView(R.layout.activity_talk);

		tvCancel = (TextView) findViewById(R.id.tvCancel);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvPublish = (TextView) findViewById(R.id.tvPublish);
		tvInputTotal = (TextView) findViewById(R.id.tvInputTotal);
		tvSendClass = (TextView) findViewById(R.id.tvSendClass);
		tvSendAll = (TextView) findViewById(R.id.tvSendAll);
		rlySend = (RelativeLayout) findViewById(R.id.rlySend);
		rlyTitle = (RelativeLayout) findViewById(R.id.rlyTitle);
		
		edInput = (EditText) findViewById(R.id.edInput);
		picGrid = (GridView) findViewById(R.id.pic_gridview);

		videolayout = (RelativeLayout)findViewById(R.id.video_layout);
		imgVideo = (ImageView) findViewById(R.id.imgVideo);
		imgVideoDel  = (ImageView) findViewById(R.id.video_del);
		imgVideoplay  = (ImageView) findViewById(R.id.video_play);
		
		if(isTeacher()) {
			rlyTitle.setBackgroundResource(R.drawable.title_top_bg_green);
		}else {
			rlyTitle.setBackgroundResource(R.drawable.title_top_bg);
		}
		
		tvCancel.setOnClickListener(this);
		tvPublish.setOnClickListener(this);
		tvSendClass.setOnClickListener(this);
		tvSendAll.setOnClickListener(this);
		rlySend.setOnClickListener(this);

		currentMode = TALK_MODE_INIT;

		mTextWatcher = new TextWatcher() {
			private CharSequence temp;
			private int editStart;
			private int editEnd;

			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {

				temp = s;
				LogUtils.d("beforeTextChanged-->s:" + s);
			}

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {

				LogUtils.d("onTextChanged-->s:" + s);
			}

			@Override
			public void afterTextChanged(Editable s) {

				LogUtils.d("afterTextChanged-->s:" + s);

				editStart = edInput.getSelectionStart();
				editEnd = edInput.getSelectionEnd();

				if (temp.length() > INPUT_CHARACTER_MAX) {

					Toast.makeText(WriteTalkActivity.this,
							R.string.input_limit, Toast.LENGTH_SHORT).show();

					s.delete(editStart - 1, editEnd);
					int tempSelection = editStart;
					edInput.setText(s);
					edInput.setSelection(tempSelection);

				} else {

					String sOrg = getResources().getString(R.string.total_word);
					String sFinalOrg = String.format(sOrg, INPUT_CHARACTER_MAX
							- s.length());
					tvInputTotal.setText(sFinalOrg);
				}
			}
		};

		edInput.addTextChangedListener(mTextWatcher);

		String sOrg = getResources().getString(R.string.total_word);
		String sFinalOrg = String.format(sOrg, INPUT_CHARACTER_MAX);
		tvInputTotal.setText(sFinalOrg);

		imgsAdapter = new TalkGridVAdapter(this, choosePics);

		// if (choosePics.size() < 1) {
		// picGrid.setVisibility(View.INVISIBLE);
		// } else {
		// picGrid.setVisibility(View.VISIBLE);
		// }

		picGrid.setAdapter(imgsAdapter);

		picGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (choosePics.size() <= 9 && position == choosePics.size()) {

					// 点的是加号
					getPhoto();

				} else {

					// 点的都是图片
					Intent intent = new Intent(WriteTalkActivity.this,
							PictureReviewActivity.class);
					intent.putStringArrayListExtra(PictureReviewActivity.RES,
							choosePics);
					intent.putExtra("position", position);
					startActivityForResult(intent, REQUEST_PIC);
				}
			}
		});

		imgVideoplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LogUtils.i("------------------>imgVideo OnClickListener");
				playVideo();
			}
		});

		imgVideoDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LogUtils.i("------------------>imgVideo onLongClick");
				delVideo();
			}
		});
	}
	
	

	private void playVideo() {
		LogUtils.d("playVideo...videoPath=" + videoPath);
		Uri uri = Uri.parse(videoPath);
		// 调用系统自带的播放器
//		 Intent intent = new Intent(Intent.ACTION_VIEW);
//		 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		 intent.putExtra("oneshot", 0);
//		 intent.putExtra("configchange", 0);
//		
//		 LogUtils.v("URI:::::::::" + uri.toString());
//		 intent.setDataAndType(uri, "video/*");

		//为vivo手机播放本地视频而修改
		 Uri data = Uri.fromFile(new File(videoPath));  
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(data, "video/3gp");

		startActivity(intent);

	}

	private void delVideo() {
		dialog = new MyCommonDialog(this, getResources().getString(
				R.string.tips), getResources().getString(
				R.string.confirm_del_video), getResources().getString(
				R.string.cancel), getResources().getString(R.string.confirm));

		dialog.setOkListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}

				videoPath = "";
				choosePics.clear();
				currentMode = TALK_MODE_INIT;
				videolayout.setVisibility(View.GONE);
				picGrid.setVisibility(View.VISIBLE);
				imgsAdapter.notifyDataSetChanged();
			}
		});

		dialog.setCancelListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});

		dialog.show();

	}

	private void getPhoto() {

		if (choosePics.size() >= 9) {

			Toast.makeText(WriteTalkActivity.this, R.string.max_photo,
					Toast.LENGTH_LONG).show();
			return;
		}

		LinearLayout lyDlg;

		Button btnTakePhoto, btnAlbum, btnCancel, btnVideo;

		choosePicDialog = new CustomDialog(WriteTalkActivity.this, true);
		choosePicDialog.setCustomView(R.layout.dlg_choose_talk);

		Window window = choosePicDialog.getDialog().getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		lyDlg = (LinearLayout) choosePicDialog.findViewById(R.id.dialog_layout);
		lyDlg.setPadding(0, 0, 0, 0);

		btnTakePhoto = (Button) choosePicDialog.findViewById(R.id.btnTakePhoto);
		btnAlbum = (Button) choosePicDialog.findViewById(R.id.btnAlbum);
		btnCancel = (Button) choosePicDialog.findViewById(R.id.btnCancel);
		btnVideo = (Button) choosePicDialog.findViewById(R.id.btnVideo);

		// if (isFirstSelect) {
		//
		// isFirstSelect = false;
		//
		// } else {
		//
		// btnVideo.setVisibility(View.GONE);
		// btnTakePhoto
		// .setBackgroundResource(R.drawable.atten_btn_down_selector);
		// }

		LogUtils.d("1 currentMode = " + currentMode);
		if (currentMode == TALK_MODE_PICS) {
			btnVideo.setVisibility(View.INVISIBLE);
			btnTakePhoto
					.setBackgroundResource(R.drawable.atten_btn_down_selector);
		}

		btnTakePhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choosePicDialog.dismiss();
				// isPic = true;
				currentMode = TALK_MODE_PICS;
				// 启动拍照
				// takePhoto();
				ActivityUtils.startTakePhotActivity(WriteTalkActivity.this,
						PIC_TAKE_PHOTO);
			}
		});

		btnVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choosePicDialog.dismiss();
				// isPic = false;
				currentMode = TALK_MODE_VIDEO;

				// picGrid.setVisibility(View.GONE);
				// imgVideo.setVisibility(View.VISIBLE);

				videoPath = "";
				videoUrl = "";

				// 启动拍视频
				makeVideo();
			}
		});

		btnAlbum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choosePicDialog.dismiss();
				Intent picIntent = new Intent(WriteTalkActivity.this,
						ImgFileListActivity.class);
				picIntent.putExtra(Consts.CHOOSE_PIC_TOTAL, choosePics.size());
				picIntent.putExtra(Consts.CHOOSE_PIC_MAX, 9);
				// LogUtils.e("----list000-->size=" + choosePics.size());
				startActivityForResult(picIntent, REQUEST_ADD_PIC);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choosePicDialog.dismiss();
			}
		});

		choosePicDialog.setCancelable(true);
		choosePicDialog.show();

	}

	// private void takePhoto() {
	// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	// // 下面这句指定调用相机拍照后的照片存储的路径
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
	// Environment.getExternalStorageDirectory(), "xiaoma.jpg")));
	// startActivityForResult(intent, PIC_TAKEPHOTO);
	//
	// }
	Camera mCamera = null;
	private void makeVideo() {
	    if (mCamera != null)
        {
            freeCameraResource();
        }
        try
        {
            mCamera = Camera.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            freeCameraResource();
        }
        if (mCamera == null)
            return;
        
        try
        {
            mCamera.getParameters();
        }
       
        catch(RuntimeException re)
        {
            re.printStackTrace();
            mCamera= null;
            showTip();
            return;
        }
        freeCameraResource();
		Intent intent = new Intent(WriteTalkActivity.this,
				PickVideoActivity.class);

		startActivityForResult(intent, REQUEST_VIDEO);
	}
	
	private void showTip() {
        final MyCommonDialog dialog = new MyCommonDialog(this, "提示", "摄像头功能被禁用，请手动打开设置","", getResources().getString(R.string.confirm));

        dialog.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

	private void freeCameraResource()
    {
        if (mCamera != null)
        {
            try {
                mCamera.setPreviewCallback(null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mCamera.stopPreview();
                mCamera.lock();
                mCamera.release();
                mCamera = null;
            }
        }
    }
	
	// 获取选择的接收人的显示文本
    private String getCaptioByChooseReceiver(ArrayList<Group> groups) {
        if (groups != null && groups.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (Group group : groups) {
                if (group.isChecked()) {
                    sb.append(group.getId());
                    sb.append(",");
                } 
            }
            if (sb.length() > 0) {
                if (',' == (sb.charAt(sb.length() - 1))) {
                    sb = sb.deleteCharAt(sb.length() - 1);
                }
            }
            return sb.toString();
        }
        return "";
    }
    
    
 // 获取选择的接收人的显示文本
    private String getNameByChooseReceiver(ArrayList<Group> groups) {
        if (groups != null && groups.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (Group group : groups) {
                if (group.isChecked()) {
                    sb.append(group.getName());
                    sb.append(",");
                } 
            }
            if (sb.length() > 0) {
                if (',' == (sb.charAt(sb.length() - 1))) {
                    sb = sb.deleteCharAt(sb.length() - 1);
                }
            }
            return sb.toString();
        }
        return "";
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {

		switch (requestCode) {
		    case REQUEST_RECEIVER:
	            if (data != null) {
	                chooseReceivers = (ArrayList<Group>) data.getExtras().getSerializable(
	                        SelectReceiverActivity.RECEIVER_RESULT);
	                classIds = (getCaptioByChooseReceiver(chooseReceivers));
	                Log.d(TAG, "onActivityResult | classIds == "+classIds);
	                String getClazzString = getNameByChooseReceiver(chooseReceivers);
	                tvSendAll.setText(getClazzString);
	            }
	            break;
		case REQUEST_PIC:
			if (data != null) {
				ArrayList<String> rawPics = data.getExtras()
						.getStringArrayList(ImgsActivity.PIC_RESULT);
				if (rawPics.size() < 1) {
					// picGrid.setVisibility(View.INVISIBLE);
					currentMode = TALK_MODE_INIT;
					choosePics.clear();
					imgsAdapter.addData(choosePics, false);
				} else {
					currentMode = TALK_MODE_PICS;
					picGrid.setVisibility(View.VISIBLE);
					choosePics.clear();
					new HandleLocalBitmapTask(rawPics).execute();
				}
			} else {
				if (choosePics.size() > 0) {
					currentMode = TALK_MODE_PICS;
				} else {
					currentMode = TALK_MODE_INIT;
				}
			}
			break;

		case REQUEST_ADD_PIC:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					ArrayList<String> rawPics = data.getExtras()
							.getStringArrayList(ImgsActivity.PIC_RESULT);
					if (rawPics.size() < 1) {
						currentMode = TALK_MODE_INIT;
						picGrid.setVisibility(View.INVISIBLE);
						choosePics.clear();
						imgsAdapter.addData(choosePics, false);
					} else {
						currentMode = TALK_MODE_PICS;
						picGrid.setVisibility(View.VISIBLE);
						new HandleLocalBitmapTask(rawPics).execute();
					}

				} else {

					if (choosePics.size() > 0) {
						currentMode = TALK_MODE_PICS;
					} else {
						currentMode = TALK_MODE_INIT;
					}
				}
			} else {
				if (choosePics.size() > 0) {
					currentMode = TALK_MODE_PICS;
				} else {
					currentMode = TALK_MODE_INIT;
				}
			}

			break;

		// case PIC_TAKEPHOTO:
		// File temp = new File(Environment.getExternalStorageDirectory()
		// + "/xiaoma.jpg");
		// startPhotoCrop(Uri.fromFile(temp));
		// break;
		//
		// case PIC_OK:
		// if (data != null) {
		// new Handler().postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// setPicToView(data);
		// }
		// }, 500);
		// }
		// break;

		case PIC_TAKE_PHOTO:
			if (resultCode == RESULT_OK) {
				currentMode = TALK_MODE_PICS;
				onTakePhotoSucced(data);
			} else {
				if (choosePics.size() > 0) {
					currentMode = TALK_MODE_PICS;
				} else {
					currentMode = TALK_MODE_INIT;
				}
			}
			LogUtils.d("PIC_TAKE_PHOTO currentMode = " + currentMode);
			break;

		case REQ_EDIT_PHOTO:
			if (resultCode == RESULT_OK) {
				onEditImageSucced(data);
			} else {
				if (choosePics.size() > 0) {
					currentMode = TALK_MODE_PICS;
				} else {
					currentMode = TALK_MODE_INIT;
				}
			}
			break;

		case REQUEST_VIDEO:
			LogUtils.d("video resultcode=" + resultCode);
			if (resultCode == RESULT_OK) {
				if (data != null) {
					String vFilePath = data
							.getStringExtra(PickVideoActivity.PICK_VIDEO_PATH);

					if (StringUtils.isEmpty(vFilePath)) {

						LogUtils.e("vFilePath is empty!!");
						picGrid.setVisibility(View.VISIBLE);
						videolayout.setVisibility(View.GONE);
						Toast.makeText(WriteTalkActivity.this, "获取视频失败，有可能是麦克风功能被禁止，请手动设置后重试",
								Toast.LENGTH_SHORT).show();
						currentMode = TALK_MODE_INIT;

					} else {

						currentMode = TALK_MODE_VIDEO;
						videoPath = vFilePath;
						pickVideoSucess(vFilePath);
					}

				} else {
					currentMode = TALK_MODE_INIT;
					LogUtils.e("PickVideoActivity take no dataF!!!");
					picGrid.setVisibility(View.VISIBLE);
					videolayout.setVisibility(View.GONE);
					Toast.makeText(WriteTalkActivity.this, "获取视频失败，有可能是麦克风功能被禁止，请手动设置后重试",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				currentMode = TALK_MODE_INIT;
				picGrid.setVisibility(View.VISIBLE);
				videolayout.setVisibility(View.GONE);
			}
		}
	}

	private void pickVideoSucess(String path) {

		

		Bitmap bmp = getVideoThumbnail(path, 96, 96,
				MediaStore.Images.Thumbnails.MICRO_KIND);

		if(bmp == null)
		{
		    currentMode = TALK_MODE_INIT;
            LogUtils.e("PickVideoActivity take no dataF!!!");
            picGrid.setVisibility(View.VISIBLE);
            videolayout.setVisibility(View.GONE);
            Toast.makeText(WriteTalkActivity.this, "获取视频失败，有可能是麦克风功能被禁止，请手动设置后重试",
                    Toast.LENGTH_SHORT).show();
            return;
		}
		
		picGrid.setVisibility(View.INVISIBLE);
        videolayout.setVisibility(View.VISIBLE);
        choosePics.clear();
        
		String spath = saveBitmapFile(bmp);

		ArrayList<String> rawPics = new ArrayList<String>();
		rawPics.add(spath);
		new HandleLocalBitmapTask(rawPics, true).execute();

	}

	/**
	 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	private Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		if(bitmap == null)
		{
		    return null;
		}
//		System.out.println("w" + bitmap.getWidth());
//		System.out.println("h" + bitmap.getHeight());
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		Matrix m = new Matrix();
	      m.setRotate(90,(float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
	      final Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
		return bm;
	}

	private void onTakePhotoSucced(Intent data) {
		LogUtils.d("onTakePhotoSucced:" + data);
		String filePath = mApp.getUploadImageOutputFile().toString();
		LogUtils.d("filePath:" + filePath);
		// sendPic(filePath);

		startActivityForResult(
				BrowseImageActivity.getEditIntent(this,Uri.fromFile(new File(filePath))), REQ_EDIT_PHOTO);
	}

	private void onEditImageSucced(Intent data) {
		LogUtils.d("onEditImageSucced:" + data);
		if (data == null)
			return;
		Uri uri = data.getData();

		String ly_time = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.getDefault()).format(Calendar.getInstance().getTime());
		String filePath = mApp.getWorkspaceImage().toString() + "/" + ly_time
				+ ".jpeg";
		try {
			ImageUtils.savePictoFile(uri, filePath);
			ArrayList<String> rawPics = new ArrayList<String>();
			rawPics.add(filePath);
			picGrid.setVisibility(View.VISIBLE);

			if (rawPics.size() > 0) {
				currentMode = TALK_MODE_PICS;
			} else {
				currentMode = TALK_MODE_INIT;
			}
			LogUtils.d("REQ_EDIT_PHOTO currentMode = " + currentMode);

			new HandleLocalBitmapTask(rawPics).execute();
		} catch (IOException e) {
			e.printStackTrace();
			currentMode = TALK_MODE_INIT;
		}

	}

	// private void setPicToView(Intent picdata) {
	// Bundle extras = picdata.getExtras();
	// if (extras != null) {
	// Bitmap photo = extras.getParcelable("data");
	// String path = saveBitmapFile(photo);
	// picGrid.setVisibility(View.VISIBLE);
	// ArrayList<String> rawPics = new ArrayList<String>();
	// rawPics.add(path);
	// new HandleLocalBitmapTask(rawPics).execute();
	// }
	// }

	public String saveBitmapFile(Bitmap bitmap) {
		// File file = mApp.getUploadImageOutputFile();// 将要保存图片的路径
		File file = createPhotoByTime();
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return file.getAbsolutePath();
	}

	private File createPhotoByTime() {
		String ImageDirPath = BaseApplication.getInstance().getWorkspaceImage()
				.getAbsolutePath();

		String sfileName = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.getDefault()).format(Calendar.getInstance().getTime())
				+ ".jpeg";

		return new File(ImageDirPath, sfileName);

		// return new File(mApp.getWorkspaceImage(), ImageDirPath + "/" +
		// sfileName);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tvCancel:
			onBackPressed();
			break;

		case R.id.tvPublish:
			publish();
			break;

		case R.id.tvSendClass:
		case R.id.tvSendAll:
		case R.id.rlySend:
		    Intent receiverIntent = new Intent(this, SelectClassActivity.class);
//            Bundle b = new Bundle();
//            b.putSerializable(SelectReceiverActivity.RECEIVER_RESULT, chooseReceivers);
//            receiverIntent.putExtras(b);
            startActivityForResult(receiverIntent, REQUEST_RECEIVER);
			break;

		default:
			LogUtils.d("odd view on click, v.getId()=" + v.getId());
			break;
		}
	}

	private void publish() {

		messageContent = edInput.getText().toString().trim();

		if (TextUtils.isEmpty(messageContent)&&(choosePics.size() == 0)) {
			T.showShort(this, "请填写消息内容");
			return;
		}
		
		if (TextUtils.isEmpty(classIds)) {
            T.showShort(this, "请选择发送范围");
            return;
        }

		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCancelable(false);
		}

		dialog = new MyCommonDialog(this, "提示消息", "您确认现在发送吗？", "取消", "确定");
		dialog.setOkListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}

				picUrls = "";
				picWids = "";
				picHeights = "";
				videoUrl = "";

				if (choosePics.size() > 0) {
//					new CaculatePicSizeTask().execute();
				    sendPic(0);
				} else {
					sendMessage(TALK_TYPE_TEXT);
				}

				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
		dialog.setCancelListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}

	@Override
	public void onBackPressed() {
		if (!TextUtils.isEmpty(edInput.getText().toString())
				|| choosePics.size() > 0) {
			dialog = new MyCommonDialog(this, getResources().getString(
					R.string.tips), getResources().getString(
					R.string.confirm_quit_edit_talk), getResources().getString(
					R.string.cancel), getResources().getString(
					R.string.quit_talk));

			dialog.setOkListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
					finish();
				}
			});

			dialog.setCancelListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
				}
			});

			dialog.show();

		} else {
			super.onBackPressed();
		}
	}

	// 发送图片
	private void sendPic(final int index) {

		if (TALK_TYPE_PICS == currentMode) {
	        mProgressDialog.setMessage("正在上传第" + (index + 1) + "张图片");
	    }else {
	    	mProgressDialog.setMessage("正在上传视频文件");
	    }
		
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}

		File file = new File(choosePics.get(index));
		List<ParamItem> params = new ArrayList<ParamItem>();
		params.add(new ParamItem("commandtype", "sendClassChatAttachment",
				ParamItem.TYPE_TEXT));
		params.add(new ParamItem("fileupload", file, ParamItem.TYPE_FILE));
		params.add(new ParamItem("filetype", 0, ParamItem.TYPE_TEXT));

		WDJsonObjectMultipartRequest mRequest = new WDJsonObjectMultipartRequest(
				Consts.SERVER_sendClassChatAttachment, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						LogUtils.i(TAG + ":response=" + response);
						if (response.optInt("ret") == 0) {
						    JSONObject temp = response.optJSONObject("data");
							if (TextUtils.isEmpty(picUrls)) {
							    
								picUrls = temp.optString("url");
							} else {
								picUrls = picUrls + ","
										+ temp.optString("url");
							}
							
							if (TextUtils.isEmpty(picWids)) {
                                
							    picWids = temp.optString("width");
                            } else {
                                picWids = picWids + ","
                                        + temp.optString("width");
                            }
							if (TextUtils.isEmpty(picHeights)) {
                                
                                picHeights = temp.optString("height");
                            } else {
                                picHeights = picHeights + ","
                                        + temp.optString("height");
                            }
							// 仍然有图片需要上传
							if (index + 1 < choosePics.size()) {
								sendPic(index + 1);
							} else if (!TextUtils.isEmpty(videoPath)) {
								sendVideo();
							} else {
								sendMessage();
							}
						} else {
							mProgressDialog.dismiss();
							StatusUtils.handleStatus(response,
									WriteTalkActivity.this);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						mProgressDialog.dismiss();
						StatusUtils.handleError(arg0, WriteTalkActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);

	}

	protected void sendVideo() {
		mProgressDialog.setMessage("正在上传视频文件");
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
		File file = new File(videoPath);
		List<ParamItem> params = new ArrayList<ParamItem>();
		params.add(new ParamItem("commandtype", "sendClassChatAttachment",
				ParamItem.TYPE_TEXT));
		params.add(new ParamItem("fileupload", file, ParamItem.TYPE_FILE));
		params.add(new ParamItem("filetype", 1, ParamItem.TYPE_TEXT));

		WDJsonObjectMultipartRequest mRequest = new WDJsonObjectMultipartRequest(
				Consts.SERVER_sendClassChatAttachment, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						LogUtils.i(TAG + ":response=" + response);
						if (response.optInt("ret") == 0) {
						    JSONObject temp = response.optJSONObject("data");
							videoUrl = temp.optString("url");
							sendMessage(TALK_TYPE_VIDEO);
						} else {
							mProgressDialog.dismiss();
							StatusUtils.handleStatus(response,
									WriteTalkActivity.this);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						mProgressDialog.dismiss();
						StatusUtils.handleError(arg0, WriteTalkActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);

	}

	private void sendMessage() {
		int type = TALK_TYPE_TEXT;

		if (currentMode == TALK_MODE_PICS) {

			if (!StringUtils.isEmpty(picUrls)) {
				type = TALK_TYPE_PICS;
			}

		} else if (currentMode == TALK_MODE_VIDEO) {

			if (!StringUtils.isEmpty(videoUrl)) {
				type = TALK_TYPE_VIDEO;
			}
		}

		sendMessage(type);
	}

	private void sendMessage(int type) {

		if (dialog.isShowing()) {
			dialog.dismiss();
		}

//		System.out.println("picurl=="+picUrls);
//		System.out.println("pic_w=="+picWids);
//		System.out.println("pic_h=="+picHeights);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "sendTalk");
		params.put("classids", classIds);
		//家长版本带当前小孩的id，教师版本传0
		params.put("userid", (isTeacher() ? 0 : getDefaultAccountChild().getId()) + "");
		params.put("content", messageContent);
		params.put("type", type + "");
		params.put("picurl", StringUtils.isEmpty(picUrls) ? "" : picUrls);
        params.put("pic_w",StringUtils.isEmpty(picWids) ? "" : picWids);
        params.put("pic_h", StringUtils.isEmpty(picHeights) ? "" : picHeights);

		if (TALK_TYPE_VIDEO == type) {
			params.put("vidourl", videoUrl);
		}

		mProgressDialog.setMessage("正在发送");
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_sendTalk, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						mProgressDialog.dismiss();
						LogUtils.i(TAG + ":response=" + response);

						if (response.optInt("ret") == 0) {
							T.showShort(WriteTalkActivity.this,
									response.optString("msg"));
			                //设置返回数据
			                WriteTalkActivity.this.setResult(RESULT_OK);
							finish();
						} else {
							T.showShort(WriteTalkActivity.this,
									response.optString("msg"));
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						mProgressDialog.dismiss();
						StatusUtils.handleError(arg0, WriteTalkActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	private class HandleLocalBitmapTask extends AsyncTask<Void, Void, Void> {
		private String tempDirPath;
		private ArrayList<String> mRawPics;

		private boolean isVideoPic = false;

		public HandleLocalBitmapTask(ArrayList<String> rawpics) {
			super();
			tempDirPath = BaseApplication.getInstance().getWorkspaceImage()
					.getAbsolutePath();
			mRawPics = rawpics;

			isVideoPic = false;
		}

		public HandleLocalBitmapTask(ArrayList<String> rawpics, boolean isvideo) {
			super();
			tempDirPath = BaseApplication.getInstance().getWorkspaceImage()
					.getAbsolutePath();
			mRawPics = rawpics;

			isVideoPic = isvideo;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if (isVideoPic) {
				ProgressDialogUtils.showProgressDialog("视频处理中",
						WriteTalkActivity.this);
			} else {
				ProgressDialogUtils.showProgressDialog("图片处理中",
						WriteTalkActivity.this);
			}

		}

		@Override
		protected Void doInBackground(Void... params) {
			// choosePics.clear();
			for (int i = 0; i < mRawPics.size(); i++) {
				String temp = pressPic(mRawPics.get(i));
				// String temp = mRawPics.get(i);
				if (!TextUtils.isEmpty(temp)) {
					choosePics.add(temp);
				}
			}
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
						return aimPath;
					} else {
						return null;
					}
				}
				return null;
			} else {
				return resultFileName;
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			ProgressDialogUtils.dismissProgressBar();
			if (isVideoPic) {
				LogUtils.d("video pic, choosePics.size()=" + choosePics.size());

				if (null != choosePics && choosePics.size() > 0) {

					Utils util = new Utils(WriteTalkActivity.this);
					Bitmap bitmap;
					try {
						bitmap = util.getPathBitmap(
								Uri.fromFile(new File(choosePics.get(0))), 96,
								96);
						if (bitmap != null) {

							imgVideo.setImageBitmap(bitmap);
							imgVideo.setScaleType(ScaleType.CENTER_CROP);

						} else {

							LogUtils.d("video bitmap is null!!!");
						}

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

				} else {
					LogUtils.d("no video pic!!!");
				}

			} else {
				imgsAdapter.notifyDataSetChanged();
			}

			LogUtils.i("1111111111111111111111111111");
			for (int i = 0; i < choosePics.size(); i++) {
				LogUtils.i("i=" + i + " path:" + choosePics.get(i));
			}
		}
	}

	private class CaculatePicSizeTask extends AsyncTask<Void, Void, Void> {

		public CaculatePicSizeTask() {
			super();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ProgressDialogUtils.showProgressDialog("图片处理中",
					WriteTalkActivity.this);
			LogUtils.d("caculate pic size...");

			for (int i = 0; i < choosePics.size(); i++) {
				LogUtils.i("i=" + i + " path:" + choosePics.get(i));
			}
		}

		@Override
		protected Void doInBackground(Void... params) {

			picWids = "";
			picHeights = "";

			int wd = 0, ht = 0;

			for (int i = 0; i < choosePics.size(); i++) {
				FileInputStream fileinputstream = null;

				try {
					Bitmap bitmap = null;
					wd = 0;
					ht = 0;

					fileinputstream = new FileInputStream(choosePics.get(i));
					FileDescriptor filedescriptor = fileinputstream.getFD();
					BitmapFactory.Options options = new BitmapFactory.Options();

					options.inJustDecodeBounds = true;

					bitmap = BitmapFactory.decodeFileDescriptor(filedescriptor,
							null, options);

					wd = options.outWidth;
					ht = options.outHeight;
					LogUtils.e("name:" + choosePics.get(i));
					LogUtils.e("Bitmap w,h:" + wd + ", " + ht);

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

				picWids = picWids  + wd + ",";
				picHeights = picHeights + ht + ",";
			}

			if(picWids.endsWith(","))
			{
			    picWids = picWids.substring(0, picWids.length() - 1);
			}
			if(picHeights.endsWith(","))
			{
			    picHeights = picHeights.substring(0, picHeights.length() - 1);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			ProgressDialogUtils.dismissProgressBar();

			LogUtils.i("ws:" + picWids + " hs:" + picHeights);

			if (choosePics.size() > 0) {
				sendPic(0);
			} else {
				LogUtils.d("withs pic mode,but has no pics!! currentMode="
						+ currentMode);
			}
		}
	}
	
	@Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
	    super.onDestroy();
	    BaseApplication.getInstance().cancelPendingRequests(TAG);
        
    }
	
}
