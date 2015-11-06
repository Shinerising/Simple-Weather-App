package us.wayshine.apollo.myweather;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Created by Apollo on 9/29/15.
 */
public class MyAnimator {
    final static int ANIMATION_DURATION = 300;

    static public void fadeIn(View v, int delay) {
        ObjectAnimator mAnimator;
        v.setVisibility(View.VISIBLE);
        v.setAlpha(0);
        mAnimator = ObjectAnimator.ofFloat(v, "alpha", 0, 1);
        mAnimator.setDuration(ANIMATION_DURATION);
        mAnimator.setStartDelay(delay);
        mAnimator.start();
    }

    static public void fadeOut(final View v, int delay) {
        ObjectAnimator mAnimator;
        mAnimator = ObjectAnimator.ofFloat(v, "alpha", 1, 0);
        mAnimator.setDuration(ANIMATION_DURATION);
        mAnimator.setStartDelay(delay);
        mAnimator.start();
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    static public void deflateFadeIn(View v, int delay) {
        ObjectAnimator mAnimator1, mAnimator2;
        Path path = new Path();
        path.moveTo(1.2f, 1.2f);
        path.lineTo(1, 1);
        v.setAlpha(0);
        mAnimator1 = ObjectAnimator.ofFloat(v, "alpha", 0, 1);
        mAnimator2 = ObjectAnimator.ofFloat(v, View.SCALE_X, View.SCALE_Y, path);
        mAnimator1.setDuration(ANIMATION_DURATION);
        mAnimator2.setDuration(ANIMATION_DURATION);
        mAnimator1.setStartDelay(delay);
        mAnimator2.setStartDelay(delay);
        mAnimator1.start();
        mAnimator2.start();
    }

    static public void flowFadeIn(View v, int delay) {
        ObjectAnimator mAnimator1, mAnimator2;
        Path path = new Path();
        path.moveTo(20, 6);
        path.lineTo(0, 0);
        v.setAlpha(0);
        mAnimator1 = ObjectAnimator.ofFloat(v, "alpha", 0, 1);
        mAnimator2 = ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, View.TRANSLATION_Z, path);
        mAnimator1.setDuration(ANIMATION_DURATION);
        mAnimator2.setDuration(ANIMATION_DURATION);
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
        mAnimator1.setDuration(ANIMATION_DURATION);
        mAnimator2.setDuration(ANIMATION_DURATION);
        mAnimator1.setStartDelay(delay);
        mAnimator2.setStartDelay(delay);
        mAnimator1.start();
        mAnimator2.start();
    }

    static public void inflate(View v, int delay) {
        ObjectAnimator mAnimator1;
        Path path = new Path();
        path.moveTo(v.getScaleX(), v.getScaleY());
        path.lineTo(1.5f, 1.5f);
        mAnimator1 = ObjectAnimator.ofFloat(v, View.SCALE_X, View.SCALE_Y, path);
        mAnimator1.setDuration(ANIMATION_DURATION);
        mAnimator1.setStartDelay(delay);
        mAnimator1.start();
    }

    static public void deflate(View v, int delay) {
        ObjectAnimator mAnimator1;
        Path path = new Path();
        path.moveTo(v.getScaleX(), v.getScaleY());
        path.lineTo(1, 1);
        mAnimator1 = ObjectAnimator.ofFloat(v, View.SCALE_X, View.SCALE_Y, path);
        mAnimator1.setDuration(ANIMATION_DURATION);
        mAnimator1.setStartDelay(delay);
        mAnimator1.start();
    }

    static public void rotate(View v, float angel, int delay) {
        ObjectAnimator mAnimator1;
        mAnimator1 = ObjectAnimator.ofFloat(v, View.ROTATION, v.getRotation(), angel);
        mAnimator1.setDuration(ANIMATION_DURATION);
        mAnimator1.setStartDelay(delay);
        mAnimator1.start();
    }

    static public void flowIn(View v, int delay) {
        ObjectAnimator mAnimator1;
        Path path = new Path();
        path.moveTo(v.getX(), v.getY() + 100);
        path.lineTo(v.getX(), v.getY());
        v.setY(v.getY() + 100);
        mAnimator1 = ObjectAnimator.ofFloat(v, View.X, View.Y, path);
        mAnimator1.setDuration(ANIMATION_DURATION);
        mAnimator1.setStartDelay(delay);
        mAnimator1.start();
    }

    static public void windowOpen(View v, View v0, int delay) {

        int finalRadius = Math.max(v.getWidth(), v.getHeight());

        int cx = (v0.getLeft() + v0.getRight()) / 2;
        int cy = (v0.getTop() + v0.getBottom()) / 2;

        Animator anim =
                ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
        anim.setDuration(ANIMATION_DURATION);
        anim.setStartDelay(delay);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        v.setVisibility(View.VISIBLE);
        v.setAlpha(1);
        anim.start();

    }
}
