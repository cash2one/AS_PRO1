package com.linkage.mobile72.sh.activity.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.linkage.lib.util.LogUtils;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;

public class ChatContactActivity extends BaseActivity {
	public static String SHOW_TYPE_KEY = "show_type";
	public static int SHOW_TYPE_CONTACTS = 1;
	public static int SHOW_TYPE_GROUP = 2;
	public static int SHOW_TYPE_ALL = 3;

	public String sendmsg = "";
	public int show_type;

	public static void start(Context context, String msg) {
		Intent intent = new Intent(context, ChatContactActivity.class);
		intent.putExtra("sendmsg", msg);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty);
		sendmsg = getIntent().getStringExtra("sendmsg");
		show_type = getIntent().getIntExtra(SHOW_TYPE_KEY, 1);
		LogUtils.d("sendmsg" + sendmsg);
//		if (savedInstanceState == null) {
//			FragmentTransaction ft = getSupportFragmentManager()
//					.beginTransaction();
//			ft.replace(R.id.container, getChatContactTabFragment(),
//					ChatContactTabFragment.class.getName());
//			ft.commit();
//		}

	}

//	private ChatContactTabFragment getChatContactTabFragment() {
//		ChatContactTabFragment chatContactTabFragment = null;
//		chatContactTabFragment = (ChatContactTabFragment) getSupportFragmentManager()
//				.findFragmentByTag(ChatContactTabFragment.class.getName());
//		if (chatContactTabFragment == null) {
//			chatContactTabFragment = ChatContactTabFragment.newInstance("");
//		}
//		return chatContactTabFragment;
//	}

}
