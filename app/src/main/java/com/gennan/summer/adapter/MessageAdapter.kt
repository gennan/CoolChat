package com.gennan.summer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.AVObject
import com.gennan.summer.R
import com.gennan.summer.util.LogUtil


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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleTv.text = conversationList[position].getString("name")
        LogUtil.d("MessageAdapter","conversation data ----> ${conversationList[position]}")
        //todo:信息再写的详细一些 然后添加item的点击事件

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
        val titleTv: TextView = itemView.findViewById(R.id.tv_chat_title_recycle_item)
    }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }
}