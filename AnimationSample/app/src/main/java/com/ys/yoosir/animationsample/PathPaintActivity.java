


package com.ys.yoosir.animationsample;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ViewAnimator;

public class PathPaintActivity extends AppCompatActivity {

    CanvasView mCanvasView;
    ObjectAnimator mAnimator;
    ValueAnimator mPathAnimator;
    //默认画板大小
    final static Path sTraversalPath = new Path();
    final static float TRAVERSE_PATH_SIZE = 7.0f;

    static {
        float inverse_sqrt8 = (float) Math.sqrt(0.125);
        RectF bounds = new RectF(1,1,3,3);
        sTraversalPath.addArc(bounds,45,180);
        sTraversalPath.addArc(bounds,225,180);

        bounds.set(1.5f + inverse_sqrt8,1.5f + inverse_sqrt8,2.5f + inverse_sqrt8,2.5f + inverse_sqrt8);
        sTraversalPath.addArc(bounds,45,180);
        sTraversalPath.addArc(bounds,225,180);

        bounds.set(4,1,6,3);
        sTraversalPath.addArc(bounds,135 ,-180);
        sTraversalPath.addArc(bounds,-45 ,-180);

        bounds.set(4.5f - inverse_sqrt8,1.5f + inverse_sqrt8,5.5f - inverse_sqrt8,2.5f + inverse_sqrt8);
        sTraversalPath.addArc(bounds,135,-180);
        sTraversalPath.addArc(bounds,-45,-180);

        sTraversalPath.addCircle(3.5f,3.5f,0.5f, Path.Direction.CCW);

        sTraversalPath.addArc(new RectF(1,2,6,6),0,180);

    }

    private void startAnimator(){
        if(mAnimator != null){
            mAnimator.cancel();
            mAnimator = null;
        }

        View view = findViewById(R.id.moved_item);
        Path path = mCanvasView.getPath();
        if(path.isEmpty()){
            return;
        }

//        mAnimator = ObjectAnimator.ofFloat(view,"x","y",path);
//        mAnimator.setDuration(10000);
//        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
//        mAnimator.setRepeatCount(1);
//        mAnimator.setInterpolator(new LinearInterpolator());
//        mAnimator.start();

//        mPathAnimator = ValueAnimator.ofFloat(0,1);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mCanvasView = (CanvasView) findViewById(R.id.canvas_view);
    }

    public static class CanvasView extends FrameLayout {

        Paint mPathPaint = new Paint();//创建画笔
        Path mPath = new Path();

        public CanvasView(Context context) {
            super(context);
            init();
        }

        public CanvasView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init(){
            //对于View Group 当什么都不 draw时，可以设置此方法来优化
            setWillNotDraw(false);
            mPathPaint.setColor(0xFFFF0000);
            mPathPaint.setStrokeWidth(2.0f);
            mPathPaint.setStyle(Paint.Style.STROKE);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            if(changed){
                //对图画进行缩放
                Matrix scale = new Matrix();
                float scaleWidth = (right - left)/TRAVERSE_PATH_SIZE;
                float scaleHeight = (bottom - top)/TRAVERSE_PATH_SIZE;
                scale.setScale(scaleWidth,scaleHeight);
                sTraversalPath.transform(scale,mPath);
            }
        }

        public Path getPath(){
            return mPath;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawPath(mPath,mPathPaint);
            super.draw(canvas);
        }
    }
}
