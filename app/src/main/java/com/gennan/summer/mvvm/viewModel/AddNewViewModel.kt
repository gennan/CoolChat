package com.gennan.summer.mvvm.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avos.avoscloud.*
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/20.
 */
class AddNewViewModel : ViewModel {
    var liveData = MutableLiveData<String>()

    constructor()

    val TAG = "AddNewViewModel"
    fun addNewFriend(userName: String) {
        val query = AVQuery<AVObject>("_User")
        query.whereEqualTo("username", userName)
        query.findInBackground(object : FindCallback<AVObject>() {
            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                val userObjectId = avObjects!![0].objectId
                LogUtil.d(TAG, "objectId ----> $userObjectId")
                AVUser.getCurrentUser().followInBackground(userObjectId, object : FollowCallback<AVObject>() {
                    override fun done(avObject: AVObject?, e: AVException?) {
                        when {
                            e == null -> {
                                liveData.value = "添加好友成功"
                                LogUtil.d(TAG, "添加好友成功")
                            }
                            e.code == AVException.DUPLICATE_VALUE -> {
                                liveData.value = "此人已经是你好友了"
                                LogUtil.d(TAG, "此人已经是你好友了")
                            }
                            else -> {
                                liveData.value = "添加好友失败"
                                LogUtil.d(TAG, "添加好友失败 ----> $e")
                            }
                        }
                    }
                })
            }
        })


    }

    fun addNewConversation() {

    }

    fun createNewConversation(userArray: List<String>) {

    }

}