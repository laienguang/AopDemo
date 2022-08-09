package com.aop.base

import android.util.Log

object LogUtils {
    private const val TAG = "AOPDemo"

    fun e(msg: String): Int {
        return Log.e(TAG, msg)
    }

    fun e(msg: String?, tr: Throwable?): Int {
        return Log.e(TAG, msg, tr)
    }

    fun d(msg: String) {
        Log.d(TAG, msg)
    }
}