package com.gennan.summer.ui.view.fragment

import android.app.ProgressDialog
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
import com.avos.avoscloud.im.v2.AVIMConversation
import com.gennan.summer.R
import com.gennan.summer.ui.view.activity.AddNewActivity
import com.gennan.summer.ui.view.activity.ChatActivity
import com.gennan.summer.adapter.MessageAdapter
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.event.BackFromChatToMessageEvent
import com.gennan.summer.event.ClientOpenEvent
import com.gennan.summer.event.ConversationObjectEvent
import com.gennan.summer.ui.mvp.contract.IMessageViewCallback
import com.gennan.summer.ui.mvp.presenter.MessagePresenter
import com.gennan.summer.util.LogUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *Created by Gennan.
 */
class MessageFragment : BaseFragment(), MessageAdapter.OnItemClickListener, IMessageViewCallback,
    MessageAdapter.OnLastMessageLoadedListener {

    val TAG = "MessageFragment"
    lateinit var progressDialog: ProgressDialog
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
        messagePresenter.queryConversationList()
        progressDialog = ProgressDialog(activity, R.style.AppTheme_Dark_Dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("加载中...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        return view.rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messagePresenter.unAttachViewCallback(this)
        CoolChatApp.getAppEventBus().unregister(this)
    }

    /**
     * 找到MessageFragment里的一些控件实例
     */
    private fun initView(view: View?, container: ViewGroup?) {
        swipeRefreshLayout = view!!.findViewById(R.id.swipe_fresh_layout_fragment_message)
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(container!!.context, R.color.colorPrimary))
        addFriendIv = view.findViewById(R.id.iv_add_friend)//点击跳转到添加好友和群聊的页面
        newsIsEmptyRl =
            view.findViewById(R.id.rl_news_is_empty_message)//数据为空时显示这个
        newsList = view.findViewById(R.id.rv_news_message)//数据不为空时显示数据
        //一些有关RecyclerView的设置
        val manager = LinearLayoutManager(container.context)
        newsList.layoutManager = manager
        messageAdapter = MessageAdapter(container.context)
        messageAdapter.setOnLastMessageLoadedListener(this)
        messageAdapter.setOnItemClickListener(this)
        newsList.adapter = messageAdapter
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
            val intent = Intent(context, AddNewActivity::class.java)
            startActivity(intent)
        }
    }


    /**
     * item单击进入聊天界面
     */
    override fun onItemClick(conversation: AVIMConversation?) {
        CoolChatApp.getAppEventBus().postSticky(ConversationObjectEvent(conversation))
        val intent = Intent(activity, ChatActivity::class.java)
        startActivity(intent)
    }

    /**
     * 长按item 删除item
     */
    override fun onItemLongClick(position: Int, conversationList: MutableList<AVObject>) {
        //todo：长按删除的功能无法添加 SDK没有直接删除Conversation的方法 只能先假装长按删除一下了 刷新以后又会出现了
        conversationList.remove(conversationList[position])
        messageAdapter.notifyDataSetChanged()
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
        progressDialog.dismiss()
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * 获取对话iconUrl的回调
     */
    override fun onConversationIconUrlLoaded(url: String) {
        messageAdapter.setConversationIconUrl(url)
        messageAdapter.notifyDataSetChanged()
    }

    /**
     * framgment可见状态转换时的事件处理
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            progressDialog.dismiss()
            LogUtil.d(TAG, "当前Fragment由可见变为不可见")
        } else {
            messagePresenter.queryConversationList()
            progressDialog = ProgressDialog(activity, R.style.AppTheme_Dark_Dialog)
            progressDialog.isIndeterminate = true
            progressDialog.setMessage("加载中...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }
    }

    /**
     * 最后一条消息加载完成的回调 这里我把他当成是所有数据都加载完成
     * sdk逻辑感觉有点问题 但是跑了很多次都是最后一条消息的加载速度最慢 所以把这个当成是界面加载完成的时间即可
     */
    override fun onLastMessageLoaded() {
        progressDialog.dismiss()
    }

    /**
     * client.open()方法调用成功的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onClientOpenEvent(event: ClientOpenEvent) {
        messagePresenter.queryConversationList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onBackFromChatToMessageEventHandle(event: BackFromChatToMessageEvent) {
        messagePresenter.queryConversationList()
        CoolChatApp.getAppEventBus().removeStickyEvent(BackFromChatToMessageEvent::class.java)
    }
}