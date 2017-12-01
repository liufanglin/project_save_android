package com.ximai.savingsmore.save.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.LocationSource;
import com.easemob.easeui.EaseConstant;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.toolbox.PopupWindowFromBottomUtil;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.common.BaseApplication;
import com.ximai.savingsmore.save.modle.AppInfo;
import com.ximai.savingsmore.save.modle.BusinessMessage;
import com.ximai.savingsmore.save.modle.GoodDetial;
import com.ximai.savingsmore.save.modle.Location;
import com.ximai.savingsmore.save.modle.User;
import com.ximai.savingsmore.save.modle.UserExtInfo;
import com.ximai.savingsmore.save.utils.APPUtil;
import com.ximai.savingsmore.save.utils.NativeDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by caojian on 16/11/29.
 */
public class TakeMeActivity extends BaseActivity implements View.OnClickListener {
    private TextView Store_name;
    private TextView phone_number;
    private ImageView send_message, call;
    private TextView location;
    private TextView yingye_time;
    private TextView is_yingye;
    private User user;
    private UserExtInfo userExtInfo;
    private ImageView walk, bus, talk;
    private Location loc_now = null;
    private Location loc_end = null;
    private String type;
    private AppInfo baidu;
    private AppInfo gaode;
    private PopupWindow setIconWindow;
    private TextView mubiao_location;
    private BusinessMessage businessMessage;
    // new Location(30.862644, 103.663077, "e");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_me_activity);
        setCenterTitle("带我去");
        setLeftBackMenuVisibility(TakeMeActivity.this, "");
        Store_name = (TextView) findViewById(R.id.store_name);
        phone_number = (TextView) findViewById(R.id.phone_number);
        send_message = (ImageView) findViewById(R.id.send_message);
        call = (ImageView) findViewById(R.id.phone);
        mubiao_location = (TextView) findViewById(R.id.mubiao_location);
        call.setOnClickListener(this);
        location = (TextView) findViewById(R.id.location);
        yingye_time = (TextView) findViewById(R.id.yingye_time);
        is_yingye = (TextView) findViewById(R.id.is_yingye);
        walk = (ImageView) findViewById(R.id.walk);
        bus = (ImageView) findViewById(R.id.bus);
        talk = (ImageView) findViewById(R.id.talk);
        walk.setOnClickListener(this);
        bus.setOnClickListener(this);
        talk.setOnClickListener(this);
        send_message.setOnClickListener(this);
        if (getIntent().getStringExtra("isgood").equals("true")) {
            user = (User) getIntent().getSerializableExtra("good");
            userExtInfo = user.UserExtInfo;
        } else {
            businessMessage = (BusinessMessage) getIntent().getSerializableExtra("good");
            userExtInfo = businessMessage.UserExtInfo;
        }

