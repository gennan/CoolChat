package com.gennan.summer.event

import com.avos.avoscloud.im.v2.AVIMConversation

/**
 *Created by Gennan on 2019/8/18.
 */
class ConversationObjectEvent(
    val conversation: AVIMConversation?
)
//从MessageFragment点击进入ChatActivity的event