package com.gennan.summer.ui.mvp.contract

import com.avos.avoscloud.AVObject
import com.gennan.summer.base.IBasePresenter

/**
 *Created by Gennan on 2019/8/17.
 */

interface IMessagePresenter : IBasePresenter<IMessageViewCallback> {
    fun queryConversationList()
}

interface IMessageViewCallback {
    fun onLoadConversationListHaveNumbers(mutableList: MutableList<AVObject>)
    fun onLoadConversationListHaveNoNumbers()
    fun onConversationIconUrlLoaded(url: String)
}