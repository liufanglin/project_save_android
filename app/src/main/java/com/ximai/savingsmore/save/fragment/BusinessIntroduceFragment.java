package com.ximai.savingsmore.save.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.easemob.easeui.EaseConstant;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.library.toolbox.PopupWindowFromBottomUtil;
import com.ximai.savingsmore.save.activity.ChatActivity;
import com.ximai.savingsmore.save.activity.LeaveMessageActivity;
import com.ximai.savingsmore.save.activity.TakeMeActivity;
import com.ximai.savingsmore.save.modle.BusinessMessage;
import com.ximai.savingsmore.save.modle.GoodDetial;
import com.ximai.savingsmore.save.modle.GoodSalesType;
import com.ximai.savingsmore.save.modle.ServiceList;
import com.ximai.savingsmore.save.modle.User;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/11/26.
 */
//商家介绍
public class BusinessIntroduceFragment extends Fragment implements View.OnClickListener {
    private TextView phone, url, location, date, type, range;
    //private User user;
    //private GoodDetial good;
    private BusinessMessage businessMessage;
    private TextView flow_me;
    private PopupWindow setIconWindow;
    private TextView seiverse;
    private List<GoodSalesType> Serive_list = new ArrayList<GoodSalesType>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.business_introduce, null);
        phone = (TextView) view.findViewById(R.id.phone);
        url = (TextView) view.findViewById(R.id.url);
        location = (TextView) view.findViewById(R.id.adress);
        date = (TextView) view.findViewById(R.id.company_date);
        type = (TextView) view.findViewById(R.id.Goods_type);
        range = (TextView) view.findViewById(R.id.range);
        seiverse = (TextView) view.findViewById(R.id.servise);
        seiverse.setOnClickListener(this);
        businessMessage = (BusinessMessage) getArguments().getSerializable("good");
        flow_me = (TextView) view.findViewById(R.id.flow_me);
        flow_me.setOnClickListener(this);
        if (null != businessMessage) {
            phone.setText(businessMessage.PhoneNumber);
            url.setText(businessMessage.UserExtInfo.WebSite);
            location.setText(businessMessage.Domicile);
            date.setText(businessMessage.ApprovalDateName);
            if (businessMessage.UserExtInfo.IsBag) {
                type.setText("提袋商品");
            } else {
                type.setText("非提袋商品");
            }
            if (null != businessMessage.BusinessScopes.get(0).DictNode.Name) {
                range.setText(businessMessage.BusinessScopes.get(0).DictNode.Name);
            }
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flow_me:
                Intent intent1 = new Intent(getActivity(), TakeMeActivity.class);
                intent1.putExtra("isgood", "false");
                intent1.putExtra("good", businessMessage);
                startActivity(intent1);
                break;
            case R.id.servise:
                showSetIconWindow();
                break;
            case R.id.btnCancel:
                setIconWindow.dismiss();
                break;
            case R.id.btnCamera:
                getServiceList();
//                Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "58366991"));
//                startActivity(call);
                setIconWindow.dismiss();
                break;
            case R.id.btnAlbum:
                Intent leave = new Intent(getActivity(), LeaveMessageActivity.class);
                leave.putExtra("Id", businessMessage.Id);
                startActivity(leave);
                setIconWindow.dismiss();
                break;
        }


    }

    private void showSetIconWindow() {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.view_message_popuwindow, null);
        View parentView = LayoutInflater.from(getActivity()).inflate(R.layout.good_details_activity, null);


        setIconWindow = PopupWindowFromBottomUtil.showWindow(contentView, parentView, getActivity());

        Button btnCancel = (Button) contentView.findViewById(R.id.btnCancel);
        Button btnCamera = (Button) contentView.findViewById(R.id.btnCamera);
        Button btnAlbum = (Button) contentView.findViewById(R.id.btnAlbum);

        btnCancel.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
    }

    //得到客服列表
    private void getServiceList() {
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebRequestHelper.json_post(getActivity(), URLText.SERVICE_LIST, stringEntity, new MyAsyncHttpResponseHandler(getActivity()) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String string = new String(responseBody);
                ServiceList serviceList = GsonUtils.fromJson(string, ServiceList.class);
                Serive_list = serviceList.MainData;
                if (Serive_list.size() > 0 && null != Serive_list) {
                    Intent send = new Intent(getActivity(), ChatActivity.class);
                    send.putExtra(EaseConstant.EXTRA_USER_ID, Serive_list.get(0).Id);
                    startActivity(send);
                }
            }
        });
    }
}
