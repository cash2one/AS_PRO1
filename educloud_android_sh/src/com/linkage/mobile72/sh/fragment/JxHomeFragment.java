package com.linkage.mobile72.sh.fragment;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.AttenActivity;
import com.linkage.mobile72.sh.activity.ClazzInviteActivity;
import com.linkage.mobile72.sh.activity.ClazzScoreActivity;
import com.linkage.mobile72.sh.activity.CreateCommentActivity;
import com.linkage.mobile72.sh.activity.CreateHomeworkActivity;
import com.linkage.mobile72.sh.activity.CreateNoticeActivity;
import com.linkage.mobile72.sh.activity.CreateOfficesmsActivity;
import com.linkage.mobile72.sh.activity.JxMbManagerListActivity;
import com.linkage.mobile72.sh.activity.KaoqinActivity;
import com.linkage.mobile72.sh.activity.MainActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.APK.APKUtils;
import com.linkage.mobile72.sh.utils.AdActionUtils;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

@SuppressLint("ClickableViewAccessibility")
public class JxHomeFragment extends BaseFragment implements OnClickListener {
	
	private final static String TAG = "JxHomeFragment";

	private AccountData account;
	private CircularImage avatar;
	private TextView title;
	private Button configBtn;

	/**
	 * 顶部滚动广告需要的
	 */
	private List<NetworkImageView> adImgs;// 广告图片
	private ImageView[] indicators;// 圆点指示器
	private ViewGroup indicatorLayout;// 圆点指示器布局
	private AdImgAdapter mAdImgAdapter;
	private ViewPager mImageSwitcher;

	private RelativeLayout homeworkLayout, noticeLayout, commentLayout, mbLayout;
	private RelativeLayout gzsjLayout, officesmsLayout, kaoqinLayout, scoreLayout;

	private SimpleDateFormat dateFormat;
	private ImageView indicate;
    private PopupWindow popWindow;
    private List<ClassRoom> mClassRoomList;
    private ClassRoom mCurrentClass;
    private View fragmentView;
    
    private ScheduledExecutorService scheduledExecutorService;
    private int oldPosition = 0;//记录上一次点的位置
    private int currentItem; //当前页面
	
	public static JxHomeFragment newInstance() {
		JxHomeFragment fragment = new JxHomeFragment();
		Bundle args = new Bundle();

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		account = getCurAccount();
		dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		mCurrentClass = getDefaultAccountClass();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		account = getCurAccount();
		if (account != null) {
//			imageLoader.displayImage(account.getAvatar(), avatar);
			mClassRoomList = getAccountClass();
			if(mClassRoomList != null && mClassRoomList.size() > 0) {
				if(mClassRoomList.size() == 1) {
					indicate.setVisibility(View.INVISIBLE);
					mCurrentClass = mClassRoomList.get(0);
					refreshDefaultClassRoom();
				}else {
					//indicate.setVisibility(View.VISIBLE);
					indicate.setVisibility(View.INVISIBLE);
					initPopWindow();
					for(ClassRoom c : mClassRoomList) {
						if(c.getDefaultClass() == 1) {
							mCurrentClass = c;
							break;
						}
					}
					if(mCurrentClass == null) {
						mCurrentClass = mClassRoomList.get(0);
						refreshDefaultClassRoom();
					}
				}
				setTitle(fragmentView, "家校互动");
				//setTitle(fragmentView, mCurrentClass.getName());
			}else {
				setTitle(fragmentView, "家校互动");
			}
			imageLoader.displayImage(account.getAvatar(), avatar);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_jxhome, container, false);
		fragmentView = view;
		avatar = (CircularImage) view.findViewById(R.id.user_avater);
		imageLoader.displayImage(account.getAvatar(), avatar);
		indicate = (ImageView)view.findViewById(R.id.indicate);
		setTitle(view, "家校");
		configBtn = (Button) view.findViewById(R.id.jia_button);
		configBtn.setVisibility(View.GONE);
		configBtn.setText("班级邀请");
		configBtn.setOnClickListener(this);
		
		title = (TextView)view.findViewById(R.id.title);
		indicate.setVisibility(View.GONE);
		//title.setOnClickListener(this);
		
		homeworkLayout = (RelativeLayout) view.findViewById(R.id.jxhd_index_homework);
		noticeLayout = (RelativeLayout) view.findViewById(R.id.jxhd_index_notice);
		commentLayout = (RelativeLayout) view.findViewById(R.id.jxhd_index_comment);
//        gzsjLayout = (RelativeLayout) view.findViewById(R.id.jxhd_index_gzsj);
		mbLayout = (RelativeLayout) view.findViewById(R.id.jxhd_index_mb);
		officesmsLayout = (RelativeLayout) view.findViewById(R.id.jxhd_index_officesms);
		kaoqinLayout = (RelativeLayout) view.findViewById(R.id.jxhd_index_kaoqin);
		scoreLayout = (RelativeLayout) view.findViewById(R.id.jxhd_index_score);
		kaoqinLayout.setVisibility(View.GONE);
//		scoreLayout.setVisibility(View.INVISIBLE);
		indicatorLayout = (ViewGroup) view.findViewById(R.id.tips);
		mImageSwitcher = (ViewPager) view.findViewById(R.id.today_topic_layout);
		adImgs = new ArrayList<NetworkImageView>();
		mAdImgAdapter = new AdImgAdapter(adImgs);
		mImageSwitcher.setAdapter(mAdImgAdapter);

		// mImageSwitcher.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View arg0, MotionEvent event) {
		// arg0.getParent().requestDisallowInterceptTouchEvent(true);
		//
		// return false;
		// }
		// });
//		MainActivity.slideMenu.addIgnoredView(mImageSwitcher);
		mImageSwitcher.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				setImageBackground(position % adImgs.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		mImageSwitcher.setCurrentItem(0);
		avatar.setOnClickListener(this);
		homeworkLayout.setOnClickListener(this);
		noticeLayout.setOnClickListener(this);
		commentLayout.setOnClickListener(this);
//        gzsjLayout.setOnClickListener(this);
		mbLayout.setOnClickListener(this);
		officesmsLayout.setOnClickListener(this);
		kaoqinLayout.setOnClickListener(this);
		scoreLayout.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getRollAds();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 6, 6, TimeUnit.SECONDS);
	}

