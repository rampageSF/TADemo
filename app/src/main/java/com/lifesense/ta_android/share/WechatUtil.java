package com.lifesense.ta_android.share;

import android.graphics.Bitmap;

import com.lifesense.ta_android.MyApp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;


/**
 * Created by wrh on 16/2/14.
 */
public class WechatUtil {
    private static final String TAG = WechatUtil.class.getSimpleName();
    /**
     * 微信分享的图片大小不得超过32K ,所以要对大于32K的图片进行压缩
     */
    private static final float MAX_SIZE = 32f;
    private IWXAPI iwxapi;
    private static WechatUtil instance = null;
    private boolean isRegisterSuccess = false;

    private WechatUtil() {
        if (iwxapi == null) {
            String appId = "wxd8adb8d81c77e8fe";
            iwxapi = WXAPIFactory.createWXAPI(MyApp.getApp(), appId, true);
            // 将应用的appId注册到微信
            iwxapi.registerApp(appId);
        }
    }

    public IWXAPI getWxApi() {
        return iwxapi;
    }



    public static WechatUtil getInstance() {
        if (instance == null) {
            synchronized (WechatUtil.class) {
                if (instance == null) {
                    instance = new WechatUtil();
                }
            }
        }
        return instance;
    }

    /***
     * @return 返回注册成功与否
     */
    public boolean isRegisterSuccess() {
        return isRegisterSuccess;
    }

    /**
     * 是否安装了微信
     *
     * @return
     */
    public boolean isWXAppInstalled() {
        return iwxapi.isWXAppInstalled();
    }

    /**
     * 发送文本到朋友圈
     *
     * @param text
     * @return
     */
    public boolean sendTextReqToMoments(String text) {
        return sendTextReq(text, false);
    }

    /**
     * 发送文本到好友
     *
     * @param text
     * @return
     */
    public boolean sendTextReqToFriend(String text) {
        return sendTextReq(text, true);
    }

    /**
     * 发送文本
     *
     * @param text
     * @param isFriend true为好友，false为朋友圈
     * @return
     */
    private boolean sendTextReq(String text, boolean isFriend) {
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        if (isFriend) {
            req.scene = SendMessageToWX.Req.WXSceneSession;//好友
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline; // 发送的目标场景是朋友圈
        }
        return iwxapi.sendReq(req);
    }





    /**
     * 发送链接
     *
     * @param title
     * @param desc
     * @param linkUrl
     * @param bitmap
     * @param isFriend
     * @return
     */
    public boolean sendLinkReq(String title, String desc, String linkUrl, byte[] bitmap, boolean isFriend) {
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = linkUrl;
        WXMediaMessage wxMediaMessage = new WXMediaMessage(webpageObject);
        wxMediaMessage.title = title;
        wxMediaMessage.description = desc;
        wxMediaMessage.thumbData=bitmap;

        // wxMediaMessage.setThumbImage(thumbBmp);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        // req.transaction = buildTransaction("webpage");
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = wxMediaMessage;
        if (isFriend) {
            req.scene = SendMessageToWX.Req.WXSceneSession;// SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        return iwxapi.sendReq(req);
    }

    /**
     * 发送图片给好友
     *
     * @param title
     * @param desc
     * @param bitmap
     * @return
     */
    public boolean sendImageReqToFriend(String title, String desc, Bitmap bitmap) {
        return sendImageReq(title, desc, bitmap, true);
    }

    /**
     * 发送图片到朋友圈
     *
     * @param title
     * @param desc
     * @param bitmap
     * @return
     */
    public boolean sendImageReqToMoments(String title, String desc, Bitmap bitmap) {
        return sendImageReq(title, desc, bitmap, false);
    }


    /**
     * 发送图片
     *
     * @param title
     * @param desc
     * @param bitmap
     * @param isFriend
     * @return
     */
    public boolean sendImageReq(String title, String desc, Bitmap bitmap, boolean isFriend) {
        if (bitmap == null) {
            return false;
        }

        WXImageObject imageObject = new WXImageObject(bitmap);

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.title = title;
        mediaMessage.description = desc;
        mediaMessage.mediaObject = imageObject;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        // shareBitmap.recycle();
        mediaMessage.thumbData = bmpToByteArray(thumbBmp, false); // 设置缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();

        req.message = mediaMessage;
        if (isFriend) {
            req.scene = SendMessageToWX.Req.WXSceneSession;// SendMessageToWX.Req.WXSceneSession;
            req.transaction += "_USER";
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
            req.transaction += "_FRIEND";
        }

        boolean result = iwxapi.sendReq(req);
        return result;
    }


    private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(Bitmap.CompressFormat.PNG, options, output);
        byte[] result = output.toByteArray();
        Bitmap bitmap = bmp;
        while (result.length / 1024f > MAX_SIZE) {
            output.reset();
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
            bitmap.compress(Bitmap.CompressFormat.PNG, options, output);
            result = output.toByteArray();
        }

        if (needRecycle) {
            bmp.recycle();
        }

        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return result;
    }




    public void sendMiniProgramToFriend(String title, String desc, Bitmap bitmap, String linkUrl, String userName, String path,int miniprogramType,boolean withShareTicket) {
        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
        miniProgramObj.webpageUrl = linkUrl; // 兼容低版本的网页链接
        miniProgramObj.miniprogramType = miniprogramType;// 正式版:0，测试版:1，体验版:2
        miniProgramObj.userName = userName;     // 小程序原始id
        miniProgramObj.path = path;            //小程序页面路径
        miniProgramObj.withShareTicket = withShareTicket;
        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
        msg.title = title;                    // 小程序消息title
        msg.description = desc;
        if (bitmap != null) {// 小程序消息desc
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
            msg.thumbData = bmpToByteArray(thumbBmp, false);
        }// 小程序消息封面图片，小于128k

        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("miniProgram");
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前只支持会话
        iwxapi.sendReq(req);
    }


}
