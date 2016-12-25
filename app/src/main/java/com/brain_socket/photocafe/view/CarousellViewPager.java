package com.brain_socket.photocafe.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CarousellViewPager extends ViewPager {

    private boolean isSlidingEnabled = true;

    public CarousellViewPager(Context context) {
        super(context);
        init();
    }

    public CarousellViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //setPageTransformer(false, new RotationPageTransformer(0));
        setPageTransformer(false, new FadePageTransformer());
    }

    public void setSlidingEnabled(Boolean val) {
        isSlidingEnabled = val;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if(isSlidingEnabled)
            return super.onInterceptTouchEvent(arg0);
        else
            return false ;
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (isSlidingEnabled) {
            return super.onTouchEvent(arg0);
        } else {
            return false;
        }
    }

    public class FadePageTransformer implements ViewPager.PageTransformer{
        private float minAlpha = 0.5f;
        private float minScale = 0.6f;

        public void transformPage(View view, float position){
            position -=0.3;

            view.setAlpha( minAlpha + (1.0f-minAlpha));
            float  scale = minScale + (1.0f-minScale) ;
            view.setScaleX(scale);
            view.setScaleY(scale);

//            if(position < -1){
//                view.setAlpha(minAlpha);
//                view.setScaleX(minScale);
//                view.setScaleY(minScale);
//            }else if(position <= 1){
//                view.setAlpha( minAlpha + (1.0f-minAlpha)* (1-Math.abs(position)) );
//                float  scale = minScale + (1.0f-minScale)* (1-Math.abs(position)) ;
//                view.setScaleX(scale);
//                view.setScaleY(scale);
//            }else{
//                view.setAlpha(minAlpha);
//                view.setScaleX(minScale);
//                view.setScaleY(minScale);
//            }
        }

    }

}