package com.gennan.summer.ui.mvp.contract

import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVUser
import com.gennan.summer.base.IBasePresenter
/**
 *Created by Gennan.
 */
interface ILoginPresenter : IBasePresenter<ILoginViewCallback> {
    fun login(username: String, password: String)
}

interface ILoginViewCallback {
    fun onLoginSucceeded(avUser: AVUser, username: String, password: String)
    fun onLoginFailed(e: AVException)
}