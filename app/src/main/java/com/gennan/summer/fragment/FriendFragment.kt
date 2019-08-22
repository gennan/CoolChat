package com.gennan.summer.fragment

/**
 * 注释的部分都是群聊部分的代码
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
import com.gennan.summer.activity.ChatActivity
import com.gennan.summer.adapter.FriendAdapter
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.event.FriendItemClickEvent
import com.gennan.summer.mvp.contract.IUserViewCallback
import com.gennan.summer.mvp.presenter.UserPresenter

class FriendFragment : BaseFragment(), IUserViewCallback, FriendAdapter.OnUserItemClickListener {
    override fun onFriendIsNull() {
        if (swipeRefreshLayout != null && swipeRefreshLayout!!.isRefreshing) {
            swipeRefreshLayout?.isRefreshing = false
            Toast.makeText(activity, "您当前还没有好友", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onUserItemClick(conversation: AVIMConversation?) {
        CoolChatApp.getAppEventBus().postSticky(FriendItemClickEvent(conversation))
        val intent = Intent(activity, ChatActivity::class.java)
        startActivity(intent)
    }

    override fun onFriendListAdded(avObject: AVObject) {
        friendAdapter?.addData(avObject)
        friendAdapter?.notifyDataSetChanged()
        if (swipeRefreshLayout != null && swipeRefreshLayout!!.isRefreshing) {
            swipeRefreshLayout?.isRefreshing = false
            Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
        }
    }
    //群聊
//    override fun onConversationListLoaded(avObjects: MutableList<AVObject>) {
//        conversationAdapter?.addData(avObjects)
//        conversationAdapter?.notifyDataSetChanged()
//    }
//    var conversationAdapter: ConversationAdapter? = null


    val userPresenter = UserPresenter.instance
    var friendAdapter: FriendAdapter? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
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
        userPresenter.queryFriendList()
        swipeRefreshLayout?.setColorSchemeColors(ContextCompat.getColor(container!!.context, R.color.colorPrimary))
        swipeRefreshLayout?.setOnRefreshListener {
            friendAdapter?.clearData()
            userPresenter.queryFriendList()
        }
// 群聊
//        val conversationList: RecyclerView = view.findViewById(R.id.rv_conversation)
//        val conversationLayoutManager = LinearLayoutManager(container?.context)
//        conversationList.layoutManager = conversationLayoutManager
//        conversationAdapter = ConversationAdapter()
//        conversationList.adapter = conversationAdapter
//        userPresenter.queryConversationList()
        userPresenter.attachViewCallback(this)
        return view.rootView
    }

    override fun onDestroy() {
        userPresenter.unAttachViewCallback(this)
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (hidden) {

        } else {
            friendAdapter?.clearData()
            userPresenter.queryFriendList()
        }
    }
}