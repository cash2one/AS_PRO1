package com.linkage.mobile72.sh.view;

import java.text.NumberFormat;  
import java.util.ArrayList;  
import java.util.List;

import com.linkage.mobile72.sh.R;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;  
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.DashPathEffect;  
import android.graphics.Paint;  
import android.graphics.Paint.Style;  
import android.util.AttributeSet;
import android.view.Display;  
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;  
import android.view.WindowManager;  
import android.view.GestureDetector.OnGestureListener;
  
public class BarChartPanel extends  View{  
      
	/* 用户点击到了无效位置 */
	public static final int INVALID_POSITION = -1;
	
    private DataSeries series;  
    public final static int[] platterTable = new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN};  
    
    private GestureDetector mGestureDetector = null;
	private OnChartItemClickListener mOnChartItemClickListener = null;
    
    public BarChartPanel(Context context, AttributeSet attrs) {  
        super(context, attrs);
        setWillNotDraw(false);
        mGestureDetector = new GestureDetector(context, new RangeBarChartOnGestureListener() );
    }  
      
    public BarChartPanel(Context context) {  
        super(context);  
    }  
    public BarChartPanel(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		setWillNotDraw(false);
	} 
    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	// get default screen size from system service  
        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);  
        Display display = wm.getDefaultDisplay();  
        int width = display.getWidth();
        int hight = display.getHeight();
		// TODO Auto-generated method stub
		setMeasuredDimension(width, (int) (hight*0.34));
	}
    /**
    public BarChartPanel(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		setWillNotDraw(false);
	}

	public BarChartPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
	}  
    public BarChartPanel(Context context) {  
        super(context);
    }  
    */
      
    public void setSeries(DataSeries series) {  
        this.series = series;  
    }  
   
    @SuppressLint("ResourceAsColor")
	@Override    
    public void onDraw(Canvas canvas) {   
          
        // get default screen size from system service  
        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);  
        Display display = wm.getDefaultDisplay();  
        int width = display.getWidth();  
          
        // remove application title height  
        int height = (int) (display.getHeight()*0.34);   
          
        // draw background  
        Paint myPaint = new Paint();  
       
        // draw XY Axis  
        int xOffset = 0;  
        int yOffset = (int)(height * 0.2);  
        myPaint.setColor(R.color.white_gray);  
        myPaint.setStrokeWidth(2);
        myPaint.setTextSize(20);
        //canvas.drawLine(2+xOffset, height-2-yOffset, 2+xOffset, 2, myPaint);
