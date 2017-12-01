package com.ximai.savingsmore.save.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.L;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.FileSystem.FileSystem;
import com.ximai.savingsmore.library.cache.MyImageLoader;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.library.toolbox.PopupWindowFromBottomUtil;
import com.ximai.savingsmore.library.toolbox.PreferencesUtils;
import com.ximai.savingsmore.library.toolbox.UsePicker;
import com.ximai.savingsmore.library.view.HorizontalListView;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.modle.Area;
import com.ximai.savingsmore.save.modle.BaseMessage;
import com.ximai.savingsmore.save.modle.GoodDetial;
import com.ximai.savingsmore.save.modle.GoodSalesType;
import com.ximai.savingsmore.save.modle.GoodSalesTypeList;
import com.ximai.savingsmore.save.modle.Images;
import com.ximai.savingsmore.save.modle.ListBaseMessage;
import com.ximai.savingsmore.save.modle.MyProduct;
import com.ximai.savingsmore.save.modle.MyUserInfoUtils;
import com.ximai.savingsmore.save.modle.UpPhoto;
import com.ximai.savingsmore.save.utils.ImageTools;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by caojian on 16/12/10.
 */
public class AddGoodsAcitivyt extends BaseActivity implements View.OnClickListener {
    private static int PICK_FROM_IMAGE = 1011;
    private static int PICK_FROM_CAMERA = 1022;
    private static int CROP_PHOTO_CODE = 7;
    private ImageView head_view;
    private TextView my_name;
    private PopupWindow setIconWindow;
    private List<BaseMessage> list = new ArrayList<BaseMessage>();
    private List<BaseMessage> good_one_classify = new ArrayList<BaseMessage>();
    private List<BaseMessage> good_two_classify = new ArrayList<BaseMessage>();
    private List<BaseMessage> brand_list = new ArrayList<BaseMessage>();
    private List<GoodSalesType> goodSalesTypes = new ArrayList<GoodSalesType>();
    private List<BaseMessage> danwei_list = new ArrayList<BaseMessage>();
    private List<BaseMessage> bizhong_list = new ArrayList<BaseMessage>();
    private List<BaseMessage> fapiao_list = new ArrayList<BaseMessage>();
    private List<BaseMessage> yuanyin_list = new ArrayList<BaseMessage>();
    private TextView yijifenlei, erjifenlei, brand;
    private LinearLayout xingshi_item, yuanyin_item, dizhi_item, satrt_item, end_item, danwei_item, bizhong_item, fapiao_item;
    private TextView xingshi, yuanyin, dizhi, start, end, danwei, bizhong, fapiao;
    private String oneId;
    private EditText yuan_price, cuxiao_price;
    private TextView cuxiao_text;
    private String xingshi_id;
    private List<Images> images = new ArrayList<Images>();
    private MyAdapter myAdapter;
    private HorizontalListView horizontalListView;
    private MyProduct myProduct = new MyProduct();
    private EditText explain, descript;
    private TextView fabu;
    private EditText product_name;
    private TextView product_bianhao;
    TextView xiangxi_address;
    private String Id;
    private GoodDetial goodDetial;
    private TextView cuxiaoshuoming, shangpingmiaoshu;
    private int shuoming_number = 0, miaoshu_number = 0;
    private AlertDialog classity_dialog, brand_dialog, bug_dialog;
    private TextView custom_type;
    private String start_date = "";
    private TextView servise;
    private TextView zidingyi_brand;
    private String type = "";
    private boolean isEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_good_activity);
        setCenterTitle("促销发布");
        setLeftBackMenuVisibility(AddGoodsAcitivyt.this, "");
        if (null == MyUserInfoUtils.getInstance().myUserInfo.ProvinceId || null == MyUserInfoUtils.getInstance().myUserInfo.CityId) {
            Intent intent = new Intent(AddGoodsAcitivyt.this, BusinessMessageActivity.class);
            startActivity(intent);
            finish();
        } else {
            head_view = (ImageView) findViewById(R.id.head_image);
            my_name = (TextView) findViewById(R.id.name);
            MyImageLoader.displayDefaultImage(URLText.img_url + MyUserInfoUtils.getInstance().myUserInfo.PhotoPath, head_view);
            if (null != MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo) {
                my_name.setText(MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.StoreName);
            }
            cuxiaoshuoming = (TextView) findViewById(R.id.cuxiaoshuoming);
            shangpingmiaoshu = (TextView) findViewById(R.id.shangpingmiaoshu);
            horizontalListView = (HorizontalListView) findViewById(R.id.myGridview);
            yijifenlei = (TextView) findViewById(R.id.one_classity);
            erjifenlei = (TextView) findViewById(R.id.two_classity);
            brand = (TextView) findViewById(R.id.brand_name);
            xingshi_item = (LinearLayout) findViewById(R.id.xingshi_item);
            xingshi = (TextView) findViewById(R.id.xingshi);
            yuanyin_item = (LinearLayout) findViewById(R.id.yuanyin_item);
            yuanyin = (TextView) findViewById(R.id.yuanyin);
            dizhi_item = (LinearLayout) findViewById(R.id.dizhi_item);
            dizhi = (TextView) findViewById(R.id.dizhi);
            satrt_item = (LinearLayout) findViewById(R.id.start_time_item);
            start = (TextView) findViewById(R.id.start_time);
            end_item = (LinearLayout) findViewById(R.id.end_time_item);
            end = (TextView) findViewById(R.id.end_time);
            danwei_item = (LinearLayout) findViewById(R.id.danwei_item);
            danwei = (TextView) findViewById(R.id.danwei);
            bizhong_item = (LinearLayout) findViewById(R.id.bizhong_item);
            bizhong = (TextView) findViewById(R.id.bizhong);
            fapiao_item = (LinearLayout) findViewById(R.id.fapiao_item);
            fapiao = (TextView) findViewById(R.id.fapiao);
            yuan_price = (EditText) findViewById(R.id.yuan_price);
            cuxiao_price = (EditText) findViewById(R.id.cuxiao_price);
            cuxiao_text = (TextView) findViewById(R.id.cuxiao_text);
            fabu = (TextView) findViewById(R.id.fabu);
            product_bianhao = (TextView) findViewById(R.id.product_bianhao);
            product_name = (EditText) findViewById(R.id.product_name);
            xiangxi_address = (TextView) findViewById(R.id.xiangxi_address);
            servise = (TextView) findViewById(R.id.servise);
            servise.setOnClickListener(this);
            fabu.setOnClickListener(this);
            yijifenlei.setOnClickListener(this);
            erjifenlei.setOnClickListener(this);
            brand.setOnClickListener(this);
            xingshi_item.setOnClickListener(this);
            yuanyin_item.setOnClickListener(this);
            // dizhi_item.setOnClickListener(this);
            satrt_item.setOnClickListener(this);
            end_item.setOnClickListener(this);
            danwei_item.setOnClickListener(this);
            bizhong_item.setOnClickListener(this);
            fapiao_item.setOnClickListener(this);
            yuan_price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (null != xingshi_id) {
                        if (xingshi_id.equals("3") || xingshi_id.equals("7") || xingshi_id.equals("12")) {
                            cuxiao_price.setText(s.toString());
                        }
                    }
                }
            });
            myAdapter = new MyAdapter();
            horizontalListView.setAdapter(myAdapter);
            queryDicNode();
            queryDicNode2();
            explain = (EditText) findViewById(R.id.explain);
            descript = (EditText) findViewById(R.id.decript);
            shuoming_number = explain.getText().length();
            miaoshu_number = descript.getText().length();
            explain.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    shuoming_number = explain.getText().length();
                    cuxiaoshuoming.setText("促销说明(还可以输入" + (200 - shuoming_number) + "个字)");
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            descript.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    miaoshu_number = descript.getText().length();
                    shangpingmiaoshu.setText("商品描述(还可以输入" + (200 - miaoshu_number) + "个字)");
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            zidingyi_brand = (TextView) findViewById(R.id.zidingyi_brand);
            custom_type = (TextView) findViewById(R.id.custom_type);
            zidingyi_brand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    brand_dialog = new AlertDialog.Builder(AddGoodsAcitivyt.this).create();
                    View view = LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.save_brand, null);
                    final TextView queding, quxiao;
                    final EditText content;
                    queding = (TextView) view.findViewById(R.id.comfirm);
                    quxiao = (TextView) view.findViewById(R.id.cannel);
                    content = (EditText) view.findViewById(R.id.content);
                    queding.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != content.getText() && !TextUtils.isEmpty(content.getText())) {
                                save_brand(content.getText().toString());
                            }
                        }
                    });
                    quxiao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            brand_dialog.dismiss();
                        }
                    });
                    brand_dialog.setView(view);
                    brand_dialog.show();
                }
            });
            custom_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (oneId != null && !TextUtils.isEmpty(oneId)) {
                        classity_dialog = new AlertDialog.Builder(AddGoodsAcitivyt.this).create();
                        View view = LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.custom_classfy, null);
                        final TextView queding, quxiao;
                        final EditText content;
                        queding = (TextView) view.findViewById(R.id.comfirm);
                        quxiao = (TextView) view.findViewById(R.id.cannel);
                        content = (EditText) view.findViewById(R.id.content);
                        queding.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != content.getText() && !TextUtils.isEmpty(content.getText())) {
                                    apply_calssity(content.getText().toString(), oneId);
                                }
                            }
                        });
                        quxiao.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                classity_dialog.dismiss();
                            }
                        });
                        classity_dialog.setView(view);
                        classity_dialog.show();
                    } else {
                        Toast.makeText(AddGoodsAcitivyt.this, "请先选择一级分类", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            cuxiao_price.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equals("买就送")) {
                        bug_dialog = new AlertDialog.Builder(AddGoodsAcitivyt.this).create();
                        View view = LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.bug_give, null);
                        final TextView queding, quxiao;
                        final EditText number1, number2;
                        queding = (TextView) view.findViewById(R.id.comfirm);
                        quxiao = (TextView) view.findViewById(R.id.cannel);
                        number1 = (EditText) view.findViewById(R.id.number1);
                        number2 = (EditText) view.findViewById(R.id.number2);
                        queding.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != number1.getText() && !TextUtils.isEmpty(number1.getText()) && null != number2.getText() && !TextUtils.isEmpty(number2.getText())) {
                                    cuxiao_price.setText("买" + number1.getText().toString() + "送" + number2.getText().toString());
                                    bug_dialog.dismiss();
                                }
                            }
                        });
                        quxiao.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bug_dialog.dismiss();
                            }
                        });
                        bug_dialog.setView(view);
                        bug_dialog.show();
                    }
                }
            });
            if (null != MyUserInfoUtils.getInstance().myUserInfo.Area) {
                dizhi.setText(MyUserInfoUtils.getInstance().myUserInfo.Province.Name + " " + MyUserInfoUtils.getInstance().myUserInfo.City.Name + "" + MyUserInfoUtils.getInstance().myUserInfo.Area.Name);
            } else {
                dizhi.setText(MyUserInfoUtils.getInstance().myUserInfo.Province.Name + " " + MyUserInfoUtils.getInstance().myUserInfo.City.Name);
            }
            if (null != MyUserInfoUtils.getInstance().myUserInfo.BusinessScopes && MyUserInfoUtils.getInstance().myUserInfo.BusinessScopes.size() > 0) {
                yijifenlei.setTextColor(getResources().getColor(R.color.text_black));
                yijifenlei.setText(MyUserInfoUtils.getInstance().myUserInfo.BusinessScopes.get(0).DictNode.Name);
                myProduct.FirstClassId = MyUserInfoUtils.getInstance().myUserInfo.BusinessScopes.get(0).DictNode.Id;
                oneId = MyUserInfoUtils.getInstance().myUserInfo.BusinessScopes.get(0).DictNode.Id;
            }
            xiangxi_address.setText(MyUserInfoUtils.getInstance().myUserInfo.Domicile);
            myProduct.ProvinceId = MyUserInfoUtils.getInstance().myUserInfo.ProvinceId;
            myProduct.CityId = MyUserInfoUtils.getInstance().myUserInfo.CityId;
            myProduct.AreaId = MyUserInfoUtils.getInstance().myUserInfo.AreaId;
            if (null != getIntent().getStringExtra("id")) {
                Id = getIntent().getStringExtra("id");
                isEnd = getIntent().getBooleanExtra("isEnd", false);
                getgood_detial(Id);
            }
        }
    }


    //查询基础字典
    private void queryDicNode() {
        good_one_classify.clear();
        brand_list.clear();
        danwei_list.clear();
        bizhong_list.clear();
        fapiao_list.clear();
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebRequestHelper.json_post(AddGoodsAcitivyt.this, URLText.QUERYDICNODE, stringEntity, new MyAsyncHttpResponseHandler(AddGoodsAcitivyt.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                ListBaseMessage listBaseMessage = GsonUtils.fromJson(new String(responseBody), ListBaseMessage.class);
                list = listBaseMessage.MainData;
                for (int i = 0; i < list.size(); i++) {
                    if (null != list.get(i).Name && null != list.get(i).ParentId && null != list.get(i).Id && list.get(i).ParentId.equals("ba335639-52c2-4e8d-8d2b-faf8ed097418")) {
                        good_one_classify.add(list.get(i));
                    }
                    if (null != list.get(i).Name && null != list.get(i).ParentId && null != list.get(i).Id && list.get(i).ParentId.equals("a390a2ff-40a2-487d-a719-c9ae5980fbae")) {
                        brand_list.add(list.get(i));
                    }
                    if (null != list.get(i).Name && null != list.get(i).ParentId && null != list.get(i).Id && list.get(i).ParentId.equals("3a8eb937-691f-47be-84d5-bf0b531009d5")) {
                        danwei_list.add(list.get(i));
                    }
                    if (null != list.get(i).Name && null != list.get(i).ParentId && null != list.get(i).Id && list.get(i).ParentId.equals("f8467615-d17b-4f30-877f-2bb1a4a0f8c0")) {
                        bizhong_list.add(list.get(i));
                    }
                    if (null != list.get(i).Name && null != list.get(i).ParentId && null != list.get(i).Id && list.get(i).ParentId.equals("21a595ce-99f9-4533-a112-b3f21984d231")) {
                        fapiao_list.add(list.get(i));
                    }
                    if (null != list.get(i).Name && null != list.get(i).ParentId && null != list.get(i).Id && list.get(i).ParentId.equals("558f9fab-3e39-40cd-a5ca-7d4c76e4d5a4")) {
                        yuanyin_list.add(list.get(i));
                    }

                }
                BaseMessage brand_zidiyi = new BaseMessage();
                brand_zidiyi.Name = "自定义";
                brand_list.add(brand_zidiyi);
            }
        });
    }

    //查询促销形式
    private void queryDicNode2() {
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebRequestHelper.json_post(AddGoodsAcitivyt.this, URLText.QUERYDICNODE2, stringEntity, new MyAsyncHttpResponseHandler(AddGoodsAcitivyt.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                GoodSalesTypeList goodSalesTypeList = GsonUtils.fromJson(new String(responseBody), GoodSalesTypeList.class);
                goodSalesTypes = goodSalesTypeList.ShowData;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.servise:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "58366991"));
                startActivity(intent);
                break;

            case R.id.one_classity:
//                PopupWindowFromBottomUtil.shouRange(AddGoodsAcitivyt.this, LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.business_my_center_activity, null), good_one_classify, new PopupWindowFromBottomUtil.Listener2() {
//                    @Override
//                    public void callBack(BaseMessage Id1, String content, PopupWindow popupWindow) {
//                        yijifenlei.setTextColor(getResources().getColor(R.color.text_black));
//                        yijifenlei.setText(Id1.Name);
//                        myProduct.FirstClassId = Id1.Id;
//                        oneId = Id1.Id;
//                        popupWindow.dismiss();
//                    }
//                });
                break;
            case R.id.two_classity:
                if (null != oneId && !TextUtils.isEmpty(oneId)) {
                    good_two_classify.clear();
                    for (int i = 0; i < list.size(); i++) {
                        if (null != list.get(i).Name && null != list.get(i).ParentId && null != list.get(i).Id && list.get(i).ParentId.equals(oneId)) {
                            good_two_classify.add(list.get(i));
                        }
                    }
                    BaseMessage zidiyi = new BaseMessage();
                    zidiyi.Name = "自定义";
                    good_two_classify.add(zidiyi);
                    PopupWindowFromBottomUtil.shouRange(AddGoodsAcitivyt.this, LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.business_my_center_activity, null), good_two_classify, new PopupWindowFromBottomUtil.Listener2() {
                        @Override
                        public void callBack(BaseMessage Id1, String content, PopupWindow popupWindow) {
                            if (Id1.Name.equals("自定义")) {
                                if (oneId != null && !TextUtils.isEmpty(oneId)) {
                                    classity_dialog = new AlertDialog.Builder(AddGoodsAcitivyt.this).create();
                                    View view = LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.custom_classfy, null);
                                    final TextView queding, quxiao;
                                    final EditText content1;
                                    queding = (TextView) view.findViewById(R.id.comfirm);
                                    quxiao = (TextView) view.findViewById(R.id.cannel);
                                    content1 = (EditText) view.findViewById(R.id.content);
                                    queding.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (null != content1.getText() && !TextUtils.isEmpty(content1.getText())) {
                                                apply_calssity(content1.getText().toString(), oneId);
                                            }
                                        }
                                    });
                                    quxiao.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            classity_dialog.dismiss();
                                        }
                                    });
                                    classity_dialog.setView(view);
                                    classity_dialog.show();
                                } else {
                                    Toast.makeText(AddGoodsAcitivyt.this, "请先选择一级分类", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                erjifenlei.setTextColor(getResources().getColor(R.color.text_black));
                                erjifenlei.setText(Id1.Name);
                                myProduct.SecondClassId = Id1.Id;
                            }
                            popupWindow.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(AddGoodsAcitivyt.this, "请先选择一级分类", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.yuanyin_item:
                PopupWindowFromBottomUtil.shouRange(AddGoodsAcitivyt.this, LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.business_my_center_activity, null), yuanyin_list, new PopupWindowFromBottomUtil.Listener2() {
                    @Override
                    public void callBack(BaseMessage Id1, String content, PopupWindow popupWindow) {
                        yuanyin.setTextColor(getResources().getColor(R.color.text_black));
                        yuanyin.setText(Id1.Name);
                        myProduct.PromotionCause = Id1.Id;
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.brand_name:
                PopupWindowFromBottomUtil.shouRange(AddGoodsAcitivyt.this, LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.business_my_center_activity, null), brand_list, new PopupWindowFromBottomUtil.Listener2() {
                    @Override
                    public void callBack(BaseMessage Id1, String content, PopupWindow popupWindow) {
                        if (Id1.Name.equals("自定义")) {
                            brand_dialog = new AlertDialog.Builder(AddGoodsAcitivyt.this).create();
                            View view = LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.save_brand, null);
                            final TextView queding, quxiao;
                            final EditText content1;
                            queding = (TextView) view.findViewById(R.id.comfirm);
                            quxiao = (TextView) view.findViewById(R.id.cannel);
                            content1 = (EditText) view.findViewById(R.id.content);
                            queding.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (null != content1.getText() && !TextUtils.isEmpty(content1.getText())) {
                                        save_brand(content1.getText().toString());
                                    }
                                }
                            });
                            quxiao.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    brand_dialog.dismiss();
                                }
                            });
                            brand_dialog.setView(view);
                            brand_dialog.show();
                        } else {
                            brand.setTextColor(getResources().getColor(R.color.text_black));
                            brand.setText(Id1.Name);
                            myProduct.BrandId = Id1.Id;
                        }
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.xingshi_item:
                PopupWindowFromBottomUtil.shouSalesType(AddGoodsAcitivyt.this, LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.business_my_center_activity, null), goodSalesTypes, new PopupWindowFromBottomUtil.Listenrt3() {
                    @Override
                    public void callback(GoodSalesType goodSalesType, PopupWindow popupWindow) {
                        type = "";
                        myProduct.PromotionType = goodSalesType.Id;
                        yuan_price.setText("");
                        cuxiao_price.setText("");
                        yuan_price.setEnabled(true);
                        cuxiao_price.setEnabled(true);
                        cuxiao_text.setText("促销价格");
                        xingshi.setTextColor(getResources().getColor(R.color.text_black));
                        xingshi.setText(goodSalesType.Value);
                        xingshi_id = goodSalesType.Id;
                        if (goodSalesType.Id.equals("5")) {
                            yuan_price.setText("10");
                            cuxiao_price.setText("10");
                            yuan_price.setEnabled(false);
                            cuxiao_price.setEnabled(false);
                        } else if (goodSalesType.Id.equals("6")) {
                            yuan_price.setText("1");
                            cuxiao_price.setText("1");
                            yuan_price.setEnabled(false);
                            cuxiao_price.setEnabled(false);
                        } else if (goodSalesType.Id.equals("11")) {
                            yuan_price.setText("0");
                            cuxiao_price.setText("0");
                            yuan_price.setEnabled(false);
                            cuxiao_price.setEnabled(false);
                        } else if (goodSalesType.Id.equals("13")) {
                            yuan_price.setText("0");
                            cuxiao_price.setText("0");
                            yuan_price.setEnabled(false);
                            cuxiao_price.setEnabled(false);
                        } else if (goodSalesType.Id.equals("2")) {
                            type = "买就送";
                            // cuxiao_text.setText("买N送N");
                        }
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.dizhi_item:
                PopupWindowFromBottomUtil.showAddress(AddGoodsAcitivyt.this, LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.business_my_center_activity, null), list, new PopupWindowFromBottomUtil.Listenre1() {
                    @Override
                    public void callBack(String Id1, String Id2, String Id3, String content, PopupWindow popupWindow) {
                        myProduct.ProvinceId = Id1;
                        myProduct.CityId = Id2;
                        myProduct.AreaId = Id3;
                        dizhi.setTextColor(getResources().getColor(R.color.text_black));
                        dizhi.setText(content);
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.start_time_item:
                UsePicker.showAll(AddGoodsAcitivyt.this, new UsePicker.CallBack() {
                    @Override
                    public void callBack(String time) {
                        SimpleDateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date date1 = null;
                        try {
                            date1 = dd.parse(time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Long time1 = date1.getTime();
                        Date dt = new Date();
                        Long now = dt.getTime();
                        if (now > time1) {
                            Toast.makeText(AddGoodsAcitivyt.this, "促销时间不能早于当前时间", Toast.LENGTH_SHORT).show();
                        } else {
                            start_date = time;
                            myProduct.StartTime = time;
                            start.setTextColor(getResources().getColor(R.color.text_black));
                            start.setText(time);
                        }
                    }
                }, start.getText().toString());
                break;
            case R.id.end_time_item:
                UsePicker.showAll(AddGoodsAcitivyt.this, new UsePicker.CallBack() {
                    @Override
                    public void callBack(String time) {
                        SimpleDateFormat dd = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            Date date1 = dd.parse(start_date);
                            Date date2 = dd.parse(time);
                            Long time1 = date1.getTime();
                            Long time2 = date2.getTime();
                            if ((time2 > time1)) {
                                myProduct.EndTime = time;
                                end.setTextColor(getResources().getColor(R.color.text_black));
                                end.setText(time);
                            } else {
                                Toast.makeText(AddGoodsAcitivyt.this, "结束时间要大于开始时间", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                }, end.getText().toString());
                break;
            case R.id.danwei_item:
                PopupWindowFromBottomUtil.shouRange(AddGoodsAcitivyt.this, LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.business_my_center_activity, null), danwei_list, new PopupWindowFromBottomUtil.Listener2() {
                    @Override
                    public void callBack(BaseMessage Id1, String content, PopupWindow popupWindow) {
                        myProduct.UnitId = Id1.Id;
                        danwei.setTextColor(getResources().getColor(R.color.text_black));
                        danwei.setText(Id1.Name);
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.bizhong_item:
                PopupWindowFromBottomUtil.shouRange(AddGoodsAcitivyt.this, LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.business_my_center_activity, null), bizhong_list, new PopupWindowFromBottomUtil.Listener2() {
                    @Override
                    public void callBack(BaseMessage Id1, String content, PopupWindow popupWindow) {
                        myProduct.CurrencyId = Id1.Id;
                        bizhong.setTextColor(getResources().getColor(R.color.text_black));
                        bizhong.setText(Id1.Name);
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.fapiao_item:
                PopupWindowFromBottomUtil.shouRange(AddGoodsAcitivyt.this, LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.business_my_center_activity, null), fapiao_list, new PopupWindowFromBottomUtil.Listener2() {
                    @Override
                    public void callBack(BaseMessage Id1, String content, PopupWindow popupWindow) {
                        myProduct.InvoiceId = Id1.Id;
                        fapiao.setTextColor(getResources().getColor(R.color.text_black));
                        fapiao.setText(Id1.Name);
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.btnCancel:
                setIconWindow.dismiss();
                break;
            case R.id.btnCamera:
                openCamera();
                setIconWindow.dismiss();
                break;
            case R.id.btnAlbum:
                openAlbum();
                setIconWindow.dismiss();
                break;
            case R.id.fabu:
                myProduct.Name = my_name.getText().toString();
                myProduct.Name = product_name.getText().toString();
                myProduct.Number = product_bianhao.getText().toString();
                myProduct.Address = xiangxi_address.getText().toString();
                myProduct.OriginalPrice = yuan_price.getText().toString();
                myProduct.Price = cuxiao_price.getText().toString();
                myProduct.Introduction = explain.getText().toString();
                myProduct.Description = descript.getText().toString();
                myProduct.Images = images;
                if (!TextUtils.isEmpty(myProduct.Price) && !TextUtils.isEmpty(myProduct.OriginalPrice) && null != myProduct.Price && null != myProduct.OriginalPrice && myProduct.Images.size() > 0 && null != myProduct.Name && null != myProduct.InvoiceId && null != myProduct.Address && null != myProduct.BrandId && null != myProduct.CurrencyId
                        && null != myProduct.Description && null != myProduct.EndTime && null != myProduct.Description && null != myProduct.FirstClassId && null != myProduct.SecondClassId) {
                    saveMyProduct(myProduct);
                } else {
                    Toast.makeText(AddGoodsAcitivyt.this, "请填写所有信息", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return images.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position < images.size()) {
                return images.get(position);
            }
            if (position < images.size()) {
                return images.size();
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            MyViewHodel myViewHodel;
            myViewHodel = new MyViewHodel();
            convertView = LayoutInflater.from(AddGoodsAcitivyt.this).inflate(R.layout.horizontal_listview_item, null);
            myViewHodel.image = (ImageView) convertView.findViewById(R.id.iamge);
            myViewHodel.delete = (ImageView) convertView.findViewById(R.id.delete);
            convertView.setTag(myViewHodel);
            if (position < images.size()) {
                myViewHodel.delete.setVisibility(View.VISIBLE);
                MyImageLoader.displayDefaultImage(URLText.img_url + images.get(position).ImagePath, myViewHodel.image);
            }
//            else if (position <  images.size()) {
//                myViewHodel.delete.setVisibility(View.VISIBLE);
//                MyImageLoader.displayDefaultImage("file://" + imagePath.get(position - images.size()), myViewHodel.image);
//            }
            else {
                // myViewHodel.image.setImageDrawable(R.drawable.add_image);
                myViewHodel.image.setBackgroundResource(R.mipmap.add_image);
                myViewHodel.delete.setVisibility(View.GONE);
            }
            myViewHodel.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == (images.size())) {
                        showSetIconWindow();
                    }
                }
            });
            myViewHodel.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position < images.size()) {
                        images.remove(position);
                    } else {
                        // imagePath.remove(position - images.size());
                    }
                    myAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    private class MyViewHodel {
        private ImageView image;
        private ImageView delete;

    }

    //打开相册
    private void openAlbum() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(openAlbumIntent, PICK_FROM_IMAGE);
    }

    //打开照相机
    private void openCamera() {
        Uri imageUri = null;
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ImageTools.deletePhotoAtPathAndName(FileSystem.getCachesDir(this, true).getAbsolutePath(), PreferencesUtils.getString(this, "tempName"));
        String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        PreferencesUtils.putString(this, "tempName", fileName);
        imageUri = Uri.fromFile(new File(FileSystem.getCachesDir(this, true).getAbsolutePath(), fileName));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, PICK_FROM_CAMERA);
    }

    //图片裁剪
    public void cropImage(Uri uri, int requestCode) {
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("isCrop", false);
        startActivityForResult(intent, requestCode);
    }

    private void showSetIconWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.view_set_icon_popwindow, null);
        View parentView = LayoutInflater.from(this).inflate(R.layout.business_my_center_activity, null);


        setIconWindow = PopupWindowFromBottomUtil.showWindow(contentView, parentView, this);

        Button btnCancel = (Button) contentView.findViewById(R.id.btnCancel);
        Button btnCamera = (Button) contentView.findViewById(R.id.btnCamera);
        Button btnAlbum = (Button) contentView.findViewById(R.id.btnAlbum);

        btnCancel.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        } else if (requestCode == PICK_FROM_CAMERA || requestCode == PICK_FROM_IMAGE) {

            Uri uri = null;
            if (null != intent && intent.getData() != null) {
                uri = intent.getData();
            } else {
                String fileName = PreferencesUtils.getString(this, "tempName");
                uri = Uri.fromFile(new File(FileSystem.getCachesDir(this, true).getAbsolutePath(), fileName));
            }

            if (uri != null) {
                cropImage(uri, CROP_PHOTO_CODE);
            }
        } else if (requestCode == CROP_PHOTO_CODE) {
            Uri photoUri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            try {
                upLoadImage(new File((new URI(photoUri.toString()))), "BusinessLicense");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            //addImage(imagePath);

        }
    }

    private void upLoadImage(File file, final String type) {
        WebRequestHelper.post(URLText.UPLOAD_IMAGE, RequestParamsPool.upLoad(file, type), new MyAsyncHttpResponseHandler(AddGoodsAcitivyt.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    UpPhoto upPhoto = GsonUtils.fromJson(jsonObject.optString("MainData"), UpPhoto.class);
                    Images images1 = new Images();
                    if (null != upPhoto) {
                        images1.ImageId = upPhoto.Id;
                        images1.ImagePath = upPhoto.FilePath;
                        images.add(images1);
                        myAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveMyProduct(MyProduct myProduct) {
        WebRequestHelper.json_post(AddGoodsAcitivyt.this, URLText.SAVEMYPRODUCT, RequestParamsPool.getMyProduct(myProduct), new MyAsyncHttpResponseHandler(AddGoodsAcitivyt.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                String isSucess = null;
                JSONObject object = null;
                try {
                    object = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String message = object.optString("Message");
                try {
                    isSucess = object.optString("IsSuccess");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isSucess.equals("true")) {
                    finish();
                }
                Toast.makeText(AddGoodsAcitivyt.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getgood_detial(final String Id) {
        WebRequestHelper.json_post(AddGoodsAcitivyt.this, URLText.GET_GOOD_DETIAL, RequestParamsPool.getGoodDetial(Id), new MyAsyncHttpResponseHandler(AddGoodsAcitivyt.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(result);
                    Boolean isSuccess = object.optBoolean("IsSuccess");
                    String MainData = object.optString("MainData");
                    goodDetial = GsonUtils.fromJson(MainData, GoodDetial.class);
                    product_bianhao.setTextColor(getResources().getColor(R.color.text_black));
                    yijifenlei.setTextColor(getResources().getColor(R.color.text_black));
                    erjifenlei.setTextColor(getResources().getColor(R.color.text_black));
                    brand.setTextColor(getResources().getColor(R.color.text_black));
                    xingshi.setTextColor(getResources().getColor(R.color.text_black));
                    yuanyin.setTextColor(getResources().getColor(R.color.text_black));
                    dizhi.setTextColor(getResources().getColor(R.color.text_black));
                    start.setTextColor(getResources().getColor(R.color.text_black));
                    end.setTextColor(getResources().getColor(R.color.text_black));
                    danwei.setTextColor(getResources().getColor(R.color.text_black));
                    bizhong.setTextColor(getResources().getColor(R.color.text_black));
                    fapiao.setTextColor(getResources().getColor(R.color.text_black));
                    myProduct.Id = Id;
                    if (null != goodDetial) {
                        if (null != goodDetial.Images)
                            images = goodDetial.Images;
                        myAdapter.notifyDataSetChanged();
                        myProduct.FirstClassId = goodDetial.FirstClassId;
                        yijifenlei.setText(goodDetial.FirstClass.Name);
                        myProduct.SecondClassId = goodDetial.SecondClassId;
                        erjifenlei.setText(goodDetial.SecondClass.Name);
                        product_name.setText(goodDetial.Name);
                        yuanyin.setText(goodDetial.PromotionCause.Name);
                        myProduct.PromotionCause = goodDetial.PromotionCause.Id;
                        product_bianhao.setText(goodDetial.Number);
                        brand.setText(goodDetial.Brand.Name);
                        myProduct.BrandId = goodDetial.BrandId;
                        xingshi.setText(goodDetial.PromotionTypeName);
                        myProduct.PromotionType = goodDetial.PromotionType;
                        xingshi_id = goodDetial.PromotionType;
//                        if (null != MyUserInfoUtils.getInstance().myUserInfo.Area) {
//                            dizhi.setText(MyUserInfoUtils.getInstance().myUserInfo.Province.Name + " " + MyUserInfoUtils.getInstance().myUserInfo.City.Name + "" + MyUserInfoUtils.getInstance().myUserInfo.Area.Name);
//                        } else {
//                            dizhi.setText(MyUserInfoUtils.getInstance().myUserInfo.Province.Name + " " + MyUserInfoUtils.getInstance().myUserInfo.City.Name);
//                        }
//                        xiangxi_address.setText(MyUserInfoUtils.getInstance().myUserInfo.Domicile);
//                        myProduct.ProvinceId = MyUserInfoUtils.getInstance().myUserInfo.ProvinceId;
//                        myProduct.CityId = MyUserInfoUtils.getInstance().myUserInfo.CityId;
//                        myProduct.AreaId = MyUserInfoUtils.getInstance().myUserInfo.AreaId;
                        if (isEnd) {
                            start.setTextColor(getResources().getColor(R.color.stepcolor));
                            end.setTextColor(getResources().getColor(R.color.stepcolor));
                        }
                        start.setText(goodDetial.StartTimeName);
                        end.setText(goodDetial.EndTimeName);
                        myProduct.StartTime = goodDetial.StartTime;
                        myProduct.EndTime = goodDetial.EndTime;
                        yuan_price.setText(goodDetial.OriginalPrice);
                        cuxiao_price.setText(goodDetial.Price);
                        myProduct.UnitId = goodDetial.UnitId;
                        if (null != goodDetial.Unit) {
                            danwei.setText(goodDetial.Unit.Name);
                        }
                        myProduct.CurrencyId = goodDetial.CurrencyId;
                        if (null != goodDetial.Currency) {
                            bizhong.setText(goodDetial.Currency.Name);
                        }
                        myProduct.InvoiceId = goodDetial.InvoiceId;
                        if (null != goodDetial.Invoice) {
                            fapiao.setText(goodDetial.Invoice.Name);
                        }
                        explain.setText(goodDetial.Introduction);
                        descript.setText(goodDetial.Description);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void apply_calssity(String name, String ParentId) {
        WebRequestHelper.json_post(AddGoodsAcitivyt.this, URLText.APPLY_CLASSITY, RequestParamsPool.apply_classity(name, ParentId), new MyAsyncHttpResponseHandler(AddGoodsAcitivyt.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(result);
                    String message = object.optString("Message");
                    String is = object.optString("IsSuccess");
                    if (is.equals("true")) {
                        queryDicNode();
                        classity_dialog.dismiss();
                    }
                    Toast.makeText(AddGoodsAcitivyt.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void save_brand(String name) {
        WebRequestHelper.json_post(AddGoodsAcitivyt.this, URLText.SAVEBRAND, RequestParamsPool.save_brand(name), new MyAsyncHttpResponseHandler(AddGoodsAcitivyt.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(result);
                    String message = object.optString("Message");
                    String is = object.optString("IsSuccess");
                    if (is.equals("true")) {
                        queryDicNode();
                        if (null != brand_dialog) {
                            brand_dialog.dismiss();
                        }
                    }
                    Toast.makeText(AddGoodsAcitivyt.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
