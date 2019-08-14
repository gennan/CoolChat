package com.gennan.summer.mvp.contract

import com.avos.avoscloud.AVException
import com.gennan.summer.base.IBasePresenter

interface IRegisterPresenter : IBasePresenter<IRegisterViewCallback> {
    fun register(username: String, password: String)
}

interface IRegisterViewCallback {
    fun onRegisterSucceeded()
    fun onRegisterFailed(e:AVException)
}