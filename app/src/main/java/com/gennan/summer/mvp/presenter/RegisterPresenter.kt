package com.gennan.summer.mvp.presenter

import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.SignUpCallback
import com.gennan.summer.mvp.contract.IRegisterPresenter
import com.gennan.summer.mvp.contract.IRegisterViewCallback
import com.gennan.summer.util.Constants.Companion.DEFAULT_ICON
import com.gennan.summer.util.LogUtil


class RegisterPresenter : IRegisterPresenter {
    val callbacks = mutableListOf<IRegisterViewCallback>()

    companion object {
        val instance: RegisterPresenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { RegisterPresenter() }
    }

    override fun register(username: String, password: String) {

        val user = AVUser()
        user.username = username
        user.setPassword(password)
        user.put("iconUrl", DEFAULT_ICON)//默认给创建的用户设置一个头像
        user.signUpInBackground(object : SignUpCallback() {
            override fun done(e: AVException?) {
                if (e == null) {
                    // 注册成功
                    for (callback in callbacks) {
                        callback.onRegisterSucceeded()
                    }
                } else {
                    // 失败的原因可能有多种，常见的是用户名已经存在。
                    LogUtil.d("RegisterPresenter", "error---->$e")
                    for (callback in callbacks) {
                        callback.onRegisterFailed(e)
                    }
                }
            }
        })
    }


    override fun attachViewCallback(t: IRegisterViewCallback) {
        callbacks.add(t)
    }

    override fun unAttachViewCallback(t: IRegisterViewCallback) {
        callbacks.remove(t)
    }
}