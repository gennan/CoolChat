package com.gennan.summer

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.avos.avoscloud.AVException
import com.gennan.summer.base.BaseMvpActivity
import com.gennan.summer.mvp.contract.IRegisterViewCallback
import com.gennan.summer.mvp.presenter.RegisterPresenter
import com.gennan.summer.util.LogUtil
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseMvpActivity(), IRegisterViewCallback {
    lateinit var username: String
    lateinit var password: String
    val registerPresenter: RegisterPresenter = RegisterPresenter.instance
    lateinit var progressDialog: ProgressDialog


    override fun onRegisterSucceeded() {
        progressDialog.dismiss()
        btn_register_sign_up.isEnabled = true
        LogUtil.d("RegisterActivity", "账号注册成功---->1")
        Toast.makeText(this, "账号注册成功", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRegisterFailed(e: AVException) {
        progressDialog.dismiss()
        btn_register_sign_up.isEnabled = true
        LogUtil.d("RegisterActivity", "账号注册失败---->0")
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        //todo:把异常最后转换成能看的懂得文字
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
            username = et_register_username.text.toString()
            password = et_register_password.text.toString()

            if (username.isEmpty() || username.length > 10 || username.length < 3) {
                LogUtil.d("RegisterActivity", "username.length ---->${username.length}")
                Toast.makeText(this, "用户名的长度应为3~10个字符", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty() || password.length > 16 || password.length < 6) {
                Toast.makeText(this, "密码的长度应为6~16个字符", Toast.LENGTH_SHORT).show()
            } else {
                btn_register_sign_up.isEnabled = false
                progressDialog = ProgressDialog(this, R.style.AppTheme_Dark_Dialog)
                progressDialog.isIndeterminate = true
                progressDialog.setMessage("加载中...")
                progressDialog.show()
                registerPresenter.register(username, password)
            }
        }
        //从注册界面跳转到登录界面
        tv_register_link_to_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
