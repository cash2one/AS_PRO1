package com.linkage.app;


public class Consts {
	
	public static final boolean DEBUG_MODE = true;
	
	//聊天相关
	public static final String IM_AUTHORITY = "com.linkage.mobile72.sh.im";
	public final static int LOGIN_ERROR_ACCOUNT_PASS = 2;
	public final static int SERVER_UNAVAILABLE = 3;
	public final static int LOGIN_SECCESS = 1;
	public final static String BROADCAST_NEW_CHAT_MESSAGE = "com.linkage.mobile72.sh.intent.action.chat_message";
	public final static String BROADCAST_SYS_MESSAGE = "com.linkage.mobile72.sh.intent.action.sys_message";
	public final static int CHAT_NOTIFICATION_ID = 0x33;
	
	// test teacher
	// parent add mark 
	
	public static Boolean is_Teacher = true;
	public static final String SECRET_KEY = "njxtqgjypt";

	public static final String PATH_APP = "gnx/";
	
	
	public static final String PATH_DOWNLOAD = "download/";
	public static final String PATH_VOICE = "voice/";
	public static final String PATH_IMAGE = "image/";
	public static final String PATH_VIDEO = "video/";
	public static final String UPLOAD_IMAGE_FILE = "upload.jpeg";
	

	public static final String ORIGIN_FORMAT = "p%sa01v%s";
	public static final String ORIGIN_KEY = "origin";
	public static final String AccessToken = "token";
	public static final String PROVINCE = "11";
	public static final String VERSION_NAME = "1.0.0";
	public static final String PAGE_SIZE = "20";

	//阿里云
	/*public static String SERVER_IP = "http://121.41.62.98:9200";//"http://112.4.28.173:8000";
	public static String SERVER_PRO = SERVER_IP + "/educloud_new";
	public static String SERVER_URL = SERVER_IP + "/educloud_new/api/terminal"; //主路径
////	//shanghai
//	public static String SERVER_IP = "http://221.130.6.212:3381";
//	public static String SERVER_PRO = SERVER_IP + "/educloud";
//	public static String SERVER_URL = SERVER_IP + "/educloud/api/terminal"; //主路径
	//现网主机
//	public static String SERVER_IP = "http://221.130.183.5:8080";
//	public static String SERVER_PRO = SERVER_IP + "/educloud";
//	public static String SERVER_URL = SERVER_PRO + "/api/terminal"; //主路径*/

	public static String SERVER_IP;//"http://112.4.28.173:8000";
	public static String SERVER_PRO;
	public static String SERVER_URL;//主路径

	public static String APPKEY;
	public static String OPINIONURL;//意见反馈详情的url

	static {
		if(DEBUG_MODE) {
			//阿里云
			SERVER_IP = "http://121.41.62.98:9200";//"http://112.4.28.173:8000";
			SERVER_PRO = SERVER_IP + "/educloud_new";
			SERVER_URL = SERVER_IP + "/educloud_new/api/terminal"; //主路径
			OPINIONURL = SERVER_PRO+ "/ucenter/adviceReply/reply";
			APPKEY = "34e5b17345f3d7c5";
		}else {
			//现网主机
			SERVER_IP = "http://Aservice.139jy.cn";
			SERVER_PRO = SERVER_IP + "/educloud";
			SERVER_URL = SERVER_IP + "/educloud/api/terminal"; //主路径
			OPINIONURL = SERVER_PRO+ "/ucenter/adviceReply/reply";
			APPKEY = "34e5b17345f3d7c5";
		}
	}

	public static String SERVER_URL_NEW = SERVER_IP+"/educloud/api/terminalClient";
	
