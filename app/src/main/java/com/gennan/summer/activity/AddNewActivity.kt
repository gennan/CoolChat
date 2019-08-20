package com.gennan.summer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gennan.summer.R
import com.gennan.summer.mvvm.viewModel.AddNewViewModel
import com.gennan.summer.util.LogUtil
import kotlinx.android.synthetic.main.activity_add_new.*

class AddNewActivity : AppCompatActivity() {

    val TAG = "AddNewActivity"
    var mAddNewViewModel: AddNewViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new)
        mAddNewViewModel = ViewModelProviders.of(this).get(AddNewViewModel::class.java)
        mAddNewViewModel?.liveData?.observe(this, Observer {
            LogUtil.d(TAG, "查看下MVVM的效果 ----> $it")

        })
        tv_add_new_friend.setOnClickListener {
            mAddNewViewModel?.addNewFriend(et_add_new_friend.text.toString())
        }
    }
}
