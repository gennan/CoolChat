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
import com.avos.avoscloud.AVObject
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
import com.gennan.summer.mvp.contract.IMessageViewCallback
import com.gennan.summer.mvp.presenter.MessagePresenter
import com.gennan.summer.util.LogUtil

class MessageFragment : BaseFragment(), MessageAdapter.OnItemClickListener, IMessageViewCallback {
    /**
     * 返回的对话列表不为空的回调
     */
    override fun onLoadConversationListHaveNumbers(mutableList: MutableList<AVObject>) {
        newsIsEmptyRl.visibility = View.GONE
        newsList.visibility = View.VISIBLE
        LogUtil.d("MessageFragment", "avObjects.size ----> ${mutableList.size}")
        messageAdapter.setData(mutableList)
        messageAdapter.notifyDataSetChanged()

        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 返回的对话列表为空的回调
     */
    override fun onLoadConversationListHaveNoNumbers() {
        newsList.visibility = View.GONE
        newsIsEmptyRl.visibility = View.VISIBLE

        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
        }
    }

    lateinit var messageAdapter: MessageAdapter
    lateinit var newsList: RecyclerView
    lateinit var newsIsEmptyRl: RelativeLayout
    lateinit var addFriendIv: ImageView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val messagePresenter = MessagePresenter.instance


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)
        messagePresenter.attachViewCallback(this)
        initView(view, container)
//      clientGetConversation() 一个获取对话的例子
//      clientOpen()  Client 然后发送一些消息
        initEvent()
        messagePresenter.queryConversationList()
        return view
    }

    /**
     * 通过conversationId来获得conversation 然后来发送消息啥的
     */
    private fun clientGetConversation() {
        CoolChatApp.avImClient?.open(object : AVIMClientCallback() {
            override fun done(client: AVIMClient?, e: AVIMException?) {
                val conversation: AVIMConversation? = client?.getConversation("5d5765c8c320f1ab6f38a86c")
                val msg: AVIMTextMessage = AVIMTextMessage()
                msg.text = "看看是不是我xiaohu和mlxg的对话"
                conversation?.sendMessage(msg, object : AVIMConversationCallback() {
                    override fun done(e: AVIMException?) {
                        if (e == null) {
                            LogUtil.d("MessageFragment", "消息发送成功")
                        } else {
                            LogUtil.d("MessageFragment", "AVIMException ----> $e")
                        }
                    }

                })

            }

        })
    }

    /**
     * 处理控件的一些事件
     */
    private fun initEvent() {
        swipeRefreshLayout.setOnRefreshListener {
            LogUtil.d("MessageFragment", "下拉刷新中...")
            messagePresenter.queryConversationList()
        }
        addFriendIv.setOnClickListener {
            val intent = Intent(activity, AddNewActivity::class.java)
            startActivity(intent)
        }
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
        //todo：长按删除的功能还没添加
        LogUtil.d("MessageFragment", "长按了第${position}个item---->1")
    }

    override fun onDestroy() {
        super.onDestroy()
        messagePresenter.unAttachViewCallback(this)
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