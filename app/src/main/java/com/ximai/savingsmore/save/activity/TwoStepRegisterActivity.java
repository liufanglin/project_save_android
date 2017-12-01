package com.ximai.savingsmore.save.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.save.common.BaseActivity;

/**
 * Created by caojian on 16/11/17.
 */
public class TwoStepRegisterActivity extends BaseActivity implements View.OnClickListener{
    private EditText phone,password,confin;
    private TextView nextStep;
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_register_activity);
        setLeftBackMenuVisibility(TwoStepRegisterActivity.this,"");
        setCenterTitle("注册");
        phone= (EditText) findViewById(R.id.number);
        password= (EditText) findViewById(R.id.password);
        confin= (EditText) findViewById(R.id.confirm);
        nextStep= (TextView) findViewById(R.id.next_step);
        nextStep.setOnClickListener(this);
        Intent intent=getIntent();
        if(null!=intent&&null!=intent.getStringExtra("number")){
            phone.setText(intent.getStringExtra("number"));
            type=intent.getStringExtra("type");
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.next_step){
            if(!TextUtils.isEmpty(phone.getText())&&phone.getText().toString().length()==11){
               if(!TextUtils.isEmpty(password.getText())&&!TextUtils.isEmpty(confin.getText())&&!TextUtils.isEmpty(confin.getText())&&password.getText().toString().equals(confin.getText().toString())){
                   Intent intent=new Intent(TwoStepRegisterActivity.this,ThereStepRegisterActivity.class);
                   intent.putExtra("number",phone.getText().toString());
                   intent.putExtra("type",type);
                   intent.putExtra("password",password.getText().toString());
                   startActivity(intent);
                   finish();
               }
                else {
                   Toast.makeText(TwoStepRegisterActivity.this,"密码和确认密码不一致",Toast.LENGTH_LONG).show();
               }
            }
            else {
                Toast.makeText(TwoStepRegisterActivity.this,"请输入11的手机号",Toast.LENGTH_LONG).show();
            }
        }
    }
}
