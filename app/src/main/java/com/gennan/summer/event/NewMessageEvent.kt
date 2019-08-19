package com.gennan.summer.event

import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMMessage

/**
 *Created by Gennan on 2019/8/19.
 */
class NewMessageEvent(val message: AVIMMessage,
                      val conversation: AVIMConversation,
                      val client: AVIMClient)