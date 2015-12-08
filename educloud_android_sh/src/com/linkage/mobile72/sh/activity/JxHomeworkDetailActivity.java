package com.linkage.mobile72.sh.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION_CODES;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.AndroidAuthenticator;
import com.gauss.speex.recorder.SpeexPlayer;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.CreateHomeworkActivity.AudioAnimationHandler;
import com.linkage.mobile72.sh.adapter.AttachmentPicListAdapter;
import com.linkage.mobile72.sh.adapter.VoteListTeacherAdapter;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.JXBean;
import com.linkage.mobile72.sh.data.http.JXBeanDetail;
import com.linkage.mobile72.sh.data.http.JXBeanDetail.JXMessageAttachment;
import com.linkage.mobile72.sh.data.http.JXBeanDetail.JXVote;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.FileUtils;
import com.linkage.mobile72.sh.utils.HttpDownloader;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.widget.ListViewForScrollView;
import com.linkage.mobile72.sh.Consts;
/**
 * 教师的查看作业、通知、点评详情页面
 * @author Yao
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class JxHomeworkDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = JxHomeworkDetailActivity.class.getName();
    private JXBean bean; 
	private JXBeanDetail jxbean;//请求接口后返回的详情对象
    private Button back;
    private TextView receiverTextView, receiverTimeText, preSendTimeText;
    private EditText contentText;
    private TextView send_type_net_textview,send_type_email_textview,send_type_mob_textview;
    private RelativeLayout readStateLayout, replyStateLayout; LinearLayout state_reply_progress;
    private ProgressBar readProgress, replyProgress;
    private TextView send_to_names,readNumText, unReadNumText, replyNumText, unReplyNumText;
    private RelativeLayout voteLayout, imageLayout, voiceLayout;
    private ListViewForScrollView voteListView;
    private ArrayList<JXVote> votes;
    private VoteListTeacherAdapter mVoteAdapter;
    private ListViewForScrollView photoListView;
	private ArrayList<JXBeanDetail.JXMessageAttachment> photos;
    private ArrayList<String> choosePics;
    private JXBeanDetail.JXMessageAttachment voiceAttach;
	private AttachmentPicListAdapter mPicAdapter;
    private ScrollView scrollView;
    private TextView voiceTimeText;
    private ImageView voiceAnimImageView;
    private SpeexPlayer splayer = null;
    private String voicePathName = "";
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    // 记录语音动画图片
 	private int index = 1;
 	private AudioAnimationHandler audioAnimationHandler = null;
 	
 	private TextView state_text_read;
 	private TextView state_text_reply;
 	 private TextView sendOpenBtn;
 	 
 	private PopupWindow popWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
		if(intent == null) {
			finish();
			return;
		}
		bean = (JXBean)intent.getSerializableExtra("jxbean");
        setContentView(R.layout.activity_homework_detail);
        if(String.valueOf(Consts.JxhdType.COMMENT).equals(bean.getSmsMessageType())) {
			setTitle("点评详情");
		}else if(String.valueOf(Consts.JxhdType.HOMEWORK).equals(bean.getSmsMessageType())) {
			setTitle("作业详情");
		}else if(String.valueOf(Consts.JxhdType.NOTICE).equals(bean.getSmsMessageType())) {
			setTitle("通知详情");
		}else {
			setTitle("消息详情");
		}
        back = (Button)findViewById(R.id.back);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        receiverTextView = (TextView)findViewById(R.id.send_to);
        send_to_names = (TextView)findViewById(R.id.send_to_names);
        receiverTimeText = (TextView)findViewById(R.id.send_time);
        preSendTimeText = (TextView)findViewById(R.id.presend_time);
        contentText = (EditText)findViewById(R.id.edit_input);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			contentText.setTextIsSelectable(true);
			contentText.setKeyListener(null);
		}else{
			contentText.setKeyListener(null);
		}
        readStateLayout = (RelativeLayout)findViewById(R.id.layout_state_read);
        replyStateLayout = (RelativeLayout)findViewById(R.id.layout_state_reply);
        state_reply_progress = (LinearLayout)findViewById(R.id.state_reply_progress);
        readProgress = (ProgressBar) findViewById(R.id.progress_read);
        replyProgress = (ProgressBar) findViewById(R.id.progress_reply);
        readNumText = (TextView) findViewById(R.id.read_num);
        unReadNumText = (TextView) findViewById(R.id.unread_num);
        replyNumText = (TextView) findViewById(R.id.reply_num);
        unReplyNumText = (TextView) findViewById(R.id.unreply_num);
        state_text_read = (TextView) findViewById(R.id.state_text_read);
        state_text_reply = (TextView) findViewById(R.id.state_text_reply);
        send_type_net_textview  = (TextView) findViewById(R.id.send_type_net_textview);
        send_type_email_textview = (TextView) findViewById(R.id.send_type_email_textview);
        send_type_mob_textview  = (TextView) findViewById(R.id.send_type_mob_textview);
        
        photoListView = (ListViewForScrollView) findViewById(R.id.photo_list);
        voteLayout = (RelativeLayout)findViewById(R.id.layout_vote);
        voteListView = (ListViewForScrollView)findViewById(R.id.vote_list);
        voiceTimeText = (TextView)findViewById(R.id.voice_time);
        imageLayout = (RelativeLayout)findViewById(R.id.layout_photo);
        voiceLayout = (RelativeLayout)findViewById(R.id.layout_voice);
        voiceAnimImageView = (ImageView) findViewById(R.id.voice_anim_img);
        //voteText = (TextView) findViewById(R.id.vote_txt);
        
        sendOpenBtn = (TextView)findViewById(R.id.send_to_open);
        sendOpenBtn.setOnClickListener(this);
        back.setOnClickListener(this);
        readProgress.setOnClickListener(this);
        replyProgress.setOnClickListener(this);
        voiceLayout.setOnClickListener(this);
        readStateLayout.setOnClickListener(this);
        replyStateLayout.setOnClickListener(this);
        state_reply_progress.setOnClickListener(this);
        photos = new ArrayList<JXBeanDetail.JXMessageAttachment>();
        votes = new ArrayList<JXVote>();
        mPicAdapter = new AttachmentPicListAdapter(this, imageLoader, defaultOptionsPhoto, photos);
        mVoteAdapter = new VoteListTeacherAdapter(this, votes);
        photoListView.setAdapter(mPicAdapter);
        photoListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(JxHomeworkDetailActivity.this,
                        PictureReviewNetActivity.class);
                intent.putStringArrayListExtra(PictureReviewNetActivity.RES, choosePics);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        voteListView.setAdapter(mVoteAdapter);
        voteListView.setDivider(getResources().getDrawable(R.drawable.separate__line_xu));
        voteListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				JXVote vote = votes.get(position);
				Intent i = new Intent(JxHomeworkDetailActivity.this, VoteDetailActivity.class);
				i.putExtra(VoteDetailActivity.EXTRAS, vote);
				startActivity(i);
			}
		});
        
        getDetail();
    }

    @SuppressLint("SimpleDateFormat")
    private void getDetail() {
        ProgressDialogUtils.showProgressDialog("", this, true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getMessageDetailNew");
		params.put("id", ""+bean.getId());
		params.put("type", "2");
		params.put("smsMessageType", bean.getSmsMessageType());
		params.put("time", new SimpleDateFormat("yyyyMM").format(Utils.getDateFromDefString(bean.getRecvTime())));
		
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getMessageDetail,
				Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();
						if (response.optInt("ret") == 0) {
							jxbean = JXBeanDetail.parseFromJson(response.optJSONObject("data"), String.valueOf(Consts.JxhdType.NOTICE));
							fillDataToPage(jxbean);
						}else {
							if(StringUtils.isEmpty(response.optString("msg"))) {
								UIUtilities.showToast(JxHomeworkDetailActivity.this, "获取详情失败");
							}else {
								UIUtilities.showToast(JxHomeworkDetailActivity.this, response.optString("msg"));
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, JxHomeworkDetailActivity.this);
						finish();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		scrollView.smoothScrollTo(0, 0);
	}
	
	private void fillDataToPage(JXBeanDetail jxbean) {
		if(jxbean != null) {
		    initPopWindow();
		    send_to_names.setText(jxbean.getRecvUserName());
			receiverTimeText.setText("发送时间：" + jxbean.getSendTime());
            if(!TextUtils.isEmpty(jxbean.getPreSendTime()) && !TextUtils.isEmpty(jxbean.getPreSendTime().trim())) {
                preSendTimeText.setText("定时时间：" + jxbean.getPreSendTime());
                preSendTimeText.setVisibility(View.VISIBLE);
            }
			contentText.setText(jxbean.getMessageContent());
			send_type_net_textview.setText("" + jxbean.getWebNum());
	        send_type_email_textview.setText("" + jxbean.getSmsNum());
	        send_type_mob_textview.setText("" + jxbean.getPhoneNum());
			
			
//			readNumText.setText(""+jxbean.getReadNum());
//			unReadNumText.setText(""+jxbean.getUnReadNum());
//			replyNumText.setText(""+jxbean.getReplyNum());
//			unReplyNumText.setText(""+jxbean.getUnReplyNum());
			int readProgressInt = 50;
			if( jxbean.getReadNum() + jxbean.getUnReadNum() != 0)
			{
				readProgressInt= (int)((jxbean.getReadNum()*100 / (jxbean.getReadNum() + jxbean.getUnReadNum())));
			}
			readProgress.setProgress(readProgressInt);
			readProgress.setMax(100);
//			state_text_read.setText("");
			int all = jxbean.getReadNum() + jxbean.getUnReadNum();
//			state_text_read.setText("已读家长("+ jxbean.getReadNum() + "/"+all + ")");
		
			int replyProgressInt = 50;
			if( jxbean.getReplyNum() + jxbean.getUnReplyNum() != 0)
			{
				replyProgressInt =(int)((jxbean.getReplyNum()*100 / (jxbean.getReplyNum() + jxbean.getUnReplyNum())) );
			}
			replyProgress.setProgress(replyProgressInt);
			replyProgress.setMax(100);
			state_text_reply.setText("");
            all = jxbean.getReplyNum() + jxbean.getUnReplyNum();
            state_text_reply.setText("回复 ("+ jxbean.getReplyNum() + "/"+all + ")");
            ((TextView)findViewById(R.id.state_reply_has_reply_num)).setText("已回复 (" +jxbean.getReplyNum() + ")");
            ((TextView)findViewById(R.id.state_reply_has_unreply_num)).setText("未回复 (" +jxbean.getUnReplyNum() + ")");
            
			List<JXVote> votes = jxbean.getVoteJson();
			if(votes != null && votes.size() > 0) {
				voteLayout.setVisibility(View.VISIBLE);
				mVoteAdapter.addData(votes, false);
			}else {
				voteLayout.setVisibility(View.GONE);
			}
			List<JXBeanDetail.JXMessageAttachment> attachs = jxbean.getMsgAttachment();
			List<JXBeanDetail.JXMessageAttachment> imageAttachs = new ArrayList<JXBeanDetail.JXMessageAttachment>();
			if(attachs != null && attachs.size() > 0) {
                choosePics = new ArrayList<String>();
				for(JXMessageAttachment ma : attachs) {
					if(ma.getAttachmentType() == 0) {
                        choosePics.add(ma.getAttachmentUrl());
						imageAttachs.add(ma);
					}else {
						voiceAttach = ma;
					}
				}
			}
			if(null != voiceAttach) {
				final HttpDownloader download = new HttpDownloader();
				voicePathName = mApp.getWorkspaceVoice().getAbsolutePath() + FileUtils.getFileExtend(voiceAttach.getAttachmentUrl());
				new Thread(new Runnable() {
					@Override
					public void run() {
						download.downFile(voiceAttach.getAttachmentUrl(), mApp.getWorkspaceVoice().getAbsolutePath(), FileUtils.getFileExtend(voiceAttach.getAttachmentUrl()));
					}
				}).start();
			}
			if(imageAttachs != null && imageAttachs.size() > 0) {
				imageLayout.setVisibility(View.VISIBLE);
				mPicAdapter.addData(imageAttachs, false);
			}else {
				imageLayout.setVisibility(View.GONE);
			}
			if(voiceAttach != null) {
				voiceLayout.setVisibility(View.VISIBLE);
				int voiceTime = 0;
				if(!TextUtils.isEmpty(voiceAttach.getVoiceTime())){
					try {
						voiceTime = (int)Double.valueOf(voiceAttach.getVoiceTime()).intValue();
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				voiceTimeText.setText(voiceTime+"\"");
			}else {
				voiceLayout.setVisibility(View.GONE);
			}
		}
		scrollView.smoothScrollTo(0, 0);
	}
	
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_to_open:
            	if(jxbean == null || StringUtils.isEmpty(jxbean.getRecvUserName()))
                {
                    return;
                }
                if (popWindow.isShowing()) {  
                    popWindow.dismiss();  
                } else {  
                    popWindow.showAsDropDown(titleLayout, 0, 0);  
                }  
                break;
            case R.id.back:
                finish();
                break;
            case R.id.layout_state_read:
            	Intent n1 = new Intent(this, NotifyReplyActivity.class);
            	n1.putExtra("id", bean.getId());
            	n1.putExtra("type", 1);
            	n1.putExtra("time", bean.getRecvTime());
                startActivity(n1);
                break;
            case R.id.state_reply_progress:
            case R.id.layout_state_reply:
            	Intent n2 = new Intent(this, NotifyReadActivity.class);
            	n2.putExtra("id", bean.getId());
            	n2.putExtra("type", 2);
            	n2.putExtra("time", bean.getRecvTime());
                startActivity(n2);
                break;
            case R.id.layout_voice:
            	splayer = new SpeexPlayer(voicePathName);
				splayer.endPlay();
				splayer.startPlay();
				playAudioAnimation(voiceAnimImageView, 1);
				break;
//            case R.id.vote_txt:
//                startActivity(new Intent(this, VoteDetailActivity.class));
//                break;
        }
    }
    
    private void initPopWindow() {
        LayoutInflater inflater = LayoutInflater.from(JxHomeworkDetailActivity.this);
        // 引入窗口配置文件  
        View view = inflater.inflate(R.layout.popup_window_names, null);

        TextView names = (TextView)view.findViewById(R.id.send_to_names);
        TextView close = (TextView)view.findViewById(R.id.send_to_close);
        
        names.setText(jxbean.getRecvUserName());
        popWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, false);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setOutsideTouchable(true);  
        popWindow.setFocusable(true);
        popWindow.update();
        
        close.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                popWindow.dismiss();
            }
        });
    }
    
    /**
	 * 停止
	 */
	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}

	}
	
    private void playAudioAnimation(final ImageView imageView, final int inOrOut) {
		// 定时器检查播放状态
		stopTimer();
		mTimer = new Timer();
		// 将要关闭的语音图片归位
		if (audioAnimationHandler != null) {
			Message msg = new Message();
			msg.what = 3;
			audioAnimationHandler.sendMessage(msg);
		}

		audioAnimationHandler = new AudioAnimationHandler(imageView, inOrOut);
		mTimerTask = new TimerTask() {
			public boolean hasPlayed = false;

			@Override
			public void run() {
				if (splayer.isAlive()) {
					hasPlayed = true;
					index = (index + 1) % 3;
					Message msg = new Message();
					msg.what = index;
					audioAnimationHandler.sendMessage(msg);
				} else {
					// 当播放完时
					Message msg = new Message();
					msg.what = 3;
					audioAnimationHandler.sendMessage(msg);
					// 播放完毕时需要关闭Timer等
					if (hasPlayed) {
						stopTimer();
					}
				}
			}
		};
		// 调用频率为500毫秒一次
		mTimer.schedule(mTimerTask, 0, 500);
	}
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	BaseApplication.getInstance().cancelPendingRequests(TAG);
    	if(splayer != null && splayer.isAlive()) {
			splayer.endPlay();
		}
    }
}
