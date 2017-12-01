package com.ximai.savingsmore.save.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.fragment.FinishSalesGoodFragment;
import com.ximai.savingsmore.save.fragment.SalesGoodFrament;

/**
 * Created by caojian on 17/1/9.
 */
//收藏汇总
public class CollectionAllAcitvity extends BaseActivity implements View.OnClickListener {
    private TextView isBag, noBag;
    private View introduce, sales_good;
    private FragmentManager fragmentManager;
    private SalesGoodFrament isBagGoodFragment;
    private FinishSalesGoodFragment noBagGoodFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collecttion_activity);
        setLeftBackMenuVisibility(CollectionAllAcitvity.this, "");
        setCenterTitle("收藏汇总");
        introduce = findViewById(R.id.introduce);
        sales_good = findViewById(R.id.cuxiao_goog);
        isBag = (TextView) findViewById(R.id.is_bag);
        noBag = (TextView) findViewById(R.id.no_bag);
        isBag.setOnClickListener(this);
        noBag.setOnClickListener(this);
        isBagGoodFragment = new SalesGoodFrament();
        noBagGoodFragment = new FinishSalesGoodFragment();
        Bundle bundle = new Bundle();
        bundle.putString("collect", "true");
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
