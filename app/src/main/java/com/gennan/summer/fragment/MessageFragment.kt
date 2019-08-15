package com.gennan.summer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.gennan.summer.R
import com.gennan.summer.base.BaseFragment

class MessageFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)
        val addFriendIv: ImageView = view.findViewById(R.id.iv_add_friend)//点击跳转到添加好友和群聊的页面
        val newsIsEmptyRl: RelativeLayout =
            view.findViewById(R.id.rl_news_is_empty_message)//数据为空时显示这个
        val newsList: RecyclerView = view.findViewById(R.id.rv_news_message)//数据不为空时显示数据
        //todo：如何获得消息列表 然后显示 并实时刷新 然后又是如何点击进入聊天界面
        return view
    }

}