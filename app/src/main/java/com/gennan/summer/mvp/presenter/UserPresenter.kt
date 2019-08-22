package com.gennan.summer.mvp.presenter

import com.avos.avoscloud.*
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.mvp.contract.IUserPresenter
import com.gennan.summer.mvp.contract.IUserViewCallback
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/20.
 */
class UserPresenter : IUserPresenter {
    val TAG = "UserPresenter"
    val callbacks = mutableListOf<IUserViewCallback>()

    companion object {
        val instance: UserPresenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { UserPresenter() }
    }

    override fun queryFriendList() {
        val friendQuery = CoolChatApp.avUser?.followeeQuery(AVUser::class.java)
        friendQuery?.findInBackground(object : FindCallback<AVUser>() {
            override fun done(avObjects: MutableList<AVUser>?, avException: AVException?) {
                if (avException == null) {
                    //这个是所有与我有关的好友列表
                    if (avObjects != null && avObjects.size > 0) {
                        for (i in 0 until avObjects.size) {
                            val query = AVQuery<AVObject>("_User")
                            query.whereEqualTo("objectId", avObjects[i].objectId)
                            query.findInBackground(object : FindCallback<AVObject>() {
                                override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                                    if (avException == null) {
                                        if (avObjects != null && avObjects.size > 0) {
                                            for (callback in callbacks) {
                                                callback.onFriendListAdded(avObjects[0])
                                            }
                                        }
                                    } else {
                                        LogUtil.d(TAG, "avException ----> $avException")
                                    }
                                }
                            })
                        }
                    } else if (avObjects?.size == 0) {
                        LogUtil.d(TAG, "返回的对话数量为空")
                        for (callback in callbacks) {
                            callback.onFriendIsNull()
                        }
                    }
                } else {
                    LogUtil.d(TAG, "avException ----> $avException")
                }
            }
        })
    }

    /**
     * 获取群聊对话列表
     */
//    override fun queryConversationList() {
//        val query = AVQuery<AVObject>("_Conversation")
//        //查询和当前用户有关的对话
//        query.whereContainsAll("m", Arrays.asList(CoolChatApp.avUser?.username))
//        query.findInBackground(object : FindCallback<AVObject>() {
//            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
//                if (avException == null) {
//                    if (avObjects!!.size > 0) {
//                        //这个是所有与我有关的对话列表 我们只需要取成员大于2的部分 即群聊部分
//                        for (callback in callbacks) {
//                            callback.onConversationListLoaded(avObjects)
//                        }
//                    } else {
//                        //查询的对话列表为空
//                    }
//                } else {
//                    LogUtil.d("UserPresenter", "avException ----> $avException")
//                }
//            }
//        })
//    }

    override fun attachViewCallback(t: IUserViewCallback) {
        callbacks.add(t)
    }

    override fun unAttachViewCallback(t: IUserViewCallback) {
        callbacks.remove(t)
    }
}