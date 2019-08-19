package com.gennan.summer.mvp.presenter

import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage
import com.gennan.summer.mvp.contract.IChatPresenter
import com.gennan.summer.mvp.contract.IChatViewCallback
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/18.
 */
class ChatPresenter : IChatPresenter {
    override fun sendImgMessage(avimImageMessage: AVIMImageMessage, conversation: AVIMConversation) {
        conversation.sendMessage(avimImageMessage, object : AVIMConversationCallback() {
            override fun done(e: AVIMException?) {
                if (e == null) {
                    for (callback in callbacks) {
                        callback.onImgMessageSendSucceeded(avimImageMessage)
                    }
                } else {
                    for (callback in callbacks) {
                        LogUtil.d("zz","AVIMException----> $e")
                        callback.onImgMessageSendFailed()
                    }
                }
            }

        })
    }

    var oldestMessage: AVIMMessage = AVIMMessage()

    val callbacks = mutableListOf<IChatViewCallback>()

    companion object {
        val instance: ChatPresenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ChatPresenter() }
    }

    override fun getMsgHistory(oldestMsg: AVIMMessage, messageNumber: Int, conversation: AVIMConversation) {
        conversation.queryMessages(
            oldestMsg.messageId,
            oldestMsg.timestamp,
            messageNumber,
            object : AVIMMessagesQueryCallback() {
                override fun done(messages: MutableList<AVIMMessage>?, e: AVIMException?) {
                    if (e == null) {
                        if (messages?.size != 0) {
                            for (callback in callbacks) {
                                callback.onHistoryMsgLoadedSucceeded(messages, messages!![0])
                            }
                        } else {
                            for (callback in callbacks) {
                                callback.onHistoryMsgIsNull()
                            }
                        }
                    } else {
                        for (callback in callbacks) {
                            callback.onHistoryMsgLoadedFailed(e)
                        }
                    }
                }
            })
    }

    override fun getFirstTenMsg(messageNumber: Int, conversation: AVIMConversation) {
        conversation.queryMessages(messageNumber, object : AVIMMessagesQueryCallback() {
            override fun done(messages: MutableList<AVIMMessage>?, e: AVIMException?) {
                if (e == null) {
                    if (messages?.size != 0) {
                        for (callback in callbacks) {
                            callback.onFirstTenMsgLoadedSucceeded(messages, messages!![0])
                        }
                    } else {
                        for (callback in callbacks) {
                            callback.onFirstTenMsgIsNull()
                        }
                    }
                } else {
                    for (callback in callbacks) {
                        callback.onFirstTenMsgLoadedFailed(e)
                    }
                }
            }
        })
    }

    override fun sendTextMessage(text: String, conversation: AVIMConversation) {
        val msg = AVIMTextMessage()
        msg.text = text
        conversation.sendMessage(msg, object : AVIMConversationCallback() {
            override fun done(e: AVIMException?) {
                if (e == null) {
                    for (callback in callbacks) {
                        callback.onTextMessageSendSucceeded(msg)
                    }
                } else {
                    for (callback in callbacks) {
                        callback.onTextMessageSendFailed()
                    }
                }
            }
        })
    }

    override fun attachViewCallback(t: IChatViewCallback) {
        callbacks.add(t)
    }

    override fun unAttachViewCallback(t: IChatViewCallback) {
        callbacks.remove(t)
    }
}