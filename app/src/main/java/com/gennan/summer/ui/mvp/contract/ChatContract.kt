package com.gennan.summer.ui.mvp.contract

import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.base.IBasePresenter
import com.gennan.summer.ui.mvp.model.bean.AVIMMessageBean

/**
 *Created by Gennan on 2019/8/18.
 */
interface IChatPresenter : IBasePresenter<IChatViewCallback> {
    /**
     * 发送图片消息
     */
    fun sendImgMessage(avimImageMessage: AVIMImageMessage, conversation: AVIMConversation)

    /**
     * 发送文字消息
     */
    fun sendTextMessage(text: String, conversation: AVIMConversation)

    /**
     * 获取历史消息
     */
    fun getMsgHistory(oldestMsg: AVIMMessage, messageNumber: Int, conversation: AVIMConversation)

    /**
     * 第一次进入对话获取前十条消息
     */
    fun getFirstTenMsg(messageNumber: Int, conversation: AVIMConversation)

    /**
     * 开始录取语音
     */
    fun startRecordAudio(activity: BaseActivity)
    /**
     * 停止录取语音
     */
    fun stopRecordAudio()

    /**
     * 发送语音消息
     */
    fun sendAudioMessage(msg: AVIMAudioMessage, conversation: AVIMConversation)

    /**
     * 开始播放语音
     */
    fun playReceivedAudioMessage(msgBean: AVIMMessageBean)

    /**
     * 停止播放语音
     */
    fun stopReceivedAudioMessage()
}

interface IChatViewCallback {
    /**
     * 语音消息发送成功的回调
     */
    fun onAudioMessageSendSucceeded(msg: AVIMMessage)

    /**
     * 语音消息发送失败的回调
     */
    fun onAudioMessageSendFailed()

    /**
     * 文字消息发送成功的回调
     */
    fun onTextMessageSendSucceeded(msg: AVIMMessage)

    /**
     * 文字消息发送失败的回调
     */
    fun onTextMessageSendFailed()

    /**
     * 图片消息发送成功的回调
     */
    fun onImgMessageSendSucceeded(msg: AVIMMessage)

    /**
     * 图片消息发送失败的回调
     */
    fun onImgMessageSendFailed()

    /**
     * 获取语音的文件路径的回调
     */
    fun onAudioFilePathGetSucceeded(filePath: String)

    /**
     * 下拉刷新成功的回调
     */
    fun onHistoryMsgLoadedSucceeded(messages: MutableList<AVIMMessage>?, oldestMsg: AVIMMessage)

    /**
     * 下拉刷新失败的回调
     */
    fun onHistoryMsgLoadedFailed(e: AVIMException?)

    /**
     * 下拉刷新的消息为空的回调
     */
    fun onHistoryMsgIsNull()

    /**
     * 第一次进入ChatActivity加载历史记录成功的回调
     */
    fun onFirstTenMsgLoadedSucceeded(messages: MutableList<AVIMMessage>?, oldestMsg: AVIMMessage)
    /**
     * 第一次进入ChatActivity加载历史记录失败的回调
     */
    fun onFirstTenMsgLoadedFailed(e: AVIMException?)
    /**
     * 第一次进入ChatActivity加载历史记录为空的回调
     */
    fun onFirstTenMsgIsNull()
}