package com.example.karan.traffikill.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Karan on 31-07-2017.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {
    private Context context;

    public CustomTextView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }


    public void init() {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        this.setTypeface(face);
    }
}
