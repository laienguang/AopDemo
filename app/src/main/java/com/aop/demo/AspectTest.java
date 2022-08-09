package com.aop.demo;

import com.aop.aspect.DebugLog;
import com.aop.base.LogUtils;

public class AspectTest {

    @DebugLog
    public final void test(String input) {
        LogUtils.INSTANCE.d("hello " + input);
    }
}
