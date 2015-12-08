package com.linkage.mobile72.sh.im.bean;

import org.json.JSONException;
import org.json.JSONObject;

import com.linkage.mobile72.sh.app.BaseApplication;

public class SendAction extends Action {

	private static final String TEXT = "txt";
	private static final String PIC = "picture";
	private static final String AUDIO = "audio";

	private static final String HOMEWORK = "homework";

	private static final String NOTICE = "notice";

	public static class To extends MessageOut {

		public static final String TYPE_PERSON = "person";
		public static final String TYPE_GROUP = "group";

		To(long id) {
			put("id", id);
//			put("id", "100001");
			put("name", "");
			put("type", TYPE_PERSON);
		}

		To(long id, String type) {
			put("id", id);
//			put("id", "100001");
			put("name", "");
			put("type", type);
		}

	}

	public SendAction(To to, String content, String contentType, String name) {
		try {
			put(KEY_ACTION, VALUE_SEND);
			put("to", new JSONObject(to.toJson()));
			put("msg_type", "");
			put("content", content + ";" + System.currentTimeMillis());
			put("content_type", contentType);
			put("name", name);
			put("usertype",
					String.valueOf(BaseApplication.getInstance().getDefaultAccount().getUserType()));
			// put("name",
			// SchoolApp.getInstance().getAccountManager().getAccountFromdb().getUserName());
			// put("usertype", String.valueOf(senderUserType));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static String sendText(long toId, String content) {
		return new SendAction(new To(toId), content, TEXT, BaseApplication.getInstance().getDefaultAccount().getLoginname()).toJson();
	}

	public static String sendTextToGroup(long toId, String content, String name) {
		return new SendAction(new To(toId, To.TYPE_GROUP), content, TEXT, name)
				.toJson();
	}

	public static String sendPicture(long toId, String content) {
		return new SendAction(new To(toId), content, PIC, BaseApplication.getInstance().getDefaultAccount()
				.getLoginname()).toJson();
	}

	public static String sendPictureToGroup(long toId, String content,
			String name) {
		return new SendAction(new To(toId, To.TYPE_GROUP), content, PIC, name)
				.toJson();
	}

	public static String sendAudio(long toId, String content) {
		return new SendAction(new To(toId), content, AUDIO, BaseApplication.getInstance().getDefaultAccount()
				.getLoginname()).toJson();
	}

	public static String sendAudioToGroup(long toId, String content, String name) {
		return new SendAction(new To(toId, To.TYPE_GROUP), content, AUDIO, name)
				.toJson();
	}

	public static String sendHome(long toId, String content) {
		return new SendAction(new To(toId), content, HOMEWORK, BaseApplication.getInstance().getDefaultAccount()
				.getLoginname()).toJson();
	}

	public static String sendHomeToGroup(long toId, String content, String name) {
		return new SendAction(new To(toId, To.TYPE_GROUP), content, HOMEWORK,
				name).toJson();
	}

	public static String sendNotice(long toId, String content) {
		return new SendAction(new To(toId), content, NOTICE, BaseApplication.getInstance().getDefaultAccount()
				.getLoginname()).toJson();
	}

	public static String sendNoticeTOGroup(long toId, String content,
			String name) {
		return new SendAction(new To(toId, To.TYPE_GROUP), content, NOTICE,
				name).toJson();
	}

}
