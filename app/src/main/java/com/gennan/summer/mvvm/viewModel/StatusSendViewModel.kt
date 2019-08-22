package com.gennan.summer.mvvm.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVFile
import com.avos.avoscloud.AVStatus
import com.avos.avoscloud.SaveCallback
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/22.
 */
class StatusSendViewModel : ViewModel() {
    val TAG = "StatusSendViewModel"
    var sendStatusLiveData = MutableLiveData<String>()
    var savedUrlLiveData = MutableLiveData<String>()

    fun sendStatus(textMessage: String, imageUrl: String) {
        val status = AVStatus()
        status.imageUrl = imageUrl
        status.message = textMessage
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