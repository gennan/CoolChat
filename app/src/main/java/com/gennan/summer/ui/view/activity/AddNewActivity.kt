package com.gennan.summer.ui.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gennan.summer.R
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.ui.mvvm.viewModel.AddNewViewModel
import com.gennan.summer.util.ClickUtil
import com.gennan.summer.util.LogUtil
import kotlinx.android.synthetic.main.activity_add_new.*

/**
 *Created by Gennan.
 */
class AddNewActivity : BaseActivity() {
    val tag = "AddNewActivity"
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
    }

    private fun initEvent() {
        tv_add_new_friend.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            val friendNameWillBeAdd = et_add_new_friend.text.toString()
            if (friendNameWillBeAdd == "") {
                LogUtil.d(tag, "输入的好友名为空 ----> 0")
            } else {
                mAddNewViewModel?.addNewFriend(friendNameWillBeAdd)
            }
        }
        iv_back_add_new.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            finish()
        }
    }
}
