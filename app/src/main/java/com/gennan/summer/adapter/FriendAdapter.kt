package com.gennan.summer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.AVUser
import com.gennan.summer.R
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/20.
 */
class FriendAdapter : RecyclerView.Adapter<FriendAdapter.InnerHolder>() {
    var avUserList: MutableList<AVUser> = mutableListOf()
    val TAG = "FriendAdapter"

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userIcon: ImageView = itemView.findViewById(R.id.iv_user_icon_recycle_item_user)
        val userName: TextView = itemView.findViewById(R.id.tv_user_name_recycle_item_user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_user, parent, false)
        return InnerHolder(view)
    }

    override fun getItemCount(): Int {
        return avUserList.size
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.userName.text = avUserList[position].username
        LogUtil.d(TAG, "iconUrl ----> ${avUserList[position].getString("iconUrl")}")
    }

    fun setData(avObjects: MutableList<AVUser>) {
        avUserList = avObjects
    }
}