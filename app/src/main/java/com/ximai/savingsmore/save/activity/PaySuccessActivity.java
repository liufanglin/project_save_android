package com.ximai.savingsmore.save.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.library.toolbox.LogUtils;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.modle.PayResult;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by caojian on 17/1/5.
 */
public class PaySuccessActivity extends BaseActivity {
    private TextView shoukuanfang, order_number, pay_number, pay_type, pay_date, pay_jifen;
    private PayResult payResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_success_activity);
        setCenterTitle("支付成功");
        setLeftBackMenuVisibility(PaySuccessActivity.this, "");
        shoukuanfang = (TextView) findViewById(R.id.shoukuanfang);
        order_number = (TextView) findViewById(R.id.order_number);
        pay_number = (TextView) findViewById(R.id.pay_price);
        pay_type = (TextView) findViewById(R.id.pay_type);
        pay_date = (TextView) findViewById(R.id.pay_date);
        pay_jifen = (TextView) findViewById(R.id.pay_jifen);
        getPayResult(getIntent().getStringExtra("Id"));
    }

    private void getPayResult(String Id) {
        WebRequestHelper.json_post(PaySuccessActivity.this, URLText.PAY_RESULT, RequestParamsPool.payResult(Id), new MyAsyncHttpResponseHandler(PaySuccessActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                LogUtils.instance.d("支付结果=" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    payResult = GsonUtils.fromJson(object.optString("MainData"), PayResult.class);
                    if (null != payResult) {
                        shoukuanfang.setText(payResult.StoreName);
                        order_number.setText(payResult.Number);
                        pay_number.setText("￥" + payResult.Price);
                        pay_type.setText(payResult.PayAppName);
                        pay_date.setText(payResult.PayTimeName);
                        pay_jifen.setText("在确认收货后获得");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject object = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
