package com.ximai.savingsmore.save.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.modle.PointMessage;
import com.ximai.savingsmore.save.modle.RedPacketCount;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by caojian on 16/12/28.
 */
public class PointManagerActivity extends BaseActivity {
    private TextView share_all, share_zhekou, share_liucun, share_guzhe;
    private TextView buy_all, buy_zhekou, buy_liucun, buy_guzhe;
    private TextView commnet_all, comment_zhekou, comment_liucun, comment_guzhe;
    private TextView heji_all, heji_zhekou, heji_liucun, heji_guzhe;
    private PointMessage pointMessage;
    private ImageView jinpai, yingpai, tongpai;
    private TextView text;
    private TextView pagket;
    private RedPacketCount redPacketCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_manager_activity);
        setCenterTitle("积分管理");
        setLeftBackMenuVisibility(PointManagerActivity.this, "");
        share_all = (TextView) findViewById(R.id.share_all);
        share_zhekou = (TextView) findViewById(R.id.share_zhekou);
        share_liucun = (TextView) findViewById(R.id.share_liucun);
        share_guzhe = (TextView) findViewById(R.id.share_guizhe);
        buy_all = (TextView) findViewById(R.id.buy_all);
        buy_zhekou = (TextView) findViewById(R.id.buy_zhekou);
        buy_liucun = (TextView) findViewById(R.id.buy_liucun);
        buy_guzhe = (TextView) findViewById(R.id.buy_guizhe);
        commnet_all = (TextView) findViewById(R.id.comment_all);
        comment_zhekou = (TextView) findViewById(R.id.comment_zhekou);
        comment_liucun = (TextView) findViewById(R.id.comment_liucun);
        comment_guzhe = (TextView) findViewById(R.id.comment_guizhe);
        heji_all = (TextView) findViewById(R.id.heji_all);
        heji_zhekou = (TextView) findViewById(R.id.heji_zhekou);
        heji_liucun = (TextView) findViewById(R.id.heji_liucun);
        heji_guzhe = (TextView) findViewById(R.id.heji_guizhe);
        jinpai = (ImageView) findViewById(R.id.jinpai);
        yingpai = (ImageView) findViewById(R.id.yingpai);
        tongpai = (ImageView) findViewById(R.id.tongpai);
        text = (TextView) findViewById(R.id.text);
        pagket = (TextView) findViewById(R.id.packet);
        text.setText("4.\"省又省\" 保留解释权和更改权利");
        getPoint();
        getRedPacket();
    }

    private void getPoint() {
        WebRequestHelper.json_post(PointManagerActivity.this, URLText.GET_POINT, RequestParamsPool.getPoint(), new MyAsyncHttpResponseHandler(PointManagerActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    pointMessage = GsonUtils.fromJson(object.optString("MainData"), PointMessage.class);
                    if (null != pointMessage) {
                        share_all.setText(pointMessage.SharedPoint);
                        share_liucun.setText("0");
                        share_zhekou.setText("0");
                        share_guzhe.setText(pointMessage.SharedExchangeMethod);
                        buy_all.setText(pointMessage.OrderPoint);
                        buy_liucun.setText("0");
                        buy_zhekou.setText("0");
                        buy_guzhe.setText(pointMessage.OrderExchangeMethod);
                        commnet_all.setText(pointMessage.CommentPoint);
                        comment_liucun.setText("0");
                        comment_zhekou.setText("0");
                        comment_guzhe.setText(pointMessage.CommentExchangeMethod);
                        heji_liucun.setText(pointMessage.Point);
                        heji_zhekou.setText(pointMessage.UsedPoint);
                        heji_all.setText(pointMessage.TotalPoint);
                        // heji_guzhe.setText(pointMessage.OrderExchangeMethod);
                        if (Integer.parseInt(pointMessage.Point) > 1000) {
                            jinpai.setVisibility(View.VISIBLE);
                        } else if (Integer.parseInt(pointMessage.Point) > 699) {
                            yingpai.setVisibility(View.VISIBLE);
                        } else {
                            tongpai.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void getRedPacket() {
        WebRequestHelper.json_post(PointManagerActivity.this, URLText.redPacket, RequestParamsPool.getPoint(), new MyAsyncHttpResponseHandler(PointManagerActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject object = new JSONObject(result);
                    redPacketCount = GsonUtils.fromJson(result, RedPacketCount.class);
                    if (redPacketCount.MainData==0) {
                        pagket.setVisibility(View.GONE);
                    } else {
                        pagket.setText( redPacketCount.MainData+"个红包:");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
