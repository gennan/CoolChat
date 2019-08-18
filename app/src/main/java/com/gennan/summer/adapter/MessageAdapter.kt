package com.gennan.summer.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.im.v2.AVIMMessage
import com.bumptech.glide.Glide
import com.gennan.summer.R
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.bean.TextMessageBean
import com.gennan.summer.event.ConversationTitleEvent
import com.gennan.summer.mvp.presenter.MessagePresenter
import java.text.SimpleDateFormat


/**
 *Created by Gennan on 2019/8/15.
 */
class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private var msg: AVIMMessage = AVIMMessage()
    private var url: String = ""
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
        MessagePresenter.instance.getConversationIconAndLastMessage(conversationList[position])
        holder.titleTv.text = conversationList[position].getString("name")
        val dateStr = SimpleDateFormat("hh:mm").format(conversationList[position].getDate("lm"))
        holder.lastTimeTv.text = dateStr
        Glide.with(CoolChatApp.getAppContext()!!).load(url).error(R.drawable.place_holder_user_icon)
            .into(holder.iconIv)
        val textMessage =
            CoolChatApp.getAppGson()?.fromJson(msg.content, TextMessageBean::class.java)
        holder.lastMsgTv.text = textMessage?._lctext


        holder.itemView.setOnClickListener {
            CoolChatApp.getAppEventBus()
                .postSticky(ConversationTitleEvent(conversationList[position].getString("name")))
            listener.onItemClick(position, conversationList)
        }
        holder.itemView.setOnLongClickListener {
            listener.onItemLongClick(position, conversationList)
            true//设置为true表示执行完长按后的事件不再处理点击的事件
        }
    }

    fun setData(conversationList: MutableList<AVObject>) {
        this.conversationList = conversationList
    }

    fun setConversationIconUrl(url: String) {
        this.url = url
    }

    fun setLastMessage(msg: AVIMMessage) {
        this.msg = msg
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
        fun onItemClick(position: Int, conversationList: MutableList<AVObject>)
        fun onItemLongClick(position: Int, conversationList: MutableList<AVObject>)
    }
}