package com.ximai.savingsmore.save.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.fragment.IsBagGoodFragment;
import com.ximai.savingsmore.save.fragment.NoBagGoodFragment;

/**
 * Created by caojian on 16/11/28.
 */
//热门促销
public class  HotSalesGoods extends BaseActivity  implements View.OnClickListener{
    private TextView isBag,noBag;
    private View introduce,sales_good;
    private FragmentManager fragmentManager;
    private IsBagGoodFragment isBagGoodFragment;
    private NoBagGoodFragment noBagGoodFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hot_sales_goods);
        setLeftBackMenuVisibility(HotSalesGoods.this,"");
        setCenterTitle(getIntent().getStringExtra("title"));
        introduce=findViewById(R.id.introduce);
        sales_good=findViewById(R.id.cuxiao_goog);
        isBag= (TextView) findViewById(R.id.is_bag);
        noBag= (TextView) findViewById(R.id.no_bag);
        isBag.setOnClickListener(this);
        noBag.setOnClickListener(this);
        isBagGoodFragment=new IsBagGoodFragment();
        noBagGoodFragment=new NoBagGoodFragment();
        fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragment,isBagGoodFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment,noBagGoodFragment).commit();
        fragmentManager.beginTransaction().show(isBagGoodFragment).commit();
        fragmentManager.beginTransaction().hide(noBagGoodFragment).commit();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.is_bag:
                introduce.setVisibility(View.VISIBLE);
                sales_good.setVisibility(View.INVISIBLE);
                fragmentManager.beginTransaction().hide(noBagGoodFragment).commit();
                fragmentManager.beginTransaction().show(isBagGoodFragment).commit();
                break;
            case  R.id.no_bag:
                introduce.setVisibility(View.INVISIBLE);
                sales_good.setVisibility(View.VISIBLE);
                fragmentManager.beginTransaction().hide(isBagGoodFragment).commit();
                fragmentManager.beginTransaction().show(noBagGoodFragment).commit();
                break;
        }
    }
}
