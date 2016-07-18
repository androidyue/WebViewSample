package com.droidyue.webviewsample;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = parseIntent(url);
                if (intent != null) {
                    startActivity(intent);
                    return true;
                }
                if (url.startsWith("market://")) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        webView.addJavascriptInterface(new JsInteration(), "controller");
        webView.loadUrl("http://1.toolite.sinaapp.com/test_dir/webview_test.html");
    }

    class JsInteration {
        @JavascriptInterface
        public boolean checkAppInstalled(String packageName) {
            PackageManager pkgManager = getPackageManager();
            boolean appInstalled = false;
            try {
                pkgManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                appInstalled = true;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return appInstalled;
        }
    }

    public static Intent parseIntent(String url) {
        Intent intent = null;
        // Parse intent URI into Intent Object
        int flags = 0;
        boolean isIntentUri = false;
        if (url.startsWith("intent:")) {
            isIntentUri = true;
            flags = Intent.URI_INTENT_SCHEME;
        } else if (url.startsWith("#Intent;")) {
            isIntentUri = true;
        }
        if (isIntentUri) {
            try {
                intent = Intent.parseUri(url, flags);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return intent;
    }

}
