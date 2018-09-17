package com.android.dongnao.lsn7task2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by user on 2017/5/20.
 */

public class MySearchView extends View {

    public final static int ANIMAL_STATE_NONE = 0;
    public final static int ANIMAL_STATE_OPEN = 1;
    public final static int ANIMAL_STATE_CLOSE = 2;

    @IntDef({ANIMAL_STATE_NONE, ANIMAL_STATE_OPEN, ANIMAL_STATE_CLOSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimalState {
    }

    @AnimalState
    private int mAnimalState = ANIMAL_STATE_NONE;

    private ViewController mController;

    // 控件大小
    private int mViewWidth;
    private int mViewHeight;

    // 圆弧半径
    private int mRadio;

    // 中心坐标
    private int mCenterX;
    private int mCenterY;

    // 手柄末端初始坐标
    private float lineX;
    private float lineY;

    // 画笔
    private Paint mPaint;

    // 圆弧区域
    private RectF mArcRect;

    // 线条颜色
    private int mColor = Color.WHITE;

    private DetachedViewLinstenner mDetachedViewLinstenner;

    public MySearchView(Context context) {
        super(context, null);
    }

    public MySearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        // 根据控件宽度算出圆弧半径
        mRadio = mViewWidth / 16;

        // 中心点坐标
        mCenterX = mViewWidth / 2;
        mCenterY = mViewHeight / 2;

        mArcRect = new RectF(mCenterX - mRadio, mCenterY - mRadio, mCenterX + mRadio, mCenterY + mRadio);
    }

    private void drawDefault(Canvas canvas) {
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.save();
        canvas.rotate(45f, mCenterX, mCenterY);
        canvas.drawArc(mArcRect, 0f, 360f, false, mPaint);
        lineX = mArcRect.right + mRadio;
        lineY = mCenterY;
        canvas.drawLine(mArcRect.right, mCenterY, mArcRect.right + mRadio, mCenterY, mPaint);

        canvas.translate(lineX, lineY);
        canvas.rotate(-45f);
        canvas.restore();
    }

    private void drawOpen(Canvas canvas) {
        final float radio = mController.mAnimatedValue;

        mArcRect.left = (mCenterX - mRadio) + (mViewWidth - (mCenterX + mRadio)) * 1.0f * 2.0f / 3 * radio;
        mArcRect.right = mArcRect.left + mRadio * 2;

        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.save();
        // 前一半时间,这段时间内圆弧完成消失
        if (radio <= 0.5f) {
            canvas.drawArc(mArcRect, 45f, -(360f * (1.0f - radio * 2)), false, mPaint);
            // 绘图坐标系先旋转了45度，这个时候手柄的Y值就不用变了
            canvas.rotate(45f, mArcRect.left + mRadio, mCenterY);
            canvas.drawLine(mArcRect.right, mCenterY, mArcRect.right + mRadio, mCenterY, mPaint);
        }
        // 后一半时间，手柄从最大到完全消失
        else {
            // 先旋转绘图坐标系45度，这个时候就只需要改变手柄的长度就可以了
            canvas.rotate(45f, mArcRect.left + mRadio, mCenterY);
            canvas.drawLine(mArcRect.right + mRadio * 2 * (radio - 0.5f), mCenterY, mArcRect.right + mRadio, mCenterY, mPaint);
        }
        canvas.restore();

        canvas.save();

        // 回到手柄末端起点，向两边画线
        canvas.rotate(45f, mCenterX, mCenterY);
        canvas.translate(lineX, lineY);
        canvas.rotate(-45f);
        // 画右边,最后的总长度为圆弧区域X轴移动的总长度
        canvas.drawLine(0, 0, mArcRect.right - (mCenterX + mRadio), 0, mPaint);
        // 画左边，最后的总长为控件的左边减去一定的值
        canvas.drawLine(0, 0, -(mCenterX - 20) * radio, 0, mPaint);

        canvas.restore();
    }

    private void drawClose(Canvas canvas) {
        // 1 - 0之间变化
        final float radio = 1.0f - mController.mAnimatedValue;

        mArcRect.left = (mCenterX - mRadio) + (mViewWidth - (mCenterX + mRadio)) * 1.0f * 2.0f / 3 * radio;
        mArcRect.right = mArcRect.left + mRadio * 2;

        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.save();
        // 后一半时间,这段时间内圆弧从完成消失到完全显示
        if (radio <= 0.5f) {
            canvas.drawArc(mArcRect, 45f, -(360f * (1.0f - radio * 2)), false, mPaint);
            // 绘图坐标系先旋转了45度，这个时候手柄的Y值就不用变了
            canvas.rotate(45f, mArcRect.left + mRadio, mCenterY);
            canvas.drawLine(mArcRect.right, mCenterY, mArcRect.right + mRadio, mCenterY, mPaint);
        }
        // 后一半时间，手柄从完全消失到最大
        else {
            // 先旋转绘图坐标系45度，这个时候就只需要改变手柄的长度就可以了
            canvas.rotate(45f, mArcRect.left + mRadio, mCenterY);
            canvas.drawLine(mArcRect.right + mRadio * 2 * (radio - 0.5f), mCenterY, mArcRect.right + mRadio, mCenterY, mPaint);
        }
        canvas.restore();

        canvas.save();

        // 回到手柄末端起点，向两边画线
        canvas.rotate(45f, mCenterX, mCenterY);
        canvas.translate(lineX, lineY);
        canvas.rotate(-45f);
        // 画右边,从最大长度到0
        canvas.drawLine(0, 0, mArcRect.right - (mCenterX + mRadio), 0, mPaint);
        // 画左边，从最大长度到0
        canvas.drawLine(0, 0, -(mCenterX - 20) * radio, 0, mPaint);

        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mAnimalState) {
            case ANIMAL_STATE_NONE:
                drawDefault(canvas);
                break;
            case ANIMAL_STATE_OPEN:
                drawOpen(canvas);
                break;
            case ANIMAL_STATE_CLOSE:
                drawClose(canvas);
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // View被Detached的时候移除Controller
        mController = null;
    }

    public void addController(ViewController controller) {
        mController = controller;
        if (mDetachedViewLinstenner != null) {
            mDetachedViewLinstenner.onDetachedView();
        }
    }

    public int getAnimaState() {
        return mAnimalState;
    }

    public void setAnimaState(@AnimalState int animaState) {
        this.mAnimalState = animaState;
    }

    public void setColor(@ColorInt int color) {
        mColor = color;
    }

    public void setDetachedViewLinstenner(DetachedViewLinstenner listenner) {
        mDetachedViewLinstenner = listenner;
    }

    public interface DetachedViewLinstenner {
        void onDetachedView();
    }
}
