package com.gennan.summer

import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/18.
 */
class CoolChatConversationEventHandler : AVIMConversationEventHandler {


    private val TAG = "CoolChatConversationEventHandler"

    constructor()

    override fun onMemberJoined(
        client: AVIMClient?,
        conversation: AVIMConversation?,
        members: MutableList<String>?,
        invitedBy: String?
    ) {
        LogUtil.d(TAG, "有新的成员加入")
    }

    override fun onKicked(client: AVIMClient?, conversation: AVIMConversation?, kickedBy: String?) {
        LogUtil.d(TAG, "被踢了")
    }

    override fun onMemberLeft(
        client: AVIMClient?,
        conversation: AVIMConversation?,
        members: MutableList<String>?,
        kickedBy: String?
    ) {
        LogUtil.d(TAG, "有成员离开了")
    }

    override fun onInvited(client: AVIMClient?, conversation: AVIMConversation?, operator: String?) {
        LogUtil.d(TAG, "被邀请了")
    }
}