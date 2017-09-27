package com.virginiatech.slapdash.slapdash.HelperClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by nima on 1/27/17.
 * <p>
 * Includes compatibility wrapper for all the function in andorid that have
 * been deprecated and have been used throughout the project.
 */

public class CompatibilityHelper {
    /////////////////////////////////////////////////////////////////////////////////////////////
    //                               Public Static Methods                                     //
    /////////////////////////////////////////////////////////////////////////////////////////////
    public static Drawable getDrawable(Context context, int resourceId) {
        Drawable newDraw = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            newDraw = context.getResources().getDrawable(
                    resourceId, context.getTheme());
        } else {
            newDraw = context.getResources().getDrawable(resourceId);
        }
        return newDraw;
    }

    //-------------------------------------------------------------------------------------------
    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    //-------------------------------------------------------------------------------------------
    public static void setTime(TimePicker timePicker, int hourOfDay, int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hourOfDay);
            timePicker.setMinute(minute);
        } else {
            timePicker.setCurrentHour(hourOfDay);
            timePicker.setCurrentMinute(minute);
        }
    }
}
