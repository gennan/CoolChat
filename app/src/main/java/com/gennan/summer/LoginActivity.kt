package com.gennan.summer

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVUser
import com.gennan.summer.base.BaseMvpActivity
import com.gennan.summer.mvp.contract.ILoginViewCallback
import com.gennan.summer.mvp.presenter.LoginPresenter
import com.gennan.summer.util.LogUtil
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseMvpActivity(), ILoginViewCallback {
    override fun onLoginSucceeded(avUser: AVUser) {
        progressDialog.dismiss()
        btn_login_sign_in.isEnabled = true
        LogUtil.d("LoginActivity", "登录成功---->1")
        //todo:跳转到新的界面 然后还要添加记住密码功能
    }

    override fun onLoginFailed(e: AVException) {
        progressDialog.dismiss()
        btn_login_sign_in.isEnabled = true
        LogUtil.d("LoginActivity", "登录失败---->0")
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        //todo:和注册界面一样要转换
    }

    lateinit var username: String
    lateinit var password: String
    val loginPresenter = LoginPresenter.instance
    lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginPresenter.attachViewCallback(this)
        initEvent()
    }

    private fun initEvent() {
        //登录
        btn_login_sign_in.setOnClickListener {
            username = et_login_username.text.toString()
            password = et_login_password.text.toString()

            if (username.isEmpty() || username.length > 10 || username.length < 3) {
                Toast.makeText(this, "用户名的长度应为3~10个字符", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty() || password.length > 16 || password.length < 6) {
                Toast.makeText(this, "密码的长度应为6~16个字符", Toast.LENGTH_SHORT).show()
            } else {
                btn_login_sign_in.isEnabled = false
                progressDialog = ProgressDialog(this, R.style.AppTheme_Dark_Dialog)
                progressDialog.isIndeterminate = true
                progressDialog.setMessage("加载中...")
                progressDialog.show()
                loginPresenter.login(username, password)
            }
        }
        //从登录界面跳转到注册界面
        tv_login_link_to_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.unAttachViewCallback(this)
    }
}