	public static final String IM_ATTACHMENT_PACKAGE = SERVER_IP;
//	public static String IM_ATTACHMENT_PACKAGE = "http://112.33.2.96:8882/educloudapi";
	public static String SERVER_uploadAttachment =  SERVER_IP + "/userApi/uploadFile";
//	public static String SERVER_uploadAttachment = "http://112.33.2.96:8882/educloudapi/api/upload"; //上传聊天附件
//	public static final String IM_SERVER = "ws://192.168.20.98:8180/im";//外网环境
	public static final String IM_SERVER = "ws://112.25.222.170:8888/im";//外网环境
	
	
	public static final String RIGHT_RESERVE = SERVER_PRO + "/html/ucenter/disclaimer.html";
	
	
	public static String SERVER_HOST = ""; //主域名
	public static String SERVER_GetSMSCode = SERVER_IP + "/userApi/sendSMSCode"; //获取验证码
	public static String SERVER_EnteringPerson_Register = SERVER_IP + "/userApi/setPasswordOffRegister"; //未注册且已由校长录入用户设置密码
	public static String SERVER_Register = SERVER_IP + "/userApi/register"; //注册
	public static String SERVER_SetPerson_Info = SERVER_IP + "/userApi/updateUserInfo"; //修改姓名头像接口
	public static String SERVER_ChangePwd = SERVER_IP + "/userApi/updatePassword"; //修改密码
	public static String SERVER_ResetPwd = SERVER_IP + "/userApi/resetPassword"; //重置密码
	public static String SERVER_GetVersion = SERVER_IP + "/userApi/checkVersion"; //版本更新
	
	//组织（学校）相关
	public static String SERVER_GetSchoolInfo = SERVER_IP + "/schoolApi/getAllSchoolList"; //获取组织（学校）列表
	public static String SERVER_Add_School = SERVER_IP + "/schoolApi/joinSchool"; //加入组织（学校）
	public static String SERVER_Exit_School = SERVER_IP + "/schoolApi/quitSchool"; //退出组织（学校）
	
	//子组织（班级）相关
	public static String SERVER_GetClassroom_bySchoolID = SERVER_IP + "/classroomApi/getClassroomListBySchool"; //根据学校返回相关子组织（班级）列表
	public static String SERVER_Exit_Classroom = SERVER_IP + "/classroomApi/quitClassroom"; //退出子组织（班级）
	public static String SERVER_SetClassroomEnter_Auth = SERVER_IP + "/classroomApi/setClassroomIsAutoApprove"; //设置子组织（班级）是否开启自动加入权限
	public static String SERVER_GetClassroom_PendingAudit = SERVER_IP + "/classroomApi/getWaitApproveUserList"; //子组织（班级）待审核列表
	public static String SERVER_ReviewClassroom = SERVER_IP + "/classroomApi/approveJoinClassroom"; //审核加入子组织（班级）
	public static String SERVER_Apply_EnterClassroom = SERVER_IP + "/classroomApi/applyJoinClassroom"; //申请加入子组织（班级）
	public static String SERVER_Kickoff_Classroom = SERVER_IP + "/classroomApi/kickoffClassroomMember"; //班主任删除子组织（班级）成员
	
	public static String SERVER_Change_ClassroomInfo = SERVER_IP + "/classroomApi/updateClassroomInfo"; //修改子组织（班级）名称、头像、简介
	public static String SERVER_Change_ClassroomName = SERVER_IP + "/classroomApi/updateClassroomVisitCard"; //修改群名片
	public static String SERVER_Invite_ClassroomName = SERVER_IP + "/classroomApi/invitationJoinClassroom"; //邀请加入子组织（都是发短信）
//	public static String SERVER_GetClassroomAndFriendList = SERVER_IP + "/classroomApi/getJoinedClassroomAndFriendList"; //根据用户返回已加入子组织（班级）列表和好友
	public static String SERVER_GetClassroom_member = SERVER_IP + "/classroomApi/getClassroomMemberList"; //获取子组织（班级）成员列表
	
