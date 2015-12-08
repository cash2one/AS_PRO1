package com.linkage.mobile72.sh.fragment;

import java.sql.SQLException;
import java.text.ParseException;
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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.linkage.mobile72.sh.activity.JxHomeworkListActivity;
import com.linkage.mobile72.sh.activity.JxHomeworkListActivity3;
import com.linkage.mobile72.sh.activity.KaoqinActivity2;
import com.linkage.mobile72.sh.activity.MainActivity;
import com.linkage.mobile72.sh.activity.OrderJxInteractionActivity;
import com.linkage.mobile72.sh.activity.ScoreActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.app.BaseFragment;
import com.linkage.mobile72.sh.data.AccountChild;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.http.JXBean;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.AdActionUtils;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.lib.util.LogUtils;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.PullToRefreshBase;
import com.linkage.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.linkage.ui.widget.PullToRefreshScrollView;

public class JxParentFragment extends BaseFragment implements OnClickListener {
	
	private final static String TAG = JxParentFragment.class.getName();
	
	private AccountData account;
	private CircularImage avatar;
	private Button configBtn;
	private ImageView indicate;
	/**
	 * 顶部滚动广告需要的
	 */
	private List<NetworkImageView> adImgs;// 广告图片
	private ImageView[] indicators;// 圆点指示器
	private ViewGroup indicatorLayout;// 圆点指示器布局
	private AdImgAdapter mAdImgAdapter;
	private ViewPager mViewPager;

	private PullToRefreshScrollView scrollView;
	public ImageView attendRemind;
	private RelativeLayout homeworkLayout,noticeLayout,commentLayout,kaoqingLayout,scoreLayout;
	private TextView title, todaysHomework;
	private PopupWindow popWindow;
	private List<AccountChild> childs;
	private AccountChild defaultChild;
	private View fragmentView;
	private SimpleDateFormat dateFormat1;
	private ScheduledExecutorService scheduledExecutorService;
    private int currentItem; //当前页面
    private MyCommonDialog dialog;
    
	public static JxParentFragment newInstance() {
		JxParentFragment fragment = new JxParentFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		BaseApplication.getInstance().jxParentFragment = fragment;
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		account = getCurAccount();
		dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_jxparent, container, false);
		fragmentView = view;
		avatar = (CircularImage) view.findViewById(R.id.user_avater);
		imageLoader.displayImage(account.getAvatar(),avatar);
		configBtn = (Button) view.findViewById(R.id.jia_button);
		configBtn.setVisibility(View.INVISIBLE);
		indicate = (ImageView)view.findViewById(R.id.indicate);
		childs = getAccountChild();
		if(childs != null && childs.size() > 0) {
			if(childs.size() == 1) {
				indicate.setVisibility(View.GONE);
				defaultChild = childs.get(0);
			}else {
				indicate.setVisibility(View.GONE);
				configBtn.setVisibility(View.VISIBLE);
				configBtn.setBackgroundDrawable(null);
	            configBtn.setText("切换");
	            configBtn.setOnClickListener(this);
				initPopWindow();
				for(AccountChild c : childs) {
					if(c.getDefaultChild() == 1) {
						defaultChild = c;
						break;
					}
				}
			}
		}
		if(defaultChild!=null){
			setTitle(view, defaultChild.getName());
		}else{
			setTitle(view,"家校互动");
		}
		title = (TextView)view.findViewById(R.id.title);
		title.setOnClickListener(this);
		
		indicatorLayout = (ViewGroup) view.findViewById(R.id.tips);
		mViewPager = (ViewPager) view.findViewById(R.id.today_topic_layout);
		adImgs = new ArrayList<NetworkImageView>();
		mAdImgAdapter = new AdImgAdapter(adImgs);
		mViewPager.setAdapter(mAdImgAdapter);
		MainActivity.slideMenu.addIgnoredView(mViewPager);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
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
		mViewPager.setCurrentItem(0);
		
		scrollView = (PullToRefreshScrollView)view.findViewById(R.id.scrollView);
		
		homeworkLayout = (RelativeLayout)view.findViewById(R.id.homework_layout);
		noticeLayout = (RelativeLayout)view.findViewById(R.id.notice_layout);
		commentLayout = (RelativeLayout)view.findViewById(R.id.comment_layout);
		scoreLayout = (RelativeLayout)view.findViewById(R.id.score_layout);
		kaoqingLayout = (RelativeLayout)view.findViewById(R.id.attend_layout);
		todaysHomework = (TextView)view.findViewById(R.id.textView2);
		kaoqingLayout.setVisibility(View.GONE);
//		kaoqingTextView = (TextView)view.findViewById(R.id.textView_kaoqing);
//		kaoqinginfoTextView = (TextView)view.findViewById(R.id.textView_kaoqing_info);
		homeworkLayout.setOnClickListener(this);
		noticeLayout.setOnClickListener(this);
		commentLayout.setOnClickListener(this);
		kaoqingLayout.setOnClickListener(this);
		scoreLayout.setOnClickListener(this);
		attendRemind = (ImageView)view.findViewById(R.id.attend_remind);
		
		
		
		scrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						scrollView.onRefreshComplete();
					}
				}, 1000);
			}
		});
		scrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				refreshData(0);
			}
			
		});
		avatar.setOnClickListener(this);
		MainActivity.slideMenu.addIgnoredView(mViewPager);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		refreshData(1);
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 6, 6, TimeUnit.SECONDS);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshData(2);
		SharedPreferences sp = BaseApplication.getInstance().getSharedPreferences(""+getDefaultAccountChild().getId(), Context.MODE_PRIVATE);
        int i = sp.getInt("attend_notice", 0);
        if(i == 1) {
        	attendRemind.setVisibility(View.VISIBLE);
        }else {
        	attendRemind.setVisibility(View.INVISIBLE);
        }
        account = getCurAccount();
		if (account != null)
			imageLoader.displayImage(account.getAvatar(), avatar);
	}
	
	private void refreshData(int type) {
		if(type == 1) {
			getRollAds();
		}else if (type == 2) {
			getLatestHomework();
		}else {
			getRollAds();
			getLatestHomework();
		}
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
						LogUtils.i(TAG + "response=" + response);
						if (response.optInt("ret") == 0) {
							scrollView.onRefreshComplete();
							JSONArray array = response.optJSONArray("data");
							initRollAdView(array);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						scrollView.onRefreshComplete();
						StatusUtils.handleError(arg0, getActivity());
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}

	// 初始化滚动广告的views
	private void initRollAdView(JSONArray array) {
		adImgs.clear();
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
					AdActionUtils.open(getActivity(), obj.optString("action"), obj.optString("monitorParam"));
				}
			});
			adImgs.add(img);
		}
		// 圆点
		indicatorLayout.removeAllViews();
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
		scrollView.getRefreshableView().smoothScrollTo(0, 0);
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
	
	private void getLatestHomework() {
		if(defaultChild == null) {
			todaysHomework.setText("今日暂无作业");
			todaysHomework.setTextColor(getResources().getColor(R.color.jx_home_parent_homework_color));
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getTodayMessage");
		params.put("studentid", defaultChild.getId()+"");
		params.put("smsMessageType", "14");// 参看接口固定值
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getTodayMessage,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						scrollView.onRefreshComplete();
						if (response.optInt("ret") == 0) {
							JXBean homework = JXBean.parseFromJsonForParentHome(response.optJSONObject("data"));
							if(homework != null && !TextUtils.isEmpty(homework.getMessageContent())) {
								Date sendTime = null;
								try {
									sendTime = dateFormat1.parse(homework.getRecvTime());
								} catch (ParseException e) {
									e.printStackTrace();
								}
								if(sendTime != null) {
//										todayHomeworkTime.setText(dateFormat2.format(sendTime));
								}
								todaysHomework.setText(""+homework.getMessageContent());
								todaysHomework.setTextColor(getResources().getColor(R.color.black));
							}else {
								todaysHomework.setText("今日暂无作业");
								todaysHomework.setTextColor(getResources().getColor(R.color.jx_home_parent_homework_color));
							}
						}else {
							todaysHomework.setText("今日暂无作业");
							todaysHomework.setTextColor(getResources().getColor(R.color.jx_home_parent_homework_color));
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						scrollView.onRefreshComplete();
						StatusUtils.handleError(arg0, getActivity());
						todaysHomework.setText("今日暂无作业");
						todaysHomework.setTextColor(getResources().getColor(R.color.jx_home_parent_homework_color));
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	private void getHomeAndSchoolOpenState(final int toPage) {
		ProgressDialogUtils.showProgressDialog("", getActivity(), true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getHomeAndSchoolOpenState");
		params.put("studentid", defaultChild.getId()+"");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getHomeAndSchoolOpenState,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						LogUtils.e(response.toString());
						if (response.optInt("ret") == 0) {
							String token = response.optString("token");
							String url = response.optString("url");
							Intent mIntent = new Intent(getActivity(), OrderJxInteractionActivity.class);
		                    mIntent.putExtra(OrderJxInteractionActivity.KEY_TITLE, "订购家校互动");
		                    mIntent.putExtra(OrderJxInteractionActivity.KEY_TOKEN, token);
		                    mIntent.putExtra(OrderJxInteractionActivity.KEY_GOTO, toPage);
		                    mIntent.putExtra(OrderJxInteractionActivity.KEY_URL, url);
		                    startActivity(mIntent);
						}else {
							Toast.makeText(getActivity(), "获取token失败", Toast.LENGTH_SHORT).show();
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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		    case R.id.jia_button:
		        if(childs != null && childs.size() > 1) {
	                if(popWindow.isShowing()) {
	                    indicate.setImageResource(R.drawable.jx_parent_choose_child_down);
	                    popWindow.dismiss();
	                }else {
	                    indicate.setImageResource(R.drawable.jx_parent_choose_child_up);
	                    popWindow.showAsDropDown(configBtn, 0, 15);
	                    backgroundAlpha(0.5f);
	                }
	            }
		        break;
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
			/*if(childs != null && childs.size() > 1) {
				if(popWindow.isShowing()) {
					indicate.setImageResource(R.drawable.jx_parent_choose_child_down);
					popWindow.dismiss();
				}else {
					indicate.setImageResource(R.drawable.jx_parent_choose_child_up);
					backgroundAlpha(0.5f);
					popWindow.showAsDropDown(title, 0, 15);
				}
			}*/
			break;
		case R.id.homework_layout:
			gotoNext(Consts.JxhdType.HOMEWORK);
			break;
		case R.id.notice_layout:
			gotoNext(Consts.JxhdType.NOTICE);
			break;
		case R.id.comment_layout:
			gotoNext(Consts.JxhdType.COMMENT);
			break;
		case R.id.score_layout:
			gotoNext(20);
			break;
		case R.id.attend_layout:
			gotoNext(21);
			break;
		}
	}
	
	private void gotoNext(final int toPage) {
//		if (defaultChild != null && defaultChild.getXxt_type() != 1) {
//	        dialog = new MyCommonDialog(getActivity(), "提示消息", "您现在还没有订购家校互动业务，请您填写资料并开通。", "取消", "确认");
//            dialog.setOkListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                    getHomeAndSchoolOpenState(toPage);
//                }
//            });
//            dialog.setCancelListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                    Intent mIntent = null;
//                    if(toPage > 0 && toPage < 20) {
//                    	mIntent = new Intent(getActivity(), JxHomeworkListActivity3.class);
//                    	mIntent.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE, String.valueOf(toPage));
//                    }else if(toPage == 20) {
//                        mIntent = new Intent(getActivity(), ScoreActivity.class);
//                    }else if(toPage == 21) {
//                    	mIntent = new Intent(getActivity(), KaoqinActivity2.class);
//                    }
//                    startActivity(mIntent);
//                }
//            });
//            dialog.show();
//        } else {
        	Intent mIntent = null;
            if(toPage > 0 && toPage < 20) {
            	mIntent = new Intent(getActivity(), JxHomeworkListActivity3.class);
            	mIntent.putExtra(JxHomeworkListActivity.KEY_SMSMESSAGETYPE, String.valueOf(toPage));
            }else if(toPage == 20) {
                mIntent = new Intent(getActivity(), ScoreActivity.class);
            }else if(toPage == 21) {
            	mIntent = new Intent(getActivity(), KaoqinActivity2.class);
            }
            startActivity(mIntent);
//        }
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
				AccountChild child = adapter.getItem(position);
				if(child.getId() != defaultChild.getId()) {
					ProgressDialogUtils.showProgressDialog("", getActivity(), true);
					try {
						long childid = child.getId();
						long userid = child.getUserid();
						getDBHelper().getAccountChildDao().updateRaw("update AccountChild set defaultChild = 0 where userid = " + userid);
						getDBHelper().getAccountChildDao().updateRaw("update AccountChild set defaultChild = 1 where userid = " + userid + " and id = " + childid);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					defaultChild = child;
					refreshData(2);
					setTitle(fragmentView, defaultChild.getName());
				}
				if(popWindow != null && popWindow.isShowing()) {
					popWindow.dismiss();
				}
			}
		});
        listView.setItemsCanFocus(false);    
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        popWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setOutsideTouchable(true); 
        popWindow.setFocusable(true);
        popWindow.update();
        popWindow.setOnDismissListener(new PpoponDismissListener());
	}
	
	public void backgroundAlpha(float bgAlpha) {  
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();  
        lp.alpha = bgAlpha; //0.0-1.0  
        getActivity().getWindow().setAttributes(lp);  
    }  
	
	class PpoponDismissListener implements PopupWindow.OnDismissListener{
        @Override  
        public void onDismiss() {
            backgroundAlpha(1f);
            indicate.setImageResource(R.drawable.jx_parent_choose_child_down);
        }  
    }
	
	class ChildAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return childs.size();
		}

		@Override
		public AccountChild getItem(int position) {
			// TODO Auto-generated method stub
			return childs.get(position);
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
			AccountChild r = getItem(position);
			viewHolder.textView.setText(r.getName());
			viewHolder.textView.setTextColor(Color.rgb(96, 205, 246));
			
			return convertView;
		}

		class ViewHolder {
			public TextView textView;
		}
		
	}
	//切换图片
    private class ViewPagerTask implements Runnable
    {
        @Override
        public void run()
        {
            currentItem = (currentItem + 1) % adImgs.size();
            handler.obtainMessage().sendToTarget();
        }
    }
    
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            mViewPager.setCurrentItem(currentItem);
        }
    };
}