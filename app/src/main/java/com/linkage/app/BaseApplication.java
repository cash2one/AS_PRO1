package com.linkage.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

/**
 * Created by Yao on 2015/12/7.
 */
public class BaseApplication extends Application {

    public final String TAG = "BaseApplication";
    private RequestQueue mRequestQueue;
    private static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // mHttpClient = new DefaultHttpClient();
            // PoolingClientConnectionManager
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("添加请求至: %s", TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("添加请求至队列: %s", req.getUrl());
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        VolleyLog.d(tag.toString(), "从队列里移除请求");
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void cancelAllRequest() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    String reqUrl = request.getUrl();
                    if(reqUrl != null && reqUrl.startsWith(Consts.SERVER_IP)) {
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

}
