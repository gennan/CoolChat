package com.gennan.summer.mvp.presenter

import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.FindCallback
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.mvp.contract.IMessagePresenter
import com.gennan.summer.mvp.contract.IMessageViewCallback
import com.gennan.summer.util.LogUtil
import java.util.*

/**
 *Created by Gennan on 2019/8/17.
 */
class MessagePresenter : IMessagePresenter {
    override fun getConversationIconAndLastMessage(avObject: AVObject) {
        if (CoolChatApp.openedClient != null) {
            val conversation = CoolChatApp.openedClient!!.getConversation(avObject.objectId)
            LogUtil.d("MessageAdapter", "conversation ----> $conversation")
            for (callback in callbacks) {
                //todo:这里的图片不知道为什么加载不出url了来了 刚才还可以的 我草了...
                LogUtil.d("MessageFragment", "url ----> ${conversation.getAttribute("conversationIconUrl")}")
                callback.onConversationIconUrlLoaded("")
            }
            conversation.getLastMessage(object : AVIMSingleMessageQueryCallback() {
                override fun done(msg: AVIMMessage?, e: AVIMException?) {

                    for (callback in callbacks) {
                        callback.onConversationLastMessageLoaded(msg!!)
                    }
                }

            })
        }
    }

    val callbacks = mutableListOf<IMessageViewCallback>()

    companion object {
        val instance: MessagePresenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { MessagePresenter() }
    }

    /**
     * 获得当前用户特有的对话列表
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