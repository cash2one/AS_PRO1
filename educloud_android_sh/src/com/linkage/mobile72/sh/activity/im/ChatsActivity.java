package com.linkage.mobile72.sh.activity.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.fragment.NewMessageFragment;


public class ChatsActivity extends BaseActivity {
	
	private static final String EXTRAS_NOTIFY = "extras_notify";
	
	public static Intent getNotifyIntent(Context context) {
		Intent intent = new Intent(context, ChatsActivity.class);
		intent.putExtra(EXTRAS_NOTIFY, "from_notify");
		return intent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty);
		
		if(savedInstanceState==null)
		{
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.container, getMessageFrgment(),NewMessageFragment.class.getName());
			ft.commit();
		}
	
	}
	
	private NewMessageFragment getMessageFrgment()
	{
		NewMessageFragment chatsFrgment = null;
		chatsFrgment = (NewMessageFragment)getSupportFragmentManager().findFragmentByTag(NewMessageFragment.class.getName());
		if(chatsFrgment==null)
		{
			chatsFrgment =  NewMessageFragment.create(R.string.tab_txt_message);
		}
	    return chatsFrgment;
	}
}
