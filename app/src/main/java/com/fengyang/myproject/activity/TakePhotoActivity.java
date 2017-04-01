package com.fengyang.myproject.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fengyang.myproject.R;
import com.fengyang.myproject.utils.StringUtils;

/**
 * 调用系统相机返回bitmap
 * Created by wuhuihui on 2017/4/1.
 */
public class TakePhotoActivity extends BaseActivity{

    private final int TAKE_PHOTO_CODE = 1;
    private Bitmap bitmap;
    private Button takePhoto;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        photo = (ImageView) findViewById(R.id.photo);
        takePhoto = (Button) findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){   //如果可用
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, TAKE_PHOTO_CODE);
                } else {
                    StringUtils.show1Toast(context, "SDCard不可用!");
                    finish();
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!= null) {
            if (requestCode == TAKE_PHOTO_CODE) {
                //两种方式 获取拍好的图片
                if (data.getData() != null || data.getExtras() != null) { //防止没有返回结果
                    Uri uri = data.getData();
                    if (uri != null) {
                        bitmap = BitmapFactory.decodeFile(uri.getPath()); //拿到图片
                    }
                    if (bitmap == null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            bitmap = (Bitmap) bundle.get("data");
                            photo.setImageBitmap(bitmap);
                        } else {
                            Toast.makeText(getApplicationContext(), "找不到图片", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //处理图片
                    //裁剪图片
                }
            }
        }
    }
}
