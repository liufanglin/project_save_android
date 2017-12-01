package com.ximai.savingsmore.save.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.save.common.BaseActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by caojian on 16/11/17.
 */
public class ForgetPasswordActivity  extends BaseActivity implements View.OnClickListener{
    private EditText phonenumber;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_activity);
        setLeftBackMenuVisibility(ForgetPasswordActivity.this,"");
        setCenterTitle(getIntent().getStringExtra("title"));
        phonenumber= (EditText) findViewById(R.id.phone_number);
        button= (Button) findViewById(R.id.find_password);
        button.setText(getIntent().getStringExtra("title"));
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.find_password){
            if(!TextUtils.isEmpty(phonenumber.getText())&&!phonenumber.getText().equals("请输入注册时的手机号码")&&phonenumber.getText().length()==11){
                sendCode(phonenumber.getText().toString());
                Intent intent=new Intent(ForgetPasswordActivity.this,ResetPasswordActivity.class);
                intent.putExtra("phone",phonenumber.getText().toString());
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(ForgetPasswordActivity.this,"请输入11的手机号码",Toast.LENGTH_LONG).show();
            }

        }
    }

    //发送验证码
    private void sendCode(String number) {
        WebRequestHelper.post(URLText.SEND_CODE, RequestParamsPool.getCodeParams(number,2), new MyAsyncHttpResponseHandler(ForgetPasswordActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result=new String(responseBody);
                try {
                    JSONObject object=new JSONObject(new String(responseBody));
                    String message=object.optString("Message");
                    Boolean isLogin=object.optBoolean("IsSuccess");
                    Toast.makeText(ForgetPasswordActivity.this,message,Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
