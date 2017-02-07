package com.ys.yoosir.animationsample.weights;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
    private boolean isRolling; //是否正在执行动画

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
     *  添加图片
     * @param bitmap
     */
    public void addImageBitmap(Bitmap bitmap){
        mBitmapList.add(bitmap);
        initBitmaps();
        invalidate();
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

    ValueAnimator valueAnimator;
    int rollDuration = 1 * 1000;
    public void toNext(){
        if(isRolling)
            return;

        if(rollMode == RollMode.RollInTurn){
            valueAnimator = ValueAnimator.ofFloat(0,90 + (mPartNumber - 1) * 30);
        }else if(rollMode == RollMode.Jalousie){
            valueAnimator = ValueAnimator.ofFloat(0,180);
        }else{
            valueAnimator = ValueAnimator.ofFloat(0,90);
        }
        isRolling = true;
        //动画时长
        valueAnimator.setDuration(rollDuration);
        //动画过程监听
        valueAnimator.addUpdateListener(mUpdateListener);
        //动画结束，执行下一个动画
        valueAnimator.addListener(mToNextAnimListener);
        valueAnimator.start();
    }

    /**
     *  执行从 next 到 curr 的翻转过程
     */
    public void toPre(){
        if(isRolling)
            return;
        int startRotate = 0;
        if(rollMode == RollMode.RollInTurn){
            startRotate = 90 + (mPartNumber - 1) * 30;
        }else if(rollMode == RollMode.Jalousie){
            startRotate = 180;
        }else{
            startRotate = 90;
        }

        //rotateDegree = 0 说明 curr 在当前显示
        //设置角度为90或者180 nextIndex和currIndex preIndex轮转互换，使next显示到当前的图片，然后完成翻转
        //可以通俗的理解为 先倒过来，再翻过去
        //只不过倒过来之前把图片也互换了，所以看不出来而已
        rollIndex(true);
        setRotateDegree(startRotate);

        isRolling = true;
        valueAnimator = ValueAnimator.ofFloat(startRotate,0);
        valueAnimator.setDuration(rollDuration);
        valueAnimator.addUpdateListener(mUpdateListener);
        valueAnimator.addListener(mToPreAnimListener);
        valueAnimator.start();
    }

    private void rollIndex(boolean toPre) {
        int temp;
        if(toPre){
            temp = currIndex;
            currIndex = preIndex;
            preIndex = nextIndex;
            nextIndex = temp;
        }else{
            temp = currIndex;
            currIndex = nextIndex;
            nextIndex = preIndex;
            preIndex = temp;
        }
    }

    private ValueAnimator.AnimatorUpdateListener mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float value = (float) valueAnimator.getAnimatedValue();
            setRotateDegree(value);
        }
    };

    private AnimatorListenerAdapter mToNextAnimListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            currIndex++;
            if(currIndex > mBitmapList.size() - 1)
                currIndex = 0;
            initIndex();
            //更新Index 旋转角度归0
            setRotateDegree(0);
            isRolling = false;
        }
    };

    private AnimatorListenerAdapter mToPreAnimListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //index位置恢复
            rollIndex(false);
            currIndex--;
            if(currIndex < 0)
                currIndex = mBitmapList.size() - 1;

            initIndex();
            isRolling = false;
            invalidate();//index 位置修正之后刷新一下
        }
    };



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
                drawRollWhole3D(canvas,false);
                break;
            case SepartConbine:
                drawSepartConbine(canvas);
                break;
            case RollInTurn:
                drawRollInTurn(canvas);
                break;
            case Jalousie:
                drawJalousie(canvas);
                break;
        }
    }

    /**
     *  在前面的matrix中我们讲到过，其实屏幕后方是一个三维坐标系，这个坐标系的y轴正方向是朝上的，
     *  z轴是朝里面的，屏幕像一个窗口，我们看到的是窗口外面的物体投射到窗口上的二维镜像。
     *  Camera实际上就像我们的眼睛，眼睛看到的是物体投射到窗口上的图形，
     *  其实这里就有3个要素，一是物体，二是窗子，三是眼睛，也就是物体，屏幕和camera。
     *  最终呈现在用户面前的是屏幕上的图形。影响物体投射到屏幕上的效果，
     *  可以移动物体（前面讲解的matrix），也可以移动眼睛（看下面的setLocation方法解析，会有详细讲解）。
     *  通过这些不同的操作，最终使得映射在屏幕上的图形不同，然后呈现给用户的也就不同了。
     */

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
            //执行当前图片动画
            mCamera.save();
            if(draw2D){
                //2D x轴翻转
                mCamera.rotateX(0);
            }else{
                mCamera.rotateX( -rotateDegree);
            }
            mCamera.getMatrix(mMatrix);
            mCamera.restore();

            mMatrix.preTranslate(-viewWidth/2,0);
            mMatrix.postTranslate(viewWidth/2,mAxisY);
            canvas.drawBitmap(currWholeBitmap,mMatrix,mPaint);

            //执行下张图片动画
            mCamera.save();
            if(draw2D) {
                mCamera.rotateX(0);
            }else {
                mCamera.rotateX((90 - rotateDegree));
            }
            mCamera.getMatrix(mMatrix);
            mCamera.restore();

            mMatrix.preTranslate(-viewWidth/2,-viewHeight);
            mMatrix.postTranslate(viewWidth/2,mAxisY);
            canvas.drawBitmap(nextWholeBitmap,mMatrix,mPaint);
        }else{
            mCamera.save();
            if(draw2D) {
                mCamera.rotateY(0);
            }else {
                mCamera.rotateY(rotateDegree);
            }
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

    /**
     *  纵向 头部接合 尾部分离效果
     *
     *  degree 0 -> 90 往下翻滚 或者往右翻滚 90 -> 0 往上翻滚 或者往左翻滚
     *
     * @param canvas
     */
    private void drawSepartConbine(Canvas canvas){
        for (int i = 0; i < mPartNumber;i++){
            Bitmap currBitmap = mBitmaps[currIndex][i];
            Bitmap nextBitmap = mBitmaps[nextIndex][i];

            canvas.save();
            if(orientation == 1){//纵向 , 向下
                mCamera.save();
                mCamera.rotateX(-rotateDegree);
                mCamera.getMatrix(mMatrix);
                mCamera.restore();

                mMatrix.preTranslate(-currBitmap.getWidth()/2,0);
                mMatrix.postTranslate(currBitmap.getWidth()/2 + i * mAverWidth,mAxisY);
                canvas.drawBitmap(currBitmap,mMatrix,mPaint);

                mCamera.save();
                mCamera.rotateX(90 - rotateDegree);
                mCamera.getMatrix(mMatrix);
                mCamera.restore();

                mMatrix.preTranslate(-nextBitmap.getWidth()/2,-nextBitmap.getHeight());
                mMatrix.postTranslate(nextBitmap.getWidth()/2 + i * mAverWidth,mAxisY);
                canvas.drawBitmap(nextBitmap,mMatrix,mPaint);
            }else{
                mCamera.save();
                mCamera.rotateY(rotateDegree);
                mCamera.getMatrix(mMatrix);
                mCamera.restore();

                mMatrix.preTranslate(0,-currBitmap.getHeight()/2);
                mMatrix.postTranslate(mAxisX,currBitmap.getHeight() / 2 + i * mAverHeight);
                canvas.drawBitmap(currBitmap,mMatrix,mPaint);

                mCamera.save();
                mCamera.rotateY(rotateDegree - 90);
                mCamera.getMatrix(mMatrix);
                mCamera.restore();

                mMatrix.preTranslate(-nextBitmap.getWidth(),-nextBitmap.getHeight()/2);
                mMatrix.postTranslate(mAxisX,nextBitmap.getHeight() / 2 + i * mAverHeight);
                canvas.drawBitmap(nextBitmap,mMatrix,mPaint);
            }
            canvas.restore();
        }
    }

    /**
     *  依次翻转
     * @param canvas
     */
    private void drawRollInTurn(Canvas canvas) {
        for (int i = 0; i < mPartNumber; i++){
            Bitmap currBitmap = mBitmaps[currIndex][i];
            Bitmap nextBitmap = mBitmaps[nextIndex][i];

            //分段，每30度执行 一图片块的动画
            float tDegree = rotateDegree - i * 30;
            if(tDegree < 0)
                tDegree = 0;
            if(tDegree > 90)
                tDegree = 90;

            canvas.save();
            if(orientation == 1){
                //垂直方向，则绕 X轴旋转
                //此图片块已旋转的角度，从而产生的 Y轴位移
                float tAxisY = tDegree / 90f * viewHeight;
                if(tAxisY > viewHeight)
                    tAxisY = viewHeight;
                if(tAxisY < 0)
                    tAxisY = 0;

                mCamera.save();
                mCamera.rotateX(-tDegree);
                mCamera.getMatrix(mMatrix);
                mCamera.restore();

                mMatrix.preTranslate(-currBitmap.getWidth(),0);
                mMatrix.preTranslate(currBitmap.getWidth() + i * mAverWidth,tAxisY);
                canvas.drawBitmap(currBitmap,mMatrix,mPaint);

                mCamera.save();
                mCamera.rotateX(90 - tDegree);
                mCamera.getMatrix(mMatrix);
                mCamera.restore();

                mMatrix.preTranslate(-nextBitmap.getWidth(),-nextBitmap.getHeight());
                mMatrix.postTranslate(nextBitmap.getWidth() + i * mAverWidth,tAxisY);
                canvas.drawBitmap(nextBitmap,mMatrix,mPaint);
            }else{
                //此图片块已旋转的角度，从而产生的 X轴位移
                float tAxisX = tDegree / 90f * viewWidth;
                if(tAxisX > viewHeight)
                    tAxisX = viewHeight;
                if(tAxisX < 0)
                    tAxisX = 0;

                mCamera.save();
                mCamera.rotateY(tDegree);
                mCamera.getMatrix(mMatrix);
                mCamera.restore();

                mMatrix.preTranslate(0,-currBitmap.getHeight()/2);
                mMatrix.preTranslate(tAxisX,currBitmap.getHeight()/2 + i * mAverHeight);
                canvas.drawBitmap(currBitmap,mMatrix,mPaint);

                mCamera.save();
                mCamera.rotateX(tDegree - 90);
                mCamera.getMatrix(mMatrix);
                mCamera.restore();

                mMatrix.preTranslate(-nextBitmap.getWidth(),-nextBitmap.getHeight()/2);
                mMatrix.postTranslate(tAxisX,nextBitmap.getHeight() / 2 + i * mAverHeight);
                canvas.drawBitmap(nextBitmap,mMatrix,mPaint);
            }
            canvas.restore();
        }
    }

    /**
     *  百叶窗翻页
     * @param canvas
     */
    private void drawJalousie(Canvas canvas){
        for(int i = 0; i < mPartNumber;i++){
            Bitmap currBitmap = mBitmaps[currIndex][i];
            Bitmap nextBitmap = mBitmaps[nextIndex][i];

            canvas.save();
            //注意，百叶窗的旋转方向和其他模式是相反的，横向的时候纵翻 纵向的时候横翻
            if(orientation == 1){
                if(rotateDegree < 90){
                    mCamera.save();
                    mCamera.rotateX(rotateDegree);
                    mCamera.getMatrix(mMatrix);
                    mCamera.restore();

                    mMatrix.preTranslate(-currBitmap.getWidth() / 2,-currBitmap.getHeight() /2);
                    mMatrix.postTranslate(currBitmap.getWidth() / 2,currBitmap.getHeight() / 2 + i * mAverHeight);
                    canvas.drawBitmap(currBitmap,mMatrix,mPaint);
                }else{
                    mCamera.save();
                    mCamera.rotateX(180 - rotateDegree);
                    mCamera.getMatrix(mMatrix);
                    mCamera.restore();

                    mMatrix.preTranslate(-nextBitmap.getWidth() / 2, -nextBitmap.getHeight() / 2);
                    mMatrix.postTranslate(nextBitmap.getWidth() / 2,nextBitmap.getHeight() / 2 + i * mAverHeight);
                    canvas.drawBitmap(nextBitmap,mMatrix,mPaint);
                }
            }else{
                if(rotateDegree < 90){
                    mCamera.save();
                    mCamera.rotateY(rotateDegree);
                    mCamera.getMatrix(mMatrix);
                    mCamera.restore();

                    mMatrix.preTranslate(-currBitmap.getWidth() / 2 , -currBitmap.getHeight() / 2);
                    mMatrix.postTranslate(currBitmap.getWidth() / 2 + i * mAverWidth,currBitmap.getHeight() / 2);
                    canvas.drawBitmap(currBitmap,mMatrix,mPaint);
                }else{
                    mCamera.save();
                    mCamera.rotateY(180 - rotateDegree);
                    mCamera.getMatrix(mMatrix);
                    mCamera.restore();

                    mMatrix.preTranslate(-nextBitmap.getWidth() / 2,-nextBitmap.getHeight() / 2);
                    mMatrix.postTranslate(nextBitmap.getWidth() / 2 + i * mAverWidth,nextBitmap.getHeight()/2);
                    canvas.drawBitmap(nextBitmap,mMatrix,mPaint);
                }
            }
            canvas.restore();
        }
    }
}
