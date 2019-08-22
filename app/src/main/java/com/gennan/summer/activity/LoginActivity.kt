package com.gennan.summer.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.messages.AVIMFileMessage
import com.gennan.summer.R
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.mvp.contract.ILoginViewCallback
import com.gennan.summer.mvp.presenter.LoginPresenter
import com.gennan.summer.util.ClickUtil
import com.gennan.summer.util.LogUtil
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), ILoginViewCallback {
    lateinit var username: String
    private lateinit var password: String
    private val loginPresenter = LoginPresenter.instance
    lateinit var progressDialog: ProgressDialog
    lateinit var loginSharedPreferences: SharedPreferences
    lateinit var loginSPEditor: SharedPreferences.Editor
    private var isRemembered: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//todo:在上线了之后或者是添加了退出当前账号的功能之后把这里的自动登录功能加上 有点问题先不加了 在退出登录之后又会自己登进来
//        judgeUserWhetherNeedToLogin()
        initData()
        loginPresenter.attachViewCallback(this)
        initEvent()
    }

    /**
     * 判断用户是否需要登录 就是登录一次以后就不用再输入账号密码进行登录了
     */
    private fun judgeUserWhetherNeedToLogin() {
        val currentUser = AVUser.getCurrentUser()
        if (currentUser != null) {
            //在再次登录的时候再往CoolChat里添加一份
            CoolChatApp.avUser = currentUser
            CoolChatApp.avImClient = AVIMClient.getInstance(currentUser.username)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * 获取SP以及EditText的文字
     */
    private fun initData() {
        loginSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        isRemembered = loginSharedPreferences.getBoolean("rememberPassword", false)
        if (isRemembered) {
            et_login_username.setText(loginSharedPreferences.getString("user", ""))
            et_login_password.setText(loginSharedPreferences.getString("password", ""))
            checkbox_login.isChecked = isRemembered
        }
    }

    /**
     * 点击事件啥的处理
     */
    private fun initEvent() {
        //登录
        btn_login_sign_in.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
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
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * 登录成功的回调
     */
    @SuppressLint("CommitPrefEdits")
    override fun onLoginSucceeded(avUser: AVUser, username: String, password: String) {
        progressDialog.dismiss()
        btn_login_sign_in.isEnabled = true
        LogUtil.d("LoginActivity", "登录成功---->1")
        loginSPEditor = loginSharedPreferences.edit()
        if (checkbox_login.isChecked) {
            loginSPEditor.putString("user", username)
            loginSPEditor.putString("password", password)
            loginSPEditor.putBoolean("rememberPassword", true)
        } else {
            loginSPEditor.clear()
        }
        loginSPEditor.commit()
        CoolChatApp.avUser = avUser//通过这里使AVUser全局可获得
        //获取AVIMClient里并保存为全局可得的变量 这里的avImClient是还没有open过的avImClient
        CoolChatApp.avImClient = AVIMClient.getInstance(avUser.username)
        LogUtil.d("LoginActivity", "avImClient ----> ${CoolChatApp.avImClient}")
        //跳转到新的界面
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * 登录失败的回调
     */
    override fun onLoginFailed(e: AVException) {
        progressDialog.dismiss()
        btn_login_sign_in.isEnabled = true
        LogUtil.d("LoginActivity", "登录失败---->0")
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.unAttachViewCallback(this)
    }
}