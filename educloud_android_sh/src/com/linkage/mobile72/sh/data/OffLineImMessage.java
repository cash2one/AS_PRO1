package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.linkage.gson.annotations.SerializedName;

public class OffLineImMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int _id; // 主键,int类型,数据库建表时此字段会设为自增长
	
	public int get_id() {
		return _id;
	}
	public String position;
	public String uuid;
	
	@SerializedName("ImGroup")
	public OffLineIMGroup ImGroup;
	
	@SerializedName("ImMessage")
	public V2Message ImMessage;
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public OffLineIMGroup getImGroup() {
		return ImGroup;
	}

	public void setImGroup(OffLineIMGroup imGroup) {
		ImGroup = imGroup;
	}

	public V2Message getImMessage() {
		return ImMessage;
	}

	public void setImMessage(V2Message imMessage) {
		ImMessage = imMessage;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * 离线消息，ol服务器
	 */
	public static List<OffLineMessage> getOffLineMessage(String json)
	{
		// json=
		// "{\"result\": 200,\"position\": \"8\",\"resultMsg\": \"会话成功!\",\"offlineList\": [{\"id\": 602320,\"message\": {\"result\": 0,\"position\": 6,\"uuid\": \"050db0a3-3d4e-4091-a9ab-00c2a54ff58c\",\"ImMessage\": {\"fromUserId\": \"4767137154\",\"groupId\": 0,\"id\": 0,\"reserve1\": \"\",\"reserve2\": \"\",\"reserve3\": \"\",\"reserve4\": \"\",\"sendContents\": {\"title\": \"九月开学大堵车当第一课！\",\"content\": \"９月是很多中小学的开学季，大家都有准备，但水泄不通的大堵车却来的有些猝不及防。今天你被“堵”了吗？？点击http://139jy.cn/kxdc参与话题讨论！\",\"id\": 1852}, \"sendTime\": \"\",\"serverTime\": \"2014-09-04 14:31:29\",\"type\": \"1\"}},\"postion\": 6,\"reserve1\": \"\",\"reserve2\": \"2014-09-04 14:31:42\",\"userId\": \"4765305979\",\"uuid\": \"050db0a3-3d4e-4091-a9ab-00c2a54ff58c\"}]}";
		int n = 0;
		boolean isflag = false;
		if (json.contains("\\"))
		{
			isflag = true;
		}
		try
		{
			JSONObject jsonresponse = new JSONObject(json);
			JSONArray offListJsons = jsonresponse.getJSONArray("offlineList");
			List<OffLineMessage> message = new ArrayList<OffLineMessage>();
			String len = Integer.toString(offListJsons.length());
			System.out.print(len);
			for (int i = 0; i < offListJsons.length(); i++)
			{
				V2Message v2Message = new V2Message();
				OffLineMessage offLineMessage = new OffLineMessage();
				OffLineImMessage offLineImMessage = new OffLineImMessage();
				OffLineIMGroup offLineIMGroup = new OffLineIMGroup();
				JSONObject chatJson = offListJsons.getJSONObject(i);
				JSONObject msgJson = chatJson.getJSONObject("message");
				String position = msgJson.getString("position");
				offLineImMessage.setPosition(position);
				if (position.equalsIgnoreCase("1"))
				{// 1单聊，6群推
					JSONObject imGroupobject = msgJson.getJSONObject("ImGroup");
					offLineIMGroup.setCreateTime(imGroupobject.getString("createTime"));
					offLineIMGroup.setFromUserId(imGroupobject.getString("fromUserId"));
					offLineIMGroup.setFromUserName(imGroupobject.getString("fromUserName"));
					offLineIMGroup.setPersons(imGroupobject.getString("persons"));
					offLineIMGroup.setType(imGroupobject.getString("type"));
					offLineImMessage.setImGroup(offLineIMGroup);
				}
				JSONObject imMessageobject = msgJson.getJSONObject("ImMessage");
				if (isflag)
				{
					v2Message.setSendContents(imMessageobject.getString("sendContents"));
				} else
				{// 正常解析
					if (imMessageobject.getString("type").equals("1"))
					{// 群推和单聊文本
						JSONObject imMessage = imMessageobject.getJSONObject("sendContents");
						v2Message.setSendContents(imMessage.toString());
					} else
					// 音频和图片
					{
						v2Message.setSendContents(imMessageobject.getString("sendContents"));
					}
				}
				v2Message.setFromUserId(imMessageobject.getString("fromUserId"));
				v2Message.setType(imMessageobject.getString("type"));
				String sendTime = imMessageobject.getString("sendTime");
				String serverTime = imMessageobject.getString("serverTime");
				if ("".equalsIgnoreCase(sendTime) || sendTime == null)
				{
					if ("".equalsIgnoreCase(serverTime) || serverTime == null)
					{
						v2Message.setSendTime("2014-09-01 00:00:00");
					} else
					{
						v2Message.setSendTime(serverTime);
					}
				} else
				{
					v2Message.setSendTime(sendTime);
				}

				// 封装OffLineImMessage uuid/ImMessage
				offLineImMessage.setImMessage(v2Message);
				offLineImMessage.setUuid(chatJson.getString("uuid"));

				// 封装OffLineMessage message
				offLineMessage.setMessage(offLineImMessage);
				message.add(offLineMessage);

			}
			return message;
		} catch (JSONException e){
			// 解析5月份的数据
			try
			{
				OffLineMessage offLineMessage = new OffLineMessage();
				List<OffLineMessage> message = new ArrayList<OffLineMessage>();
				OffLineIMGroup offLineIMGroup = new OffLineIMGroup();
				OffLineImMessage offLineImMessage = new OffLineImMessage();
				JSONObject chatJson;
				V2Message v2Message = new V2Message();
				JSONObject jsonresponse = new JSONObject(json);
				JSONArray offListJsons = jsonresponse.getJSONArray("offlineList");

				chatJson = offListJsons.getJSONObject(n);
				JSONObject msgJson = chatJson.getJSONObject("message");
				String position = msgJson.getString("position");
				offLineImMessage.setPosition(position);
				if (position.equalsIgnoreCase("1"))
				{// 1单聊，6群推
					JSONObject imGroupobject = msgJson.getJSONObject("ImGroup");
					offLineIMGroup.setCreateTime(imGroupobject.getString("createTime"));
					offLineIMGroup.setFromUserId(imGroupobject.getString("fromUserId"));
					offLineIMGroup.setFromUserName(imGroupobject.getString("fromUserName"));
					offLineIMGroup.setPersons(imGroupobject.getString("persons"));
					offLineIMGroup.setPersons(imGroupobject.getString("type"));
				}
				JSONObject imMessageobject = msgJson.getJSONObject("ImMessage");
				v2Message.setSendContents(imMessageobject.getString("sendContents"));
				v2Message.setFromUserId(imMessageobject.getString("fromUserId"));
				v2Message.setType(imMessageobject.getString("type"));

				String sendTime = imMessageobject.getString("sendTime");
				String serverTime = imMessageobject.getString("serverTime");
				if ("".equalsIgnoreCase(sendTime) || sendTime == null)
				{
					if ("".equalsIgnoreCase(serverTime) || serverTime == null)
					{
						v2Message.setSendTime("2014-09-01 00:00:00");
					} else
					{
						v2Message.setSendTime(serverTime);
					}
				} else
				{
					v2Message.setSendTime(sendTime);
				}

				// 封装OffLineImMessage uuid/ImMessage
				offLineImMessage.setImMessage(v2Message);
				offLineImMessage.setUuid(chatJson.getString("uuid"));
				offLineImMessage.setImGroup(offLineIMGroup);
				// 封装OffLineMessage message
				offLineMessage.setMessage(offLineImMessage);
				message.add(offLineMessage);
				return message;
			} catch (JSONException ee)
			{
				ee.printStackTrace();
			}

			e.printStackTrace();
		}

		return null;
	}

	/**
	 * new在线接收消息
	 */
	public static OffLineImMessage getNewReceiveMessage(String json)
	{
		OffLineImMessage offLineImMessage = new OffLineImMessage();
		OffLineIMGroup offLineIMGroup = new OffLineIMGroup();
		V2Message v2Message = new V2Message();

		try
		{
			JSONObject jsonObject = new JSONObject(json);
			String position = jsonObject.getString("position");
			offLineImMessage.setPosition(position);
			//offLineImMessage.setUuid(jsonObject.getString("uuid"));
//			if (position.equalsIgnoreCase("1"))
//			{// 1单聊，6群推){
//				JSONObject ImGroupobject = jsonObject.getJSONObject("ImGroup");
//				offLineIMGroup.setCreateTime(ImGroupobject.getString("createTime"));
//				offLineIMGroup.setFromUserName(ImGroupobject.getString("fromUserName"));
//				offLineIMGroup.setPersons(ImGroupobject.getString("persons"));
//				offLineImMessage.setImGroup(offLineIMGroup);
//			}
			JSONObject ImMessageobject = jsonObject.getJSONObject("ImMessage");
			v2Message.setFromUserId(ImMessageobject.getString("fromUserId"));
			v2Message.setSendContents(ImMessageobject.getString("sendContents"));

			String sendTime = ImMessageobject.getString("sendTime");
			String serverTime = ImMessageobject.getString("serverTime");
			if ("".equalsIgnoreCase(sendTime) || sendTime == null)
			{
				if ("".equalsIgnoreCase(serverTime) || serverTime == null)
				{
					v2Message.setSendTime("2014-09-01 00:00:00");
				} else
				{
					v2Message.setSendTime(serverTime);
				}
			} else
			{
				v2Message.setSendTime(sendTime);
			}
			v2Message.setType(ImMessageobject.getString("type"));
			// 封装实体

			offLineImMessage.setImMessage(v2Message);

			return offLineImMessage;
		} catch (JSONException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
