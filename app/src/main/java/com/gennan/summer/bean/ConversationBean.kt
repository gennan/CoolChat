package com.gennan.summer.bean

/**
 *Created by Gennan on 2019/8/17.
 */
class ConversationBean(
    val conversationId: String,//对话唯一的objectId 可以用这个id通过getConversation来获得id
    val iconUrl: String, //该对话的头像Url  ----> 单聊的话就为聊天用户的url  群聊的话就默认头像就完事了
    val conversationName: String, //对话的名称  ----> 单聊的话就聊天用户的用户名 群聊的话就看自己起的是什么就好了
    val lastMessage: String,//最后的消息 这个可以通过获得的conversation利用queryMessage来获得聊天记录 最好进行分页 可以节省流量
    val lastTime: String,//最后收到消息的时间
    val userName: String//用户名 这个名字可以用于在ChatActivity再点击要查看个人详细资料的时候通过名字获得User使用
)