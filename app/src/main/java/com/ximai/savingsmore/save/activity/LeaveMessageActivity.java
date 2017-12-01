package com.ximai.savingsmore.save.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
 * Created by caojian on 16/12/19.
 */
public class LeaveMessageActivity extends BaseActivity implements View.OnClickListener {
    private EditText name, city, phone, email, qq_wechat, content;
    private TextView submit;
    private String id, t_name, t_city, t_phone, t_email, t_qq, t_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leave_message_activity);
        setCenterTitle("请留言");
        setLeftBackMenuVisibility(LeaveMessageActivity.this, "");
        name = (EditText) findViewById(R.id.name);
        city = (EditText) findViewById(R.id.l_city);
        phone = (EditText) findViewById(R.id.number);
        email = (EditText) findViewById(R.id.email);
        qq_wechat = (EditText) findViewById(R.id.qq_wechat);
        content = (EditText) findViewById(R.id.leave_message);
        submit = (TextView) findViewById(R.id.submit_message);
        submit.setOnClickListener(this);
        id = getIntent().getStringExtra("Id");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_message:
                t_name = name.getText().toString();
                t_city = city.getText().toString();
                t_phone = phone.getText().toString();
                t_email = email.getText().toString();
                t_qq = qq_wechat.getText().toString();
                t_content = content.getText().toString();
                if (null != t_name && !TextUtils.isEmpty(t_name) && null != t_city && !TextUtils.isEmpty(t_city) && null != t_phone && !TextUtils.isEmpty(t_phone) && null != t_email && !TextUtils.isEmpty(t_email) && null != t_qq && !TextUtils.isEmpty(t_qq) && null != t_content && !TextUtils.isEmpty(t_content)) {
                    leave_message();
                } else {
                    Toast.makeText(LeaveMessageActivity.this, "信息没有填写完整", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void leave_message() {
        WebRequestHelper.json_post(LeaveMessageActivity.this, URLText.SEND_MESSAGE, RequestParamsPool.sendMessage(id, t_name, t_city, t_phone, t_email, t_qq, t_content), new MyAsyncHttpResponseHandler(LeaveMessageActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    String message = object.optString("Message");
                    String isSucess = object.optString("IsSuccess");
                    if ((isSucess.equals("true"))) {
                        finish();
                    }
                    Toast.makeText(LeaveMessageActivity.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
