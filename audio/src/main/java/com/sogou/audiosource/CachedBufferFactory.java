// Copyright 2018 Sogou Inc. All rights reserved. 
// Use of this source code is governed by the Apache 2.0 
// license that can be found in the LICENSE file. 
package com.sogou.audiosource;

/**
 * Created by zhouqilin on 16/10/28.
 */

public class CachedBufferFactory {
    public static abstract class BufferFactoryBase implements IBufferFactory{
        private Object mCachedBuffer;
        private int mCurrentLength;

        public BufferFactoryBase(int initialSize){
            mCurrentLength = initialSize;
            mCachedBuffer = createBufferArray(initialSize);
        }

        @Override
        public Object newBuffer(int length) {
            if (mCurrentLength >= length){
                return mCachedBuffer;
            }
            mCachedBuffer = createBufferArray(length);
            mCurrentLength = length;
            return mCachedBuffer;
        }

        protected abstract Object createBufferArray(int length);
    }

    public static class ByteBufferFactory extends BufferFactoryBase {

        public ByteBufferFactory(int initialSize) {
            super(initialSize);
        }

        @Override
        protected Object createBufferArray(int length) {
            return new byte[length];
        }
    }

    public static class ShortBufferFactory extends BufferFactoryBase {

        public ShortBufferFactory(int initialSize) {
            super(initialSize);
        }

        @Override
        protected Object createBufferArray(int length) {
            return new short[length];
        }
    }
}