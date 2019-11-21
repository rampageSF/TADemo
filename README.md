# Andorid SDK接入文档  V1.0

## 接入流程图:
![image.png](https://cdn.nlark.com/yuque/0/2019/png/223400/1574313537893-8ac674b1-f0d2-44a3-a42d-54089730242e.png#align=left&display=inline&height=223&name=image.png&originHeight=446&originWidth=881&search=&size=28651&status=done&width=440.5)
## 1 引入SDK
#### 1) 配置依赖
在项目根目录的build.gradle中声明maven仓库
```java
allprojects {
    repositories {
        jcenter()
        maven {url 'http://maven.lifesense.com/nexus/content/repositories/releases'}
    }
}
```
在子项目build.gradle的dependencies中引入依赖
```java
api 'com.lifesense.android.opensdk:lsinsurance:0.0.1'
```
#### 2) **配置权限**
已在SDK种添加各项权限配置，以下仅为补充说明
```xml
		<uses-permission android:name="android.permission.INTERNET"/>
    <!-- 检测联网方式, 区分用户设备使用的是2G、3G或是WiFi -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <!-- 外部存储写入权限 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <!-- 为了更好的体验需要获取地理信息的权限 (以下非必须权限) -->
    <uses-permission android:name="android.permission.ACCESS_GPS" />
    <uses-permission android:name="android.permission.ACCESS_ASSISTED_GPS" />
    <uses-permission android:name="android.permission.ACCESS_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <!-- 蓝牙相关 -->
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <!-- 相机 -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.FLASHLIGHT" />
```

特殊权限说明：

- 获取地址权限是用于多网点的店铺将可以选择附近的门店
- 外部存储读写权限是用于文件选择，日志备份
- 网络状态权限是用于处理网络
- 蓝牙权限是用于接入乐心手环等蓝牙设备
- 相机权限是用于心率测试以及各种运动状态识别

#### 3) 初始化SDK
在Application或者加载页面之前对SDK进行初始化, 示例如下:
```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        LSConfig lsConfig =new LSConfig.Builder(mIImageLoad,mILsShareCallback)
                .setLogPath(logPath)
                .setServerUrl(getServerUrl())
                .setApiRequest(mIApiRequestImp)
                .build();
        LifesenseAgent.init(this,lsConfig);
    }
    //分享功能实现
    ILsShareCallback mILsShareCallback = new ILsShareCallback(){
        @Override
        public void onShare(ShareData shareData) {
            if (shareData.getShareType() == ShareData.SHARE_TYPE_URL) {
                ShareData.ShareUrl shareUrl = (ShareData.ShareUrl) shareData.getShare();
                WechatUtil.getInstance().sendLinkReq(shareUrl.getTitle(),
                        shareUrl.getDesc(), shareUrl.getUrl(),
                        shareUrl.getThumbImgBitmap(),
                        shareData.getShareChannel() == ShareData.SHARE_CHANNEL_WECHAT_FRIEND);
            } else if (shareData.getShareType() == ShareData.SHARE_TYPE_SCREEN_SHOT) {
                ShareData.ShareScreenshot shareScreenshot = (ShareData.ShareScreenshot) shareData.getShare();
                WechatUtil.getInstance().sendImageReq("", "", shareScreenshot.getBitmap(), shareData.getShareChannel() == ShareData.SHARE_CHANNEL_WECHAT_FRIEND);
            }
        }
    };
	//网络请求实现
    IApiRequestImp mIApiRequestImp = new IApiRequestImp() {
        @Override
        public void send(LsRequest lsRequest, ApiUtils.IApiResultCallback iApiResultCallback) {
            OkHttpUtils.getInstance().send(lsRequest,iApiResultCallback);
        }
    };
    //图片加载实现
    IImageLoadImpl mIImageLoad = new IImageLoadImpl() {
        @Override
        public void load(ImageView imageView, String url) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .into(imageView);
        }

        @Override
        public void onGetNetworkImageBytes(String imgUrl, final ImgUtils.SimpleGetImageBytes simpleGetImageBytes) {
            Glide.with(mMyApp).asBitmap().load(imgUrl).listener(new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    simpleGetImageBytes.onLoadFailed();
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    if (resource == null) {
                        simpleGetImageBytes.onLoadFailed();
                        return false;
                    }
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    byte[] bitmapByte = stream.toByteArray();
                    simpleGetImageBytes.onResourceReady(bitmapByte);
                    return false;
                }
            }).submit();
        }
    };
}
```
## 2 加载视图
通过MainHyActivity/BaseHyFragment加载
#### 2.1 activity入口

```java
 	Bundle bundle = new Bundle();
	bundle.putString(HybridConstant.INTENT_EXTRA_KEY_TOPAGE, text);
   	bundle.putSerializable(HybridConstant.INTENT_EXTRA_KEY_ANIMATION, HybridParamAnimation.PUSH);
    bundle.putBoolean(HybridConstant.INTENT_EXTRA_KEY_HASNAVGATION, true);
    Intent intent = new Intent(getContext(), BaseHyActivity.class);
    intent.putExtras(bundle);
	startActivityForResult(intent, HybridConstant.RQ_RESULT);
```

#### 2.2 fragment入口

```java
//直接传入h5地址进入fragment
LifesenseAgent.buildLsFragment(MyApp.h5mine)
```

## 3 登录认证

```java
LoginInfo loginInfo = LoginInfo.build(userId)
LifesenseAgent.login(loginInfo);
```

demo地址：
[https://github.com/rampageSF/TADemo](https://github.com/rampageSF/TADemo)


