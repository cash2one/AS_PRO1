package com.linkage.mobile72.sh.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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

public class WDJsonObjectMultipartRequest extends Request<JSONObject> {

	/** Charset */
	private static final String PROTOCOL_CHARSET = "utf-8";
	/** ContentType */
	private static final String PROTOCOL_CONTENT_TYPE = String.format(
			"multipart/form-data; boundary=%s", PROTOCOL_CHARSET);
	private MultipartEntity mParams;
	private final Listener<JSONObject> mListener;
	private Boolean mNeedAuth = true;

	public WDJsonObjectMultipartRequest(String requesturl, int method,
			List<ParamItem> params, Boolean needAuth,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, requesturl, errorListener);
		this.setRetryPolicy(new DefaultRetryPolicy(60000, 0, 1.0f));
		mListener = listener;
		mNeedAuth = needAuth;
		mParams = new MultipartEntity();
		try {
            String origin = "aa";
			mParams.addPart("origin", new StringBody(origin, Charset.forName(PROTOCOL_CHARSET)));
			for (int i = 0; i < params.size(); i++) {
				ParamItem pi = params.get(i);
				if (pi.getType() == ParamItem.TYPE_TEXT) {
					mParams.addPart(pi.getKey(), new StringBody(pi.getValue(),
							Charset.forName(PROTOCOL_CHARSET)));
					//L.i(pi.getKey() + ":" + pi.getValue());
				} else if (pi.getType() == ParamItem.TYPE_FILE) {
					if (TextUtils.isEmpty(pi.getValue())) {
						File kong = new File(BaseApplication.getInstance()
								.getWorkspace().getAbsoluteFile()
								+ "/temp");
						if (!kong.exists()) {
							kong.createNewFile();
						}
						FileBody file = new FileBody(kong);
						mParams.addPart(pi.getKey(), file);
					} else {
						FileBody file = new FileBody(new File(pi.getValue()));
						mParams.addPart(pi.getKey(), file);
					}
				}
			}
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
            //String extend = DesUtil.encrypt(sb.toString(), Consts.SECRET_KEY);
            String extend = Des3.encode(sb.toString());
            try {
                //LogUtils.i("sb.toString()=" + sb.toString());
                mParams.addPart("extend",
                        new StringBody(extend, Charset.forName(PROTOCOL_CHARSET)));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            params.add(new ParamItem("origin", origin, ParamItem.TYPE_TEXT));
            params.add(new ParamItem("extend", extend, ParamItem.TYPE_TEXT));
            String sig =  C.getSig(params);
            //L.e("sig", "sig = "+ sig);
            mParams.addPart("sig", new StringBody(sig, Charset.forName(PROTOCOL_CHARSET)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public RetryPolicy getRetryPolicy() {
		return new DefaultRetryPolicy(60000, 0, 1.0f);
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
            String respJson = Des3.decode(resp);
            //LogUtils.e("resp after", respJson);
            return Response.success(new JSONObject(respJson),
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
		return mParams.getContentType().getValue();
	}

	@Override
	public byte[] getBody() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			mParams.writeTo(bos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}
	
}