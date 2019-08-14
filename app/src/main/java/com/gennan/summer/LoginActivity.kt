package com.gennan.summer

import android.content.Intent
import android.os.Bundle
import com.gennan.summer.base.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseMvpActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initEvent()
    }

    private fun initEvent() {
        //登录
        btn_login_sign_in.setOnClickListener {

        }
        //从登录界面跳转到注册界面
        tv_login_link_to_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
