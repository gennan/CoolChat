package com.gennan.summer

import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.AVIMMessageHandler
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.event.NewMessageEvent
import com.gennan.summer.util.Constants.Companion.NOTIFICATION_NEW_MESSAGE
import com.gennan.summer.util.LogUtil
import com.gennan.summer.util.NotificationUtil

/**
 *Created by Gennan on 2019/8/18.
 */
class CoolChatMessageEventHandler : AVIMMessageHandler() {
    private val TAG = "CoolChatMessageEventHandler"

    override fun onMessage(message: AVIMMessage?, conversation: AVIMConversation?, client: AVIMClient?) {
        super.onMessage(message, conversation, client)
        CoolChatApp.getAppEventBus().postSticky(NewMessageEvent(message!!, conversation!!, client!!))
        LogUtil.d(TAG, "收到新消息了")
        LogUtil.d(TAG, "message ----> $message")
        LogUtil.d(TAG, "conversation ----> $conversation")
        LogUtil.d(TAG, "client ----> $client")
    }
}