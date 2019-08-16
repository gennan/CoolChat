package com.gennan.summer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.AVObject
import com.gennan.summer.R


/**
 *Created by Gennan on 2019/8/15.
 */
class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
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
        //信息再写的详细一些 然后添加item的点击事件
    }

    fun setData(conversationList: MutableList<AVObject>) {
        this.conversationList = conversationList
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.tv_chat_title_recycle_item)
    }

}