package com.mredrock.cyxbs.freshman.lin.util

import android.content.Context

/**
 * Created By Gennan on 2019/7/15.
 * dp转成px的工具类
 */
object Dip2pxUtil {
    fun dip2px(context: Context, dpValue: Double): Int {
        val density = context.resources.displayMetrics.density
        return (dpValue * density + 0.5).toInt()
    }
}