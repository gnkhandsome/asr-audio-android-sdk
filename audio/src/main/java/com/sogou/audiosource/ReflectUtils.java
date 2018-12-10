// Copyright 2018 Sogou Inc. All rights reserved. 
// Use of this source code is governed by the Apache 2.0 
// license that can be found in the LICENSE file. 
package com.sogou.audiosource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author linyinlong
 * @create_at 2014-6-20
 */
public class ReflectUtils {
	public static int RES_STATE_OK = 0;
	public static int RES_STATE_PROPERTY_NOT_EXISTS = -1;
	public static int RES_STATE_EXCEPTION_RAISE = -2;

	@SuppressWarnings("rawtypes")
	public static Object invoke(Object obj, String methodName,
			Class[] paramTypes, Object[] params) {
		try {
			Method method = obj.getClass().getDeclaredMethod(methodName,
					paramTypes);
			method.setAccessible(true);
			return method.invoke(obj, params);
		} catch (Exception exception) {
			return null;
		}
	}

	public static int inject(Object obj, String property, Object value) {
		try {
			Field f = obj.getClass().getField(property);
			f.setAccessible(true);
			f.set(obj, value);
		} catch (NoSuchFieldException e) {
			return RES_STATE_PROPERTY_NOT_EXISTS;
		} catch (Exception e) {
			return RES_STATE_EXCEPTION_RAISE;
		}
		return RES_STATE_OK;
	}
}