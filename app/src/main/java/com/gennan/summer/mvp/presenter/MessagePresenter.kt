package com.gennan.summer.mvp.presenter

import android.widget.Toast
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.FindCallback
import com.gennan.summer.mvp.contract.IMessagePresenter
import com.gennan.summer.mvp.contract.IMessageViewCallback
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/17.
 */
class MessagePresenter : IMessagePresenter {

    val callbacks = mutableListOf<IMessageViewCallback>()

    companion object {
        val instance: MessagePresenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { MessagePresenter() }
    }

    /**
     * 获得当前用户特有的对话列表
     */
    override fun queryConversationList() {
        val query = AVQuery<AVObject>("_Conversation")
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