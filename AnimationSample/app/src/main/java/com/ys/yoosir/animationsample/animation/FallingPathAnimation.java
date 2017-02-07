package com.ys.yoosir.animationsample.animation;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.ys.yoosir.animationsample.weights.FallingLayout;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  飘落路径动画
 * Created by ys on 2017/2/7 0007.
 */

public class FallingPathAnimation{

    private final AtomicInteger mCounter = new AtomicInteger(0);
    private final FallingLayout.Config mConfig;
    private final Random mRandom;
    private Handler mHandler;

    public FallingPathAnimation(FallingLayout.Config config){
        mConfig = config;
        mRandom = new Random();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void start(final View child, final ViewGroup parent){
        //将 子View 添加到 父View上
        parent.addView(child,new ViewGroup.LayoutParams(mConfig.rainWidth,mConfig.rainHeight));
        //创建动画路径
        Path animPath = createPath(parent,2);
        //创建动画
        FloatAnimation anim = new FloatAnimation(animPath,randomRotation(),parent,child);
        anim.setDuration(mConfig.animDuration);
        anim.setInterpolator(new LinearInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mCounter.incrementAndGet();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        parent.removeView(child);
                    }
                });
                mCounter.decrementAndGet();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        child.startAnimation(anim);
    }

    public Path createPath(View parent,int factor ){
        //主要通过 Path.cubicTo 方法，生成一个贝塞尔曲线
        // cubicTo(float x1, float y1, float x2, float y2,float x3, float y3)
        // 我们需要三个坐标点
        int x = mRandom.nextInt(mConfig.initX);
        int x1 = mRandom.nextInt(mConfig.xRand);
        x1 = mRandom.nextBoolean() ? x1 + x : Math.abs(x - x1);
        int x2 = mRandom.nextInt(mConfig.xRand);
        x2 = mRandom.nextBoolean() ? x2 + x1 : Math.abs(x1 - x2);
        x1 = x1 + (mRandom.nextBoolean() ? 0 : mConfig.xPointFactor);
        x2 = x2 + ( mRandom.nextBoolean() ? mConfig.xPointFactor:0);
        int y = mConfig.initY;
        int y2 = mCounter.intValue() * 15 + mConfig.animLength * factor + mRandom.nextInt(mConfig.animLengthRand);
        factor = y2 / mConfig.bezierFactor;

        int y3 = parent.getHeight();
        y2 = y2/2;
        Path p = new Path();
        p.moveTo(x,y);
        //贝塞尔曲线
        p.cubicTo(x,y ,x1,y2 - factor , x1,y2);
        p.moveTo(x1,y2);
        p.cubicTo(x1,y2 + factor,x2,y3 - factor,x2,y3);
        return p;
    }

    static class FloatAnimation extends Animation{

        private PathMeasure mPm; //计算路径距离
        private View mChildView;
        private float mDistance;
        private float mRotation;

        public FloatAnimation(Path path, float rotation, View parent, View child){
            mPm = new PathMeasure(path,false);
            mDistance = mPm.getLength();
            mChildView = child;
            mRotation = rotation;
            parent.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
//            Matrix matrix = t.getMatrix();
//            mPm.getMatrix(mDistance * interpolatedTime,matrix,PathMeasure.POSITION_MATRIX_FLAG);
            float[] pos = {0,0};
            float[] tan = {0,0};
            mPm.getPosTan(mDistance * interpolatedTime,pos,tan);
            Log.d("FallingPathAnimation","pos = ("+pos[0] +","+pos[1]+")");
            Log.d("FallingPathAnimation","tan = ("+tan[0] +","+tan[1]+")");
            mChildView.setRotation(mRotation * interpolatedTime);
            //缩放
            float scale = 1F;
            if (3000.0F * interpolatedTime < 200.0F) {
                scale = scale(interpolatedTime, 0.0D, 0.06666667014360428D, 0.20000000298023224D, 1.100000023841858D);
            } else if (3000.0F * interpolatedTime < 300.0F) {
                scale = scale(interpolatedTime, 0.06666667014360428D, 0.10000000149011612D, 1.100000023841858D, 1.0D);
            }
            mChildView.setScaleX(scale);
            mChildView.setScaleY(scale);
            mChildView.setX(pos[0]);
            mChildView.setY(pos[1]);
            //渐变
//            t.setAlpha(1.0F - interpolatedTime);
        }
    }

    private static float scale(double a, double b, double c, double d, double e) {
        return (float) ((a - b) / (c - b) * (e - d) + d);
    }

    public float randomRotation() {
        return mRandom.nextFloat() * 28.6F - 14.3F;
    }

}
