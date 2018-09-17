package com.android.dongnao.lsn7task2;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

/**
 * Created by user on 2017/5/20.
 */

public class ViewController implements MySearchView.DetachedViewLinstenner {

    private MySearchView vSearchView;

    // 动画默认执行时间
    public final static int DEFAULT_DURATION = 600;

    public float mAnimatedValue;

    public ViewController() {

    }

    public void setSearchView(MySearchView serachView) {
        // MyserarchView和Controller互相绑定
        vSearchView = serachView;
        serachView.addController(this);
        serachView.setDetachedViewLinstenner(this);
    }

    public void open() {
        // 开始执行动画
        startAnimation(MySearchView.ANIMAL_STATE_OPEN);
    }

    public void close() {
        startAnimation(MySearchView.ANIMAL_STATE_CLOSE);
    }

    private void startAnimation(@MySearchView.AnimalState int state) {
        // 开始执行动画
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1.0f);
        animator.setDuration(DEFAULT_DURATION);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatedValue = (float) animation.getAnimatedValue();
                if (vSearchView != null) {
                    vSearchView.invalidate();
                }
            }
        });
        if (vSearchView != null) {
            vSearchView.setAnimaState(state);
        }
        animator.start();
    }

    @Override
    public void onDetachedView() {
        vSearchView = null;
    }
}
