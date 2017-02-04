package com.ys.yoosir.animationsample.animation;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 在两个指定角度之间旋转Y轴上的视图的动画。
 * 此动画还在Z轴（深度）上添加了翻转以提高效果。
 *
 * Created by ys on 2017/2/4 0004.
 */

public class Rotate3dAnimation extends Animation{

    private final float mFromDegrees;   //开始的角度
    private final float mToDegrees;     //结束的角度
    private final float mCenterX;       //中心点 x 轴坐标
    private final float mCenterY;       //中心点 y 轴坐标
    private final float mDepthZ;        // z 轴深度
    private final boolean mReverse;     //是否反转
    private Camera mCamera;              //相机
    private float scale = 1;             //像素密度

    public Rotate3dAnimation(Context context, float fromDegrees, float toDegrees, float centerX,
                             float centerY, float depthZ, boolean reverse){
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
        mReverse = reverse;

        //获取手机像素密度 （即dp与px的比例）
        scale = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();
        //调节 Z 轴深度
        if(mReverse){
            camera.translate(0.0f,0.0f,mDepthZ * interpolatedTime);
        }else{
            camera.translate(0.0f,0.0f,mDepthZ * (1.0f - interpolatedTime));
        }
        //绕Y轴旋转
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        //修正失真，主要修改 MPERSP_0 和 MPERSP_1
        float[] mValues = new float[9];
        matrix.getValues(mValues);
        mValues[6] = mValues[6]/scale;  //修正数值
        mValues[7] = mValues[7]/scale;  //修正数值
        matrix.setValues(mValues);      //重新赋值

        //调节中心点
        matrix.preTranslate(-centerX,-centerY);
        matrix.postTranslate(centerX,centerY);
    }
}
