package com.ximai.savingsmore.save.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.core.CoreJob;
import com.ximai.savingsmore.library.toolbox.LogUtils;
import com.ximai.savingsmore.library.toolbox.PreferencesUtils;
import com.ximai.savingsmore.save.fragment.BusinessFragment;
import com.ximai.savingsmore.save.fragment.MapFragment;
import com.ximai.savingsmore.save.fragment.PersonFragment;
import com.ximai.savingsmore.save.modle.MyUserInfoUtils;
import com.ximai.savingsmore.save.utils.EventCenter;

import java.util.Observable;
import java.util.Observer;


/**
 * Created by caojian on 16/11/21.
 */
public class MainActivity extends SlidingFragmentActivity implements View.OnClickListener, EMEventListener {
    private PersonFragment personFragment;
    private FragmentManager manager;
    private MapFragment mapFragment;
    public SlidingMenu sm;
    private ImageView login, search;
    private TextView location;
    String city1;
    double Longitude1 = 0;
    double Latitude1 = 0;
    private boolean first;
    private AlertDialog alertDialog1, alertDialog2;
    private Handler mHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CoreJob.addToActivityStack(this);
        login = (ImageView) findViewById(R.id.login);
        login.setOnClickListener(this);
        search = (ImageView) findViewById(R.id.search);
        location = (TextView) findViewById(R.id.location);
        search.setOnClickListener(this);
        first = getIntent().getBooleanExtra("first_login", false);
        //注册聊天监听
        registerEventListener();
        // EMChatManager.getInstance().unregisterEventListener(this);
        //  EventCenter.getInstance().addObserver(this);
        // mActivity = this;
        manager = getSupportFragmentManager();

        // check if the content frame contains the menu frame
        if (findViewById(R.id.menu_frame) == null) {
            setBehindContentView(R.layout.menu_frame);
            getSlidingMenu().setSlidingEnabled(true);
//			getSlidingMenu()
//					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        } else {
            // add a dummy view
            View v = new View(this);
            setBehindContentView(v);
            getSlidingMenu().setSlidingEnabled(false);
            //getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

        getSlidingMenu().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        // set the Above View Fragment
//		if (savedInstanceState != null) {
//			if(null!=manager)
//			mContent = (com.shopex.westore.activity.MainTabContentFragment)manager.getFragment(savedInstanceState, "mContent");
//		}

        if (mapFragment == null) {
            mapFragment = new MapFragment();
            mapFragment.setDate(MainActivity.this, new MapFragment.CallBack() {
                @Override
                public void location(String city, double Longitude, double Latitude) {
                    location.setText(city);
                    city1 = city;
                    Longitude1 = Longitude;
                    Latitude1 = Latitude;
                }
            });
        }
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mapFragment).commit();

        // set the Behind View Fragment
        if (null != MyUserInfoUtils.getInstance().myUserInfo && MyUserInfoUtils.getInstance().myUserInfo.UserType.equals("3")) {
            if (first) {
                show_business();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.menu_frame, new BusinessFragment()).commit();
        } else {
            if (first) {
                show_person();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.menu_frame, new PersonFragment()).commit();
        }

        // customize the SlidingMenu
        sm = getSlidingMenu();
        WindowManager wm = (WindowManager) this.getWindowManager();
        sm.setBehindOffset(wm.getDefaultDisplay().getWidth() * 1 / 5);
//		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeEnabled(false);
        sm.setBehindScrollScale(0.25f);
        sm.setFadeDegree(0.25f);
        sm.setBackgroundResource(R.mipmap.point_default);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            if (sm != null) {
                if (!sm.isShown()) {
                    sm.showContent();
                } else {
                    sm.showMenu();
                }
            }
        }
        if (R.id.list == v.getId()) {
            if (Longitude1 != 0 && 0 != Latitude1) {
                Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                intent.putExtra("long", Longitude1);
                intent.putExtra("lat", Latitude1);
                intent.putExtra("title", city1);
                startActivity(intent);
            }
        }
        if (R.id.search == v.getId()) {
            Intent intent1 = new Intent(MainActivity.this, SearchActivity.class);
            intent1.putExtra("title", city1);
            startActivity(intent1);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void show_person() {
        alertDialog1 = new AlertDialog.Builder(MainActivity.this).create();
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.person_nofity, null);
        final ImageView queding, quxiao;
        queding = (ImageView) view.findViewById(R.id.known);
        quxiao = (ImageView) view.findViewById(R.id.quxiao);
        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        alertDialog1.setView(view);
        alertDialog1.show();
    }

    private void show_business() {
        alertDialog1 = new AlertDialog.Builder(MainActivity.this).create();
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.business_notify, null);
        final ImageView queding, quxiao;
        queding = (ImageView) view.findViewById(R.id.known);
        quxiao = (ImageView) view.findViewById(R.id.quxiao);
        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddGoodsAcitivyt.class);
                startActivity(intent);
                alertDialog1.dismiss();

            }
        });

        alertDialog1.setView(view);
        alertDialog1.show();
    }

    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {
        int message = PreferencesUtils.getInt(MainActivity.this, MyUserInfoUtils.getInstance().myUserInfo.NickName, 0);
        message = message + 1;
        PreferencesUtils.putInt(MainActivity.this, MyUserInfoUtils.getInstance().myUserInfo.NickName, message);
        Message msg = mHandler.obtainMessage();
        if (MyUserInfoUtils.getInstance().myUserInfo.UserType.equals("2")) {
            msg.what = 0;
        } else {
            msg.what = 1;
        }
        msg.obj = message;
        mHandler.sendMessage(msg);
    }


    private void registerEventListener() {

        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});


    }


    public void setHandler(Handler handler) {
        mHandler = handler;
    }

}
