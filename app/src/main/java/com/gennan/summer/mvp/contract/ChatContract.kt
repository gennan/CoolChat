package com.gennan.summer.mvp.contract

import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.gennan.summer.base.IBasePresenter

/**
 *Created by Gennan on 2019/8/18.
 */
interface IChatPresenter : IBasePresenter<IChatViewCallback> {
    fun sendMessage(text: String, conversation: AVIMConversation)
    fun getMsgHistory(oldestMsg: AVIMMessage, messageNumber: Int, conversation: AVIMConversation)
    fun getFirstTenMsg(messageNumber: Int, conversation: AVIMConversation)
}

interface IChatViewCallback {
    fun onMessageSendSucceeded(msg: AVIMMessage)
    fun onMessageSendFailed()
    fun onHistoryMsgLoadedSucceeded(messages: MutableList<AVIMMessage>?, oldestMsg: AVIMMessage)
    fun onHistoryMsgLoadedFailed(e: AVIMException?)
    fun onHistoryMsgIsNull()
    fun onFirstTenMsgLoadedSucceeded(messages: MutableList<AVIMMessage>?, oldestMsg: AVIMMessage)
    fun onFirstTenMsgLoadedFailed(e: AVIMException?)
    fun onFirstTenMsgIsNull()
}