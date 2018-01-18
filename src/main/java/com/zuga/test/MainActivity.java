package com.zuga.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bug.test.R;
import com.zuga.bainu.BNApi;
import com.zuga.bainu.BNApiFactory;
import com.zuga.bainu.objects.BNImageObject;
import com.zuga.bainu.BNMediaMessage;
import com.zuga.bainu.BNSendRequest;
import com.zuga.bainu.objects.BNTextObject;
import com.zuga.bainu.objects.BNWebPageObject;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private int scene = BNSendRequest.SCENE_YARLQAA;
    private BNApi api;
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RadioButton rvAbdr = (RadioButton) findViewById(R.id.rb_abdr);
        RadioButton rvQomrlg = (RadioButton) findViewById(R.id.rb_qomrlg);
        RadioButton rvYarlqaa = (RadioButton) findViewById(R.id.rb_yarlqaa);
        rvAbdr.setOnClickListener(this);
        rvQomrlg.setOnClickListener(this);
        rvYarlqaa.setOnClickListener(this);
        tvLog = (TextView) findViewById(R.id.tv_log);
        Log.e(TAG, "file: " + getFilesDir());
        Log.e(TAG, "cache: " + getCacheDir());

        //获取api
        api = BNApiFactory.getBNApi();
    }

    /*文字分享*/
    public void TextShare(View view) {
        //bainu目前只能支持蒙科里编码书写的蒙古语,其他语言文字用Unicode（android默认）

        //1.创建文字对象（蒙语用蒙科里编码写）
        BNTextObject textObject = new BNTextObject();
        textObject.setText("mongolian text should be MenkCode!");

        //2.创建信息
        BNMediaMessage bnMediaMessage = new BNMediaMessage(textObject);
//        bnMediaMessage.setTitle("Text");

        //3.创建
        BNSendRequest bnSendRequest = new BNSendRequest();
        bnSendRequest.message = bnMediaMessage;
        bnSendRequest.scene = scene;
        //分享时:调试使用,返回错误信息
        //登录时:接受返回信息
        bnSendRequest.setRespListener(new BNSendRequest.RespListener() {
            /**
             * 调试时使用，当内容分享到Bainu出错时调用此方法。注意没有错误时不一定每次调用此方法，但是出错一定会调用此方法
             * @param errorType 错误类型，值为0没有错误;值为其他，出错
             * @param message errorType == 0时，返回成功信息.errorType != 0时，返回错误信息
             */
            @Override
            public void resp(int errorType, String message) {
                if (errorType != 0) {
                    tvLog.setText("ErrorType: " + errorType + "-----" + "message: " + message);
                } else {
                    tvLog.setText("success");
                }
            }
        });

        //4.发送
        if (api.isBainuInstalledOrLatestVersion()) {//如果没有判断bainu是否安装或最新版本，
            // 且确实手机没有装或跟新，自动转到bainu下载浏览器页面
            api.send(bnSendRequest);
        } else {
            Log.e(TAG, "bainuDownUri: " + api.getBainuDownUri());
        }

    }


    /*图片分享*/
    public void imageShare(View view) {
        getType();
    }


    /*网页分享*/
    public void webPageShare(View view) {

       /*
        * 分享的网页的Uri必须以 "http://" 或 "https://" 开头
        *
        * 设置网页的缩略图:Uri
        *   所略图的Uri必须以 "http://" 或 "https://" 开头
        * */


        //1.创建对象
        BNWebPageObject webObject = new BNWebPageObject();
        webObject.setWebUri(Uri.parse("http://www.zuga-tech.com"));

        //2.设置跟随信息
        BNMediaMessage message = new BNMediaMessage(webObject);
        message.setTitle("webPage title");//此项必须有
        message.setDescription("webPage description");//此项可以不设定
        //设定缩略图有两种方法
//        1）网络图片
        message.setThumbNetPicUri(Uri.parse("http://b.hiphotos.baidu.com/" +
                "zhidao/pic/item/f9dcd100baa1cd11a345a9b1bf12c8fcc2ce2db4.jpg"));

        //3.设置request
        BNSendRequest request = new BNSendRequest();
        request.message = message;
        request.scene = scene;
        request.setRespListener(new BNSendRequest.RespListener() {
            @Override
            public void resp(int errorType, String message) {
                if (errorType != 0) {
                    tvLog.setText("ErrorType: " + errorType + "-----" + "message: " + message);
                } else {
                    tvLog.setText("success");
                }
            }
        });

        //4.发送
        if (api.isBainuInstalledOrLatestVersion()) {//如果没有判断bainu是否安装或最新版本，
            // 且确实手机没有装或跟新，自动转到bainu下载浏览器页面
            api.send(request);
        } else {
            Log.e(TAG, "bainuDownUri: " + api.getBainuDownUri());
        }
    }

    /*bainu 登录*/
    public void login(View view) {
        BNSendRequest request = new BNSendRequest();
        request.isLogin = true;
        request.setRespListener(new BNSendRequest.RespListener() {
            /**
             * 登录成功或失败调用此方法
             * @param errorType 错误类型，值为0没有错误，值为其它出错
             * @param message errorType == 0返回code参数，errorType != 0返回错误信息
             */
            @Override
            public void resp(int errorType, String message) {
                if (errorType != 0) {
                    tvLog.setText("ErrorType: " + errorType + "-----" + "message: " + message);
                } else {
                    tvLog.setText("success" + "------" + "code: " + message);
                }
            }
        });

        //4.发送
        if (api.isBainuInstalledOrLatestVersion()) {//如果没有判断bainu是否安装或最新版本，
            // 且确实手机没有装或跟新，自动转到bainu下载浏览器页面
            api.send(request);
        } else {
            Log.e(TAG, "bainuDownUri: " + api.getBainuDownUri());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_abdr:
                scene = BNSendRequest.SCENE_ABDR;
                break;
            case R.id.rb_yarlqaa:
                scene = BNSendRequest.SCENE_YARLQAA;
                break;
            case R.id.rb_qomrlg:
                scene = BNSendRequest.SCENE_QOMRLG;
                break;
        }
    }

    private void getType() {
        new AlertDialog.Builder(this)
                .setMessage("你分享本地图片还是网络图片")
                .setPositiveButton("网络图片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sharePic(false);
                    }
                })
                .setNegativeButton("本地图片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sharePic(true);
                    }
                }).show();
    }

    private void sharePic(boolean isLocalPic) {
        /*
        * 两种方法进行图片分享:1.本地图片Uri,2.网络图片Uri
        *
        * 1.本地图片Uri必须以 "file://" 开头
        *
        * 2.网络图片Uri必须以 "http://" 或 "https://" 开头
        * */

        //1.创建对象
        BNImageObject imageObject = new BNImageObject();
        if (isLocalPic) {
            //1)设置本地图片Uri
            imageObject.setLocalImageUri(Uri.parse("file:///storage/emulated/0/DCIM/Camera/test.jpg"));
            Toast.makeText(this, "请到MainActivity的sharePic方法里修改本地图片", Toast.LENGTH_LONG).show();
        } else {
            //2)设置网络图片Uri
            imageObject.setNetImageUri(Uri.parse(
                    "http://b.hiphotos.baidu.com/zhidao/pic/item/f9dcd100baa1cd11a345a9b1bf12c8fcc2ce2db4.jpg"));
        }
        //2.设置跟随信息
        BNMediaMessage message = new BNMediaMessage(imageObject);
        message.setDescription("Tomcat picture");//此项可以不设定
        message.setTitle("picture");//此项可以不设定,但网页分享中必须设定

        //3.设置request
        BNSendRequest request = new BNSendRequest();
        request.message = message;
        request.scene = scene;
        request.setRespListener(new BNSendRequest.RespListener() {
            @Override
            public void resp(int errorType, String message) {
                if (errorType != 0) {
                    tvLog.setText("ErrorType: " + errorType + "-----" + "message: " + message);
                } else {
                    tvLog.setText("success");
                }
            }
        });

        //4.发送
        if (api.isBainuInstalledOrLatestVersion()) {//如果没有判断bainu是否安装或最新版本，
            // 且确实手机没有装或跟新，自动转到bainu下载浏览器页面
            api.send(request);
        } else {
            Log.e(TAG, "bainuDownUri: " + api.getBainuDownUri());
        }
    }
}


