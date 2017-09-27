package com.virginiatech.slapdash.slapdash;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Nelson on 9/30/2016.
 *
 * For disabling the ability to swipe between fragments
 */
public class NonSwipeableViewPager extends ViewPager {
    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                   Constructors                                          //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public NonSwipeableViewPager(Context context) {
        super(context);
    }
    //-------------------------------------------------------------------------------------------

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                       Overrides                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }
}
