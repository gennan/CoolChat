package com.gennan.summer.mvp.contract

import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage
import com.gennan.summer.base.IBasePresenter

/**
 *Created by Gennan on 2019/8/18.
 */
interface IChatPresenter : IBasePresenter<IChatViewCallback> {
    fun sendImgMessage(avimImageMessage: AVIMImageMessage, conversation: AVIMConversation)
    fun sendTextMessage(text: String, conversation: AVIMConversation)
    fun getMsgHistory(oldestMsg: AVIMMessage, messageNumber: Int, conversation: AVIMConversation)
    fun getFirstTenMsg(messageNumber: Int, conversation: AVIMConversation)
}

interface IChatViewCallback {
    fun onTextMessageSendSucceeded(msg: AVIMMessage)
    fun onTextMessageSendFailed()
    fun onImgMessageSendSucceeded(msg: AVIMMessage)
    fun onImgMessageSendFailed()
    fun onHistoryMsgLoadedSucceeded(messages: MutableList<AVIMMessage>?, oldestMsg: AVIMMessage)
    fun onHistoryMsgLoadedFailed(e: AVIMException?)
    fun onHistoryMsgIsNull()
    fun onFirstTenMsgLoadedSucceeded(messages: MutableList<AVIMMessage>?, oldestMsg: AVIMMessage)
    fun onFirstTenMsgLoadedFailed(e: AVIMException?)
    fun onFirstTenMsgIsNull()
}