package com.linkage.mobile72.sh.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.data.http.ClassInfoBean;
import com.linkage.mobile72.sh.data.http.ClassMemberBean;
import com.linkage.mobile72.sh.Consts;

public class ClazzMyNickActivity extends BaseActivity implements OnClickListener {

	private ClassInfoBean clazz;
	private Button back;
	private RelativeLayout myInfoLayout,myNickLayout;
	private ImageView myAccoutAvatar;
	private TextView myAccountName, myAccountRole, myAccountPhone, myNickName;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_clazz_my_nick);
		setTitle(R.string.title_clazz_my_nick);
		clazz = (ClassInfoBean)getIntent().getExtras().getSerializable("CLAZZ");
		if(clazz == null) {
			finish();
		}
		List<ClassMemberBean> clazzMember = clazz.getMemberInfoList();
		ClassMemberBean memberInClazz = null;
		for(ClassMemberBean m : clazzMember) {
			if(m.getUserId().longValue() == getCurAccount().getUserId()) {
				memberInClazz = m;
				break;
			}
		}
		back = (Button)findViewById(R.id.back);
		myInfoLayout = (RelativeLayout)findViewById(R.id.my_info_layout);
		myNickLayout = (RelativeLayout)findViewById(R.id.my_nick_layout);
		myAccoutAvatar = (ImageView)findViewById(R.id.imageview_classavater);
		myAccountName = (TextView)findViewById(R.id.textview_my_account_name);
		myAccountRole = (TextView)findViewById(R.id.textview_my_account_role);
		myAccountPhone = (TextView)findViewById(R.id.textview_my_account_phone);
		myNickName = (TextView)findViewById(R.id.textview_my_nick);
		
		imageLoader.displayImage(Consts.SERVER_HOST + memberInClazz.getAvatar(), myAccoutAvatar);
		myAccountName.setText(memberInClazz.getNickName());
		if(memberInClazz.getUserRole() == null) {
			myAccountRole.setText("未知角色");
		}else {
			myAccountRole.setText(memberInClazz.getUserRole() == 1 ? "家长" : "教师");
		}
		myAccountPhone.setText(memberInClazz.getUserId()+"");
		myNickName.setText(memberInClazz.getNickName());
		
		back.setOnClickListener(this);
		myNickLayout.setOnClickListener(this);
		myInfoLayout.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.my_nick_layout:
			Intent intent = new Intent(this, ClazzMyNickEditActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("CLAZZ", clazz);
			intent.putExtras(bundle);
			startActivityForResult(intent, 1);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 1) { 
	        if (resultCode == RESULT_OK) {
	        	clazz = (ClassInfoBean)getIntent().getExtras().getSerializable("CLAZZ");
	    		if(clazz != null) {
	    			myNickName.setText(clazz.getMycard());
	    		}
	        }
		}
	}
	
}
