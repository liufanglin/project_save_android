package com.ximai.savingsmore.save.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.save.common.BaseActivity;

/**
 * Created by caojian on 16/12/13.
 */
public class CommentSuccessActivity extends BaseActivity {
    private TextView jifen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_success);
        setCenterTitle("评论成功");
        addRightTextMenu("关闭", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        jifen = (TextView) findViewById(R.id.jifen);
        if (null != getIntent() && null != getIntent().getStringExtra("jifen") && getIntent().getStringExtra("jifen").equals("2")) {
            jifen.setText("本次评论获得2分");
        } else {
            jifen.setText("本次评论获得1分");
        }
    }
}
