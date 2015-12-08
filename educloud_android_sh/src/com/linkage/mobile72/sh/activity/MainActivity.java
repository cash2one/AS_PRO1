package com.linkage.mobile72.sh.activity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.register.InputPasswordActivity;
import com.linkage.mobile72.sh.activity.register.Register_SendCodeActivity;
import com.linkage.mobile72.sh.activity.register.Reset_SendCodeActivity;
import com.linkage.mobile72.sh.activity.register.Validate_SmsActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseSlidingFragmentActivity;
import com.linkage.mobile72.sh.data.AccountChild;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.datasource.DataHelper;
import com.linkage.mobile72.sh.fragment.JzhFragment;
import com.linkage.mobile72.sh.fragment.MainFragment;
import com.linkage.mobile72.sh.fragment.MainFragment2;
import com.linkage.mobile72.sh.fragment.MenuFragment;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.Utilities;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.slidingmenu.SlidingMenu;

public class MainActivity extends BaseSlidingFragmentActivity  {

    private static final String TAG = MainActivity.class.getName();
	private Fragment mContent;
	public static SlidingMenu slideMenu;
	public static MainActivity instance;
	private MyCommonDialog dialog;
	private DataHelper dataHelper;
	private String oldPkg = "com.linkage.mobile72.js";

    public static final String EXTRA_USERNAME = "extra_username";
    public static final String EXTRA_PASSWORD = "extra_password";
    public static final String EXTRA_DATA = "extra_data";
    private String extraData, extraUserName, extraPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent == null) {
            finish();
            return;
        }
        extraData = intent.getStringExtra(EXTRA_DATA);
        extraUserName = intent.getStringExtra(EXTRA_USERNAME);
        extraPassword = intent.getStringExtra(EXTRA_PASSWORD);
        onLoginSuccess(extraUserName, extraPassword, extraData);

		finishObviousPage();
		instance = this;
		setContentView(R.layout.activity_main);

		slideMenu = getSlidingMenu();
		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
//			getSlidingMenu().setSlidingEnabled(true);
//			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
//			getSlidingMenu().setSlidingEnabled(false);
//			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
		slideMenu.setSlidingEnabled(true);
		slideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		// set the Above View Fragment
		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		}

		if (mContent == null) {
			if(Consts.is_Teacher) {
				mContent = new MainFragment();
			}else {
				mContent = new MainFragment2();
				BaseApplication.getInstance().mainFragment2 = (MainFragment2) mContent;
			}
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// set the Behind View Fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MenuFragment()).commit();

		// customize the SlidingMenu
		slideMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);//拉开后 离边框距离
		slideMenu.setFadeEnabled(false);
		slideMenu.setBehindScrollScale(0.5f);
		slideMenu.setFadeDegree(0.3f);
		if(isTeacher()) {
			slideMenu.setBackgroundImage(R.drawable.main_bg_teacher);
		}else {
			slideMenu.setBackgroundImage(R.drawable.main_bg_teacher);
		}
		slideMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.3 + 0.7);
				canvas.scale(scale, scale, -canvas.getWidth() / 2,
						canvas.getHeight() / 2);
			}
		});

		slideMenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.25);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
        dataHelper = DataHelper.getHelper(this);
        //startService(new Intent(this, GetGroupService.class));
//        if(!hasClassRoomData()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    syncClassRoom();
                }
            }).start();
//        }
//        if(!hasContactData()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    syncContact();
                }
            }).start();
