package com.aop.proxy

import com.aop.base.LogUtils
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import kotlin.system.measureTimeMillis

class DynamicProxy(proxy: ProxyInterface) : InvocationHandler {
    private var inner: ProxyInterface = proxy

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        var result: Any
        val takeTime = measureTimeMillis {
            if (args == null) {
                result = method.invoke(inner)
            } else {
                result = method.invoke(inner, *args)
            }
        }
        LogUtils.d("DynamicProxy method=${method.name} takeTime:$takeTime")
        return result
    }
}