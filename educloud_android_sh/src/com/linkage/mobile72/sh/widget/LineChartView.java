package com.linkage.mobile72.sh.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.LineChart;
import org.xclcharts.chart.LineData;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.GraphicalView;

import com.linkage.lib.util.LogUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LineChartView extends GraphicalView {
	private int colorTitalAxes = Color.rgb(222, 222, 222);

	private String TAG = "LineChart03View";

	private LineChart chart = new LineChart();

	private LineClickListener listener = null;

	private int lineChartWidth = -1;

	// 标签集合
	/*
	 * private LinkedList<String> labels = new LinkedList<String>();
	 * 
	 * private LinkedList<LineData> chartData = new LinkedList<LineData>();
	 */

	private List<String> labels = new ArrayList<String>();

	private List<LineData> chartData = new ArrayList<LineData>();

	private Paint mPaintTooltips = new Paint(Paint.ANTI_ALIAS_FLAG);

	public LineChartView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		chartLabels();
		chartDataSet();
		chartRender();
	}

	public LineChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		chartLabels();
		chartDataSet();
		chartRender();

	}

	public LineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		chartLabels();
		chartDataSet();
		chartRender();
	}

	@SuppressLint("NewApi")
	private void chartRender() {
		try {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			// setLayerType(View.LAYER_TYPE_NONE, null);

			// 设定数据源
			chart.setCategories(labels);
			chart.setDataSource(chartData);

			// 数据轴最大值
			chart.getDataAxis().setAxisMax(50);
			// 数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(20);
			chart.getDataAxis().hideAxisLabels();

			// chart.getDataAxis().setAxisLineVisible(false);
			chart.getDataAxis().hide();

			chart.getCategoryAxis().getTickLabelPaint()
					.setTextAlign(Align.LEFT);
			// chart.getCategoryAxis().setTickLabelRotateAngle(90);
			chart.getCategoryAxis().setTickLabelRotateAngle(0);
			// chart.getCategoryAxis().setAxisSteps(steps);

			// chart.getAxisTitle().setLowerTitle("(年份)");

			// chart.hideRightAxis();
			// chart.hideTopAxis();

			// chart.getPlotLegend().hide();

			// 轴颜色
			chart.getDataAxis().getAxisPaint().setColor(colorTitalAxes);
			chart.getCategoryAxis().getAxisPaint().setColor(colorTitalAxes);
			chart.getDataAxis().getTickMarksPaint().setColor(colorTitalAxes);
			chart.getCategoryAxis().getTickMarksPaint()
					.setColor(colorTitalAxes);

			// 忽略Java的float计算误差
			chart.disableHighPrecision();

			// 批注
			// List<AnchorDataPoint> mAnchorSet = new
			// ArrayList<AnchorDataPoint>();
			//
			// AnchorDataPoint an1 = new
			// AnchorDataPoint(2,0,XEnum.AnchorStyle.RECT);
			// an1.setAlpha(200);
			// an1.setBgColor(Color.RED);
			// an1.setAreaStyle(XEnum.DataAreaStyle.FILL);
			//
			// AnchorDataPoint an2 = new
			// AnchorDataPoint(1,1,XEnum.AnchorStyle.CIRCLE);
			// an2.setBgColor(Color.GRAY);
			//
			// AnchorDataPoint an3 = new
			// AnchorDataPoint(0,2,XEnum.AnchorStyle.RECT);
			// an3.setBgColor(Color.BLUE);
			//
			// mAnchorSet.add(an1);
			// mAnchorSet.add(an2);
			// mAnchorSet.add(an3);
			// chart.setAnchorDataPoint(mAnchorSet);

			chart.disablePanMode(); // 禁掉平移，这样线上的标注框在最左和最右时才能显示全
			// chart.setAnchorDataPoint(null);

			// chart.setXCoordFirstTickmarksBegin(true);

			// 激活点击监听
			chart.ActiveListenItemClick();
			chart.extPointClickRange(20);
			chart.showClikedFocus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtils.e(TAG + e.toString());
		}
	}

	private void chartDataSet() {
		LinkedList<Double> dataSeries3 = new LinkedList<Double>();
		dataSeries3.add(65d);
		dataSeries3.add(75d);
		dataSeries3.add(55d);
		dataSeries3.add(65d);
		dataSeries3.add(95d);
		dataSeries3.add(0d);
		dataSeries3.add(30d);
		// LineData lineData3 = new LineData("圆点",dataSeries3,Color.rgb(123, 89,
		// 168));
		LineData lineData3 = new LineData("", dataSeries3, Color.rgb(104, 204,
				250));
		lineData3.setDotRadius(10);
		lineData3.setDotStyle(XEnum.DotStyle.DOT);

		chartData.add(lineData3);

	}

	private void chartLabels() {
		labels.add("2009");
		labels.add("2010");
		labels.add("2011");
		labels.add("2012");
		labels.add("2013");
		labels.add("2014");
		labels.add("2015");
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 图所占范围大小
		chart.setChartRange(w, h);
	}

	/*
	 * 
	 * 1. 右边轴view 遮住右边view视图中最左边点或线的处理办法： xml FrameLayout 中 LineChart03View_left
	 * 要放到后面，放前面会盖住scrollview中的图 HLNScrollActivity 中的horiView.setPadding()可以注释掉
	 * 然后在 LineChart03View 中 通过设置chart.setpadding中的left来对整齐 或通过
	 * chart.setChartRange()中的x位置来偏移即可
	 * 
	 * 2. 如果觉得左滑范围太大，可以调整 HLNScrollActivity中 的horiView.setPadding() 也可以调整
	 * chart.setChartRange()中的x位置如 chart.setChartRange(60,0, ....)
	 */

	@Override
	public void render(Canvas canvas) {

		try {

			if (lineChartWidth > 0) {

				this.getLayoutParams().width = lineChartWidth;

			}

//			LogUtils.e("in render....w=" + this.getLayoutParams().width + " h="
//					+ this.getLayoutParams().height);

			chart.setChartRange(0, 0, this.getLayoutParams().width - 10,
					this.getLayoutParams().height - 10);

			// 设置绘图区内边距
			// chart.setPadding( 70,120, 100,180 );
			chart.setPadding(12, 12, 80, 40);

			chart.render(canvas);

		} catch (Exception e) {
			LogUtils.e(TAG + e.toString());
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

	}

	/*
	 * @Override protected void onMeasure(int widthMeasureSpec, int
	 * heightMeasureSpec) {
	 * 
	 * super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	 * 
	 * int width = DensityUtil.dip2px(getContext(), 500); int height =
	 * DensityUtil.dip2px(getContext(), 500);
	 * 
	 * setMeasuredDimension(width,height); }
	 */

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		if (event.getAction() == MotionEvent.ACTION_UP) {
			triggerClick(event.getX(), event.getY());
		}
		super.onTouchEvent(event);
		return true;
	}

	// 触发监听
	private void triggerClick(float x, float y) {

		LogUtils.d("triggerClick x=" + x + " y=" + y);

		PointPosition record = chart.getPositionRecord(x, y);
		if (null == record) {
			if (chart.getDyLineVisible())
				this.invalidate();
			return;
		}

		LogUtils.e("record.getDataChildID()=" + record.getDataChildID());

		LineData lData = chartData.get(record.getDataID());
		Double lValue = lData.getLinePoint().get(record.getDataChildID());

		float r = record.getRadius();
		// chart.showFocusPointF(record.getPosition(), r + r * 6.0f);
		/* chart.getFocusPaint().setStyle(Style.STROKE); */
		chart.getFocusPaint().setStyle(Style.FILL);
		// chart.getFocusPaint().setStrokeWidth(3);

		LogUtils.d("record.getDataID()=" + record.getDataID());

		chart.getFocusPaint().setColor(Color.RED);

		chart.showFocusPointF(record.getPosition(), r + r * 2.0f);
		this.invalidate();

		if (null != listener) {
			listener.OnClick(x, y, record.getDataChildID());
		}

	}

	public void setXAxisData(List<String> xlist) {
		if(xlist != null && xlist.size() > 0) {
			Collections.reverse(xlist);
		}
		labels = xlist;
		chart.setCategories(labels);
	}

	public void setChartData(List<Double> dataSeries) {
		List<Double> d = new ArrayList<Double>();
		if(dataSeries != null && dataSeries.size() > 0) {
			Collections.reverse(dataSeries);
			for(double dd : dataSeries) {
				d.add(dd/2);
			}
		}
		LineData lineData3 = new LineData("", d, Color.rgb(123, 89,
				168));
		lineData3.setDotRadius(9);
		lineData3.setDotStyle(XEnum.DotStyle.DOT);

		chartData.clear();
		chartData.add(lineData3);
	}

	public void notifyDataSetChanged() {
		
		this.invalidate();
	}

	public void updateLineChart(List<Double> dataSeries) {
		List<Double> d = new ArrayList<Double>();
		if(dataSeries != null && dataSeries.size() > 0) {
			Collections.reverse(dataSeries);
			for(double dd : dataSeries) {
				d.add(dd/2);
			}
		}
		LineData lineData3 = new LineData("", d, Color.rgb(104, 204,
				250));
		lineData3.setDotRadius(10);
		lineData3.setDotStyle(XEnum.DotStyle.DOT);

		chartData.clear();
		chartData.add(lineData3);

		chart.setDataSource(chartData);

		this.invalidate();
	}

	/**
	 * @return the listener
	 */
	public LineClickListener getListener() {
		return listener;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(LineClickListener listener) {
		this.listener = listener;
	}

	public int getLineChartWidth() {
		return lineChartWidth;
	}

	public void setLineChartWidth(int lineChartWidth) {
		this.lineChartWidth = lineChartWidth;
	}

}
