package com.ys.yoosir.animationsample.weights;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ys.yoosir.animationsample.R;

/**
 *  飘落物
 * Created by ys on 2017/2/7 0007.
 */

public class FallingBodyView extends ImageView {

    private static final Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private int mBodyId = R.mipmap.heart0;
    private int mBodyBorderResId = R.mipmap.heart1;
    private static Bitmap sBody;
    private static Bitmap sBodyBorder;
    private static final Canvas sCanvas = new Canvas();

    public FallingBodyView(Context context) {
        super(context);
    }

    public FallingBodyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FallingBodyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDrawable(int resourceId){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),resourceId);
        setImageDrawable(new BitmapDrawable(getResources(),bitmap));
    }
}
