package com.gennan.summer.mvp.contract

import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVUser
import com.gennan.summer.base.IBasePresenter

interface ILoginPresenter : IBasePresenter<ILoginViewCallback> {
    fun login(username: String, password: String)
}

interface ILoginViewCallback {
    fun onLoginSucceeded(avUser: AVUser)
    fun onLoginFailed(e: AVException)
}