package app.turnerroom.tv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

/** Shows the gallery full screen. Hold OK on the remote to change the address. */
public class MainActivity extends Activity {
    private WebView web;
    private String loadedUrl = "";
    private boolean askedForUrl = false;
    private boolean settingsOpening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        web = GalleryWeb.create(this);
        setContentView(web);
        hideSystemUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
        hideSystemUi();
        settingsOpening = false;
        if (!GalleryWeb.hasUrl(this)) {
            if (askedForUrl) {
                finish(); // backed out without entering an address
            } else {
                askedForUrl = true;
                openSettings();
            }
            return;
        }
        String url = GalleryWeb.url(this);
        if (!url.equals(loadedUrl)) {
            loadedUrl = url;
            web.loadUrl(url);
        }
        web.requestFocus();
    }

    @Override
    protected void onPause() {
        web.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        web.destroy();
        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int code = event.getKeyCode();
        if (code == KeyEvent.KEYCODE_DPAD_CENTER || code == KeyEvent.KEYCODE_ENTER) {
            // Hold OK for about a second to change the gallery address.
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() > 0
                    && event.getEventTime() - event.getDownTime() > 900 && !settingsOpening) {
                settingsOpening = true;
                openSettings();
            }
            return true;
        }
        if ((code == KeyEvent.KEYCODE_MENU || code == KeyEvent.KEYCODE_SETTINGS)
                && event.getAction() == KeyEvent.ACTION_UP) {
            openSettings();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}
