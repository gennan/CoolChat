package com.gennan.summer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        }
    }
//    override fun onConversationListLoaded(avObjects: MutableList<AVObject>) {
//        conversationAdapter?.addData(avObjects)
//        conversationAdapter?.notifyDataSetChanged()
//    }


    val userPresenter = UserPresenter.instance
    var friendAdapter: FriendAdapter? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
//    var conversationAdapter: ConversationAdapter? = null

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
}