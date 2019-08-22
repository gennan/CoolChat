package com.gennan.summer.activity

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gennan.summer.R
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.mvvm.viewModel.AddNewViewModel
import com.gennan.summer.util.LogUtil
import kotlinx.android.synthetic.main.activity_add_new.*

class AddNewActivity : BaseActivity() {
    val TAG = "AddNewActivity"
    private var mAddNewViewModel: AddNewViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new)
        mAddNewViewModel = ViewModelProviders.of(this).get(AddNewViewModel::class.java)
        bindingData()
        initEvent()
    }

    private fun bindingData() {
        mAddNewViewModel?.addNewFriendLiveData?.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            if (it == "添加好友成功") {
                finish()
            }
        })
        //todo：群聊
//        mAddNewViewModel?.addConversationLiveData?.observe(this, Observer {
//            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//            if (it == "添加群聊成功") {
//                finish()
//            }
//        })
//        mAddNewViewModel?.createConversaionLiveData?.observe(this, Observer {
//            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//            if (it == "创建群聊成功") {
//                finish()
//            }
//        })
    }

    private fun initEvent() {
        tv_add_new_friend.setOnClickListener {
            val friendNameWillBeAdd = et_add_new_friend.text.toString()
            if (friendNameWillBeAdd == "") {
                LogUtil.d(TAG, "好友名不能为空")
            } else {
                mAddNewViewModel?.addNewFriend(friendNameWillBeAdd)
            }
        }
        iv_back_add_new.setOnClickListener {
            finish()
        }
        //todo：群聊
//        tv_add_conversation.setOnClickListener {
//            val conversationNameWillBeAdd = et_add_conversation.text.toString()
//            if (conversationNameWillBeAdd == "") {
//                LogUtil.d(TAG, "群聊名不能为空")
//            } else {
//                mAddNewViewModel?.addNewConversation(conversationNameWillBeAdd)
//            }
//        }
//        tv_create_new_conversation.setOnClickListener {
//            val conversationNameWillBeCreate = et_create_new_conversation.text.toString()
//            if (conversationNameWillBeCreate == "") {
//                LogUtil.d(TAG, "群聊名不能为空")
//            } else {
//                val conversationMembersList = conversationNameWillBeCreate.split(" ")
//                mAddNewViewModel?.createNewConversation(conversationMembersList)
//            }
//        }
    }
}
