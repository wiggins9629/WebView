package com.wiggins.webview;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MainActivity mActivity = null;
    private SafeWebView mWebView;
    private LinearLayout mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;

        initView();
    }

    private void initView() {
        mRoot = (LinearLayout) findViewById(R.id.llMain);
        mWebView = (SafeWebView) findViewById(R.id.webView);
        // Android 4.2关闭辅助功能
        mWebView.disableAccessibility(getApplicationContext());

        initWebSettings();
        initListener();
        initInterface();
        mWebView.loadUrl("file:///android_asset/test.html");
    }

    private void initWebSettings() {
        WebSettings webSettings = mWebView.getSettings();
        if (webSettings == null) {
            return;
        }
        // 设置字体缩放倍数，默认100
        webSettings.setTextZoom(100);
        // 支持Js使用
        webSettings.setJavaScriptEnabled(true);
        // 开启DOM缓存
        webSettings.setDomStorageEnabled(true);
        // 开启数据库缓存
        webSettings.setDatabaseEnabled(true);
        // 支持自动加载图片
        webSettings.setLoadsImagesAutomatically(hasKitkat());
        // 设置WebView缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启AppCache功能
        webSettings.setAppCacheEnabled(true);
        // 设置AppCache最大缓存值(现在官方已经不提倡使用，已废弃)
        webSettings.setAppCacheMaxSize(8 * 1024 * 1024);
        // 私有缓存存储，如果不调用setAppCachePath方法，WebView将不会产生这个目录
        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());
        // 数据库路径
        if (!hasKitkat()) {
            webSettings.setDatabasePath(getDatabasePath("html").getPath());
        }
        // 关闭密码保存提醒功能
        webSettings.setSavePassword(false);
        // 支持缩放
        webSettings.setSupportZoom(true);
        // 设置UserAgent属性
        webSettings.setUserAgentString("");
        // 允许加载本地html文件
        webSettings.setAllowFileAccess(true);
        // 允许通过file url加载的JavaScript读取其他的本地文件，Android 4.1之前默认是true，在 Android 4.1及以后默认是false，也就是禁止
        webSettings.setAllowFileAccessFromFileURLs(false);
        // 允许通过file url加载的JavaScript可以访问其他的源，包括其他的文件和http、https等其他的源
        // Android 4.1之前默认是true，在 Android 4.1及以后默认是false，也就是禁止
        // 如果此设置是允许，则setAllowFileAccessFromFileURLs不起做用
        webSettings.setAllowUniversalAccessFromFileURLs(false);
    }

    private void initListener() {
        mWebView.setWebViewClient(new SafeWebViewClient());
        mWebView.setWebChromeClient(new SafeWebChromeClient());
    }

    private void initInterface() {
        mWebView.addJavascriptInterface(new NativeInterface(this), "AndroidNative");
    }

    public class SafeWebViewClient extends WebViewClient {

        /**
         * 是否在WebView内加载页面
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        /**
         * WebView开始加载页面时回调，一次Frame加载对应一次回调
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        /**
         * WebView完成加载页面时回调，一次Frame加载对应一次回调
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        /**
         * 当WebView的页面Scale值发生改变时回调
         */
        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
        }

        /**
         * WebView加载页面资源时回调，每个资源产生一次网络加载，除非本地有当前Url对应的缓存，否则就会加载
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        /**
         * WebView可以拦截某一次的request来返回我们自己加载的数据，这个方法在后面缓存会有很大作用
         *
         * @param view    WebView
         * @param request 当前产生request请求
         */
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        /**
         * WebView访问Url出错
         */
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        /**
         * WebView SSL访问证书出错
         */
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();      // 表示等待证书响应
            // handler.cancel();    // 表示挂起连接，取消加载，为默认方式
            // handler.handleMessage(null);// 可做其他处理
        }
    }

    public class SafeWebChromeClient extends WebChromeClient {

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return super.onConsoleMessage(consoleMessage);
        }

        /**
         * 当前WebView加载网页进度
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        /**
         * 接收Web页面的Icon
         */
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        /**
         * 接收Web页面的Title
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        /**
         * Js中调用alert()函数产生的对话框
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            // 创建一个Builder来显示网页中的对话框
            new AlertDialog.Builder(mActivity)
                    .setTitle("Alert对话框")
                    .setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setCancelable(false)
                    .show();
            return true;
        }

        /**
         * 处理Js中的Confirm对话框
         */
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("Confirm对话框")
                    .setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();
            return true;
        }

        /**
         * 处理Js中的Prompt对话框
         */
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            // 获得LayoutInflater对象加载指定布局
            final LayoutInflater inflater = LayoutInflater.from(mActivity);
            final View mView = inflater.inflate(R.layout.prompt_view, null);

            // 设置TextView对应网页中的提示信息，设置EditText对应网页中的默认文字
            TextView text = mView.findViewById(R.id.text);
            final EditText edit = mView.findViewById(R.id.edit);
            text.setText(message);
            edit.setHint(defaultValue);

            // 定义对话框上的确定按钮
            new AlertDialog.Builder(mActivity)
                    .setTitle("Prompt对话框")
                    .setView(mView)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 单击确定后获取输入值传给网页进行处理
                            String value = edit.getText().toString();
                            result.confirm(value);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .show();
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == event.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRoot != null) {
            mRoot.removeView(mWebView);
        }
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.clearMatches();
            mWebView.clearHistory();
            mWebView.clearSslPreferences();
            mWebView.clearCache(true);
            mWebView.loadUrl("about:blank");
            mWebView.removeAllViews();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mWebView.removeJavascriptInterface("AndroidNative");
            }
            mWebView.destroy();
        }
        mWebView = null;
    }

    private static boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= 19;
    }
}
