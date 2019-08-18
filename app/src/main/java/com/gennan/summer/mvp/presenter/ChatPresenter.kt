package com.gennan.summer.mvp.presenter

import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage
import com.gennan.summer.mvp.contract.IChatPresenter
import com.gennan.summer.mvp.contract.IChatViewCallback

/**
 *Created by Gennan on 2019/8/18.
 */
class ChatPresenter : IChatPresenter {
    companion object {
        val instance: ChatPresenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ChatPresenter() }
    }

    val callbacks = mutableListOf<IChatViewCallback>()


    override fun sendMessage(text: String, conversation: AVIMConversation) {
        val msg = AVIMTextMessage()
        msg.text = text
        conversation.sendMessage(msg, object : AVIMConversationCallback() {
            override fun done(e: AVIMException?) {
                if (e == null) {
                    for (callback in callbacks) {
                        callback.onMessageSendSucceeded()
                    }
                } else {
                    for (callback in callbacks) {
                        callback.onMessageSendFailed()
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