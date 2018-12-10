// Copyright 2018 Sogou Inc. All rights reserved. 
// Use of this source code is governed by the Apache 2.0 
// license that can be found in the LICENSE file. 
package com.sogou.audiosource;

/**
 * Created by zhouqilin on 16/10/28.
 */

public class NewBufferFactory {
    public static class ByteBufferFactory implements IBufferFactory{
        @Override
        public Object newBuffer(int length) {
            return new byte[length];
        }
    }

    public static class ShortBufferFactory implements IBufferFactory{
        @Override
        public Object newBuffer(int length) {
            return new short[length];
        }
    }
}