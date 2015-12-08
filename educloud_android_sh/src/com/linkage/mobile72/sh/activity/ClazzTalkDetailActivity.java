package com.linkage.mobile72.sh.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
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
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.http.ClazzTalk;
import com.linkage.mobile72.sh.data.http.ClazzTalkReply;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.im.bean.ClazzImage;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.RelativeDateFormat;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.widget.CustomImageView;
import com.linkage.mobile72.sh.widget.Image;
import com.linkage.mobile72.sh.widget.InnerListView;
import com.linkage.mobile72.sh.widget.NineGridlayout;
import com.linkage.mobile72.sh.widget.ScreenTools;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;
import com.linkage.ui.widget.CustomDialog;
import com.linkage.ui.widget.NoScorllGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.tencent.mm.sdk.platformtools.Log;

public class ClazzTalkDetailActivity extends BaseActivity {

	private final static String TAG = "ClazzTalkDetailActivity";

	private final static int ISPRAISE = 1;
	private final static int NOTPRAISE = 0;
	private final static int MAXREPLYNUM = 100;
	private final static int CLICKFROMUSER = 1;
	private final static int CLICKTOUSER = 2;
	
	private long talkId;
	private ClazzTalk talk;
	
	private RelativeLayout shareLayout;
	private LinearLayout attachLayout,replyLayout;
	private NineGridlayout attachView;
	private CustomImageView iv;
	private ImageView senderAvatar, senderShareImage;
	private TextView senderName, senderTime, talkContent, senderShareContent, shareNoticeText;
	private LinearLayout talkZanbtn, talkReplybtn;
	private TextView talkZanNum, talkReplyNum;
	private NoScorllGridView attachGridView;
	private InnerListView replyListView;
	
	private PopupWindow popupWindow;
	private View popupWindowView;
	private DisplayImageOptions defaultOptionsPhoto, clazzImageOption;
	private CustomDialog deleteReplyDialog;
	 
