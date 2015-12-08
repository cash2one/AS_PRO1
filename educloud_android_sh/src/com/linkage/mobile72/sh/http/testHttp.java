package com.linkage.mobile72.sh.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.Utilities;
import com.linkage.mobile72.sh.Consts;

public class testHttp {
	
    private static final String TAG = testHttp.class.getSimpleName();
    private Context context;
		
	//��ȡ��֤��
	private void getSMSCode(String account,String smsType )
	{
		try {
			ProgressDialogUtils.showProgressDialog("��ʾ��", context, false);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("account", account);
			params.put("smsType", smsType);
		
			WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_GetSMSCode, Request.Method.POST, params, false, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					ProgressDialogUtils.dismissProgressBar();

					System.out.println("response=" + response);
					if (response.optInt("ret") == 0) {
						// TODO ��¼�ɹ�����ʺŸ��µ�
				    
//							onLoginSuccess( user);
					} 
					else {
					
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleStatus(response, context);
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					
					ProgressDialogUtils.dismissProgressBar();
					StatusUtils.handleError(arg0, context);
				}
			});
			BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	
	//δע��������У��¼���û���������
		private void SERVER_EnteringPerson_Register(String account,String password ,String smsCode)
		{
			try {
				ProgressDialogUtils.showProgressDialog("��ʾ��", context, false);
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("account", account);
				params.put("password", password);
				params.put("smsCode", smsCode);
				String IMEI = Utilities.getIMEI(context);
				if (IMEI == null) {
					IMEI = android.os.Build.MODEL;
				}
				if (IMEI == null) {
					IMEI = "";
				}
				params.put("term_manufacturer", "android,"+IMEI);
				WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_EnteringPerson_Register, Request.Method.POST, params, false, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						ProgressDialogUtils.dismissProgressBar();

						System.out.println("response=" + response);
						if (response.optInt("ret") == 0) {
							// TODO ��¼�ɹ�����ʺŸ��µ�
					        AccountData user = AccountData.parseFromJson(response.optJSONObject("data"));					
							if(user ==null || "".equalsIgnoreCase(user.getToken()))
							{
								StatusUtils.handleOtherError("��¼ʧ�ܣ�userInfoΪ��", context);
								return;
							}		
//								Success( user);
						} 
						else {
						
							ProgressDialogUtils.dismissProgressBar();
							StatusUtils.handleStatus(response, context);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						
						ProgressDialogUtils.dismissProgressBar();
						StatusUtils.handleError(arg0, context);
					}
				});
				BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		
		//ע��
				private void SERVER_Register(String account,String password ,String nickname)
				{
					try {
						ProgressDialogUtils.showProgressDialog("��ʾ��", context, false);
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("account", account);
						params.put("password", password);
						params.put("nickname", nickname);
						String IMEI = Utilities.getIMEI(context);
						if (IMEI == null) {
							IMEI = android.os.Build.MODEL;
						}
						if (IMEI == null) {
							IMEI = "";
						}
						params.put("term_manufacturer", "android,"+IMEI);
						WDJsonObjectRequest mRequest = new WDJsonObjectRequest(Consts.SERVER_Register, Request.Method.POST, params, false, new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								ProgressDialogUtils.dismissProgressBar();

								System.out.println("response=" + response);
								if (response.optInt("ret") == 0) {
									// TODO ��¼�ɹ�����ʺŸ��µ�
							        AccountData user = AccountData.parseFromJson(response.optJSONObject("data"));					
									if(user ==null || "".equalsIgnoreCase(user.getToken()))
									{
										StatusUtils.handleOtherError("��¼ʧ�ܣ�userInfoΪ��", context);
										return;
									}		
//										Success( user);
								} 
								else {
								
									ProgressDialogUtils.dismissProgressBar();
									StatusUtils.handleStatus(response, context);
								}
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
								
								ProgressDialogUtils.dismissProgressBar();
								StatusUtils.handleError(arg0, context);
							}
						});
						BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
				
				//�޸�����ͷ��ӿ�
				private void SERVER_SetPerson_Info(String avatar,String nickName)
				{
					try {
						ProgressDialogUtils.showProgressDialog("��ʾ��", context, false);
						List<ParamItem> params = new ArrayList<ParamItem>();
						
						params.add(new ParamItem("nickName", nickName, ParamItem.TYPE_TEXT));
						params.add(new ParamItem("avatar",avatar, ParamItem.TYPE_FILE));
					
						WDJsonObjectMultipartRequest mRequest = new WDJsonObjectMultipartRequest(Consts.SERVER_SetPerson_Info, Request.Method.POST, params, true,
								new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								ProgressDialogUtils.dismissProgressBar();

								System.out.println("response=" + response);
								if (response.optInt("ret") == 0) {
									// TODO ��¼�ɹ�����ʺŸ��µ�
							    
//										onLoginSuccess( user);
								} 
								else {
								
									ProgressDialogUtils.dismissProgressBar();
									StatusUtils.handleStatus(response, context);
								}
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
								
								ProgressDialogUtils.dismissProgressBar();
								StatusUtils.handleError(arg0, context);
							}
						});
						BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
		
}
