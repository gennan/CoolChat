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
import com.avos.avoscloud.im.v2.AVIMMessage
import com.gennan.summer.R
import com.gennan.summer.activity.AddNewActivity
import com.gennan.summer.activity.ChatActivity
import com.gennan.summer.adapter.MessageAdapter
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.event.ClientOpenEvent
import com.gennan.summer.event.ConversationObjectEvent
import com.gennan.summer.mvp.contract.IMessageViewCallback
import com.gennan.summer.mvp.presenter.MessagePresenter
import com.gennan.summer.util.LogUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MessageFragment : BaseFragment(), MessageAdapter.OnItemClickListener, IMessageViewCallback {


    private lateinit var messageAdapter: MessageAdapter
    private lateinit var newsList: RecyclerView
    private lateinit var newsIsEmptyRl: RelativeLayout
    private lateinit var addFriendIv: ImageView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val messagePresenter = MessagePresenter.instance


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)
        CoolChatApp.getAppEventBus().register(this)
        messagePresenter.attachViewCallback(this)
        initView(view, container)
        initEvent()
        return view
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
    override fun onItemClick(position: Int, conversationList: MutableList<AVObject>) {
        CoolChatApp.getAppEventBus().postSticky(ConversationObjectEvent(conversationList, position))
        val intent = Intent(activity, ChatActivity::class.java)
        startActivity(intent)
    }

    /**
     * 长按item 删除item
     */
    override fun onItemLongClick(position: Int, conversationList: MutableList<AVObject>) {
        //todo：长按删除的功能还没添加
        LogUtil.d("MessageFragment", "长按了第${position}个item---->1")
    }

    /**
     * 刷新返回的对话列表不为0的回调
     */
    override fun onLoadConversationListHaveNumbers(mutableList: MutableList<AVObject>) {
        newsIsEmptyRl.visibility = View.GONE
        newsList.visibility = View.VISIBLE
        messageAdapter.setData(mutableList)
        messageAdapter.notifyDataSetChanged()
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 刷新返回的对话列表为0的回调
     */
    override fun onLoadConversationListHaveNoNumbers() {
        newsList.visibility = View.GONE
        newsIsEmptyRl.visibility = View.VISIBLE

        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * 获取对话item的回调
     */
    override fun onConversationIconUrlLoaded(url: String) {
        messageAdapter.setConversationIconUrl(url)

    }

    /**
     * 获取对话message的回调
     */
    override fun onConversationLastMessageLoaded(msg: AVIMMessage) {
        messageAdapter.setLastMessage(msg)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        messagePresenter.unAttachViewCallback(this)
        CoolChatApp.getAppEventBus().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onClientOpenEvent(event: ClientOpenEvent) {
        messagePresenter.queryConversationList()
    }
}