package com.linkage.mobile72.sh.http;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.utils.C;
import com.linkage.mobile72.sh.utils.Des3;
import com.linkage.mobile72.sh.utils.Utilities;
import com.linkage.lib.util.LogUtils;

public class WDJsonObjectForChatRequest extends Request<JSONObject> {

	/** Charset */
	private static final String PROTOCOL_CHARSET = "utf-8";

	/** ContentType */
	private static final String PROTOCOL_CONTENT_TYPE = String.format(
			"application/x-www-form-urlencoded; charset=%s", PROTOCOL_CHARSET);

	/** 键值对形式参数 */
	private HashMap<String, String> mParams;
	private final Listener<JSONObject> mListener;
	private Boolean mNeedAuth = true;

	public WDJsonObjectForChatRequest(String requset_url, int method,
			HashMap<String, String> params, Boolean needAuth,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, requset_url, errorListener);
		this.setRetryPolicy(new DefaultRetryPolicy(45000, 0, 1.0f));
		mListener = listener;
		mParams = params;
		mNeedAuth = needAuth;
		try {
		StringBuilder sb = new StringBuilder();
		if(mNeedAuth) {
			String accessToken = BaseApplication.getInstance().getAccessToken();
			//L.e("accessToken", "accessToken = "+ accessToken);
			if(TextUtils.isEmpty(accessToken)) {
				throw new IllegalStateException("need an accessToken, but now is null");
			}
			sb.append(accessToken);
		}
		sb.append(",");
		sb.append(Utilities.formatNow(null));
		sb.append(",");
		sb.append(Utilities.randomLong());
		
		
			//LogUtils.e("extend", "sb.toString()=" + sb.toString());
			//String extend = DesUtil.encrypt(sb.toString(), Consts.SECRET_KEY);
			String extend = Des3.encode(sb.toString());
			mParams.put("extend", extend);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        mParams.put("origin", "aa");
        mParams.put("sig", C.getSig(mParams));
		for (String key : mParams.keySet()) {
			LogUtils.w(key+":"+ mParams.get(key));
		}
	}

	
	@Override
	public RetryPolicy getRetryPolicy() {
		return new DefaultRetryPolicy(45000, 0, 1.0f);
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		mListener.onResponse(response);
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String resp = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			//LogUtils.e("resp before", resp);
            /*String p = Environment.getExternalStorageDirectory() + "/abcd";
            File f = new File(p);
            if(!f.exists()) {
                f.mkdirs();
            }
            File file = new File(f, "log.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            PrintWriter pw=new PrintWriter(new FileWriter(file));
            pw.print(resp);
            pw.flush();
            pw.close();
            LogUtils.e("resp", resp);*/
//            String respJson = Des3.decode(resp);
//            LogUtils.e("resp after", respJson);
			return Response.success(new JSONObject(resp),
					HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		} catch (Exception e) {
            return Response.error(new ParseError(e));
        }
	}

	@Override
	public String getBodyContentType() {
		return PROTOCOL_CONTENT_TYPE;
	}

	@Override
	public byte[] getBody() {
		return encodeParameters(mParams, PROTOCOL_CHARSET);
	}
}