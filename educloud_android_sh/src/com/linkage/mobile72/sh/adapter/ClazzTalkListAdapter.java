package com.linkage.mobile72.sh.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.ImgDisplayActivity;
import com.linkage.mobile72.sh.activity.PersonalInfoActivity;
import com.linkage.mobile72.sh.activity.TopicDetailActivity;
import com.linkage.mobile72.sh.activity.WebViewHtHdActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountChild;
import com.linkage.mobile72.sh.data.http.ClazzTalk;
import com.linkage.mobile72.sh.data.http.ClazzTalkReply;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.im.bean.ClazzImage;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.RelativeDateFormat;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.widget.CustomImageView;
import com.linkage.mobile72.sh.widget.NineGridlayout;
import com.linkage.mobile72.sh.widget.ScreenTools;
import com.linkage.mobile72.sh.Consts;
import com.linkage.ui.widget.CustomDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;


@SuppressLint("ClickableViewAccessibility")
public class ClazzTalkListAdapter extends BaseAdapter {

	private final static String TAG = "ClazzTalkListAdapter";
	
	public final static int TYPE_CLAZZ = 0;
	public final static int TYPE_PERSONAL = 1;
	private final static int ISPRAISE = 1;
	private final static int NOTPRAISE = 0;
	private final static int MAXREPLYNUM = 100;
	private final static int CLICKFROMUSER = 1;
	private final static int CLICKTOUSER = 2;
	
	private Context context;
	private ImageLoader imageLoader;
	private DisplayImageOptions options, clazzImageOption;
	private List<ClazzTalk> data;
	private PopupWindow popupWindow;
    private View popupWindowView;
    private View parentView;
    private AccountChild account;
    private CustomDialog deleteReplyDialog;
    private int listType;
    
	public ClazzTalkListAdapter (Context context, ImageLoader imageLoader, DisplayImageOptions options, List<ClazzTalk> data, AccountChild account, View parentView, int type) {
		this.context = context;
		this.imageLoader = imageLoader;
		this.options = options;
		this.data = data;
		this.account = account;
		this.parentView = parentView;
		listType = type;
		clazzImageOption = new DisplayImageOptions.Builder().cacheOnDisc()
				.showStubImage(R.drawable.appdetail_icon_def)
				.showImageForEmptyUri(R.drawable.appdetail_icon_def)
				.showImageOnFail(R.drawable.appdetail_icon_def).resetViewBeforeLoading() // default
																				// 设置图片在加载前是否重置、复位
				.delayBeforeLoading(500) // 下载前的延迟时间
				.cacheInMemory() // default 设置下载的图片是否缓存在内存中
				.cacheOnDisc() // default 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
																		// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.ARGB_4444) // default 设置图片的解码类型
				.displayer(new SimpleBitmapDisplayer()) // default 还可以设置圆角图片new
														// RoundedBitmapDisplayer(20)
				.handler(new Handler()) // default
				.build();
	}
	
