package com.gennan.summer.ui.mvvm.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVFile
import com.avos.avoscloud.SaveCallback
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.util.LogUtil


/**
 *Created by Gennan on 2019/8/22.
 */
class SettingViewModel : ViewModel() {

    val TAG = "SettingViewModel"
    var changeAvatarLiveData = MutableLiveData<String>()

    /**
     * 上传图片文件并更换头像 SDK里的subscribe()方法都没了
     */
    fun saveImageFileAndChangeAvatar(file: AVFile) {
        file.saveInBackground(object : SaveCallback() {
            override fun done(e: AVException?) {
                file.saveInBackground(object : SaveCallback() {
                    override fun done(e: AVException?) {
                        if (e == null) {
                            LogUtil.d(TAG, "保存文件成功")
                            val url = file.url//我草了 这谁想的到还可以这样子拿url的啊
                            CoolChatApp.avUser?.put("iconUrl", url)
                            CoolChatApp.avUser?.saveInBackground(object : SaveCallback() {
                                override fun done(e: AVException?) {
                                    if (e == null) {
                                        changeAvatarLiveData.value = "头像更换成功"
                                    } else {
                                        LogUtil.d(TAG, "更换头像失败 ----> $e")
                                    }
                                }
                            })
                        } else {
                            LogUtil.d(TAG, "保存图像失败 ----> $e")
                        }
                    }
                })
            }
        })
    }
}