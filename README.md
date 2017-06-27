# bainuSdk to user

## 一、简介

本文为Bainu Android终端SDK使用教程，只涉及教授SDK的使用方法，默认读者已经熟悉AndroidStudio(推荐)和eclipse开发工具的基本使用方法，以及具有一定的编程知识基础等。

**本文只给出用Android Studio开发环境sdk配置的介绍（demo 用Android Studio编写），eclipse用户参考Android studio版**

## 二、 注册获取资源

目前Bainu开放平台不对外公开注册，不过原则上不会拒绝符合法律法规的应用开发者。请发送邮件到 business@zuga-tech.com，附上您应用的：

- **蒙文名称**
- **英文名称**
- **iOS Bundle ID**
- **Android 包名**
- **应用Logo**（640*640）
- **官网地址**
- **AppStore下载地址**
- **安卓下载地址**（非apk地址）
- **蒙文介绍**

我们审核通过之后将会发回接入所需的APP_ID，APP_SECRET，SDK和Demo。


- **测试APP_ID：** bn0428040730
- **测试APP_SECRET：** 2KzJu3ub7mlkklYRxG6BJwTxChApED06O0gItbk0X5LIQ90Ofx
- **测试包名：** com.zuga.test

## 三、 搭建开发环境

### 1. 导入Sdk

#### 1.1 android studio 导入依赖包

- 在project的 build.gradle 中设置jitpack

```java
allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

- 在moudle build.gradle 的 dependencies 节点加入如下依赖：

```java
compile 'com.github.zuga-tech:bainu-sdk-android:1.0.3'//后续随时更新
compile 'com.android.support:appcompat-v7:23.2.1'//版本根据自己的项目
```

### 1.2 eclipse 导入jar包

```java
- 打开sdk包，bainuSdk(版本号).jar 包导入到工程。
- 导入android.support.v7，版本根据自己的项目
```

## 四、初始化sdk

### 添加权限

```java
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/><!-sdk不用这个权限，分享本地图片时请自行添加此权限->
```

### 创建Application子类
在工程中创建Appication的子类（Demo 上类名为MyApplication),并在Android Manifest的Application 字节点添加

```java
android:name="com.zuga.test.MyApplication"
```

### 添加初始化代码
在Applicaltion子类的 `onCreate()` 方法中添加如下代码

```java
BNApiFactory.init(Context context,String appId);
```

- appId:从官网上申请的App_ID

整体代码如下

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BNApiFactory.init(this,"bn0428040730");
    }
}
```

运行之后打印如下log,表示sdk初始化成功。

```java
                                BainuSdkErr---->
                                BainuSdkErr---->
                                BainuSdkErr---->
                                BainuSdkErr---->
                                BainuSdkErr---->bainu sdk set success
                                <------BainuSdkErr
                                <------BainuSdkErr
                                <------BainuSdkErr
                                <------BainuSdkErr
```

## 五、分享

分享之前在分享Activity的`onCreate()`方法中获取BNApi对象，代码如下

```java
api = BNApiFactory.getBNApi();
```

目前支持分享到三种场景：

```java
request.scene = BNSendRequest.SCENE_QOMRLG;//朋友圈
request.scene = BNSendRequest.SCENE_YARLQAA;//对话
request.scene = BNSendRequest.SCENE_ABDR;//收藏
```

### 1. 分享文字

```java
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
        bnSendRequest.scene = BNSendRequest.SCENE_QOMRLG;
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
                    Log.e(TAG, "errorMessage: " + message);
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
```

### 2. 分享图片

