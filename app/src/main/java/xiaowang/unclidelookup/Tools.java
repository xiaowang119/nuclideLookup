package xiaowang.unclidelookup;

import android.content.Context;
import android.content.res.Resources;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import MyUtil.AdFilterTool;

public class Tools extends AppCompatActivity {
    private WebView tools;
    private int androidVersion = Build.VERSION.SDK_INT;//系统版本
    private int listSize;//历史长度
    private int historyPostion;//历史索引位置

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_explore);

        tools = (WebView)findViewById(R.id.explore_web);
        //设置WebView属性，能够执行Javascript脚本
        tools.getSettings().setJavaScriptEnabled(true);
        //支持DOM API
        tools.getSettings().setDomStorageEnabled(true);
        tools.getSettings().setDefaultTextEncodingName("utf-8");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //设置当一个安全站点企图加载来自一个不安全站点资源时WebView的行为,
            //在这种模式下,WebView将允许一个安全的起源从其他来源加载内容，即使那是不安全的.
            //如果app需要安全性比较高，不应该设置此模式
            //解决app中部分页面非https导致的问题
            tools.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        tools.loadUrl("http://m.ab126.com/");
        tools.setWebViewClient(new WebViewClient() {
            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                resend.sendToTarget();
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                // return false;//可以解决由于重定向导致的webview.goback()无法返回的问题
                if (!AdFilterTool.isAd(Tools.this, url)) {
                    if (androidVersion == 22) {//安卓5.1webview.goback（）无法正常返回。
                        WebBackForwardList list = tools.copyBackForwardList();//webView队列
                        listSize = list.getSize();
                        historyPostion = listSize;
                    }
                    view.loadUrl(url);
                    return true;
                } else {
                    return false;
                }
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();// 接受所有网站的证书
                //super.onReceivedSslError(view, handler, error);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (!AdFilterTool.isAd(Tools.this, request.getUrl().toString())) {
                    return super.shouldInterceptRequest(view, request);
                } else {
                    return new WebResourceResponse(null, null, null);
                    //return  null;
                }
            }
        });

        tools.addJavascriptInterface(new JsObject(tools), "history");//重写了webview中的js方法

    }

    private class JsObject {
        private static final String TAG = "JsObject";
        private WebView JsObjectwebView;

        public JsObject(WebView webView) {
            JsObjectwebView = webView;
        }

        @JavascriptInterface
        public void back() {

            JsObjectwebView.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "goBack:");
                    JsObjectwebView.goBack();

                }
            });
        }
        @JavascriptInterface
        public void forward() {
            Log.i(TAG, "forward:");
            JsObjectwebView.post(new Runnable() {
                @Override
                public void run() {
                    JsObjectwebView.goForward();
                }
            });
        }
        @JavascriptInterface
        public void go(final String i) {
            JsObjectwebView.post(new Runnable() {
                @Override
                public void run() {
                    JsObjectwebView.goBackOrForward(Integer.parseInt(i));
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == keyEvent.KEYCODE_BACK) {//监听返回键，如果可以后退就后退
            if (tools.canGoBack()) {
                if (androidVersion == 22) {//部分android5.1 webview存在问题，需要自己查找历史记录并loadUrl
                    WebBackForwardList list = tools.copyBackForwardList();
                    historyPostion--;
                    String url = list.getItemAtIndex(historyPostion).getUrl();
                    tools.loadUrl(url);
                } else {
                    tools.goBack();
                }

                return true;
            }
        }
        return super.onKeyDown(keyCode, keyEvent);
    }


}
