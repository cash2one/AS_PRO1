package com.linkage.mobile72.sh.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.data.DataChangedListener;
import com.linkage.mobile72.sh.data.StudentAtten;
import com.linkage.mobile72.sh.data.StudentAttenSum;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.CustomDialog;

public class ClassAttenSearchAdapter extends BaseAdapter
{
    private String TAG = ClassAttenAdapter.class.getSimpleName();
    
    private final int NUM_PER_LINE = 3;
    
    private Context mContext;
    
    private LayoutInflater mLayoutInflater;
    
    private List<StudentAtten> stuAttenList = new ArrayList<StudentAtten>();
    
    private StudentAttenSum attenSum = new StudentAttenSum();
    
    private DataChangedListener dataChangedListener = null;
    
    // 确认考勤用
    private long classId = 0;
    
    private String uploadImgUrl;
    
    private CustomDialog dialog;
    
    private boolean isConfirmed = false;
    
    public ClassAttenSearchAdapter(Context context, List<StudentAtten> list)
    {
        this.mContext = context;
        stuAttenList = list;
        
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }
    
    public ClassAttenSearchAdapter(Context context, List<StudentAtten> list,
            DataChangedListener listener, boolean confirmed)
    {
        this.mContext = context;
        stuAttenList = list;
        
        dataChangedListener = listener;
        
        this.mLayoutInflater = LayoutInflater.from(mContext);
        
        this.isConfirmed = confirmed;
        //LogUtils.i("ClassAttenSearchAdapter, isConfirmed:" + isConfirmed);
    }
    
    @Override
    public int getCount()
    {
        int count = stuAttenList.size() / NUM_PER_LINE;
        
        if (0 != stuAttenList.size() % NUM_PER_LINE)
        {
            count += 1;
        }
        
        return count;
    }
    
    @Override
    public Object getItem(int position)
    {
        return stuAttenList.get(position * NUM_PER_LINE);
    }
    
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;
        
        //        LogUtils.e(TAG, " getview position=" + position);
        
        if (null == convertView)
        {
            convertView = mLayoutInflater.inflate(R.layout.atten_search_item,
                    parent,
                    false);
            
            holder = new ViewHolder();
            
            holder.rlyStudent1 = (RelativeLayout) convertView.findViewById(R.id.rlyStudent1);
            holder.rlyStudent2 = (RelativeLayout) convertView.findViewById(R.id.rlyStudent2);
            holder.rlyStudent3 = (RelativeLayout) convertView.findViewById(R.id.rlyStudent3);
            
            holder.tvStudent1 = (TextView) convertView.findViewById(R.id.tvStudent1);
            holder.tvStudent2 = (TextView) convertView.findViewById(R.id.tvStudent2);
            holder.tvStudent3 = (TextView) convertView.findViewById(R.id.tvStudent3);
            
            holder.imgv1 = (ImageView) convertView.findViewById(R.id.imgv1);
            holder.imgv2 = (ImageView) convertView.findViewById(R.id.imgv2);
            holder.imgv3 = (ImageView) convertView.findViewById(R.id.imgv3);
            
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        
        StudentAtten stuAtten;
        
        int size = stuAttenList.size();
        
        int strtPos = position * NUM_PER_LINE;
        
        //        LogUtils.e(TAG, " strtPos=" + strtPos);
        
        for (int i = strtPos; i < strtPos + 3 && i < size; i++)
        {
            stuAtten = stuAttenList.get(i);
            //            LogUtils.e(TAG + " i=" + i + "  i % NUM_PER_LINE=" + i % NUM_PER_LINE
            //                    + " name=" + stuAtten.getName());
            
            switch (i % NUM_PER_LINE)
            {
                case 0:
                    holder.tvStudent1.setText(stuAtten.getName());
                    
                    if (isConfirmed) {
                    	
                    	holder.tvStudent1.setTextColor(mContext.getResources().getColor(R.color.at_mid_gray));
                    	holder.tvStudent1.setBackgroundResource(R.drawable.atten_common2);
                    	holder.rlyStudent1.setOnClickListener(null);
                    	
					} else {
	
						// holder.tvStudent1.setTextColor(R.drawable.atten_stu_text_selector);
						holder.tvStudent1
								.setBackgroundResource(R.drawable.atten_student_selector);
	
						holder.rlyStudent1
								.setOnClickListener(new StudentOnClickListener(i,
										holder.tvStudent1));
	
					}
                    
                    dspStateImg(holder.imgv1, stuAtten.getState());
                    
                    break;
                case 1:
                    holder.tvStudent2.setText(stuAtten.getName());
                    
					if (isConfirmed) {
	
						holder.tvStudent2.setTextColor(mContext.getResources()
								.getColor(R.color.at_mid_gray));
						holder.tvStudent2
								.setBackgroundResource(R.drawable.atten_common2);
						holder.rlyStudent2.setOnClickListener(null);
	
					} else {
	
						// holder.tvStudent2
						// .setTextColor(R.drawable.atten_stu_text_selector);
						holder.tvStudent2
								.setBackgroundResource(R.drawable.atten_student_selector);
	
						holder.rlyStudent2
								.setOnClickListener(new StudentOnClickListener(i,
										holder.tvStudent2));
	
					}
                    
                    dspStateImg(holder.imgv2, stuAtten.getState());
                    
                    break;
                
                case 2:
                    holder.tvStudent3.setText(stuAtten.getName());
                    
                    if (isConfirmed) {

    					holder.tvStudent3.setTextColor(mContext.getResources()
    							.getColor(R.color.at_mid_gray));
    					holder.tvStudent3
    							.setBackgroundResource(R.drawable.atten_common2);
    					holder.rlyStudent3.setOnClickListener(null);

					} else {
	
						// holder.tvStudent3
						// .setTextColor(R.drawable.atten_stu_text_selector);
						holder.tvStudent3
								.setBackgroundResource(R.drawable.atten_student_selector);
	
						holder.rlyStudent3
								.setOnClickListener(new StudentOnClickListener(i,
										holder.tvStudent3));
	
					}
                    
                    dspStateImg(holder.imgv3, stuAtten.getState());
                    
                    break;
            }
        }
        
        // last line
        if (position - 1 == size / NUM_PER_LINE)
        {
        	LogUtils.e("----->last line!!!");
        	
            int blank = NUM_PER_LINE - (size % NUM_PER_LINE);
            
            switch (blank)
            {
                case 0:
                    break;
                case 1:
                	holder.tvStudent3
					.setBackgroundResource(R.drawable.atten_common2);
			holder.rlyStudent3.setOnClickListener(null);
                    holder.tvStudent3.setText("");
                    holder.imgv3.setVisibility(View.GONE);
					
                    break;
                case 2:
                	holder.tvStudent2
					.setBackgroundResource(R.drawable.atten_common2);
			holder.rlyStudent2.setOnClickListener(null);
			
			holder.tvStudent3
					.setBackgroundResource(R.drawable.atten_common2);
			holder.rlyStudent3.setOnClickListener(null);
                    holder.tvStudent2.setText("");
                    holder.tvStudent3.setText("");
                    holder.imgv2.setVisibility(View.GONE);
                    holder.imgv3.setVisibility(View.GONE);
					
                    break;
                default:
                    break;
            }
        }
        
        return convertView;
    }
    
