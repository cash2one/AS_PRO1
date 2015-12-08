/**
 * Copyright 2014  XCL-Charts
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 	
 * @Project XCL-Charts 
 * @Description Android图表基类�? * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @Copyright Copyright (c) 2014 XCL-Charts (www.xclcharts.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */
package com.linkage.mobile72.sh.widget;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.DountChart;
import org.xclcharts.chart.PieData;
import org.xclcharts.event.click.ArcPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.info.PlotArcLabelInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.linkage.mobile72.sh.R;
import com.linkage.lib.util.LogUtils;

/**
 * @ClassName DountChart01View
 * @Description  ����ͼ����
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 */
public class DountChart01View extends DemoView
{
    
    private String TAG = "DountChart01View";
    
    private DountChart chart = new DountChart();
    
    LinkedList<PieData> lPieData = new LinkedList<PieData>();
    
    Context mContext;
    
    public DountChart01View(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
        initView();
    }
    
    public DountChart01View(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        
        mContext = context;
        
        initView();
    }
    
    public DountChart01View(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mContext = context;
        
        initView();
    }
    
    private void initView()
    {
        chartDataSet();
        chartRender();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        //图所占范围大小
        chart.setChartRange(w, h);
    }
    
    private void chartRender()
    {
        try
        {
            //设置绘图区默认缩进px值
            //            int [] ltrb = getPieDefaultSpadding();
            //            chart.setPadding(ltrb[0], ltrb[1] + 10, ltrb[2], ltrb[3]);
            chart.setPadding(20, 20, 20, 20);
            
            //数据源
            chart.setDataSource(lPieData);
            
            //标签显示(隐藏，显示在中间，显示在扇区外面) 
            chart.setLabelStyle(XEnum.SliceLabelStyle.HIDE);
            
            chart.setBorderWidth(0);
            
            chart.setApplyBackgroundColor(false);
            
            //内环背景色
            chart.getInnerPaint().setColor(Color.rgb(255, 255, 255));
            
            //显示边框线，并设置其颜色
            chart.getArcBorderPaint().setColor(Color.YELLOW);
            
            chart.setInitialAngle(300.0f);
            
            chart.setInnerRadius(0.8f);
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            LogUtils.e(TAG + ":" + e.toString());
        }
    }
    
    private void chartDataSet()
    {
        lPieData.clear();
        
        //        Log.d(TAG, "lPieData size:" + lPieData.size());
        //设置图表数据源               
        //PieData(标签，百分比，在饼图中对应的颜色)
        lPieData.add(new PieData("", "", 70, Color.rgb(126, 210, 21)));
        lPieData.add(new PieData("", "", 20, Color.rgb(236, 105, 65)));
        lPieData.add(new PieData("", "", 10, Color.rgb(247, 181, 81)));
        
        //        Log.d(TAG, "lPieData size2:" + lPieData.size());
    }
    
    @Override
    public void render(Canvas canvas)
    {
        try
        {
            chart.render(canvas);
            
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.pieaa);
            
            ArrayList<PlotArcLabelInfo> mLstLabels = chart.getLabelsPosition();
            for (PlotArcLabelInfo info : mLstLabels)
            {
                PointF pos = info.getLabelPointF();
                if (null == pos)
                    continue;
                //String posXY = " x="+Float.toString(pos.x)+" y="+Float.toString(pos.y);
                //LogUtils.e("Pie","label="+lPieData.get(info.getID())+" "+posXY);      
                
                canvas.drawBitmap(bmp, pos.x, pos.y, null);
            }
            
        }
        catch (Exception e)
        {
            LogUtils.e(TAG + ":" + e.toString());
        }
    }
    
    @Override
    public List<XChart> bindChart()
    {
        // TODO Auto-generated method stub      
        List<XChart> lst = new ArrayList<XChart>();
        lst.add(chart);
        return lst;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO Auto-generated method stub      
        //        super.onTouchEvent(event);      
        //        if(event.getAction() == MotionEvent.ACTION_UP) 
        //        {                       
        ////          triggerClick(event.getX(),event.getY());
        //        }
        return true;
    }
    
    //触发监听
    private void triggerClick(float x, float y)
    {
        if (!chart.getListenItemClickStatus())
            return;
        ArcPosition record = chart.getPositionRecord(x, y);
        if (null == record)
            return;
        
        PieData pData = lPieData.get(record.getDataID());
        
        boolean isInvaldate = true;
        for (int i = 0; i < lPieData.size(); i++)
        {
            PieData cData = lPieData.get(i);
            if (i == record.getDataID())
            {
                if (cData.getSelected())
                {
                    isInvaldate = false;
                    break;
                }
                else
                {
                    cData.setSelected(true);
                }
            }
            else
                cData.setSelected(false);
        }
        
        if (isInvaldate)
            this.invalidate();
    }
    
    /**
     * @return the lPieData
     */
    public LinkedList<PieData> getlPieData()
    {
        return lPieData;
    }
    
    /**
     * @param lPieData the lPieData to set
     */
    public void setlPieData(LinkedList<PieData> lPieData)
    {
        this.lPieData = lPieData;
    }
    
    public void updateDount(LinkedList<PieData> lPieData)
    {
        this.lPieData = lPieData;
        chart.setDataSource(lPieData);
        
        this.invalidate();
    }
    
}
