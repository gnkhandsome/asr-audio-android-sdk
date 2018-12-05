package com.sogou.audiosource;

import android.os.Build;

/**
 * Created by admin on 2017/10/26.
 */

public class SDKUtils {
    public static final boolean isSumsung5() {
        return "Samsung".equalsIgnoreCase(Build.MANUFACTURER)&&21==Build.VERSION.SDK_INT;
//        return "Samsung".equalsIgnoreCase(Build.MANUFACTURER);
    }
}
