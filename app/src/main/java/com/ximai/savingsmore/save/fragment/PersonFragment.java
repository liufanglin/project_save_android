package com.ximai.savingsmore.save.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
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
import com.ximai.savingsmore.library.toolbox.PreferencesUtils;
import com.ximai.savingsmore.library.view.RoundImageView;
import com.ximai.savingsmore.library.view.ScrollViewWithListView;
import com.ximai.savingsmore.save.activity.CollectCenterActivity;
import com.ximai.savingsmore.save.activity.HotSalesGoods;
import com.ximai.savingsmore.save.activity.MainActivity;
import com.ximai.savingsmore.save.activity.MessageCenterActivity;
import com.ximai.savingsmore.save.activity.OrderCenterActivity;
import com.ximai.savingsmore.save.activity.PersonalMyMessageActivity;
import com.ximai.savingsmore.save.activity.PointManagerActivity;
import com.ximai.savingsmore.save.activity.PushMessageActivity;
import com.ximai.savingsmore.save.activity.SearchActivity;
import com.ximai.savingsmore.save.activity.SettingActivity;
import com.ximai.savingsmore.save.modle.BaseMessage;
import com.ximai.savingsmore.save.modle.GoodSalesType;
import com.ximai.savingsmore.save.modle.GoodSalesTypeList;
import com.ximai.savingsmore.save.modle.ListBaseMessage;
import com.ximai.savingsmore.save.modle.MenuNumber;
import com.ximai.savingsmore.save.modle.MyUserInfoUtils;
import com.ximai.savingsmore.save.modle.PushSettings;
import com.ximai.savingsmore.save.modle.ShareData;
import com.ximai.savingsmore.save.utils.EventCenter;
import com.ximai.savingsmore.save.utils.ShareUtils;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by caojian on 16/11/21.
 */