	//好友相关
	public static String SERVER_FriendList = SERVER_IP+"/friendApi/getFriendList"; //好友列表
	public static String SERVER_Invite_Friend = SERVER_IP+"/friendApi/invitationFriend"; //邀请好友（非注册用户）(通过短信方式通知)
//	public static String SERVER_AddFriend = SERVER_IP+"/friendApi/addFriend"; //加好友
	public static String SERVER_ApplyFriend = SERVER_IP+"/friendApi/approveFriendInvitation"; //审核好友邀请
	public static String SERVER_DeleteFriend = SERVER_IP+"/friendApi/deleteFriend"; //删除好友
//	public static String SERVER_GetFriend_PendingAudit = SERVER_IP+"/friendApi/getWaitApproveFriendList"; //好友审核列表
	
//	public static String SERVER_SeacherGroupById = SERVER_IP+"/classroomApi/findClassroomByCId"; //按ID号查找群组(唯一)
//	public static String SERVER_SeacherFriend = SERVER_IP+"/friendApi/findFriend"; //查找好友
//	public static String SERVER_SeacherGroup = SERVER_IP+"/classroomApi/findClassroomByCNO"; //查找群组
	//家校互动
	public static String SERVER_sendMessage = SERVER_URL + "Client/sendMessage";
	public static String SERVER_sendMessageAttachment = SERVER_URL + "Client/sendMessageAttachment";
	public static String SERVER_getMessageList = SERVER_URL + "Client/getMessageList";
	public static String SERVER_getTheLatestOneMessage = SERVER_URL + "Client/getTheLatestOneMessage";
	public static String SERVER_getTodayMessage = SERVER_URL + "Client/getTodayMessage";
	public static String SERVER_getSubjectList = SERVER_URL + "Client/getSubjectList";
	public static String SERVER_getMessageDetail = SERVER_URL + "Client/getMessageDetailNew";
	public static String SERVER_deleteMessage = SERVER_URL + "Client/deleteMessage";
	public static String SERVER_messageRead = SERVER_URL + "Client/messageRead";
	public static String SERVER_messageReplied = SERVER_URL + "Client/messageReplied";
	public static String SERVER_getMessageUnreadList = SERVER_URL + "Client/getMessageUnreadList";
	public static String SERVER_getMessageUnRePlyList = SERVER_URL + "Client/getMessageUnRePlyListNew";
	public static String SERVER_sendVote = SERVER_URL + "Client/sendVote";
	public static String SERVER_sendOfficeMessage = SERVER_URL + "Client/sendOfficeMessage";
	public static String SERVER_getOfficeMessageList = SERVER_URL + "Client/getOfficeMessageList";
	public static String SERVER_getOfficeMessageDetail = SERVER_URL + "Client/getOfficeMessageDetail";
	public static String SERVER_getOfficeGroupInfo = SERVER_URL + "Client/getOfficeGroupInfo";
	public static String SERVER_getHomeSchoolGroupInfo = SERVER_URL + "Client/getHomeSchoolGroupInfo";
	public static String SERVER_sendSMSCode = SERVER_URL + "Client/sendSMSCode";
	public static String SERVER_getRollAds = SERVER_URL + "Client/getRollAds";
	public static String SERVER_getClassResultBySubject = SERVER_URL + "Client/getClassResultBySubject";
	public static String SERVER_getStuResultList = SERVER_URL + "Client/getStuResultList";
	public static String SERVER_getReceiveFlag = SERVER_URL + "Client/getReceiveFlag";
	public static String SERVER_settingsReceiveFlag = SERVER_URL + "Client/settingsReceiveFlag";
	public static String SERVER_getMyAppList = SERVER_URL + "Client/getMyAppList";
	public static String SERVER_getAppList = SERVER_URL + "Client/getAppList";
	public static String SERVER_getAppDetail = SERVER_URL + "Client/getAppDetail";
	public static String SERVER_refreshScore = SERVER_URL + "Client/refreshScore";
	public static String SERVER_appPay = SERVER_URL + "Client/appPay";
	public static String SERVER_sendPayCode = SERVER_URL + "Client/sendPayCode";
	public static String SERVER_getActivityList = SERVER_URL + "Client/getActivityList";
	public static String SERVER_sendTopicComment = SERVER_URL + "Client/sendTopicComment";
	public static String SERVER_getClazzAlbum = SERVER_URL + "Client/getClassAlbum";
	public static String SERVER_getClazzTalk = SERVER_URL + "Client/getClassTalk";
	public static String SERVER_sendTalk = SERVER_URL + "Client/sendTalk";
	public static String SERVER_sendClassChatAttachment = SERVER_URL + "Client/sendClassChatAttachment";
	public static String SERVER_shareToClass = SERVER_URL + "Client/shareToClass";
	public static String SERVER_praiseTalk = SERVER_URL + "Client/praiseTalk";
	public static String SERVER_replyTalk = SERVER_URL + "Client/replyTalk";
	public static String SERVER_deleteReplyTalk = SERVER_URL + "Client/deleteReplyTalk";
	public static String SERVER_getClassTalkDetail = SERVER_URL + "Client/getClassTalkDetail";
	public static String SERVER_deleteTalk = SERVER_URL + "Client/deleteTalk";
	public static String SERVER_getClassTalkReply = SERVER_URL + "Client/getClassTalkReply";
	public static String SERVER_reginfoForApply = SERVER_URL + "Client/reginfoForApply";
	public static String SERVER_clazzQrcode = SERVER_URL + "Client/clazzQrcode";
	
