package com.gennan.summer.mvp.contract

import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage
import com.gennan.summer.base.IBasePresenter

/**
 *Created by Gennan on 2019/8/18.
 */
interface IChatPresenter : IBasePresenter<IChatViewCallback> {
    fun sendMessage(text: String, conversation: AVIMConversation)
}

interface IChatViewCallback {
    fun onMessageSendSucceeded()
    fun onMessageSendFailed()
}