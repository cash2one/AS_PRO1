package com.linkage.mobile72.sh.activity.sports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import org.json.JSONObject;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.RankListAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.DistanceRunData;
import com.linkage.mobile72.sh.data.RankNumber;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.widget.MyCommonDialog;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

public class MapPatternActivity extends BaseActivity implements OnClickListener, OnChronometerTickListener{

	private static final String TAG = MapPatternActivity.class.getSimpleName();
	
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocationClient;//定位SDK的核心类
	private MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	private TextView runType;
	private Button timeStartBtn, timeEndBtn;
//	private static TimeThread mTimeThread;
	private MyCommonDialog myCommonDialog;
	private ArrayAdapter<String> mAdapter;
	private String[] strs = {"800米", "1000米", "1500米"};
	private Long[] categorys ; 
	private Chronometer chronometer;
	private int miss=0;
	private List<LatLng> points;
	private List<Integer> index;
	private int position = 0;
	private boolean isStart = false;
	double allDistance = 0d;
	private LocationManager locationManager;
	private int images[] = {R.drawable.icon1, R.drawable.icon2, R.drawable.icon3,
			R.drawable.icon4, R.drawable.icon5, R.drawable.icon6};
	
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			runType.setText(mAdapter.getItem(position)+"米");
			myCommonDialog.dismiss();
		}
	};
	
	private Handler mhHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_map);
		
		setTitle(R.string.sports_title);
		findViewById(R.id.back).setOnClickListener(this);
//		getDistanceCategory();
		mAdapter = new ArrayAdapter<String>(this,
				R.layout.distance_run_typre_item, R.id.type_text,strs);
		myCommonDialog = new MyCommonDialog(MapPatternActivity.this, "设定您准备跑步的距离",mAdapter, mItemClickListener, null, "取消");
		myCommonDialog.setOkListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				myCommonDialog.dismiss();
			}
		});
		myCommonDialog.show();
		
		runType = (TextView)findViewById(R.id.run_type_text);
		chronometer = (Chronometer)findViewById(R.id.chronometer);
		chronometer.setText("00:00:00");
		chronometer.setOnChronometerTickListener(this);
		
		timeStartBtn = (Button)findViewById(R.id.time_start);
		timeEndBtn = (Button)findViewById(R.id.time_end);
		timeStartBtn.setOnClickListener(this);
		timeEndBtn.setOnClickListener(this);
		
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mCurrentMode = LocationMode.NORMAL;
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
						mCurrentMode, true, mCurrentMarker));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(16).build()));
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		initLocation();
		points = new ArrayList<LatLng>();
		index = new ArrayList<Integer>();
	}
	
	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius()).satellitesNum(location.getSatelliteNumber())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			LogUtils.e("locData.latitude:"+locData.latitude+"----------locData.longitude:"+locData.longitude);
			mBaiduMap.setMyLocationData(locData);
//			if (isFirstLoc) {
//				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			if (isStart) {
				points.add(ll);
				index.add(position);
				position ++;
				DistanceUtil diUtil = new DistanceUtil();
				LatLng latLng = null;;
				if (position > 1 && position < 999) {
					//构造对象
					OverlayOptions ooPolyline = new PolylineOptions().width(4).color(0xAAFF0000).points(points).textureIndex(index);
					//添加到地图
					mBaiduMap.addOverlay(ooPolyline);
					double distance = diUtil.getDistance(latLng, ll);
					allDistance += distance;
					if (allDistance > 800) {
						isStart = false;
					}
				}
				latLng = ll;
			}	
//			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	private void getDistanceCategory(){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("commandtype", "getSetToRunCategory");
			
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_URL_NEW, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						LogUtils.e("response=" + response);
						if (response.optInt("ret") == 0) {
							ArrayList<DistanceRunData> sorts = DistanceRunData.parseFromJson(response.optJSONArray("data"));
							if (sorts != null && sorts.size() >0) {
								for (int i = 0; i < sorts.size(); i++) {
									categorys[i] = sorts.get(i).getRange();
								}
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						StatusUtils.handleError(error, MapPatternActivity.this);
					}
				}
			);
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override  
    protected void onResume() {  
        super.onResume();  
        mMapView.onResume();  
    }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        mMapView.onPause();  
    }  
 
    @Override  
    protected void onDestroy() {  
    	mLocationClient.stop();
    	mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();  
        mMapView = null;  
        super.onDestroy();  
    } 
    
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationMode.Hight_Accuracy);//设置高精度定位定位模式
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll");//设置百度经纬度坐标系格式
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为1000ms
        option.setIsNeedAddress(true);//反编译获得具体位置，只有网络定位才可以
        mLocationClient.setLocOption(option);
		mLocationClient.start();
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.time_start:
			miss = 0;
			chronometer.setBase(SystemClock.elapsedRealtime());
			chronometer.start();
			timeStartBtn.setEnabled(false);
			isStart = true;
			break;
		case R.id.time_end:
			chronometer.stop(); 
			timeStartBtn.setEnabled(true);
			isStart = false;
			clearClick();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onChronometerTick(Chronometer chronometer) {
		chronometer.setText(FormatMiss(miss));
		miss++;
	}
	
	public static String FormatMiss(int miss){     
        String hh=miss/3600>9?miss/3600+"":"0"+miss/3600;
        String  mm=(miss % 3600)/60>9?(miss % 3600)/60+"":"0"+(miss % 3600)/60;
        String ss=(miss % 3600) % 60>9?(miss % 3600) % 60+"":"0"+(miss % 3600) % 60;
        return hh+":"+mm+":"+ss;      
    }
	
	private void clearClick() {
		// 清除所有图层
		mMapView.getMap().clear();
		points.clear();
		index.clear();
	}
}