	public void addAll(List<ClazzTalk> list, boolean append){
		if(append) {
			if(this.data != null)
				this.data.addAll(list);
			else
				this.data = list;
		}
		this.data = list;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public ClazzTalk getItem(int position) {
		return data == null ? null : data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return data == null ? 0 : data.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_clazz_talk, parent, false);
			viewHolder.senderAvatar = (ImageView) convertView.findViewById(R.id.talk_sender_avatar);
			viewHolder.senderName = (TextView) convertView.findViewById(R.id.talk_sender_name);
			viewHolder.senderTime = (TextView) convertView.findViewById(R.id.talk_sender_time);
			viewHolder.contentLayout = (RelativeLayout) convertView.findViewById(R.id.talk_content_layout);
			viewHolder.talkContent = (TextView) convertView.findViewById(R.id.talk_sender_content);
			viewHolder.shareLayout = (RelativeLayout) convertView.findViewById(R.id.talk_share_layout);
			viewHolder.senderShareImage = (ImageView) convertView.findViewById(R.id.talk_sender_share_image);
			viewHolder.senderShareContent = (TextView) convertView.findViewById(R.id.talk_sender_share_text);
			viewHolder.talkZanbtn = (LinearLayout) convertView.findViewById(R.id.talk_zan_btn);
			viewHolder.talkReplybtn = (LinearLayout) convertView.findViewById(R.id.talk_reply_btn);
			viewHolder.talkZanNum = (TextView) convertView.findViewById(R.id.clazz_talk_zan_num);
			viewHolder.talkReplyNum = (TextView) convertView.findViewById(R.id.clazz_talk_reply_num);
			viewHolder.attachLayout = (LinearLayout) convertView.findViewById(R.id.talk_attach_layout);
//			viewHolder.attachGridView = (NoScorllGridView) convertView.findViewById(R.id.talk_attach_list);
			viewHolder.attachView = (NineGridlayout) convertView.findViewById(R.id.iv_ngrid_layout);
			viewHolder.iv = (CustomImageView) convertView.findViewById(R.id.iv_oneimage);
			viewHolder.replyLayout = (LinearLayout) convertView.findViewById(R.id.talk_reply_layout);
//			viewHolder.replyListView = (InnerListView) convertView.findViewById(R.id.talk_reply_list);
			viewHolder.detailLinear = (LinearLayout) convertView.findViewById(R.id.detail_linear);
			viewHolder.icVideo = convertView.findViewById(R.id.ic_video);
			viewHolder.shareNoticeText = (TextView) convertView.findViewById(R.id.share_notice_text);
			viewHolder.deleteTalk = (TextView) convertView.findViewById(R.id.talk_delete);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
//		viewHolder.detailLinear.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent in = new Intent(context, ClazzTalkDetailActivity.class);
//				in.putExtra("talkId", data.get(position).getId());
//				context.startActivity(in);
//			}
//		});
		
		final ClazzTalk talk = getItem(position);
		imageLoader.displayImage(talk.getCreaterUrl(), viewHolder.senderAvatar);
		viewHolder.senderName.setText(talk.getCreaterName());
		viewHolder.senderAvatar.setOnClickListener(new creatorNameOnclick(position));
		viewHolder.senderName.setOnClickListener(new creatorNameOnclick(position));
		//几分钟 几小时 几天前 处理
		viewHolder.senderTime.setText(RelativeDateFormat.format(talk.getCreateDate()));
		
		if(talk.getContent() == null || talk.getContent().equals("null")){
			viewHolder.talkContent.setVisibility(View.GONE);
		} else {
			viewHolder.talkContent.setVisibility(View.VISIBLE);
			viewHolder.talkContent.setText(talk.getContent());
		}
		if(talk.getOpt_type() == 0) {//原创
			viewHolder.shareLayout.setVisibility(View.GONE);
			viewHolder.shareNoticeText.setVisibility(View.GONE);
		}else if(talk.getOpt_type() == 1) {//分享
			viewHolder.shareNoticeText.setVisibility(View.VISIBLE);
			viewHolder.shareLayout.setVisibility(View.VISIBLE);
			imageLoader.displayImage(talk.getShare_pic(), viewHolder.senderShareImage, clazzImageOption);
			viewHolder.senderShareContent.setText(talk.getShare_title());
			viewHolder.shareLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					Intent mIntent = new Intent(context, WebViewActivity.class);
//					mIntent.putExtra(WebViewActivity.KEY_URL, talk.getShare_url());
//					mIntent.putExtra(WebViewActivity.KEY_TITLE, "分享详情");
//	                context.startActivity(mIntent);
					if(talk.getShare_type() == 1) {
						Intent mIntent = new Intent(context, TopicDetailActivity.class);
						mIntent.putExtra(TopicDetailActivity.KEY_URL, talk.getShare_url());
						mIntent.putExtra(TopicDetailActivity.KEY_TITLE, "正文");
						mIntent.putExtra(TopicDetailActivity.COMMENT_URL, talk.getShare_comment_url());
						mIntent.putExtra(TopicDetailActivity.KEY_ID, String.valueOf(talk.getShare_id()));
	                	context.startActivity(mIntent);
					}else if(talk.getShare_type() == 2) {
						Intent mIntent = new Intent(context, WebViewHtHdActivity.class);
		                mIntent.putExtra(WebViewHtHdActivity.KEY_URL, talk.getShare_url());
		                mIntent.putExtra(WebViewHtHdActivity.KEY_TITLE, talk.getShare_title());
		                mIntent.putExtra(WebViewHtHdActivity.KEY_TOKEN, BaseApplication.getInstance().getAccessToken());
		                context.startActivity(mIntent);
					}
				}
			});
		}
		
		//附件
		if(talk.getPicImages() != null && talk.getPicImages().size() > 0) {
			
			viewHolder.attachLayout.setVisibility(View.VISIBLE);
			/*TalkImageAdapter talkImageAdapter = new TalkImageAdapter(context, attachList, imageLoader, options);
			if(attachList.size() == 1){
				viewHolder.attachGridView.setNumColumns(1);
			}else{
				viewHolder.attachGridView.setNumColumns(3);
			}
			viewHolder.attachGridView.setAdapter(talkImageAdapter);*/
			if (talk.getPicImages().isEmpty()) {
	            viewHolder.attachView.setVisibility(View.GONE);
	            viewHolder.iv.setVisibility(View.GONE);
	            viewHolder.icVideo.setVisibility(View.GONE);
	        } else if (talk.getPicImages().size() == 1) {
	            viewHolder.attachView.setVisibility(View.GONE);
	            viewHolder.iv.setVisibility(View.VISIBLE);
	            handlerOneImage(viewHolder, talk);
	        } else {
	            viewHolder.attachView.setVisibility(View.VISIBLE);
	            viewHolder.iv.setVisibility(View.GONE);
	            viewHolder.icVideo.setVisibility(View.GONE);
	            viewHolder.attachView.setImagesData(talk);
	        }
		}else {
			viewHolder.attachLayout.setVisibility(View.GONE);
		}
		
		//赞、评论数
		if (talk.getIsPraise() == NOTPRAISE) {
			Drawable drawable = context.getResources().getDrawable(
					R.drawable.clazz_talk_list_zan_normal);
			viewHolder.talkZanNum.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null,
					null);
			viewHolder.talkZanNum.setText("赞 (" + talk.getPraiseNum() + ")");
			viewHolder.talkZanbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sendZanFromNet(position, 1);// 1为点赞
				}
			});
		} else {
			Drawable drawable = context.getResources().getDrawable(
					R.drawable.clazz_talk_list_zan_click);
			viewHolder.talkZanNum.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null,
					null);
			viewHolder.talkZanNum.setText("赞 (" + talk.getPraiseNum() + ")");
			viewHolder.talkZanbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sendZanFromNet(position, 2);// 2为取消赞
				}
			});
		}
		//回复
		viewHolder.talkReplyNum.setText("评论 (" + talk.getReplyNum() + ")");
		viewHolder.talkReplybtn.setOnClickListener(new ReplyOnclick(position, -1));
		
		//评论列表
		viewHolder.replyLayout.removeAllViews();
		final List<ClazzTalkReply> replyList = talk.getReplyList();
		if(replyList != null && replyList.size() > 0) {
			viewHolder.replyLayout.setVisibility(View.VISIBLE);
			for(int i = 0;i < (replyList.size() < 100 ? replyList.size() : 100);i++){
				final View replyItem = View.inflate(context, R.layout.item_talk_reply, null);
				
				TextView content = (TextView) replyItem.findViewById(R.id.reply_content);
				content.setOnClickListener(new ReplyOnclick(position, i));
				content.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						return true;
					}
				});
				String fromName = replyList.get(i).getFromName();
		        SpannableString spanFromName = new SpannableString(fromName);
		        ClickableSpan clickFromName = new ReplyNameOnclick(position, i, CLICKFROMUSER);
		        spanFromName.setSpan(clickFromName, 0, fromName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		        content.setText(spanFromName);
		        
		        content.append("回复");
		        
		        if(replyList.get(i).getToName() != null){
		        	String toName = replyList.get(i).getToName();
		        	SpannableString spanToName = new SpannableString(toName);
		        	ClickableSpan clickToName = new ReplyNameOnclick(position, i, CLICKTOUSER);
		        	spanToName.setSpan(clickToName, 0, toName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		        	content.append(spanToName);
		        }
		        
		        content.append(":" + replyList.get(i).getContent());
		        content.setMovementMethod(LinkMovementMethod.getInstance());
//				content.setText(replyList.get(i).getContent());
				viewHolder.replyLayout.addView(replyItem, i);
			}
		}else {
			viewHolder.replyLayout.setVisibility(View.GONE);
		}
		
		if(BaseApplication.getInstance().getDefaultAccount().getUserId() != data.get(position).getCreaterId()){
			viewHolder.deleteTalk.setVisibility(View.GONE);
		} else {
			//删除说说
			viewHolder.deleteTalk.setVisibility(View.VISIBLE);
			viewHolder.deleteTalk.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					deleteReplyDialog = new CustomDialog(context, true);
					deleteReplyDialog.setCustomView(R.layout.dlg_delete_item);
					Window window = deleteReplyDialog.getDialog().getWindow();
					window.setGravity(Gravity.BOTTOM);
					window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					Button delete = (Button) deleteReplyDialog.findViewById(R.id.btnDelete);
					Button cancel = (Button) deleteReplyDialog.findViewById(R.id.btnCancel);
					LinearLayout lyDlg;
					lyDlg = (LinearLayout) deleteReplyDialog.findViewById(R.id.dialog_layout);
					lyDlg.setPadding(0, 0, 0, 0);
					delete.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							deleteTalkFromNet(position);
							deleteReplyDialog.dismiss();
						}
					});
					cancel.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							deleteReplyDialog.dismiss();
						}
					});
					deleteReplyDialog.setCancelable(true);
					deleteReplyDialog.show();
				}
			});
		}
		
		return convertView;
	}
	
	class creatorNameOnclick implements OnClickListener{
		
		long creatorId;
		public creatorNameOnclick(int talkIndex) {
			creatorId = data.get(talkIndex).getCreaterId();
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, PersonalInfoActivity.class);
			intent.putExtra("id", creatorId);
			context.startActivity(intent);
		}
	}
	
	class ReplyNameOnclick extends ClickableSpan{

		private ClazzTalkReply talkReply;
		private int type; //点击的用户类型 fromid == 1 || toid == 2
		
		public ReplyNameOnclick(int talkIndex, int replyIndex, int type) {
			this.type = type;
			if(replyIndex >= 0){
				this.talkReply = data.get(talkIndex).getReplyList().get(replyIndex);
			}
		}
	
		@Override
	    public void updateDrawState(TextPaint ds) {
	        ds.setColor(0xff52bde7);
	    }
		
		@Override
		public void onClick(View v) {
			long getid = ((type == CLICKFROMUSER) ? talkReply.getFromId() : talkReply.getToId());
			if(getid != BaseApplication.getInstance().getDefaultAccount().getUserId()){
				Intent intent = new Intent(context, PersonalInfoActivity.class);
				intent.putExtra("id", getid);
				context.startActivity(intent);
			}
		}
		
	}
	
	//初始化回复的popupwindows
	class ReplyOnclick implements OnClickListener{

		private ClazzTalkReply talkReply;
		private int talkIndex;
		private int replyIndex;
		private int replyNum;
		
		public ReplyOnclick(int talkIndex, int replyIndex) {
			this.replyIndex = replyIndex;
			this.talkIndex = talkIndex;
			this.replyNum = data.get(talkIndex).getReplyList().size();
			if(replyIndex >= 0){
				this.talkReply = data.get(talkIndex).getReplyList().get(replyIndex);
			}
		}
		
		private void showPop(){
			popupWindowView = View.inflate(context, R.layout.popup_reply_talk, null);
            popupWindow = new PopupWindow(popupWindowView,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,true);
            final EditText inputReply = (EditText) popupWindowView.findViewById(R.id.input_message);
            if(replyIndex >= 0){
            	inputReply.setHint("回复：" + talkReply.getFromName());
            }else{
            	inputReply.setHint("评论：");
            }
            Button sendReply = (Button) popupWindowView.findViewById(R.id.send_btn);
            sendReply.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// 发送评论
					String inputString = inputReply.getText().toString().trim();
					if(inputString.length() > 0){
						sendReplyFromNet(talkIndex, talkReply, inputString);
					} else {
						Toast.makeText(context, "内容不可为空！", Toast.LENGTH_SHORT).show();
					}
				}
			});
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //设置PopupWindow的弹出和消失效果
            popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
            openKeyboard(new Handler(), 200);
		}
		
		@Override
		public void onClick(View v) {
			if(talkReply  == null){
				if(replyNum >= MAXREPLYNUM){
					Toast.makeText(context, "超过评论数的最大值了，不可评论！", Toast.LENGTH_SHORT).show();
					return;
				} else {
					showPop();
				}
			}else if(talkReply.getFromId() == BaseApplication.getInstance().getDefaultAccount().getUserId()){
				deleteReplyDialog = new CustomDialog(context, true);
				deleteReplyDialog.setCustomView(R.layout.dlg_delete_item);
				Window window = deleteReplyDialog.getDialog().getWindow();
				window.setGravity(Gravity.BOTTOM);
				window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				Button delete = (Button) deleteReplyDialog.findViewById(R.id.btnDelete);
				Button cancel = (Button) deleteReplyDialog.findViewById(R.id.btnCancel);
				LinearLayout lyDlg;
				lyDlg = (LinearLayout) deleteReplyDialog.findViewById(R.id.dialog_layout);
				lyDlg.setPadding(0, 0, 0, 0);
				delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						deleteReplyFromNet(talkIndex ,replyIndex);
					}
				});
				cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						deleteReplyDialog.dismiss();
					}
				});
				deleteReplyDialog.setCancelable(true);
				deleteReplyDialog.show();
			} else {
				if(replyNum >= MAXREPLYNUM){
					Toast.makeText(context, "超过评论数的最大值了，不可评论！", Toast.LENGTH_SHORT).show();
					return;
				} else {
					showPop();
				}
			}
		}
	}

	private void handlerOneImage(ViewHolder viewHolder, final ClazzTalk talk) {
		final List<ClazzImage> imageList = new ArrayList<ClazzImage>();
		ClazzImage newImage = new ClazzImage();
		newImage.setTalkContent(talk.getContent());
		//根据服务器缩略图“http://XXXXX_small.jpg”把后面的“_small”去掉，则会获取到高清图
		String[] subPicUri = talk.getPicUrl().get(0).split("_small");
		String picUri = subPicUri[0] + subPicUri[1];
		
		newImage.setOrgPath(picUri);
		newImage.setIsPraise(talk.getIsPraise());
		newImage.setReplyNum(talk.getReplyNum());
		newImage.setSupportNum(talk.getPraiseNum());
		newImage.setTalkId(talk.getId());;
		imageList.add(newImage);
		
        int totalWidth; //适应宽度
        int imageWidth; //宽度
        int imageHeight; //高度
        ScreenTools screentools = ScreenTools.instance(context);
        totalWidth = screentools.getScreenWidth() - screentools.dip2px(20);
        
        imageHeight = screentools.dip2px(140);
        viewHolder.iv.setClickable(true);
        ViewGroup.LayoutParams layoutparams = viewHolder.iv.getLayoutParams();
        //getUrl_type == | 1 拍视频 | 0 图片
        if(talk.getUrl_type() == 1){
        	imageWidth = imageHeight + 40;
        	viewHolder.icVideo.setVisibility(View.VISIBLE);
        	viewHolder.iv.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				Intent intent = new Intent(Intent.ACTION_VIEW);
    				String type = "video/3gp";
    				Uri uri = Uri.parse(talk.getVideoUrl());
    				intent.setDataAndType(uri, type);
    				context.startActivity(intent);
    			}
    		});
            layoutparams.width = imageWidth;
            layoutparams.height = imageHeight;
            viewHolder.iv.setLayoutParams(layoutparams);
            viewHolder.iv.setClickable(true);
        	viewHolder.iv.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
        	//显示小图
            viewHolder.iv.setImageUrl(talk.getPicUrl().get(0));
		} else {
			imageWidth = totalWidth;
			viewHolder.icVideo.setVisibility(View.GONE);
			viewHolder.iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent inte = new Intent(context,
							ImgDisplayActivity.class);
					Bundle bu = new Bundle();
					bu.putSerializable("images", (Serializable) imageList);
					inte.putExtra("bundle", bu);
					inte.putExtra("position", 0);
					context.startActivity(inte);
				}
			});
	        layoutparams.width = imageWidth;
	        layoutparams.height = imageHeight + 80;
	        viewHolder.iv.setLayoutParams(layoutparams);
			viewHolder.iv.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
	        //一张图时显示高清图
	        viewHolder.iv.setImageUrl(picUri);
		}
    }
	
	/**
	 * 打开软键盘
	 */
	private void openKeyboard(Handler mHandler, int s) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, s);
	}
	
	class ViewHolder {
		ImageView senderAvatar;
		TextView senderName;
		TextView senderTime;
		RelativeLayout contentLayout;
		TextView talkContent;
		RelativeLayout shareLayout;
		ImageView senderShareImage;
		TextView senderShareContent;
		LinearLayout talkZanbtn;
		LinearLayout talkReplybtn;
		TextView talkZanNum;
		TextView talkReplyNum;
		LinearLayout attachLayout;
//		NoScorllGridView attachGridView;
		NineGridlayout attachView;
		CustomImageView iv;
		LinearLayout replyLayout;
		LinearLayout detailLinear;
		View icVideo;
		TextView shareNoticeText;
		TextView deleteTalk;
//		InnerListView replyListView;
	}
	
	/**
	 * 网络请求发送评论
	 * @param position 第position个说说
	 * @param talkReply 选中的回复对象 若对说说回复，则为null
	 * @param content 评论的文字
	 */
	private void sendReplyFromNet(final int position, final ClazzTalkReply talkReply, final String content) {
		ProgressDialogUtils.showProgressDialog("", context);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "replyTalk");
		params.put("id", data.get(position).getId() + "");
		params.put("studentId", (BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : account.getId()) + "");
		if(talkReply != null){
			params.put("toId", talkReply.getFromId() + "");
			params.put("toStudentId", talkReply.getFromStudentId() + "");
		} else {
			params.put("toId", 0 + "");
			params.put("toStudentId", 0 + "");
		}
		params.put("content", content);
		
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_replyTalk, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							int count = response.optInt("count");
//							String fromName = response.optString("fromName");
							data.get(position).setReplyNum(count);
