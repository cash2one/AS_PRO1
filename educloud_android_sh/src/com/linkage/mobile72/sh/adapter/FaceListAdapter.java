package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.utils.FaceUtils.Face;

public class FaceListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<Face> mFaces;
	
	public FaceListAdapter(Context context, List<Face> faces) {
		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.mFaces = faces;
	}
	
	@Override
	public int getCount() {
		if(mFaces != null) {
			return mFaces.size();
		}
		return 0;
	}

	@Override
	public Face getItem(int position) {
		if(mFaces != null) {
			return mFaces.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.chat_face_grid_item, parent, false);
		}
		
		Face face = getItem(position);
		
		ImageView faceImage = (ImageView) convertView.findViewById(R.id.face_image);
		faceImage.setImageResource(face.imageId);
		
		return convertView;
	}

}
