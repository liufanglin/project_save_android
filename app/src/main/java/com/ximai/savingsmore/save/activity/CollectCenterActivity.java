package com.ximai.savingsmore.save.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.cache.MyImageLoader;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.modle.Business;
import com.ximai.savingsmore.save.modle.BusinessList;
import com.ximai.savingsmore.save.modle.Goods;
import com.ximai.savingsmore.save.modle.GoodsList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/11/29.
 */
public class CollectCenterActivity extends BaseActivity implements View.OnClickListener {
    private List<Goods> listGoods = new ArrayList<Goods>();
    private List<Business> listBusiness = new ArrayList<Business>();
    private GoodsAdapter goodsAdapter;
    private BusinessAdapter businessAdapter;
    private ListView listView1, listView2;
    private View line1, line2;
    private TextView goods, business;
    private TextView editor;
    private boolean isGoods = true, isBusiness = false, isEditor = false;
    private LinearLayout all_select;
    private ImageView all_select_image;
    private TextView delect;
    private boolean isAllSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_center_activity);
        setCenterTitle("收藏中心");
        setLeftBackMenuVisibility(CollectCenterActivity.this, "");
        goodsAdapter = new GoodsAdapter();
        businessAdapter = new BusinessAdapter();
        listView1 = (ListView) findViewById(R.id.list1);
        listView2 = (ListView) findViewById(R.id.list2);
        goods = (TextView) findViewById(R.id.is_bag);
        all_select = (LinearLayout) findViewById(R.id.all_select);
        all_select_image = (ImageView) findViewById(R.id.all_select_image);
        delect = (TextView) findViewById(R.id.delect);
        business = (TextView) findViewById(R.id.no_bag);
        goods.setOnClickListener(this);
        business.setOnClickListener(this);
        line1 = findViewById(R.id.introduce);
        line2 = findViewById(R.id.cuxiao_goog);
        listView1.setAdapter(goodsAdapter);
        listView2.setAdapter(businessAdapter);
        editor = (TextView) findViewById(R.id.editor);
        editor.setOnClickListener(this);
        all_select.setOnClickListener(this);
        all_select_image.setOnClickListener(this);
        delect.setOnClickListener(this);
        getCollectBusiness();
        getCollectGoods();
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CollectCenterActivity.this, GoodDetailsActivity.class);
                intent.putExtra("id", listGoods.get(position).ProductId);
                startActivity(intent);
            }
        });

    }

    //得到收藏的商品
    private void getCollectGoods() {
        WebRequestHelper.json_post(CollectCenterActivity.this, URLText.COLLECT_GOODS, RequestParamsPool.collectGoods(), new MyAsyncHttpResponseHandler(CollectCenterActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    //JSONObject object=new JSONObject();
                    GoodsList goodsList = GsonUtils.fromJson(new String(responseBody), GoodsList.class);
                    listGoods = goodsList.MainData;
                    goodsAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //得到收藏的店铺
    private void getCollectBusiness() {
        WebRequestHelper.json_post(CollectCenterActivity.this, URLText.COLLECT_BUSINESS, RequestParamsPool.collectBusiness(), new MyAsyncHttpResponseHandler(CollectCenterActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                BusinessList businessList = GsonUtils.fromJson(new String(responseBody), BusinessList.class);
                listBusiness = businessList.MainData;
                businessAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.is_bag) {
            if (isEditor == false) {
                isGoods = true;
                isBusiness = false;
                line1.setVisibility(View.VISIBLE);
                line2.setVisibility(View.INVISIBLE);
                listView1.setVisibility(View.VISIBLE);
                listView2.setVisibility(View.GONE);
            }
        }
        if (v.getId() == R.id.no_bag) {
            if (isEditor == false) {
                isBusiness = true;
                isGoods = false;
                listView2.setVisibility(View.VISIBLE);
                listView1.setVisibility(View.GONE);
                line1.setVisibility(View.INVISIBLE);
                line2.setVisibility(View.VISIBLE);
            }
        }
        if (v.getId() == R.id.editor) {
            if (!isEditor) {
                all_select.setVisibility(View.VISIBLE);
                editor.setText("完成");
                delect.setVisibility(View.VISIBLE);
                isEditor = true;
                if (isGoods && listGoods != null && listGoods.size() != 0) {
                    for (int i = 0; i < listGoods.size(); i++) {
                        listGoods.get(i).IsEditor = true;
                    }
                    listView1.setAdapter(goodsAdapter);
                } else if (isBusiness && listBusiness != null && listBusiness.size() != 0) {
                    for (int i = 0; i < listBusiness.size(); i++) {
                        listBusiness.get(i).IsEditor = true;
                    }
                    listView2.setAdapter(businessAdapter);
                }
            } else {
                isAllSelect = false;
                if (isGoods) {
                    for (int i = 0; i < listGoods.size(); i++) {
                        listGoods.get(i).IsSelect = false;
                    }
                }
                if (isBusiness) {
                    for (int i = 0; i < listBusiness.size(); i++) {
                        listBusiness.get(i).IsSelect = false;
                    }
                }
                delect.setVisibility(View.GONE);
                all_select.setVisibility(View.GONE);
                isEditor = false;
                editor.setText("编辑");
                if (isGoods && listGoods != null && listGoods.size() != 0) {
                    for (int i = 0; i < listGoods.size(); i++) {
                        listGoods.get(i).IsEditor = false;
                    }
                    listView1.setAdapter(goodsAdapter);
                } else if (isBusiness && listBusiness != null && listBusiness.size() != 0) {
                    for (int i = 0; i < listBusiness.size(); i++) {
                        listBusiness.get(i).IsEditor = false;
                    }
                    listView2.setAdapter(businessAdapter);
                }
            }
            if (!isAllSelect) {
                all_select_image.setBackgroundResource(R.mipmap.noselect);
            } else {
                all_select_image.setBackgroundResource(R.mipmap.select);
            }


        }
        if (v.getId() == R.id.all_select_image) {
            if (isGoods) {
                int a = 0;
                for (int i = 0; i < listGoods.size(); i++) {
                    if (listGoods.get(i).IsSelect) {
                        a++;
                    }
                }
                if (a == listGoods.size()) {
                    isAllSelect = true;
                } else {
                    isAllSelect = false;
                }
            } else if (isBusiness) {
                int a = 0;
                for (int i = 0; i < listBusiness.size(); i++) {
                    if (listBusiness.get(i).IsSelect) {
                        a++;
                    }
                }
                if (a == listBusiness.size()) {
                    isAllSelect = true;
                } else {
                    isAllSelect = false;
                }
            }
            if (isAllSelect) {
                all_select_image.setBackgroundResource(R.mipmap.noselect);
            } else {
                all_select_image.setBackgroundResource(R.mipmap.select);
            }
            if (isGoods) {
                if (isAllSelect) {
                    for (int i = 0; i < listGoods.size(); i++) {
                        listGoods.get(i).IsSelect = false;
                    }
                    goodsAdapter.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < listGoods.size(); i++) {
                        listGoods.get(i).IsSelect = true;
                    }
                    goodsAdapter.notifyDataSetChanged();
                }
            } else if (isBusiness) {
                if (isAllSelect) {
                    for (int i = 0; i < listBusiness.size(); i++) {
                        listBusiness.get(i).IsSelect = false;
                    }
                    businessAdapter.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < listBusiness.size(); i++) {
                        listBusiness.get(i).IsSelect = true;
                    }
                    businessAdapter.notifyDataSetChanged();
                }
            }
        }
        if (v.getId() == R.id.delect) {
            if (isGoods) {
                List<String> list = new ArrayList<>();
                List<Goods> goodsList1 = new ArrayList<Goods>();
                for (int i = 0; i < listGoods.size(); i++) {
                    if (listGoods.get(i).IsSelect) {
                        list.add(listGoods.get(i).Id);
                    } else {
                        goodsList1.add(listGoods.get(i));
                    }
                }
                cancelCollect(list);
                listGoods = goodsList1;
            }
            if (isBusiness) {
                List<String> list = new ArrayList<>();
                List<Business> goodsList1 = new ArrayList<Business>();
                for (int i = 0; i < listBusiness.size(); i++) {
                    if (listBusiness.get(i).IsSelect) {
                        list.add(listBusiness.get(i).Id);
                    } else {
                        goodsList1.add(listBusiness.get(i));
                    }
                }
                cancelBusiness(list);
                listBusiness = goodsList1;
            }
        }

    }

    private class GoodsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listGoods.size();
        }

        @Override
        public Object getItem(int position) {
            return listGoods.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            GoodsViewHodel viewHodel = null;
            if (null == convertView) {
                viewHodel = new GoodsViewHodel();
                convertView = LayoutInflater.from(CollectCenterActivity.this).inflate(R.layout.good_editor_item, null);
                viewHodel.isEditor = (ImageView) convertView.findViewById(R.id.isSelect);
                viewHodel.select = (LinearLayout) convertView.findViewById(R.id.select);
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
            viewHodel = (GoodsViewHodel) convertView.getTag();
            viewHodel.select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < listGoods.size(); i++) {
                        if (i == position) {
                            if (listGoods.get(i).IsSelect) {
                                listGoods.get(i).IsSelect = false;
                            } else {
                                listGoods.get(i).IsSelect = true;
                            }
                        }
                    }
                    goodsAdapter.notifyDataSetChanged();
                }
            });
            MyImageLoader.displayDefaultImage(URLText.img_url + listGoods.get(position).Image, viewHodel.imageView);
            if (listGoods.get(position).IsEditor) {
                viewHodel.select.setVisibility(View.VISIBLE);
            } else {
                viewHodel.select.setVisibility(View.GONE);
            }
            if (listGoods.get(position).IsSelect) {
                viewHodel.isEditor.setBackgroundResource(R.mipmap.select);
            }
            if (null != listGoods.get(position).SaleCount) {
                viewHodel.sales_number.setText("销量:" + listGoods.get(position).SaleCount);
            }

            if (!listGoods.get(position).IsSelect) {
                viewHodel.isEditor.setBackgroundResource(R.mipmap.noselect);
            }
            if (null != listGoods.get(position).Name) {
                viewHodel.name.setText(listGoods.get(position).Name);
            }
            if (null != listGoods.get(position).Preferential) {
                viewHodel.zhekou.setText(listGoods.get(position).PromotionTypeName);
            }
            if (null != listGoods.get(position).StoreName) {
                viewHodel.shop_name.setText(listGoods.get(position).StoreName);
            }
            if (null != listGoods.get(position).FavouriteCount) {
                viewHodel.shou_chang.setText(listGoods.get(position).FavouriteCount);
            }
            viewHodel.liulan.setText(listGoods.get(position).HitCount);
            viewHodel.share.setText(listGoods.get(position).SharedCount);
            if (null != listGoods.get(position).Address) {
                viewHodel.location.setText(listGoods.get(position).Address);
            }
            if (null != listGoods.get(position).StartTimeName) {
                viewHodel.start_time.setText(listGoods.get(position).StartTimeName.split(" ")[0]);
            }
            if (null != listGoods.get(position).EndTimeName) {
                viewHodel.end_time.setText(listGoods.get(position).EndTimeName.split(" ")[0]);
            }
            viewHodel.price.setText("￥" + listGoods.get(position).Price.substring(0, listGoods.get(position).Price.indexOf(".")));

            viewHodel.dazhe_style.setText(listGoods.get(position).Preferential);
            viewHodel.high_price.setText("原价:￥" + listGoods.get(position).OriginalPrice.substring(0, listGoods.get(position).OriginalPrice.indexOf(".")));

            return convertView;
        }
    }

    private class BusinessAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listBusiness.size();
        }

        @Override
        public Object getItem(int position) {
            return listBusiness.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            BusinessViewHodel businessViewHodel = null;
            if (convertView == null) {
                businessViewHodel = new BusinessViewHodel();
                convertView = LayoutInflater.from(CollectCenterActivity.this).inflate(R.layout.business_editor_item, null);
                businessViewHodel.isEditor = (ImageView) convertView.findViewById(R.id.isSelect);
                businessViewHodel.select = (LinearLayout) convertView.findViewById(R.id.select);
                businessViewHodel.headIamge = (ImageView) convertView.findViewById(R.id.business_image);
                businessViewHodel.StoreName = (TextView) convertView.findViewById(R.id.business_name);
                businessViewHodel.Location = (TextView) convertView.findViewById(R.id.business_address);
                businessViewHodel.into_shop = (TextView) convertView.findViewById(R.id.into_shop);
                convertView.setTag(businessViewHodel);
            }
            businessViewHodel = (BusinessViewHodel) convertView.getTag();
            businessViewHodel.into_shop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CollectCenterActivity.this, BusinessMessageActivity.class);
                    intent.putExtra("id", listBusiness.get(position).UserId);
                    startActivity(intent);
                }
            });
            businessViewHodel.select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < listBusiness.size(); i++) {
                        if (i == position) {
                            if (listBusiness.get(i).IsSelect) {
                                listBusiness.get(i).IsSelect = false;
                            } else {
                                listBusiness.get(i).IsSelect = true;
                            }
                        }
                    }
                    businessAdapter.notifyDataSetChanged();
                }
            });
            if (listBusiness.get(position).IsEditor) {
                businessViewHodel.select.setVisibility(View.VISIBLE);
            } else {
                businessViewHodel.select.setVisibility(View.GONE);
            }
            if (listBusiness.get(position).IsSelect) {
                businessViewHodel.isEditor.setBackgroundResource(R.mipmap.select);
            }
            if (!listBusiness.get(position).IsSelect) {
                businessViewHodel.isEditor.setBackgroundResource(R.mipmap.noselect);
            }
            MyImageLoader.displayDefaultImage(URLText.img_url + listBusiness.get(position).PhotoPath, businessViewHodel.headIamge);
            businessViewHodel.StoreName.setText(listBusiness.get(position).StoreName);
            businessViewHodel.Location.setText(listBusiness.get(position).Province + " " + listBusiness.get(position).City);
            return convertView;
        }
    }

    class GoodsViewHodel {
        private TextView sales_number;
        public LinearLayout select;
        public ImageView isEditor;
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
    }

    class BusinessViewHodel {
        TextView into_shop;
        public LinearLayout select;
        public ImageView isEditor;
        ImageView headIamge;
        TextView StoreName;
        TextView Location;
    }

    //取消收藏商品
    public void cancelCollect(List<String> list) {
        WebRequestHelper.json_post(CollectCenterActivity.this, URLText.CANCEL_COLLECT, RequestParamsPool.cancelColect("", list), new MyAsyncHttpResponseHandler(CollectCenterActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.optString("Message");
                    Toast.makeText(CollectCenterActivity.this, message, Toast.LENGTH_SHORT).show();
                    goodsAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //移除收藏店铺
    private void cancelBusiness(List<String> list) {
        WebRequestHelper.json_post(CollectCenterActivity.this, URLText.CANCEL_COLLECT_BUSINESS, RequestParamsPool.cancelBusiness("", list), new MyAsyncHttpResponseHandler(CollectCenterActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.optString("Message");
                    Toast.makeText(CollectCenterActivity.this, message, Toast.LENGTH_SHORT).show();
                    businessAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
