package com.aop.aspect

import android.os.Build
import android.os.Looper
import android.os.Trace
import android.util.Log
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.CodeSignature
import org.aspectj.lang.reflect.MethodSignature
import java.util.concurrent.TimeUnit

@Aspect
class KtDebugLogImpl {

    companion object {
        @Volatile
        private var enabled = true
    }

    @Pointcut("within(@com.aop.aspect.KtDebugLog *)")
    fun withinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    fun methodInsideAnnotatedType() {
    }

    @Pointcut("execution(!synthetic *.new(..)) && withinAnnotatedClass()")
    fun constructorInsideAnnotatedType() {
    }

    @Pointcut("execution(@com.aop.aspect.KtDebugLog * *(..)) || methodInsideAnnotatedType()")
    fun method() {
    }

    @Pointcut("execution(@com.aop.aspect.KtDebugLog *.new(..)) || constructorInsideAnnotatedType()")
    fun constructor() {
    }

    fun setEnabled(enable: Boolean) {
        enabled = enable
    }

    @Around("method() || constructor()")
    @Throws(Throwable::class)
    fun logAndExecute(joinPoint: ProceedingJoinPoint): Any? {
        enterMethod(joinPoint)
        val startNanos = System.nanoTime()
        val result = joinPoint.proceed()
        val stopNanos = System.nanoTime()
        val lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos)
        exitMethod(joinPoint, result, lengthMillis)
        return result
    }

    private fun enterMethod(joinPoint: JoinPoint) {
        if (!enabled) return
        val codeSignature = joinPoint.signature as CodeSignature
        val cls = codeSignature.declaringType
        val methodName = codeSignature.name
        val parameterNames = codeSignature.parameterNames
        val parameterValues = joinPoint.args
        val builder = StringBuilder("\u21E2 ")
        builder.append(methodName).append('(')
        for (i in parameterValues.indices) {
            if (i > 0) {
                builder.append(", ")
            }
            builder.append(parameterNames[i]).append('=')
            builder.append(Strings.toString(parameterValues[i]))
        }
        builder.append(')')
        if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append(" [Thread:\"").append(Thread.currentThread().name).append("\"]")
        }
        Log.v(asTag(cls), builder.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val section = builder.toString().substring(2)
            Trace.beginSection(section)
        }
    }

    private fun exitMethod(joinPoint: JoinPoint, result: Any?, lengthMillis: Long) {
        if (!enabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.endSection()
        }
        val signature = joinPoint.signature
        val cls = signature.declaringType
        val methodName = signature.name
        val hasReturnType = (signature is MethodSignature
                && signature.returnType != Void.TYPE)
        val builder = StringBuilder("\u21E0 ")
            .append(methodName)
            .append(" [")
            .append(lengthMillis)
            .append("ms]")
        if (hasReturnType) {
            builder.append(" = ")
            builder.append(Strings.toString(result))
        }
        Log.v(asTag(cls), builder.toString())
    }

    private fun asTag(cls: Class<*>): String? {
        return if (cls.isAnonymousClass) {
            asTag(cls.enclosingClass)
        } else cls.simpleName
    }
}