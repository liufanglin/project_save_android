package com.ximai.savingsmore.save.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.easeui.EaseConstant;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.FileSystem.FileSystem;
import com.ximai.savingsmore.library.cache.MyImageLoader;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.library.toolbox.LogUtils;
import com.ximai.savingsmore.library.toolbox.PopupWindowFromBottomUtil;
import com.ximai.savingsmore.library.view.CircleFlowIndicator;
import com.ximai.savingsmore.library.view.FlowView;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.modle.BaseMessage;
import com.ximai.savingsmore.save.modle.CartDetail;
import com.ximai.savingsmore.save.modle.GoodDetial;
import com.ximai.savingsmore.save.modle.GoodSalesType;
import com.ximai.savingsmore.save.modle.Goods;
import com.ximai.savingsmore.save.modle.Images;
import com.ximai.savingsmore.save.modle.MyUserInfoUtils;
import com.ximai.savingsmore.save.modle.ServiceList;
import com.ximai.savingsmore.save.modle.ShareData;
import com.ximai.savingsmore.save.utils.ShareUtils;


import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/11/22.
 */
// 商品详情页
public class GoodDetailsActivity extends Activity implements View.OnClickListener {
    private String id;
    private ScrollView scrollView;
    private LinearLayout business_message;
    GoodDetial goodDetial;
    private FlowView mTopAdsView;
    private ImageView back, share, collect, big_imae, message, phone, comment1, comment2, comment3, comment4, comment5;
    private TextView name, price, high_price, dazhe_style, start_time, end_time, comment_number,tv_cuxiao_name,
            store_name, location, distance, pingpai, danwei, style, reson, bizhong, explain, descript, flow_me, service, score,tv_more;
    private Boolean isFavourite;
    private RelativeLayout comment;
    private LinearLayout comment_score;
    private ImageView send_message;
    private TextView servise;
    private PopupWindow setIconWindow;
    private ShareUtils shareUtils = null;
    private TextView buy;
    private TextView sales_count;
    private List<GoodSalesType> Serive_list = new ArrayList<GoodSalesType>();
    List<Goods> list = new ArrayList<Goods>();
    private CartDetail cartDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.good_details_activity);
        back = (ImageView) findViewById(R.id.back);
        share = (ImageView) findViewById(R.id.share);
        collect = (ImageView) findViewById(R.id.shouchang);
        mTopAdsView = (FlowView) findViewById(R.id.big_image);
        message = (ImageView) findViewById(R.id.send_message);
        phone = (ImageView) findViewById(R.id.phone);
        phone.setOnClickListener(this);
        name = (TextView) findViewById(R.id.name);
        price = (TextView) findViewById(R.id.price);
        high_price = (TextView) findViewById(R.id.high_price);
        dazhe_style = (TextView) findViewById(R.id.dazhe_style);
        start_time = (TextView) findViewById(R.id.start_time);
        end_time = (TextView) findViewById(R.id.end_time);
        comment_number = (TextView) findViewById(R.id.comment_number);
        store_name = (TextView) findViewById(R.id.store_name);
        location = (TextView) findViewById(R.id.location);
        tv_cuxiao_name= (TextView) findViewById(R.id.tv_cuxiao_name);
        tv_more= (TextView) findViewById(R.id.tv_more);
        distance = (TextView) findViewById(R.id.distance);
        pingpai = (TextView) findViewById(R.id.pingpai);
        danwei = (TextView) findViewById(R.id.danwei);
        bizhong = (TextView) findViewById(R.id.bizhong);
        style = (TextView) findViewById(R.id.style);
        reson = (TextView) findViewById(R.id.resonse);
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        explain = (TextView) findViewById(R.id.explain);
        descript = (TextView) findViewById(R.id.describe);
        flow_me = (TextView) findViewById(R.id.flow_me);
        service = (TextView) findViewById(R.id.servise);
        score = (TextView) findViewById(R.id.score);
        comment = (RelativeLayout) findViewById(R.id.comment);
        comment_score = (LinearLayout) findViewById(R.id.comment_score);
        send_message = (ImageView) findViewById(R.id.send_message);
        servise = (TextView) findViewById(R.id.servise);
        sales_count = (TextView) findViewById(R.id.sales_number);
        buy = (TextView) findViewById(R.id.buy);
        if (MyUserInfoUtils.getInstance().myUserInfo.UserType.equals("3")) {
            buy.setVisibility(View.GONE);
        }
        buy.setOnClickListener(this);
        share.setOnClickListener(this);
        service.setOnClickListener(this);
        send_message.setOnClickListener(this);
        comment_score.setOnClickListener(this);
        comment.setOnClickListener(this);
        business_message = (LinearLayout) findViewById(R.id.business_message);
        business_message.setOnClickListener(this);
        flow_me.setOnClickListener(this);
        collect.setOnClickListener(this);
        high_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        scrollView.setVerticalScrollBarEnabled(false);
        back.setOnClickListener(this);
        tv_more.setOnClickListener(this);
        id = getIntent().getStringExtra("id");
        if (null != getIntent().getStringExtra("isCar") && getIntent().getStringExtra("isCar").equals("true")) {
            buy.setText("加入购物车");
        }
        getgood_detial(id);
        startAutoRun();

    }

    private void getgood_detial(String Id) {
        WebRequestHelper.json_post(GoodDetailsActivity.this, URLText.GET_GOOD_DETIAL, RequestParamsPool.getGoodDetial(Id), new MyAsyncHttpResponseHandler(GoodDetailsActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(result);
                    Boolean isSuccess = object.optBoolean("IsSuccess");
                    String MainData = object.optString("MainData");
                    goodDetial = GsonUtils.fromJson(MainData, GoodDetial.class);
                    if (null != goodDetial) {
                        if (null != goodDetial.Name) {
                            name.setText(goodDetial.Name);
                            tv_cuxiao_name.setText("促销品名: "+goodDetial.Name);
                        }
                        sales_count.setText("销量:" + goodDetial.SaleCount);
                        price.setText("￥" + goodDetial.Price);
                        high_price.setText("原价: " + goodDetial.OriginalPrice);
                        dazhe_style.setText(goodDetial.Preferential);
                        comment_number.setText(goodDetial.CommentCount + "人评论");
                        location.setText("促销地点: "+goodDetial.Address);
                        pingpai.setText(goodDetial.Name);
                        danwei.setText(goodDetial.Unit.Name);
                        bizhong.setText(goodDetial.Currency.Name);
                        style.setText(goodDetial.PromotionTypeName);
                        if (null != goodDetial.PromotionCause) {
                            reson.setText(goodDetial.PromotionCause.Name);
                        }
                        explain.setText(goodDetial.Introduction);
                        descript.setText(goodDetial.Description);
                        start_time.setText(goodDetial.StartTimeName);
                        end_time.setText(goodDetial.EndTimeName);
                        store_name.setText("商家名称: "+goodDetial.User.ShowName);
                        distance.setText(goodDetial.Distance);
                        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                                50, 50);
                        layout.setMargins(5, 0, 5, 0);
                        if (goodDetial.CommentCount.equals("0")) {
                            score.setText(" 0 分");
                            for (int i = 0; i < 5; i++) {
                                ImageView imageView = new ImageView(GoodDetailsActivity.this);
                                imageView.setLayoutParams(layout);
                                imageView.setBackgroundResource(R.mipmap.comment_start_gray);
                                comment_score.addView(imageView);
                            }
                        } else {
                            score.setText(goodDetial.Score + "分");
                            if (goodDetial.Score.length() > 1) {
                                int score1 = Integer.parseInt(goodDetial.Score.substring(0, 1));
                                for (int i = 0; i < score1; i++) {
                                    ImageView imageView = new ImageView(GoodDetailsActivity.this);
                                    imageView.setLayoutParams(layout);
                                    imageView.setBackgroundResource(R.mipmap.comment_star);
                                    comment_score.addView(imageView);
                                }
                                ImageView imageView = new ImageView(GoodDetailsActivity.this);
                                imageView.setLayoutParams(layout);
                                imageView.setBackgroundResource(R.mipmap.start_half);
                                comment_score.addView(imageView);

                                for (int i = 0; i < 5 - score1 - 1; i++) {
                                    ImageView imageView1 = new ImageView(GoodDetailsActivity.this);
                                    imageView1.setLayoutParams(layout);
                                    imageView1.setBackgroundResource(R.mipmap.comment_start_gray);
                                    comment_score.addView(imageView1);
                                }
                            } else {
                                int score1 = Integer.parseInt(goodDetial.Score);
                                for (int i = 0; i < score1; i++) {
                                    ImageView imageView = new ImageView(GoodDetailsActivity.this);
                                    imageView.setLayoutParams(layout);
                                    imageView.setBackgroundResource(R.mipmap.comment_star);
                                    comment_score.addView(imageView);
                                }

                                for (int i = 0; i < 5 - score1; i++) {
                                    ImageView imageView = new ImageView(GoodDetailsActivity.this);
                                    imageView.setLayoutParams(layout);
                                    imageView.setBackgroundResource(R.mipmap.comment_start_gray);
                                    comment_score.addView(imageView);
                                }
                            }
                        }


                        if (goodDetial.IsFavourite) {
                            isFavourite = true;
                            collect.setBackgroundResource(R.mipmap.comment_star);
                        } else {
                            isFavourite = false;
                            collect.setBackgroundResource(R.mipmap.shouchang_white);
                        }

                        reloadMainView(goodDetial.Images);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //轮播图适配器
    private class FlowAdapter extends BaseAdapter {
        private Context mContext;
        private List<Images> mTopAdsArray;

        public FlowAdapter(List<Images> mTopAdsArray) {
            this.mTopAdsArray = mTopAdsArray;
            // this.mContext = context;
        }

        @Override
        public int getCount() {
            return mTopAdsArray == null ? 0 : mTopAdsArray.size();
        }

        @Override
        public Images getItem(int position) {
            return mTopAdsArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                ImageView view = new ImageView(GoodDetailsActivity.this);
                view.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                //view.setOnClickListener(mAdViewClickListener);
                convertView = view;
            }

            String image = mTopAdsArray.get(position).ImagePath;
            //JSONObject topAdsObject = getItem(position);
            Uri imageUri = Uri.parse(image);
            convertView.setTag(R.id.tag_object, image);
            convertView.setTag(imageUri);
            MyImageLoader.displayDefaultImage(URLText.img_url + image, (ImageView) convertView);

            return convertView;
        }
    }

    /**
     * 设置自动滚动
     */
    public final int MESSAGE_AUTO_SNAP_FLOWVIEW = 1;
    private final long INTERVAL_AUTO_SNAP_FLOWVIEW = 2000L;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_AUTO_SNAP_FLOWVIEW) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_AUTO_SNAP_FLOWVIEW, INTERVAL_AUTO_SNAP_FLOWVIEW);
                int count = mTopAdsView.getViewsCount();
                if (count > 1) {
                    int curScreen = mTopAdsView.getSelectedItemPosition();
                    if (curScreen >= (count - 1)) {
                        mTopAdsView.smoothScrollToScreen(0);

                    } else {
                        mTopAdsView.smoothScrollToScreen(curScreen + 1);
                    }
                }
            }
        }
    };

    private void reloadMainView(List<Images> mTopAdsArray) {
        if (mTopAdsArray != null && mTopAdsArray.size() > 0) {
//         添加轮播图
            mTopAdsView.setAdapter(new FlowAdapter(mTopAdsArray));
//
        }
    }

    //开始自动滚动
    public void startAutoRun() {
        mHandler.sendEmptyMessage(MESSAGE_AUTO_SNAP_FLOWVIEW);
    }

    ;

    //停止滚动
    public void stopAutoRun() {
        mHandler.removeMessages(MESSAGE_AUTO_SNAP_FLOWVIEW);
    }


