package com.cw.fatmuscle.view.widget;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.cw.fatmuscle.view.MeasureView;

public class FlashHelper {
    public static void startFlick(final View view) {
        if (view == null) {
            return;
        }
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(1000L);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(3);
        alphaAnimation.setRepeatMode(2);
        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.marvoto.fat.widget.FlashHelper.1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
                ((MeasureView) view).setShowUltrasoundImg(false);
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                ((MeasureView) view).setShowUltrasoundImg(true);
            }
        });
    }

    public static void stopFlick(View view) {
        if (view == null) {
            return;
        }
        view.clearAnimation();
    }

}
