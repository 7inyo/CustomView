package com.qf.customview;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LeafLoadView extends View {

    private static String TAG = "felix";

    private Resources mResource;
    // 淡白色
    private static final int WHITE_COLOR = 0xfffde399;
    // 橙色
    private static final int ORANGE_COLOR = 0xffffa800;
    // 用于控制绘制的进度条距离左／上／下的距离
    private static final int LEFT_MARGIN = 9;
    // 用于控制绘制的进度条距离右的距离
    private static final int RIGHT_MARGIN = 25;
    private int mLeftMargin, mRightMargin;

    // 叶子飘动一个周期所花的时间
    private static final long LEAF_FLOAT_TIME = 3000;
    // 叶子旋转一周需要的时间
    private static final long LEAF_ROTATE_TIME = 2000;
    // 叶子飘动一个周期所花的时间
    private long mLeafFloatTime = LEAF_FLOAT_TIME;
    // 叶子旋转一周需要的时间
    private long mLeafRotateTime = LEAF_ROTATE_TIME;
    // 中等振幅大小
    private static final int MIDDLE_AMPLITUDE = 30;
    // 不同类型之间的振幅差距
    private static final int AMPLITUDE_DISPARITY = 15;
    // 中等振幅大小
    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;
    // 振幅差
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;
    // total progress
    public static final int TOTAL_PROGRESS = 100;
    // main thread handler
    private Handler mh;
    public static final int EVENT_ON_SIZE_CHANGED = 0;
    // current progress
    private int mProgress = 0;
    // when to draw the progress view
    private long mProgressStartTime = 0;
    // first leaf arrived left
    private boolean mFirstLeafArrived = false;
    // if loading done
    private boolean loadingDone = false;
    // 所绘制的进度条部分的宽度
    private int mProgressWidth;
    // the radius of the arc
    private int mArcRadius;
    // current progress in px
    private int mCurrentProgressWidth;
    private Bitmap mLeafBitmap, mLeafOuter;
    private int mLeafWidth, mLeafHeight;
    private int mOuterWidth, mOuterHeight;
    private Rect mOuterSrcRect, mOuterDestRect;
    private RectF mWhiteRectF, mOrangeRectF, mArcRectF;

    private Paint mBitmapPaint, mWhitePaint, mOrangePaint;

    private int mTotalWidth, mTotalHeight;
    private LeafFactory mLeafFactory;
    private List<Leaf> mLeafInfos;

    public LeafLoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "constructor leaf load view");
        mResource = getResources();
        mLeftMargin = UiUtils.dipToPx(context, LEFT_MARGIN);
        mRightMargin = UiUtils.dipToPx(context, RIGHT_MARGIN);

        initBitmap();
        initPaint();

        mLeafFactory = new LeafFactory();
        mLeafInfos = mLeafFactory.generateLeafs();
        mProgressStartTime += LEAF_FLOAT_TIME;
    }

    private void initBitmap() {
        Log.d(TAG, "init bitmap");
        mLeafBitmap = ((BitmapDrawable) mResource.getDrawable(R.drawable.leaf)).getBitmap();
        mLeafWidth = mLeafBitmap.getWidth();
        mLeafHeight = mLeafBitmap.getHeight();

        mLeafOuter = ((BitmapDrawable) mResource.getDrawable(R.drawable.leaf_kuang)).getBitmap();
        mOuterWidth = mLeafOuter.getWidth();
        mOuterHeight = mLeafOuter.getHeight();

    }

    private void initPaint() {
        Log.d(TAG, "init paint");
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(WHITE_COLOR);

        mOrangePaint = new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(ORANGE_COLOR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "leaf load view on draw");
        drawProgress(canvas);
        drawLeafs(canvas);
        canvas.drawBitmap(mLeafOuter, mOuterSrcRect, mOuterDestRect, mBitmapPaint);
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Message msg = new Message();
        msg.what = EVENT_ON_SIZE_CHANGED;
        this.mh.sendMessage(msg);
        Log.d(TAG, "on size changed, w=" + w + ", h=" + h);
        mTotalWidth = w;
        mTotalHeight = h;
        mOuterSrcRect = new Rect(0, 0, mOuterWidth, mOuterHeight);
        mOuterDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);
        mProgressWidth = mTotalWidth - mLeftMargin - mRightMargin;
        mArcRadius = (mTotalHeight - 2 * mLeftMargin) / 2;

        mArcRectF = new RectF(mLeftMargin, mLeftMargin, mLeftMargin + 2 * mArcRadius, mTotalHeight - mLeftMargin);
        mWhiteRectF = new RectF(mLeftMargin + mArcRadius, mLeftMargin, mTotalWidth - mRightMargin,
                mTotalHeight - mLeftMargin);
        mOrangeRectF = new RectF(mLeftMargin + mArcRadius, mLeftMargin, mLeftMargin + mCurrentProgressWidth,
                mTotalHeight - mLeftMargin);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "leaf load view on measure");
    }

    private void drawProgress(Canvas canvas) {
        if (mProgress >= TOTAL_PROGRESS) {
            // draw orange arc
            canvas.drawArc(mArcRectF, 90, 180, false, mOrangePaint);
            // draw orange rect
            canvas.drawRect(mOrangeRectF, mOrangePaint);
            if (!loadingDone) {
                // send only once
                mh.sendEmptyMessage(LeafActivity.PROGRESS_DONE);
                loadingDone = true;
            }
            return;
        }
        if (mProgress == 0) {
            // draw white arc
            canvas.drawArc(mArcRectF, 90, 180, false, mWhitePaint);
            // draw white rect
            canvas.drawRect(mWhiteRectF, mWhitePaint);
        }
        long currentTime = System.currentTimeMillis();
        if (mProgressStartTime > currentTime) {
            Log.d(TAG, "leaf not arrived yet");
            return;
        }
        // first leaf arrived left
        if (!mFirstLeafArrived) {
            mFirstLeafArrived = true;
            // here send only once
            mh.sendEmptyMessage(LeafActivity.REFRESH_PROGRESS);
        }
        mCurrentProgressWidth = mProgressWidth * mProgress / TOTAL_PROGRESS;
        Log.d(TAG,
                "mprogress=" + mProgress + ", mcurrentprogresswidth=" + mCurrentProgressWidth + ", arc=" + mArcRadius);
        if (mCurrentProgressWidth < mArcRadius) {
            // draw white arc
            canvas.drawArc(mArcRectF, 90, 180, false, mWhitePaint);
            // draw white rect
            canvas.drawRect(mWhiteRectF, mWhitePaint);
            // draw orange arc
            int angle = (int) Math.toDegrees(Math.acos((mArcRadius - mCurrentProgressWidth) / (float) mArcRadius));
            int startAngle = 180 - angle;
            int sweepAngle = 2 * angle;
            canvas.drawArc(mArcRectF, startAngle, sweepAngle, false, mOrangePaint);
        } else {
            // draw orange arc
            canvas.drawArc(mArcRectF, 90, 180, false, mOrangePaint);
            // draw orange rect
            mOrangeRectF.right = mCurrentProgressWidth + mLeftMargin;
            canvas.drawRect(mOrangeRectF, mOrangePaint);
            // draw white rect
            mWhiteRectF.left = mCurrentProgressWidth + mLeftMargin;
            canvas.drawRect(mWhiteRectF, mWhitePaint);
        }
    }

    /**
     * 绘制叶子
     * 
     * @param canvas
     */
    private void drawLeafs(Canvas canvas) {
        if (mProgress >= TOTAL_PROGRESS)
            return;
        mLeafRotateTime = mLeafRotateTime <= 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mLeafInfos.size(); i++) {
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                // 获取叶子位置
                getLeafLocation(leaf, currentTime);
                // Log.d(TAG, "leaf x=" + leaf.x + ", leaf y=" + leaf.y);
                //
                canvas.save();
                //
                Matrix matrix = new Matrix();
                // 叶子位移
                float transX = mLeftMargin + leaf.x;
                float transY = mLeftMargin + leaf.y;
                matrix.postTranslate(transX, transY);
                // 叶子旋转，与旋转一周的时间关联起来
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime) / (float) mLeafRotateTime;
                // Log.d(TAG, "curr-start=" + (currentTime - leaf.startTime) +
                // ", mLeafRotateTime=" + mLeafRotateTime);
                int angle = (int) (360 * rotateFraction);
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : leaf.rotateAngle - angle;
                // Log.d(TAG, "rotateFraction=" + rotateFraction + ", angle=" +
                // angle + ", rotate=" + rotate);
                matrix.postRotate(rotate, transX + mLeafWidth / 2, transY + mLeafHeight / 2);
                canvas.drawBitmap(mLeafBitmap, matrix, mBitmapPaint);
                canvas.restore();
            }
        }
    }

    private void getLeafLocation(Leaf leaf, long currentTime) {
        long intervalTime = currentTime - leaf.startTime;
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            leaf.startTime = System.currentTimeMillis() + new Random().nextInt((int) mLeafFloatTime);
        }
        float fraction = (float) intervalTime / mLeafFloatTime;
        leaf.x = mProgressWidth * (1.0f - fraction);
        leaf.y = getLocationY(leaf);
    }

    private float getLocationY(Leaf leaf) {
        // y = A sin(wx + Q) + h 正弦曲线
        float w = (float) ((float) 2 * Math.PI / mProgressWidth);
        float a = mMiddleAmplitude;
        switch (leaf.type) {
        case LITTLE:
            a = mMiddleAmplitude - mAmplitudeDisparity;
            break;
        case MEDIUM:
            break;
        case BIG:
            a = mMiddleAmplitude + mAmplitudeDisparity;
            break;
        }
        float q = (new Random().nextInt(2 * mArcRadius)) - mArcRadius;
        // Log.d(TAG, "w=" + w + ", a=" + a + ", q=" + q);
        return (float) (a * Math.sin(w * leaf.x) + mArcRadius);
    }

    private enum StartType {
        LITTLE, MEDIUM, BIG
    }

    private class Leaf {
        // 叶子坐标
        float x, y;
        // 叶子振幅
        StartType type;
        // 旋转角度
        int rotateAngle;
        // 旋转方向 0 --顺时针， 1--逆时针
        int rotateDirection;
        // 随机值，使叶子错落
        long startTime;
    }

    private class LeafFactory {
        private static final int MAX_LEAFS = 8;
        Random random = new Random();

        public Leaf generateLeaf() {
            Leaf leaf = new Leaf();
            int randomType = random.nextInt(3);
            StartType type = StartType.MEDIUM;
            switch (randomType) {
            case 0:
                type = StartType.LITTLE;
                break;
            case 2:
                type = StartType.BIG;
                break;
            default:
                break;
            }
            leaf.type = type;
            leaf.rotateAngle = random.nextInt(360);
            leaf.rotateDirection = random.nextInt(2);
            int addTime = random.nextInt((int) (2 * mLeafFloatTime));
            leaf.startTime = System.currentTimeMillis() + addTime;
            if (mProgressStartTime == 0) {
                mProgressStartTime = leaf.startTime;
            } else {
                if (mProgressStartTime > leaf.startTime)
                    mProgressStartTime = leaf.startTime;
            }
            Log.d(TAG, "mProgressStartTime=" + mProgressStartTime + ", startTime=" + leaf.startTime);
            return leaf;
        }

        // 根据最大叶子数产生叶子信息
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        // 根据传入的叶子数量产生叶子信息
        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<Leaf>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }

    }

    public void setHandler(Handler h) {
        this.mh = h;
    }

    /**
     * 设置中等振幅
     * 
     * @param amplitude
     */
    public void setMiddleAmplitude(int amplitude) {
        this.mMiddleAmplitude = amplitude;
    }

    /**
     * 设置振幅差
     * 
     * @param disparity
     */
    public void setMplitudeDisparity(int disparity) {
        this.mAmplitudeDisparity = disparity;
    }

    /**
     * 获取中等振幅
     * 
     * @param amplitude
     */
    public int getMiddleAmplitude() {
        return mMiddleAmplitude;
    }

    /**
     * 获取振幅差
     * 
     * @param disparity
     */
    public int getMplitudeDisparity() {
        return mAmplitudeDisparity;
    }

    /**
     * 设置进度
     * 
     * @param progress
     */
    public void setProgress(int progress) {
        this.mProgress = progress;
        postInvalidate();
    }

    /**
     * 设置叶子飘完一个周期所花的时间
     * 
     * @param time
     */
    public void setLeafFloatTime(long time) {
        this.mLeafFloatTime = time;
    }

    /**
     * 设置叶子旋转一周所花的时间
     * 
     * @param time
     */
    public void setLeafRotateTime(long time) {
        this.mLeafRotateTime = time;
    }

    /**
     * 获取叶子飘完一个周期所花的时间
     */
    public long getLeafFloatTime() {
        mLeafFloatTime = mLeafFloatTime == 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        return mLeafFloatTime;
    }

    /**
     * 获取叶子旋转一周所花的时间
     */
    public long getLeafRotateTime() {
        mLeafRotateTime = mLeafRotateTime == 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        return mLeafRotateTime;
    }

}
