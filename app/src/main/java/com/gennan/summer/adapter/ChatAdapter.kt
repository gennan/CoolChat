package com.gennan.summer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.im.v2.AVIMMessage
import com.gennan.summer.R
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.bean.AVIMMessageBean

/**
 *Created by Gennan on 2019/8/18.
 */
class ChatAdapter : RecyclerView.Adapter<ChatAdapter.InnerHolder> {

    private var list: MutableList<AVIMMessage>

    constructor(list: MutableList<AVIMMessage>) {
        this.list = list
    }

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftLayout: LinearLayout = itemView.findViewById(R.id.ll_left_item_chat)
        val rightLayout: LinearLayout = itemView.findViewById(R.id.ll_right_item_chat)
        val leftTextMessage: TextView = itemView.findViewById(R.id.tv_left_msg_item_chat)
        val rightTextMessage: TextView = itemView.findViewById(R.id.tv_right_msg_item_chat)
//        val leftImgMessage: ImageView = itemView.findViewById(R.id.iv_left_img_item_chat)
//        val rightImgMessage: ImageView = itemView.findViewById(R.id.iv_right_img_item_chat)
//        val leftVideoView: VideoView = itemView.findViewById(R.id.video_view_left_chat)
//        val rightVideoView: VideoView = itemView.findViewById(R.id.video_view_right_chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_chat, parent, false)
        return InnerHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        //收到消息的处理
       val msg = list[position]
        val msgBean = CoolChatApp.getAppGson()?.fromJson(msg.content, AVIMMessageBean::class.java)
        if (msg.from != CoolChatApp.openedClient?.clientId) {
            //别人发送的消息
            holder.leftLayout.visibility = View.VISIBLE
            holder.rightLayout.visibility = View.GONE
            if (msgBean?._lctype == -1) {
                holder.leftTextMessage.text = msgBean._lctext
            }
        } else {
            //自己发送的消息
            holder.leftLayout.visibility = View.GONE
            holder.rightLayout.visibility = View.VISIBLE
            if (msgBean?._lctype == -1) {
                holder.rightTextMessage.text = msgBean._lctext
            }
        }
    }
}