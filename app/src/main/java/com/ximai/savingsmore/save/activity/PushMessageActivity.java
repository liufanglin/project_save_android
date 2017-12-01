package com.ximai.savingsmore.save.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ximai.savingsmore.save.modle.Goods;
import com.ximai.savingsmore.save.modle.GoodsList;
import com.ximai.savingsmore.save.modle.LoginUser;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/12/23.
 */
public class PushMessageActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private GoodsList goodsList = new GoodsList();
    private List<Goods> list = new ArrayList<Goods>();
    private MyAdapter myAdapter;
    private OnItemClickEventListener listener = null;
    private LinearLayoutManager mLayoutManager;
    private AlertDialog classity_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_message_activity);
        setCenterTitle("推送消息");
        setLeftBackMenuVisibility(PushMessageActivity.this, "");
        recyclerView = (RecyclerView) findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(PushMessageActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        getPushGood();
    }

    private void getPushGood() {
        WebRequestHelper.json_post(PushMessageActivity.this, URLText.PUSH_GOODS, RequestParamsPool.getPushGood(), new MyAsyncHttpResponseHandler(PushMessageActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                goodsList = GsonUtils.fromJson(new String(responseBody), GoodsList.class);
                if (goodsList.IsSuccess) {
                    if (null != goodsList.MainData) {
                        list.addAll(goodsList.MainData);
                        if (list.size() == 0) {
                            // Toast.makeText(SearchResultActivity.this,"请调整搜索条件,再搜",Toast.LENGTH_SHORT).show();
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void removePushGood(String Id) {
        WebRequestHelper.json_post(PushMessageActivity.this, URLText.REMOVE_PUSH_GOODS, RequestParamsPool.removePushGoods(Id), new MyAsyncHttpResponseHandler(PushMessageActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                JSONObject object = new JSONObject();
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(PushMessageActivity.this).inflate(R.layout.good_item, null);
            return new MyViewHodel(layout, new OnItemClickEventListener() {
                @Override
                public void onItemClick(int position) {
                    if (LoginUser.getInstance().isLogin()) {
                        Intent intent = new Intent(PushMessageActivity.this, GoodDetailsActivity.class);
                        intent.putExtra("id", list.get(position).ProductId);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(PushMessageActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(PushMessageActivity.this, "温馨提示,您还没有登录", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onItemLongClick(final int position) {
                    classity_dialog = new AlertDialog.Builder(PushMessageActivity.this).create();
                    View view = LayoutInflater.from(PushMessageActivity.this).inflate(R.layout.delect_good_dialog, null);
                    final TextView queding, quxiao;
                    final EditText content;
                    queding = (TextView) view.findViewById(R.id.commit);
                    quxiao = (TextView) view.findViewById(R.id.cancel);
                    queding.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removePushGood(list.get(position).Id);
                            list.remove(position);
                            myAdapter.notifyDataSetChanged();
                            classity_dialog.dismiss();
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
                viewHodel.number.setText("销量:" + list.get(position).SaleCount);
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
        public TextView number;
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


        public MyViewHodel(View itemView, final OnItemClickEventListener lis) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.sales_number);
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
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        lis.onItemLongClick(getAdapterPosition());
                        return false;
                    }
                });
            }
        }
    }
}
