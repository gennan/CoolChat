package com.gennan.summer.mvp.presenter

import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.LogInCallback
import com.gennan.summer.mvp.contract.ILoginPresenter
import com.gennan.summer.mvp.contract.ILoginViewCallback


class LoginPresenter : ILoginPresenter {
    val callbacks = mutableListOf<ILoginViewCallback>()

    companion object {
        val instance: LoginPresenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { LoginPresenter() }
    }

    override fun login(username: String, password: String) {
        AVUser.logInInBackground(username, password, object : LogInCallback<AVUser>() {
            override fun done(avUser: AVUser?, e: AVException?) {
                if (e == null) {
                    for (callback in callbacks) {
                        callback.onLoginSucceeded(avUser!!, username, password)
                    }
                } else {
                    for (callback in callbacks) {
                        callback.onLoginFailed(e)
                    }
                }
            }
        })
    }

    override fun attachViewCallback(t: ILoginViewCallback) {
        callbacks.add(t)
    }

    override fun unAttachViewCallback(t: ILoginViewCallback) {
        callbacks.remove(t)
    }
}
