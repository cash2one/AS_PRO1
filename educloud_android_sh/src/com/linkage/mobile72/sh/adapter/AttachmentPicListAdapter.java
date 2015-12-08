package com.linkage.mobile72.sh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.http.JXBeanDetail;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class AttachmentPicListAdapter extends BaseAdapter {

    private Context context;
    private List<JXBeanDetail.JXMessageAttachment> data;
    private ImageLoader imageLoader;
    DisplayImageOptions defaultOptionsPhoto;

	public AttachmentPicListAdapter(Context context, ImageLoader imageLoader, DisplayImageOptions defaultOptionsPhoto, List<JXBeanDetail.JXMessageAttachment> data) {
		this.context = context;
		this.data = data;
		this.imageLoader = imageLoader;
        this.defaultOptionsPhoto = defaultOptionsPhoto;

	}

	public void addData(List<JXBeanDetail.JXMessageAttachment> list, boolean append) {
        if(list != null && list.size() > 0) {
            if(!append)
                data.clear();
            data.addAll(list);
        }
        notifyDataSetChanged();
    }
	
	@Override
	public int getCount() {
        return data.size();
	}

	@Override
	public JXBeanDetail.JXMessageAttachment getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_attachment_pic_list, parent, false);
            holder = new Holder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        JXBeanDetail.JXMessageAttachment ma = getItem(position);
        if(ma != null) {
        	imageLoader.displayImage(ma.getAttachmentUrl(), holder.imageView, defaultOptionsPhoto);
        }
		return convertView;
	}
	
	class Holder{
		ImageView imageView;
	}

}
