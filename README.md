# BainuSdk

[中文版文档](./README_CN.md)
## 1. Introduction

This document includes the usage of our android SDK. At this point, we assume that you are familiar with Android Studio and Eclipse, and have a basic knowledge of programming and similar project experience.

**Note**: This introduction only covers the Android Studio part, so for those who still use Eclipse, sorry!

## 2. Registering and Downloading

The Bainu Android SDK is not yet completely open for third parties at this moment, but we will receive your request for integrating our SDK into your application if you send the following informations to our E-mail: business@zuga-tech.com. After having reviewed your app, if everything is OK, we will send you the APP_ID, APP_SECRET, SDK and Demo App.

- **Mongolian Name of your App** (Traditional Mongolian)
- **English Name of your App**
- **iOS Bundle ID**
- **Android Package Name**
- **Logo** （640*640）
- **Home Page**
- **iOS AppStore URL**
- **Android URL** （!apk_address）
- **Introduction** (in traditional Mongolian)

**For test:**
- **APP_ID：** bn0428040730
- **APP_SECRET：** 2KzJu3ub7mlkklYRxG6BJwTxChApED06O0gItbk0X5LIQ90Ofx
- **package name** com.zuga.test

## 3. Development Environment Setup

### 1. Import the SDK

#### 1.1 Add gradle dependency in Android Studio

- Add the **jitpack** maven repository into your project's `build.gradle`

```java
allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

- In your App's `build.gradle`, add the following dependencies:

```java
//below the gradle3.0 use `compile` instead of `implementation`
implementation 'com.github.zuga-tech:bainu-sdk-android:1.0.4' // check the latest version on github
implementation 'com.android.support:appcompat-v7:27.0.2' // change version according to your project
```

    


### 1.2 Import Jar package into your Eclipse project

```java
 import bainuSdk(version_code).jar into project
 import android.support.v7(version_code) into project
```

## 4. Initialization

### Add permissions in your App's `AndroidManifest.xml`

```java
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/><!-The sdk doesn't use this permission, but you're going to need this if you want to share a local image->
```

### 5. Create a class that extends Application
Create a class extends `Applicaion`(see MyApplication in Demo), and in `AndroidManifest.xml`'s application tag add: 

```java
android:name="com.zuga.test.MyApplication"
```

### Initialize SDK in your newly created `Applicaion.java`
Add the following line in your Application's `onCreate()` method

```java
BNApiFactory.init(Context context,String appId);
```

- `appId`:App_ID that was provided to you

example:

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BNApiFactory.init(this,"bn0428040730");
    }
}
```

- Now, run your app. If the following lines appear in the logcat window, it means the SDK initialized without problem. We are good to go.

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

## 5. Sharing

Before sharing content, get the `BNApi` instance in your Activity's `onCreate()` method:

```java
api = BNApiFactory.getBNApi();
```

We only support the following scenes for sharing content:

```java
request.scene = BNSendRequest.SCENE_QOMRLG; // Bainu circle (moment|chomorlig etc.)
request.scene = BNSendRequest.SCENE_YARLQAA; // Bainu chat (p2p chat or group chat)
request.scene = BNSendRequest.SCENE_ABDR; // Bainu favourites (abdar in Mongolian)
```

### 1. Sharing text

```java
       /*sharing text content*/
    public void TextShare(View view) {
      //bainu supports only Menksoft code for Traditional Mongolian, for other languages, Unicode

        //1.Instanciate text object
        BNTextObject textObject = new BNTextObject();
        textObject.setText("Mongolian text should be Menksoft Code!");

        //2.Create media message
        BNMediaMessage bnMediaMessage = new BNMediaMessage(textObject);
        // bnMediaMessage.setTitle("Text");

        //3.Create request
        BNSendRequest bnSendRequest = new BNSendRequest();
        bnSendRequest.message = bnMediaMessage;
        bnSendRequest.scene = BNSendRequest.SCENE_QOMRLG;

        bnSendRequest.setRespListener(new BNSendRequest.RespListener() {
            /**
             * This listener may not be called when share success, but is bound to be called on every error.
             * @param errorType 0 means success, others for errors
             * @param message  contains error message only when errorType!=0
             */
            @Override
            public void resp(int errorType, String message) {
                if (errorType != 0) {
                    Log.e(TAG, "errorMessage: " + message);
                }
            }
        });

        //4.Send
        if (api.isBainuInstalledOrLatestVersion()) {//Check if bainu installed on device or version matches SDK requirement
          //installed && version matches requirement
            api.send(bnSendRequest);
        } else {
          //bainu download url, load this url for user if you like
            Log.e(TAG, "bainuDownUri: " + api.getBainuDownUri());
        }

    }
```

