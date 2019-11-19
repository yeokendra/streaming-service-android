package com.nontivi.nonton.util;

import android.os.SystemClock;

/**
 * Created by mac on 11/16/17.
 */

public class ClickUtil {
    private static long sLastClickTime = 0L;

    public static boolean isFastDoubleClick() {
        long nowTime = SystemClock.elapsedRealtime();
        if ((nowTime - sLastClickTime) < 250) {
            return true;
        } else {
            sLastClickTime = nowTime;
            return false;
        }
    }
}
