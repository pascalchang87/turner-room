package app.turnerroom.tv;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/** One field: the address of your gallery site. Type it with the Google TV phone remote. */
public class SettingsActivity extends Activity {
    private EditText field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int pad = dp(48);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_VERTICAL);
        root.setPadding(pad * 2, pad, pad * 2, pad);
        root.setBackgroundColor(Color.rgb(22, 19, 15));

        TextView title = new TextView(this);
        title.setText("Gallery address");
        title.setTextColor(Color.rgb(236, 228, 211));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        root.addView(title);

        TextView hint = new TextView(this);
        hint.setText("Enter your gallery site, for example my-turner-room.vercel.app. "
                + "Tip: the Google TV app on your phone has a keyboard.");
        hint.setTextColor(Color.rgb(163, 154, 136));
        hint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        hint.setPadding(0, dp(8), 0, dp(20));
        root.addView(hint);

        field = new EditText(this);
        field.setSingleLine(true);
        field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        field.setImeOptions(EditorInfo.IME_ACTION_DONE);
        field.setTextColor(Color.WHITE);
        field.setHintTextColor(Color.rgb(111, 103, 88));
        field.setHint("my-turner-room.vercel.app");
        field.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        String current = GalleryWeb.url(this);
        if (current != null) field.setText(current);
        field.setOnEditorActionListener((v, actionId, event) -> {
            save();
            return true;
        });
        root.addView(field, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Button button = new Button(this);
        button.setText("Save and open gallery");
        button.setOnClickListener(v -> save());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(20);
        root.addView(button, lp);

        setContentView(root);
        field.requestFocus();
    }

    private void save() {
        String value = field.getText().toString();
        if (GalleryWeb.normalise(value).isEmpty()) {
            field.setError("Enter the address of your gallery site");
            return;
        }
        GalleryWeb.saveUrl(this, value);
        finish();
    }

    private int dp(int v) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics()));
    }
}
