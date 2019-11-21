package com.lifesense.ta_android;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lifesense.lshybird.LSConfig;
import com.lifesense.lshybird.LifesenseAgent;
import com.lifesense.lshybird.api.ApiUtils;
import com.lifesense.lshybird.api.LsRequest;
import com.lifesense.lshybird.impl.IApiRequestImp;
import com.lifesense.lshybird.impl.IImageLoadImpl;
import com.lifesense.lshybird.share.ILsShareCallback;
import com.lifesense.lshybird.share.ShareData;
import com.lifesense.lshybird.user.LoginInfo;
import com.lifesense.lshybird.utils.ImgUtils;
import com.lifesense.lsstepcounter.TodayStepCounter;
import com.lifesense.ta_android.share.WechatUtil;
import com.lifesense.ta_android.utils.OkHttpUtils;
import com.lifesense.ta_android.utils.SpUtils;

import java.io.ByteArrayOutputStream;


/**
 * Created by Sinyi.liu on 2019/8/24.
 */
public class MyApp extends Application {
    private static MyApp mMyApp;
//    private static final String serverUrl = "https://sports.lifesense.com";
    //使用ok网络框架以斜杠结尾"/"
    private static final String serverUrl = "https://sports-beta.lifesense.com/";

    //    private static final String h5Url = "https://cdn.lifesense.com/ta/#/home";
    private static final String h5Url = "https://static-qa.lifesense.com/ta/#/v-home";

    public static String getH5Url() {
        String url = SpUtils.getDomain();
        if (TextUtils.isEmpty(url)) {
            return h5Url;
        }
        return url;
    }

    public static String getServerUrl() {
        return (String) SpUtils.get("serviceUrl", serverUrl);

    }

    @Override
    public void onCreate() {
        super.onCreate();


        mMyApp = this;
        //打开日志打印到文件

        String logPath = Environment.getDataDirectory().getAbsolutePath();
        if (this.getExternalCacheDir() != null) {
            logPath = this.getExternalCacheDir().getPath();
        }
        LSConfig lsConfig =new LSConfig.Builder(mIImageLoad,mILsShareCallback)
                .setLogPath(logPath)
                .setServerUrl(getServerUrl())
                .setApiRequest(mIApiRequestImp)
                .build();
        LifesenseAgent.init(this,lsConfig);



    }

    public static Context getApp() {
        return mMyApp;
    }

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

    IApiRequestImp mIApiRequestImp = new IApiRequestImp() {
        @Override
        public void send(LsRequest lsRequest, ApiUtils.IApiResultCallback iApiResultCallback) {
            OkHttpUtils.getInstance().send(lsRequest,iApiResultCallback);
        }
    };
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


