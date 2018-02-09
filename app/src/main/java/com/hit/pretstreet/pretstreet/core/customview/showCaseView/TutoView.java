package com.hit.pretstreet.pretstreet.core.customview.showCaseView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.hit.pretstreet.pretstreet.core.customview.showCaseView.shapes.Circle;
import com.hit.pretstreet.pretstreet.core.customview.showCaseView.shapes.RoundRect;
import com.hit.pretstreet.pretstreet.core.customview.showCaseView.shapes.Shape;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by User on 24/01/2018.
 * Showcase view - TutoView
 */
class TutoView extends View {

    static final int DEFAULT_ALPHA_COLOR = 200;
    int backgroundOverlayColor = Color.argb(DEFAULT_ALPHA_COLOR, 0, 0, 0);
    float rotateValue = 0;
    List<Shape> shapes;

    public TutoView(Context context) {
        super(context);
        initialize();
    }

    public TutoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TutoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void addCircle(Circle circle) {
        this.shapes.add(circle);
    }

    public void addRoundRect(RoundRect roundRect) {
        this.shapes.add(roundRect);
    }

    public void addRotation(float value){
        rotateValue = value;
    }

    public int getBackgroundOverlayColor() {
        return backgroundOverlayColor;
    }

    public void setBackgroundOverlayColor(int backgroundOverlayColor) {
        this.backgroundOverlayColor = backgroundOverlayColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(backgroundOverlayColor);
        canvas.rotate(rotateValue);
        for (Shape shape : shapes) {
            shape.drawOn(canvas);
        }

    }

    private void initialize() {
        shapes = new ArrayList<>();

        setDrawingCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

    }
}
