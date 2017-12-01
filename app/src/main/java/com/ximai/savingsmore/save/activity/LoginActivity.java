package com.ximai.savingsmore.save.activity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.ximai.savingsmore.save.modle.RegisterNumber;
import com.ximai.savingsmore.save.modle.UserInfo;
import com.ximai.savingsmore.save.service.JpushReceiver;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by caojian on 16/11/16.
 */
//个人登录
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText name;
    private EditText password;
    private Button button;
    private ImageView btnWechat, btnQQ, btnXinlang;
    private TextView register, forgetPassword;
    //用户的类型  2个人 3商家
    private int type = 2;
    private final String PLATFORM_WEIBO = "sina";
    private final String PLATFORM_WECHAT = "weixin";
    private final String PLATFORM_QQ = "qq";
    private String sinaOpenId;
    private String sinaToken;
    private LinearLayout person, business;
    private TextView business_number, good_number, person_number, today_number;
    private String ExternalSigninType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_login_activity);
        setLeftBackMenuVisibility(LoginActivity.this, "");
        Intent intent = getIntent();
        setCenterTitle("登录");
        styleRightTextMenuLayout.setVisibility(View.VISIBLE);
        rightTextMenuTextView.setText(" 商家入口");
        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        if (null != getIntent()) {
            name.setText(intent.getStringExtra("phone"));
        }
        button = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        forgetPassword = (TextView) findViewById(R.id.forget_password);
        btnQQ = (ImageView) findViewById(R.id.account_loginuser__qq);
        btnWechat = (ImageView) findViewById(R.id.account_login_user_wechat);
        btnXinlang = (ImageView) findViewById(R.id.account_login_user_xinlang);
        person = (LinearLayout) findViewById(R.id.person);
        business = (LinearLayout) findViewById(R.id.business);
        business_number = (TextView) findViewById(R.id.business_number);
        good_number = (TextView) findViewById(R.id.pingzhong_number);
        person_number = (TextView) findViewById(R.id.kuhu_number);
        today_number = (TextView) findViewById(R.id.all_number);
        btnQQ.setOnClickListener(this);
        btnWechat.setOnClickListener(this);
        btnXinlang.setOnClickListener(this);
        register.setOnClickListener(this);
        styleRightTextMenuLayout.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        button.setOnClickListener(this);
        getRegisterNUmber();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                if (!TextUtils.isEmpty(name.getText().toString()) && null != name.getText() && !TextUtils.isEmpty(password.getText().toString()) && null != password.getText()) {
                    login(name.getText().toString(), password.getText().toString());
                } else {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.register:
                Intent intent = new Intent(LoginActivity.this, OneStepRegisterActivity.class);
                intent.putExtra("register_type", type + "");
                startActivity(intent);
                break;
            case R.id.forget_password:
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class).putExtra("title", "找回密码"));
                break;
            case R.id.style_right_text_menu:
                if (type == 3) {
                    rightTextMenuTextView.setText("商家入口");
                    person.setVisibility(View.VISIBLE);
                    business.setVisibility(View.GONE);
                    type = 2;
                } else {
                    rightTextMenuTextView.setText("个人入口");
                    business.setVisibility(View.VISIBLE);
                    person.setVisibility(View.GONE);
                    type = 3;
                }
                break;
            case R.id.account_loginuser__qq:
                ExternalSigninType = "1";
                Platform platQQ = ShareSDK.getPlatform(LoginActivity.this, QQ.NAME);
                platQQ.setPlatformActionListener(new ThirdLoginListener(PLATFORM_QQ));
                platQQ.SSOSetting(false);// 设置为false或者不设置这个值，如果设置为 true 则调用客户端
                platQQ.showUser(null);
                break;
            case R.id.account_login_user_wechat:
                ExternalSigninType = "2";
                Platform platWX = ShareSDK.getPlatform(LoginActivity.this, Wechat.NAME);
                platWX.setPlatformActionListener(new ThirdLoginListener(
                        PLATFORM_WECHAT));
                platWX.SSOSetting(true);
                platWX.showUser(null);
                break;
            case R.id.account_login_user_xinlang:
                ExternalSigninType = "3";
                Platform platWB = ShareSDK.getPlatform(LoginActivity.this, SinaWeibo.NAME);
                platWB.setPlatformActionListener(new ThirdLoginListener(
                        PLATFORM_WEIBO));
                platWB.SSOSetting(false);// 设置为false或者不设置这个值，如果设置为 true 则调用客户端
                platWB.showUser(null);
                break;

        }
    }

    private class ThirdLoginListener implements PlatformActionListener {
        private String platformName;

        public ThirdLoginListener(String platName) {
            this.platformName = platName;
        }

        @Override
        public void onError(Platform arg0, int arg1, Throwable arg2) {
            System.out.println("-------" + arg2.getMessage() + "--------");
            arg2.printStackTrace();
        }

        @Override
        public void onComplete(Platform arg0, int arg1,
                               HashMap<String, Object> arg2) {
            ThirdUserLogin(platformName, arg0, arg2);

        }

        @Override
        public void onCancel(Platform arg0, int arg1) {
            System.out.println("-------MSG_AUTH_CANCEL--------");

        }

    }

    //发送网络请求的方法
    private void ThirdUserLogin(String platName, Platform arg0,
                                HashMap<String, Object> arg2) {
        String platformName, openid, realname, nickname;
        String gender, userIcon;
        String country;
        String province;
        String email;
        String lactionString;
        platformName = platName;
        openid = arg0.getDb().getUserId();
        realname = arg0.getDb().getUserName();
        nickname = arg0.getDb().getUserName();
        gender = arg0.getDb().getUserGender() == "m" ? "0" : "1";
        userIcon = arg0.getDb().getUserIcon();
        if (platformName == PLATFORM_WEIBO) {
            sinaOpenId = openid;
            lactionString = (String) arg2.get("location");
            sinaToken = arg0.getDb().getToken();
        } else if (platformName == PLATFORM_QQ) {// QQ 微信返回地址
            userIcon = (String) arg2.get("figureurl_qq_2");
            lactionString = (String) arg2.get("province")
                    + (String) arg2.get("city");
            email = (String) arg2.get("email");
        } else if (platformName == PLATFORM_WECHAT) {
            lactionString = (String) arg2.get("province")
                    + (String) arg2.get("city");
            province = (String) arg2.get("province");
            country = (String) arg2.get("city");
            email = (String) arg2.get("email");
        }
        ThirdLogin(openid, nickname, ExternalSigninType, type + "");
        //做网络请求传参数

    }


    //第三方登录
    private void ThirdLogin(String OpenId, String NickName, String ExternalSigninType, String UserType) {
        WebRequestHelper.post(URLText.THIRD_LOGIN, RequestParamsPool.thirldLogin(OpenId, NickName, ExternalSigninType, UserType), new MyAsyncHttpResponseHandler(LoginActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
            }
        });
    }

    //登录
    private void login(final String name, final String password) {
        WebRequestHelper.post(URLText.LOGIN_URL, RequestParamsPool.getLoginParams(name, password, JPushInterface.getRegistrationID(LoginActivity.this), type), new MyAsyncHttpResponseHandler(LoginActivity.this) {
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
                        saveLoginUserAccountAndPwd(name, password, type);
                        getUsereInfo();

                    }
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    //得到用户的信息
    private void getUsereInfo() {
        WebRequestHelper.json_post(LoginActivity.this, URLText.GET_USERINFO, RequestParamsPool.getUserInfo(), new MyAsyncHttpResponseHandler(LoginActivity.this) {
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
                requestLoginEaseChat(MyUserInfoUtils.getInstance().myUserInfo.IMUserName, MyUserInfoUtils.getInstance().myUserInfo.IMPassword);
            }
        });

    }

    private void getRegisterNUmber() {
        StringEntity s = null;
        try {
            s = new StringEntity("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebRequestHelper.json_post(LoginActivity.this, URLText.REGISTER_NUMBER, s, new MyAsyncHttpResponseHandler(LoginActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    RegisterNumber registerNumber = GsonUtils.fromJson(new String(responseBody), RegisterNumber.class);
                    if (null != registerNumber.MainData) {
                        person_number.setText(registerNumber.MainData.TodayNormalCount);
                        today_number.setText(registerNumber.MainData.NormalCount);
                        business_number.setText(registerNumber.MainData.SellerCount);
                        good_number.setText(registerNumber.MainData.ProductCount);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("first_login", true);
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

    /**
     * 保存帐号和密码
     *
     * @param account
     * @param pwd
     */
    public void saveLoginUserAccountAndPwd(String account, String pwd, int type) {
        PreferencesUtils.putString(BaseApplication.getInstance(), "account", account);
        PreferencesUtils.putString(BaseApplication.getInstance(), "pwd", pwd);
        PreferencesUtils.putInt(BaseApplication.getInstance(), "type", type);

    }
}