	public static String SERVER_getChildResults = SERVER_URL + "Client/getChildResults";
	public static String SERVER_getHomeAndSchoolOpenState = SERVER_URL + "Client/getHomeAndSchoolOpenState";
	
	public static String SERVER_companyAppGrant = SERVER_URL + "Client/companyAppGrant";
	/*public static String SERVER_sendMessage = SERVER_URL;
	public static String SERVER_sendMessageAttachment = SERVER_URL;
	public static String SERVER_getMessageList = SERVER_URL;
	public static String SERVER_getTheLatestOneMessage = SERVER_URL;
	public static String SERVER_getTodayMessage = SERVER_URL;
	public static String SERVER_getSubjectList = SERVER_URL;
	public static String SERVER_getMessageDetail = SERVER_URL;
	public static String SERVER_deleteMessage = SERVER_URL;
	public static String SERVER_messageRead = SERVER_URL;
	public static String SERVER_messageReplied = SERVER_URL;
	public static String SERVER_getMessageUnreadList = SERVER_URL;
	public static String SERVER_getMessageUnRePlyList = SERVER_URL;
	public static String SERVER_sendVote = SERVER_URL;
	public static String SERVER_sendOfficeMessage = SERVER_URL;
	public static String SERVER_getOfficeMessageList = SERVER_URL;
	public static String SERVER_getOfficeMessageDetail = SERVER_URL;
	public static String SERVER_getOfficeGroupInfo = SERVER_URL;
	public static String SERVER_getHomeSchoolGroupInfo = SERVER_URL;
	public static String SERVER_sendSMSCode = SERVER_URL;
	public static String SERVER_getRollAds = SERVER_URL;*/
	//缴费
	public static String SERVER_PaymentType = SERVER_IP + "/orderApi/getSystemOrderProjectList"; //缴费类型
	public static String SERVER_PaymentCreate = SERVER_IP + "/orderApi/teacherCreateOrder"; //创建缴费
	public static String SERVER_PaymentCreateList = SERVER_IP + "/orderApi/getTeacherOrderProjectList"; //教师的创建缴费列表
	public static String SERVER_PaymentList = SERVER_IP + "/orderApi/getMyOrderList"; //我的缴费列表
	public static String SERVER_PaymentDetail = SERVER_IP + "/orderApi/getOrderDetailByProjectId"; //缴费详细
	public static String SERVER_PaymentRefuse = SERVER_IP + "/orderApi/refuseOrder"; //缴费忽略
	
