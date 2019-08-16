package com.gennan.summer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gennan.summer.R
import com.gennan.summer.adapter.MessageAdapter
import com.gennan.summer.base.BaseFragment

class MessageFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)
        val addFriendIv: ImageView = view.findViewById(R.id.iv_add_friend)//点击跳转到添加好友和群聊的页面
        val newsIsEmptyRl: RelativeLayout =
            view.findViewById(R.id.rl_news_is_empty_message)//数据为空时显示这个
        val newsList: RecyclerView = view.findViewById(R.id.rv_news_message)//数据不为空时显示数据

        //一些有关RecyclerView的设置
        val manager = LinearLayoutManager(container?.context)
        newsList.layoutManager = manager
        val messageAdapter = MessageAdapter()
        newsList.adapter = messageAdapter


        //todo：如何获得消息列表 然后显示 并实时刷新 然后又是如何点击进入聊天界面
//        addFriendIv.setOnClickListener {
//            LogUtil.d("MessageFragment", "AppAVUser---->${CoolChatApp.avUser}")
//            LogUtil.d("MessageFragment", "AppAVIMClient---->${CoolChatApp.avImClient}")
//        }
        return view
    }


    private fun clientOpen() {
//     发送消息的简单逻辑
//        CoolChatApp.avImClient?.open(object : AVIMClientCallback() {
//            override fun done(client: AVIMClient?, e: AVIMException?) {
//                if (e == null) {
//                    LogUtil.d("MainActivity", "client.open()成功---->1")
//                    CoolChatApp.avImClient?.createConversation(
//                        listOf("xiaohu"),
//                        "zz",
//                        null,
//                        false,
//                        true, object : AVIMConversationCreatedCallback() {
//                            override fun done(conversation: AVIMConversation?, e: AVIMException?) {
//                                if (e == null) {
//                                    LogUtil.d("MainActivity", "创建对话成功 ----> 1")
//                                    val avImTextMessage = AVIMTextMessage()
//                                    avImTextMessage.text = "xiaohu 起床了"
//                                    conversation?.sendMessage(avImTextMessage, object : AVIMConversationCallback(){
//                                        override fun done(e: AVIMException?) {
//                                            if(e==null){
//                                                LogUtil.d("MainActivity","消息发送成功 ----> 1")
//                                            }else{
//                                                LogUtil.d("MainActivity","AVIMException ----> $e")
//                                            }
//                                        }
//                                    })
//                                } else {
//                                    LogUtil.d("MainActivity", "AVIMException ----> $e")
//                                }
//                            }
//                        })
//                }
//            }
//
//        })


    }
}