package com.ximai.savingsmore.save.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.cache.MyImageLoader;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.library.toolbox.LogUtils;
import com.ximai.savingsmore.library.toolbox.PopupWindowFromBottomUtil;
import com.ximai.savingsmore.library.view.ScrollViewWithListView;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.common.BaseApplication;
import com.ximai.savingsmore.save.modle.AlipaySignResult;
import com.ximai.savingsmore.save.modle.BaseMessage;
import com.ximai.savingsmore.save.modle.CartDetail;
import com.ximai.savingsmore.save.modle.DikouResult;
import com.ximai.savingsmore.save.modle.GoodDetial;
import com.ximai.savingsmore.save.modle.Goods;
import com.ximai.savingsmore.save.modle.GoodsList;
import com.ximai.savingsmore.save.modle.ListBaseMessage;
import com.ximai.savingsmore.save.modle.MyUserInfoUtils;
import com.ximai.savingsmore.save.modle.OrderIdList;
import com.ximai.savingsmore.save.modle.SubmitOrderResult;
import com.ximai.savingsmore.save.modle.WeChatSign;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/12/29.
 */
//下单购买
public class OrderBuyActivity extends BaseActivity implements View.OnClickListener {
    private EditText name, phone, address;
    private TextView city;
    private TextView addAdress;
    List<Goods> list = new ArrayList<Goods>();
    List<Goods> ls_list = new ArrayList<Goods>();
    private GoodDetial goodDetial;
    private boolean isEditor = false;
    private String PrivoceId;
    private String CityId;
    private String AreaId;
    private List<BaseMessage> list1 = new ArrayList<BaseMessage>();
    private ScrollViewWithListView listView1, topListView;
    private MyAdapter myAdapter;
    private GoodsList goodsList = new GoodsList();
    private List<Goods> list_good = new ArrayList<Goods>();
    private MyHotAdapter myHotAdapter;
    private RelativeLayout hotSales;
    private ImageView hot_up;
    private CartDetail cartDetail;
    private RelativeLayout wechatPay, aliPay;
    private ImageView aliPaySelect, weChatPayselect;
    private TextView jiesuan;
    //是否用积分
    private boolean IsUsePoint = false;
    private TextView fapiao;
    private EditText beizhu;
    private List<String> idList = new ArrayList<String>();
    private OrderIdList orderId;
    private AlipaySignResult alipaySignResult;
    private SubmitOrderResult submitOrderResult;
    private static final int SDK_PAY_FLAG = 1;
    private TextView dikou;
    private DikouResult dikouResult;
    private TextView allPrice, realityPrice;
    private double sallPrice = 0, srealityPrice = 0;
    private boolean isAlipay = true;
    private WeChatSign weChatSign;
    private ImageView liuyan;
    private LinearLayout more_good;
    private boolean isMoreGood = false;
    DecimalFormat df = new DecimalFormat("######0.00");
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    // @SuppressWarnings("unchecked")
                    // PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    // String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    // String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (msg.obj.toString().contains("resultStatus={9000}")) {
                        finish();
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(OrderBuyActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OrderBuyActivity.this, PaySuccessActivity.class);
                        if (orderId.MainData.size() > 0) {
                            intent.putExtra("Id", orderId.MainData.get(0).Id);
                            startActivity(intent);
                        }

                    } else {
                        finish();
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(OrderBuyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_buy_activity);
        setLeftBackMenuVisibility(OrderBuyActivity.this, "");
        setCenterTitle("下单购买");
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);
        addAdress = (TextView) findViewById(R.id.add_address);
        city = (TextView) findViewById(R.id.city);
        listView1 = (ScrollViewWithListView) findViewById(R.id.list1);
        topListView = (ScrollViewWithListView) findViewById(R.id.sale_top);
        hotSales = (RelativeLayout) findViewById(R.id.hot_sales);
        hot_up = (ImageView) findViewById(R.id.hot_up);
        wechatPay = (RelativeLayout) findViewById(R.id.wechat_pay);
        aliPay = (RelativeLayout) findViewById(R.id.alipay);
        aliPaySelect = (ImageView) findViewById(R.id.alipay_select);
        weChatPayselect = (ImageView) findViewById(R.id.wechat_select);
        jiesuan = (TextView) findViewById(R.id.jiesuan);
        fapiao = (TextView) findViewById(R.id.fapiao);
        beizhu = (EditText) findViewById(R.id.beizhu);
        dikou = (TextView) findViewById(R.id.dikou);
        allPrice = (TextView) findViewById(R.id.all_price);
        realityPrice = (TextView) findViewById(R.id.reality_price);
        liuyan = (ImageView) findViewById(R.id.liuyan);
        more_good = (LinearLayout) findViewById(R.id.more_good);
        more_good.setOnClickListener(this);
        liuyan.setOnClickListener(this);
        wechatPay.setOnClickListener(this);
        aliPay.setOnClickListener(this);
        hotSales.setOnClickListener(this);
        addAdress.setOnClickListener(this);
        city.setOnClickListener(this);
        jiesuan.setOnClickListener(this);
        dikou.setOnClickListener(this);
        name.setEnabled(false);
        phone.setEnabled(false);
        address.setEnabled(false);
        // goodDetial=getIntent().getSerializableExtra()
        goodDetial = (GoodDetial) getIntent().getSerializableExtra("good");
        if (null != MyUserInfoUtils.getInstance().myUserInfo) {
            PrivoceId = MyUserInfoUtils.getInstance().myUserInfo.ProvinceId;
            CityId = MyUserInfoUtils.getInstance().myUserInfo.CityId;
            AreaId = MyUserInfoUtils.getInstance().myUserInfo.AreaId;
            name.setText(MyUserInfoUtils.getInstance().myUserInfo.ShowName);
            phone.setText(MyUserInfoUtils.getInstance().myUserInfo.PhoneNumber);
            address.setText(MyUserInfoUtils.getInstance().myUserInfo.Domicile);
            if (null != MyUserInfoUtils.getInstance().myUserInfo.Area) {
                city.setText(MyUserInfoUtils.getInstance().myUserInfo.Province.Name + MyUserInfoUtils.getInstance().myUserInfo.City.Name + MyUserInfoUtils.getInstance().myUserInfo.Area.Name);
            } else if (null != MyUserInfoUtils.getInstance().myUserInfo.Province && null != MyUserInfoUtils.getInstance().myUserInfo.City) {
                city.setText(MyUserInfoUtils.getInstance().myUserInfo.Province.Name + MyUserInfoUtils.getInstance().myUserInfo.City.Name);
            }
        }
        fapiao.setText(goodDetial.Invoice.Name);
        // list.add(goodDetial);
        // getMyCar(goodDetial.User.UserExtInfo.IsBag.toString());
        if (null != goodDetial.User) {
            getAllGoods(goodDetial.User.Id);
        }
        myAdapter = new MyAdapter();
        myHotAdapter = new MyHotAdapter();
        listView1.setAdapter(myAdapter);
        topListView.setAdapter(myHotAdapter);
        topListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrderBuyActivity.this, GoodDetailsActivity.class);
                intent.putExtra("isCar", "true");
                intent.putExtra("id", list_good.get(position).Id);
                startActivity(intent);
            }
        });
        queryDicNode();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != goodDetial) {
            getMyCar(goodDetial.User.UserExtInfo.IsBag.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_good:
                isMoreGood = true;
                myHotAdapter.notifyDataSetChanged();
                more_good.setVisibility(View.GONE);
                hot_up.setVisibility(View.VISIBLE);
                break;
            case R.id.liuyan:
                if (beizhu.getVisibility() == View.GONE) {
                    beizhu.setVisibility(View.VISIBLE);
                    liuyan.setBackgroundResource(R.mipmap.search_dowm3);
                } else {
                    beizhu.setVisibility(View.GONE);
                    liuyan.setBackgroundResource(R.mipmap.search_up3);
                }
                break;
            case R.id.dikou:
                IsUsePoint = true;
                jiefenDeduction(idList);
                break;
            case R.id.jiesuan:
                if (null != address.getText() && null != PrivoceId && null != CityId && null != phone.getText()) {
                    submitOrder(IsUsePoint, name.getText().toString(), phone.getText().toString(), PrivoceId, CityId, AreaId, address.getText().toString(), fapiao.getText().toString(), beizhu.getText().toString(), idList);
                } else {
                    Toast.makeText(OrderBuyActivity.this, "收货信息没有填写完整", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.alipay:
                isAlipay = true;
                aliPaySelect.setBackgroundResource(R.mipmap.select);
                weChatPayselect.setBackgroundResource(R.mipmap.noselect);
                break;
            case R.id.wechat_pay:
                isAlipay = false;
                aliPaySelect.setBackgroundResource(R.mipmap.noselect);
                weChatPayselect.setBackgroundResource(R.mipmap.select);
                break;
            case R.id.hot_sales:
                more_good.setVisibility(View.VISIBLE);
                hot_up.setVisibility(View.GONE);
                isMoreGood = false;
                myHotAdapter.notifyDataSetChanged();
//                if (topListView.getVisibility() == View.VISIBLE) {
//                    topListView.setVisibility(View.GONE);
//                    hot_up.setBackgroundResource(R.mipmap.search_down3);
//                } else {
//                    hot_up.setBackgroundResource(R.mipmap.search_up3);
//                    topListView.setVisibility(View.VISIBLE);
//                }
                break;
            case R.id.add_address:
                if (addAdress.getText().equals("新的收货人信息")) {
                    isEditor = true;
                    addAdress.setText("保存");
                    name.setText("");
                    phone.setText("");
                    address.setText("");
                    PrivoceId = null;
                    CityId = null;
                    AreaId = null;
                    city.setText("请选择省份,市,区");
                    city.setTextColor(getResources().getColor(R.color.stepcolor));
                    name.setEnabled(true);
                    phone.setEnabled(true);
                    address.setEnabled(true);
                } else {
                    isEditor = false;
                    if (!TextUtils.isEmpty(name.getText()) && !TextUtils.isEmpty(phone.getText()) && !TextUtils.isEmpty(address.getText()) && null != PrivoceId && null != CityId && null != AreaId) {
                        addAdress.setText("新的收货人信息");
                        name.setEnabled(false);
                        phone.setEnabled(false);
                        address.setEnabled(false);
                    } else {
                        Toast.makeText(OrderBuyActivity.this, "信息没有填写完整", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.city:
                if (isEditor) {
                    PopupWindowFromBottomUtil.showAddress(OrderBuyActivity.this, LayoutInflater.from(OrderBuyActivity.this).inflate(R.layout.business_my_center_activity, null), list1, new PopupWindowFromBottomUtil.Listenre1() {
                        @Override
                        public void callBack(String Id1, String Id2, String Id3, String content, PopupWindow popupWindow) {
                            PrivoceId = Id1;
                            CityId = Id2;
                            AreaId = Id3;
                            city.setTextColor(getResources().getColor(R.color.text_black));
                            city.setText(content);
                            popupWindow.dismiss();
                        }
                    });
                }
                break;
        }
    }


    //查询基础字典

    private void queryDicNode() {
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebRequestHelper.json_post(OrderBuyActivity.this, URLText.QUERYDICNODE, stringEntity, new MyAsyncHttpResponseHandler(OrderBuyActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                ListBaseMessage listBaseMessage = GsonUtils.fromJson(new String(responseBody), ListBaseMessage.class);
                list1 = listBaseMessage.MainData;
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHodel viewHodel;
            if (null == convertView) {
                viewHodel = new ViewHodel();
                convertView = (LinearLayout) LayoutInflater.from(OrderBuyActivity.this).inflate(R.layout.buy_good_item, null);
                viewHodel.sales_number = (TextView) convertView.findViewById(R.id.sales_number);
                viewHodel.bianhao = (TextView) convertView.findViewById(R.id.bianhao);
                viewHodel.delete_car = (ImageView) convertView.findViewById(R.id.delete_car);
                viewHodel.name = (TextView) convertView.findViewById(R.id.name);
                viewHodel.imageView = (ImageView) convertView.findViewById(R.id.image);
                viewHodel.zhekou = (TextView) convertView.findViewById(R.id.zhekou);
                viewHodel.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
                viewHodel.shou_chang = (TextView) convertView.findViewById(R.id.dainzan_number);
                viewHodel.liulan = (TextView) convertView.findViewById(R.id.clock_number);
                viewHodel.share = (TextView) convertView.findViewById(R.id.share_number);
                viewHodel.location = (TextView) convertView.findViewById(R.id.location);
                viewHodel.start_time = (TextView) convertView.findViewById(R.id.statr_time);
                viewHodel.end_time = (TextView) convertView.findViewById(R.id.end_time);
                viewHodel.price = (TextView) convertView.findViewById(R.id.price);
                viewHodel.dazhe_style = (TextView) convertView.findViewById(R.id.dazhe);
                viewHodel.high_price = (TextView) convertView.findViewById(R.id.high_price);
                viewHodel.high_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                viewHodel.add = (LinearLayout) convertView.findViewById(R.id.add);
                viewHodel.reduce = (LinearLayout) convertView.findViewById(R.id.reduce);
                viewHodel.number = (TextView) convertView.findViewById(R.id.number);
                convertView.setTag(viewHodel);
            }
            viewHodel = (ViewHodel) convertView.getTag();
            if (null != list.get(position).Image) {
                MyImageLoader.displayDefaultImage(URLText.img_url + list.get(position).Image, viewHodel.imageView);
            }
            if (null != list.get(position).Number) {
                viewHodel.bianhao.setText("商品号:" + list.get(position).Number);
            }
            if (null != list.get(position).SaleCount) {
                viewHodel.sales_number.setText("销量:" + list.get(position).SaleCount);
            }
            if (null != list.get(position).Name) {
                viewHodel.name.setText(list.get(position).Name);
            }
            if (null != list.get(position).Preferential) {
                viewHodel.zhekou.setText(list.get(position).PromotionTypeName);
            }
            if (null != list.get(position).StoreName) {
                viewHodel.shop_name.setText(list.get(position).StoreName);
            }
            if (null != list.get(position).FavouriteCount) {
                viewHodel.shou_chang.setText(list.get(position).FavouriteCount);
            }
            viewHodel.liulan.setText(list.get(position).HitCount);
            viewHodel.share.setText(list.get(position).SharedCount);
            if (null != list.get(position).Address) {
                viewHodel.location.setText(list.get(position).Address);
            }
            if (null != list.get(position).StartTimeName) {
                viewHodel.start_time.setText(list.get(position).StartTimeName.split(" ")[0]);
            }
            if (null != list.get(position).EndTimeName) {
                viewHodel.end_time.setText(list.get(position).EndTimeName.split(" ")[0]);
            }
            viewHodel.price.setText("￥" + list.get(position).Price);
            viewHodel.dazhe_style.setText(list.get(position).Preferential);
            viewHodel.high_price.setText("原价:￥" + list.get(position).OriginalPrice);
            viewHodel.number.setText(list.get(position).Quantity + "");
            final TextView number = viewHodel.number;
            viewHodel.delete_car.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCat(list.get(position).ProductId, "1", "4");
                }
            });
            viewHodel.reduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.size() > position) {
                        list.get(position).Quantity = list.get(position).Quantity - 1;
                        if (list.get(position).Quantity == 0) {
                            addCat(list.get(position).ProductId, "1", "4");
                        } else {
                            addCat(list.get(position).ProductId, "1", "2");
                            number.setText((list.get(position).Quantity + ""));
                        }
                        double price = 0;
                        for (int i = 0; i < list.size(); i++) {
                            price = price + list.get(i).Quantity * Double.parseDouble(list.get(i).Price);
                        }
                        allPrice.setText("￥" + df.format(price) + "");
                        realityPrice.setText("￥" + df.format(price) + "");
                        sallPrice = price;
                    }
                }
            });
            viewHodel.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.size() > position) {
                        list.get(position).Quantity = list.get(position).Quantity + 1;
                        addCat(list.get(position).ProductId, "1", "1");
                        number.setText((list.get(position).Quantity + ""));
                        double price = 0;
                        for (int i = 0; i < list.size(); i++) {
                            price = price + list.get(i).Quantity * Double.parseDouble(list.get(i).Price);
                        }
                        allPrice.setText("￥" + df.format(price) + "");
                        realityPrice.setText("￥" + df.format(price) + "");
                        sallPrice = price;
                        //price = price + list.get(position).Quantity * Double.parseDouble(list.get(position).Price);
//                        if (list.get(position).Quantity == 0) {
//                            getMyCar(goodDetial.User.UserExtInfo.IsBag + "");
//                        }
                    }
                }
            });
            return convertView;
        }
    }


    private class MyHotAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (list_good.size() > 2) {
                if (isMoreGood) {
                    return list_good.size();
                } else {
                    return 2;
                }
            } else {
                return list_good.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return list_good.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodel viewHodel;
            if (null == convertView) {
                viewHodel = new ViewHodel();
                convertView = (LinearLayout) LayoutInflater.from(OrderBuyActivity.this).inflate(R.layout.good_item, null);
                viewHodel.sales_number = (TextView) convertView.findViewById(R.id.sales_number);
                viewHodel.name = (TextView) convertView.findViewById(R.id.name);
                viewHodel.imageView = (ImageView) convertView.findViewById(R.id.image);
                viewHodel.zhekou = (TextView) convertView.findViewById(R.id.zhekou);
                viewHodel.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
                viewHodel.shou_chang = (TextView) convertView.findViewById(R.id.dainzan_number);
                viewHodel.liulan = (TextView) convertView.findViewById(R.id.clock_number);
                viewHodel.share = (TextView) convertView.findViewById(R.id.share_number);
                viewHodel.location = (TextView) convertView.findViewById(R.id.location);
                viewHodel.start_time = (TextView) convertView.findViewById(R.id.statr_time);
                viewHodel.end_time = (TextView) convertView.findViewById(R.id.end_time);
                viewHodel.price = (TextView) convertView.findViewById(R.id.price);
                viewHodel.dazhe_style = (TextView) convertView.findViewById(R.id.dazhe);
                viewHodel.high_price = (TextView) convertView.findViewById(R.id.high_price);
                viewHodel.high_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                convertView.setTag(viewHodel);
            }
            viewHodel = (ViewHodel) convertView.getTag();
            if (null != list_good.get(position).SaleCount) {
                viewHodel.sales_number.setText("销量:" + list_good.get(position).SaleCount);
            }
            if (null != list_good.get(position).Image) {
                MyImageLoader.displayDefaultImage(URLText.img_url + list_good.get(position).Image, viewHodel.imageView);
            }
            if (null != list_good.get(position).Name) {
                viewHodel.name.setText(list_good.get(position).Name);
            }
            if (null != list_good.get(position).Preferential) {
                viewHodel.zhekou.setText(list_good.get(position).PromotionTypeName);
            }
            if (null != list_good.get(position).StoreName) {
                viewHodel.shop_name.setText(list_good.get(position).StoreName);
            }
            if (null != list_good.get(position).FavouriteCount) {
                viewHodel.shou_chang.setText(list_good.get(position).FavouriteCount);
            }
            viewHodel.liulan.setText(list_good.get(position).HitCount);
            viewHodel.share.setText(list_good.get(position).SharedCount);
            if (null != list_good.get(position).Address) {
                viewHodel.location.setText(list_good.get(position).Address);
            }
            if (null != list_good.get(position).StartTimeName) {
                viewHodel.start_time.setText(list_good.get(position).StartTimeName.split(" ")[0]);
            }
            if (null != list_good.get(position).EndTimeName) {
                viewHodel.end_time.setText(list_good.get(position).EndTimeName.split(" ")[0]);
            }
            viewHodel.price.setText("￥" + list_good.get(position).Price.substring(0, list_good.get(position).Price.indexOf(".")));
            viewHodel.dazhe_style.setText(list_good.get(position).Preferential);
            viewHodel.high_price.setText("原价:￥" + list_good.get(position).OriginalPrice.substring(0, list_good.get(position).OriginalPrice.indexOf(".")));
            return convertView;
        }
    }


    class ViewHodel {
        public TextView sales_number;
        public TextView bianhao;
        public ImageView delete_car;
        public ImageView imageView;
        public TextView name;
        public TextView zhekou;
        public TextView shop_name;
        public TextView shou_chang;
        public TextView liulan;
        public TextView share;
        public TextView location;
        public TextView start_time;
        public TextView end_time;
        public TextView price;
        public TextView dazhe_style;
        public TextView high_price;
        public LinearLayout add;
        public LinearLayout reduce;
        public TextView number;
    }

    private void getAllGoods(String SellerId) {
        WebRequestHelper.json_post(OrderBuyActivity.this, URLText.GET_GOODS, RequestParamsPool.getSalesGoods(SellerId), new MyAsyncHttpResponseHandler(OrderBuyActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String resule = new String(responseBody);
                goodsList = GsonUtils.fromJson(new String(responseBody), GoodsList.class);
                if (goodsList.IsSuccess) {
                    if (null != goodsList.MainData) {
                        list_good.addAll(goodsList.MainData);
                        if (list_good.size() > 2) {
                            more_good.setVisibility(View.VISIBLE);
                        }
                        myHotAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void getMyCar(String isBag) {
        // ls_list = list;
        list.clear();
        WebRequestHelper.json_post(OrderBuyActivity.this, URLText.GET_MYCAR, RequestParamsPool.get_car(isBag), new MyAsyncHttpResponseHandler(OrderBuyActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    cartDetail = GsonUtils.fromJson(object.optString("MainData"), CartDetail.class);
                    if (null != cartDetail) {
                        if (null != cartDetail.CartProduct && cartDetail.CartProduct.size() > 0) {
                            double price = 0;
                            for (int i = cartDetail.CartProduct.size() - 1; i < cartDetail.CartProduct.size() && i > -1; i--) {
                                if (cartDetail.CartProduct.get(i).Quantity > 0) {
                                    list.add(cartDetail.CartProduct.get(i));
                                    idList.add(cartDetail.CartProduct.get(i).ProductId);
                                    price = price + cartDetail.CartProduct.get(i).Quantity * Double.parseDouble(cartDetail.CartProduct.get(i).Price);
                                }
                            }
                            // if (ls_list.size() != list.size()) {
                            // }
                            allPrice.setText("￥" + df.format(price) + "");
                            realityPrice.setText("￥" + df.format(price) + "");
                            sallPrice = price;
                        }
                        else {
                            double price=0;
                            allPrice.setText("￥" + df.format(price) + "");
                            realityPrice.setText("￥" + df.format(price) + "");
                            sallPrice = price;
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //加入购物车
    private void addCat(String ProductId, String Quantity, final String CartOperaType) {
        WebRequestHelper.json_post(OrderBuyActivity.this, URLText.UPDATE_CAR, RequestParamsPool.update_car(ProductId, Quantity, CartOperaType), new MyAsyncHttpResponseHandler(OrderBuyActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    String success = object.optString("IsSuccess");
                    String message = object.optString("Message");
                    if (CartOperaType.equals("4")) {
                        getMyCar(goodDetial.User.UserExtInfo.IsBag + "");
                        Toast.makeText(OrderBuyActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
//                    if (success.equals("true")) {
//                        Intent intent2 = new Intent(OrderBuyActivity.this, OrderBuyActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("good", goodDetial);
//                        intent2.putExtras(bundle);
//                        startActivity(intent2);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //提交订单
    private void submitOrder(boolean IsUsePoint, String Recipients, String PhoneNumber, String ProvinceId, String CityId, String AreaId, String Address, String InvoiceTitle, String Remark, List<String> list) {
        WebRequestHelper.json_post(OrderBuyActivity.this, URLText.SUBMIT_ORDER, RequestParamsPool.submit_order(IsUsePoint, Recipients, PhoneNumber, ProvinceId, CityId, AreaId, Address, InvoiceTitle, Remark, list), new MyAsyncHttpResponseHandler(OrderBuyActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(result);
                    // submitOrderResult = GsonUtils.fromJson(object.optString("MainData"), SubmitOrderResult.class);
                    orderId = GsonUtils.fromJson(object.toString(), OrderIdList.class);
                    if (null != orderId && null != orderId.MainData && orderId.MainData.size() > 0) {
                        if (isAlipay) {
                            alipaySign(orderId.MainData.get(0).Id);
                        } else {
                            weChatPaySign(orderId.MainData.get(0).Id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //支付宝支付签名
    private void alipaySign(String Id) {
        WebRequestHelper.json_post(OrderBuyActivity.this, URLText.ALIPAY_SIGN, RequestParamsPool.alipaySign(Id), new MyAsyncHttpResponseHandler(OrderBuyActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                JSONObject object = null;
                try {
                    object = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alipaySignResult = GsonUtils.fromJson(object.optString("MainData"), AlipaySignResult.class);
                if (null != alipaySignResult) {
                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(OrderBuyActivity.this);
                            String result = alipay.pay(aliPayParameter(alipaySignResult));
                            Log.i("msp", result.toString());
                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };

                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                }
            }
        });
    }

    private void weChatPaySign(final String Id) {
        WebRequestHelper.json_post(OrderBuyActivity.this, URLText.WECHAT_SIGN, RequestParamsPool.weChatSign(Id), new MyAsyncHttpResponseHandler(OrderBuyActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    weChatSign = GsonUtils.fromJson(jsonObject.optString("MainData"), WeChatSign.class);
                    final IWXAPI msgApi = WXAPIFactory.createWXAPI(OrderBuyActivity.this, null);
                    msgApi.registerApp(weChatSign.appid);
                    LogUtils.instance.d("appid=" + weChatSign.appid);
//                    WXPayEntryActivity wxPayEntryActivity = new WXPayEntryActivity();
//                    wxPayEntryActivity.setOrderId(Id);
                    PayReq req = new PayReq();
                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                    BaseApplication.getInstance().OrderId = Id;
                    req.appId = weChatSign.appid;
                    req.partnerId = weChatSign.partnerid;
                    req.prepayId = weChatSign.prepayid;
                    req.nonceStr = weChatSign.noncestr;
                    req.timeStamp = weChatSign.timestamp;
                    req.packageValue = "Sign=WXPay";
                    req.sign = weChatSign.sign;
                    msgApi.sendReq(req);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //构造支付宝支付参数
    private String aliPayParameter(AlipaySignResult result) {
        String aliPayString = "app_id=" + result.app_id + "&";
        aliPayString = aliPayString + "method=" + result.method + "&";
        aliPayString = aliPayString + "format=" + result.format + "&";
        aliPayString = aliPayString + "charset=" + result.charset + "&";
        aliPayString = aliPayString + "sign_type=" + result.sign_type + "&";
        aliPayString = aliPayString + "timestamp=" + result.timestamp + "&";
        aliPayString = aliPayString + "version=" + result.version + "&";
        aliPayString = aliPayString + "notify_url=" + result.notify_url + "&";
        aliPayString = aliPayString + "biz_content=" + result.biz_content + "&";
        aliPayString = aliPayString + "sign=" + result.sign;
        return aliPayString;
    }

    //积分抵扣
    private void jiefenDeduction(List<String> list) {
        WebRequestHelper.json_post(OrderBuyActivity.this, URLText.JIFEN_DIKOU, RequestParamsPool.jifenDikou(list), new MyAsyncHttpResponseHandler(OrderBuyActivity.this) {
                    @Override
                    public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                        String result = new String(responseBody);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dikouResult = GsonUtils.fromJson(object.optString("MainData"), DikouResult.class);
                        if (object.optString("IsSuccess").equals("true")) {
                            Toast.makeText(OrderBuyActivity.this, object.optString("Message"), Toast.LENGTH_SHORT).show();
                            srealityPrice = sallPrice - dikouResult.DeductionPrice;
                            realityPrice.setText("￥" + df.format(srealityPrice) + "");
                            dikou.setText("扣" + dikouResult.DeductionPoint + "分 — " + dikouResult.DeductionPrice + "元");
                        } else {
                            Toast.makeText(OrderBuyActivity.this, "不能使用积分抵扣", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );
    }


}
