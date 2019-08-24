package com.gennan.summer.ui.mvvm.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avos.avoscloud.*
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/22.
 */
class StatusSendViewModel : ViewModel() {
    val TAG = "StatusSendViewModel"
    var sendStatusLiveData = MutableLiveData<String>()
    var savedUrlLiveData = MutableLiveData<String>()

    /**
     * 发送动态
     */
    fun sendStatus(textMessage: String, imageUrl: String) {
        val status = AVStatus()
        status.imageUrl = imageUrl
        status.message = textMessage
        status.data = mutableMapOf(Pair("username", AVUser.getCurrentUser().username)) as Map<String, Any>?
        status.data = mutableMapOf(Pair("iconUrl", AVUser.getCurrentUser().getString("iconUrl"))) as Map<String, Any>?
        AVStatus.sendStatusToFollowersInBackgroud(status, object : SaveCallback() {
            override fun done(e: AVException?) {
                if (e == null) {
                    LogUtil.d(TAG, "动态发送成功")
                    sendStatusLiveData.value = "动态发送成功"
                } else {
                    LogUtil.d(TAG, "动态发送失败 ----> $e")
                    sendStatusLiveData.value = "动态发送失败"
                }
            }
        })
    }

    /**
     * 储存图像文件
     */
    fun saveFile(file: AVFile) {
        file.saveInBackground(object : SaveCallback() {
            override fun done(e: AVException?) {
                if (e == null) {
                    LogUtil.d(TAG, "储存图像成功")
                    savedUrlLiveData.value = file.url
                } else {
                    LogUtil.d(TAG, "储存图像失败")
                    savedUrlLiveData.value = ""
                }
            }
        })
    }
}