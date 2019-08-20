package com.gennan.summer.mvvm.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avos.avoscloud.*
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/20.
 */
class AddNewViewModel : ViewModel() {
    var addNewFriendLiveData = MutableLiveData<String>()

    val TAG = "AddNewViewModel"
    fun addNewFriend(userName: String) {
        val query = AVQuery<AVObject>("_User")
        query.whereEqualTo("username", userName)
        query.findInBackground(object : FindCallback<AVObject>() {
            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                if (avException == null) {
                    val userObjectId = avObjects!![0].objectId
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
                    LogUtil.d(TAG, "avException ----> $avException")
                }
            }
        })


    }

    fun addNewConversation(conversationName: String) {
        val query = AVQuery<AVObject>("_Conversation")
        query.whereEqualTo("name", conversationName)
        query.findInBackground(object : FindCallback<AVObject>() {
            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                if (avException == null) {
                    //无bug
                    val conversationObjectId = avObjects!![0].objectId
                    val conversationMembers = avObjects[0].getList("m")
                    val memberSize = conversationMembers.size
                    LogUtil.d(TAG, "conversationObjectId ----> $conversationObjectId")
                    LogUtil.d(TAG, "conversationMembers ----> $conversationMembers")
                    LogUtil.d(TAG, "membersize ----> $memberSize")

                    //todo:获取到对话的objectId后就可以开始去加入那个conversation了
                } else {
                    LogUtil.d(TAG, "avException ----> $avException")
                }
            }

        })

    }

    fun createNewConversation(userArray: List<String>) {

    }

}