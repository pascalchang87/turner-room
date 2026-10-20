package app.turnerroom.tv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** Shared setup for the full-screen gallery WebView used by the app and the screensaver. */
final class GalleryWeb {
    private static final String PREFS = "turner_room";
    private static final String KEY_URL = "gallery_url";

    private GalleryWeb() {}

    static String url(Context c) {
        String saved = prefs(c).getString(KEY_URL, "");
        if (saved != null && !saved.isEmpty()) return saved;
        return BuildConfig.GALLERY_URL;
    }

    static boolean hasUrl(Context c) {
        String u = url(c);
        return u != null && u.startsWith("http");
    }

    static void saveUrl(Context c, String url) {
        prefs(c).edit().putString(KEY_URL, normalise(url)).apply();
    }

    /** Accepts "my-site.vercel.app" and adds https:// and ?show=1. */
    static String normalise(String raw) {
        String u = raw == null ? "" : raw.trim();
        if (u.isEmpty()) return "";
        if (!u.startsWith("http://") && !u.startsWith("https://")) u = "https://" + u;
        if (!u.contains("show=")) u += (u.contains("?") ? "&" : "?") + "show=1";
        return u;
    }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    static WebView create(Context c) {
        final WebView web = new WebView(c);
        web.setBackgroundColor(Color.BLACK);
        web.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        web.setFocusable(true);
        web.setFocusableInTouchMode(true);
        web.setVerticalScrollBarEnabled(false);
        web.setHorizontalScrollBarEnabled(false);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        // The page switches into wall mode when it sees this marker.
        s.setUserAgentString(s.getUserAgentString() + " TurnerRoomTV/1.0");

        final Handler retry = new Handler(Looper.getMainLooper());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false; // stay inside the gallery
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    view.loadData("<html><body style='background:#000'></body></html>", "text/html", "utf-8");
                    retry.removeCallbacksAndMessages(null);
                    retry.postDelayed(() -> view.loadUrl(url(view.getContext())), 30_000);
                }
            }
        });
        return web;
    }
}
