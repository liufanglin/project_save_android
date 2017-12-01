package com.ximai.savingsmore.save.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.library.toolbox.LogUtils;
import com.ximai.savingsmore.library.toolbox.PreferencesUtils;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.common.BaseApplication;
import com.ximai.savingsmore.save.modle.LoginUser;
import com.ximai.savingsmore.save.modle.MyUserInfo;
import com.ximai.savingsmore.save.modle.MyUserInfoUtils;
import com.ximai.savingsmore.save.modle.UserInfo;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by caojian on 16/11/18.
 */
public class SplashActivity extends Activity {
    public static final String NOT_FIRST_OPEN = "not_first_open";
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!PreferencesUtils.getBoolean(SplashActivity.this, NOT_FIRST_OPEN)) {
                Intent it = new Intent(SplashActivity.this, GuidePageActivity.class);
                startActivity(it);
                finish();
            } else {
                String account = PreferencesUtils.getString(BaseApplication.getInstance(), "account", null);
                String pwd = PreferencesUtils.getString(BaseApplication.getInstance(), "pwd", null);
                int type = PreferencesUtils.getInt(BaseApplication.getInstance(), "type");
                if (null != account && null != pwd && type != 0) {
                    login(account, pwd, type);
                } else {
                    finish();
                    boolean isAutoLogin = false;
                    if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(pwd)) {
                        isAutoLogin = true;
                    }
                    Intent it = new Intent(SplashActivity.this, NoLoginMainactivity.class);
                    // it.putExtra("isAutoLogin",isAutoLogin);
                    startActivity(it);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        myHandler.sendEmptyMessageDelayed(0, 500);

    }

    private void login(final String name, final String password, int type) {
        WebRequestHelper.post(URLText.LOGIN_URL, RequestParamsPool.getLoginParams(name, password, JPushInterface.getRegistrationID(SplashActivity.this), type), new MyAsyncHttpResponseHandler(SplashActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    String message = object.optString("Message");
                    Boolean isLogin = object.optBoolean("IsSuccess");
                    if (isLogin) {
                        String MainData = object.optString("MainData");
                        UserInfo userInfo = GsonUtils.fromJson(MainData, UserInfo.class);
                        LoginUser.getInstance().userInfo = userInfo;
                        if (null != userInfo) {
                            BaseApplication.getInstance().Token = userInfo.TokenType + " " + userInfo.AccessToken;
                        }
                        LoginUser.getInstance().setIsLogin(true);
                        getUsereInfo();

                    }
                    //Toast.makeText(SplashActivity.this, message, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    //得到用户的信息
    private void getUsereInfo() {
        WebRequestHelper.json_post(SplashActivity.this, URLText.GET_USERINFO, RequestParamsPool.getUserInfo(), new MyAsyncHttpResponseHandler(SplashActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                JSONObject object = null;
                try {
                    object = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String MianData = object.optString("MainData");
                MyUserInfoUtils.getInstance().myUserInfo = GsonUtils.fromJson(MianData, MyUserInfo.class);
                if (null != MyUserInfoUtils.getInstance().myUserInfo) {
                    requestLoginEaseChat(MyUserInfoUtils.getInstance().myUserInfo.IMUserName, MyUserInfoUtils.getInstance().myUserInfo.IMPassword);
                }
            }
        });

    }

    public void requestLoginEaseChat(final String accountStr, final String pwd) {
        EMChatManager.getInstance().login(accountStr, pwd, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                // EMGroupManager.getInstance().loadAllGroups();//加载群组 木有此功能
                EMChatManager.getInstance().loadAllConversations();

                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                        BaseApplication.currentUserNick.trim());
                if (!updatenick) {
                    LogUtils.instance.d("update current user nick fail");
                }
                //异步获取当前用户的昵称和头像(从自己服务器获取...)
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }

    @Override
    public void onBackPressed() {

    }

}