        if (null != user && null != userExtInfo) {
            Store_name.setText(userExtInfo.StoreName);
            phone_number.setText(userExtInfo.OfficePhone);
            if (null != user.Area && null != user.Province && null != user.City) {
                location.setText(user.Province.Name + user.City.Name + user.Area.Name + user.Domicile);
            } else if (null != user.Province && null != user.City) {
                location.setText(user.Province.Name + user.City.Name + user.Domicile);
            }
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            int a = Integer.parseInt(str.split(":")[0]);
            if (a > Integer.parseInt(userExtInfo.StartHours.split(":")[0]) && a < Integer.parseInt(userExtInfo.EndHours.split(":")[0])) {
                is_yingye.setText("营业中");
            } else {
                is_yingye.setText("暂停营业");
            }
            yingye_time.setText(userExtInfo.StartHours + "-" + userExtInfo.EndHours);
            mubiao_location.setText(user.Domicile);
            loc_end = new Location(Double.parseDouble(user.Latitude), Double.parseDouble(user.Longitude));
            loc_now = new Location(BaseApplication.getInstance().Longitude, BaseApplication.getInstance().Latitude);
        } else if (null != businessMessage && null != userExtInfo) {
            Store_name.setText(businessMessage.ShowName);
            phone_number.setText(userExtInfo.OfficePhone);
            if (null != businessMessage.Area) {
                location.setText(businessMessage.Province.Name + businessMessage.City.Name + businessMessage.Area.Name + businessMessage.Domicile);
            } else {
                location.setText(businessMessage.Province.Name + businessMessage.City.Name + businessMessage.Domicile);
            }
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            int a = Integer.parseInt(str.split(":")[0]);
            if (a > Integer.parseInt(userExtInfo.StartHours.split(":")[0]) && a < Integer.parseInt(userExtInfo.EndHours.split(":")[0])) {
                is_yingye.setText("营业中");
            } else {
                is_yingye.setText("暂停营业");
            }
            yingye_time.setText(userExtInfo.StartHours + "-" + userExtInfo.EndHours);
            mubiao_location.setText(businessMessage.Domicile);
            //终点
            loc_end = new Location(Double.parseDouble(businessMessage.Longitude), Double.parseDouble(businessMessage.Latitude));
            loc_now = new Location(BaseApplication.getInstance().Longitude, BaseApplication.getInstance().Latitude);
        }
        baidu = APPUtil.getAppInfoByPak(TakeMeActivity.this, "com.baidu.BaiduMap");
        gaode = APPUtil.getAppInfoByPak(TakeMeActivity.this, "com.autonavi.minimap");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message:
                Intent send = new Intent(TakeMeActivity.this, ChatActivity.class);
                if (null != user) {
                    send.putExtra(EaseConstant.EXTRA_USER_ID, user.IMUserName);
                } else {
                    send.putExtra(EaseConstant.EXTRA_USER_ID, businessMessage.IMUserName);
                }
                startActivity(send);
                break;
            case R.id.phone:
                if (null != user.PhoneNumber) {
                    Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.PhoneNumber));
                    startActivity(in);
                } else if (null != businessMessage.PhoneNumber) {
                    Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + businessMessage.PhoneNumber));
                    startActivity(in);
                }
                break;
            case R.id.talk:
                if (baidu == null && gaode == null) {
                    Toast.makeText(TakeMeActivity.this, "您手机上还没有安装导航App,请到应用市场下载", Toast.LENGTH_SHORT).show();
                } else {
                    type = "driving";
                    showSetIconWindow();
                }
                break;
            case R.id.bus:
                if (baidu == null && gaode == null) {
                    Toast.makeText(TakeMeActivity.this, "您手机上还没有安装导航App,请到应用市场下载", Toast.LENGTH_SHORT).show();
                } else {
                    type = "transit";
                    showSetIconWindow();
                }
                break;
            case R.id.walk:
                if (baidu == null && gaode == null) {
                    Toast.makeText(TakeMeActivity.this, "您手机上还没有安装导航App,请到应用市场下载", Toast.LENGTH_SHORT).show();
                } else {
                    type = "walking";
                    showSetIconWindow();
                }
                break;
            case R.id.btnCamera:
                setIconWindow.dismiss();
                startNative_Baidu(loc_now, loc_end, type);
                break;
            case R.id.btnAlbum:
                setIconWindow.dismiss();
                startNative_Gaode(loc_end);
                break;
            case R.id.btnCancel:
                setIconWindow.dismiss();
                break;
        }

    }

    private void showSetIconWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.view_daohang, null);
        View parentView = LayoutInflater.from(this).inflate(R.layout.take_me_activity, null);


        setIconWindow = PopupWindowFromBottomUtil.showWindow(contentView, parentView, this);

        Button btnCancel = (Button) contentView.findViewById(R.id.btnCancel);
        Button btnCamera = (Button) contentView.findViewById(R.id.btnCamera);
        Button btnAlbum = (Button) contentView.findViewById(R.id.btnAlbum);


        if (null == baidu) {
            btnCamera.setVisibility(View.GONE);
        } else {
            btnCamera.setVisibility(View.VISIBLE);
        }
        if (null == gaode) {
            btnAlbum.setVisibility(View.GONE);
        } else {
            btnAlbum.setVisibility(View.VISIBLE);
        }

        btnCancel.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
    }

    public void startNative_Baidu(Location loc1, Location loc2, String type) {
        if (loc1 == null || loc2 == null) {
            return;
        }
        if (loc1.getAddress() == null || "".equals(loc1.getAddress())) {
            loc1.setAddress("我的位置");
        }
        if (loc2.getAddress() == null || "".equals(loc2.getAddress())) {
            loc2.setAddress("目的地");
        }
        try {
            Intent intent = Intent.getIntent("intent://map/direction?origin=latlng:" + loc1.getStringLatLng() + "|name:" + loc1.getAddress() + "&destination=latlng:" + loc2.getStringLatLng() + "|name:" + loc2.getAddress() + "&mode=" + type + "&src=重庆快易科技|CC房车-车主#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(TakeMeActivity.this, "地址解析错误", Toast.LENGTH_SHORT).show();
        }
    }

    public void startNative_Gaode(Location loc) {
        if (loc == null) {
            return;
        }
        if (loc.getAddress() == null || "".equals(loc.getAddress())) {
            loc.setAddress("目的地");
        }
        try {
            Intent intent = new Intent("android.intent.action.VIEW",
                    android.net.Uri.parse("androidamap://navi?sourceApplication=CC房车-车主&poiname=重庆快易科技&lat=" + loc.getLat() + "&lon=" + loc.getLng() + "&dev=0&style=0"));
            intent.setPackage("com.autonavi.minimap");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(TakeMeActivity.this, "地址解析错误", Toast.LENGTH_SHORT).show();
        }
    }
}
