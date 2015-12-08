package com.linkage.mobile72.sh.imol.xmppmanager;

import java.util.HashMap;
import java.util.UUID;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
//import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
//import org.jivesoftware.smackx.search.UserSearch;

import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.OLConfig;

import android.widget.Toast;

/**
 * 
 * XMPP服务器连接工具类.
 * 
 * @author shimiso
 */
public class XmppConnectionManager {

	public static int type = 0;
	public static int pushType = 0;
	private XMPPConnection connection;
	private static ConnectionConfiguration connectionConfig;
	private static XmppConnectionManager xmppConnectionManager;
    private HashMap<String,Boolean> hs=new HashMap<String,Boolean>();
	// private S
	private XmppConnectionManager() {

	}

	static {
		try {
			Class.forName("org.jivesoftware.smack.ReconnectionManager");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static XmppConnectionManager getInstance() {
		if (xmppConnectionManager == null) {
			xmppConnectionManager = new XmppConnectionManager();
		}
		return xmppConnectionManager;
	}

	// init
	public XMPPConnection init() {

		Connection.DEBUG_ENABLED = false;
		ProviderManager pm = ProviderManager.getInstance();
		configure(pm);

		/*
		 * 配置连接服务器的IP地址，端口号，域名 
		 */
		OLConfig olConfig = BaseApplication.getInstance().getOlConfig();
		if(olConfig!=null){
			String ip=olConfig.ol_pushserver_ip;
			int port= Integer.parseInt(String.valueOf(olConfig.ol_pushserver_port));
			String host_name=olConfig.ol_hostname;
			//connectionConfig = new ConnectionConfiguration(ip, port,"172.18.18.181");
			connectionConfig = new ConnectionConfiguration(ip, port,host_name);
		}
		connectionConfig.setSASLAuthenticationEnabled(false);// 不使用SASL验证，设置为false
		connectionConfig
				.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
		// 允许自动连接
		connectionConfig.setReconnectionAllowed(true);
		// 允许登录成功后更新在线状态
		connectionConfig.setSendPresence(true);
		// 收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
		Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

		return null;
	}

	/**
	 * 
	 * 返回一个有效的xmpp连接,如果无效则返回空.
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-4 下午6:54:31
	 */
	public XMPPConnection getConnection() {
		if (connection != null) {
			return connection;
		} else {
			connection = new XMPPConnection(connectionConfig);
			return connection;
		}
	}

	// private TaxiConnectionListener connectionListener;

	/**
	 * 登录
	 * 
	 * @param account
	 *            登录帐号
	 * @param password
	 *            登录密码
	 * @return
	 */
	public boolean login(String account, String password) {
		try {
			if (getConnection() == null)
				return false;
			getConnection().login(account, password);
			// 离线信息管理
			OfflineMessageManager offlineManager = new OfflineMessageManager(
					getConnection());

			// 删除离线消息
			offlineManager.deleteMessages();

			// 更改在綫狀態
			Presence presence = new Presence(Presence.Type.available);
			getConnection().sendPacket(presence);
			// 添加連接監聽
//			 connectionListener = new TaxiConnectionListener();
//			 getConnection().addConnectionListener(connectionListener);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * 销毁xmpp连接.
	 * 
	 * @author shimiso
	 * @update 2012-7-4 下午6:55:03
	 */
	public void disconnect() {
		if (connection != null) {
			connection.disconnect();
		}
	}

	public void configure(ProviderManager pm) {

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
		}

		// XHTML
//		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
//				new XHTMLExtensionProvider());

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());
		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());
		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());
		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());
		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());
		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());
		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());
		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());
		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
		}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());
		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
//		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());
		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

	}

	public void reconnect() {
		
	}
}