    private void dspStateImg(ImageView imgv, int state)
    {
        if (StudentAtten.ATTEN_ASK_FOR_LEAVE == state)
        {
            imgv.setVisibility(View.VISIBLE);
            imgv.setImageResource(R.drawable.atten_ask_leave);
        }
        else if (StudentAtten.ATTEN_LEAVE == state)
        {
            imgv.setVisibility(View.VISIBLE);
            imgv.setImageResource(R.drawable.atten_leave);
        }
        else if (StudentAtten.ATTEN_NORMAL == state)
        {
            imgv.setVisibility(View.VISIBLE);
            imgv.setImageResource(R.drawable.atten_normal);
        }
        else
        {
            imgv.setVisibility(View.GONE);
        }
    }
    
    /**
     * @return the stuAttenList
     */
    public List<StudentAtten> getStuAttenList()
    {
        return stuAttenList;
    }
    
    /**
     * @param stuAttenList the stuAttenList to set
     */
    public void setStuAttenList(List<StudentAtten> stuAttenList)
    {
        this.stuAttenList = stuAttenList;
    }
    
    /**
     * @return the attenSum
     */
    public StudentAttenSum getAttenSum()
    {
        return attenSum;
    }
    
    /**
     * @param attenSum the attenSum to set
     */
    public void setAttenSum(StudentAttenSum attenSum)
    {
        this.attenSum = attenSum;
    }
    
    /**
     * @return the classId
     */
    public long getClassId()
    {
        return classId;
    }
    
    /**
     * @param classId the classId to set
     */
    public void setClassId(long classId)
    {
        this.classId = classId;
    }
    
    /**
     * @return the uploadImgUrl
     */
    public String getUploadImgUrl()
    {
        return uploadImgUrl;
    }
    
    /**
     * @param uploadImgUrl the uploadImgUrl to set
     */
    public void setUploadImgUrl(String uploadImgUrl)
    {
        this.uploadImgUrl = uploadImgUrl;
    }
    
    private class ViewHolder
    {
        
        private RelativeLayout rlyStudent1, rlyStudent2, rlyStudent3;
        
        private TextView tvStudent1, tvStudent2, tvStudent3;
        
