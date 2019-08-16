package com.gennan.summer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.FindCallback
import com.gennan.summer.R
import com.gennan.summer.adapter.MessageAdapter
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.util.LogUtil

class MessageFragment : BaseFragment() {

    lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)
        initView(view, container)
        getAVIMConversationList()//获取对话列表
        return view
    }

    private fun getAVIMConversationList() {
        val query = AVQuery<AVObject>("_Conversation")
        query.findInBackground(object : FindCallback<AVObject>() {
            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                if (avException == null) {
                    LogUtil.d("MessageFragment", "avObjects.size ----> ${avObjects?.size}")
                    messageAdapter.setData(avObjects!!)
                    messageAdapter.notifyDataSetChanged()
                } else {
                    LogUtil.d("MessageFragment", "avException ----> $avException")
                }
            }
        })
    }

    /**
     * 找到MessageFragment里的一些控件实例
     */
    private fun initView(view: View?, container: ViewGroup?) {
        val addFriendIv: ImageView = view!!.findViewById(R.id.iv_add_friend)//点击跳转到添加好友和群聊的页面
        val newsIsEmptyRl: RelativeLayout =
            view.findViewById(R.id.rl_news_is_empty_message)//数据为空时显示这个
        val newsList: RecyclerView = view.findViewById(R.id.rv_news_message)//数据不为空时显示数据
        //一些有关RecyclerView的设置
        newsList.visibility = View.VISIBLE
        val manager = LinearLayoutManager(container?.context)
        newsList.layoutManager = manager
        messageAdapter = MessageAdapter()
        newsList.adapter = messageAdapter
//        if (conversationList.size > 0) {
//            newsIsEmptyRl.visibility = View.GONE
//            newsList.visibility = View.VISIBLE
//            newsList.adapter = messageAdapter
//        } else {
//            newsList.visibility = View.GONE
//            newsIsEmptyRl.visibility = View.VISIBLE
//        }
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