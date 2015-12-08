package com.linkage.mobile72.sh.activity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.dao.Dao;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.ClassAttenAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.AttenPicState;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.StudentAtten;
import com.linkage.mobile72.sh.data.StudentAttenSum;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.http.ParamItem;
import com.linkage.mobile72.sh.http.WDJsonObjectMultipartRequest;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ActivityUtils;
import com.linkage.mobile72.sh.utils.BitmapUtils;
import com.linkage.mobile72.sh.utils.ImageUtils;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.Mode;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener2;
import com.linkage.ui.widget.PullToRefreshListView;

public class AttenActivity extends BaseActivity implements OnClickListener {
	public final static int REQUEST_CODE_CAMERA = 2001;

	public final static int REQUEST_CODE_SEARCH = 2002;

	public final static int REQUEST_CODE_DSP = 2003;

	public static final String CURRENT_CLASS_ID = "current_class_id";
	
	public static final String IS_CONFIRMED = "is_confirmed";

	private String TAG = AttenActivity.this.getClass().getSimpleName();

	private LinearLayout lySearch;

	private Button btnBack;

	private TextView tvTitle, tvAddPic, tvEmpty;

	private PullToRefreshListView mListView;

	private ClassRoom mCurrentClass;

	private String mDateUI, mDate;

	private List<StudentAtten> mStudentAttenList = new ArrayList<StudentAtten>();

	private ClassAttenAdapter mAdapter;

	private StudentAttenSum studentAttenSum = new StudentAttenSum();

	private String ImageDirPath;

	private ProgressDialog mProgressDialog;

	private String picUrls = "";

	private boolean firstRefresh = true;

	private String pressPath;

	private AccountData account;
	private ImageView indicate;
	private PopupWindow popWindow;
	private List<ClassRoom> mClassRoomList;

	private AttenPicState mPicState;
	
	private boolean isConfirmed = false;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			LogUtils.i("get msg, what=" + msg.what);

			switch (msg.what) {

			case 1:
				reSendPic(mPicState.getLocalPath());
				break;
			}
			// super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attendance);

		btnBack = (Button) findViewById(R.id.back);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		indicate = (ImageView) findViewById(R.id.indicate);
		tvAddPic = (TextView) findViewById(R.id.tvAddPic);
		tvEmpty = (TextView) findViewById(R.id.tvEmpty);

		lySearch = (LinearLayout) findViewById(R.id.ly_search);
		// BaseApplication.getInstance().setTakeAttenPhoto(false);
		btnBack.setOnClickListener(this);
		tvTitle.setOnClickListener(this);
		tvAddPic.setOnClickListener(this);
		lySearch.setOnClickListener(this);

		showClazzTitle();
		if (null != mCurrentClass) {
			LogUtils.d("11mCurrentClass id = " + mCurrentClass.getId()
					+ " name=" + mCurrentClass.getName());
		}
		mListView = (PullToRefreshListView) findViewById(R.id.lstAtten);

