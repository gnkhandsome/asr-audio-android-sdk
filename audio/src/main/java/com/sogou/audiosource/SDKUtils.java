// Copyright 2018 Sogou Inc. All rights reserved. 
// Use of this source code is governed by the Apache 2.0 
// license that can be found in the LICENSE file. 
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