	private View parentView, icVideo;
	private Context context;
	private Button back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clazz_talk_detail);
		setTitle("说说详情");
		RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.relativelayout1);
		if (isTeacher()) {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg_green);
		} else {
			titleLayout.setBackgroundResource(R.drawable.title_top_bg);
		}
		
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
		
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		talkId = getIntent().getLongExtra("talkId", 0);
		defaultOptionsPhoto = new DisplayImageOptions.Builder().cacheOnDisc().cacheInMemory()
	            .showStubImage(R.drawable.imgbg)
	            .showImageForEmptyUri(R.drawable.imgbg)
	            .showImageOnFail(R.drawable.imgbg).build();
		senderAvatar = (ImageView) findViewById(R.id.talk_sender_avatar);
		senderName = (TextView) findViewById(R.id.talk_sender_name);
		senderTime = (TextView) findViewById(R.id.talk_sender_time);
		talkContent = (TextView) findViewById(R.id.talk_sender_content);
		shareLayout = (RelativeLayout) findViewById(R.id.talk_share_layout);
		senderShareImage = (ImageView) findViewById(R.id.talk_sender_share_image);
		senderShareContent = (TextView) findViewById(R.id.talk_sender_share_text);
		talkZanbtn = (LinearLayout) findViewById(R.id.talk_zan_btn);
		talkReplybtn = (LinearLayout) findViewById(R.id.talk_reply_btn);
		talkZanNum = (TextView) findViewById(R.id.clazz_talk_zan_num);
		talkReplyNum = (TextView) findViewById(R.id.clazz_talk_reply_num);
		attachLayout = (LinearLayout) findViewById(R.id.talk_attach_layout);
		replyLayout = (LinearLayout) findViewById(R.id.talk_reply_layout);
		
		attachView = (NineGridlayout) findViewById(R.id.iv_ngrid_layout);
		iv = (CustomImageView) findViewById(R.id.iv_oneimage);
		replyLayout = (LinearLayout) findViewById(R.id.talk_reply_layout);
		icVideo = findViewById(R.id.ic_video);
		shareNoticeText = (TextView) findViewById(R.id.share_notice_text);
		
		context = ClazzTalkDetailActivity.this;
		parentView = View.inflate(context, R.layout.activity_clazz_talk_detail, null);
		fetchData(talkId);
	}
	
	private void fetchData(long id) {
        ProgressDialogUtils.showProgressDialog("", this, true);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("commandtype", "getClassTalkDetail");//测试，后面要，改这个地址不对
        params.put("id", String.valueOf(id));
        params.put("studentid", (BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : getDefaultAccountChild().getUserid()) + "");
		
        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
                Consts.SERVER_getClassTalkDetail, Request.Method.POST, params, true,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressDialogUtils.dismissProgressBar();
                        if (response.optInt("ret") == 0) {
                            talk = ClazzTalk.parseFromJson(response.optJSONObject("data"));
                            loadView();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        ProgressDialogUtils.dismissProgressBar();
                        StatusUtils.handleError(arg0, ClazzTalkDetailActivity.this);
                    }
                });
        BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }
	
	private void loadView() {
		if(talk != null) {
			imageLoader.displayImage(talk.getCreaterUrl(), senderAvatar);
			senderName.setText(talk.getCreaterName());
			senderAvatar.setOnClickListener(new creatorNameOnclick());
			senderName.setOnClickListener(new creatorNameOnclick());
			senderTime.setText(RelativeDateFormat.format(talk.getCreateDate()));
			
			if(talk.getContent() == null || talk.getContent().equals("null")){
				talkContent.setVisibility(View.GONE);
			} else {
				talkContent.setVisibility(View.VISIBLE);
				talkContent.setText(talk.getContent());
			}
			if(talk.getOpt_type() == 0) {//原创
				shareLayout.setVisibility(View.GONE);
				shareNoticeText.setVisibility(View.GONE);
			}else if(talk.getOpt_type() == 1) {//分享
				shareLayout.setVisibility(View.VISIBLE);
				shareNoticeText.setVisibility(View.VISIBLE);
				imageLoader.displayImage(talk.getShare_pic(), senderShareImage, clazzImageOption);
				senderShareContent.setText(talk.getShare_title());
				shareLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String shareUrl = talk.getShare_url();
						Intent inte = new Intent(context, NewWebViewActivity.class);
						inte.putExtra(NewWebViewActivity.KEY_TITLE, "分享详情");
						inte.putExtra(NewWebViewActivity.KEY_URL, shareUrl);
						context.startActivity(inte);
					}
				});
			}
			
			//附件
			if(talk.getPicImages() != null && talk.getPicImages().size() > 0) {
				
				attachLayout.setVisibility(View.VISIBLE);
				if (talk.getPicImages().isEmpty()) {
		            attachView.setVisibility(View.GONE);
		            iv.setVisibility(View.GONE);
		        } else if (talk.getPicImages().size() == 1) {
		            attachView.setVisibility(View.GONE);
		            iv.setVisibility(View.VISIBLE);
		            handlerOneImage(talk);
		        } else {
		            attachView.setVisibility(View.VISIBLE);
		            iv.setVisibility(View.GONE);

		            attachView.setImagesData(talk);
		        }
			}else {
				attachLayout.setVisibility(View.GONE);
			}
			
			//赞、评论数
			if (talk.getIsPraise() == NOTPRAISE) {
				Drawable drawable = getResources().getDrawable(
						R.drawable.clazz_talk_list_zan_normal);
				talkZanNum.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null,
						null);
				talkZanNum.setText("赞 (" + talk.getPraiseNum() + ")");
				talkZanbtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						sendZanFromNet(1);// 1为点赞
					}
				});
			} else {
				Drawable drawable = getResources().getDrawable(
						R.drawable.clazz_talk_list_zan_click);
				talkZanNum.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null,
						null);
				talkZanNum.setText("赞 (" + talk.getPraiseNum() + ")");
				talkZanbtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						sendZanFromNet(2);// 2为取消赞
					}
				});
			}
			//回复
			talkReplyNum.setText("评论 (" + talk.getReplyNum() + ")");
			talkReplybtn.setOnClickListener(new ReplyOnclick(-1));
			
			//评论列表
			replyLayout.removeAllViews();
			final List<ClazzTalkReply> replyList = talk.getReplyList();
			if(replyList != null && replyList.size() > 0) {
				replyLayout.setVisibility(View.VISIBLE);
				for(int i = 0;i < replyList.size();i++){
					final View replyItem = View.inflate(context, R.layout.item_talk_reply, null);
					
					TextView content = (TextView) replyItem.findViewById(R.id.reply_content);
					content.setOnClickListener(new ReplyOnclick(i));
					content.setOnLongClickListener(new OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View v) {
							// TODO Auto-generated method stub
							return true;
						}
					});
					String fromName = replyList.get(i).getFromName();
			        SpannableString spanFromName = new SpannableString(fromName);
			        ClickableSpan clickFromName = new ReplyNameOnclick(i, CLICKFROMUSER);
			        spanFromName.setSpan(clickFromName, 0, fromName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			        content.setText(spanFromName);
			        
			        content.append("回复");
			        
			        if(replyList.get(i).getToName() != null){
			        	String toName = replyList.get(i).getToName();
			        	SpannableString spanToName = new SpannableString(toName);
			        	ClickableSpan clickToName = new ReplyNameOnclick(i, CLICKTOUSER);
			        	spanToName.setSpan(clickToName, 0, toName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			        	content.append(spanToName);
			        }
			        
			        content.append(":" + replyList.get(i).getContent());
			        content.setMovementMethod(LinkMovementMethod.getInstance());
//					content.setText(replyList.get(i).getContent());
					replyLayout.addView(replyItem, i);
				}
			}else {
				replyLayout.setVisibility(View.GONE);
			}
			
		}
	}
	
	class creatorNameOnclick implements OnClickListener{
		
		long creatorId;
		public creatorNameOnclick() {
			creatorId = talk.getCreaterId();
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
		
		public ReplyNameOnclick(int replyIndex, int type) {
			this.type = type;
			if(replyIndex >= 0){
				this.talkReply = talk.getReplyList().get(replyIndex);
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
	
	private void handlerOneImage(final ClazzTalk talk) {
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
        iv.setClickable(true);
        ViewGroup.LayoutParams layoutparams = iv.getLayoutParams();
        //getUrl_type == | 1 拍视频 | 0 图片
        if(talk.getUrl_type() == 1){
        	imageWidth = imageHeight + 40;
        	icVideo.setVisibility(View.VISIBLE);
        	iv.setOnClickListener(new OnClickListener() {
    			
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
            iv.setLayoutParams(layoutparams);
            iv.setClickable(true);
        	iv.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
        	//显示小图
            iv.setImageUrl(talk.getPicUrl().get(0));
		} else {
			imageWidth = totalWidth;
			icVideo.setVisibility(View.GONE);
			iv.setOnClickListener(new OnClickListener() {
				
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
	        iv.setLayoutParams(layoutparams);
			iv.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
	        //一张图时显示高清图
	        iv.setImageUrl(picUri);
		}
    }
	
	//初始化回复的popupwindows
	class ReplyOnclick implements OnClickListener{

		private ClazzTalkReply talkReply;
		private int replyIndex;
		private int replyNum;
		
		public ReplyOnclick(int replyIndex) {
			if(replyIndex >= 0){
				this.talkReply = talk.getReplyList().get(replyIndex);
			}
			this.replyNum = talk.getReplyList().size();
			this.replyIndex = replyIndex;
		}
		
		private void showPop(){
			popupWindowView = View.inflate(context, R.layout.popup_reply_talk, null);
            popupWindow = new PopupWindow(popupWindowView,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,true);
            final EditText inputReply = (EditText) popupWindowView.findViewById(R.id.input_message);
            if(replyIndex > 0){
            	inputReply.setHint("回复：" + talkReply.getFromName());
            }else{
            	inputReply.setHint("评论：");
            }
            Button sendReply = (Button) popupWindowView.findViewById(R.id.send_btn);
            sendReply.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String inputString = inputReply.getText().toString().trim();
					if(inputString.length() > 0){
						sendReplyFromNet(talkReply, inputString);
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
						deleteReplyFromNet(replyIndex);
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
	/**
	 * 关闭软键盘
	 */
	private void closeKeyboard(Handler mHandler, int s) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				View view = getWindow().peekDecorView();
			     if (view != null) {
			         InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			         inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
			     }
			}
		}, s);
	}
	
	/**
	 * 网络请求发送评论
	 * @param talkReply 选中的回复对象 若对说说回复，则为null
	 * @param content 评论的文字
	 */
	private void sendReplyFromNet(final ClazzTalkReply talkReply, final String content) {
		ProgressDialogUtils.showProgressDialog("", context);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "replyTalk");
		params.put("id", talk.getId() + "");
		params.put("studentId", (BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : getDefaultAccountChild().getId()) + "");
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
							talk.setReplyNum(count);
//							ClazzTalkReply newReply = new ClazzTalkReply();
//							newReply.setContent(content);
//							newReply.setFromId(BaseApplication.getInstance().getDefaultAccount().getUserId());
//							newReply.setFromName(fromName);
//							newReply.setFromStudentId(BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : getDefaultAccountChild().getId());
//							newReply.setId(talk.getId());
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
//							talk.getReplyList().add(0, newReply);
							getReplyFromNet();
							popupWindow.dismiss();
							closeKeyboard(new Handler(), 200);
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
	 * 网络请求获取回复
	 * @param talkIndex 第talkIndex个说说
	 */
	private void getReplyFromNet() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "getClassTalkReply");
		params.put("id", talk.getId() + "");
		params.put("fromId", 0 + "");
		params.put("size", 100 + "");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_getClassTalkReply, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							List<ClazzTalkReply> listReply = ClazzTalkReply.parseFromJson(response.optJSONArray("data"));
							talk.setReplyList(listReply);
							loadView();
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
	 * @param type 请求类型 1为赞 2为取消赞
	 */
	private void sendZanFromNet(final int type) {
		ProgressDialogUtils.showProgressDialog("", context);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "praiseTalk");
		params.put("id", talk.getId() + "");
		params.put("type", type + "");
		params.put("studentid", (BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : getDefaultAccountChild().getId()) + "");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_praiseTalk, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							int count = response.optInt("count");
							talk.setPraiseNum(count);
							if(type == 1){
								talk.setIsPraise(ISPRAISE);
							} else {
								talk.setIsPraise(NOTPRAISE);
							}
							loadView();
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
	 * @param replyIndex 第replyIndex个说说
	 */
	private void deleteReplyFromNet(final int replyIndex) {
		ProgressDialogUtils.showProgressDialog("", context);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("commandtype", "deleteReplyTalk");
		params.put("id", talk.getReplyList().get(replyIndex).getId() + "");
		params.put("studentid", (BaseApplication.getInstance().getDefaultAccount().getUserType() == 1 ? 0 : getDefaultAccountChild().getId()) + "");
		WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
				Consts.SERVER_deleteReplyTalk, Request.Method.POST, params, true,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.optInt("ret") == 0) {
							int count = response.optInt("count");
							talk.getReplyList().remove(replyIndex);
							talk.setReplyNum(count);
							loadView();
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
}
