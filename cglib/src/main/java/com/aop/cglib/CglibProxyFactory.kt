package com.aop.cglib

import android.content.Context
import com.aop.cglib.proxy.Enhancer

object CglibProxyFactory {
    fun <T : Any> getProxyInstance(context: Context, target: T): T {
        return getProxyInstanceInner(context, target) as T
    }

    private fun getProxyInstanceInner(context: Context, target: Any): Any {
        val enhancer = Enhancer(context).apply {
            setSuperclass(target.javaClass)
            setCallback(CglibProxy())
        }
        return enhancer.create()
    }
}