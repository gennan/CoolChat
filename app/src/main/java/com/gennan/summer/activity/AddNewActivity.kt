package com.gennan.summer.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gennan.summer.R
import com.gennan.summer.mvvm.viewModel.AddNewViewModel
import kotlinx.android.synthetic.main.activity_add_new.*

class AddNewActivity : AppCompatActivity() {

    val TAG = "AddNewActivity"
    var mAddNewViewModel: AddNewViewModel? = null

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
            mAddNewViewModel?.addNewFriend(et_add_new_friend.text.toString())
        }
    }
}
