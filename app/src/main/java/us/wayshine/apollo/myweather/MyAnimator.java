package us.wayshine.apollo.myweather;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.view.View;

/**
 * Created by Apollo on 9/29/15.
 */
public class MyAnimator {

    static public void fadeIn(View v, int delay) {
        ObjectAnimator mAnimator;
        v.setAlpha(0);
        mAnimator = ObjectAnimator.ofFloat(v, "alpha", 0, 1);
        mAnimator.setDuration(300);
        mAnimator.setStartDelay(delay);
        mAnimator.start();
    }

    static public void deflateFadeIn(View v, int delay) {
        ObjectAnimator mAnimator1, mAnimator2;
        Path path = new Path();
        path.moveTo(1.2f, 1.2f);
        path.lineTo(1, 1);
        v.setAlpha(0);
        mAnimator1 = ObjectAnimator.ofFloat(v, "alpha", 0, 1);
        mAnimator2 = ObjectAnimator.ofFloat(v, View.SCALE_X, View.SCALE_Y, path);
        mAnimator1.setDuration(300);
        mAnimator2.setDuration(300);
        mAnimator1.setStartDelay(delay);
        mAnimator2.setStartDelay(delay);
        mAnimator1.start();
        mAnimator2.start();
    }

    static public void expandFadeIn(View v, int delay) {
        ObjectAnimator mAnimator1, mAnimator2;
        Path path = new Path();
        path.moveTo(1f, 0f);
        path.lineTo(1, 1);
        v.setAlpha(0);
        mAnimator1 = ObjectAnimator.ofFloat(v, "alpha", 0, 1);
        mAnimator2 = ObjectAnimator.ofFloat(v, View.SCALE_X, View.SCALE_Y, path);
        mAnimator1.setDuration(300);
        mAnimator2.setDuration(300);
        mAnimator1.setStartDelay(delay);
        mAnimator2.setStartDelay(delay);
        mAnimator1.start();
        mAnimator2.start();
    }
}
