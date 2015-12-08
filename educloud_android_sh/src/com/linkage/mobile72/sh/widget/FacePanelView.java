package com.linkage.mobile72.sh.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.adapter.FaceListAdapter;
import com.linkage.mobile72.sh.utils.FaceUtils.Face;

public class FacePanelView extends LinearLayout implements OnItemClickListener {
	
	public static interface OnFaceClickListener {
		void onFaceClick(Face face);
	}

	private GridView mGrid;
	private OnFaceClickListener mListener;
	
	public FacePanelView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mGrid = (GridView) findViewById(R.id.face_grid);
		mGrid.setOnItemClickListener(this);
	}

	public void setAdapter(FaceListAdapter adapter) {
		mGrid.setAdapter(adapter);
	}
	
	public void setOnFaceClickListener(OnFaceClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Face face = (Face) parent.getAdapter().getItem(position);
		if(mListener != null) {
			mListener.onFaceClick(face);
		}
	}
}
