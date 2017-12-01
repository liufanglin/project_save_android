package com.ximai.savingsmore.save.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.modle.MyUserInfoUtils;
import com.ximai.savingsmore.save.modle.Order;
import com.ximai.savingsmore.save.modle.OrderList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/12/28.
 */
public class OrderCenterActivity extends BaseActivity {
    private List<Order> order = new ArrayList<Order>();
    private MyAdapter myAdapter;
    private ListView list;
    private OrderList orderList;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_center_activity);
        //title = getIntent().getStringExtra("title");
        if (MyUserInfoUtils.getInstance().myUserInfo.UserType.equals("2")) {
            setCenterTitle("收货中心");
            title = "收货中心";
        } else {
            setCenterTitle("订单中心");
            title = "订单中心";
        }
        setLeftBackMenuVisibility(OrderCenterActivity.this, "");
        list = (ListView) findViewById(R.id.list);
        myAdapter = new MyAdapter();
        list.setAdapter(myAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrderCenterActivity.this, OrderDetialActivity.class);
                if (title.equals("订单中心")) {
                    intent.putExtra("type", "business");
                } else {
                    intent.putExtra("type", "person");
                }
                intent.putExtra("Id", order.get(position).Id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyOrder();
    }

    private void getMyOrder() {
        String url = null;
        if (title.equals("订单中心")) {
            url = URLText.BUSINESS_GET_ORDER;
        } else {
            url = URLText.GET_ORDER;
        }
        WebRequestHelper.json_post(OrderCenterActivity.this, url, RequestParamsPool.getOrder(), new MyAsyncHttpResponseHandler(OrderCenterActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    orderList = GsonUtils.fromJson(object.toString(), OrderList.class);
                    if (null != orderList) {
                        order = orderList.MainData;
                        myAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return order.size();
        }

        @Override
        public Object getItem(int position) {
            return order.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodel viewHodel;
            if (convertView == null) {
                viewHodel = new ViewHodel();
                convertView = LayoutInflater.from(OrderCenterActivity.this).inflate(R.layout.order_center_item, null);
                viewHodel.bianhao = (TextView) convertView.findViewById(R.id.bianhao);
                viewHodel.price = (TextView) convertView.findViewById(R.id.price);
                viewHodel.time = (TextView) convertView.findViewById(R.id.time);
                viewHodel.sate = (TextView) convertView.findViewById(R.id.state);
                convertView.setTag(viewHodel);
            }
            viewHodel = (ViewHodel) convertView.getTag();
            viewHodel.bianhao.setText(order.get(position).Number);
            viewHodel.price.setText("￥" + order.get(position).Price);
            viewHodel.time.setText(order.get(position).CreateTimeName);
            viewHodel.sate.setText(order.get(position).OrderStateName);
            return convertView;
        }
    }

    private class ViewHodel {
        TextView bianhao;
        TextView price;
        TextView time;
        TextView sate;
    }
}
