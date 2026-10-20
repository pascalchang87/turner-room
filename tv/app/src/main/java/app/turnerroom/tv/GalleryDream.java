package app.turnerroom.tv;

import android.service.dreams.DreamService;
import android.webkit.WebView;

/** The screensaver: the same gallery page, full screen, until someone presses a button. */
public class GalleryDream extends DreamService {
    private WebView web;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);
        setFullscreen(true);
        setScreenBright(true);
        web = GalleryWeb.create(this);
        setContentView(web);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        if (GalleryWeb.hasUrl(this)) web.loadUrl(GalleryWeb.url(this));
        web.onResume();
    }

    @Override
    public void onDreamingStopped() {
        if (web != null) web.onPause();
        super.onDreamingStopped();
    }

    @Override
    public void onDetachedFromWindow() {
        if (web != null) {
            web.destroy();
            web = null;
        }
        super.onDetachedFromWindow();
    }
}
