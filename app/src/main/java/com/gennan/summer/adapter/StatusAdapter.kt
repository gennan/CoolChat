package com.gennan.summer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gennan.summer.R

/**
 *Created by Gennan on 2019/8/22.
 */
class StatusAdapter : RecyclerView.Adapter<StatusAdapter.InnerHolder> {
    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    constructor()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_status, parent, false)
        return InnerHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {

    }
}