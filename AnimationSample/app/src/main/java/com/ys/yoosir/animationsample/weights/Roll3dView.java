package com.ys.yoosir.animationsample.weights;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 *  3D 动画 图片View
 * Created by ys on 2017/2/4 0004.
 */

public class Roll3dView extends View {

    private static final String TAG = "Roll3dView";
    private int viewWidth,viewHeight;
    private Paint mPaint;
    private Camera mCamera;
    private Matrix mMatrix;

    private List<Bitmap> mBitmapList;
    private Bitmap[][] mBitmaps;
    private int mPartNumber = 1;            //显示的图片布局的个数
    private int mAverWidth = 0, mAverHeight = 0; //平均每个布局的宽高

    private int preIndex = 0,currIndex = 0,nextIndex = 0;

    //滚动方向： 1 垂直方向  ； 其他为水平方向
    private int orientation = 1;
    //默认动画模式
    private RollMode rollMode = RollMode.SepartConbine;
    //翻转角度
    private float rotateDegree = 0;
    // 中心点 x方向旋转轴，Y方向旋转轴
    private float mAxisX = 0;
    private float mAxisY = 0;

    //3D 滚动模式
    public enum RollMode {
        //3D 整体滚动 ，尾部逐渐分离再合并 ，各模块一次滚动，百叶窗
        Roll2D,Whole3D,SepartConbine,RollInTurn,Jalousie
    }

    public Roll3dView(Context context) {
        this(context,null);
    }

