package com.aop.demo

import com.aop.aspect.KtDebugLog
import com.aop.base.LogUtils.d

class KtAspectTest {
    @KtDebugLog
    fun test(input: String) {
        d("hello $input")
    }
}