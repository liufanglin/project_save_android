package com.ximai.savingsmore.save.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.ScreenUtils;
import com.ximai.savingsmore.save.modle.ShareData;


import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


public class ShareUtils implements OnClickListener, PlatformActionListener {
    private Context context;
    public PopupWindow shareWindow;
    private ShareData data;
    private String Remark;

    private LinearLayout wechat, qzone, wechatMoments, weibo, duanxin, youxiang;

    public ShareUtils(ShareData data, Context context) {
        this.data = data;
        this.context = context;

    }


    //设置弹窗的显示
    public void show(View parentView) {
        if (shareWindow != null && shareWindow.isShowing()) {
            shareWindow.dismiss();
            setAlpath(1f);
        }
        View cw = ((Activity) context).getLayoutInflater().inflate(R.layout.item_shareutil_layout, null);
        wechat = (LinearLayout) cw.findViewById(R.id.wechat);
        wechat.setOnClickListener(this);
        qzone = (LinearLayout) cw.findViewById(R.id.qzone);
        weibo = (LinearLayout) cw.findViewById(R.id.weibo);
        duanxin = (LinearLayout) cw.findViewById(R.id.duanxin);
        youxiang = (LinearLayout) cw.findViewById(R.id.youxiang);
        duanxin.setOnClickListener(this);
        youxiang.setOnClickListener(this);
        weibo.setOnClickListener(this);
        qzone.setOnClickListener(this);
        wechatMoments = (LinearLayout) cw.findViewById(R.id.wechatmoments);
        wechatMoments.setOnClickListener(this);

        setAlpath(0.5f);
        shareWindow = new PopupWindow(cw, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        shareWindow.setFocusable(true);
        shareWindow.setTouchable(true);
        shareWindow.setOutsideTouchable(true);

        shareWindow.showAtLocation(parentView.getRootView(), Gravity.BOTTOM, 0, 0);
        ((LinearLayout) cw).setFocusable(true);
        ((LinearLayout) cw).setFocusableInTouchMode(true);
        //弹窗的点击事件
        cw.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });

        cw.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            dismiss();
                            break;
                        case KeyEvent.KEYCODE_MENU:
                            dismiss();
                            break;
                    }
                }
                return true;
            }
        });
    }

    //关闭弹窗
    public void dismiss() {
        if (shareWindow != null && shareWindow.isShowing()) {
            shareWindow.dismiss();
            setAlpath(1f);
        }
    }


    //取消分享
    @Override
    public void onCancel(Platform arg0, int arg1) {
        Toast.makeText(context, "取消分享", Toast.LENGTH_SHORT).show();

    }

    //分享成功
    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
        Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
        shareApp(Remark);
    }

    //分享错误
    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        Toast.makeText(context, "分享错误", Toast.LENGTH_SHORT).show();
    }

    //点击监听事件
    @Override
    public void onClick(View v) {
        SharePlatfrom sharePlatfrom = new SharePlatfrom();
        if (v.getId() == R.id.wechat) {
            Remark = "微信";
            dismiss();
            if (isWeixinAvilible(context))
                sharePlatfrom.platfrom("wechat", data);
            else
                Toast.makeText(context, "您的微信还未安装呢！", Toast.LENGTH_SHORT).show();

        }
        if (v.getId() == R.id.wechatmoments) {
            Remark = "朋友圈";
            dismiss();
            if (isWeixinAvilible(context))
                sharePlatfrom.platfrom("wechat_circle", data);
            else
                Toast.makeText(context, "您的微信还未安装呢！", Toast.LENGTH_SHORT).show();


        }
        if (v.getId() == R.id.weibo) {
            Remark = "微博";
            dismiss();
            sharePlatfrom.platfrom("weibo", data);
        }
        if (v.getId() == R.id.qzone) {
            Remark = "qq空间";
            dismiss();
            sharePlatfrom.platfrom("qzone", data);
        }
        if (v.getId() == R.id.duanxin) {
            shareApp("短信");
            dismiss();
            Uri smsToUri = Uri.parse("smsto:10000");
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
            intent.putExtra("sms_body", "实体商铺在促销、在活动、在甩卖!省又省购物省钱、省心、省时间! http://login.savingsmore.com/Home/Download");
            context.startActivity(intent);
        }
        if (v.getId() == R.id.youxiang) {
            shareApp("邮箱");
            dismiss();
            Intent email = new Intent(android.content.Intent.ACTION_SEND);
            email.setType("plain/text");

            String emailBody = "实体商铺在促销、在活动、在甩卖!省又省购物省钱、省心、省时间! http://login.savingsmore.com/Home/Download";
            //邮件主题
            email.putExtra(android.content.Intent.EXTRA_SUBJECT, "省又省-促销专卖APP");
            //邮件内容
            email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);

            context.startActivity(Intent.createChooser(email, "请选择邮件发送内容"));

        }


    }


    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    //设置透明度
    public void setAlpath(float alpath) {
        WindowManager.LayoutParams params = ((Activity) context).getWindow().getAttributes();
        params.alpha = alpath;
        ((Activity) context).getWindow().setAttributes(params);
    }

    //实现点击窗体以外的部分能关闭弹窗
    public void setWindowsBackground() {
        if (shareWindow != null && shareWindow.isShowing()) {
            shareWindow.dismiss();
            setAlpath(1f);
            shareWindow = null;
        }

    }


    //判断分享类型，设置分享数据
    public class SharePlatfrom {
        private final static String PENGYOUQUAN = "wechat_circle";
        private final static String QQKONGJIAN = "qzone";
        private final static String WEIXIN = "wechat";
        private final static String WEIBO = "weibo";

        public void platfrom(String type, ShareData data) {
            Platform platform = null;
            String shareImagePath = data.getImagePath();
            String shareUrl = data.getUrl();
            String shareText = data.getText();
            String shareimageUrl = data.getImageUrl();
            String shareTitle = data.getTitle();
            String shareSite = data.getSite();
            String shareSiteUrl = data.getSiteUrl();
            String shareTitltUrl = data.getTitleurl();
            if (type == PENGYOUQUAN) {
       /*        微信分享图文：必须设置title，imageUrl（imagepath，ImageData) text为可选参数
                微信分享文本：title text都必须有
                微信网页分享:title text imageUrl	url*/

                platform = ShareSDK.getPlatform(context, WechatMoments.NAME);
                WechatMoments.ShareParams params = new WechatMoments.ShareParams();
                if (shareUrl != null) {
                    params.setShareType(Platform.SHARE_WEBPAGE);
                    params.setUrl(shareUrl);
                } else {
                    if (shareimageUrl != null || shareImagePath != null) {
                        params.setShareType(Platform.SHARE_IMAGE);
                    } else {
                        params.setShareType(Platform.SHARE_TEXT);
                    }
                }

                if (shareImagePath != null) {
                    params.setImagePath(shareImagePath);
                }
                if (shareText != null) {
                    params.setText(shareText);
                }
                if (shareimageUrl != null) {
                    params.setImageUrl(shareimageUrl);
                }
                if (shareTitle != null) {
                    params.setTitle(shareTitle);
                }
                platform.setPlatformActionListener(ShareUtils.this);
                platform.share(params);
            }
            if (type == WEIBO) {
                platform = ShareSDK.getPlatform(context, SinaWeibo.NAME);
                SinaWeibo.ShareParams params = new SinaWeibo.ShareParams();
                if (shareImagePath != null) {
                    params.setImagePath(shareImagePath);
                }
                if (shareText != null) {
                    params.setText(shareText);
                }
                if (shareimageUrl != null) {
                    params.setImageUrl(shareimageUrl);
                }
                if (shareTitle != null) {
                    //  params.setTitle(shareTitle);
                }
                if (shareSite != null) {
                    //  params.setSite(shareSite);
                }
                if (shareSiteUrl != null) {
                    // params.setSiteUrl(null);
                }
                if (shareTitltUrl != null) {
                    // params.setTitleUrl(shareTitltUrl);
                }
                platform.setPlatformActionListener(ShareUtils.this);
                platform.share(params);
            }
            if (type == QQKONGJIAN) {
                //qq空间图文分享必须设置四个参数不能少：setTitle(),setTitleUrl(),setText(),setImageUrl();

                // platform = ShareSDK.getPlatform(context, QZone.NAME);
                platform = ShareSDK.getPlatform(context, QQ.NAME);

                QQ.ShareParams params = new QQ.ShareParams();
                if (shareImagePath != null) {
                    params.setImagePath(shareImagePath);
                }
                if (shareText != null) {
                    params.setText(shareText);
                }
                if (shareimageUrl != null) {
                    params.setImageUrl(shareimageUrl);
                }
                if (shareTitle != null) {
                    params.setTitle(shareTitle);
                }
                if (shareSite != null) {
                    params.setSite(shareSite);
                }
                if (shareSiteUrl != null) {
                    params.setSiteUrl(null);
                }
                if (shareTitltUrl != null) {
                    params.setTitleUrl(shareTitltUrl);
                }

                platform.setPlatformActionListener(ShareUtils.this);
                platform.share(params);
            }
            if (type == WEIXIN) {
                platform = ShareSDK.getPlatform(context, Wechat.NAME);
                Wechat.ShareParams params = new Wechat.ShareParams();
                if (shareUrl != null) {
                    params.setShareType(Platform.SHARE_WEBPAGE);
                    params.setUrl(shareUrl);
                } else {
                    if (shareimageUrl != null || shareImagePath != null) {
                        params.setShareType(Platform.SHARE_IMAGE);
                    } else {
                        params.setShareType(Platform.SHARE_TEXT);
                    }
                }

                if (shareImagePath != null) {
                    params.setImagePath(shareImagePath);
                }
                if (shareText != null) {
                    params.setText(shareText);
                }
                if (shareimageUrl != null) {
                    params.setImageUrl(shareimageUrl);
                }
                if (shareTitle != null) {
                    params.setTitle(shareTitle);
                }
                platform.setPlatformActionListener(ShareUtils.this);
                platform.share(params);
            }
        }
    }

    private void shareApp(String Remark) {
        WebRequestHelper.json_post(context, URLText.SHARE_APP, RequestParamsPool.sahreApp(Remark), new MyAsyncHttpResponseHandler(context) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                //   Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                JSONObject object = new JSONObject();
            }
        });
    }


}
