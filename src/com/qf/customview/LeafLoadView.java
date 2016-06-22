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
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class LeafLoadView extends View {

    private static String TAG = "qiufei";

    private Resources mResource;
    // ����ɫ
    private static final int WHITE_COLOR = 0xfffde399;
    // ��ɫ
    private static final int ORANGE_COLOR = 0xffffa800;
    // ���ڿ��ƻ��ƵĽ������������ϣ��µľ���
    private static final int LEFT_MARGIN = 9;
    // ���ڿ��ƻ��ƵĽ����������ҵľ���
    private static final int RIGHT_MARGIN = 25;
    private int mLeftMargin, mRightMargin;

    // Ҷ��Ʈ��һ������������ʱ��
    private static final long LEAF_FLOAT_TIME = 3000;
    // Ҷ����תһ����Ҫ��ʱ��
    private static final long LEAF_ROTATE_TIME = 2000;
    // Ҷ��Ʈ��һ������������ʱ��
    private long mLeafFloatTime = LEAF_FLOAT_TIME;
    // Ҷ����תһ����Ҫ��ʱ��
    private long mLeafRotateTime = LEAF_ROTATE_TIME;
    // �е������С
    private static final int MIDDLE_AMPLITUDE = 30;
    // ��ͬ����֮���������
    private static final int AMPLITUDE_DISPARITY = 15;
    // �е������С
    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;
    // �����
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;
    // �ܽ���
    private static final int TOTAL_PROGRESS = 100;
    // ��ǰ����
    private int mProgress;
    // �����ƵĽ��������ֵĿ��
    private int mProgressWidth;
    // ���εİ뾶
    private int mArcRadius;
    private Bitmap mLeafBitmap, mLeafOuter;
    private int mLeafWidth, mLeafHeight;
    private int mOuterWidth, mOuterHeight;
    private Rect mOuterSrcRect, mOuterDestRect;
    private RectF mWhiteRectF, mOrangeRectF, mArcRectF;

    private Paint mBitmapPaint, mWhitePaint;

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
        Log.d(TAG, "on size changed, w=" + w + ", h=" + h);
        mTotalWidth = w;
        mTotalHeight = h;
        mOuterSrcRect = new Rect(0, 0, mOuterWidth, mOuterHeight);
        mOuterDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);
        mProgressWidth = mTotalWidth - mLeftMargin - mRightMargin;
        mArcRadius = (mTotalHeight - 2 * mLeftMargin) / 2;
        
        mArcRectF = new RectF(mLeftMargin, mLeftMargin, mLeftMargin+2*mArcRadius, mTotalHeight-mLeftMargin);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "leaf load view on measure");
    }

    private void drawProgress(Canvas canvas) {
        canvas.drawArc(mArcRectF, 90, 180, false, mWhitePaint);
    }

    /**
     * ����Ҷ��
     * 
     * @param canvas
     */
    private void drawLeafs(Canvas canvas) {
        mLeafRotateTime = mLeafRotateTime <= 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mLeafInfos.size(); i++) {
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                // ��ȡҶ��λ��
                getLeafLocation(leaf, currentTime);
                Log.d(TAG, "leaf x=" + leaf.x + ", leaf y=" + leaf.y);
                //
                canvas.save();
                //
                Matrix matrix = new Matrix();
                // Ҷ��λ��
                float transX = mLeftMargin + leaf.x;
                float transY = mLeftMargin + leaf.y;
                matrix.postTranslate(transX, transY);
                // Ҷ����ת������תһ�ܵ�ʱ���������
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime) / (float) mLeafRotateTime;
                Log.d(TAG, "curr-start=" + (currentTime - leaf.startTime) + ", mLeafRotateTime=" + mLeafRotateTime);
                int angle = (int) (360 * rotateFraction);
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : leaf.rotateAngle - angle;
                Log.d(TAG, "rotateFraction=" + rotateFraction + ", angle=" + angle + ", rotate=" + rotate);
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
        // y = A sin(wx + Q) + h ��������
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
        Log.d(TAG, "w=" + w + ", a=" + a + ", q=" + q);
        return (float) (a * Math.sin(w * leaf.x) + mArcRadius);
    }

    private enum StartType {
        LITTLE, MEDIUM, BIG
    }

    private class Leaf {
        // Ҷ������
        float x, y;
        // Ҷ�����
        StartType type;
        // ��ת�Ƕ�
        int rotateAngle;
        // ��ת���� 0 --˳ʱ�룬 1--��ʱ��
        int rotateDirection;
        // ���ֵ��ʹҶ�Ӵ���
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
            return leaf;
        }

        // �������Ҷ��������Ҷ����Ϣ
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        // ���ݴ����Ҷ����������Ҷ����Ϣ
        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<Leaf>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }

    }

    /**
     * �����е����
     * 
     * @param amplitude
     */
    public void setMiddleAmplitude(int amplitude) {
        this.mMiddleAmplitude = amplitude;
    }

    /**
     * ���������
     * 
     * @param disparity
     */
    public void setMplitudeDisparity(int disparity) {
        this.mAmplitudeDisparity = disparity;
    }

    /**
     * ��ȡ�е����
     * 
     * @param amplitude
     */
    public int getMiddleAmplitude() {
        return mMiddleAmplitude;
    }

    /**
     * ��ȡ�����
     * 
     * @param disparity
     */
    public int getMplitudeDisparity() {
        return mAmplitudeDisparity;
    }

    /**
     * ���ý���
     * 
     * @param progress
     */
    public void setProgress(int progress) {
        this.mProgress = progress;
        postInvalidate();
    }

    /**
     * ����Ҷ��Ʈ��һ������������ʱ��
     * 
     * @param time
     */
    public void setLeafFloatTime(long time) {
        this.mLeafFloatTime = time;
    }

    /**
     * ����Ҷ����תһ��������ʱ��
     * 
     * @param time
     */
    public void setLeafRotateTime(long time) {
        this.mLeafRotateTime = time;
    }

    /**
     * ��ȡҶ��Ʈ��һ������������ʱ��
     */
    public long getLeafFloatTime() {
        mLeafFloatTime = mLeafFloatTime == 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        return mLeafFloatTime;
    }

    /**
     * ��ȡҶ����תһ��������ʱ��
     */
    public long getLeafRotateTime() {
        mLeafRotateTime = mLeafRotateTime == 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        return mLeafRotateTime;
    }

}