//        }
        PackageInfo pi = null;
		try {
			pi = getPackageManager().getPackageInfo(oldPkg, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (pi != null && pi.applicationInfo != null && pi.versionCode < 400000) {
			dialog = new MyCommonDialog(MainActivity.this, "提示消息", "检测到您的手机中存在旧版的客户端，请点击确定删除。", null, "确定");
			dialog.setOkListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Uri packageURI = Uri.parse("package:" + oldPkg);
					Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
					uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(uninstallIntent);
				}
			});
			dialog.show();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

    protected void onLoginSuccess(String loginName, String password, String data) {
        AccountData user = null;
        try {
            user = AccountData.parseFromJson(new JSONObject(data));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<AccountChild> childs = user.getStudentData();
        DataHelper helper = getDBHelper();
        try {
            helper.getAccountDao().updateRaw(
                    "update AccountData set defaultUser = 0");
            helper.getAccountDao().updateRaw(
                    "update AccountData set lastLoginUser = 0");
            // 初始化用户数据
            user.setLoginname(loginName);
//            if (isSavePassd) {
                user.setLoginpwd(password);
//            } else {
//                user.setLoginpwd("");
//            }
            user.setDefaultUser(1);
            user.setLastLoginUser(1);
            user.setLoginDate(System.currentTimeMillis());
            QueryBuilder<AccountData, Integer> accountBuilder = helper.getAccountDao().queryBuilder();
            accountBuilder.where().eq("userId", user.getUserId()).and().eq("userType", user.getUserType());
            AccountData ad = accountBuilder.queryForFirst();
            if(ad != null) {
                helper.getAccountDao().update(user);
            }else {
                helper.getAccountDao().create(user);
            }
            //helper.getAccountDao().createOrUpdate(user);
            if (null != childs && childs.size() > 0) {
                DeleteBuilder<AccountChild, Integer> deleteBuilder;
                deleteBuilder = helper.getAccountChildDao().deleteBuilder();
                deleteBuilder.where().eq("userid", user.getUserId());
                deleteBuilder.delete();
                for (int i = 0; i < childs.size(); i++) {
                    AccountChild child = childs.get(i);
                    if (i == 0)
                        child.setDefaultChild(1);
                    child.setUserid(user.getUserId());
                    helper.getAccountChildDao().create(child);
                }
            } else {
                DeleteBuilder<AccountChild, Integer> deleteBuilder;
                deleteBuilder = helper.getAccountChildDao().deleteBuilder();
                deleteBuilder.where().eq("userid", user.getUserId());
                deleteBuilder.delete();
                AccountChild child = new AccountChild();
                child.setId(-100);
                child.setName("家校互动");
                child.setXxt_type(0);
                child.setDefaultChild(1);
                child.setUserid(user.getUserId());
                helper.getAccountChildDao().create(child);
            }
            mApp.notifyAccountChanged();

            // 上次加载网络图片的Url
            final String historyLocal = mApp.getSp().getString(
                    AdvActivity.LOADING_PIC_HISTORY_LOCAL + user.getUserId(), "");
            final String historyServer = mApp.getSp().getString(
                    AdvActivity.LOADING_PIC_HISTORY_SERVER + user.getUserId(), "");
            Log.d("tag_", "historty----------------" + historyLocal);
            Log.d("tag_", "histortyServer----------------" + historyServer);
            // 用当前时间字符串命名图片名称
            /*picName = Utilities.formatNow(new SimpleDateFormat("yyyyMMdd")).substring(0, 8) + "_" + user.getUserId();
            //如果本地图片为空，或者与现创文件的文件名不匹配，则进入线程
            if(StringUtils.isEmpty(getCurAccount().getLoadingImg())) {
                startActivityHandle.sendEmptyMessage(1);
            }else {
                if(StringUtils.isEmpty(historyLocal)) {
                    startActivityHandle.sendEmptyMessage(2);
                }else {
                    String historyLocalTime = historyLocal.split("_")[0];
                    String todayTime = picName.split("_")[0];
                    if(!historyLocalTime.equals(todayTime)) {
                        startActivityHandle.sendEmptyMessage(2);
                    }else {
                        if(!historyServer.equals(getCurAccount().getLoadingImg())) {
                            startActivityHandle.sendEmptyMessage(2);
                        }else {
                            startActivityHandle.sendEmptyMessage(1);
                        }
                    }
                }
            }*/
        } catch (android.database.SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            StatusUtils.handleOtherError("网络通讯异常", instance);
        } catch (java.sql.SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            StatusUtils.handleOtherError("网络通讯异常", instance);
        }
        // loginIm(loginName, password, user.getUserType());
    }

    public static void loginIm(final String name, final String password,
                               final int userType) {
        BaseApplication.getInstance().callWhenServiceConnected(new Runnable() {

            @Override
            public void run() {
                try {
                    BaseApplication.getInstance().getChatService()
                            .login(name, password, userType);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

	private void finishObviousPage() {
		if(SplashActivity.instance!=null && !SplashActivity.instance.isFinishing())
			SplashActivity.instance.finish();
		if(LoginActivity.instance!=null && !LoginActivity.instance.isFinishing())
			LoginActivity.instance.finish();
		if(AlterPasswordActivity.instance!=null && !AlterPasswordActivity.instance.isFinishing())
			AlterPasswordActivity.instance.finish();
		if(Register_SendCodeActivity.instance!=null && !Register_SendCodeActivity.instance.isFinishing())
			Register_SendCodeActivity.instance.finish();
		if(Reset_SendCodeActivity.instance!=null && !Reset_SendCodeActivity.instance.isFinishing())
			Reset_SendCodeActivity.instance.finish();
		if(InputPasswordActivity.instance!=null && !InputPasswordActivity.instance.isFinishing())
			InputPasswordActivity.instance.finish();
		if(Validate_SmsActivity.instance!=null && !Validate_SmsActivity.instance.isFinishing())
			Validate_SmsActivity.instance.finish();
	}

    private boolean hasClassRoomData(){
        QueryBuilder<ClassRoom, Integer> classroomBuilder = null;
        List<ClassRoom> classRooms = null;
        try {
            classroomBuilder = dataHelper.getClassRoomData().queryBuilder();
            classroomBuilder.where().eq("loginName", getCurAccount().getLoginname());
            classRooms = classroomBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classRooms != null && classRooms.size() > 0;
    }

    private boolean hasContactData(){
        QueryBuilder<Contact, Integer> contactBuilder = null;
        List<Contact> contacts = null;
        try {
            contactBuilder = dataHelper.getContactData().queryBuilder();
            contactBuilder.where().eq("loginName", getCurAccount().getLoginname());
            contacts = contactBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts != null && contacts.size() > 0;
    }

    //************获取班级列表 开始*****************//
    private void syncClassRoom() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("commandtype", "getJoinedClassroomList");
        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
                Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optInt("ret") == 0) {
                    try{
                        List<ClassRoom> classList1 = ClassRoom.parseFromJson(
                                response.optJSONArray("data"), 1);
                        List<ClassRoom> classList2 = ClassRoom.parseFromJson(
                                response.optJSONArray("data2"), 2);
                        classList1.addAll(classList2);
                        syncClassRoomSuccess(classList1);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }

    private void syncClassRoomSuccess(List<ClassRoom> classRooms) {
        LogUtils.e("Finish Net,Start Local****");
        SaveClassRoomTask saveTask = new SaveClassRoomTask(classRooms);
        saveTask.execute();
    }
    class SaveClassRoomTask extends AsyncTask<Void, Void, Boolean> {

        private List<ClassRoom> classRooms;

        SaveClassRoomTask(List<ClassRoom> classRooms) {
            this.classRooms = classRooms;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String loginName = BaseApplication.getInstance().getDefaultAccount().getLoginname();
            try {
                DeleteBuilder<ClassRoom, Integer> deleteClassroomBuilder = dataHelper.getClassRoomData().deleteBuilder();
                deleteClassroomBuilder.where().eq("loginName", loginName);
                int result2 = deleteClassroomBuilder.delete();
                LogUtils.e("ClassRoomData().deleteBuilder().delete():" + result2);
                if(classRooms != null && classRooms.size() > 0) {
	                for (int i = 0; i < classRooms.size(); i++) {
	                	ClassRoom classroom = classRooms.get(i);
	                	if(i == 0)classroom.setDefaultClass(1);
	                    LogUtils.e("******"+classroom.getName());
	                    dataHelper.getClassRoomData().create(classroom);
	                    fetchClazzMember(classroom.getId());
	                }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }
    }

    private void fetchClazzMember(final long classId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("commandtype", "getClassroomRemListByCId");
        params.put("classroomId", String.valueOf(classId));

        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optInt("ret") == 0) {
                    List<Contact> contactList = Contact.parseFromJsonByClassMember(response
                            .optJSONArray("data"), classId);
                    syncContactsSuccess(contactList, (int)classId);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }
    //************获取班级列表 结束*****************//
    //************获取好友列表 开始*****************//
    private void syncContact() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("commandtype", "getJoinedFriendList");
        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL,
                Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optInt("ret") == 0) {
                    List<Contact> contactList = Contact.parseFromJson(response
                            .optJSONArray("data"));
                    List<Contact> tmp1 = new ArrayList<Contact>();
                    List<Contact> tmp2 = new ArrayList<Contact>();
                    if(contactList!=null && contactList.size()>0){
                        for(Contact c:contactList){
                            if(c.getUsertype() == 1){
                                tmp1.add(c);
                            }else {
                                tmp2.add(c);
                            }
                        }
                    }
                    syncContactsSuccess(tmp1, 1);
                    syncContactsSuccess(tmp2, 2);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }

    private void syncContactsSuccess(List<Contact> contacts, int userType) {
        SaveContactTask saveTask = new SaveContactTask(contacts, userType);
        saveTask.execute();
    }

    class SaveContactTask extends AsyncTask<Void, Void, Boolean> {

        private List<Contact> contacts;
        private int userType;

        SaveContactTask(List<Contact> contacts, int userType) {
            this.contacts = contacts;
            this.userType = userType;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
        	if(BaseApplication.getInstance()!=null && BaseApplication.getInstance().getDefaultAccount()!=null) {
        		String loginName = BaseApplication.getInstance().getDefaultAccount().getLoginname();
	            if(contacts != null && contacts.size() > 0) {
		            try {
		                DeleteBuilder<Contact, Integer> deleteContactBuilder = dataHelper.getContactData().deleteBuilder();
		                deleteContactBuilder.where().eq("loginName", loginName).and().eq("usertype", userType);
		                int result1 = deleteContactBuilder.delete();
		                LogUtils.i("ContactData().deleteBuilder().delete():" + result1);
		                for (Contact contact : contacts) {
		                	contact.setUsertype(userType);
		                    dataHelper.getContactData().create(contact);
		                }
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
	            }
        	}
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }
    }
    //************获取好友列表 结束*****************//

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApp.stopIMService();
        mApp.cancelAllRequest();
        //BaseApplication.getInstance().cancelPendingRequests(TAG);
    }

    private long mkeyTime;
    
	public void onBackPressed() {
		Boolean cangoback;
		try{
			cangoback = JzhFragment.onKeyDown();
		}catch(Exception e){
			cangoback = false;
		}
		if(cangoback == false){
//			dialog = new CommonDialog(MainActivity.this, "提示消息", "退出和校园客户端？", "取消", "退出");
//			dialog.setOkListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//                    mApp.stopIMService();
//                    
//					MainActivity.this.finish();
//				}
//			});
//			dialog.setCancelListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(dialog.isShowing())
//					dialog.dismiss();
//				}
//			});
//			dialog.show();
		    
            if ((System.currentTimeMillis() - mkeyTime) > 2000)
            {
                mkeyTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出和校园", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mApp.stopIMService();
                MainActivity.this.finish();
            }
		}  
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		mContent.onActivityResult(arg0, arg1, arg2);
	}
}
