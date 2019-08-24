package com.gennan.summer.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.AVStatus
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.gennan.summer.GlideApp
import com.gennan.summer.R
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.util.LogUtil
import java.text.SimpleDateFormat

/**
 *Created by Gennan on 2019/8/22.
 */
class StatusAdapter : RecyclerView.Adapter<StatusAdapter.InnerHolder> {

    private lateinit var onStatusImageClickListener: OnStatusImageClickListener
    private var activity: BaseActivity
    private var list: MutableList<AVStatus>? = null
    val tag = "StatusAdapter"

    constructor(activity: BaseActivity) {
        this.activity = activity
    }

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusSenderAvatarIv: ImageView = itemView.findViewById(R.id.iv_user_avatar_status_item)//发送者的头像
        val statusSenderUserNameTv: TextView = itemView.findViewById(R.id.tv_user_send_status_item)//发送者的用户名
        val statusSendTimeTv: TextView = itemView.findViewById(R.id.tv_status_send_time)//动态发送的时间
        val statusTextContentTv: TextView = itemView.findViewById(R.id.tv_content_send_status_item)//动态的文字内容
        val statusImageContentIv: ImageView = itemView.findViewById(R.id.iv_content_send_status_item)//动态的图片内容
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_status, parent, false)
        return InnerHolder(view)
    }

    override fun getItemCount(): Int {
        return if (list != null) {
            list!!.size
        } else {
            0
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        LogUtil.d(tag, "从列表中得到的AVStatus ----> ${list!![position]}")
        GlideApp.with(activity)
            .load(list!![position].get("iconUrl"))
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(holder.statusSenderAvatarIv)
        holder.statusSenderUserNameTv.text = list!![position].get("username").toString()
        holder.statusSendTimeTv.text = SimpleDateFormat("MM-dd HH:mm").format(list!![position].createdAt)

        if (list!![position].message == "") {
            holder.statusTextContentTv.visibility = View.GONE
        } else {
            holder.statusTextContentTv.text = list!![position].message
        }

        if (list!![position].imageUrl == "") {
            holder.statusImageContentIv.visibility = View.GONE
        } else {
            GlideApp.with(activity)
                .load(list!![position].imageUrl)
                .into(holder.statusImageContentIv)
            holder.itemView.setOnClickListener {
                onStatusImageClickListener.onStatusImageItemClick(list!![position].imageUrl)
            }
        }

    }

    fun setData(list: MutableList<AVStatus>?) {
        this.list = list
    }

    //暴露点击事件
    fun setOnStatusImageClickListener(onStatusImageClickListener: OnStatusImageClickListener) {
        this.onStatusImageClickListener = onStatusImageClickListener
    }

    interface OnStatusImageClickListener {
        fun onStatusImageItemClick(imageUrl: String)
    }
}