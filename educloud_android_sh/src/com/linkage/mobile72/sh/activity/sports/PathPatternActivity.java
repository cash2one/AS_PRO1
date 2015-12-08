package com.linkage.mobile72.sh.activity.sports;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.lib.util.LogUtils;

public class PathPatternActivity extends BaseActivity implements OnClickListener{
	
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocationClient;//定位SDK的核心类
	private MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	private ImageView signalImg;
	private int signalLevel = 0;
	
	private TextView dateText, startTimeText, endTimeText;
	private Button startBtn, endBtn;
	private int images[] = {R.drawable.icon1, R.drawable.icon2, R.drawable.icon3,
			R.drawable.icon4, R.drawable.icon5, R.drawable.icon6};
	Timer timer = new Timer(); 
	
	TimerTask task = new TimerTask(){    
        public void run() {  
            Message message = new Message();      
            message.what = signalLevel;      
            mhHandler.sendMessage(message);
            LogUtils.e("发送一次消息----------");
        }            
    };  
	
	private Handler mhHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			LogUtils.e("msg.what-----------"+msg.what);
			switch (msg.what) {
			case 1:
				signalImg.setBackgroundResource(R.drawable.icon1);
				break;
			case 2:
				signalImg.setBackgroundResource(R.drawable.icon2);
				break;
			case 3:
				signalImg.setBackgroundResource(R.drawable.icon3);
				break;
			case 4:
				signalImg.setBackgroundResource(R.drawable.icon4);
				break;
			case 5:
				signalImg.setBackgroundResource(R.drawable.icon5);
				break;
			case 6:
				signalImg.setBackgroundResource(R.drawable.icon6);
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
		setContentView(R.layout.activity_path_pattern);
		
		setTitle(R.string.sports_title);
		findViewById(R.id.back).setOnClickListener(this);
		init();
		timer.schedule(task, 5000); 
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mCurrentMode = LocationMode.NORMAL;
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
						mCurrentMode, true, null));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(16).build()));
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		initLocation();
		 
	}
	
	private void init(){
		signalImg = (ImageView)findViewById(R.id.signal_img);
		dateText = (TextView)findViewById(R.id.run_date_text);
		startTimeText = (TextView)findViewById(R.id.start_time);
		endTimeText = (TextView)findViewById(R.id.end_time);
		startBtn = (Button)findViewById(R.id.time_start);
		endBtn = (Button)findViewById(R.id.time_end);
		endBtn.setEnabled(false);
		
		long  currentDate = System.currentTimeMillis();
		CharSequence startTimeStr = DateFormat.format("MM-dd", currentDate);
		dateText.setText(startTimeStr);
		
		startBtn.setOnClickListener(this);
		endBtn.setOnClickListener(this);
	}
	
	 private void initLocation(){
	        LocationClientOption option = new LocationClientOption();
//	        option.setLocationMode(LocationMode.Hight_Accuracy);//设置高精度定位定位模式
	        option.setOpenGps(true);// 打开gps
	        option.setCoorType("bd09ll");//设置百度经纬度坐标系格式
	        option.setScanSpan(5000);//设置发起定位请求的间隔时间为1000ms
	        option.setIsNeedAddress(true);//反编译获得具体位置，只有网络定位才可以
	        mLocationClient.setLocOption(option);
			mLocationClient.start();
	    }
	 
		/**
		 * 定位SDK监听函数
		 */
		private class MyLocationListenner implements BDLocationListener {

			@Override
			public void onReceiveLocation(BDLocation location) {
				// map view 销毁后不在处理新接收的位置
				if (location == null || mMapView == null){
					LogUtils.e("location= null---------------");
					return;
				}
				MyLocationData locData = new MyLocationData.Builder()
						.accuracy(location.getRadius()).satellitesNum(location.getSatelliteNumber())
						// 此处设置开发者获取到的方向信息，顺时针0-360
						.direction(location.getDirection()).latitude(location.getLatitude())
						.longitude(location.getLongitude()).build();
				mBaiduMap.setMyLocationData(locData);
//				if (isFirstLoc) {
//					isFirstLoc = false;
					LatLng ll = new LatLng(location.getLatitude(),
							location.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
					mBaiduMap.animateMapStatus(u);
//				}
				LogUtils.e("location.getSatelliteNumber()---------"+location.getSatelliteNumber());
				if (location.getSatelliteNumber() > 0 && location.getSatelliteNumber() < 5) {
					signalLevel = 2;
				}else if(location.getSatelliteNumber() >= 5 && location.getSatelliteNumber() < 10){
					signalLevel = 3;
				}else if(location.getSatelliteNumber() >= 10 && location.getSatelliteNumber() < 15){
					signalLevel = 4;
				}else if(location.getSatelliteNumber() >= 15 && location.getSatelliteNumber() < 20){
					signalLevel = 5;
				}else if(location.getSatelliteNumber() >= 20 && location.getSatelliteNumber() < 25){
					signalLevel = 6;
				}
			}

			public void onReceivePoi(BDLocation poiLocation) {
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.time_start:
			startBtn.setEnabled(false);
			endBtn.setEnabled(true);
			long startTime = System.currentTimeMillis();
			CharSequence startTimeStr = DateFormat.format("yyyy-MM-dd hh:mm", startTime);
			startTimeText.setText(startTimeStr);
			break;
		case R.id.time_end:
			startBtn.setEnabled(true);
			endBtn.setEnabled(false);
			long endTime = System.currentTimeMillis();
			CharSequence endTimeStr = DateFormat.format("yyyy-MM-dd hh:mm", endTime);
			endTimeText.setText(endTimeStr);
			break;
		default:
			break;
		}
	}

}