### 2. Sharing Image

```java
    /*Shareing image*/
    public void imageShare(View view) {

        /*
        * We support 2 types of image: 1.Local image 2.Image from internet
        * For local images, Uri must begin with "file://
        * For internet images, "http://" or "https://"
        * */

        //1.Create image object
        BNImageObject imageObject = new BNImageObject();
        //1)Set Uri for local image
        // imageObject.setLocalImageUri(Uri.parse("file:///storage/emulated/0/DCIM/Camera/fff.jpg"));
        //2)Set Uri for internet image
        imageObject.setNetImageUri(Uri.parse(
                "http://b.hiphotos.baidu.com/zhidao/pic/item/f9dcd100baa1cd11a345a9b1bf12c8fcc2ce2db4.jpg"));

        //2.Set other params
        BNMediaMessage message = new BNMediaMessage(imageObject);
        message.setDescription("Tomcat picture");//optional
        message.setTitle("picture");//optional for local image, necessary for internet image

        //3.Set request
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

        //4.Send
        api.send(request);
    }
```

### 3. Sharing web url

```java
    /*Sharing web url*/
    public void webPageShare(View view) {

       /*
        * url must start with "http://" or "https://"
        * url thumb image must be internet image or bitmap data (byte[])
        * Uri of internet thumb image must start with "http://" or "https://"
        * */

        //1.Create web object
        BNWebPageObject webObject = new BNWebPageObject();
        webObject.setWebUri(Uri.parse("http://www.zuga-tech.com"));

        //2.Set other data
        BNMediaMessage message = new BNMediaMessage(webObject);
        message.setTitle("webPage title");//must set
        message.setDescription("webPage description");//optional
        //set thumb image
        message.setThumbNetPicUri(Uri.parse("http://b.hiphotos.baidu.com/" +
                "zhidao/pic/item/f9dcd100baa1cd11a345a9b1bf12c8fcc2ce2db4.jpg"));

        //3.create request
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

        //4.Send
        api.send(request);
    }
```

## 6. Login with Bainu account

- Third party login with Bainu account is based on OAuth2.0 authorization.

- In this section, we assume you have already got your AppID and AppScret.

- **Note:** Bainu must be installed on the device, otherwise the user will not be able to log in. We only provide native authorization.

### 1.Procedure


Bainu OAuth2.0 authorization enables a user to securely log in to a third party app with his/her bainu account. After the user has logged in via this authorization, the third party app will get the user's access_token for this login session to get the user's basic informations from our open APIs.

Bainu OAuth2.0 only supports the authorization_code method, which is recommended for apps that have a server back-end. The main procedure is:

  - Third party apps request Bainu authorization. The Bainu user will decide if he/she wants to log in. If yes, Bainu will call the third party app and feed the code that was given from the Bainu server back to the third party app for temperarily authorization.

- With this code and AppID and AppSecret, the third party app may get the access_token of this session. We will send this access_token to the third party's server backend.

- The third party app may call our open APIs with this access_token to get the user's basic information.

### 2. Getting the `code`

```java
    /*bainu login*/
    public void login(View view) {
        BNSendRequest request = new BNSendRequest();
        request.isLogin = true;
        request.setRespListener(new BNSendRequest.RespListener() {
            /**
             * This listener will be called no matter whether the login succeeded or failed.
             * @param errorType 0 for success, others for error
             * @param message error message when errorType!=0
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

## 7.About Versions and updates

### V1.0.0 :

- Text, image, web url sharing is implemented
- Third party login is implemented

### V1.0.1 :

- Fixed the android M permission problems
- Added third party app's name to be displayed in Bainu when login

### V1.0.2

- some bugs fixed

### v1.0.3

- modified initializing apis
- SDK repository changed

### v1.0.4

- update gradle version to 3.0


### Please raise an issue for any question, or contact: help@zuga-tech.com with the subject starting with "BainuSDK"

