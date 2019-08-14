package com.gennan.summer.mvp.contract

import com.gennan.summer.base.IBasePresenter

interface ILoginPresenter : IBasePresenter<ILoginViewCallback> {
    fun login(username: String, password: String)
}

interface ILoginViewCallback {
    fun onLoginSucceeded()
    fun onLoginFailed()
}