//个人侧栏
public class PersonFragment extends Fragment implements View.OnClickListener, EMEventListener, Observer {
    private RoundImageView head;
    private RelativeLayout hot_sales;
    private TextView name;
    private ImageView setting;
    private RelativeLayout search, collect, message_center, point_manager, order_center;
    private String result;
    private List<BaseMessage> list = new ArrayList<BaseMessage>();
    private List<BaseMessage> isbag_good_one_classify = new ArrayList<BaseMessage>();
    private List<BaseMessage> nobag_good_one_classify = new ArrayList<BaseMessage>();
    private ScrollViewWithListView is_bag_list, no_bag_list;
    private MyAdpter1 myAdpter1;
    private MyAdpter2 myAdpter2;
    List<PushSettings> push_list = new ArrayList<PushSettings>();
    private ImageView notify;
    private ImageView Pack_image;
    private LinearLayout sales_good_push;
    private ImageView is_bag_pack;
    private ImageView no_bag_pack;
    private List<GoodSalesType> goodSalesTypes = new ArrayList<GoodSalesType>();
    private TextView distance;
    private String distance_Id;
    private TextView hot, comment, share, order, jifen;
    private RelativeLayout share_app;
    private ShareUtils shareUtils = null;
    private ImageView jifen_image;
    private LinearLayout jifen_push;
    private ImageView share_pack, comment_pack, buy_pack;
    private boolean share_open, comment_open, buy_open;
    private TextView message_number;
    private MainActivity mActivity;
    private ImageView push_dot;
    private RelativeLayout business_item;
    private ImageView business_images;
    private LinearLayout business_distance;
    private RelativeLayout buxian;
    private RelativeLayout wubai;
    private RelativeLayout yiqian;
    private RelativeLayout sanqian;
    private RelativeLayout more;
    private ImageView buxian_image;
    private ImageView wubai_image;
    private ImageView yiqian_image;
    private ImageView sanqian_iamge;
    private ImageView more_image;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                message_number.setText(msg.obj + "");
            }
        }
    };

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_side_fragment, null);
        head = (RoundImageView) view.findViewById(R.id.user_head);
        name = (TextView) view.findViewById(R.id.name);
        search = (RelativeLayout) view.findViewById(R.id.search);
        collect = (RelativeLayout) view.findViewById(R.id.collect);
        message_center = (RelativeLayout) view.findViewById(R.id.message_center);
        notify = (ImageView) view.findViewById(R.id.notify);
        Pack_image = (ImageView) view.findViewById(R.id.pack_image);
        sales_good_push = (LinearLayout) view.findViewById(R.id.sales_good_push);
        is_bag_pack = (ImageView) view.findViewById(R.id.is_bag_pack);
        no_bag_pack = (ImageView) view.findViewById(R.id.no_bag_pack);
        distance = (TextView) view.findViewById(R.id.distance);
        hot = (TextView) view.findViewById(R.id.hot);
        comment = (TextView) view.findViewById(R.id.comment);
        share = (TextView) view.findViewById(R.id.share);
        share_app = (RelativeLayout) view.findViewById(R.id.share_app);
        point_manager = (RelativeLayout) view.findViewById(R.id.jifen_manager);
        order_center = (RelativeLayout) view.findViewById(R.id.order_center);
        order = (TextView) view.findViewById(R.id.order);
        jifen = (TextView) view.findViewById(R.id.jifen);
        jifen_image = (ImageView) view.findViewById(R.id.jifen_image);
        jifen_push = (LinearLayout) view.findViewById(R.id.jifen_push);
        share_pack = (ImageView) view.findViewById(R.id.share_pack);
        comment_pack = (ImageView) view.findViewById(R.id.comment_pack);
        buy_pack = (ImageView) view.findViewById(R.id.buy_pack);
        message_number = (TextView) view.findViewById(R.id.message_number);
        push_dot = (ImageView) view.findViewById(R.id.push_dot);
        business_item= (RelativeLayout) view.findViewById(R.id.business_distance);
        business_images= (ImageView) view.findViewById(R.id.business_image);
        business_distance= (LinearLayout) view.findViewById(R.id.distance_item);
        buxian= (RelativeLayout) view.findViewById(R.id.buxian);
        yiqian= (RelativeLayout) view.findViewById(R.id.yiqian);
        wubai= (RelativeLayout) view.findViewById(R.id.wubai);
        sanqian= (RelativeLayout) view.findViewById(R.id.sanqian);
        more= (RelativeLayout) view.findViewById(R.id.more);
        buxian_image= (ImageView) view.findViewById(R.id.buxian_image);
        wubai_image= (ImageView) view.findViewById(R.id.wubai_image);
        yiqian_image= (ImageView) view.findViewById(R.id.yiqian_image);
        sanqian_iamge= (ImageView) view.findViewById(R.id.sanqian_image);
        more_image= (ImageView) view.findViewById(R.id.more_image);
        buxian.setOnClickListener(this);
        yiqian.setOnClickListener(this);
        wubai.setOnClickListener(this);
        sanqian.setOnClickListener(this);
        more.setOnClickListener(this);
        business_item.setOnClickListener(this);
        share_pack.setOnClickListener(this);
        comment_pack.setOnClickListener(this);
        buy_pack.setOnClickListener(this);
        jifen_image.setOnClickListener(this);
        point_manager.setOnClickListener(this);
        order_center.setOnClickListener(this);
        share_app.setOnClickListener(this);
        distance.setOnClickListener(this);
        is_bag_pack.setOnClickListener(this);
        no_bag_pack.setOnClickListener(this);
        Pack_image.setOnClickListener(this);
        notify.setOnClickListener(this);
        collect.setOnClickListener(this);
        search.setOnClickListener(this);
        EventCenter.getInstance().addObserver(this);
        hot_sales = (RelativeLayout) view.findViewById(R.id.hot_sales);
        setting = (ImageView) view.findViewById(R.id.setting);
        setting.setOnClickListener(this);
        hot_sales.setOnClickListener(this);
        message_center.setOnClickListener(this);
        head.setOnClickListener(this);
        if (null != MyUserInfoUtils.getInstance().myUserInfo) {
            MyImageLoader.displayDefaultImage(URLText.img_url + MyUserInfoUtils.getInstance().myUserInfo.PhotoPath, head);
            name.setText(MyUserInfoUtils.getInstance().myUserInfo.ShowName);
        }
        is_bag_list = (ScrollViewWithListView) view.findViewById(R.id.is_bag_list);
        no_bag_list = (ScrollViewWithListView) view.findViewById(R.id.no_bag_list);
        myAdpter1 = new MyAdpter1();
        myAdpter2 = new MyAdpter2();
        is_bag_list.setAdapter(myAdpter1);
        no_bag_list.setAdapter(myAdpter2);
        is_bag_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < isbag_good_one_classify.size(); i++) {
                    if (i == position) {
                        if (isbag_good_one_classify.get(position).isSelect) {
                            isbag_good_one_classify.get(position).isSelect = false;
                        } else {
                            isbag_good_one_classify.get(position).isSelect = true;
                        }
                        break;
                    }
                }
                myAdpter1.notifyDataSetChanged();
                getPushList();
            }
        });
        no_bag_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < nobag_good_one_classify.size(); i++) {
                    if (i == position) {
                        if (nobag_good_one_classify.get(position).isSelect) {
                            nobag_good_one_classify.get(position).isSelect = false;
                        } else {
                            nobag_good_one_classify.get(position).isSelect = true;
                        }
                        break;
                    }
                }
                getPushList();
                myAdpter2.notifyDataSetChanged();
            }
        });
        queryDicNode();
        //registerEventListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        menu_number();
        message_number.setText(PreferencesUtils.getInt(getActivity(), MyUserInfoUtils.getInstance().myUserInfo.NickName, 0) + "");
        if (null != MyUserInfoUtils.getInstance() && null != MyUserInfoUtils.getInstance().myUserInfo) {
            if (null != name && null != head) {
                if (null != MyUserInfoUtils.getInstance().myUserInfo) {
                    MyImageLoader.displayDefaultImage(URLText.img_url + MyUserInfoUtils.getInstance().myUserInfo.PhotoPath, head);
                    name.setText(MyUserInfoUtils.getInstance().myUserInfo.ShowName);
                }
            }
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        mActivity.setHandler(handler);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buxian:
                distance_Id = "0";
                getPushList();
                buxian_image.setBackgroundResource(R.mipmap.select);
                wubai_image.setBackgroundResource(R.mipmap.noselect);
                yiqian_image.setBackgroundResource(R.mipmap.noselect);
                sanqian_iamge.setBackgroundResource(R.mipmap.noselect);
                more_image.setBackgroundResource(R.mipmap.noselect);
                break;
            case R.id.wubai:
                distance_Id ="1";
                getPushList();
                buxian_image.setBackgroundResource(R.mipmap.noselect);
                wubai_image.setBackgroundResource(R.mipmap.select);
                yiqian_image.setBackgroundResource(R.mipmap.noselect);
                sanqian_iamge.setBackgroundResource(R.mipmap.noselect);
                more_image.setBackgroundResource(R.mipmap.noselect);
                break;
            case R.id.yiqian:
                distance_Id = "2";
                getPushList();
                buxian_image.setBackgroundResource(R.mipmap.noselect);
                wubai_image.setBackgroundResource(R.mipmap.noselect);
                yiqian_image.setBackgroundResource(R.mipmap.select);
                sanqian_iamge.setBackgroundResource(R.mipmap.noselect);
                more_image.setBackgroundResource(R.mipmap.noselect);
                break;
            case R.id.sanqian:
                distance_Id = "3";
                getPushList();
                buxian_image.setBackgroundResource(R.mipmap.noselect);
                wubai_image.setBackgroundResource(R.mipmap.noselect);
                yiqian_image.setBackgroundResource(R.mipmap.noselect);
                sanqian_iamge.setBackgroundResource(R.mipmap.select);
                more_image.setBackgroundResource(R.mipmap.noselect);
                break;
            case R.id.more:
                distance_Id = "4";
                getPushList();
                buxian_image.setBackgroundResource(R.mipmap.noselect);
                wubai_image.setBackgroundResource(R.mipmap.noselect);
                yiqian_image.setBackgroundResource(R.mipmap.noselect);
                sanqian_iamge.setBackgroundResource(R.mipmap.noselect);
                more_image.setBackgroundResource(R.mipmap.select);
                break;
            case R.id.business_distance:
                if(business_distance.getVisibility()==View.VISIBLE){
                    business_distance.setVisibility(View.GONE);
                    business_images.setBackgroundResource(R.mipmap.search_up3);
                }
                else {
                    business_distance.setVisibility(View.VISIBLE);
                    business_images.setBackgroundResource(R.mipmap.search_dowm3);
                }
                break;
            case R.id.share_app:
                ShareData data = new ShareData();
                data.setTitleUrl("http://login.savingsmore.com/Home/Download");
                data.setUrl("http://login.savingsmore.com/Home/Download");
                data.setTitle("省又省-促销专卖APP");
                data.setImagePath(FileSystem.getCachesDir(getActivity(), true).getAbsolutePath() + File.separator + "icon.jpg");
                data.setText("实体商铺在促销、在活动、在甩卖!省又省购物省钱、省心、省时间!");
                shareUtils = new ShareUtils(data, getActivity());
                shareUtils.show(share_app);
                break;
            case R.id.distance:
//                PopupWindowFromBottomUtil.shouSalesType(getActivity(), LayoutInflater.from(getActivity()).inflate(R.layout.business_my_center_activity, null), goodSalesTypes, new PopupWindowFromBottomUtil.Listenrt3() {
//                    @Override
//                    public void callback(GoodSalesType goodSalesType, PopupWindow popupWindow) {
//                        distance.setText(goodSalesType.Value);
//                        distance_Id = goodSalesType.Id;
//                        getPushList();
//                        popupWindow.dismiss();
//                    }
//                });
//                break;
            case R.id.share_pack:
                if (share_open) {
                    share_open = false;
                    share_pack.setBackgroundResource(R.mipmap.is_bag_pack);
                } else {
                    share_open = true;
                    share_pack.setBackgroundResource(R.mipmap.is_bag_pack_red);
                }
                getPushList();
                break;
            case R.id.comment_pack:
                if (comment_open) {
                    comment_open = false;
                    comment_pack.setBackgroundResource(R.mipmap.is_bag_pack);
                } else {
                    comment_open = true;
                    comment_pack.setBackgroundResource(R.mipmap.is_bag_pack_red);
                }
                getPushList();
                break;
            case R.id.buy_pack:
                if (buy_open) {
                    buy_open = false;
                    buy_pack.setBackgroundResource(R.mipmap.is_bag_pack);
                } else {
                    buy_open = true;
                    buy_pack.setBackgroundResource(R.mipmap.is_bag_pack_red);
                }
                getPushList();
                break;
            case R.id.is_bag_pack:
                if (is_bag_list.getVisibility() == View.VISIBLE) {
                    is_bag_list.setVisibility(View.GONE);
                    is_bag_pack.setBackgroundResource(R.mipmap.is_bag_pack);
                    for (int i = 0; i < isbag_good_one_classify.size(); i++) {
                        isbag_good_one_classify.get(i).isSelect = false;
                    }
                    is_bag_list.setAdapter(myAdpter1);
                    getPushList();
                } else {
                    is_bag_list.setVisibility(View.VISIBLE);
                    is_bag_pack.setBackgroundResource(R.mipmap.is_bag_pack_red);
                    for (int i = 0; i < isbag_good_one_classify.size(); i++) {
                        isbag_good_one_classify.get(i).isSelect = true;
                    }
                    is_bag_list.setAdapter(myAdpter1);
                    getPushList();
                }
                break;
            case R.id.jifen_image:
                if (jifen_push.getVisibility() == View.GONE) {
                    jifen_push.setVisibility(View.VISIBLE);
                    jifen_image.setBackgroundResource(R.mipmap.search_dowm3);
                } else {
                    jifen_push.setVisibility(View.GONE);
                    jifen_image.setBackgroundResource(R.mipmap.search_up3);
                }
                break;
            case R.id.no_bag_pack:
                if (no_bag_list.getVisibility() == View.VISIBLE) {
                    no_bag_list.setVisibility(View.GONE);
                    no_bag_pack.setBackgroundResource(R.mipmap.is_bag_pack);
                    for (int i = 0; i < nobag_good_one_classify.size(); i++) {
                        nobag_good_one_classify.get(i).isSelect = false;
                    }
                    no_bag_list.setAdapter(myAdpter2);
                    getPushList();
                } else {
                    no_bag_list.setVisibility(View.VISIBLE);
                    no_bag_pack.setBackgroundResource(R.mipmap.is_bag_pack_red);
                    for (int i = 0; i < nobag_good_one_classify.size(); i++) {
                        nobag_good_one_classify.get(i).isSelect = true;
                    }
                    no_bag_list.setAdapter(myAdpter2);
                    getPushList();
                }
                break;
            case R.id.pack_image:
                if (sales_good_push.getVisibility() == View.VISIBLE) {
                    Pack_image.setBackgroundResource(R.mipmap.search_up3);
                    sales_good_push.setVisibility(View.GONE);
                } else {
                    Pack_image.setBackgroundResource(R.mipmap.search_dowm3);
                    sales_good_push.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.notify:
                Intent intent7 = new Intent(getActivity(), PushMessageActivity.class);
                startActivity(intent7);
                push_dot.setVisibility(View.GONE);
                break;
            case R.id.setting:
                Intent intent0 = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent0);
                break;
            case R.id.hot_sales:
                Intent intent = new Intent(getActivity(), HotSalesGoods.class);
                intent.putExtra("title", "最热门促销");
                startActivity(intent);
                break;
            case R.id.search:
                Intent intent1 = new Intent(getActivity(), SearchActivity.class);
                intent1.putExtra("title", "促销商品搜索");
                startActivity(intent1);
                break;
            case R.id.collect:
                Intent intent2 = new Intent(getActivity(), CollectCenterActivity.class);
                startActivity(intent2);
                break;
            case R.id.user_head:
                Intent intent3 = new Intent(getActivity(), PersonalMyMessageActivity.class);
                startActivity(intent3);
                break;
            case R.id.message_center:
                Intent intent6 = new Intent(getActivity(), MessageCenterActivity.class);
                intent6.putExtra("list", result);
                startActivity(intent6);
                break;
            case R.id.jifen_manager:
                Intent intent4 = new Intent(getActivity(), PointManagerActivity.class);
                startActivity(intent4);
                break;
            case R.id.order_center:
                Intent intent5 = new Intent(getActivity(), OrderCenterActivity.class);
                intent5.putExtra("title", "收货中心");
                startActivity(intent5);
                break;
        }
    }

    //查询基础字典queryDicNode()
    private void queryDicNode() {
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebRequestHelper.json_post(getActivity(), URLText.QUERYDICNODE, stringEntity, new MyAsyncHttpResponseHandler(getActivity()) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                ListBaseMessage listBaseMessage = GsonUtils.fromJson(new String(responseBody), ListBaseMessage.class);
                list = listBaseMessage.MainData;
                for (int i = 0; i < list.size(); i++) {
                    if (null != list.get(i).Name && null != list.get(i).ParentId && null != list.get(i).Id && list.get(i).ParentId.equals("ba335639-52c2-4e8d-8d2b-faf8ed097418") && list.get(i).IsBag.equals("true")) {
                        isbag_good_one_classify.add(list.get(i));
                    } else if (null != list.get(i).Name && null != list.get(i).ParentId && null != list.get(i).Id && list.get(i).ParentId.equals("ba335639-52c2-4e8d-8d2b-faf8ed097418") && list.get(i).IsBag.equals("false")) {
                        nobag_good_one_classify.add(list.get(i));
                    }
                }
                if (null != MyUserInfoUtils.getInstance().myUserInfo.PushSettings) {
                    for (int i = 0; i < MyUserInfoUtils.getInstance().myUserInfo.PushSettings.size(); i++) {
                        for (int j = 0; j < isbag_good_one_classify.size(); j++) {
                            if (MyUserInfoUtils.getInstance().myUserInfo.PushSettings.get(i).PushValue.equals(isbag_good_one_classify.get(j).Id)) {
                                isbag_good_one_classify.get(j).isSelect = true;
                                is_bag_pack.setBackgroundResource(R.mipmap.is_bag_pack_red);
                                is_bag_list.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    for (int i = 0; i < MyUserInfoUtils.getInstance().myUserInfo.PushSettings.size(); i++) {
                        for (int j = 0; j < nobag_good_one_classify.size(); j++) {
                            if (MyUserInfoUtils.getInstance().myUserInfo.PushSettings.get(i).PushValue.equals(nobag_good_one_classify.get(j).Id)) {
                                nobag_good_one_classify.get(j).isSelect = true;
                                no_bag_pack.setBackgroundResource(R.mipmap.is_bag_pack_red);
                                no_bag_list.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                }
                queryDicNode2();
                myAdpter2.notifyDataSetChanged();
                myAdpter1.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {
        int message = PreferencesUtils.getInt(getActivity(), MyUserInfoUtils.getInstance().myUserInfo.NickName, 0);
        message = message + 1;
        message_number.setText(message + "");
        LogUtils.instance.d("收到一条消息");
        PreferencesUtils.putInt(getActivity(), MyUserInfoUtils.getInstance().myUserInfo.NickName, message);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (EventCenter.isContainsEnent(EventCenter.ADD_RED_DOT_ON_DISCOVERYTAB)) {
            push_dot.setVisibility(View.VISIBLE);
        }
    }

    public class MyAdpter1 extends BaseAdapter {

        @Override
        public int getCount() {
            return isbag_good_one_classify.size();
        }

        @Override
        public Object getItem(int position) {
            return isbag_good_one_classify.get(position);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.push_select_item, null);
                viewHodel.name = (TextView) convertView.findViewById(R.id.push_item_name);
                viewHodel.iamge = (ImageView) convertView.findViewById(R.id.push_item_image);
                convertView.setTag(viewHodel);
            }
            viewHodel = (ViewHodel) convertView.getTag();
            viewHodel.name.setText(isbag_good_one_classify.get(position).Name);
            if (isbag_good_one_classify.get(position).isSelect) {
                viewHodel.iamge.setImageResource(R.mipmap.select);
            } else {
                viewHodel.iamge.setImageResource(R.mipmap.noselect);
            }
            return convertView;
        }
    }

    public class MyAdpter2 extends BaseAdapter {

        @Override
        public int getCount() {
            return nobag_good_one_classify.size();
        }

        @Override
        public Object getItem(int position) {
            return nobag_good_one_classify.get(position);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.push_select_item, null);
                viewHodel.name = (TextView) convertView.findViewById(R.id.push_item_name);
                viewHodel.iamge = (ImageView) convertView.findViewById(R.id.push_item_image);
                convertView.setTag(viewHodel);
            }
            viewHodel = (ViewHodel) convertView.getTag();
            viewHodel.name.setText(nobag_good_one_classify.get(position).Name);
            if (nobag_good_one_classify.get(position).isSelect) {
                viewHodel.iamge.setImageResource(R.mipmap.select);
            } else {
                viewHodel.iamge.setImageResource(R.mipmap.noselect);
            }
            return convertView;
        }
    }

    public class ViewHodel {
        TextView name;
        ImageView iamge;
    }

    private void setPush(boolean IsPush, List<PushSettings> list) {
        WebRequestHelper.json_post(getActivity(), URLText.SET_PUSH, RequestParamsPool.savePush(IsPush, list), new MyAsyncHttpResponseHandler(getActivity()) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                JSONObject object = new JSONObject();
                //Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPushList() {
        push_list.clear();
        for (int i = 0; i < isbag_good_one_classify.size(); i++) {
            if (isbag_good_one_classify.get(i).isSelect) {
                PushSettings pushSettings = new PushSettings();
                pushSettings.PushType = "1";
                pushSettings.PushValue = isbag_good_one_classify.get(i).Id;
                push_list.add(pushSettings);
            }
        }
        for (int i = 0; i < nobag_good_one_classify.size(); i++) {
            if (nobag_good_one_classify.get(i).isSelect) {
                PushSettings pushSettings = new PushSettings();
                pushSettings.PushType = "1";
                pushSettings.PushValue = nobag_good_one_classify.get(i).Id;
                push_list.add(pushSettings);
            }
        }
        if (null != distance_Id) {
            PushSettings pushSettings = new PushSettings();
            pushSettings.PushType = "2";
            pushSettings.PushValue = distance_Id;
            push_list.add(pushSettings);
        }
        if (share_open) {
            PushSettings pushSettings = new PushSettings();
            pushSettings.PushType = "4";
            pushSettings.PushValue = "true";
            push_list.add(pushSettings);
        }
        if (comment_open) {
            PushSettings pushSettings = new PushSettings();
            pushSettings.PushType = "5";
            pushSettings.PushValue = "true";
            push_list.add(pushSettings);
        }
        if (buy_open) {
            PushSettings pushSettings = new PushSettings();
            pushSettings.PushType = "6";
            pushSettings.PushValue = "true";
            push_list.add(pushSettings);
        }
        setPush(true, push_list);
    }

    //查询促销形式
    private void queryDicNode2() {
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebRequestHelper.json_post(getActivity(), URLText.QUERYDICNODE3, stringEntity, new MyAsyncHttpResponseHandler(getActivity()) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                GoodSalesTypeList goodSalesTypeList = GsonUtils.fromJson(new String(responseBody), GoodSalesTypeList.class);
                goodSalesTypes = goodSalesTypeList.ShowData;
                for (int i = 0; i < MyUserInfoUtils.getInstance().myUserInfo.PushSettings.size(); i++) {
                    for (int j = 0; j < goodSalesTypes.size(); j++) {
                        if (MyUserInfoUtils.getInstance().myUserInfo.PushSettings.get(i).PushValue.equals(goodSalesTypes.get(j).Id)) {
                            distance_Id = goodSalesTypes.get(j).Id;
                            if(distance_Id.equals("0")){
                                buxian_image.setBackgroundResource(R.mipmap.select);
                                wubai_image.setBackgroundResource(R.mipmap.noselect);
                                yiqian_image.setBackgroundResource(R.mipmap.noselect);
                                sanqian_iamge.setBackgroundResource(R.mipmap.noselect);
                                more_image.setBackgroundResource(R.mipmap.noselect);
                            }
                            if(distance_Id.equals("1")){
                                buxian_image.setBackgroundResource(R.mipmap.noselect);
                                wubai_image.setBackgroundResource(R.mipmap.select);
                                yiqian_image.setBackgroundResource(R.mipmap.noselect);
                                sanqian_iamge.setBackgroundResource(R.mipmap.noselect);
                                more_image.setBackgroundResource(R.mipmap.noselect);
                            }
                            if(distance_Id.equals("2")){
                                buxian_image.setBackgroundResource(R.mipmap.noselect);
                                wubai_image.setBackgroundResource(R.mipmap.noselect);
                                yiqian_image.setBackgroundResource(R.mipmap.select);
                                sanqian_iamge.setBackgroundResource(R.mipmap.noselect);
                                more_image.setBackgroundResource(R.mipmap.noselect);
                            }
                            if(distance_Id.equals("3")){
                                buxian_image.setBackgroundResource(R.mipmap.noselect);
                                wubai_image.setBackgroundResource(R.mipmap.noselect);
                                yiqian_image.setBackgroundResource(R.mipmap.noselect);
                                sanqian_iamge.setBackgroundResource(R.mipmap.select);
                                more_image.setBackgroundResource(R.mipmap.noselect);
                            }
                            if(distance_Id.equals("4")){
                                buxian_image.setBackgroundResource(R.mipmap.noselect);
                                wubai_image.setBackgroundResource(R.mipmap.noselect);
                                yiqian_image.setBackgroundResource(R.mipmap.noselect);
                                sanqian_iamge.setBackgroundResource(R.mipmap.noselect);
                                more_image.setBackgroundResource(R.mipmap.select);
                            }
                        }
                    }
                    if (MyUserInfoUtils.getInstance().myUserInfo.PushSettings.get(i).PushType.equals("4")) {
                        share_open = true;
                        share_pack.setBackgroundResource(R.mipmap.is_bag_pack_red);
                    }
                    if (MyUserInfoUtils.getInstance().myUserInfo.PushSettings.get(i).PushType.equals("5")) {
                        comment_open = true;
                        comment_pack.setBackgroundResource(R.mipmap.is_bag_pack_red);
                    }
                    if (MyUserInfoUtils.getInstance().myUserInfo.PushSettings.get(i).PushType.equals("6")) {
                        buy_open = true;
                        buy_pack.setBackgroundResource(R.mipmap.is_bag_pack_red);
                    }
                }

            }
        });
    }

    //侧栏统计
    private void menu_number() {
        WebRequestHelper.json_post(getActivity(), URLText.MENU_NUMBER, RequestParamsPool.getMenuNumber(), new MyAsyncHttpResponseHandler(getActivity()) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject object = null;
                try {
                    object = new JSONObject(new String(responseBody));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MenuNumber menuNumber = GsonUtils.fromJson(object.optString("MainData"), MenuNumber.class);
                if (null != menuNumber) {
                    order.setText(menuNumber.Receipting);
                    hot.setText(menuNumber.HotProduct);
                    comment.setText(menuNumber.Favourite);
                    share.setText(menuNumber.Shared);
                    jifen.setText(menuNumber.Point);
                }
            }
        });
    }

    private void registerEventListener() {
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});


    }


}
