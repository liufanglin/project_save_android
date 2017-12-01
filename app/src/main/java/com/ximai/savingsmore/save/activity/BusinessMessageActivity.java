package com.ximai.savingsmore.save.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.easeui.EaseConstant;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.cache.MyImageLoader;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.fragment.BusinessIntroduceFragment;
import com.ximai.savingsmore.save.fragment.PersonFragment;
import com.ximai.savingsmore.save.fragment.SalesGoodsFragment;
import com.ximai.savingsmore.save.modle.BusinessMessage;
import com.ximai.savingsmore.save.modle.GoodDetial;
import com.ximai.savingsmore.save.modle.GoodSalesType;
import com.ximai.savingsmore.save.modle.ServiceList;
import com.ximai.savingsmore.save.modle.User;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/11/26
 */
//商家信息
public class BusinessMessageActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout linearLayout;
    private TextView business_introduce, business_sales;
    private View introduce, sales_good;
    private FragmentManager fragmentManager;
    private BusinessIntroduceFragment introduceFragment;
    private SalesGoodsFragment salesGoodsFragment;
    private FragmentTransaction fragmentTransaction;
    //private User user;
    private ImageView head_image;
    private TextView store_name;
    private TextView address;
    //private GoodDetial good;
    private ImageView focus;
    private Boolean isFocus = false;
    private BusinessMessage businessMessage;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_message_activity);
        setLeftBackMenuVisibility(BusinessMessageActivity.this, "");
        setCenterTitle("商家信息");
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        //good = (GoodDetial) intent.getSerializableExtra("good");
        // user = good.User;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        introduce = findViewById(R.id.introduce);
        sales_good = findViewById(R.id.cuxiao_goog);
        business_introduce = (TextView) findViewById(R.id.business_introduce);
        business_sales = (TextView) findViewById(R.id.business_sales);
        head_image = (ImageView) findViewById(R.id.head_image);
        store_name = (TextView) findViewById(R.id.store_name);
        address = (TextView) findViewById(R.id.address);
        focus = (ImageView) findViewById(R.id.foues);
        focus.setOnClickListener(this);
        business_introduce.setOnClickListener(this);
        business_sales.setOnClickListener(this);
        getBusinessMessage(id);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.business_introduce:
                introduce.setVisibility(View.VISIBLE);
                sales_good.setVisibility(View.INVISIBLE);
                fragmentManager.beginTransaction().hide(salesGoodsFragment).commit();
                fragmentManager.beginTransaction().show(introduceFragment).commit();
                break;
            case R.id.business_sales:
                introduce.setVisibility(View.INVISIBLE);
                sales_good.setVisibility(View.VISIBLE);
                fragmentManager.beginTransaction().hide(introduceFragment).commit();
                fragmentManager.beginTransaction().show(salesGoodsFragment).commit();
                break;
            case R.id.foues:
                if (null != isFocus && isFocus) {
                    if (null != businessMessage) {
                        cancelBusiness(businessMessage.Id);
                        isFocus = false;
                    }
                } else if (null != isFocus && isFocus == false) {
                    if (null != businessMessage) {
                        collectBusiness(businessMessage.Id);
                        isFocus = true;
                    }
                }
                break;
        }
    }

    //收藏
    private void collectBusiness(String id) {
        WebRequestHelper.json_post(BusinessMessageActivity.this, URLText.ADD_COLLECT_BUSINESS, RequestParamsPool.focusBusiness(id), new MyAsyncHttpResponseHandler(BusinessMessageActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {

                    focus.setBackgroundResource(R.mipmap.comment_star);
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.optString("Message");
                    Toast.makeText(BusinessMessageActivity.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    //移除收藏
    private void cancelBusiness(String id) {
        List<String> list = new ArrayList<String>();
        list.add(id);
        WebRequestHelper.json_post(BusinessMessageActivity.this, URLText.CANCEL_COLLECT_BUSINESS, RequestParamsPool.cancelBusiness("", list), new MyAsyncHttpResponseHandler(BusinessMessageActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    focus.setBackgroundResource(R.mipmap.comment_start_gray);
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.optString("Message");
                    Toast.makeText(BusinessMessageActivity.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getBusinessMessage(String id) {
        WebRequestHelper.json_post(BusinessMessageActivity.this, URLText.USER_DETIAL, RequestParamsPool.getBusinessMessage(id), new MyAsyncHttpResponseHandler(BusinessMessageActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                JSONObject object = null;
                try {
                    object = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String MainData = new String(object.optString("MainData"));
                businessMessage = GsonUtils.fromJson(MainData, BusinessMessage.class);
                MyImageLoader.displayDefaultImage(URLText.img_url + businessMessage.PhotoPath, head_image);
                store_name.setText(businessMessage.ShowName);
                if (null != businessMessage.City && null != businessMessage.Province) {
                    address.setText(businessMessage.Province.Name + " " + businessMessage.City.Name);
                } else {
                    address.setText("上海" + " " + "浦东新区");
                }
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("good", businessMessage);
                introduceFragment = new BusinessIntroduceFragment();
                salesGoodsFragment = new SalesGoodsFragment();
                introduceFragment.setArguments(bundle1);
                salesGoodsFragment.setArguments(bundle1);
                fragmentManager.beginTransaction().add(R.id.fragment, introduceFragment).commit();
                fragmentManager.beginTransaction().add(R.id.fragment, salesGoodsFragment).commit();
                fragmentManager.beginTransaction().show(introduceFragment).commit();
            }
        });
    }


}
