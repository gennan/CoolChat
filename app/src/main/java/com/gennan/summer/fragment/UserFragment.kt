package com.gennan.summer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVUser
import com.gennan.summer.R
import com.gennan.summer.adapter.ConversationAdapter
import com.gennan.summer.adapter.FriendAdapter
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.mvp.contract.IUserViewCallback
import com.gennan.summer.mvp.presenter.UserPresenter

class UserFragment : BaseFragment(), IUserViewCallback {
    override fun onConversationListLoaded(avObjects: MutableList<AVObject>) {
        conversationAdapter?.setData(avObjects)
        conversationAdapter?.notifyDataSetChanged()
    }

    override fun onFriendListLoaded(avObjects: MutableList<AVUser>) {
        friendAdapter?.setData(avObjects)
        friendAdapter?.notifyDataSetChanged()
    }

    val userPresenter = UserPresenter.instance
    var friendAdapter: FriendAdapter? = null
    var conversationAdapter: ConversationAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        val friendList: RecyclerView = view.findViewById(R.id.rv_friend)
        val conversationList: RecyclerView = view.findViewById(R.id.rv_conversation)
        val friendLayoutManager = LinearLayoutManager(container?.context)
        val conversationLayoutManager = LinearLayoutManager(container?.context)
        friendList.layoutManager = friendLayoutManager
        conversationList.layoutManager = conversationLayoutManager
        friendAdapter = FriendAdapter()
        conversationAdapter = ConversationAdapter()
        friendList.adapter = friendAdapter
        conversationList.adapter = conversationAdapter
        userPresenter.queryFriendList()
        userPresenter.queryConversationList()
        userPresenter.attachViewCallback(this)
        return view.rootView
    }


    override fun onDestroy() {
        userPresenter.unAttachViewCallback(this)
        super.onDestroy()
    }
}