package com.ximai.savingsmore.save.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.easemob.chat.EMConversation;
import com.easemob.easeui.EaseConstant;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.easeUI.MyEaseConversationListFragment;

/**
 * Created by caojian on 16/12/16.
 */
public class MessageCenterActivity extends BaseActivity {
    private FragmentManager fragmentManager;
    private MyEaseConversationListFragment myEaseConversationListFragment;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_center_activity);
        setCenterTitle("消息中心");
        setLeftBackMenuVisibility(MessageCenterActivity.this, "");
        result = getIntent().getStringExtra("list");
        fragmentManager = getSupportFragmentManager();
        myEaseConversationListFragment = new MyEaseConversationListFragment();
        Bundle intent=new Bundle();
        intent.putString("list",result);
        myEaseConversationListFragment.setArguments(intent);
        fragmentManager.beginTransaction().add(R.id.message_list, myEaseConversationListFragment).show(myEaseConversationListFragment).commit();
        initListener(myEaseConversationListFragment);
    }

    public void initListener(MyEaseConversationListFragment fragment) {
        fragment.setConversationListItemClickListener(new MyEaseConversationListFragment.EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                startActivity(new Intent(MessageCenterActivity.this, ChatActivity.class)
                        .putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName()));
            }

        });
    }


}
