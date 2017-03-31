package com.fengyang.myproject.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fengyang.myproject.R;
import com.fengyang.myproject.Utils.FileUtils;
import com.fengyang.myproject.Utils.PermissionUtils;
import com.fengyang.myproject.Utils.StringUtils;

/**
 * 权限打开后显示手机联系人
 */
public class ImageActivity extends BaseActivity {

    private boolean isClicked = false;//按钮是否已点击标志
    private Button downloadImage;
    private ProgressBar progressBar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        downloadImage = (Button) findViewById(R.id.downloadImage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.imageView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        doDownload();//申请弹出获取联系人权限系统框后用户会选择允许或拒绝，弹出框消失，消失后会再次调用onResume方法
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
                    downloadImage.setOnClickListener(new View.OnClickListener() {
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
        String fileName = FileUtils.basePath + "image.jpg";
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

}
