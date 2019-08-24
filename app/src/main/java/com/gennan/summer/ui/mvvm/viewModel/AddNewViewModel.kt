package com.gennan.summer.ui.mvvm.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avos.avoscloud.*
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/20.
 */
class AddNewViewModel : ViewModel() {
    val TAG = "AddNewViewModel"
    var addNewFriendLiveData = MutableLiveData<String>()

    /**
     * 添加好友
     */
    fun addNewFriend(userName: String) {
        val query = AVQuery<AVObject>("_User")
        query.whereEqualTo("username", userName)
        query.findInBackground(object : FindCallback<AVObject>() {
            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                if (avException == null) {
                    if (avObjects != null && avObjects.size > 0) {
                        val userObjectId = avObjects[0].objectId
                        LogUtil.d(TAG, "objectId ----> $userObjectId")
                        AVUser.getCurrentUser().followInBackground(userObjectId, object : FollowCallback<AVObject>() {
                            override fun done(avObject: AVObject?, e: AVException?) {
                                when {
                                    e == null -> {
                                        addNewFriendLiveData.value = "添加好友成功"
                                        LogUtil.d(TAG, "添加好友成功")
                                    }
                                    e.code == AVException.DUPLICATE_VALUE -> {
                                        addNewFriendLiveData.value = "此人已经是你好友了"
                                        LogUtil.d(TAG, "此人已经是你好友了")
                                    }
                                    else -> {
                                        addNewFriendLiveData.value = "添加好友失败"
                                        LogUtil.d(TAG, "添加好友失败 ----> $e")
                                    }
                                }
                            }
                        })
                    } else {
                        addNewFriendLiveData.value ="不存在此用户"
                    }
                } else {
                    LogUtil.d(TAG, "查询用户失败 ----> $avException")
                }
            }
        })
    }
}