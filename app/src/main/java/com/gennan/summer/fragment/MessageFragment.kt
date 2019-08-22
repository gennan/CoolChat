package com.gennan.summer.fragment

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

class MessageFragment : BaseFragment(), MessageAdapter.OnItemClickListener, IMessageViewCallback,
    MessageAdapter.OnLastMessageLoadedListener {


    /**
     * sdk逻辑感觉有点问题 但是跑了很多次都是最后一条消息的加载速度最慢 所以把这个当成是界面加载完成的时间即可
     */
    override fun onLastMessageLoaded() {
        progressDialog.dismiss()
    }

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
        progressDialog.setMessage("采坑中...")
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
     * 获取对话iconUrl的回调
     */
    override fun onConversationIconUrlLoaded(url: String) {
        messageAdapter.setConversationIconUrl(url)
        messageAdapter.notifyDataSetChanged()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            LogUtil.d(TAG, "被隐藏了")
        } else {
            messagePresenter.queryConversationList()
            progressDialog = ProgressDialog(activity, R.style.AppTheme_Dark_Dialog)
            progressDialog.isIndeterminate = true
            progressDialog.setMessage("采坑中...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }
    }

    /**
     * client.open()方法调用成功的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onClientOpenEvent(event: ClientOpenEvent) {
        messagePresenter.queryConversationList()
    }
}