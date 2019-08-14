package com.gennan.summer

import android.content.Intent
import android.os.Bundle
import com.avos.avoscloud.AVException
import com.gennan.summer.base.BaseMvpActivity
import com.gennan.summer.mvp.contract.IRegisterViewCallback
import com.gennan.summer.mvp.presenter.RegisterPresenter
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseMvpActivity(), IRegisterViewCallback {
    //todo:获取数据的位置好像不对
//    val username: String = et_register_username.text.toString()
//    val password: String = et_register_password.text.toString()
    val registerPresenter: RegisterPresenter = RegisterPresenter.instance


    override fun onRegisterSucceeded() {

    }

    override fun onRegisterFailed(e: AVException) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerPresenter.attachViewCallback(this)
        initEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        registerPresenter.unAttachViewCallback(this)
    }

    private fun initEvent() {
        //注册
        btn_register_sign_up.setOnClickListener {
            //            btn_register_sign_up.isEnabled = false//todo 设置为无法点击了 失败或成功后都应该恢复
//            registerPresenter.register()
        }
        //从注册界面跳转到登录界面
        tv_register_link_to_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
