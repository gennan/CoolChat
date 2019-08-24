package com.gennan.summer.ui.mvp.presenter

import com.avos.avoscloud.*
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.ui.mvp.contract.IUserPresenter
import com.gennan.summer.ui.mvp.contract.IUserViewCallback
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

    /**
     * 查询用户的好友列表 sdk有毒 只能疯狂魔改了
     */
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
                                        LogUtil.d(TAG, "查询用户失败 ----> $avException")
                                    }
                                }
                            })
                        }
                    } else if (avObjects?.size == 0) {
                        LogUtil.d(TAG, "返回的对话数量为空 ----> 0")
                        for (callback in callbacks) {
                            callback.onFriendIsNull()
                        }
                    }
                } else {
                    LogUtil.d(TAG, "查询好友列表失败 ----> $avException")
                }
            }
        })
    }


    override fun attachViewCallback(t: IUserViewCallback) {
        callbacks.add(t)
    }

    override fun unAttachViewCallback(t: IUserViewCallback) {
        callbacks.remove(t)
    }
}