    public Roll3dView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Roll3dView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mBitmapList = new ArrayList<>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCamera = new Camera();
        mMatrix = new Matrix();
    }

    /**
     *  设置滚动模式
     * @param rollMode
     */
    public void setRollMode(RollMode rollMode){
        this.rollMode = rollMode;
    }

    /**
     *  设置滚动方向  1 垂直方向 /  其他水平方向
     * @param orientation
     */
    public void setRotateOrientation(int orientation){
        this.orientation = orientation;
        initBitmaps();
    }

    /**
     *  设置图片块数
     * @param partNumber
     */
    public void setPartNumber(int partNumber){
        this.mPartNumber = partNumber;
        initBitmaps();
    }

    /**
     *  当前旋转的角度，并刷新
     * @param rotateDegree
     */
    public void setRotateDegree(float rotateDegree){
        this.rotateDegree = rotateDegree;
        //需要旋转的角度
        float degree = rollMode == RollMode.Jalousie ? 180 : 90;
        if(orientation == 1){
            mAxisY = rotateDegree / degree * viewHeight; // Y轴产生的偏移
        }else{
            mAxisX = rotateDegree / degree * viewWidth; // X轴产生的偏移
        }
        invalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initBitmaps();
    }

    /**
     *  初始化 图片列表
     */
    private void initBitmaps() {
        if(viewHeight <= 0 && viewWidth <= 0)
            return;
        if(null == mBitmapList || mBitmapList.size() == 0)
            return;
        //定义数组
        mBitmaps = new Bitmap[mBitmapList.size()][mPartNumber];
        //初始位置标记
        initIndex();

        //计算每个图片布局的宽高
        mAverWidth = viewWidth / mPartNumber;
        mAverHeight = viewHeight / mPartNumber;
        Bitmap partBitmap;
        for (int i = 0; i < mBitmapList.size(); i++){
            for(int j = 0; j < mPartNumber; j++){
                Rect rect;
                if(rollMode != RollMode.Jalousie){
                    //百叶窗模式
                    if(orientation == 1){
                        //纵向分块
                        //块区域
                        rect = new Rect(j * mAverWidth,0,(j+1) * mAverWidth,viewHeight);
                        //获取图片块
                        partBitmap = getPartBitmap(mBitmapList.get(i),j * mAverWidth,0,rect);
                    }else{
                        //横向分块
                        rect = new Rect(0,j * mAverHeight,viewWidth,(j + 1) * mAverHeight);
                        partBitmap = getPartBitmap(mBitmapList.get(i),0,j * mAverHeight,rect);
                    }
                }else{
                    if(orientation == 1){
                        rect = new Rect(0,j * mAverHeight,viewWidth,(j+1) * mAverHeight);
                        partBitmap = getPartBitmap(mBitmapList.get(i),0,j * mAverHeight,rect);
                    }else{
                        rect = new Rect(j * mAverWidth,0,(j+1) * mAverWidth,viewHeight);
                        partBitmap = getPartBitmap(mBitmapList.get(i),j * mAverWidth,0,rect);
                    }
                }
                mBitmaps[i][j] = partBitmap;
            }
        }
    }

    //初始化位置
    //标记 前 中 后位置
    private void initIndex() {
        int listSize = mBitmapList.size();
        nextIndex = currIndex + 1;
        preIndex = currIndex - 1;
        if(nextIndex > listSize - 1)
            nextIndex = 0;
        if(preIndex < 0)
            preIndex = listSize - 1;
    }

    /**
     *  获取图片块
     * @param bitmap  原始图片
     * @param x       起始 X坐标
     * @param y       起始 Y坐标
     * @param rect    图片块大小
     * @return
     */
    private Bitmap getPartBitmap(Bitmap bitmap,int x,int y,Rect rect){
        return Bitmap.createBitmap(bitmap,x,y,rect.width(),rect.height());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        if(viewWidth != 0 && viewHeight != 0){
            //缩放处理bitmap
            for (int i = 0; i < mBitmapList.size();i++){
                mBitmapList.set(i,scaleBitmap(mBitmapList.get(i)));
            }
            initBitmaps();
            invalidate();
        }
    }

    /**
     *  缩放bitmap
     * @param origin
     * @return
     */
    private Bitmap scaleBitmap(Bitmap origin) {
        if(origin == null)
            return null;

        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = viewWidth * 1.0f /width;
        float scaleHeight = viewHeight * 1.0f / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        return Bitmap.createBitmap(origin,0,0,width,height,matrix,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(null == mBitmapList || mBitmapList.size() <= 0)
            return;
        switch (rollMode){
            case Roll2D:
                drawRollWhole3D(canvas,true);
                break;
            case Whole3D:
                break;
            case SepartConbine:
                break;
            case RollInTurn:
                break;
            case Jalousie:
                break;
        }
    }

    /**
     *  整体翻转
     *   角度 degree 0 -> 90 往下翻转或往右翻转
     * @param canvas
     * @param draw2D  是否是2D效果：true 画2D效果； false 画3D效果
     */
    private void drawRollWhole3D(Canvas canvas, boolean draw2D) {

        //当前图片
        Bitmap currWholeBitmap = mBitmapList.get(currIndex);
        //下一个图片
        Bitmap nextWholeBitmap = mBitmapList.get(nextIndex);
        canvas.save();

        if(orientation == 1){
            //纵向
            mCamera.save();
            if(draw2D){
                //2D x轴翻转
                mCamera.rotateX(0);
            }else{
                mCamera.rotateX( -rotateDegree);
            }
            mCamera.getMatrix(mMatrix);
            mCamera.restore();

            mMatrix.preTranslate(-viewHeight/2,0);
            mMatrix.postTranslate(viewWidth/2,mAxisY);
            canvas.drawBitmap(currWholeBitmap,mMatrix,mPaint);

            mCamera.save();
            if(draw2D)
                mCamera.rotateX(0);
            else
                mCamera.rotateX((90 - rotateDegree));
            mCamera.getMatrix(mMatrix);
            mCamera.restore();

            mMatrix.preTranslate(-viewWidth/2,-viewHeight);
            mMatrix.postTranslate(viewWidth/2,mAxisY);
            canvas.drawBitmap(nextWholeBitmap,mMatrix,mPaint);
        }else{
            mCamera.save();
            if(draw2D)
                mCamera.rotateY(0);
            else
                mCamera.rotateY(rotateDegree);
            mCamera.getMatrix(mMatrix);
            mCamera.restore();

            mMatrix.preTranslate(0,-viewHeight/2);
            mMatrix.postTranslate(mAxisX,viewHeight/2);

            canvas.drawBitmap(currWholeBitmap,mMatrix,mPaint);

            mCamera.save();
            if(draw2D)
                mCamera.rotateY(0);
            else
                mCamera.rotateY(rotateDegree - 90);
            mCamera.getMatrix(mMatrix);
            mCamera.restore();

            mMatrix.preTranslate(-viewWidth,-viewHeight/2);
            mMatrix.postTranslate(mAxisX,viewHeight/2);
            canvas.drawBitmap(nextWholeBitmap,mMatrix,mPaint);
        }
        canvas.restore();
    }
}
