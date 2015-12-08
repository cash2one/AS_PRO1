package com.linkage.mobile72.sh.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.Person;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.http.ParamItem;
import com.linkage.mobile72.sh.http.WDJsonObjectMultipartRequest;
import com.linkage.mobile72.sh.utils.MyDatePickerDialog;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.widget.JudgeDate;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.widget.MyEditDialog;
import com.linkage.mobile72.sh.widget.ScreenInfo;
import com.linkage.mobile72.sh.widget.WheelMain;
import com.linkage.mobile72.sh.Consts;

public class PersonalInfoEditActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = PersonalInfoEditActivity.class.getSimpleName();

	private static final int SCHOOL_TYPE = 1001;
	private static final int SCHOOL_CHOOSE = 1002;
	private static final int GRADE_CHOOSE = 1003;
	private static final int PIC_TAKEPHOTO = 1004;
	private static final int PIC_ALBUM = 1005;
	private static final int PIC_OK = 1006;

	private File file;
	private Person person;
	private Button set;
	private CircularImage avaterImage;
	private WheelMain wheelMain;
	private RelativeLayout nickLayout, sexLayout, birthLayout, mailLayout;
	private RelativeLayout schoolTypeLayout, schoolLayout, gradeLayout;
	private TextView accountText, nickText, sexText, birthText, mailText;
	private TextView schoolTypeText, schoolText, gradeText, roleText;

	private MyDatePickerDialog datePickerDialog;
	private MyDatePickerDialog.OnDateSetListener dateListener;
	private MyEditDialog editDialog;
	private String dialogDisplayNick, dialogDisplayMail;
	private String displayDate, pickerDate;
	private PopupWindow pop;
	private View view;
	private int schooltypeid = 0;
	private int gradeid = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info_edit);

		person = (Person) getIntent().getExtras().getSerializable("PERSON");
		if (person == null) {
			finish();
		}
		
		setTitle(R.string.title_person_info_edit);
		findViewById(R.id.back).setOnClickListener(this);
//		set = (Button) findViewById(R.id.set);
//		set.setVisibility(View.VISIBLE);
//		set.setText("提交");
//		set.setOnClickListener(this);

		avaterImage = (CircularImage) findViewById(R.id.user_avater);
		accountText = (TextView) findViewById(R.id.personal_info_account_text);
		nickLayout = (RelativeLayout) findViewById(R.id.personal_info_nickname_layout);
		nickText = (TextView) findViewById(R.id.personal_info_nick);
		sexLayout = (RelativeLayout) findViewById(R.id.personal_info_sex_layout);
		sexText = (TextView) findViewById(R.id.personal_info_sex);
		birthLayout = (RelativeLayout) findViewById(R.id.personal_info_birth_layout);
		birthText = (TextView) findViewById(R.id.personal_info_birth);
		mailLayout = (RelativeLayout) findViewById(R.id.personal_info_mail_layout);
		mailText = (TextView) findViewById(R.id.personal_info_mail);
		schoolTypeLayout = (RelativeLayout) findViewById(R.id.personal_info_schooltype_layout);
		schoolTypeText = (TextView) findViewById(R.id.personal_info_schooltype);
		schoolLayout = (RelativeLayout) findViewById(R.id.personal_info_school_layout);
		schoolText = (TextView) findViewById(R.id.personal_info_school);
		gradeLayout = (RelativeLayout) findViewById(R.id.personal_info_grade_layout);
		gradeText = (TextView) findViewById(R.id.personal_info_grade);
