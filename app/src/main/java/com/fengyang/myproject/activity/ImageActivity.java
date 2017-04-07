package com.fengyang.myproject.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fengyang.myproject.R;
import com.fengyang.myproject.utils.FileUtils;
import com.fengyang.myproject.utils.PermissionUtils;
import com.fengyang.myproject.utils.StringUtils;

import java.io.File;

/**
 * 权限打开后下载显示图片
 */
public class ImageActivity extends BaseActivity {

    private Button loadImage;
    private ProgressBar progressBar;
    private ImageView imageView;
    private final int TAKE_PHOTO_CODE = 1;
    private Bitmap bitmap;
    private boolean isClicked, isDownload, isSucessful;//按钮是否已点击标志,下载页面标志，相机权限获取成功标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("图片处理", R.layout.activity_image);
        loadImage = (Button) findViewById(R.id.loadImage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.imageView);

        isDownload = getIntent().getBooleanExtra("isDownload", false);
        if (isDownload) loadImage.setText("下载图片");
        else loadImage.setText("拍照");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isDownload) {
            doDownload();//申请弹出获取联系人权限系统框后用户会选择允许或拒绝，弹出框消失，消失后会再次调用onResume方法
        } else {
            //避免成功获取权限返回后重复调用相机加判断,仅当不成功时再次调用权限判断
            if (! isSucessful) takePhoto();
        }
    }

    /**
     * 下载图片并显示
     * 注意:避免用户拒绝访问权限时出现无限循环的系统框弹出
     */
    private void doDownload() {
        PermissionUtils.checkSDcardPermission(ImageActivity.this, new PermissionUtils.OnCheckCallback() {
            @Override
            public void onCheck(boolean isSucess) {
                if (isClicked) {
                    if (isSucess) {
                        getSDCardSucessed();
                    } else {//也要考虑某些手机（比如vivo，oppo）自动禁止权限的问题
                        StringUtils.show1Toast(context, "可能读取SDCard权限未打开，请检查后重试！");
                    }
                } else {//设置按钮的点击事件，进一步判断权限的获取或跳转指定界面
                    loadImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionUtils.checkSDcardPermission(ImageActivity.this, new PermissionUtils.OnCheckCallback() {
                                @Override
                                public void onCheck(final boolean isSucess) {
                                    if (isSucess) {
                                        getSDCardSucessed();
                                    } else {
                                        //权限获取失败后再次弹出系统框，将按钮的点击跳转标志设为true,保证用户点击“允许”后可直接跳转指定界面
                                        getSDCardFailed();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    /**
     * 获取SD卡权限成功的情况
     * 权限获取成功后，如果跳转按钮已点击则直接下载图片，并将标志还原
     */
    private void getSDCardSucessed() {
        isClicked = false;
        progressBar.setVisibility(View.VISIBLE);
        String url = "http://pic.qiantucdn.com/58pic/10/96/99/18u58PICXCS.jpg";
        String fileName = FileUtils.dirPath + "image.jpg";
        FileUtils.downLoadImage(url, fileName, new FileUtils.onSucessCallback() {
            @Override
            public void onSucess(Bitmap bitmap) {
                if (bitmap != null) {
                    //不可在线程中刷新UI，需借用handler刷新，Message.obtain()
                    Message message = Message.obtain();
                    message.obj = bitmap;
                    handler.sendMessage(message);
                }
            }
        });
    }

    //下载完成后刷新UI
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            if (bitmap != null) {
                progressBar.setVisibility(View.GONE);
                imageView.setImageBitmap(bitmap);
            }
        }
    };

    /**
     * 获取SDCard权限失败的情况
     */
    private void getSDCardFailed() {
        isClicked = true;
        PermissionUtils.notPermission(ImageActivity.this, PermissionUtils.PERMISSIONS_STORAGE);
    }



    /******************************************************************************
     * 获取权限，调用相机
    /******************************************************************************

    /**
     * 获取权限，调用相机
     */
    private void takePhoto() {
        //申请弹出获取联系人权限系统框后用户会选择允许或拒绝，弹出框消失，消失后会再次调用onResume方法
        PermissionUtils.checkCameraPermission(ImageActivity.this, new PermissionUtils.OnCheckCallback() {
            @Override
            public void onCheck(boolean isSucess) {
                if (isClicked) {
                    if (isSucess) {
                        //权限获取成功后，如果跳转按钮已点击则将标志还原,成功标志为true,为避免onReume时不重复调用相机
                        isSucessful = true;
                        isClicked = false;
                    }
                    //TODO 由于测试时发现该提示老出现，体验不好将其注释
//                    else {//也要考虑某些手机（比如vivo，oppo）自动禁止权限的问题
//                        StringUtils.show1Toast(context, "可能读取相机权限未打开，请检查后重试！");
//                    }
                } else {//设置按钮的点击事件，进一步判断权限的获取或跳转指定界面
                    loadImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionUtils.checkCameraPermission(ImageActivity.this, new PermissionUtils.OnCheckCallback() {
                                @Override
                                public void onCheck(final boolean isSucess) {
                                    if (isSucess) {
                                        isSucessful = true;
                                        isClicked = false;
                                    } else {
                                        //权限获取失败后再次弹出系统框，将按钮的点击跳转标志设为true,保证用户点击“允许”后可直接跳转指定界面
                                        isClicked = true;
                                        PermissionUtils.notPermission(ImageActivity.this, PermissionUtils.PERMISSIONS_CAMERA);
                                        isSucessful = true;
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && requestCode == TAKE_PHOTO_CODE) {
                File file = new File(FileUtils.dirPath + "camera.jpg");
                if(file.exists()) {
                    imageView.setImageBitmap(FileUtils.readImage(FileUtils.dirPath + "camera.jpg"));
                }
            }

    }


}
