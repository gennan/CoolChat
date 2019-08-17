package com.gennan.summer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.FindCallback
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage
import com.gennan.summer.R
import com.gennan.summer.activity.AddNewActivity
import com.gennan.summer.activity.ChatActivity
import com.gennan.summer.adapter.MessageAdapter
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.util.LogUtil

class MessageFragment : BaseFragment(), MessageAdapter.OnItemClickListener {
    lateinit var messageAdapter: MessageAdapter
    lateinit var newsList: RecyclerView
    lateinit var newsIsEmptyRl: RelativeLayout
    lateinit var addFriendIv: ImageView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)
        initView(view, container)
//        clientOpen()
        initEvent()
        getAVIMConversationList()//获取对话列表
        return view
    }

    private fun initEvent() {
        swipeRefreshLayout.setOnRefreshListener {
            LogUtil.d("MessageFragment", "下拉刷新中...")
            getAVIMConversationList()
        }
        addFriendIv.setOnClickListener {
            val intent = Intent(activity, AddNewActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 获取对话列表 然后把对话传入刷新对话的RecyclerView中
     */
    private fun getAVIMConversationList() {
        //todo：当有新消息的时候要通知这里重新加载对话列表 然后刷新
        val query = AVQuery<AVObject>("_Conversation")
        query.findInBackground(object : FindCallback<AVObject>() {
            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                if (avException == null) {
                    if (avObjects!!.size > 0) {
                        newsIsEmptyRl.visibility = View.GONE
                        newsList.visibility = View.VISIBLE
                        LogUtil.d("MessageFragment", "avObjects.size ----> ${avObjects.size}")
                        messageAdapter.setData(avObjects)
                        messageAdapter.notifyDataSetChanged()
                    } else {
                        newsList.visibility = View.GONE
                        newsIsEmptyRl.visibility = View.VISIBLE
                    }
                    if (swipeRefreshLayout.isRefreshing) {
                        swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
                    }
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
        swipeRefreshLayout = view!!.findViewById(R.id.swipe_fresh_layout)
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(container!!.context, R.color.colorPrimary))
        addFriendIv = view.findViewById(R.id.iv_add_friend)//点击跳转到添加好友和群聊的页面
        newsIsEmptyRl =
            view.findViewById(R.id.rl_news_is_empty_message)//数据为空时显示这个
        newsList = view.findViewById(R.id.rv_news_message)//数据不为空时显示数据
        //一些有关RecyclerView的设置
        val manager = LinearLayoutManager(container.context)
        newsList.layoutManager = manager
        messageAdapter = MessageAdapter()
        messageAdapter.setOnItemClickListener(this)
        newsList.adapter = messageAdapter

    }


    /**
     * item单击进入聊天界面
     */
    override fun onItemClick(position: Int) {
        val intent = Intent(activity, ChatActivity::class.java)
        startActivity(intent)
    }

    /**
     * 长按item 删除item
     */
    override fun onItemLongClick(position: Int) {
        LogUtil.d("MessageFragment", "长按了第${position}个item---->1")
    }

    /**
     * 用来创建对话的工具hhhh
     */
    private fun clientOpen() {
//     发送消息的简单逻辑
        CoolChatApp.avImClient?.open(object : AVIMClientCallback() {
            override fun done(client: AVIMClient?, e: AVIMException?) {
                if (e == null) {
                    LogUtil.d("MainActivity", "client.open()成功---->1")
                    CoolChatApp.avImClient?.createConversation(
                        listOf("xiaohu"),
                        "xiaohu",
                        null,
                        false,
                        true, object : AVIMConversationCreatedCallback() {
                            override fun done(conversation: AVIMConversation?, e: AVIMException?) {
                                if (e == null) {
                                    LogUtil.d("MainActivity", "创建对话成功 ----> 1")
                                    val avImTextMessage = AVIMTextMessage()
                                    avImTextMessage.text = "xiaohu 起床了"
                                    conversation?.sendMessage(avImTextMessage, object : AVIMConversationCallback() {
                                        override fun done(e: AVIMException?) {
                                            if (e == null) {
                                                LogUtil.d("MainActivity", "消息发送成功 ----> 1")
                                            } else {
                                                LogUtil.d("MainActivity", "AVIMException ----> $e")
                                            }
                                        }
                                    })
                                } else {
                                    LogUtil.d("MainActivity", "AVIMException ----> $e")
                                }
                            }
                        })
                }
            }

        })


    }
}