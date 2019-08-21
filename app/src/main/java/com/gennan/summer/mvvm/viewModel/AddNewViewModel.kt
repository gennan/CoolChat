package com.gennan.summer.mvvm.viewModel

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
    // var addConversationLiveData = MutableLiveData<String>()
//    var createConversaionLiveData = MutableLiveData<String>()
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
                    LogUtil.d(TAG, "avException ----> $avException")
                }
            }
        })
    }
//    fun addNewConversation(conversationName: String) {
//        val query = AVQuery<AVObject>("_Conversation")
//        query.whereEqualTo("name", conversationName)
//        query.findInBackground(object : FindCallback<AVObject>() {
//            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
//                if (avException == null) {
//                    //无bug
//                    val conversationObjectId = avObjects!![0].objectId
//                    val conversationMembers = avObjects[0].getList("m")
//                    val memberSize = conversationMembers.size
//                    LogUtil.d(TAG, "conversationObjectId ----> $conversationObjectId")
//                    LogUtil.d(TAG, "conversationMembers ----> $conversationMembers")
//                    LogUtil.d(TAG, "memberSize ----> $memberSize")
//                    if (memberSize > 2) {
//                        //这才是群聊
//                        val conversation = CoolChatApp.openedClient?.getConversation(conversationObjectId)
//                        conversation?.join(object : AVIMConversationCallback() {
//                            override fun done(e: AVIMException?) {
//                                if (e == null) {
//                                    addConversationLiveData.value = "添加群聊成功"
//                                } else {
//                                    addConversationLiveData.value = "添加群聊失败"
//                                }
//                            }
//                        })
//                    } else {
//                        LogUtil.d(TAG, "conversation的人数小于2 这不能算是群聊")
//                    }
//                } else {
//                    LogUtil.d(TAG, "avException ----> $avException")
//                }
//            }
//        })
//    }
//    fun createNewConversation(userList: List<String>) {
//        CoolChatApp.openedClient?.createConversation(
//            userList,
//            userList.toString(),
//            null,
//            object : AVIMConversationCreatedCallback() {
//                override fun done(conversation: AVIMConversation?, e: AVIMException?) {
//                    if (e == null) {
//                        createConversaionLiveData.value = "创建群聊成功"
//                    } else {
//                        createConversaionLiveData.value = "创建群聊失败"
//                    }
//                }
//            })
//    }
}