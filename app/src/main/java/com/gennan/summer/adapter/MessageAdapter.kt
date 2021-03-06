package com.gennan.summer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
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
import com.gennan.summer.ui.mvp.model.bean.AVIMMessageBean
import com.gennan.summer.event.ConversationTitleEvent
import com.gennan.summer.util.ClickUtil
import com.gennan.summer.util.Constants.Companion.BLANK_STRING
import com.gennan.summer.util.LogUtil
import java.text.SimpleDateFormat


/**
 *Created by Gennan on 2019/8/15.
 */
class MessageAdapter(private var context: Context) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {


    private lateinit var onLastMessageLoadedListener: OnLastMessageLoadedListener
    private var url: String = BLANK_STRING
    private lateinit var listener: OnItemClickListener
    var conversationList = mutableListOf<AVObject>()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //这个要通过名称来找User的iconUrl
        val iconIv: ImageView =
            itemView.findViewById(R.id.circle_image_view_recycle_item_message)
        val titleTv: TextView = itemView.findViewById(R.id.tv_chat_title_recycle_item)
        val lastMsgTv: TextView = itemView.findViewById(R.id.tv_chat_last_msg_recycle_item)
        val lastTimeTv: TextView = itemView.findViewById(R.id.tv_chat_last_time_recycle_item)
        val unreadMessageRl: RelativeLayout = itemView.findViewById(R.id.rl_unread_message_count)
        val unreadMessageCount: TextView = itemView.findViewById(R.id.tv_unread_message_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_message, parent, false)
        return ViewHolder(view)
    }


    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //SDK的逻辑太乱了 只能先这样魔改了 想不出啥办法了

        if (conversationList[position].getList("m").size == 2) {
            var conversationName = ""
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
                        onLastMessageLoadedListener.onLastMessageLoaded()
                    }
                } else {
                    LogUtil.d("MessageAdapter", "AVException ----> $e")
                }
            }
        })

        if (conversation?.unreadMessagesCount == 0) {
            holder.unreadMessageRl.visibility = View.GONE
        } else if (conversation?.unreadMessagesCount == null) {
            holder.unreadMessageRl.visibility = View.GONE
        } else {
            holder.unreadMessageRl.visibility = View.VISIBLE
            holder.unreadMessageCount.text = conversation.unreadMessagesCount.toString()
        }

        holder.itemView.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            //单聊
            if (conversationList[position].getList("m").size == 2) {
                val conversationName =
                    conversationList[position].getString("name").replace(CoolChatApp.avUser!!.username, "")
                CoolChatApp.getAppEventBus()
                    .postSticky(ConversationTitleEvent(conversationName))
            }
            listener.onItemClick(conversation)
        }
        holder.itemView.setOnLongClickListener {
            listener.onItemLongClick(position, conversationList)
            true//设置为true表示执行完长按后的事件不再处理点击的事件
        }
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

    /**
     * 把消息列表传进来
     */
    fun setData(conversationList: MutableList<AVObject>) {
        this.conversationList = conversationList
    }

    /**
     * 把用户的iconUrl传进来
     */
    fun setConversationIconUrl(url: String) {
        this.url = url
    }

    //暴露点击事件给外部

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(conversation: AVIMConversation?)
        fun onItemLongClick(position: Int, conversationList: MutableList<AVObject>)
    }


    fun setOnLastMessageLoadedListener(onLastMessageLoadedListener: OnLastMessageLoadedListener) {
        this.onLastMessageLoadedListener = onLastMessageLoadedListener
    }

    interface OnLastMessageLoadedListener {
        fun onLastMessageLoaded()
    }
}