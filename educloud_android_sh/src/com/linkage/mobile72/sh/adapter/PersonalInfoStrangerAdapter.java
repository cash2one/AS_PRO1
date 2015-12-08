package com.linkage.mobile72.sh.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.AccountChild;
import com.linkage.mobile72.sh.widget.CircularImage;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalInfoStrangerAdapter extends BaseAdapter {
	
	private List<AccountChild> infos;
	private LayoutInflater inflater;
	private Context context;
	private ImageLoader imageLoader;

	public PersonalInfoStrangerAdapter(List<AccountChild> infos, Context context, ImageLoader imageLoader) {
		this.infos = infos;
		this.context = context;
		this.imageLoader = imageLoader;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public AccountChild getItem(int position) {
		LogUtils.e(position + "   getItem");
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LogUtils.e(position + "   getView");
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.personal_stranger_list_item,
					parent, false);
			holder = new ViewHolder();
			holder.nameText = (TextView) convertView
					.findViewById(R.id.user_name);
			holder.nameText.setText(infos.get(position).getName());
			holder.idText = (TextView) convertView
					.findViewById(R.id.user_id);
			
//			holder.schoolText = (TextView) convertView
//					.findViewById(R.id.personal_info_school_text);
//			holder.schoolText.setText(infos.get(position).getSchoolName());
//			holder.schoolTypeText = (TextView) convertView
//					.findViewById(R.id.personal_info_schooltype_text);
//			holder.schoolTypeText.setText(infos.get(position).getSchoolType());
//			holder.gradeText = (TextView) convertView
//					.findViewById(R.id.personal_info_grade_text);
//			holder.gradeText.setText(infos.get(position).getGrade());
			holder.image = (CircularImage) convertView
					.findViewById(R.id.user_avater);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AccountChild child = getItem(position);
		if(child != null) {
			holder.idText.setText(child.getId()+"");
			imageLoader.displayImage(Consts.SERVER_HOST + child.getPicture(), holder.image);
		}
		return convertView;
	}

	class ViewHolder {
		private TextView nameText, idText, schoolText, schoolTypeText, gradeText;
		private CircularImage image;
	}

}