//							ClazzTalkReply newReply = new ClazzTalkReply();
//							newReply.setContent(content);
//							newReply.setFromId(BaseApplication.getInstance().getDefaultAccount().getUserId());
//							newReply.setFromName(fromName);
//							newReply.setFromStudentId(BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : account.getId());
//							newReply.setId(data.get(position).getId());
//							
//							if(talkReply != null){
//								newReply.setToId(talkReply.getFromId());
//								newReply.setToName(talkReply.getFromName());
//								newReply.setToStudentId(talkReply.getFromStudentId());
//							} else {
//								newReply.setToId(0);
//								newReply.setToName(null);
//								newReply.setToStudentId(0);
//							}
//							data.get(position).getReplyList().add(0, newReply);
//							notifyDataSetChanged();
							getReplyFromNet(position);
							popupWindow.dismiss();
						} else if (response.optInt("ret") == 1){
							Toast.makeText(context, response.optString("msg"), Toast.LENGTH_SHORT).show();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, context);
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	/**
	 * 网络请求获取回复
	 * @param talkIndex 第talkIndex个说说
	 */
	private void getReplyFromNet(final int talkIndex) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getClassTalkReply");
		params.put("id", data.get(talkIndex).getId() + "");
		params.put("fromId", 0 + "");
		params.put("size", 100 + "");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_getClassTalkReply, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							List<ClazzTalkReply> listReply = ClazzTalkReply.parseFromJson(response.optJSONArray("data"));
							data.get(talkIndex).setReplyList(listReply);
							notifyDataSetChanged();
						} else if (response.optInt("ret") == 1){
							Toast.makeText(context, response.optString("msg"), Toast.LENGTH_SHORT).show();
						}
						ProgressDialogUtils.dismissProgressBar();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, context);
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	/**
	 * 网络请求赞或取消赞
	 * @param position 第position个说说
	 * @param type 请求类型 1为赞 2为取消赞
	 */
	private void sendZanFromNet(final int position ,final int type) {
		ProgressDialogUtils.showProgressDialog("", context);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "praiseTalk");
		params.put("id", data.get(position).getId() + "");
		params.put("type", type + "");
		params.put("studentid", (BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : account.getId()) + "");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_praiseTalk, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							int count = response.optInt("count");
							data.get(position).setPraiseNum(count);
							if(type == 1){
								data.get(position).setIsPraise(ISPRAISE);
							} else {
								data.get(position).setIsPraise(NOTPRAISE);
							}
							notifyDataSetChanged();
							Toast.makeText(context, response.optString("msg"), Toast.LENGTH_SHORT).show();
						} else if (response.optInt("ret") == 1){
							Toast.makeText(context, response.optString("msg"), Toast.LENGTH_SHORT).show();
						}
						ProgressDialogUtils.dismissProgressBar();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, context);
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	/**
	 * 网络请求删除评论
	 * @param talkIndex 第talkIndex个说说
	 * @param replyIndex 第replyIndex个说说
	 */
	private void deleteReplyFromNet(final int talkIndex, final int replyIndex) {
		ProgressDialogUtils.showProgressDialog("", context);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "deleteReplyTalk");
		params.put("id", data.get(talkIndex).getReplyList().get(replyIndex).getId() + "");
		params.put("studentid", (BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : account.getId()) + "");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_deleteReplyTalk, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							int count = response.optInt("count");
							data.get(talkIndex).getReplyList().remove(replyIndex);
							data.get(talkIndex).setReplyNum(count);
							notifyDataSetChanged();
							deleteReplyDialog.dismiss();
							Toast.makeText(context, response.optString("msg"), Toast.LENGTH_SHORT).show();
						} else if (response.optInt("ret") == 1){
							Toast.makeText(context, response.optString("msg"), Toast.LENGTH_SHORT).show();
						}
						ProgressDialogUtils.dismissProgressBar();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, context);
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
	
	/**
	 * 网络请求删除说说
	 * @param talkIndex 第talkIndex个说说
	 */
	private void deleteTalkFromNet(final int talkIndex) {
		ProgressDialogUtils.showProgressDialog("", context);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "deleteTalk");
		params.put("id", data.get(talkIndex).getId() + "");
		params.put("studentid", (BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : account.getId()) + "");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_deleteTalk, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							data.remove(talkIndex);
							notifyDataSetChanged();
							Toast.makeText(context, response.optString("msg"), Toast.LENGTH_SHORT).show();
						} else if (response.optInt("ret") == 1){
							Toast.makeText(context, response.optString("msg"), Toast.LENGTH_SHORT).show();
						}
						ProgressDialogUtils.dismissProgressBar();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						StatusUtils.handleError(arg0, context);
						ProgressDialogUtils.dismissProgressBar();
					}
				});
		BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
	}
}
