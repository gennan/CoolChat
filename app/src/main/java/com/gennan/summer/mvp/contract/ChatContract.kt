package com.gennan.summer.mvp.contract

import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.base.IBasePresenter
import com.gennan.summer.bean.AVIMMessageBean

/**
 *Created by Gennan on 2019/8/18.
 */
interface IChatPresenter : IBasePresenter<IChatViewCallback> {
    fun sendImgMessage(avimImageMessage: AVIMImageMessage, conversation: AVIMConversation)
    fun sendTextMessage(text: String, conversation: AVIMConversation)
    fun getMsgHistory(oldestMsg: AVIMMessage, messageNumber: Int, conversation: AVIMConversation)
    fun getFirstTenMsg(messageNumber: Int, conversation: AVIMConversation)
    fun startRecordAudio(activity: BaseActivity)
    fun stopRecordAudio()
    fun sendAudioMessage(msg: AVIMAudioMessage, conversation: AVIMConversation)
    fun playReceivedAudioMessage(msgBean: AVIMMessageBean)
    fun stopReceivedAudioMessage()
}

interface IChatViewCallback {
    fun onAudioMessageSendSucceeded(msg: AVIMMessage)
    fun onAudioMessageSendFailed()
    fun onTextMessageSendSucceeded(msg: AVIMMessage)
    fun onTextMessageSendFailed()
    fun onImgMessageSendSucceeded(msg: AVIMMessage)
    fun onImgMessageSendFailed()
    fun onAudioFilePathGetSucceeded(filePath: String)
    fun onHistoryMsgLoadedSucceeded(messages: MutableList<AVIMMessage>?, oldestMsg: AVIMMessage)
    fun onHistoryMsgLoadedFailed(e: AVIMException?)
    fun onHistoryMsgIsNull()
    fun onFirstTenMsgLoadedSucceeded(messages: MutableList<AVIMMessage>?, oldestMsg: AVIMMessage)
    fun onFirstTenMsgLoadedFailed(e: AVIMException?)
    fun onFirstTenMsgIsNull()
}