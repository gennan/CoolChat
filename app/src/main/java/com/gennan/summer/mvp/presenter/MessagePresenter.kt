package com.gennan.summer.mvp.presenter

import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.FindCallback
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.mvp.contract.IMessagePresenter
import com.gennan.summer.mvp.contract.IMessageViewCallback
import com.gennan.summer.util.LogUtil
import java.util.*

/**
 *Created by Gennan on 2019/8/17.
 */
class MessagePresenter : IMessagePresenter {
    override fun getConversationIconAndLastMessage(list: MutableList<AVObject>) {
        if (CoolChatApp.openedClient != null) {
            for (avObject in list) {
                val conversation =
                    CoolChatApp.openedClient!!.getConversation(avObject.objectId)
                //获取最后一条消息
                conversation.queryMessages(1, object : AVIMMessagesQueryCallback() {
                    override fun done(messages: MutableList<AVIMMessage>?, e: AVIMException?) {
                        for (callback in callbacks) {
                            if (messages != null && messages.size != 0) {
                                if (messages[0].content != null) {
                                    callback.onConversationLastMessageLoaded(messages[0])
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    val callbacks = mutableListOf<IMessageViewCallback>()

    companion object {
        val instance: MessagePresenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { MessagePresenter() }
    }

    /**
     * 获得当前用户拥有的对话列表
     */
    override fun queryConversationList() {
        val query = AVQuery<AVObject>("_Conversation")
        query.whereContainsAll("m", Arrays.asList(CoolChatApp.avUser?.username))//查询和当前用户有关的对话
        query.findInBackground(object : FindCallback<AVObject>() {
            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                if (avException == null) {
                    if (avObjects!!.size > 0) {
                        for (callback in callbacks) {
                            callback.onLoadConversationListHaveNumbers(avObjects)
                        }
                    } else {
                        for (callback in callbacks) {
                            callback.onLoadConversationListHaveNoNumbers()
                        }
                    }
                } else {
                    LogUtil.d("MessageFragment", "avException ----> $avException")
                }
            }
        })
    }

    override fun attachViewCallback(t: IMessageViewCallback) {
        callbacks.add(t)
    }

    override fun unAttachViewCallback(t: IMessageViewCallback) {
        callbacks.remove(t)
    }
}