//        canvas.drawLine(50, yOffset, width-50, yOffset, myPaint);
        canvas.drawLine(100, height-1-yOffset, width-100, height-1-yOffset, myPaint);  
          
        // draw text title  
        myPaint.setAntiAlias(true);  
        myPaint.setStyle(Style.FILL);  
          
        // draw data series now......  
        if(series == null) {  
            getMockUpSeries();  
        }  
        int xPadding = 10;  
        if(series != null) {  
        //每个柱所占的宽度
        int xUnit = (width-200)/6;  
            String[] seriesNames = series.getSeriesKeys();
            //绘制x轴坐标
            for(int i=0; i<seriesNames.length; i++) {  
//                canvas.drawText(seriesNames[i], xOffset + 20 + xPadding + xUnit*i, height-yOffset + 15, myPaint);  
            	canvas.drawText(seriesNames[i], 80+xUnit*i, height-yOffset + 30, myPaint);  
            }  
            
//             Y Axis markers  
            float min = 0, max = 10000;  
//            for(int i=0; i<seriesNames.length; i++) {  
//                DataElement dataElement = series.getItems(seriesNames[i]);  
//                
//                if(dataElement.getValue() > max) {  
//                    max = dataElement.getValue();  
//                }  
//                if(dataElement.getValue() < min) {  
//                    min = dataElement.getValue();  
//                }  
//            }  
            
            int yUnit = 10;   
            int unitValue = (height-100-yOffset)/yUnit;
//            int unitValue = height/yUnit;
            myPaint.setStrokeWidth(2);  
            myPaint.setColor(Color.BLACK);  
            float ymarkers = (max-min)/yUnit;  
            NumberFormat nf = NumberFormat.getInstance();  
            nf.setMinimumFractionDigits(2);  
            nf.setMaximumFractionDigits(2);  
            /**
            for(int i=0; i<10; i++) {  
                canvas.drawLine(2+xOffset, height-2-yOffset - (unitValue * (i+1)), 6+xOffset, height-2-yOffset - (unitValue * (i+1)), myPaint);  
            } 
            */
            // clear the path effect  
            myPaint.setColor(Color.BLACK);  
            myPaint.setStyle(Style.STROKE);  
            myPaint.setStrokeWidth(0);  
            myPaint.setPathEffect(null);   
            /**
            for(int i=0; i<10; i++) {  
                float markValue = ymarkers * (i+1);  
                canvas.drawText(nf.format(markValue), 2, height-2-yOffset - (unitValue * (i+1)), myPaint);  
            }  
              */
            // draw bar chart now  
            myPaint.setStyle(Style.FILL);  
            myPaint.setStrokeWidth(0);  
//            String maxItemsKey = null;  
//            int maxItem = 0;  
            Paint paint = new Paint();  
            paint.setColor(Color.BLACK);  
            paint.setStrokeWidth(2);
            paint.setTextSize(20);
            for(int i=0; i<seriesNames.length; i++) {  
            	DataElement dataElement = series.getItems(seriesNames[i]);  
                int barWidth = 10;  
//                int startPos = xOffset + 20 + xPadding + xUnit*i;  
                int startPos = 100+(xUnit - 10/(seriesNames.length-1))*i;
                int index = 0;  
                int interval = barWidth/2;  
                
                myPaint.setColor(dataElement.getColor());  
                int barHeight = (int)((dataElement.getValue()/ymarkers) * unitValue);  
                canvas.drawRect(startPos + barWidth*index + interval*index, height-2-yOffset-barHeight,   
                        startPos + barWidth*index + interval*index + barWidth, height-2-yOffset, myPaint);  
//                canvas.drawText(String.valueOf(dataElement.getValue())+"米", startPos + barWidth*index, height-8-yOffset-barHeight, paint);
                index++;                  
            }  
        }  
    }  
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		if( mGestureDetector != null ){
			return mGestureDetector.onTouchEvent(event);
		}
		return true;
	}

    /**
	 *	手势监听器
	 * @author A Shuai
	 *
	 */
	private class RangeBarChartOnGestureListener implements OnGestureListener{

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {  }

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			int position = identifyWhickItemClick(e.getX(), e.getY());
			if( position != INVALID_POSITION && mOnChartItemClickListener != null ){
				mOnChartItemClickListener.onItemClick(position);
			}
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {  }

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}
		
	}
	
	private int identifyWhickItemClick( float x, float y ){
		WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);  
        Display display = wm.getDefaultDisplay();  
        int width = display.getWidth();
		
		float leftx = 0;
		float rightx = 0;
		for( int i = 0; i < getMockUpSeries().getSeriesCount(); i++ ){
			leftx = 100+((width-200)/6)*i;
			rightx = 110 +((width-200)/6)*i;
			if( x < leftx ){
				break;
			}
			if( leftx <= x && x <= rightx ){
				return i;
			}
		}
		return INVALID_POSITION;
	}
	
	public OnChartItemClickListener getOnChartItemClickListener() {
		return mOnChartItemClickListener;
	}

	public void setOnChartItemClickListener(OnChartItemClickListener mOnChartItemClickListener) {
		this.mOnChartItemClickListener = mOnChartItemClickListener;
	}
      
    public DataSeries getMockUpSeries() {  
        
        return series;  
    }  
  
} 