		mAdapter = new ClassAttenAdapter(this, mStudentAttenList, handler);
		mAdapter.setAttenSum(studentAttenSum);
		mListView.setAdapter(mAdapter);
		mListView.setMode(Mode.PULL_FROM_START);

		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (null != mCurrentClass) {
					getClassAtten(mCurrentClass.getId());
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
			}
		});

		mDateUI = getDate("yyyy-MM-dd");
		mDate = getDate("yyyyMMdd");

		mAdapter.setmDate(mDate);

		// Log.d(TAG, "mDateUI=" + mDateUI + "  mDate=" + mDate);

		mProgressDialog = new ProgressDialog(this);
		ImageDirPath = BaseApplication.getInstance().getWorkspaceImage()
				.getAbsolutePath();
		// 获取数据
		mCurrentClass = getDefaultAccountClass();

		if (null != mCurrentClass) {
			LogUtils.d("mCurrentClass id = " + mCurrentClass.getId() + " name="
					+ mCurrentClass.getName());
		}

		// 更新页面显示
		tvTitle.setText(mCurrentClass.getName());
		// 请求考勤信息
		getClassAtten(mCurrentClass.getId());

		updatePicState();

	}

	private void updatePicState() {
		Hashtable<String, AttenPicState> map = (Hashtable<String, AttenPicState>) mApp
				.getPicStateMap();

		if (null != mCurrentClass) {

			if (null != map) {

				mPicState = map.get(mCurrentClass.getId() + "");

				if (null == mPicState) {

					mPicState = new AttenPicState();

					mPicState.setAtPicUrl("");
					mPicState.setLocalPath("");
					mPicState.setTakeAttenPhoto(false);

					map.put("" + mCurrentClass.getId(), mPicState);

					LogUtils.d("add new mPicState, id = "
							+ mCurrentClass.getId());

				} else {

					LogUtils.d("has mPicState");
				}

			} else {

				LogUtils.d("picstates  map is null!!");

				map = new Hashtable<String, AttenPicState>();

				mPicState = new AttenPicState();

				mPicState.setAtPicUrl("");
				mPicState.setLocalPath("");
				mPicState.setTakeAttenPhoto(false);

				map.put("" + mCurrentClass.getId(), mPicState);

				mApp.setPicStateMap(map);
			}
		}

		mAdapter.setmPicState(mPicState);
	}

	private void showClazzTitle() {
		account = getCurAccount();
		if (account != null) {
			mClassRoomList = getAccountClass();
			if (mClassRoomList != null && mClassRoomList.size() > 0) {
				if (mClassRoomList.size() == 1) {
					indicate.setVisibility(View.INVISIBLE);
					mCurrentClass = mClassRoomList.get(0);
					refreshDefaultClassRoom();
				} else {
					indicate.setVisibility(View.VISIBLE);
					initPopWindow();
					for (ClassRoom c : mClassRoomList) {
						if (c.getDefaultClass() == 1) {
							mCurrentClass = c;
							break;
						}
					}
					if (mCurrentClass == null) {
						mCurrentClass = mClassRoomList.get(0);
						refreshDefaultClassRoom();
					}
				}
				tvTitle.setText(mCurrentClass.getName());
			} else {
				tvTitle.setText("家校互动");
			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String getDate(String type) {
		String strDate;

		if (StringUtils.isEmpty(type)) {
			type = "yyyy-MM-dd";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(type);
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		strDate = formatter.format(curDate);

		return strDate;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		case R.id.tvAddPic:
			if (null == mCurrentClass) {
				Toast.makeText(AttenActivity.this, R.string.no_class_pic,
						Toast.LENGTH_SHORT).show();
			} else {
				startPhoto();
			}

			break;

		case R.id.ly_search:
			startSearch();
			break;

		case R.id.tvTitle:
			if (mClassRoomList != null && mClassRoomList.size() > 1) {
				if (popWindow.isShowing()) {
					indicate.setImageResource(R.drawable.jx_parent_choose_child_down);
					popWindow.dismiss();
				} else {
					indicate.setImageResource(R.drawable.jx_parent_choose_child_up);
					popWindow.showAsDropDown(tvTitle, 0, 15);
				}
			}
			break;
		}

	}

	private void startPhoto() {
		// if (mApp.isTakeAttenPhoto()) {
		if (mPicState.isTakeAttenPhoto()) {

			Intent iEdit = new Intent(AttenActivity.this,
					AttenPhotoActivity.class);

			iEdit.putExtra(CURRENT_CLASS_ID, mCurrentClass.getId());
			startActivityForResult(iEdit, REQUEST_CODE_DSP);
			LogUtils.d("ate, clsid=" + mCurrentClass.getId());

		} else {
			ActivityUtils.startTakePhotActivity(this, PIC_TAKE_PHOTO);
		}

	}

	private void startSearch() {
		// Log.d(TAG, "search on clicked!!!");
		// 准备数据
		// DataHelper helper = getDBHelper();
		// DataHelper.getHelper(this);
		// try
		// {
		// Dao<StudentAtten, Integer> stDao = helper.getStudentAttenData();
		// long size = stDao.countOf();
		//
		// if (size > 0)
		// {
		// List<StudentAtten> stAllList = stDao.queryForAll();
		// for (StudentAtten stAtten : stAllList)
		// {
		// stDao.delete(stAtten);
		// }
		// }
		//
		// for (StudentAtten stAtten : mStudentAttenList)
		// {
		// stDao.create(stAtten);
		// }
		//
		// }
		// catch (SQLException e)
		// {
		// e.printStackTrace();
		// }

		SaveAttenTask saveTask = new SaveAttenTask(mStudentAttenList, false);
		saveTask.execute();

		LogUtils.d("search on clicked!!!22222 isConfirmed = " + isConfirmed);

		// 启动搜索页面
		Intent iSearch = new Intent(AttenActivity.this,
				AttenSearchActivity.class);
		
		iSearch.putExtra(IS_CONFIRMED, isConfirmed);
		
		startActivityForResult(iSearch, REQUEST_CODE_SEARCH);
	}

	class SaveAttenTask extends AsyncTask<Void, Void, Boolean> {

		private List<StudentAtten> attenList;

		private boolean flag;

		SaveAttenTask(List<StudentAtten> list, boolean f_flag) {
			this.attenList = list;
			this.flag = f_flag;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DataHelper helper = getDBHelper();
			DataHelper.getHelper(AttenActivity.this);
			try {
				Dao<StudentAtten, Integer> stDao = helper.getStudentAttenData();
				long size = stDao.countOf();

				if (size > 0) {
					List<StudentAtten> stAllList = stDao.queryForAll();
					for (StudentAtten stAtten : stAllList) {
						stDao.delete(stAtten);
					}
				}

				for (StudentAtten stAtten : attenList) {
					stDao.create(stAtten);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			return flag;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_SEARCH:
				if (null != data) {
					boolean isChanged = data.getBooleanExtra(
							AttenSearchActivity.STUDENT_SEARCH, true);
					if (isChanged) {
						updateData();
					}
				}
				break;

			case REQUEST_CODE_DSP:
				if (null != data) {
					int changeMode = data.getIntExtra(
							AttenPhotoActivity.PHOTO_STATE,
							AttenPhotoActivity.PHOTO_STATE_ORG);

					switch (changeMode) {
					case AttenPhotoActivity.PHOTO_STATE_DEL:
						picUrls = "";
						// mApp.setAtPicUrl(picUrls);
						mPicState.setAtPicUrl(picUrls);
						mAdapter.setUploadImgUrl(picUrls);
						// mApp.setTakeAttenPhoto(false);
						mPicState.setTakeAttenPhoto(false);
						break;

					case AttenPhotoActivity.PHOTO_STATE_EDIT:

						String fileName = data
								.getStringExtra(AttenPhotoActivity.PHOTO_NAME);

						LogUtils.d("PHOTO_STATE_EDIT, fileName=" + fileName);

						if (!StringUtils.isEmpty(fileName)) {
							File file = new File(fileName);

							if (file.exists() && file.isFile()) {
								sendPic(file);
								// mApp.setLocalPath(ImageDirPath + "/" +
								// fileName);
								// new HandleLocalBitmapTask(ImageDirPath + "/"
								// + fileName).execute();
							} else {
								LogUtils.d("file is invalid, file.exists="
										+ file.exists() + "  file.isFile="
										+ file.isFile());
							}
						} else {
							LogUtils.d("fileName is null");
						}

						break;

					case AttenPhotoActivity.PHOTO_STATE_ORG:
						break;
					default:
						LogUtils.e("invalid changeMode=" + changeMode);
						break;
					}

				}
				break;
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
			mPicState.setTakeAttenPhoto(true);
			new HandleLocalBitmapTask(filePath).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void updateData() {
		DataHelper helper = getDBHelper();
		DataHelper.getHelper(this);
		try {
			Dao<StudentAtten, Integer> stDao = helper.getStudentAttenData();

			List<StudentAtten> stAllList = stDao.queryForAll();

			if (stAllList.size() > 0) {
				mStudentAttenList.clear();
				mStudentAttenList.addAll(stAllList);

				summaryAtten();

				mAdapter.notifyDataSetChanged();

				// 清空数据
				for (StudentAtten stAtten : stAllList) {
					stDao.delete(stAtten);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void summaryAtten() {
		int askLeave = 0, leave = 0;
		for (int i = 0; i < mStudentAttenList.size(); i++) {
			switch (mStudentAttenList.get(i).getState()) {
			case StudentAtten.ATTEN_NORMAL:

				break;
			case StudentAtten.ATTEN_ASK_FOR_LEAVE:
				askLeave++;
				break;
			case StudentAtten.ATTEN_LEAVE:
				leave++;
				break;
			default:
				LogUtils.e(TAG + ":error, state:"
						+ mStudentAttenList.get(i).getState());
				break;
			}
		}

		studentAttenSum.setStuCount(mStudentAttenList.size());
		studentAttenSum.setDate(mDateUI);
		studentAttenSum.setAskLeave(askLeave);
		studentAttenSum.setLeave(leave);

		mAdapter.setAttenSum(studentAttenSum);
	}

	private void getClassAtten(long classid) {
		// mListView.setMode(PullToRefreshBase.Mode.BOTH);

		if (firstRefresh) {
			ProgressDialogUtils.showProgressDialog("查询中", this, false);
			firstRefresh = false;
		}

		mAdapter.setClassId(classid);

		HashMap<String, String> params = new HashMap<String, String>();
		// params.put("classid", String.valueOf(10000339));
		params.put("classid", String.valueOf(classid));
		params.put("date", mDate);
		params.put("commandtype", "getAttendanceMembers");

		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_URL, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// 更新UI
						mListView.onRefreshComplete();
						ProgressDialogUtils.dismissProgressBar();

						if (response.optInt("ret") == 0
								|| response.optInt("ret") == 1) {
							List<StudentAtten> data = null;

							try {
								// data =
								// StudentAtten.parseFromJson(response.optJSONObject("data").getJSONArray("list"));
								data = StudentAtten.parseFromJson(response
										.getJSONArray("data"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							mAdapter.setRet(response.optInt("ret"));
							if (response.optInt("ret") == 0) {
								tvAddPic.setVisibility(View.VISIBLE);
								isConfirmed = false;
							} else {
								tvAddPic.setVisibility(View.GONE);
								isConfirmed = true;
							}
							if (null != data && data.size() > 0) {
								// mListView.setMode(PullToRefreshBase.Mode.BOTH);
								mStudentAttenList.clear();
								mStudentAttenList.addAll(data);
								summaryAtten();

								mListView.setVisibility(View.VISIBLE);
								tvEmpty.setVisibility(View.GONE);

								mAdapter.notifyDataSetChanged();

							} else {
								tvEmpty.setText(R.string.no_atten_info);
								tvEmpty.setVisibility(View.VISIBLE);
								mListView.setVisibility(View.GONE);
							}
						} else {
							tvEmpty.setText(R.string.no_atten_info);
							tvEmpty.setVisibility(View.VISIBLE);
							mListView.setVisibility(View.GONE);
							isConfirmed = true;
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						mListView.onRefreshComplete();
						ProgressDialogUtils.dismissProgressBar();
						isConfirmed = true;
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	private void reSendPic(String filepath) {

		LogUtils.d("classid=" + mCurrentClass.getId() + "  filepath="
				+ filepath);

		File file = new File(filepath);

		if (file.exists() && file.isFile()) {
			sendPic(file, true);

		} else {
			LogUtils.d("file is invalid, file.exists=" + file.exists()
					+ "  file.isFile=" + file.isFile());
		}

	}

	private void sendPic(File file, final boolean isRe) {
		mProgressDialog.setMessage("正在上传图片");
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}

		List<ParamItem> params = new ArrayList<ParamItem>();
		params.add(new ParamItem("commandtype", "sendMessageAttachment",
				ParamItem.TYPE_TEXT));
		// params.add(new ParamItem("fileupload", currentPhoto,
		// ParamItem.TYPE_FILE));
		params.add(new ParamItem("fileupload", file, ParamItem.TYPE_FILE));

		WDJsonObjectMultipartRequest mRequest = new WDJsonObjectMultipartRequest(
				Consts.SERVER_sendMessageAttachment, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						LogUtils.i(TAG + ":response=" + response);
						mProgressDialog.dismiss();
						if (response.optInt("ret") == 0) {
							picUrls = response.optString("path");
							// mApp.setAtPicUrl(picUrls);
							mPicState.setAtPicUrl(picUrls);
							mPicState.setTakeAttenPhoto(true);
							Hashtable<String, AttenPicState> map = new Hashtable<String, AttenPicState>();
							map.put("" + mCurrentClass.getId(), mPicState);
							mApp.setPicStateMap(map);
							mAdapter.setUploadImgUrl(picUrls);

							if (isRe) {
								// /////////////////////////////////////////////////testaa
								mAdapter.uploadClassAtten(mCurrentClass.getId());
//								 mAdapter.uploadClassAtten(1111);
							}
						} else {

							if (isRe) {
								Toast.makeText(AttenActivity.this, "确认考勤失败",
										Toast.LENGTH_SHORT).show();
							} else {
								StatusUtils.handleStatus(response,
										AttenActivity.this);
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						mProgressDialog.dismiss();

						if (isRe) {
							Toast.makeText(AttenActivity.this, "确认考勤失败",
									Toast.LENGTH_SHORT).show();
						} else {
							StatusUtils.handleError(arg0, AttenActivity.this);
						}
					}
				});

		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	private void sendPic(File file) {
		sendPic(file, false);
	}

	private class HandleLocalBitmapTask extends AsyncTask<Void, Void, Void> {
		private String tempDirPath;
		private String mRawPics;

		public HandleLocalBitmapTask(String rawpics) {
			tempDirPath = BaseApplication.getInstance().getWorkspaceImage()
					.getAbsolutePath();
			mRawPics = rawpics;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ProgressDialogUtils.showProgressDialog("图片处理中", AttenActivity.this);
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
			File file = new File(pressPath);

			if (file.exists() && file.isFile()) {
				sendPic(file);
				// mApp.setLocalPath(pressPath);
				mPicState.setLocalPath(pressPath);
			} else {
				LogUtils.d("file is invalid, file.exists=" + file.exists()
						+ "  file.isFile=" + file.isFile());
			}
		}
	}

	private void initPopWindow() {
		LayoutInflater inflater = LayoutInflater.from(this);
		// 引入窗口配置文件
		View view = inflater.inflate(R.layout.pop_jx_parent_choose_child, null);
		ListView listView = (ListView) view.findViewById(R.id.listView);
		final ChildAdapter adapter = new ChildAdapter();
		listView.setAdapter(adapter);
		listView.setDivider(null);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ClassRoom child = adapter.getItem(position);
				mCurrentClass = child;
				updatePicState();
				refreshDefaultClassRoom();
				// refreshData();
				if (popWindow.isShowing()) {
					popWindow.dismiss();
				}
				// BaseApplication.getInstance().setTakeAttenPhoto(false);
				tvTitle.setText(mCurrentClass.getName());
				firstRefresh = true;
				mAdapter.setUploadImgUrl(mPicState.getAtPicUrl());
				getClassAtten(mCurrentClass.getId());
			}
		});
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		popWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, false);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setOutsideTouchable(true);
		popWindow.setFocusable(true);
		popWindow.update();
		popWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				indicate.setImageResource(R.drawable.jx_parent_choose_child_down);
			}
		});

	}

	class ChildAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mClassRoomList.size();
		}

		@Override
		public ClassRoom getItem(int position) {
			// TODO Auto-generated method stub
			return mClassRoomList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(AttenActivity.this).inflate(
						R.layout.item_list_single_text_center, null);
				viewHolder = new ViewHolder();
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.list_textshow);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			ClassRoom r = getItem(position);
			viewHolder.textView.setText(r.getName());
			viewHolder.textView.setTextColor(Color.rgb(96, 205, 246));

			return convertView;
		}

		class ViewHolder {
			public TextView textView;
		}
	}

	private void refreshDefaultClassRoom() {
		try {
			getDBHelper().getClassRoomData().updateRaw(
					"update ClassRoom set defaultClass = 0 where loginName = "
							+ getAccountName() + " and joinOrManage = 1");
			getDBHelper().getClassRoomData().updateRaw(
					"update ClassRoom set defaultClass = 1 where loginName = "
							+ getAccountName() + " and joinOrManage = 1"
							+ " and id = " + mCurrentClass.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
