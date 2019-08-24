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
import com.gennan.summer.ui.mvp.model.bean.AVIMMessageBean
import com.gennan.summer.util.ClickUtil
import com.gennan.summer.util.Constants.Companion.AUDIO_MSG_TYPE
import com.gennan.summer.util.Constants.Companion.IMG_MSG_TYPE
import com.gennan.summer.util.Constants.Companion.TEXT_MSG_TYPE
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/18.
 */
class ChatAdapter : RecyclerView.Adapter<ChatAdapter.InnerHolder> {


    val tag = "ChatAdapter"
    private lateinit var imageItemClickListener: OnImageItemClickListener
    private lateinit var voiceItemClickListener: OnVoiceItemClickListener
    private var context: Context
    private var list: MutableList<AVIMMessage>
    var isVoicePlaying = false//语音消息的播放状态

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
            if (msgBean?._lctype == TEXT_MSG_TYPE) {
                holder.leftTextMessage.visibility = View.VISIBLE
                holder.leftImgMessage.visibility = View.GONE
                holder.leftVoiceMessage.visibility = View.GONE
                holder.leftTextMessage.text = msgBean._lctext
            } else if (msgBean?._lctype == IMG_MSG_TYPE) {
                holder.leftTextMessage.visibility = View.GONE
                holder.leftImgMessage.visibility = View.VISIBLE
                holder.leftVoiceMessage.visibility = View.GONE
                GlideApp.with(context).load(msgBean._lcfile.url).into(holder.leftImgMessage)
                holder.itemView.setOnClickListener {
                    if (!ClickUtil.isFastClick()) {
                        return@setOnClickListener
                    }
                    imageItemClickListener.onImgItemClicked(msgBean._lcfile.url)
                }
            } else if (msgBean?._lctype == AUDIO_MSG_TYPE) {
                holder.leftTextMessage.visibility = View.GONE
                holder.leftImgMessage.visibility = View.GONE
                holder.leftVoiceMessage.visibility = View.VISIBLE
                holder.itemView.setOnClickListener {
                    if (!ClickUtil.isFastClick()) {
                        return@setOnClickListener
                    }
                    if (isVoicePlaying) {
//                        holder.leftVoiceMessage.text = "语音"
                        voiceItemClickListener.onVoiceItemPlayStop()
                    } else {
//                        holder.leftVoiceMessage.text = "正在播放"
                        voiceItemClickListener.onVoiceItemPlayStart(msgBean)
                    }
                    isVoicePlaying = !isVoicePlaying
                }
                //收到的是音频文件
            }

        } else {
            //自己发送的消息
            holder.leftLayout.visibility = View.GONE
            holder.rightLayout.visibility = View.VISIBLE
            LogUtil.d(tag, "转换换的msgBeanType ----> ${msgBean?._lctype}")
            if (msgBean?._lctype == TEXT_MSG_TYPE) {
                holder.rightTextMessage.visibility = View.VISIBLE
                holder.rightImgMessage.visibility = View.GONE
                holder.rightVoiceMessage.visibility = View.GONE
                holder.rightTextMessage.text = msgBean._lctext
            } else if (msgBean?._lctype == IMG_MSG_TYPE) {
                holder.rightTextMessage.visibility = View.GONE
                holder.rightImgMessage.visibility = View.VISIBLE
                holder.rightVoiceMessage.visibility = View.GONE
                GlideApp.with(context).load(msgBean._lcfile.url).into(holder.rightImgMessage)
                holder.itemView.setOnClickListener {
                    if (!ClickUtil.isFastClick()) {
                        return@setOnClickListener
                    }
                    imageItemClickListener.onImgItemClicked(msgBean._lcfile.url)
                }
            } else if (msgBean?._lctype == AUDIO_MSG_TYPE) {
                holder.rightTextMessage.visibility = View.GONE
                holder.rightImgMessage.visibility = View.GONE
                holder.rightVoiceMessage.visibility = View.VISIBLE
                holder.itemView.setOnClickListener {
                    if (!ClickUtil.isFastClick()) {
                        return@setOnClickListener
                    }
                    if (isVoicePlaying) {
//                        holder.rightVoiceMessage.text = "语音"
                        voiceItemClickListener.onVoiceItemPlayStop()
                    } else {
//                        holder.rightVoiceMessage.text = "正在播放"
                        voiceItemClickListener.onVoiceItemPlayStart(msgBean)
                    }
                    isVoicePlaying = !isVoicePlaying
                }
            }
        }
    }


    //给外部暴露接口
    fun setOnVoiceItemClickListener(listener: OnVoiceItemClickListener) {
        this.voiceItemClickListener = listener
    }

    interface OnVoiceItemClickListener {
        fun onVoiceItemPlayStart(msgBean: AVIMMessageBean)
        fun onVoiceItemPlayStop()
    }

    fun setOnImageItemClickListener(listener: OnImageItemClickListener) {
        this.imageItemClickListener = listener
    }

    interface OnImageItemClickListener {
        fun onImgItemClicked(url: String)
    }

    /**
     * 重新获取position解决RecyclerView的item复用导致显示错乱的问题
     */
    override fun getItemViewType(position: Int): Int {
        return position
    }
}