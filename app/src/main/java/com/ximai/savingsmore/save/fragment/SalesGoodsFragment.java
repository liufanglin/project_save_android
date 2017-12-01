package com.ximai.savingsmore.save.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.cache.MyImageLoader;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.library.toolbox.LogUtils;
import com.ximai.savingsmore.save.activity.GoodDetailsActivity;
import com.ximai.savingsmore.save.modle.BusinessMessage;
import com.ximai.savingsmore.save.modle.GoodDetial;
import com.ximai.savingsmore.save.modle.Goods;
import com.ximai.savingsmore.save.modle.GoodsList;
import com.ximai.savingsmore.save.modle.User;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/11/28.
 */
//促销商品
public class SalesGoodsFragment extends Fragment {
    //private User user;
    private GridView recyclerView;
    private GoodsList goodsList = new GoodsList();
    private List<Goods> list = new ArrayList<Goods>();
    private MyAdapter myAdapter;
    private GoodDetial good;
    private BusinessMessage businessMessage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.business_sales_good, null);
        recyclerView = (GridView) view.findViewById(R.id.gridview);
        //good = (User) getArguments().getSerializable("user");
        businessMessage= (BusinessMessage) getArguments().getSerializable("good");
       // user=good.User;
        getAllGoods(businessMessage.Id);
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),GoodDetailsActivity.class);
                intent.putExtra("id",list.get(position).Id);
                startActivity(intent);
            }
        });
        return view;
    }

    private void getAllGoods(String SellerId) {
        WebRequestHelper.json_post(getActivity(), URLText.GET_GOODS, RequestParamsPool.getSalesGoods(SellerId), new MyAsyncHttpResponseHandler(getActivity()) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String resule = new String(responseBody);
                goodsList = GsonUtils.fromJson(new String(responseBody), GoodsList.class);
                if (goodsList.IsSuccess) {
                    if (null != goodsList.MainData) {
                        list.addAll(goodsList.MainData);
                        myAdapter.notifyDataSetChanged();
                    }
                }
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodel viewHodel;
            if (null == convertView) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.sales_goods_item, null);
                viewHodel = new ViewHodel();
                viewHodel.big_image = (ImageView) convertView.findViewById(R.id.images);
                viewHodel.dazhe_style = (TextView) convertView.findViewById(R.id.small_image);
                viewHodel.name = (TextView) convertView.findViewById(R.id.goods_name);
                viewHodel.price = (TextView) convertView.findViewById(R.id.good_price);
                viewHodel.original_price = (TextView) convertView.findViewById(R.id.original_price);
                viewHodel.original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                convertView.setTag(viewHodel);
            }
            viewHodel = (ViewHodel) convertView.getTag();
            MyImageLoader.displayDefaultImage(URLText.img_url + list.get(position).Image, viewHodel.big_image);
            viewHodel.dazhe_style.setText(list.get(position).Preferential);
            viewHodel.price.setText("￥"+list.get(position).Price);
            viewHodel.original_price.setText("原价"+list.get(position).OriginalPrice);
            viewHodel.name.setText(list.get(position).Name);

            return convertView;
        }

        class ViewHodel {
            ImageView big_image;
            TextView dazhe_style;
            TextView name;
            TextView price;
            TextView original_price;
        }
    }

}
