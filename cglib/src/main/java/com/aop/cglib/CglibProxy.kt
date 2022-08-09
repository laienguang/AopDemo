package com.aop.cglib

import com.aop.base.LogUtils
import com.aop.cglib.proxy.MethodInterceptor
import com.aop.cglib.proxy.MethodProxy

import kotlin.system.measureTimeMillis

class CglibProxy: MethodInterceptor {

    override fun intercept(obj: Any, args: Array<out Any>?, methodProxy: MethodProxy): Any {
        var result: Any
        val takeTime = measureTimeMillis {
            result = methodProxy.invokeSuper(obj, args)
        }
        LogUtils.d("CglibProxy method=${methodProxy.methodName} takeTime:$takeTime")
        return result
    }
}