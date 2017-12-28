package com.hit.pretstreet.pretstreet.core.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.Gravity;

import com.hit.pretstreet.pretstreet.R;


/**
 * Created by User on 29/6/17.
 * Create a custom widget which has inbuilt font
 */
public class ButtonPret extends AppCompatButton {

    //default font taken from assets folder
    private static final String defaultFontName = "MERRIWEATHER-REGULAR.TTF";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ButtonPret(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public ButtonPret(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ButtonPret(Context context) {
        super(context);
        init(null, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomStyle);
            String fontName = a.getString(R.styleable.CustomStyle_pret_typeface);
            try {
                if (fontName == null) fontName = defaultFontName;
                //applying custom font
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "" + fontName);
                setTypeface(myTypeface);
            } catch (Exception e) {
                e.printStackTrace();
            }
            a.recycle();
        }
    }

}