//    // 添加广告指示器
//    private void addTopAdIndicator() {
//        CircleFlowIndicator mTopAdsIndicator = (CircleFlowIndicator) adsFrameLayout
//                .findViewById(R.id.goods_detail_images_indicator);
//        mTopAdsIndicator.setVisibility(View.VISIBLE);
//        mTopAdsView.setFlowIndicator(mTopAdsIndicator);
//        mTopAdsIndicator.setViewFlow(mTopAdsView);
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy:
//                if (null != goodDetial) {
//                    if (goodDetial.Preferential.equals("赠送奖品")) {
//                        Toast.makeText(GoodDetailsActivity.this, "无需下单购买,请于商家联系取货", Toast.LENGTH_SHORT).show();
//                    } else {
//                        addCat(id, "1", "1");
//                    }
//                }
                getMyCar1("true");
                break;
            case R.id.share:
                ShareData data = new ShareData();
                data.setTitleUrl("http://login.savingsmore.com/Home/Download");
                data.setUrl("http://login.savingsmore.com/Home/Download");
                data.setTitle("省又省-促销专卖APP");
                data.setImagePath(FileSystem.getCachesDir(GoodDetailsActivity.this, true).getAbsolutePath() + File.separator + "icon.jpg");
                data.setText("实体商铺在促销、在活动、在甩卖!省又省购物省钱、省心、省时间!");
                shareUtils = new ShareUtils(data, GoodDetailsActivity.this);
                shareUtils.show(share);
                break;
            case R.id.back:
                this.finish();
                break;
            case R.id.business_message:
                Intent intent = new Intent(GoodDetailsActivity.this, BusinessMessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("id", goodDetial.User.Id);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.flow_me:
                Intent intent1 = new Intent(GoodDetailsActivity.this, TakeMeActivity.class);
                intent1.putExtra("isgood", "true");
                intent1.putExtra("good", goodDetial.User);
                startActivity(intent1);
                break;
            case R.id.shouchang:
                if (null != isFavourite && isFavourite) {
                    cancelCollect(goodDetial.Id);
                    isFavourite = false;
                } else if (null != isFavourite && isFavourite == false) {
                    addCollect(goodDetial.Id);
                    isFavourite = true;
                }
                break;
            case R.id.phone:
                Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + goodDetial.User.PhoneNumber));
                startActivity(in);
                break;
            case R.id.comment:
                Intent comment = new Intent(GoodDetailsActivity.this, GoodsCommentActiviyt.class);
                comment.putExtra("id", goodDetial.Id);
                comment.putExtra("score", goodDetial.Score);
                comment.putExtra("isComment", "false");
                startActivity(comment);
                break;
            case R.id.send_message:
                Intent send = new Intent(GoodDetailsActivity.this, ChatActivity.class);
                send.putExtra(EaseConstant.EXTRA_USER_ID, goodDetial.User.IMUserName);
                startActivity(send);
                break;
            case R.id.servise:
                showSetIconWindow();
                break;
            case R.id.btnCancel:
                setIconWindow.dismiss();
                break;
            case R.id.btnCamera:
                getServiceList();
