package com.gennan.summer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.gennan.summer.GlideApp
import com.gennan.summer.R
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.event.ConversationTitleEvent
import com.gennan.summer.util.ClickUtil
import com.gennan.summer.util.LogUtil
import java.util.*

/**
 *Created by Gennan on 2019/8/20.
 */
class FriendAdapter(private var context: Context) : RecyclerView.Adapter<FriendAdapter.InnerHolder>() {
    var listener: OnUserItemClickListener? = null
    private var avUserList: MutableList<AVObject> = mutableListOf()
    val tag = "FriendAdapter"

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userIcon: ImageView = itemView.findViewById(R.id.iv_user_icon_recycle_item_user)
        val userName: TextView = itemView.findViewById(R.id.tv_user_name_recycle_item_user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_user, parent, false)
        return InnerHolder(view)
    }

    override fun getItemCount(): Int {
        return avUserList.size
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        LogUtil.d(tag, "avUser的ObjectId ----> ${avUserList[position].objectId}")
        val username = avUserList[position].getString("username")
        holder.userName.text = username

        GlideApp
            .with(context)
            .load(avUserList[position].getString("iconUrl"))
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(holder.userIcon)

        holder.itemView.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            CoolChatApp.getAppEventBus().postSticky(ConversationTitleEvent(username))
            CoolChatApp.openedClient?.createConversation(
                Arrays.asList(username), CoolChatApp.avUser?.username + username, null,
                object : AVIMConversationCreatedCallback() {
                    override fun done(conversation: AVIMConversation?, e: AVIMException?) {
                        if (e == null) {
                            LogUtil.d(tag, "获取conversation成功 ----> $conversation")
                            listener?.onUserItemClick(conversation)
                        } else {
                            LogUtil.d(tag, "获取conversation失败 ----> $e")
                        }
                    }
                })
        }
    }

    /**
     * 往好友列表里添加好友
     */
    fun addData(avObject: AVObject) {
        avUserList.add(avObject)
    }

    /**
     * 清除列表数据
     */
    fun clearData() {
        avUserList.clear()
    }

    //向外暴露接口
    fun setOnUserItemClickListener(listener: OnUserItemClickListener) {
        this.listener = listener
    }

    interface OnUserItemClickListener {
        fun onUserItemClick(conversation: AVIMConversation?)
    }

    /**
     * 解决item复用
     */
    override fun getItemViewType(position: Int): Int {
        return position
    }
}