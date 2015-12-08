package com.linkage.mobile72.sh.utils.multipic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.linkage.mobile72.sh.R;
import com.linkage.mobile72.sh.activity.CreateHomeworkActivity;
import com.linkage.mobile72.sh.app.BaseActivity;
import com.linkage.mobile72.sh.utils.Utils;
import com.linkage.mobile72.sh.Consts;
import com.linkage.lib.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImgFileListActivity extends BaseActivity implements OnItemClickListener,View.OnClickListener {

    private static final int REQUEST = 1;
    public static final String PIC_RESULT = "pic_result";
    public static final String RES = "res";
    ListView listView;
    Utils util;
    ImgFileListAdapter listAdapter;
    List<FileTraversal> locallist;
    private boolean append;
    private ArrayList<String> imglist;
    
    private int size = 0, maxSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imgfilelist);
        setTitle("选择目录");
        Button back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView1);
        util = new Utils(this);
        locallist = util.LocalImgFileList();
        List<HashMap<String, String>> listdata = new ArrayList<HashMap<String, String>>();
        Bitmap bitmap[] = null;
        if (locallist != null) {
            bitmap = new Bitmap[locallist.size()];
            for (int i = 0; i < locallist.size(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("filecount", locallist.get(i).filecontent.size() + "个文件");
                map.put("imgpath", locallist.get(i).filecontent.get(0) == null ? null : (locallist.get(i).filecontent.get(0)));
                map.put("filename", locallist.get(i).filename);
                listdata.add(map);
            }
        }
        listAdapter = new ImgFileListAdapter(this, listdata);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        append = getIntent().getBooleanExtra("append", false);
        imglist = getIntent().getStringArrayListExtra(RES);
        
        Intent intent = getIntent();
        if (null != intent)
        {
            size = intent.getIntExtra(Consts.CHOOSE_PIC_TOTAL, 0);
            maxSize = intent.getIntExtra(Consts.CHOOSE_PIC_MAX, 8);
            //LogUtils.e("----list-->size=" + size);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent(this, ImgsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", locallist.get(arg2));
        bundle.putStringArrayList(RES, imglist);
        intent.putExtras(bundle);
        intent.putExtra(Consts.CHOOSE_PIC_TOTAL, size);
        intent.putExtra(Consts.CHOOSE_PIC_MAX, maxSize);
        startActivityForResult(intent, REQUEST);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST:
                if (data != null) {
                    ArrayList<String> choosePic = data.getExtras().getStringArrayList(ImgsActivity.PIC_RESULT);
                    Intent intent = new Intent(this, CreateHomeworkActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("append", append);
                    bundle.putStringArrayList(PIC_RESULT, choosePic);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }
}
