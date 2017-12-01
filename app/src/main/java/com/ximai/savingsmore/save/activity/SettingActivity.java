package com.ximai.savingsmore.save.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.core.CoreJob;
import com.ximai.savingsmore.library.toolbox.PopupWindowFromBottomUtil;
import com.ximai.savingsmore.library.toolbox.PreferencesUtils;
import com.ximai.savingsmore.library.view.Form_item;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.common.BaseApplication;
import com.ximai.savingsmore.save.modle.BaseMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/12/15.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private Form_item number;
    private TextView xiugai_password, about_we, falu, toushu, login_out;
    private List<String> marker_number = new ArrayList<String>();
    private AlertDialog classity_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        setCenterTitle("设置");
        setLeftBackMenuVisibility(SettingActivity.this, "");
        number = (Form_item) findViewById(R.id.number);
        number.setOnClickListener(this);
        xiugai_password = (TextView) findViewById(R.id.xiugai_password);
        about_we = (TextView) findViewById(R.id.about_we);
        falu = (TextView) findViewById(R.id.falu);
        toushu = (TextView) findViewById(R.id.toushu);
        login_out = (TextView) findViewById(R.id.login_out);
        login_out.setOnClickListener(this);
        xiugai_password.setOnClickListener(this);
        about_we.setOnClickListener(this);
        falu.setOnClickListener(this);
        toushu.setOnClickListener(this);
        marker_number.add("10");
        marker_number.add("20");
        marker_number.add("30");
        marker_number.add("40");
        marker_number.add("50");
        number.setmTvRight(PreferencesUtils.getString(SettingActivity.this, "number"));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xiugai_password:
                Intent intent = new Intent(SettingActivity.this, ForgetPasswordActivity.class);
                intent.putExtra("title", "修改密码");
                startActivity(intent);
                break;
            case R.id.about_we:
                Intent intent1 = new Intent(SettingActivity.this, AboutWeActivity.class);
                startActivity(intent1);
                break;
            case R.id.falu:
                Intent intent2 = new Intent(SettingActivity.this, LowStateActivity.class);
                startActivity(intent2);
                break;
            case R.id.toushu:
                Intent intent3 = new Intent(SettingActivity.this, ComplainActivity.class);
                startActivity(intent3);
                break;
            case R.id.login_out:
                showDialog();
                break;
            case R.id.number:
                PopupWindowFromBottomUtil.shouWindowWithWheel(SettingActivity.this, LayoutInflater.from(SettingActivity.this).inflate(R.layout.business_my_center_activity, null), marker_number, new PopupWindowFromBottomUtil.Listener() {
                    @Override
                    public void confirm(String content, PopupWindow window) {
                        number.setmTvRight(content);
                        PreferencesUtils.putString(BaseApplication.getInstance(), "number", content);
                        window.dismiss();
                    }
                });
                break;
        }
    }

    private void showDialog() {
        classity_dialog = new AlertDialog.Builder(SettingActivity.this).create();
        View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.login_out_dialog, null);
        final TextView queding, quxiao;
        final EditText content;
        queding = (TextView) view.findViewById(R.id.commit);
        quxiao = (TextView) view.findViewById(R.id.cancel);
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoreJob.exitApplication();
                finish();
                classity_dialog.dismiss();
            }
        });
        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classity_dialog.dismiss();
            }
        });
        classity_dialog.setView(view);
        classity_dialog.show();
    }
}
