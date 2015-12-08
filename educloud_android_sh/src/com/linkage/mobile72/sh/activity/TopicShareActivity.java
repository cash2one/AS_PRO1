package com.linkage.mobile72.sh.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.Group;
import com.linkage.mobile72.sh.http.WDJsonObjectRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.StringUtils;
import com.linkage.mobile72.sh.utils.T;
import com.linkage.mobile72.sh.Consts;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * @author wl
 *话题分享到班级空间的界面
 */
public class TopicShareActivity extends BaseActivity implements OnClickListener
{
    private static final String TAG = TopicShareActivity.class.getSimpleName();
    
    private static final int REQUEST_RECEIVER = 1;
    
    public static final String ID = "id";
    public static final String PICURL = "picurl";
    public static final String TITLE = "title";
    public static final String DURL = "durl";
    public static final String TYPE = "type"; 
    
    private Button back;
    
    /**
     * 右上角发布按钮
     */
    private Button set;
    
//    private Topic curTopicInfo;
    
    /**
     * 选择班级
     */
//    private TextView selectedBtn;
    
    private RelativeLayout relativelayout3;
    
    /**
     * 全部班级
     */
    private TextView allBtn;
    
    private EditText editText;
    
    private TextView shareInputTip;
    
    private ImageView shareIcon;
    
    private TextView shareTitle;
    
    private ArrayList<Group> chooseReceivers;
    
    private String classIds;
    
    private String id,picurl,title,durl; 
    
    private int type;
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra(ID);
        picurl = getIntent().getStringExtra(PICURL);
        title = getIntent().getStringExtra(TITLE);
        durl = getIntent().getStringExtra(DURL);
        type = getIntent().getIntExtra(TYPE,1);
        
        setContentView(R.layout.activity_topicshare);
        
        setTitle("分享");
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        set = (Button) findViewById(R.id.set);
        set.setVisibility(View.VISIBLE);
        set.setText("发布");
        set.setOnClickListener(this);
        
        shareInputTip = (TextView) findViewById(R.id.share_input_tip);
        shareInputTip.setText(getResources().getString(R.string.share_input_tip,
                250));
        
//        selectedBtn = (TextView) findViewById(R.id.select_class_btn);
        allBtn = (TextView) findViewById(R.id.all_class_btn);
//        selectedBtn.setOnClickListener(this);
        allBtn.setOnClickListener(this);
        
        relativelayout3 = (RelativeLayout) findViewById(R.id.relativelayout3);
        relativelayout3.setOnClickListener(this);
        
        editText = (EditText) findViewById(R.id.share_comment_edit);
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {
                shareInputTip.setText(getResources().getString(R.string.share_input_tip,
                        250 - editText.getText().toString().length()));
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {
            }
            
            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });
        
        DisplayImageOptions defaultOptions_group = new DisplayImageOptions.Builder().cacheOnDisc()
                .showStubImage(R.drawable.default_today_topic_item)
                .showImageForEmptyUri(R.drawable.default_today_topic_item)
                .showImageOnFail(R.drawable.default_today_topic_item)
                .build();
        
        shareIcon = (ImageView) findViewById(R.id.share_icon);
        imageLoader_group.displayImage(Consts.SERVER_HOST
                + picurl,
                shareIcon,
                defaultOptions_group);
        shareTitle = (TextView) findViewById(R.id.share_title);
        shareTitle.setText(title);
    }
    
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case R.id.back:
                finish();
                break;
            case R.id.set:
                shareTopicToclass();
                break;
//            case R.id.select_class_btn:
//                showShartTypeDialog();
//                break;
            case R.id.all_class_btn:
            case R.id.relativelayout3:
                Intent receiverIntent = new Intent(this, SelectClassActivity.class);
              startActivityForResult(receiverIntent, REQUEST_RECEIVER);
                break;
        }
    }
    
    
    
    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            final Intent data) {

        switch (requestCode) {
            case REQUEST_RECEIVER:
                if (data != null) {
                    chooseReceivers = (ArrayList<Group>) data.getExtras().getSerializable(
                            SelectReceiverActivity.RECEIVER_RESULT);
                    classIds = (getCaptioByChooseReceiver(chooseReceivers));
                    Log.d(TAG, "onActivityResult | classIds == "+classIds);
                    if(!StringUtils.isEmpty(classIds)){
                    	allBtn.setText(getNameByChooseReceiver(chooseReceivers));
                    }
                }
                break;
        }
    }
    
 // 获取选择的接收人的显示文本
    private String getCaptioByChooseReceiver(ArrayList<Group> groups) {
        if (groups != null && groups.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (Group group : groups) {
                if (group.isChecked()) {
                    sb.append(group.getId());
                    sb.append(",");
                } 
            }
            if (sb.length() > 0) {
                if (',' == (sb.charAt(sb.length() - 1))) {
                    sb = sb.deleteCharAt(sb.length() - 1);
                }
            }
            return sb.toString();
        }
        return "";
    }
    
 // 获取选择的接收人的显示文本
    private String getNameByChooseReceiver(ArrayList<Group> groups) {
        if (groups != null && groups.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (Group group : groups) {
                if (group.isChecked()) {
                    sb.append(group.getName());
                    sb.append(",");
                } 
            }
            if (sb.length() > 0) {
                if (',' == (sb.charAt(sb.length() - 1))) {
                    sb = sb.deleteCharAt(sb.length() - 1);
                }
            }
            return sb.toString();
        }
        return "";
    }
    
    private void shareTopicToclass()
    {
        /*if(StringUtils.isEmpty(editText.getText().toString().trim()))
        {
            T.showShort(TopicShareActivity.this, "请写下分享的内容");
            return ;
        }*/
        if (TextUtils.isEmpty(classIds)) {
            T.showShort(this, "请选择发送范围");
            return;
        }
        ProgressDialogUtils.showProgressDialog("", this, true);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("commandtype", "shareToClass");
        params.put("id", id);
        //studentId √ long 分享的学生id，教师时为0
        params.put("studentId", "0");
        params.put("picurl", picurl);
        params.put("title", title);
        params.put("sub_title", "");
        params.put("content", editText.getText().toString().trim());
        // url String  分享的链接
        params.put("url", durl);
        params.put("classIds", classIds);
        // type  √  int  1:话题  2: 活动 
        params.put("type", String.valueOf(type));
        WDJsonObjectRequest mRequest = new WDJsonObjectRequest(
                Consts.SERVER_shareToClass, Request.Method.POST, params, true,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        ProgressDialogUtils.dismissProgressBar();
                        System.out.println("response=  " + response);
                        
                        if (response.optInt("ret") == 0)
                        {
                            T.showShort(TopicShareActivity.this, "分享成功");
                            finish();
                        }
                        else
                        {
                            StatusUtils.handleStatus(response,
                                    TopicShareActivity.this);
                        }
                        
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError arg0)
                    {
                        ProgressDialogUtils.dismissProgressBar();
                        StatusUtils.handleError(arg0, TopicShareActivity.this);
                    }
                });
        BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
        
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        BaseApplication.getInstance().cancelPendingRequests(TAG);
    }
}
