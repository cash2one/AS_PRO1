package com.linkage.mobile72.sh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.video.MovieRecorderView;
import com.linkage.ui.widget.video.MovieRecorderView.OnRecordFinishListener;

public class PickVideoActivity extends BaseActivity {

	public static final String PICK_VIDEO_PATH = "pick_video_path";
	
	private String fileDir;
	
	private MovieRecorderView movieRecorderView;
	private TextView tvRec, cancel;
	private RelativeLayout endview;
	private ImageView imgArrow;

	private LinearLayout lyUpCancel;
	
	private String filePath;
	private int downY;
	private int startY;
	
	private boolean isCanceled = false;
	
	private OnRecordFinishListener stopRecListener = new OnRecordFinishListener()
	{

		@Override
		public void onRecordFinish(String path,int recordeTime) {
			filePath = path;
			Log.d("PickVideoActivity", "vide path:" + filePath+ " | recordeTime == "+recordeTime);
			if(recordeTime < 2)
			{
			    movieRecorderView.cancelRec();
			    T.showShort(PickVideoActivity.this, "时间过短，请重新拍摄");
			}
			else {
			    closeVideo(isCanceled);
            }
			
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pick_video);
		
		movieRecorderView = (MovieRecorderView) findViewById(R.id.movierecordview);
        cancel = (TextView)findViewById(R.id.tvCancel);
        tvRec = (TextView) findViewById(R.id.tvRec);
        lyUpCancel = (LinearLayout)findViewById(R.id.lyUpCancel);
        imgArrow = (ImageView)findViewById(R.id.imgArrow);
        endview= (RelativeLayout) findViewById(R.id.endview);
        
        fileDir = mApp.getWorkspaceVideo().toString() + "/";
        
        movieRecorderView.setVideoDir(fileDir);
        
        tvRec.setOnTouchListener(new OnTouchListener()
        {
            
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                	startY = (int) event.getRawY();
                    movieRecorderView.record(stopRecListener);
                    lyUpCancel.setVisibility(View.VISIBLE);
                }
                else if(event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    if ((startY - (int) event.getRawY()) > 90) 
                    {
                        LogUtils.i("ACTION_MOVEACTION_MOVEACTION_MOVE>90" );
                        
                        cancel.setText("松开取消");
                        imgArrow.setVisibility(View.INVISIBLE);
                    }
                    else {
                        LogUtils.i("ACTION_MOVEACTION_MOVEACTION_MOVE<90" );
                        imgArrow.setVisibility(View.VISIBLE);
                        cancel.setText("上移取消");
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                	LogUtils.i("startY=" + startY + " endY=" + (int) event.getRawY()
                    		+ " cha=" + (startY - (int) event.getRawY()));
                    if ((startY - (int) event.getRawY()) > 90) 
                    {
                    	LogUtils.i("up canceliing^^^^^^" );
                    	isCanceled = true;
                    	lyUpCancel.setVisibility(View.GONE);
                    	cancel.setText("上移取消");
                    	movieRecorderView.cancelRec();
                    }
                    else
                    {
                        isCanceled = false;
                        movieRecorderView.stopRec();
                    }
//                    movieRecorderView.stopRec();
                }
                return true;
            }
        });
        
//        endview.setOnTouchListener(new upTouchEvent());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		movieRecorderView.setOpenCamera(true);
	}
	
	
	
	@Override
	protected void onStop() {
		movieRecorderView.setOpenCamera(false);
		super.onStop();
	}

	private void closeVideo(boolean isCancel)
	{
		Intent mIntent = new Intent(PickVideoActivity.this,
				WriteTalkActivity.class);
		
//		if (isCancel) {
//			LogUtils.i("canceing url, rescode=" + RESULT_CANCELED);
//			setResult(RESULT_CANCELED, mIntent);
//		}
//		else {
			mIntent.putExtra(PICK_VIDEO_PATH, filePath);
			setResult(RESULT_OK, mIntent);
//		}
		
		finish();
	}
	
	
	class upTouchEvent implements OnTouchListener
    {
        
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            
            final int[] location = new int[2];
            int bound = (int) (location[1] + 100);
            
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    downY = (int) event.getRawY();
//                    Log.i("ACTION_DOWN", "down>>>>>>>>>>" + downY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dy = (int) event.getRawY() - downY;
                    if (dy < 0)
                    {
                        Log.i("ACTION_MOVE", "move-up>>>>>>>>>>" + dy);
                        break;
                    }
                    else
                    {
                        Log.i("ACTION_MOVE", "move>>>>>>>>>>" + dy);
                    }
                    Log.i("ACTION_MOVE", "move>>>>>>>>event.getRawY()" + event.getRawY());
                    break;
                case MotionEvent.ACTION_UP:
                    dy = downY - (int) event.getRawY();
                    Log.i("ACTION_MOVE", "dy>>>>>>>>" + dy);
                    Log.i("ACTION_MOVE", "bound" + bound + ",up>>>>>>>>>>"
                            + event.getRawY());
//                    Log.i("ACTION_MOVE", "up>>>>>>>>event.getRawY()" + event.getRawY());
                    if (dy > bound)
                    {
                    	closeVideo(true);
                    } else {
                    	
                    	Log.d("aa", "down ges");
                    }
                    
                    break;
            }
            return true;
        }
    }

}
