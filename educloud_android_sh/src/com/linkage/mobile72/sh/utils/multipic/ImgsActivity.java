package com.linkage.mobile72.sh.utils.multipic;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

public class ImgsActivity extends BaseActivity implements OnClickListener {

    public static final String PIC_RESULT = "pic_result";
    Bundle bundle;
    FileTraversal fileTraversal;
    GridView imgGridView;
    ImgsAdapter imgsAdapter;
    LinearLayout select_layout;
    Utils util;
    RelativeLayout relativeLayout2;
    HashMap<Integer, ImageView> hashImage;
    Button choise_button;
    ArrayList<String> filelist;
    
    int size = 0, maxSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imgphotogrally);
        setTitle("选择图片");
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        Button commit = (Button)findViewById(R.id.set);
        commit.setVisibility(View.VISIBLE);
        commit.setText("提交");
        commit.setOnClickListener(this);
        imgGridView = (GridView) findViewById(R.id.gridView1);
        bundle = getIntent().getExtras();
        fileTraversal = bundle.getParcelable("data");
        ArrayList<String> l = bundle.getStringArrayList(ImgFileListActivity.RES);
        filelist = new ArrayList<String>();
        if(l != null) {
            filelist.addAll(l);
        }
        imgsAdapter = new ImgsAdapter(this, fileTraversal.filecontent, onItemClickClass);
        imgGridView.setAdapter(imgsAdapter);
        select_layout = (LinearLayout) findViewById(R.id.selected_image_layout);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        choise_button = (Button) findViewById(R.id.button3);
        hashImage = new HashMap<Integer, ImageView>();
//		imgGridView.setOnItemClickListener(this);
        util = new Utils(this);
        
        Intent intent = getIntent();
        if (null != intent)
        {
            size = intent.getIntExtra(Consts.CHOOSE_PIC_TOTAL, 0);
            maxSize = intent.getIntExtra(Consts.CHOOSE_PIC_MAX, 8);
        }
        
//        LogUtils.e("----list111-->size=" + size);
    }

    @SuppressLint("NewApi")
    public ImageView iconImage(String filepath, int index, CheckBox checkBox) throws FileNotFoundException {
        LayoutParams params = new LayoutParams(relativeLayout2.getMeasuredHeight() - 10, relativeLayout2.getMeasuredHeight() - 10);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(params);
        imageView.setBackgroundResource(R.drawable.imgbg);
        float alpha = 100;
        imageView.setAlpha(alpha);
        util.imgExcute(imageView, imgCallBack, filepath);
        imageView.setOnClickListener(new ImgOnclick(filepath, checkBox));
        return imageView;
    }

    ImgCallBack imgCallBack = new ImgCallBack() {
        @Override
        public void resultImgCall(ImageView imageView, Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    };

    class ImgOnclick implements OnClickListener {
        String filepath;
        CheckBox checkBox;

        public ImgOnclick(String filepath, CheckBox checkBox) {
            this.filepath = filepath;
            this.checkBox = checkBox;
        }

        @Override
        public void onClick(View arg0) {
            checkBox.setChecked(false);
            select_layout.removeView(arg0);
            choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
            filelist.remove(filepath);
        }
    }

    ImgsAdapter.OnItemClickClass onItemClickClass = new ImgsAdapter.OnItemClickClass() {
        @Override
        public void OnItemClick(View v, int Position, CheckBox checkBox) {
            LogUtils.e("OnItemClickClass:"+filelist.size()+"");
            String filapath = fileTraversal.filecontent.get(Position);
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
                select_layout.removeView(hashImage.get(Position));
                filelist.remove(filapath);
                choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
            } else {
                //if(filelist != null && filelist.size() >=8) {
                if(filelist != null && filelist.size()+size >= maxSize) {
                    if (size <= 0)
                    {
                        Toast.makeText(ImgsActivity.this,
                                "最多只能上传"+maxSize+"张图片",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String info = "最多只能上传"+maxSize+"张图片,已经选择" + size + "张,"
                                + "当前最多只能再选" + (maxSize - size) + "张";
                        Toast.makeText(ImgsActivity.this,
                                info,
                                Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                try {
                    checkBox.setChecked(true);
                    Log.i("img", "img choise position->" + Position);
                    ImageView imageView = iconImage(filapath, Position, checkBox);
                    if (imageView != null) {
                        hashImage.put(Position, imageView);
                        filelist.add(filapath);
                        select_layout.addView(imageView);
                        choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.set:
                if(null == filelist || filelist.isEmpty())
                {
                    Toast.makeText(ImgsActivity.this, "请选择图片!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, ImgFileListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(PIC_RESULT, filelist);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

}