```java
    /*图片分享*/
    public void imageShare(View view) {

        /*
        * 两种方法进行图片分享:1.本地图片Uri,2.网络图片Uri
        *
        * 1.本地图片Uri必须以 "file://" 开头
        *
        * 2.网络图片Uri必须以 "http://" 或 "https://" 开头
        * */

        //1.创建对象
        BNImageObject imageObject = new BNImageObject();
        //1)设置本地图片Uri
//        imageObject.setLocalImageUri(Uri.parse("file:///storage/emulated/0/DCIM/Camera/fff.jpg"));
        //2)设置网络图片Uri
        imageObject.setNetImageUri(Uri.parse(
                "http://b.hiphotos.baidu.com/zhidao/pic/item/f9dcd100baa1cd11a345a9b1bf12c8fcc2ce2db4.jpg"));

        //2.设置跟随信息
        BNMediaMessage message = new BNMediaMessage(imageObject);
        message.setDescription("Tomcat picture");//此项可以不设定
        message.setTitle("picture");//此项可以不设定,但网页分享中必须设定

        //3.设置request
        BNSendRequest request = new BNSendRequest();
        request.message = message;
        request.scene = BNSendRequest.SCENE_QOMRLG;
        request.setRespListener(new BNSendRequest.RespListener() {
            @Override
            public void resp(int errorType, String message) {
                if (errorType != 0) {
                    Log.e(TAG, "errorMessage: " + message);
                }
            }
        });

        //4.发送
        api.send(request);
    }
```

### 3. 分享网页

```java
    /*网页分享*/
    public void webPageShare(View view) {

       /*
        * 分享的网页的Uri必须以 "http://" 或 "https://" 开头
        *
        * 设置网页的缩略图有两种方法:图片网络Uri 或 图片的byte[]
        *   所略图的Uri必须以 "http://" 或 "https://" 开头
        * */


        //1.创建对象
        BNWebPageObject webObject = new BNWebPageObject();
        webObject.setWebUri(Uri.parse("http://www.zuga-tech.com"));

        //2.设置跟随信息
        BNMediaMessage message = new BNMediaMessage(webObject);
        message.setTitle("webPage title");//此项必须有
        message.setDescription("webPage description");//此项可以不设定
        //设定缩略图
        message.setThumbNetPicUri(Uri.parse("http://b.hiphotos.baidu.com/" +
                "zhidao/pic/item/f9dcd100baa1cd11a345a9b1bf12c8fcc2ce2db4.jpg"));

        //3.设置request
        BNSendRequest request = new BNSendRequest();
        request.message = message;
        request.scene = BNSendRequest.SCENE_QOMRLG;
        request.setRespListener(new BNSendRequest.RespListener() {
            @Override
            public void resp(int errorType, String message) {
                if (errorType != 0) {
                    Log.e(TAG, "errorMessage: " + message);
                }
            }
        });

        //4.发送
        api.send(request);
    }
```

## 六、Bainu登录

移动应用Bainu登录是基于OAuth2.0协议标准构建的Bainu OAuth2.0授权登录系统。

在进行Bainu OAuth2.0授权登录接入之前，请联系Bainu开发人员，提交应用详情并获取对应的AppID和AppSecret，可开始接入流程。

目前移动应用上Bainu登录只提供原生的登录方式，需要用户安装Bainu客户端才能配合使用。

### 1. 授权流程说明

Bainu OAuth2.0授权登录让Bainu用户使用Bainu身份安全登录第三方应用，在Bainu用户授权登录已接入Bainu OAuth2.0的第三方应用后，第三方可以获取到用户的接口调用凭证（access_token），通过access_token可以进行Bainu开放平台授权关系接口调用，从而可实现获取Bainu用户基本开放信息和帮助用户实现基础开放功能等。

Bainu OAuth2.0授权登录目前支持authorization_code模式，适用于拥有server端的应用授权。该模式整体流程为：

- 第三方发起Bainu授权登录请求，Bainu用户允许授权第三方应用后，Bainu会拉起应用，并且带上授权临时票据code参数；(BainuSdk只做这一步，获得code参数)
- 通过code参数加上AppID和AppSecret等，通过API换取access_token；（第三方应用把code传给第三方应用服务器，服务器作此步骤）
- 通过access_token进行接口调用，获取用户基本数据资源或帮助用户实现基本操作。（第三方服务器反馈给第三方应用bainu用户基本信息）

### 2. 获取code参数

```java
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
                if (errorType == 0) {
                    Log.e(TAG, "code:" + message);
                } else {
                    Log.e(TAG, "errorMessage: " + message);
                }
            }
        });

        api.send(request);
    }
}
```

## 七、版本介绍

### V1.0.0 :

- 实现文字、图片、网页分享到Bainu

- 实现第三方应用从Bainu授权登陆

### V1.0.1 :

- 修改android 6.0及以上权限问题

- 增加第三方应用名称传给Bainu

### V1.0.2

- 修复一些bug

### v1.0.3

- 修改初始化接口

- 换仓库

