package com.ximai.savingsmore.save.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.FileSystem.FileSystem;
import com.ximai.savingsmore.library.cache.MyImageLoader;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.library.toolbox.LogUtils;
import com.ximai.savingsmore.library.toolbox.PreferencesUtils;
import com.ximai.savingsmore.library.view.RoundImageView;
import com.ximai.savingsmore.save.activity.BrowseActivity;
import com.ximai.savingsmore.save.activity.BusinessMyCenterActivity;
import com.ximai.savingsmore.save.activity.CollectionAllAcitvity;
import com.ximai.savingsmore.save.activity.FourStepRegisterActivity;
import com.ximai.savingsmore.save.activity.HotSalesGoods;
import com.ximai.savingsmore.save.activity.IssuGoodActivity;
import com.ximai.savingsmore.save.activity.MainActivity;
import com.ximai.savingsmore.save.activity.MessageCenterActivity;
import com.ximai.savingsmore.save.activity.MyCommentCenterActivity;
import com.ximai.savingsmore.save.activity.OrderCenterActivity;
import com.ximai.savingsmore.save.activity.SearchActivity;
import com.ximai.savingsmore.save.activity.SettingActivity;
import com.ximai.savingsmore.save.modle.IMUser;
import com.ximai.savingsmore.save.modle.IMUserList;
import com.ximai.savingsmore.save.modle.LoginUser;
import com.ximai.savingsmore.save.modle.MenuNumber;
import com.ximai.savingsmore.save.modle.MyUserInfoUtils;
import com.ximai.savingsmore.save.modle.ShareData;
import com.ximai.savingsmore.save.utils.ShareUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by caojian on 16/11/25.
 */
public class BusinessFragment extends Fragment implements View.OnClickListener {
    private RoundImageView head;
    private TextView name;
    private RelativeLayout hot_sales;
    private RelativeLayout search;
    private RelativeLayout myCenter;
    private RelativeLayout fabu;
    private RelativeLayout comment_center;
    private ImageView setting;
    private RelativeLayout message_center;
    //private List<IMUser> imUsers=new ArrayList<IMUser>();
    private String result;
    private TextView hot, product, comment, share, order, liulan_number, collect_number;
    private ShareUtils shareUtils = null;
    private RelativeLayout share_app;
    private RelativeLayout orderCeter;
    private RelativeLayout liulan;
    private RelativeLayout collect;
    private TextView message_number;
    private MainActivity mActivity;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                message_number.setText(msg.obj + "");
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.business_side_fragment, null);
        head = (RoundImageView) view.findViewById(R.id.user_head);
        name = (TextView) view.findViewById(R.id.name);
        hot_sales = (RelativeLayout) view.findViewById(R.id.hot_sales);
        order = (TextView) view.findViewById(R.id.order_number);
        hot_sales.setOnClickListener(this);
        search = (RelativeLayout) view.findViewById(R.id.search);
        search.setOnClickListener(this);
        if (null != MyUserInfoUtils.getInstance().myUserInfo) {
            MyImageLoader.displayDefaultImage(URLText.img_url + MyUserInfoUtils.getInstance().myUserInfo.PhotoPath, head);
            name.setText(MyUserInfoUtils.getInstance().myUserInfo.ShowName);
        }
        myCenter = (RelativeLayout) view.findViewById(R.id.my_center);
        comment_center = (RelativeLayout) view.findViewById(R.id.comment_center);
        setting = (ImageView) view.findViewById(R.id.setting);
        message_center = (RelativeLayout) view.findViewById(R.id.message_center);
        message_number = (TextView) view.findViewById(R.id.message_number);
        hot = (TextView) view.findViewById(R.id.hot);
        product = (TextView) view.findViewById(R.id.product);
        comment = (TextView) view.findViewById(R.id.comment);
        share = (TextView) view.findViewById(R.id.shared);
        share_app = (RelativeLayout) view.findViewById(R.id.share_app);
        orderCeter = (RelativeLayout) view.findViewById(R.id.order_center);
        liulan = (RelativeLayout) view.findViewById(R.id.liulan);
        collect = (RelativeLayout) view.findViewById(R.id.collect);
        liulan_number = (TextView) view.findViewById(R.id.liulan_number);
        collect_number = (TextView) view.findViewById(R.id.collect_number);
        orderCeter.setOnClickListener(this);
        liulan.setOnClickListener(this);
        collect.setOnClickListener(this);
        share_app.setOnClickListener(this);
        message_center.setOnClickListener(this);
        setting.setOnClickListener(this);
        head.setOnClickListener(this);
        comment_center.setOnClickListener(this);
        myCenter.setOnClickListener(this);
        fabu = (RelativeLayout) view.findViewById(R.id.fabu_cuxiao);
        fabu.setOnClickListener(this);
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
            case R.id.setting:
                Intent intent0 = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent0);
                break;
            case R.id.hot_sales:
                Intent intent = new Intent(getActivity(), HotSalesGoods.class);
                intent.putExtra("title", "热门促销");
                startActivity(intent);
                break;
            case R.id.search:
                Intent intent1 = new Intent(getActivity(), SearchActivity.class);
                intent1.putExtra("title", "谁在促销");
                startActivity(intent1);
                break;
            case R.id.my_center:
                Intent intent2 = new Intent(getActivity(), BusinessMyCenterActivity.class);
                intent2.putExtra("title", "我的中心");
                startActivity(intent2);
                break;
            case R.id.fabu_cuxiao:
                Intent intent3 = new Intent(getActivity(), IssuGoodActivity.class);
                intent3.putExtra("title", "促销品发布");
                startActivity(intent3);
                break;
            case R.id.comment_center:
                Intent intent4 = new Intent(getActivity(), MyCommentCenterActivity.class);
                intent4.putExtra("title", "评论中心");
                startActivity(intent4);
                break;
            case R.id.user_head:
                Intent intent5 = new Intent(getActivity(), BusinessMyCenterActivity.class);
                intent5.putExtra("title", "我的中心");
                startActivity(intent5);
                break;
            case R.id.message_center:
                Intent intent6 = new Intent(getActivity(), MessageCenterActivity.class);
                intent6.putExtra("list", result);
                startActivity(intent6);
                break;
            case R.id.order_center:
                Intent intent7 = new Intent(getActivity(), OrderCenterActivity.class);
                intent7.putExtra("title", "订单中心");
                startActivity(intent7);
                break;
            case R.id.collect:
                Intent intent8 = new Intent(getActivity(), CollectionAllAcitvity.class);
                startActivity(intent8);
                break;
            case R.id.liulan:
                Intent intent9 = new Intent(getActivity(), BrowseActivity.class);
                startActivity(intent9);
                break;
        }
    }

    //侧栏统计
    private void menu_number() {
        WebRequestHelper.json_post(getActivity(), URLText.MENU_NUMBER, RequestParamsPool.getMenuNumber(), new MyAsyncHttpResponseHandler(getActivity()) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                LogUtils.instance.d("侧栏统计");
                JSONObject object = null;
                try {
                    object = new JSONObject(new String(responseBody));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MenuNumber menuNumber = GsonUtils.fromJson(object.optString("MainData"), MenuNumber.class);
                if (null != menuNumber) {
                    hot.setText(menuNumber.HotProduct);
                    comment.setText(menuNumber.Comment);
                    share.setText(menuNumber.Shared);
                    product.setText(menuNumber.Product);
                    order.setText(menuNumber.Order);
                    liulan_number.setText(menuNumber.Hit);
                    collect_number.setText(menuNumber.Favourite);

                }
            }
        });
    }


}
