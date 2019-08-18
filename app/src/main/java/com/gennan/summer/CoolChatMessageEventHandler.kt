package com.gennan.summer

import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.AVIMMessageHandler
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/18.
 */
class CoolChatMessageEventHandler : AVIMMessageHandler {
    private val TAG = "CoolChatMessageEventHandler"

    constructor()

    override fun onMessage(message: AVIMMessage?, conversation: AVIMConversation?, client: AVIMClient?) {
        super.onMessage(message, conversation, client)
        LogUtil.d(TAG, "收到新消息了")
    }
}