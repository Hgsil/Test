package com.hgsil.android.gank;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Pictures> mPictures = new ArrayList<>();
    MyAdapter mAdapter;
    Context mContent;
    RecyclerView mRecyclerView;
    SharedPreferences mSharedPreferences ;
    TextView setPermission;
    int i = 0;
    boolean havePermission = false;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    mAdapter = new MyAdapter(mPictures,mContent);
                    mAdapter.setHavePermission(havePermission);
                    mRecyclerView.setAdapter(mAdapter);
                    mPictures.clear();
                    break;
                case 2:
                    mAdapter.addPictures(mPictures);
                    mPictures.clear();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycle_main);
        setPermission = (TextView)findViewById(R.id.getermission_main);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mSharedPreferences = getSharedPreferences("PictureData", MODE_PRIVATE);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState ==RecyclerView.SCROLL_STATE_IDLE &&
                        lastVisibleItem+ 1 == mAdapter.getItemCount()){
                        getInformation(false);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        setPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    havePermission = true;
                    mAdapter.setHavePermission(havePermission);
                }
            }
        });
        mContent = this;
        getInformation(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    havePermission = true;
                    mAdapter.setHavePermission(havePermission);
                }else{
                    Toast.makeText(this,"无SD卡写入权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    public void getInformation(boolean isFirst){
        final boolean isFirstTime = isFirst;
        new Thread(new Runnable() {
            @Override
            public void run() {
                 {
                    try {
                        String response = HttpUtil.get("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/0/0");
                        JSONObject jsonObject = new JSONObject(response);
                        Message message = new Message();
                        //第一次加载
                        if (isFirstTime){
                            if (!jsonObject.getBoolean("error")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("results");
                                for (; i < 10; i++) {
                                    setPicture(jsonArray.getJSONObject(i), mPictures);
                                }
                                message.what = 1;
                                mHandler.sendMessage(message);
                            }
                            else {
                                Looper.prepare();
                                Toast.makeText(MainActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                        //非第一次加载
                        else {
                            int time = 0;
                            if (!jsonObject.getBoolean("error")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("results");
                                while (time<10){
                                    i++;
                                    setPicture(jsonArray.getJSONObject(i), mPictures);
                                    time++;
                                }
                                message.what = 2;
                                mHandler.sendMessage(message);
                            }
                            else {
                                Looper.prepare();
                                Toast.makeText(MainActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();


    }
    public static void setPicture(JSONObject jsonObject,List<Pictures> picturesList){
        Pictures onePicture = new Pictures();
        try {
            onePicture.setId(jsonObject.getString("_id"));
            onePicture.setCreatedAt(jsonObject.getString("createdAt"));
            onePicture.setPublishedAt(jsonObject.getString("publishedAt"));
            onePicture.setUrl(jsonObject.getString("url"));
            onePicture.setWho(jsonObject.getString("who"));
        }catch (Exception e){
            e.printStackTrace();
        }
        picturesList.add(onePicture);
    }
}
