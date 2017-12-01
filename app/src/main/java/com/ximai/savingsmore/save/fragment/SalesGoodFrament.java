package com.ximai.savingsmore.save.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.cache.MyImageLoader;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.save.activity.AddGoodsAcitivyt;
import com.ximai.savingsmore.save.activity.GoodDetailsActivity;
import com.ximai.savingsmore.save.activity.GoodsCommentActiviyt;
import com.ximai.savingsmore.save.modle.Comment;
import com.ximai.savingsmore.save.modle.CommentList;
import com.ximai.savingsmore.save.modle.Goods;
import com.ximai.savingsmore.save.modle.GoodsList;
import com.ximai.savingsmore.save.modle.LoginUser;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/12/9.
 */
public class SalesGoodFrament extends Fragment {
    private RecyclerView recyclerView;
    private List<Goods> list = new ArrayList<Goods>();
    private OnItemClickEventListener listener = null;
    private GoodsList goodsList = new GoodsList();
    private MyAdapter myAdapter;
    private LinearLayoutManager mLayoutManager;
    private boolean isComment, isHit, isCollect;
    private AlertDialog classity_dialog;
    private List<Comment> commentList = new ArrayList<Comment>();
    private List<Goods> all_list = new ArrayList<Goods>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_bag_goods, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        myAdapter = new MyAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(myAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != getArguments().getString("isComment")) {
            if (getArguments().getString("isComment").equals("true")) {
                isComment = true;
            }
        }
        if (null != getArguments().getString("hit")) {
            if (getArguments().getString("hit").equals("true")) {
                isHit = true;
            }
        }
        if (null != getArguments().getString("collect")) {
            if (getArguments().getString("collect").equals("true")) {
                isCollect = true;
            }
        }
        // getAllGoods();
    }

    private class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.good_item, null);
            return new MyViewHodel(layout, new OnItemClickEventListener() {
                @Override
                public void onItemClick(int position) {
                    if (LoginUser.getInstance().isLogin()) {
                        if (isComment) {
                            Intent intent = new Intent(getActivity(), GoodsCommentActiviyt.class);
                            intent.putExtra("id", list.get(position).Id);
                            intent.putExtra("score", list.get(position).Score);
                            intent.putExtra("isComment", "true");
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(getActivity(), AddGoodsAcitivyt.class);
                            intent.putExtra("id", list.get(position).Id);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onItemLongClick(final int position) {
                    classity_dialog = new AlertDialog.Builder(getActivity()).create();
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.delect_good_dialog, null);
                    final TextView queding, quxiao;
                    final EditText content;
                    queding = (TextView) view.findViewById(R.id.commit);
                    quxiao = (TextView) view.findViewById(R.id.cancel);
                    queding.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeGood(list.get(position).Id);
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
            }

            );
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHodel viewHodel = (MyViewHodel) holder;
            MyImageLoader.displayDefaultImage(URLText.img_url + list.get(position).Image, viewHodel.imageView);
            if (null != list.get(position).Name) {
                viewHodel.name.setText(list.get(position).Name);
            }
            if (null != list.get(position).SaleCount) {
                viewHodel.sales_number.setText("销量:" + list.get(position).SaleCount);
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
        public TextView sales_number;
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


        public MyViewHodel(View itemView, final OnItemClickEventListener lis) {
            super(itemView);
            sales_number = (TextView) itemView.findViewById(R.id.sales_number);
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
                        return true;
                    }
                });
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getAllGoods();
    }

    //得到所有的商品
    private void getAllGoods() {
//        isRefreshing = true;
//        swipeRefreshLayout.setRefreshing(isRefreshing);
        WebRequestHelper.json_post(getActivity(), URLText.GET_SALES_GOODS, RequestParamsPool.getMySalesGoods(false, isComment, isHit, isCollect), new MyAsyncHttpResponseHandler(getActivity()) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String resule = new String(responseBody);
                goodsList = GsonUtils.fromJson(new String(responseBody), GoodsList.class);
                if (goodsList.IsSuccess) {
                    if (null != goodsList.MainData) {
                        all_list = goodsList.MainData;
                    }
                    if (isComment && all_list.size() > 0) {
                        list.clear();
                        for (int i = 0; i < all_list.size(); i++) {
                            getComment(all_list.get(i).Id, all_list.get(i));
                        }
                    } else {
                        list = all_list;
                        myAdapter.notifyDataSetChanged();
                    }
                }
//                isRefreshing = false;
//                swipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }

    //移除商品
    private void removeGood(String Id) {
        WebRequestHelper.json_post(getActivity(), URLText.REMOVE_MYGOODS, RequestParamsPool.removeGoods(Id), new MyAsyncHttpResponseHandler(getActivity()
        ) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                JSONObject object = new JSONObject();
            }
        });
    }

    private void getComment(String id, final Goods goods) {
        WebRequestHelper.json_post(getActivity(), URLText.GOODS_COMMENT, RequestParamsPool.getGoodsComment(id), new MyAsyncHttpResponseHandler(getActivity()) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                CommentList commentList1 = GsonUtils.fromJson(result, CommentList.class);
                commentList = commentList1.MainData;
                if (commentList.size() > 0) {
                    list.add(goods);
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
