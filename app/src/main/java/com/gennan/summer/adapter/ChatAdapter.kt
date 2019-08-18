package com.gennan.summer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gennan.summer.R

/**
 *Created by Gennan on 2019/8/18.
 */
class ChatAdapter : RecyclerView.Adapter<ChatAdapter.InnerHolder>() {
    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_chat, parent, false)
        return InnerHolder(view)
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}