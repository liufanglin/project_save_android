package com.ximai.savingsmore.save.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.save.common.BaseActivity;

/**
 * Created by caojian on 17/1/12.
 */
public class WebViewActivity extends BaseActivity {
    private WebView webView;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        setCenterTitle(getIntent().getStringExtra("title"));
        type = getIntent().getStringExtra("type");
        webView = (WebView) findViewById(R.id.webview);
        setLeftBackMenuVisibility(WebViewActivity.this, "");
//        setWebViewLeftBackMenuVisibility(title, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (webView.canGoBack()) {
//                    webView.goBack();
//                } else {
//                    finish();
//                }
//            }
//        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

            }

        });
        WebSettings ws = webView.getSettings();
        webView.setHorizontalScrollBarEnabled(false);
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(false);
        ws.setUseWideViewPort(false);
        ws.setJavaScriptEnabled(true);
        ws.setUseWideViewPort(true);
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        if (type.equals("2")) {
            webView.loadUrl("http://www.salespk.com/User_Agreement.aspx");
        } else if (type.equals("1")) {
            webView.loadUrl("http://www.salespk.com/Personal_Agreement.aspx");
        }
    }
}
