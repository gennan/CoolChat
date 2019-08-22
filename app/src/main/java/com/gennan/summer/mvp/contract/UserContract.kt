package com.gennan.summer.mvp.contract

import com.avos.avoscloud.AVObject
import com.gennan.summer.base.IBasePresenter

/**
 *Created by Gennan on 2019/8/20.
 *
 */

interface IUserPresenter : IBasePresenter<IUserViewCallback> {
    //    fun queryConversationList()
    fun queryFriendList()
}

interface IUserViewCallback {
    //    fun onConversationListLoaded(avObjects: MutableList<AVObject>)
    fun onFriendListAdded(avObject: AVObject)

    fun onFriendIsNull()
}