package com.linkage.mobile72.sh.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.Consts;

public class OTTPullActitvity extends BaseActivity implements OnClickListener {

    private final static String TAG = "OTTPullActitvity";
	private CheckBox recvImg;
	private Button back;

	
	//是否接受，1是接受，0是不接受
	private int smstype = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ott_pull);
		setTitle("接收设置");
		recvImg = (CheckBox) findViewById(R.id.recvImg);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		recvImg.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                setSmsType(((Checkable) v).isChecked());
            }
        });
		getSmsType();
	}
	
	
	private void getSmsType() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("commandtype", "getReceiveFlag");
//        params.put("smsmessagetype", String.valueOf(6));
        
       
        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_getReceiveFlag,
                Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        
                        System.out.println("response=" + response);
                        if (response.optInt("ret") == 0) {
                            if(response.optInt("flag") == 0)
                            {
                                smstype = 0;
                                recvImg.setChecked(false);
                            }
                            else if(response.optInt("flag") == 1){
                                smstype = 1;
                                recvImg.setChecked(true);
                            }
                        } else {
                            StatusUtils.handleStatus(response, OTTPullActitvity.this);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        StatusUtils.handleError(arg0, OTTPullActitvity.this);
                        
                    }
                });
        BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }
	
	private void setSmsType(boolean flag) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("commandtype", "settingsReceiveFlag");
        params.put("smsmessagetype", String.valueOf(6));
        params.put("flag", String.valueOf(flag == true ? 1 : 0));
        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_settingsReceiveFlag,
                Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        
                        System.out.println("response=" + response);
                        if (response.optInt("ret") == 0) {
                            T.showShort(OTTPullActitvity.this, "设置成功");
                            smstype = recvImg.isChecked() == true ? 1 : 0;
                        } else {
                            StatusUtils.handleStatus(response, OTTPullActitvity.this);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        StatusUtils.handleError(arg0, OTTPullActitvity.this);
                        
                    }
                });
        BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}
	
	@Override
    protected void onDestroy()
    {
        BaseApplication.getInstance().cancelPendingRequests(TAG);
        super.onDestroy();
    }

}
