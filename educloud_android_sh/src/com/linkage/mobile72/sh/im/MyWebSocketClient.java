package com.linkage.mobile72.sh.im;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import com.linkage.lib.util.LogUtils;

public class MyWebSocketClient extends WebSocketClient {
	
	public static interface WebSocketClientListener {
		void onClose(int arg0, String arg1, boolean arg2);
		void onError(Exception arg0);
		void onMessage(String arg0);
		void onOpen(ServerHandshake arg0);
	}
	
	private WebSocketClientListener mListener;
	
	public MyWebSocketClient(String url) throws URISyntaxException {
		super(new URI(url), new Draft_17());
		LogUtils.e("***MyWebSocketClient***url:" + url);
	}
	public MyWebSocketClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}
	
	public void setWebSocketClientListener(WebSocketClientListener listener) {
		mListener = listener;
	}
	
	public void removeListener() {
		mListener = null;
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		super.onClosing(code, reason, remote);
		if(mListener != null) {
			mListener.onClose(code, reason, remote);
		}
	}
	
	@Override
	public void onError(Exception arg0) {
		if(mListener != null) { 
			mListener.onError(arg0);
		}
	}

	@Override
	public void onMessage(String arg0) {
		if(mListener != null) {
			mListener.onMessage(arg0);
		}
	}
	
	@Override
	public void onOpen(ServerHandshake arg0) {
		if(mListener != null) {
			mListener.onOpen(arg0);
		}
	}
}
