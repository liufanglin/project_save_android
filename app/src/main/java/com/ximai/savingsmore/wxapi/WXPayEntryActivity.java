package com.ximai.savingsmore.wxapi;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.library.toolbox.LogUtils;
import com.ximai.savingsmore.save.activity.PaySuccessActivity;
import com.ximai.savingsmore.save.common.BaseActivity;

/**
 * Created by caojian on 17/1/11.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ximai.savingsmore.save.common.BaseApplication;
import com.ximai.savingsmore.save.modle.PayResult;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;
    private String Id;

    private TextView shoukuanfang, order_number, pay_number, pay_type, pay_date, pay_jifen;
    private PayResult payResult;
    private LinearLayout pay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, "wx9d2fb51599ac7698");
        api.handleIntent(getIntent(), this);
        setContentView(R.layout.pay_success_activity);
        setCenterTitle("支付成功");
        setLeftBackMenuVisibility(WXPayEntryActivity.this, "");
        shoukuanfang = (TextView) findViewById(R.id.shoukuanfang);
        order_number = (TextView) findViewById(R.id.order_number);
        pay_number = (TextView) findViewById(R.id.pay_price);
        pay_type = (TextView) findViewById(R.id.pay_type);
        pay_date = (TextView) findViewById(R.id.pay_date);
        pay_jifen = (TextView) findViewById(R.id.pay_jifen);
        pay = (LinearLayout) findViewById(R.id.pay);
    }

    public void setOrderId(String Id) {
        this.Id = Id;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                getPayResult(BaseApplication.getInstance().OrderId);
            } else if (resp.errCode == -2) {
                // pay.setVisibility(View.GONE);
                setCenterTitle("支付失败");
                Toast.makeText(this, "没有交易", Toast.LENGTH_SHORT).show();
                Toast.makeText(WXPayEntryActivity.this, "没有交易", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // pay.setVisibility(View.GONE);
                setCenterTitle("支付失败");
                Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
                Toast.makeText(WXPayEntryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getPayResult(String Id) {
        LogUtils.instance.d("Id+" + Id);
        WebRequestHelper.json_post(WXPayEntryActivity.this, URLText.PAY_RESULT, RequestParamsPool.payResult(Id), new MyAsyncHttpResponseHandler(WXPayEntryActivity.this) {
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
            }
        });
    }
}