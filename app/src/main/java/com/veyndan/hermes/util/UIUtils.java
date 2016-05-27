package com.veyndan.hermes.util;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

public class UIUtils {

    /**
     * Gets the screen width in pixels.
     *
     * @return Pixel count.
     */
    public static int getScreenWidth(@NonNull AppCompatActivity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

}
