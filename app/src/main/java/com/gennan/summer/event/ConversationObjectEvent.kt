package com.gennan.summer.event

import com.avos.avoscloud.im.v2.AVIMConversation

/**
 *Created by Gennan on 2019/8/18.
 */
class ConversationObjectEvent(
//    val conversationList: MutableList<AVObject>,
//    val position: Int
    val conversation: AVIMConversation?
)