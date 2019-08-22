package com.gennan.summer.util

/**
 *Created by Gennan on 2019/8/22.
 * 用来控制短时间内多次进行的点击事件
 */
object ClickUtil {
    // 两次点击按钮之间的点击间隔不能少于500毫秒
    private val MIN_CLICK_DELAY_TIME = 500
    private var lastClickTime: Long = 0

    fun isFastClick(): Boolean {
        var flag = false
        val curClickTime = System.currentTimeMillis()
        if (curClickTime - lastClickTime >= MIN_CLICK_DELAY_TIME) {
            flag = true
        }
        lastClickTime = curClickTime
        return flag
    }

}