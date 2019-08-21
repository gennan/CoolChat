package com.gennan.summer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.FindCallback
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.gennan.summer.R
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.bean.AVIMMessageBean
import com.gennan.summer.event.ConversationTitleEvent
import com.gennan.summer.util.LogUtil
import java.text.SimpleDateFormat


/**
 *Created by Gennan on 2019/8/15.
 */
class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    constructor(context: Context) {
        this.context = context
    }

    private var context: Context
    val lastMsgList: MutableList<AVIMMessage> = mutableListOf()
    //    private var msg: AVIMMessage = AVIMMessage()
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

        if (conversationList[position].getList("m").size == 2) {
//            holder.msgType.text = "单聊"
            var conversationName: String = ""
            if (conversationList[position].getString("name") != null) {
                conversationName =
                    conversationList[position].getString("name").replace(CoolChatApp.avUser!!.username, "")

            }
            holder.titleTv.text = conversationName
            //对话头像的url
            val query = AVQuery<AVObject>("_User")
            query.whereEqualTo("username", conversationName)
            query.findInBackground(object : FindCallback<AVObject>() {
                override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                    if (avObjects != null && avObjects.size != 0) {
                        if (avObjects[0].getString("iconUrl") != null) {
                            val url = avObjects[0].getString("iconUrl")
                            Glide.with(context).load(url).apply(RequestOptions.bitmapTransform(CircleCrop()))
                                .into(holder.iconIv)
                        }
                    }
                }
            })
        }


        //对话的最后发送时间
        val dateStr: String? = if (conversationList[position].getDate("lm") != null) {
            SimpleDateFormat("MM-dd HH:mm").format(conversationList[position].getDate("lm"))
        } else {
            ""
        }
        holder.lastTimeTv.text = dateStr


        val conversationObjectId = conversationList[position].objectId
        val conversation = CoolChatApp.openedClient?.getConversation(conversationObjectId)
        conversation?.queryMessages(1, object : AVIMMessagesQueryCallback() {
            override fun done(messages: MutableList<AVIMMessage>?, e: AVIMException?) {
                if (e == null) {
                    if (messages != null && messages.size > 0) {
                        val lastMessage =
                            CoolChatApp.getAppGson()?.fromJson(messages[0].content, AVIMMessageBean::class.java)
                        when {
                            -1 == lastMessage?._lctype -> holder.lastMsgTv.text = lastMessage._lctext
                            -2 == lastMessage?._lctype -> holder.lastMsgTv.text = "[图片]"
                            -3 == lastMessage?._lctype -> holder.lastMsgTv.text = "[语音]"
                            -4 == lastMessage?._lctype -> holder.lastMsgTv.text = "[视频]"
                        }
                    }
                } else {
                    LogUtil.d("MessageAdapter", "AVException ----> $e")
                }
            }
        })

//================================================================================================

//
//        //最后获得的消息
//        val lm =
//            CoolChatApp.getAppGson()?.fromJson(msg.content, AVIMMessageBean::class.java)
//        //设置为true表示执行完长按后的事件不再处理点击的事件
//        //在这里根据类型设置的收到消息类型
//        when {
//            -1 == lm?._lctype -> holder.lastMsgTv.text = lm._lctext
//            -2 == lm?._lctype -> holder.lastMsgTv.text = "[图片]"
//            -3 == lm?._lctype -> holder.lastMsgTv.text = "[语音]"
//            -4 == lm?._lctype -> holder.lastMsgTv.text = "[视频]"
//        }

//================================================================================================

        holder.itemView.setOnClickListener {
            //单聊
            if (conversationList[position].getList("m").size == 2) {
                val conversationName =
                    conversationList[position].getString("name").replace(CoolChatApp.avUser!!.username, "")
                CoolChatApp.getAppEventBus()
                    .postSticky(ConversationTitleEvent(conversationName))
            }
//            listener.onItemClick(position, conversationList)
            listener.onItemClick(conversation)
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

//    fun addLastMessage(msg: AVIMMessage) {
//        this.msg = msg
//    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconIv: ImageView =
            itemView.findViewById(R.id.circle_image_view_recycle_item_message)//这个要通过名称来找User的iconUrl
        val titleTv: TextView = itemView.findViewById(R.id.tv_chat_title_recycle_item)
        val lastMsgTv: TextView = itemView.findViewById(R.id.tv_chat_last_msg_recycle_item)//通过查询消息来获得最新的消息
        val lastTimeTv: TextView = itemView.findViewById(R.id.tv_chat_last_time_recycle_item)
//        val msgType: TextView = itemView.findViewById(R.id.tv_msg_type_recycle_item)
    }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        //        fun onItemClick(position: Int, conversationList: MutableList<AVObject>)
        fun onItemClick(conversation: AVIMConversation?)

        fun onItemLongClick(position: Int, conversationList: MutableList<AVObject>)
    }
}