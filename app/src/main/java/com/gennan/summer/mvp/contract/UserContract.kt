package com.gennan.summer.mvp.contract

import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVUser
import com.gennan.summer.base.IBasePresenter

/**
 *Created by Gennan on 2019/8/20.
 */

interface IUserPresenter : IBasePresenter<IUserViewCallback> {
    fun queryConversationList()
    fun queryFriendList()
}

interface IUserViewCallback {
    fun onConversationListLoaded(avObjects: MutableList<AVObject>)
    fun onFriendListLoaded(avObjects: MutableList<AVUser>)
}