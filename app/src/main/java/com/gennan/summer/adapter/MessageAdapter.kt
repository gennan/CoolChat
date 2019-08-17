package com.gennan.summer.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage
import com.gennan.summer.R
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.util.LogUtil
import java.text.SimpleDateFormat


/**
 *Created by Gennan on 2019/8/15.
 */
class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private lateinit var listener: OnItemClickListener
    var conversationList = mutableListOf<AVObject>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleTv.text = conversationList[position].getString("name")
        val dateStr = SimpleDateFormat("hh:mm").format(conversationList[position].getDate("lm"))
        holder.lastTimeTv.text = dateStr
        LogUtil.d("MessageAdapter", "conversation data ----> ${conversationList[position]}")
        //在这里用objectId获取conversation 然后conv.query()
        //在这里通过User的名字获取UrlIcon
        //todo:信息再写的详细一些

        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
        holder.itemView.setOnLongClickListener {
            listener.onItemLongClick(position)
            true//设置为true表示执行完长按后的事件不再处理点击的事件
        }
    }

    fun setData(conversationList: MutableList<AVObject>) {
        this.conversationList = conversationList
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconIv: ImageView =
            itemView.findViewById(R.id.rounded_image_view_recycle_item_message)//这个要通过名称来找User的iconUrl
        val titleTv: TextView = itemView.findViewById(R.id.tv_chat_title_recycle_item)
        val lastMsgTv: TextView = itemView.findViewById(R.id.tv_chat_last_msg_recycle_item)//通过查询消息来获得最新的消息
        val lastTimeTv: TextView = itemView.findViewById(R.id.tv_chat_last_time_recycle_item)
    }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }
}