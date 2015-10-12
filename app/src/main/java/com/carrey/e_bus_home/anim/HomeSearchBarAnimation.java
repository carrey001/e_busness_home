package com.carrey.e_bus_home.anim;

import android.util.DisplayMetrics;
import android.view.View;

import com.carrey.e_bus_home.MainActivity;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by sunning on 15/8/18.
 */
public class HomeSearchBarAnimation {

    private int bannerHeight;

    private int width;

    private View view;

    private MainActivity homeFragment;

    private boolean isShow;


    public HomeSearchBarAnimation(MainActivity homeFragment, View view) {
        this.view = view;
        this.homeFragment = homeFragment;
        DisplayMetrics dm = new DisplayMetrics();
        homeFragment.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.width = dm.widthPixels;
        setViewParam();
        init();
    }

    private void setViewParam() {
        bannerHeight =  width / 2 ;
    }

    private void init() {
        view.post(new Runnable() {
            @Override
            public void run() {
                scrollHeaderTo(0);
            }
        });
    }

    public void doAnimateShow() {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            isShow = true;
        }
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "alpha", 0f, 1));
        set.setDuration(1 * 500).start();
    }

    public void doAnimateHide() {
        if (view.getVisibility() != View.INVISIBLE) {
            view.setVisibility(View.INVISIBLE);
            isShow = false;
        }
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "alpha", 1f, 0));
        set.setDuration(1 * 500).start();
    }

    public void scrollHeaderTo(int scrollTo) {
        notifyOnHeaderScrollChangeListener((float) -scrollTo / bannerHeight, bannerHeight, -scrollTo);
    }

    private void notifyOnHeaderScrollChangeListener(float progress, int height, int scroll) {
        progress = (float) scroll / (height - homeFragment.getHomeTitle().getHeight());
        if (progress > 1f) {
            progress = 1f;
        }
        homeFragment.getFadingHelper().setHeadViewAlpha((int) (255 * progress));
    }


    public boolean isShow() {
        return isShow;
    }
}
