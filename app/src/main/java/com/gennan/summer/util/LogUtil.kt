package com.gennan.summer.util

import android.util.Log

class LogUtil {
    companion object {
        //在BaseApplication中初始化就可以控制是否输出Log了
        var sTAG = "LogUtil"

        //控制是否要输出Log
        var sIsRelease = false


        //如果是要发布了，可以在application里面把这里release改一下，这样子就没有Log输出了

        fun init(baseTag: String, isRelease: Boolean) {
            sTAG = baseTag
            sIsRelease = isRelease
        }

        fun d(TAG: String, content: String) {
            if (!sIsRelease) {
                Log.d("[$sTAG]$TAG", content)
            }
        }

        fun v(TAG: String, content: String) {
            if (!sIsRelease) {
                Log.v("[$sTAG]$TAG", content)
            }
        }

        fun i(TAG: String, content: String) {
            if (!sIsRelease) {
                Log.i("[$sTAG]$TAG", content)
            }
        }

        fun w(TAG: String, content: String) {
            if (!sIsRelease) {
                Log.w("[$sTAG]$TAG", content)
            }
        }

        fun e(TAG: String, content: String) {
            if (!sIsRelease) {
                Log.e("[$sTAG]$TAG", content)
            }
        }
    }
}