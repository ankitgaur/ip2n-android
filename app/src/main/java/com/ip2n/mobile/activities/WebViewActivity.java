package com.ip2n.mobile.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ip2n.mobile.R;


public class WebViewActivity extends Activity {
    private String webUrl;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webUrl = getIntent().getStringExtra("WEBVIEW_URL");
        init();
    }

    private void init() {
        webView = (WebView) findViewById(R.id.entertainment_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

        webView.loadUrl(webUrl);


    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;

        }
    }
}
