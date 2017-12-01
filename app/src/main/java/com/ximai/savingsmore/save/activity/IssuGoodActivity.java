package com.ximai.savingsmore.save.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.fragment.FinishSalesGoodFragment;
import com.ximai.savingsmore.save.fragment.IsBagGoodFragment;
import com.ximai.savingsmore.save.fragment.NoBagGoodFragment;
import com.ximai.savingsmore.save.fragment.SalesGoodFrament;
import com.ximai.savingsmore.save.modle.MyUserInfoUtils;

/**
 * Created by caojian on 16/12/9.
 */
public class IssuGoodActivity extends BaseActivity implements View.OnClickListener {
    private TextView isBag, noBag;
    private View introduce, sales_good;
    private FragmentManager fragmentManager;
    private SalesGoodFrament isBagGoodFragment;
    private FinishSalesGoodFragment noBagGoodFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_good_activity);
        setCenterTitle("促销品发布");
        setLeftBackMenuVisibility(IssuGoodActivity.this, "");
        addRightImageMenu(R.mipmap.add_good, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUserInfoUtils.getInstance().myUserInfo.ApprovalState.equals("2") || MyUserInfoUtils.getInstance().myUserInfo.ApprovalState.equals("4")) {
                    Toast.makeText(IssuGoodActivity.this, "您的入驻申请还未听过审核，请联系我们021-58366991。", Toast.LENGTH_SHORT).show();
                } else if (MyUserInfoUtils.getInstance().myUserInfo.ApprovalState.equals("3")) {
                    Intent intent = new Intent(IssuGoodActivity.this, AddGoodsAcitivyt.class);
                    startActivity(intent);
                }
            }
        });
        introduce = findViewById(R.id.introduce);
        sales_good = findViewById(R.id.cuxiao_goog);
        isBag = (TextView) findViewById(R.id.is_bag);
        noBag = (TextView) findViewById(R.id.no_bag);
        isBag.setOnClickListener(this);
        noBag.setOnClickListener(this);
        isBagGoodFragment = new SalesGoodFrament();
        noBagGoodFragment = new FinishSalesGoodFragment();
        Bundle bundle = new Bundle();
        bundle.putString("isComment", "false");
        isBagGoodFragment.setArguments(bundle);
        noBagGoodFragment.setArguments(bundle);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragment, isBagGoodFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment, noBagGoodFragment).commit();
        fragmentManager.beginTransaction().show(isBagGoodFragment).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.is_bag:
                introduce.setVisibility(View.VISIBLE);
                sales_good.setVisibility(View.INVISIBLE);
                fragmentManager.beginTransaction().hide(noBagGoodFragment).commit();
                fragmentManager.beginTransaction().show(isBagGoodFragment).commit();
                break;
            case R.id.no_bag:
                introduce.setVisibility(View.INVISIBLE);
                sales_good.setVisibility(View.VISIBLE);
                fragmentManager.beginTransaction().hide(isBagGoodFragment).commit();
                fragmentManager.beginTransaction().show(noBagGoodFragment).commit();
                break;
        }
    }


}
