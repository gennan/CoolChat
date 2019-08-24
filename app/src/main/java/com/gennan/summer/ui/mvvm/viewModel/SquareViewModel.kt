package com.gennan.summer.ui.mvvm.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVStatus
import com.avos.avoscloud.FindCallback
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/22.
 */
class SquareViewModel : ViewModel() {
    val TAG = "SquareViewModel"
    var getStatusLiveData = MutableLiveData<MutableList<AVStatus>>()
    var notGetStatusLiveData = MutableLiveData<String>()

    fun getRecentStatus() {
        val inboxQuery = AVStatus.inboxQuery(CoolChatApp.avUser, AVStatus.INBOX_TYPE.TIMELINE.toString())
        inboxQuery.limit = 50
        inboxQuery.sinceId = 0
        inboxQuery.findInBackground(object : FindCallback<AVStatus>() {
            override fun done(avStatus: MutableList<AVStatus>?, avException: AVException?) {
                if (avException == null) {
                    if (avStatus?.size!! > 0) {
                        LogUtil.d(TAG, "AVStatus ----> ${avStatus.size}")//查看下第一个数据
                        getStatusLiveData.value = avStatus
                    } else {
                        notGetStatusLiveData.value ="你的广场还没有动态"
                        LogUtil.d(TAG, "返回来的状态列表的数据为0")
                    }
                } else {
                    LogUtil.d(TAG, "查询动态失败 ----> $avException")
                }
            }
        })
    }
}