package com.linkage.mobile72.sh.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.app.BaseApplication;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.http.ParamItem;
import com.linkage.mobile72.sh.http.WDJsonObjectMultipartRequest;
import com.linkage.mobile72.sh.utils.ProgressDialogUtils;
import com.linkage.mobile72.sh.utils.StatusUtils;
import com.linkage.mobile72.sh.utils.UIUtilities;
import com.linkage.mobile72.sh.Consts;

public class ApplyToTeacherActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = ApplyToTeacherActivity.class.getSimpleName();
	
	private Button back, submit;
	private File file;
	private ImageView imageView;
	private EditText editText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applyto_teacher);
		setTitle(R.string.title_account_apply_teacher);
		back = (Button)findViewById(R.id.back);
		submit = (Button)findViewById(R.id.set);
		submit.setBackgroundResource(getResources().getColor(R.color.transparent));
		submit.setVisibility(View.VISIBLE);
		submit.setTextColor(getResources().getColor(R.color.white));
		submit.setText("确认");
		imageView = (ImageView)findViewById(R.id.upload_certificate_photo);
		editText = (EditText)findViewById(R.id.desc);
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
		imageView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.upload_certificate_photo:
			showPickDialog();
			break;
		case R.id.set:
			String desc = editText.getText().toString();
			if(file != null) {
				submit(desc);
			}else {
				UIUtilities.showToast(this, "您还没有上传证件照片");
			}
			break;
		}
	}
	
	/**  
     * 选择提示对话框  
     */ 
    private void showPickDialog() {
        new AlertDialog.Builder(this)  
                .setTitle("设置头像...")  
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();
                        /**  
                         * 刚开始，我自己也不知道ACTION_PICK是干嘛的，后来直接看Intent源码，  
                         * 可以发现里面很多东西，Intent是个很强大的东西，大家一定仔细阅读下  
                         */ 
                        Intent intent = new Intent(Intent.ACTION_PICK, null);  
                          
                        /**  
                         * 下面这句话，与其它方式写是一样的效果，如果：  
                         * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  
                         * intent.setType(""image/*");设置数据类型  
                         * 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"  
                         * 这个地方小马有个疑问，希望高手解答下：就是这个数据URI与类型为什么要分两种形式来写呀？有什么区别？  
                         */ 
                        intent.setDataAndType(  
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  
                                "image/*");  
                        startActivityForResult(intent, 1);  
 
                    }  
                })  
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        dialog.dismiss();  
                        /**  
                         * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方  
                         * 文档，you_sdk_path/docs/guide/topics/media/camera.html  
                         * 我刚看的时候因为太长就认真看，其实是错的，这个里面有用的太多了，所以大家不要认为  
                         * 官方文档太长了就不看了，其实是错的，这个地方小马也错了，必须改正  
                         */  
                        Intent intent = new Intent(  
                                MediaStore.ACTION_IMAGE_CAPTURE);  
                        //下面这句指定调用相机拍照后的照片存储的路径  
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri  
                                .fromFile(new File(Environment  
                                        .getExternalStorageDirectory(),  
                                        "xiaoma.jpg")));  
                        startActivityForResult(intent, 2);  
                    }  
                }).show();  
    }  
 
    @Override 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        switch (requestCode) {  
        // 如果是直接从相册获取  
        case 1:  
        	if(data != null)
            startPhotoZoom(data.getData());  
            break;  
        // 如果是调用相机拍照时  
        case 2:  
            File temp = new File(Environment.getExternalStorageDirectory()  
                    + "/xiaoma.jpg");  
            startPhotoZoom(Uri.fromFile(temp));  
            break;  
        // 取得裁剪后的图片  
        case 3:  
            /**  
             * 非空判断大家一定要验证，如果不验证的话，  
             * 在剪裁之后如果发现不满意，要重新裁剪，丢弃  
             * 当前功能时，会报NullException，小马只  
             * 在这个地方加下，大家可以根据不同情况在合适的  
             * 地方做判断处理类似情况  
             *   
             */ 
            if(data != null){  
                setPicToView(data);  
            }  
            break;  
        default:  
            break;  
 
        }  
        super.onActivityResult(requestCode, resultCode, data);  
    }  
      
    /**  
     * 裁剪图片方法实现  
     * @param uri  
     */ 
    public void startPhotoZoom(Uri uri) {  
        /*  
         * 下面这个Intent的ACTION Look
         * yourself_sdk_path/docs/reference/android/content/Intent.html  
         * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,  
         * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么  
         * 制做的了...吼吼  
         */ 
        Intent intent = new Intent("com.android.camera.action.CROP");  
        intent.setDataAndType(uri, "image/*");  
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪  
        intent.putExtra("crop", "true");  
        // aspectX aspectY 是宽高的比例  
        intent.putExtra("aspectX", 1);  
        intent.putExtra("aspectY", 1);  
        // outputX outputY 是裁剪图片宽高  
        intent.putExtra("outputX", 150);  
        intent.putExtra("outputY", 150);  
        intent.putExtra("return-data", true);  
        startActivityForResult(intent, 3);  
    }  
      
    /**  
     * 保存裁剪之后的图片数据  
     * @param picdata  
     */  
    @SuppressWarnings("deprecation")
	private void setPicToView(Intent picdata) {  
        Bundle extras = picdata.getExtras();  
        if (extras != null) {  
            Bitmap photo = extras.getParcelable("data");  
            Drawable drawable = new BitmapDrawable(photo);  
              
            saveBitmapFile(photo);
            imageView.setImageDrawable(drawable);
        }  
    }
    
    public void saveBitmapFile(Bitmap bitmap){
        file = mApp.getUploadImageOutputFile();//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	public void submit(String desc) {
		ProgressDialogUtils.showProgressDialog("正在提交，请稍候", this, false);
    	
    	List<ParamItem> params = new ArrayList<ParamItem>();
    	params.add(new ParamItem("commandtype", "applyTeacher", ParamItem.TYPE_TEXT));
    	params.add(new ParamItem("desc", desc, ParamItem.TYPE_TEXT));
		if(file != null) {
			params.add(new ParamItem("fileupload", file, ParamItem.TYPE_FILE));
	    	WDJsonObjectMultipartRequest mRequest = new WDJsonObjectMultipartRequest(Consts.SERVER_URL, Request.Method.POST, params, true, new Response.Listener<JSONObject>() {
	    		@Override
	    		public void onResponse(JSONObject response) {
	    			ProgressDialogUtils.dismissProgressBar();
	    			System.out.println("response=" + response);
	    			if (response.optInt("ret") == 0) {
	    				AccountData currentAccount = BaseApplication.getInstance().getDefaultAccount();
	    				currentAccount.setUserType(-1);
	    				try {
	    					getDBHelper().getAccountDao().createOrUpdate(currentAccount);
	    					mApp.notifyAccountChanged();
						} catch (SQLException e) {
							e.printStackTrace();
							UIUtilities.showToast(ApplyToTeacherActivity.this, "申请教师失败");
						}
	    				imageLoader.clearDiscCache();
	    				imageLoader.clearMemoryCache();
	    				UIUtilities.showToast(ApplyToTeacherActivity.this, "申请教师成功");
	    				finish();
	    			}else {
	    				StatusUtils.handleStatus(response, ApplyToTeacherActivity.this);
	    			}
	    		}
	    	}, new Response.ErrorListener() {
	    		@Override
	    		public void onErrorResponse(VolleyError arg0) {
	    			ProgressDialogUtils.dismissProgressBar();
					StatusUtils.handleError(arg0, ApplyToTeacherActivity.this);
	    		}
	    	});
	    	BaseApplication.getInstance().addToRequestQueue(mRequest, TAG);
		}
	}
}