//                Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "58366991"));
//                startActivity(call);
                setIconWindow.dismiss();
                break;
            case R.id.btnAlbum:
                Intent leave = new Intent(GoodDetailsActivity.this, LeaveMessageActivity.class);
                leave.putExtra("Id", goodDetial.User.Id);
                startActivity(leave);
                setIconWindow.dismiss();
                break;
            case R.id.tv_more:
                Intent intent2 = new Intent(GoodDetailsActivity.this, BusinessMessageActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("id", goodDetial.User.Id);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
        }
    }

    private void showSetIconWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.view_message_popuwindow, null);
        View parentView = LayoutInflater.from(this).inflate(R.layout.good_details_activity, null);


        setIconWindow = PopupWindowFromBottomUtil.showWindow(contentView, parentView, this);

        Button btnCancel = (Button) contentView.findViewById(R.id.btnCancel);
        Button btnCamera = (Button) contentView.findViewById(R.id.btnCamera);
        Button btnAlbum = (Button) contentView.findViewById(R.id.btnAlbum);

        btnCancel.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
    }

    // 收藏商品
    private void addCollect(String id) {
        WebRequestHelper.json_post(GoodDetailsActivity.this, URLText.ADD_COLLECT, RequestParamsPool.addColect(id), new MyAsyncHttpResponseHandler(GoodDetailsActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    collect.setBackgroundResource(R.mipmap.comment_star);
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.optString("Message");
                    Toast.makeText(GoodDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    //取消收藏商品
    public void cancelCollect(String id) {
        List<String> list = new ArrayList<String>();
        list.add(id);
        WebRequestHelper.json_post(GoodDetailsActivity.this, URLText.CANCEL_COLLECT, RequestParamsPool.cancelColect(null, list), new MyAsyncHttpResponseHandler(GoodDetailsActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    collect.setBackgroundResource(R.mipmap.shouchang_white);
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.optString("Message");
                    Toast.makeText(GoodDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //加入购物车
    private void addCat(String ProductId, String Quantity, String CartOperaType) {
        WebRequestHelper.json_post(GoodDetailsActivity.this, URLText.UPDATE_CAR, RequestParamsPool.update_car(ProductId, Quantity, CartOperaType), new MyAsyncHttpResponseHandler(GoodDetailsActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    String success = object.optString("IsSuccess");
                    String message = object.optString("Message");
                    Toast.makeText(GoodDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                    if (success.equals("true")) {
                        Intent intent2 = new Intent(GoodDetailsActivity.this, OrderBuyActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("good", goodDetial);
                        intent2.putExtras(bundle);
                        startActivity(intent2);
                        finish();
                    } else if (message.equals("购物车中存在其他商铺的商品，请先结算购物车中的商品！")) {
//                        Intent intent2 = new Intent(GoodDetailsActivity.this, WXPayEntryActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("good", goodDetial);
//                        intent2.putExtras(bundle);
//                        startActivity(intent2);
//                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //得到客服列表
    private void getServiceList() {
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebRequestHelper.json_post(GoodDetailsActivity.this, URLText.SERVICE_LIST, stringEntity, new MyAsyncHttpResponseHandler(GoodDetailsActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String string = new String(responseBody);
                ServiceList serviceList = GsonUtils.fromJson(string, ServiceList.class);
                Serive_list = serviceList.MainData;
                if (Serive_list.size() > 0 && null != Serive_list) {
                    Intent send = new Intent(GoodDetailsActivity.this, ChatActivity.class);
                    if (MyUserInfoUtils.getInstance().myUserInfo.UserType.equals("3")) {
                        LogUtils.instance.d("商家与后台聊天");
                        send.putExtra(EaseConstant.EXTRA_USER_ID, Serive_list.get(0).Id);
                        startActivity(send);
                    } else {
                        LogUtils.instance.d("个人与后台聊天");
                        send.putExtra(EaseConstant.EXTRA_USER_ID, Serive_list.get(1).Id);
                        startActivity(send);
                    }
                }
            }
        });
    }

    private void getMyCar1(String isBag) {
        // list.clear();
        WebRequestHelper.json_post(GoodDetailsActivity.this, URLText.GET_MYCAR, RequestParamsPool.get_car(isBag), new MyAsyncHttpResponseHandler(GoodDetailsActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    cartDetail = GsonUtils.fromJson(object.optString("MainData"), CartDetail.class);
                    if (null != cartDetail) {
                        if (null != cartDetail.CartProduct && cartDetail.CartProduct.size() > 0) {
                            if (!goodDetial.User.ShowName.equals(cartDetail.CartProduct.get(0).StoreName)) {
                                clearCar();
                            } else {
                                getMyCar2("false");
                            }
                        } else {
                            getMyCar2("false");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getMyCar2(String isBag) {
        WebRequestHelper.json_post(GoodDetailsActivity.this, URLText.GET_MYCAR, RequestParamsPool.get_car(isBag), new MyAsyncHttpResponseHandler(GoodDetailsActivity.this) {
                    @Override
                    public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject object = new JSONObject(new String(responseBody));
                            cartDetail = GsonUtils.fromJson(object.optString("MainData"), CartDetail.class);
                            if (null != cartDetail) {
                                if (null != cartDetail.CartProduct && cartDetail.CartProduct.size() > 0) {
                                    if (!goodDetial.User.ShowName.equals(cartDetail.CartProduct.get(0).StoreName)) {
                                        clearCar();
                                    } else {
                                        if (goodDetial.Price.equals("0")) {
                                            Toast.makeText(GoodDetailsActivity.this, "无需下单购买,请与商家联系取货", Toast.LENGTH_SHORT).show();
                                        } else {
                                            addCat(id, "1", "1");
                                        }
                                    }
                                } else {
                                    if (goodDetial.Price.equals("0")) {
                                        Toast.makeText(GoodDetailsActivity.this, "无需下单购买,请与商家联系取货", Toast.LENGTH_SHORT).show();
                                    } else {
                                        addCat(id, "1", "1");
                                    }
                                }


                            }

                        } catch (
                                JSONException e
                                )

                        {
                            e.printStackTrace();
                        }
                    }
                }

        );
    }

    private void clearCar() {
        WebRequestHelper.json_post(GoodDetailsActivity.this, URLText.CLEAR_CAR, RequestParamsPool.clearCar(), new MyAsyncHttpResponseHandler(GoodDetailsActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                // LogUtils.instance.d("清空购物车=" + result);
                if (null != goodDetial) {
                    if (goodDetial.Price.equals("0")) {
                        Toast.makeText(GoodDetailsActivity.this, "无需下单购买,请于商家联系取货", Toast.LENGTH_SHORT).show();
                    } else {
                        addCat(id, "1", "1");
                    }
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
