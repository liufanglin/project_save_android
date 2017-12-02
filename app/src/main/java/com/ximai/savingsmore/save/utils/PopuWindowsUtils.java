package com.ximai.savingsmore.save.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.cache.MyImageLoader;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.save.activity.GoodDetailsActivity;
import com.ximai.savingsmore.save.activity.LoginActivity;
import com.ximai.savingsmore.save.common.BaseApplication;
import com.ximai.savingsmore.save.fragment.MapFragment;
import com.ximai.savingsmore.save.modle.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/11/21.
 */
public class PopuWindowsUtils implements View.OnClickListener {
    private Context context;
    private PopupWindow popupWindow;
    //到这里去按钮
    private Button goBtn;
    private ViewPager viewPager;
    private List<Goods> list;
    private List<View> viewList;
    private MyAdapter myAdapter;
    private callBack callBack;
    private boolean isScoll = false;

    public PopuWindowsUtils(final Context context, List<Goods> list, final callBack callBack) {
        this.callBack = callBack;
        this.context = context;
        this.list = list;
        View view = LayoutInflater.from(context).inflate(R.layout.item_share_layout, null);
        viewList = new ArrayList<View>();
        for (int i = 0; i < list.size(); i++) {
            View item = LayoutInflater.from(context).inflate(R.layout.map_goods_item, null);
            viewList.add(item);
        }
        myAdapter = new MyAdapter();
        viewPager = (ViewPager) view.findViewById(R.id.viewPage);
        viewPager.setAdapter(myAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                callBack.call(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources()));
        //viewPager= (ViewPager) view.findViewById(R.id.viewPager);
        //goBtn = (Button) view.findViewById(R.id.go_to_hotel_btn);
        // goBtn.setOnClickListener(this);

    }

    // 下拉式 弹出 pop菜单 parent 右下角
    public void showAsDropDown(View parent, int position) {
        viewPager.setCurrentItem(position);

        // 保证尺寸是根据屏幕像素密度来的
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        // 使其聚集
        popupWindow.setFocusable(false);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 设置动画
        //popupWindow.setAnimationStyle(R.style.PopupWindowAnimStyle);
        // 刷新状态
        popupWindow.update();
    }

    public void setDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        popupWindow.setOnDismissListener(onDismissListener);
    }

    // 隐藏菜单
    public void dismixss() {
        popupWindow.dismiss();
    }

    // 是否显示
    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    @Override
    public void onClick(View v) {

    }


    public interface callBack {
        void call(int posiiton);
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
            ;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            View itemView = viewList.get(position);
            ImageView imageView;
            TextView sales_number;
            TextView name;
            TextView zhekou;
            TextView shop_name;
            TextView shou_chang;
            TextView liulan;
            TextView share;
            TextView location;
            TextView start_time;
            TextView end_time;
            TextView price;
            TextView dazhe_style;
            TextView high_price;
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


            MyImageLoader.displayDefaultImage(URLText.img_url + list.get(position).Image, imageView);

            if (null != list.get(position).Name) {
                name.setText(list.get(position).Name);
            }

            if (null != list.get(position).SaleCount) {
                sales_number.setText("销量:" + list.get(position).SaleCount+"件");
            }
            if (null != list.get(position).Preferential) {
                zhekou.setText(list.get(position).PromotionTypeName);
            }
            if (null != list.get(position).StoreName) {
                shop_name.setText(list.get(position).StoreName);
            }
            if (null != list.get(position).FavouriteCount) {
                shou_chang.setText("赞 "+list.get(position).FavouriteCount);
            }
            liulan.setText("评论 "+list.get(position).HitCount);
            share.setText("分享 "+list.get(position).SharedCount);
            if (null != list.get(position).Address) {
                location.setText(list.get(position).Address);
            }
            if (null != list.get(position).StartTimeName) {
                start_time.setText(list.get(position).StartTimeName.split(" ")[0]);
            }
            if (null != list.get(position).EndTimeName) {
                end_time.setText(list.get(position).EndTimeName.split(" ")[0]);
            }
            price.setText("￥" + list.get(position).Price.substring(0, list.get(position).Price.indexOf(".")));
            dazhe_style.setText(list.get(position).Preferential);
            high_price.setText("原价:￥" + list.get(position).OriginalPrice.substring(0, list.get(position).OriginalPrice.indexOf(".")));
            container.addView(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (com.ximai.savingsmore.save.modle.LoginUser.getInstance().isLogin()) {
                        Intent intent = new Intent(context, GoodDetailsActivity.class);
                        intent.putExtra("id", list.get(position).Id);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        Toast.makeText(context, "温馨提示,您还没有登录", Toast.LENGTH_LONG).show();
                    }
                }
            });
            return itemView;
        }
    }


}
