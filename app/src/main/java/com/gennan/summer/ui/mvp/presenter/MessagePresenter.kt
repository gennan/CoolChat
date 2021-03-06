package com.gennan.summer.ui.mvp.presenter

import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.FindCallback
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.ui.mvp.contract.IMessagePresenter
import com.gennan.summer.ui.mvp.contract.IMessageViewCallback
import com.gennan.summer.util.LogUtil
import java.util.*

/**
 *Created by Gennan on 2019/8/17.
 */
class MessagePresenter : IMessagePresenter {
    val TAG = "MessagePresenter"
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
                    LogUtil.d(TAG, "avException ----> $avException")
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