//		roleLayout = (RelativeLayout) findViewById(R.id.personal_info_role_layout);
		roleText = (TextView) findViewById(R.id.personal_info_role);

		initPopupWindow();
		displayDate = "2000-01-01";
		dateListener = new MyDatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                pickerDate = year + "-" + (month + 1) + "-" + dayOfMonth;
				displayDate = pickerDate;
				birthText.setText(displayDate);
			}
			
		};
		fillDataToPage(person);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SCHOOL_TYPE:
				String schoolType = data.getStringExtra("schoolType");
				String schoolTypeId = data.getStringExtra("schoolTypeId");
				this.schooltypeid = Integer.valueOf(schoolTypeId);
				schoolTypeText.setText(schoolType);
				break;
			case GRADE_CHOOSE:
				String grade = data.getStringExtra("grade");
				String gradeId = data.getStringExtra("gradeId");
				this.gradeid = Integer.valueOf(gradeId);
				gradeText.setText(grade);
				break;
			case SCHOOL_CHOOSE:
//				schoolid = data.getLongExtra("SCHOOL_ID", 0);
				String schoolName = data.getStringExtra("SCHOOL_NAME");
				schoolText.setText(schoolName);
				break;

			// 如果是直接从相册获取
			case PIC_ALBUM:
				if (data != null) {
					startPhotoZoom(data.getData());
				}
				break;
			// 如果是调用相机拍照时
			case PIC_TAKEPHOTO:
				File temp = new File(Environment.getExternalStorageDirectory() + "/xiaoma.jpg");
				startPhotoZoom(Uri.fromFile(temp));
				break;
			case PIC_OK:
				if (data != null) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							setPicToView(data);
						}
					}, 500);
				}
				break;

			}
		}
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PIC_OK);
	}

	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(getResources(), photo);

			saveBitmapFile(photo);
			avaterImage.setImageDrawable(drawable);
			
			updateUserInfo(person);
		}
	}

	public void saveBitmapFile(Bitmap bitmap) {
		file = mApp.getUploadImageOutputFile();// 将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fillDataToPage(Person person) {
		if (person != null) {
			imageLoader.displayImage(person.getUseravatar(), avaterImage);
			accountText.setText(person.getAccountID());
			nickText.setText(person.getName());
			sexText.setText(person.getSex().equals("1") ? "男" : "女");
			birthText.setText(person.getBrith());
			mailText.setText(person.getEmail());
			schoolTypeText.setText(person.getEductionalystme());
			schoolText.setText(person.getSchool());
			gradeText.setText(person.getGrade());
			roleText.setText(person.getUsertype().equals("0") ? "家长" : "教师");

			dialogDisplayNick = person.getName();
			dialogDisplayMail = person.getEmail();
			
			avaterImage.setOnClickListener(this);
			nickLayout.setOnClickListener(this);
			sexLayout.setOnClickListener(this);
			birthLayout.setOnClickListener(this);
			mailLayout.setOnClickListener(this);
			schoolTypeLayout.setOnClickListener(this);
			schoolLayout.setOnClickListener(this);
			gradeLayout.setOnClickListener(this);
		}
	}

	private void updateUserInfo(Person p) {
		String nickRegex = "^[0-9A-Za-z\u4e00-\u9fa5]{1,12}$";
        String mailRegex = "\\w+@\\w+\\.\\w+";
		final String nickName = nickText.getText().toString().trim();
		String mail = mailText.getText().toString().trim();
		String sex = sexText.getText().toString().trim().equals("男") ? "1" : "2";
		String birth = birthText.getText().toString().trim();
		int schoolTypeId = Integer.valueOf(schooltypeid);
		int gradeId = Integer.valueOf(gradeid);
		if (StringUtils.isEmpty(nickName)) {
			UIUtilities.showToast(PersonalInfoEditActivity.this, "用户昵称不能为空");
			return;
		}
		if (!nickName.matches(nickRegex)) {
			UIUtilities.showToast(PersonalInfoEditActivity.this, "昵称只能包含汉字、字母、数字，最长12位");
			return;
		}
		if (!StringUtils.isEmpty(mail) && !mail.matches(mailRegex)) {
			UIUtilities.showToast(PersonalInfoEditActivity.this, "邮箱格式不正确");
			return;
		}
		ProgressDialogUtils.showProgressDialog("正在提交，请稍候", this, false);

		List<ParamItem> params = new ArrayList<ParamItem>();
		params.add(new ParamItem("commandtype", "updateUserInfo", ParamItem.TYPE_TEXT));
		params.add(new ParamItem("nickName", nickName, ParamItem.TYPE_TEXT));
		params.add(new ParamItem("sex", sex, ParamItem.TYPE_TEXT));
		params.add(new ParamItem("birthday", birth, ParamItem.TYPE_TEXT));
		if (!StringUtils.isEmpty(mail) && mail.matches(mailRegex)) {
			params.add(new ParamItem("mail", mail, ParamItem.TYPE_TEXT));
		}
		params.add(new ParamItem("schoolType", schoolTypeId, ParamItem.TYPE_TEXT));
		params.add(new ParamItem("schoolId", p.getSchoolId(), ParamItem.TYPE_TEXT));
		params.add(new ParamItem("gradeType", gradeId, ParamItem.TYPE_TEXT));
		if (file != null) {
			params.add(new ParamItem("fileupload", file, ParamItem.TYPE_FILE));
		} else {
			params.add(new ParamItem("fileupload", "", ParamItem.TYPE_FILE));
		}
		WDJsonObjectMultipartRequest mRequest = new WDJsonObjectMultipartRequest(Consts.SERVER_URL,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						System.out.println("response=" + response);
						if (response.optInt("ret") == 0) {
							String msg = response.optString("msg");
							
							String data = response.optString("data");
							if(data!=null && !"".equalsIgnoreCase(data))
							{
								AccountData account = getCurAccount();
								account.setAvatar(data);
								account.setUserName(nickName);
								DataHelper helper = getDBHelper();
								try {
									helper.getAccountDao().createOrUpdate(account);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else{
								AccountData account = getCurAccount();
								account.setUserName(nickName);
								DataHelper helper = getDBHelper();
								try {
									helper.getAccountDao().createOrUpdate(account);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
								
							UIUtilities.showToast(PersonalInfoEditActivity.this, msg);
							imageLoader.clearDiscCache();
							imageLoader.clearMemoryCache();
//							setResult(RESULT_OK);
//							finish();
						} else {
							StatusUtils.handleStatus(response, PersonalInfoEditActivity.this);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, PersonalInfoEditActivity.this);
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	private void initPopupWindow() {
		view = this.getLayoutInflater().inflate(R.layout.popup_window_sex, null);
		pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT, true);
		pop.setOutsideTouchable(true);
		ColorDrawable dw = new ColorDrawable(-00000);
		pop.setBackgroundDrawable(dw);
		pop.setAnimationStyle(R.style.popupWindowAnimation);
		pop.update();
		Button pop_cancel = (Button) view.findViewById(R.id.btn_pop_cancel);
		pop_cancel.setOnClickListener(this);
		Button pop_male = (Button) view.findViewById(R.id.btn_pop_male);
		pop_male.setOnClickListener(this);
		Button pop_female = (Button) view.findViewById(R.id.btn_pop_female);
		pop_female.setOnClickListener(this);
	}

	/**
	 * 选择提示对话框
	 */
	private void showPickDialog() {
		new AlertDialog.Builder(this).setTitle("设置头像")
				.setNegativeButton("相册", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_PICK, null);
						intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
						startActivityForResult(intent, PIC_ALBUM);
					}
				}).setPositiveButton("拍照", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						// 下面这句指定调用相机拍照后的照片存储的路径
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
								.getExternalStorageDirectory(), "xiaoma.jpg")));
						startActivityForResult(intent, PIC_TAKEPHOTO);
					}
				}).show();
	}
	private MyCommonDialog dialog;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.set:
		    dialog = new MyCommonDialog(this, "提示消息", "您确认提交吗？", "取消", "确认");
            dialog.setOkListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    updateUserInfo(person);
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
			
			break;
		case R.id.user_avater:
			showPickDialog();
			break;
		case R.id.personal_info_sex_layout:
			if (pop.isShowing()) {
				pop.dismiss();
			} else {
				pop.showAtLocation(findViewById(R.id.edit_personalinfo_layout), Gravity.BOTTOM, 0,
						0);
			}
			break;
		case R.id.personal_info_nickname_layout:
			if(StringUtils.isEmpty(dialogDisplayNick)) {
				dialogDisplayNick = "";
			}
			editDialog = new MyEditDialog(this, 12, "请输入昵称", dialogDisplayNick, "取消", "确定");
            editDialog.setCancelListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editDialog.dismiss();
                }
            });
            editDialog.setOkListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newNick = editDialog.getEditView().getText().toString().trim();
                    if (TextUtils.isEmpty(newNick)) {
                        UIUtilities.showToast(PersonalInfoEditActivity.this, "请输入昵称");
                    } else {
                    	dialogDisplayNick = newNick;
                        nickText.setText(dialogDisplayNick);
                        updateUserInfo(person);
                        editDialog.dismiss();
                    }
                }
            });
            editDialog.show();
			break;
		case R.id.personal_info_mail_layout:
			if(StringUtils.isEmpty(dialogDisplayMail)) {
				dialogDisplayMail = "";
			}
			editDialog = new MyEditDialog(this, 50, "请输入你的邮箱地址", dialogDisplayMail, "取消", "确定");
            editDialog.setCancelListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editDialog.dismiss();
                }
            });
            editDialog.setOkListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newMail = editDialog.getEditView().getText().toString().trim();
                    if (TextUtils.isEmpty(newMail)) {
                        UIUtilities.showToast(PersonalInfoEditActivity.this, "请输入你的邮箱地址");
                    } else {
                    	dialogDisplayMail = newMail;
                        mailText.setText(dialogDisplayMail);
                        updateUserInfo(person);
                        editDialog.dismiss();
                    }
                }
            });
            editDialog.show();
			break;
		case R.id.personal_info_birth_layout:
			LayoutInflater inflater=LayoutInflater.from(PersonalInfoEditActivity.this);
			final View timepickerview=inflater.inflate(R.layout.timepicker, null);
			ScreenInfo screenInfo = new ScreenInfo(PersonalInfoEditActivity.this);
			wheelMain = new WheelMain(timepickerview);
			wheelMain.screenheight = screenInfo.getHeight();
			String time = birthText.getText().toString();
			Calendar calendar = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			if(JudgeDate.isDate(time, "yyyy-MM-dd")){
				try {
					calendar.setTime(dateFormat.parse(time));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			wheelMain.initDateTimePicker(year,month,day);
			new AlertDialog.Builder(PersonalInfoEditActivity.this)
			.setTitle("请选择日期")
			.setView(timepickerview)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					birthText.setText(wheelMain.getTime());
					updateUserInfo(person);
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		
			break;
		case R.id.personal_info_schooltype_layout:
			Intent i = new Intent(this, SchoolTypeActivity.class);
			startActivityForResult(i, SCHOOL_TYPE);
			break;
		case R.id.personal_info_school_layout:
			Intent selectSchoolIntent = new Intent(this, SelectSchoolActivity.class);
			startActivityForResult(selectSchoolIntent, SCHOOL_CHOOSE);
			break;
		case R.id.personal_info_grade_layout:
			Intent j = new Intent(this, GradeChooseActivity.class);
			startActivityForResult(j, GRADE_CHOOSE);
			break;
		case R.id.btn_pop_cancel:
			pop.dismiss();
			break;
		case R.id.btn_pop_male:
			sexText.setText("男");
			updateUserInfo(person);
			pop.dismiss();
			break;
		case R.id.btn_pop_female:
			sexText.setText("女");
			updateUserInfo(person);
			pop.dismiss();
			break;
		}
	}
	
	@TargetApi(11)
    public void dateCallback(){
		if(Build.VERSION.SDK_INT >= 11){
			DatePicker datePicker = datePickerDialog.getDatePicker();
			dateListener.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        }
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}
	
	
}