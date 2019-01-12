package MyUtil;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class MyClickSpan extends ClickableSpan {

    @Override
    public void onClick(View view) {
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.parseColor("#89ED9B"));
        ds.setUnderlineText(false);
    }

}