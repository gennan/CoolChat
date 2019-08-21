package com.gennan.summer.adapter

/**
 *Created by Gennan on 2019/8/20.
 *LeanCloud同时写单聊和群聊也太难受了 就还是只能写一个单聊了 先放着后面再慢慢补上
 */
//class ConversationAdapter : RecyclerView.Adapter<ConversationAdapter.InnerHolder>() {
//
//    private var mutableList: MutableList<AVObject> = mutableListOf()
//    val TAG = "ConversationAdapter"
//
//    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val userIcon: ImageView = itemView.findViewById(R.id.iv_user_icon_recycle_item_user)
//        val userName: TextView = itemView.findViewById(R.id.tv_user_name_recycle_item_user)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_user, parent, false)
//        return InnerHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return mutableList.size
//    }
//
//    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
//        val conversationMembersSize = mutableList[position].getList("m").size
//        if (conversationMembersSize > 2) {
//            //成员大于2了才算是群聊嘛 先有成员大于2的群聊再来设置吧 头像就先随缘吧 啊啊啊啊 要死了
//            //这里可以用隐藏显示的方法来写要好一些
//            holder.userName.text = mutableList[position].getString("name")
//        }
//    }
//
//    fun addData(avObjects: MutableList<AVObject>) {
//        this.mutableList = avObjects
//    }
//}