package com.ximai.savingsmore.save.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.save.common.BaseActivity;

/**
 * Created by caojian on 16/11/17.
 */
public class OneStepRegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText number;
    private TextView nextStep;
    private String type;
    private ImageView select;
    private TextView xieyi;
    private boolean select_state = false;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_register_activity);
        setLeftBackMenuVisibility(OneStepRegisterActivity.this, "");
        setCenterTitle("注册");
        number = (EditText) findViewById(R.id.input_number);
        nextStep = (TextView) findViewById(R.id.next_step);
        select = (ImageView) findViewById(R.id.select);
        xieyi = (TextView) findViewById(R.id.xieyi);
        nextStep.setOnClickListener(this);
        select.setOnClickListener(this);
        xieyi.setOnClickListener(this);
        Intent intent = getIntent();
        if (null != intent) {
            type = getIntent().getStringExtra("register_type");
            if (type.equals("2")) {
                xieyi.setText("已阅读并同意了《省又省用户服务协议》");
            } else {
                xieyi.setText("已阅读并同意了《省又省实体商户服务协议》");
            }
        }
        show();


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next_step) {
            Intent intent = new Intent(OneStepRegisterActivity.this, TwoStepRegisterActivity.class);
            if (!TextUtils.isEmpty(number.getText()) && number.getText().toString().length() == 11) {
                if (select_state) {
                    intent.putExtra("number", number.getText().toString());
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                } else {
                    if (type.equals("2")) {
                        Toast.makeText(OneStepRegisterActivity.this, "您还没有同意《省又省用户服务协议》", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(OneStepRegisterActivity.this, "您还没有同意《省又省实体商户服务协议》", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(OneStepRegisterActivity.this, "请输入11的手机号", Toast.LENGTH_LONG).show();
            }
        }
        if (v.getId() == R.id.xieyi) {
            if (type.equals("2")) {
                Intent intent = new Intent(OneStepRegisterActivity.this, PersonalAgreement.class);
                intent.putExtra("title", "《省又省个人用户服务协议》");
                intent.putExtra("type", "1");
                startActivity(intent);
            } else if (type.equals("3")) {
                Intent intent = new Intent(OneStepRegisterActivity.this, BUsinessAgreement.class);
                intent.putExtra("title", "“省又省”APP实体商户服务协议");
                intent.putExtra("type", "2");
                startActivity(intent);
            }
        }
        if (v.getId() == R.id.select) {
            if (select_state) {
                select_state = false;
                select.setBackgroundResource(R.mipmap.kuang);
            } else {
                select_state = true;
                select.setBackgroundResource(R.mipmap.select_kuang);
            }
        }
    }

    private void show() {
        alertDialog = new AlertDialog.Builder(OneStepRegisterActivity.this).create();
        View view = LayoutInflater.from(OneStepRegisterActivity.this).inflate(R.layout.register_nofity, null);
        final ImageView queding, quxiao;
        queding = (ImageView) view.findViewById(R.id.known);
        quxiao = (ImageView) view.findViewById(R.id.quxiao);
        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.show();
    }


}
