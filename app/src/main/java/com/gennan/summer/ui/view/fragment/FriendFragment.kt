package com.gennan.summer.ui.view.fragment

/**
 *Created by Gennan.
 */
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.im.v2.AVIMConversation
import com.gennan.summer.R
import com.gennan.summer.ui.view.activity.ChatActivity
import com.gennan.summer.adapter.FriendAdapter
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.event.FriendItemClickEvent
import com.gennan.summer.ui.mvp.contract.IUserViewCallback
import com.gennan.summer.ui.mvp.presenter.UserPresenter
import com.gennan.summer.util.LogUtil

class FriendFragment : BaseFragment(), IUserViewCallback, FriendAdapter.OnUserItemClickListener {

    private val userPresenter = UserPresenter.instance
    private var friendAdapter: FriendAdapter? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    val TAG = "FriendFragment"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val friendList: RecyclerView = view.findViewById(R.id.rv_friend)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_fragment_user)
        val friendLayoutManager = LinearLayoutManager(container?.context)
        friendList.layoutManager = friendLayoutManager
        friendAdapter = FriendAdapter(activity!!)
        friendList.adapter = friendAdapter
        friendAdapter?.setOnUserItemClickListener(this)
        friendAdapter?.clearData()
        userPresenter.queryFriendList()
        swipeRefreshLayout?.setColorSchemeColors(ContextCompat.getColor(container!!.context, R.color.colorPrimary))
        swipeRefreshLayout?.setOnRefreshListener {
            friendAdapter?.clearData()
            userPresenter.queryFriendList()
        }
        userPresenter.attachViewCallback(this)

        return view.rootView
    }

    override fun onDestroy() {
        userPresenter.unAttachViewCallback(this)
        super.onDestroy()
    }

    /**
     * Fragment是否可见状态的转化
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            LogUtil.d(TAG,"当前的Fragment由可见变为不可见了")
        } else {
            friendAdapter?.clearData()
            userPresenter.queryFriendList()
        }
    }

    /**
     * 当前用户好友为空的处理
     */
    override fun onFriendIsNull() {
        if (swipeRefreshLayout != null && swipeRefreshLayout!!.isRefreshing) {
            swipeRefreshLayout?.isRefreshing = false
            Toast.makeText(activity, "您当前还没有好友", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 点击进入聊天界面
     */
    override fun onUserItemClick(conversation: AVIMConversation?) {
        CoolChatApp.getAppEventBus().postSticky(FriendItemClickEvent(conversation))
        val intent = Intent(activity, ChatActivity::class.java)
        startActivity(intent)
    }

    /**
     * 有新的好友添加
     */
    override fun onFriendListAdded(avObject: AVObject) {
        friendAdapter?.addData(avObject)
        friendAdapter?.notifyDataSetChanged()
        if (swipeRefreshLayout != null && swipeRefreshLayout!!.isRefreshing) {
            swipeRefreshLayout?.isRefreshing = false
            Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
        }
    }

}