package com.ximai.savingsmore.save.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.ximai.savingsmore.save.common.BaseApplication;
import com.ximai.savingsmore.save.modle.BaseMessage;
import com.ximai.savingsmore.save.modle.GoodSalesType;
import com.ximai.savingsmore.save.modle.Goods;
import com.ximai.savingsmore.save.modle.GoodsList;
import com.ximai.savingsmore.save.modle.ListBaseMessage;
import com.ximai.savingsmore.save.modle.LoginUser;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/11/21.
 */
//搜索结果
public class SearchResultActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private RecyclerView recyclerView;
    private List<Goods> list = new ArrayList<Goods>();
    private OnItemClickEventListener listener = null;
    private GoodsList goodsList = new GoodsList();
    private MyAdapter myAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;
    private int pageSize = 10;
    private Boolean isRefreshing = false;
    private int lastVisibleItem;
    private LinearLayout zhekou, jiage, date, juli;
    private ImageView zhekou_image, jiage_image, date_image, juli_image;
    private Boolean IsRebateDesc = false, IsPriceDesc = false, IsStartTimeDesc = false, IsDistanceDesc = false;
    private EditText search;
    private String isBag, state;
    private String classify1, classify2, brand, typeId, keyWord;
    String Longitude1;
    String Latitude1;
    String Area;
    private List<BaseMessage> qu_list = new ArrayList<BaseMessage>();
    private String AreaId;
    String Sheng;
    String ProvceId;
    String Shi;
    String CityId;
    boolean isSearch;
    private String IsPromotion = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list_activity);
        setLeftBackMenuVisibility(SearchResultActivity.this, "");
        setCenterTitle(getIntent().getStringExtra("title"));
        recyclerView = (RecyclerView) findViewById(R.id.list);
        zhekou = (LinearLayout) findViewById(R.id.zhekou);
        jiage = (LinearLayout) findViewById(R.id.jiage);
        date = (LinearLayout) findViewById(R.id.riqi);
        juli = (LinearLayout) findViewById(R.id.juli);
        zhekou_image = (ImageView) findViewById(R.id.zhekou_direction);
        jiage_image = (ImageView) findViewById(R.id.jiage_direction);
        date_image = (ImageView) findViewById(R.id.riqi_direction);
        juli_image = (ImageView) findViewById(R.id.juli_direction);
        search = (EditText) findViewById(R.id.search);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    page = 1;
                    list.clear();
                    myAdapter.notifyDataSetChanged();
                    keyWord = v.getText().toString();
                    getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, v.getText().toString());
                }
                return true;
            }
        });
        zhekou.setOnClickListener(this);
        jiage.setOnClickListener(this);
        date.setOnClickListener(this);
        juli.setOnClickListener(this);
        recyclerView.setOnScrollListener(new MyOnScrollListener());
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.good_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_purple, android.R.color.holo_blue_bright, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);
        myAdapter = new MyAdapter();
        mLayoutManager = new LinearLayoutManager(SearchResultActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(myAdapter);

        Intent intent = getIntent();
        Area = getIntent().getStringExtra("qu");
        Shi = getIntent().getStringExtra("shi");
        Sheng = getIntent().getStringExtra("sheng");
        Longitude1 = getIntent().getStringExtra("long");
        Latitude1 = getIntent().getStringExtra("lat");
        if (null == Longitude1) {
            Longitude1 = BaseApplication.getInstance().Longitude + "";
        }
        if (null == Latitude1) {
            Latitude1 = BaseApplication.getInstance().Latitude + "";
        }
        isSearch = getIntent().getBooleanExtra("search", false);
        if (null != getIntent()) {
            IsPromotion = intent.getStringExtra("state");
            if (null != IsPromotion) {
                if (IsPromotion.equals("all")) {
                    IsPromotion = null;
                }
            }
            isBag = intent.getStringExtra("isBag");
            keyWord = intent.getStringExtra("keyStore");
            classify1 = intent.getStringExtra("classify1");
            classify2 = intent.getStringExtra("classify2");
            state = intent.getStringExtra("state");
            brand = intent.getStringExtra("brand");
            typeId = intent.getStringExtra("typeId");
            ProvceId = intent.getStringExtra("shengId");
            CityId = intent.getStringExtra("shiId");
            AreaId = intent.getStringExtra("xianId");
        }
        if (null != Area) {
            queryDicNode(Area);
        } else {
            getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);
        }

    }


    //下拉刷新
    @Override
    public void onRefresh() {
        page = 1;
        list.clear();
        myAdapter.notifyDataSetChanged();
        if (!isRefreshing) {
            getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zhekou:
                if (IsRebateDesc == null) {
                    IsRebateDesc = false;
                }
                page = 1;
                list.clear();
                myAdapter.notifyDataSetChanged();
                if (IsRebateDesc) {
                    IsRebateDesc = false;
                    IsPriceDesc = null;
                    IsStartTimeDesc = null;
                    IsDistanceDesc = null;
                    getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);
                    zhekou_image.setBackgroundResource(R.mipmap.up3);
                } else {
                    IsRebateDesc = true;
                    IsPriceDesc = null;
                    IsStartTimeDesc = null;
                    IsDistanceDesc = null;
                    getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);
                    zhekou_image.setBackgroundResource(R.mipmap.down3);
                }
                break;
            case R.id.jiage:
                if (IsPriceDesc == null) {
                    IsPriceDesc = false;
                }
                page = 1;
                list.clear();
                myAdapter.notifyDataSetChanged();
                if (IsPriceDesc) {
                    IsPriceDesc = false;
                    IsRebateDesc = null;
                    IsStartTimeDesc = null;
                    IsDistanceDesc = null;
                    getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);
                    jiage_image.setBackgroundResource(R.mipmap.up3);
                } else {
                    IsPriceDesc = true;
                    IsRebateDesc = null;
                    IsStartTimeDesc = null;
                    IsDistanceDesc = null;
                    getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);
                    jiage_image.setBackgroundResource(R.mipmap.down3);
                }
                break;
            case R.id.riqi:
                if (IsStartTimeDesc == null) {
                    IsStartTimeDesc = false;
                }
                page = 1;
                list.clear();
                myAdapter.notifyDataSetChanged();
                if (IsStartTimeDesc) {
                    IsStartTimeDesc = false;
                    IsPriceDesc = null;
                    IsRebateDesc = null;
                    IsDistanceDesc = null;
                    getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);
                    date_image.setBackgroundResource(R.mipmap.up3);
                } else {
                    IsStartTimeDesc = true;
                    IsPriceDesc = null;
                    IsRebateDesc = null;
                    IsDistanceDesc = null;
                    getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);
                    date_image.setBackgroundResource(R.mipmap.down3);
                }
                break;
            case R.id.juli:
                if (IsDistanceDesc == null) {
                    IsDistanceDesc = false;
                }
                page = 1;
                list.clear();
                myAdapter.notifyDataSetChanged();
                if (IsDistanceDesc) {
                    IsDistanceDesc = false;
                    IsStartTimeDesc = null;
                    IsPriceDesc = null;
                    IsRebateDesc = null;
                    getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);
                    juli_image.setBackgroundResource(R.mipmap.up3);
                } else {
                    IsDistanceDesc = true;
                    IsStartTimeDesc = null;
                    IsPriceDesc = null;
                    IsRebateDesc = null;
                    getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);
                    juli_image.setBackgroundResource(R.mipmap.down3);
                }
                break;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(SearchResultActivity.this).inflate(R.layout.good_item, null);
            return new MyViewHodel(layout, new OnItemClickEventListener() {
                @Override
                public void onItemClick(int position) {
                    if (LoginUser.getInstance().isLogin()) {
                        Intent intent = new Intent(SearchResultActivity.this, GoodDetailsActivity.class);
                        intent.putExtra("id", list.get(position).Id);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SearchResultActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(SearchResultActivity.this, "温馨提示,您还没有登录", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onItemLongClick(int position) {

                }
            });
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHodel viewHodel = (MyViewHodel) holder;
            MyImageLoader.displayDefaultImage(URLText.img_url + list.get(position).Image, viewHodel.imageView);
            if (null != list.get(position).Name) {
                viewHodel.name.setText(list.get(position).Name);
            }
            if (null != list.get(position).SaleCount) {
                viewHodel.sales_count.setText("销量:" + list.get(position).SaleCount);
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
            viewHodel.price.setText("￥" + list.get(position).Price.substring(0, list.get(position).Price.indexOf(".")));
            viewHodel.dazhe_style.setText(list.get(position).Preferential);
            viewHodel.high_price.setText("原价:￥" + list.get(position).OriginalPrice.substring(0, list.get(position).OriginalPrice.indexOf(".")));


        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    public interface OnItemClickEventListener {

        void onItemClick(int position);

        void onItemLongClick(int position);
    }

    public void setOnItemClickEventListener(OnItemClickEventListener listener) {
        this.listener = listener;
    }

    class MyViewHodel extends RecyclerView.ViewHolder {
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
        public TextView sales_count;


        public MyViewHodel(View itemView, final OnItemClickEventListener lis) {
            super(itemView);
            sales_count = (TextView) itemView.findViewById(R.id.sales_number);
            name = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            zhekou = (TextView) itemView.findViewById(R.id.zhekou);
            shop_name = (TextView) itemView.findViewById(R.id.shop_name);
            shou_chang = (TextView) itemView.findViewById(R.id.dainzan_number);
            liulan = (TextView) itemView.findViewById(R.id.clock_number);
            share = (TextView) itemView.findViewById(R.id.share_number);
            location = (TextView) itemView.findViewById(R.id.location);
            start_time = (TextView) itemView.findViewById(R.id.statr_time);
            end_time = (TextView) itemView.findViewById(R.id.end_time);
            price = (TextView) itemView.findViewById(R.id.price);
            dazhe_style = (TextView) itemView.findViewById(R.id.dazhe);
            high_price = (TextView) itemView.findViewById(R.id.high_price);
            high_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);


            if (null != lis) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lis.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }

    //得到所有的商品
    private void getAllGoods(String Provice, String City, String AreaId, String Longitude, String Latitude, String isBag, String isState, String class1, String class2, String brand, String type, int pageNo, int pageSize, Boolean IsRebateDesc, Boolean IsPriceDesc, Boolean IsStartTimeDesc, Boolean IsDistanceDesc, String keyword) {
        isRefreshing = true;
        swipeRefreshLayout.setRefreshing(isRefreshing);
        WebRequestHelper.json_post(SearchResultActivity.this, URLText.GET_GOODS, RequestParamsPool.getAllGoods(IsPromotion, Provice, City, AreaId, Longitude1, Latitude1, pageNo, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyword, isBag, isState, class1, class2, brand, type), new MyAsyncHttpResponseHandler(SearchResultActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String resule = new String(responseBody);
                goodsList = GsonUtils.fromJson(new String(responseBody), GoodsList.class);
                if (goodsList.IsSuccess) {
                    if (null != goodsList.MainData) {
                        list.addAll(goodsList.MainData);
                        if (list.size() == 0 && isSearch) {
                            Toast.makeText(SearchResultActivity.this, "请调整搜索条件，再搜！", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                }
                isRefreshing = false;
                swipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }

    //分页
    private class MyOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            swipeRefreshLayout.setEnabled(((LinearLayoutManager) mLayoutManager).findFirstCompletelyVisibleItemPosition() == 0);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                    lastVisibleItem + 1 == myAdapter.getItemCount()&&myAdapter.getItemCount()>4) {
                page++;
                getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);
            }
        }
    }

    //查询基础字典
    private void queryDicNode(final String Area) {
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebRequestHelper.json_post(SearchResultActivity.this, URLText.QUERYDICNODE, stringEntity, new MyAsyncHttpResponseHandler(SearchResultActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                ListBaseMessage listBaseMessage = GsonUtils.fromJson(new String(responseBody), ListBaseMessage.class);
                qu_list = listBaseMessage.MainData;
                for (int i = 0; i < qu_list.size(); i++) {
                    if (Sheng.equals(Shi)) {
                        if (null != qu_list.get(i).Name && null != qu_list.get(i).ParentId && null != qu_list.get(i).Id && Sheng.contains(qu_list.get(i).Name)) {
                            ProvceId = qu_list.get(i).Id;
                        }
                        if (null != qu_list.get(i).Name && null != qu_list.get(i).ParentId && null != qu_list.get(i).Id && qu_list.get(i).Name.equals(Area)) {
                            CityId = qu_list.get(i).Id;
                        }
                    } else {
                        if (null != qu_list.get(i).Name && null != qu_list.get(i).ParentId && null != qu_list.get(i).Id && Sheng.contains(qu_list.get(i).Name)) {
                            ProvceId = qu_list.get(i).Id;
                        }
                        if (null != qu_list.get(i).Name && null != qu_list.get(i).ParentId && null != qu_list.get(i).Id && Shi.contains(qu_list.get(i).Name)) {
                            CityId = qu_list.get(i).Id;
                        }
                        if (null != qu_list.get(i).Name && null != qu_list.get(i).ParentId && null != qu_list.get(i).Id && qu_list.get(i).Name.equals(Area)) {
                            AreaId = qu_list.get(i).Id;
                        }
                    }
                }

                getAllGoods(ProvceId, CityId, AreaId, Longitude1, Latitude1, isBag, state, classify1, classify2, brand, typeId, page, pageSize, IsRebateDesc, IsPriceDesc, IsStartTimeDesc, IsDistanceDesc, keyWord);

            }
        });
    }
}