	//考勤
	public static String SERVER_GETKAOQINMEMBERS= SERVER_URL + "/getKaoqinMembers";
	
	public static String HOST_AVATAR=SERVER_IP+"/userApi/getUserAvatar?userId=";
	public static String HOST_GROUP_AVATAR=SERVER_IP+"/userApi/getClassroomAvatar?classroomId=";
	
	public static String getServer() {
		return is_Teacher ? "a01" : "a02";
	}
	
	public static interface UserType {
		int DEFAULT = 0;
		int TEACHER = 1;
		int PARENT = 3;
	}
	
	public static interface ChatType {
		int CHAT_TYPE_SINGLE = 0;
		int CHAT_TYPE_GROUP = 1;
		int CHAT_TYPE_NOTICE = 2;
		int CHAT_TYPE_TODAY_TOPIC = 10;
        int CHAT_TYPE_WONDER_EXER = 11;
	}
	
	public static interface JxhdType {
		int HOMEWORK = 14;
		int OFFICESMS = 1;
		int NOTICE = 2;
		int COMMENT = 3;
		int TOUPIAO = 10;
		int SCORE = 4;
	}
	
	public static interface projectType{
		int TIME = 1;
		int COUNT = 2;
		int LONG = 3;
	}
	
	public static interface MessageListType {
		int SEND_BOX = 2;
		int RECEIVE_BOX = 1;
	}
	
	public final static int SEARCH_TYPE_CLASS = 1;
	public final static int SEARCH_TYPE_FRIEND = 2;
	public final static int SEARCH_TYPE_CONTRACT = 3;
	
	public static final int BROADCAST_REJECT = 1;
    public static final int BROADCAST_DISCONNECT = 2;
    public static final int BROADCAST_JX_REMIND_SHOW = 3;
    public static final int BROADCAST_JX_REMIND_HIDE = 4;
    public static final String BROADCAST_ACTTYPE_CONNECT = "actiontype";
    public static final String BROADCAST_ACTION_CONNECT = "com.linkage.mobile72.sh.activity.manager.socketreceiver";
    
    public static final String CHOOSE_PIC_TOTAL = "choose_pic_total";
    public static final String CHOOSE_PIC_MAX = "choose_pic_max";
    
    public static final String MY_YOUDOU_URL = SERVER_PRO + "/ucenter/jifen/html5/toAddJifen";
	public static final String MY_EXPENSE_URL = SERVER_PRO + "/ucenter/consumption/html5/toMyConsumption";
	public static final String HELP_CENTER_URL = SERVER_PRO + "/ucenter/help/html5/toUserRule";
	public static final String YOUDOU_RULE = SERVER_PRO + "/ucenter/help/html5/toJifenHelp";
	public static final String SET_YHQY = SERVER_PRO + "/ucenter/help/html5/toUserTarriffy";
	
	//友盟统计自定义事件id
	public static final String CLICK_ADV = "clickAdv";
	public static final String CLICK_APP_OPEN = "clickAppOpen";
	public static final String CLICK_CHAT_SEND = "clickChatSend";
	public static final String CLICK_HOMEWORK_PIC = "clickHomeworkPic";
	public static final String CLICK_HOMEWORK_VOICE = "clickHomeworkVoice";
	public static final String CLICK_HOMEWORK_TEMPLATE = "clickHomeworkTemplate";
	public static final String CLICK_HOMEWORK_TIMING = "clickHomeworkTiming";
	public static final String CLICK_NOTICE_SEND = "clickNoticeSend";
    
    
//  public static final String APP_ID_Head = "1000";//测试
	public static final String APP_ID_Head = "1031";//上海正式
	public static final String APP_ID = APP_ID_Head + "_";//单聊
	public static final String APP_ID0 = APP_ID_Head;//群组
}
