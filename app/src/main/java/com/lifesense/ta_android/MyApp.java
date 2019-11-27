package com.lifesense.ta_android;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import com.lifesense.lshybird.utils.ImgUtils;
import com.lifesense.rpm.doctor.dev.R;
import com.lifesense.ta_android.share.WechatUtil;
import com.lifesense.ta_android.utils.OkHttpUtils;
import com.lifesense.ta_android.utils.SpUtils;
import com.lifesense.ta_android.wiget.CustomDialog;

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

    //普通用户
    public static final String h5normal = "https://static-qa.lifesense.com/tauser/#/home";
    //vip用户
    public static final String h5vip="https://static-qa.lifesense.com/ta/#/v-home";
    //业务员
    public static final String h5busiss="https://static-qa.lifesense.com/tasales/#/home";
    //me
    public static final String h5mine="https://static-qa.lifesense.com/tauser/#/user";

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
//        Log.d("lifesense", "onCreate:打印日志路径： " + logPath);
//        LifesenseAgent.openWriteLogToFile(logPath);

        LSConfig lsConfig =new LSConfig.Builder(mIImageLoad,mILsShareCallback)
                .setLogPath(logPath)
                .setServerUrl(getServerUrl())
                .setApiRequest(mIApiRequestImp)
                .build();
        LifesenseAgent.init(this,lsConfig);

    }
    private void showShareDialog(Context context, final ShareData shareData) {

        final CustomDialog customDialog = createMenuDialog(context, R.layout.dialog_share);
        customDialog.findViewById(R.id.ls_tv_wechat_friend_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                shareData.setShareChannel(ShareData.SHARE_CHANNEL_WECHAT_FRIEND);
                share(shareData);
            }
        });
        customDialog.findViewById(R.id.ls_tv_wechat_monents_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                shareData.setShareChannel(ShareData.SHARE_CHANNEL_WECHAT_MOMENTS);
                share(shareData);
            }
        });
        customDialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消分享
                shareData.setShareChannel(0);
                LifesenseAgent.execute(shareData);
                customDialog.dismiss();

            }
        });
        customDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                shareData.setShareChannel(0);
                LifesenseAgent.execute(shareData);
            }
        });
        customDialog.show();
    }

    private void share(ShareData shareData) {
        //执行分享回调通知后台添加积分和改变h5ui
        LifesenseAgent.execute(shareData);
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

    private CustomDialog createMenuDialog(Context context, int layoutId) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int w = (int) (dm.widthPixels);
        int h = (int) (ViewGroup.LayoutParams.WRAP_CONTENT);
        return new CustomDialog(context, w, h, layoutId, Gravity.BOTTOM);
    }

    public static Context getApp() {
        return mMyApp;
    }

    ILsShareCallback mILsShareCallback = new ILsShareCallback(){

        @Override
        public void onShare(ShareData shareData) {
            showShareDialog(shareData.getContext(),shareData);
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


