package com.aop.proxy

import java.lang.reflect.Proxy

object ProxyFactory {
    fun getProxyInstance(target: ProxyInterface): ProxyInterface {
        return Proxy.newProxyInstance((DynamicProxy::class as Any).javaClass.classLoader,
            target::class.java.interfaces,
            DynamicProxy(target)) as ProxyInterface
    }
}