        private ImageView imgv1, imgv2, imgv3;
        
    }
    
    private class StudentOnClickListener implements OnClickListener
    {
        private int index;
        
        private TextView tvName;
        
        LinearLayout lyDlg;
        
        private Button btnAskLeave, btnLeave, btnPhone, btnCancel;
        
        public StudentOnClickListener(int clickIndex, TextView tv)
        {
            index = clickIndex;
            tvName = tv;
        }
        
        @Override
        public void onClick(View v)
        {
            //            tvName.setText(tvName.getText().toString() + index);
            
            dialog = new CustomDialog(mContext, true);
            dialog.setCustomView(R.layout.avatar_choose);
            
            Window window = dialog.getDialog().getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            
            lyDlg = (LinearLayout) dialog.findViewById(R.id.dialog_layout);
            lyDlg.setPadding(0, 0, 0, 0);
            
            btnAskLeave = (Button) dialog.findViewById(R.id.btnAskLeave);
            btnLeave = (Button) dialog.findViewById(R.id.btnLeave);
            btnPhone = (Button) dialog.findViewById(R.id.btnPhone);
            btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            
            int state = stuAttenList.get(index).getState();
            
            if (StudentAtten.ATTEN_ASK_FOR_LEAVE == state)
            {
                btnAskLeave.setText(R.string.cancel_ask_for_leave);
            }
            
            if (StudentAtten.ATTEN_LEAVE == state)
            {
                btnLeave.setText(R.string.cancel_no_atten);
            }
            
            btnAskLeave.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    //                    stuAttenList.get(index)
                    //                            .setState(StudentAtten.ATTEN_ASK_FOR_LEAVE);
                    
                    int state = stuAttenList.get(index).getState();
                    
                    switch (state)
                    {
                        case StudentAtten.ATTEN_NORMAL:
                            stuAttenList.get(index)
                                    .setState(StudentAtten.ATTEN_ASK_FOR_LEAVE);
                            //                            attenSum.setAskLeave(attenSum.getAskLeave() + 1);
                            break;
                        
                        case StudentAtten.ATTEN_ASK_FOR_LEAVE:
                            stuAttenList.get(index)
                                    .setState(StudentAtten.ATTEN_NORMAL);
                            //                            attenSum.setAskLeave(attenSum.getAskLeave() - 1);
                            break;
                        
                        case StudentAtten.ATTEN_LEAVE:
                            stuAttenList.get(index)
                                    .setState(StudentAtten.ATTEN_ASK_FOR_LEAVE);
                            //                            attenSum.setAskLeave(attenSum.getAskLeave() + 1);
                            //                            attenSum.setLeave(attenSum.getLeave() - 1);
                            break;
                        
                        default:
                            LogUtils.e(TAG + "invalid state, state=" + state);
                            
                    }
                    
                    dialog.dismiss();
                    notifyDataSetChanged();
                    
                    if (null != dataChangedListener)
                    {
                        dataChangedListener.onDataChanged(index);
                    }
                }
            });
            btnLeave.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    //                    stuAttenList.get(index).setState(StudentAtten.ATTEN_LEAVE);
                    
                    int state = stuAttenList.get(index).getState();
                    
                    switch (state)
                    {
                        case StudentAtten.ATTEN_NORMAL:
                            stuAttenList.get(index)
                                    .setState(StudentAtten.ATTEN_LEAVE);
                            //                            attenSum.setLeave(attenSum.getLeave() + 1);
                            break;
                        
                        case StudentAtten.ATTEN_ASK_FOR_LEAVE:
                            stuAttenList.get(index)
                                    .setState(StudentAtten.ATTEN_LEAVE);
                            //                            attenSum.setAskLeave(attenSum.getAskLeave() - 1);
                            //                            attenSum.setLeave(attenSum.getLeave() + 1);
                            break;
                        
                        case StudentAtten.ATTEN_LEAVE:
                            stuAttenList.get(index)
                                    .setState(StudentAtten.ATTEN_NORMAL);
                            //                            attenSum.setLeave(attenSum.getLeave() - 1);
                            break;
                        
                        default:
                            LogUtils.e(TAG + "invalid state, state=" + state);
                            
                    }
                    
                    dialog.dismiss();
                    notifyDataSetChanged();
                    
                    if (null != dataChangedListener)
                    {
                        dataChangedListener.onDataChanged(index);
                    }
                }
            });
            btnPhone.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"
                            + stuAttenList.get(index).getParentPhone()));
                    
                    dialog.dismiss();
                    
                    mContext.startActivity(intent);
                }
            });
            btnCancel.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    dialog.dismiss();
                }
            });
            
            dialog.setCancelable(true);
            dialog.show();
        }
        
    }
    
}
