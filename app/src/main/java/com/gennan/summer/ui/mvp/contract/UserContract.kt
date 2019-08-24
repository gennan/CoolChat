package com.gennan.summer.ui.mvp.contract

import com.avos.avoscloud.AVObject
import com.gennan.summer.base.IBasePresenter

/**
 *Created by Gennan on 2019/8/20.
 */

interface IUserPresenter : IBasePresenter<IUserViewCallback> {
    fun queryFriendList()
}

interface IUserViewCallback {
    fun onFriendListAdded(avObject: AVObject)
    fun onFriendIsNull()
}