	// 获取顶部滚动广告
	private void getRollAds() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getRollAds");
		params.put("radsType", "1");// 参看接口固定值
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getRollAds,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						LogUtils.i(TAG + ":getRollAds:response=" + response);
						if (response.optInt("ret") == 0) {
							JSONArray array = response.optJSONArray("data");
							initRollAdView(array);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, getActivity());
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	// 初始化滚动广告的views
	private void initRollAdView(JSONArray array) {
		int imgLen = array.length();
		indicators = new ImageView[imgLen];
		for (int i = 0; i < imgLen; i++) {
			final JSONObject obj = array.optJSONObject(i);

			NetworkImageView img = new NetworkImageView(getActivity());
			img.setDefaultImageResId(R.drawable.app_one);
			img.setImageUrl(obj.optString("url"), BaseApplication.getInstance()
					.getNetworkImageLoader());
			int width = Utils.getWindowWidth(getActivity());
			int height = (int)(width * 0.4);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(width, height));
			img.setLayoutParams(layoutParams);
			img.setScaleType(ScaleType.CENTER_CROP);
			img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

//					AdActionUtils.open(getActivity(), obj.optString("action"), obj.optString("monitorParam"));
				}
			});
			adImgs.add(img);
		}
		// 圆点
		for (int i = 0; i < imgLen; i++) {
			ImageView mDot = new ImageView(getActivity());
			mDot.setBackgroundResource(R.drawable.dot);
			indicators[i] = mDot;

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			layoutParams.rightMargin = 5;
			layoutParams.leftMargin = 5;
			layoutParams.bottomMargin = 5;
			layoutParams.width = 17;
			layoutParams.height = 17;
			indicatorLayout.addView(indicators[i], layoutParams);
		}
		setImageBackground(0);
		mAdImgAdapter.notifyDataSetChanged();
		scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 6, 6, TimeUnit.SECONDS);
	}

	private void setImageBackground(int selectItems) {
		for (int i = 0; i < indicators.length; i++) {
			if (i == selectItems) {
				indicators[i].setBackgroundResource(R.drawable.dot_select);
			} else {
				indicators[i].setBackgroundResource(R.drawable.dot);
			}
		}
	}

	class AdImgAdapter extends PagerAdapter {
		List<NetworkImageView> views;

		public AdImgAdapter(List<NetworkImageView> imgs) {
			views = imgs;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(views.get(position));
			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return views.size();
		}
	};

	@Override
	public void onClick(View v) {
		Intent mIntent;
		switch (v.getId()) {
		case R.id.user_avater:
			if (!MainActivity.slideMenu.isMenuShowing()) {
				new Handler().post(new Runnable() {
					public void run() {
						MainActivity.slideMenu.showMenu(true);
					}
				});
			}
			break;
		case R.id.title:
			if(mClassRoomList != null && mClassRoomList.size() > 1) {
				if(popWindow.isShowing()) {
					indicate.setImageResource(R.drawable.jx_parent_choose_child_down);
					popWindow.dismiss();
				}else {
					indicate.setImageResource(R.drawable.jx_parent_choose_child_up);
					popWindow.showAsDropDown(title, 0, 15);
				}
			}
			break;
		case R.id.jxhd_index_homework:
//			mIntent = new Intent(getActivity(), JxHomeworkListActivity.class);
//			mIntent.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
//					JxHomeworkListActivity.SMSMESSAGETYPE_HOMEWORK);
		    mIntent = new Intent(getActivity(), CreateHomeworkActivity.class);
			startActivity(mIntent);
			break;
		case R.id.jxhd_index_notice:
//			mIntent = new Intent(getActivity(), JxHomeworkListActivity.class);
//			mIntent.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
//					JxHomeworkListActivity.SMSMESSAGETYPE_NOTICE);
		    mIntent = new Intent(getActivity(), CreateNoticeActivity.class);
			startActivity(mIntent);
			break;
		case R.id.jxhd_index_comment:
//			mIntent = new Intent(getActivity(), JxHomeworkListActivity.class);
//			mIntent.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE,
//					JxHomeworkListActivity.SMSMESSAGETYPE_COMMENT);
		    mIntent = new Intent(getActivity(), CreateCommentActivity.class);
			startActivity(mIntent);
			break;
        case R.id.jxhd_index_gzsj:
           /*if(Utils.checkApkExist(getActivity(), "com.roya.vwechat")) {
                Utils.runApp(getActivity(), "com.roya.vwechat");
            }else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://112.4.17.117:10016/v/wapdownload.html")));
            }*/
//        	AdActionUtils.open(getActivity(), "http://aservice.139jy.cn/educloud/html/ucenter/v.html", "2");
			PackageManager pm = getActivity().getPackageManager();
			Intent intent = pm.getLaunchIntentForPackage("com.linkage.webviewapp");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);

            break;
		case R.id.jxhd_index_officesms:
//			Intent intentOfficesms = new Intent(getActivity(), JxOfficesmsListActivity.class);
		    Intent intentOfficesms = new Intent(getActivity(), CreateOfficesmsActivity.class);
			startActivity(intentOfficesms);
			break;
		case R.id.jxhd_index_mb:
			Intent intentMbManager = new Intent(getActivity(), JxMbManagerListActivity.class);
			startActivity(intentMbManager);
			break;
		case R.id.jxhd_index_kaoqin:
			checkTodayAttandance();
			break;
		case R.id.jxhd_index_score:
			Intent intentScore = new Intent(getActivity(), ClazzScoreActivity.class);
			startActivity(intentScore);
			break;
		case R.id.jia_button:
			startActivity(new Intent(getActivity(), ClazzInviteActivity.class));
			break;
		}
	}
	
	private void checkTodayAttandance() {
		mCurrentClass = getDefaultAccountClass();
		mClassRoomList = getAccountClass();
		if(mClassRoomList != null && mClassRoomList.size() > 0) {
			if(mClassRoomList.size() == 1) {
				mCurrentClass = mClassRoomList.get(0);
			}else {
				for(ClassRoom c : mClassRoomList) {
					if(c.getDefaultClass() == 1) {
						mCurrentClass = c;
						break;
					}
				}
				if(mCurrentClass == null) {
					mCurrentClass = mClassRoomList.get(0);
				}
			}
		}
		if(mCurrentClass ==null) {
			 Toast.makeText(getActivity(), "没有可以管理的班级", Toast.LENGTH_SHORT).show();
			return;
		}
		ProgressDialogUtils.showProgressDialog("", getActivity(), true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getClassAttendanceByDate");
		params.put("classid", String.valueOf(mCurrentClass.getId()));
		params.put("date", dateFormat.format(new Date()));
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogUtils.dismissProgressBar();
				//-1 服务器try catch一些异常
				//0 已考勤 不管全到还是没到
				//1 未考勤
				if (response.optInt("ret") == 1) {
					Intent intentAtten = new Intent(getActivity(), AttenActivity.class);
					startActivity(intentAtten);
				}else {
					Intent intentAtten = new Intent(getActivity(), KaoqinActivity.class);
					startActivity(intentAtten);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressDialogUtils.dismissProgressBar();
				StatusUtils.handleError(arg0, getActivity());
			}
		});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	private void initPopWindow() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
        // 引入窗口配置文件  
        View view = inflater.inflate(R.layout.pop_jx_parent_choose_child, null);
        ListView listView = (ListView)view.findViewById(R.id.listView);
        final ChildAdapter adapter = new ChildAdapter();
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ClassRoom child = adapter.getItem(position);
				mCurrentClass = child;
				refreshDefaultClassRoom();
				//refreshData();
				if(popWindow.isShowing()) {
					popWindow.dismiss();
				}
				setTitle(fragmentView, mCurrentClass.getName());
			}
		});
        listView.setItemsCanFocus(false);    
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        popWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, false);
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
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_single_text_center, null);
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
			getDBHelper().getClassRoomData().updateRaw("update ClassRoom set defaultClass = 0 where loginName = " + getAccountName() + " and joinOrManage = 1");
			getDBHelper().getClassRoomData().updateRaw("update ClassRoom set defaultClass = 1 where loginName = " + getAccountName() + " and joinOrManage = 1" + " and id = " + mCurrentClass.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//切换图片
    private class ViewPagerTask implements Runnable {
   
    @Override
    public void run() {
   // TODO Auto-generated method stub
       
   currentItem = (currentItem +1) % adImgs.size();
//    LogUtils.i(TAG + "currentItem"+currentItem);
    handler.obtainMessage().sendToTarget(); 
    }
   }
    
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            LogUtils.i(TAG + "Handler:currentItem"+currentItem);
            mImageSwitcher.setCurrentItem(currentItem);
        }
    };
}