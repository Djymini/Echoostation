package com.djymini.echoostation.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

public class ParallelogramImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Path clipPath;

    public ParallelogramImageView(Context context) {
        super(context);
        init();
    }

    public ParallelogramImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParallelogramImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setScaleType(ScaleType.CENTER_CROP); // Pour bien remplir
        clipPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        clipPath.reset();
        // Parallélogramme incliné vers la droite
        clipPath.moveTo(20, 0); // essaye avec une valeur plus petite
        clipPath.lineTo(w, 0);
        clipPath.lineTo(w - 20, h);
        clipPath.lineTo(0, h);
        clipPath.close();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
        canvas.restore();
    }
}
