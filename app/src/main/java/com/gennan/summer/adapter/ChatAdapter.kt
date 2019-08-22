package com.gennan.summer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.im.v2.AVIMMessage
import com.gennan.summer.GlideApp
import com.gennan.summer.R
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.bean.AVIMMessageBean
import com.gennan.summer.util.ClickUtil
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/18.
 */
class ChatAdapter : RecyclerView.Adapter<ChatAdapter.InnerHolder> {

    private lateinit var listener: OnVoiceItemClickListener
    private var context: Context
    private var list: MutableList<AVIMMessage>
    var isVoicePlaying = false

    constructor(list: MutableList<AVIMMessage>, context: Context) {
        this.context = context
        this.list = list
    }

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftLayout: LinearLayout = itemView.findViewById(R.id.ll_left_item_chat)
        val rightLayout: LinearLayout = itemView.findViewById(R.id.ll_right_item_chat)
        val leftTextMessage: TextView = itemView.findViewById(R.id.tv_left_msg_item_chat)
        val rightTextMessage: TextView = itemView.findViewById(R.id.tv_right_msg_item_chat)
        val leftImgMessage: ImageView = itemView.findViewById(R.id.iv_left_img_item_chat)
        val rightImgMessage: ImageView = itemView.findViewById(R.id.iv_right_img_item_chat)
        val leftVoiceMessage: TextView = itemView.findViewById(R.id.tv_left_voice_item_chat)
        val rightVoiceMessage: TextView = itemView.findViewById(R.id.tv_right_voice_item_chat)
        //先被省略的视频部分
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
                holder.leftTextMessage.visibility = View.VISIBLE
                holder.leftImgMessage.visibility = View.GONE
                holder.leftVoiceMessage.visibility = View.GONE
                holder.leftTextMessage.text = msgBean._lctext
            } else if (msgBean?._lctype == -2) {
                holder.leftTextMessage.visibility = View.GONE
                holder.leftImgMessage.visibility = View.VISIBLE
                holder.leftVoiceMessage.visibility = View.GONE
                GlideApp.with(context).load(msgBean._lcfile.url).into(holder.leftImgMessage)
            } else if (msgBean?._lctype == -3) {
                holder.leftTextMessage.visibility = View.GONE
                holder.leftImgMessage.visibility = View.GONE
                holder.leftVoiceMessage.visibility = View.VISIBLE
                holder.itemView.setOnClickListener {
                    if (!ClickUtil.isFastClick()) {
                        return@setOnClickListener
                    }
                    if (isVoicePlaying) {
//                        holder.leftVoiceMessage.text = "语音"
                        listener.onVoiceItemPlayStop()
                    } else {
//                        holder.leftVoiceMessage.text = "正在播放"
                        listener.onVoiceItemPlayStart(msgBean)
                    }
                    isVoicePlaying = !isVoicePlaying
                }
                //收到的是音频文件
            }

        } else {
            //自己发送的消息
            holder.leftLayout.visibility = View.GONE
            holder.rightLayout.visibility = View.VISIBLE
            LogUtil.d("zz", "msgBeanType ----> ${msgBean?._lctype}")
            if (msgBean?._lctype == -1) {
                holder.rightTextMessage.visibility = View.VISIBLE
                holder.rightImgMessage.visibility = View.GONE
                holder.rightVoiceMessage.visibility = View.GONE
                holder.rightTextMessage.text = msgBean._lctext
            } else if (msgBean?._lctype == -2) {
                holder.rightTextMessage.visibility = View.GONE
                holder.rightImgMessage.visibility = View.VISIBLE
                holder.rightVoiceMessage.visibility = View.GONE
                GlideApp.with(context).load(msgBean._lcfile.url).into(holder.rightImgMessage)
            } else if (msgBean?._lctype == -3) {
                holder.rightTextMessage.visibility = View.GONE
                holder.rightImgMessage.visibility = View.GONE
                holder.rightVoiceMessage.visibility = View.VISIBLE
                holder.itemView.setOnClickListener {
                    if (!ClickUtil.isFastClick()) {
                        return@setOnClickListener
                    }
                    if (isVoicePlaying) {
//                        holder.rightVoiceMessage.text = "语音"
                        listener.onVoiceItemPlayStop()
                    } else {
//                        holder.rightVoiceMessage.text = "正在播放"
                        listener.onVoiceItemPlayStart(msgBean)
                    }
                    isVoicePlaying = !isVoicePlaying
                }
            }
        }
    }


    fun setOnVoiceItemClickListener(listener: OnVoiceItemClickListener) {
        this.listener = listener
    }

    interface OnVoiceItemClickListener {
        fun onVoiceItemPlayStart(msgBean: AVIMMessageBean)
        fun onVoiceItemPlayStop()
    }
}