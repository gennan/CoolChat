package com.gennan.summer.mvp.contract

import com.avos.avoscloud.AVObject
import com.avos.avoscloud.im.v2.AVIMMessage
import com.gennan.summer.base.IBasePresenter

/**
 *Created by Gennan on 2019/8/17.
 */

interface IMessagePresenter : IBasePresenter<IMessageViewCallback> {
    fun queryConversationList()
    fun getConversationIconAndLastMessage(avObject:AVObject)
}

interface IMessageViewCallback {
    fun onLoadConversationListHaveNumbers(mutableList: MutableList<AVObject>)
    fun onLoadConversationListHaveNoNumbers()
    fun onConversationIconUrlLoaded(url: String)
    fun onConversationLastMessageLoaded(msg: AVIMMessage)
}