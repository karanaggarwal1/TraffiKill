package com.example.karan.traffikill.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Karan on 31-07-2017.
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {
    Context context;

    public CustomEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        this.setTypeface(face);
    }
}
