package com.linkage.mobile72.sh.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.renderscript.Type;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.widget.SelectClazzDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

@SuppressLint("SimpleDateFormat")
public class ClazzInviteActivity extends BaseActivity implements
		OnClickListener {
	private final static String TAG = "ClazzInviteActivity";

	private String getNetString, getNetUrl;
	private ImageView erweimaImg;
	private Button back;
	private TextView saveBtn, getInfoText, getUrlText, copyBtn, txtSelector, txtClazzNum, txtShenhe;
	private RelativeLayout empty;

	private DisplayImageOptions clazzImageOption;
	private ImageLoader mLoader;
	// 班级选择器
	private int selectorNum = 0;
	private SelectClazzDialog selectClazz;
	// 班级名称列表
	private String[] clazzs;
	private long clazzId = 0;
	private List<ClassRoom> mClassRooms;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clazz_invite);
		empty = (RelativeLayout) findViewById(R.id.empty);
		txtShenhe = (TextView) findViewById(R.id.tvSet);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		setTitle("班级邀请");
		clazzImageOption = new DisplayImageOptions.Builder().cacheOnDisc()
				.showStubImage(R.drawable.appdetail_icon_def)
				.showImageForEmptyUri(R.drawable.appdetail_icon_def)
				.showImageOnFail(R.drawable.appdetail_icon_def).resetViewBeforeLoading() // default
																				// 设置图片在加载前是否重置、复位
				.delayBeforeLoading(500) // 下载前的延迟时间
				.cacheInMemory() // default 设置下载的图片是否缓存在内存中
				.cacheOnDisc() // default 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
																		// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default 设置图片的解码类型
				.displayer(new SimpleBitmapDisplayer()) // default 还可以设置圆角图片new
														// RoundedBitmapDisplayer(20)
				.handler(new Handler()) // default
				.build();
		mLoader = ImageLoader.getInstance();
		new LoadContacts().execute();
	}

	private void init() {
		erweimaImg = (ImageView) findViewById(R.id.erweima_img);
		saveBtn = (TextView) findViewById(R.id.save_btn);
		getInfoText = (TextView) findViewById(R.id.get_info_text);
		getUrlText = (TextView) findViewById(R.id.get_url_text);
		copyBtn = (TextView) findViewById(R.id.copy_btn);
		txtSelector = (TextView) findViewById(R.id.txt_selector);
		txtClazzNum = (TextView) findViewById(R.id.txt_clazz_num);
		erweimaImg.setDrawingCacheEnabled(true);
		saveBtn.setOnClickListener(this);
//		getUrlText.setOnClickListener(this);
		copyBtn.setOnClickListener(this);
		txtSelector.setOnClickListener(this);
		txtShenhe.setOnClickListener(this);
		txtShenhe.setText("班级审核");
		txtShenhe.setVisibility(View.VISIBLE);
		txtClazzNum.setText(clazzs[selectorNum]);
		if (mClassRooms.size() == 1) {
			 txtSelector.setVisibility(View.GONE);
		}else{
			 txtSelector.setVisibility(View.VISIBLE);
		}
		getInviteInfoFromNet();
	}

	/**
	 * 读取本地班级列表 班级数＝0 显示空界面 ＝1 不可切换班级 >1 可以切换班级
	 */
	private class LoadContacts extends AsyncTask<Integer, Void, Boolean> {

		public LoadContacts() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// showProgressBar(true);
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			DataHelper helper = getDBHelper();
			DataHelper.getHelper(ClazzInviteActivity.this);
			String loginName = BaseApplication.getInstance()
					.getDefaultAccount().getLoginname();
			try {
				QueryBuilder<ClassRoom, Integer> classroomBuilder = helper
						.getClassRoomData().queryBuilder();
				classroomBuilder.orderBy("joinOrManage", true)
						.orderBy("schoolId", true).where()
						.eq("loginName", loginName);
				mClassRooms = classroomBuilder.query();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// 去掉自己
			if (mClassRooms == null) {
				mClassRooms = new ArrayList<ClassRoom>();
			}
			if (mClassRooms.size() > 0) {
				empty.setVisibility(View.GONE);
				clazzs = new String[mClassRooms.size()];
				clazzId = mClassRooms.get(selectorNum).getId();
				for (int i = 0; i < mClassRooms.size(); i++) {
					clazzs[i] = mClassRooms.get(i).getName();
				}
				init();
				if (mClassRooms.size() > 1) {
					initDialog();
				}
			} else {
				txtShenhe.setVisibility(View.GONE);
				empty.setVisibility(View.VISIBLE);
			}
		}
	}

	// Dialog初始化
	private void initDialog() {
		selectClazz = new SelectClazzDialog(ClazzInviteActivity.this, mClassRooms, "取消", "确定");
		selectClazz.setCancelListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectClazz.setCheckNum(selectorNum);
				selectClazz.dismiss();
			}
		});
		selectClazz.setOkListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(selectorNum != selectClazz.getCheckNum()){
					selectorNum = selectClazz.getCheckNum();
					txtClazzNum.setText(clazzs[selectorNum]);
					clazzId = mClassRooms.get(selectorNum).getId();
					getInviteInfoFromNet();
				}
				selectClazz.dismiss();
			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_btn:
			String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            	Toast.makeText(ClazzInviteActivity.this,
            			"SD卡不可用！",
						Toast.LENGTH_SHORT).show();
            	return;
            }
			FileOutputStream b = null;
            File file = mApp.getWorkspaceImage();
            
            Bitmap bitmap = Bitmap.createBitmap(erweimaImg.getDrawingCache(false));// 获取相机返回的数据，并转换为Bitmap图片格式
            //照片的命名，目标文件夹下，以当前时间数字串为名称，即可确保每张照片名称不相同。
            String str=null;
            Date date=null;
            
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");//获取当前时间，进一步转化为字符串
            date =new Date();
            str=format.format(date);
            String fileName = file.getPath() + "/" + str + ".jpg";
            try {
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                MediaStore.Images.Media.insertImage(getContentResolver(), fileName, str + ".jpg", "");
                Toast.makeText(ClazzInviteActivity.this, "已保存成功！保存于" + "/" + Consts.PATH_APP + Consts.PATH_IMAGE + "下", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ClazzInviteActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    Toast.makeText(ClazzInviteActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            
			break;
//		case R.id.get_url_text:
//			Intent inte = new Intent(ClazzInviteActivity.this, NewWebViewActivity.class);
//			inte.putExtra(NewWebViewActivity.KEY_TITLE, "和校园");
//			inte.putExtra(NewWebViewActivity.KEY_URL, getNetUrl);
//			startActivity(inte);
//			break;
		case R.id.copy_btn:
			ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			cmb.setPrimaryClip(ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, getNetString + getNetUrl));
			Toast.makeText(ClazzInviteActivity.this, "复制内容成功！", Toast.LENGTH_SHORT).show();
			break;
		case R.id.back:
			finish();
			break;
		case R.id.tvSet:
			getShenheFromNet();
			break;
		case R.id.txt_selector:
			selectClazz.show();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 网络请求二维码信息
	 */
	private void getInviteInfoFromNet() {
		ProgressDialogUtils.showProgressDialog("", ClazzInviteActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "clazzQrcode");
		params.put("clazzId", clazzId + "");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_clazzQrcode, Request.Method.POST, params,
				true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							JSONObject data = response.optJSONObject("data");
							try {
								String[] getCodeString = data.optString("qrCodeText").split("http");
								getNetString = getCodeString[0];
								getNetUrl = "http" + getCodeString[1];
								getInfoText.setText(getNetString);
								getUrlText.setText(getNetUrl);
							} catch (Exception e) {
								e.printStackTrace();
								getInfoText.setText(data.optString("qrCodeText"));
							}
							mLoader.displayImage(data.optString("qrCodeImgUrl"), erweimaImg, clazzImageOption);
						} else if (response.optInt("ret") == 1) {
							Toast.makeText(ClazzInviteActivity.this,
									response.optString("msg"),
									Toast.LENGTH_SHORT).show();
						}
						ProgressDialogUtils.dismissProgressBar();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, ClazzInviteActivity.this);
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	/**
	 * 网络请求审核信息
	 */
	private void getShenheFromNet() {
		ProgressDialogUtils.showProgressDialog("", ClazzInviteActivity.this);
		String clazzids = "";
		for (int i = 0; i < mClassRooms.size(); i++) {
			clazzids = clazzids + mClassRooms.get(i).getId() + "";
			if (i != mClassRooms.size() - 1) {
				clazzids = clazzids + ",";
			}
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("classids", clazzids);
		params.put("commandtype", "reginfoForApply");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_reginfoForApply, Request.Method.POST, params,
				true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							String token = response.optString("token");
							String url = response.optString("url");
							Intent inte = new Intent(ClazzInviteActivity.this, NewWebViewActivity.class);
							inte.putExtra(NewWebViewActivity.KEY_TITLE, "班级审核");
							inte.putExtra(NewWebViewActivity.KEY_URL, url);
							inte.putExtra(NewWebViewActivity.KEY_TOKEN, token);
							startActivity(inte);
						} else if (response.optInt("ret") == 1) {
							Toast.makeText(ClazzInviteActivity.this,
									response.optString("msg"),
									Toast.LENGTH_SHORT).show();
						}
						ProgressDialogUtils.dismissProgressBar();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, ClazzInviteActivity.this);
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
}
