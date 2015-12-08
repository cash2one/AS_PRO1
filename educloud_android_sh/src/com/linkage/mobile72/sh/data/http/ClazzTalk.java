package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.widget.Image;

public class ClazzTalk implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private long createrId;
	private String createrName;
	private String createrUrl;
	private String createDate;
	private int opt_type;
	private int share_type;
	private String share_title;
	private String share_sub_title;
	private String share_sub_content;
	private String share_pic;
	private long share_id;
	private String share_url;
	private String share_comment_url;
	private String content;
	private int url_type;
	private List<String> picUrl;
	private List<Image> picImages;
	private String videoUrl;
	private int praiseNum;
	private int replyNum;
	private int isPraise;
	private List<ClazzTalkReply> replyList;
	
	public static ClazzTalk parseFromJson(JSONObject jsonObj) {
		ClazzTalk talk = new ClazzTalk();
		talk.setId(jsonObj.optLong("id"));
		talk.setContent(jsonObj.optString("content"));
		talk.setCreateDate(jsonObj.optString("createDate"));
		talk.setCreaterId(jsonObj.optLong("createrId"));
		talk.setCreaterName(jsonObj.optString("createrName"));
		talk.setCreaterUrl(jsonObj.optString("createrUrl"));
		talk.setIsPraise(jsonObj.optInt("isPraise"));
		talk.setOpt_type(jsonObj.optInt("opt_type"));
		String picUrl = jsonObj.optString("picUrl");
		if(!StringUtils.isEmpty(picUrl)) {
			if(picUrl.contains(",")) {
				String[] pics = picUrl.split(",");
				talk.setPicUrl(Arrays.asList(pics));
			}else {
				List<String> picUrls = new ArrayList<String>();
				picUrls.add(picUrl);
				talk.setPicUrl(picUrls);
			}
		}
		
		List<Image> images = new ArrayList<Image>();
		if(talk.getPicUrl() != null && talk.getPicUrl().size() > 0) {
			for(String s : talk.getPicUrl()) {
				Image i = new Image(s, 200, 100);
				images.add(i);
			}
		}
		talk.setPicImages(images);
		talk.setPraiseNum(jsonObj.optInt("praiseNum"));
		talk.setReplyList(ClazzTalkReply.parseFromJson(jsonObj.optJSONArray("replyList")));
		talk.setReplyNum(jsonObj.optInt("replyNum"));
		talk.setShare_id(jsonObj.optLong("share_id"));
		talk.setShare_pic(jsonObj.optString("share_pic"));
		talk.setShare_type(jsonObj.optInt("share_type"));
		talk.setShare_title(jsonObj.optString("share_title"));
		talk.setShare_url(jsonObj.optString("share_url"));
		talk.setShare_sub_title(jsonObj.optString("share_sub_title"));
		talk.setShare_sub_content(jsonObj.optString("share_sub_content"));
		talk.setShare_comment_url(jsonObj.optString("commentUrl"));
		talk.setUrl_type(jsonObj.optInt("url_type"));
		talk.setVideoUrl(jsonObj.optString("videoUrl"));
		return talk;
	}

	public static List<ClazzTalk> parseFromJson(JSONArray jsonArray) {
		List<ClazzTalk> talks = new ArrayList<ClazzTalk>();
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				ClazzTalk talk = parseFromJson(jsonArray.optJSONObject(i));
				if (talk != null)
					talks.add(talk);
			}
		}
		return talks;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCreaterId() {
		return createrId;
	}

	public void setCreaterId(long createrId) {
		this.createrId = createrId;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public String getCreaterUrl() {
		return createrUrl;
	}

	public void setCreaterUrl(String createrUrl) {
		this.createrUrl = createrUrl;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getOpt_type() {
		return opt_type;
	}

	public void setOpt_type(int opt_type) {
		this.opt_type = opt_type;
	}

	public int getShare_type() {
		return share_type;
	}

	public void setShare_type(int share_type) {
		this.share_type = share_type;
	}

	public String getShare_title() {
		return share_title;
	}

	public void setShare_title(String share_title) {
		this.share_title = share_title;
	}

	public String getShare_sub_title() {
		return share_sub_title;
	}

	public void setShare_sub_title(String share_sub_title) {
		this.share_sub_title = share_sub_title;
	}

	public String getShare_sub_content() {
		return share_sub_content;
	}

	public void setShare_sub_content(String share_sub_content) {
		this.share_sub_content = share_sub_content;
	}

	public String getShare_pic() {
		return share_pic;
	}

	public void setShare_pic(String share_pic) {
		this.share_pic = share_pic;
	}

	public long getShare_id() {
		return share_id;
	}

	public void setShare_id(long share_id) {
		this.share_id = share_id;
	}

	public String getShare_url() {
		return share_url;
	}

	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getUrl_type() {
		return url_type;
	}

	public void setUrl_type(int url_type) {
		this.url_type = url_type;
	}

	public List<String> getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(List<String> picUrl) {
		this.picUrl = picUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public int getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(int praiseNum) {
		this.praiseNum = praiseNum;
	}

	public int getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(int replyNum) {
		this.replyNum = replyNum;
	}

	public int getIsPraise() {
		return isPraise;
	}

	public void setIsPraise(int isPraise) {
		this.isPraise = isPraise;
	}

	public List<ClazzTalkReply> getReplyList() {
		return replyList;
	}

	public void setReplyList(List<ClazzTalkReply> replyList) {
		this.replyList = replyList;
	}

	public List<Image> getPicImages() {
		return picImages;
	}

	public void setPicImages(List<Image> picImages) {
		this.picImages = picImages;
	}

	public String getShare_comment_url() {
		return share_comment_url;
	}

	public void setShare_comment_url(String share_comment_url) {
		this.share_comment_url = share_comment_url;
	}
	
}
